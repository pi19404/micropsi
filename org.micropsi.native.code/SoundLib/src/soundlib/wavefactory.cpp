#include "soundlib/wavefactory.h"
#include "soundlib/wavefile.h"
#include "baselib/debugprint.h"

using std::map;
using std::string;

namespace SoundLib
{

CWaveFactory* CWaveFactory::ms_pxInst;


//-----------------------------------------------------------------------------
///	default constructor
CWaveFactory::CWaveFactory()
{
	AddSearchPath("./");
}


//-----------------------------------------------------------------------------
///	default destructor
CWaveFactory::~CWaveFactory()
{
    if (!m_xFilenameMap.empty())
    {
        map<string, CWaveFileEntry*>::iterator cur;
	    cur = m_xFilenameMap.begin();
	    while(cur != m_xFilenameMap.end())
	    {
		    delete cur->second;
		    cur->second = 0;
		    cur++;
	    }
    }
}


//------------------------------------------------------------------------------
CWaveFactory& CWaveFactory::Get()							
{ 
    if(!ms_pxInst)
    {
        ms_pxInst = new CWaveFactory();
    }
    return *ms_pxInst; 
}
	
//------------------------------------------------------------------------------
void CWaveFactory::Shut()
{
    if(ms_pxInst)
    {
        delete ms_pxInst;
        ms_pxInst = 0;
    }
}


//-----------------------------------------------------------------------------
/** 
	registriert ein neu angelegtes WaveFile und versorgt das WaveFile mit den 
	eigentlichen Sounddaten. Es gibt zwei Möglichkeiten
	a) das WaveFile ist schon geladen, dann können die Daten direkt verwendet werden
	   d.h. zwei oder mehr WaveFiles teilen sich die Daten
    b) die WaveFile ist noch nicht geladen, dann wird das hier getan
*/
void 
CWaveFactory::RegisterWaveFile(const CStr& p_ksrFilename, CWaveFile& po_xrWaveFile)
{
    CWaveFileEntry* pxEntry = CreateOrFindEntry(p_ksrFilename);
	if(pxEntry)
    {
    	po_xrWaveFile.m_pxData = &(pxEntry->m_xData);
    }
    else
    {
        po_xrWaveFile.m_pxData = 0;
    }
}


//-----------------------------------------------------------------------------
/// gibt Referenz auf WaveFile wieder frei
void 
CWaveFactory::UnregisterWaveFile(const CWaveFile& p_krxWaveFile)
{
	assert(p_krxWaveFile.m_pxData);
	if(!p_krxWaveFile.m_pxData) { return; }

	map<string, CWaveFileEntry*>::iterator cur;
	cur = m_xFilenameMap.find(p_krxWaveFile.m_pxData->m_sFilename.c_str());

	assert(cur != m_xFilenameMap.end());		// freizugebene WaveFile nicht gefunden... das sollte nie passieren!
	if(cur == m_xFilenameMap.end()) { return; }

	CWaveFileEntry* pxEntry = cur->second;
	pxEntry->m_iRefCount--;
	if(pxEntry->m_iRefCount == 0)
	{
		delete pxEntry;
		m_xFilenameMap.erase(cur);
	}
}



//-----------------------------------------------------------------------------
void 
CWaveFactory::AddSearchPath(const CStr& p_krsPath)
{
	CStr sPath = p_krsPath;
	if(!sPath.IsEmpty())
	{
		sPath.Replace('\\', '/');
		if(sPath.GetAt(sPath.GetLength()-1) != '/')
		{
			sPath += '/';
		}
		m_asSearchPaths.push_back(sPath);
	}
}


//-----------------------------------------------------------------------------
CWaveFactory::CWaveFileData::CWaveFileData()
{
	m_iNumChannels		= 0;
	m_iSamplesPerSec	= 0;
	m_iBitsPerSample	= 0;
	m_iNumSamples		= 0;
	m_iSoundDataSize	= 0;
	m_pSoundData		= 0;
}


//-----------------------------------------------------------------------------
CWaveFactory::CWaveFileData::~CWaveFileData()
{
	if(m_pSoundData)
	{
		delete m_pSoundData;
	}
}


//-----------------------------------------------------------------------------
bool 
CWaveFactory::ReadWaveFile(const char* p_pcFile, CWaveFileData& po_rxData)
{	
	HMMIO           hFile;
#pragma pack(1)
	PCMWAVEFORMAT   pcmWaveFormat;  
	MMCKINFO		xRIFFChunk;				// der RIFF-Chunk (identfiziert Dateitype)
	MMCKINFO        xFormatChunk;			// der Format-Chunk (enthält das Sample-Format)	
	MMCKINFO        xDataChunk;				// der Data-Chunk (Sample-Daten)
#pragma pack()
	int				iError;

	hFile = NULL;
	hFile = mmioOpen((LPSTR) p_pcFile, NULL, MMIO_ALLOCBUF | MMIO_READ);
	if(hFile == NULL)
	{
		return false;
	}

	// ersten Chunk der Datei öffnen --> muss laut offizieller Spezifikation immer der RIFF-Chunk sein (und hoffentlich Typ WAVE)
	iError=mmioDescend(hFile, &xRIFFChunk, NULL, 0);
	if(iError != 0  ||  
	   xRIFFChunk.ckid != mmioFOURCC('R', 'I', 'F', 'F')  ||
	   xRIFFChunk.fccType != mmioFOURCC('W', 'A', 'V', 'E'))
	{
		DebugPrint("Error: %s does not seem to be a WAV-File", p_pcFile);
		mmioClose(hFile, 0);
		return false;
	}

	// Format-Chunk suchen
	xFormatChunk.ckid = mmioFOURCC('f', 'm', 't', ' ');
	if ((iError = (int)mmioDescend(hFile, &xFormatChunk, &xRIFFChunk, MMIO_FINDCHUNK)) != 0)
	{
		DebugPrint("Error: %s - could not parse; file corrupted?", p_pcFile);
		mmioClose(hFile, 0);
		return false;
	}

	if (xFormatChunk.cksize < (long) sizeof(PCMWAVEFORMAT))
	{
		DebugPrint("Error: %s - could not parse; file corrupted?", p_pcFile);
		mmioClose(hFile, 0);
		return false;
	}

	if (mmioRead(hFile, (HPSTR) &pcmWaveFormat, (long) sizeof(pcmWaveFormat)) != (long) sizeof(pcmWaveFormat))
	{
		DebugPrint("Error: %s - read error", p_pcFile);
		mmioClose(hFile, 0); 
		return false;
	}

	if(pcmWaveFormat.wf.wFormatTag != WAVE_FORMAT_PCM)
	{
		DebugPrint("Error: %s - file is not PCM; compressed WAV files are not supported at the moment (sorry)", p_pcFile);
		mmioClose(hFile, 0); 
		return false;
	}

	po_rxData.m_iNumChannels = pcmWaveFormat.wf.nChannels;
	po_rxData.m_iSamplesPerSec = pcmWaveFormat.wf.nSamplesPerSec;
	po_rxData.m_iBitsPerSample = pcmWaveFormat.wBitsPerSample;

	// Daten-Chunk suchen
	iError = mmioAscend(hFile, &xFormatChunk, 0);
	if(iError != MMSYSERR_NOERROR)
	{
		return false;
	}

    if(mmioSeek(hFile, xRIFFChunk.dwDataOffset + sizeof(FOURCC), SEEK_SET) == - 1)
	{
		return false;
	}

	xDataChunk.ckid = mmioFOURCC('d', 'a', 't', 'a');
	iError = mmioDescend(hFile, &xDataChunk, &xRIFFChunk, MMIO_FINDCHUNK);
	if(iError != MMSYSERR_NOERROR)
	{
		return false;
	}
	// Buffer mit Sounddaten füllen
	po_rxData.m_iNumSamples = xDataChunk.cksize / pcmWaveFormat.wf.nBlockAlign;
	po_rxData.m_iSoundDataSize = xDataChunk.cksize;
	po_rxData.m_pSoundData = new unsigned char[po_rxData.m_iSoundDataSize];

	if(mmioRead(hFile, (HPSTR)po_rxData.m_pSoundData, (long)po_rxData.m_iSoundDataSize) != (long)po_rxData.m_iSoundDataSize)
	{
		DebugPrint("Error: %s - read error", p_pcFile);
		delete [] po_rxData.m_pSoundData;
		mmioClose(hFile, 0); 
		return false;
	}

	FillWaveFormatStructure(po_rxData.m_xWaveFormat, po_rxData.m_iNumChannels, po_rxData.m_iSamplesPerSec, po_rxData.m_iBitsPerSample);
	
	return true;
}


//-----------------------------------------------------------------------------
/**
    liefert einen WaveFile-Entry für diesen Filenamen; entweder einen 
    existierenden, oder einen neu erstellen mit Ref-Count 1 oder einen 
    null-Pointer, wenn das File sich nicht laden ließ
*/
CWaveFactory::CWaveFileEntry* 
CWaveFactory::CreateOrFindEntry(const CStr& p_krsWaveFile)
{
	map<string, CWaveFileEntry*>::iterator cur;
	cur = m_xFilenameMap.find(p_krsWaveFile.c_str());
	if(cur != m_xFilenameMap.end())
	{
		/// gefunden! (kann ein 0-Eintrag sein)
		if(cur->second)
		{
	    	cur->second->m_iRefCount++;
		}
		return cur->second;
	}
	else
	{		
		// nicht gefunden, WaveFile laden
	
		CWaveFileEntry* pxEntry = new CWaveFileEntry();

		// Suchpfade durchlaufen
		bool bLoaded = false;
		unsigned int iSearchPath = 0;
		while(!bLoaded  &&  iSearchPath < m_asSearchPaths.size())
		{
			bLoaded = ReadWaveFile((m_asSearchPaths[iSearchPath] + p_krsWaveFile).c_str(), pxEntry->m_xData);
			if(!bLoaded)
			{
//				DebugPrint ("Warning: failed to load: %s", (m_asSearchPaths[iSearchPath] + p_krsWaveFile).c_str());
			}
			else
			{
//				DebugPrint ("loaded: %s", (m_asSearchPaths[iSearchPath] + p_krsWaveFile).c_str());
			}
			iSearchPath++; 
		} 
		
		// ohne Suchpfad laden?
		if(!bLoaded)
		{
			bLoaded = ReadWaveFile(p_krsWaveFile.c_str(), pxEntry->m_xData);
		}

		if(bLoaded)
		{
			pxEntry->m_xData.m_sFilename = p_krsWaveFile;
	        m_xFilenameMap[p_krsWaveFile.c_str()] = pxEntry;
        	pxEntry->m_iRefCount++;
			return pxEntry;
		}
		else
		{
			DebugPrint ("Warning: failed to load: %s", (p_krsWaveFile).c_str());
	        m_xFilenameMap[p_krsWaveFile.c_str()] = 0;
			delete pxEntry;
			return 0;
		}
	}
}


//-----------------------------------------------------------------------------
void
CWaveFactory::Prefetch(const CStr& p_krsWaveFile)
{
    if(CreateOrFindEntry(p_krsWaveFile) != 0)
    {
		m_asPrefetchedFiles.push_back(p_krsWaveFile);
    }
}


//-----------------------------------------------------------------------------
void 
CWaveFactory::ClearPrefetch()
{
    for(unsigned int i=0; i<m_asPrefetchedFiles.size(); ++i)
    {
	    map<string, CWaveFileEntry*>::iterator cur;
	    cur = m_xFilenameMap.find(m_asPrefetchedFiles[i].c_str());

	    assert(cur != m_xFilenameMap.end());		// freizugebene WaveFile nicht gefunden... das sollte nie passieren!

	    CWaveFileEntry* pxEntry = cur->second;
	    pxEntry->m_iRefCount--;
	    if(pxEntry->m_iRefCount == 0)
	    {
		    delete pxEntry;
		    m_xFilenameMap.erase(cur);
	    }
    }
}

//-----------------------------------------------------------------------------
/// Hilfsfunktion: füllt eine Windows-WAVEFORMATEX-Struktur korrekt aus
void
CWaveFactory::FillWaveFormatStructure(WAVEFORMATEX& po_rxFormat, int p_iChannels, int p_iSampleRate, int p_iBits)
{
	assert(p_iBits == 8  ||  p_iBits == 16);
	assert(p_iChannels == 1  ||  p_iChannels == 2);
	assert(p_iSampleRate > 0);

	int iSampleSize = p_iBits == 8 ? 1 : 2;

	po_rxFormat.cbSize = sizeof(WAVEFORMATEX);
	po_rxFormat.nAvgBytesPerSec = p_iSampleRate * p_iChannels * iSampleSize;
	po_rxFormat.nBlockAlign = iSampleSize * p_iChannels;
	po_rxFormat.nChannels = p_iChannels;
	po_rxFormat.nSamplesPerSec = p_iSampleRate;
	po_rxFormat.wBitsPerSample = p_iBits;
	po_rxFormat.wFormatTag = WAVE_FORMAT_PCM;
}

//-----------------------------------------------------------------------------

} // namespace SoundLib


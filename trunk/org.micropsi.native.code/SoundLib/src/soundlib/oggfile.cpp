#include "soundlib/oggfile.h"
#include "soundlib/wavefactory.h"
#include "baselib/debugprint.h"
#include "vorbis/vorbisfile.h"

namespace SoundLib
{

//---------------------------------------------------------------------------------------------------------------------
COggFile::COggFile(const char* p_pcFilename) : CGenericSoundFile(p_pcFilename)
{
	m_pxOggFile = 0;

	FILE *f = 0;
	const std::vector<CStr>& asSearchPaths = CWaveFactory::Get().GetSearchPaths();
	unsigned int iSearchPath = 0;

	// zuerst Suchpfade
	while(!f  &&  iSearchPath < asSearchPaths.size())
	{
		f = fopen((asSearchPaths[iSearchPath] + p_pcFilename).c_str(), "rb");
		iSearchPath++; 
	} 

	// ohne Suchpfad?
	if(!f)
	{
		f = fopen(p_pcFilename, "rb");
	}

	if(f)
	{
		vorbis_info *pInfo;
		m_pxOggFile = new OggVorbis_File;
		int i = ov_open(f, m_pxOggFile, NULL, 0);

		pInfo = ov_info(m_pxOggFile, -1);
		CWaveFactory::FillWaveFormatStructure(m_xWaveFormat, pInfo->channels, pInfo->rate, 16);
	}
	else
	{
		DebugPrint("Error: failed to open file %s", p_pcFilename);
	}
}

//---------------------------------------------------------------------------------------------------------------------
COggFile::~COggFile()
{
	if(m_pxOggFile)
	{
		ov_clear(m_pxOggFile);
		delete m_pxOggFile;
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool	
COggFile::IsValid() const
{
	return m_pxOggFile != 0;
}

//---------------------------------------------------------------------------------------------------------------------
double	
COggFile::GetTotalTimeInSeconds() const
{
	assert(m_pxOggFile);
	return ov_time_total(m_pxOggFile, -1);
}

//---------------------------------------------------------------------------------------------------------------------
int		
COggFile::GetNumSamples() const
{
	assert(m_pxOggFile);
	return (int) ov_pcm_total(m_pxOggFile, -1);
}

//---------------------------------------------------------------------------------------------------------------------
int		
COggFile::GetSizeInBytes() const
{
	assert(m_pxOggFile);
	return GetNumSamples() * m_xWaveFormat.nBlockAlign;
}

//---------------------------------------------------------------------------------------------------------------------
bool	
COggFile::Seek(double p_dTimeInSeconds)
{
	assert(m_pxOggFile);
	ov_time_seek(m_pxOggFile, p_dTimeInSeconds);
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
double	
COggFile::GetReadCursorTime() const
{
	return ov_time_tell(m_pxOggFile);
}

//---------------------------------------------------------------------------------------------------------------------
const WAVEFORMATEX*	
COggFile::GetWaveFormat() const
{
	assert(m_pxOggFile);
	return &m_xWaveFormat;
}

//---------------------------------------------------------------------------------------------------------------------
bool	
COggFile::FillSoundBuffer(char* p_pcWrite, DWORD p_dwLength, bool p_bLoopData)
{
	assert(m_pxOggFile);

	static int bitStream;
	long iBytesRead;
	bool bRes = true;

	do {
		iBytesRead = ov_read(m_pxOggFile, p_pcWrite, p_dwLength, 0, 2, 1, &bitStream);
		if(iBytesRead == 0  &&  p_dwLength > 0)
		{
			// End of File...
			if(p_bLoopData)
			{
				// zurück an den Anfang des Files!
				ov_raw_seek(m_pxOggFile, 0);
			}
			else
			{
				// Daten zu Ende --> mit 0 füllen
				iBytesRead = p_dwLength;
				memset(p_pcWrite, 0, iBytesRead);
				bRes = false;
				break;
			}
		}
		p_pcWrite += iBytesRead;
		p_dwLength -= iBytesRead;
	} while (p_dwLength > 0);

	return bRes;

}

//---------------------------------------------------------------------------------------------------------------------

} // namespace SoundLib


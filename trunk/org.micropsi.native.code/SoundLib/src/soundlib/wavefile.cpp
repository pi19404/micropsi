#include "soundlib/stdincludes.h"
#include "soundlib/wavefile.h"

namespace SoundLib
{

int CWaveFile::ms_iWaveFiles = 0;

//---------------------------------------------------------------------------------------------------------------------
CWaveFile::CWaveFile(const char* p_pcFile) : CGenericSoundFile(p_pcFile)
{
	CWaveFactory::Get().RegisterWaveFile(p_pcFile, *this);
	ms_iWaveFiles++;
	m_iReadCursor = 0;
}


//---------------------------------------------------------------------------------------------------------------------
CWaveFile::~CWaveFile()
{
	if(m_pxData)
	{
		CWaveFactory::Get().UnregisterWaveFile(*this);
		m_pxData = 0;
	}	
	ms_iWaveFiles--;
}

//---------------------------------------------------------------------------------------------------------------------
bool		
CWaveFile::IsValid() const								
{ 
	return m_pxData != 0; 
}

//---------------------------------------------------------------------------------------------------------------------
double	
CWaveFile::GetTotalTimeInSeconds() const
{
	assert(m_pxData);
	return (double) m_pxData->m_iNumSamples / (double) m_pxData->m_iSamplesPerSec;
}

//---------------------------------------------------------------------------------------------------------------------
int		
CWaveFile::GetNumSamples() const
{
	assert(m_pxData);
	return m_pxData->m_iNumSamples;
}

//---------------------------------------------------------------------------------------------------------------------
int		
CWaveFile::GetSizeInBytes() const
{
	return m_pxData->m_iSoundDataSize;
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CWaveFile::Seek(double p_dTimeInSeconds)
{
	assert(m_pxData);

	if(p_dTimeInSeconds > 0.0)
	{
		m_iReadCursor = (int) ((double) GetSizeInBytes() / GetTotalTimeInSeconds() * p_dTimeInSeconds);
		m_iReadCursor = min(m_iReadCursor, GetSizeInBytes());
	}
	else
	{
		m_iReadCursor = 0;
	}

	return false;
}

//---------------------------------------------------------------------------------------------------------------------
double	
CWaveFile::GetReadCursorTime() const
{
	return ((double) m_iReadCursor / (double) GetSizeInBytes()) * GetTotalTimeInSeconds();
}

//---------------------------------------------------------------------------------------------------------------------
const WAVEFORMATEX*	
CWaveFile::GetWaveFormat() const
{
	assert(m_pxData);
	return &(m_pxData->m_xWaveFormat);
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CWaveFile::FillSoundBuffer(char* p_pcWrite, DWORD p_dwLength, bool p_bLoopData)
{
	assert(m_pxData);

	long iBytesRead;
	bool bRes = true;

	do {
		iBytesRead = min(p_dwLength, (DWORD) GetSizeInBytes() - m_iReadCursor);
		memcpy(p_pcWrite, ((char*) m_pxData->m_pSoundData) + m_iReadCursor, iBytesRead);
		if(iBytesRead == 0  &&  p_dwLength > 0)
		{
			// End of File...
			if(p_bLoopData)
			{
				// zurück an den Anfang des Files!
				m_iReadCursor = 0;
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

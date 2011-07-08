#include "soundlib/genericsoundfile.h"

namespace SoundLib
{

//---------------------------------------------------------------------------------------------------------------------
CGenericSoundFile::CGenericSoundFile(const char* p_pcFilename)
{
	m_sFilename = p_pcFilename;
}


//---------------------------------------------------------------------------------------------------------------------
CGenericSoundFile::~CGenericSoundFile()
{
}

//---------------------------------------------------------------------------------------------------------------------
std::string		
CGenericSoundFile::GetFilename() const
{
	return m_sFilename;
}

//---------------------------------------------------------------------------------------------------------------------
int				
CGenericSoundFile::GetBytesPerSecond() const
{
	return (int) ((double) GetSizeInBytes() / GetTotalTimeInSeconds()); 
}


} // namespace SoundLib


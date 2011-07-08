#include "soundlib/multichannelsound.h"
#include "baselib/str.h"
#include "baselib/xmlutils.h"

namespace SoundLib
{

using std::string;

//----------------------------------------------------------------------------
CMultiChannelSound::CMultiChannelSound()
{
	m_fVolume	= 100.0f;
	m_fPitch	= 1.0f;
	m_fPriority	= 0.0f;
}


//----------------------------------------------------------------------------
void
CMultiChannelSound::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
	if(!p_pXmlElement) { return; }
	m_sWaveFile = XMLUtils::GetXMLTagString(p_pXmlElement, "wavefile");
	m_fVolume = XMLUtils::GetXMLTagFloat(p_pXmlElement, "volume");
	m_fPitch = XMLUtils::GetXMLTagFloat(p_pXmlElement, "pitch");
	m_fPriority = XMLUtils::GetXMLTagFloat(p_pXmlElement, "priority");
}


//----------------------------------------------------------------------------
void	
CMultiChannelSound::ToXMLElement(TiXmlElement* p_pXmlElement) const
{
	if(!p_pXmlElement) { return; }
	XMLUtils::WriteXMLTagString(p_pXmlElement, "wavefile", m_sWaveFile);
	XMLUtils::WriteXMLTagFloat(p_pXmlElement, "volume", m_fVolume);
	XMLUtils::WriteXMLTagFloat(p_pXmlElement, "pitch", m_fPitch);
	XMLUtils::WriteXMLTagFloat(p_pXmlElement, "priority", m_fPriority);
}

//-----------------------------------------------------------------------------
bool 
CMultiChannelSound::operator==(const CMultiChannelSound& p_rxEnv)
{
	return	m_sWaveFile == p_rxEnv.m_sWaveFile  &&
			m_fVolume   == p_rxEnv.m_fVolume &&
			m_fPitch	== p_rxEnv.m_fPitch  &&
			m_fPriority == p_rxEnv.m_fPriority;
}

//----------------------------------------------------------------------------
bool 
CMultiChannelSound::operator!=(const CMultiChannelSound& p_rxEnv)
{
	return !(*this == p_rxEnv);
}

//----------------------------------------------------------------------------

} // namespace SoundLib


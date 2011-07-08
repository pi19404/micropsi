#include "soundlib/3dsound.h"
#include "baselib/XMLUtils.h"

namespace SoundLib
{

using std::string;

//----------------------------------------------------------------------------
C3DSound::C3DSound()
{
	m_fMinimumFadeDistance = 1.0f;				
	m_fMaximumFadeDistance = 10000.0f;				
	m_fMaximumHearingDistance = 10000.0f;			
}


//----------------------------------------------------------------------------
void
C3DSound::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
	if(!p_pXmlElement) { return; }
	__super::FromXMLElement(p_pXmlElement);

	m_fMinimumFadeDistance = XMLUtils::GetXMLTagFloat(p_pXmlElement, "minfadedistance");
	m_fMaximumFadeDistance = XMLUtils::GetXMLTagFloat(p_pXmlElement, "maxfadedistance");
	m_fMaximumHearingDistance = XMLUtils::GetXMLTagFloat(p_pXmlElement, "maxhearingdistance");
}

//----------------------------------------------------------------------------
void	
C3DSound::ToXMLElement(TiXmlElement* p_pXmlElement) const
{
	if(!p_pXmlElement) { return; }
	__super::ToXMLElement(p_pXmlElement);

	XMLUtils::WriteXMLTagFloat(p_pXmlElement, "minfadedistance", m_fMinimumFadeDistance);
	XMLUtils::WriteXMLTagFloat(p_pXmlElement, "maxfadedistance", m_fMaximumFadeDistance);
	XMLUtils::WriteXMLTagFloat(p_pXmlElement, "maxhearingdistance", m_fMaximumHearingDistance);
}

//-----------------------------------------------------------------------------
bool 
C3DSound::operator==(const C3DSound& p_rxEnv)
{
	return	__super::operator==(p_rxEnv)  &&
			m_fMinimumFadeDistance    == p_rxEnv.m_fMinimumFadeDistance &&
			m_fMaximumFadeDistance	  == p_rxEnv.m_fMaximumFadeDistance  &&
			m_fMaximumHearingDistance == p_rxEnv.m_fMaximumHearingDistance;
}

//----------------------------------------------------------------------------
bool 
C3DSound::operator!=(const C3DSound& p_rxEnv)
{
	return !(*this == p_rxEnv);
}

//----------------------------------------------------------------------------

} // namespace SoundLib


#include "soundlib/soundenvironment.h"
#include "baselib/str.h"
#include "baselib/XMLUtils.h"


namespace SoundLib
{

using std::string;

//----------------------------------------------------------------------------
CSoundEnvironment::CSoundEnvironment()
{
	m_iPreset = 0;
	m_fRadius = 1.0f;
}


//----------------------------------------------------------------------------
void
CSoundEnvironment::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
	if(!p_pXmlElement) { return; }
	m_iPreset = XMLUtils::GetXMLTagInt(p_pXmlElement, "preset");
	m_fRadius = XMLUtils::GetXMLTagFloat(p_pXmlElement, "radius");
}


//----------------------------------------------------------------------------
void	
CSoundEnvironment::ToXMLElement(TiXmlElement* p_pXmlElement) const
{
	if(!p_pXmlElement) { return; }
	XMLUtils::WriteXMLTagInt(p_pXmlElement, "preset", m_iPreset);
	XMLUtils::WriteXMLTagFloat(p_pXmlElement, "radius", m_fRadius);
}

//----------------------------------------------------------------------------
bool 
CSoundEnvironment::operator==(const CSoundEnvironment& p_rxEnv)
{
	return	m_iPreset == p_rxEnv.m_iPreset  &&
			m_fRadius == p_rxEnv.m_fRadius;

}

//----------------------------------------------------------------------------
bool 
CSoundEnvironment::operator!=(const CSoundEnvironment& p_rxEnv)
{
	return !(*this == p_rxEnv);
}

//----------------------------------------------------------------------------

} // namespace SoundLib


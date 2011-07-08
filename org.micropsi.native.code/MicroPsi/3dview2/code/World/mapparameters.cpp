#include "Application/stdinc.h"
#include "World/mapparameters.h"

#include "baselib/xmlutils.h"

using std::string;
using namespace XMLUtils;

//---------------------------------------------------------------------------------------------------------------------
CMapParameters::CMapParameters()
{	
	m_sHeightMap		= "";
	m_sMaterialMap		= "";
	m_vOffset			= CVec3(0.0f, 0.0f, 0.0f);
	m_vScaling			= CVec3(1.0f, 1.0f, 1.0f);
	m_vAbsoluteSize		= CVec3(-1.0f, -1.0f, -1.0f);
	m_vObserverStartPos	= CVec3(0.0f, 0.0f, 0.0f);
	m_vObserverLookAt	= CVec3(1.0f, 0.0f, 0.0f);
	m_bWrapAround		= false;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CMapParameters::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
	m_sHeightMap		= GetXMLTagString(p_pXmlElement, "heightmap");
	m_sMaterialMap		= GetXMLTagString(p_pXmlElement, "materialmap");
	m_vOffset			= GetXMLTagVector(p_pXmlElement, "offset");
	m_vScaling			= GetXMLTagVector(p_pXmlElement, "scaling");
	m_vAbsoluteSize		= GetXMLTagVector(p_pXmlElement, "absolutesize");
	m_vObserverStartPos	= GetXMLTagVector(p_pXmlElement, "observerstartpos");
	m_vObserverLookAt	= GetXMLTagVector(p_pXmlElement, "observerlookat");
	m_bWrapAround		= GetXMLTagBool(p_pXmlElement,   "wraparound");

	if(m_vObserverStartPos == m_vObserverLookAt)
	{
		m_vObserverLookAt.x() += 1.0f;
	}
}

//---------------------------------------------------------------------------------------------------------------------
void	
CMapParameters::ToXMLElement(TiXmlElement* p_pXmlElement) const
{
	WriteXMLTagString(p_pXmlElement, "heightmap", m_sHeightMap);
	WriteXMLTagString(p_pXmlElement, "materialmap", m_sMaterialMap);
	WriteXMLTagVector(p_pXmlElement, "offset", m_vOffset);
	WriteXMLTagVector(p_pXmlElement, "scaling", m_vScaling);
	WriteXMLTagVector(p_pXmlElement, "absolutesize", m_vAbsoluteSize);
	WriteXMLTagVector(p_pXmlElement, "observerstartpos", m_vObserverStartPos);
	WriteXMLTagVector(p_pXmlElement, "observerlookat", m_vObserverLookAt);
	WriteXMLTagBool(p_pXmlElement, "wraparound", m_bWrapAround);
}

//---------------------------------------------------------------------------------------------------------------------

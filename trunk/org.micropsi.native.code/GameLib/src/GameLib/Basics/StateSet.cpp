#include "stdafx.h"
#include "GameLib/Basics/StateSet.h"

#include "tinyxml.h"
#include "BaseLib/XMLUtils.h"

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CStateSet::CStateSet()
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
CStateSet::~CStateSet()
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CStateSet::AddKey(const std::string& sKey, const std::string& sType, const std::string& sInitialValue)
{
    m_xStates[sKey].m_sType = sType;
    m_xStates[sKey].m_sValue = sInitialValue;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void	
CStateSet::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
	Clear();

	const TiXmlElement* pxSubNode = p_pXmlElement->FirstChildElement("stateset");
	if(!pxSubNode)	{ return; }

	const TiXmlElement* pxState = pxSubNode->FirstChildElement("state");
	while(pxState)
	{	
		string sKey, sType, sValue;
		sKey = XMLUtils::GetXMLTagString(pxState, "key");
		assert(sKey.length() != 0);
		sType = XMLUtils::GetXMLTagString(pxState, "type");
		sValue = XMLUtils::GetXMLTagString(pxState, "value");

		AddKey(sKey, sType, sValue);
		pxState = pxState->NextSiblingElement("state");
	}
}

//------------------------------------------------------------------------------------------------------------------------------------------
void	
CStateSet::ToXMLElement(TiXmlElement* p_pXmlElement) const
{
	TiXmlElement xElement("stateset");
	TiXmlElement* pxElement = p_pXmlElement->InsertEndChild(xElement)->ToElement();

	CStateSet::StateIterator i;
	StartIterateStates(i);
	string sKey;
	while(IterateStates(i, sKey))
	{
		TiXmlElement xElement("state");
		TiXmlElement* pxState = pxElement->InsertEndChild(xElement)->ToElement();

		XMLUtils::WriteXMLTagString(pxState, "key", sKey);
		XMLUtils::WriteXMLTagString(pxState, "type", GetStateType(sKey));
		XMLUtils::WriteXMLTagString(pxState, "value", GetStateValue(sKey));
	}
}

//------------------------------------------------------------------------------------------------------------------------------------------

#include "stdafx.h"
#include "GameLib\World\GameObjectPropertyEnum.h"

#include "tinyxml.h"

//---------------------------------------------------------------------------------------------------------------------
std::string	
CGameObjPropertyEnum::GetTypeName()
{
	return "enum";
} 

//---------------------------------------------------------------------------------------------------------------------
std::string				
CGameObjPropertyEnum::GetType() const
{ 
	return CGameObjPropertyEnum::GetTypeName(); 
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjPropertyEnum*
CGameObjPropertyEnum::Create(std::string p_rsName)
{
	return new CGameObjPropertyEnum(p_rsName);
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyEnum::Delete()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjProperty*			
CGameObjPropertyEnum::Clone()
{
	CGameObjPropertyEnum* p = CGameObjPropertyEnum::Create(m_sName);
	*p = *this;
	return p;
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjProperty::CValue*	
CGameObjPropertyEnum::CreateValue()			
{ 
	CValue* p = new CValue(); 
	p->m_iListIndex = m_iDefaultIndex; 
	return p; 
}

//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyEnum::DestroyValue(CGameObjProperty::CValue* p_pxValue)
{
	delete (CGameObjPropertyEnum::CValue*) p_pxValue;
}

//---------------------------------------------------------------------------------------------------------------------
void					
CGameObjPropertyEnum::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
	m_iDefaultIndex = XMLUtils::GetXMLTagInt(p_pXmlElement, "index", -1);
	const TiXmlElement* pItem = p_pXmlElement->FirstChildElement("item");
	while(pItem)
	{
		m_asPossibleValues.push_back(XMLUtils::GetElementText(pItem));
		pItem = pItem->NextSiblingElement("item");
	}
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyEnum::ValueToXMLElement(const TiXmlElement* p_pXmlElement, CGameObjProperty::CValue* p_pxValue)
{
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyEnum::SetValue(CValue* p_pxValue, int p_iIndex)
{
	((CValue* )p_pxValue)->m_iListIndex = clamp(p_iIndex, 0, (int) m_asPossibleValues.size()); 
}
//---------------------------------------------------------------------------------------------------------------------

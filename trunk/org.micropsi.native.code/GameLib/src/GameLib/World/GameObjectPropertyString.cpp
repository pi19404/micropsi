#include "stdafx.h"
#include "GameLib\World\GameObjectPropertyString.h"

//---------------------------------------------------------------------------------------------------------------------
std::string	
CGameObjPropertyString::GetTypeName()
{
	return "string";
} 

//---------------------------------------------------------------------------------------------------------------------
std::string				
CGameObjPropertyString::GetType() const
{ 
	return CGameObjPropertyString::GetTypeName(); 
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjPropertyString*
CGameObjPropertyString::Create(std::string p_rsName)
{
	return new CGameObjPropertyString(p_rsName);
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyString::Delete()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjProperty*			
CGameObjPropertyString::Clone()
{
	CGameObjPropertyString* p = CGameObjPropertyString::Create(m_sName);
	*p = *this;
	return p;
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjProperty::CValue*	
CGameObjPropertyString::CreateValue()			
{ 
	CValue* p = new CValue(); 
	p->m_sValue = m_sDefaultValue; 
	return p; 
}

//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyString::DestroyValue(CGameObjProperty::CValue* p_pxValue)
{
	delete (CGameObjPropertyString::CValue*) p_pxValue;
}

//---------------------------------------------------------------------------------------------------------------------
void					
CGameObjPropertyString::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
	m_sDefaultValue = XMLUtils::GetXMLTagString(p_pXmlElement, "value", "");
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyString::ValueToXMLElement(const TiXmlElement* p_pXmlElement, CGameObjProperty::CValue* p_pxValue)
{
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyString::SetValue(CValue* p_pxValue, std::string p_sValue)
{
	((CValue* )p_pxValue)->m_sValue = p_sValue; 
}
//---------------------------------------------------------------------------------------------------------------------

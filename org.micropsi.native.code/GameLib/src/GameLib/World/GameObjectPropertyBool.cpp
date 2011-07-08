#include "stdafx.h"
#include "GameLib\World\GameObjectPropertyBool.h"

//---------------------------------------------------------------------------------------------------------------------
std::string	
CGameObjPropertyBool::GetTypeName()
{
	return "bool";
} 
//---------------------------------------------------------------------------------------------------------------------
std::string				
CGameObjPropertyBool::GetType() const
{ 
	return CGameObjPropertyBool::GetTypeName(); 
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjPropertyBool*
CGameObjPropertyBool::Create(std::string p_rsName)
{
	return new CGameObjPropertyBool(p_rsName);
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyBool::Delete()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjProperty*			
CGameObjPropertyBool::Clone()
{
	CGameObjPropertyBool* p = CGameObjPropertyBool::Create(m_sName);
	*p = *this;
	return p;
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjProperty::CValue*	
CGameObjPropertyBool::CreateValue()			
{ 
	CValue* p = new CValue(); 
	p->m_bValue = m_bDefaultValue; 
	return p; 
}

//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyBool::DestroyValue(CGameObjProperty::CValue* p_pxValue)
{
	delete (CGameObjPropertyBool::CValue*) p_pxValue;
}

//---------------------------------------------------------------------------------------------------------------------
void					
CGameObjPropertyBool::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
	m_bDefaultValue = XMLUtils::GetXMLTagBool(p_pXmlElement, "value", false);
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyBool::ValueToXMLElement(const TiXmlElement* p_pXmlElement, CGameObjProperty::CValue* p_pxValue)
{
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyBool::SetValue(CValue* p_pxValue, bool p_bValue) 
{ 
	((CValue* )p_pxValue)->m_bValue = p_bValue; 
}

//---------------------------------------------------------------------------------------------------------------------

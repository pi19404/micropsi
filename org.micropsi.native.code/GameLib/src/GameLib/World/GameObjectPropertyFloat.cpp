#include "stdafx.h"
#include "GameLib\World\GameObjectPropertyFloat.h"

//---------------------------------------------------------------------------------------------------------------------
std::string	
CGameObjPropertyFloat::GetTypeName()
{
	return "float";
} 

//---------------------------------------------------------------------------------------------------------------------
std::string				
CGameObjPropertyFloat::GetType() const
{ 
	return CGameObjPropertyFloat::GetTypeName(); 
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjPropertyFloat*
CGameObjPropertyFloat::Create(std::string p_rsName)
{
	return new CGameObjPropertyFloat(p_rsName);
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyFloat::Delete()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjProperty*			
CGameObjPropertyFloat::Clone()
{
	CGameObjPropertyFloat* p = CGameObjPropertyFloat::Create(m_sName);
	*p = *this;
	return p;
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjProperty::CValue*	
CGameObjPropertyFloat::CreateValue()			
{ 
	CValue* p = new CValue(); 
	p->m_fValue = m_fDefaultValue; 
	return p; 
}

//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyFloat::DestroyValue(CGameObjProperty::CValue* p_pxValue)
{
	delete (CGameObjPropertyFloat::CValue*) p_pxValue;
}

//---------------------------------------------------------------------------------------------------------------------
void					
CGameObjPropertyFloat::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
	m_fDefaultValue	= XMLUtils::GetXMLTagFloat(p_pXmlElement, "value", 0);
	m_fMinValue		= XMLUtils::GetXMLTagFloat(p_pXmlElement, "min", FLT_MIN);
	m_fMaxValue		= XMLUtils::GetXMLTagFloat(p_pXmlElement, "max", FLT_MAX);
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyFloat::ValueToXMLElement(const TiXmlElement* p_pXmlElement, CGameObjProperty::CValue* p_pxValue)
{
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyFloat::SetValue(CValue* p_pxValue, float p_fValue)
{
	((CValue* )p_pxValue)->m_fValue = clamp(p_fValue, m_fMinValue, m_fMaxValue); 
}
//---------------------------------------------------------------------------------------------------------------------

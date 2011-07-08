#include "stdafx.h"
#include "GameLib\World\GameObjectPropertyInt.h"

//---------------------------------------------------------------------------------------------------------------------
std::string	
CGameObjPropertyInt::GetTypeName()
{
	return "int";
} 

//---------------------------------------------------------------------------------------------------------------------
std::string				
CGameObjPropertyInt::GetType() const
{ 
	return CGameObjPropertyInt::GetTypeName(); 
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjPropertyInt*
CGameObjPropertyInt::Create(std::string p_rsName)
{
	return new CGameObjPropertyInt(p_rsName);
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyInt::Delete()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjProperty*			
CGameObjPropertyInt::Clone()
{
	CGameObjPropertyInt* p = CGameObjPropertyInt::Create(m_sName);
	*p = *this;
	return p;
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjProperty::CValue*	
CGameObjPropertyInt::CreateValue()			
{ 
	CValue* p = new CValue(); 
	p->m_iValue = m_iDefaultValue; 
	return p; 
}

//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyInt::DestroyValue(CGameObjProperty::CValue* p_pxValue)
{
	delete (CGameObjPropertyInt::CValue*) p_pxValue;
}

//---------------------------------------------------------------------------------------------------------------------
void					
CGameObjPropertyInt::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
	m_iDefaultValue	= XMLUtils::GetXMLTagInt(p_pXmlElement, "value", 0);
	m_iMinValue		= XMLUtils::GetXMLTagInt(p_pXmlElement, "min", INT_MIN);
	m_iMaxValue		= XMLUtils::GetXMLTagInt(p_pXmlElement, "max", INT_MAX);
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyInt::ValueToXMLElement(const TiXmlElement* p_pXmlElement, CGameObjProperty::CValue* p_pxValue)
{
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyInt::SetValue(CValue* p_pxValue, int p_iValue)
{
	((CValue* )p_pxValue)->m_iValue = clamp(p_iValue, m_iMinValue, m_iMaxValue); 
}
//---------------------------------------------------------------------------------------------------------------------

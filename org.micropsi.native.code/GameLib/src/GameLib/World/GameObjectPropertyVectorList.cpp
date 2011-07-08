#include "stdafx.h"
#include "GameLib\World\GameObjectPropertyVectorList.h"

//---------------------------------------------------------------------------------------------------------------------
std::string	
CGameObjPropertyVectorList::GetTypeName()
{
	return "vectorlist";
} 

//---------------------------------------------------------------------------------------------------------------------
std::string				
CGameObjPropertyVectorList::GetType() const
{ 
	return CGameObjPropertyVectorList::GetTypeName(); 
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjPropertyVectorList*
CGameObjPropertyVectorList::Create(std::string p_rsName)
{
	return new CGameObjPropertyVectorList(p_rsName);
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyVectorList::Delete()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjProperty*			
CGameObjPropertyVectorList::Clone()
{
	CGameObjPropertyVectorList* p = CGameObjPropertyVectorList::Create(m_sName);
	*p = *this;
	return p;
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjProperty::CValue*	
CGameObjPropertyVectorList::CreateValue()			
{ 
	CValue* p = new CValue(); 
	return p; 
}

//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyVectorList::DestroyValue(CGameObjProperty::CValue* p_pxValue)
{
	delete (CGameObjPropertyVectorList::CValue*) p_pxValue;
}

//---------------------------------------------------------------------------------------------------------------------
void					
CGameObjPropertyVectorList::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
}
//---------------------------------------------------------------------------------------------------------------------
void
CGameObjPropertyVectorList::ValueToXMLElement(const TiXmlElement* p_pXmlElement, CGameObjProperty::CValue* p_pxValue)
{
}
//---------------------------------------------------------------------------------------------------------------------

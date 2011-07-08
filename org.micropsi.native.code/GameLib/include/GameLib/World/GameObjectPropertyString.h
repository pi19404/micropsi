#pragma once

#ifndef GAMELIB_GAMEOBJPROPERTYSTRING_H_INCLUDED
#define GAMELIB_GAMEOBJPROPERTYSTRING_H_INCLUDED

#include "GameObjectProperty.h"

class CGameObjPropertyString : public CGameObjProperty
{
public:
	class CValue : public CGameObjProperty::CValue
	{
	public:
		std::string	m_sValue;
	};

	CGameObjPropertyString(std::string p_rsName) : CGameObjProperty(p_rsName) {};

	static  std::string					GetTypeName(); 
	static CGameObjPropertyString*		Create(std::string p_rsName);

	virtual void						Delete();
	virtual CGameObjProperty*			Clone();
	virtual std::string					GetType() const;
	virtual CGameObjProperty::CValue*	CreateValue();
	virtual void						DestroyValue(CGameObjProperty::CValue* p_pxValue);
	virtual void						FromXMLElement(const TiXmlElement* p_pXmlElement);
	virtual void						ValueToXMLElement(const TiXmlElement* p_pXmlElement, CGameObjProperty::CValue* p_pxValue);
	virtual void						SetValue(CValue* p_pxValue, std::string p_sValue);

	std::string	m_sDefaultValue;
};

#endif GAMELIB_GAMEOBJPROPERTYSTRING_H_INCLUDED

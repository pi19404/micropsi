#pragma once

#ifndef GAMELIB_GAMEOBJPROPERTYBOOL_H_INCLUDED
#define GAMELIB_GAMEOBJPROPERTYBOOL_H_INCLUDED

#include "GameObjectProperty.h"

class CGameObjPropertyBool : public CGameObjProperty
{
public:
	class CValue : public CGameObjProperty::CValue
	{
	public:
		bool		m_bValue;
	};

	CGameObjPropertyBool(std::string p_rsName) : CGameObjProperty(p_rsName) {};

	static std::string					GetTypeName(); 
	static CGameObjPropertyBool*		Create(std::string p_rsName);

	virtual void						Delete();
	virtual CGameObjProperty*			Clone();
	virtual std::string					GetType() const;
	virtual CGameObjProperty::CValue*	CreateValue();
	virtual void						DestroyValue(CGameObjProperty::CValue* p_pxValue);
	virtual void						FromXMLElement(const TiXmlElement* p_pXmlElement);
	virtual void						ValueToXMLElement(const TiXmlElement* p_pXmlElement, CGameObjProperty::CValue* p_pxValue);
	virtual void						SetValue(CValue* p_pxValue, bool p_bValue);

	bool		m_bDefaultValue;
};

#endif GAMELIB_GAMEOBJPROPERTYBOOL_H_INCLUDED

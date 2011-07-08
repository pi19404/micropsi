#pragma once

#ifndef GAMELIB_GAMEOBJPROPERTYFLOAT_H_INCLUDED
#define GAMELIB_GAMEOBJPROPERTYFLOAT_H_INCLUDED

#include "GameObjectProperty.h"

class CGameObjPropertyFloat : public CGameObjProperty
{
public:
	class CValue : public CGameObjProperty::CValue
	{
	public:
		float	m_fValue;
	};

	CGameObjPropertyFloat(std::string p_rsName) : CGameObjProperty(p_rsName) {};

	static  std::string					GetTypeName(); 
	static CGameObjPropertyFloat*		Create(std::string p_rsName);

	virtual void						Delete();
	virtual CGameObjProperty*			Clone();
	virtual std::string					GetType() const;
	virtual CGameObjProperty::CValue*	CreateValue();
	virtual void						DestroyValue(CGameObjProperty::CValue* p_pxValue);
	virtual void						FromXMLElement(const TiXmlElement* p_pXmlElement);
	virtual void						ValueToXMLElement(const TiXmlElement* p_pXmlElement, CGameObjProperty::CValue* p_pxValue);
	virtual void						SetValue(CValue* p_pxValue, float p_fValue);

	float		m_fDefaultValue;
	float		m_fMinValue;
	float		m_fMaxValue;
};

#endif GAMELIB_GAMEOBJPROPERTYFLOAT_H_INCLUDED

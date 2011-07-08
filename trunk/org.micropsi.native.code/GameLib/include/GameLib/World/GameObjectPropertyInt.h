#pragma once

#ifndef GAMELIB_GAMEOBJPROPERTYINT_H_INCLUDED
#define GAMELIB_GAMEOBJPROPERTYINT_H_INCLUDED

#include "GameObjectProperty.h"

class CGameObjPropertyInt : public CGameObjProperty
{
public:
	class CValue : public CGameObjProperty::CValue
	{
	public:
		int		m_iValue;
	};

	CGameObjPropertyInt(std::string p_rsName) : CGameObjProperty(p_rsName) {};

	static  std::string					GetTypeName(); 
	static CGameObjPropertyInt*			Create(std::string p_rsName);

	virtual void						Delete();
	virtual CGameObjProperty*			Clone();
	virtual std::string					GetType() const;
	virtual CGameObjProperty::CValue*	CreateValue();
	virtual void						DestroyValue(CGameObjProperty::CValue* p_pxValue);
	virtual void						FromXMLElement(const TiXmlElement* p_pXmlElement);
	virtual void						ValueToXMLElement(const TiXmlElement* p_pXmlElement, CGameObjProperty::CValue* p_pxValue);
	virtual void						SetValue(CValue* p_pxValue, int p_iValue);

	int			m_iDefaultValue;
	int			m_iMinValue;
	int			m_iMaxValue;
};

#endif GAMELIB_GAMEOBJPROPERTYINT_H_INCLUDED

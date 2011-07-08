#pragma once

#ifndef GAMELIB_GAMEOBJPROPERTYENUM_H_INCLUDED
#define GAMELIB_GAMEOBJPROPERTYENUM_H_INCLUDED

#include "GameObjectProperty.h"
#include <vector>

class CGameObjPropertyEnum : public CGameObjProperty
{
public:
	class CValue : public CGameObjProperty::CValue
	{
	public:
		int		m_iListIndex;
	};

	CGameObjPropertyEnum(std::string p_rsName) : CGameObjProperty(p_rsName) {};

	static std::string					GetTypeName(); 
	static CGameObjPropertyEnum*		Create(std::string p_rsName);

	virtual void						Delete();
	virtual CGameObjProperty*			Clone();
	virtual std::string					GetType() const;
	virtual CGameObjProperty::CValue*	CreateValue();
	virtual void						DestroyValue(CGameObjProperty::CValue* p_pxValue);
	virtual void						FromXMLElement(const TiXmlElement* p_pXmlElement);
	virtual void						ValueToXMLElement(const TiXmlElement* p_pXmlElement, CGameObjProperty::CValue* p_pxValue);
	virtual void						SetValue(CValue* p_pxValue, int p_iIndex);

	int							m_iDefaultIndex;
	std::vector<std::string>	m_asPossibleValues;
};

#endif GAMELIB_GAMEOBJPROPERTYENUM_H_INCLUDED

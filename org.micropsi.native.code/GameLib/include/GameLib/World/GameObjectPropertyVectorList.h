#pragma once

#ifndef GAMELIB_GAMEOBJPROPERTYVECTORLIST_H_INCLUDED
#define GAMELIB_GAMEOBJPROPERTYVECTORLIST_H_INCLUDED

#include "GameObjectProperty.h"
#include "baselib/geometry/CVector.h"
#include <vector>

class CGameObjPropertyVectorList : public CGameObjProperty
{
public:
	class CValue : public CGameObjProperty::CValue
	{
	public:
		std::vector<CVec3>		m_avVectors;
	};

	CGameObjPropertyVectorList(std::string p_rsName) : CGameObjProperty(p_rsName) {};

	static  std::string					GetTypeName(); 
	static CGameObjPropertyVectorList*	Create(std::string p_rsName);

	virtual void						Delete();
	virtual CGameObjProperty*			Clone();
	virtual std::string					GetType() const;
	virtual CGameObjProperty::CValue*	CreateValue();
	virtual void						DestroyValue(CGameObjProperty::CValue* p_pxValue);
	virtual void						FromXMLElement(const TiXmlElement* p_pXmlElement);
	virtual void						ValueToXMLElement(const TiXmlElement* p_pXmlElement, CGameObjProperty::CValue* p_pxValue);
};

#endif GAMELIB_GAMEOBJPROPERTYVECTORLIST_H_INCLUDED

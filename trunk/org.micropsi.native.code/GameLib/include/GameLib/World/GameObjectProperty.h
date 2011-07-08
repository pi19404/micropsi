#pragma once

#ifndef GAMELIB_GAMEOBJPROPERTY_H_INCLUDED
#define GAMELIB_GAMEOBJPROPERTY_H_INCLUDED

#include <string>
#include "baselib/macros.h"
#include "GameLib/Utilities/XMLUtils.h"
class TiXmlElement;

class CGameObjProperty
{
public:
	class CValue
	{
	public:
		CValue() {}; 
		virtual ~CValue() {};
	};

	CGameObjProperty(std::string p_rsName);
	virtual ~CGameObjProperty();

	/// löscht dieses Objekt
	virtual void						Delete() = 0;

	/// dubliziert dieses Objekt
	virtual CGameObjProperty*			Clone() = 0;

	/// liefert den Typend dieser Property
	virtual std::string					GetType() const = 0;

	/// erzeugt einen Wert vom entsprechenden Typ; wird normalerweise auf Defaultwert gesetzt
	virtual CGameObjProperty::CValue*	CreateValue() = 0;

	/// Gegenstück zum erzeugen eines Wertes vom entsprechenden Typ
	virtual void						DestroyValue(CGameObjProperty::CValue* p_pxValue) = 0;

	/// liest die Werte aus einem XML-Format ein
	virtual void						FromXMLElement(const TiXmlElement* p_pXmlElement) = 0;

	// schreibt <property name="propname">value</property>
	virtual void						ValueToXMLElement(const TiXmlElement* p_pXmlElement, CGameObjProperty::CValue* p_pxValue) = 0;

	std::string			m_sName;				///< Name der Eigenschaft
	std::string			m_sDescription;			///< Beschreibungstext
	bool				m_bEditable;			///< true: Wert im Objekt darf geändert werden; false: immer fester Defaultwert
	int					m_iIndex;				///< fortlaufende Nummerierung der Properties innerhalb eines Objektes
};

//---------------------------------------------------------------------------------------------------------------------

#include "GameObjectProperty.inl"

#endif // GAMELIB_GAMEOBJPROPERTY_H_INCLUDED




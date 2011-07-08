#include "stdafx.h"
#include <stdio.h>
#include "uilib/controls/panel.h"
#include "uilib/core/windowmanager.h"

namespace UILib
{


//---------------------------------------------------------------------------------------------------------------------
/**
	Default Konstruktor
*/
CPanel::CPanel()
{
	m_xColor = CColor(255, 255, 255, 255);
	SetSize(100, 100);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Destruktor
*/
CPanel::~CPanel()
{
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	Erzeugt ein neues Panel
*/
CPanel* 
CPanel::Create()
{
	return new CPanel();
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	Setzt die Hintergrundfarbe
*/
void 
CPanel::SetColor(const CColor& p_rxColor)
{
	if(m_xColor != p_rxColor)
	{
		m_xColor=p_rxColor; 
		InvalidateWindow(); 
	}
}

//---------------------------------------------------------------------------------------------------------------------
CColor 
CPanel::GetColor() const
{
	return m_xColor;
}

//---------------------------------------------------------------------------------------------------------------------
/** 
	Paint
*/
void 
CPanel::Paint(const CPaintContext& p_rxCtx)
{
	p_rxCtx.FillRect(GetRect(), m_xColor);
}

//---------------------------------------------------------------------------------------------------------------------
CStr 
CPanel::GetDebugString() const					
{ 
	CRct xRct = GetRect();
	CColor xColor = GetColor();
	return CStr::Create("CPanel extends(%d, %d, %d, %d), color(%d, %d, %d, %d)", 
		xRct.left, xRct.top, xRct.right, xRct.bottom, xColor.m_cRed, xColor.m_cGreen, xColor.m_cBlue, xColor.m_cAlpha); 
}

//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt den Wert eines benannten Attributes. Siehe UIlib Doku für eine vollständige Liste der Attribute.
	\param	p_rsName	Name des Attributes
	\param	p_rsValue	neuer Wert
	\return	true, wenn erfolgreich
*/
bool 
CPanel::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if(p_rsName=="color")
	{
		int r, g, b, a;
		r = g = b = a = 0;
		sscanf(p_rsValue.c_str(), "%d,%d,%d,%d",r,g,b,a);
		SetColor(CColor(r,g,b,a));
		return true;
	}
	return	__super::SetAttrib(p_rsName, p_rsValue);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	liefert den Wert eines benannten Attributes. Siehe UILib Doku für eine vollständige Liste der Attribute. ues

	\param	p_rsName		Name des Attributes
	\param	po_srValue		Ausgabe-Parameter: String, der den Wert entgegennimmt
	\return true, wenn Wert zurückgeliefert wurde; false, wenn der Attributnamen nicht bekannt ist
*/
bool 
CPanel::GetAttrib(const CStr& p_rsName, CStr& po_srValue) const
{
	if(p_rsName=="color")
	{
		po_srValue.Format("%d,%d,%d,%d", m_xColor.GetRed(), m_xColor.GetGreen(), m_xColor.GetBlue(), m_xColor.GetAlpha());
		return true;
	}
	return __super::GetAttrib(p_rsName,po_srValue);
}

//---------------------------------------------------------------------------------------------------------------------

} // namespace UIlib


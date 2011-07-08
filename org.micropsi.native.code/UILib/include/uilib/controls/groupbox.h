
#pragma once 
#ifndef UILIB_GROUPBOX_H_INCLUDED 
#define UILIB_GROUPBOX_H_INCLUDED

#include "uilib/core/window.h"

namespace UILib
{

class CGroupBox : public CWindow
{
public:
	/// erzeugt einen neue GroupBox
	static CGroupBox*	Create();

	/// set text label
	void			SetText(const CStr& p_sText);

	/// add child window
	virtual bool	AddChild(WHDL p_hWnd);

	/// liefert die Anzahl der Kind-Fenster
	bool			AddChild(CWindow* p_pxWindow);

	/// liefert die Anzahl der Kind-Fenster
	virtual int		NumChildWindows() const;

	/// liefert ein Kind-Fenster
	virtual WHDL	GetChild(int p_iIndex) const;

	/// entfernt ein Kind-Fenster
	virtual void	RemoveChild(WHDL p_hWnd);

	/// setzt den Wert eines benannten Attributes
	virtual bool	SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);

	/// liefert den Wert eines benannten Attributes
	virtual bool	GetAttrib(const CStr& p_rsName, CStr& po_rsValue) const;

	/// liefert eine String-Repräsentation dieses Fensters für Debugzwecke
	CStr			GetDebugString() const;

protected:

	CGroupBox();
	virtual ~CGroupBox();

	virtual void Paint(const CPaintContext& p_xrCtx);
	virtual bool OnResize();
	virtual bool OnVisualizationChange();

private:
	CWindow*	m_pxClientArea;			///< client area window
	CStr		m_sText;				///< caption text
	CRct		m_xFrameSize;			///< size of frame + padding

private:
	CGroupBox(const CGroupBox&);
	operator=(const CGroupBox&);
};

#include "groupbox.inl"

} // namespace UILib

#endif // ifdef UILIB_GROUPBOX_H_INCLUDED


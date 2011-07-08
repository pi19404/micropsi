#pragma once 
#ifndef UILIB_DIALOGWINDOW_H_INCLUDED 
#define UILIB_DIALOGWINDOW_H_INCLUDED

#include "uilib/controls/basicbutton.h"

namespace UILib
{

class CDialogWindow : public CWindow
{
public:

	/// erzeugt ein neues CDialogWindow
	static CDialogWindow* Create();

	/// fügt ein (logisches) Kindefenster hinzu
	virtual bool		AddChild(WHDL p_hWnd);

	/// entfernt ein (logisches) Kindfenster
	virtual void		RemoveChild(WHDL p_hWnd);

	/// liefert Anzahl der (logischen) Kindfenster
	virtual int			NumChildWindows() const;

	/// liefert ein (logisches) Kindfenster
	virtual WHDL		GetChild(int p_iIndex) const;

	/// setzt den Text in der Titelleiste
	void				SetCaption(const CStr& p_rsText);

	/// liefert den Text in der Titelleiste
	CStr				GetCaption() const;

	/// bestimmt, ob dieses Fenster mit der Maus gezogen werden kann (default = true)
	void				SetDraggable(bool p_bDraggable); 

	/// liefert true, wenn dieses Fenster mit der Maus gezogen werden kann
	bool				GetDraggable() const;

	/// bestimmt, ob das Fenster einen Close-Button hat
	void				SetHasCloseButton(bool p_bCloseButton);

	/// liefert true, wenn dieses Fenster einen Close-Button hat
	bool				GetHasCloseButton() const;

	/// setzt den Wert eines benannten Attributes
	virtual bool		SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);

	/// liefert den Wert eines benannten Attributes
	virtual bool		GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;

	/// wird gerufen, wenn der Close-Button geklickt wird
	virtual bool		OnClose();

	/// setzt Callbackfunktion für "Close Button geklickt"
	void				SetOnCloseCallback(CFunctionPointer1<CDialogWindow*>& rxCallback);

protected:

	CDialogWindow();
	virtual ~CDialogWindow();

	/// eigene Unterklasse für Close-Button
	class CCloseButton : public CBasicButton
	{
	public:		
		static CCloseButton* Create();
	protected:
		virtual void DeleteNow();
		CCloseButton() {}
		virtual void Paint(const CPaintContext& p_rxCtx);
		virtual bool OnClick();
	private:
		CCloseButton(const CCloseButton&) {}
		operator=(const CCloseButton&) {}
	};

	/// get debug info string
	virtual CStr GetDebugString() const;

	virtual bool HandleMsg(const CMessage& p_rxMessage);
	virtual void Paint(const CPaintContext& p_xrCtx);

	void		 InvalidateTitleBar();

	virtual bool OnLButtonDown(const CPnt& p_rxMousePos);
	virtual bool OnLButtonUp(const CPnt& p_rxMousePos);
	virtual bool OnMouseMove(const CPnt& p_rxMousePos);

	virtual bool OnResize();
	virtual bool OnVisualizationChange();
	virtual bool OnActivate();
	virtual bool OnDeactivate();
	virtual bool OnActivateIndirect();
	virtual bool OnDeactivateIndirect();

private:
	CStr			m_sCaption;					///< Text in der Titelleiste
	CWindow*		m_pxClientArea;				///< Innenbereich
	bool			m_bDraggable;				///< true, wenn mit der Maus ziehbar

	bool			m_bDrag;					///< true wenn das Fenster momentan mit der Maus gezogen wird
	CPnt			m_pntDragStart;				///< Punkt wo das Ziehen begonnen hat (absolut)
	CPnt			m_pntOriginalPos;			///< ursprüngliche Position des Fensters relativ zum Parent

	int				m_iTitleBarHeight;			///< Höhe der Titelleiste; updated bei Device- oder Visualisierungs-Wechsel
	CPnt			m_xTitleBarPos;				///< Position der Titelleiste (Entfernung links/rechts, Entfernung von oben)
	CRct			m_xFrameSize;				///< Breite des Rahmens; updated bei Device- oder Visualisierungs-Wechsel
	int				m_iClientAreaY;				///< Y-pos des Innenbereiches
	CCloseButton*	m_pxCloseButton;			///< Close-Button, 0 wenn keiner existiert

	CFunctionPointer1<CDialogWindow*>		m_xOnCloseCallback;			///< Callbackfunktion bei Close

};

static const char* msgDialogWindowClosed = "DlgClose";
class CDialogWindowClosedMsg : public CMessage
{ public: CDialogWindowClosedMsg(WHDL hWnd) : CMessage(msgDialogWindowClosed, false, true, hWnd)	{} };

#include "dialogwindow.inl"

} //namespace UILib


#endif //UILIB_DIALOGWINDOW_H_INCLUDED



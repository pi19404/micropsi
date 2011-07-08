#ifndef UILIB_BASICSCROLLWINDOW_H_INCLUDED 
#define UILIB_BASICSCROLLWINDOW_H_INCLUDED

#include "uilib/core/window.h"

namespace UILib
{

class CBasicScrollWindow : public CWindow
{
public:
	static	CBasicScrollWindow*	Create();

	/// setzt ein anderes Innenraumfenster (vorhandenes wird gelöscht)
	void			SetInteriorWindow(CWindow* p_pxNewInterior);

	/// liefert horizontale Größe des scrollbaren Innenraumes
	int				GetHSize() const;

	/// liefert vertikale Größe des scrollbaren Innenraumes
	int				GetVSize() const;

	/// liefert maximale horizontale Scroll-Position (gültige Positionen sind >= 0 und <= maximum)
	int				GetHScrollRange() const;

	/// liefert maximale vertikale Scroll-Position (gültige Positionen sind >= 0 und <= maximum)
	int				GetVScrollRange() const;

	/// liefert aktuelle horizontale Scroll-Position
	int				GetHScrollPos() const;

	/// liefert aktuelle vertikale Scroll-Position
	int				GetVScrollPos() const;

	/// setzt horizontale Scroll-Position; liefert neue (validierte) Position zurück
	int				SetHScrollPos(int iHPos);

	/// setzt vertikale Scroll-Position; liefert neue (validierte) Position zurück
	int				SetVScrollPos(int iVPos);

	/// setzt Scroll-Position
	void			SetScrollPos(int iHPos, int iVPos);

	/// setzt die Größe des scrollbaren Innenbereiches 
	void			SetClientAreaSize(const CSize& p_rxSize);

	/// liefert die Größe des scrollbaren Innenbereiches 
	CSize			GetClientAreaSize() const;

	/// liefert Pointer auf inneres Fenster
	CWindow*		GetClientWindow();

	/// fügt Kindfenster hinzu (in den scrollbaren Innenbereich)
	virtual bool	AddChild(WHDL p_hWnd);

	/// entfernt Kindfenster (aus dem scrollbaren Innenbereich)
	virtual void	RemoveChild(WHDL p_hWnd);

	/// liefert die Anzahl Kindfenster (im scrollbaren Innenbereich)
	virtual int		NumChildWindows() const;

	/// liefert ein Kindfenster (aus dem scrollbaren Innenbereich)
	virtual WHDL	GetChild(int p_iIndex) const;

protected:

	CBasicScrollWindow();
	virtual ~CBasicScrollWindow();

	virtual CStr	GetDebugString() const;

	virtual bool	HandleMsg(const CMessage& p_rxMessage);
	virtual bool	OnResize();

	CWindow*		m_pxInnerWindow;		///< der scrollbare Innenbereich ist in Wahrheit in Kindfenster
};

#include "basicscrollwindow.inl"

static const char* msgBasicScrollWindowChanged = "BScrWChn";
class CBasicScrollWindowChangedMsg : public CMessage
{ public: CBasicScrollWindowChangedMsg (WHDL hWnd) : CMessage(msgBasicScrollWindowChanged, false, false, hWnd) {} };

} // namespace UILib

#endif // ifndef UILIB_BASICSCROLLWINDOW_H_INCLUDED

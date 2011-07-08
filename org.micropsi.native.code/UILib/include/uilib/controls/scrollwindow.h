#ifndef UILIB_SCROLLWINDOW_H_INCLUDED 
#define UILIB_SCROLLWINDOW_H_INCLUDED

#include "uilib/controls/basicscrollwindow.h"
#include "uilib/controls/scrollbar.h"

namespace UILib
{

class CScrollWindow : public CWindow
{
public:
	enum ScrollType
	{
		ST_AUTO,				///< Scrollbars werden nach Bedarf eingeblendet
		ST_ALWAYS,				///< Scrollbars sind immer sichtbar
		ST_NEVER,				///< Scrollbars sind nie sichtbar
		ST_NUMTYPES
	};

	static CScrollWindow* Create();

	/// setzt ein anderes Innenraumfenster (vorhandenes wird gel�scht)
	void			SetInteriorWindow(CWindow* p_pxNewInterior);

	/// liefert horizontale Gr��e des scrollbaren Innenraumes
	int				GetHSize() const;

	/// liefert vertikale Gr��e des scrollbaren Innenraumes
	int				GetVSize() const;

	/// liefert vertikal sichtbare Gr��e des scrollbaren Innenraums (= Fenstergr��e - Gr��e des Scrollbars (wenn vorhanden))
	int				GetVisibleVSize() const;
	
	/// liefert horizontal sichtbare Gr��e des scrollbaren Innenraums (= Fenstergr��e - Gr��e des Scrollbars (wenn vorhanden))
	int				GetHScrollRange() const;

	/// liefert maximale vertikale Scroll-Position (g�ltige Positionen sind >= 0 und <= maximum)
	int				GetVScrollRange() const;

	/// liefert aktuelle horizontale Scroll-Position
	int				GetHScrollPos() const;

	/// liefert aktuelle vertikale Scroll-Position
	int				GetVScrollPos() const;

	/// setzt horizontale Scroll-Position; liefert neue (validierte) Position zur�ck
	int				SetHScrollPos(int iHPos);

	/// setzt vertikale Scroll-Position; liefert neue (validierte) Position zur�ck
	int				SetVScrollPos(int iVPos);

	/// setze horizontale Scrollgeschwindigkeit (Anzahl Einheiten wenn Button gedr�ckt wird)
	int				SetHScrollSpeed(int p_iSpeed);

	/// setze vertikale Scrollgeschwindigkeit (Anzahl Einheiten wenn Button gedr�ckt wird)
	int				SetVScrollSpeed(int p_iSpeed);

	/// setze Scrollgeschwindigkeit f�r beide Richtungen (Anzahl Einheiten wenn Button gedr�ckt wird)
	void			SetScrollSpeed(int p_iSpeed);

	/// setzt Scroll-Position
	void			SetScrollPos(int iHPos, int iVPos);

	/// setzt die Gr��e des scrollbaren Innenbereiches 
	void			SetClientAreaSize(const CSize& p_rxSize);

	/// liefert die Gr��e des scrollbaren Innenbereiches 
	CSize			GetClientAreaSize() const;

	/// liefert true, wenn das Fenster momentan einen horizontalen Scrollbar hat
	bool			HasHScrollbar()	const;

	/// liefert true, wenn das Fenster momentan einen vertikalen Scrollbar hat 
	bool			HasVScrollbar()	const;

	/// setzt horizontalen Scrolltypen
	void			SetHScrollType(int p_iScrollType);

	/// setzt vertikalen Scrolltypen
	void			SetVScrollType(int p_iScrollType);

	/// liefert Pointer auf Innenraumfenster
	CWindow*		GetClientWindow();

	/// f�gt Kindfenster hinzu (in den scrollbaren Innenbereich)
	virtual bool	AddChild(WHDL p_hWnd);

	/// entfernt Kindfenster (aus dem scrollbaren Innenbereich)
	virtual void	RemoveChild(WHDL p_hWnd);

	/// liefert die Anzahl Kindfenster (im scrollbaren Innenbereich)
	virtual int		NumChildWindows() const;

	/// liefert ein Kindfenster (aus dem scrollbaren Innenbereich)
	virtual WHDL	GetChild(int p_iIndex) const;

protected:

	CScrollWindow();
	virtual ~CScrollWindow();

	virtual CStr	GetDebugString() const;

	virtual bool	HandleMsg(const CMessage& p_rxMessage);
	virtual void	Paint(const CPaintContext& p_rxCtx);

	virtual bool	OnResize();
	virtual bool	OnVisualizationChange();

	CBasicScrollWindow*	m_pxClientArea;			///< client area window
	CScrollBar*			m_pxHScrollBar;			///< horizontal scrollbar
	CScrollBar*			m_pxVScrollBar;			///< vertical scrollbar

	int					m_iHScrollType;			///< horizontal scrolling type
	int					m_iVScrollType;			///< vertical scrolling type
};

#include "scrollwindow.inl"

} // namespace UILib

#endif // ifndef UILIB_SCROLLWINDOW_H_INCLUDED

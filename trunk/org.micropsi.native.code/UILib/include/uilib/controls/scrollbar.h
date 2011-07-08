#ifndef UILIB_SCROLLBAR_H_INCLUDED 
#define UILIB_SCROLLBAR_H_INCLUDED

#include "uilib/core/window.h"
#include "uilib/controls/button.h"


namespace UILib
{

class CScrollBar : public CWindow
{
public:
	/// erzeugt einen neuen CScrollBar
	static CScrollBar*	Create();

	enum ScrollBarStyles
	{
		SB_Horizontal = 1 << 0,			///< horizontaler Scrollbar
		SB_Vertical   = 1 << 1,			///< verticaler Scrollbar
		SB_NoButtons  = 1 << 2			///< keine Buttons anzeigen
	};	

	/// bestimmt die Größe des Fensters
	virtual void	SetSize(const CSize& p_rxSize);

	/// schaltet die Buttons ein oder aus (default ist ein)
	void			SetButtons(bool p_bButtons = true);

	/// liefert true wenn Buttons vorhanden sind
	bool			GetHasButtons() const;

	/// bestimmt den Stil des Scrollbars (z.B. vertikal oder horizontal, Buttons ja/nein)
	void			SetStyle(int p_iStyle);

	/// bestimmt Maximalwert des Scrollers (vgl. Unterschied zu GetScrollLimit())
	void			SetScrollRange(int p_iRange);

	/// bestimmt die Scroll-Position; muss zwischen 0 und GetScrollLimit() liegen
    int				SetScrollPos(int p_iScrollPos);

	/// bestimmt die Größe einer Seite; wird benutzt, um die Größe des Griffs zu berechnen
    int				SetPageSize(int p_iPageSize);

	/// bestimmt, wie viele Einheiten gescrollt wird, wenn der User die Button drückt (default ist 1)
	int				SetScrollSpeed(int p_iSpeed);

	/// liefert die Scroll-Position; liegt zwischen 0 und GetScrollLimit()
	int				GetScrollPos() const;

	/// liefert Maximalwert des Scrollers (vgl. Unterschied zu GetScrollLimit())
	int				GetScrollRange() const;

	/// liefert die Größe einer Seite; wird benutzt, um die Größe des Griffs zu berechnen
	int				GetPageSize() const;

	/// liefert tatsächlich annehmbaren Maximalwert des Scrollers; d.h. unter Berücksichtigung der Seitengröße
	int				GetScrollLimit() const;

	/// setzt den Wert eines benannten Attributes
	virtual bool	SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);

	/// liefert den Wert eines benannten Attributes
	virtual bool	GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;

	/// zum Überschreiben: wird aufgerufen, wenn sich an der Position des ScrollBars etwas ändert 
	virtual bool	OnChange();

	/// setzt Callbackfunktion für "Change"
	void			SetOnChangeCallback(CFunctionPointer1<CScrollBar*>& rxCallback);

protected:

	CScrollBar();
	virtual ~CScrollBar();

	/// löscht dieses Fenster sofort (sollte nur der WindowMgr machen)
	virtual void		DeleteNow();

	/// Lokale Klasse für Scrollbar-Buttons
	class CScrollBarButton : public CBasicButton
	{
	public:		
		CScrollBarButton() { m_eButtonType = CVisualization::BT_UpArrow; }
		void SetButtonType(CVisualization::ButtonType p_eButtonType);
	protected:
		virtual void Paint(const CPaintContext& p_rxCtx);

		CVisualization::ButtonType		m_eButtonType;			///< Symbol des Buttons (Pfeil hoch/runter/links/rechts)
	private:
		CScrollBarButton(const CScrollBarButton&) {}
		operator=(const CScrollBarButton&) {}
	};


	/// liefert String für Debug-Zwecke
	virtual CStr GetDebugString() const;

	void CalcKnobRect();

	virtual bool HandleMsg(const CMessage& p_rxMessage);
	virtual void Paint(const CPaintContext& p_rxCtx);

	virtual bool OnLButtonDown(const CPnt& p_rxMousePos);
	virtual bool OnLButtonUp(const CPnt& p_rxMousePos);
	virtual bool OnMouseMove(const CPnt& p_rxMousePos);

	virtual bool OnResize();
	virtual bool OnVisualizationChange();

	int			m_iScrollPos;				///< aktuelle Scroller-Position
	int			m_iScrollRange;				///< maximale Scroller-Position
	int			m_iPageSize;				///< Größe einer Seite
	int			m_iScrollSpeed;				///< Anzahl Einheiten, die bei einen Buttondruck gescrollt werden
	int			m_iStyle;					///< Stil
	bool		m_bSoftDrag;				///< true: Griff kann Stufenlos mit der Maus gezogen werden; snappt erst beim loslassen

	CSize		m_xButtonSize;				///< Größe der Buttons; meistens braucht man eher m_xRealButtonSize
	CSize		m_xRealButtonSize;			///< == m_xButtonSize wenn Buttons eingeschaltet sind, aber (0, 0) wenn nicht

	bool		m_bDragging;				///< true, wenn der User den Griff momentan mit der Maus zieht
	CRct		m_xKnobRct;					///< Rechteck des Griffs, wird von CalcKnobRect() berechnet
	CPnt		m_xDragPoint;				///< während Griff mit Maus gezogen wird: Position der Maus relativ zum Griff

	CScrollBarButton*	m_pxButton1;		///< linker oder oberer Button; 0 wenn der Scrollbar keine Buttons hat
	CScrollBarButton*	m_pxButton2;		///< rechter oder unterer Button; 0 wenn der Scrollbar keine Buttons hat

	CFunctionPointer1<CScrollBar*>	m_xOnChangeCallback;	///< Callbackfunktion bei jeder Veränderung

private:
	CScrollBar(const CScrollBar&);
	operator=(const CScrollBar&);
};

#include "scrollbar.inl"

static const char* msgScrollBarChanged = "BtnClick";
class CScrollBarChangedMsg : public CMessage
{ public: CScrollBarChangedMsg(WHDL hWnd) : CMessage(msgScrollBarChanged, false, true, hWnd)	{} };


} //namespace UILib


#endif	// ifndef UILIB_SCROLLBAR_H_INCLUDED 


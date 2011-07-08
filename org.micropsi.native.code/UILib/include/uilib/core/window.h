#ifndef UILIB_WINDOW_H_INCLUDED
#define UILIB_WINDOW_H_INCLUDED

#include <deque>

#include "baselib/debugprint.h"
#include "baselib/functionpointer.h"
#include "windowhandle.h"
#include "message.h"
#include "paintcontext.h"
#include "mousecursor.h"

namespace UILib 
{

class COutputDevice;


class CWindow
{	
public:

	/// erzeugt ein neues Fenster
	static CWindow*		Create();

	/// löscht dieses Fenster (verzögert)
	virtual void		Destroy();
	
	/// liefert das Fensterhandle
	WHDL				GetWHDL() const;
	
	/// erklärt die komplette Zeichenfläche des Fensters für ungültig
	void				InvalidateWindow();	

	void				InvalidateWindow(const CRct& p_xrRect);	

    /// bestimmt den Tooltip-Text dieses Fenster; auf "" setzen, um den Tooltip zu entfernen
	void				SetToolTipText(CStr p_sText);


// --- Fenstergröße -------------------------------------------------------------------------------

	/// setzt die Größe des Fensters
	virtual void		SetSize(const CSize& p_rxSize);

	/// setzt die Größe des Fensters
	void				SetSize(int p_iWidth, int p_iHeight);

	/// stellt sicher, dass Fenster momentan mindestens so groß ist; verkleinert Fenster notfalls
	virtual void		AssureMinSize(const CSize& p_rxSize);

	/// stellt sicher, dass Fenster momentan höchstens so groß ist; verkleinert Fenster notfalls
	virtual void		AssureMaxSize(const CSize& p_rxSize);

	/// Bringt fenster automatisch auf optimale Größe (hängt u.a. vom Fenstertyp und dessen Parametern ab)
	virtual void		AutoSize(bool p_bMayShrink = true);

	/// Setzt Größe des Fensters
	CSize				GetSize() const;

	/// Setzt Maximalgröße; Fenster kann niemals kleiner werden als Maximalgröße
	void				SetMinSize(const CSize& p_rxMinSize);

	/// Setzt Minimalgröße; Fenster kann niemals kleiner werden als Minmalgröße
	void				SetMaxSize(const CSize& p_rxMaxSize);

	/// Begrenzt die Minimalgröße des Fensters; d.h. Minimalgröße wird ggf. erhöht, um in Rechteck zu passen (aber nicht reduziert)
	void				ConstraintMinSize(const CSize& p_rxMinSize);

	/// Begrenzt die Maximalgröße des Fensters; d.h. Maximalgröße wird ggf. reduziert, um in Rechteck zu passen (aber nicht erhöht)
	void				ConstraintMaxSize(const CSize& p_rxMaxSize);

	/// liefert Minimalgröße des Fenster
	CSize				GetMinSize() const;
	
	/// liefert Maximalgröße des Fensters
	CSize				GetMaxSize() const;

	/// liefert relatives Rechteck dieses Fensters (0, 0, Breite, Höhe)
	virtual CRct		GetRect() const;
	
	/// liefert das von diesem Fenster überdeckte Rechteck in absoluten Koordinaten
	CRct				GetAbsRect() const;
	

// --- Fensterposition -------------------------------------------------------------------------------

	/// setzt die Position des Fensters relativ zu seinem Elternfenster (außer Top Level Windows; die sind absolut)
	virtual void		SetPos(CPnt p_pntPos);
	
	/// setzt die Position des Fensters relativ zu seinem Elternfenster (außer Top Level Windows; die sind absolut)
	void				SetPos(int p_iX, int p_iY);

	/// liefert die Position des Fensters relativ zu seinem Elternfenster 
	CPnt				GetPos() const;

	/// liefert die absolute Position des Fensters
	CPnt				GetAbsPos() const;
	
	/// konvertiert absolute Koordinaten in relative Fensterkoordinaten
	CPnt				ConvertToClientPos(const CPnt& p_rxAbsPos) const;

	// zentriert das Fenster in seinem Parent (sofern vorhanden)
    void				CenterOnParentWindow();	

// --- Eltern- und Kindfenster --------------------------------------------------------------------

	/// liefert das allerunterste Elternfenster
	CWindow*			GetRootWindow() const;

	/// liefert (physisches) Elternfenster
	WHDL				GetParent() const;

	/// liefert true, wenn dieses Fenster ein Kind (über beliebig viele Ebenen) vom Parameter-Fenster ist
	bool				IsChildOf(WHDL p_hWnd) const;

	/// entfernt Fenster von seinem Elternfenster
	void				RemoveFromParent();

	/// liefert die Anzahl der (logischen) Kindfenster (logisch: Fensterklassen können die Kindbeziehung anders implementieren)
	virtual int			NumChildWindows() const;

	/// liefert die Anzahl der (physischen) Kindfenster
	int					NumPhysicalChildWindows() const;

	/// liefert ein logisches Kindfenster
	virtual WHDL		GetChild(int p_iIndex) const;

	/// liefert ein physisches Kindfenster
	WHDL				GetPhysicalChild(int p_iIndex) const;

	/// entfernt physisches Kindfenster
	void				RemovePhysicalChild(WHDL p_hWnd);

	/// fügt (logisches) Kindfenster hinzu
	virtual bool		AddChild(WHDL p_hWnd);

	/// fügt (logisches) Kindfenster hinzu
	bool				AddChild(CWindow* p_pxWindow);

	/// entfernt (logisches) Kindfenster
	virtual void		RemoveChild(WHDL p_hWnd);


// --- Cursor -------------------------------------------------------------------------------------

	/// bestimmt den Mauscursor, der innerhalb dieses Fensters dargestellt werden soll
	void				SetCursor(int p_iCursorType);

	/// setzt den Standardmauscursor für dieses Fenster
	void				SetStandardCursor();

	/// liefert den Mauscursor, der innerhalb dieses Fensters dargestellt werden soll
	CMouseCursor::CursorType GetCursor() const;
	

// --- Zustand und Flags des Fensters -------------------------------------------------------------


	/// set disabled state of window 
	virtual void		SetDisabled(bool p_bDisabled = true);

	/// \return true if window is disabled
	bool				GetDisabled() const;

	/// set visible state of window
	virtual void		SetVisible(bool p_bVisible = true);

	/// \return true if window is disabled
	bool				GetVisible() const;

	/// set tranparent state of window
	virtual void		SetTransparent(bool p_bTransparent = true);

	/// \return true if window is transparent
	bool				GetTransparent() const;

	/// liefert true, wenn dieses Fenster oder ein Kind von ihm den Fokus hat
	virtual bool		HasFocusOrChildHasFocus() const;

	/// liefert true, wenn dieses Fenster den Fokus hat
	bool				HasFocus() const;

	/// liefert true, wenn dieses Fenster aktiv werden darf, selbst, wenn ein anderes Fenster modal ist
	virtual bool		GetIgnoreModals() const;

	///	erlaubt dem Fenster die Aktivierung, selbst dann, wenn ein anderes Fenster modal ist 
	virtual void		SetIgnoreModals(bool p_bIgnore = true);

	/// liefert true, wenn dieses Fenster immer im Vordergrund ist
	virtual	bool		GetAlwaysOnTop() const;

	///	bestimmt, ob dieses Fenster immer im Vordergrund sein muss
	virtual void		SetAlwaysOnTop(bool p_bAlwaysOnTop = true);

	/// liefert true, wenn dieses Fenster den Fokus erhalten kann
	virtual bool		GetCanReceiveFocus() const;

	///	bestimmt, ob das Fenster den Fokus erhalten kann
	virtual void		SetCanReceiveFocus(bool p_bCanFocus = true);

	/// liefert true, wenn dieses Fenster ein Deskop ist (d.h. deckt ein Device komplett ab, hat keine Elternfenster)
	bool				GetIsDesktop() const;

	/// liefert true, wenn dieses Fenster ein Top Level Window ist (d.h. Elternfenster ist ein Desktop)
	bool				GetIsTopLevelWindow() const;

	/// bestimmt, ob dieses Fenster Meldungen erhalten will, wenn sich der Status von HasFocusOrChildHasFocus() ändert (default: nein) 
	void				SetIndirectActivationMessages(bool b);

	/// true: dieses Fenster schreibt seinen Alphakanal einfach auf das Device; false: Alphablending mit Device
	void				SetWriteAlpha(bool p_bAlpha = true);

	/// true: dieses Fenster schreibt seinen Alphakanal einfach auf das Device; false: Alphablending mit Device
	bool				GetWriteAlpha() const;

	/// aktiviert dieses Fenster, d.h. holt es in den Vordergrund und gibt ihm den Eingabefokus
	void				SetFocus() const;

// --- Textuelle Attribute ------------------------------------------------------------------------


	/// setzt den Wert eines benannten Attributes
	virtual	bool		SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);

	/// liefert den Wert eines benannten Attributes
	virtual	bool		GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;


// --- Visualisierungen ---------------------------------------------------------------------------

	/// ändert die aktuelle Visualisierung des Fensters
	virtual void		ChangeVisualization(CFourCC p_xType);

	/// liefert den Typ der aktuellen Visualisierung
	CFourCC				GetVisualizationType() const;


// --- Timer --------------------------------------------------------------------------------------

	/// stellt einen neues Timer für dieses Fenster
	int					SetTimer(int p_iInterval, bool p_bRepeating);

	/// löscht einen Timer
	bool				UnsetTimer(int p_iID);

	/// verändert die Paramter eines existierenden Timers
	bool				ResetTimer(int p_iID, int p_iInterval, bool p_bRepeating);


// --- Callback-Definitionen ----------------------------------------------------------------------

	/// Mausbewegung
	virtual void		SetOnMouseMoveCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback);

	/// linke Maustaste heruntergedrückt
	virtual void		SetOnLButtonDownCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback);

	/// rechte Maustaste heruntergedrückt
	virtual void		SetOnRButtonDownCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback);

	/// mittlere Maustaste heruntergedrückt
	virtual void		SetOnMButtonDownCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback);

	/// linke Maustaste losgelassen
	virtual void		SetOnLButtonUpCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback);

	/// rechte Maustaste losgelassen
	virtual void		SetOnRButtonUpCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback);

	/// mittlere Maustaste losgelassen
	virtual void		SetOnMButtonUpCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback);

	/// Doppelklick linke Maustaste
	virtual void		SetOnLButtonDoubleClickCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback);

	/// Doppelklick rechte Maustaste
	virtual void		SetOnRButtonDoubleClickCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback);

	/// Doppelklick mittlere Maustaste
	virtual void		SetOnMButtonDoubleClickCallback(CFunctionPointer2<CWindow*, const CPnt&>& rxCallback);

protected:

	/// Default Konstruktor
	CWindow();
	
	/// Destruktor
	virtual ~CWindow();

	/// liefert die relative Position des Fensters
	CPnt				GetRelPos() const;

	/// löscht dieses Fenster sofort (sollte nur der WindowMgr machen)
	virtual void		DeleteNow();

	/// behandelt Messages
	virtual bool		HandleMsg(const CMessage& p_rxMessage);	

	/// behandelt Mouse-Messages (wird nur intern verwendet)
	bool				HandleMouseMsg(const CMessage& p_rxMessage);	

	/// testet, ob dieses Fenster für die übergebenen (absolute) Position verantwortlich ist
	virtual CWindow*	HitTest(const CPnt& p_rxMousePos);
	
	/// zeichnet das Fenster
	virtual void		Paint(const CPaintContext& p_rxCtx);

	/// behandelt Messages im Nachrichtenpuffer
	void				MessagePump();


	void	SetParentDisabled(bool p_bParentDisabled);
	void	SetParentVisible(bool p_bParentVisible);
	void	OnDisabledStateChange();
	void	OnVisibleStateChange();


// --- überladbare Funktionen  --------------------------------------------------------------------


	/// wird gerufen, wenn eine Timer-Message empfangen wird 
	virtual bool		OnTimer(int p_iID);

	/// wird gerufen, wenn Fenster Fokus erhalten hat
	virtual bool		OnActivate();

	/// wird gerufen, wenn Fenster Fokus verloren hat
	virtual bool		OnDeactivate();

	/// Reaktion darauf, dass HasFocusOrChildHasFocus() von false auf true gewechselt hat 
	virtual bool		OnActivateIndirect();

	/// Reaktion darauf, dass HasFocusOrChildHasFocus() von true auf false gewechselt hat 
	virtual bool		OnDeactivateIndirect();

	/// wird gerufen, wenn Fenster einen Zeichen-Tastendruck erhält
	virtual bool		OnCharacterKey(int p_iKey, unsigned char p_iModifier);

	/// wird gerufen, wenn Fenster einen Steuerungs-Tastendruck erhält
	virtual bool		OnControlKey(int p_iKey, unsigned char p_iModifier);

	/// wird gerufen, wenn die Größe des Fensters sich ändert
	virtual bool		OnResize();

	/// wird gerufen, wenn das zugrundeliegende Device sich ändert
	virtual bool		OnDeviceChange();

	/// wird gerufen, wenn die zugrundeliegende Visualisierung sich ändert
	virtual bool		OnVisualizationChange();

	/// wird gerufen, wenn das Fenster sich zeichnen soll
	virtual bool		OnPaint();

	/// Mausbewegung
	virtual bool		OnMouseMove(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier);

	/// linke Maustaste heruntergedrückt
	virtual bool		OnLButtonDown(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier);

	/// rechte Maustaste heruntergedrückt
	virtual bool		OnRButtonDown(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier);

	/// mittlere Maustaste heruntergedrückt
	virtual bool		OnMButtonDown(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier);

	/// linke Maustaste losgelassen
	virtual bool		OnLButtonUp(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier);

	/// rechte Maustaste losgelassen
	virtual bool		OnRButtonUp(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier);

	/// mittlere Maustaste losgelassen
	virtual bool		OnMButtonUp(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier);

	/// Doppelklick linke Maustaste
	virtual bool		OnLButtonDoubleClick(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier);

	/// Doppelklick rechte Maustaste
	virtual bool		OnRButtonDoubleClick(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier);

	/// Doppelklick mittlere Maustaste
	virtual bool		OnMButtonDoubleClick(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier);


	/// liefert Stringdarstellung dieses Fenster für Debugzwecke
	virtual CStr		GetDebugString() const;


	CMouseCursor::CursorType	m_eCursor;		///< Cursor Typ

	CDynArray<WHDL>				m_ahSubs;		///< Kindfenster, Reihenfolge im Array ist z-Order - letztes = oben
	std::deque<CMessage>		m_xMsgQueue;	///< Message queue

private:
	
	/// bestimmt des Elternfenster; sollte nur von AddChild() benutzt werden
	void	SetParent(WHDL p_hParent);

	WHDL	m_hWnd;					///< Fensterhandle dieses Fensters
	WHDL	m_hParent;				///< Elternfenster

	CFourCC	m_xVisType;				///< Visualisierungstyp

	bool	m_bDisabled;			///< enabled/disabled
	bool	m_bParentDisabled;		///< Elternfenster ist disabled
	bool	m_bVisible;				///< Fenster ist sichtbar/unsichtbar
	bool	m_bParentVisible;		///< Elternfester ist sichtbar/unsichtbar
	bool	m_bTransparent;			///< bei transparenten Windows wird vorher der (ansonsten verdeckte) Hintergrund gezeichnet
	bool	m_bAllWndsTransparent;	///< true, wenn alle Fenster dieser Visualisierung transparent sein müssen
	bool	m_bDesktop;				///< true, wenn dieses Fenster ein Desktop ist
	bool	m_bIgnoreModals;		///< true, wenn dieses Fenster trotz eines anderen, modalen Fensters den Fokus bekommen kann
	bool	m_bAlwaysOnTop;			///< true, wenn dieses Fenster immer ganz oben in der Z-Order sein soll
	bool	m_bCanReceiveFocus;		///< true, wenn dieses Fenster den Fokus erhalten darf
	bool	m_bWriteAlpha;			///< true: Alphakanal soll in Device geschrieben werden; false: Alphablend mit Device


	CPnt	m_xPos;					///< Position des Fensters, releativ zum Elternfenster
	CSize	m_xSize;				///< Größe des Fensters
    CSize	m_xMinSize;				///< minimal erlaubte Größe für dieses Fenster
	CSize	m_xMaxSize;				///< maximal erlaubte Größe für dieses Fenster; (-1, -1) bedeutet unbeschränkt
	CStr	m_sToolTipText;			///< Tooltip-Text, kann leer sein (d.h. kein Tooltip)

	CFunctionPointer2<CWindow*, const CPnt&>	m_xOnMouseMoveCallback;
	CFunctionPointer2<CWindow*, const CPnt&>	m_xOnLButtonDownCallback;
	CFunctionPointer2<CWindow*, const CPnt&>	m_xOnRButtonDownCallback;
	CFunctionPointer2<CWindow*, const CPnt&>	m_xOnMButtonDownCallback;
	CFunctionPointer2<CWindow*, const CPnt&>	m_xOnLButtonUpCallback;
	CFunctionPointer2<CWindow*, const CPnt&>	m_xOnRButtonUpCallback;
	CFunctionPointer2<CWindow*, const CPnt&>	m_xOnMButtonUpCallback;
	CFunctionPointer2<CWindow*, const CPnt&>	m_xOnLButtonDoubleClickCallback;
	CFunctionPointer2<CWindow*, const CPnt&>	m_xOnRButtonDoubleClickCallback;
	CFunctionPointer2<CWindow*, const CPnt&>	m_xOnMButtonDoubleClickCallback;

	
	CWindow(const CWindow&);
	operator=(const CWindow&);

	friend class CWindowMgr;
};

#include "window.inl"

} // namespace UILib


#endif // ifndef UILIB_WINDOW_H_INCLUDED


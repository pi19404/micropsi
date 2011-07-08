#pragma once
#ifndef UILIB_WINDOWMANAGER_H_INCLUDED
#define UILIB_WINDOWMANAGER_H_INCLUDED

#include <stdio.h>
#include <vector>

#include "baselib/dynarray.h"
#include "baselib/fourcc.h"
#include "message.h"
#include "baselib/handledset.h"
#include "timer.h"
#include "paintcontext.h"

namespace UILib
{

class	COutputDevice;
class	CWindow;


class CWindowMgr
{
protected:

	CWindowMgr();
	~CWindowMgr();

	static CWindowMgr*		ms_pxInst;				///< einzige Instanz des WindowMgr (Singleton)
	
	struct TDevInfo									
	{
		COutputDevice*		m_pxDevice;				///< Ausgabedevice
		CRct				m_xArea;				///< vom Device bedeckte Fläche
		CWindow*			m_pxDesktop;			///< Desktop Fenster
		bool				m_bOwnDesktop;			///< true, wenn Desktop dem WindowMgr gehört (d.h. von ihm gelöscht werden muss)
	};
	CDynArray<TDevInfo>		m_axAllDevices;			///< alle angemeldeten Devices
	
	/// definiert zusätzliche Flags für Eigenschaften des Fensters
	enum WindowProperties
	{
		WP_WantMouseEnterAndLeave =  1 << 0,		///< möchte MouseLeave und MouseEnter-Messages erhalten
		WP_WantIndirectActivateMsg = 1 << 1			///< möchte Message, wenn seine Kinder Fokus erhalten/verlieren
	};

	/// enthält die gesamte mit einem Fenster assoziierte Information
	struct TWindowInfo
	{
		CWindow*			m_pxWnd;				///< Zeiger auf eigentliches Fenster
		COutputDevice*		m_pxDevice;				///< Zeifer auf Device dieses Fensters (kann wechseln)
		unsigned long		m_iWindowProps;			///< zusätzliche Eigenschaften (Flags); siehe enum WindowProperties
	};

	CHandledSet<TWindowInfo>	m_axAllWindows;		///< alle existierenden Fenster
	CDynArray<CWindow*,5,false> m_apxDeleteList;	///< Liste der am Ende des Ticks zu löschenden Fenster
	WHDL						m_hTopWindow;		///< Fenster, das den Fokus hat
	WHDL						m_hMouseWindow;		///< Fenster, das zuletzt ein Mouse-Enter bekommen hat
	CPnt						m_xMousePos;		///< aktuelle Position der Maus
	CDynArray<CWindow*>			m_apxCaptureWnd;	///< stack mit Fenstern, die die Maus gecaptured haben
	CDynArray<CWindow*>			m_apxModalWnd;		///< stack mit Fenstern, die modal sind
	
	CFourCC						m_xStandardVisType;	///< Standard-Visualisierung für neue Fenster

	/// registiert ein neues Fenster beim WindowMgr; nur der Konstruktor von CWindow macht das
	WHDL			RegisterWindow(CWindow* p_pxWnd);	
	
	/// meldet das Fenster beim WindowMgr ab, nur der Destruktor von CWindow macht das
	void			UnregisterWindow(CWindow* p_pxWnd);
	
	/// liefert das Device Info des Devices (falls existent)
	TDevInfo*		GetDevInfo(const COutputDevice* p_pxDevice) const;	

	/// liefert einen Pointer auf das OutputDevice des Fensters; kann 0 sein, falls das Fenster momentan kein Device hat
	COutputDevice*	GetDevice(WHDL p_hWnd) const;

	/// überprüft, ob das Fenster, von dem wir denken, das es unter der Maus liegt, wirklich unter der Maus liegt
	void			CheckHoveredWindow(); 

	/// errechnet die UILib-Mouseposition aus einer Windows-Message
	CPnt			GetMousePos(HWND p_hWnd, LPARAM p_lParam);

	/// Unterfunktion von DoPaint()
	void			DoPaintSub(CPaintContext p_xCtx, CWindow* p_pxWnd, CRctList p_rxDamageList);

	/// DebugFunktion: testet timers-pq
	void			CheckTimerPQ(); 


	/// behandelt die Timer (löscht abgelaufene Timer und erzeugt Timer-Nachrichten)
	void			HandleTimers();

	/// bearbeitet Nachrichten in den Warteschlangen der Fenster 
	void			HandleWindowMessages();

	/// aktualisiert die ToolTips
	void			UpdateToolTips();

    /// löscht alle Fenster, die in der Delete-List stehen.
	void			ExecuteDeleteList();


	std::vector<CTimer*>			m_apxTimersPQ;				///< Prioritätswarteschlange aller Timer
	CHandledSet<CTimer*>			m_apxAllTimers;				///< Menge aller Timer - nur als Pointer!

	WHDL							m_xToolTip;					///< ToolTip-Fenster, kann 0 sein
	__int64							m_iLastMouseActivityTimeMS;	///< Zeitpunkt der letzen Mousebeweung, in Millisekunden
	static const __int64			m_iToolTipDelay = 1000;		///< Verzögerung bis zum Einschalten der ToolTips, in MS

	friend class CWindow;

public:
	
	/// get window manager instance
	static	CWindowMgr& Get();
	
    static  void        Shut();

	/// liefert Zeiger für ein Fensterhandle oder 0, falls das Fenster nicht existiert.
	CWindow* GetWindow(WHDL p_hWnd) const;

	/// liefert true, wenn das Fensterhandle gültig ist
	bool	IsValid(WHDL p_hWnd) const;

	/// delayed delete of window
	void	DeleteWindowDelayed(CWindow* p_pxWindow);

	/// get const device; cannot be used for drawing, only for determining metrics
	const COutputDevice* GetDeviceConst(WHDL p_hWnd) const;

	/// set device of window
	void	SetDevice(WHDL p_hWnd, COutputDevice* p_pxDevice);

	/// get value of property flag of window
	const bool GetWindowProperty(WHDL p_hWnd, WindowProperties p_eProp) const;

	/// set value of property flag of window
	void	SetWindowProperty(WHDL p_hWnd, WindowProperties p_eProp, bool p_bValue);

	///  brings the specified window to the top of the Z order
	void	BringWindowToTop(WHDL p_hWnd, bool p_bActivate = true);


	/// get window that has the (keyboard) focus
	WHDL	GetFocusWindow() const;

	/// sucht Fenster, das momentan für bestimmte absolute Bildschirmkoordinaten verantwortlich ist
	CWindow* HitTest(const CPnt& p_rxMousePos) const;
	
	/// handle message
	void	HandleMsg(WHDL p_hWndTarget, const CMessage& p_rxMessage);	
	
	/// invalidate window
	void	Invalidate(WHDL p_hWnd);
	
	/// invalidate windowrect
	void	Invalidate(WHDL p_hWnd, const CRct& p_rxRect);
	
	/// invalidate window rectlist
	void	Invalidate(WHDL p_hWnd, const CRctList& p_rxRectList);
	
	/// invalidate device
	void	Invalidate(COutputDevice* p_pxDevice);
	
	/// invalidate rect on device p_pxDevice
	void	Invalidate(COutputDevice* p_pxDevice, const CRct& p_rxRect);
	
	/// add new device
	WHDL	AddDevice(COutputDevice* p_pxDevice, CRct p_rectArea, CWindow* p_pxDesktopWindow = 0);

	/// release device
	void	RemoveDevice(COutputDevice* p_pxDevice);

	/// add window p_hWndwindow as top level window of device 0 (there must at least be one registered device)
	void	AddTopLevelWindow(WHDL p_hWnd);

	/// remove window p_hWndwindow 
	void	RemoveTopLevelWindow(WHDL p_hWnd);	

	/// \return true if point coordinates are on any registered device 
	bool	IsPointOnAnyDevice(CPnt p_xPos);
	
	/// \return true: window is on new device now, false: window is still on same device
	bool	WindowDeviceChange(CWindow* p_pxWnd);

	/// set capture window
	void	SetCapture(CWindow* p_pxWnd);
	
	/// get window that has captured the mouse (0 if none)
	CWindow* GetCapture() const;
	
	/// release capture
	void	ReleaseCapture(CWindow* p_pxWnd);

	/// set window to modal
	void	SetModal(CWindow* p_pxWnd);
	
	/// get current modal window (or 0 if none)
	CWindow* GetModalWindow() const;
	
	/// window is no longer modal
	void	ReleaseModal(CWindow* p_pxWnd);

	/// get current mouse cursor position (absolute)
	CPnt	GetMousePos() const;

	/// convert point from global to client coordinates
	void	ToClientPos(WHDL p_hWnd, CPnt& p_pntMouse);

	/// paint all windows
	void	DoPaint();

	/// do maintainance tick
	void	Tick();

	/// send message; message is processed immediatly 
	void	SendMsg(const CMessage& p_rxMsg, WHDL p_hWndTarget);

	/// post message; message is put into target windows message queue
	void	PostMsg(const CMessage& p_rxMsg, WHDL p_hWndTarget);
	
	/// send windows message
	bool	SendWindowsMessage(HWND p_hWnd, UINT message, WPARAM wParam, LPARAM lParam);

	/// determine target window for message
	WHDL	GetMsgTargetWindow(const CMessage& p_rxMsg);

	/// determines pressed modifier keys of current processed message
	unsigned char GetKeyModifierState() const;

	/// \return true if capslock-key is locked during current processed input message	
	bool	GetCapKeyState() const;	

	/// copy string to clipboard
	bool	FillClipBoard(const CStr& p_sString) const;

	/// get contents of clipboard
	CStr	GetClipBoardContents() const; 

	/// set a timer
	int		SetTimer(WHDL p_hWindow, int p_iInterval, bool p_bRepeating = false);
	
	/// unset (remove) a timer
	bool	UnsetTimer(int p_iID);

	/// reset a timer (change its parameters)
	bool	ResetTimer(int p_iID, int p_iInterval, bool p_bRepeating = false);

	/// set standard visualization type
	void	SetStandardVisualization(const CStr& p_rsType);

	/// set standard visualization type
	void	SetStandardVisualizationType(CFourCC p_xType);

	/// get standard visualization type
	CFourCC	GetStandardVisualizationType();

	/// debug dump
	void	Dump(bool p_bVisibleOnly = false);

	/// debug dump
	void	Dump(CStr p_sFilename, bool p_bVisibleOnly = false);

	/// debug dump - subtree
	void	DumpSubTree(FILE* p_f, CWindow* p_pxWindow, int p_iIndent, bool p_bVisibleOnly);

	/// liefert desktop mit der angegebenen Nummer oder 0, fall er nicht existiert
	CWindow* GetDesktop(unsigned int p_iIndex = 0) const;

	/// liefert das Desktopfenster des angegebenen Devices oder 0, falls das Device nicht registriert ist
	CWindow* GetDesktopByDevice(const COutputDevice* p_pxDevice) const;

	/// liefert das Fenster das momentan unter dem Mousecursor ist
	CWindow* GetHoveredWindow() const;

	/// get desktop window  that is currently under the mouse cursor
	CWindow* GetHoveredDesktop() const;

	/// window wants to receive mouse enter and mouse leave messages
	void	SetMouseEnterAndLeaveMsg(WHDL p_hWnd, bool b);

	/// window wants to receive indirect activation / deactivation messages
	void	SetIndirectActivationMessages(WHDL p_hWnd, bool b);

	void	PrintWindowTree(WHDL p_hWindow, int p_iTab=0);
};

#include "windowmanager.inl"

} // namespace UILib


#endif // ifndef UILIB_WINDOWMANAGER_H_INCLUDED


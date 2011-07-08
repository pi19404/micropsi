#include "stdafx.h"
#include <stdio.h>
#include <windows.h>
#include <windowsx.h>
#include <algorithm>

#include "uilib/core/windowmanager.h"
#include "uilib/core/virtualkeycodes.h"
#include "uilib/core/outputdevice.h"
#include "uilib/core/paintcontext.h"
#include "uilib/controls/panel.h"
#include "uilib/controls/tooltip.h"

namespace UILib
{

CWindowMgr* CWindowMgr::ms_pxInst = 0;

//-------------------------------------------------------------------------------------------------------------------------------------------------------------
CWindowMgr::CWindowMgr()
{
	m_iLastMouseActivityTimeMS = CTimer::GetSystemTimeInMS();
	m_xStandardVisType = "STND";
}

//-------------------------------------------------------------------------------------------------------------------------------------------------------------
CWindowMgr::~CWindowMgr()
{
	for(unsigned int i=0; i<m_axAllDevices.Size(); ++i)
	{
		if(m_axAllDevices[i].m_bOwnDesktop)
		{
			m_axAllDevices[i].m_pxDesktop->DeleteNow();
		}
	}
	m_axAllDevices.Clear();

	// ein letzter Tick, damit die lösch-Queue noch abgearbeitet wird
	Tick();

	if(m_axAllWindows.Size() > 0)
	{
		DebugPrint("WindowMgr: Error: %d windows still present at shutdown", m_axAllWindows.Size());
	}
}

//-------------------------------------------------------------------------------------------------------------------------------------------------------------
void 
CWindowMgr::Shut()
{
    if(ms_pxInst)
    {
        delete ms_pxInst;
        ms_pxInst = 0;
    }
}

//-------------------------------------------------------------------------------------------------------------------------------------------------------------
///	registriert neues Fenster; sollte nur vom Konstruktor von CWindow gemacht werden
WHDL 
CWindowMgr::RegisterWindow(CWindow* p_pxWnd)
{
	unsigned int uiHandle = m_axAllWindows.PushEntry();
	TWindowInfo& xDevInfo = m_axAllWindows.Element(uiHandle);
	xDevInfo.m_pxWnd = p_pxWnd;
	xDevInfo.m_pxDevice  = 0;
	xDevInfo.m_iWindowProps = 0;
	return WHDL(uiHandle);	
}


//-------------------------------------------------------------------------------------------------------------------------------------------------------------
/**
	Löscht die Registrierung des übergebenen Fensters
	Sollte nur vom Destruktor von CWindow gemacht werden!
	\param p_hWnd	Fensterhandle
*/
void 
CWindowMgr::UnregisterWindow(CWindow* p_pxWnd)
{
	// falls das Fenster die Maus gecaptured hatte --> Capture aufheben
	if(m_apxCaptureWnd.Find(p_pxWnd) >= 0)
	{
		ReleaseCapture(p_pxWnd);
	}

	// falls das Fenster modal war, wird es jetzt aus der Liste genommen
	if(m_apxModalWnd.Find(p_pxWnd) >= 0)
	{
		ReleaseModal(p_pxWnd);
	}

	// falls das Fenster noch Timer laufen hat, werden diese jetzt gelöscht
	CDynArray<unsigned long> aiInvalidTimers;
	unsigned long iter;
	m_apxAllTimers.StartIterate(iter);
	CTimer* pTimer;
	while(m_apxAllTimers.Iterate(iter, pTimer))
	{
		if((pTimer)->m_hWindow == p_pxWnd->GetWHDL())
		{
			aiInvalidTimers.PushEntry(pTimer->m_iID);
		}
	}
	for(unsigned int i=0; i<aiInvalidTimers.Size(); ++i)
	{
		UnsetTimer(aiInvalidTimers[i]);
	}

	
	// wenn es das Fokusfenster war, vergessen wir das ganz schnell
	if(p_pxWnd->GetWHDL() == m_hTopWindow)
	{
		m_hTopWindow = 0;
	}


	m_axAllWindows.DeleteEntry(p_pxWnd->GetWHDL());
}


//-------------------------------------------------------------------------------------------------------------------------------------------------------------
/**
	liefert DevInfo-Struktur für das OutputDevice p_pxDevice
	\param p_pxDevice	Zeiger auf Outputdevice
	\return				Zeiger auf DevInfo-Struktur oder 0, wenn sie das Device nicht registriert ist
*/
CWindowMgr::TDevInfo* 
CWindowMgr::GetDevInfo(const COutputDevice* p_pxDevice) const
{
	int i,iC=m_axAllDevices.Size();
	for(i=0;i<iC;i++)
	{
		if(m_axAllDevices[i].m_pxDevice==p_pxDevice)
		{
			return &m_axAllDevices[i];
		}
	}
	return 0;
}

//-------------------------------------------------------------------------------------------------------------------------------------------------------------
/**
	Bringt das Fenster in der Z-Order nach oben und gibt ihm auf Wunsch den Fokus

	\param p_pWnd		Fensterhandle
	\param p_bActivate	true, wenn dieses Fenster den Fokus bekommen soll
*/
void 
CWindowMgr::BringWindowToTop(WHDL p_hWnd, bool p_bActivate)
{
	if(m_hTopWindow == p_hWnd)
	{
		// Fenster ist schon ganz oben
		return;
	}

	CWindow* pWnd = GetWindow(p_hWnd);

	// überprüfen, ob modales Fenster Fokuswechsel verhindern könnte
	CWindow* pxModalWnd = GetModalWindow();
	if(pxModalWnd)
	{
		if(!pWnd->GetIgnoreModals() &&  pWnd != pxModalWnd  &&  !pWnd->IsChildOf(pxModalWnd->GetWHDL()))
		{
			// es gibt ein modales Fenster, das bin weder ich noch bin ich ein Kind davon und ich habe auch keine
			// Erlaubnis, das zu ignorieren --> ich kann nicht nach vorn geholt werden
			return;
		}
	}

	if(pWnd->GetParent() != 0)
	{
		// Fenster hat ein Elternfenster (das ist Voraussetzung, um die Z-Order ändern zu können)

		BringWindowToTop(pWnd->GetParent(), false);

		CWindow* pParent = GetWindow(pWnd->GetParent());
		unsigned int i = pParent->m_ahSubs.Find(p_hWnd);
		assert(i>=0);

		// suche oberstes Fenster das nicht als "always on top" markiert ist
		unsigned int iTopWindow = pParent->m_ahSubs.TopOfStack();
		while(i<iTopWindow  &&  GetWindow(pParent->m_ahSubs[iTopWindow])->GetAlwaysOnTop())
		{
			iTopWindow--;
		}

		// Fenster nach oben verschieben, falls möglich
		if(i < iTopWindow)
		{
			for(unsigned int j=iTopWindow; j>i; --j)
			{
				CRct xRect = GetWindow(pParent->m_ahSubs[j])->GetAbsRect().Clip(pWnd->GetAbsRect());
				Invalidate(GetDevice(p_hWnd), xRect);
			}
			pParent->m_ahSubs.MoveEntry(i,iTopWindow);
		}
	}
	else
	{
		// hat kein Elternfenster --> entweder ein Desktop oder nicht in einer Hierarchie --> kann die Z-Order nicht ändern
	}


	if(p_bActivate  &&  pWnd->GetCanReceiveFocus())
	{
		// schicke eine Aktivierungsmeldung das neue Fokusfenster und eine Deaktivierung an das alte Fokusfenster	
		if(m_hTopWindow != p_hWnd)
		{
			WHDL hOldTopWnd = m_hTopWindow;
			m_hTopWindow = p_hWnd;

			CWindow* pxTopWnd = GetWindow(m_hTopWindow);
			CWindow* pxOldTopWnd = GetWindow(hOldTopWnd);

			SendMsg(CWindowActivationMsg(), m_hTopWindow);
			SendMsg(CWindowDeactivationMsg(), hOldTopWnd);

			// send indirekt activation / deactivation messages to windows that want them
			CWindow* pxWnd = GetWindow(m_hTopWindow);
			if(pxWnd)
			{
				while(pxWnd->GetParent())
				{
					WHDL hWnd = pxWnd->GetParent();
					pxWnd = GetWindow(hWnd);
					if(GetWindowProperty(hWnd, WP_WantIndirectActivateMsg))
					{
						if(pxOldTopWnd == 0 || !pxOldTopWnd->IsChildOf(hWnd))
						{
							SendMsg(CWindowIndirectActivationMsg(), hWnd);
						}
					}
				}
			}

			pxWnd = GetWindow(hOldTopWnd);
			if(pxWnd)
			{
				while(pxWnd->GetParent())
				{
					WHDL hWnd = pxWnd->GetParent();
					pxWnd = GetWindow(hWnd);
					if(GetWindowProperty(hWnd, WP_WantIndirectActivateMsg))
					{
						if(pxTopWnd == 0 || !pxTopWnd->IsChildOf(hWnd))
						{
							SendMsg(CWindowIndirectDeactivationMsg(), hWnd);
						}
					}
				}
			}
		}
	}
}


//-------------------------------------------------------------------------------------------------------------------------------------------------------------
/**
	liefert Fenster, das momentan für bestimmte absolute Bildschirmkoordinaten verantwortlich ist
	liefert evtl. 0, wenn keins gefunden wird
*/
CWindow* 
CWindowMgr::HitTest(const CPnt& p_rxMousePos) const
{
	if(m_axAllDevices.Size() == 0)
	{
		return 0;
	}

	const TDevInfo* pxDevInfo = 0;
	for(unsigned int i=0; i<m_axAllDevices.Size(); ++i)
	{
		if(m_axAllDevices[i].m_xArea.Hit(p_rxMousePos))
		{
			pxDevInfo=GetDevInfo(m_axAllDevices[i].m_pxDevice);
			break;
		}
	}

	if(pxDevInfo == 0)
	{
		return 0; 
	}


	CWindow* pxHit=pxDevInfo->m_pxDesktop->HitTest(p_rxMousePos);			
	if(pxHit != 0)
	{
		return pxHit;
	}

	return 0;
}


//-------------------------------------------------------------------------------------------------------------------------------------------------------------
/**
	Behandelt eine Nachricht. Die Nachricht wird an das entsprechende Fenster geschickt,
	vorher schaut der WindowMgr sich die Nachricht allerdings selbst an und führt evtl.
	bestimmte Aktionen aus. 
	Mouse-Enter- und Mouse-Leave-Nachrichten werden z.B. hier generiert. 

	\param p_hWndTarget		Zielfenster
	\param p_rxMsg			Nachricht
*/
void 
CWindowMgr::HandleMsg(WHDL p_hWndTarget, const CMessage& p_rxMessage)
{	
	if(p_hWndTarget == 0)
	{
		return;
	}

	CWindow* pxWndTarget=GetWindow(p_hWndTarget);
	if(p_rxMessage == msgMouseLeftButtonDown)
	{
		BringWindowToTop(pxWndTarget->GetWHDL());
	}		

	if(p_rxMessage.IsMouseMessage())
	{
		m_iLastMouseActivityTimeMS = CTimer::GetSystemTimeInMS();

		if(p_rxMessage == msgMouseMove)
		{
			// eine Maus-Move-Nachricht ---> Mauscursor evtl. ändern! (nur, wenn Maus nicht gecaptured ist)
			if(GetCapture() == 0)
			{
				// suche erstes Fenster (aufwärts), das einen Cursor setzen möchte
				CWindow* pxWalk = pxWndTarget;
				while(pxWalk && pxWalk->GetCursor() == CMouseCursor::CT_None) 
				{
					if(pxWalk->GetParent())	
					{ 
						pxWalk = GetWindow(pxWalk->GetParent());
					}
					else
					{
						pxWalk = 0;
					}
				}
				if(pxWalk)
				{
					CMouseCursor::SetCursor(pxWalk->GetCursor());
				}
				else
				{
					CMouseCursor::SetStandardCursor();
				}
	
				// sende MouseEnter- und MouseLeave-Nachrichten

				WHDL hWalkLeave = m_hMouseWindow;
				while(hWalkLeave && m_axAllWindows.IsValid(hWalkLeave) && !GetWindowProperty(hWalkLeave, WP_WantMouseEnterAndLeave))
				{
					hWalkLeave = GetWindow(hWalkLeave)->GetParent();
				}
				WHDL hWalkEnter = pxWndTarget->GetWHDL();
				while(hWalkEnter && !GetWindowProperty(hWalkEnter, WP_WantMouseEnterAndLeave))
				{
					hWalkEnter = GetWindow(hWalkEnter)->GetParent();
				}
				if(hWalkEnter != hWalkLeave)
				{
					if(hWalkLeave != 0) { SendMsg(CMouseLeaveMsg(), hWalkLeave); }
					if(hWalkEnter != 0) { SendMsg(CMouseEnterMsg(), hWalkEnter); }
				}
				m_hMouseWindow = pxWndTarget->GetWHDL();
			}

			m_xMousePos = p_rxMessage.GetPos();
		}
	}

	// jetzt Nachricht ans Fenster durchreichen
	CWindow* pxModalWindow = GetModalWindow();
	while(pxWndTarget && !pxWndTarget->HandleMsg(p_rxMessage))
	{
		if(!pxWndTarget->GetParent()  ||  pxWndTarget == pxModalWindow)
		{
			break;
		}
		pxWndTarget=GetWindow(pxWndTarget->GetParent());			
	}		
}

//-------------------------------------------------------------------------------------------------------------------------------------------------------------
CPnt 
CWindowMgr::GetMousePos(HWND p_hWnd, LPARAM p_lParam)
{
	CPnt p; 

	p.x = GET_X_LPARAM(p_lParam);
	p.y = GET_Y_LPARAM(p_lParam);

	if(GetDesktop())
	{
		RECT r;
		::GetClientRect(p_hWnd, &r);
		CSize xSize = GetDesktop()->GetSize();
		float fX = (float) xSize.cx / (float) (r.right - r.left) * p.x;
		float fY = (float) xSize.cy / (float) (r.bottom - r.top) * p.y;
		
		p.x = (int) fX;
		p.y = (int) fY;
	}

	return p;
}

//-------------------------------------------------------------------------------------------------------------------------------------------------------------
/** 
	Übersetzt Windows-Nachrichten in UILib-Nachrichten
	Nachrichten, die kein Äquivalent haben, werden ignoriert; d.h. es ist kein Problem, einfach alle Windows-Nachrichten
	durch diese Funktion zu schicken

	\return			false, if message was processed, true otherwise
*/
bool 
CWindowMgr::SendWindowsMessage(HWND p_hWnd, UINT p_uiMessage, WPARAM p_wParam, LPARAM p_lParam)
{
	switch(p_uiMessage)
	{				
		case WM_MOUSEMOVE:
			{
				CPnt p = GetMousePos(p_hWnd, p_lParam);
				if((p.x!=m_xMousePos.x) || (p.y!=m_xMousePos.y))
				{
					CMouseMoveMsg xMsg(p.x, p.y, GetKeyModifierState());
					SendMsg(xMsg, GetMsgTargetWindow(xMsg));
				}
			}
			break;
		case WM_LBUTTONDOWN: 
			{
				::SetCapture(p_hWnd);

				CPnt p = GetMousePos(p_hWnd, p_lParam);
				CMouseLeftButtonDownMsg xMsg(p.x, p.y, GetKeyModifierState()); 
				SendMsg(xMsg, GetMsgTargetWindow(xMsg));
			}
			break;

		case WM_RBUTTONDOWN: 
			{
				::SetCapture(p_hWnd);

				CPnt p = GetMousePos(p_hWnd, p_lParam);
				CMouseRightButtonDownMsg xMsg(p.x, p.y, GetKeyModifierState()); 
				SendMsg(xMsg, GetMsgTargetWindow(xMsg));
			}
			break;

		case WM_MBUTTONDOWN: 
			{
				::SetCapture(p_hWnd);

				CPnt p = GetMousePos(p_hWnd, p_lParam);
				CMouseMiddleButtonDownMsg xMsg(p.x, p.y, GetKeyModifierState()); 
				SendMsg(xMsg, GetMsgTargetWindow(xMsg));

			}
			break;

		case WM_LBUTTONUP:
			{
				::ReleaseCapture();

				CPnt p = GetMousePos(p_hWnd, p_lParam);
				CMouseLeftButtonUpMsg xMsg(p.x, p.y, GetKeyModifierState()); 
				SendMsg(xMsg, GetMsgTargetWindow(xMsg));

			}
			break;

		case WM_RBUTTONUP:
			{
				::ReleaseCapture();

				CPnt p = GetMousePos(p_hWnd, p_lParam);
				CMouseRightButtonUpMsg xMsg(p.x, p.y, GetKeyModifierState()); 
				SendMsg(xMsg, GetMsgTargetWindow(xMsg));
			}
			break;

		case WM_MBUTTONUP:
			{
				::ReleaseCapture();

				CPnt p = GetMousePos(p_hWnd, p_lParam);
				CMouseMiddleButtonUpMsg xMsg(p.x, p.y, GetKeyModifierState()); 
				SendMsg(xMsg, GetMsgTargetWindow(xMsg));
			}
			break;

		case WM_LBUTTONDBLCLK:
			{
				CPnt p = GetMousePos(p_hWnd, p_lParam);
				CMouseLeftButtonDoubleClickMsg xMsg(p.x, p.y, GetKeyModifierState()); 
				SendMsg(xMsg, GetMsgTargetWindow(xMsg));
			}
			break;

		case WM_RBUTTONDBLCLK:
			{
				CPnt p = GetMousePos(p_hWnd, p_lParam);
				CMouseRightButtonDoubleClickMsg xMsg(p.x, p.y, GetKeyModifierState()); 
				SendMsg(xMsg, GetMsgTargetWindow(xMsg));
			}
			break;
		
		case WM_CHAR:
			{
				char cMod=GetKeyModifierState();
				if ( !cMod || cMod == KM_SHIFT  || (cMod== (KM_ALT|KM_CONTROL)))
				{
					CCharacterKeyMsg xMsg((int)p_wParam, 0);
					SendMsg(xMsg, GetMsgTargetWindow(xMsg));
				}
			}
			break;

		case WM_SYSKEYDOWN:			
		case WM_KEYDOWN: 
			{								
				char cKeyModifier = GetKeyModifierState();												
				int iVKChar = 0;								
				switch(p_wParam) 
				{
					case VK_PAUSE:		iVKChar = VKey_Pause;		break;
					case VK_CAPITAL:	iVKChar = VKey_Capital;		break;
					
					case VK_LEFT:		iVKChar = VKey_Left;		break;
					case VK_UP:			iVKChar = VKey_Up;			break;
					case VK_RIGHT:		iVKChar = VKey_Right;		break;
					case VK_DOWN:		iVKChar = VKey_Down;		break;
					case VK_PRINT:		iVKChar = VKey_Print;		break;
					case VK_SNAPSHOT:	iVKChar = VKey_Snapshot;	break;

					case VK_ESCAPE:		iVKChar = VKey_Escape;		break;					
					case VK_BACK:		iVKChar = VKey_Backspace;	break;					 	
					case VK_RETURN:		iVKChar = VKey_Return;		break;
					case VK_TAB:		iVKChar = VKey_Tab;			break;

					case VK_PRIOR:		iVKChar = VKey_PageUp;		break;
					case VK_NEXT:		iVKChar = VKey_PageDown;	break;												
					case VK_INSERT:		iVKChar = VKey_Insert;		break;
					case VK_DELETE:		iVKChar = VKey_Delete;		break;
					case VK_HOME:		iVKChar = VKey_Home;		break;
					case VK_END:		iVKChar = VKey_End;			break;
										
					case VK_F1:			iVKChar = VKey_F1;			break;
					case VK_F2:			iVKChar = VKey_F2;			break;
					case VK_F3:			iVKChar = VKey_F3;			break;
					case VK_F4:			iVKChar = VKey_F4;			break;
					case VK_F5:			iVKChar = VKey_F5;			break;
					case VK_F6:			iVKChar = VKey_F6;			break;
					case VK_F7:			iVKChar = VKey_F7;			break;
					case VK_F8:			iVKChar = VKey_F8;			break;
					case VK_F9:			iVKChar = VKey_F9;			break;
					case VK_F10:		iVKChar = VKey_F10;			break;
					case VK_F11:		iVKChar = VKey_F11;			break;
					case VK_F12:		iVKChar = VKey_F12;			break;

					case VK_NUMPAD0:	iVKChar = VKey_NumPad0;		break;
					case VK_NUMPAD1:	iVKChar = VKey_NumPad1;		break;
					case VK_NUMPAD2:	iVKChar = VKey_NumPad2;		break;
					case VK_NUMPAD3:	iVKChar = VKey_NumPad3;		break;
					case VK_NUMPAD4:	iVKChar = VKey_NumPad4;		break;
					case VK_NUMPAD5:	iVKChar = VKey_NumPad5;		break;
					case VK_NUMPAD6:	iVKChar = VKey_NumPad6;		break;
					case VK_NUMPAD7:	iVKChar = VKey_NumPad7;		break;
					case VK_NUMPAD8:	iVKChar = VKey_NumPad8;		break;
					case VK_NUMPAD9:	iVKChar = VKey_NumPad9;		break;
												
					case VK_MULTIPLY:	iVKChar = VKey_Mul;			break;
					case VK_ADD:		iVKChar = VKey_Add;			break;
					case VK_SEPARATOR:	iVKChar = VKey_Separator;	break;						
					case VK_SUBTRACT:	iVKChar = Vkey_Sub;			break;
					case VK_DECIMAL:	iVKChar = VKey_DecimalPad;	break;
					case VK_DIVIDE:		iVKChar = VKey_Div;			break;
				}

				if(iVKChar)
				{		
					CControlKeyMsg xMsg(iVKChar, cKeyModifier);
					SendMsg(xMsg, GetMsgTargetWindow(xMsg));					
				}				

				CKeyDownMsg xMsg((int) p_wParam, cKeyModifier);
				//DebugPrint("keydown %d (%c) %d", p_wParam, p_wParam, cKeyModifier);
				SendMsg(xMsg, GetMsgTargetWindow(xMsg));
			}			
			break;

		case WM_SYSKEYUP:
		case WM_KEYUP:
			{				
				CKeyUpMsg xMsg((int) p_wParam, GetKeyModifierState());
				SendMsg(xMsg, GetMsgTargetWindow(xMsg));
			}
			break;

		default:				
			return true;
	}

	return false;
}


//-------------------------------------------------------------------------------------------------------------------------------------------------------------
/**
	erklärt Fensterinhalt für ungültig; löst Neuzeichnen aus
*/
void 
CWindowMgr::Invalidate(WHDL p_hWnd)
{
	CWindow* pxWnd=GetWindow(p_hWnd);
	if(pxWnd)
	{
		CRct xUpdateRct=pxWnd->GetAbsRect();
		Invalidate(GetDevice(p_hWnd), xUpdateRct);
	}	
}


//-------------------------------------------------------------------------------------------------------------------------------------------------------------
/**
	erklärt Teil des Fensterinhalts für ungültig; löst Neuzeichnen aus
*/
void 
CWindowMgr::Invalidate(WHDL p_hWnd, const CRct& p_rxRect)
{
	COutputDevice* pxDevice = GetDevice(p_hWnd);
	if(pxDevice)
	{
		CRct xUpdateRect=p_rxRect;
		CWindow* pxWnd=GetWindow(p_hWnd);
		Invalidate(pxDevice, xUpdateRect + pxWnd->GetAbsPos());
	}
}


//-------------------------------------------------------------------------------------------------------------------------------------------------------------
/**
	erklärt eine Liste von Rechtecken für ungültig
	\param p_hWnd			Fensterhandle
	\param p_rxRectList	Liste von Rechtecken
*/
void 
CWindowMgr::Invalidate(WHDL p_hWnd, const CRctList& p_rxRectList)
{
	COutputDevice* pxDevice = GetDevice(p_hWnd);
	if(pxDevice)
	{
		CWindow* pxWin = GetWindow(p_hWnd);
		CRct xRct;
		unsigned int iIter;
		p_rxRectList.StartIterate(iIter);
		while(p_rxRectList.Iterate(iIter, xRct))
		{
			Invalidate(pxDevice, xRct + pxWin->GetAbsPos());
		}
	}
}

//-------------------------------------------------------------------------------------------------------------------------------------------------------------
/**
	Erklärt ein komplettes Device für ungültig
	\param p_pxDevice	Device
*/
void 
CWindowMgr::Invalidate(COutputDevice* p_pxDevice)
{
	p_pxDevice->Invalidate();	
}

//-------------------------------------------------------------------------------------------------------------------------------------------------------------
/**
	Erklärt ein Rechteck auf einem Device für ungültig
	\param p_pxDevice	Device
	\param p_rxRect	Rechteck
*/
void 
CWindowMgr::Invalidate(COutputDevice* p_pxDevice, const CRct& p_rxRect)
{
	if(p_pxDevice)
	{		
		TDevInfo* pDevInfo = GetDevInfo(p_pxDevice);
		p_pxDevice->Invalidate(p_rxRect - CPnt(pDevInfo->m_xArea.left, pDevInfo->m_xArea.top));
	}	
}

//-------------------------------------------------------------------------------------------------------------------------------------------------------------
/**
	Fügt ein neues Device hinzu
	\param p_pxDevice	Device
	\param p_xArea		Rechteck, das von diesem Device überdeckt werden soll
	\return				handle des Desktopfensters
*/
WHDL 
CWindowMgr::AddDevice(COutputDevice* p_pxDevice, CRct p_xArea, CWindow* p_pxDesktopWindow)
{
	unsigned int iIndex=m_axAllDevices.PushEntry();
	m_axAllDevices[iIndex].m_pxDevice=p_pxDevice;
	m_axAllDevices[iIndex].m_xArea = p_xArea;
	if(p_pxDesktopWindow != 0)
	{
		m_axAllDevices[iIndex].m_bOwnDesktop = false;
		p_pxDesktopWindow->SetPos(CPnt(p_xArea.left, p_xArea.top));
		p_pxDesktopWindow->SetSize(CSize(p_xArea.Width(), p_xArea.Height()));
		m_axAllDevices[iIndex].m_pxDesktop = p_pxDesktopWindow;
	}
	else
	{
		m_axAllDevices[iIndex].m_bOwnDesktop = true;
        m_axAllDevices[iIndex].m_pxDesktop = CPanel::Create();

		m_axAllDevices[iIndex].m_pxDesktop->SetPos(p_xArea.left, p_xArea.top);
		m_axAllDevices[iIndex].m_pxDesktop->SetSize(p_xArea.Width(), p_xArea.Height()); 
	}
	m_axAllDevices[iIndex].m_pxDesktop->m_bDesktop = true;
	SetDevice(m_axAllDevices[iIndex].m_pxDesktop->GetWHDL(), p_pxDevice);

	if(m_hTopWindow == 0)
	{
		m_hTopWindow = m_axAllDevices[iIndex].m_pxDesktop->GetWHDL();
	}

	return m_axAllDevices[iIndex].m_pxDesktop->GetWHDL();
}


//-------------------------------------------------------------------------------------------------------------------------------------------------------------
/** 
	remove device from window manager
	the desktop window will only be deleted if you did not provide one when registering this device
	if you did create the desktop window yourself, you have to delete it
	note that if the desktop window is deleted, all of its child windows will be delete, too
*/
void 
CWindowMgr::RemoveDevice(COutputDevice* p_pxDevice)
{
	unsigned int i;
	for(i=0; i<m_axAllDevices.Size(); ++i)
	{
		if(m_axAllDevices[i].m_pxDevice == p_pxDevice)
		{
			break;
		}
	}
	if(i>=m_axAllDevices.Size())
	{
		// device not registerd
		return;
	}

	if(m_axAllDevices[i].m_bOwnDesktop)
	{
		m_axAllDevices[i].m_pxDesktop->DeleteNow();
	}
	else
	{
		SetDevice(m_axAllDevices[i].m_pxDesktop->GetWHDL(), 0);
	}

	m_axAllDevices.DeleteEntry(i);
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	Fügt das übergebene Fenster als Top-Level-Fenster dem System hinzu	
	Das Fenster wird anhand seiner aktuellen Koodinaten automatsich einem passenden Device
	zugeordnet. 
*/
void 
CWindowMgr::AddTopLevelWindow(WHDL p_hWnd)
{
	assert(m_axAllWindows.IsValid(p_hWnd));			// Window Handle ungültig

	if(m_axAllDevices.Size() == 0)
	{
		assert(false);			// keine Devices registriert!!!
		return;
	}

	CWindow* pWnd = GetWindow(p_hWnd);
	if(pWnd->GetParent())
	{ 
		GetWindow(pWnd->GetParent())->RemoveChild(p_hWnd);
	}

	for(unsigned int i=0; i<m_axAllDevices.Size(); ++i)
	{
		if(m_axAllDevices[i].m_xArea.Hit(pWnd->GetPos()))
		{
			m_axAllDevices[i].m_pxDesktop->AddChild(p_hWnd);

			// SetPos() kann hier nicht verwendet werden, da es selbst auf Devices prüfen würde
			pWnd->m_xPos = ( pWnd->GetRelPos() - CPnt(m_axAllDevices[i].m_xArea.left, m_axAllDevices[i].m_xArea.top) );
			SetDevice(p_hWnd, m_axAllDevices[i].m_pxDevice);
			Invalidate(p_hWnd);

			break;
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// \return true, wenn der übergebene Punkt auf einem der registriertes Devices liegt
bool 
CWindowMgr::IsPointOnAnyDevice(CPnt p_xPos)
{
	for(unsigned int i=0; i<m_axAllDevices.Size(); ++i)
	{
		if(m_axAllDevices[i].m_xArea.Hit(p_xPos))
		{
			return true;
		}
	}
	return false;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	\return true: window is on new device now, false: window is still on same device
*/
bool
CWindowMgr::WindowDeviceChange(CWindow* p_pxWnd)
{
	if(!p_pxWnd->GetIsTopLevelWindow())
	{
		/// only top level windows can move to a different device; child windows always move with their parents 
		return false;
	}

	unsigned int i;
	for(i=0; i<m_axAllDevices.Size(); ++i)
	{
		if(m_axAllDevices[i].m_xArea.Hit(p_pxWnd->GetRelPos()))
		{
			break;
		}
	}
	if(i>=m_axAllDevices.Size())
	{
		// point is not on any device
		return false;
	}

	if(GetDevice(p_pxWnd->GetWHDL())  ==  m_axAllDevices[i].m_pxDevice)
	{
		// point is on same device as window
		return false;
	}

	GetWindow(p_pxWnd->GetParent())->RemoveChild(p_pxWnd->GetWHDL());
	m_axAllDevices[i].m_pxDesktop->AddChild(p_pxWnd->GetWHDL());
	SetDevice(p_pxWnd->GetWHDL(), m_axAllDevices[i].m_pxDevice);

	return false;
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	Remove window from top level windows of its current device.
	Attention: removing the window does not mean that is loses focus - you should manually transfer focus
	to another window. 
	\param p_hWnd the window
*/
void 
CWindowMgr::RemoveTopLevelWindow(WHDL p_hWnd)
{
	assert(m_axAllWindows.IsValid(p_hWnd));

	TDevInfo* pxDevInfo=GetDevInfo(GetDeviceConst(p_hWnd));
	if(pxDevInfo)
	{
		Invalidate(p_hWnd);
		pxDevInfo->m_pxDesktop->RemoveChild(p_hWnd);
	}
}	

//---------------------------------------------------------------------------------------------------------------------
/**
	\return pointer to desktop window
*/
CWindow* 
CWindowMgr::GetDesktop(unsigned int p_iIndex) const
{
	if(p_iIndex >= m_axAllDevices.Size()  ||  p_iIndex < 0)
	{
		return 0;
	}
	else
	{
		return (m_axAllDevices[p_iIndex].m_pxDesktop);
	}
}

//---------------------------------------------------------------------------------------------------------------------
/** 
	liefert das Desktopfenster des angegebenen Devices oder 0, falls das Device nicht registriert ist
*/
CWindow* 
CWindowMgr::GetDesktopByDevice(const COutputDevice* p_pxDevice) const
{
	TDevInfo* p = GetDevInfo(p_pxDevice);
	return p ? p->m_pxDesktop : 0;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	get window that is currently under the mouse cursor
*/
CWindow* 
CWindowMgr::GetHoveredWindow() const
{
	return HitTest(m_xMousePos);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	get desktop window that is currently under the mouse cursor
*/
CWindow* 
CWindowMgr::GetHoveredDesktop() const
{
	for(unsigned int i=0; i<m_axAllDevices.Size(); ++i)
	{
		if(m_axAllDevices[i].m_xArea.Hit(m_xMousePos))
		{
			return m_axAllDevices[i].m_pxDesktop;
		}
	}
	return 0;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	set device of window
*/
void 
CWindowMgr::SetDevice(WHDL p_hWnd, COutputDevice* p_pxDevice)
{
	TWindowInfo* p = m_axAllWindows.ElementPtr(p_hWnd); 
	assert(p);
	if(p)
	{
		p->m_pxDevice = p_pxDevice;

		// zuerst Kindfenster, da die Eltern evtl. die schon geänderte Info brauchen
		CWindow* pWnd = GetWindow(p_hWnd);
		for(unsigned int i=0; i<pWnd->m_ahSubs.Size(); ++i)
		{
			SetDevice(pWnd->m_ahSubs[i], p_pxDevice);
		}

		SendMsg(CWindowDeviceChangeMsg(), p_hWnd);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	set value of property flag of window
*/
void 
CWindowMgr::SetWindowProperty(WHDL p_hWnd, WindowProperties p_eProp, bool p_bValue)
{
	TWindowInfo* p = m_axAllWindows.ElementPtr(p_hWnd); 
	assert(p);
	if(p)
	{
		if(p_bValue)
		{
			p->m_iWindowProps |= p_eProp;
		}
		else
		{
			p->m_iWindowProps &= (~p_eProp);
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Capture the mouse; window will receive mouse messages no matter where the mouse is.
	If more than one window captures the mouse, they form a stack and only the topmost (last one that 
	called SetCapture()) receives all mouse messages. It is safe to call SetCapture() 
	even if you have already captured the mouse; this will move your window to the top of the stack. 

	\param p_pxWnd pointer to window that wants to receive all mouse messages
*/
void 
CWindowMgr::SetCapture(CWindow* p_pxWnd)
{
	int i = m_apxCaptureWnd.Find(p_pxWnd);
	if(i>=0)
	{
		m_apxCaptureWnd.DeleteEntry(i);
	}
	m_apxCaptureWnd.PushEntry(p_pxWnd);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	\return pointer to window that currently has captured the mouse; 0 if none
*/
CWindow* 
CWindowMgr::GetCapture() const
{
	int iC = m_apxCaptureWnd.Size();
	if(iC <= 0) { return 0; }
	return m_apxCaptureWnd[iC-1];
}


//---------------------------------------------------------------------------------------------------------------------
/**
	release mouse capture
*/
void 
CWindowMgr::ReleaseCapture(CWindow* p_pxWnd)
{
    int i = m_apxCaptureWnd.Find(p_pxWnd);
	if(i>= 0)
	{
		m_apxCaptureWnd.DeleteEntry(i);
	}
	else
	{
		// Fenster hat die Maus gar nicht gecaptured
		assert (false);	
	}
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	macht fenster modal
*/
void 
CWindowMgr::SetModal(CWindow* p_pxWnd)
{
	int i = m_apxModalWnd.Find(p_pxWnd);
	if(i >= 0)
	{
		m_apxModalWnd.DeleteEntry(i);
	}
	m_apxModalWnd.PushEntry(p_pxWnd);
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	get current modal window (or 0 if none)
*/
CWindow* 
CWindowMgr::GetModalWindow() const
{
	int iC = m_apxModalWnd.Size();
	if(iC <= 0) { return 0; }
	return m_apxModalWnd[iC-1];
}
	

//---------------------------------------------------------------------------------------------------------------------
/** 
	window is no longer modal
*/
void 
CWindowMgr::ReleaseModal(CWindow* p_pxWnd)
{
    int i = m_apxModalWnd.Find(p_pxWnd);
	if(i >= 0)
	{
			m_apxModalWnd.DeleteEntry(i);
	}
	else
	{
		// Fenster ist gar nicht modal
		assert(false);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	convert point from global to client coordinates
*/
void 
CWindowMgr::ToClientPos(WHDL p_hWnd, CPnt& p_pntMouse)
{
	CWindow* pWnd = GetWindow(p_hWnd);
	p_pntMouse -= pWnd->GetAbsPos();
}


//---------------------------------------------------------------------------------------------------------------------
/**
	paint damaged regions of all devices 
*/
void 
CWindowMgr::DoPaint()
{
	for(unsigned int i=0; i<m_axAllDevices.Size(); ++i)
	{
		TDevInfo* pxDevInfo = &m_axAllDevices[i];
		CRctList xInvalidRects = pxDevInfo->m_pxDevice->GetInvalidRegions();
		xInvalidRects.Compact();

		if(xInvalidRects.Size() > 0)
		{
			pxDevInfo->m_pxDevice->BeginPaint();

			CPaintContext xCtx(pxDevInfo->m_pxDevice);
			// device origin is (0, 0), so move the desktop window for a second 
			CPnt xOldDesktopPos = pxDevInfo->m_pxDesktop->m_xPos;
			pxDevInfo->m_pxDesktop->m_xPos = CPnt(0, 0);

			DoPaintSub(xCtx, pxDevInfo->m_pxDesktop, xInvalidRects);

			// repair desktop window
			pxDevInfo->m_pxDesktop->m_xPos = xOldDesktopPos;

			pxDevInfo->m_pxDevice->EndPaint();
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	paint damaged region of a window and damaged regions of all of its subwindows
	called be DoPaint
*/
void 
CWindowMgr::DoPaintSub(CPaintContext p_xCtx, CWindow* p_pxWnd, CRctList p_rxDamageList)
{
	unsigned int iIter;		
	CRct xRct;
	CRctList xNewDamageList;
	CDynArray<CRctList> axChildDamageList;
	int iNumChildren = p_pxWnd->NumPhysicalChildWindows();
	axChildDamageList.SetSize(iNumChildren);

	// first, remove all areas from my damagelist which are covered by my children

	// for each invalid rect in this window...
	while(p_rxDamageList.Pop(xRct))
	{
		// we are looking for a child that intersects with it - so far, we have not found one :)
		bool bFound = false;

		// for each child window (in z-order from top to bottom)...
		int iChild;
		for(iChild=iNumChildren-1; iChild>=0; --iChild)
		{
			CWindow* pxChild = GetWindow(p_pxWnd->GetPhysicalChild(iChild));
			if(pxChild->GetVisible())
			{
				CRct xChildRect = pxChild->GetAbsRect();
				CRct xClipRect = xChildRect.Clip(xRct);
				// if invalid rect intersects with child...
				if(!xClipRect.IsEmpty())
				{
					// add intersection to childs rect list
					axChildDamageList[iChild].Add(xClipRect);
					
					// remove intersection from my damagelist (if child is not transparent)
					if(!pxChild->GetTransparent())
					{
						CRctList xRemaining;
						xRemaining.Push(xRct);
						xRemaining.Sub(xChildRect);
						p_rxDamageList.Push(xRemaining);

						// we are done with this rect, no need to look at the other children
						bFound = true;
						break;
					}
				}
			}
		}

		// if no child window was responsible for this rect (or part of it), add it to my damage list
		if(!bFound)
		{
			xNewDamageList.Push(xRct);
		}
	}

	// draw every rect from the new damage list 
	p_xCtx.SetOffset(p_pxWnd->GetAbsPos());
	xNewDamageList.StartIterate(iIter);
	while(xNewDamageList.Iterate(iIter, xRct))
	{
		p_xCtx.SetClip(xRct);
		p_pxWnd->Paint(p_xCtx);
	}

	// for each child window 
	int iChild;
	for(iChild=0; iChild<iNumChildren; ++iChild)
	{
		// if its damage list is not empty ...
		if(axChildDamageList[iChild].Size() > 0)
		{
			// do everything again for that window
			CWindow* pxChild = GetWindow(p_pxWnd->GetPhysicalChild(iChild));
			DoPaintSub(p_xCtx, pxChild, axChildDamageList[iChild]);
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
///	behandelt die Timer
void
CWindowMgr::HandleTimers()
{
	// Timer behandeln
	__int64 iNow = CTimer::GetSystemTimeInMS();
	while(m_apxTimersPQ.size() > 0  &&  m_apxTimersPQ.front()->m_iActivationTime <= iNow)
	{
		CTimer* pxTimer = m_apxTimersPQ.front();
//		DebugPrint("size before pop %d", m_apxTimersPQ.size());
		std::pop_heap(m_apxTimersPQ.begin(), m_apxTimersPQ.end(), TimerPtrLess);
		m_apxTimersPQ.pop_back();
//		DebugPrint("size after pop %d", m_apxTimersPQ.size());

		//CheckTimerPQ();

		int iID = pxTimer->m_iID;
		WHDL hWnd = pxTimer->m_hWindow;

		if(pxTimer->m_iInterval <= 0)
		{
			// einmaliger Timer

			m_apxAllTimers.DeleteEntry(iID);
			delete pxTimer;
		}
		else
		{
			// wiederholender Timer

			pxTimer->m_iActivationTime = iNow + pxTimer->m_iInterval;
			m_apxTimersPQ.push_back(pxTimer);
			if(m_apxTimersPQ.size() > 1)
			{
				std::push_heap(m_apxTimersPQ.begin(), m_apxTimersPQ.end(), TimerPtrLess);
			}
			//CheckTimerPQ();
		}
		SendMsg(CTimerMsg(iID),hWnd);
	}
}
//---------------------------------------------------------------------------------------------------------------------
/// Nachrichten in den Warteschlangen der Fenster bearbeiten
void
CWindowMgr::HandleWindowMessages()
{
	unsigned long iIter;
	m_axAllWindows.StartIterate(iIter);
	TWindowInfo* p;
	while(m_axAllWindows.Iterate(iIter, p))
	{
		if(p->m_pxWnd->m_xMsgQueue.size() > 0)
		{
			p->m_pxWnd->MessagePump();
		}
	}
}
//---------------------------------------------------------------------------------------------------------------------
/// ToolTips
void
CWindowMgr::UpdateToolTips()
{
	if(!IsValid(m_xToolTip)  &&  m_iLastMouseActivityTimeMS + m_iToolTipDelay <= CTimer::GetSystemTimeInMS())
	{
		CWindow* pxWnd = HitTest(m_xMousePos);
		while(pxWnd && pxWnd->m_sToolTipText.IsEmpty())
		{
			if(pxWnd->GetParent())
			{
				pxWnd = GetWindow(pxWnd->GetParent());
			}
			else
			{
				pxWnd = 0;
			}
		}
		if(pxWnd)
		{
			CToolTip* pxToolTip = CToolTip::Create(pxWnd->GetWHDL());
			pxToolTip->SetPos(GetMousePos() - CPnt(0, pxToolTip->GetSize().cy));
			pxToolTip->SetText(pxWnd->m_sToolTipText);
			m_xToolTip = pxToolTip->GetWHDL();
			AddTopLevelWindow(m_xToolTip);
		}
	}
}
//---------------------------------------------------------------------------------------------------------------------
/// Alle Fenster löschen, die in der Delete-List stehen.
void
CWindowMgr::ExecuteDeleteList()
{
	while(m_apxDeleteList.Size() > 0)
	{
		m_apxDeleteList[0]->DeleteNow();
		m_apxDeleteList.DeleteEntry(0);
	}
}
//---------------------------------------------------------------------------------------------------------------------
///	Tick-Methode, muss zyklisch (möglichst oft, z.B. einmal pro Frame) aufgerufen werden
void
CWindowMgr::Tick()
{
	HandleTimers();

	HandleWindowMessages();

	UpdateToolTips();

	ExecuteDeleteList();
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Send message; does not return until message is processed.
	If you send a mouse message and a window has captured the mouse then this window will receive the 
	message instead of the target window.

	\param p_pxMsg			the message to send
	\param p_hWndTarget		the target window
*/
void 
CWindowMgr::SendMsg(const CMessage& p_rxMessage, WHDL p_hWndTarget)
{
	// first send message to window that has captured the mouse
	// should be done before normal message handling, because another window might capture the mouse during normal
	// message handling; that window should not receive the message twice

	CWindow* pMouseCaptureWnd = GetCapture();
	if(pMouseCaptureWnd != 0  &&  p_hWndTarget != pMouseCaptureWnd->GetWHDL()  &&  p_rxMessage.IsMouseMessage())
	{
		HandleMsg(GetCapture()->GetWHDL(), p_rxMessage);
	}
	else
	{
		HandleMsg(p_hWndTarget, p_rxMessage);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	Post message; message is put into target windows message queue.
	If you post a mouse message and a window has captured the mouse then this window will receive the 
	message as well.

	\param p_xMsg			the message to send
	\param p_hWndTarget		target window
*/
void 
CWindowMgr::PostMsg(const CMessage& p_rxMessage, WHDL p_hWndTarget)
{
	CWindow* pMouseCaptureWnd = GetCapture();
	if(pMouseCaptureWnd != 0  &&  p_hWndTarget != pMouseCaptureWnd->GetWHDL()  &&  p_rxMessage.IsMouseMessage())
	{
		pMouseCaptureWnd->m_xMsgQueue.push_back(p_rxMessage);
	}
	else
	{
		if(p_hWndTarget != 0)
		{
			GetWindow(p_hWndTarget)->m_xMsgQueue.push_back(p_rxMessage);
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	Determines the target window for the message
	\param p_pxMsg		the message to send
	\return				target window for that message; may be 0 (in that case drop the message)	
*/
WHDL 
CWindowMgr::GetMsgTargetWindow(const CMessage& p_rxMessage) 
{
	if(p_rxMessage.IsMouseMessage())
	{
		// mouse message: determine target window by mouse position

		CWindow* pxTarget = HitTest(p_rxMessage.GetPos());
		if(!pxTarget)
		{
			return 0;
		}
		CWindow* pxModalWindow = GetModalWindow();
		if(pxModalWindow)
		{
			if(!pxTarget->GetIgnoreModals() &&  pxTarget != pxModalWindow  &&  
			   !pxTarget->IsChildOf(pxModalWindow->GetWHDL()))
			{
				// mouse is outside a modal window; drop message
				return 0;
			}
		}
		return pxTarget->GetWHDL();
	}
	else
	{
		// all other messages: window that has focus receives the message

		return GetFocusWindow();
	}
}


//---------------------------------------------------------------------------------------------------------------------
///	fill byte with current status of modifier keys (shift, control, alt)
unsigned char 
CWindowMgr::GetKeyModifierState() const
{
	unsigned char cMod = KM_NONE;
	if ( GetKeyState( VK_SHIFT ) & 0x8000 ) { cMod |= KM_SHIFT; }
	if ( GetKeyState(VK_CONTROL) & 0x8000 ) { cMod |= KM_CONTROL; }
	if ( (GetKeyState(VK_MENU) & 0x8000) )	{ cMod |= KM_ALT; }
	return cMod;
}


//---------------------------------------------------------------------------------------------------------------------
///	copy string to clipboard
bool 
CWindowMgr::FillClipBoard(const CStr& p_sString) const
{
	if(::OpenClipboard(0))
	{
		::EmptyClipboard();
 
		if(p_sString.GetLength() == 0)
		{
			::CloseClipboard();
			return true;
		}

        // Allocate a global memory object for the text. 
 		HGLOBAL hglbCopy = ::GlobalAlloc(GMEM_MOVEABLE, (p_sString.GetLength() + 1) * sizeof(TCHAR)); 
        if (hglbCopy == 0) 
        { 
			::CloseClipboard(); 
            return false; 
        } ;
 
        // Lock the handle and copy the text to the buffer. 
		char* pcStrCopy = (char*) ::GlobalLock(hglbCopy); 
        memcpy(pcStrCopy, p_sString.c_str(), p_sString.GetLength() * sizeof(TCHAR)); 
        pcStrCopy[p_sString.GetLength()] = (TCHAR) 0;    
		::GlobalUnlock(hglbCopy); 
 
		::SetClipboardData(CF_TEXT, hglbCopy); 
		::CloseClipboard();

		return true;
	}

	return false;
}


//---------------------------------------------------------------------------------------------------------------------
/// get contents of clipboard
CStr 
CWindowMgr::GetClipBoardContents() const
{
	if(::OpenClipboard(0))
	{
		HGLOBAL hglb = ::GetClipboardData(CF_TEXT); 
        if (hglb != 0) 
        { 
            char* pcString = (char*) GlobalLock(hglb); 
            CStr sRV(pcString);
            ::CloseClipboard();
            return sRV;
        }
        ::CloseClipboard();
	}
	return "";
} 


/**
	reine Debugfunktion: testet, ob Timer-Priority-Queue in Ordnung ist
*/
void
CWindowMgr::CheckTimerPQ()
{
	int iSize = (int) m_apxTimersPQ.size(); 
	for(int i=1; i<iSize; ++i)
	{
		if(m_apxTimersPQ[0]->m_iActivationTime > m_apxTimersPQ[i]->m_iActivationTime)
		{
			for(int i=0; i<iSize; ++i)
			{
				DebugPrint("timer %d, time %f", m_apxTimersPQ[i]->m_iID, (float) m_apxTimersPQ[i]->m_iActivationTime);
			}
		}
		assert(m_apxTimersPQ[0]->m_iActivationTime <= m_apxTimersPQ[i]->m_iActivationTime);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Set (create) a timer

	\param p_hWindow WHDL of window that will receive timer messages
	\param p_iInterval timer interval in milliseconds
	\param p_bRepeating true, if this is a repeating timer

	The window will receive a evtTimer message after or every p_iIntervall milliseconds, depending on whether 
	this is a repeating timer or not. Repeating timers must be unset with UnsetTimer() or can be made non-repeating
	timers with ResetTimer(). Non-repeating timers are deleted automatically when the timer event is sent.

	\return timer ID
*/
int	
CWindowMgr::SetTimer(WHDL p_hWindow, int p_iInterval, bool p_bRepeating)
{
	assert(p_iInterval >= 10);

	int iID = m_apxAllTimers.PushEntry();
	CTimer* pxTimer = new CTimer(p_hWindow, iID, p_iInterval, p_bRepeating);
	m_apxAllTimers.SetElement(iID, pxTimer);
	m_apxTimersPQ.push_back(pxTimer);
	if(m_apxTimersPQ.size() > 1)
	{
		std::push_heap(m_apxTimersPQ.begin(), m_apxTimersPQ.end(), TimerPtrLess);
	}
	//CheckTimerPQ();

//	DebugPrint("added timer %d, pqsize is %d", iID, m_apxTimersPQ.size());
	return iID;
}


//---------------------------------------------------------------------------------------------------------------------
///	löscht einen Timer (nur wiederholende Timer müssen so gelöscht werden!)
bool
CWindowMgr::UnsetTimer(int p_iID)
{
	CTimer** ppxTimer = m_apxAllTimers.ElementPtr(p_iID);
	assert(ppxTimer != 0);
	if(ppxTimer == 0)	{ return false; }
	CTimer* pxTimer = *ppxTimer;

	std::vector<CTimer*>::iterator cur = std::find(m_apxTimersPQ.begin(), m_apxTimersPQ.end(), *ppxTimer);
	assert(cur != m_apxTimersPQ.end());
	m_apxTimersPQ.erase(cur);
	cur = std::find(m_apxTimersPQ.begin(), m_apxTimersPQ.end(), *ppxTimer);
	assert(cur == m_apxTimersPQ.end());

	int iSize = (int) m_apxTimersPQ.size();
	if(iSize > 1)
	{
		std::make_heap(m_apxTimersPQ.begin(), m_apxTimersPQ.end(), TimerPtrLess); 
	}
	//CheckTimerPQ();

	m_apxAllTimers.DeleteEntry(p_iID);
	delete pxTimer;
	return true;
}



//---------------------------------------------------------------------------------------------------------------------
/** 
	Setzt einen Timer neu, d.h. ändert die Parameter des Timers

	\param p_iID			ID des fraglichen Timers
	\param p_iInterval		Zeit bis Timer abgelaufen ist
	\param p_bRepeating		false: Timer meldet sich nur einmal; true: immer wieder
	\return					true, wenn erfolgreich, false wenn ungültiges Handle
*/
bool 
CWindowMgr::ResetTimer(int p_iID, int p_iInterval, bool p_bRepeating)
{
//	DebugPrint("reset timer %d, pqsize before is %d", p_iID, m_apxTimersPQ.size());
	CTimer** ppxTimer = m_apxAllTimers.ElementPtr(p_iID);
	assert(ppxTimer != 0);
	if(ppxTimer == 0)	{ return false; }

	(*ppxTimer)->Reset(p_iInterval, p_bRepeating);
	std::make_heap(m_apxTimersPQ.begin(), m_apxTimersPQ.end(), TimerPtrLess); 
	//CheckTimerPQ();

//	DebugPrint("reset timer %d, pqsize after is %d", p_iID, m_apxTimersPQ.size());

	return true;
}


//--------------------------------------------------------------------------------------------------------------------- 
///	debug dump
void 
CWindowMgr::Dump(bool p_bVisibleOnly)
{
	Dump("uilibdump.txt", p_bVisibleOnly);
}


//---------------------------------------------------------------------------------------------------------------------
///	debug dump
void 
CWindowMgr::Dump(CStr p_sFilename, bool p_bVisibleOnly)
{
	FILE* f = fopen(p_sFilename.c_str(), "wb");
	
	fprintf(f, "UIlib Dump\n\n");

	for(unsigned int i=0; i<m_axAllDevices.Size(); ++i)
	{
		fprintf(f, "Device %i: \n\n", i);
		DumpSubTree(f, m_axAllDevices[i].m_pxDesktop, 2, p_bVisibleOnly);
	}

	fprintf(f, "\n\n\"Orphaned\" Windows: \n\n", i);

	int iNumWindows = 0;
	unsigned long iIter;
	m_axAllWindows.StartIterate(iIter);
	TWindowInfo* p;
	while(m_axAllWindows.Iterate(iIter, p))
	{
		iNumWindows++;
		if(p->m_pxWnd->GetParent() == 0  &&  p->m_pxWnd->GetIsDesktop() == false)
		{
			bool bFound = false;
			for(unsigned int j=0; j<m_axAllDevices.Size(); ++j)
			{
				if(m_axAllDevices[j].m_pxDesktop == p->m_pxWnd)
				{
					bFound = true;
					break;
				}
				if(!bFound)
				{
					DumpSubTree(f, p->m_pxWnd, 2, p_bVisibleOnly);
					fprintf(f, "\n\n");
				}
			}
		}
	}


	fprintf(f, "\n\nTotal Number of Windows: %d \n\n", iNumWindows);
	fclose(f);
}


//---------------------------------------------------------------------------------------------------------------------
///	debug dump - subtree
void 
CWindowMgr::DumpSubTree(FILE* p_f, CWindow* p_pxWindow, int p_iIndent, bool p_bVisibleOnly)
{
	if(p_bVisibleOnly  &&  p_pxWindow->GetVisible() == false)
	{
		return;
	}

	for(int i=0; i<p_iIndent; ++i) { fprintf(p_f, " "); }
	
	fprintf(p_f, p_pxWindow->GetDebugString().c_str());
	if(p_pxWindow->GetIsDesktop())
	{
		fprintf(p_f, " DESKTOP");
	}
	if(p_pxWindow->GetAlwaysOnTop())
	{
		fprintf(p_f, " ALWAYSONTOP");
	}
	if(p_pxWindow->GetDisabled())
	{
		fprintf(p_f, " DISABLED");
	}
	if(p_pxWindow->GetTransparent())
	{
		fprintf(p_f, " TRANSPARENT");
	}
	if(!p_pxWindow->GetVisible())
	{
		fprintf(p_f, " INVISIBLE");
	}
	fprintf(p_f, "\n");

	for(unsigned int i=0; i<p_pxWindow->m_ahSubs.Size(); ++i)
	{
		DumpSubTree(p_f, GetWindow(p_pxWindow->m_ahSubs[i]), p_iIndent + 4, p_bVisibleOnly);
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CWindowMgr::PrintWindowTree( WHDL p_hWindow, int p_iTab )
{	
	CWindow* pxWnd = GetWindow(p_hWindow);
	CStr sString="";
	int i; for ( i=0; i < p_iTab; i++ ) { sString += "\t"; }

	int iT, iC=pxWnd->m_ahSubs.Size();		
	for ( iT = 0; iT<iC; iT++ )
	{					
		PrintWindowTree( pxWnd->m_ahSubs[iT], ++p_iTab );		
	}	
}

} // namespace UIlib


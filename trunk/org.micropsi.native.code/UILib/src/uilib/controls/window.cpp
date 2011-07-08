#include "stdafx.h"
#include "uilib/core/window.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"


namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CWindow::CWindow()
{
	CWindowMgr& xWM=CWindowMgr::Get();	
	m_hWnd=xWM.RegisterWindow(this);	

	m_bDisabled			= false;
	m_bParentDisabled	= false;
	m_bVisible			= true;
	m_bParentVisible	= true;
	m_bTransparent		= false;
	m_bAllWndsTransparent = false;
	m_bIgnoreModals		= false;
	m_bAlwaysOnTop		= false;
	m_bCanReceiveFocus	= true;
	m_bWriteAlpha		= false;
	m_bDesktop			= false;

	m_xSize	   = CSize(1, 1);
	m_xMinSize = CSize(0, 0);
	m_xMaxSize = CSize(-1, -1);

	m_eCursor  = CMouseCursor::CT_Arrow;
	m_xVisType = xWM.GetStandardVisualizationType();

}


//---------------------------------------------------------------------------------------------------------------------
CWindow::~CWindow()
{
	CWindowMgr& wm = CWindowMgr::Get();

	if(GetParent())
	{
		wm.GetWindow(GetParent())->RemovePhysicalChild(GetWHDL());
	}

	// alle Kinder löschen - Vorsicht: ein Kind zu löschen könnte das Löschen anderer
	// nach sich ziehen

	int iC = m_ahSubs.Size();
	while(iC > 0)
	{
		CWindow* pxWalk=wm.GetWindow(m_ahSubs[0]);
		pxWalk->SetParent(0);
		pxWalk->DeleteNow();
		m_ahSubs.DeleteEntry(0);
		iC = m_ahSubs.Size();
	}

	CWindowMgr::Get().UnregisterWindow(this);
}


//---------------------------------------------------------------------------------------------------------------------
///	erzeugt ein neues Fenster
CWindow*
CWindow::Create()
{ 
	return new CWindow(); 
}


//---------------------------------------------------------------------------------------------------------------------
void
CWindow::Destroy()
{ 
	SetVisible(false);
	CWindowMgr::Get().DeleteWindowDelayed(this); 
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	setzt die Größe des Fensters; Fenster erhält eine Resize-Message, falls 
	die Größe sich wirklich geändert hat
*/
void
CWindow::SetSize(const CSize& p_rxSize) 
{ 
	CSize xNewSize = p_rxSize;
	xNewSize.cx = max(p_rxSize.cx, m_xMinSize.cx);
	xNewSize.cy = max(p_rxSize.cy, m_xMinSize.cy);
	if(m_xMaxSize.cx >= 0)
	{
		xNewSize.cx = min(xNewSize.cx, m_xMaxSize.cx);
	}
	if(m_xMaxSize.cy >= 0)
	{
		xNewSize.cy = min(xNewSize.cy, m_xMaxSize.cy);
	}

	if(m_xSize != xNewSize  &&  !GetIsDesktop())
	{
		CWindowMgr& wm = CWindowMgr::Get();
		wm.Invalidate(m_hWnd);
		m_xSize=xNewSize; 
		wm.Invalidate(m_hWnd);
		wm.SendMsg(CWindowSizeChangeMsg(), GetWHDL());
		if(GetParent())
		{
			wm.SendMsg(CWindowChildResizedMsg(GetWHDL()), GetParent());
		}
	}
}



//---------------------------------------------------------------------------------------------------------------------
void
CWindow::AssureMinSize(const CSize& p_rxSize)
{
	int iW = max(GetSize().cx, p_rxSize.cx);
	int iH = max(GetSize().cy, p_rxSize.cy);
	
	SetSize(iW, iH);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CWindow::AssureMaxSize(const CSize& p_rxSize)
{
	int iW, iH;
	if(p_rxSize.cx >= 0)
	{
		iW = min(GetSize().cx, p_rxSize.cx);
	}
	else
	{
		iW = GetSize().cx;
	}
	if(p_rxSize.cy >= 0)
	{
		iH = min(GetSize().cy, p_rxSize.cy);
	}
	else
	{
		iH = GetSize().cy;
	}
	
	SetSize(iW, iH);
}



//---------------------------------------------------------------------------------------------------------------------
void 
CWindow::AutoSize(bool p_bMayShrink)
{
	CSize xMaxSize;
	
	if(p_bMayShrink)
	{
		xMaxSize = CSize(0, 0);
	}
	else
	{
		xMaxSize = GetSize();
	}

	CWindowMgr& wm = CWindowMgr::Get();

	int i, iC = m_ahSubs.Size();
	for(i=0; i<iC; ++i)
	{
		CWindow* pWnd = wm.GetWindow(m_ahSubs[i]);
		CSize xSize = CSize(pWnd->GetPos().x, pWnd->GetPos().y) + pWnd->GetSize();
		xMaxSize.cx = max(xSize.cx, xMaxSize.cx);
		xMaxSize.cy = max(xSize.cy, xMaxSize.cy);
	}

	AssureMinSize(xMaxSize);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CWindow::ConstraintMinSize(const CSize& p_rxMinSize)
{
	CSize xMinSize;
	xMinSize.cx = (m_xMinSize.cx < p_rxMinSize.cx) ? p_rxMinSize.cx : m_xMinSize.cx; 
	xMinSize.cy = (m_xMinSize.cy < p_rxMinSize.cy) ? p_rxMinSize.cy : m_xMinSize.cy; 
	SetMinSize(xMinSize);
}



//---------------------------------------------------------------------------------------------------------------------
void 
CWindow::ConstraintMaxSize(const CSize& p_rxMaxSize)
{
	CSize xMaxSize = GetMaxSize();
	if(p_rxMaxSize.cx >= 0)
	{
		if(xMaxSize.cx >= 0)
		{
			xMaxSize.cx = (m_xMaxSize.cx > p_rxMaxSize.cx) ? p_rxMaxSize.cx : m_xMaxSize.cx; 
		}
		else
		{
			xMaxSize.cx = p_rxMaxSize.cx;
		}
	}
	if(p_rxMaxSize.cy >= 0)
	{
		if(xMaxSize.cy >= 0)
		{
			xMaxSize.cy = (m_xMaxSize.cy > p_rxMaxSize.cy) ? p_rxMaxSize.cy : m_xMaxSize.cy;
		}
		else
		{
			xMaxSize.cy = p_rxMaxSize.cy;
		}
	}
	SetMaxSize(xMaxSize);
}



//---------------------------------------------------------------------------------------------------------------------
/** 
	überprüft, ob dieses Fenster oder eines seiner Kinder für die geg. Koordinaten
	verantwortlich ist. Liefert 0 wenn nicht und das fragliche Fenster wenn doch
*/
CWindow* 
CWindow::HitTest(const CPnt& p_rxMousePos)
{
	if(!GetVisible())
	{
		return 0;
	}

	CRct rctAbsRect=GetAbsRect();
	if(rctAbsRect.Hit(p_rxMousePos))
	{
		CWindow* pxSub;
		int i,iC=m_ahSubs.Size();
		for(i=iC-1; i>=0; --i)
		{
			pxSub=CWindowMgr::Get().GetWindow(m_ahSubs[i]);
			CWindow* pxHit=pxSub->HitTest(p_rxMousePos);
			if(pxHit)
			{
				return pxHit;
			}
		}
		return this;		
	}	
	return 0;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CWindow::Paint(const CPaintContext& p_rxCtx)
{	
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());
	v->DrawBackground(p_rxCtx, GetRect());
}


//---------------------------------------------------------------------------------------------------------------------
/// sagt dem WindowMgr, dass wir indirekte Aktivierungs- und Deaktivierungnachrichten wollen
void 
CWindow::SetIndirectActivationMessages(bool b)
{ 
	CWindowMgr::Get().SetWindowProperty(GetWHDL(), CWindowMgr::WP_WantIndirectActivateMsg, b); 
}



//---------------------------------------------------------------------------------------------------------------------
/// Behandelt Nachricht; liefert false, wenn Nachricht nicht behandelt wurde
bool
CWindow::HandleMsg(const CMessage& p_rxMessage)
{	
	if(p_rxMessage.IsMouseMessage())						{ return HandleMouseMsg(p_rxMessage); }
	else if(p_rxMessage == msgCharacterKey)					{ return OnCharacterKey(p_rxMessage.GetKey(), p_rxMessage.GetKeyModifier()); } 
	else if(p_rxMessage == msgControlKey)					{ return OnControlKey(p_rxMessage.GetKey(), p_rxMessage.GetKeyModifier());	}
	else if(p_rxMessage == msgWindowActivation)				{ return OnActivate(); }		
	else if (p_rxMessage == msgWindowDeactivation)			{ return OnDeactivate(); }
	else if(p_rxMessage == msgWindowIndirectActivation)		{ return OnActivateIndirect(); } 
	else if (p_rxMessage == msgWindowIndirectDeactivation)	{ return OnDeactivateIndirect(); }
	else if(p_rxMessage == msgWindowSizeChange)				{ return OnResize(); }
	else if(p_rxMessage == msgWindowChildResized)			{ return true; }
	else if(p_rxMessage == msgWindowDeviceChange)			{ return OnDeviceChange(); }	
	else if(p_rxMessage == msgWindowVisualizationChange)	{ return OnVisualizationChange(); }
	else if(p_rxMessage == msgTimer)						{ return this->OnTimer(p_rxMessage.GetTimerID()); }
	else return false;
}

//---------------------------------------------------------------------------------------------------------------------
/// Behandelt Nachricht; liefert false, wenn Nachricht nicht behandelt wurde
bool
CWindow::HandleMouseMsg(const CMessage& p_rxMessage)
{
	CPnt xMousePos = p_rxMessage.GetPos();
	CWindowMgr& wm = CWindowMgr::Get();
	wm.ToClientPos(GetWHDL(), xMousePos);

	if(p_rxMessage == msgMouseMove)							{ return this->OnMouseMove(xMousePos, p_rxMessage.GetKeyModifier()); }	
	else if(p_rxMessage == msgMouseLeftButtonDown)			{ return this->OnLButtonDown(xMousePos, p_rxMessage.GetKeyModifier()); }
	else if(p_rxMessage == msgMouseLeftButtonUp)			{ return this->OnLButtonUp(xMousePos, p_rxMessage.GetKeyModifier()); }
	else if(p_rxMessage == msgMouseLeftButtonDoubleClick)	{ return this->OnLButtonDoubleClick(xMousePos, p_rxMessage.GetKeyModifier()); }
	else if(p_rxMessage == msgMouseRightButtonDown)			{ return this->OnRButtonDown(xMousePos, p_rxMessage.GetKeyModifier()); }
	else if(p_rxMessage == msgMouseRightButtonUp)			{ return this->OnRButtonUp(xMousePos, p_rxMessage.GetKeyModifier()); }
	else if(p_rxMessage == msgMouseRightButtonDoubleClick)	{ return this->OnRButtonDoubleClick(xMousePos, p_rxMessage.GetKeyModifier()); }
	else if(p_rxMessage == msgMouseMiddleButtonDown)		{ return this->OnMButtonDown(xMousePos, p_rxMessage.GetKeyModifier()); }
	else if(p_rxMessage == msgMouseMiddleButtonUp)			{ return this->OnMButtonUp(xMousePos, p_rxMessage.GetKeyModifier()); }
	else if(p_rxMessage == msgMouseMiddleButtonDoubleClick)	{ return this->OnMButtonDoubleClick(xMousePos, p_rxMessage.GetKeyModifier()); }
	else return false;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CWindow::OnMouseMove(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier)
{
	if(m_xOnMouseMoveCallback)
	{
		m_xOnMouseMoveCallback(this, p_rxRelativeMousePos);
		return true;
	}
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CWindow::OnLButtonDown(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier)
{
	if(m_xOnLButtonDownCallback)
	{
		m_xOnLButtonDownCallback(this, p_rxRelativeMousePos);
		return true;
	}
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CWindow::OnRButtonDown(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier)
{
	if(m_xOnRButtonDownCallback)
	{
		m_xOnRButtonDownCallback(this, p_rxRelativeMousePos);
		return true;
	}
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CWindow::OnMButtonDown(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier)
{
	if(m_xOnMButtonDownCallback)
	{
		m_xOnMButtonDownCallback(this, p_rxRelativeMousePos);
		return true;
	}
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CWindow::OnLButtonUp(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier)
{
	if(m_xOnLButtonUpCallback)
	{
		m_xOnLButtonUpCallback(this, p_rxRelativeMousePos);
		return true;
	}
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CWindow::OnRButtonUp(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier)
{
	if(m_xOnRButtonUpCallback)
	{
		m_xOnRButtonUpCallback(this, p_rxRelativeMousePos);
		return true;
	}
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CWindow::OnMButtonUp(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier)
{
	if(m_xOnMButtonUpCallback)
	{
		m_xOnMButtonUpCallback(this, p_rxRelativeMousePos);
		return true;
	}
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CWindow::OnLButtonDoubleClick(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier)
{
	if(m_xOnLButtonDoubleClickCallback)
	{
		m_xOnLButtonDoubleClickCallback(this, p_rxRelativeMousePos);
		return true;
	}
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CWindow::OnRButtonDoubleClick(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier)
{
	if(m_xOnRButtonDoubleClickCallback)
	{
		m_xOnRButtonDoubleClickCallback(this, p_rxRelativeMousePos);
		return true;
	}
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CWindow::OnMButtonDoubleClick(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier)
{
	if(m_xOnMButtonDoubleClickCallback)
	{
		m_xOnMButtonDoubleClickCallback(this, p_rxRelativeMousePos);
		return true;
	}
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
///	behandelt Nachrichten in der Nachrichtenschlange
void 
CWindow::MessagePump()
{
	// die Nachrichtenwarteschlange kann während der Verarbeitung wachsen
	// es ist wichtig, dass nur die alten Nachrichten jetzt verarbeitet werden,
	// Nachrichten, die erst während der Verarbeitung dazukommen, müssen einen
	// Takt warten (sonst gibt es potentiell eine Endlosschleife)

	size_t iNumOldEvents = m_xMsgQueue.size();
	CWindowMgr& wm = CWindowMgr::Get();
	CWindow* pxModalWindow = wm.GetModalWindow();
	while(iNumOldEvents>0)
	{
		if(!this->HandleMsg(m_xMsgQueue.front())  &&  GetParent()  &&  
			m_xMsgQueue.front().DoesBubbleUp() &&  pxModalWindow != this)
		{
			wm.SendMsg(m_xMsgQueue.front(), GetParent());
		}
		m_xMsgQueue.pop_front();
		iNumOldEvents--;
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CWindow::OnActivate()
{
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CWindow::OnDeactivate()
{
	return true;
}	


//---------------------------------------------------------------------------------------------------------------------
bool 
CWindow::OnPaint()
{
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CWindow::OnResize()
{
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CWindow::OnDeviceChange()
{
	return this->OnVisualizationChange();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CWindow::OnVisualizationChange()
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDevice(GetWHDL());
	if(pxDevice)
	{
		CVisualization* v = CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());
		m_bAllWndsTransparent = v->GetMetrics()->m_bAllWindowsTransparent;
	}

	InvalidateWindow();	
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
CRct 
CWindow::GetAbsRect() const
{
	CWindowMgr& xWM=CWindowMgr::Get();
	CRct xResult=GetRect()+m_xPos;
	WHDL hParent=m_hParent;
	while(hParent)
	{
		CWindow* pxParent=xWM.GetWindow(hParent);
		xResult+=pxParent->m_xPos;	   
		hParent=pxParent->m_hParent;
	}
	return xResult;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CWindow::SetPos(CPnt p_pntPos)
{ 
	if(GetIsTopLevelWindow())
	{
		CWindowMgr& wm = CWindowMgr::Get(); 

		InvalidateWindow();
		m_xPos=p_pntPos; 
		wm.WindowDeviceChange(this);

		CWindow* pxParent = wm.GetWindow(GetParent());
		m_xPos -= pxParent->GetPos();

		InvalidateWindow();
	}
	else if(m_xPos != p_pntPos  &&  !GetIsDesktop())
	{
		InvalidateWindow();
		m_xPos=p_pntPos; 
		InvalidateWindow();
	}
}


//---------------------------------------------------------------------------------------------------------------------
CPnt 
CWindow::GetPos() const
{
	if(GetIsTopLevelWindow())
	{
		return GetAbsPos();
	}
	else
	{
		return GetRelPos(); 
	}
}



//---------------------------------------------------------------------------------------------------------------------
CPnt 
CWindow::GetAbsPos() const
{
	CWindowMgr& xWM=CWindowMgr::Get();
	CPnt pntRetPos=GetRelPos();
	WHDL hWalk=m_hParent;
	while(hWalk)
	{
		CWindow* pxWalk=xWM.GetWindow(hWalk);
		pntRetPos+=pxWalk->GetRelPos();		
		hWalk=pxWalk->m_hParent;		
	}
	return pntRetPos;
}


//---------------------------------------------------------------------------------------------------------------------
CPnt 
CWindow::ConvertToClientPos(const CPnt& p_rxAbsPos) const
{
	return p_rxAbsPos-GetAbsPos();	
}

//---------------------------------------------------------------------------------------------------------------------
void
CWindow::CenterOnParentWindow()
{
	if(GetParent())
	{
		CSize xSize = CWindowMgr::Get().GetWindow(GetParent())->GetSize();
		SetPos((xSize.cx - GetSize().cx) / 2, (xSize.cy - GetSize().cy) / 2);
	}
}
//---------------------------------------------------------------------------------------------------------------------
CWindow* 
CWindow::GetRootWindow() const
{
	CWindowMgr& xWM=CWindowMgr::Get();

	CWindow* pxWalk = xWM.GetWindow(m_hWnd);
	while(pxWalk->m_hParent)
	{
		pxWalk=xWM.GetWindow(pxWalk->m_hParent);
	}	
	return pxWalk;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CWindow::IsChildOf(WHDL p_hWnd) const
{
	CWindowMgr& xWM=CWindowMgr::Get();

	WHDL hWalk = m_hWnd;
	while((hWalk=xWM.GetWindow(hWalk)->GetParent()) != 0)
	{
		if(p_hWnd == hWalk)	
		{ 
			return true; 
		}
	}	

	return false;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CWindow::RemoveFromParent()
{
	if(GetParent())
	{
		CWindowMgr::Get().GetWindow(GetParent())->RemoveChild(GetWHDL());
	}
}



//---------------------------------------------------------------------------------------------------------------------
void 
CWindow::InvalidateWindow()
{
	if(GetVisible()) 
	{
		CWindowMgr::Get().Invalidate(m_hWnd);
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CWindow::InvalidateWindow(const CRct& p_xrRct)
{
	if(GetVisible()) 
	{
		CWindowMgr::Get().Invalidate(m_hWnd, p_xrRct);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	fügt diesem Fenster ein Kindfenster hinz
	auf dem anderen Fenster wird implizit SetParent() aufgerufen
*/
bool 
CWindow::AddChild(WHDL p_hWnd)
{
 	assert(p_hWnd != m_hWnd);		// Fenster will sein eigenes Kind sein???
	assert(!IsChildOf(p_hWnd));		// Fenster will ein Elternfenster zu seinem Kind machen???
    
	int i = m_ahSubs.Find(p_hWnd);
	if(i != -1)
	{
		assert(false);		// ist schon ein Kind
		return false;
	}


	CWindowMgr& xWM=CWindowMgr::Get();
	CWindow* pxWnd=xWM.GetWindow(p_hWnd);
	assert(pxWnd);		// handle ungültig?

	pxWnd->RemoveFromParent();

	unsigned int iNewPos=m_ahSubs.Size();
	while(iNewPos>0  &&  xWM.GetWindow(m_ahSubs[iNewPos-1])->GetAlwaysOnTop())
	{
		iNewPos--;
	}
	int iIndex=m_ahSubs.PushEntry();
	m_ahSubs[iIndex]=p_hWnd;
	if(iNewPos != iIndex)
	{
		m_ahSubs.MoveEntry(iIndex, iNewPos);
	}

	// flags vom Elternfenster übernehmen

	pxWnd->SetParentDisabled(GetDisabled());
	pxWnd->SetParentVisible(GetVisible());
	if(GetIgnoreModals())
	{
		// ignore modals an, wenn Elternfenster es erlaubt (aber nicht aus wenn nicht!!!)
		pxWnd->SetIgnoreModals(true);
	}

	// Reihenfolge ist wichtig --> SetParent() könnte das Device ändern
	pxWnd->SetParent(m_hWnd);	

	pxWnd->InvalidateWindow();	

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	entfernt ein logisches Kindfenster von diesem Fenster
*/
void 
CWindow::RemoveChild(WHDL p_hWnd)
{
	RemovePhysicalChild(p_hWnd);
}

//---------------------------------------------------------------------------------------------------------------------
/** 
	entfernt ein physisches Kindfenster von diesem Fenster
*/
void 
CWindow::RemovePhysicalChild(WHDL p_hWnd)
{
	CWindowMgr& xWM=CWindowMgr::Get();
	CWindow* pxWnd=xWM.GetWindow(p_hWnd);
	int i = m_ahSubs.Find(p_hWnd);
	assert(i >= 0);		// Parameter ist kein Kind
	
	pxWnd->InvalidateWindow();

	if(i>=0)
	{
		m_ahSubs.DeleteEntry(i);
	}
	pxWnd->SetParent(0);
}

	
//---------------------------------------------------------------------------------------------------------------------
///	bestimmt den Cursor, der in diesem Fenster angezeigt werden soll
void 
CWindow::SetCursor(int p_iCursorType)
{
	m_eCursor = (CMouseCursor::CursorType) p_iCursorType;
	
	if(m_eCursor == CMouseCursor::CT_None  &&  GetIsDesktop())
	{
		// desktops haben kein Elternfenster, deshalb sollten sie immer einen Cursor setzen.
		m_eCursor = CMouseCursor::CT_Arrow;
	}
}


//---------------------------------------------------------------------------------------------------------------------
// löscht das Fenster sofort
void 
CWindow::DeleteNow()
{
	delete this;
}



//---------------------------------------------------------------------------------------------------------------------	
/**
	weist diesem Fenster ein Elternfenster zu; diese Methode wird nur intern
	von AddChild verwendet!!!
*/
void 
CWindow::SetParent(WHDL p_hParent)
{
	assert(m_hParent == 0  ||  p_hParent == 0);		// Elternfenster einfach so überschreiben? Vorher RemoveChild rufen!
	m_hParent=p_hParent;

	CWindowMgr& wm = CWindowMgr::Get();
	if(m_hParent != 0)
	{
		wm.SetDevice(m_hWnd, wm.GetDevice(p_hParent));
	}
	else
	{
		wm.SetDevice(m_hWnd, 0);
	}
}


//---------------------------------------------------------------------------------------------------------------------	
void 
CWindow::SetDisabled(bool p_bDisabled)
{
	if(m_bDisabled != p_bDisabled)
	{
		m_bDisabled = p_bDisabled;  
		OnDisabledStateChange();
	}
}


//---------------------------------------------------------------------------------------------------------------------	
void
CWindow::SetParentDisabled(bool p_bParentDisabled)	
{ 
	if(m_bParentDisabled != p_bParentDisabled)
	{
		m_bParentDisabled = p_bParentDisabled; 
		OnDisabledStateChange();
	}
}


//---------------------------------------------------------------------------------------------------------------------	
void 
CWindow::SetParentVisible(bool p_bParentVisible)	
{ 
	if(m_bParentVisible != p_bParentVisible)
	{
		m_bParentVisible = p_bParentVisible; 
		OnVisibleStateChange();		
	}
}


//---------------------------------------------------------------------------------------------------------------------	
/// erledigt verschiedene Dinge, wenn Fenster enabled/disabled wird	
void 
CWindow::OnDisabledStateChange()
{
	CWindowMgr& wm = CWindowMgr::Get();
	InvalidateWindow(); 

	for(unsigned int i=0; i<m_ahSubs.Size(); ++i)
	{
		CWindow* pxWnd = wm.GetWindow(m_ahSubs[i]);
		pxWnd->SetParentDisabled(GetDisabled());
	}

	// give focus to another window
	if(m_bDisabled  &&  HasFocus())
	{
		WHDL hWalk = GetParent();
		while(hWalk != 0  &&  wm.GetWindow(hWalk)->GetDisabled())
		{
			hWalk = wm.GetWindow(hWalk)->GetParent();
		}
		wm.BringWindowToTop(hWalk);
	}
}



//---------------------------------------------------------------------------------------------------------------------	
/// erledigt verschiedene Dinge, wenn Fenster sichtbar/unsichtbar wird	
void 
CWindow::OnVisibleStateChange()
{
	CWindowMgr& wm = CWindowMgr::Get();
	wm.Invalidate(GetWHDL());		// InvalidateWindow() würde unsere Sichtbarkeit prüfen :)

	for(unsigned int i=0; i<m_ahSubs.Size(); ++i)
	{
		CWindow* pxWnd = wm.GetWindow(m_ahSubs[i]);
		pxWnd->SetParentVisible(GetVisible());
	}
}


//---------------------------------------------------------------------------------------------------------------------	
/** 
	liefert true, wenn dieses Fenster ein Top-Level-Fenster ist, d.h. direktes
	Kind eines Desktops
*/
bool 
CWindow::GetIsTopLevelWindow() const
{
	if(GetParent())
	{
		CWindow* pParent = CWindowMgr::Get().GetWindow(GetParent());
		return pParent->GetIsDesktop();
	}
	else
	{
		return false;
	}
}


//---------------------------------------------------------------------------------------------------------------------	
void 
CWindow::SetToolTipText(CStr p_sText)
{ 
	if(m_sToolTipText != p_sText)
	{
		m_sToolTipText = p_sText;
	}
}



//---------------------------------------------------------------------------------------------------------------------	
///	macht das Fenster sichtbar oder nicht sichtbar
void 
CWindow::SetVisible(bool p_bVisible)
{
	if(m_bVisible != p_bVisible)
	{
		m_bVisible = p_bVisible;  
		OnVisibleStateChange();
	}
}

//---------------------------------------------------------------------------------------------------------------------	
/// \return true if window or one of its children has focus
bool 
CWindow::HasFocusOrChildHasFocus() const
{
	CWindowMgr& wm = CWindowMgr::Get();
	WHDL hFocusWnd = wm.GetFocusWindow();
	
	if(GetWHDL() == hFocusWnd)
	{
		return true;
	}
	if(hFocusWnd)
	{
		CWindow* pxFocusWnd = wm.GetWindow(hFocusWnd);
		return pxFocusWnd->IsChildOf(GetWHDL());
	}
	return false;
}


//---------------------------------------------------------------------------------------------------------------------	
///	\return		true, wenn das Fenster den Fokus hat
bool 
CWindow::HasFocus() const
{
	return GetWHDL() == CWindowMgr::Get().GetFocusWindow();
}


//---------------------------------------------------------------------------------------------------------------------	
/**	
	Erlaubt dem Fenster den Fokus zu erhalten, selbst wenn ein anderes Fenster modal ist.
	Dieses Flag wird automatisch an alle Kindfenster (und deren Kinder usw.) weitergegeben.
	Dieses Flag wird von einigen Fenstertypen benötigt, die eigenen Top-Level-Fenster anlegen, z.B.
	CComboBox.
*/
void 
CWindow::SetIgnoreModals(bool p_bIgnore)
{
	m_bIgnoreModals = p_bIgnore; 

	CWindowMgr& wm = CWindowMgr::Get();
	int i, iC = m_ahSubs.Size();
	for(i=0; i<iC; ++i)
	{
		wm.GetWindow(m_ahSubs[i])->SetIgnoreModals(p_bIgnore);
	}
} 


//---------------------------------------------------------------------------------------------------------------------	
///	Fenster soll in der Z-Order immer ganz oben sein.
void 
CWindow::SetAlwaysOnTop(bool p_bAlwaysOnTop)
{
	if(p_bAlwaysOnTop != m_bAlwaysOnTop)
	{
		m_bAlwaysOnTop = p_bAlwaysOnTop;
		CWindowMgr& wm = CWindowMgr::Get();
		if(m_bAlwaysOnTop)
		{
			wm.BringWindowToTop(GetWHDL(), false);
		}
		else
		{
			if(GetParent())
			{
				CWindow* pxParent = wm.GetWindow(GetParent());
				for(unsigned int i=0; i<pxParent->m_ahSubs.Size(); ++i)
				{
					CWindow* pxSibling = wm.GetWindow(pxParent->m_ahSubs[i]);
					if(pxSibling->GetAlwaysOnTop())
					{
						wm.BringWindowToTop(pxSibling->GetWHDL(), false);
					}
				}
			}
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**	
	false:  bitmap wird mit Hintergrund alpha-ge-blended
	true:	die Bitmap wird gnadenlos auf das Device geschrieben, inklusive ihres Alphakanals
*/
void CWindow::SetWriteAlpha(bool p_bWriteAlpha)
{
	if(m_bWriteAlpha != p_bWriteAlpha)
	{
		m_bWriteAlpha = p_bWriteAlpha;
		InvalidateWindow();
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt den Wert eines benannten Attributes. Siehe UIlib Doku für eine vollständige Liste der Attribute.
	\param	p_rsName	Name des Attributes
	\param	p_rsValue	neuer Wert
	\return	true, wenn erfolgreich
*/
bool 
CWindow::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if(p_rsName=="pos_x")			{ CPnt xNP=GetPos(); xNP.x=p_rsValue.ToInt(); SetPos(xNP); return true; }
	if(p_rsName=="pos_y")			{ CPnt xNP=GetPos(); xNP.y=p_rsValue.ToInt(); SetPos(xNP); return true; }
	if(p_rsName=="width")			{ CSize xNS=GetSize(); xNS.cx=p_rsValue.ToInt(); SetSize(xNS); return true; }
	if(p_rsName=="height")			{ CSize xNS=GetSize(); xNS.cy=p_rsValue.ToInt(); SetSize(xNS); return true; }
	if(p_rsName=="tooltip")		{ SetToolTipText(p_rsValue); return true; }
	if(p_rsName=="disabled")		{ SetDisabled(p_rsValue.ToInt()!=0); return true; }
	if(p_rsName=="visible")		{ SetVisible(p_rsValue.ToInt()!=0); return true; }
	if(p_rsName=="transparency")	{ SetTransparent(p_rsValue.ToInt()!=0); return true; }
	if(p_rsName=="visualization")	{ ChangeVisualization(CFourCC(p_rsValue)) ;return true; }
	if(p_rsName=="writealpha")		{ SetWriteAlpha(p_rsValue.ToInt() !=0 ); return true; }
	return false;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	liefert den Wert eines benannten Attributes. Siehe UILib Doku für eine vollständige Liste der Attribute. ues

	\param	p_rsName		Name des Attributes
	\param	po_srValue		Ausgabe-Parameter: String, der den Wert entgegennimmt
	\return true, wenn Wert zurückgeliefert wurde; false, wenn der Attributnamen nicht bekannt ist
*/
bool 
CWindow::GetAttrib(const CStr& p_rsName, CStr& po_srValue) const
{
	if(p_rsName == "pos_x")		{ po_srValue.Format("%d",GetPos().x); return true; }
	if(p_rsName == "pos_y")		{ po_srValue.Format("%d",GetPos().y); return true; }
	if(p_rsName == "width")		{ po_srValue.Format("%d",GetSize().cx); return true; }
	if(p_rsName == "height")		{ po_srValue.Format("%d",GetSize().cy); return true; }
	if(p_rsName == "tooltip")		{ po_srValue = m_sToolTipText; return true; }
	if(p_rsName == "disabled")		{ po_srValue = (GetDisabled()?"1":"0"); return true; }
	if(p_rsName == "visible")		{ po_srValue = (m_bVisible?"1":"0"); return true; }		
	if(p_rsName == "transparency")	{ po_srValue = (m_bTransparent?"1":"0"); return true; }
	if(p_rsName == "writealpha")	{ po_srValue = (GetWriteAlpha() ? "1" : "0"); return true; }
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CWindow::ChangeVisualization(CFourCC p_xVisType)
{
	CWindowMgr& wm = CWindowMgr::Get();

	m_xVisType = p_xVisType;		///< visualization type
	wm.SendMsg(CWindowVisualizationChangeMsg(), GetWHDL());
	InvalidateWindow();
}


//---------------------------------------------------------------------------------------------------------------------
/// setzt einen Timer für dieses Fenster
int 
CWindow::SetTimer(int p_iInterval, bool p_bRepeating)
{
	return CWindowMgr::Get().SetTimer(GetWHDL(), p_iInterval, p_bRepeating);
}


//---------------------------------------------------------------------------------------------------------------------
/// löscht einen Timer für dieses Fenster
bool 
CWindow::UnsetTimer(int p_iID)
{
	return CWindowMgr::Get().UnsetTimer(p_iID);
}


//---------------------------------------------------------------------------------------------------------------------
/// setzt einen Timer für dieses Fenster neu (d.h. ändert seine Parameter)
bool 
CWindow::ResetTimer(int p_iID, int p_iInterval, bool p_bRepeating)
{
	return CWindowMgr::Get().ResetTimer(p_iID, p_iInterval, p_bRepeating);
}

//---------------------------------------------------------------------------------------------------------------------
/// aktiviert dieses Fenster, d.h. holt es in den Vordergrund und gibt ihm den Eingabefokus
void				
CWindow::SetFocus() const
{
	CWindowMgr::Get().BringWindowToTop(GetWHDL(), true);
}
//---------------------------------------------------------------------------------------------------------------------

} //namespace UILib




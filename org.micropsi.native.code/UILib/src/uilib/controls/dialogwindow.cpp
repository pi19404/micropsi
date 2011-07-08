#include "stdafx.h"
#include "uilib/controls/dialogwindow.h"
#include "uilib/core/windowmanager.h"	
#include "uilib/core/visualizationfactory.h"	

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CDialogWindow::CCloseButton* 
CDialogWindow::CCloseButton::Create()
{
	return new CCloseButton();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CDialogWindow::CCloseButton::DeleteNow()
{
	delete this;	
}

//---------------------------------------------------------------------------------------------------------------------
void 
CDialogWindow::CCloseButton::Paint(const CPaintContext& p_rxCtx)
{
	CVisualization::FrameType eFt = CVisualization::FT_BtnUp;
	if(!GetDisabled())
	{
		if(GetButtonDown()) { eFt = CVisualization::FT_BtnDown; }
	}
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());
	v->DrawCloseButton(p_rxCtx, GetRect(), eFt);
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CDialogWindow::CCloseButton::OnClick()
{
	if(GetParent() != 0)
	{
		return ((CDialogWindow*) CWindowMgr::Get().GetWindow(GetParent()))->OnClose();
	}
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
CDialogWindow::CDialogWindow()
{
	m_bDraggable = true;
	m_bDrag  = false;
	m_iTitleBarHeight = 0;
	m_iClientAreaY = 0;

	m_pxClientArea = CWindow::Create();
	CWindow::AddChild(m_pxClientArea->GetWHDL());
	SetCaption("DialogWindow");

	CWindowMgr::Get().SetIndirectActivationMessages(GetWHDL(), true);

	m_pxCloseButton = 0;
}

//---------------------------------------------------------------------------------------------------------------------
CDialogWindow::~CDialogWindow()
{
}

//---------------------------------------------------------------------------------------------------------------------
CDialogWindow* 
CDialogWindow::Create()
{
	return new CDialogWindow();
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CDialogWindow::AddChild(WHDL p_hWnd)
{
	return m_pxClientArea->AddChild(p_hWnd);
}

//---------------------------------------------------------------------------------------------------------------------
int 
CDialogWindow::NumChildWindows() const
{ 
	return m_pxClientArea->NumChildWindows(); 
}

//---------------------------------------------------------------------------------------------------------------------
WHDL 
CDialogWindow::GetChild(int p_iIndex) const
{ 
	return m_pxClientArea->GetChild(p_iIndex); 
}

//---------------------------------------------------------------------------------------------------------------------
void 
CDialogWindow::RemoveChild(WHDL p_hWnd)
{
	m_pxClientArea->RemoveChild(p_hWnd);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CDialogWindow::SetCaption(const CStr& p_rsText)
{
	if(m_sCaption != p_rsText)
	{
		m_sCaption = p_rsText;
		InvalidateTitleBar();
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CDialogWindow::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgKeyDown  ||  p_rxMessage == msgKeyDown)
	{
		return true;		
	}
	else if(p_rxMessage == msgMouseLeftButtonDown)
	{
		return this->OnLButtonDown(p_rxMessage.GetPos());
	}
	else if(p_rxMessage == msgMouseLeftButtonUp)
	{
		return this->OnLButtonUp(p_rxMessage.GetPos());
	}
	else if(p_rxMessage == msgMouseMove)
	{
		return this->OnMouseMove(p_rxMessage.GetPos());
	}	
	else
	{
		return __super::HandleMsg(p_rxMessage);
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CDialogWindow::OnLButtonDown(const CPnt& p_rxMousePos)
{
	// check if mouse is over title bar
	if(m_bDraggable)
	{
		CPnt pntMouse = p_rxMousePos;
		CWindowMgr::Get().ToClientPos(GetWHDL(), pntMouse);
		if(	pntMouse.y > m_xTitleBarPos.y  &&  pntMouse.y < m_xTitleBarPos.y + m_iTitleBarHeight  && 
			pntMouse.x > m_xTitleBarPos.x  &&  pntMouse.x < GetSize().cx - m_xTitleBarPos.x)
		{
			CWindowMgr::Get().SetCapture(this);
			m_bDrag = true;
			m_pntDragStart	 = p_rxMousePos;
			m_pntOriginalPos = GetPos();
		}
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CDialogWindow::OnLButtonUp(const CPnt& p_rxMousePos)
{
	if(m_bDrag)
	{
		CWindowMgr::Get().ReleaseCapture(this);
		m_bDrag = false;
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CDialogWindow::OnMouseMove(const CPnt& p_rxMousePos)
{
	if(m_bDrag)
	{
		if(GetParent())
		{
			CWindow* pxParent = CWindowMgr::Get().GetWindow(GetParent());
			if(pxParent->GetIsDesktop())
			{
				// ignore dragging if mouse is not on a device
				if(!CWindowMgr::Get().IsPointOnAnyDevice(p_rxMousePos))
				{
					return true;
				}
			}
			else
			{
				// ignore dragging if mouse is outside parent window
				if(!pxParent->GetAbsRect().Hit(p_rxMousePos))
				{
					return true;
				}
			}
		}

		CPnt pntMouse = p_rxMousePos;
		CWindowMgr::Get().ToClientPos(GetWHDL(), pntMouse);
		SetPos(m_pntOriginalPos + (p_rxMousePos - m_pntDragStart));
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CDialogWindow::Paint(const CPaintContext& p_rxCtx)
{	
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());
	CRct xFrameSize  = v->GetFrameSize(CVisualization::FT_WindowFixed);

	CRct xInterior = GetRect();
	xInterior.left   += m_xFrameSize.left;
	xInterior.right  -= m_xFrameSize.right;
	xInterior.top    += m_iClientAreaY;
	xInterior.bottom -= m_xFrameSize.bottom;
//	v->DrawBackground(p_rxCtx, xInterior);

	v->DrawTitleBar(p_rxCtx, m_xTitleBarPos, GetSize().cx - 2*m_xTitleBarPos.x, m_sCaption, HasFocusOrChildHasFocus());
	v->DrawFrame(p_rxCtx, GetRect(), CVisualization::FT_WindowFixed, GetDisabled());
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CDialogWindow::OnResize()
{
	m_pxClientArea->SetSize(CSize(GetSize().cx - m_xFrameSize.left - m_xFrameSize.right, GetSize().cy - m_xFrameSize.bottom - m_iClientAreaY));
	if(m_pxCloseButton)
	{
		m_pxCloseButton->SetSize(CSize(m_iTitleBarHeight - 2, m_iTitleBarHeight - 2));
		m_pxCloseButton->SetPos(GetSize().cx - m_xFrameSize.right - 1 - m_pxCloseButton->GetSize().cx, m_xFrameSize.top + 1);
	}
	return this->CWindow::OnResize();
}

//---------------------------------------------------------------------------------------------------------------------
/**
	called when visualization has changed
*/
bool 
CDialogWindow::OnVisualizationChange()
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		CVisualization* v	= CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());
		m_xFrameSize		= v->GetFrameSize(CVisualization::FT_WindowFixed);
		m_iTitleBarHeight	= v->GetMetrics()->m_iTitleBarHeight;
		m_xTitleBarPos		= v->GetMetrics()->m_xTitleBarPos;
	}

	m_iClientAreaY = max(m_xFrameSize.top, m_xTitleBarPos.y + m_iTitleBarHeight);
	m_pxClientArea->SetPos(CPnt(m_xFrameSize.left, m_iClientAreaY));
	m_pxClientArea->SetSize(CSize(GetSize().cx - m_xFrameSize.left - m_xFrameSize.right, GetSize().cy - m_xFrameSize.bottom - m_iClientAreaY));

	CSize xMinSize = CSize(m_iTitleBarHeight*3 + m_xFrameSize.left + m_xFrameSize.right, m_iClientAreaY + m_xFrameSize.bottom);
	xMinSize.cx = max(xMinSize.cx, GetMinSize().cx);
	xMinSize.cy = max(xMinSize.cy, GetMinSize().cy);
	SetMinSize(xMinSize);

	OnResize();
	return this->__super::OnVisualizationChange();
}


//---------------------------------------------------------------------------------------------------------------------
void 
CDialogWindow::InvalidateTitleBar()
{
	CWindowMgr::Get().Invalidate(GetWHDL(), CRct(m_xTitleBarPos.x, m_xTitleBarPos.y, 
												  GetSize().cx - m_xTitleBarPos.x, m_xTitleBarPos.y + m_iTitleBarHeight));
}



//---------------------------------------------------------------------------------------------------------------------
bool 
CDialogWindow::OnActivateIndirect()
{ 
	InvalidateTitleBar(); 
	return true; 
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CDialogWindow::OnDeactivateIndirect()
{ 
	InvalidateTitleBar(); 
	return true; 
}

//---------------------------------------------------------------------------------------------------------------------
void
CDialogWindow::SetHasCloseButton(bool p_bCloseButton)
{
	if(p_bCloseButton && !m_pxCloseButton)
	{
		// hab keinen, brauche einen

		m_pxCloseButton = CCloseButton::Create();
		CWindow::AddChild(m_pxCloseButton->GetWHDL());
		OnResize();
	}
	else if(!p_bCloseButton  &&  m_pxCloseButton)
	{
		// hab einen, sollte aber nicht

		m_pxCloseButton->Destroy();
		m_pxCloseButton = 0;
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
CDialogWindow::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if(p_rsName=="caption")		{ SetCaption(p_rsValue);				return true; }
	if(p_rsName=="draggable")	{ SetDraggable(p_rsValue.ToInt() != 0);	return true; }
	return __super::SetAttrib(p_rsName,p_rsValue);
}

//---------------------------------------------------------------------------------------------------------------------
/**
	liefert den Wert eines benannten Attributes. Siehe UILib Doku für eine vollständige Liste der Attribute. ues

	\param	p_rsName		Name des Attributes
	\param	po_srValue		Ausgabe-Parameter: String, der den Wert entgegennimmt
	\return true, wenn Wert zurückgeliefert wurde; false, wenn der Attributnamen nicht bekannt ist
*/
bool 
CDialogWindow::GetAttrib(const CStr& p_rsName, CStr& po_rsValue) const
{
	if(p_rsName=="caption")		{ po_rsValue = GetCaption();				return true; }
	if(p_rsName=="draggable")	{ po_rsValue = (GetDraggable()?"1":"0");	return true; }
	return __super::GetAttrib(p_rsName,po_rsValue);
}

//---------------------------------------------------------------------------------------------------------------------
bool		
CDialogWindow::OnClose()
{
	CWindowMgr::Get().PostMsg(CDialogWindowClosedMsg(GetWHDL()), GetParent());
	if(m_xOnCloseCallback)
	{
		m_xOnCloseCallback(this);
	}
	return true; 
}

//---------------------------------------------------------------------------------------------------------------------


} //namespace UILib




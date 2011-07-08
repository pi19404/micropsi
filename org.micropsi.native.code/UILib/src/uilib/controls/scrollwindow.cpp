#include "stdafx.h"
#include "uilib/controls/scrollwindow.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CScrollWindow::CScrollWindow()
{
	m_pxHScrollBar = CScrollBar::Create(); 
	m_pxHScrollBar->SetStyle(CScrollBar::SB_Horizontal);
	m_pxVScrollBar = CScrollBar::Create(); 
	m_pxVScrollBar->SetStyle(CScrollBar::SB_Vertical);
	m_pxClientArea = CBasicScrollWindow::Create();
	CWindow::AddChild(m_pxHScrollBar->GetWHDL());
	CWindow::AddChild(m_pxVScrollBar->GetWHDL());
	CWindow::AddChild(m_pxClientArea->GetWHDL());
	m_pxClientArea->SetPos(CPnt(0, 0));
	m_iHScrollType = m_iVScrollType = ST_AUTO;
}

//---------------------------------------------------------------------------------------------------------------------
CScrollWindow::~CScrollWindow()
{
	CWindow::RemoveChild(m_pxHScrollBar->GetWHDL());
	CWindow::RemoveChild(m_pxVScrollBar->GetWHDL());
	CWindow::RemoveChild(m_pxClientArea->GetWHDL());
	m_pxHScrollBar->Destroy();
	m_pxVScrollBar->Destroy();
	m_pxClientArea->Destroy();
}

//---------------------------------------------------------------------------------------------------------------------
CScrollWindow* 
CScrollWindow::Create()
{
	return new CScrollWindow();
}

//---------------------------------------------------------------------------------------------------------------------
int 
CScrollWindow::SetHScrollPos(int iHPos)	
{	
	m_pxClientArea->SetScrollPos(iHPos, GetVScrollPos()); 
	m_pxHScrollBar->SetScrollPos(m_pxClientArea->GetHScrollPos());
	return m_pxClientArea->GetHScrollPos();	
}

//---------------------------------------------------------------------------------------------------------------------
int	
CScrollWindow::SetVScrollPos(int iVPos)	
{	
	m_pxClientArea->SetScrollPos(GetHScrollPos(), iVPos); 
	m_pxVScrollBar->SetScrollPos(m_pxClientArea->GetVScrollPos());
	return m_pxClientArea->GetVScrollPos();	
}

//---------------------------------------------------------------------------------------------------------------------
int 
CScrollWindow::SetHScrollSpeed(int p_iSpeed)
{
	return m_pxHScrollBar->SetScrollSpeed(p_iSpeed);
}

//---------------------------------------------------------------------------------------------------------------------
int	
CScrollWindow::SetVScrollSpeed(int p_iSpeed)
{
	return m_pxVScrollBar->SetScrollSpeed(p_iSpeed);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CScrollWindow::SetScrollSpeed(int p_iSpeed)
{
	m_pxHScrollBar->SetScrollSpeed(p_iSpeed);
	m_pxVScrollBar->SetScrollSpeed(p_iSpeed);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CScrollWindow::SetClientAreaSize(const CSize& p_rxSize)	
{ 
	m_pxClientArea->SetClientAreaSize(p_rxSize); 
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CScrollWindow::AddChild(WHDL p_hWnd)
{
	return m_pxClientArea->AddChild(p_hWnd);
}

//---------------------------------------------------------------------------------------------------------------------
int 
CScrollWindow::NumChildWindows() const
{ 
	return m_pxClientArea->NumChildWindows(); 
}

//---------------------------------------------------------------------------------------------------------------------
WHDL 
CScrollWindow::GetChild(int p_iIndex) const
{ 
	return m_pxClientArea->GetChild(p_iIndex); 
}

//---------------------------------------------------------------------------------------------------------------------
void 
CScrollWindow::RemoveChild(WHDL p_hWnd)
{
	m_pxClientArea->RemoveChild(p_hWnd);
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CScrollWindow::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgBasicScrollWindowChanged)
	{
		OnResize();
		return true;
	}
	else if(p_rxMessage == msgScrollBarChanged)
	{
		m_pxClientArea->SetScrollPos(m_pxHScrollBar->GetScrollPos(), m_pxVScrollBar->GetScrollPos());
		return true;
	}
	else
	{
		return CWindow::HandleMsg(p_rxMessage);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CScrollWindow::Paint(const CPaintContext& p_rxCtx)
{
	if(HasHScrollbar()  &&  HasVScrollbar())
	{
		CRct r = GetRect();
		r.top  = r.bottom - m_pxHScrollBar->GetSize().cy;
		r.left = r.right  - m_pxVScrollBar->GetSize().cx;

		CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());
		v->DrawBackground(p_rxCtx, r);
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CScrollWindow::OnResize()
{
	CSize xClientAreaSize = GetSize();
	m_pxClientArea->SetSize(xClientAreaSize);

	bool bHBar = false;

	m_pxHScrollBar->SetPos(CPnt(0, GetSize().cy - m_pxHScrollBar->GetSize().cy));
	m_pxVScrollBar->SetPos(CPnt(GetSize().cx - m_pxVScrollBar->GetSize().cx, 0));

	if(HasHScrollbar())
	{
		xClientAreaSize.cy -= m_pxHScrollBar->GetSize().cy;
		m_pxClientArea->SetSize(xClientAreaSize);
		bHBar = true;
		m_pxHScrollBar->SetVisible(true);
	}
	else
	{
		m_pxHScrollBar->SetVisible(false);
	}

	if(HasVScrollbar())
	{
		xClientAreaSize.cx -= m_pxVScrollBar->GetSize().cx;
		m_pxClientArea->SetSize(xClientAreaSize);
		m_pxVScrollBar->SetVisible(true);
	}
	else
	{
		m_pxVScrollBar->SetVisible(false);
	}

	if(bHBar == false  &&  HasHScrollbar())
	{
		m_pxHScrollBar->SetVisible(true);
		xClientAreaSize.cy -= m_pxHScrollBar->GetSize().cy;
		m_pxClientArea->SetSize(xClientAreaSize);
	}

	if(HasHScrollbar())
	{
		if(HasVScrollbar())
		{
			m_pxHScrollBar->SetSize(CSize(GetSize().cx - m_pxVScrollBar->GetSize().cx, 0));
		}
		else
		{
			m_pxHScrollBar->SetSize(CSize(GetSize().cx, 0));
		}
		m_pxHScrollBar->SetScrollRange(m_pxClientArea->GetClientAreaSize().cx -1);
		m_pxHScrollBar->SetPageSize(m_pxClientArea->GetSize().cx);
		m_pxHScrollBar->SetDisabled(GetHScrollRange() == 0);
	}

	if(HasVScrollbar())
	{
		if(HasHScrollbar())
		{
			m_pxVScrollBar->SetSize(CSize(0, GetSize().cy - m_pxHScrollBar->GetSize().cy));
		}
		else
		{
			m_pxVScrollBar->SetSize(CSize(0, GetSize().cy));
		}
		m_pxVScrollBar->SetScrollRange(m_pxClientArea->GetClientAreaSize().cy -1);
		m_pxVScrollBar->SetPageSize(m_pxClientArea->GetSize().cy);
		m_pxVScrollBar->SetDisabled(GetVScrollRange() == 0);
	}

	m_pxClientArea->SetScrollPos(m_pxHScrollBar->GetScrollPos(), m_pxVScrollBar->GetScrollPos());
	InvalidateWindow();

	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CScrollWindow::OnVisualizationChange()
{
	return OnResize();
}

//---------------------------------------------------------------------------------------------------------------------
CStr 
CScrollWindow::GetDebugString() const		
{ 
	return "CScrollWindow"; 
}
//---------------------------------------------------------------------------------------------------------------------

} // namespace UILib


#include "stdafx.h"
#include "uilib/controls/basicscrollwindow.h"
#include "uilib/core/windowmanager.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CBasicScrollWindow::CBasicScrollWindow()
{
	m_pxInnerWindow = CWindow::Create();
	CWindow::AddChild(m_pxInnerWindow->GetWHDL());
}

//---------------------------------------------------------------------------------------------------------------------
CBasicScrollWindow::~CBasicScrollWindow()
{
}

//---------------------------------------------------------------------------------------------------------------------
CBasicScrollWindow*		
CBasicScrollWindow::Create()
{ 
	return new CBasicScrollWindow(); 
}

//---------------------------------------------------------------------------------------------------------------------
void			
CBasicScrollWindow::SetInteriorWindow(CWindow* p_pxNewInterior)
{
	CSize xOldSize = m_pxInnerWindow->GetSize();
	m_pxInnerWindow->Destroy();

	m_pxInnerWindow = p_pxNewInterior;
	m_pxInnerWindow->SetSize(xOldSize);
	CWindow::AddChild(m_pxInnerWindow->GetWHDL());
	OnResize();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CBasicScrollWindow::SetScrollPos(int iHPos, int iVPos)
{
	iHPos = min(iHPos, GetHScrollRange());
	iHPos = max(iHPos, 0);
	iVPos = min(iVPos, GetVScrollRange());
	iVPos = max(iVPos, 0);

	m_pxInnerWindow->SetPos(CPnt(-iHPos, -iVPos));
}

//---------------------------------------------------------------------------------------------------------------------
void 
CBasicScrollWindow::SetClientAreaSize(const CSize& p_rxSize)		
{ 
	m_pxInnerWindow->SetSize(p_rxSize); 
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicScrollWindow::AddChild(WHDL p_hWnd)
{
	return m_pxInnerWindow->AddChild(p_hWnd);
}

//---------------------------------------------------------------------------------------------------------------------
int 
CBasicScrollWindow::NumChildWindows() const
{ 
	return m_pxInnerWindow->NumChildWindows(); 
}

//---------------------------------------------------------------------------------------------------------------------
WHDL 
CBasicScrollWindow::GetChild(int p_iIndex) const
{ 
	return m_pxInnerWindow->GetChild(p_iIndex); 
}

//---------------------------------------------------------------------------------------------------------------------
void 
CBasicScrollWindow::RemoveChild(WHDL p_hWnd)
{
	m_pxInnerWindow->RemoveChild(p_hWnd);
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicScrollWindow::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgWindowChildResized)
	{
		if(p_rxMessage.GetWindow() == m_pxInnerWindow->GetWHDL())
		{
			m_pxInnerWindow->AssureMinSize(GetSize());
			OnResize();
			if(GetParent())
			{
				CWindowMgr::Get().SendMsg(CBasicScrollWindowChangedMsg(GetWHDL()), GetParent());
			}
		}
		return true;
	}
	else
	{
		return CWindow::HandleMsg(p_rxMessage);
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicScrollWindow::OnResize()
{
	// validiert die Positionen:
	SetScrollPos(GetHScrollPos(), GetVScrollPos());  

	return __super::OnResize();
}

//---------------------------------------------------------------------------------------------------------------------
CStr	
CBasicScrollWindow::GetDebugString() const		
{ 
	return "CBasicScrollWindow"; 
}
//---------------------------------------------------------------------------------------------------------------------

} // namespace UILib

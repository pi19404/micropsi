#include "stdafx.h"
#include "uilib/controls/basicspincontrol.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"
#include "uilib/core/virtualkeycodes.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
void 
CBasicSpinControl::CBasicSpinControlButton::Paint(const CPaintContext& p_rxCtx)
{
	CVisualization::FrameType eFt = CVisualization::FT_BtnUp;
	if(!GetDisabled())
	{
		if(GetMouseButtonPressed()  ||  GetButtonDown()) { eFt = CVisualization::FT_BtnDown; }
	}

	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());
	v->DrawSpinCtrlButton(p_rxCtx, CPnt(0, 0), m_eButtonType, eFt);
}


//---------------------------------------------------------------------------------------------------------------------
CBasicSpinControl::CBasicSpinControl()
{
	m_pxEditCtrl = CEditControl::Create();
	m_pxButton1 = CBasicSpinControlButton::Create();
	m_pxButton2 = CBasicSpinControlButton::Create();

	m_pxEditCtrl->SetFrame(false);
	AddChild(m_pxEditCtrl->GetWHDL());
	AddChild(m_pxButton1->GetWHDL());
	AddChild(m_pxButton2->GetWHDL());
	SetStyle(SC_UpDown);
	SetSize(CSize(100, 100));
}


//---------------------------------------------------------------------------------------------------------------------
CBasicSpinControl::~CBasicSpinControl()
{
}


//---------------------------------------------------------------------------------------------------------------------
CBasicSpinControl* 
CBasicSpinControl::Create()
{
	return new CBasicSpinControl();
}


//---------------------------------------------------------------------------------------------------------------------
void 
CBasicSpinControl::SetSize(const CSize& p_rxSize)
{
	CSize xSize = p_rxSize;
	if(m_eStyle == SC_UpDown)
	{
		xSize.cy = 2*m_xButtonSize.cy + m_xFrameSize.top + m_xFrameSize.bottom;
	}
	else
	{
		xSize.cy = m_xButtonSize.cy + m_xFrameSize.top + m_xFrameSize.bottom;
	}
	CWindow::SetSize(xSize);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CBasicSpinControl::SetStyle(SpinControlStyles p_eStyle)
{
	m_eStyle = p_eStyle;
	if(p_eStyle == SC_UpDown)
	{
		m_pxButton1->SetButtonType(CVisualization::BT_UpArrow);
		m_pxButton2->SetButtonType(CVisualization::BT_DownArrow);
	}
	else
	{
		m_pxButton2->SetButtonType(CVisualization::BT_LeftArrow);
		m_pxButton1->SetButtonType(CVisualization::BT_RightArrow);
	}

	OnVisualizationChange();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicSpinControl::OnActivate()
{
	CWindowMgr::Get().BringWindowToTop(m_pxEditCtrl->GetWHDL(), true);
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CBasicSpinControl::Paint(const CPaintContext& p_rxCtx)
{	
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());
	v->DrawFrame(p_rxCtx, GetRect(), CVisualization::FT_TextBox, GetDisabled());
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicSpinControl::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgButtonDown)
	{
		if(p_rxMessage.GetWindow() == m_pxButton1->GetWHDL())
		{
			Up();
			return true;
		}
		else if (p_rxMessage.GetWindow() == m_pxButton2->GetWHDL())
		{
			Down();
			return true;
		}
	}
	return __super::HandleMsg(p_rxMessage);
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicSpinControl::OnControlKey(int p_iKey)
{
	if(!GetDisabled())
	{
		switch(p_iKey) 
		{
			case VKey_Up:
				Up();
				break;

			case VKey_Down:		
				Down();
				break;

			default:
				return false;
		}
	}

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicSpinControl::OnResize()
{
	if(m_eStyle == SC_UpDown)
	{
	   	m_pxEditCtrl->SetSize( CSize(GetSize().cx - m_xButtonSize.cx - m_xFrameSize.left - m_xFrameSize.right, 
								     GetSize().cy - m_xFrameSize.top - m_xFrameSize.bottom));
		m_pxEditCtrl->SetPos(CPnt(m_xFrameSize.left, m_xFrameSize.top));
		m_pxButton1->SetPos(CPnt(m_xFrameSize.left + m_pxEditCtrl->GetSize().cx, m_xFrameSize.top));
		m_pxButton2->SetPos(CPnt(m_xFrameSize.left + m_pxEditCtrl->GetSize().cx, m_xFrameSize.top + m_xButtonSize.cy));
	}
	else
	{
	   	m_pxEditCtrl->SetSize( CSize(GetSize().cx - 2*m_xButtonSize.cx - m_xFrameSize.left - m_xFrameSize.right, 
								     GetSize().cy - m_xFrameSize.top - m_xFrameSize.bottom));
		m_pxEditCtrl->SetPos(CPnt(m_xFrameSize.left+m_xButtonSize.cx, m_xFrameSize.top));
		m_pxButton2->SetPos(CPnt(m_xFrameSize.left, m_xFrameSize.top));
		m_pxButton1->SetPos(CPnt(m_xFrameSize.left + m_xButtonSize.cx + m_pxEditCtrl->GetSize().cx, m_xFrameSize.top ));
	}

	return this->__super::OnResize();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicSpinControl::OnVisualizationChange()
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		CVisualization* v	= CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());
		if(m_eStyle == SC_UpDown)
		{
			m_xButtonSize		= v->GetMetrics()->m_xVSpinBtnSize;
		}
		else
		{
			m_xButtonSize		= v->GetMetrics()->m_xHSpinBtnSize;
		}

		m_pxButton1->SetSize(m_xButtonSize.cx, m_xButtonSize.cy);
		m_pxButton2->SetSize(m_xButtonSize.cx, m_xButtonSize.cy);
		m_xFrameSize = v->GetFrameSize(CVisualization::FT_TextBox);

		SetSize(GetSize());  // SetSize() berücksichtigt die neue Buttongröße
	}

	this->OnResize();

	return __super::OnVisualizationChange();
}

//---------------------------------------------------------------------------------------------------------------------
// benachrichtigt das Elternfenster, dass sich das SpinControl verändert hat
bool
CBasicSpinControl::OnChange()
{				
	CWindowMgr::Get().PostMsg(CSpinControlChangedMsg(GetWHDL()), GetParent());
	if(m_xOnChangeCallback)
	{
		m_xOnChangeCallback(this);
	}	
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
CStr 
CBasicSpinControl::GetDebugString() const		
{ 
	return "CBasicSpinControl"; 
}
//---------------------------------------------------------------------------------------------------------------------

} // namespace UILib



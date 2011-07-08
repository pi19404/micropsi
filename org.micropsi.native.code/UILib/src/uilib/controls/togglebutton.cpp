#include "stdafx.h"
#include "uilib/controls/togglebutton.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CToggleButton::CToggleButton()
{
	m_bState = false;
	m_bInGroup = false;
	m_bAllowUntoggle = true;
}

//---------------------------------------------------------------------------------------------------------------------
CToggleButton::~CToggleButton()
{
}


//---------------------------------------------------------------------------------------------------------------------
CToggleButton* 
CToggleButton::Create()
{
	return new CToggleButton();
}


//---------------------------------------------------------------------------------------------------------------------
/// ändert Status; true = gedrückt; false = nicht gedrückt
void 
CToggleButton::SetToggleButtonState(bool p_bState)		
{ 
	if(p_bState != m_bState)
	{
		m_bState = p_bState;
		UpdateBitmap();

		if(m_bInGroup  &&  m_bState)
		{
			if(GetParent())
			{
				CWindowMgr& wm = CWindowMgr::Get();
				CWindow* pParent = wm.GetWindow(GetParent());
				int i, iC = pParent->NumChildWindows();
				for(i=0; i<iC; ++i)
				{
					if(GetWHDL() != (WHDL) pParent->GetChild(i))
					{
						wm.SendMsg(CClearToggleButtonMsg(), pParent->GetChild(i));
					}
				}
			}
		}

		OnStateChange();
		InvalidateWindow();	
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CToggleButton::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgClearToggleButton)
	{
		if(m_bInGroup)
		{
			SetToggleButtonState(false);	
		}
		return true;
	}
	else
	{
		return __super::HandleMsg(p_rxMessage);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// ändert die Bitmap abhänig vom Status
void 
CToggleButton::UpdateBitmap()
{
	if(m_xNormalBmp)
	{
		const CBitmap* pxBmp;
		if(GetDisabled()  &&  m_xDisabledBmp.IsNotEmpty())
		{
			pxBmp = m_xDisabledBmp;
		}
		else if((GetToggleButtonState()  ||  GetButtonDown())  &&  m_xDownBmp.IsNotEmpty())
		{
			pxBmp = m_xDownBmp;
		}
		else if(m_bHovered  &&  m_xHoveredBmp.IsNotEmpty())
		{
			pxBmp = m_xHoveredBmp;
		}
		else
		{
			pxBmp = m_xNormalBmp;
		}

		m_pxLabel->SetBitmap(pxBmp);
		m_pxLabel->SetPos(m_xLabelPos);
	}
	else
	{
		if(GetButtonDown() || GetToggleButtonState())
		{
			m_pxLabel->SetPos(m_xLabelPos + m_xTextDisplacement);
		}
		else
		{
			m_pxLabel->SetPos(m_xLabelPos);
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CToggleButton::Paint(const CPaintContext& p_rxCtx)
{	
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());

	v->DrawBackground(p_rxCtx, GetRect(), CVisualization::BG_Button);

	if(m_bFrame)
	{
		CVisualization::FrameType eFt = CVisualization::FT_BtnUp;
		if(GetButtonDown() || GetToggleButtonState()) { eFt = CVisualization::FT_BtnDown; }
		else if(HasFocusOrChildHasFocus()  &&  !GetDisabled()) { eFt = CVisualization::FT_BtnUpActive; }

		v->DrawFrame(p_rxCtx, GetRect(), eFt, GetDisabled());
	}
}

//---------------------------------------------------------------------------------------------------------------------
/// Aktionen, die ausgeführt werden müssen, wenn der Button gedrückt wird
bool 
CToggleButton::OnClick()
{
	if(GetToggleButtonState()  &&  !GetAllowUntoggle())
	{
		return true;
	}
	SetToggleButtonState(!GetToggleButtonState());

	return __super::OnClick();
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CToggleButton::OnStateChange()			
{ 
	CWindowMgr::Get().PostMsg(CToggleButtonChangedMsg(GetWHDL()), GetParent());
	if(m_xOnStateChangeCallback)
	{
		m_xOnStateChangeCallback(this);
	}
	return true; 
}

//---------------------------------------------------------------------------------------------------------------------
CStr 
CToggleButton::GetDebugString() const		
{ 
	return CStr("CToggleButton Label = ") + GetText(); 
}
//---------------------------------------------------------------------------------------------------------------------

} //namespace UILib



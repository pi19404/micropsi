#include "stdafx.h"
#include "uilib/controls/basicbutton.h"
#include "uilib/core/windowmanager.h"


namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CBasicButton::CBasicButton()
{
	m_bMouseButtonPressed = false;
	m_bSpacePressed = false;
	m_bButtonDown = false;
	m_iBtnDownTimer = 0;
}


//---------------------------------------------------------------------------------------------------------------------
CBasicButton::~CBasicButton()
{
	if(m_iBtnDownTimer != 0)
	{
		UnsetTimer(m_iBtnDownTimer);
	}
}


//---------------------------------------------------------------------------------------------------------------------
CBasicButton* 
CBasicButton::Create()
{ 
	return new CBasicButton(); 
}


//---------------------------------------------------------------------------------------------------------------------
void 
CBasicButton::DeleteNow()
{
	delete this;
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicButton::OnClick()									
{ 
	CWindowMgr::Get().PostMsg(CButtonClickedMsg(GetWHDL()), GetParent());
	if(m_xOnClickCallback)
	{
		m_xOnClickCallback(this);
	}
	return true; 
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicButton::OnButtonDown()								
{ 
	CWindowMgr::Get().PostMsg(CButtonDownMsg(GetWHDL()), GetParent());
	if(m_xOnButtonDownCallback)
	{
		m_xOnButtonDownCallback(this);
	}
	return true; 
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicButton::HandleMsg(const CMessage& p_rxEvent)
{
	if(p_rxEvent == msgKeyDown)
	{
		if(p_rxEvent.GetKey() == ' ')
		{
			if(!GetDisabled())
			{
				m_bSpacePressed = true;
				SetButtonDown(true);
				OnButtonDown();
				if(m_iBtnDownTimer == 0)
				{
					m_iBtnDownTimer = SetTimer(m_iDelayBeforeAutoRepeat, true);
				}
				InvalidateWindow();
			}
		}
		return true;
	}
	else if(p_rxEvent == msgKeyUp)
	{
 		if(p_rxEvent.GetKey() == ' ')
		{
			if(m_bSpacePressed)
			{
				m_bSpacePressed = false;
				if(!m_bMouseButtonPressed)
				{
					if(m_iBtnDownTimer != 0)
					{
						UnsetTimer(m_iBtnDownTimer);
						m_iBtnDownTimer = 0;
					}
					InvalidateWindow();

					if(GetButtonDown())
					{
						SetButtonDown(false);
						OnClick();
					}
				}
			}
		}
		return true;		
	}
	else if(p_rxEvent == msgTimer)
	{
		if(p_rxEvent.GetTimerID() == m_iBtnDownTimer)
		{
			OnButtonDown();
			CWindowMgr::Get().ResetTimer(m_iBtnDownTimer, m_iAutoRepeatDelay, true);
			return true;
		}
		else
		{
			return __super::HandleMsg(p_rxEvent);
		}
	}
	else
	{
		return __super::HandleMsg(p_rxEvent);
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicButton::OnLButtonDown(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier)
{
	if(!GetDisabled())
	{
		CWindowMgr& wm = CWindowMgr::Get();

		m_bMouseButtonPressed = true;
		SetButtonDown(true);
		wm.SetCapture(this);
		
		OnButtonDown();
		if(m_iBtnDownTimer == 0)
		{
			m_iBtnDownTimer = wm.SetTimer(GetWHDL(), m_iDelayBeforeAutoRepeat, true);
		}

		InvalidateWindow();
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicButton::OnLButtonUp(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier)
{
	if(m_bMouseButtonPressed)
	{
		CWindowMgr& wm = CWindowMgr::Get();
	
		m_bMouseButtonPressed = false;
		wm.ReleaseCapture(this);
		if(!m_bSpacePressed)
		{
			if(m_iBtnDownTimer != 0)
			{
				wm.UnsetTimer(m_iBtnDownTimer);
				m_iBtnDownTimer = 0;
			}
			InvalidateWindow();

			if(GetButtonDown())
			{
				SetButtonDown(false);
				OnClick();
			}
		}
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicButton::OnMouseMove(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier)
{
	if(m_bMouseButtonPressed)
	{
		if(GetRect().Hit(p_rxRelativeMousePos))
		{
			if(!GetButtonDown())
			{
				SetButtonDown(true);
				InvalidateWindow();
			}
		}
		else
		{
			if(GetButtonDown())
			{
				SetButtonDown(false);
				InvalidateWindow();
			}
		}
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CBasicButton::OnDeactivate()
{
	CWindowMgr& wm = CWindowMgr::Get();

	m_bSpacePressed = false;
	
	if(m_bMouseButtonPressed)
	{
		m_bMouseButtonPressed = false;
		wm.ReleaseCapture(this);
	}

	if(m_iBtnDownTimer != 0)
	{
		wm.UnsetTimer(m_iBtnDownTimer);
		m_iBtnDownTimer = 0;
	}

	if(GetButtonDown())
	{
		SetButtonDown(false);
		InvalidateWindow();
	}

	return __super::OnDeactivate();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CBasicButton::SetButtonDown(bool p_bButtonDown)
{
	m_bButtonDown = p_bButtonDown;	
}

//---------------------------------------------------------------------------------------------------------------------
CStr 
CBasicButton::GetDebugString() const			
{ 
	return "CBasicButton"; 
}
//---------------------------------------------------------------------------------------------------------------------

} //namespace UILib


#include "Application/stdinc.h"
#include "UI/Windows/spectatormodepanel.h"

#include "Application/3dview2.h"
#include "Observers/observercontrollerswitcher.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CSpectatorModePanel::CSpectatorModePanel()
{
	SetColor(CColor(200, 200, 200, 150));
	SetSize(MB_NumButtons * 44, 44);


	for(int i=0; i<MB_NumButtons; ++i)
	{
		m_apxModeButtons[i] = CToggleButton::Create();
	}

	m_apxModeButtons[MB_Freelook]->SetBitmap("freecamera.png", "freecamera_down.png", "freecamera_hovered.png", "");
	m_apxModeButtons[MB_Freelook]->SetToolTipText("Free Camera Mode");
	m_apxModeButtons[MB_Walk]->SetBitmap("walkcamera.png", "walkcamera_down.png", "walkcamera_hovered.png", "");
	m_apxModeButtons[MB_Walk]->SetToolTipText("Walk Mode");
	m_apxModeButtons[MB_Helicopter]->SetBitmap("helicamera.png", "helicamera_down.png", "helicamera_hovered.png", "");
	m_apxModeButtons[MB_Helicopter]->SetToolTipText("Helicopter Mode");
	
	for(int i=0; i<MB_NumButtons; ++i)
	{
		m_apxModeButtons[i]->SetGrouped(true);
		m_apxModeButtons[i]->SetPos(i*44, 0);
		m_apxModeButtons[i]->SetSize(CSize(44, 44));
		AddChild(m_apxModeButtons[i]->GetWHDL());
	}
}


//---------------------------------------------------------------------------------------------------------------------
CSpectatorModePanel::~CSpectatorModePanel()
{
}


//---------------------------------------------------------------------------------------------------------------------
CSpectatorModePanel*	
CSpectatorModePanel::Create()
{
	return new CSpectatorModePanel();
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSpectatorModePanel::DeleteNow()
{
	delete this;
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CSpectatorModePanel::HandleMsg(const CMessage& p_krxMessage)
{
	if(p_krxMessage == msgButtonClicked)
	{

		for(int i=0; i<MB_NumButtons; ++i)
		{
			if(p_krxMessage.GetWindow() == m_apxModeButtons[i]->GetWHDL())
			{
				switch (i)
				{
				case MB_Freelook:		
					C3DView2::Get()->GetObserverControllerSwitcher()->SwitchObserver("freelook");
					break;
				case MB_Walk:
					C3DView2::Get()->GetObserverControllerSwitcher()->SwitchObserver("walk");
					break;
				case MB_Helicopter:
					C3DView2::Get()->GetObserverControllerSwitcher()->SwitchObserver("helicopter");
					break;
				default: 
					assert(false);
				}

				return true;
			}
		}
	}

	return __super::HandleMsg(p_krxMessage);
}

//---------------------------------------------------------------------------------------------------------------------
void	
CSpectatorModePanel::SetMode(std::string p_sNewMode)
{
	if(p_sNewMode == "freelook")
	{
		m_apxModeButtons[MB_Freelook]->SetToggleButtonState(true);
	}
	if(p_sNewMode == "walk")
	{
		m_apxModeButtons[MB_Walk]->SetToggleButtonState(true);
	}
	if(p_sNewMode == "helicopter")
	{
		m_apxModeButtons[MB_Helicopter]->SetToggleButtonState(true);
	}
}
//---------------------------------------------------------------------------------------------------------------------

#include "Application/stdinc.h"
#include "UI/Screens/spectatorscreen.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/panel.h"

#include "GameLib/UserInterface/Input/InputManager.h"
#include "GameLib/UserInterface/UIScreenStateMachine.h"

#include "UI/compass.h"
#include "UI/Windows/spectatormodepanel.h"
#include "UI/Windows/connectionstatuspanel.h"

#include "Application/3dview2.h"

#include "Observers/ObserverControllerSwitcher.h"

using namespace UILib;
using std::string;

//---------------------------------------------------------------------------------------------------------------------
CSpectatorScreen::CSpectatorScreen()
{
	CreateUIElements();
}
 
//---------------------------------------------------------------------------------------------------------------------
CSpectatorScreen::~CSpectatorScreen()
{
	delete m_pxCompass;
}

//---------------------------------------------------------------------------------------------------------------------
CUIScreen*
__cdecl CSpectatorScreen::Create()
{
	return new CSpectatorScreen;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CSpectatorScreen::Destroy() const
{
	delete this;
}

//---------------------------------------------------------------------------------------------------------------------
void
CSpectatorScreen::CreateUIElements()
{
    // Background-Panel
    {
        m_pxBackgroundPanel = CPanel::Create();
        m_pxBackgroundPanel->SetColor(CColor(0x30, 0x70, 0x90, 0));
        m_pxBackgroundPanel->SetPos(0, 0);
        m_pxBackgroundPanel->SetSize(CWindowMgr::Get().GetDesktop()->GetSize());
		m_pxBackgroundPanel->SetVisible(false);
		CWindowMgr::Get().AddTopLevelWindow(m_pxBackgroundPanel->GetWHDL());
	}	

	m_pxSpectatorModePanel = CSpectatorModePanel::Create();
	m_pxBackgroundPanel->AddChild(m_pxSpectatorModePanel->GetWHDL());

	m_pxConnectionStatusPanel = CConnectionStatusPanel::Create();
	m_pxBackgroundPanel->AddChild(m_pxConnectionStatusPanel->GetWHDL());

	m_pxCompass = new CCompass();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSpectatorScreen::Init() 
{
}
 
//---------------------------------------------------------------------------------------------------------------------
void 
CSpectatorScreen::Update()
{
	const CInputManager* pInputManager = C3DView2::Get()->GetInputManager();

	std::string sNewMode;
	if(pInputManager->IsFullfilled("spectator_freelook"))
	{
		sNewMode = "freelook";
	}
	else if(pInputManager->IsFullfilled("spectator_walk"))
	{
		sNewMode = "walk";
	}
	else if(pInputManager->IsFullfilled("spectator_helicopter"))
	{
		sNewMode = "helicopter";
	}
	else if(pInputManager->IsFullfilled("exit"))
	{
		m_pUIScreenStateMachine->SwitchToScreen("mainmenu");
	}

	if(sNewMode.size() > 0)
	{
		if(m_pxSpectatorModePanel)
		{
			m_pxSpectatorModePanel->SetMode(sNewMode);
		}
		C3DView2::Get()->GetObserverControllerSwitcher()->SwitchObserver(sNewMode);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSpectatorScreen::Render() 
{
	m_pxCompass->Render();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSpectatorScreen::OnEnter() 
{
	m_pxBackgroundPanel->SetVisible(true);

	string sStartMode = "freelook";
	if(m_pxSpectatorModePanel)
	{
		m_pxSpectatorModePanel->SetMode(sStartMode);
	}
	C3DView2::Get()->GetObserverControllerSwitcher()->SwitchObserver(sStartMode);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSpectatorScreen::OnLeave()
{
	m_pxBackgroundPanel->SetVisible(false);
}
//---------------------------------------------------------------------------------------------------------------------

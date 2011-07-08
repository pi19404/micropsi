#include "Application/stdinc.h"
#include "UI/Screens/MinimalGUIScreen.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/panel.h"

#include "GameLib/UserInterface/Input/InputManager.h"
#include "GameLib/UserInterface/UIScreenStateMachine.h"

#include "Application/3DEmotion.h"

using namespace UILib;
using std::string;

//---------------------------------------------------------------------------------------------------------------------
CMinimalGUIScreen::CMinimalGUIScreen()
{
	CreateUIElements();
}
 
//---------------------------------------------------------------------------------------------------------------------
CMinimalGUIScreen::~CMinimalGUIScreen()
{
}

//---------------------------------------------------------------------------------------------------------------------
CUIScreen*
__cdecl CMinimalGUIScreen::Create()
{
	return new CMinimalGUIScreen();
}
//---------------------------------------------------------------------------------------------------------------------
void 
CMinimalGUIScreen::Destroy() const
{
	delete this;
}

//---------------------------------------------------------------------------------------------------------------------
void
CMinimalGUIScreen::CreateUIElements()
{
    // Background-Panel
    {
        m_pxBackgroundPanel = CPanel::Create();
        m_pxBackgroundPanel->SetColor(CColor(0x00, 0xFF, 0x00, 0x00));
        m_pxBackgroundPanel->SetPos(0, 0);
        m_pxBackgroundPanel->SetSize(CWindowMgr::Get().GetDesktop()->GetSize());
		m_pxBackgroundPanel->SetVisible(false);
		CWindowMgr::Get().AddTopLevelWindow(m_pxBackgroundPanel->GetWHDL());
	}	
}

//---------------------------------------------------------------------------------------------------------------------
void 
CMinimalGUIScreen::Init() 
{
}
 
//---------------------------------------------------------------------------------------------------------------------
void 
CMinimalGUIScreen::Update()
{
#ifndef MICROPSI3DEMOTION_JNI_DLL
	CInputManager* pInputManager = C3DEmotion::Get()->GetInputManager();
	if(pInputManager->ConsumeEvent("switchscreen"))
	{
		m_pUIScreenStateMachine->SwitchToScreen("fullgui");
	}
#endif
}

//---------------------------------------------------------------------------------------------------------------------
void 
CMinimalGUIScreen::Render() 
{
}

//---------------------------------------------------------------------------------------------------------------------
void 
CMinimalGUIScreen::OnEnter() 
{
	m_pxBackgroundPanel->SetVisible(true);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CMinimalGUIScreen::OnLeave()
{
	m_pxBackgroundPanel->SetVisible(false);
}
//---------------------------------------------------------------------------------------------------------------------

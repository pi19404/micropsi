#include "Application/stdinc.h"
#include "UI/Screens/mainmenuscreen.h"

#include "baselib/filelocator.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/panel.h"

#include "GameLib/UserInterface/UIScreenStateMachine.h"

#include "UI/Windows/MainMenuDialog.h"
#include "UI/Windows/loadofflineworlddialog.h"
#include "UI/Windows/connect2worldserverdialog.h"

#include "Application/3dview2.h"
#include "Communication/CommunicationModule.h"

#include "Observers/ObserverControllerSwitcher.h"


using namespace UILib;
using std::string;

//---------------------------------------------------------------------------------------------------------------------
CMainMenuScreen::CMainMenuScreen()
{
	CreateUIElements();
}
 
//---------------------------------------------------------------------------------------------------------------------
CMainMenuScreen::~CMainMenuScreen()
{
}

//---------------------------------------------------------------------------------------------------------------------
CUIScreen*
__cdecl CMainMenuScreen::Create()
{
	return new CMainMenuScreen;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CMainMenuScreen::Destroy() const
{
	delete this;
}

//---------------------------------------------------------------------------------------------------------------------
void
CMainMenuScreen::CreateUIElements()
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

	m_pxMainMenuDialog = CMainMenuDialog::Create();
	m_pxMainMenuDialog->SetMainMenuScreen(this);
	m_pxBackgroundPanel->AddChild(m_pxMainMenuDialog);
	m_pxMainMenuDialog->CenterOnParentWindow();

	m_pxLoadOfflineWorldDialog = CLoadOfflineWorldDialog::Create();
	CWindowMgr::Get().AddTopLevelWindow(m_pxLoadOfflineWorldDialog->GetWHDL());
	m_pxLoadOfflineWorldDialog->CenterOnParentWindow();
	m_pxLoadOfflineWorldDialog->SetVisible(false);

	m_pxConnect2WorldServerDialog = CConnect2WorldServerDialog::Create();
	CWindowMgr::Get().AddTopLevelWindow(m_pxConnect2WorldServerDialog->GetWHDL());
	m_pxConnect2WorldServerDialog->CenterOnParentWindow();
	m_pxConnect2WorldServerDialog->SetVisible(false);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CMainMenuScreen::Init() 
{
}
 
//---------------------------------------------------------------------------------------------------------------------
void 
CMainMenuScreen::Update()
{
}
    
//---------------------------------------------------------------------------------------------------------------------
void 
CMainMenuScreen::Render() 
{
}

//---------------------------------------------------------------------------------------------------------------------
void 
CMainMenuScreen::OnEnter() 
{
	m_pxBackgroundPanel->SetVisible(true);
	C3DView2::Get()->GetObserverControllerSwitcher()->SwitchObserver("passive");
}

//---------------------------------------------------------------------------------------------------------------------
void 
CMainMenuScreen::OnLeave()
{
	m_pxBackgroundPanel->SetVisible(false);
}

//---------------------------------------------------------------------------------------------------------------------
bool
CMainMenuScreen::LoadWorld(std::string p_sWorldFile, std::string p_sVisualizationFile, CWorld::WrapState p_eWrapState)
{
	string sWorldFile = C3DView2::Get()->GetFileLocator()->GetPath(string("offlineworlds>") + p_sWorldFile);
	string sVizFile = C3DView2::Get()->GetFileLocator()->GetPath(string("visualization>") + p_sVisualizationFile);

	bool bSuccess = C3DView2::Get()->GetWorld()->LoadFromXML(sWorldFile.c_str(), p_sVisualizationFile != "" ? sVizFile.c_str() : 0, p_eWrapState);
	if(bSuccess)
	{
		C3DView2::Get()->GetWorld()->ResetObserver(C3DView2::Get()->GetCurrentObserver());
	}

	return bSuccess;
} 

//---------------------------------------------------------------------------------------------------------------------
void	
CMainMenuScreen::OnMainMenuButton(int p_iButton)
{
	switch(p_iButton)
	{
		case CMainMenuDialog::Buttons::BT_ConnectToWorld:
			m_pxConnect2WorldServerDialog->SetVisible(true);
			CWindowMgr::Get().BringWindowToTop(m_pxConnect2WorldServerDialog->GetWHDL());
			break;

		case CMainMenuDialog::Buttons::BT_LoadOfflineWorld:
			m_pxLoadOfflineWorldDialog->SetVisible(true);
			CWindowMgr::Get().BringWindowToTop(m_pxLoadOfflineWorldDialog->GetWHDL());
			break;

		case CMainMenuDialog::Buttons::BT_SpectatorMode:
			m_pUIScreenStateMachine->SwitchToScreen("spectator");
			break;

		case CMainMenuDialog::Buttons::BT_AgentMode:
			m_pUIScreenStateMachine->SwitchToScreen("agent");
			break;

		case CMainMenuDialog::Buttons::BT_EditorMode:
			m_pUIScreenStateMachine->SwitchToScreen("leveleditor");
			break;

		default:
			assert(false);
	}
}
//---------------------------------------------------------------------------------------------------------------------



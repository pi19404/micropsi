#include "Application/stdinc.h"
#include "UI/Windows/mainmenudialog.h"

#include "uilib/core/windowmanager.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CMainMenuDialog::CMainMenuDialog()
{
	m_pxMainMenuScreen = 0;

	SetSize(300, 280);
	SetColor(CColor(200, 200, 200, 100));

	m_apxButtons[BT_ConnectToWorld] = CButton::Create();
	m_apxButtons[BT_ConnectToWorld]->SetPos(10, 10);
	m_apxButtons[BT_ConnectToWorld]->SetSize(280, 40);
	m_apxButtons[BT_ConnectToWorld]->SetText("Connect to World Server");
	AddChild(m_apxButtons[BT_ConnectToWorld]);

	m_apxButtons[BT_LoadOfflineWorld] = CButton::Create();
	m_apxButtons[BT_LoadOfflineWorld]->SetPos(10, 60);
	m_apxButtons[BT_LoadOfflineWorld]->SetSize(280, 40);
	m_apxButtons[BT_LoadOfflineWorld]->SetText("Load Offline World");
	AddChild(m_apxButtons[BT_LoadOfflineWorld]);

	m_apxButtons[BT_SpectatorMode] = CButton::Create();
	m_apxButtons[BT_SpectatorMode]->SetPos(10, 130);
	m_apxButtons[BT_SpectatorMode]->SetSize(280, 40);
	m_apxButtons[BT_SpectatorMode]->SetText("Spectator Mode");
	AddChild(m_apxButtons[BT_SpectatorMode]);

	m_apxButtons[BT_AgentMode] = CButton::Create();
	m_apxButtons[BT_AgentMode]->SetPos(10, 180);
	m_apxButtons[BT_AgentMode]->SetSize(280, 40);
	m_apxButtons[BT_AgentMode]->SetText("Agent Mode");
	AddChild(m_apxButtons[BT_AgentMode]);

	m_apxButtons[BT_EditorMode] = CButton::Create();
	m_apxButtons[BT_EditorMode]->SetPos(10, 230);
	m_apxButtons[BT_EditorMode]->SetSize(280, 40);
	m_apxButtons[BT_EditorMode]->SetText("World Editor Mode");
	AddChild(m_apxButtons[BT_EditorMode]);
}

//---------------------------------------------------------------------------------------------------------------------
CMainMenuDialog::~CMainMenuDialog()
{
}

//---------------------------------------------------------------------------------------------------------------------
CMainMenuDialog*		
CMainMenuDialog::Create()
{
	return new CMainMenuDialog();
}

//---------------------------------------------------------------------------------------------------------------------
void
CMainMenuDialog::DeleteNow()
{
	delete this;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CMainMenuDialog::SetMainMenuScreen(CMainMenuScreen* p_pxMMS)
{
	m_pxMainMenuScreen = p_pxMMS;
}

//---------------------------------------------------------------------------------------------------------------------
void
CMainMenuDialog::SetButtonDisabled(Buttons p_eButton, bool p_bDisabled)
{
	m_apxButtons[p_eButton]->SetDisabled(p_bDisabled);
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CMainMenuDialog::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgButtonClicked)
	{
		assert(m_pxMainMenuScreen);
		for(int i=0; i<Buttons::BT_NumButtons; ++i)
		{
			if(m_apxButtons[i]->GetWHDL() == p_rxMessage.GetWindow())
			{
				m_pxMainMenuScreen->OnMainMenuButton(i);
				return true;
			}
		}
		assert(false);
	}

	return __super::HandleMsg(p_rxMessage);
}
//---------------------------------------------------------------------------------------------------------------------




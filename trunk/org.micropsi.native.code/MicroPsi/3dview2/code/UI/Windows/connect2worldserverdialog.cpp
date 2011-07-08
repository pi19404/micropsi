#include "Application/stdinc.h"
#include "UI/Windows/connect2worldserverdialog.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/label.h"
#include "uilib/controls/groupbox.h"

#include "GameLib/Utilities/ExtendedConfigFile.h"

#include "UI/Screens/mainmenuscreen.h"
#include "UI/Windows/visualizationpicker.h"

#include "Application/3dview2.h"
#include "Communication/CommunicationModule.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CConnect2WorldServerDialog::CConnect2WorldServerDialog()
{
	m_pxMainMenuScreen = 0;

	SetSize(430, 350);
	SetColor(CColor(200, 200, 200, 100));

	// connect, cancel

	m_pxConnectButton = CButton::Create();
	m_pxConnectButton->SetSize(60, 30);
	m_pxConnectButton->SetPos(290, 310);
	m_pxConnectButton->SetText("Connect");
	AddChild(m_pxConnectButton);

	m_pxCancelButton = CButton::Create();
	m_pxCancelButton->SetSize(60, 30);
	m_pxCancelButton->SetPos(360, 310);
	m_pxCancelButton->SetText("Cancel");
	AddChild(m_pxCancelButton);

	// server - settings

	CGroupBox* pxGroup = CGroupBox::Create();
	pxGroup->SetText("Server Settings");
	pxGroup->SetSize(200, 280);
	pxGroup->SetPos(10, 10);
	AddChild(pxGroup);

	m_pxConnectionMode = CComboBox::Create();
	m_pxConnectionMode->SetPos(5, 10);
	m_pxConnectionMode->SetSize(180, 20);
	m_pxConnectionMode->AddItem("Webservice (XML over HTTP)");
	m_pxConnectionMode->AddItem("Prorietary binary (depricated)");
	pxGroup->AddChild(m_pxConnectionMode);


	m_pxServerLabel = CLabel::Create();
	m_pxServerLabel->SetText("Server Name or IP:");
	m_pxServerLabel->SetTextAlign(CLabel::TA_Left);
	m_pxServerLabel->SetSize(50, 20);
	m_pxServerLabel->SetPos(5, 35);
	pxGroup->AddChild(m_pxServerLabel);

	m_pxServer = CEditControl::Create();
	m_pxServer->SetSize(180, 20);
	m_pxServer->SetPos(5, 55);
	pxGroup->AddChild(m_pxServer);

	m_pxWorldPortLabel = CLabel::Create();
	m_pxWorldPortLabel->SetText("World Service Port:");
	m_pxWorldPortLabel->SetTextAlign(CLabel::TA_Left);
	m_pxWorldPortLabel->SetSize(100, 20);
	m_pxWorldPortLabel->SetPos(5, 80);
	pxGroup->AddChild(m_pxWorldPortLabel);

	m_pxWorldPort = CEditControl::Create();
	m_pxWorldPort->SetSize(60, 20);
	m_pxWorldPort->SetPos(5, 100);
	pxGroup->AddChild(m_pxWorldPort);


	m_pxWorldURLLabel = CLabel::Create();
	m_pxWorldURLLabel->SetText("Console Service URL:");
	m_pxWorldURLLabel->SetTextAlign(CLabel::TA_Left);
	m_pxWorldURLLabel->SetSize(180, 20);
	m_pxWorldURLLabel->SetPos(5, 125);
	pxGroup->AddChild(m_pxWorldURLLabel);

	m_pxWorldURL = CEditControl::Create();
	m_pxWorldURL->SetSize(180, 20);
	m_pxWorldURL->SetPos(5, 145);
	pxGroup->AddChild(m_pxWorldURL);

	m_pxAgentServiceURLLabel = CLabel::Create();
	m_pxAgentServiceURLLabel->SetText("Agent Service URL:");
	m_pxAgentServiceURLLabel->SetTextAlign(CLabel::TA_Left);
	m_pxAgentServiceURLLabel->SetSize(180, 20);
	m_pxAgentServiceURLLabel->SetPos(5, 170);
	pxGroup->AddChild(m_pxAgentServiceURLLabel);

	m_pxAgentServiceURL = CEditControl::Create();
	m_pxAgentServiceURL->SetSize(180, 20);
	m_pxAgentServiceURL->SetPos(5, 190);
	pxGroup->AddChild(m_pxAgentServiceURL);


	m_pxDefaultSettings = CButton::Create();
	m_pxDefaultSettings->SetText("Restore Default Settings");
	m_pxDefaultSettings->SetSize(180, 25);
	m_pxDefaultSettings->SetPos(5, 220);
	pxGroup->AddChild(m_pxDefaultSettings);

	// visualization settings

	m_pxVisualizationPicker = CVisualizationPicker::Create();
	m_pxVisualizationPicker->SetPos(230, 10);
	AddChild(m_pxVisualizationPicker);

	SetDefaults();
	SetVisible(true);
}

//---------------------------------------------------------------------------------------------------------------------
CConnect2WorldServerDialog::~CConnect2WorldServerDialog()
{
}

//---------------------------------------------------------------------------------------------------------------------
CConnect2WorldServerDialog*		
CConnect2WorldServerDialog::Create()
{
	return new CConnect2WorldServerDialog();
}

//---------------------------------------------------------------------------------------------------------------------
void
CConnect2WorldServerDialog::DeleteNow()
{
	delete this;
}

//---------------------------------------------------------------------------------------------------------------------
void					
CConnect2WorldServerDialog::SetDefaults()
{
	const CConfigFile& rxConfig = C3DView2::Get()->GetConfiguration();

	m_pxServer->SetText(rxConfig.GetValueString("networking/worldserver").c_str());

	if(m_pxConnectionMode->GetSelectedItem() == 0)
	{
		m_pxWorldPort->SetText(rxConfig.GetValueString("networking/httpport").c_str());
		m_pxWorldURL->SetText(rxConfig.GetValueString("networking/consoleserviceurl").c_str());	
		m_pxAgentServiceURL->SetText(rxConfig.GetValueString("networking/avatarserviceurl").c_str());	
		m_pxWorldURLLabel->SetVisible(true);
		m_pxWorldURL->SetVisible(true);
		m_pxAgentServiceURLLabel->SetVisible(true);
		m_pxAgentServiceURL->SetVisible(true);
	}
	else
	{
		m_pxWorldPort->SetText(rxConfig.GetValueString("networking/tcpworldserverport").c_str());
		m_pxWorldURLLabel->SetVisible(false);
		m_pxWorldURL->SetVisible(false);
		m_pxAgentServiceURLLabel->SetVisible(false);
		m_pxAgentServiceURL->SetVisible(false);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void	
CConnect2WorldServerDialog::SetMainMenuScreen(CMainMenuScreen* p_pxMMS)
{
	m_pxMainMenuScreen = p_pxMMS;
}
//---------------------------------------------------------------------------------------------------------------------
bool 
CConnect2WorldServerDialog::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgButtonClicked)
	{
		if(m_pxDefaultSettings->GetWHDL() == p_rxMessage.GetWindow())
		{
			SetDefaults();
			return true;
		}
		else if(m_pxConnectButton->GetWHDL() == p_rxMessage.GetWindow())
		{
			if(m_pxConnectionMode->GetSelectedItem() == 0)
			{
				C3DView2::Get()->GetCommunicationModule()->OpenHTTPConnection(m_pxServer->GetText().c_str(), m_pxWorldPort->GetText().ToInt(), 
					m_pxWorldURL->GetText().c_str(), m_pxAgentServiceURL->GetText().c_str());
			}
			else
			{
				C3DView2::Get()->GetCommunicationModule()->OpenTCPConnection(m_pxServer->GetText().c_str(), m_pxWorldPort->GetText().ToInt());
			}

			SetVisible(false);
			return true;
		}
		else if(m_pxCancelButton->GetWHDL() == p_rxMessage.GetWindow())
		{
			SetVisible(false);
			return true;
		}
		else
		{
			assert(false);
		}
	}
	else if(p_rxMessage == msgComboBoxChanged)
	{
		if(m_pxConnectionMode->GetWHDL() == p_rxMessage.GetWindow())
		{
			SetDefaults();
		}
		else
		{
			assert(false);
		}
	}

	return __super::HandleMsg(p_rxMessage);
}
//---------------------------------------------------------------------------------------------------------------------
void			
CConnect2WorldServerDialog::SetVisible(bool p_bVisible)
{
	if(p_bVisible)
	{
		CWindowMgr::Get().SetModal(this);
		m_pxVisualizationPicker->UpdateList();
	}
	else
	{
		CWindowMgr::Get().ReleaseModal(this);
	}
	__super::SetVisible(p_bVisible);
}
//---------------------------------------------------------------------------------------------------------------------
#include "Application/stdinc.h"
#include "UI/Windows/connectionstatuspanel.h"

#include "uilib/core/windowmanager.h"
#include "Application/3dview2.h"
#include "Communication/CommunicationModule.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CConnectionStatusPanel::CConnectionStatusPanel()
{
	SetColor(CColor(200, 200, 200, 150));
	SetSize(MB_NumLabels * 43, 44);
	SetColor(CColor(0, 0, 0, 128));

	for(int i=0; i<MB_NumLabels; ++i)
	{
		m_apxLabels[i] = CLabel::Create();
	}

	SetStatus(false, false, false);

	for(int i=0; i<MB_NumLabels; ++i)
	{
		m_apxLabels[i]->SetPos(i*42 + 2, 2);
		m_apxLabels[i]->SetSize(CSize(40, 40));
		AddChild(m_apxLabels[i]->GetWHDL());
	}

	m_iUpdateTimer = SetTimer(500, true);
}


//---------------------------------------------------------------------------------------------------------------------
CConnectionStatusPanel::~CConnectionStatusPanel()
{
}


//---------------------------------------------------------------------------------------------------------------------
CConnectionStatusPanel*	
CConnectionStatusPanel::Create()
{
	return new CConnectionStatusPanel();
}


//---------------------------------------------------------------------------------------------------------------------
void 
CConnectionStatusPanel::DeleteNow()
{
	delete this;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CConnectionStatusPanel::SetStatus(bool p_bWorldConnected, bool p_bAgentConnected, bool p_bServerConnected)
{
	if(p_bWorldConnected)
	{
		m_apxLabels[MB_World]->SetBitmap("world_connected.png");
		m_apxLabels[MB_World]->SetToolTipText("Connected to\nmicroPSI world server");
	}
	else
	{
		m_apxLabels[MB_World]->SetBitmap("world_notconnected.png");
		m_apxLabels[MB_World]->SetToolTipText("Not connected to\nmicroPSI world server");
	}

	if(p_bAgentConnected)
	{
		m_apxLabels[MB_Agent]->SetBitmap("agent_connected.png");
		m_apxLabels[MB_Agent]->SetToolTipText("Connected to\nmicroPSI agent service");
	}
	else
	{
		m_apxLabels[MB_Agent]->SetBitmap("agent_notconnected.png");
		m_apxLabels[MB_Agent]->SetToolTipText("Not connected to\nmicroPSI agent service");
	}

	if(p_bServerConnected)
	{
		m_apxLabels[MB_Server]->SetBitmap("server_connected.png");
		m_apxLabels[MB_Server]->SetToolTipText("Connected to\nmicroPSI server");
	}
	else
	{
		m_apxLabels[MB_Server]->SetBitmap("server_notconnected.png");
		m_apxLabels[MB_Server]->SetToolTipText("Not connected to\nmicroPSI server");
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CConnectionStatusPanel::OnVisualizationChange()
{
	if(GetParent())
	{
		CSize xSize = UILib::CWindowMgr::Get().GetWindow(GetParent())->GetSize();
		SetPos(xSize.cx - GetSize().cx, 0);
	}
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CConnectionStatusPanel::OnTimer(int p_iID)
{
	if(p_iID == m_iUpdateTimer)
	{
		CCommunicationModule* pxComModule = C3DView2::Get()->GetCommunicationModule();
		SetStatus(	pxComModule->IsConnectedToWorld(), 
					pxComModule->IsConnectedToAgentService(), 
					pxComModule->GetCurrentConnectionMethod() != CCommunicationModule::CM_OFFLINE);
		return true;
	}
	else
	{
		return __super::OnTimer(p_iID);
	}
}
//---------------------------------------------------------------------------------------------------------------------

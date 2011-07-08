#include "Application/stdinc.h"
#include "UI/Screens/agentcontrolscreen.h"

#include "baselib/geometry/CVector.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/panel.h"

#include "GameLib/UserInterface/Input/InputManager.h"
#include "GameLib/UserInterface/UIScreenStateMachine.h"

#include "UI/compass.h"
#include "UI/Windows/connectionstatuspanel.h"

#include "Application/3dview2.h"

#include "Observers/ObserverControllerSwitcher.h"
#include "Observers/ObjectFocusObserverController.h"

#include "Communication/CommunicationModule.h"
#include "Communication/RemoteAgentController.h"

#include "World/world.h"
#include "World/ObjectManager.h"
#include "World/WorldObject.h"

using namespace UILib;
using std::string;

//---------------------------------------------------------------------------------------------------------------------
CAgentControlScreen::CAgentControlScreen()
{
	CreateUIElements();
	m_vAgentOrientation = CVec3(1.0f, 0.0f, 0.0f);
}
 
//---------------------------------------------------------------------------------------------------------------------
CAgentControlScreen::~CAgentControlScreen()
{
	delete m_pxCompass;
}

//---------------------------------------------------------------------------------------------------------------------
CUIScreen*
__cdecl CAgentControlScreen::Create()
{
	return new CAgentControlScreen;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CAgentControlScreen::Destroy() const
{
	delete this;
}

//---------------------------------------------------------------------------------------------------------------------
void
CAgentControlScreen::CreateUIElements()
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

	m_pxConnectionStatusPanel = CConnectionStatusPanel::Create();
	m_pxBackgroundPanel->AddChild(m_pxConnectionStatusPanel->GetWHDL());

	m_pxCompass = new CCompass();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CAgentControlScreen::Init() 
{
}
 
//---------------------------------------------------------------------------------------------------------------------
void 
CAgentControlScreen::Update()
{
	const CInputManager* pInputManager = C3DView2::Get()->GetInputManager();
	CRemoteAgentController* pxRemoteAgent = C3DView2::Get()->GetCommunicationModule()->GetRemoteAgentController();
	if(!pxRemoteAgent)
	{
		return;
	}

	std::string sNewMode;
	if(pInputManager->IsFullfilled("exit"))
	{
		m_pUIScreenStateMachine->SwitchToScreen("mainmenu");
	}

	
	// check if we are already looking at the right object
	CObjectFocusObserverController* pxObjFocusObserver = (CObjectFocusObserverController*) 
		C3DView2::Get()->GetObserverControllerSwitcher()->GetCurrentObserverController();

	if(!pxObjFocusObserver->IsWatchingValidObject()  ||  m_sAgentName != pxRemoteAgent->GetAgentName())
	{
		m_sAgentName = pxRemoteAgent->GetAgentName();
		DebugPrint("Trying to locate object with name %s", m_sAgentName.c_str());
		CWorldObject* pxObj = C3DView2::Get()->GetWorld()->GetObjectManager()->FindObj(m_sAgentName);
		if(pxObj)
		{
			DebugPrint("setting focus to object %I64d", pxObj->GetID());
			pxObjFocusObserver->SetFocusObject(pxObj->GetID());
		}
		else
		{
			// must set focus to -1 because if agent name has changed we need dont know the new ID yet we must remember to try the search again
			pxObjFocusObserver->SetFocusObject(-1);
		}
	}

	// check actions

	float fSpeed = 2.5f;
	float fRotation = 0.02f;

	if(pInputManager->IsFullfilled("walk_straferight"))
	{
//		pxRemoteAgent->Move(fSpeed, 0.0f);
		CMat3S xRot;
		xRot.SetRotationZ(fRotation);
		m_vAgentOrientation = m_vAgentOrientation * xRot;
		pxRemoteAgent->Move(m_vAgentOrientation.x() * 0.01f, m_vAgentOrientation.y() * 0.01f);
	}
	if(pInputManager->IsFullfilled("walk_strafeleft"))
	{
//		pxRemoteAgent->Move(-fSpeed, 0.0f);
		CMat3S xRot;
		xRot.SetRotationZ(-fRotation);
		m_vAgentOrientation = m_vAgentOrientation * xRot;
		pxRemoteAgent->Move(m_vAgentOrientation.x() * 0.01f, m_vAgentOrientation.y() * 0.01f);
	}
	if(pInputManager->IsFullfilled("walk_forward"))
	{
		pxRemoteAgent->Move(m_vAgentOrientation.x() * fSpeed, m_vAgentOrientation.y() * fSpeed);
	}
	//if(pInputManager->IsFullfilled("walk_backward"))
	//{
	//	pxRemoteAgent->Move(m_vAgentOrientation.x() * -fSpeed, m_vAgentOrientation.y() * -fSpeed);
	//}
	if(pInputManager->IsFullfilled("walk_turnleft"))
	{
	}
	if(pInputManager->IsFullfilled("walk_turnright"))
	{
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CAgentControlScreen::Render() 
{
	m_pxCompass->Render();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CAgentControlScreen::OnEnter() 
{
	m_pxBackgroundPanel->SetVisible(true);

	string sStartMode = "objectfocus";
	C3DView2::Get()->GetObserverControllerSwitcher()->SwitchObserver(sStartMode);

	if(!C3DView2::Get()->GetCommunicationModule()->IsConnectedToAgentService())
	{
		C3DView2::Get()->GetCommunicationModule()->CreateRemoteAgent();
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CAgentControlScreen::OnLeave()
{
	m_pxBackgroundPanel->SetVisible(false);
}
//---------------------------------------------------------------------------------------------------------------------

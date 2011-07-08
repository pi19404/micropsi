#include "Application/stdinc.h"
#include "UI/Screens/leveleditorscreen.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/panel.h"

#include "GameLib/UserInterface/Input/InputManager.h"
#include "GameLib/UserInterface/UIScreenStateMachine.h"

#include "UI/compass.h"
#include "UI/Windows/editorpanel.h"
#include "UI/Windows/connectionstatuspanel.h"

#include "World/world.h"
#include "World/leveleditor.h"

#include "Application/3dview2.h"

#include "Observers/ObserverControllerSwitcher.h"


using namespace UILib;


//-----------------------------------------------------------------------------------------------------------------------------------------
CLevelEditorScreen::CClickPanel::CClickPanel(ClickCallback p_fpClickCallback, void* p_pUserData)
:   m_fpClickCallback(p_fpClickCallback),
    m_pUserData(p_pUserData)
{
}

//-----------------------------------------------------------------------------------------------------------------------------------------
CLevelEditorScreen::CClickPanel::~CClickPanel()
{
}

//---------------------------------------------------------------------------------------------------------------------
CUIScreen*
__cdecl CLevelEditorScreen::Create()
{
	return new CLevelEditorScreen;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CLevelEditorScreen::Destroy() const
{
	delete this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
bool 
CLevelEditorScreen::CClickPanel::HandleMsg(const UILib::CMessage& p_krxEvent)
{
    if(	p_krxEvent == UILib::msgMouseLeftButtonDown  ||
   		p_krxEvent == UILib::msgMouseLeftButtonDoubleClick)
	{
		CPnt xPos = p_krxEvent.GetPos();
        m_fpClickCallback(m_pUserData, xPos, true);
        return true;
	}
    if(p_krxEvent == UILib::msgMouseRightButtonDown)
	{
		CPnt xPos = p_krxEvent.GetPos();
        m_fpClickCallback(m_pUserData, xPos, false);
        return true;
	}
	else if(p_krxEvent == UILib::msgWindowActivation)
	{
//		CLevelEditor::Get().SetCameraControl(true);
	}
	else if(p_krxEvent == UILib::msgWindowDeactivation)
	{
//		CLevelEditor::Get().SetCameraControl(false);
	}
    return __super::HandleMsg(p_krxEvent);
}

//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CLevelEditorScreen::CClickPanel::DeleteNow()
{
	delete this;
}

//-----------------------------------------------------------------------------------------------------------------------------------------
void 
__cdecl CLevelEditorScreen::ClickCallback(void* pUserData, const CPnt& xPos, bool bLeft)
{
    ((CLevelEditorScreen*)pUserData)->WorldClick(xPos, bLeft);
}

//---------------------------------------------------------------------------------------------------------------------
CLevelEditorScreen::CLevelEditorScreen()
{
	CreateUIElements();
}
 
//---------------------------------------------------------------------------------------------------------------------
CLevelEditorScreen::~CLevelEditorScreen()
{
	delete m_pxCompass;
}

//---------------------------------------------------------------------------------------------------------------------
void
CLevelEditorScreen::CreateUIElements()
{
    // Background-Panel
    {
        m_pxBackgroundPanel = new CClickPanel(ClickCallback, this);
        m_pxBackgroundPanel->SetColor(CColor(0x30, 0x70, 0x90, 0));
        m_pxBackgroundPanel->SetPos(0, 0);
        m_pxBackgroundPanel->SetSize(CWindowMgr::Get().GetDesktop()->GetSize());
		m_pxBackgroundPanel->SetVisible(false);
		CWindowMgr::Get().AddTopLevelWindow(m_pxBackgroundPanel->GetWHDL());
	}	

	m_pxCompass = new CCompass();

	m_pxEditorPanel			= CEditorPanel::Create();
	m_pxBackgroundPanel->AddChild(m_pxEditorPanel->GetWHDL());

	m_pxConnectionStatusPanel = CConnectionStatusPanel::Create();
	m_pxBackgroundPanel->AddChild(m_pxConnectionStatusPanel->GetWHDL());
}

//---------------------------------------------------------------------------------------------------------------------
void 
CLevelEditorScreen::Init() 
{
}
 
//---------------------------------------------------------------------------------------------------------------------
void
CLevelEditorScreen::Update()
{
	m_pxEditorPanel->Tick();
	const CInputManager* pInputManager = C3DView2::Get()->GetInputManager();
	if(pInputManager->IsFullfilled("exit"))
	{
		m_pUIScreenStateMachine->SwitchToScreen("mainmenu");
	}
	else if(pInputManager->IsFullfilled("deleteobject"))
	{
		C3DView2::Get()->GetWorld()->GetEditor()->DeleteSelectedObjects();
	}
}
    
//---------------------------------------------------------------------------------------------------------------------
void 
CLevelEditorScreen::Render() 
{
	m_pxCompass->Render();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CLevelEditorScreen::OnEnter() 
{
	m_pxEditorPanel->Update();
	m_pxBackgroundPanel->SetVisible(true);
	C3DView2::Get()->GetWorld()->GetEditor()->SetRenderingEnabled(true);
	C3DView2::Get()->GetObserverControllerSwitcher()->SwitchObserver("freelook");
}

//---------------------------------------------------------------------------------------------------------------------
void 
CLevelEditorScreen::OnLeave()
{
	m_pxBackgroundPanel->SetVisible(false);
	C3DView2::Get()->GetWorld()->GetEditor()->SetRenderingEnabled(false);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CLevelEditorScreen::WorldClick(const CPnt& xPos, bool bLeft)
{
	if(bLeft)
	{
	    CSize xSize = CWindowMgr::Get().GetDesktop()->GetSize();

		float fXPos = +xPos.x / (float)xSize.cx * 2.0f - 1.0f;
		float fYPos = -xPos.y / (float)xSize.cy * 2.0f + 1.0f;

		C3DView2::Get()->GetWorld()->GetEditor()->OnClick(fXPos, fYPos);
	}
	else
	{
		C3DView2::Get()->GetWorld()->GetEditor()->SetClickMode(CLevelEditor::ClickMode::CM_Select);
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------


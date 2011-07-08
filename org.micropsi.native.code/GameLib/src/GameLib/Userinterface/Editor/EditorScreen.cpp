#include "stdafx.h"
#include "GameLib/UserInterface/Editor/EditorScreen.h"

#include "e42/Camera.h"

#include "GameLib/GameLibApplication.h"
#include "GameLib/UserInterface/Input/InputManager.h"
#include "GameLib/UserInterface/UIScreenStateMachine.h"
#include "GameLib/UserInterface/Editor/EditorToolBar.h"
#include "GameLib/UserInterface/Editor/ObjectListWindow.h"
#include "GameLib/UserInterface/Editor/OptionsWindow.h"
#include "GameLib/UserInterface/Editor/ObjectPropertiesWindow.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CEditorScreen::CClickPanel::CClickPanel(ClickCallback p_fpClickCallback, void* p_pUserData)
:   m_fpClickCallback(p_fpClickCallback),
    m_pUserData(p_pUserData)
{
}
//---------------------------------------------------------------------------------------------------------------------
CEditorScreen::CClickPanel::~CClickPanel()
{
}
//---------------------------------------------------------------------------------------------------------------------
bool 
CEditorScreen::CClickPanel::HandleMsg(const UILib::CMessage& p_krxEvent)
{
    if(p_krxEvent == UILib::msgMouseLeftButtonDown  ||
	   p_krxEvent == UILib::msgMouseRightButtonDown)
	{
		CPnt xPos = p_krxEvent.GetPos();
        m_fpClickCallback(m_pUserData, xPos, p_krxEvent == UILib::msgMouseLeftButtonDown);
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
//---------------------------------------------------------------------------------------------------------------------
void 
CEditorScreen::CClickPanel::DeleteNow()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
void 
__cdecl CEditorScreen::ClickCallback(void* pUserData, const CPnt& xPos, bool p_bLeft)
{
    ((CEditorScreen*)pUserData)->WorldClick(xPos, p_bLeft);
}
//---------------------------------------------------------------------------------------------------------------------
CEditorScreen::CEditorScreen()
{
    // hand shake
//    m_pLevelEditor = &CLevelEditor::Get();

	CreateConfig();
	m_xConfiguration.Load("editorconfig.xml");

    CreateUIElements();
	CreateKeyMapping();

    m_pxWorldClickPanel->SetVisible(false);
	m_pxOptionsWindow->SetPos(	m_xConfiguration.GetValueInt("windows/settings/x"), 
								m_xConfiguration.GetValueInt("windows/settings/y"));
	m_pxOptionsWindow->SetVisible(m_xConfiguration.GetValueBool("windows/settings/visible"));

	m_pxObjectListWindow->SetPos(	m_xConfiguration.GetValueInt("windows/objectlist/x"), 
									m_xConfiguration.GetValueInt("windows/objectlist/y"));
	m_pxObjectListWindow->SetVisible(m_xConfiguration.GetValueBool("windows/objectlist/visible"));

	m_pxObjectPropertiesWindow->SetPos(	m_xConfiguration.GetValueInt("windows/objectprops/x"), 
										m_xConfiguration.GetValueInt("windows/objectprops/y"));
	m_pxObjectPropertiesWindow->SetVisible(m_xConfiguration.GetValueBool("windows/objectprops/visible"));

}
//---------------------------------------------------------------------------------------------------------------------
CEditorScreen::~CEditorScreen()
{
	m_xConfiguration.SetValueInt("windows/settings/x", m_pxOptionsWindow->GetPos().x);
	m_xConfiguration.SetValueInt("windows/settings/y", m_pxOptionsWindow->GetPos().y);
	m_xConfiguration.SetValueBool("windows/settings/visible", m_pxOptionsWindow->GetVisible());

	m_xConfiguration.SetValueInt("windows/objectlist/x", m_pxObjectListWindow->GetPos().x);
	m_xConfiguration.SetValueInt("windows/objectlist/y", m_pxObjectListWindow->GetPos().y);
	m_xConfiguration.SetValueBool("windows/objectlist/visible", m_pxObjectListWindow->GetVisible());

	m_xConfiguration.SetValueInt("windows/objectprops/x", m_pxObjectPropertiesWindow->GetPos().x);
	m_xConfiguration.SetValueInt("windows/objectprops/y", m_pxObjectPropertiesWindow->GetPos().y);
	m_xConfiguration.SetValueBool("windows/objectprops/visible", m_pxObjectPropertiesWindow->GetVisible());

	m_xConfiguration.Save("editorconfig.xml");
}
//---------------------------------------------------------------------------------------------------------------------
CUIScreen*
__cdecl CEditorScreen::Create()
{
	return new CEditorScreen();
}
//---------------------------------------------------------------------------------------------------------------------
void 
CEditorScreen::Destroy() const
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
void
CEditorScreen::CreateUIElements()
{
    m_pxWorldClickPanel = new CClickPanel(ClickCallback, this);
	m_pxWorldClickPanel->SetColor(CColor(0x00, 0x0, 0x0, 0x00));
    m_pxWorldClickPanel->SetPos(0, 0);
    m_pxWorldClickPanel->SetSize(CWindowMgr::Get().GetDesktop()->GetSize());
    CWindowMgr::Get().AddTopLevelWindow(m_pxWorldClickPanel->GetWHDL());

	m_pxEditorToolBar		= CEditorToolBar::Create(this);
	m_pxWorldClickPanel->AddChild(m_pxEditorToolBar->GetWHDL());

	m_pxObjectListWindow	= CObjectsListWindow::Create(this);
	m_pxWorldClickPanel->AddChild(m_pxObjectListWindow->GetWHDL());

	m_pxOptionsWindow	= COptionsWindow::Create(this);
	m_pxWorldClickPanel->AddChild(m_pxOptionsWindow->GetWHDL());

	m_pxObjectPropertiesWindow  = CObjectPropertiesWindow::Create(this);
	m_pxWorldClickPanel->AddChild(m_pxObjectPropertiesWindow->GetWHDL());
}
//---------------------------------------------------------------------------------------------------------------------
void
CEditorScreen::CreateConfig()
{
	m_xConfiguration.AddParameterInt("windows/settings/x", "", 590, 0, 1000);
	m_xConfiguration.AddParameterInt("windows/settings/y", "", 30, 0, 1000);
	m_xConfiguration.AddParameterBool("windows/settings/visible", "", false);

	m_xConfiguration.AddParameterInt("windows/objectlist/x", "", 0, 0, 1000);
	m_xConfiguration.AddParameterInt("windows/objectlist/y", "", 30, 0, 1000);
	m_xConfiguration.AddParameterBool("windows/objectlist/visible", "", false);

	m_xConfiguration.AddParameterInt("windows/objectprops/x", "", 0, 0, 1000);
	m_xConfiguration.AddParameterInt("windows/objectprops/y", "", 30, 0, 1000);
	m_xConfiguration.AddParameterBool("windows/objectprops/visible", "", false);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CEditorScreen::WorldClick(const CPnt& xPos, bool p_bLeft)
{
	if(p_bLeft)
	{
	    CSize xSize = CWindowMgr::Get().GetDesktop()->GetSize();

	    float fXPos = +xPos.x / (float)xSize.cx * 2.0f - 1.0f;
		float fYPos = -xPos.y / (float)xSize.cy * 2.0f + 1.0f;

//		m_pLevelEditor->OnClick(fXPos, fYPos);
	}
	else
	{
//		m_pLevelEditor->SetClickMode(CLevelEditor::CM_Select);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CEditorScreen::Update()
{
	MoveCamera();
	m_pxEditorToolBar->Tick();
	m_pxObjectListWindow->Tick();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CEditorScreen::OnEnter()
{
//    CGameUI::Get().SetLevelEditorEnable(true);
    ShowCursor(true);
    m_pxWorldClickPanel->SetVisible(true);
	CWindowMgr::Get().BringWindowToTop(m_pxWorldClickPanel->GetWHDL());
//    m_pLevelEditor->Enable();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CEditorScreen::OnLeave()
{
	CWindowMgr::Get().BringWindowToTop(CWindowMgr::Get().GetDesktop()->GetWHDL(), true); 
 //   m_pLevelEditor->Disable();
    m_pxWorldClickPanel->SetVisible(false);
    ShowCursor(false);
//    CGameUI::Get().SetLevelEditorEnable(false);
}
//---------------------------------------------------------------------------------------------------------------------
void
CEditorScreen::SetControlMode(ControlMode p_eMode)
{
	CInputManager* pxInputManager = CGameLibApplication::Get().GetInputManager();
	//pxInputManager->RemoveMapping("le_zoomin");
	//pxInputManager->RemoveMapping("le_zoomout");
	//pxInputManager->RemoveMapping("le_panupdown");
	//pxInputManager->RemoveMapping("le_panleftright");
	//pxInputManager->RemoveMapping("le_rotateleftright");
	//pxInputManager->RemoveMapping("le_rotateupdown");

	if(p_eMode == CM_Maya)
	{
		pxInputManager->Map("keyboard:alt.held  &&  mouse:leftbutton.held   &&  mouse:x.value!=0",	"le_mouseturnleftright");
		pxInputManager->Map("keyboard:alt.held  &&  mouse:leftbutton.held   &&  mouse:x.value!=0",	"le_mouseturnleftright");
	}
	else if(p_eMode == CM_Max)
	{

	}
	else
	{
		assert(false);
	}
}
//---------------------------------------------------------------------------------------------------------------------
void 
CEditorScreen::CreateKeyMapping()
{
	CInputManager* pxInputManager = CGameLibApplication::Get().GetInputManager();
	pxInputManager->Map("keyboard:control.held",	"le_fast");

	pxInputManager->Map("keyboard:shift.notheld  &&  keyboard:left.held",	"le_strafeleft");
	pxInputManager->Map("keyboard:shift.notheld  &&  keyboard:right.held",	"le_straferight");
	pxInputManager->Map("keyboard:shift.notheld  &&  keyboard:up.held",		"le_forward");
	pxInputManager->Map("keyboard:shift.notheld  &&  keyboard:down.held",	"le_backward");
	pxInputManager->Map("keyboard:shift.notheld  &&  keyboard:space.held",	"le_up");
	pxInputManager->Map("keyboard:shift.notheld  &&  keyboard:return.held",	"le_down");

	SetControlMode(CM_Maya);
}
//---------------------------------------------------------------------------------------------------------------------
void
CEditorScreen::MoveCamera()
{
    CInputManager*	pxInputManager = CGameLibApplication::Get().GetInputManager();
//    CCamera*        pCamera = (CCamera*)CWorld::Get().GetCamera();
//    float           fDeltaTime = (float)CGameUI::Get().GetDurationOfLastFrame();

    bool bSpeedModifier = pxInputManager->IsFullfilled("le_fast");
/*
    if (pxInputManager->IsFullfilled("le_mouseturnleftright")  ||
		pxInputManager->IsFullfilled("le_mouseturnupdown"))
    {
        // drehen

		float fDX = pxInputManager->GetAxisValue("le_mouseturnleftright") / 100.0f;
		float fDY = pxInputManager->GetAxisValue("le_mouseturnupdown") / 100.0f;

        CVec3 vCamOrientation = pCamera->GetOrientation();
        vCamOrientation = CMat3S::CalcRotationMatrix(CAxisAngle(pCamera->GetUpVec(), fDX)) * vCamOrientation;
		vCamOrientation = CMat3S::CalcRotationMatrix(CAxisAngle(pCamera->GetRightVec(), fDY)) * vCamOrientation;

		pCamera->SetOrientation(vCamOrientation);
	}
*/
/*
    CVec3 vCamPos = pCamera->GetPos();
    float fPanSpeed = m_fCamPanSpeed;
    if (bSpeedModifier) fPanSpeed *= 6;

	if(pxInputManager->IsFullfilled("le_strafeleft"))
	{
        vCamPos -= pCamera->GetRightVec() * fDeltaTime * fPanSpeed;
	}
	if(pxInputManager->IsFullfilled("le_straferight"))
	{
        vCamPos += pCamera->GetRightVec() * fDeltaTime * fPanSpeed;
	}
	if(pxInputManager->IsFullfilled("le_forward"))
	{
        vCamPos += pCamera->GetOrientation() * fDeltaTime * fPanSpeed;
	}
	if(pxInputManager->IsFullfilled("le_backward"))
	{
        vCamPos -= pCamera->GetOrientation() * fDeltaTime * fPanSpeed;
	}
	if(pxInputManager->IsFullfilled("le_up"))
	{
        vCamPos += pCamera->GetUpVec() * fDeltaTime * fPanSpeed;
	}
	if(pxInputManager->IsFullfilled("le_down"))
	{
        vCamPos -= pCamera->GetUpVec() * fDeltaTime * fPanSpeed;
	}

    pCamera->SetPos(vCamPos);
*/
}
//-----------------------------------------------------------------------------------------------------------------------

#include "TestScreen.h"

#include "uilib/controls/panel.h"
#include "uilib/controls/label.h"

#include "GameLib/UserInterface/UIScreenStateMachine.h"
#include "GameLib/UserInterface/Input/InputManager.h"

using namespace UILib;
using namespace std;

//---------------------------------------------------------------------------------------------------------------------
CTestScreen::CTestScreen()
{
    CreateUIElements();

    m_pxBackgroundPanel->SetVisible(false);
}
//---------------------------------------------------------------------------------------------------------------------
CTestScreen::~CTestScreen()
{
}
//---------------------------------------------------------------------------------------------------------------------
CUIScreen*
CTestScreen::Create()
{
	return new CTestScreen();
}
//---------------------------------------------------------------------------------------------------------------------
void 
CTestScreen::Destroy() const
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
void
CTestScreen::CreateUIElements()
{
    m_pxBackgroundPanel = CPanel::Create();
    m_pxBackgroundPanel->SetColor(CColor(0xFF, 0xFF, 0xFF, 0));
    m_pxBackgroundPanel->SetPos(0, 0);
    m_pxBackgroundPanel->SetSize(CWindowMgr::Get().GetDesktop()->GetSize());
    CWindowMgr::Get().GetDesktop()->AddChild(m_pxBackgroundPanel->GetWHDL());

	CLabel* pTextLabel =  CLabel::Create();
	pTextLabel->SetText("b - create box\n"
						"f - toggle follow mode for buggy");
	pTextLabel->SetTextAlign(CLabel::TA_Left);
	m_pxBackgroundPanel->AddChild(pTextLabel->GetWHDL());
	

	m_pCarSpeedLabel = CLabel::Create();
	m_pCarSpeedLabel->SetSize(100, 20);
	m_pCarSpeedLabel->SetPos(0, 40);
	m_pCarSpeedLabel->SetText("---");
	m_pxBackgroundPanel->AddChild(m_pCarSpeedLabel->GetWHDL());
}
//---------------------------------------------------------------------------------------------------------------------
void 
CTestScreen::Update()
{
}
//---------------------------------------------------------------------------------------------------------------------
void 
CTestScreen::OnEnter()
{
	m_pxBackgroundPanel->SetVisible(true);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CTestScreen::OnLeave()
{
    m_pxBackgroundPanel->SetVisible(false);
}
//---------------------------------------------------------------------------------------------------------------------

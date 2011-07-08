#include "Application/stdinc.h"
#include "UI/Screens/FullGUIScreen.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/panel.h"

#include "GameLib/UserInterface/Input/InputManager.h"
#include "GameLib/UserInterface/UIScreenStateMachine.h"

#include "Application/3DEmotion.h"
#include "Application/Face.h"
#include "UI/Controls/SliderPanel.h"

using namespace UILib;
using std::string;
using std::map;

//---------------------------------------------------------------------------------------------------------------------
CFullGUIScreen::CFullGUIScreen()
{
	CreateUIElements();
}
 
//---------------------------------------------------------------------------------------------------------------------
CFullGUIScreen::~CFullGUIScreen()
{
}

//---------------------------------------------------------------------------------------------------------------------
CUIScreen*
__cdecl CFullGUIScreen::Create()
{
	return new CFullGUIScreen();
}
//---------------------------------------------------------------------------------------------------------------------
void 
CFullGUIScreen::Destroy() const
{
	delete this;
}

//---------------------------------------------------------------------------------------------------------------------
void
CFullGUIScreen::CreateUIElements()
{
    // Background-Panel
    {
        m_pxBackgroundPanel = CPanel::Create();
        m_pxBackgroundPanel->SetColor(CColor(0xFF, 0x00, 0x00, 0x00));
        m_pxBackgroundPanel->SetPos(0, 0);
        m_pxBackgroundPanel->SetSize(CWindowMgr::Get().GetDesktop()->GetSize());
		m_pxBackgroundPanel->SetVisible(false);
		CWindowMgr::Get().AddTopLevelWindow(m_pxBackgroundPanel->GetWHDL());

		m_pxSliderPanel = CSliderPanel::Create();
		m_pxBackgroundPanel->AddChild(m_pxSliderPanel->GetWHDL());
	}	
}

//---------------------------------------------------------------------------------------------------------------------
void 
CFullGUIScreen::Init() 
{
}
 
//---------------------------------------------------------------------------------------------------------------------
void 
CFullGUIScreen::Update()
{
	CInputManager* pInputManager = C3DEmotion::Get()->GetInputManager();
	if(pInputManager->ConsumeEvent("switchscreen"))
	{
		m_pUIScreenStateMachine->SwitchToScreen("minimalgui");
	}


	// set all bones
	CFace* pxFace = C3DEmotion::Get()->GetFace();
	const map<string, CFace::CBone>& axBones = pxFace->GetBones();
	map<string, CFace::CBone>::const_iterator i;
	if(!axBones.empty())
	{
		for(i = axBones.begin(); i != axBones.end(); i++)
		{
			pxFace->SetBonePos(i->second.m_sName, m_pxSliderPanel->GetCurrentValue(i->second.m_sName));
		}
	}
}
//---------------------------------------------------------------------------------------------------------------------
void 
CFullGUIScreen::Render() 
{
}

//---------------------------------------------------------------------------------------------------------------------
void 
CFullGUIScreen::OnEnter() 
{
	m_pxBackgroundPanel->SetVisible(true);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CFullGUIScreen::OnLeave()
{
	m_pxBackgroundPanel->SetVisible(false);
}
//---------------------------------------------------------------------------------------------------------------------

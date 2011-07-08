#include "stdafx.h"
#include "GameLib/GameLibApplication.h"

#include "baselib/filelocator.h"
#include "GameLib/UserInterface/Input/InputManager.h"
#include "GameLib/UserInterface/UIScreenStateMachine.h"
#include "GameLib/Utilities/SystemUtils.h"


//-----------------------------------------------------------------------------------------------------------------------
CGameLibApplication::CGameLibApplication(HINSTANCE hInstance)
:   CE42Application(hInstance),
	m_dTime(0),
	m_iNumUIScreenDesktops(1)
{
}
//-----------------------------------------------------------------------------------------------------------------------
CGameLibApplication::~CGameLibApplication()
{
}
//-----------------------------------------------------------------------------------------------------------------------
bool 
CGameLibApplication::OnWindowMessage(UINT msg, WPARAM wParam, LPARAM lParam)
{
	if (m_pxUIScreenStateMachine)
	{
		return m_pxUIScreenStateMachine->HandleWindowMessage(msg, wParam, lParam);
	}

	return true;
}
//-----------------------------------------------------------------------------------------------------------------------
void    
CGameLibApplication::CreateScene() 
{
   	m_pxInputManager = new CInputManager();
	m_pxInputManager->SetKeyboard(GetKeyboardDevice());
	m_pxInputManager->SetMouse(GetMouseDevice());

	for (int i = 0; i < GetNumGamepads(); i++)
	{
		m_pxInputManager->AddGamePad(GetGamepadDevice(i));
	}

    UILib::CBitmapFactory::Get().AddSearchPath(GetFileLocator()->GetPath("interface>").c_str());
	m_pxUIScreenStateMachine = new CUIScreenStateMachine(this, m_pxInputManager, GetWindowWidth(), GetWindowHeight(), m_iNumUIScreenDesktops);
}
//-----------------------------------------------------------------------------------------------------------------------
void    
CGameLibApplication::Terminate() 
{
	delete m_pxInputManager;
	delete m_pxUIScreenStateMachine;
}

//-----------------------------------------------------------------------------------------------------------------------
void
CGameLibApplication::Input() 
{
	m_dTimeSinceLastUpdate = Utils::GetDeltaSeconds(m_dTime);

#ifdef _DEBUG
    // wegen Breakpoints
	if (m_dTimeSinceLastUpdate > 5.0f) m_dTimeSinceLastUpdate = 0.2;
#else
    // um Stalls zu verhindern -> bremst das Gameplay ab
    if (m_dTimeSinceLastUpdate > 0.5f) m_dTimeSinceLastUpdate = 0.5f;
#endif

	m_pxInputManager->UpdateFromDevice((float) m_dTimeSinceLastUpdate);
}
//-----------------------------------------------------------------------------------------------------------------------
void
CGameLibApplication::Update()
{
}
//-----------------------------------------------------------------------------------------------------------------------
void
CGameLibApplication::Output()
{
}
//-----------------------------------------------------------------------------------------------------------------------
void    
CGameLibApplication::OnIdle() 
{
	Input();
	Update();
	Output();
}
//---------------------------------------------------------------------------------------------------------------------

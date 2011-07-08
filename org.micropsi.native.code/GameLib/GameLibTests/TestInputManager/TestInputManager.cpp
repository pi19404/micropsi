#include "TestInputManager.h"

#include <dinput.h>
#include <d3d9.h>

#ifdef _DEBUG
    #include <malloc.h>     // _heapchk()
#endif

#include "resources/resource.h"
#include "GameLib/UserInterface/Input/InputManager.h"
#include "GameLib/UserInterface/UIScreenStateMachine.h"
#include "GameLib/Userinterface/Input/GamepadState.h"
#include "TestScreen.h"

using std::string;
//-----------------------------------------------------------------------------------------------------------------------
CE42Application* 
CreateE42Application(HINSTANCE hInstance)
{
    return new CTestInputManager(hInstance);
}
//-----------------------------------------------------------------------------------------------------------------------
void
DestroyE42Application(CE42Application* pApplication)
{
    delete pApplication;
}
//-----------------------------------------------------------------------------------------------------------------------
CTestInputManager::CTestInputManager(HINSTANCE hInstance)
:   CGameLibApplication(hInstance)
{

    m_iWindowWidth = 1024;
    m_iWindowHeight = 768;

    m_dwMaxMultiSampleQuality = 4;
    
    HICON hIcon = LoadIcon(m_hInstance, MAKEINTRESOURCE(IDI_RIP));
    m_WindowClass.hIcon = hIcon;
    m_WindowClass.hIconSm = NULL;
    m_WindowClass.style = CS_CLASSDC | CS_DBLCLKS;
    m_WindowClass.lpszMenuName = NULL;

    m_WindowClass.lpszClassName = "InputManager Test";

    m_uiWindowStyle = WS_OVERLAPPED | WS_CAPTION | WS_SYSMENU;

    m_uiMouseCoopFlags =    DISCL_NONEXCLUSIVE | DISCL_FOREGROUND;
    m_uiKeyboardCoopFlags = DISCL_NONEXCLUSIVE | DISCL_FOREGROUND; 
    m_uiGamepadCoopFlags =  DISCL_EXCLUSIVE | DISCL_FOREGROUND; 

    ShowCursor(false);

    if (m_bFullScreen)
    {
        m_bTripleBuffering = true;
        m_bVSyncEnable = true;

        m_uiKeyboardCoopFlags |= DISCL_NOWINKEY;
    }
}
//-----------------------------------------------------------------------------------------------------------------------
CTestInputManager::~CTestInputManager()
{
    assert((_heapchk() == _HEAPOK) && "ShutDownError: Heapcheck failed");
}
//-----------------------------------------------------------------------------------------------------------------------
void 
CTestInputManager::CreateScene()
{
	__super::CreateScene();

	CTestScreen* pTestScreen = (CTestScreen*) CTestScreen::Create();
	GetUIScreenStateMachine()->AddScreen("test", pTestScreen);
	GetUIScreenStateMachine()->SwitchToScreen("test");

	CreateInputManagerMapping(pTestScreen);
	ShowCursor(true);
}
//-----------------------------------------------------------------------------------------------------------------------
void	
CTestInputManager::Input()
{
	__super::Input();
}
//-----------------------------------------------------------------------------------------------------------------------
void	
CTestInputManager::Update()
{
	__super::Update();
	GetUIScreenStateMachine()->Tick();
}
//-----------------------------------------------------------------------------------------------------------------------
void	
CTestInputManager::Output()
{
	IDirect3DDevice9* pD3DDevice = GetDevice();

	if (SUCCEEDED(GetDevice()->BeginScene()))
	{	
		pD3DDevice->Clear(0, NULL, D3DCLEAR_TARGET | D3DCLEAR_ZBUFFER, 0x204080, 1.0f, 0);
		GetUIScreenStateMachine()->Render();

		pD3DDevice->EndScene();
	}
	GetDevice()->Present(0, 0, 0, 0);
}
//-----------------------------------------------------------------------------------------------------------------------
void 
CTestInputManager::Terminate()
{
	__super::Terminate();
}
//-----------------------------------------------------------------------------------------------------------------------
void 
CTestInputManager::CreateInputManagerMapping(CTestScreen* pTestScreen)
{
	// der Testscreen hat eine Map-Funktion, die der des Inputmanagers verblüffend ähnelt...
	
	pTestScreen->Map("gamepad0:anykey",				"Gamepad 0 Any Button");
	pTestScreen->Map("gamepad0:button0.down",		"Gamepad 0 Button0 Down");
	pTestScreen->Map("gamepad0:button0.up",			"Gamepad 0 Button0 Up");
	pTestScreen->Map("gamepad0:button0.held",		"Gamepad 0 Button0 Held");
	pTestScreen->Map("gamepad0:axis0.value != 0.0f", "Gamepad 0 Axis 0");
	pTestScreen->Map("gamepad0:axis1.value != 0.0f", "Gamepad 0 Axis 1");
	//pTestScreen->Map("gamepad0:axis2.value != 0.0f", "Gamepad 0 Axis 2");
	//pTestScreen->Map("gamepad0:axis3.value != 0.0f", "Gamepad 0 Axis 3");
	//pTestScreen->Map("gamepad0:axis4.value != 0.0f", "Gamepad 0 Axis 4");
	pTestScreen->Map("gamepad0:axis5.value != 0.0f", "Gamepad 0 Axis 5");
//	pTestScreen->Map("gamepad0:axis6.value != 0.0f", "Gamepad 0 Axis 6");
	//pTestScreen->Map("gamepad0:axis7.value != 0.0f", "Gamepad 0 Axis 7");

	//pTestScreen->Map("gamepad1:axis0.value != 0.0f", "Gamepad 1 Axis 0");
	//pTestScreen->Map("gamepad1:axis1.value != 0.0f", "Gamepad 1 Axis 1");
	//pTestScreen->Map("gamepad1:axis5.value != 0.0f", "Gamepad 1 Axis 5");

	pTestScreen->AddSeparator();

	pTestScreen->Map("mouse:anykey",				"Mouse Any Button");
	pTestScreen->Map("mouse:button0.down",			"Mouse Button0 Down");
	pTestScreen->Map("mouse:button0.up",			"Mouse Button0 Up");
	pTestScreen->Map("mouse:button0.held",			"Mouse Button0 Held");
	pTestScreen->Map("mouse:axis0.value != 0.0f",	"Mouse Axis 0");
	pTestScreen->Map("mouse:axis1.value != 0.0f",	"Mouse Axis 1");

	pTestScreen->AddSeparator();

	pTestScreen->Map("keyboard:anykey",				"Keyboard Any Key");
	pTestScreen->Map("keyboard:space.down",			"Keyboard Space Down");
	pTestScreen->Map("keyboard:space.up",			"Keyboard Space Up");
	pTestScreen->Map("keyboard:space.held",			"Keyboard Space Held");
	pTestScreen->Map("keyboard:space.repeatevent",	"Keyboard Space KeyRepeat");
	pTestScreen->Map("keyboard:scrolllock",			"Keyboard ScrollLock State");
	pTestScreen->Map("keyboard:win.held",			"Keyboard Windows Key Held");
	pTestScreen->Map("keyboard:control.held",		"Keyboard Control Key Held");
	pTestScreen->Map("keyboard:alt.held",			"Keyboard Alt Key Held");
	pTestScreen->Map("keyboard:shift.held",			"Keyboard Shift Key Held");
	pTestScreen->Map("keyboard:lshift.held",		"Keyboard Left Shift Key Held");
	pTestScreen->Map("keyboard:rshift.held",		"Keyboard Right Shift Key Held");
	pTestScreen->Map("keyboard:print.down",			"Keyboard Print Screen Key Held");

	pTestScreen->AddSeparator();

	pTestScreen->Map("keyboard:up.held  &&  keyboard:shift.held",		"Walk Slowly");
	pTestScreen->Map("keyboard:w.repeatevent  &&  keyboard:shift.held",	"Shift W Repeat Event");
	pTestScreen->Map("keyboard:control.held",							"Fire");
	pTestScreen->Map("mouse:button0.held",								"Fire");
	pTestScreen->Map("keyboard:up.held",								"Accelerate", -0.5f);
	pTestScreen->Map("keyboard:up.held  &&  keyboard:control.held",		"Accelerate", -1.0f);
	pTestScreen->Map("gamepad0:axis1.value < 0.0f",						"Accelerate");

	//GetInputManager()->SetAxisMode(0, CGamepadState::AX_Y, CGamepadState::AM_UNSIGNED);
	//GetInputManager()->SetAxisMode(0, CGamepadState::AX_ZROT, CGamepadState::AM_UNSIGNED);
	//GetInputManager()->SetAxisMode(1, CGamepadState::AX_Y, CGamepadState::AM_UNSIGNED);
	//GetInputManager()->SetAxisMode(1, CGamepadState::AX_ZROT, CGamepadState::AM_UNSIGNED);
}
//-----------------------------------------------------------------------------------------------------------------------

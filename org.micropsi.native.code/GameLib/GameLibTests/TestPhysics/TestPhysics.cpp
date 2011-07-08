#include "TestPhysics.h"

#include <dinput.h>
#include <d3d9.h>

#ifdef _DEBUG
    #include <malloc.h>     // _heapchk()
#endif

#include "resources/resource.h"
#include "baselib/FileLocator.h"

#include "e42/core/ModelFactory.h"
#include "e42/core/XFileLoader.h"
#include "e42/core/EffectFactory.h"
#include "e42/core/Model.h"
#include "e42/utils/GfxDebugMarker.h"

#include "uilib/controls/label.h"

#include "GameLib/UserInterface/UIScreenStateMachine.h"
#include "GameLib/UserInterface/Input/InputManager.h"
#include "TestScreen.h"

#include "ode/ode.h"
#include "GameLib/Physics/DynamicsSimulation.h"
#include "GameLib/Physics/DOPlane.h"
#include "GameLib/Physics/DOBox.h"
#include "GameLib/Physics/DOSimpleBuggy.h"

using std::string;
//-----------------------------------------------------------------------------------------------------------------------
CE42Application* 
CreateE42Application(HINSTANCE hInstance)
{
    return new CTestPhysics(hInstance);
}
//-----------------------------------------------------------------------------------------------------------------------
void
DestroyE42Application(CE42Application* pApplication)
{
    delete pApplication;
}
//-----------------------------------------------------------------------------------------------------------------------
CTestPhysics::CTestPhysics(HINSTANCE hInstance)
:   CGameLibApplication(hInstance)
{

    m_iWindowWidth = 800;
    m_iWindowHeight = 600;

    m_dwMaxMultiSampleQuality = 4;
    
    HICON hIcon = LoadIcon(m_hInstance, MAKEINTRESOURCE(IDI_RIP));
    m_WindowClass.hIcon = hIcon;
    m_WindowClass.hIconSm = NULL;
    m_WindowClass.style = CS_CLASSDC | CS_DBLCLKS;
    m_WindowClass.lpszMenuName = NULL;

    m_WindowClass.lpszClassName = "Physics Test";

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
CTestPhysics::~CTestPhysics()
{
    assert((_heapchk() == _HEAPOK) && "ShutDownError: Heapcheck failed");
}
//---------------------------------------------------------------------------------------------------------------------
void
CTestPhysics::SetFilesystemMapping()
{
	GetFileLocator()->SetAlias("xfl-texture", "../textures");
	GetFileLocator()->SetAlias("xfl-shader", "../shaders");
    GetFileLocator()->SetAlias("model", "../models");
}
//-----------------------------------------------------------------------------------------------------------------------
void
CTestPhysics::MoveCamera(float fDeltaTime)
{
	bool bSpeedModifier = GetInputManager()->IsFullfilled("fast");

	if (GetInputManager()->IsFullfilled("mouseturnleftright")  ||
		GetInputManager()->IsFullfilled("mouseturnupdown"))
	{
		// drehen
		float fDX = GetInputManager()->GetAxisValue("mouseturnleftright") / 100.0f;
		float fDY = GetInputManager()->GetAxisValue("mouseturnupdown") / 100.0f;

		m_xCamera.RotateWorldY(fDX);
		m_xCamera.RotateUp(-fDY);
	}

	float fPanSpeed = 5.0f;
	if (bSpeedModifier) fPanSpeed *= 6;
	float fMove = fDeltaTime * fPanSpeed;

	if (GetInputManager()->IsFullfilled("strafeleft"))	m_xCamera.MoveRight(-fMove);
	if (GetInputManager()->IsFullfilled("straferight"))	m_xCamera.MoveRight(+fMove);
	if (GetInputManager()->IsFullfilled("forward"))		m_xCamera.MoveForward(+fMove);
	if (GetInputManager()->IsFullfilled("backward"))	m_xCamera.MoveForward(-fMove);
	if (GetInputManager()->IsFullfilled("up"))			m_xCamera.MoveWorldUp(+fMove);
	if (GetInputManager()->IsFullfilled("down"))		m_xCamera.MoveWorldUp(-fMove);
}
//---------------------------------------------------------------------------------------------------------------------
void
CTestPhysics::CreateInputManagerMapping()
{
	GetInputManager()->Map("keyboard:control.held",	"fast");	

	GetInputManager()->Map("keyboard:shift.notheld  &&  keyboard:a.held",		"strafeleft");
	GetInputManager()->Map("keyboard:shift.notheld  &&  keyboard:d.held",		"straferight");
	GetInputManager()->Map("keyboard:shift.notheld  &&  keyboard:w.held",		"forward");
	GetInputManager()->Map("keyboard:shift.notheld  &&  keyboard:s.held",		"backward");
	GetInputManager()->Map("keyboard:shift.notheld  &&  keyboard:e.held",		"up");
	GetInputManager()->Map("keyboard:shift.notheld  &&  keyboard:q.held",		"down");

	GetInputManager()->Map("keyboard:shift.notheld  &&  keyboard:left.held",	"strafeleft");
	GetInputManager()->Map("keyboard:shift.notheld  &&  keyboard:right.held",	"straferight");
	GetInputManager()->Map("keyboard:shift.notheld  &&  keyboard:up.held",		"forward");
	GetInputManager()->Map("keyboard:shift.notheld  &&  keyboard:down.held",	"backward");
	GetInputManager()->Map("keyboard:shift.notheld  &&  keyboard:space.held",	"up");
	GetInputManager()->Map("keyboard:shift.notheld  &&  keyboard:return.held",	"down");

	GetInputManager()->Map("mouse:rightbutton.held   &&  mouse:x.value!=0",		"mouseturnleftright");
	GetInputManager()->Map("mouse:rightbutton.held   &&  mouse:y.value!=0",		"mouseturnupdown");

	GetInputManager()->Map("keyboard:b.down",								"createbox");
	GetInputManager()->Map("keyboard:b.held  &&  keyboard:shift.held",		"createbox");

	GetInputManager()->Map("keyboard:f.down",								"togglefollowmode");

	GetInputManager()->Map("keyboard:tab.down",								"toggledebugrendering");
}
//-----------------------------------------------------------------------------------------------------------------------
void 
CTestPhysics::CreateScene()
{
	SetFilesystemMapping();

	__super::CreateScene();

	CreateInputManagerMapping();

    GetXFileLoader()->LoadFXMapping("../shaders/fxselect.xml");
    TEffectHandle hndConstantsEffect = GetEffectFactory()->CreateEffect("../shaders/constants.fx");
    GetEffectFactory()->SetSharedVarsEffect(hndConstantsEffect);

	m_pDynamicsSimulation = new CDynamicsSimulation();

	m_hBoxModel = GetModelFactory()->CreateModelFromFile("model>box.x");
	m_hLevel	= GetModelFactory()->CreateModelFromFile("model>level.x");

	m_pPlane = new CDOPlane(m_pDynamicsSimulation, 0.0f, 1.0f, 0.0f, 0.0f);
	m_pBuggy = new CDOSimpleBuggy(m_pDynamicsSimulation);

	m_xCamera.SetFarPlaneDistance(1000.0f);
	m_xCamera.SetPos(CVec3(20.0f, 5.0f, 10.0f));
	m_xCamera.SetOrientationByLookAtPoint(CVec3(0.0f, 0.0f, 0.0f));

	m_pTestScreen = (CTestScreen*) CTestScreen::Create();
	GetUIScreenStateMachine()->AddScreen("test", m_pTestScreen);
	GetUIScreenStateMachine()->SwitchToScreen("test");

	m_bDebugRendering = true;
	m_bCameraFollowsBuggy = false;
	ShowCursor(true);
}
//-----------------------------------------------------------------------------------------------------------------------
void	
CTestPhysics::Input()
{
	__super::Input();
}
//-----------------------------------------------------------------------------------------------------------------------
void	
CTestPhysics::Update()
{
	__super::Update();

	MoveCamera((float) GetTimeInSecondsSinceLastUpdate());
	if(GetInputManager()->IsFullfilled("toggledebugrendering"))		{ m_bDebugRendering = !m_bDebugRendering; }
	UpdateWorld();
	m_pDynamicsSimulation->Update(GetTimeInSecondsSinceLastUpdate());

	GetUIScreenStateMachine()->Tick();
}
//-----------------------------------------------------------------------------------------------------------------------
void	
CTestPhysics::Output()
{
	IDirect3DDevice9* pD3DDevice = GetDevice();

	if (SUCCEEDED(GetDevice()->BeginScene()))
	{	
		pD3DDevice->Clear(0, NULL, D3DCLEAR_TARGET | D3DCLEAR_ZBUFFER, 0x204080, 1.0f, 0);

		m_xCamera.SetupMatrices();
		m_xCamera.UpdateViewFrustum();

		TRenderContextPtr spxRenderContext;
		spxRenderContext.Create();
		spxRenderContext->m_fTime = 0.0f;
		spxRenderContext->m_fDeltaTime = (float) GetTimeInSecondsSinceLastUpdate();;
		spxRenderContext->m_vLightDir = CVec3(1.0f, 0.0f, 0.0f);
		spxRenderContext->m_pxEngineController = this;
        
		m_xCamera.SetupRenderContext(spxRenderContext);
		
		m_pBuggy->Render(m_xCamera);

		CMat4S mWorld;
		for(unsigned int i=0; i<m_apBoxes.size(); ++i)
		{
			if(m_bDebugRendering)
			{
				m_apBoxes[i]->Render(m_xCamera);
			}
			else
			{
				m_apBoxes[i]->GetMatrix(mWorld);
				m_hBoxModel->Render(spxRenderContext, mWorld);
			}
		}

		mWorld.SetIdentity();
		m_hLevel->Render(spxRenderContext, mWorld);
		
		GetUIScreenStateMachine()->Render();

		pD3DDevice->EndScene();
	}
	GetDevice()->Present(0, 0, 0, 0);
}
//-----------------------------------------------------------------------------------------------------------------------
void 
CTestPhysics::Terminate()
{
	delete m_pPlane;
	delete m_pBuggy;

	for(unsigned int i=0; i<m_apBoxes.size(); ++i)
	{
		delete m_apBoxes[i];
	}

	m_hBoxModel.Release();
	m_hLevel.Release();
	CGfxDebugMarker::Shut();

	delete m_pDynamicsSimulation;

	__super::Terminate();
}
//-----------------------------------------------------------------------------------------------------------------------
void
CTestPhysics::UpdateWorld()
{
	m_pBuggy->Update();

	if (GetInputManager()->ConsumeEvent("createbox"))	
	{
		CDOBox* pBox = new CDOBox(m_pDynamicsSimulation, 1.0f, 1.0f, 1.0f);
		pBox->SetPosition(CVec3(-3.0f, 2.0f, -3.0f));
		m_apBoxes.push_back(pBox);
	}

	if (GetInputManager()->ConsumeEvent("togglefollowmode"))
	{
		m_bCameraFollowsBuggy = !m_bCameraFollowsBuggy;
	}
	if(m_bCameraFollowsBuggy)
	{
		m_xCamera.SetPos(m_pBuggy->GetPosition() + CVec3(0.0f, 1.0, -3.0f));
		m_xCamera.SetOrientationByLookAtPoint(m_pBuggy->GetPosition());
	}
	

	m_pTestScreen->m_pCarSpeedLabel->SetText(CStr::Create("%.2f", m_pBuggy->GetVelocityInKmH()));
}
//-----------------------------------------------------------------------------------------------------------------------

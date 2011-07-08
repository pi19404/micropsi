#include "3demotion.h"

#include <dinput.h>

#ifdef _DEBUG
    #include <malloc.h>     // _heapchk()
#endif

#include "baselib/filelocator.h"
#include "baselib/str.h"

#include "e42/core/XFileLoader.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/TextureFactory.h"
#include "e42/core/SurfaceFactory.h"
#include "e42/core/EffectShader.h"
#include "e42/core/EffectFactory.h"
#include "baselib/XMLUtils.h"
#include "e42/RenderTargetTexture.h"
#include "e42/utils/GfxDebugMarker.h"

#include "SoundLib/soundsystem.h"

#include "GameLib/World/SimTimeCtrl.h"
#include "GameLib/UserInterface/Input/InputManager.h"
#include "GameLib/UserInterface/UIScreenStateMachine.h"

#include "Application/Face.h"
#include "UI/Screens/FullGUIScreen.h"
#include "UI/Screens/MinimalGUIScreen.h"
#include "Utilities/systemutils.h"

#include "tinyxml.h"
#include "Resources/resource.h"

using std::string;
using std::vector;
using namespace XMLUtils;

//---------------------------------------------------------------------------------------------------------------------

C3DEmotion* C3DEmotion::ms_pxInstance = 0;
HWND C3DEmotion::ms_hParentWindow = NULL;
string	C3DEmotion::ms_sBasePath = "";
string	C3DEmotion::ms_sCommandLine = "";

//---------------------------------------------------------------------------------------------------------------------
CE42Application* 
CreateE42Application(HINSTANCE hInstance)
{
    return new C3DEmotion(hInstance);
}
//---------------------------------------------------------------------------------------------------------------------
void
DestroyE42Application(CE42Application* pApplication)
{
    delete pApplication;
}
//---------------------------------------------------------------------------------------------------------------------
string 
TextureLoadCallBack(const CFileLocator* p_pxFileLocator, string p_sAlias, string p_sRemainingPath, string p_sSubsitutePath, void* p_pUserData)
{
	struct TRes
	{
		int		 	 m_iRes;
		const char*  m_pcDir;
	};

	const int iMaxRes = 6;
	static TRes xResolutions[iMaxRes] = { {32, "32"}, {64, "64"}, {128, "128"}, {256, "256"}, {512, "512"}, {1024, "1024"} }; 

	int* piMaxTexRes = (int*) p_pUserData;

	int iDesiredTexRes;
	for(iDesiredTexRes=0; iDesiredTexRes<iMaxRes; ++iDesiredTexRes)
	{
		if(xResolutions[iDesiredTexRes].m_iRes == *piMaxTexRes)
		{
			break;
		}
	}
	if(iDesiredTexRes == iMaxRes)
	{
		assert(false);		// illegal maximum texture resolution
		iDesiredTexRes = iMaxRes-1;
	}

	// search downwards
	for(int i=iDesiredTexRes; i>=0; --i)
	{
		string sFile = CFileLocator::ConcatPaths(p_sSubsitutePath, xResolutions[i].m_pcDir);
		sFile = CFileLocator::ConcatPaths(sFile, p_sRemainingPath);
		if(CFileLocator::FileExists(sFile))
		{
			return sFile;
		}
	}

	// search upwards
	for(int i=iDesiredTexRes+1; i<iMaxRes; ++i)
	{
		string sFile = CFileLocator::ConcatPaths(p_sSubsitutePath, xResolutions[i].m_pcDir);
		sFile = CFileLocator::ConcatPaths(sFile, p_sRemainingPath);
		if(CFileLocator::FileExists(sFile))
		{
			return sFile;
		}
	}

	return CFileLocator::ConcatPaths(p_sSubsitutePath, p_sRemainingPath);
}
//---------------------------------------------------------------------------------------------------------------------
string 
ShaderLoadCallBack(const CFileLocator* p_pxFileLocator, string p_sAlias, string p_sRemainingPath, string p_sSubsitutePath, void* p_pUserData)
{
	const int iMaxShaderVersion = 3;
	static const char* pcShaderDirs[iMaxShaderVersion] = { "dx9", "dx8", "dx7" }; 
	string* psShaderVersion = (string*) p_pUserData;

	int iDesiredShaderVersion;
	for(iDesiredShaderVersion=0; iDesiredShaderVersion<iMaxShaderVersion; ++iDesiredShaderVersion)
	{
		if(*psShaderVersion == pcShaderDirs[iDesiredShaderVersion])
		{
			break;
		}
	}

	// search downwards
	for(int i=iDesiredShaderVersion; i<iMaxShaderVersion; ++i)
	{
		string sFile = CFileLocator::ConcatPaths(p_sSubsitutePath, pcShaderDirs[i]);
		sFile = CFileLocator::ConcatPaths(sFile, p_sRemainingPath);
		if(CFileLocator::FileExists(sFile))
		{
			return sFile;
		}
	}

	return CFileLocator::ConcatPaths(p_sSubsitutePath, p_sRemainingPath);
}
//---------------------------------------------------------------------------------------------------------------------
C3DEmotion::C3DEmotion(HINSTANCE hInstance)
:   CGameLibApplication(hInstance),
    m_bWideScreen(false)
{
//	_CrtSetBreakAlloc(230573);

	ms_pxInstance = this;
	m_bIsRunning = false;
	m_bWireframe = false;
	
	SetDefaultConfig();
	m_xConfiguration.Load((ms_sBasePath + "../data/config/3demotion.config.xml").c_str());
	m_xConfiguration.Save((ms_sBasePath + "../data/config/3demotion.config.xml").c_str());
	ReadCommandLineOptions();

	m_xSettings.m_iWindowWidth		= m_xConfiguration.GetValueSize("application/windowsize").cx;
	m_xSettings.m_iWindowHeight		= m_xConfiguration.GetValueSize("application/windowsize").cy;
	m_xSettings.m_bFullScreen		= m_xConfiguration.GetValueBool("application/fullscreen");
	m_xSettings.m_bTripleBuffering	= m_xConfiguration.GetValueBool("general/triplebuffering");
	m_xSettings.m_bVSyncEnable		= m_xConfiguration.GetValueBool("general/vsync");
    m_xSettings.m_dwMaxMultiSampleQuality = 0;
	m_xSettings.m_DepthStencilFormat = D3DFMT_D24X8;	// Fixme: checken, ob unterstützt!

	HICON hIcon = LoadIcon(m_hInstance, MAKEINTRESOURCE(IDI_DEMOTION));
    m_WindowClass.hIcon = hIcon;
    m_WindowClass.hIconSm = NULL;
    m_WindowClass.style = CS_CLASSDC | CS_DBLCLKS;
    m_WindowClass.lpszMenuName = NULL;

    m_WindowClass.lpszClassName = "microPSI 3DView2 - 3DEmotion Viewer";
    m_uiWindowStyle = WS_OVERLAPPEDWINDOW;

	m_xSettings.m_uiMouseCoopFlags =    DISCL_NONEXCLUSIVE | DISCL_FOREGROUND;
    m_xSettings.m_uiKeyboardCoopFlags = DISCL_NONEXCLUSIVE | DISCL_FOREGROUND; 
    m_xSettings.m_uiGamepadCoopFlags =  DISCL_EXCLUSIVE | DISCL_FOREGROUND; 

    if(m_xSettings.m_bFullScreen)
    {
        m_xSettings.m_uiKeyboardCoopFlags |= DISCL_NOWINKEY;
    }

	m_pxSimTimeCtrl				= 0;

	m_bDebugKeysEnabled			= m_xConfiguration.GetValueBool("debug/debugkeysenabled");
	m_iDefaultTextureResolution = atoi(m_xConfiguration.GetValueString("general/defaulttextureresolution").c_str());
}


//---------------------------------------------------------------------------------------------------------------------
C3DEmotion::~C3DEmotion()
{
    assert((_heapchk() == _HEAPOK) && "ShutDownError: Heapcheck failed");
}


//---------------------------------------------------------------------------------------------------------------------
/**
	reads and interprets the command line
*/
void
C3DEmotion::ReadCommandLineOptions()
{
	m_xConfiguration.ReadFromCommandLine(ms_sCommandLine.c_str());
}

//---------------------------------------------------------------------------------------------------------------------
void
C3DEmotion::SetDefaultConfig()
{
	m_xConfiguration.AddParameterBool(	"application/fullscreen", 
										"Determines whether the application should start in fullscreen mode (as opposed to windowed mode)",
										false);

	m_xConfiguration.AddParameterBool(	"application/alwaysontop",
										"Determines whether the application window should always be above all other windows",
										false);

	m_xConfiguration.AddParameterSize(	"application/windowsize",
										"Screen or window resolution in pixels",
										CSize(800, 600), CSize(10, 10), CSize(10000, 10000));

	m_xConfiguration.AddParameterBool(	"general/vsync",
										"Determines whether syncronization the screen update (\"vertical retrace\") is enabled. This can reduce flickering, but will slow the application down somewhat.",
										false);

	m_xConfiguration.AddParameterBool(	"general/triplebuffering",
										"Triple buffering reduces the negative impact of vsync on application speed - at the cost of video memory. Note that enabling triple buffering makes only sense if vsync is enabled as well.",
										false);

	m_xConfiguration.AddParameterString("general/defaulttextureresolution",
										"Determines the default texture resolution. Reduce this value if your graphics card does not have enough video memory or the application runs too slow. This will reduce the visual quality.",
										"1024", "32, 64, 128, 256, 512, 1024");

	m_xConfiguration.AddParameterBool(	"sound/soundenabled",
										"Determines whether sound is enabled",
										true);

	m_xConfiguration.AddParameterString("debug/shaderversion",
										"Determines the shader version to use. dx9 requires at least pixel shader 2.0 support. dx8 requires pixel shader 1.4. dx7 uses the fixed function pipeline. detect will use the maximum version that is available.",
										"detect", "detect, dx7, dx8, dx9");

	m_xConfiguration.AddParameterBool(	"debug/debugkeysenabled",
										"Determines whether special debugging keyboard shortcuts are enabled",
										true);

	m_xConfiguration.AddParameterString("face/model",
										"The model file for the face", 
										"face.x");
} 	
//---------------------------------------------------------------------------------------------------------------------
void 
C3DEmotion::SelectShaderVersion()
{
	m_sShaderVersion = m_xConfiguration.GetValueString("debug/shaderversion");
	if(m_sShaderVersion == "detect")
	{
		if(GetDeviceCaps()->PixelShaderVersion >= D3DPS_VERSION(2,0))
		{
			m_sShaderVersion = "dx9";
			DebugPrint("pixel shader 2.0 or better, using dx9 shaders");
		}
		else if(GetDeviceCaps()->PixelShaderVersion >= D3DPS_VERSION(1,4))
		{
			m_sShaderVersion = "dx8";
			DebugPrint("pixel shader 1.4 or better, using dx8 shaders");
		}
		else
		{
			m_sShaderVersion = "dx7";
			DebugPrint("no pixel shader support (at least 1.4 required), using dx7 shaders");
		}
	}
}
//---------------------------------------------------------------------------------------------------------------------
void 
C3DEmotion::CreateScene()
{
	SelectShaderVersion();
	SetFilesystemMapping();

	__super::CreateScene();

	if(m_xConfiguration.GetValueBool("application/alwaysontop"))
	{
		::SetWindowPos(GetWindowHandle(), HWND_TOPMOST, 0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE | SWP_FRAMECHANGED);
	}
	else
	{
		::SetWindowPos(GetWindowHandle(), HWND_TOP, 0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE | SWP_FRAMECHANGED);
	}
	
    SoundLib::CSoundSystem::Init(GetWindowHandle());
	SoundLib::CSoundSystem::Get().SetSoundEnabled(m_xConfiguration.GetValueBool("sound/soundenabled"));
	SoundLib::CWaveFactory::Get().AddSearchPath(GetFileLocator()->GetPath("sounds>").c_str());

    GetXFileLoader()->LoadFXMapping(ms_sBasePath + "../data/shaders/fxselect.xml");
    TEffectHandle hndConstantsEffect = 
		GetEffectFactory()->CreateEffect(ms_sBasePath + "../data/shaders/constants.fx");
    GetEffectFactory()->SetSharedVarsEffect(hndConstantsEffect);

	m_pxFace = new CFace(m_xConfiguration.GetValueString("face/model"));
	
	m_pxSimTimeCtrl					= new CSimTimeCtrl();
	m_pxSimTimeCtrl->SetSimStepDuration(1 / 30.0f);
	m_pxSimTimeCtrl->SetSimCallback(CreateMemberCallback(this, C3DEmotion::OnSimulationTick));

	SetInputMapping();

	m_xCamera.SetFarPlaneDistance(1000.0f);
	m_xCamera.SetNearPlaneDistance(0.5f); 
	m_xCamera.SetFieldOfViewHeight(55.0f / 180.0f * PIf);

//	m_xCamera.SetPos(CVec3(0.0f, 0.0f, -50.0f));

	m_xCamera.SetPos(CVec3(0.0f, -550.0f, 150.0f));
	m_xCamera.SetOrientation(CVec3(0.0f, 1.0f, 0.0f));
	m_xCamera.SetUpVec(CVec3(0.0f, 0.0f, 1.0f));

	m_bIsRunning = true;

	AttachToParentWindow();

	GetUIScreenStateMachine()->AddScreen("fullgui", CFullGUIScreen::Create());
	GetUIScreenStateMachine()->AddScreen("minimalgui", CMinimalGUIScreen::Create());
#ifdef MICROPSI3DEMOTION_JNI_DLL
	GetUIScreenStateMachine()->SwitchToScreen("minimalgui");
#else
	GetUIScreenStateMachine()->SwitchToScreen("fullgui");
#endif
}
//-----------------------------------------------------------------------------------------------------------------------
void
C3DEmotion::Input()
{
	__super::Input();
}
//-----------------------------------------------------------------------------------------------------------------------
void
C3DEmotion::Update()
{
	SoundLib::CSoundSystem::Get().Tick();
    GetUIScreenStateMachine()->Tick();
	UpdateFPS();
	m_pxSimTimeCtrl->ElapseTime(GetTimeInSecondsSinceLastUpdate());
}
//-----------------------------------------------------------------------------------------------------------------------
void
C3DEmotion::Output()
{
	Render();
}
//---------------------------------------------------------------------------------------------------------------------
void
C3DEmotion::SetInputMapping()
{
	// -- General --
	CInputManager* pxInputManager = GetInputManager();

	pxInputManager->Map("keyboard:escape.down",	"exit");
	pxInputManager->Map("keyboard:space.down",	"switchscreen");

	// -- Debug Keys --

	pxInputManager->Map("keyboard:control.held  &&  keyboard:f2.down",	"debug_wireframe");


	// -- Movement --

	pxInputManager->Map("keyboard:shift.held",		"move_fast");
	pxInputManager->Map("keyboard:control.held",	"move_turbo");

	pxInputManager->Map("keyboard:a.held",		"move_strafeleft");
	pxInputManager->Map("keyboard:d.held",		"move_straferight");
	pxInputManager->Map("keyboard:w.held",		"move_forward");
	pxInputManager->Map("keyboard:s.held",		"move_backward");
	pxInputManager->Map("keyboard:e.held",		"move_up");
	pxInputManager->Map("keyboard:q.held",		"move_down");

	pxInputManager->Map("keyboard:left.held",		"move_strafeleft");
	pxInputManager->Map("keyboard:right.held",		"move_straferight");
	pxInputManager->Map("keyboard:up.held",			"move_forward");
	pxInputManager->Map("keyboard:down.held",		"move_backward");

	pxInputManager->Map("mouse:rightbutton.held   &&  mouse:x.value!=0",	"move_mouseturnleftright");
	pxInputManager->Map("mouse:rightbutton.held   &&  mouse:y.value!=0",	"move_mouseturnupdown");
	pxInputManager->Map("mouse:middlebutton.held  &&  mouse:x.value!=0",	"move_mouseturnleftright");
	pxInputManager->Map("mouse:middlebutton.held  &&  mouse:y.value!=0",	"move_mouseturnupdown");
}

//---------------------------------------------------------------------------------------------------------------------
void
C3DEmotion::SetFilesystemMapping()
{
	GetFileLocator()->SetAlias("texture", ms_sBasePath + "../data/textures", TextureLoadCallBack, (void*) &m_iDefaultTextureResolution );
	GetFileLocator()->SetAlias("xfl-texture", ms_sBasePath + "../data/textures", TextureLoadCallBack, (void*) &m_iDefaultTextureResolution);
	GetFileLocator()->SetAlias("prt-texture", ms_sBasePath + "../data/textures", TextureLoadCallBack, (void*) &m_iDefaultTextureResolution);
	GetFileLocator()->SetAlias("xfl-shader", ms_sBasePath + "../data/shaders", ShaderLoadCallBack, (void*) &m_sShaderVersion);
	GetFileLocator()->SetAlias("prt-shader", ms_sBasePath + "../data/shaders", ShaderLoadCallBack, (void*) &m_sShaderVersion);
	GetFileLocator()->SetAlias("shader", ms_sBasePath + "../data/shaders", ShaderLoadCallBack, (void*) &m_sShaderVersion);
    GetFileLocator()->SetAlias("model", ms_sBasePath + "../data/models");
    GetFileLocator()->SetAlias("animation", ms_sBasePath + "../data/anims");
    GetFileLocator()->SetAlias("interface", ms_sBasePath + "../data/ui");
    GetFileLocator()->SetAlias("sounds", ms_sBasePath + "../data/sounds");
}

//---------------------------------------------------------------------------------------------------------------------
void 
C3DEmotion::Terminate()
{
	__super::Terminate();

	delete m_pxFace;

	delete m_pxSimTimeCtrl;

	CGfxDebugMarker::Shut();
	SoundLib::CSoundSystem::Shut();
    SoundLib::CWaveFactory::Shut();

	m_bIsRunning = false;
}
//---------------------------------------------------------------------------------------------------------------------
void
C3DEmotion::OnSimulationTick()
{
	if(m_bDebugKeysEnabled)
	{
		CheckDebugKeys();
		MoveCamera();
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
C3DEmotion::Render()
{
	SetEngineTime(m_pxSimTimeCtrl->GetContinuousSimTime());
	GetDeviceStateMgr()->Reset();

	if(true || m_bWireframe)
	{
		HRESULT hr = GetDevice()->Clear(0, 0, D3DCLEAR_TARGET | D3DCLEAR_ZBUFFER, 0x00000000, 1.0, 0);
	}
	else
	{
		HRESULT hr = GetDevice()->Clear(0, 0, D3DCLEAR_ZBUFFER, 0x002040FF, 1.0, 0);
	}

	if (SUCCEEDED(GetDevice()->BeginScene()))
    {
		HRESULT hr;

		SetDefaultSamplerState();
		SetDefaultRenderState();

		// face

		m_xCamera.SetupMatrices();
		m_xCamera.UpdateViewFrustum();

		TRenderContextPtr spxRenderContext;
		spxRenderContext.Create();
		spxRenderContext->m_fTime = (float) GetSimTimeCtrl()->GetContinuousSimTime();
		spxRenderContext->m_fDeltaTime = (float) GetSimTimeCtrl()->GetContinuousSimTimeDelta();
		spxRenderContext->m_vLightDir = CVec3(1.0f, 0.0f, 0.0f);
		spxRenderContext->m_pxEngineController = this;
        
		m_xCamera.SetupRenderContext(spxRenderContext);
		
		m_pxFace->Render(spxRenderContext);

		// pass windowsystem 

		GetDeviceStateMgr()->SetRenderState(D3DRS_FILLMODE, D3DFILL_SOLID);
		GetUIScreenStateMachine()->Render();

		GetDevice()->EndScene();
		
		hr = GetDevice()->Present(0, 0, 0, 0);
		assert(SUCCEEDED(hr));
    }
}

//---------------------------------------------------------------------------------------------------------------------
void
C3DEmotion::SetDefaultSamplerState()
{
    for (int iStage = 0; iStage < 8; iStage++)
    {
        HRESULT hr;
        hr = GetDeviceStateMgr()->SetSamplerState(iStage, D3DSAMP_MAGFILTER, D3DTEXF_LINEAR);      assert(SUCCEEDED(hr));
        hr = GetDeviceStateMgr()->SetSamplerState(iStage, D3DSAMP_MINFILTER, D3DTEXF_LINEAR);      assert(SUCCEEDED(hr));
        hr = GetDeviceStateMgr()->SetSamplerState(iStage, D3DSAMP_MIPFILTER, D3DTEXF_LINEAR);      assert(SUCCEEDED(hr));

        hr = GetDeviceStateMgr()->SetSamplerState(iStage, D3DSAMP_ADDRESSU, D3DTADDRESS_WRAP);     assert(SUCCEEDED(hr));
        hr = GetDeviceStateMgr()->SetSamplerState(iStage, D3DSAMP_ADDRESSV, D3DTADDRESS_WRAP);     assert(SUCCEEDED(hr));
        hr = GetDeviceStateMgr()->SetSamplerState(iStage, D3DSAMP_ADDRESSW, D3DTADDRESS_WRAP);     assert(SUCCEEDED(hr));
    }
}
//---------------------------------------------------------------------------------------------------------------------
void
C3DEmotion::SetDefaultRenderState()
{
    GetDeviceStateMgr()->SetRenderState(D3DRS_ZENABLE, D3DZB_TRUE);
    GetDeviceStateMgr()->SetRenderState(D3DRS_ZFUNC, D3DCMP_LESS);
    GetDeviceStateMgr()->SetRenderState(D3DRS_CULLMODE, D3DCULL_CW);
    GetDeviceStateMgr()->SetRenderState(D3DRS_LIGHTING, FALSE);

    if (m_bWireframe)
    {
		GetDeviceStateMgr()->SetRenderState(D3DRS_FILLMODE, D3DFILL_WIREFRAME);
    }
    else
    {
		GetDeviceStateMgr()->SetRenderState(D3DRS_FILLMODE, D3DFILL_SOLID);
    }

}
//---------------------------------------------------------------------------------------------------------------------
void
C3DEmotion::AttachToParentWindow()
{
	if(ms_hParentWindow != NULL)
	{
		HWND oldhwnd = ::SetParent(GetWindowHandle(), ms_hParentWindow);
		if(oldhwnd == NULL)
		{
			DebugPrint("SetParent failed!");
		}
		SetWindowLong(GetWindowHandle(), GWL_STYLE, WS_VISIBLE);			// remove frame and caption
	}
}
//---------------------------------------------------------------------------------------------------------------------
void
C3DEmotion::UpdateFPS()
{
	float fAvgFrameDuration = 1 / m_fAvgFPS;
    fAvgFrameDuration = (float)(GetTimeInSecondsSinceLastUpdate() * 1 / 60 + fAvgFrameDuration * 59 / 60);
    m_fAvgFPS = 1.0f / fAvgFrameDuration;
}
//---------------------------------------------------------------------------------------------------------------------
void 
C3DEmotion::CheckDebugKeys()
{
	if(GetInputManager()->IsFullfilled("debug_wireframe"))
	{
		m_bWireframe = !m_bWireframe;	
	}
}
//---------------------------------------------------------------------------------------------------------------------
void
C3DEmotion::MoveCamera()
{
    CInputManager*			pxInputManager = GetInputManager();
	CCamera*				pCamera = &m_xCamera;
	float					fDeltaTime = (float) GetTimeInSecondsSinceLastUpdate();

    bool bSpeedModifier = pxInputManager->IsFullfilled("move_fast");
	bool bTurboModifier = pxInputManager->IsFullfilled("move_turbo");

	if (pxInputManager->IsFullfilled("move_mouseturnleftright")  ||
		pxInputManager->IsFullfilled("move_mouseturnupdown"))
	{
		// drehen
		float fDX = pxInputManager->GetAxisValue("move_mouseturnleftright") / 100.0f;
		float fDY = pxInputManager->GetAxisValue("move_mouseturnupdown") / 100.0f;

		pCamera->RotateRight(fDX);
		pCamera->RotateUp(-fDY);
	}

    float fPanSpeed = 50.0f;
    if (bTurboModifier) fPanSpeed *= 40;
    else if (bSpeedModifier) fPanSpeed *= 6;

	if(pxInputManager->IsFullfilled("move_strafeleft"))			pCamera->MoveRight(-fDeltaTime * fPanSpeed);
	if(pxInputManager->IsFullfilled("move_straferight"))		pCamera->MoveRight(fDeltaTime * fPanSpeed);
	if(pxInputManager->IsFullfilled("move_forward"))			pCamera->MoveForward(fDeltaTime * fPanSpeed);
	if(pxInputManager->IsFullfilled("move_backward"))			pCamera->MoveForward(-fDeltaTime * fPanSpeed);
	if(pxInputManager->IsFullfilled("move_up"))					pCamera->MoveUp(fDeltaTime * fPanSpeed);
	if(pxInputManager->IsFullfilled("move_down"))				pCamera->MoveUp(-fDeltaTime * fPanSpeed);
}
//---------------------------------------------------------------------------------------------------------------------

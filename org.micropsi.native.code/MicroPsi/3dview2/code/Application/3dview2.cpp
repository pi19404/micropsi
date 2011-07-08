#include "3dview2.h"

#include <dinput.h>

#ifdef _DEBUG
    #include <malloc.h>     // _heapchk()
#endif

#include "baselib/filelocator.h"
#include "baselib/str.h"

#include "e42/core/MaterialConverter.h"
#include "e42/core/XFileLoader.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/TextureFactory.h"
#include "e42/core/SurfaceFactory.h"
#include "e42/core/EffectShader.h"
#include "baselib/xmlutils.h"
#include "e42/RenderTargetTexture.h"
#include "e42/utils/GfxDebugMarker.h"

#include "SoundLib/soundsystem.h"

#include "GameLib/World/SimTimeCtrl.h"
#include "GameLib/UserInterface/Input/InputManager.h"
#include "GameLib/UserInterface/UIScreenStateMachine.h"

#include "Engine/Terrain/terrainsystem.h"
#include "Engine/Terrain/splatchunkmanager.h"

#include "Observers/Observer.h"
#include "Observers/ObserverControllerSwitcher.h"

#include "World/world.h"
#include "World/objectmanager.h"

#include "Utilities/systemutils.h"

#include "Communication/CommunicationModule.h"

#include "UI/Windows/performancewindow.h"
#include "UI/Screens/LevelEditorScreen.h"
#include "UI/Screens/MainMenuScreen.h"
#include "UI/Screens/SpectatorScreen.h"
#include "UI/Screens/AgentControlScreen.h"

#include "tinyxml.h"

#include "opcode.h"

#include "Resources/resource.h"

using std::string;
using std::vector;
using namespace XMLUtils;

//---------------------------------------------------------------------------------------------------------------------

C3DView2* C3DView2::ms_pxInstance = 0;
HWND C3DView2::ms_hParentWindow = NULL;
string	C3DView2::ms_sBasePath = "";
string	C3DView2::ms_sCommandLine = "";

//---------------------------------------------------------------------------------------------------------------------
CE42Application* 
CreateE42Application(HINSTANCE hInstance)
{
    return new C3DView2(hInstance);
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
C3DView2::C3DView2(HINSTANCE hInstance)
:   CE42Application(hInstance)
{
//	_CrtSetBreakAlloc(230573);

	Opcode::InitOpcode();

	ms_pxInstance = this;
	m_bIsRunning = false;
	m_bWireframe = false;
	
    m_dTimeOfCurrentFrame = 0;
    m_dDurationOfLastFrame = 0;
	m_xPerformanceData.m_fAvgFPS = 30.0f;

	SetDefaultConfig();
	m_xConfiguration.Load((ms_sBasePath + "../data/config/configuration.xml").c_str());
	m_xConfiguration.Save((ms_sBasePath + "../data/config/configuration.xml").c_str());
	ReadCommandLineOptions();

	m_xSettings.m_iWindowWidth		= m_xConfiguration.GetValueSize("application/windowsize").cx;
	m_xSettings.m_iWindowHeight		= m_xConfiguration.GetValueSize("application/windowsize").cy;
	m_xSettings.m_bFullScreen		= m_xConfiguration.GetValueBool("application/fullscreen");
	m_xSettings.m_bTripleBuffering	= m_xConfiguration.GetValueBool("general/triplebuffering");
	m_xSettings.m_bVSyncEnable		= m_xConfiguration.GetValueBool("general/vsync");
    m_xSettings.m_dwMaxMultiSampleQuality = 0;

	HICON hIcon = LoadIcon(m_hInstance, MAKEINTRESOURCE(IDI_3DVIEW2));
    m_WindowClass.hIcon = hIcon;
    m_WindowClass.hIconSm = NULL;
    m_WindowClass.style = CS_CLASSDC | CS_DBLCLKS;
    m_WindowClass.lpszMenuName = NULL;

    m_WindowClass.lpszClassName = "microPSI 3DView2";
    m_uiWindowStyle = WS_OVERLAPPEDWINDOW;
//	m_uiWindowStyle = WS_OVERLAPPED | WS_CAPTION | WS_THICKFRAME | WS_MINIMIZEBOX | WS_MAXIMIZEBOX;

	m_xSettings.m_uiMouseCoopFlags =    DISCL_NONEXCLUSIVE | DISCL_FOREGROUND;
    m_xSettings.m_uiKeyboardCoopFlags = DISCL_NONEXCLUSIVE | DISCL_FOREGROUND; 
    m_xSettings.m_uiGamepadCoopFlags =  DISCL_EXCLUSIVE | DISCL_FOREGROUND; 

    if (m_xSettings.m_bFullScreen)
    {
        m_xSettings.m_uiKeyboardCoopFlags |= DISCL_NOWINKEY;
    }

	m_pxUIScreenStateMachine	= 0;
	m_pxWorld					= 0;
	m_pxObserverControllerSwitcher = 0;
	m_pxObserver				= 0;
	m_pxSimTimeCtrl				= 0;
	m_pxCommunicationModule		= 0;

	m_pxWaterReflectionTexture	= 0;
	m_bShowReflectionTexture	= false;

	m_pxShadowMapTexture		= 0;
	m_bShowShadowMap			= false;

	m_bDebugKeysEnabled			= m_xConfiguration.GetValueBool("debug/debugkeysenabled");
	m_fRangeOfVision			= m_xConfiguration.GetValueFloat("general/rangeofvision");
	m_iDefaultTextureResolution = atoi(m_xConfiguration.GetValueString("general/defaulttextureresolution").c_str());
	m_fMaxObjDistance			= m_xConfiguration.GetValueFloat("general/maxobjectdistance");
	m_fMaxObjReflectionDistance = m_xConfiguration.GetValueFloat("water/maxobjectreflectiondistance");
	m_bShadowsEnabled			= m_xConfiguration.GetValueBool("shadow/shadowsenabled");
	m_bWaterReflectsShadows		= m_xConfiguration.GetValueBool("water/reflectshadows");
	m_bDistancePasses			= m_xConfiguration.GetValueBool("general/distancepasses");
}


//---------------------------------------------------------------------------------------------------------------------
C3DView2::~C3DView2()
{
	Opcode::CloseOpcode();

	assert((_heapchk() == _HEAPOK) && "ShutDownError: Heapcheck failed");
}


//---------------------------------------------------------------------------------------------------------------------
/**
	reads and interprets the command line
*/
void
C3DView2::ReadCommandLineOptions()
{
	DebugPrint("David: Command line is: %s", ms_sCommandLine.c_str());
	m_xConfiguration.ReadFromCommandLine(ms_sCommandLine.c_str());
}

//---------------------------------------------------------------------------------------------------------------------
void
C3DView2::SetDefaultConfig()
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

	m_xConfiguration.AddParameterString("networking/worldserver",
										"Default World server to connect to",
										"localhost");

	m_xConfiguration.AddParameterInt(	"networking/tcpworldserverport",
										"for legacy tpc connection (deprecated): connection port on the world server",
										10001, 0, 65535);

	m_xConfiguration.AddParameterInt(	"networking/httpport",
										"micropsi server http port ",
										8080, 0, 65535);

	m_xConfiguration.AddParameterString("networking/consoleserviceurl",
										"URL of the microPSI console service (used for communication with the world simulation)",
										"/micropsi/consoleservice");

	m_xConfiguration.AddParameterString("networking/avatarserviceurl",
										"URL of the microPSI avatar service (used for simulation of remote agent)",
										"/micropsi/avatarservice");

	m_xConfiguration.AddParameterBool(	"general/vsync",
										"Determines whether syncronization the screen update (\"vertical retrace\") is enabled. This can reduce flickering, but will slow the application down somewhat.",
										false);

	m_xConfiguration.AddParameterBool(	"general/triplebuffering",
										"Triple buffering reduces the negative impact of vsync on application speed - at the cost of video memory. Note that enabling triple buffering makes only sense if vsync is enabled as well.",
										false);

	m_xConfiguration.AddParameterBool(	"general/distancepasses",
										"Uses two render passes for different distances. Reduces Z-Fighting, but slows the application down.",
										false);

	m_xConfiguration.AddParameterString("general/defaulttextureresolution",
										"Determines the default texture resolution. Reduce this value if your graphics card does not have enough video memory or the application runs too slow. This will reduce the visual quality.",
										"1024", "32, 64, 128, 256, 512, 1024");

	m_xConfiguration.AddParameterFloat(	"general/rangeofvision",
										"Determines the range of vision in the application. Reduce this value if the application runs too slow.",
										1000.0f, 100.0f, 2000.0f);

	m_xConfiguration.AddParameterFloat(	"general/maxobjectdistance",
										"Determines the maximum visibility distance of world objects. Lowering this value improves performance. This value cannot be higher than the range of vision",
										800.0f, 0.0f, 2000.0f);

	m_xConfiguration.AddParameterFloat(	"general/maxdistancefromterrain",
										"Determines the maximum distance you can move away from the terrain (only if map is not wrapped around).",
										500.0f, 0.0f, 10000.0f);

	m_xConfiguration.AddParameterFloat(	"water/maxobjectreflectiondistance",
										"Determines the maximum distance of world objects reflected on the water surface. Lowering this value improves performance. Set this value to 0 to disable water reflections of world objects altogether.",
										300.0f, 0.0f, 2000.0f);

	m_xConfiguration.AddParameterString("water/reflectiontextureresolution",
										"Determines the resolution of the water reflections texture. The higher, the better the reflections will look - but the price of beauty is speed and texture memory. However, the actual size used will never be higher than screen size.",
										"512", "1024 512 256 128 64 32");

	m_xConfiguration.AddParameterBool  ("water/reflectshadows",
										"Determines whether object shadows are reflected on the water surface. This feature is hardly noticable, however, the performance cost is only very small. Shadows must be enabled for this to work.",
										true);

	m_xConfiguration.AddParameterBool(  "shadow/shadowsenabled",
										"Determines whether shadows are enabled or not. Shadows look nice, but slow the application down and use texture memory.",
										true);

	m_xConfiguration.AddParameterString("shadow/shadowmapresolution",
										"Determines the resolution of the shadow map texture. The higher, the better the shadows will look - but the price of beauty is speed and texture memory. The actual value used will never be higher than the window width in pixels.",
										"512", "4096 2048 1024 512 256 128 64 32");

	m_xConfiguration.AddParameterBool(	"sound/soundenabled",
										"Determines whether sound is enabled",
										true);

	m_xConfiguration.AddParameterString("debug/shaderversion",
										"Determines the shader version to use. dx9 requires at least pixel shader 2.0 support. dx8 requires pixel shader 1.4. dx7 uses the fixed function pipeline. detect will use the maximum version that is available.",
										"detect", "detect, dx7, dx8, dx9");

	m_xConfiguration.AddParameterBool(	"debug/debugkeysenabled",
										"Determines whether special debugging keyboard shortcuts are enabled",
										true);

	m_xConfiguration.AddParameterString("startup/mode",
										"Determines in which mode 3DView2 will start",
										"menu", "menu spectator agent editor");

	m_xConfiguration.AddParameterBool  ("startup/connect",
										"Determines wheter 3DView2 will attempt to connect to server on startup",
										false);

	m_xConfiguration.AddParameterString("startup/connectionmethod",
										"Determines what connection method 3DView2 will use when attempting to connect to server on startup",
										"tcp", "tcp http");

	m_xConfiguration.AddParameterString("startup/offlineworld",
										"Determines which offline world file 3DView2 will load when starting in offline mode",
										"defaultisland.xml");

	m_xConfiguration.AddParameterBool  ("startup/createdemoagents",
										"set to true if you want fake demo agents on startup (only in offline mode)",
										true);
} 	

//---------------------------------------------------------------------------------------------------------------------
float 
C3DView2::GetWindowAspectRatio() const
{
    return CE42Application::GetAspectRatio();
}

//---------------------------------------------------------------------------------------------------------------------
void 
C3DView2::CreateWaterReflectionTexture()
{
	int iTextureSize = atoi(m_xConfiguration.GetValueString("water/reflectiontextureresolution").c_str());
	while (iTextureSize > m_xSettings.m_iWindowWidth  &&  iTextureSize > 32)
	{
		iTextureSize = iTextureSize / 2;
	}

	m_pxWaterReflectionTexture = new CRenderTargetTexture(this, iTextureSize, iTextureSize, D3DFMT_A8R8G8B8, 0.5);
	m_pxWorld->GetTerrain()->SetWaterReflectionTexture(m_pxWaterReflectionTexture->GetTexture());
}

//---------------------------------------------------------------------------------------------------------------------
void 
C3DView2::CreateShadowMapTexture()
{
	int iTextureSize = atoi(m_xConfiguration.GetValueString("shadow/shadowmapresolution").c_str());
	m_pxShadowMapTexture = new CRenderTargetTexture(this, iTextureSize, iTextureSize, D3DFMT_A8R8G8B8, 0.5);
	m_hShadowFadeTexture = GetTextureFactory()->CreateTextureFromFile("texture>shadowmask.png");
}

//---------------------------------------------------------------------------------------------------------------------
void
C3DView2::SelectShaderVersion()
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
C3DView2::CreateScene()
{
	AttachToParentWindow();
	SelectShaderVersion();

	if(m_xConfiguration.GetValueBool("application/alwaysontop"))
	{
		::SetWindowPos(GetWindowHandle(), HWND_TOPMOST, 0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE | SWP_FRAMECHANGED);
	}
	else
	{
		::SetWindowPos(GetWindowHandle(), HWND_TOP, 0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE | SWP_FRAMECHANGED);
	}

	SetFilesystemMapping();
	
    SoundLib::CSoundSystem::Init(GetWindowHandle());
	SoundLib::CSoundSystem::Get().SetSoundEnabled(m_xConfiguration.GetValueBool("sound/soundenabled"));
	SoundLib::CWaveFactory::Get().AddSearchPath(GetFileLocator()->GetPath("sounds>").c_str());

    GetXFileLoader()->LoadFXMapping(ms_sBasePath + "../data/shaders/fxselect.xml");
    TEffectHandle hndConstantsEffect = 
		GetEffectFactory()->CreateEffect(ms_sBasePath + "../data/shaders/constants.fx");
    GetEffectFactory()->SetSharedVarsEffect(hndConstantsEffect);

	m_pxObserver					= new CObserver();
	m_pxWorld						= new CWorld(m_xConfiguration.GetValueFloat("general/maxdistancefromterrain"), m_fRangeOfVision);
	m_pxObserverControllerSwitcher	= new CObserverControllerSwitcher(m_pxWorld);


	m_pxInputManager = new CInputManager();
	m_pxInputManager->AddGamePad(GetGamepadDevice(0));
	m_pxInputManager->SetKeyboard(m_pDIDKeyboard);
	m_pxInputManager->SetMouse(m_pDIDMouse);

	SetInputMapping();


	UILib::CBitmapFactory::Get().AddSearchPath(GetFileLocator()->GetPath("ui>").c_str());
	m_pxUIScreenStateMachine		= new CUIScreenStateMachine(this, m_pxInputManager, GetWindowWidth(), GetWindowHeight());

	m_pxUIScreenStateMachine->AddScreen("mainmenu", CMainMenuScreen::Create());
	m_pxUIScreenStateMachine->AddScreen("spectator", CSpectatorScreen::Create());
	m_pxUIScreenStateMachine->AddScreen("leveleditor", CLevelEditorScreen::Create());
	m_pxUIScreenStateMachine->AddScreen("agent", CAgentControlScreen::Create());
	m_pxUIScreenStateMachine->SwitchToScreen("mainmenu");

	m_pxPerformanceWindow = CPerformanceWindow::Create();
	UILib::CWindowMgr::Get().AddTopLevelWindow(m_pxPerformanceWindow->GetWHDL());
	m_pxPerformanceWindow->SetVisible(false);

	m_pxSimTimeCtrl = new CSimTimeCtrl();
	m_pxSimTimeCtrl->SetSimStepDuration(1 / 30.0f);
	m_pxSimTimeCtrl->SetSimCallback(CreateMemberCallback(this, C3DView2::OnSimulationTick));

	m_pxObserverControllerSwitcher->SetCurrentObserver(m_pxObserver);

	m_xCamera.SetFarPlaneDistance(100.0f);
	m_xCamera.SetNearPlaneDistance(0.5f); 
	m_xCamera.SetFieldOfViewHeight(55.0f / 180.0f * PIf);

	m_pxCommunicationModule = new CCommunicationModule(m_pxWorld);

	TakeStartUpActions();


	CreateWaterReflectionTexture();
	if(m_bShadowsEnabled)
	{
		CreateShadowMapTexture();
		CSplatChunkManager::GetInstance()->SetRenderShadows(true);
	}
	UpdateLightDirection();

	m_bIsRunning = true;
}

//---------------------------------------------------------------------------------------------------------------------
void
C3DView2::SetInputMapping()
{
	// general

	m_pxInputManager->Map("keyboard:escape.down",	"exit");

	// level editor

	m_pxInputManager->Map("keyboard:delete.down",	"deleteobject");

	// walk-modus

	m_pxInputManager->Map("keyboard:shift.held",	"walk_run");
	m_pxInputManager->Map("gamepad0:button7.held",	"walk_run");

	m_pxInputManager->Map("keyboard:a.held",		"walk_strafeleft");
	m_pxInputManager->Map("keyboard:d.held",		"walk_straferight");
	m_pxInputManager->Map("keyboard:w.held",		"walk_forward");
	m_pxInputManager->Map("keyboard:s.held",		"walk_backward");
	m_pxInputManager->Map("keyboard:e.held",		"walk_jump");
	m_pxInputManager->Map("keyboard:q.held",		"walk_duck");

	m_pxInputManager->Map("keyboard:numpad1.held",	"walk_strafeleft");
	m_pxInputManager->Map("keyboard:numpad3.held",	"walk_straferight");
	m_pxInputManager->Map("keyboard:numpad5.held",	"walk_forward");
	m_pxInputManager->Map("keyboard:numpad2.held",	"walk_backward");
	m_pxInputManager->Map("keyboard:numpad6.held",	"walk_jump");
	m_pxInputManager->Map("keyboard:numpad4.held",	"walk_duck");

	m_pxInputManager->Map("keyboard:left.held",		"walk_strafeleft");
	m_pxInputManager->Map("keyboard:right.held",	"walk_straferight");
	m_pxInputManager->Map("keyboard:up.held",		"walk_forward");
	m_pxInputManager->Map("keyboard:down.held",		"walk_backward");
	m_pxInputManager->Map("keyboard:space.held",	"walk_jump");
	m_pxInputManager->Map("keyboard:return.held",	"walk_duck");

	m_pxInputManager->Map("mouse:leftbutton.held  &&  mouse:x.value!=0",	"walk_mouseturnleftright");
	m_pxInputManager->Map("mouse:leftbutton.held  &&  mouse:y.value!=0",	"walk_mouseturnupdown");

	m_pxInputManager->Map("gamepad0:axis0.value<-0.2",	"walk_strafeleft");
	m_pxInputManager->Map("gamepad0:axis0.value>0.2",	"walk_straferight");
	m_pxInputManager->Map("gamepad0:axis1.value<-0.2",	"walk_forward");
	m_pxInputManager->Map("gamepad0:axis1.value>0.2",	"walk_backward");
	m_pxInputManager->Map("gamepad0:button0.down",		"walk_jump");
	m_pxInputManager->Map("gamepad0:button1.down",		"walk_duck");
	m_pxInputManager->Map("gamepad0:button6.held  &&  gamepad0:axis0.value<-0.2",	"walk_turnleft");
	m_pxInputManager->Map("gamepad0:button6.held  &&  gamepad0:axis0.value>0.2",	"walk_turnright");
	m_pxInputManager->Map("gamepad0:button6.held  &&  gamepad0:axis1.value>-0.2",	"walk_turnup");
	m_pxInputManager->Map("gamepad0:button6.held  &&  gamepad0:axis1.value<0.2",	"walk_turndown");


	// -- Spectator Modes --

	m_pxInputManager->Map("keyboard:f1.down  &&  keyboard:control.notheld",	"spectator_freelook");
	m_pxInputManager->Map("keyboard:f2.down  &&  keyboard:control.notheld",	"spectator_walk");
	m_pxInputManager->Map("keyboard:f3.down  &&  keyboard:control.notheld",	"spectator_helicopter");


	// -- Debug Keys --

	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:f1.down",	"debug_performancewindow");
	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:f2.down",	"debug_wireframe");
	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:f3.down",	"debug_terrainnormal");
	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:f4.down",	"debug_terrainbasepassonly");	
	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:f5.down",	"debug_toggleobjectrendering");
	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:f6.down",	"debug_toggleterrainrendering");
	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:f7.down",	"debug_togglewaterrendering");
	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:f8.down",	"debug_toggleshadowrendering");

	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:g.down",		"debug_showwaterreflectiontexture");
	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:h.down",		"debug_showshadowmap");

	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:1.down",		"debug_terrainblendmap0");
	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:2.down",		"debug_terrainblendmap1");	
	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:2.down",		"debug_terrainblendmap2");
	m_pxInputManager->Map("keyboard:control.held  &&  keyboard:3.down",		"debug_terrainblendmap3");

	m_pxInputManager->Map("keyboard:alt.held  &&  mouse:x.value!=0",		"debug_mousemovelightleftright");
	m_pxInputManager->Map("keyboard:alt.held  &&  mouse:y.value!=0",		"debug_mousemovelightupdown");
}

//---------------------------------------------------------------------------------------------------------------------
void
C3DView2::SetFilesystemMapping()
{
	GetFileLocator()->SetAlias("texture", ms_sBasePath + "../data/textures", TextureLoadCallBack, (void*) &m_iDefaultTextureResolution );
	GetFileLocator()->SetAlias("xfl-texture", ms_sBasePath + "../data/textures", TextureLoadCallBack, (void*) &m_iDefaultTextureResolution);
	GetFileLocator()->SetAlias("prt-texture", ms_sBasePath + "../data/textures", TextureLoadCallBack, (void*) &m_iDefaultTextureResolution);
	GetFileLocator()->SetAlias("xfl-shader", ms_sBasePath + "../data/shaders", ShaderLoadCallBack, (void*) &m_sShaderVersion);
	GetFileLocator()->SetAlias("prt-shader", ms_sBasePath + "../data/shaders", ShaderLoadCallBack, (void*) &m_sShaderVersion);
	GetFileLocator()->SetAlias("shader", ms_sBasePath + "../data/shaders", ShaderLoadCallBack, (void*) &m_sShaderVersion);
    GetFileLocator()->SetAlias("model", ms_sBasePath + "../data/models");
    GetFileLocator()->SetAlias("animation", ms_sBasePath + "../data/anims");
    GetFileLocator()->SetAlias("terrain", ms_sBasePath + "../data/terrain");
    GetFileLocator()->SetAlias("ui", ms_sBasePath + "../data/ui");
    GetFileLocator()->SetAlias("visualizations", ms_sBasePath + "../data/visualizations");
    GetFileLocator()->SetAlias("offlineworlds", ms_sBasePath + "../data/offlineworlds");
    GetFileLocator()->SetAlias("onlineworlds", ms_sBasePath + "../data/onlineworlds");
    GetFileLocator()->SetAlias("sounds", ms_sBasePath + "../data/sounds");
}

//---------------------------------------------------------------------------------------------------------------------
void 
C3DView2::OnIdle()
{
	SoundLib::CSoundSystem::Get().Tick();
	UpdateFPS();
	m_pxSimTimeCtrl->ElapseTime(m_dDurationOfLastFrame);
	Render();
}
//---------------------------------------------------------------------------------------------------------------------
void 
C3DView2::Terminate()
{
	delete m_pxWaterReflectionTexture;
	delete m_pxShadowMapTexture;
	m_hShadowFadeTexture.Release();

	delete m_pxUIScreenStateMachine;
	delete m_pxWorld;
	delete m_pxObserverControllerSwitcher;
	delete m_pxObserver;
	delete m_pxSimTimeCtrl;
	delete m_pxInputManager;
	delete m_pxCommunicationModule;

	CGfxDebugMarker::Shut();
	SoundLib::CSoundSystem::Shut();
    SoundLib::CWaveFactory::Shut();

	m_bIsRunning = false;
}
//---------------------------------------------------------------------------------------------------------------------
bool 
C3DView2::OnWindowMessage(UINT msg, WPARAM wParam, LPARAM lParam)
{
	if(m_pxUIScreenStateMachine)
	{
		m_pxUIScreenStateMachine->HandleWindowMessage(msg, wParam, lParam);
	}
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
void
C3DView2::OnSimulationTick()
{
	m_pxInputManager->UpdateFromDevice((float) m_dDurationOfLastFrame);
	m_pxCommunicationModule->Tick(m_pxSimTimeCtrl->GetDiscreteSimTime());
	m_pxWorld->Tick();
	m_pxUIScreenStateMachine->Tick();
	m_pxObserverControllerSwitcher->Tick(m_pxInputManager);
	UpdateLightDirection();
	if(m_bDebugKeysEnabled)
	{
		CheckDebugKeys();
	}
	m_pxPerformanceWindow->Update();
}

//---------------------------------------------------------------------------------------------------------------------
void 
C3DView2::Render()
{
	SetEngineTime(m_pxSimTimeCtrl->GetContinuousSimTime());

	GetDeviceStateMgr()->Reset();

	// update lod

	m_pxObserver->UpdateCamera(m_xCamera);
	m_xCamera.SetFarPlaneDistance(m_fRangeOfVision);
	m_xCamera.SetNearPlaneDistance(0.5f);
	m_xCamera.SetupMatrices();
	m_xCamera.UpdateViewFrustum();
	m_pxWorld->GetObjectManager()->UpdateLODLevels(m_xCamera.GetViewFrustum());

	if(true)
	{
		RenderWaterReflections();
	}
	if(m_bShadowsEnabled)
	{
		RenderShadowMap();
	}

	// render scene
	GetDeviceStateMgr()->Reset();

	if(m_bWireframe)
	{
		HRESULT hr = GetDevice()->Clear(0, 0, D3DCLEAR_TARGET | D3DCLEAR_ZBUFFER, 0x002040FF, 1.0, 0);
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

		m_pxObserver->UpdateCamera(m_xCamera);

//		DebugPrint("pass sky");
		m_xCamera.SetFarPlaneDistance(100.0f);
		m_xCamera.SetNearPlaneDistance(0.5f);
		SetFog(true);
		RenderSky();

		if(m_bDistancePasses)
		{
	//		DebugPrint("pass 1");
			m_xCamera.SetFarPlaneDistance((float) m_fRangeOfVision);
			m_xCamera.SetNearPlaneDistance(22.3f);

			RenderEverything();
			m_xPerformanceData.m_iFirstPassTerrainTiles = m_pxWorld->GetTerrain()->GetNumberOfCurrentlyVisibleChunks();
			m_xPerformanceData.m_iFirstPassObjects = m_pxWorld->GetObjectManager()->GetNumberOfCurrentlyVisibleObjects();
			hr = GetDevice()->Clear(0, 0, D3DCLEAR_ZBUFFER, 0x002040FF, 1.0, 0);
			assert(SUCCEEDED(hr));

			SetDefaultSamplerState();
			SetDefaultRenderState();
			
	//		DebugPrint("pass 2");
			m_xCamera.SetFarPlaneDistance(22.3f);
			m_xCamera.SetNearPlaneDistance(0.5f);
			GetDeviceStateMgr()->Reset();
			RenderEverything();
			m_xPerformanceData.m_iSecondPassTerrainTiles = m_pxWorld->GetTerrain()->GetNumberOfCurrentlyVisibleChunks();
			m_xPerformanceData.m_iSecondPassObjects = m_pxWorld->GetObjectManager()->GetNumberOfCurrentlyVisibleObjects();
		}
		else
		{
	//		DebugPrint("pass 1");
			m_xCamera.SetFarPlaneDistance((float) m_fRangeOfVision);
			m_xCamera.SetNearPlaneDistance(0.5f);

			RenderEverything();
			m_xPerformanceData.m_iFirstPassTerrainTiles = m_pxWorld->GetTerrain()->GetNumberOfCurrentlyVisibleChunks();
			m_xPerformanceData.m_iFirstPassObjects = m_pxWorld->GetObjectManager()->GetNumberOfCurrentlyVisibleObjects();
			m_xPerformanceData.m_iSecondPassTerrainTiles = 0;
			m_xPerformanceData.m_iSecondPassObjects = 0;
		}

		// pass windowsystem 

		if(m_bShowReflectionTexture)
		{
			m_pxWaterReflectionTexture->DrawSmall(CRenderTargetTexture::CH_COLOR);
		}
		else if(m_bShowShadowMap)
		{
			m_pxShadowMapTexture->DrawSmall(CRenderTargetTexture::CH_ALPHA);
		}

		GetDeviceStateMgr()->SetRenderState(D3DRS_FILLMODE, D3DFILL_SOLID);
		SetFog(false);
		m_pxUIScreenStateMachine->Render();

		GetDevice()->EndScene();
		
		hr = GetDevice()->Present(0, 0, 0, 0);
		assert(SUCCEEDED(hr));
    }
}


//---------------------------------------------------------------------------------------------------------------------
void
C3DView2::SetFog(bool p_bEnabled)
{
	if((GetDeviceCaps()->RasterCaps & (D3DPRASTERCAPS_WFOG | D3DPRASTERCAPS_ZFOG)) == 0)
	{
		return;
	}
	if((GetDeviceCaps()->RasterCaps & D3DPRASTERCAPS_FOGTABLE) == 0)
	{
		return;
	}

	if(p_bEnabled)
	{
		if(m_pxObserver->IsUnderWater())
		{
			SetFog(0x002E94FF, 0.0f, 30.0f);
			return;
		}
		else if(m_xCamera.GetFarPlaneDistance() >= 100.0f)
		{
			SetFog(0x00CCD9EB, 100.0f, (float) m_fRangeOfVision);
			return;
		}
	}

	GetDeviceStateMgr()->SetRenderState(D3DRS_FOGENABLE, FALSE);
}

//---------------------------------------------------------------------------------------------------------------------
void
C3DView2::RenderEverything()
{
	m_xCamera.SetupMatrices();
	m_xCamera.UpdateViewFrustum();

	// fog needs dx-projection matrix, even though we do not use T&L
	CMat4S m = m_xCamera.GetProjectionMatrix();
	GetDeviceStateMgr()->SetTransform(D3DTS_PROJECTION, (const D3DMATRIX*) &m);
	SetFog(true);

    TRenderContextPtr spxRenderContext;
    spxRenderContext.Create();
	spxRenderContext->m_fTime = (float) GetSimTimeCtrl()->GetContinuousSimTime();
    spxRenderContext->m_fDeltaTime = (float) GetSimTimeCtrl()->GetContinuousSimTimeDelta();

	spxRenderContext->m_vLightDir = m_vLightDirection;

    CMat4S matWorldTransform;
	matWorldTransform = CMat4S::CalcRotationMatrix(CAxisAngle(CVec3::vYAxis, 0.7f));

    m_xCamera.SetupRenderContext(spxRenderContext);

	m_pxWorld->GetObjectManager()->SetMaxVisibilityDistance(m_fMaxObjDistance);
	CSplatChunkManager::GetInstance()->SetRenderShadows(m_bShadowsEnabled && m_bShadowCastersVisible);
	m_pxWorld->Render(spxRenderContext, matWorldTransform);
}
//---------------------------------------------------------------------------------------------------------------------
void
C3DView2::RenderSky()
{
	m_xCamera.SetupMatrices();
	m_xCamera.UpdateViewFrustum();

    TRenderContextPtr spxRenderContext;
    spxRenderContext.Create();
    spxRenderContext->m_fDeltaTime = 1.0f / 50.0f;

    m_xCamera.SetupRenderContext(spxRenderContext);

	m_pxWorld->RenderSkybox(spxRenderContext);
}

//---------------------------------------------------------------------------------------------------------------------
void
C3DView2::RenderWaterReflections()
{
	// switch render target to texture

	m_pxWaterReflectionTexture->Push();

	// flip camera on mirror surface

	m_pxObserver->UpdateCamera(m_xCamera);
	
	CVec3 vPos = m_xCamera.GetPos();
	CVec3 vUp = m_xCamera.GetUpVec();
	CVec3 vOrient = m_xCamera.GetOrientation();
	CVec3 vRight = m_xCamera.GetRightVec();

	vPos.y() *= -1.0f;
	vOrient.y() *= -1.0f;
	vUp.x() *= -1.0f;
	vUp.z() *= -1.0f;
		
	m_xCamera.SetPos(vPos);
	m_xCamera.SetOrientation(vOrient);
	m_xCamera.SetUpVec(vUp);

	m_xCamera.SetFarPlaneDistance((float) m_fRangeOfVision);
	m_xCamera.SetNearPlaneDistance(0.1f);

	m_xCamera.SetupMatrices();
	m_xCamera.UpdateViewFrustum();

	if (SUCCEEDED(GetDevice()->BeginScene()))
	{
		HRESULT hr = GetDevice()->Clear(0, 0, D3DCLEAR_TARGET | D3DCLEAR_ZBUFFER, 0x00FF8000, 1.0, 0);

		// render

		TRenderContextPtr spxRenderContext;
		spxRenderContext.Create();
		spxRenderContext->m_fDeltaTime = 1.0f / 50.0f;

        m_xCamera.SetupRenderContext(spxRenderContext);
		m_pxWorld->RenderSkybox(spxRenderContext);

		// set user clip planes
        CMat4S mViewProjectionInvT = m_xCamera.GetViewProjectionInverseMatrix().GetTransposed();

		D3DXVECTOR4 clipPlane(0.0f, 1.0f, 0.0f, 0.0f);
		D3DXVECTOR4 projClipPlane;

		// transform clip plane into projection space
		D3DXVec4Transform(&projClipPlane, &clipPlane, (D3DXMATRIX*) &mViewProjectionInvT);

		GetDeviceStateMgr()->SetClipPlane(0, (const float*)&projClipPlane);
		GetDeviceStateMgr()->SetRenderState(D3DRS_CULLMODE, D3DCULL_NONE);
		GetDeviceStateMgr()->SetRenderState(D3DRS_CLIPPLANEENABLE, D3DCLIPPLANE0);

		// tweak terrain parameters for better performance - lowest geometry lod, lowest texture detail and no water
		bool bBaseTextureOnly = CSplatChunkManager::GetInstance()->GetConfig().m_bBasePassOnly;
		CSplatChunkManager::GetInstance()->SetRenderBasePassOnly(true);
		CSplatChunkManager::GetInstance()->SetRenderLowestGeometryLODOnly(true);
		CSplatChunkManager::GetInstance()->SetRenderSkirts(false);
		CSplatChunkManager::GetInstance()->SetRenderShadows(m_bShadowCastersVisible && m_bShadowsEnabled && m_bWaterReflectsShadows);
		CSplatChunkManager::GetInstance()->SetDoubleSidedRendering(true);

		// render terrain
		m_pxWorld->GetTerrain()->RenderTerrain(spxRenderContext);

		// switch terrain parameters back to normal
		CSplatChunkManager::GetInstance()->SetRenderShadows(m_bShadowsEnabled && m_bShadowCastersVisible);
		CSplatChunkManager::GetInstance()->SetRenderBasePassOnly(bBaseTextureOnly);
		CSplatChunkManager::GetInstance()->SetRenderLowestGeometryLODOnly(false);
		CSplatChunkManager::GetInstance()->SetRenderSkirts(true);
		CSplatChunkManager::GetInstance()->SetDoubleSidedRendering(false);

		// render world objects
		if(m_fMaxObjReflectionDistance > 0)
		{
			m_pxWorld->GetObjectManager()->SetMaxVisibilityDistance(m_fMaxObjReflectionDistance);
			m_pxWorld->GetObjectManager()->Render(spxRenderContext);
			m_xPerformanceData.m_iWaterReflectionObjects = m_pxWorld->GetObjectManager()->GetNumberOfCurrentlyVisibleObjects();
		}
		else
		{
			m_xPerformanceData.m_iWaterReflectionObjects = m_pxWorld->GetObjectManager()->GetNumberOfCurrentlyVisibleObjects();
		}

		// disable clip planes
		GetDeviceStateMgr()->SetRenderState(D3DRS_CLIPPLANEENABLE, 0);
		GetDeviceStateMgr()->SetRenderState(D3DRS_CULLMODE, D3DCULL_CW);

		GetDevice()->EndScene();
	}

	m_pxWaterReflectionTexture->Pop();
}


//---------------------------------------------------------------------------------------------------------------------
void
C3DView2::RenderShadowMap()
{
/*
	float fRadius = 50.0f;
	float fDistance = 50.0f;
	CVec3 vLightDir	= m_vLightDirection;
	CVec3 vLightSphereCenter = m_xCamera.GetPos() + (m_xCamera.GetOrientation() * fDistance);
	CVec3 vLightPos = vLightSphereCenter - (vLightDir * fRadius);
	
    CVec3 vLightUp = CVec3(0, 1, 0) ^ vLightDir;
    if (vLightUp.Abs() < 0.5f)
	{
        vLightUp = CVec3(1, 0, 0) ^ vLightDir;
	}

	CCamera xCamera;
	xCamera.SetUpVec(vLightUp);
	xCamera.SetOrientation(vLightDir);
	xCamera.SetPos(vLightPos);
*/
	m_bShadowCastersVisible = false;
	float fShadowDistance = 50.0f;

	CCamera xCamera = m_xCamera;
	m_pxObserver->UpdateCamera(xCamera);
	// xCamera.SetPos(xCamera.GetPos() - (xCamera.GetOrientation().GetNormalized() * 10.0f));
    
	xCamera.SetFarPlaneDistance(fShadowDistance);
	xCamera.SetNearPlaneDistance(0.01f);

	xCamera.SetupMatrices();
	xCamera.UpdateViewFrustum();

	// objekte sammeln

	vector<CWorldObject*> apxPotentialShadowCastingObjects;
	m_pxWorld->GetObjectManager()->CollectVisibleItems(xCamera.GetViewFrustum(), apxPotentialShadowCastingObjects);

	if(apxPotentialShadowCastingObjects.size() == 0)
	{
		return;		// lucky :)
	}

	// sphere ermitteln

	vector<CWorldObject*> apxShadowCastingObjects;
	CViewFrustum xFrustum = xCamera.GetViewFrustum();
	CBoundingSphere xShadowSphere;
	xShadowSphere.Clear();
	for(int i=0; i < (int) apxPotentialShadowCastingObjects.size(); ++i)
	{
		if(apxPotentialShadowCastingObjects[i]->GetVisible())
		{
			if(xFrustum.SphereIntersects(apxPotentialShadowCastingObjects[i]->GetBoundingSphere()))
			{
				xShadowSphere.AddSphere(apxPotentialShadowCastingObjects[i]->GetBoundingSphere());
				apxShadowCastingObjects.push_back(apxPotentialShadowCastingObjects[i]);
			}
		}
	}


	if(xShadowSphere.m_fRadius == 0.0f)
	{
		return; // lucky again :)
	}
	
	m_bShadowCastersVisible = true;

	// Lichtcamera auf diese Sphere einstellen

	CVec3 vLightDir	= m_vLightDirection;
	CVec3 vLightPos = xShadowSphere.m_vCenter - (vLightDir * (xShadowSphere.m_fRadius + 1.0f));
	
    CVec3 vLightUp = CVec3(0, 1, 0) ^ vLightDir;
    if (vLightUp.Abs() < 0.5f)
	{
        vLightUp = CVec3(1, 0, 0) ^ vLightDir;
	}

	xCamera.SetUpVec(vLightUp);
	xCamera.SetOrientation(vLightDir);
	xCamera.SetPos(vLightPos);

	xCamera.SetFarPlaneDistance(xShadowSphere.m_fRadius * 2.0f + 1.0f);
	xCamera.SetNearPlaneDistance(0.1f);
    xCamera.SetFieldOfView(xShadowSphere.m_fRadius, 1.0f, false);

	xCamera.SetupMatrices();
	xCamera.UpdateViewFrustum();

	// Transformation ermitteln

	CMat4S matWorld2ShadowTransform = 
        xCamera.GetViewProjectionMatrix() *
        CMat4S( +0.5f,  0,      0,  0,
                0,      -0.5f,  0,  0,
                0,      0,      1,  0,
                0.5f,   0.5f,   0,  1);

	// Rendern

	m_pxShadowMapTexture->Push();

    TEffectHandle hndConstantsEffect = GetEffectFactory()->GetSharedVarsEffect();
	hndConstantsEffect->GetD3DXEffect()->SetTexture("c_tShadowMap", m_pxShadowMapTexture->GetTexture().GetPtr());
	hndConstantsEffect->GetD3DXEffect()->SetTexture("c_tShadowFadeTexture", m_hShadowFadeTexture.GetPtr());
    hndConstantsEffect->GetD3DXEffect()->SetMatrix("c_mWorld2Shadow", (D3DXMATRIX*) &matWorld2ShadowTransform);
	hndConstantsEffect->GetD3DXEffect()->SetBool("c_bZWriteZTest", false);

	GetDeviceStateMgr()->SetRenderState(D3DRS_COLORWRITEENABLE, D3DCOLORWRITEENABLE_ALPHA);

	if (SUCCEEDED(GetDevice()->BeginScene()))
	{
		HRESULT hr = GetDevice()->Clear(0, 0, D3DCLEAR_TARGET, 0x00FF8000, 0, 0);

		TRenderContextPtr spxRenderContext;
		spxRenderContext.Create();
		spxRenderContext->m_fDeltaTime = 1.0f / 50.0f;

        xCamera.SetupRenderContext(spxRenderContext);

		for(int i=0; i < (int) apxShadowCastingObjects.size(); ++i)
		{
			apxShadowCastingObjects[i]->Render(spxRenderContext);
		}

		m_xPerformanceData.m_iShadowMapObjects = (int) apxShadowCastingObjects.size();

		GetDevice()->EndScene();
	}

	GetDeviceStateMgr()->SetRenderState(D3DRS_COLORWRITEENABLE, D3DCOLORWRITEENABLE_ALPHA | D3DCOLORWRITEENABLE_BLUE | D3DCOLORWRITEENABLE_GREEN | D3DCOLORWRITEENABLE_RED);
	hndConstantsEffect->GetD3DXEffect()->SetBool("c_bZWriteZTest", true);

	m_pxShadowMapTexture->Pop();
}

//---------------------------------------------------------------------------------------------------------------------
void
C3DView2::SetDefaultSamplerState()
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
C3DView2::SetDefaultRenderState()
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
C3DView2::AttachToParentWindow()
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
C3DView2::UpdateFPS()
{
    // Duration of last Frame
    m_dDurationOfLastFrame = Utils::GetDeltaSeconds(m_dTimeOfCurrentFrame);

#ifdef _DEBUG
    // wegen Breakpoints
    if (m_dDurationOfLastFrame > 5.0f) m_dDurationOfLastFrame = 0.1;
#else
    // um Stalls zu verhindern -> bremst das Gameplay ab
    if (m_dDurationOfLastFrame > 0.5f) m_dDurationOfLastFrame = 0.5f;
#endif

	float fAvgFrameDuration = 1 / m_xPerformanceData.m_fAvgFPS;
    fAvgFrameDuration = (float)(m_dDurationOfLastFrame * 1 / 60 + fAvgFrameDuration * 59 / 60);
    m_xPerformanceData.m_fAvgFPS = 1.0f / fAvgFrameDuration;
}
//---------------------------------------------------------------------------------------------------------------------
void
C3DView2::SetFog(DWORD p_dwColor, float p_fFogStart, float p_fFogEnd)
{
	DWORD Mode =  D3DFOG_LINEAR;

    float Density = 0.66f;   // For exponential modes
 
    // Enable fog blending.
	GetDeviceStateMgr()->SetRenderState(D3DRS_FOGENABLE, TRUE);
 
    // Set the fog color.
    GetDeviceStateMgr()->SetRenderState(D3DRS_FOGCOLOR, p_dwColor);
    
    // Set fog parameters.
    if( Mode == D3DFOG_LINEAR )
    {
        GetDeviceStateMgr()->SetRenderState(D3DRS_FOGTABLEMODE, Mode);
        GetDeviceStateMgr()->SetRenderState(D3DRS_FOGSTART, *(DWORD *)(&p_fFogStart));
        GetDeviceStateMgr()->SetRenderState(D3DRS_FOGEND,   *(DWORD *)(&p_fFogEnd));
    }
    else
    {
        GetDeviceStateMgr()->SetRenderState(D3DRS_FOGTABLEMODE, Mode);
        GetDeviceStateMgr()->SetRenderState(D3DRS_FOGDENSITY, *(DWORD *)(&Density));
    }
}
//---------------------------------------------------------------------------------------------------------------------
void 
C3DView2::CheckDebugKeys()
{
	if(m_pxInputManager->IsFullfilled("debug_performancewindow"))
	{
		m_pxPerformanceWindow->SetVisible(!m_pxPerformanceWindow->GetVisible());
	}
	if(m_pxInputManager->IsFullfilled("debug_wireframe"))
	{
		m_bWireframe = !m_bWireframe;	
	}
	if(m_pxInputManager->IsFullfilled("debug_terrainnormal"))
	{
		CSplatChunkManager::GetInstance()->SetRenderBasePassOnly(false);
		CSplatChunkManager::GetInstance()->SetRenderBlendMaps(-1);
	}
	if(m_pxInputManager->IsFullfilled("debug_terrainbasepassonly"))
	{
		CSplatChunkManager::GetInstance()->SetRenderBasePassOnly(true);
		CSplatChunkManager::GetInstance()->SetRenderBlendMaps(-1);
	}
	if(m_pxInputManager->IsFullfilled("debug_terrainblendmap0"))
	{
		CSplatChunkManager::GetInstance()->SetRenderBasePassOnly(true);
		CSplatChunkManager::GetInstance()->SetRenderBlendMaps(0);
	}
	if(m_pxInputManager->IsFullfilled("debug_terrainblendmap1"))
	{
		CSplatChunkManager::GetInstance()->SetRenderBasePassOnly(true);
		CSplatChunkManager::GetInstance()->SetRenderBlendMaps(1);
	}
	if(m_pxInputManager->IsFullfilled("debug_terrainblendmap2"))
	{
		CSplatChunkManager::GetInstance()->SetRenderBasePassOnly(true);
		CSplatChunkManager::GetInstance()->SetRenderBlendMaps(2);
	}
	if(m_pxInputManager->IsFullfilled("debug_terrainblendmap3"))
	{
		CSplatChunkManager::GetInstance()->SetRenderBasePassOnly(true);
		CSplatChunkManager::GetInstance()->SetRenderBlendMaps(3);
	}
	if(m_pxInputManager->IsFullfilled("debug_toggleobjectrendering"))
	{
		m_pxWorld->SetRenderObjects(!m_pxWorld->GetRenderObjects());
	}
	if(m_pxInputManager->IsFullfilled("debug_toggleterrainrendering"))
	{
		m_pxWorld->SetRenderTerrain(!m_pxWorld->GetRenderTerrain());
	}
	if(m_pxInputManager->IsFullfilled("debug_togglewaterrendering"))
	{
		m_pxWorld->SetRenderWater(!m_pxWorld->GetRenderWater()); 
	}
	if(m_pxInputManager->IsFullfilled("debug_toggleshadowrendering"))
	{
		if(m_pxShadowMapTexture)
		{
			m_bShadowsEnabled = !m_bShadowsEnabled;
		}
	}
	if(m_pxInputManager->IsFullfilled("debug_showwaterreflectiontexture"))
	{
		m_bShowReflectionTexture = !m_bShowReflectionTexture;
	}
	if(m_pxInputManager->IsFullfilled("debug_showshadowmap"))
	{
		m_bShowShadowMap = !m_bShowShadowMap;
	}
}
//---------------------------------------------------------------------------------------------------------------------
void			
C3DView2::UpdateLightDirection()
{
	static CVec3 vVirtualLightPos = CVec3(1.0f, -1.0f, 1.0f);

	if(m_bDebugKeysEnabled)
	{
		if(m_pxInputManager->IsFullfilled("debug_mousemovelightleftright"))
		{
			float fVal = m_pxInputManager->GetAxisValue("debug_mousemovelightleftright");
			vVirtualLightPos.x() = clamp(vVirtualLightPos.x() + (fVal / 50.0f), -2.5f, 2.5f);
		}
		if(m_pxInputManager->IsFullfilled("debug_mousemovelightupdown"))
		{
			float fVal = m_pxInputManager->GetAxisValue("debug_mousemovelightupdown");
			vVirtualLightPos.z() = clamp(vVirtualLightPos.z() + (fVal / 50.0f), -2.5f, 2.5f);
		}
	}

	m_vLightDirection = vVirtualLightPos.GetNormalized();
}

//---------------------------------------------------------------------------------------------------------------------
/**
	looks at the startup options from the configuration file or command line and takes the necessary steps
*/
void 
C3DView2::TakeStartUpActions()
{

	string sStartUpMode			=	m_xConfiguration.GetValueString("startup/mode");
	bool   bConnect				=   m_xConfiguration.GetValueBool("startup/connect");
	string sConnectionMethod	=	m_xConfiguration.GetValueString("startup/connectionmethod");
	string sOfflineWorld		=	m_xConfiguration.GetValueString("startup/offlineworld");
	bool   bCreateFakeAgents	=	m_xConfiguration.GetValueBool("startup/createdemoagents");
	
	// connect of load offline world
	if(bConnect)
	{
		if(sConnectionMethod == "tcp")
		{
			m_pxWorld->LoadFromXML((ms_sBasePath + "../data/offlineworlds/" + sOfflineWorld).c_str());
			GetCommunicationModule()->OpenTCPConnection(m_xConfiguration.GetValueString("networking/worldserver"), 
														m_xConfiguration.GetValueInt("networking/tcpworldserverport"));
		}
		else
		{
			m_pxWorld->LoadFromXML((ms_sBasePath + "../data/offlineworlds/" + sOfflineWorld).c_str());
			GetCommunicationModule()->OpenHTTPConnection(m_xConfiguration.GetValueString("networking/worldserver"), 
													 	 m_xConfiguration.GetValueInt("networking/httpport"),
														 m_xConfiguration.GetValueString("networking/consoleserviceurl"),
														 m_xConfiguration.GetValueString("networking/avatarserviceurl"));
		}
	}
	else
	{
		m_pxWorld->LoadFromXML((ms_sBasePath + "../data/offlineworlds/" + sOfflineWorld).c_str());
	}

	// switch to right mode
	if(sStartUpMode == "spectator") 
	{
		m_pxUIScreenStateMachine->SwitchToScreen("spectator");
	}
	else if(sStartUpMode == "agent"  &&  bConnect)
	{
		m_pxUIScreenStateMachine->SwitchToScreen("agent");
	}
	else if(sStartUpMode == "editor")
	{
		m_pxUIScreenStateMachine->SwitchToScreen("leveleditor");
	}
	m_pxWorld->ResetObserver(m_pxObserver);

	// create fake agents if wanted
	if(bCreateFakeAgents)
	{
		m_pxWorld->CreateFakeAgents();
	}
}
//---------------------------------------------------------------------------------------------------------------------

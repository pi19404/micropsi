#include "stdafx.h"

#include "e42/AppSettings.h"

#define DIRECTINPUT_VERSION 0x0800   // FIXME

#include <d3d9.h>
#include <d3dx9tex.h>
#include <dinput.h>

#include "baselib/utils.h"

//-------------------------------------------------------------------------------------------------------------------------------------------
CAppSettings::CAppSettings()
{
	Init();
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CAppSettings::~CAppSettings()
{
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CAppSettings::Init()
{
    m_bUseSDKVersion90b = false;

	m_bMultiHeadDebugFake = false;
	m_bPerfHudMode = false;

    m_iWindowWidth = 640;
    m_iWindowHeight = 480;
    m_bFullScreen = false;
    m_bAutoDepthStencil = true;
    m_bTripleBuffering = false;
    m_bVSyncEnable = true;
    m_dwMaxMultiSampleQuality = 1;
	m_bUseMultipleHeads = false;
	m_DepthStencilFormat = D3DFMT_D16;


    // default Direct3D-Settings
    m_D3DAdapter = D3DADAPTER_DEFAULT;
    m_D3DDeviceType = D3DDEVTYPE_HAL;
    m_D3DBehaviorFlags = D3DCREATE_HARDWARE_VERTEXPROCESSING | D3DCREATE_PUREDEVICE;


    // default DirectInput-Settings
    m_uiMouseCoopFlags =    DISCL_NONEXCLUSIVE | DISCL_FOREGROUND;
    m_uiKeyboardCoopFlags = DISCL_NONEXCLUSIVE | DISCL_FOREGROUND;
    m_uiGamepadCoopFlags =  DISCL_NONEXCLUSIVE | DISCL_FOREGROUND;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CAppSettings::InitByCommandLine(const std::string& sCommandLine)
{
	Utils::GetCommandLineParameterBool(sCommandLine, "-fullscreen", &m_bFullScreen);
	Utils::GetCommandLineParameterBool(sCommandLine, "-perfhud", &m_bPerfHudMode);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CAppSettings::InitFromDialog()
{

}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CAppSettings::SetPresentParameters(D3DPRESENT_PARAMETERS* pxPresentParametersOut)
{
	pxPresentParametersOut->BackBufferWidth = m_iWindowWidth;
	pxPresentParametersOut->BackBufferHeight = m_iWindowHeight;
	pxPresentParametersOut->BackBufferFormat = m_bFullScreen ? D3DFMT_X8R8G8B8 : D3DFMT_UNKNOWN;
	pxPresentParametersOut->BackBufferCount = m_bTripleBuffering ? 2 : 1;
	pxPresentParametersOut->MultiSampleType = D3DMULTISAMPLE_NONE;
	pxPresentParametersOut->MultiSampleQuality = 0;
	pxPresentParametersOut->SwapEffect = D3DSWAPEFFECT_DISCARD;
	pxPresentParametersOut->Windowed = m_bFullScreen ? FALSE : TRUE;
	pxPresentParametersOut->EnableAutoDepthStencil = m_bAutoDepthStencil ? TRUE : FALSE;
	pxPresentParametersOut->AutoDepthStencilFormat = m_DepthStencilFormat;
	pxPresentParametersOut->Flags = m_bAutoDepthStencil ? D3DPRESENTFLAG_DISCARD_DEPTHSTENCIL : 0;
	pxPresentParametersOut->FullScreen_RefreshRateInHz = D3DPRESENT_RATE_DEFAULT;
	pxPresentParametersOut->PresentationInterval = m_bVSyncEnable ? D3DPRESENT_INTERVAL_ONE : D3DPRESENT_INTERVAL_IMMEDIATE;
}
//-------------------------------------------------------------------------------------------------------------------------------------------

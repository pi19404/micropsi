#include "stdafx.h"

#include "e42/E42Application.h"
#include "baselib/comobjectptr.h"
#include "baselib/filelocator.h"
#include "baselib/Utils.h"

#define DIRECTINPUT_VERSION 0x0800   // FIXME

#include <math.h>
#include <d3d9.h>
#include <d3dx9tex.h>
#include <dinput.h>
#include <dxerr9.h>

using std::string;
//-------------------------------------------------------------------------------------------------------------------------------------------
CE42Application::CE42Application(HINSTANCE hInstance)
:   m_hInstance                         (hInstance),

    m_bShutDownRequested                (false),
    m_bRenderEnable                     (false),

    m_pDI                               (NULL),
    m_pDIDMouse                         (NULL),
    m_pDIDKeyboard                      (NULL),

	m_iScreenshotIdx					(0)
{
    assert(ms_pE42Application == 0);
    ms_pE42Application = this;

	DetermineAspectRatios();


    // default WndClass-Settings
    ZeroMemory(&m_WindowClass, sizeof(WNDCLASSEX));
    m_WindowClass.cbClsExtra = 0;
    m_WindowClass.cbSize = sizeof(WNDCLASSEX);
    m_WindowClass.cbWndExtra = 0;
    m_WindowClass.hbrBackground = NULL;
    m_WindowClass.hCursor = LoadCursor(NULL, MAKEINTRESOURCE(IDC_ARROW));
    m_WindowClass.hIcon = LoadIcon(NULL, MAKEINTRESOURCE(IDI_APPLICATION));
    m_WindowClass.hIconSm = NULL;
    m_WindowClass.hInstance = m_hInstance;
    m_WindowClass.lpfnWndProc = MessageProcCallback;
    m_WindowClass.lpszClassName = "DX9Framework";
    m_WindowClass.lpszMenuName = NULL;
    m_WindowClass.style = CS_OWNDC;

    m_uiWindowStyle = WS_OVERLAPPEDWINDOW;
}

//-------------------------------------------------------------------------------------------------------------------------------------------
CE42Application::~CE42Application() 
{
    assert(ms_pE42Application);
    ms_pE42Application = 0;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
BOOL
CALLBACK
CE42Application::MonitorEnumProc(HMONITOR hMonitor, HDC hdcMonitor, LPRECT lprcMonitor, LPARAM dwData)
{
	CE42Application* pxApplication = (CE42Application*)dwData;

	pxApplication->m_afDesktopAspect.PushEntry( 
			float(::GetDeviceCaps(hdcMonitor, HORZRES)) /
			float(::GetDeviceCaps(hdcMonitor, VERTRES))); 

	pxApplication->m_afMonitorAspect.PushEntry( 
			float(::GetDeviceCaps(hdcMonitor, HORZSIZE)) /
			float(::GetDeviceCaps(hdcMonitor, VERTSIZE))); 

	DebugPrint("detected monitor (desktop: %i x %i) (size: %ix%i)\n", 
			::GetDeviceCaps(hdcMonitor, HORZRES),
			::GetDeviceCaps(hdcMonitor, VERTRES),
			::GetDeviceCaps(hdcMonitor, HORZSIZE),
			::GetDeviceCaps(hdcMonitor, VERTSIZE));


	return TRUE;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CE42Application::DetermineAspectRatios()
{
	EnumDisplayMonitors(GetDC(NULL), NULL, MonitorEnumProc, (LPARAM)this);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
float 
CE42Application::GetAspectRatio(int iHead) const
{
	if (!m_xSettings.m_bFullScreen)
	{
		// im Fenstermodus sollte das PixelAspect 1:1 sein
		// daher ist das Fensterverhältnis entscheidend
		return (float)m_xSettings.m_iWindowWidth / m_xSettings.m_iWindowHeight * (m_afDesktopAspect[iHead] / m_afMonitorAspect[iHead]);
	}
	else
	{
		// im Vollbild ist der Aspect des Monitors entscheidend (Auflösung egal)
		return (float)m_afMonitorAspect[iHead];
	}
}
//-------------------------------------------------------------------------------------------------------------------------------------------
string
CE42Application::GetCommandLineValue(const string& sKey, const char* pcCommandLine) const
{
	if (pcCommandLine == 0 || strlen(pcCommandLine) == 0)
	{
		pcCommandLine = GetCommandLine();
	}

	std::string sValue;
	if (Utils::GetCommandLineParameter(pcCommandLine, sKey, &sValue))
	{
		return sValue;
	}
	else
	{
		return "";
	}
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CE42Application::SaveCurrentFrameToDisk(const char* pcFilename, int iHead, bool bOverwriteExisting)
{
    char acFilename[64];
    sprintf(acFilename, "%s%05i.png", pcFilename, m_iScreenshotIdx++);

 	if(!bOverwriteExisting)
 	{
 		// was ist der erste freie Dateiname?
 		while(CFileLocator::FileExists(acFilename))
 		{
		    sprintf(acFilename, "%s%05i.png", pcFilename, m_iScreenshotIdx++);
 		}
 	}

    CComObjectPtr<IDirect3DSurface9> spxBackBuffer;
    GetDevice()->GetBackBuffer(iHead, 0, D3DBACKBUFFER_TYPE_MONO, &spxBackBuffer);
    D3DXSaveSurfaceToFile(acFilename, D3DXIFF_PNG, spxBackBuffer, NULL, NULL);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CE42Application::SetHeadAsRenderTarget(int iHead)
{
	if (m_xSettings.m_bMultiHeadDebugFake)
	{
		iHead = 0;
	}

	HRESULT hr;
	
	CComObjectPtr<IDirect3DSurface9> spxBackBuffer;
	hr = GetDevice()->GetBackBuffer(iHead, 0, D3DBACKBUFFER_TYPE_MONO, &spxBackBuffer);
	assert(SUCCEEDED(hr));

	hr = GetDevice()->SetRenderTarget(0, spxBackBuffer);
	assert(SUCCEEDED(hr));
}
//-------------------------------------------------------------------------------------------------------------------------------------------
bool 
CE42Application::InitWindow()
{
    // Fensterklasse anmelden
    RegisterClassEx(&m_WindowClass);


    // Größe des Fensters berechnen
    RECT rctClientRect;
    rctClientRect.left     = 0;
    rctClientRect.top      = 0;
    rctClientRect.right    = m_xSettings.m_iWindowWidth;
    rctClientRect.bottom   = m_xSettings.m_iWindowHeight;
    AdjustWindowRectEx(&rctClientRect, m_uiWindowStyle, FALSE, WS_EX_APPWINDOW);


    // Fenster anlegen
	m_ahndWindows.SetSize(m_iNumHeads);
	for (int iHead = 0; iHead < m_iNumHeads; iHead++)
	{
		m_ahndWindows[iHead] = CreateWindow( 
			m_WindowClass.lpszClassName, m_WindowClass.lpszClassName, 
			m_uiWindowStyle, 0, 0, 
			rctClientRect.right - rctClientRect.left,
			rctClientRect.bottom - rctClientRect.top,
			GetDesktopWindow(), NULL, m_WindowClass.hInstance, NULL );

		if (m_xSettings.m_bFullScreen)
		{
			SetWindowLong(m_ahndWindows[iHead], GWL_STYLE, WS_POPUP | WS_VISIBLE);
		}
	}

#ifdef _DEBUG
	if (m_xSettings.m_bMultiHeadDebugFake)
	{
		m_ahndWindows.SetSize(2);
		m_ahndWindows[1] = m_ahndWindows[0];
	}
#endif // _DEBUG


    return true;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CE42Application::ShutWindow()
{
	m_ahndWindows.Clear();
    UnregisterClass(m_WindowClass.lpszClassName, m_WindowClass.hInstance);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CE42Application::CheckMultiHeadSupport(IDirect3D9* pD3D)
{
	if (m_xSettings.m_bUseMultipleHeads && !m_xSettings.m_bFullScreen)
	{
		assert(false && "cannot create multihead-device in windowed mode");
		m_xSettings.m_bUseMultipleHeads = false;
	}

	
	// DevCaps auslesen
    D3DCAPS9 xDXDeviceCaps;
    ZeroMemory(&xDXDeviceCaps, sizeof(xDXDeviceCaps));
    pD3D->GetDeviceCaps(m_xSettings.m_D3DAdapter, m_xSettings.m_D3DDeviceType, &xDXDeviceCaps);


	if (m_xSettings.m_bUseMultipleHeads)
	{
		m_iNumHeads = xDXDeviceCaps.NumberOfAdaptersInGroup;
		assert(m_iNumHeads > 0);
	}
	else
	{
		m_iNumHeads = 1;
	}
}
//-------------------------------------------------------------------------------------------------------------------------------------------
bool 
CE42Application::InitDirect3D(IDirect3D9** ppD3DOut)
{
    // IDirect3D anlegen
    IDirect3D9* pD3D = NULL;
    if (m_xSettings.m_bUseSDKVersion90b)
    {
        pD3D = Direct3DCreate9(D3D9b_SDK_VERSION);
    }
    else
    {
        pD3D = Direct3DCreate9(D3D_SDK_VERSION);
    }

    if (!pD3D)
    {
		OnCreateD3DFailed();
	    return false;
    }

	*ppD3DOut = pD3D;

	return true;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CE42Application::ShutDirect3D(IDirect3D9* pD3D)
{
    if (pD3D)
    {
        HRESULT hr = pD3D->Release();
        assert(SUCCEEDED(hr));
    }
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CE42Application::OnCreateD3DFailed() const
{
    MessageBox(NULL, 
		"This program requires DirectX 9.0c\n"
        "You can find a DirectX 9.0c distribution on the CD.",
        "critical error", MB_ICONERROR | MB_OK);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CE42Application::DeterminePresentParameters(IDirect3D9* pD3D)
{
	// DevCaps auslesen
    D3DCAPS9 xDXDeviceCaps;
    ZeroMemory(&xDXDeviceCaps, sizeof(xDXDeviceCaps));
    pD3D->GetDeviceCaps(m_xSettings.m_D3DAdapter, m_xSettings.m_D3DDeviceType, &xDXDeviceCaps);

	
	// default Present Parameters
	m_axD3DPresentParameters.SetSize(m_iNumHeads);
	for (int iHead = 0; iHead < m_iNumHeads; iHead++)
	{
		ZeroMemory(&(m_axD3DPresentParameters[iHead]), sizeof(D3DPRESENT_PARAMETERS));
		m_xSettings.SetPresentParameters(&m_axD3DPresentParameters[iHead]);
		m_axD3DPresentParameters[iHead].hDeviceWindow = m_ahndWindows[iHead];
	}


    // HardwareVertexProcessing ausschalten, falls nicht unterstützt
    if ((xDXDeviceCaps.VertexShaderVersion & 0xffff) == 0 ||
		 GetVertexShaderDebuggingEnabled() || GetEffectDebuggingEnabled())
    {
        m_xSettings.m_D3DBehaviorFlags &= ~D3DCREATE_HARDWARE_VERTEXPROCESSING;
        m_xSettings.m_D3DBehaviorFlags &= ~D3DCREATE_MIXED_VERTEXPROCESSING;
		m_xSettings.m_D3DBehaviorFlags &= ~D3DCREATE_PUREDEVICE;
        m_xSettings.m_D3DBehaviorFlags |= D3DCREATE_SOFTWARE_VERTEXPROCESSING;
    }


    // Fullscreen-AntiAliasing
    if (m_axD3DPresentParameters[0].Windowed == FALSE)
    {
		for (int iMultiSampleType = D3DMULTISAMPLE_NONMASKABLE; iMultiSampleType <= (int)min(D3DMULTISAMPLE_16_SAMPLES, m_xSettings.m_dwMaxMultiSampleQuality); iMultiSampleType++)
		{
			DWORD dwQualityLevels = 0;
			if (SUCCEEDED(pD3D->CheckDeviceMultiSampleType(
									m_xSettings.m_D3DAdapter, m_xSettings.m_D3DDeviceType, 
									m_axD3DPresentParameters[0].BackBufferFormat, 
									m_axD3DPresentParameters[0].Windowed, 
									(D3DMULTISAMPLE_TYPE)iMultiSampleType, &dwQualityLevels)))
			{
				if (dwQualityLevels > 1  &&  m_xSettings.m_dwMaxMultiSampleQuality > 1)
				{
					m_axD3DPresentParameters[0].MultiSampleType = (D3DMULTISAMPLE_TYPE)iMultiSampleType;
					m_axD3DPresentParameters[0].MultiSampleQuality = min(dwQualityLevels - 1, m_xSettings.m_dwMaxMultiSampleQuality);
				}
			}
			else
			{
				break;
			}
		}

		for (int iHead = 1; iHead < m_iNumHeads; iHead++)
		{
            m_axD3DPresentParameters[iHead].MultiSampleType = m_axD3DPresentParameters[0].MultiSampleType;
            m_axD3DPresentParameters[iHead].MultiSampleQuality = m_axD3DPresentParameters[0].MultiSampleQuality;
		}
	}


	// MultiHead-Kram
	if (m_iNumHeads > 1)
	{
		m_xSettings.m_D3DBehaviorFlags |= D3DCREATE_ADAPTERGROUP_DEVICE;
	}
}
//-------------------------------------------------------------------------------------------------------------------------------------------
bool 
CE42Application::InitDirect3DDevice(IDirect3D9* pD3D, IDirect3DDevice9** ppd3dDeviceOut)
{
    assert(m_ahndWindows[0]);

	DeterminePresentParameters(pD3D);

    // nVidia-PerfHUD-Settings
	if (m_xSettings.m_bPerfHudMode)
	{
		m_xSettings.m_D3DAdapter = pD3D->GetAdapterCount() - 1;
		m_xSettings.m_D3DDeviceType = D3DDEVTYPE_REF;
	}


    // Direct3DDevice anlegen
    IDirect3DDevice9* pd3dDevice = NULL;

    HRESULT hr = 
        pD3D->CreateDevice(
            m_xSettings.m_D3DAdapter, m_xSettings.m_D3DDeviceType, 
			m_ahndWindows[0], m_xSettings.m_D3DBehaviorFlags, 
			m_axD3DPresentParameters.GetArrayPointer(), &pd3dDevice);


    if (FAILED(hr))
    {
		if (m_xSettings.m_bFullScreen)
		{
			ShowCursor(true);
			ShowWindow(m_ahndWindows[0], SW_HIDE);
		}

		OnCreateDeviceFailed(hr);
        return false;
    }


    // Multisampling einschalten
    if (m_axD3DPresentParameters[0].MultiSampleType != D3DMULTISAMPLE_NONE)
    {
        pd3dDevice->SetRenderState(D3DRS_MULTISAMPLEANTIALIAS, TRUE);
    }


	// SwapChains-Array anlegen
	m_aspxSwapChains.SetSize(m_iNumHeads);
	for (int iHead = 0; iHead < m_iNumHeads; iHead++)
	{
		pd3dDevice->GetSwapChain(iHead, &(m_aspxSwapChains[iHead]));
	}


	#ifdef _DEBUG
	// SwapChains-Array anlegen (debug-fake: tut so als gäbe es zwei swapchains, liefert aber jeweils dieselbe zurück)
	if (m_iNumHeads == 1 &&
		m_xSettings.m_bMultiHeadDebugFake)
	{
		m_iNumHeads = 2;
		m_aspxSwapChains.SetSize(m_iNumHeads);
		for (int iHead = 0; iHead < m_iNumHeads; iHead++)
		{
			pd3dDevice->GetSwapChain(0, &(m_aspxSwapChains[iHead]));
		}
	}
	#endif


    // Schnittstellen zurückgeben
	*ppd3dDeviceOut = pd3dDevice;

    return true;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CE42Application::ShutDirect3DDevice(IDirect3DDevice9* pd3dDevice)
{
    if (pd3dDevice)   
    {
        HRESULT hr = pd3dDevice->Release();      
        assert(SUCCEEDED(hr));   
    }

	m_axD3DPresentParameters.Clear();
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CE42Application::OnCreateDeviceFailed(HRESULT hr) const
{
	char acErrorMessage[2048];
	sprintf(acErrorMessage, "%s%s", "Direct3D-CreateDevice failed !\n hardware accelleration needed! \n", DXGetErrorString9(hr));

    MessageBox(NULL, 
        acErrorMessage,
        "critical error", MB_ICONERROR | MB_OK);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
BOOL CALLBACK 
E42Application_DIEnumDevicesCallback(LPCDIDEVICEINSTANCEA lpddi, LPVOID pvRef)
{
	// den devtype abzufragen scheint nicht so richtig zu funktionieren; mein Gamepad
	// hat jedenfalls einen "Unknown Type" laut Auskunft vom DirectX Caps Viewer

	CDynArray<GUID>& raxGamepadGUIDs = *((CDynArray<GUID>*)pvRef);

	raxGamepadGUIDs.PushEntry(lpddi->guidInstance);

	return DIENUM_CONTINUE;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
bool 
CE42Application::InitDirectInput()
{
	assert(m_ahndWindows.Size() > 0 && m_ahndWindows[0]);

    HRESULT hr;
    
    // IDirectInput8-Schnittstelle anlegen
    hr = DirectInput8Create(
        m_hInstance, DIRECTINPUT_VERSION, 
        IID_IDirectInput8, (VOID**)&m_pDI, NULL);

    if (FAILED(hr))
    {
        assert(false);
        MessageBox(NULL, 
            "DirectInput8Create failed !\n"
            "this program needs DirectX 9", 
            "critical error", MB_ICONERROR | MB_OK);
	    return false;
    }


    // MausDevice anlegen
    if (SUCCEEDED(m_pDI->CreateDevice(GUID_SysMouse, &m_pDIDMouse, NULL)))
    {
        if (FAILED(m_pDIDMouse->SetDataFormat(&c_dfDIMouse2)))											{assert(false); return false;}
        if (FAILED(m_pDIDMouse->SetCooperativeLevel(m_ahndWindows[0], m_xSettings.m_uiMouseCoopFlags)))	{assert(false); return false;}
    }


    // Keyboard
    if (SUCCEEDED(m_pDI->CreateDevice(GUID_SysKeyboard, &m_pDIDKeyboard, NULL)))
    {
        if (FAILED(m_pDIDKeyboard->SetDataFormat(&c_dfDIKeyboard)))												{assert(false); return false;}
        if (FAILED(m_pDIDKeyboard->SetCooperativeLevel(m_ahndWindows[0], m_xSettings.m_uiKeyboardCoopFlags)))	{assert(false); return false;}
    }


    // Gamepad-Device anlegen
	CDynArray<GUID> axGamepadGUIDs;
	m_pDI->EnumDevices(DI8DEVCLASS_GAMECTRL, E42Application_DIEnumDevicesCallback, &axGamepadGUIDs, DIEDFL_ATTACHEDONLY);

	m_aspxDIDGamepads.SetSize(axGamepadGUIDs.Size());
	for (unsigned int iGamepadIdx = 0; iGamepadIdx < axGamepadGUIDs.Size(); iGamepadIdx++)
	{
		CComObjectPtr<IDirectInputDevice8A> spxGamepad;

		if (SUCCEEDED(m_pDI->CreateDevice(axGamepadGUIDs[iGamepadIdx], &spxGamepad, NULL)))
		{
			if (FAILED(spxGamepad->SetDataFormat(&c_dfDIJoystick2)))											{assert(false); return false;}
			if (FAILED(spxGamepad->SetCooperativeLevel(m_ahndWindows[0], m_xSettings.m_uiGamepadCoopFlags)))	{assert(false); return false;}

			m_aspxDIDGamepads[iGamepadIdx] = spxGamepad;
		}
	}


	return true;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CE42Application::ShutDirectInput()
{
    HRESULT hr;

    if (m_pDIDKeyboard)
    {
        hr = m_pDIDKeyboard->Unacquire();
        assert(SUCCEEDED(hr));
        m_pDIDKeyboard->Release();
        m_pDIDKeyboard = NULL;
    }


    if (m_pDIDMouse)
    {
        hr = m_pDIDMouse->Unacquire();
        assert(SUCCEEDED(hr));
        m_pDIDMouse->Release();
        m_pDIDMouse = NULL;
    }


	for (int iGamepadIdx = 0; iGamepadIdx < (int)m_aspxDIDGamepads.Size(); iGamepadIdx++)
	{
		HRESULT hr = m_aspxDIDGamepads[iGamepadIdx]->Unacquire();
        assert(SUCCEEDED(hr));
	}
	m_aspxDIDGamepads.Clear();


    if (m_pDI)
    {
        hr = m_pDI->Release();
        assert(SUCCEEDED(hr));
        m_pDI = NULL;
    }
}
//-------------------------------------------------------------------------------------------------------------------------------------------
bool 
CE42Application::InitApplication()
{
    IDirect3D9* pD3D = NULL;
	if (!InitDirect3D(&pD3D))
	{
		return false;
	}

	CheckMultiHeadSupport(pD3D);

	if (!InitWindow())      
    {
        return false;
    }

    if (!InitDirectInput())
    {
        return false;
    }

    IDirect3DDevice9* pd3dDevice = NULL;
    if (!InitDirect3DDevice(pD3D, &pd3dDevice))
    {
        return false;
    }

    InitEngineController(pD3D, pd3dDevice);

    return true;
}

//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CE42Application::ShutApplication()
{
	IDirect3D9* pD3D = GetD3D();
	IDirect3DDevice9* pD3DDevice = GetDevice();

    ShutEngineController();

	ShutDirect3DDevice(pD3DDevice);

    ShutDirectInput();

    ShutWindow();

	ShutDirect3D(pD3D);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CE42Application::OnActivateWindow()
{
    if (m_pDIDMouse)
    {
        HRESULT hr = m_pDIDMouse->Acquire();
        assert(SUCCEEDED(hr) || (hr == E_ACCESSDENIED));
    }

    if (m_pDIDKeyboard)
    {
        HRESULT hr = m_pDIDKeyboard->Acquire();
        assert(SUCCEEDED(hr) || (hr == E_ACCESSDENIED));
    }

	for (int iGamepadIdx = 0; iGamepadIdx < (int)m_aspxDIDGamepads.Size(); iGamepadIdx++)
	{
        HRESULT hr = m_aspxDIDGamepads[iGamepadIdx]->Acquire();
        assert(SUCCEEDED(hr) || (hr == E_ACCESSDENIED));
	}

	SetPriorityClass(GetCurrentProcess(), NORMAL_PRIORITY_CLASS);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CE42Application::OnDeactivateWindow()
{
    if (m_pDIDMouse)
    {
        HRESULT hr = m_pDIDMouse->Unacquire();
        assert(SUCCEEDED(hr));
    }

    if (m_pDIDKeyboard)
    {
        HRESULT hr = m_pDIDKeyboard->Unacquire();
        assert(SUCCEEDED(hr));
    }

	for (int iGamepadIdx = 0; iGamepadIdx < (int)m_aspxDIDGamepads.Size(); iGamepadIdx++)
	{
		HRESULT hr = m_aspxDIDGamepads[iGamepadIdx]->Unacquire();
        assert(SUCCEEDED(hr));
	}

	SetPriorityClass(GetCurrentProcess(), IDLE_PRIORITY_CLASS);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
bool
CE42Application::DefaultHandleWindowMessage(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
    switch(msg)
    {
    case WM_DESTROY:
        RequestShutDown();
        return false;

    case WM_PAINT:
        if (!ShutDownRequested() && m_bRenderEnable)
        {
            OnIdle(true);
            ValidateRect( hWnd, NULL );
        }
        return false;

    case WM_KEYDOWN:
        OnKeyEvent(wParam);
   		return false;

    case WM_ACTIVATE:
        if ((wParam == WA_ACTIVE) ||
            (wParam == WA_CLICKACTIVE))
        {
            OnActivateWindow();
        }
        else
        {
            OnDeactivateWindow();
        }
        return false;
    }

	return true;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
LRESULT WINAPI 
CE42Application::MessageProc(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
    if (!ShutDownRequested() &&
        !OnWindowMessage(msg, wParam, lParam))
	{
		return 0;
	}


	if (!DefaultHandleWindowMessage(hWnd, msg, wParam, lParam))
	{
		return 0;
	}


    return DefWindowProc( hWnd, msg, wParam, lParam );
}
//-------------------------------------------------------------------------------------------------------------------------------------------
LRESULT WINAPI 
CE42Application::MessageProcCallback(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
    assert(ms_pE42Application);

    if (ms_pE42Application)
        return ms_pE42Application->MessageProc(hWnd, msg, wParam, lParam);
    else
        return DefWindowProc(hWnd, msg, wParam, lParam);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CE42Application::MessageLoop()
{
    // Fenster anzeigen
	for (int iHead = 0; iHead < m_iNumHeads; iHead++)
	{
		ShowWindow(m_ahndWindows[iHead], SW_SHOWDEFAULT);
		UpdateWindow(m_ahndWindows[iHead]);
	}
    m_bRenderEnable = true;


    // Message Loop
    MSG msg = {0, 0, 0, 0, 0, 0}; 
    while ((msg.message != WM_QUIT) && (!ShutDownRequested()))
    {
        if (PeekMessage(&msg, NULL, 0U, 0U, PM_REMOVE) != 0)
        {
            TranslateMessage( &msg );
            DispatchMessage( &msg );
        }
        else
        {
            if (m_bRenderEnable)
            {
                OnIdle(false);
            }
        }
    }

    RequestShutDown();
    m_bRenderEnable = false;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CE42Application::ResetDevice()
{
	GetDevice()->Reset(m_axD3DPresentParameters.GetArrayPointer());
}
//-------------------------------------------------------------------------------------------------------------------------------------------
int
CE42Application::Run()
{
    if (!ShutDownRequested())
    {
        if (InitApplication())
        {
            if (!ShutDownRequested())
            {
                CreateScene();

                if (!ShutDownRequested())
                {
                    MessageLoop();
                }

                Terminate();
            }
        }

        ShutApplication();
    }

    return 0;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CE42Application* CE42Application::ms_pE42Application = NULL;
//-------------------------------------------------------------------------------------------------------------------------------------------
INT 
WINAPI WinMain( HINSTANCE hInstance, HINSTANCE, LPSTR, INT)
{
    CE42Application* pxE42Application = CreateE42Application(hInstance);

    int iResult = 0;
	if (pxE42Application)
	{
		iResult = pxE42Application->Run();
	}

    DestroyE42Application(pxE42Application);

    return iResult;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
#ifdef _DEBUG
    #include "baselib/AutoMemCheck.h"
    CAutoMemCheck g_xMemCheck;
#endif // _DEBUG
//-------------------------------------------------------------------------------------------------------------------------------------------

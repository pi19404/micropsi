// UILibTest.cpp : Defines the entry point for the application.
//

#include "stdafx.h"
#include <d3d9.h>
#include <d3dx9.h>

#include "UILibTest.h"
#include "uilib/core/directx9device.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/bitmapfactory.h"

#define MAX_LOADSTRING 100

// Global Variables:
HINSTANCE hInst;								// current instance
TCHAR szTitle[MAX_LOADSTRING];					// The title bar text
TCHAR szWindowClass[MAX_LOADSTRING];			// the main window class name

// DirectX stuff:
IDirect3D9 *g_pD3D;								// direct3d interface
IDirect3DDevice9* g_pD3DDevice;					// direct3d device

// UILib stuff
UILib::CDirectX9Device*	g_pxUIDevice;			// uilib output device

enum TestState
{
	TEST_SURFACE,
	TEST_DX9CONTEXT,
	TEST_WINDOWSYSTEM,
	TEST_CLIPPING,
};

TestState	m_iTestState;


// Forward declarations of functions included in this code module:
ATOM				MyRegisterClass(HINSTANCE hInstance);
BOOL				InitInstance(HINSTANCE, int);
LRESULT CALLBACK	WndProc(HWND, UINT, WPARAM, LPARAM);

// Forwards aus anderen Test-cpps: 

void TestClipping_OnInit(UILib::COutputDevice* p_pxUIDevice);
void TestClipping_OnPaint(UILib::COutputDevice* p_pxUIDevice);

void TestContext_OnInit(UILib::COutputDevice* p_pxUIDevice);
void TestContext_OnPaint(UILib::COutputDevice* p_pxUIDevice);

void TestSurface_OnInit(UILib::COutputDevice* p_pxUIDevice);
void TestSurface_OnPaint(UILib::COutputDevice* p_pxUIDevice);

void TestWindowSystem_OnInit(UILib::COutputDevice* p_pxUIDevice);



int APIENTRY _tWinMain(HINSTANCE hInstance,
                     HINSTANCE hPrevInstance,
                     LPTSTR    lpCmdLine,
                     int       nCmdShow)
{
 	// TODO: Place code here.
	MSG msg;
	HACCEL hAccelTable;

	// Initialize global strings
	LoadString(hInstance, IDS_APP_TITLE, szTitle, MAX_LOADSTRING);
	LoadString(hInstance, IDC_UILIBTEST, szWindowClass, MAX_LOADSTRING);
	MyRegisterClass(hInstance);

	// Perform application initialization:
	if (!InitInstance (hInstance, nCmdShow)) 
	{
		return FALSE;
	}

	hAccelTable = LoadAccelerators(hInstance, (LPCTSTR)IDC_UILIBTEST);

	// Main message loop:
	while (GetMessage(&msg, NULL, 0, 0)) 
	{
		if (!TranslateAccelerator(msg.hwnd, hAccelTable, &msg)) 
		{
			TranslateMessage(&msg);
			DispatchMessage(&msg);
		}
	}

	delete g_pxUIDevice;
	if(g_pD3DDevice)
	{
		g_pD3DDevice->Release();
	}

	if(g_pD3D)
	{
		g_pD3D->Release();
	}

	return (int) msg.wParam;
}



//
//  FUNCTION: MyRegisterClass()
//
//  PURPOSE: Registers the window class.
//
//  COMMENTS:
//
//    This function and its usage are only necessary if you want this code
//    to be compatible with Win32 systems prior to the 'RegisterClassEx'
//    function that was added to Windows 95. It is important to call this function
//    so that the application will get 'well formed' small icons associated
//    with it.
//
ATOM MyRegisterClass(HINSTANCE hInstance)
{
	WNDCLASSEX wcex;

	wcex.cbSize = sizeof(WNDCLASSEX); 

	wcex.style			= CS_DBLCLKS | CS_HREDRAW | CS_VREDRAW;
	wcex.lpfnWndProc	= (WNDPROC)WndProc;
	wcex.cbClsExtra		= 0;
	wcex.cbWndExtra		= 0;
	wcex.hInstance		= hInstance;
	wcex.hIcon			= LoadIcon(hInstance, (LPCTSTR)IDI_UILIBTEST);
	wcex.hCursor		= LoadCursor(NULL, IDC_ARROW);
	wcex.hbrBackground	= (HBRUSH)(COLOR_WINDOW+1);
	wcex.lpszMenuName	= (LPCTSTR)IDC_UILIBTEST;
	wcex.lpszClassName	= szWindowClass;
	wcex.hIconSm		= LoadIcon(wcex.hInstance, (LPCTSTR)IDI_SMALL);

	return RegisterClassEx(&wcex);
}

//
//   FUNCTION: InitInstance(HANDLE, int)
//
//   PURPOSE: Saves instance handle and creates main window
//
//   COMMENTS:
//
//        In this function, we save the instance handle in a global variable and
//        create and display the main program window.
//
BOOL InitInstance(HINSTANCE hInstance, int nCmdShow)
{
	HWND hWnd;

	hInst = hInstance; // Store instance handle in our global variable

	hWnd = CreateWindow(szWindowClass, szTitle, WS_OVERLAPPEDWINDOW,
		CW_USEDEFAULT, 0, CW_USEDEFAULT, 0, NULL, NULL, hInstance, NULL);

	if (!hWnd)
	{
		return FALSE;
	}

	if( NULL == ( g_pD3D = Direct3DCreate9( D3D_SDK_VERSION ) ) )
		return E_FAIL;


	D3DPRESENT_PARAMETERS PParams;
	ZeroMemory(&PParams, sizeof(PParams));
	
	PParams.SwapEffect		= D3DSWAPEFFECT_DISCARD;
	PParams.hDeviceWindow	= hWnd;
	PParams.Windowed		= TRUE;

	PParams.BackBufferWidth  = 800;
	PParams.BackBufferHeight = 600;
	PParams.BackBufferFormat = D3DFMT_A8R8G8B8;
    PParams.MultiSampleType = D3DMULTISAMPLE_NONE;
    PParams.MultiSampleQuality = D3DMULTISAMPLE_NONE;

	if(FAILED(g_pD3D->CreateDevice( D3DADAPTER_DEFAULT,
									D3DDEVTYPE_HAL,
									hWnd,
									D3DCREATE_SOFTWARE_VERTEXPROCESSING,
									&PParams,
									&g_pD3DDevice)))  
	{ 
		return false;
	}
	
	UILib::CBitmapFactory::Get().AddSearchPath("../gfx");

	g_pxUIDevice = new UILib::CDirectX9Device(g_pD3DDevice, 1600, 600, 800, 600);
	g_pxUIDevice->EnableBlendShader();
    //g_pxUIDevice->SetDeviceScaling(8, 8);

	g_pxUIDevice->BeginPaint();
	g_pxUIDevice->Clear();
	g_pxUIDevice->EndPaint();

	TestContext_OnInit(g_pxUIDevice);
	TestSurface_OnInit(g_pxUIDevice);
	TestClipping_OnInit(g_pxUIDevice);

	UILib::CWindowMgr::Get().AddDevice(g_pxUIDevice, CRct(0, 0, 800, 600));
	TestWindowSystem_OnInit(g_pxUIDevice);

	//g_pxUIDevice->SetDeviceTranslation(400.0f, 300.0f); 
	//g_pxUIDevice->SetDeviceRotation(40.0f); 
	//g_pxUIDevice->SetDeviceScaling(0.01f, 0.01f);

	//g_pxUIDevice->MoveTo(0.0f, 0.0f, 10.0f); 
	//g_pxUIDevice->RotateTo(0.0f, 10.0f); 
	//g_pxUIDevice->ScaleTo(1.0f, 1.0f, 10.0f); 

	//g_pxUIDevice->SetGlobalInterfaceAlpha(20);
	//g_pxUIDevice->FadeAlphaTo(255, 20.0f); 

	ShowWindow(hWnd, nCmdShow);
	UpdateWindow(hWnd);

	return TRUE;
}

//
//  FUNCTION: WndProc(HWND, unsigned, WORD, LONG)
//
//  PURPOSE:  Processes messages for the main window.
//
//  WM_COMMAND	- process the application menu
//  WM_PAINT	- Paint the main window
//  WM_DESTROY	- post a quit message and return
//
//
LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
	int wmId, wmEvent;
	PAINTSTRUCT ps;
	HDC hdc;

	if(m_iTestState == TEST_WINDOWSYSTEM)
	{
		UILib::CWindowMgr::Get().SendWindowsMessage(hWnd, message, wParam, lParam);
	}

	switch (message) 
	{
	case WM_CREATE:
//		m_iTestState = TEST_SURFACE;
//		m_iTestState = TEST_DX9CONTEXT;
		m_iTestState = TEST_WINDOWSYSTEM;
//		m_iTestState = TEST_CLIPPING;

		::SetWindowPos(hWnd, HWND_TOP, 0, 0, 800, 600, SWP_NOMOVE);
		RECT r;
		::GetClientRect(hWnd, &r);
		::SetWindowPos(hWnd, HWND_TOP, 0, 0, 800 + (800 - r.right), 600 + (600 - r.bottom), SWP_NOMOVE);
		::GetClientRect(hWnd, &r);
		::SetTimer(hWnd, 0, 1000 / 30, NULL);
		break;

	case WM_TIMER:
		::InvalidateRect(hWnd, NULL, false);
		break;

	case WM_COMMAND:
		wmId    = LOWORD(wParam); 
		wmEvent = HIWORD(wParam); 
		// Parse the menu selections:
		switch (wmId)
		{
		case ID_TEST_SURFACE:
			if(m_iTestState != TEST_SURFACE)
			{
				m_iTestState = TEST_SURFACE;
				g_pxUIDevice->BeginPaint();
				g_pxUIDevice->Clear();
				g_pxUIDevice->EndPaint();
			}
			break;
		case ID_TEST_CONTEXT:
			if(m_iTestState != TEST_DX9CONTEXT)
			{
				m_iTestState = TEST_DX9CONTEXT;
				g_pxUIDevice->BeginPaint();
				g_pxUIDevice->Clear();
				g_pxUIDevice->EndPaint();
			}
			break;
		case ID_TEST_WINDOWSYSTEM:
			if(m_iTestState != TEST_WINDOWSYSTEM)
			{
				m_iTestState = TEST_WINDOWSYSTEM;
				g_pxUIDevice->BeginPaint();
				g_pxUIDevice->Clear();
				g_pxUIDevice->EndPaint();
				g_pxUIDevice->Invalidate();
			}
			break;
		case ID_TEST_CLIPPING:
			if(m_iTestState != ID_TEST_CLIPPING)
			{
				m_iTestState = TEST_CLIPPING;
				g_pxUIDevice->BeginPaint();
				g_pxUIDevice->Clear();
				g_pxUIDevice->EndPaint();
			}
			break;
		case IDM_EXIT:
			DestroyWindow(hWnd);
			break;
		default:
			return DefWindowProc(hWnd, message, wParam, lParam);
		}
		break;

	case WM_PAINT:
		{
			switch(m_iTestState)
			{
				case TEST_SURFACE: 
					TestSurface_OnPaint(g_pxUIDevice); 
					break;

				case TEST_DX9CONTEXT: 
					TestContext_OnPaint(g_pxUIDevice); 
					break;

				case TEST_WINDOWSYSTEM: 
					UILib::CWindowMgr::Get().Tick(); 
					UILib::CWindowMgr::Get().DoPaint(); 
					break;

				case TEST_CLIPPING: 
					TestClipping_OnPaint(g_pxUIDevice); 
					break;
			}

			g_pD3DDevice->Clear(0, 0, D3DCLEAR_TARGET, D3DCOLOR_XRGB(255, 0, 0), 0, 0);
			g_pD3DDevice->BeginScene();

			g_pxUIDevice->Render2D();

			g_pD3DDevice->EndScene();
			g_pD3DDevice->Present(0, 0, 0, 0);

			hdc = BeginPaint(hWnd, &ps);
			EndPaint(hWnd, &ps);
		}
		break;

	case WM_DESTROY:
		PostQuitMessage(0);
		break;

	default:
		return DefWindowProc(hWnd, message, wParam, lParam);
	}
	return 0;
}


/*******************************************************************************
E42Application.h
	Basisklasse für Applikationsklasse,
	Initialisierung, Termination, Fenster und D3D&DInput-Devices anlegen, 
	Idle-Loop, WindowProc, MessageLoop
*******************************************************************************/
#pragma once

#ifndef E42_E42APPLICATION_H_INCLUDED
#define E42_E42APPLICATION_H_INCLUDED

#include "e42/stdinc.h"

#include "baselib/dynarray.h"
#include "baselib/comobjectptr.h"
#include "e42/AppSettings.h"
#include "e42/core/EngineController.h"


#include <string>
#include <windows.h>
#include <d3d9types.h>

struct IDirectInput8A;
struct IDirect3DSurface9;
struct IDirectInputDevice8A;
struct IDirect3DSwapChain9;


class CE42Application : public CEngineController
{
	friend INT WINAPI WinMain( HINSTANCE hInstance, HINSTANCE, LPSTR, INT);


private:

	static CE42Application*	ms_pE42Application;


	bool					m_bShutDownRequested;							///< true, wenn die Applikation sich beenden möchte
	bool					m_bRenderEnable;								///< wird benötigt, damit WM_PAINT-Events erst nach DeviceCreation Rendering auslösen


	CDynArray<float>		m_afMonitorAspect;								///< AspectRatio der angeschlossenen Monitore (physische Größe)
	CDynArray<float>		m_afDesktopAspect;								///< AspectRatio der Desktops (Pixelverhältnis), wird für WindowModes benötigt

	static BOOL CALLBACK	MonitorEnumProc(HMONITOR hMonitor, HDC hdcMonitor, LPRECT lprcMonitor, LPARAM dwData);
	void					DetermineAspectRatios();


	/// Fenster anlegen/schließen
	bool			InitWindow();
	void			ShutWindow();


	void			CheckMultiHeadSupport(IDirect3D9* pD3D);				///< detected Anzahl der Monitore und setzt die Variablen m_iNumHeads und m_bUseMultipleHeads
	virtual void	DeterminePresentParameters(IDirect3D9* pd3dDirect3D);	///< setzt die D3DPRESENT_PARAMETERS-Strukturen der Applikation

	/// IDirect3D9-Schnittstelle anlegen/freigeben
	bool			InitDirect3D(IDirect3D9** ppD3DOut);
	void			ShutDirect3D(IDirect3D9* pD3D);
	virtual void	OnCreateD3DFailed() const;

	/// IDirect3DDevice9-Schnittstelle anlegen/freigeben
	bool			InitDirect3DDevice(IDirect3D9* pD3D, IDirect3DDevice9** ppd3dDeviceOut);
	void			ShutDirect3DDevice(IDirect3DDevice9* pd3dDevice);
	virtual void	OnCreateDeviceFailed(HRESULT hr) const;

	/// Initialisierung/Deinitialisierung von DirectInput
	bool			InitDirectInput();
	void			ShutDirectInput();

	/// Initialisierung/Deinitialisierung der Applikation (ruft obige Initialisierungs- / Shutdown-Funktionen auf)
	virtual bool	InitApplication();
	virtual void	ShutApplication();



	virtual void	MessageLoop();

	virtual void	CreateScene() {};		///< wird aufgerufen, nachdem die App initialisiert ist (inkl. Device)
	virtual void	Terminate() {};			///< wird aufgerufen, bevor die App deinitialisiert ist

	int				Run();


protected:

	virtual void	OnActivateWindow();
	virtual void	OnDeactivateWindow();

	bool			DefaultHandleWindowMessage(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam);	///< default Handling für WindowMessages (können mit HandleWindowMessage überschrieben werden)
	LRESULT WINAPI	MessageProc(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam);					///< MessageProc für die Fenster (nichtstatisch)
	static LRESULT WINAPI MessageProcCallback(HWND hWnd, UINT msg, WPARAM wParam, LPARAM lParam);	///< MessageProc für die Fenster (statisch)


	std::string     GetCommandLineValue(const std::string& sKey, const char* pcCommandLine = 0) const;


	// D3D-Einstellungen
	CAppSettings					m_xSettings;
	CDynArray<D3DPRESENT_PARAMETERS> m_axD3DPresentParameters;

	WNDCLASSEX						m_WindowClass;
	unsigned int					m_uiWindowStyle;


	// Fenster-Daten
	int								m_iNumHeads;
	CDynArray<HWND>					m_ahndWindows;
	HINSTANCE						m_hInstance;


	// Devices 
	IDirectInput8A*									m_pDI;
	IDirectInputDevice8A*							m_pDIDMouse;
	IDirectInputDevice8A*							m_pDIDKeyboard;
	CDynArray<CComObjectPtr<IDirectInputDevice8A> >	m_aspxDIDGamepads;

	CDynArray<CComObjectPtr<IDirect3DSwapChain9> >	m_aspxSwapChains;


	int								m_iScreenshotIdx;					///< Counter für Screenshot-Dateinamen


	virtual void	OnIdle(bool bPaintEvt) {if (!bPaintEvt) OnIdle();};	///< wird aus MessageLoop und bei WM_PAINT aufgerufen
	virtual void	OnIdle() {};										///< wird nur aus dem MessageLoop audgerufen
	virtual bool	OnKeyEvent(WPARAM virtualKey) {return true;};
	virtual bool	OnWindowMessage(UINT msg, WPARAM wParam, LPARAM lParam) {return true;};


	CE42Application(HINSTANCE hInstance);


public:

	virtual			~CE42Application();


	virtual void	RequestShutDown();									///< löst Shutdown der Applikation aus (wirkt verzögert, nach dem Ende von OnIdle())
	bool			ShutDownRequested() const;							///< Abfrage, ob Shutdown im Gange ist

	virtual float	GetAspectRatio(int iHead = 0) const;
	HWND			GetWindowHandle(int iHead = 0) const;
	bool			WindowIsActive() const;
	int				GetWindowWidth() const;
	int				GetWindowHeight() const;
	bool			SceneMultiSamplingEnabled() const;
	const CAppSettings& GetSettings() const;

	int				GetNumHeads() const;
	int				GetNumGamepads() const;


	void			SetHeadAsRenderTarget(int iHead);

	void            SaveCurrentFrameToDisk(const char* pcFilename = "screenshot", int iHead = 0, bool bOverwriteExisting = false);


	IDirect3DSwapChain9*	GetSwapChain(int iHead) const;
	IDirectInputDevice8A*	GetMouseDevice() const;
	IDirectInputDevice8A*	GetGamepadDevice(int iIndex = 0) const;
	IDirectInputDevice8A*	GetKeyboardDevice() const;

	void			ResetDevice();

	static CE42Application& Get();
};

CE42Application* CreateE42Application(HINSTANCE hInstance);
void DestroyE42Application(CE42Application* pApplication);

INT WINAPI WinMain(HINSTANCE hInstance, HINSTANCE, LPSTR, INT);


#include "e42/E42Application.inl"

#endif // E42_E42Application_H_INCLUDED

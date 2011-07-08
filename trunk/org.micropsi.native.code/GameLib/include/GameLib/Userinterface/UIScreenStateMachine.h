#pragma once
#ifndef GAMELIB_WINDOWSYSTEMCONTROLLER_H_INCLUDED
#define GAMELIB_WINDOWSYSTEMCONTROLLER_H_INCLUDED

#include <map>
#include <string>
#include "uilib/core/directxdevicemanager.h"
#include "uilib/core/windowmanager.h"
#include "GameLib/UserInterface/UIScreen.h"

class CEngineController;
class CInputManager;

namespace UILib
{
	class CDirectX9Device;
	class CLabel;
};

class CUIScreenStateMachine
{
public:

	CUIScreenStateMachine(CEngineController* p_pxEngineController, CInputManager* p_pxInputManager, int p_iWidth, int p_iHeight, int iNumUIScreenDesktops = 1);
	virtual ~CUIScreenStateMachine();	

	void Tick();
	void Render(int iDesktop = -1);

	bool AddScreen(std::string p_sName, CUIScreen* p_pxUIScreen);
	bool SwitchToScreen(std::string p_sName);		// schaltet Screen um (falls gerade Screen::Update() ausgeführt wird, erfolgt das Umschalten anschließend)

	/// löscht alle Screens
	void DestroyScreens();

	/// liefert true, wenn der aktuelle Screen den Bildschirm völlig und ohne transparenz einnimmt - kein weiteres Rendering nötig
    bool InterfaceIsOpaque();

	UILib::CDirectX9Device*					GetDesktopDevice(int iDeviceIndex = 0);
	UILib::CDirectXDeviceMgr::TDeviceHandle	GetDesktopDeviceHandle(int iDeviceIndex = 0);

	/// sollte für jede ankommende MS-Windows-Msg aufgerufen werden; leitet die Nachrichten an den WindowMgr der UILib weiter
    bool									HandleWindowMessage(UINT msg, WPARAM wParam, LPARAM lParam);

	/// erzeugt ein neues DirectX-UserInterface-Device (über UILib::DirectXDeviceMgr; weitere Funktionen dort!!!)
	UILib::CDirectXDeviceMgr::TDeviceHandle	CreateDevice(CRct p_xArea, UILib::CWindow** po_ppxDesktop = 0);

	/// gibt ein DirectX-Userinterface-Device wieder frei
	void									ReleaseDevice(UILib::CDirectXDeviceMgr::TDeviceHandle p_hHandle);

	/// liefert true, solange devices animationen durchführen (fading, scaling, rotating etc).
	bool									DeviceAnimationRunning() const;

	CInputManager*							GetInputManager() const;

private:

	struct TDeviceAccess
	{
		UILib::CDirectXDeviceMgr::TDeviceHandle	hDesktopDevice;			///< primary drawing device handle
		UILib::CDirectX9Device*					pxDesktopDevice;		///< primary drawing device
	};

	std::vector<TDeviceAccess>				m_axDesktopDevices;

	

	std::map<std::string, CUIScreen*>		m_mpxScreens;			///< Screens und ihre Namen
	CUIScreen*								m_pxCurrentScreen;		///< aktueller Screen
	CUIScreen*								m_pxNextScreen;			///< nachfolgender Screen
	bool									m_bInsideScreenUpdate;	///< true, während der Screen geupdatet wird (semaphore)

	CEngineController*						m_pxEngineController;	///< Engine Controller
	CInputManager*							m_pxInputManager;		///< Input Manager
	int										m_iWidth;				///< Fensterbreite
	int										m_iHeight;				///< Fensterhöhe


	void SwitchToNextScreen();										///< schaltet den Bildschirm zum nächsten Screen um
};

#include "UIScreenStateMachine.inl"

#endif // GAMELIB_WINDOWSYSTEMCONTROLLER_H_INCLUDED


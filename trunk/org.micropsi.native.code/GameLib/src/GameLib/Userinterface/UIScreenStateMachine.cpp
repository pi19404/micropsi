#include "stdafx.h"
#include "GameLib/UserInterface/UIScreenStateMachine.h"
#include "GameLib/UserInterface/uiscreen.h"

#include "baselib/filelocator.h"
#include "uilib/core/visualizationfactory.h"
#include "uilib/controls/panel.h"
#include "e42/core/EngineController.h"
#include "e42/E42Application.h"

using std::string;
using std::map;

//---------------------------------------------------------------------------------------------------------------------
CUIScreenStateMachine::CUIScreenStateMachine(CEngineController* p_pxEngineController, CInputManager* p_pxInputManager, int p_iWidth, int p_iHeight, int iNumUIScreenDesktops)
:	m_pxEngineController(p_pxEngineController),
	m_pxInputManager(p_pxInputManager),
	m_iWidth(p_iWidth),
	m_iHeight(p_iHeight),
	m_bInsideScreenUpdate(false)
{
	m_axDesktopDevices.resize(iNumUIScreenDesktops);

	for (int iDesktop = 0; iDesktop < iNumUIScreenDesktops; iDesktop++)
	{
		m_axDesktopDevices[iDesktop].hDesktopDevice = UILib::CDirectXDeviceMgr::Get().CreateDevice(p_pxEngineController->GetDevice(), p_iWidth, p_iHeight, p_iWidth, p_iHeight);

		m_axDesktopDevices[iDesktop].pxDesktopDevice = UILib::CDirectXDeviceMgr::Get().GetDevice(m_axDesktopDevices[iDesktop].hDesktopDevice); 
		m_axDesktopDevices[iDesktop].pxDesktopDevice->EnableBlendShader();
		UILib::CWindowMgr::Get().AddDevice(m_axDesktopDevices[iDesktop].pxDesktopDevice, CRct(p_iWidth * 0, 0, p_iWidth, p_iHeight));
	}

	((UILib::CPanel*)UILib::CWindowMgr::Get().GetDesktop())->SetColor(CColor(0, 0, 0, 0));


	m_pxCurrentScreen = NULL;
	m_pxNextScreen = NULL;
}


//---------------------------------------------------------------------------------------------------------------------
CUIScreenStateMachine::~CUIScreenStateMachine()
{
	DestroyScreens();

	UILib::CVisualizationFactory::Shut();
	UILib::CDirectXDeviceMgr::Shut();
	UILib::CWindowMgr::Shut();
	UILib::CBitmapFactory::Shut();
}	

//---------------------------------------------------------------------------------------------------------------------
void 
CUIScreenStateMachine::Tick()
{
	UILib::CWindowMgr::Get().Tick();

	if (m_pxCurrentScreen)
	{
		bool bStateChanged;

		do
		{
			bStateChanged = false;

			m_bInsideScreenUpdate = true;
			m_pxCurrentScreen->Update();
			m_bInsideScreenUpdate = false;

			if (m_pxNextScreen != NULL)
			{
				SwitchToNextScreen();
				bStateChanged = true;
			}
		}
		while (bStateChanged);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CUIScreenStateMachine::Render(int iDesktop)
{
	UILib::CWindowMgr::Get().DoPaint();

	if (iDesktop == -1)
	{
		UILib::CDirectXDeviceMgr::Get().Render();
	}
	else
	{
		m_axDesktopDevices[iDesktop].pxDesktopDevice->Render2D();
	}

	if (m_pxCurrentScreen)
	{
		m_pxCurrentScreen->Render(iDesktop);
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CUIScreenStateMachine::AddScreen(std::string p_sName, CUIScreen* p_pxUIScreen)
{
	assert(p_pxUIScreen);

	if (m_mpxScreens.size() > 0)
	{
		map<string, CUIScreen*>::iterator i;
		i = m_mpxScreens.find(p_sName);
		if (i != m_mpxScreens.end())
		{
			assert(false);   // screen double definition!
			return false;
		}
	}

	p_pxUIScreen->SetUIScreenStateMachine(this);
	m_mpxScreens[p_sName] = p_pxUIScreen;
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CUIScreenStateMachine::DestroyScreens()
{
	if (m_mpxScreens.size() != 0)
	{
		map<string, CUIScreen*>::iterator i;
		for (i = m_mpxScreens.begin(); i != m_mpxScreens.end(); i++)
		{
			i->second->Destroy();
		}

		m_mpxScreens.clear();
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CUIScreenStateMachine::SwitchToScreen(std::string p_sName)
{
	if (m_mpxScreens.empty())
	{
		assert(false);
		return false;
	}

	if (p_sName == "shutdown")
	{
		CE42Application::Get().RequestShutDown();
		return true;
	}


	map<string, CUIScreen*>::iterator i;
	i = m_mpxScreens.find(p_sName);
	if (i != m_mpxScreens.end())
	{
		if (i->second != m_pxCurrentScreen)
		{
			m_pxNextScreen = i->second;

			if (!m_bInsideScreenUpdate)
			{
				SwitchToNextScreen();
			}
		}

		return true;
	}
	else
	{
		return false;
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CUIScreenStateMachine::SwitchToNextScreen()
{
	assert(m_pxNextScreen);

	if (m_pxCurrentScreen)
	{
		m_pxCurrentScreen->OnLeave();
	}

	m_pxCurrentScreen = m_pxNextScreen;
	m_pxNextScreen = NULL;

	if (m_pxCurrentScreen)
	{
		m_pxCurrentScreen->OnEnter();
	}
}
//---------------------------------------------------------------------------------------------------------------------
bool
CUIScreenStateMachine::HandleWindowMessage(UINT msg, WPARAM wParam, LPARAM lParam)
{
    UILib::CWindowMgr::Get().SendWindowsMessage(CE42Application::Get().GetWindowHandle(), msg, wParam, lParam);

	return true;
}
//---------------------------------------------------------------------------------------------------------------------
/**
	erzeugt ein neues DirectX-UserInterface-Device und meldet es beim WindowMgr an
	liefert auf Wunsch einen Pointer auf das Desktopfenster
*/
UILib::CDirectXDeviceMgr::TDeviceHandle	
CUIScreenStateMachine::CreateDevice(CRct p_xArea, UILib::CWindow** po_ppxDesktop)
{
	UILib::CDirectXDeviceMgr::TDeviceHandle h =  
		UILib::CDirectXDeviceMgr::Get().CreateDevice(	m_pxEngineController->GetDevice(), 
												p_xArea.Width(), p_xArea.Height(), 
												m_iWidth, m_iHeight);
	UILib::WHDL hWnd = UILib::CWindowMgr::Get().AddDevice(UILib::CDirectXDeviceMgr::Get().GetDevice(h), p_xArea);
	if (po_ppxDesktop)
	{
		*po_ppxDesktop = UILib::CWindowMgr::Get().GetWindow(hWnd);
	}	

	return h;
}
//---------------------------------------------------------------------------------------------------------------------
void									
CUIScreenStateMachine::ReleaseDevice(UILib::CDirectXDeviceMgr::TDeviceHandle p_hHandle)
{
	UILib::CWindowMgr::Get().RemoveDevice(UILib::CDirectXDeviceMgr::Get().GetDevice(p_hHandle));
	UILib::CDirectXDeviceMgr::Get().ReleaseDevice(p_hHandle);
}
//---------------------------------------------------------------------------------------------------------------------

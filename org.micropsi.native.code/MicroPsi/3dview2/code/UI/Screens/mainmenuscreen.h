#pragma once

#ifndef MAINMENUSCREEN_H_INCLUDED
#define MAINMENUSCREEN_H_INCLUDED

#include <string>

#include "Application/stdinc.h"
#include "GameLib/UserInterface/UIScreen.h"

#include "World/world.h"

namespace UILib
{
	class CPanel;
}

class CUIScreenStateMachine;
class CMainMenuDialog;
class CLoadOfflineWorldDialog;
class CConnect2WorldServerDialog;

class CMainMenuScreen : public CUIScreen
{
public:

    CMainMenuScreen();
    virtual ~CMainMenuScreen();

    virtual void Init();
    virtual void Update();
    
    virtual void Render();

    virtual void OnEnter();
    virtual void OnLeave();

	bool	LoadWorld(std::string p_sWorldFile, std::string p_sVisualizationFile, CWorld::WrapState p_eWrapState); 
	void	OnMainMenuButton(int p_iButton);

	static CUIScreen* __cdecl Create();
	virtual void Destroy() const;

private:
	void	CreateUIElements();

	UILib::CPanel*			m_pxBackgroundPanel;
	CMainMenuDialog*		m_pxMainMenuDialog;

	CLoadOfflineWorldDialog*		m_pxLoadOfflineWorldDialog;
	CConnect2WorldServerDialog*		m_pxConnect2WorldServerDialog;
};

#endif // MAINMENUSCREEN_H_INCLUDED

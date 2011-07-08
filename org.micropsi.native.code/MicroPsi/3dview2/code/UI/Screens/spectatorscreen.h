#pragma once

#ifndef SPECTATORSCREEN_H_INCLUDED
#define SPECTATORSCREEN_H_INCLUDED

#include "Application/stdinc.h"
#include "GameLib/UserInterface/UIScreen.h"

namespace UILib
{
	class CPanel;
}

class CUIScreenStateMachine;
class CCompass;
class CSpectatorModePanel;
class CConnectionStatusPanel;

class CSpectatorScreen : public CUIScreen
{
public:

    CSpectatorScreen();
    virtual ~CSpectatorScreen();

    virtual void Init();
    virtual void Update();
    
    virtual void Render();

    virtual void OnEnter();
    virtual void OnLeave();

	static CUIScreen* __cdecl Create();
	virtual void Destroy() const;

private:
	void	CreateUIElements();

	UILib::CPanel*			m_pxBackgroundPanel;
	CCompass*				m_pxCompass;
	CSpectatorModePanel*	m_pxSpectatorModePanel;
	CConnectionStatusPanel*	m_pxConnectionStatusPanel;
};

#endif // SPECTATORSCREEN_H_INCLUDED

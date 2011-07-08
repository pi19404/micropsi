#pragma once

#ifndef AGENTCONTROLSCREEN_H_INCLUDED
#define AGENTCONTROLSCREEN_H_INCLUDED

#include "Application/stdinc.h"
#include <string>
#include "baselib/geometry/CVector.h"
#include "GameLib/UserInterface/UIScreen.h"

namespace UILib
{
	class CPanel;
}

class CUIScreenStateMachine;
class CCompass;
class CSpectatorModePanel;
class CConnectionStatusPanel;

class CAgentControlScreen : public CUIScreen
{
public:

    CAgentControlScreen();
    virtual ~CAgentControlScreen();

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
	CConnectionStatusPanel*	m_pxConnectionStatusPanel;

	std::string				m_sAgentName;
	CVec3					m_vAgentOrientation;
};

#endif // AGENTCONTROLSCREEN_H_INCLUDED

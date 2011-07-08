#pragma once

#ifndef SPECTATORMODEPANEL_H_INCLUDED
#define SPECTATORMODEPANEL_H_INCLUDED

#include "Application/stdinc.h"
#include "uilib/controls/panel.h"
#include "uilib/controls/togglebutton.h"

#include <string>

class CSpectatorModePanel : public UILib::CPanel
{
public:
	static	CSpectatorModePanel*	Create();
	void	SetMode(std::string p_sNewMode);

protected:
    CSpectatorModePanel();
    virtual ~CSpectatorModePanel();

	virtual void DeleteNow();
	virtual bool HandleMsg(const UILib::CMessage& p_krxMessage);


	enum ModeButtons
	{
		MB_Freelook,
		MB_Walk,
		MB_Helicopter,
		MB_NumButtons
	};

	UILib::CToggleButton*		m_apxModeButtons[MB_NumButtons];
};

#endif // ifndef SPECTATORMODEPANEL_H_INCLUDED

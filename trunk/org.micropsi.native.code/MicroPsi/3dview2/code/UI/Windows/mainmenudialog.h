#pragma once

#ifndef MAINMENUDIALOG_H_INCLUDED
#define MAINMENUDIALOG_H_INCLUDED

#include "uilib/controls/panel.h"
#include "uilib/controls/button.h"

#include "UI/Screens/mainmenuscreen.h"

class CMainMenuDialog : public UILib::CPanel
{
public:

	enum Buttons
	{
		BT_ConnectToWorld,
		BT_LoadOfflineWorld,
		BT_SpectatorMode,
		BT_AgentMode,
		BT_EditorMode,
		BT_NumButtons
	};

	static CMainMenuDialog*	Create();
	
	void	SetMainMenuScreen(CMainMenuScreen* p_pxMMS);
	void	SetButtonDisabled(Buttons p_eButton, bool p_bDisabled);

protected:

    CMainMenuDialog();
    virtual ~CMainMenuDialog();

	virtual void			DeleteNow();
	virtual bool			HandleMsg(const UILib::CMessage& p_rxMessage);

private:

	CMainMenuScreen*		m_pxMainMenuScreen;
	UILib::CButton*			m_apxButtons[BT_NumButtons];
};

#endif // MAINMENUDIALOG_H_INCLUDED

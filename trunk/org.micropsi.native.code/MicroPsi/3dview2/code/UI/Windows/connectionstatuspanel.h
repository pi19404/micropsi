#pragma once

#ifndef CONNECTIONSTATUSPANEL_H_INCLUDED
#define CONNECTIONSTATUSPANEL_H_INCLUDED

#include "Application/stdinc.h"
#include "uilib/controls/panel.h"
#include "uilib/controls/label.h"

#include <string>

class CConnectionStatusPanel : public UILib::CPanel
{
public:
	static	CConnectionStatusPanel*	Create();
	void	SetStatus(bool p_bWorldConnected, bool p_bAgentConnected, bool p_bServerConnected);

protected:
    CConnectionStatusPanel();
    virtual ~CConnectionStatusPanel();

	virtual void DeleteNow();
	virtual bool OnVisualizationChange();
	virtual bool OnTimer(int p_iID);

	enum ModeLabels
	{
		MB_Agent,
		MB_World,
		MB_Server,
		MB_NumLabels
	};

	UILib::CLabel*		m_apxLabels[MB_NumLabels];
	int					m_iUpdateTimer;
};

#endif // ifndef CONNECTIONSTATUSPANEL_H_INCLUDED

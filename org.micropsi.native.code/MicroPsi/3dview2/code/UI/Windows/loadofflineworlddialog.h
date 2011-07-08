#pragma once

#ifndef LOADOFFLINEWORLDDIALOG_H_INCLUDED
#define LOADOFFLINEWORLDDIALOG_H_INCLUDED

#include "uilib/controls/panel.h"
#include "uilib/controls/button.h"
#include "uilib/controls/listbox.h"
#include "uilib/controls/editcontrol.h"


class CMainMenuScreen;
class CVisualizationPicker;

class CLoadOfflineWorldDialog : public UILib::CPanel
{
public:

	static CLoadOfflineWorldDialog*	Create();

	void					SetMainMenuScreen(CMainMenuScreen* p_pxMMS);
	virtual void			SetVisible(bool p_bVisible = true);

protected:

    CLoadOfflineWorldDialog();
    virtual ~CLoadOfflineWorldDialog();

	virtual void			DeleteNow();
	virtual bool			HandleMsg(const UILib::CMessage& p_rxMessage);
	void					UpdateList();

private:

	CMainMenuScreen*		m_pxMainMenuScreen;
	UILib::CButton*			m_pxLoadButton;
	UILib::CButton*			m_pxCancelButton;
	UILib::CListBox*		m_pxFileList;
	UILib::CEditControl*	m_pxFileDescription;
	CVisualizationPicker*	m_pxVisualizationPicker;
};

#endif // LOADOFFLINEWORLDDIALOG_H_INCLUDED

#pragma once

#ifndef CONNECTTOWORLDSERVERDIALOG_H_INCLUDED
#define CONNECTTOWORLDSERVERDIALOG_H_INCLUDED

#include "uilib/controls/panel.h"
#include "uilib/controls/button.h"
#include "uilib/controls/editcontrol.h"
#include "uilib/controls/combobox.h"

class CMainMenuScreen;
class CVisualizationPicker;

class CConnect2WorldServerDialog : public UILib::CPanel
{
public:

	static CConnect2WorldServerDialog*	Create();

	void					SetMainMenuScreen(CMainMenuScreen* p_pxMMS);
	virtual void			SetVisible(bool p_bVisible = true);

protected:

    CConnect2WorldServerDialog();
    virtual ~CConnect2WorldServerDialog();

	void					SetDefaults();

	virtual void			DeleteNow();
	virtual bool			HandleMsg(const UILib::CMessage& p_rxMessage);

private:

	CMainMenuScreen*		m_pxMainMenuScreen;

	UILib::CButton*			m_pxConnectButton;				///< click this one to connect
	UILib::CButton*			m_pxCancelButton;				///< click here to cancel

	UILib::CButton*			m_pxDefaultSettings;			///< click here to restore default settings

	UILib::CComboBox*		m_pxConnectionMode;				///< allows selection of connection mode
	
	UILib::CLabel*			m_pxServerLabel;				///< label for server name box			
	UILib::CEditControl*	m_pxServer;						///< server name (alle methods)

	UILib::CLabel*			m_pxWorldPortLabel;				///< label for port box
	UILib::CEditControl*	m_pxWorldPort;					///< port for socket world service

	UILib::CLabel*			m_pxWorldURLLabel;				
	UILib::CEditControl*	m_pxWorldURL;					

	UILib::CLabel*			m_pxAgentServiceURLLabel;		
	UILib::CEditControl*	m_pxAgentServiceURL;			

	CVisualizationPicker*	m_pxVisualizationPicker;
};

#endif // CONNECTTOWORLDSERVERDIALOG_H_INCLUDED

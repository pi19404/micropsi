#pragma once

#ifndef GAMELIB_EDITORTOOLBAR_H_INCLUDED
#define GAMELIB_EDITORTOOLBAR_H_INCLUDED

#include "uilib/controls/panel.h"
#include "uilib/controls/button.h"
#include "uilib/controls/togglebutton.h"
#include "uilib/controls/combobox.h"

class CEditorScreen;

class CEditorToolBar : public UILib::CPanel
{
public:
	static	CEditorToolBar*	Create(CEditorScreen* p_pxEditorScreen);
	void	Tick();

protected:
    CEditorToolBar(CEditorScreen* p_pxEditorScreen);
    virtual ~CEditorToolBar();

	virtual void DeleteNow();
	virtual bool HandleMsg(const UILib::CMessage& p_krxMessage);

	void		 FillObjectTypeList();

	CEditorScreen*				m_pxEditorScreen;

	UILib::CButton*				m_pxSaveButton;

	UILib::CToggleButton*		m_pxSelectButton;
	UILib::CToggleButton*		m_pxMoveButton;
	UILib::CToggleButton*		m_pxRotateButton;

	UILib::CToggleButton*		m_pxCreateObjectButton;
	
	UILib::CComboBox*			m_pxObjectTypeComboBox;

	UILib::CButton*				m_pxDeleteObjectButton;
	UILib::CButton*				m_pxResetButton;

	UILib::CToggleButton*		m_pxCreateMusicButton;
	UILib::CToggleButton*		m_pxCreateSoundButton;
	UILib::CToggleButton*		m_pxCreateSoundEnvironmentButton;

	UILib::CToggleButton*		m_pxObjectPropertiesButton;
	UILib::CToggleButton*		m_pxObjectListButton;
	UILib::CToggleButton*		m_pxSettingsButton;
};

#endif // ifndef GAMELIB_EDITORTOOLBAR_H_INCLUDED

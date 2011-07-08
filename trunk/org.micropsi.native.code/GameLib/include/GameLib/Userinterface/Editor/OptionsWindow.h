#pragma once

#ifndef GAMELIB_OPTIONSWINDOW_H_INCLUDED
#define GAMELIB_OPTIONSWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"
#include "uilib/controls/button.h"
#include "uilib/controls/combobox.h"
#include "uilib/controls/checkbox.h"

class CEditorScreen;

class COptionsWindow : public UILib::CDialogWindow
{
public:
	static	COptionsWindow*	Create(CEditorScreen* p_pxEditorScreen);
	void	Tick();

protected:
    COptionsWindow(CEditorScreen* p_pxEditorScreen);
    virtual ~COptionsWindow();

	virtual void DeleteNow();
	virtual bool HandleMsg(const UILib::CMessage& p_krxMessage);


	CEditorScreen*				m_pxEditorScreen;

	UILib::CComboBox*			m_pxControlMethod;
	UILib::CComboBox*			m_pxXZGrid;
	UILib::CComboBox*			m_pxYGrid;
	UILib::CCheckBox*			m_pxShowAllRanges;
	UILib::CButton*				m_pxCloseButton;
};

#endif // ifndef GAMELIB_OPTIONSWINDOW_H_INCLUDED

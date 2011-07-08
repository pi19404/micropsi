#pragma once

#ifndef GAMELIB_OBJECTPROPERTIESWINDOW_H_INCLUDED
#define GAMELIB_OBJECTPROPERTIESWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"
#include "uilib/controls/button.h"

class CEditorScreen;

class CObjectPropertiesWindow : public UILib::CDialogWindow
{
public:
	static	CObjectPropertiesWindow*	Create(CEditorScreen* p_pxEditorScreen);
	void	Tick();

protected:
    CObjectPropertiesWindow(CEditorScreen* p_pxEditorScreen);
    virtual ~CObjectPropertiesWindow();

	virtual void DeleteNow();
	virtual bool HandleMsg(const UILib::CMessage& p_krxMessage);

	CEditorScreen*				m_pxEditorScreen;
	UILib::CButton*				m_pxCloseButton;
};

#endif // ifndef GAMELIB_OBJECTPROPERTIESWINDOW_H_INCLUDED

#pragma once

#ifndef GAMELIB_OBJECTSLISTWINDOW_H_INCLUDED
#define GAMELIB_OBJECTSLISTWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"
#include "uilib/controls/button.h"
#include "uilib/controls/listbox.h"
#include "uilib/controls/combobox.h"

class CEditorScreen;

class CObjectsListWindow : public UILib::CDialogWindow
{
public:
	static	CObjectsListWindow*	Create(CEditorScreen* p_pxEditorScreen);
	void	Tick();

protected:
    CObjectsListWindow(CEditorScreen* p_pxEditorScreen);
    virtual ~CObjectsListWindow();

	virtual void DeleteNow();
	virtual bool HandleMsg(const UILib::CMessage& p_krxMessage);

	void		FillObjectList();
	void		FillObjectTypeList();

	CEditorScreen*				m_pxEditorScreen;

	UILib::CButton*				m_pxCloseButton;
	UILib::CButton*				m_pxGotoButton;

	UILib::CListBox*			m_pxObjectsList;
	
	UILib::CComboBox*			m_pxObjectTypeFilter;
};

#endif // ifndef GAMELIB_OBJECTSLISTWINDOW_H_INCLUDED

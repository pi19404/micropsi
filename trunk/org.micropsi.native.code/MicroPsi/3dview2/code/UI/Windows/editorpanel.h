#pragma once

#ifndef EDITORPANEL_H_INCLUDED
#define EDITORPANEL_H_INCLUDED

#include "Application/stdinc.h"
#include "uilib/controls/panel.h"
#include "uilib/controls/button.h"
#include "uilib/controls/togglebutton.h"
#include "uilib/controls/listbox.h"
#include "uilib/controls/combobox.h"
#include "uilib/controls/checkbox.h"

class CLevelEditor;

class CEditorPanel : public UILib::CPanel
{
public:
	static	CEditorPanel*	Create();
	void	Update();
	void	Tick();

protected:
    CEditorPanel();
    virtual ~CEditorPanel();

	virtual void DeleteNow();
	virtual bool HandleMsg(const UILib::CMessage& p_krxMessage);

private:

	void	SaveAs(const char* p_pcFile);
	void	UpdateVariationList();


	UILib::CToggleButton*		m_pxSelectButton;
	UILib::CToggleButton*		m_pxObjectButton;
	UILib::CButton*				m_pxGotoButton;
	UILib::CButton*				m_pxDeleteButton;
	UILib::CButton*				m_pxClearButton;

	UILib::CButton*				m_pxSaveButton;
	UILib::CButton*				m_pxSaveAsButton;

	UILib::CComboBox*			m_pxObjectType;
	UILib::CComboBox*			m_pxObjectVariation;

	UILib::CCheckBox*			m_pxRandomRotation;

	UILib::CListBox*			m_pxList;

	CLevelEditor*				m_pxLevelEditor;

	std::string					m_sMessageBoxAction;			///< used to find out which type of message box is currently open

	friend class CSaveAsDialog;
};

#endif // ifndef EDITORPANEL_H_INCLUDED

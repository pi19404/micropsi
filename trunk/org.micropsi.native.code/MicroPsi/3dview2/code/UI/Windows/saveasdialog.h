#pragma once

#ifndef SAVEASDIALOG_H_INCLUDED
#define SAVEASDIALOG_H_INCLUDED

#include "Application/stdinc.h"
#include "uilib/controls/button.h"
#include "uilib/controls/editcontrol.h"
#include "uilib/controls/dialogwindow.h"

class CEditorPanel;

class CSaveAsDialog : public UILib::CDialogWindow
{
public:
	static CSaveAsDialog*	Create(CEditorPanel* p_pxEditorPanel);

protected:
    CSaveAsDialog(CEditorPanel* p_pxEditorPanel);
    virtual ~CSaveAsDialog();

	virtual void DeleteNow();
	virtual bool HandleMsg(const UILib::CMessage& p_krxMessage);

private:

	UILib::CButton*			m_pxOKButton;
	UILib::CButton*			m_pxCancelButton;

	UILib::CEditControl*	m_pxEdit;

	CEditorPanel*			m_pxEditorPanel;
};

#endif // ifndef SAVEASDIALOG_H_INCLUDED

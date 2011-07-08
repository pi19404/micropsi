#ifndef UILIBTEST_MESSAGEBOXTESTWINDOW_H_INCLUDED 
#define UILIBTEST_MESSAGEBOXTESTWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"
#include "uilib/controls/messagebox.h"

namespace UILib	{ class CRadioButton; }
namespace UILib	{ class CEditControl; }
namespace UILib	{ class CLabel; }

class CMessageBoxTestWindow : public UILib::CDialogWindow
{
public:

	static CMessageBoxTestWindow * Create();

protected:

	CMessageBoxTestWindow ();
	virtual ~CMessageBoxTestWindow ();

	virtual void	DeleteNow();
	virtual bool	OnClose();

	void	OnIconChange(UILib::CRadioButton* pxRadioButton);
	void	OnButtonsChange(UILib::CRadioButton* pxRadioButton);
	void	OnGoButton(UILib::CBasicButton* pxButton);
	void	OnDecision(UILib::CMessageBox::MsgBoxResults eResult);

	UILib::CRadioButton*	m_pxIconInformation;
	UILib::CRadioButton*	m_pxIconExclamation;
	UILib::CRadioButton*	m_pxIconWarning;

	UILib::CRadioButton*	m_pxButtonOK;
	UILib::CRadioButton*	m_pxButtonOKCANCEL;
	UILib::CRadioButton*	m_pxButtonYESNO;
	UILib::CRadioButton*	m_pxButtonRETRYCANCEL;
	UILib::CRadioButton*	m_pxButtonRETRYIGNORECANCEL;

	UILib::CEditControl*	m_pxCaptionEdit;
	UILib::CEditControl*	m_pxTextEdit;

	UILib::CLabel*			m_pxResultLabel;

	int						m_iIcon;
	int						m_iButtons;
};

#endif // ifndef UILIBTEST_MESSAGEBOXTESTWINDOW_H_INCLUDED 


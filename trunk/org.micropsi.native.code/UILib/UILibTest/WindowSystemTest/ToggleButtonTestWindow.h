#ifndef UILIBTEST_TOGGLEBUTTONTESTWINDOW_H_INCLUDED 
#define UILIBTEST_TOGGLEBUTTONTESTWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"

namespace UILib	{ class CToggleButton; }
namespace UILib	{ class CLabel; }

class CToggleButtonTestWindow : public UILib::CDialogWindow
{
public:

	static CToggleButtonTestWindow * Create();

protected:

	CToggleButtonTestWindow ();
	virtual ~CToggleButtonTestWindow ();

	virtual void	DeleteNow();
	virtual bool	OnClose();

	void	OnStateChange(UILib::CToggleButton* pxButton);
	void	OnGroupStateChange(UILib::CToggleButton* pxButton);
	void	OnToggleAllowUntoggle(UILib::CCheckBox* pxCheckBox);

	UILib::CToggleButton*	m_pxToggleButton;
	UILib::CLabel*			m_pxStatusLabel;
	UILib::CLabel*			m_pxGroupStatusLabel;
};

#endif // ifndef UILIBTEST_TOGGLEBUTTONTESTWINDOW_H_INCLUDED 


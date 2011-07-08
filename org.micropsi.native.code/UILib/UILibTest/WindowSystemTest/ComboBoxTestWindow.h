#ifndef UILIBTEST_COMBOBOXTESTWINDOW_H_INCLUDED 
#define UILIBTEST_COMBOBOXTESTWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"

namespace UILib	{ class CEditControl; }
namespace UILib	{ class CComboBox; }
namespace UILib	{ class CBasicButton; }
namespace UILib	{ class CCheckBox; }

class CComboBoxTestWindow : public UILib::CDialogWindow
{
public:

	static CComboBoxTestWindow * Create();

protected:

	CComboBoxTestWindow ();
	virtual ~CComboBoxTestWindow ();

	virtual void	DeleteNow();
	virtual bool	OnClose();

	void	OnAdd(UILib::CBasicButton* pxButton);
	void	OnDelete(UILib::CBasicButton* pxButton);
	void	OnClear(UILib::CBasicButton* pxButton);
	void	OnToggleAllowAnyText(UILib::CCheckBox* pxCheckBox);
	void	OnChange(UILib::CComboBox* pxListBox);

	UILib::CComboBox*		m_pxComboBox;
	UILib::CEditControl*	m_pxTextEdit;
	UILib::CEditControl*	m_pxResult;
};

#endif // ifndef UILIBTEST_COMBOBOXTESTWINDOW_H_INCLUDED 


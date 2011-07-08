#ifndef UILIBTEST_LISTBOXTESTWINDOW_H_INCLUDED 
#define UILIBTEST_LISTBOXTESTWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"

namespace UILib	{ class CEditControl; }
namespace UILib	{ class CListBox; }
namespace UILib	{ class CBasicButton; }
namespace UILib	{ class CCheckBox; }

class CListBoxTestWindow : public UILib::CDialogWindow
{
public:

	static CListBoxTestWindow * Create();

protected:

	CListBoxTestWindow ();
	virtual ~CListBoxTestWindow ();

	virtual void	DeleteNow();
	virtual bool	OnClose();

	void	OnAdd(UILib::CBasicButton* pxButton);
	void	OnDelete(UILib::CBasicButton* pxButton);
	void	OnClear(UILib::CBasicButton* pxButton);
	void	OnToggleScrollBar(UILib::CCheckBox* pxCheckBox);
	void	OnToggleMultiSelect(UILib::CCheckBox* pxCheckBox);
	void	OnSelect(UILib::CListBox* pxListBox);

	UILib::CListBox*		m_pxListBox;
	UILib::CEditControl*	m_pxTextEdit;
	UILib::CEditControl*	m_pxResult;
};

#endif // ifndef UILIBTEST_LISTBOXTESTWINDOW_H_INCLUDED 


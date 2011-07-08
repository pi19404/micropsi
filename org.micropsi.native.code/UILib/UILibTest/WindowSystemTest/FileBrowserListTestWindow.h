#ifndef UILIBTEST_FILEBROWSERLISTTESTWINDOW_H_INCLUDED 
#define UILIBTEST_FILEBROWSERLISTTESTWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"

namespace UILib	{ class CEditControl; }
namespace UILib	{ class CFileBrowserList; }
namespace UILib	{ class CBasicButton; }
namespace UILib	{ class CLabel; }

class CFileBrowserListTestWindow : public UILib::CDialogWindow
{
public:

	static CFileBrowserListTestWindow * Create();

protected:

	CFileBrowserListTestWindow ();
	virtual ~CFileBrowserListTestWindow ();

	virtual void	DeleteNow();
	virtual bool	OnClose();

	void	OnRefresh(UILib::CBasicButton* pxButton);
	void	OnDirUp(UILib::CBasicButton* pxButton);
	void	OnSetFilter(UILib::CBasicButton* pxButton);
	void	OnSelect(UILib::CListBox* pxButton);

	UILib::CFileBrowserList*	m_pxFileBrowserList;
	UILib::CLabel*				m_pxPath;
	UILib::CLabel*				m_pxType;
	UILib::CEditControl*		m_pxFilterEdit;
};

#endif // ifndef UILIBTEST_FILEBROWSERLISTTESTWINDOW_H_INCLUDED 


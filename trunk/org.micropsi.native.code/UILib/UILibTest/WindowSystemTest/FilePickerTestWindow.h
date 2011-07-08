#ifndef UILIBTEST_FILEPICKERTESTWINDOW_H_INCLUDED 
#define UILIBTEST_FILEPICKERTESTWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"

namespace UILib	{ class CEditControl; }
namespace UILib	{ class CFilePicker; }
namespace UILib	{ class CBasicButton; }
namespace UILib	{ class CLabel; }

class CFilePickerTestWindow : public UILib::CDialogWindow
{
public:

	static CFilePickerTestWindow * Create();

protected:

	CFilePickerTestWindow ();
	virtual ~CFilePickerTestWindow ();

	virtual void	DeleteNow();
	virtual bool	OnClose();

	UILib::CFilePicker*		m_pxFilePicker;
	UILib::CLabel*			m_pxPath;
	UILib::CLabel*			m_pxFile;
	UILib::CEditControl*	m_pxFilterEdit;
};

#endif // ifndef UILIBTEST_FILEPICKERTESTWINDOW_H_INCLUDED 


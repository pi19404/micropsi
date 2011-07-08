#ifndef UILIBTEST_MAINWINDOW_H_INCLUDED 
#define UILIBTEST_MAINWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"
#include "uilib/controls/basicbutton.h"

class CWTMainWindow : public UILib::CDialogWindow
{
public:

	static CWTMainWindow* Create();

protected:

	CWTMainWindow();
	virtual ~CWTMainWindow();

	virtual void	DeleteNow();

	void			SetToRandomPos(CWindow* pxWindow);

	void			OnTestLabel(UILib::CBasicButton* pxButton);
	void			OnTestSlider(UILib::CBasicButton* pxButton);
	void			OnTestScrollBar(UILib::CBasicButton* pxButton);
	void			OnTestMessageBox(UILib::CBasicButton* pxButton);
	void			OnTestListBox(UILib::CBasicButton* pxButton);
	void			OnTestToggleButton(UILib::CBasicButton* pxButton);
	void			OnTestFilePicker(UILib::CBasicButton* pxButton);
	void			OnTestFileBrowserList(UILib::CBasicButton* pxButton);
	void			OnTestComboBox(UILib::CBasicButton* pxButton);
};

#endif // ifndef UILIBTEST_MAINWINDOW_H_INCLUDED 


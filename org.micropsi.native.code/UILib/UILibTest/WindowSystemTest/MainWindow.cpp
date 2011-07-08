#include "stdafx.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/button.h"

#include "LabelTestWindow.h"
#include "SliderTestWindow.h"
#include "ScrollBarTestWindow.h"
#include "MessageBoxTestWindow.h"
#include "ListBoxTestWindow.h"
#include "ToggleButtonTestWindow.h"
#include "FilePickerTestWindow.h"
#include "FileBrowserListTestWindow.h"
#include "ComboBoxTestWindow.h"

#include "MainWindow.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CWTMainWindow* 
CWTMainWindow::Create()
{
	return new CWTMainWindow;
}
//---------------------------------------------------------------------------------------------------------------------
CWTMainWindow::CWTMainWindow()
{
	SetCaption("UILib Window-System Test");
	SetSize(280, 400);
	SetPos(20, 20);

	const int iNumButtons = 15;
	const int iButtonsPerRow = 2;
	CButton* ppxButton[iNumButtons];
	for(int i=0; i<iNumButtons; ++i)
	{
		ppxButton[i] = CButton::Create();
		ppxButton[i]->SetSize(CSize(120, 30));
		ppxButton[i]->SetPos(10 + (i % iButtonsPerRow) * 130, 10 + i / iButtonsPerRow * 40);
		ppxButton[i]->SetDisabled(true);
		AddChild(ppxButton[i]->GetWHDL());
	}

	ppxButton[0]->SetText("CLabel");
	ppxButton[0]->SetOnClickCallback(CreateFunctionPointer1(this, CWTMainWindow::OnTestLabel));
	ppxButton[0]->SetDisabled(false);

	ppxButton[1]->SetText("CSlider");
	ppxButton[1]->SetOnClickCallback(CreateFunctionPointer1(this, CWTMainWindow::OnTestSlider));
	ppxButton[1]->SetDisabled(false);

	ppxButton[2]->SetText("CScrollbar");
	ppxButton[2]->SetOnClickCallback(CreateFunctionPointer1(this, CWTMainWindow::OnTestScrollBar));
	ppxButton[2]->SetDisabled(false);

	ppxButton[3]->SetText("CMessageBox");
	ppxButton[3]->SetOnClickCallback(CreateFunctionPointer1(this, CWTMainWindow::OnTestMessageBox));
	ppxButton[3]->SetDisabled(false);

	ppxButton[4]->SetText("CListBox");
	ppxButton[4]->SetOnClickCallback(CreateFunctionPointer1(this, CWTMainWindow::OnTestListBox));
	ppxButton[4]->SetDisabled(false);

	ppxButton[5]->SetText("CToggleButton");
	ppxButton[5]->SetOnClickCallback(CreateFunctionPointer1(this, CWTMainWindow::OnTestToggleButton));
	ppxButton[5]->SetDisabled(false);

	ppxButton[6]->SetText("CFilePicker");
	ppxButton[6]->SetOnClickCallback(CreateFunctionPointer1(this, CWTMainWindow::OnTestFilePicker));
	ppxButton[6]->SetDisabled(false);

	ppxButton[7]->SetText("CFileBrowserList");
	ppxButton[7]->SetOnClickCallback(CreateFunctionPointer1(this, CWTMainWindow::OnTestFileBrowserList));
	ppxButton[7]->SetDisabled(false);

	ppxButton[8]->SetText("CComboBox");
	ppxButton[8]->SetOnClickCallback(CreateFunctionPointer1(this, CWTMainWindow::OnTestComboBox));
	ppxButton[8]->SetDisabled(false);
}
//---------------------------------------------------------------------------------------------------------------------
CWTMainWindow::~CWTMainWindow()
{
}
//---------------------------------------------------------------------------------------------------------------------
void	
CWTMainWindow::DeleteNow()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
void
CWTMainWindow::OnTestLabel(CBasicButton* pxButton)
{
	CLabelTestWindow* pxWnd = CLabelTestWindow::Create();
	CWindowMgr::Get().AddTopLevelWindow(pxWnd->GetWHDL());
	SetToRandomPos(pxWnd);
}
//---------------------------------------------------------------------------------------------------------------------
void
CWTMainWindow::OnTestSlider(CBasicButton* pxButton)
{
	CSliderTestWindow* pxWnd = CSliderTestWindow::Create();
	CWindowMgr::Get().AddTopLevelWindow(pxWnd->GetWHDL());
	SetToRandomPos(pxWnd);
}
//---------------------------------------------------------------------------------------------------------------------
void
CWTMainWindow::OnTestScrollBar(CBasicButton* pxButton)
{
	CScrollBarTestWindow* pxWnd = CScrollBarTestWindow::Create();
	CWindowMgr::Get().AddTopLevelWindow(pxWnd->GetWHDL());
	SetToRandomPos(pxWnd);
}
//---------------------------------------------------------------------------------------------------------------------
void
CWTMainWindow::OnTestMessageBox(UILib::CBasicButton* pxButton)
{
	CMessageBoxTestWindow* pxWnd = CMessageBoxTestWindow::Create();
	CWindowMgr::Get().AddTopLevelWindow(pxWnd->GetWHDL());
	SetToRandomPos(pxWnd);
}
//---------------------------------------------------------------------------------------------------------------------
void
CWTMainWindow::OnTestListBox(UILib::CBasicButton* pxButton)
{
	CListBoxTestWindow* pxWnd = CListBoxTestWindow::Create();
	CWindowMgr::Get().AddTopLevelWindow(pxWnd->GetWHDL());
	SetToRandomPos(pxWnd);
}
//---------------------------------------------------------------------------------------------------------------------
void
CWTMainWindow::OnTestToggleButton(UILib::CBasicButton* pxButton)
{
	CToggleButtonTestWindow* pxWnd = CToggleButtonTestWindow::Create();
	CWindowMgr::Get().AddTopLevelWindow(pxWnd->GetWHDL());
	SetToRandomPos(pxWnd);
}
//---------------------------------------------------------------------------------------------------------------------
void
CWTMainWindow::OnTestFilePicker(UILib::CBasicButton* pxButton)
{
	CFilePickerTestWindow* pxWnd = CFilePickerTestWindow::Create();
	CWindowMgr::Get().AddTopLevelWindow(pxWnd->GetWHDL());
	SetToRandomPos(pxWnd);
}
//---------------------------------------------------------------------------------------------------------------------
void
CWTMainWindow::OnTestFileBrowserList(UILib::CBasicButton* pxButton)
{
	CFileBrowserListTestWindow* pxWnd = CFileBrowserListTestWindow::Create();
	CWindowMgr::Get().AddTopLevelWindow(pxWnd->GetWHDL());
	SetToRandomPos(pxWnd);
}
//---------------------------------------------------------------------------------------------------------------------
void
CWTMainWindow::OnTestComboBox(UILib::CBasicButton* pxButton)
{
	CComboBoxTestWindow* pxWnd = CComboBoxTestWindow::Create();
	CWindowMgr::Get().AddTopLevelWindow(pxWnd->GetWHDL());
	SetToRandomPos(pxWnd);
}//---------------------------------------------------------------------------------------------------------------------
void			
CWTMainWindow::SetToRandomPos(CWindow* pxWindow)
{
	pxWindow->SetPos(CPnt(200 + rand() % 100, rand() % 100));
}
//---------------------------------------------------------------------------------------------------------------------

#include "stdafx.h"

#include "uilib/controls/label.h"
#include "uilib/controls/editcontrol.h"
#include "uilib/controls/button.h"
#include "uilib/controls/label.h"

#include "FilePickerTestWindow.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CFilePickerTestWindow * 
CFilePickerTestWindow::Create()
{
	return new CFilePickerTestWindow ;
}
//---------------------------------------------------------------------------------------------------------------------
CFilePickerTestWindow::CFilePickerTestWindow ()
{
	SetCaption("UILib CFilePicker Test");
	SetSize(424, 490);
	SetHasCloseButton(true);
}
//---------------------------------------------------------------------------------------------------------------------
CFilePickerTestWindow::~CFilePickerTestWindow ()
{
}
//---------------------------------------------------------------------------------------------------------------------
void	
CFilePickerTestWindow::DeleteNow()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CFilePickerTestWindow::OnClose()
{
	Destroy();
	return true;
}
//---------------------------------------------------------------------------------------------------------------------

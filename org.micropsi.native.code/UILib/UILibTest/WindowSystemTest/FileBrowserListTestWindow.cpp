#include "stdafx.h"

#include "uilib/controls/label.h"
#include "uilib/controls/editcontrol.h"
#include "uilib/controls/button.h"
#include "uilib/controls/label.h"
#include "uilib/controls/filebrowserlist.h"

#include "FileBrowserListTestWindow.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CFileBrowserListTestWindow * 
CFileBrowserListTestWindow::Create()
{
	return new CFileBrowserListTestWindow ;
}
//---------------------------------------------------------------------------------------------------------------------
CFileBrowserListTestWindow::CFileBrowserListTestWindow ()
{
	SetCaption("UILib CFilePicker Test");
	SetSize(424, 520);
	SetHasCloseButton(true);

	m_pxFileBrowserList = CFileBrowserList::Create();
	m_pxFileBrowserList->SetSize(400, 300);
	m_pxFileBrowserList->SetPos(10, 10);
	AddChild(m_pxFileBrowserList->GetWHDL());
	m_pxFileBrowserList->SetOnSelectCallback(CreateFunctionPointer1(this, CFileBrowserListTestWindow::OnSelect));

	// Buttons

	m_pxFilterEdit = CEditControl::Create();
	m_pxFilterEdit->SetSize(200, 20);
	m_pxFilterEdit->SetPos(10, 360);
	m_pxFilterEdit->SetMultiLine(false);
	m_pxFilterEdit->SetText("*.txt");
	AddChild(m_pxFilterEdit->GetWHDL());

	CButton* pxButton;
	
	pxButton = CButton::Create();
	pxButton->SetSize(100, 30);
	pxButton->SetPos(10, 320);
	pxButton->SetText("Refresh");
	AddChild(pxButton->GetWHDL());
	pxButton->SetOnClickCallback(CreateFunctionPointer1(this, CFileBrowserListTestWindow::OnRefresh));

	pxButton = CButton::Create();
	pxButton->SetSize(100, 30);
	pxButton->SetPos(120, 320);
	pxButton->SetText("Dir up");
	AddChild(pxButton->GetWHDL());
	pxButton->SetOnClickCallback(CreateFunctionPointer1(this, CFileBrowserListTestWindow::OnDirUp));

	pxButton = CButton::Create();
	pxButton->SetSize(100, 20);
	pxButton->SetPos(220, 360);
	pxButton->SetText("Set Filter");
	AddChild(pxButton->GetWHDL());
	pxButton->SetOnClickCallback(CreateFunctionPointer1(this, CFileBrowserListTestWindow::OnSetFilter));

	// Selection - Anzeige

	m_pxPath = CLabel::Create();
	m_pxPath->SetSize(400, 20);
	m_pxPath->SetPos(10, 400);
	m_pxPath->SetText("1");
	m_pxPath->SetTextAlign(CLabel::TA_Left);
	AddChild(m_pxPath->GetWHDL());

	m_pxType = CLabel::Create();
	m_pxType->SetSize(400, 20);
	m_pxType->SetPos(10, 420);
	m_pxType->SetText("2");
	m_pxType->SetTextAlign(CLabel::TA_Left);
	AddChild(m_pxType->GetWHDL());

	OnSelect(m_pxFileBrowserList);
}
//---------------------------------------------------------------------------------------------------------------------
CFileBrowserListTestWindow::~CFileBrowserListTestWindow ()
{
}
//---------------------------------------------------------------------------------------------------------------------
void	
CFileBrowserListTestWindow::DeleteNow()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CFileBrowserListTestWindow::OnClose()
{
	Destroy();
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
void
CFileBrowserListTestWindow::OnRefresh(UILib::CBasicButton* pxButton)
{
	m_pxFileBrowserList->Update();
}
//---------------------------------------------------------------------------------------------------------------------
void
CFileBrowserListTestWindow::OnDirUp(UILib::CBasicButton* pxButton)
{
	m_pxFileBrowserList->ChangeToParentDir();
}
//---------------------------------------------------------------------------------------------------------------------
void
CFileBrowserListTestWindow::OnSetFilter(UILib::CBasicButton* pxButton)
{
	m_pxFileBrowserList->SetFilterRule("blubb", m_pxFilterEdit->GetText());
}
//---------------------------------------------------------------------------------------------------------------------
void
CFileBrowserListTestWindow::OnSelect(UILib::CListBox* pxButton)
{
	m_pxPath->SetText(m_pxFileBrowserList->GetSelectedItemPath());
	if(m_pxFileBrowserList->IsFile(m_pxFileBrowserList->GetSelectedItem()))
	{
		m_pxType->SetText("Type: File");
	}
	else if(m_pxFileBrowserList->IsDrive(m_pxFileBrowserList->GetSelectedItem()))
	{
		m_pxType->SetText("Type: Drive");
	}
	else if(m_pxFileBrowserList->IsFolder(m_pxFileBrowserList->GetSelectedItem()))
	{
		m_pxType->SetText("Type: Directory");
	}
	else
	{
		m_pxType->SetText("Type: invalid (may be . or ..)");
	}

}
//---------------------------------------------------------------------------------------------------------------------

#include "stdafx.h"

#include "uilib/controls/label.h"
#include "uilib/controls/checkbox.h"
#include "uilib/controls/editcontrol.h"
#include "uilib/controls/button.h"
#include "uilib/controls/listbox.h"

#include "ListBoxTestWindow.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CListBoxTestWindow * 
CListBoxTestWindow ::Create()
{
	return new CListBoxTestWindow ;
}
//---------------------------------------------------------------------------------------------------------------------
CListBoxTestWindow ::CListBoxTestWindow ()
{
	SetCaption("UILib CListBox Test");
	SetSize(424, 490);
	SetHasCloseButton(true);

	m_pxListBox = CListBox::Create();
	m_pxListBox->SetSize(190, 300);
	m_pxListBox->SetPos(10, 10);
	m_pxListBox->AddItem("rot");
	m_pxListBox->AddItem("grün");
	m_pxListBox->AddItem("blau");
	m_pxListBox->AddItem("gelb");
	m_pxListBox->AddItem("pink");
	AddChild(m_pxListBox->GetWHDL());
	m_pxListBox->SetOnSelectCallback(CreateFunctionPointer1(this, CListBoxTestWindow::OnSelect));

	// Buttons

	m_pxTextEdit = CEditControl::Create();
	m_pxTextEdit->SetSize(200, 20);
	m_pxTextEdit->SetPos(210, 10);
	m_pxTextEdit->SetMultiLine(false);
	m_pxTextEdit->SetText("new Entry");
	AddChild(m_pxTextEdit->GetWHDL());

	CButton* pxButton;
	
	pxButton = CButton::Create();
	pxButton->SetSize(130, 30);
	pxButton->SetPos(210, 40);
	pxButton->SetText("Add New Item");
	AddChild(pxButton->GetWHDL());
	pxButton->SetOnClickCallback(CreateFunctionPointer1(this, CListBoxTestWindow::OnAdd));

	pxButton = CButton::Create();
	pxButton->SetSize(130, 30);
	pxButton->SetPos(210, 80);
	pxButton->SetText("Delete Selected Item");
	AddChild(pxButton->GetWHDL());
	pxButton->SetOnClickCallback(CreateFunctionPointer1(this, CListBoxTestWindow::OnDelete));

	pxButton = CButton::Create();
	pxButton->SetSize(130, 30);
	pxButton->SetPos(210, 120);
	pxButton->SetText("Clear");
	AddChild(pxButton->GetWHDL());
	pxButton->SetOnClickCallback(CreateFunctionPointer1(this, CListBoxTestWindow::OnClear));

	CCheckBox* pxCheckBox;
	
	pxCheckBox = CCheckBox::Create();
	pxCheckBox->SetSize(130, 30);
	pxCheckBox->SetPos(210, 160);
	pxCheckBox->SetText("Multiselect");
	pxCheckBox->SetChecked(m_pxListBox->GetAllowMultiSelection() ? 1 : 0);
	AddChild(pxCheckBox->GetWHDL());
	pxCheckBox->SetOnStateChangeCallback(CreateFunctionPointer1(this, CListBoxTestWindow::OnToggleMultiSelect));

	pxCheckBox = CCheckBox::Create();
	pxCheckBox->SetSize(130, 30);
	pxCheckBox->SetPos(210, 190);
	pxCheckBox->SetText("Scrollbar allowed");
	pxCheckBox->SetChecked(m_pxListBox->GetAllowScrollBar() ? 1 : 0);
	AddChild(pxCheckBox->GetWHDL());
	pxCheckBox->SetOnStateChangeCallback(CreateFunctionPointer1(this, CListBoxTestWindow::OnToggleScrollBar));


	// Selection - Anzeige

	CLabel* pxLabel = CLabel::Create();
	pxLabel->SetSize(150, 20);
	pxLabel->SetPos(210, 220);
	pxLabel->SetText("Current Selection:");
	pxLabel->SetTextAlign(CLabel::TA_Left);
	AddChild(pxLabel->GetWHDL());

	m_pxResult = CEditControl::Create();
	m_pxResult->SetSize(200, 50);
	m_pxResult->SetPos(210, 250);
	m_pxResult->SetMultiLine(true);
	m_pxResult->SetReadOnly(true);
	m_pxResult->SetText("");
	AddChild(m_pxResult->GetWHDL());

	// erstes Update
	OnSelect(m_pxListBox);
}
//---------------------------------------------------------------------------------------------------------------------
CListBoxTestWindow ::~CListBoxTestWindow ()
{
}
//---------------------------------------------------------------------------------------------------------------------
void	
CListBoxTestWindow::DeleteNow()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CListBoxTestWindow::OnClose()
{
	Destroy();
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
void
CListBoxTestWindow::OnAdd(UILib::CBasicButton* pxButton)
{
	m_pxListBox->AddItem(m_pxTextEdit->GetText());
}
//---------------------------------------------------------------------------------------------------------------------
void
CListBoxTestWindow::OnDelete(UILib::CBasicButton* pxButton)
{
	if(m_pxListBox->GetSelectedItem() >= 0)
	{
		m_pxListBox->DeleteItem(m_pxListBox->GetSelectedItem());
	}
}
//---------------------------------------------------------------------------------------------------------------------
void
CListBoxTestWindow::OnClear(UILib::CBasicButton* pxButton)
{
	m_pxListBox->Clear();
}
//---------------------------------------------------------------------------------------------------------------------
void
CListBoxTestWindow::OnToggleScrollBar(UILib::CCheckBox* pxCheckBox)
{
	m_pxListBox->SetAllowScrollBar(pxCheckBox->GetCheckMark());
}
//---------------------------------------------------------------------------------------------------------------------
void
CListBoxTestWindow::OnToggleMultiSelect(UILib::CCheckBox* pxCheckBox)
{
	m_pxListBox->SetAllowMultiSelection(pxCheckBox->GetCheckMark());
}
//---------------------------------------------------------------------------------------------------------------------
void
CListBoxTestWindow::OnSelect(UILib::CListBox* pxListBox)
{
	CStr sSelection;
	int iItem;
	m_pxListBox->StartIterateSelectedItems(iItem);
	while(m_pxListBox->IterateSelectedItems(iItem))
	{
		if(!sSelection.IsEmpty())
		{
			sSelection += ", ";
		}
		sSelection += m_pxListBox->GetItem(iItem);
	}
	m_pxResult->SetText(sSelection);
}
//---------------------------------------------------------------------------------------------------------------------

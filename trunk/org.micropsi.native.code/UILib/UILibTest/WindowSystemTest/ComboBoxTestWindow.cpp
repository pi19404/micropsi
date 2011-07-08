#include "stdafx.h"

#include "uilib/controls/label.h"
#include "uilib/controls/checkbox.h"
#include "uilib/controls/editcontrol.h"
#include "uilib/controls/button.h"
#include "uilib/controls/combobox.h"

#include "ComboBoxTestWindow.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CComboBoxTestWindow * 
CComboBoxTestWindow ::Create()
{
	return new CComboBoxTestWindow ;
}
//---------------------------------------------------------------------------------------------------------------------
CComboBoxTestWindow ::CComboBoxTestWindow ()
{
	SetCaption("UILib CComboBox Test");
	SetSize(424, 490);
	SetHasCloseButton(true);

	m_pxComboBox = CComboBox::Create();
	m_pxComboBox->SetSize(190, 30);
	m_pxComboBox->SetPos(10, 10);
	m_pxComboBox->AddItem("rot");
	m_pxComboBox->AddItem("grün");
	m_pxComboBox->AddItem("blau");
	m_pxComboBox->AddItem("gelb");
	m_pxComboBox->AddItem("pink");
	AddChild(m_pxComboBox->GetWHDL());
	m_pxComboBox->SetOnChangeCallback(CreateFunctionPointer1(this, CComboBoxTestWindow::OnChange));

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
	pxButton->SetOnClickCallback(CreateFunctionPointer1(this, CComboBoxTestWindow::OnAdd));

	pxButton = CButton::Create();
	pxButton->SetSize(130, 30);
	pxButton->SetPos(210, 80);
	pxButton->SetText("Delete Selected Item");
	AddChild(pxButton->GetWHDL());
	pxButton->SetOnClickCallback(CreateFunctionPointer1(this, CComboBoxTestWindow::OnDelete));

	pxButton = CButton::Create();
	pxButton->SetSize(130, 30);
	pxButton->SetPos(210, 120);
	pxButton->SetText("Clear");
	AddChild(pxButton->GetWHDL());
	pxButton->SetOnClickCallback(CreateFunctionPointer1(this, CComboBoxTestWindow::OnClear));

	CCheckBox* pxCheckBox;
	
	pxCheckBox = CCheckBox::Create();
	pxCheckBox->SetSize(130, 30);
	pxCheckBox->SetPos(210, 160);
	pxCheckBox->SetText("Allow Any Text");
	pxCheckBox->SetChecked(m_pxComboBox->GetAllowAnyText() ? 1 : 0);
	AddChild(pxCheckBox->GetWHDL());
	pxCheckBox->SetOnStateChangeCallback(CreateFunctionPointer1(this, CComboBoxTestWindow::OnToggleAllowAnyText));

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
	OnChange(m_pxComboBox);
}
//---------------------------------------------------------------------------------------------------------------------
CComboBoxTestWindow ::~CComboBoxTestWindow ()
{
}
//---------------------------------------------------------------------------------------------------------------------
void	
CComboBoxTestWindow::DeleteNow()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CComboBoxTestWindow::OnClose()
{
	Destroy();
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
void
CComboBoxTestWindow::OnAdd(UILib::CBasicButton* pxButton)
{
	m_pxComboBox->AddItem(m_pxTextEdit->GetText());
}
//---------------------------------------------------------------------------------------------------------------------
void
CComboBoxTestWindow::OnDelete(UILib::CBasicButton* pxButton)
{
	if(m_pxComboBox->GetSelectedItem() >= 0)
	{
		m_pxComboBox->DeleteItem(m_pxComboBox->GetSelectedItem());
	}
}
//---------------------------------------------------------------------------------------------------------------------
void
CComboBoxTestWindow::OnClear(UILib::CBasicButton* pxButton)
{
	m_pxComboBox->Clear();
}
//---------------------------------------------------------------------------------------------------------------------
void
CComboBoxTestWindow::OnToggleAllowAnyText(UILib::CCheckBox* pxCheckBox)
{
	m_pxComboBox->SetAllowAnyText(pxCheckBox->GetCheckMark());
}
//---------------------------------------------------------------------------------------------------------------------
void
CComboBoxTestWindow::OnChange(UILib::CComboBox* pxListBox)
{
	m_pxResult->SetText(m_pxComboBox->GetText());
}
//---------------------------------------------------------------------------------------------------------------------

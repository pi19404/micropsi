#include "stdafx.h"

#include "uilib/controls/label.h"
#include "uilib/controls/checkbox.h"
#include "uilib/controls/togglebutton.h"
#include "uilib/controls/groupbox.h"

#include "ToggleButtonTestWindow.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CToggleButtonTestWindow * 
CToggleButtonTestWindow ::Create()
{
	return new CToggleButtonTestWindow ;
}
//---------------------------------------------------------------------------------------------------------------------
CToggleButtonTestWindow ::CToggleButtonTestWindow ()
{
	SetCaption("UILib CToggleButton Test");
	SetSize(424, 490);
	SetHasCloseButton(true);

	// Linke Seite - einzelner Button

	m_pxToggleButton = CToggleButton::Create();
	m_pxToggleButton->SetSize(150, 25);
	m_pxToggleButton->SetPos(25, 25);
	AddChild(m_pxToggleButton->GetWHDL());
	m_pxToggleButton->SetOnStateChangeCallback(CreateFunctionPointer1(this, CToggleButtonTestWindow::OnStateChange));

	CCheckBox* pxCheckBox;
	
	pxCheckBox = CCheckBox::Create();
	pxCheckBox->SetSize(130, 30);
	pxCheckBox->SetPos(10, 80);
	pxCheckBox->SetText("Allow Untoggle");
	pxCheckBox->SetChecked(m_pxToggleButton->GetAllowUntoggle() ? 1 : 0);
	AddChild(pxCheckBox->GetWHDL());
	pxCheckBox->SetOnStateChangeCallback(CreateFunctionPointer1(this, CToggleButtonTestWindow::OnToggleAllowUntoggle));

	m_pxStatusLabel = CLabel::Create();
	m_pxStatusLabel->SetSize(150, 20);
	m_pxStatusLabel->SetPos(10, 120);
	m_pxStatusLabel->SetTextAlign(CLabel::TA_Left);
	AddChild(m_pxStatusLabel->GetWHDL());

	OnStateChange(m_pxToggleButton);

	// Rechte Seite - Gruppe von Buttons

	CGroupBox* pxGroup = CGroupBox::Create();
	pxGroup->SetSize(200, 70);
	pxGroup->SetPos(200, 10);
	pxGroup->SetText("ToggleButton Group");
	AddChild(pxGroup->GetWHDL());

	m_pxGroupStatusLabel = CLabel::Create();
	m_pxGroupStatusLabel->SetSize(150, 20);
	m_pxGroupStatusLabel->SetPos(200, 120);
	m_pxGroupStatusLabel->SetTextAlign(CLabel::TA_Left);
	AddChild(m_pxGroupStatusLabel->GetWHDL());

	CToggleButton* pxGroupedToggleButton;

	pxGroupedToggleButton = CToggleButton::Create();
	pxGroupedToggleButton->SetSize(40, 20);
	pxGroupedToggleButton->SetPos(5, 5);
	pxGroupedToggleButton->SetText("one");
	pxGroupedToggleButton->SetGrouped(true);
	pxGroupedToggleButton->SetAllowUntoggle(false);
	pxGroupedToggleButton->SetOnStateChangeCallback(CreateFunctionPointer1(this, CToggleButtonTestWindow::OnGroupStateChange));
	pxGroup->AddChild(pxGroupedToggleButton->GetWHDL());
	pxGroupedToggleButton->SetToggleButtonState(true);

	pxGroupedToggleButton = CToggleButton::Create();
	pxGroupedToggleButton->SetSize(40, 20);
	pxGroupedToggleButton->SetPos(50, 5);
	pxGroupedToggleButton->SetText("two");
	pxGroupedToggleButton->SetGrouped(true);
	pxGroupedToggleButton->SetAllowUntoggle(false);
	pxGroupedToggleButton->SetOnStateChangeCallback(CreateFunctionPointer1(this, CToggleButtonTestWindow::OnGroupStateChange));
	pxGroup->AddChild(pxGroupedToggleButton->GetWHDL());

	pxGroupedToggleButton = CToggleButton::Create();
	pxGroupedToggleButton->SetSize(40, 20);
	pxGroupedToggleButton->SetPos(95, 5);
	pxGroupedToggleButton->SetText("three");
	pxGroupedToggleButton->SetGrouped(true);
	pxGroupedToggleButton->SetAllowUntoggle(false);
	pxGroupedToggleButton->SetOnStateChangeCallback(CreateFunctionPointer1(this, CToggleButtonTestWindow::OnGroupStateChange));
	pxGroup->AddChild(pxGroupedToggleButton->GetWHDL());
}
//---------------------------------------------------------------------------------------------------------------------
CToggleButtonTestWindow ::~CToggleButtonTestWindow ()
{
}
//---------------------------------------------------------------------------------------------------------------------
void	
CToggleButtonTestWindow::DeleteNow()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CToggleButtonTestWindow::OnClose()
{
	Destroy();
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
void
CToggleButtonTestWindow::OnStateChange(UILib::CToggleButton* pxButton)
{
	if(pxButton->GetToggleButtonState())
	{
		m_pxStatusLabel->SetText("State: on");
	}
	else
	{
		m_pxStatusLabel->SetText("State: off");
	}
}
//---------------------------------------------------------------------------------------------------------------------
void
CToggleButtonTestWindow::OnGroupStateChange(UILib::CToggleButton* pxButton)
{
	if(pxButton->GetToggleButtonState())
	{
		m_pxGroupStatusLabel->SetText(CStr("Group State: ") + pxButton->GetText());
	}
}
//---------------------------------------------------------------------------------------------------------------------
void
CToggleButtonTestWindow::OnToggleAllowUntoggle(UILib::CCheckBox* pxCheckBox)
{
	m_pxToggleButton->SetAllowUntoggle(pxCheckBox->GetCheckMark());
}
//---------------------------------------------------------------------------------------------------------------------

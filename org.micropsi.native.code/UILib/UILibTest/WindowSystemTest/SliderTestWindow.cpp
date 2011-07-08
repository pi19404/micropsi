#include "stdafx.h"

#include "uilib/controls/panel.h"
#include "uilib/controls/label.h"
#include "uilib/controls/slider.h"
#include "uilib/controls/groupbox.h"
#include "uilib/controls/checkbox.h"
#include "uilib/controls/spincontrolnumber.h"

#include "SliderTestWindow.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CSliderTestWindow * 
CSliderTestWindow ::Create()
{
	return new CSliderTestWindow ;
}
//---------------------------------------------------------------------------------------------------------------------
CSliderTestWindow ::CSliderTestWindow ()
{
	SetCaption("UILib CSlider Test");
	SetSize(424, 490);
	SetHasCloseButton(true);

	// Hintergrund und Label

	CPanel* pxBackGround = CPanel::Create();
	pxBackGround->SetSize(400, 200);
	pxBackGround->SetPos(10, 10);
	pxBackGround->SetColor(CColor(210, 210, 255));
	AddChild(pxBackGround->GetWHDL());

	// Slider

	m_pxHSlider = CSlider::Create();
	m_pxHSlider->SetPos(10, 100);
	m_pxHSlider->SetStyle(CSlider::SL_Horizontal);
	m_pxHSlider->SetSize(180, 20);
	m_pxHSlider->SetSliderRange(20);
	m_pxHSlider->SetBackground(false);
	pxBackGround->AddChild(m_pxHSlider->GetWHDL());

	m_pxVSlider = CSlider::Create();
	m_pxVSlider->SetPos(290, 10);
	m_pxVSlider->SetStyle(CSlider::SL_Vertical);
	m_pxVSlider->SetSize(20, 180);
	m_pxVSlider->SetSliderRange(20);
	m_pxVSlider->SetBackground(false);
	pxBackGround->AddChild(m_pxVSlider->GetWHDL());

	m_pxHSlider->SetOnChangeCallback(CreateFunctionPointer1(this, CSliderTestWindow::OnSliderChange));
	m_pxVSlider->SetOnChangeCallback(CreateFunctionPointer1(this, CSliderTestWindow::OnSliderChange));

	// Spin Controls für Position, Range

	CGroupBox* pxGroupH = CGroupBox::Create();
	pxGroupH->SetSize(190, 90);
	pxGroupH->SetPos(10, 220);
	pxGroupH->SetText("Horizontal Slider");
	AddChild(pxGroupH->GetWHDL());
	
	CGroupBox* pxGroupV = CGroupBox::Create();
	pxGroupV->SetSize(190, 90);
	pxGroupV->SetPos(220, 220);
	pxGroupV->SetText("Vertical Slider");
	AddChild(pxGroupV->GetWHDL());

	CLabel* pxLabel;
	pxLabel = CLabel::Create();
	pxLabel->SetText("Position:");
	pxLabel->SetTextAlign(CLabel::TA_Left);
	pxLabel->SetSize(100, 20);
	pxLabel->SetPos(5, 0);
	pxGroupH->AddChild(pxLabel->GetWHDL());

	pxLabel = CLabel::Create();
	pxLabel->SetText("Range:");
	pxLabel->SetTextAlign(CLabel::TA_Left);
	pxLabel->SetSize(100, 20);
	pxLabel->SetPos(5, 20);
	pxGroupH->AddChild(pxLabel->GetWHDL());

	pxLabel = CLabel::Create();
	pxLabel->SetText("Position:");
	pxLabel->SetTextAlign(CLabel::TA_Left);
	pxLabel->SetSize(100, 20);
	pxLabel->SetPos(5, 0);
	pxGroupV->AddChild(pxLabel->GetWHDL());

	pxLabel = CLabel::Create();
	pxLabel->SetText("Range:");
	pxLabel->SetTextAlign(CLabel::TA_Left);
	pxLabel->SetSize(100, 20);
	pxLabel->SetPos(5, 20);
	pxGroupV->AddChild(pxLabel->GetWHDL());

	m_pxHPosition = CSpinControlNumber::Create();
	m_pxHPosition->SetPos(105, 0);
	m_pxHPosition->SetSize(CSize(70, 20));
	m_pxHPosition->SetDecimals(0);
	m_pxHPosition->SetLimits(0, m_pxHSlider->GetSliderRange(), 1);
	m_pxHPosition->SetValue(m_pxHSlider->GetSliderPos());
	pxGroupH->AddChild(m_pxHPosition->GetWHDL());
	m_pxHPosition->SetOnChangeCallback(CreateFunctionPointer1(this, CSliderTestWindow::OnHPositionChange));

	CSpinControlNumber* pxRange = CSpinControlNumber::Create();
	pxRange->SetPos(105, 20);
	pxRange->SetSize(CSize(70, 20));
	pxRange->SetDecimals(0);
	pxRange->SetLimits(0, 1000, 1);
	pxRange->SetValue(m_pxHSlider->GetSliderRange());
	pxRange->SetStyle(CSpinControlNumber::SC_UpDown);
	pxGroupH->AddChild(pxRange->GetWHDL());
	pxRange->SetOnChangeCallback(CreateFunctionPointer1(this, CSliderTestWindow::OnHRangeChange));

	m_pxVPosition = CSpinControlNumber::Create();
	m_pxVPosition->SetPos(105, 0);
	m_pxVPosition->SetSize(CSize(70, 20));
	m_pxVPosition->SetDecimals(0);
	m_pxVPosition->SetLimits(0, m_pxVSlider->GetSliderRange(), 1);
	m_pxVPosition->SetValue(m_pxVSlider->GetSliderPos());
	pxGroupV->AddChild(m_pxVPosition->GetWHDL());
	m_pxVPosition->SetOnChangeCallback(CreateFunctionPointer1(this, CSliderTestWindow::OnVPositionChange));


	pxRange = CSpinControlNumber::Create();
	pxRange->SetPos(105, 20);
	pxRange->SetSize(CSize(70, 20));
	pxRange->SetDecimals(0);
	pxRange->SetLimits(0, 1000, 1);
	pxRange->SetValue(m_pxVSlider->GetSliderRange());
	pxRange->SetStyle(CSpinControlNumber::SC_UpDown);
	pxGroupV->AddChild(pxRange->GetWHDL());
	pxRange->SetOnChangeCallback(CreateFunctionPointer1(this, CSliderTestWindow::OnVRangeChange));

	// Checkbox Background

	CCheckBox* pxCheckBox = CCheckBox::Create();
	pxCheckBox->SetSize(200, 20);
	pxCheckBox->SetPos(10, 320);
	pxCheckBox->SetText("Background");
	pxCheckBox->SetChecked(m_pxHSlider->GetBackground());
	pxCheckBox->SetOnStateChangeCallback(CreateFunctionPointer1(this, CSliderTestWindow::OnToggleBackground));
	AddChild(pxCheckBox->GetWHDL());
}
//---------------------------------------------------------------------------------------------------------------------
CSliderTestWindow ::~CSliderTestWindow ()
{
}
//---------------------------------------------------------------------------------------------------------------------
void	
CSliderTestWindow::DeleteNow()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CSliderTestWindow::OnClose()
{
	Destroy();
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
void
CSliderTestWindow::OnSliderChange(CSlider* pxSlider)
{
	m_pxHPosition->SetValue(m_pxHSlider->GetSliderPos());
	m_pxVPosition->SetValue(m_pxVSlider->GetSliderPos());
}
//---------------------------------------------------------------------------------------------------------------------
void
CSliderTestWindow::OnToggleBackground(CCheckBox* pxCheckBox)
{
	m_pxHSlider->SetBackground(pxCheckBox->GetCheckMark());
	m_pxVSlider->SetBackground(pxCheckBox->GetCheckMark());
}
//---------------------------------------------------------------------------------------------------------------------
void
CSliderTestWindow::OnHPositionChange(CBasicSpinControl* pxSpinner)
{
	m_pxHSlider->SetSliderPos(((CSpinControlNumber*) pxSpinner)->GetValueInt());
}
//---------------------------------------------------------------------------------------------------------------------
void	
CSliderTestWindow::OnVPositionChange(CBasicSpinControl* pxSpinner)
{
	m_pxVSlider->SetSliderPos(((CSpinControlNumber*) pxSpinner)->GetValueInt());
}
//---------------------------------------------------------------------------------------------------------------------
void
CSliderTestWindow::OnHRangeChange(CBasicSpinControl* pxSpinner)
{
	m_pxHSlider->SetSliderRange(((CSpinControlNumber*) pxSpinner)->GetValueInt());
	m_pxHPosition->SetLimits(0, m_pxHSlider->GetSliderRange(), 1);
}
//---------------------------------------------------------------------------------------------------------------------
void	
CSliderTestWindow::OnVRangeChange(CBasicSpinControl* pxSpinner)
{
	m_pxVSlider->SetSliderRange(((CSpinControlNumber*) pxSpinner)->GetValueInt());
	m_pxVPosition->SetLimits(0, m_pxVSlider->GetSliderRange(), 1);
}
//---------------------------------------------------------------------------------------------------------------------

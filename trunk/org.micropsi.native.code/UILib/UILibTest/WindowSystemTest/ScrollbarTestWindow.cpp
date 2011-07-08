#include "stdafx.h"

#include "uilib/controls/panel.h"
#include "uilib/controls/label.h"
#include "uilib/controls/scrollbar.h"
#include "uilib/controls/groupbox.h"
#include "uilib/controls/checkbox.h"
#include "uilib/controls/spincontrolnumber.h"

#include "ScrollBarTestWindow.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CScrollBarTestWindow * 
CScrollBarTestWindow ::Create()
{
	return new CScrollBarTestWindow ;
}
//---------------------------------------------------------------------------------------------------------------------
CScrollBarTestWindow ::CScrollBarTestWindow ()
{
	SetCaption("UILib CScrollBar Test");
	SetSize(424, 490);
	SetHasCloseButton(true);

	// Hintergrund und Label

	CPanel* pxBackGround = CPanel::Create();
	pxBackGround->SetSize(400, 200);
	pxBackGround->SetPos(10, 10);
	pxBackGround->SetColor(CColor(210, 210, 255));
	AddChild(pxBackGround->GetWHDL());

	// ScrollBar

	m_pxHScrollBar = CScrollBar::Create();
	m_pxHScrollBar->SetPos(10, 100);
	m_pxHScrollBar->SetStyle(CScrollBar::SB_Horizontal);
	m_pxHScrollBar->SetSize(CSize(180, 20));
	m_pxHScrollBar->SetScrollRange(20);
	pxBackGround->AddChild(m_pxHScrollBar->GetWHDL());

	m_pxVScrollBar = CScrollBar::Create();
	m_pxVScrollBar->SetPos(290, 10);
	m_pxVScrollBar->SetStyle(CScrollBar::SB_Vertical);
	m_pxVScrollBar->SetSize(CSize(20, 180));
	m_pxVScrollBar->SetScrollRange(20);
	pxBackGround->AddChild(m_pxVScrollBar->GetWHDL());

	m_pxHScrollBar->SetOnChangeCallback(CreateFunctionPointer1(this, CScrollBarTestWindow::OnScrollBarChange));
	m_pxVScrollBar->SetOnChangeCallback(CreateFunctionPointer1(this, CScrollBarTestWindow::OnScrollBarChange));

	// Spin Controls für Position, Range, Page Size

	CGroupBox* pxGroupH = CGroupBox::Create();
	pxGroupH->SetSize(190, 90);
	pxGroupH->SetPos(10, 220);
	pxGroupH->SetText("Horizontal ScrollBar");
	AddChild(pxGroupH->GetWHDL());
	
	CGroupBox* pxGroupV = CGroupBox::Create();
	pxGroupV->SetSize(190, 90);
	pxGroupV->SetPos(220, 220);
	pxGroupV->SetText("Vertical ScrollBar");
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
	pxLabel->SetText("Page Size:");
	pxLabel->SetTextAlign(CLabel::TA_Left);
	pxLabel->SetSize(100, 20);
	pxLabel->SetPos(5, 40);
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

	pxLabel = CLabel::Create();
	pxLabel->SetText("Page Size:");
	pxLabel->SetTextAlign(CLabel::TA_Left);
	pxLabel->SetSize(100, 20);
	pxLabel->SetPos(5, 40);
	pxGroupV->AddChild(pxLabel->GetWHDL());

	m_pxHPosition = CSpinControlNumber::Create();
	m_pxHPosition->SetPos(105, 0);
	m_pxHPosition->SetSize(CSize(70, 20));
	m_pxHPosition->SetDecimals(0);
	m_pxHPosition->SetLimits(0, m_pxHScrollBar->GetScrollLimit(), 1);
	m_pxHPosition->SetValue(m_pxHScrollBar->GetScrollPos());
	pxGroupH->AddChild(m_pxHPosition->GetWHDL());
	m_pxHPosition->SetOnChangeCallback(CreateFunctionPointer1(this, CScrollBarTestWindow::OnHPositionChange));

	CSpinControlNumber* pxRange = CSpinControlNumber::Create();
	pxRange->SetPos(105, 20);
	pxRange->SetSize(CSize(70, 20));
	pxRange->SetDecimals(0);
	pxRange->SetLimits(0, 1000, 1);
	pxRange->SetValue(m_pxHScrollBar->GetScrollRange());
	pxRange->SetStyle(CSpinControlNumber::SC_UpDown);
	pxGroupH->AddChild(pxRange->GetWHDL());
	pxRange->SetOnChangeCallback(CreateFunctionPointer1(this, CScrollBarTestWindow::OnHRangeChange));

	CSpinControlNumber* pxPageSize = CSpinControlNumber::Create();
	pxPageSize->SetPos(105, 40);
	pxPageSize->SetSize(CSize(70, 20));
	pxPageSize->SetDecimals(0);
	pxPageSize->SetLimits(0, 1000, 1);
	pxPageSize->SetValue(m_pxHScrollBar->GetPageSize());
	pxPageSize->SetStyle(CSpinControlNumber::SC_UpDown);
	pxGroupH->AddChild(pxPageSize->GetWHDL());
	pxPageSize->SetOnChangeCallback(CreateFunctionPointer1(this, CScrollBarTestWindow::OnHRangeChange));


	m_pxVPosition = CSpinControlNumber::Create();
	m_pxVPosition->SetPos(105, 0);
	m_pxVPosition->SetSize(CSize(70, 20));
	m_pxVPosition->SetDecimals(0);
	m_pxVPosition->SetLimits(0, m_pxVScrollBar->GetScrollLimit(), 1);
	m_pxVPosition->SetValue(m_pxVScrollBar->GetScrollPos());
	pxGroupV->AddChild(m_pxVPosition->GetWHDL());
	m_pxVPosition->SetOnChangeCallback(CreateFunctionPointer1(this, CScrollBarTestWindow::OnVPositionChange));


	pxRange = CSpinControlNumber::Create();
	pxRange->SetPos(105, 20);
	pxRange->SetSize(CSize(70, 20));
	pxRange->SetDecimals(0);
	pxRange->SetLimits(0, 1000, 1);
	pxRange->SetValue(m_pxVScrollBar->GetScrollRange());
	pxRange->SetStyle(CSpinControlNumber::SC_UpDown);
	pxGroupV->AddChild(pxRange->GetWHDL());
	pxRange->SetOnChangeCallback(CreateFunctionPointer1(this, CScrollBarTestWindow::OnVRangeChange));

	pxPageSize = CSpinControlNumber::Create();
	pxPageSize->SetPos(105, 40);
	pxPageSize->SetSize(CSize(70, 20));
	pxPageSize->SetDecimals(0);
	pxPageSize->SetLimits(0, 1000, 1);
	pxPageSize->SetValue(m_pxVScrollBar->GetPageSize());
	pxPageSize->SetStyle(CSpinControlNumber::SC_UpDown);
	pxGroupV->AddChild(pxPageSize->GetWHDL());
	pxPageSize->SetOnChangeCallback(CreateFunctionPointer1(this, CScrollBarTestWindow::OnVRangeChange));


	// Checkbox Buttons

	CCheckBox* pxCheckBox = CCheckBox::Create();
	pxCheckBox->SetSize(200, 20);
	pxCheckBox->SetPos(10, 320);
	pxCheckBox->SetText("Buttons");
	pxCheckBox->SetChecked(m_pxHScrollBar->GetHasButtons());
	pxCheckBox->SetOnStateChangeCallback(CreateFunctionPointer1(this, CScrollBarTestWindow::OnToggleButtons));
	AddChild(pxCheckBox->GetWHDL());
}
//---------------------------------------------------------------------------------------------------------------------
CScrollBarTestWindow ::~CScrollBarTestWindow ()
{
}
//---------------------------------------------------------------------------------------------------------------------
void	
CScrollBarTestWindow::DeleteNow()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CScrollBarTestWindow::OnClose()
{
	Destroy();
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
void
CScrollBarTestWindow::OnScrollBarChange(CScrollBar* pxScrollBar)
{
	m_pxHPosition->SetValue(m_pxHScrollBar->GetScrollPos());
	m_pxVPosition->SetValue(m_pxVScrollBar->GetScrollPos());
}
//---------------------------------------------------------------------------------------------------------------------
void
CScrollBarTestWindow::OnToggleButtons(CCheckBox* pxCheckBox)
{
	m_pxHScrollBar->SetButtons(pxCheckBox->GetCheckMark());
	m_pxVScrollBar->SetButtons(pxCheckBox->GetCheckMark());
}
//---------------------------------------------------------------------------------------------------------------------
void
CScrollBarTestWindow::OnHPositionChange(CBasicSpinControl* pxSpinner)
{
	m_pxHScrollBar->SetScrollPos(((CSpinControlNumber*) pxSpinner)->GetValueInt());
}
//---------------------------------------------------------------------------------------------------------------------
void	
CScrollBarTestWindow::OnVPositionChange(CBasicSpinControl* pxSpinner)
{
	m_pxVScrollBar->SetScrollPos(((CSpinControlNumber*) pxSpinner)->GetValueInt());
}
//---------------------------------------------------------------------------------------------------------------------
void
CScrollBarTestWindow::OnHRangeChange(CBasicSpinControl* pxSpinner)
{
	m_pxHScrollBar->SetScrollRange(((CSpinControlNumber*) pxSpinner)->GetValueInt());
	m_pxHPosition->SetLimits(0, m_pxHScrollBar->GetScrollLimit(), 1);
}
//---------------------------------------------------------------------------------------------------------------------
void	
CScrollBarTestWindow::OnVRangeChange(CBasicSpinControl* pxSpinner)
{
	m_pxVScrollBar->SetScrollRange(((CSpinControlNumber*) pxSpinner)->GetValueInt());
	m_pxVPosition->SetLimits(0, m_pxVScrollBar->GetScrollLimit(), 1);
}
//---------------------------------------------------------------------------------------------------------------------
void
CScrollBarTestWindow::OnHPageSizeChange(UILib::CBasicSpinControl* pxSpinner)
{
	m_pxVScrollBar->SetPageSize(((CSpinControlNumber*) pxSpinner)->GetValueInt());
	m_pxVPosition->SetLimits(0, m_pxVScrollBar->GetScrollLimit(), 1);
}
//---------------------------------------------------------------------------------------------------------------------
void
CScrollBarTestWindow::OnVPageSizeChange(UILib::CBasicSpinControl* pxSpinner)
{
	m_pxHScrollBar->SetPageSize(((CSpinControlNumber*) pxSpinner)->GetValueInt());
	m_pxHPosition->SetLimits(0, m_pxHScrollBar->GetScrollLimit(), 1);
}
//---------------------------------------------------------------------------------------------------------------------

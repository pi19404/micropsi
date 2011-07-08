#include "stdafx.h"

#include "uilib/core/bitmapfont.h"
#include "uilib/core/alphabitmapfont.h"
#include "uilib/controls/panel.h"
#include "uilib/controls/label.h"
#include "uilib/controls/groupbox.h"
#include "uilib/controls/editcontrol.h"
#include "uilib/controls/checkbox.h"
#include "uilib/controls/radiobutton.h"

#include "LabelTestWindow.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CLabelTestWindow * 
CLabelTestWindow ::Create()
{
	return new CLabelTestWindow ;
}
//---------------------------------------------------------------------------------------------------------------------
CLabelTestWindow ::CLabelTestWindow ()
{
	SetCaption("UILib CLabel Test");
	SetSize(424, 490);
	SetHasCloseButton(true);

	// Fonts

	m_hBitmapfontFont = CBitmapFont::Create("bitmapfontgreen.png", 
		"ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜßabcdefghijklmnopqrstuvwxyzäöü+-=<>^§$&%{}[]().,;:?!_#*~\\/´'|\"@1234567890", 4, 17);
	m_hAlphaBitmapFont = CAlphaBitmapFont::Create(20, "Times New Roman", CFont::L_STANDARD, CFont::PITCH_VARIABLE, CFont::W_BOLD);


	// Hintergrund und Label

	CPanel* pxBackGround = CPanel::Create();
	pxBackGround->SetSize(400, 200);
	pxBackGround->SetPos(10, 10);
	pxBackGround->SetColor(CColor(255, 150, 150));
	AddChild(pxBackGround->GetWHDL());

	CStr sDefaultText = "This is the\ndefault text.";

	m_pxLabel1 = CLabel::Create();
	m_pxLabel1->SetText(sDefaultText);
	m_pxLabel1->SetSize(180, 85);
	m_pxLabel1->SetPos(10, 10);
	pxBackGround->AddChild(m_pxLabel1);

	m_pxLabel2 = CLabel::Create();
	m_pxLabel2->SetText(sDefaultText);
	m_pxLabel2->SetSize(180, 85);
	m_pxLabel2->SetPos(200, 10);
	m_pxLabel2->SetFont(m_hAlphaBitmapFont);
	pxBackGround->AddChild(m_pxLabel2);

	m_pxLabel3 = CLabel::Create();
	m_pxLabel3->SetText(sDefaultText);
	m_pxLabel3->SetSize(180, 85);
	m_pxLabel3->SetPos(10, 105);
	m_pxLabel3->SetFont(m_hBitmapfontFont);
	pxBackGround->AddChild(m_pxLabel3);


	// Radio Buttons für Alignment

	CGroupBox* pxGroupH = CGroupBox::Create();
	pxGroupH->SetSize(190, 90);
	pxGroupH->SetPos(10, 220);
	pxGroupH->SetText("Horizontal Alignment");
	AddChild(pxGroupH->GetWHDL());
	
	m_pxHCenter = CRadioButton::Create();
	m_pxHCenter->SetText("Center");
	m_pxHCenter->SetPos(5,0);
	m_pxHCenter->SetSize(200, 20);
	m_pxHCenter->SetOnStateChangeCallback(CreateFunctionPointer1(this, CLabelTestWindow::OnAlignmentChange));
	pxGroupH->AddChild(m_pxHCenter->GetWHDL());

	m_pxHLeft = CRadioButton::Create();
	m_pxHLeft->SetText("Left");
	m_pxHLeft->SetPos(5,20);
	m_pxHLeft->SetSize(200, 20);
	m_pxHLeft->SetOnStateChangeCallback(CreateFunctionPointer1(this, CLabelTestWindow::OnAlignmentChange));
	pxGroupH->AddChild(m_pxHLeft->GetWHDL());

	m_pxHRight = CRadioButton::Create();
	m_pxHRight->SetText("Right");
	m_pxHRight->SetPos(5,40);
	m_pxHRight->SetSize(200, 20);
	m_pxHRight->SetOnStateChangeCallback(CreateFunctionPointer1(this, CLabelTestWindow::OnAlignmentChange));
	pxGroupH->AddChild(m_pxHRight->GetWHDL());


	CGroupBox* pxGroupV = CGroupBox::Create();
	pxGroupV->SetSize(190, 90);
	pxGroupV->SetPos(220, 220);
	pxGroupV->SetText("Vertical Alignment");
	AddChild(pxGroupV->GetWHDL());

	m_pxVCenter = CRadioButton::Create();
	m_pxVCenter->SetText("Center");
	m_pxVCenter->SetPos(5,0);
	m_pxVCenter->SetSize(200, 20);
	m_pxVCenter->SetOnStateChangeCallback(CreateFunctionPointer1(this, CLabelTestWindow::OnAlignmentChange));
	pxGroupV->AddChild(m_pxVCenter->GetWHDL());

	m_pxVTop = CRadioButton::Create();
	m_pxVTop->SetText("Top");
	m_pxVTop->SetPos(5,20);
	m_pxVTop->SetSize(200, 20);
	m_pxVTop->SetOnStateChangeCallback(CreateFunctionPointer1(this, CLabelTestWindow::OnAlignmentChange));
	pxGroupV->AddChild(m_pxVTop->GetWHDL());

	m_pxVBottom = CRadioButton::Create();
	m_pxVBottom->SetText("Bottom");
	m_pxVBottom->SetPos(5,40);
	m_pxVBottom->SetSize(200, 20);
	m_pxVBottom->SetOnStateChangeCallback(CreateFunctionPointer1(this, CLabelTestWindow::OnAlignmentChange));
	pxGroupV->AddChild(m_pxVBottom->GetWHDL());

	m_pxHCenter->SetSelected(true);
	m_pxVCenter->SetSelected(true);


	// Checkbox Background

	CCheckBox* pxCheckBox = CCheckBox::Create();
	pxCheckBox->SetSize(200, 20);
	pxCheckBox->SetPos(10, 320);
	pxCheckBox->SetText("Background");
	pxCheckBox->SetChecked(m_pxLabel1->GetBackground());
	pxCheckBox->SetOnStateChangeCallback(CreateFunctionPointer1(this, CLabelTestWindow::OnToggleBackground));
	AddChild(pxCheckBox->GetWHDL());

	// Checkbox Selected

	pxCheckBox = CCheckBox::Create();
	pxCheckBox->SetSize(200, 20);
	pxCheckBox->SetPos(10, 340);
	pxCheckBox->SetText("Selected");
	pxCheckBox->SetChecked(false);
	pxCheckBox->SetOnStateChangeCallback(CreateFunctionPointer1(this, CLabelTestWindow::OnToggleSelected));
	AddChild(pxCheckBox->GetWHDL());

	// Text Edit

	CLabel* pxLabel = CLabel::Create();
	pxLabel->SetSize(50, 20);
	pxLabel->SetPos(10, 370);
	pxLabel->SetText("Text:");
	pxLabel->SetTextAlign(CLabel::TA_Left);
	AddChild(pxLabel->GetWHDL());

	CEditControl* pxEdit = CEditControl::Create();
	pxEdit->SetSize(350, 50);
	pxEdit->SetPos(60, 370);
	pxEdit->SetMultiLine(true);
	pxEdit->SetText(sDefaultText);
	AddChild(pxEdit->GetWHDL());
	pxEdit->SetOnChangeCallback(CreateFunctionPointer1(this, CLabelTestWindow::OnTextChanged));

}
//---------------------------------------------------------------------------------------------------------------------
CLabelTestWindow ::~CLabelTestWindow ()
{
	CAlphaBitmapFont::Release(m_hAlphaBitmapFont);
	CBitmapFont::Release(m_hBitmapfontFont);
}
//---------------------------------------------------------------------------------------------------------------------
void	
CLabelTestWindow::DeleteNow()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CLabelTestWindow::OnClose()
{
	Destroy();
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
void
CLabelTestWindow::OnAlignmentChange(CRadioButton* pxRadioButton)
{
	CLabel::HorizontalTextAlignment eHAlign = CLabel::TA_HCenter;
	CLabel::VerticalTextAlignment eVAlign = CLabel::TA_VCenter;

	if(m_pxHLeft->GetSelected())		{ eHAlign = CLabel::TA_Left; }
	if(m_pxHRight->GetSelected())		{ eHAlign = CLabel::TA_Right; }

	if(m_pxVTop->GetSelected())			{ eVAlign = CLabel::TA_Top; }
	if(m_pxVBottom->GetSelected())		{ eVAlign = CLabel::TA_Bottom; }

	m_pxLabel1->SetTextAlign(eHAlign, eVAlign);
	m_pxLabel2->SetTextAlign(eHAlign, eVAlign);
	m_pxLabel3->SetTextAlign(eHAlign, eVAlign);
}
//---------------------------------------------------------------------------------------------------------------------
void
CLabelTestWindow::OnToggleBackground(UILib::CCheckBox* pxCheckBox)
{
	m_pxLabel1->SetBackground(pxCheckBox->GetCheckMark());
	m_pxLabel2->SetBackground(pxCheckBox->GetCheckMark());
	m_pxLabel3->SetBackground(pxCheckBox->GetCheckMark());
}
//---------------------------------------------------------------------------------------------------------------------
void
CLabelTestWindow::OnToggleSelected(UILib::CCheckBox* pxCheckBox)
{
	m_pxLabel1->SetSpecialProperty(pxCheckBox->GetCheckMark() ? CLabel::SP_Selected : CLabel::SP_Normal);
	m_pxLabel2->SetSpecialProperty(pxCheckBox->GetCheckMark() ? CLabel::SP_Selected : CLabel::SP_Normal);
	m_pxLabel3->SetSpecialProperty(pxCheckBox->GetCheckMark() ? CLabel::SP_Selected : CLabel::SP_Normal);
}
//---------------------------------------------------------------------------------------------------------------------
void
CLabelTestWindow::OnTextChanged(UILib::CEditControl* pxEdit)
{
	m_pxLabel1->SetText(pxEdit->GetText());
	m_pxLabel2->SetText(pxEdit->GetText());
	m_pxLabel3->SetText(pxEdit->GetText());
}
//---------------------------------------------------------------------------------------------------------------------

#include "stdafx.h"

#include "uilib/controls/label.h"
#include "uilib/controls/groupbox.h"
#include "uilib/controls/radiobutton.h"
#include "uilib/controls/editcontrol.h"
#include "uilib/controls/button.h"
#include "uilib/controls/messagebox.h"

#include "MessageBoxTestWindow.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CMessageBoxTestWindow * 
CMessageBoxTestWindow ::Create()
{
	return new CMessageBoxTestWindow ;
}
//---------------------------------------------------------------------------------------------------------------------
CMessageBoxTestWindow ::CMessageBoxTestWindow ()
{
	SetCaption("UILib CMessageBox Test");
	SetSize(424, 490);
	SetHasCloseButton(true);

	// Icons

	CGroupBox* pxGroupIcon = CGroupBox::Create();
	pxGroupIcon->SetSize(190, 150);
	pxGroupIcon->SetPos(10, 10);
	pxGroupIcon->SetText("Icon");
	AddChild(pxGroupIcon->GetWHDL());

	m_pxIconInformation = CRadioButton::Create();
	m_pxIconInformation->SetText("MBI_ICONINFO");
	m_pxIconInformation->SetPos(5,0);
	m_pxIconInformation->SetSize(200, 20);
	m_pxIconInformation->SetOnStateChangeCallback(CreateFunctionPointer1(this, CMessageBoxTestWindow::OnIconChange));
	pxGroupIcon->AddChild(m_pxIconInformation->GetWHDL());
	m_pxIconInformation->SetSelected(true);

	m_pxIconExclamation = CRadioButton::Create();
	m_pxIconExclamation->SetText("MBI_ICONEXCLAMATION");
	m_pxIconExclamation->SetPos(5,20);
	m_pxIconExclamation->SetSize(200, 20);
	m_pxIconExclamation->SetOnStateChangeCallback(CreateFunctionPointer1(this, CMessageBoxTestWindow::OnIconChange));
	pxGroupIcon->AddChild(m_pxIconExclamation->GetWHDL());

	m_pxIconWarning = CRadioButton::Create();
	m_pxIconWarning->SetText("MBI_ICONWARNING");
	m_pxIconWarning->SetPos(5,40);
	m_pxIconWarning->SetSize(200, 20);
	m_pxIconWarning->SetOnStateChangeCallback(CreateFunctionPointer1(this, CMessageBoxTestWindow::OnIconChange));
	pxGroupIcon->AddChild(m_pxIconWarning->GetWHDL());

	
	// Buttons

	CGroupBox* pxGroupButtons = CGroupBox::Create();
	pxGroupButtons->SetSize(190, 150);
	pxGroupButtons->SetPos(210, 10);
	pxGroupButtons->SetText("Buttons");
	AddChild(pxGroupButtons->GetWHDL());

	m_pxButtonOK = CRadioButton::Create();
	m_pxButtonOK->SetText("MBB_OK");
	m_pxButtonOK->SetPos(5,0);
	m_pxButtonOK->SetSize(200, 20);
	m_pxButtonOK->SetOnStateChangeCallback(CreateFunctionPointer1(this, CMessageBoxTestWindow::OnButtonsChange));
	pxGroupButtons->AddChild(m_pxButtonOK->GetWHDL());
	m_pxButtonOK->SetSelected(true);

	m_pxButtonOKCANCEL = CRadioButton::Create();
	m_pxButtonOKCANCEL->SetText("MBB_OKCANCEL");
	m_pxButtonOKCANCEL->SetPos(5,20);
	m_pxButtonOKCANCEL->SetSize(200, 20);
	m_pxButtonOKCANCEL->SetOnStateChangeCallback(CreateFunctionPointer1(this, CMessageBoxTestWindow::OnButtonsChange));
	pxGroupButtons->AddChild(m_pxButtonOKCANCEL->GetWHDL());

	m_pxButtonYESNO = CRadioButton::Create();
	m_pxButtonYESNO->SetText("MBB_YESNO");
	m_pxButtonYESNO->SetPos(5,40);
	m_pxButtonYESNO->SetSize(200, 20);
	m_pxButtonYESNO->SetOnStateChangeCallback(CreateFunctionPointer1(this, CMessageBoxTestWindow::OnButtonsChange));
	pxGroupButtons->AddChild(m_pxButtonYESNO->GetWHDL());

	m_pxButtonRETRYCANCEL = CRadioButton::Create();
	m_pxButtonRETRYCANCEL->SetText("MBB_RETRYCANCEL");
	m_pxButtonRETRYCANCEL->SetPos(5,60);
	m_pxButtonRETRYCANCEL->SetSize(200, 20);
	m_pxButtonRETRYCANCEL->SetOnStateChangeCallback(CreateFunctionPointer1(this, CMessageBoxTestWindow::OnButtonsChange));
	pxGroupButtons->AddChild(m_pxButtonRETRYCANCEL->GetWHDL());

	m_pxButtonRETRYIGNORECANCEL = CRadioButton::Create();
	m_pxButtonRETRYIGNORECANCEL->SetText("MBB_RETRYIGNORECANCEL");
	m_pxButtonRETRYIGNORECANCEL->SetPos(5,80);
	m_pxButtonRETRYIGNORECANCEL->SetSize(200, 20);
	m_pxButtonRETRYIGNORECANCEL->SetOnStateChangeCallback(CreateFunctionPointer1(this, CMessageBoxTestWindow::OnButtonsChange));
	pxGroupButtons->AddChild(m_pxButtonRETRYIGNORECANCEL->GetWHDL());

	// Text and Caption


	CLabel* pxLabel = CLabel::Create();
	pxLabel->SetSize(50, 20);
	pxLabel->SetPos(10, 200);
	pxLabel->SetText("Caption:");
	pxLabel->SetTextAlign(CLabel::TA_Left);
	AddChild(pxLabel->GetWHDL());

	m_pxCaptionEdit = CEditControl::Create();
	m_pxCaptionEdit->SetSize(350, 20);
	m_pxCaptionEdit->SetPos(60, 200);
	m_pxCaptionEdit->SetMultiLine(false);
	m_pxCaptionEdit->SetText("Default Caption.");
	AddChild(m_pxCaptionEdit->GetWHDL());

	pxLabel = CLabel::Create();
	pxLabel->SetSize(50, 20);
	pxLabel->SetPos(10, 230);
	pxLabel->SetText("Text:");
	pxLabel->SetTextAlign(CLabel::TA_Left);
	AddChild(pxLabel->GetWHDL());

	m_pxTextEdit = CEditControl::Create();
	m_pxTextEdit->SetSize(350, 50);
	m_pxTextEdit->SetPos(60, 230);
	m_pxTextEdit->SetMultiLine(true);
	m_pxTextEdit->SetText("This is just the default text.\nYou can change it if you want to!");
	AddChild(m_pxTextEdit->GetWHDL());


	// Go-Button

	CButton* pxButton = CButton::Create();
	pxButton->SetSize(100, 20);
	pxButton->SetPos(10, 330);
	pxButton->SetText("Go!");
	AddChild(pxButton->GetWHDL());
	pxButton->SetOnClickCallback(CreateFunctionPointer1(this, CMessageBoxTestWindow::OnGoButton));

	// Result-Label

	m_pxResultLabel = CLabel::Create();
	m_pxResultLabel->SetSize(400, 20);
	m_pxResultLabel->SetPos(10, 370);
	m_pxResultLabel->SetText("");
	AddChild(m_pxResultLabel->GetWHDL());
}
//---------------------------------------------------------------------------------------------------------------------
CMessageBoxTestWindow ::~CMessageBoxTestWindow ()
{
}
//---------------------------------------------------------------------------------------------------------------------
void	
CMessageBoxTestWindow::DeleteNow()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CMessageBoxTestWindow::OnClose()
{
	Destroy();
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
void
CMessageBoxTestWindow::OnIconChange(UILib::CRadioButton* pxRadioButton)
{
	if(pxRadioButton->GetWHDL() == m_pxIconInformation->GetWHDL())			m_iIcon = CMessageBox::MBI_ICONINFO;
	else if(pxRadioButton->GetWHDL() == m_pxIconExclamation->GetWHDL())		m_iIcon = CMessageBox::MBI_ICONEXCLAMATION;
	else if(pxRadioButton->GetWHDL() == m_pxIconWarning->GetWHDL())			m_iIcon = CMessageBox::MBI_ICONWARNING;
}
//---------------------------------------------------------------------------------------------------------------------
void
CMessageBoxTestWindow::OnButtonsChange(UILib::CRadioButton* pxRadioButton)
{
	if(pxRadioButton->GetWHDL() == m_pxButtonOK->GetWHDL())						m_iButtons = CMessageBox::MBB_OK;
	else if(pxRadioButton->GetWHDL() == m_pxButtonOKCANCEL->GetWHDL())			m_iButtons = CMessageBox::MBB_OKCANCEL;
	else if(pxRadioButton->GetWHDL() == m_pxButtonYESNO->GetWHDL())				m_iButtons = CMessageBox::MBB_YESNO;
	else if(pxRadioButton->GetWHDL() == m_pxButtonRETRYCANCEL->GetWHDL())		m_iButtons = CMessageBox::MBB_RETRYCANCEL;
	else if(pxRadioButton->GetWHDL() == m_pxButtonRETRYIGNORECANCEL->GetWHDL())	m_iButtons = CMessageBox::MBB_RETRYIGNORECANCEL;
}
//---------------------------------------------------------------------------------------------------------------------
void
CMessageBoxTestWindow::OnGoButton(UILib::CBasicButton* pxButton)
{
	CMessageBox::Create(m_pxCaptionEdit->GetText(), m_pxTextEdit->GetText(), m_iIcon | m_iButtons, 
						CreateFunctionPointer1(this, CMessageBoxTestWindow::OnDecision));
}
//---------------------------------------------------------------------------------------------------------------------
void	
CMessageBoxTestWindow::OnDecision(UILib::CMessageBox::MsgBoxResults eResult)
{
	CStr sText = "You clicked: ";
	switch(eResult)
	{
		case CMessageBox::ID_ERROR:	sText += "ID_ERROR";	break;
		case CMessageBox::ID_OK:		sText += "ID_OK";		break;
		case CMessageBox::ID_CANCEL: sText += "ID_CANCEL";	break;
		case CMessageBox::ID_YES:	sText += "ID_YES";		break;
		case CMessageBox::ID_NO:		sText += "ID_NO";		break;
		case CMessageBox::ID_RETRY:	sText += "ID_RETRY";	break;
		case CMessageBox::ID_IGNORE: sText += "ID_IGNORE";	break;
	}
	m_pxResultLabel->SetText(sText);
}
//---------------------------------------------------------------------------------------------------------------------

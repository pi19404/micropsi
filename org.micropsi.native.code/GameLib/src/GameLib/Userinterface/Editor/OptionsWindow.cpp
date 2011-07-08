#include "stdafx.h"
#include "GameLib/UserInterface/Editor/OptionsWindow.h"

#include "GameLib/World/GameObjectClassManager.h"
#include "GameLib/UserInterface/Editor/EditorScreen.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/label.h"

#include <string>

using namespace UILib;
using std::vector;
using std::string;

//----------------------------------------------------------------------------------------------------------------------
COptionsWindow::COptionsWindow(CEditorScreen* p_pxEditorScreen)
{
	m_pxEditorScreen = p_pxEditorScreen;
	
	SetCaption("Settings");
	SetSize(210, 300);
	SetPos(0, 0);

	CLabel* pxControlMethodLabel = CLabel::Create();
	AddChild(pxControlMethodLabel->GetWHDL());
	pxControlMethodLabel->SetPos(10, 10);
	pxControlMethodLabel->SetSize(80, 24);
	pxControlMethodLabel->SetText("Contol Method:");
	pxControlMethodLabel->SetTextAlign(CLabel::TA_Left);

	m_pxControlMethod = CComboBox::Create();
	AddChild(m_pxControlMethod->GetWHDL());
	m_pxControlMethod->SetPos(100, 10);
	m_pxControlMethod->SetSize(100, 24);
	m_pxControlMethod->AddItem("Maya Style");
	m_pxControlMethod->AddItem("3DS Max Style");

	CLabel* pxXZGridLabel = CLabel::Create();
	AddChild(pxXZGridLabel->GetWHDL());
	pxXZGridLabel->SetPos(10, 50);
	pxXZGridLabel->SetSize(80, 24);
	pxXZGridLabel->SetText("XZ-Grid:");
	pxXZGridLabel->SetTextAlign(CLabel::TA_Left);

	m_pxXZGrid = CComboBox::Create();
	AddChild(m_pxXZGrid->GetWHDL());
	m_pxXZGrid->SetPos(100, 50);
	m_pxXZGrid->SetSize(70, 24);
	m_pxXZGrid->AddItem("none");
	m_pxXZGrid->AddItem("0.5");
	m_pxXZGrid->AddItem("1");
	m_pxXZGrid->AddItem("2");

	CLabel* pxYGridLabel = CLabel::Create();
	AddChild(pxYGridLabel->GetWHDL());
	pxYGridLabel->SetPos(10, 80);
	pxYGridLabel->SetSize(80, 24);
	pxYGridLabel->SetText("Y-Grid:");
	pxYGridLabel->SetTextAlign(CLabel::TA_Left);

	m_pxYGrid = CComboBox::Create();
	AddChild(m_pxYGrid->GetWHDL());
	m_pxYGrid->SetPos(100, 80);
	m_pxYGrid->SetSize(70, 24);
	m_pxYGrid->AddItem("none");
	m_pxYGrid->AddItem("0.5");
	m_pxYGrid->AddItem("1");
	m_pxYGrid->AddItem("2");

	m_pxShowAllRanges = CCheckBox::Create();
	AddChild(m_pxShowAllRanges->GetWHDL());
	m_pxShowAllRanges->SetPos(10, 120);
	m_pxShowAllRanges->SetSize(180, 24);
	m_pxShowAllRanges->SetText("Show all Ranges");


	m_pxCloseButton = CButton::Create();
	AddChild(m_pxCloseButton->GetWHDL());
	m_pxCloseButton->SetText("Close");
	m_pxCloseButton->SetSize(80, 24);
	m_pxCloseButton->SetPos(116, 250);
}


//----------------------------------------------------------------------------------------------------------------------
COptionsWindow::~COptionsWindow()
{
}


//----------------------------------------------------------------------------------------------------------------------
COptionsWindow*	
COptionsWindow::Create(CEditorScreen* p_pxEditorScreen)
{
	return new COptionsWindow(p_pxEditorScreen);
}


//----------------------------------------------------------------------------------------------------------------------
void 
COptionsWindow::DeleteNow()
{
	delete this;
}

//----------------------------------------------------------------------------------------------------------------------

bool 
COptionsWindow::HandleMsg(const CMessage& p_krxMessage)
{
	if(p_krxMessage == msgButtonClicked)
	{
		if(p_krxMessage.GetWindow() == m_pxCloseButton->GetWHDL())
		{
			SetVisible(false);
			return true;
		}
	}

	return __super::HandleMsg(p_krxMessage);
}

//----------------------------------------------------------------------------------------------------------------------
void
COptionsWindow::Tick()
{
}

//----------------------------------------------------------------------------------------------------------------------

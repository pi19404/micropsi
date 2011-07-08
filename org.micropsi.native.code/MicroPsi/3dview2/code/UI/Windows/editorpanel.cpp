#include "Application/stdinc.h"
#include "UI/Windows/editorpanel.h"

#include "baselib/filelocator.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/messagebox.h"

#include "World/world.h"
#include "World/visualization.h"
#include "World/leveleditor.h"
#include "World/objectmanager.h"

#include "UI/windows/saveasdialog.h"

#include "Application/3dview2.h"

using namespace UILib;
using std::vector;
using std::string;

//------------------------------------------------------------------------------------------------------------------------------------------
CEditorPanel::CEditorPanel()
{
	m_pxLevelEditor = 0;

	SetColor(CColor(200, 200, 200, 150));
	SetSize(CWindowMgr::Get().GetDesktop()->GetSize().cx, 150);
	SetPos(0, CWindowMgr::Get().GetDesktop()->GetSize().cy - GetSize().cy);

	m_pxSelectButton	= CToggleButton::Create();
	m_pxObjectButton	= CToggleButton::Create();

	m_pxList			= CListBox::Create();

	m_pxGotoButton		= CButton::Create();
	m_pxDeleteButton	= CButton::Create();
	m_pxClearButton		= CButton::Create();
	m_pxSaveButton		= CButton::Create();
	m_pxSaveAsButton	= CButton::Create();

	m_pxObjectType		= CComboBox::Create();
	m_pxObjectVariation = CComboBox::Create();
	
	m_pxRandomRotation	= CCheckBox::Create();


	AddChild(m_pxSelectButton);
	m_pxSelectButton->SetText("Select\nObject");
	m_pxSelectButton->SetSize(85, 35);
	m_pxSelectButton->SetPos(10, 10);
	m_pxSelectButton->SetGrouped(true);
	m_pxSelectButton->SetAllowUntoggle(false);

	AddChild(m_pxObjectButton);
	m_pxObjectButton->SetText("Create New\nObject");
	m_pxObjectButton->SetSize(100, 35);
	m_pxObjectButton->SetPos(110, 10);
	m_pxObjectButton->SetGrouped(true);
	m_pxObjectButton->SetAllowUntoggle(false);

	AddChild(m_pxObjectType);
	m_pxObjectType->SetSize(CSize(150, 20));
	m_pxObjectType->SetPos(110, 55);
	m_pxObjectType->SetMaxPopUpListHeight(250);

	AddChild(m_pxObjectVariation);
	m_pxObjectVariation->SetSize(CSize(150, 20));
	m_pxObjectVariation->SetPos(110, 85);
	m_pxObjectVariation->SetMaxPopUpListHeight(100);
	m_pxObjectVariation->AddItem("Random Variation");

	AddChild(m_pxRandomRotation);
	m_pxRandomRotation->SetSize(CSize(150, 20));
	m_pxRandomRotation->SetPos(110, 115);
	m_pxRandomRotation->SetText("Random Rotation");
	m_pxRandomRotation->SetChecked();
	m_pxRandomRotation->SetBackground(false);

	m_pxSelectButton->SetToggleButtonState(true);

	m_pxList->SetSize(190, 130);
	m_pxList->SetPos(300, 10);
	AddChild(m_pxList);

	AddChild(m_pxGotoButton);
	m_pxGotoButton->SetText("Goto");
	m_pxGotoButton->SetSize(85, 20);
	m_pxGotoButton->SetPos(500, 10);
	m_pxGotoButton->SetDisabled(true);

	AddChild(m_pxDeleteButton);
	m_pxDeleteButton->SetText("Delete");
	m_pxDeleteButton->SetSize(85, 20);
	m_pxDeleteButton->SetPos(500, 50);

	AddChild(m_pxClearButton);
	m_pxClearButton->SetText("Clear");
	m_pxClearButton->SetSize(85, 20);
	m_pxClearButton->SetPos(500, 90);

	AddChild(m_pxSaveButton);
	m_pxSaveButton->SetText("Save");
	m_pxSaveButton->SetSize(85, 20);
	m_pxSaveButton->SetPos(600, 10);

	AddChild(m_pxSaveAsButton);
	m_pxSaveAsButton->SetText("Save As...");
	m_pxSaveAsButton->SetSize(85, 20);
	m_pxSaveAsButton->SetPos(600, 50);
}


//------------------------------------------------------------------------------------------------------------------------------------------
CEditorPanel::~CEditorPanel()
{
}


//------------------------------------------------------------------------------------------------------------------------------------------
CEditorPanel*	
CEditorPanel::Create()
{
	return new CEditorPanel();
}


//------------------------------------------------------------------------------------------------------------------------------------------
void 
CEditorPanel::DeleteNow()
{
	delete this;
}

//------------------------------------------------------------------------------------------------------------------------------------------

bool 
CEditorPanel::HandleMsg(const CMessage& p_krxMessage)
{
	if(m_pxLevelEditor)
	{
		if(p_krxMessage == msgButtonClicked)
		{
			if(p_krxMessage.GetWindow() == m_pxObjectButton->GetWHDL())
			{
				// select mode

				m_pxLevelEditor->SetClickMode(CLevelEditor::ClickMode::CM_CreateObject);
				return true;
			}
			else if(p_krxMessage.GetWindow() == m_pxSelectButton->GetWHDL())
			{
				// place objects mode

				m_pxLevelEditor->SetClickMode(CLevelEditor::ClickMode::CM_Select);
				return true;
			}
			else if(p_krxMessage.GetWindow() == m_pxDeleteButton->GetWHDL())
			{
				// delete selected object

				m_pxLevelEditor->DeleteSelectedObjects();
				return true;
			}
			else if(p_krxMessage.GetWindow() == m_pxClearButton->GetWHDL())
			{
				CMessageBox::Create("Confirm Clear", "Are you sure you want to delete all object?", 
					CMessageBox::MBI_ICONEXCLAMATION | CMessageBox::MBB_YESNO, GetWHDL());
				m_sMessageBoxAction = "clear";
			}
			else if(p_krxMessage.GetWindow() == m_pxGotoButton->GetWHDL())
			{
				// goto selected object

				return true;
			}
			else if(p_krxMessage.GetWindow() == m_pxSaveButton->GetWHDL())
			{
				// save world
	
				if(m_pxLevelEditor->GetCurrentWorldFileName().size() > 0)
				{
					bool bSuccess = m_pxLevelEditor->SaveWorld();
					if(bSuccess)
					{
						CMessageBox::Create("Success", "File saved.");
					}
					else
					{
						CMessageBox::Create("Error", CStr::Create("Could not save file\n'%s'", m_pxLevelEditor->GetCurrentWorldFileName().c_str()), 
							CMessageBox::MBI_ICONWARNING | CMessageBox::MBB_OK);
					}
				}
				return true;
			}
			else if(p_krxMessage.GetWindow() == m_pxSaveAsButton->GetWHDL())
			{
				CSaveAsDialog* pxDialog = CSaveAsDialog::Create(this);
				CWindowMgr::Get().AddTopLevelWindow(pxDialog->GetWHDL());
				pxDialog->CenterOnParentWindow();
				return true;
			}
		}
		else if(p_krxMessage == msgListBoxChanged)
		{
			int iIdx = m_pxList->GetSelectedItem();
			if(iIdx >= 0)
			{
			}
			return true;
		}
		else if(p_krxMessage == msgComboBoxChanged)
		{
			if(p_krxMessage.GetWindow() == m_pxObjectType->GetWHDL())
			{
				m_pxLevelEditor->SetObjectTypeToCreate(m_pxObjectType->GetText().c_str());
				UpdateVariationList();
				return true;
			}
			else if(p_krxMessage.GetWindow() == m_pxObjectVariation->GetWHDL())
			{
				m_pxLevelEditor->SetVariationToCreate(m_pxObjectVariation->GetSelectedItem() - 1);
				return true;
			}
		}
		else if(p_krxMessage == msgCheckBoxChanged)
		{
			if(p_krxMessage.GetWindow() == m_pxRandomRotation->GetWHDL())
			{
				m_pxLevelEditor->SetRandomRotationForNewObjects(m_pxRandomRotation->GetCheckMark());
			}
		}
		else if(p_krxMessage == msgMessageBoxDecision)
		{
			if(m_sMessageBoxAction == "clear")
			{
				if(p_krxMessage.GetIntParameter() == CMessageBox::ID_YES)
				{
					m_pxLevelEditor->Unselect();
					m_pxLevelEditor->ClearAllObjects();
				}
			}
			m_sMessageBoxAction = "";
		}
	}

	return __super::HandleMsg(p_krxMessage);
}

//------------------------------------------------------------------------------------------------------------------------------------------
void
CEditorPanel::Tick()
{
	// Buttons disablen/enablen
	m_pxDeleteButton->SetDisabled(m_pxLevelEditor->NumSelectedObjects() == 0);

	// Click-Einstellung
	switch(m_pxLevelEditor->GetClickMode())
	{
	case CLevelEditor::CM_Select:
		m_pxSelectButton->SetToggleButtonState(true);
		break;
	case CLevelEditor::CM_CreateObject:
		m_pxObjectButton->SetToggleButtonState(true);
		break;
	}
}


//------------------------------------------------------------------------------------------------------------------------------------------
void
CEditorPanel::Update()
{
	// update leveleditor

	m_pxLevelEditor = C3DView2::Get()->GetWorld()->GetEditor();


	// add object types to list

	const ::CVisualization& rxVisualization = C3DView2::Get()->GetWorld()->GetVisualization();
	m_pxObjectType->Clear();	
	for(int i=0; i<rxVisualization.GetNumberOfObjectTypes(); ++i)
	{
		m_pxObjectType->AddItem(rxVisualization.GetObjectTypeName(i).c_str());
	}
	m_pxLevelEditor->SetObjectTypeToCreate(m_pxObjectType->GetText().c_str());
	UpdateVariationList();
}

//------------------------------------------------------------------------------------------------------------------------------------------
void
CEditorPanel::UpdateVariationList()
{
	const ::CVisualization& rxVisualization = C3DView2::Get()->GetWorld()->GetVisualization();

	m_pxObjectVariation->Clear();
	m_pxObjectVariation->AddItem("Random Variation");

	if(m_pxObjectType->GetSelectedItem() < 0)
	{
		m_pxObjectVariation->SetDisabled(true);
		return;
	}
	m_pxObjectVariation->SetDisabled(false);

	string sType = m_pxObjectType->GetSelectedItemAsString().c_str();
	int iVariations = rxVisualization.GetNumObjectVariations(sType.c_str());
	for(int i=0; i<iVariations; ++i)
	{
		m_pxObjectVariation->AddItem(CStr::Create("Variation %d", i));
	}
}

//------------------------------------------------------------------------------------------------------------------------------------------
void
CEditorPanel::SaveAs(const char* p_pcFile)
{
	std::string sFile = p_pcFile;
	sFile += ".xml";

	bool bSuccess = m_pxLevelEditor->SaveWorldAs(sFile.c_str());
	if(bSuccess)
	{
		CMessageBox::Create("Success", "File saved.");
	}
	else
	{
		CMessageBox::Create("Error", CStr::Create("Could not save file\n'%s'", sFile.c_str()), 
			CMessageBox::MBI_ICONWARNING | CMessageBox::MBB_OK);
	}
}
//------------------------------------------------------------------------------------------------------------------------------------------

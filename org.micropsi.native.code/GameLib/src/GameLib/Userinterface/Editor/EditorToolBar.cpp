#include "stdafx.h"
#include "GameLib/UserInterface/Editor/EditorToolBar.h"

#include "uilib/core/windowmanager.h"
#include "GameLib/UserInterface/Editor/EditorScreen.h"
#include "GameLib/World/GameObjectClassManager.h"
#include "GameLib/UserInterface/Editor/ObjectListWindow.h"
#include "GameLib/UserInterface/Editor/OptionsWindow.h"
#include "GameLib/UserInterface/Editor/ObjectPropertiesWindow.h"

//#include "LevelEditor/LevelEditor.h"
//#include "World/World.h"
//#include "World/Level.h"
//#include "World/GameObjectMarker.h"
//#include "Sound/SoundController.h"

#include <string>

using namespace UILib;
using std::vector;
using std::string;

//----------------------------------------------------------------------------------------------------------------------
CEditorToolBar::CEditorToolBar(CEditorScreen* p_pxEditorScreen)
{
	m_pxEditorScreen = p_pxEditorScreen;

	SetColor(CColor(200, 200, 200, 150));

	if(CWindowMgr::Get().GetDesktop())
	{
		SetSize(CWindowMgr::Get().GetDesktop()->GetSize().cx, 30);
	}
	else
	{
		SetSize(800, 30);
	}
	SetPos(0, 0);

	m_pxSaveButton = CButton::Create();
	AddChild(m_pxSaveButton);
	m_pxSaveButton->SetBitmap("symbol_save.png");
	m_pxSaveButton->SetToolTipText("Save Level");
	m_pxSaveButton->SetSize(24, 24);
	m_pxSaveButton->SetPos(3, 3);

	m_pxSelectButton = CToggleButton::Create();
	AddChild(m_pxSelectButton);
	m_pxSelectButton->SetBitmap("symbol_select.png");
	m_pxSelectButton->SetToolTipText("Select Object Mode");
	m_pxSelectButton->SetSize(24, 24);
	m_pxSelectButton->SetPos(43, 3);
	m_pxSelectButton->SetGrouped(true);
	m_pxSelectButton->SetAllowUntoggle(false);

	m_pxMoveButton = CToggleButton::Create();
	AddChild(m_pxMoveButton);
	m_pxMoveButton->SetBitmap("symbol_move.png");
	m_pxMoveButton->SetToolTipText("Move Object Mode");
	m_pxMoveButton->SetSize(24, 24);
	m_pxMoveButton->SetPos(73, 3);
	m_pxMoveButton->SetGrouped(true);
	m_pxMoveButton->SetAllowUntoggle(false);

	m_pxRotateButton = CToggleButton::Create();
	AddChild(m_pxRotateButton);
	m_pxRotateButton->SetBitmap("symbol_rotate.png");
	m_pxRotateButton->SetToolTipText("Rotate Object Mode");
	m_pxRotateButton->SetSize(24, 24);
	m_pxRotateButton->SetPos(103, 3);
	m_pxRotateButton->SetGrouped(true);
	m_pxRotateButton->SetAllowUntoggle(false);

	m_pxCreateObjectButton = CToggleButton::Create();
	AddChild(m_pxCreateObjectButton);
	m_pxCreateObjectButton->SetBitmap("symbol_create.png");
	m_pxCreateObjectButton->SetToolTipText("Create New Objects Mode");
	m_pxCreateObjectButton->SetSize(24, 24);
	m_pxCreateObjectButton->SetPos(143, 3);
	m_pxCreateObjectButton->SetGrouped(true);
	m_pxCreateObjectButton->SetAllowUntoggle(false);	

	m_pxObjectTypeComboBox = CComboBox::Create();
	AddChild(m_pxObjectTypeComboBox);
	m_pxObjectTypeComboBox ->SetToolTipText("Select Object Type to create");
	m_pxObjectTypeComboBox->SetPos(173, 3);
	m_pxObjectTypeComboBox->SetSize(174, 24);
	m_pxObjectTypeComboBox->SetMaxPopUpListHeight(500);
	FillObjectTypeList();

	m_pxDeleteObjectButton = CButton::Create();
	AddChild(m_pxDeleteObjectButton);
	m_pxDeleteObjectButton->SetBitmap("symbol_delete.png");
	m_pxDeleteObjectButton->SetToolTipText("Delete selected Object");
	m_pxDeleteObjectButton->SetSize(24, 24);
	m_pxDeleteObjectButton->SetPos(353, 3);

	m_pxResetButton = CButton::Create();
	AddChild(m_pxResetButton);
	m_pxResetButton->SetBitmap("symbol_reset.png");
	m_pxResetButton->SetToolTipText("Reset All Objects");
	m_pxResetButton->SetSize(24, 24);
	m_pxResetButton->SetPos(383, 3);

	m_pxCreateMusicButton = CToggleButton::Create();
	AddChild(m_pxCreateMusicButton);
	m_pxCreateMusicButton->SetBitmap("symbol_music.png");
	m_pxCreateMusicButton->SetToolTipText("Create Music Marker Object");
	m_pxCreateMusicButton->SetSize(24, 24);
	m_pxCreateMusicButton->SetPos(423, 3);
	m_pxCreateMusicButton->SetGrouped(true);
	m_pxCreateMusicButton->SetAllowUntoggle(false);	

	m_pxCreateSoundButton = CToggleButton::Create();
	AddChild(m_pxCreateSoundButton);
	m_pxCreateSoundButton->SetBitmap("symbol_sound.png");
	m_pxCreateSoundButton->SetToolTipText("Create Sound Marker Object");
	m_pxCreateSoundButton->SetSize(24, 24);
	m_pxCreateSoundButton->SetPos(453, 3);
	m_pxCreateSoundButton->SetGrouped(true);
	m_pxCreateSoundButton->SetAllowUntoggle(false);	

	m_pxCreateSoundEnvironmentButton = CToggleButton::Create();
	AddChild(m_pxCreateSoundEnvironmentButton);
	m_pxCreateSoundEnvironmentButton->SetBitmap("symbol_soundenvironment.png");
	m_pxCreateSoundEnvironmentButton->SetToolTipText("Create Sound Environment Marker Object");
	m_pxCreateSoundEnvironmentButton->SetSize(24, 24);
	m_pxCreateSoundEnvironmentButton->SetPos(483, 3);
	m_pxCreateSoundEnvironmentButton->SetGrouped(true);
	m_pxCreateSoundEnvironmentButton->SetAllowUntoggle(false);	

	m_pxObjectPropertiesButton = CToggleButton::Create();
	AddChild(m_pxObjectPropertiesButton);
	m_pxObjectPropertiesButton->SetBitmap("symbol_properties.png");
	m_pxObjectPropertiesButton->SetToolTipText("Object Properties Window");
	m_pxObjectPropertiesButton->SetSize(24, 24);
	m_pxObjectPropertiesButton->SetPos(523, 3);
	m_pxObjectPropertiesButton->SetGrouped(false);
	m_pxObjectPropertiesButton->SetAllowUntoggle(true);	

	m_pxObjectListButton = CToggleButton::Create();
	AddChild(m_pxObjectListButton);
	m_pxObjectListButton->SetBitmap("symbol_list.png");
	m_pxObjectListButton->SetToolTipText("Object List Window");
	m_pxObjectListButton->SetSize(24, 24);
	m_pxObjectListButton->SetPos(553, 3);
	m_pxObjectListButton->SetGrouped(false);
	m_pxObjectListButton->SetAllowUntoggle(true);	

	m_pxSettingsButton = CToggleButton::Create();
	AddChild(m_pxSettingsButton);
	m_pxSettingsButton->SetBitmap("symbol_options.png");
	m_pxSettingsButton->SetToolTipText("Settings Window");
	m_pxSettingsButton->SetSize(24, 24);
	m_pxSettingsButton->SetPos(583, 3);
	m_pxSettingsButton->SetGrouped(false);
	m_pxSettingsButton->SetAllowUntoggle(true);	
}


//----------------------------------------------------------------------------------------------------------------------
CEditorToolBar::~CEditorToolBar()
{
}


//----------------------------------------------------------------------------------------------------------------------
CEditorToolBar*	
CEditorToolBar::Create(CEditorScreen* p_pxEditorScreen)
{
	return new CEditorToolBar(p_pxEditorScreen);
}


//----------------------------------------------------------------------------------------------------------------------
void 
CEditorToolBar::DeleteNow()
{
	delete this;
}

//----------------------------------------------------------------------------------------------------------------------

bool 
CEditorToolBar::HandleMsg(const CMessage& p_krxMessage)
{
	if(p_krxMessage == msgButtonClicked)
	{
	}
	else if(p_krxMessage == msgToggleButtonChanged)
	{
		if(p_krxMessage.GetWindow() == m_pxObjectPropertiesButton->GetWHDL())
		{
			m_pxEditorScreen->GetObjectPropertiesWindow()->SetVisible(m_pxObjectPropertiesButton->GetToggleButtonState());
			return true;
		}
		else if(p_krxMessage.GetWindow() == m_pxObjectListButton->GetWHDL())
		{
			m_pxEditorScreen->GetObjectsListWindow()->SetVisible(m_pxObjectListButton->GetToggleButtonState());
			return true;
		}
		else if(p_krxMessage.GetWindow() == m_pxSettingsButton->GetWHDL())
		{
			m_pxEditorScreen->GetOptionsWindow()->SetVisible(m_pxSettingsButton->GetToggleButtonState());
			return true;
		}
	}

	return __super::HandleMsg(p_krxMessage);
}

//----------------------------------------------------------------------------------------------------------------------
void
CEditorToolBar::Tick()
{
	m_pxObjectPropertiesButton->SetToggleButtonState(m_pxEditorScreen->GetObjectPropertiesWindow()->GetVisible());
	m_pxObjectListButton->SetToggleButtonState(m_pxEditorScreen->GetObjectsListWindow()->GetVisible());
	m_pxSettingsButton->SetToggleButtonState(m_pxEditorScreen->GetOptionsWindow()->GetVisible());
}

//----------------------------------------------------------------------------------------------------------------------
void
CEditorToolBar::FillObjectTypeList()
{
	m_pxObjectTypeComboBox->Clear();
	CGameObjClassMgr::GameObjClassIterator i;
	CGameObjClassMgr& xOCM = CGameObjClassMgr::Get();
	xOCM.StartIterateClasses(i);
	string s;
	while(xOCM.IterateClasses(i, s))
	{
		if(!xOCM.IsVirtualClass(s))
		{
			m_pxObjectTypeComboBox->AddItem(s.c_str());
		}
	}
}
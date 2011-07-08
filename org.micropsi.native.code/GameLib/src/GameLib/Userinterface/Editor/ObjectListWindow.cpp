#include "stdafx.h"
#include "GameLib/UserInterface/Editor/ObjectListWindow.h"

#include "GameLib/World/GameObjectClassManager.h"
#include "GameLib/UserInterface/Editor/EditorScreen.h"
#include "uilib/core/windowmanager.h"

#include <string>

using namespace UILib;
using std::vector;
using std::string;

//----------------------------------------------------------------------------------------------------------------------
CObjectsListWindow::CObjectsListWindow(CEditorScreen* p_pxEditorScreen)
{
	m_pxEditorScreen = p_pxEditorScreen;
	
	SetCaption("Object List");
	SetSize(240, 400);
	SetPos(0, 0);

	m_pxObjectsList = CListBox::Create();
	AddChild(m_pxObjectsList->GetWHDL());
	m_pxObjectsList->SetPos(10, 10);
	m_pxObjectsList->SetSize(216, 300);

	m_pxObjectTypeFilter = CComboBox::Create();
	AddChild(m_pxObjectTypeFilter->GetWHDL());
	m_pxObjectTypeFilter->SetPos(10, 320);
	m_pxObjectTypeFilter->SetSize(216, 24);
	m_pxObjectTypeFilter->SetMaxPopUpListHeight(500);
	FillObjectTypeList();

	m_pxGotoButton = CButton::Create();
	AddChild(m_pxGotoButton->GetWHDL());
	m_pxGotoButton->SetText("Go to Object");
	m_pxGotoButton->SetSize(80, 24);
	m_pxGotoButton->SetPos(10, 350);

	m_pxCloseButton = CButton::Create();
	AddChild(m_pxCloseButton->GetWHDL());
	m_pxCloseButton->SetText("Close");
	m_pxCloseButton->SetSize(80, 24);
	m_pxCloseButton->SetPos(144, 350);
}


//----------------------------------------------------------------------------------------------------------------------
CObjectsListWindow::~CObjectsListWindow()
{
}


//----------------------------------------------------------------------------------------------------------------------
CObjectsListWindow*	
CObjectsListWindow::Create(CEditorScreen* p_pxEditorScreen)
{
	return new CObjectsListWindow(p_pxEditorScreen);
}


//----------------------------------------------------------------------------------------------------------------------
void 
CObjectsListWindow::DeleteNow()
{
	delete this;
}

//----------------------------------------------------------------------------------------------------------------------

bool 
CObjectsListWindow::HandleMsg(const CMessage& p_krxMessage)
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
CObjectsListWindow::Tick()
{
}

//----------------------------------------------------------------------------------------------------------------------
void
CObjectsListWindow::FillObjectList()
{

}

//----------------------------------------------------------------------------------------------------------------------
void
CObjectsListWindow::FillObjectTypeList()
{
	m_pxObjectTypeFilter->Clear();
	CGameObjClassMgr::GameObjClassIterator i;
	CGameObjClassMgr& xOCM = CGameObjClassMgr::Get();
	xOCM.StartIterateClasses(i);
	string s;
	while(xOCM.IterateClasses(i, s))
	{
		if(!xOCM.IsVirtualClass(s))
		{
			m_pxObjectTypeFilter->AddItem(s.c_str());
		}
	}
}
//----------------------------------------------------------------------------------------------------------------------

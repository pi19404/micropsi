#include "stdafx.h"
#include "GameLib/UserInterface/Editor/ObjectPropertiesWindow.h"

#include "GameLib/World/GameObjectClassManager.h"
#include "GameLib/UserInterface/Editor/EditorScreen.h"
#include "uilib/core/windowmanager.h"

#include <string>

using namespace UILib;
using std::vector;
using std::string;

//----------------------------------------------------------------------------------------------------------------------
CObjectPropertiesWindow::CObjectPropertiesWindow(CEditorScreen* p_pxEditorScreen)
{
	m_pxEditorScreen = p_pxEditorScreen;
	
	SetCaption("Object Properties");
	SetSize(240, 400);
	SetPos(0, 0);
}


//----------------------------------------------------------------------------------------------------------------------
CObjectPropertiesWindow::~CObjectPropertiesWindow()
{
}


//----------------------------------------------------------------------------------------------------------------------
CObjectPropertiesWindow*	
CObjectPropertiesWindow::Create(CEditorScreen* p_pxEditorScreen)
{
	return new CObjectPropertiesWindow(p_pxEditorScreen);
}


//----------------------------------------------------------------------------------------------------------------------
void 
CObjectPropertiesWindow::DeleteNow()
{
	delete this;
}

//----------------------------------------------------------------------------------------------------------------------

bool 
CObjectPropertiesWindow::HandleMsg(const CMessage& p_krxMessage)
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
CObjectPropertiesWindow::Tick()
{
}

//----------------------------------------------------------------------------------------------------------------------

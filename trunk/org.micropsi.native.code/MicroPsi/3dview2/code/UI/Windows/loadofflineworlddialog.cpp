#include "Application/stdinc.h"
#include "UI/Windows/loadofflineworlddialog.h"

#include "baselib/filelocator.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/groupbox.h"

#include "UI/Windows/visualizationpicker.h"
#include "UI/Screens/mainmenuscreen.h"

#include "Application/3dview2.h"

#include "World/World.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CLoadOfflineWorldDialog::CLoadOfflineWorldDialog()
{
	m_pxMainMenuScreen = 0;

	SetSize(430, 350);
	SetColor(CColor(200, 200, 200, 100));

	m_pxLoadButton = CButton::Create();
	m_pxLoadButton->SetSize(60, 30);
	m_pxLoadButton->SetPos(290, 310);
	m_pxLoadButton->SetText("Load");
	AddChild(m_pxLoadButton);

	m_pxCancelButton = CButton::Create();
	m_pxCancelButton->SetSize(60, 30);
	m_pxCancelButton->SetPos(360, 310);
	m_pxCancelButton->SetText("Cancel");
	AddChild(m_pxCancelButton);

	CGroupBox* pxGroup = CGroupBox::Create();
	pxGroup->SetText("World File");
	pxGroup->SetSize(200, 280);
	pxGroup->SetPos(10, 10);
	AddChild(pxGroup);

	m_pxFileList = CListBox::Create();
	m_pxFileList->SetSize(180, 180);
	m_pxFileList->SetPos(5, 0);
	pxGroup->AddChild(m_pxFileList);

	m_pxFileDescription = CEditControl::Create();
	m_pxFileDescription->SetSize(180, 66);
	m_pxFileDescription->SetPos(5, 185);
	m_pxFileDescription->SetMultiLine(true);
	m_pxFileDescription->SetReadOnly(true);
	pxGroup->AddChild(m_pxFileDescription);

	m_pxVisualizationPicker = CVisualizationPicker::Create();
	m_pxVisualizationPicker->SetPos(220, 10);
	AddChild(m_pxVisualizationPicker);

	SetVisible(true);
}

//---------------------------------------------------------------------------------------------------------------------
CLoadOfflineWorldDialog::~CLoadOfflineWorldDialog()
{
}

//---------------------------------------------------------------------------------------------------------------------
CLoadOfflineWorldDialog*		
CLoadOfflineWorldDialog::Create()
{
	return new CLoadOfflineWorldDialog();
}

//---------------------------------------------------------------------------------------------------------------------
void
CLoadOfflineWorldDialog::DeleteNow()
{
	delete this;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CLoadOfflineWorldDialog::SetMainMenuScreen(CMainMenuScreen* p_pxMMS)
{
	m_pxMainMenuScreen = p_pxMMS;
}
//---------------------------------------------------------------------------------------------------------------------
bool 
CLoadOfflineWorldDialog::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgButtonClicked)
	{
		if(m_pxLoadButton->GetWHDL() == p_rxMessage.GetWindow())
		{
			CWorld::WrapState eWrapState;
			switch(m_pxVisualizationPicker->GetWrapAround())
			{
				case CVisualizationPicker::WS_ForceWrapAround:		eWrapState = CWorld::WS_ForceWrapAround; break;
				case CVisualizationPicker::WS_ForceNoWrapAround:	eWrapState = CWorld::WS_ForceNoWrapAround; break;
				default:	eWrapState = CWorld::WS_MapDefault; break;
			};

			m_pxMainMenuScreen->LoadWorld(	m_pxFileList->GetSelectedItemAsString().c_str(), 
											m_pxVisualizationPicker->GetSelectedFile(),
											eWrapState);
			SetVisible(false);
			return true;
		}
		else if(m_pxCancelButton->GetWHDL() == p_rxMessage.GetWindow())
		{
			SetVisible(false);
			return true;
		}
	}
	else if(p_rxMessage == msgListBoxChanged)
	{
		assert(p_rxMessage.GetWindow() == m_pxFileList->GetWHDL());
		if(m_pxFileList->GetSelectedItem() < 0)
		{
			m_pxFileDescription->SetText("please select a world file");
		}
		else
		{
			std::string sFile = C3DView2::Get()->GetFileLocator()->GetPath(std::string("offlineworlds>") + m_pxFileList->GetSelectedItemAsString().c_str());
			m_pxFileDescription->SetText(::CWorld::GetDescriptionFromXML(sFile.c_str()).c_str());
		}
		return true;
	}

	return __super::HandleMsg(p_rxMessage);
}
//---------------------------------------------------------------------------------------------------------------------
void			
CLoadOfflineWorldDialog::SetVisible(bool p_bVisible)
{
	if(p_bVisible)
	{
		CWindowMgr::Get().SetModal(this);
		UpdateList();
		m_pxVisualizationPicker->UpdateList();
	}
	else
	{
		CWindowMgr::Get().ReleaseModal(this);
	}
	__super::SetVisible(p_bVisible);
}
//---------------------------------------------------------------------------------------------------------------------
void
CLoadOfflineWorldDialog::UpdateList()
{
	m_pxFileList->Clear();
	WIN32_FIND_DATA FindFileData;
	HANDLE hFind;
	hFind = FindFirstFile(C3DView2::Get()->GetFileLocator()->GetPath("offlineworlds>*.xml").c_str(), &FindFileData);
	if (hFind != INVALID_HANDLE_VALUE) 
    {
		do {
			m_pxFileList->AddItem(FindFileData.cFileName);
		} while(FindNextFile(hFind, &FindFileData));
		FindClose(hFind);
	}

	if(m_pxFileList->NumItems() > 0)
	{
		m_pxFileList->Select(0);
	}
}
//---------------------------------------------------------------------------------------------------------------------

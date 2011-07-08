
#include "stdafx.h"
#include "uilib/controls/filepicker.h"
#include "uilib/core/windowmanager.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CFilePicker::CFilePicker()
{
	SetSize(300, 300);
	m_iPickMode = PM_PickFiles;
	m_sSelection.Clear();

	m_pxOKButton = CButton::Create();
	m_pxCancelButton = CButton::Create();
	m_pxList = CFileBrowserList::Create();

	m_pxOKButton->SetText("OK");
	m_pxOKButton->SetSize(50, 20);
	m_pxOKButton->SetPos(180, 270);
	AddChild(m_pxOKButton);

	m_pxCancelButton->SetText("Cancel");
	m_pxCancelButton->SetSize(50, 20);
	m_pxCancelButton->SetPos(240, 270);
	AddChild(m_pxCancelButton);

	m_pxList->SetSize(280, 250);
	m_pxList->SetPos(10, 10);
	AddChild(m_pxList);

	UpdateOKButton();
}


//---------------------------------------------------------------------------------------------------------------------
CFilePicker::~CFilePicker()
{
}


//---------------------------------------------------------------------------------------------------------------------
CFilePicker*	
CFilePicker::Create()
{
	return new CFilePicker();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CFilePicker::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgButtonClicked)
	{
		if(p_rxMessage.GetWindow() == m_pxOKButton->GetWHDL())
		{
			m_sSelection = m_pxList->GetSelectedItemPath();
			OnResult(RES_FilePicked);
			return true;
		}
		else if(p_rxMessage.GetWindow() == m_pxCancelButton->GetWHDL())
		{
			OnResult(RES_Canceled);
			return true;
		}
	}
	else if(p_rxMessage == msgListBoxChanged)
	{
		if(p_rxMessage.GetWindow() == m_pxList->GetWHDL())
		{
			UpdateOKButton();
			return true;
		}
	}

	return __super::HandleMsg(p_rxMessage);
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CFilePicker::OnVisualizationChange()
{
	CWindow* pxParent = CWindowMgr::Get().GetWindow(GetParent());
	if(pxParent)
	{
		SetPos((pxParent->GetSize().cx - GetSize().cx) / 2, (pxParent->GetSize().cy - GetSize().cy) / 2);
	}
	return __super::OnVisualizationChange();
}

//---------------------------------------------------------------------------------------------------------------------
void	
CFilePicker::SetCallbackWindow(WHDL p_xCallback)
{
	m_xCallbackWindow = p_xCallback;
}


//---------------------------------------------------------------------------------------------------------------------
CStr	
CFilePicker::GetSelection() const
{
	return m_sSelection;
}


//---------------------------------------------------------------------------------------------------------------------
/// setzt den Picking Modus - was kann ausgewählt werden
void	
CFilePicker::SetPickMode(int p_iMode)
{
	m_iPickMode = p_iMode;
}

//---------------------------------------------------------------------------------------------------------------------
void
CFilePicker::UpdateOKButton()
{
	m_pxOKButton->SetDisabled(true);
	if(m_pxList->IsFile(m_pxList->GetSelectedItem()))
	{
		m_pxOKButton->SetDisabled((m_iPickMode & PM_PickFiles) == 0);
	}
	else if(m_pxList->IsFolder(m_pxList->GetSelectedItem()))
	{
		m_pxOKButton->SetDisabled((m_iPickMode & PM_PickFolders) == 0);
	}
	else if(m_pxList->IsDrive(m_pxList->GetSelectedItem()))
	{
		m_pxOKButton->SetDisabled((m_iPickMode & PM_PickDrives) == 0);
	}
}

//---------------------------------------------------------------------------------------------------------------------
/// setzt den aktuellen Pfad
void 
CFilePicker::SetPath(const CStr& p_rsPath)
{
	m_pxList->SetPath(p_rsPath);
}

//---------------------------------------------------------------------------------------------------------------------
/// setzt einen Dateifilter (Standard = "*.*")
void 
CFilePicker::SetFilterRule(const CStr& p_rsDesc, const CStr& p_rsFilter)
{
	m_pxList->SetFilterRule(p_rsDesc, p_rsFilter);
}

//---------------------------------------------------------------------------------------------------------------------
void
CFilePicker::OnResult(Result eResult)
{
	CWindowMgr& wm = CWindowMgr::Get();
	if(eResult == RES_FilePicked)
	{
		if(wm.IsValid(m_xCallbackWindow))
		{
			CWindowMgr::Get().PostMsg(CFilePickedMsg(GetWHDL()), m_xCallbackWindow);
		}
		else
		{
			CWindowMgr::Get().PostMsg(CFilePickedMsg(GetWHDL()), GetParent());
		}
	}
	else
	{
		if(wm.IsValid(m_xCallbackWindow))
		{
			CWindowMgr::Get().PostMsg(CFilePickCanceledMsg(GetWHDL()), m_xCallbackWindow);
		}
		else
		{
			CWindowMgr::Get().PostMsg(CFilePickCanceledMsg(GetWHDL()), GetParent());
		}
	}

	if(m_xOnResultCallback)
	{
		m_xOnResultCallback(this, eResult);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void			
CFilePicker::SetOnResultCallback(CFunctionPointer2<CFilePicker*, CFilePicker::Result>& rxCallback)
{
	m_xOnResultCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------


}


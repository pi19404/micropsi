#include "Application/stdinc.h"
#include "UI/Windows/visualizationpicker.h"

#include "baselib/filelocator.h"

#include "Application/3dview2.h"

#include "World/visualization.h"

using namespace UILib;

//---------------------------------------------------------------------------------------------------------------------
CVisualizationPicker::CVisualizationPicker()
{
	SetText("Visualization");
	SetSize(200, 280);

	m_pxFileList = CListBox::Create();
	m_pxFileList->SetSize(180, 100);
	m_pxFileList->SetPos(5, 0);
	AddChild(m_pxFileList);

	m_pxFileDescription = CEditControl::Create();
	m_pxFileDescription->SetSize(180, 40);
	m_pxFileDescription->SetPos(5, 105);
	m_pxFileDescription->SetMultiLine(true);
	m_pxFileDescription->SetReadOnly(true);
	AddChild(m_pxFileDescription);

	m_pxDefaultWrap = CRadioButton::Create();
	m_pxDefaultWrap->SetText("use map's default wrap mode");
	m_pxDefaultWrap->SetTransparent(true);
	m_pxDefaultWrap->SetPos(5, 195);
	m_pxDefaultWrap->SetSize(180, 20);
	m_pxDefaultWrap->SetSelected(true);
	AddChild(m_pxDefaultWrap);

	m_pxForceWrap = CRadioButton::Create();
	m_pxForceWrap->SetText("force wrap-around");
	m_pxForceWrap->SetTransparent(true);
	m_pxForceWrap->SetPos(5, 215);
	m_pxForceWrap->SetSize(180, 20);
	AddChild(m_pxForceWrap);

	m_pxForceNoWrap = CRadioButton::Create();
	m_pxForceNoWrap->SetText("force no wrap-around");
	m_pxForceNoWrap->SetTransparent(true);
	m_pxForceNoWrap->SetPos(5, 235);
	m_pxForceNoWrap->SetSize(180, 20);
	AddChild(m_pxForceNoWrap);
}

//---------------------------------------------------------------------------------------------------------------------
CVisualizationPicker::~CVisualizationPicker()
{
}

//---------------------------------------------------------------------------------------------------------------------
CVisualizationPicker*		
CVisualizationPicker::Create()
{
	return new CVisualizationPicker();
}

//---------------------------------------------------------------------------------------------------------------------
void
CVisualizationPicker::DeleteNow()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
bool 
CVisualizationPicker::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgListBoxChanged)
	{
		assert(p_rxMessage.GetWindow() == m_pxFileList->GetWHDL());
		if(m_pxFileList->GetSelectedItem() < 0)
		{
			m_pxFileDescription->SetText("please select a visualization file");
		}
		else if(m_pxFileList->GetSelectedItem() == 0)
		{
			m_pxFileDescription->SetText("Uses the visualization that the world file or world server chooses. This is the default.");
		}
		else
		{
			std::string sFile = C3DView2::Get()->GetFileLocator()->GetPath(std::string("visualizations>") + m_pxFileList->GetSelectedItemAsString().c_str());
			m_pxFileDescription->SetText(::CVisualization::GetDescriptionFromXML(sFile.c_str()).c_str());
		}
		return true;
	}

	return __super::HandleMsg(p_rxMessage);
}

//---------------------------------------------------------------------------------------------------------------------
void
CVisualizationPicker::UpdateList()
{
	m_pxFileList->Clear();
	m_pxFileList->AddItem("<world default>");

	WIN32_FIND_DATA FindFileData;
	HANDLE hFind;
	hFind = FindFirstFile(C3DView2::Get()->GetFileLocator()->GetPath("visualizations>*.xml").c_str(), &FindFileData);
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
std::string
CVisualizationPicker::GetSelectedFile() const
{
	if(m_pxFileList->GetSelectedItem() > 0)
	{
		return m_pxFileList->GetSelectedItemAsString().c_str();
	}
	else
	{
		return "";
	}
}

//---------------------------------------------------------------------------------------------------------------------
CVisualizationPicker::WrapState
CVisualizationPicker::GetWrapAround() const
{
	if(m_pxDefaultWrap->GetSelected())
	{
		return WS_MapDefault;
	}
	else if(m_pxForceWrap->GetSelected())
	{
		return WS_ForceWrapAround;
	}
	else
	{
		return WS_ForceNoWrapAround;
	}
}
//---------------------------------------------------------------------------------------------------------------------

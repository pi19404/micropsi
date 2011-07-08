#include "Application/stdinc.h"
#include "UI/Windows/saveasdialog.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/messagebox.h"

#include "UI/Windows/editorpanel.h"

using namespace UILib;
using std::string;

//------------------------------------------------------------------------------------------------------------------------------------------
CSaveAsDialog::CSaveAsDialog(CEditorPanel* p_pxEditorPanel)
{
	m_pxEditorPanel = p_pxEditorPanel;

	SetCaption("Save As...");
	SetSize(400, 100);
	SetAlwaysOnTop(true);
	CWindowMgr::Get().SetModal(this);
	
	m_pxOKButton		= CButton::Create();
	m_pxCancelButton	= CButton::Create();

	m_pxEdit			= CEditControl::Create();

	AddChild(m_pxOKButton->GetWHDL());
	m_pxOKButton->SetText("OK");
	m_pxOKButton->SetSize(50, 20);
	m_pxOKButton->SetPos(280, 50);

	AddChild(m_pxCancelButton->GetWHDL());
	m_pxCancelButton->SetText("Cancel");
	m_pxCancelButton->SetSize(50, 20);
	m_pxCancelButton->SetPos(340, 50);

	AddChild(m_pxEdit->GetWHDL());
	m_pxEdit->SetPos(10, 10);
	m_pxEdit->SetSize(380, 20);
	m_pxEdit->SetText("newisland");
}


//------------------------------------------------------------------------------------------------------------------------------------------
CSaveAsDialog::~CSaveAsDialog()
{
}


//------------------------------------------------------------------------------------------------------------------------------------------
CSaveAsDialog*	
CSaveAsDialog::Create(CEditorPanel* p_pxEditorPanel)
{
	return new CSaveAsDialog(p_pxEditorPanel);
}


//------------------------------------------------------------------------------------------------------------------------------------------
void 
CSaveAsDialog::DeleteNow()
{
	delete this;
}

//------------------------------------------------------------------------------------------------------------------------------------------

bool 
CSaveAsDialog::HandleMsg(const CMessage& p_krxMessage)
{
	if(p_krxMessage == msgButtonClicked)
	{
		if(p_krxMessage.GetWindow() == m_pxOKButton->GetWHDL())
		{
			m_pxEditorPanel->SaveAs(m_pxEdit->GetText().c_str());
			Destroy();
			return true;
		}
		else if(p_krxMessage.GetWindow() == m_pxCancelButton->GetWHDL())
		{
			Destroy();
			return true;
		}
	}

	return __super::HandleMsg(p_krxMessage);
}
//------------------------------------------------------------------------------------------------------------------------------------------

#include "TestScreen.h"

#include "uilib/controls/panel.h"

#include "GameLib/UserInterface/UIScreenStateMachine.h"
#include "GameLib/UserInterface/Input/InputManager.h"

#include "EventDisplayPanel.h"

using namespace UILib;
using namespace std;

//---------------------------------------------------------------------------------------------------------------------
CTestScreen::CTestScreen()
{
    CreateUIElements();

    m_pxBackgroundPanel->SetVisible(false);
}
//---------------------------------------------------------------------------------------------------------------------
CTestScreen::~CTestScreen()
{
}
//---------------------------------------------------------------------------------------------------------------------
CUIScreen*
CTestScreen::Create()
{
	return new CTestScreen();
}
//---------------------------------------------------------------------------------------------------------------------
void 
CTestScreen::Destroy() const
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
void
CTestScreen::CreateUIElements()
{
    m_pxBackgroundPanel = CPanel::Create();
    m_pxBackgroundPanel->SetColor(CColor(0xFF, 0xFF, 0xFF, 0));
    m_pxBackgroundPanel->SetPos(0, 0);
    m_pxBackgroundPanel->SetSize(CWindowMgr::Get().GetDesktop()->GetSize());
    CWindowMgr::Get().GetDesktop()->AddChild(m_pxBackgroundPanel->GetWHDL());

}
//---------------------------------------------------------------------------------------------------------------------
void 
CTestScreen::Update()
{
	if(m_mPanels.size() != 0)
	{
		map<string, CEventDisplayPanel*>::iterator i;
		for(i=m_mPanels.begin(); i!=m_mPanels.end(); i++)
		{
			i->second->Update();
		}
	}
}
//---------------------------------------------------------------------------------------------------------------------
void 
CTestScreen::OnEnter()
{
	m_pxBackgroundPanel->SetVisible(true);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CTestScreen::OnLeave()
{
    m_pxBackgroundPanel->SetVisible(false);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CTestScreen::Map(string sCondition, string sName, float fAxisValue)
{
	CEventDisplayPanel* pPanel;

	map<string, CEventDisplayPanel*>::iterator i;
	i = m_mPanels.find(sName);
	if(m_mPanels.size() == 0  ||  i == m_mPanels.end())
	{
		// nicht gefunden
		pPanel = CEventDisplayPanel::Create();
		m_pxBackgroundPanel->AddChild(pPanel->GetWHDL());
		m_mPanels[sName] = pPanel;
		m_aPanels.push_back(pPanel);
	}
	else
	{
		pPanel = i->second;
	}

	pPanel->Map(sName, sCondition, fAxisValue);
	Layout();
}
//---------------------------------------------------------------------------------------------------------------------
void 
CTestScreen::AddSeparator()
{
	m_aPanels.push_back(0);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CTestScreen::Layout()
{
	int iYPos = 20;
	for(unsigned int i=0; i<m_aPanels.size(); ++i)
	{
		if(m_aPanels[i] != 0)
		{
			m_aPanels[i]->SetPos(CPnt(20, iYPos));
			iYPos += 2 + m_aPanels[i]->GetSize().cy;
		}
		else
		{
			iYPos += 10;
		}
	}
}
//---------------------------------------------------------------------------------------------------------------------

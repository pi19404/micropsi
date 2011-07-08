#include "EventDisplayPanel.h"
#include "GameLib/UserInterface/Input/InputManager.h"

#include "GameLib/GameLibApplication.h"
#include "uilib/controls/label.h"

using namespace UILib;

//----------------------------------------------------------------------------------------------------------------------
CEventDisplayPanel::CEventDisplayPanel()
{
	SetColor(CColor(200, 200, 200, 200));
	SetSize(810, ms_iLineHeight);


	m_pName = CLabel::Create();
	m_pName->SetTextAlign(CLabel::TA_Left);
	m_pName->SetPos(10, 0);
	m_pName->SetSize(200, ms_iLineHeight);
	m_pName->SetBackground(false);
	AddChild(m_pName->GetWHDL());

	m_pTotalCount = CLabel::Create();
	m_pTotalCount->SetTextAlign(CLabel::TA_Left);
	m_pTotalCount->SetPos(710, 0);
	m_pTotalCount->SetSize(30, ms_iLineHeight);
	m_pTotalCount->SetBackground(false);
	AddChild(m_pTotalCount->GetWHDL());

	m_pLastCount = CLabel::Create();
	m_pLastCount->SetTextAlign(CLabel::TA_Left);
	m_pLastCount->SetPos(740, 0);
	m_pLastCount->SetSize(30, ms_iLineHeight);
	m_pLastCount->SetBackground(false);
	AddChild(m_pLastCount->GetWHDL());

	m_pValue = CLabel::Create();
	m_pValue->SetTextAlign(CLabel::TA_Left);
	m_pValue->SetPos(770, 0);
	m_pValue->SetSize(40, ms_iLineHeight);
	m_pValue->SetBackground(false);
	AddChild(m_pValue->GetWHDL());

	m_iTotalEvents = 0;
}

//----------------------------------------------------------------------------------------------------------------------
CEventDisplayPanel::~CEventDisplayPanel()
{
}

//----------------------------------------------------------------------------------------------------------------------
CEventDisplayPanel*	
CEventDisplayPanel::Create()
{
	return new CEventDisplayPanel();
}

//----------------------------------------------------------------------------------------------------------------------
void 
CEventDisplayPanel::DeleteNow()
{
	delete this;
}
//----------------------------------------------------------------------------------------------------------------------
void
CEventDisplayPanel::Map(std::string sName, std::string sCondtion, float fUserValue)
{
	CInputManager* pxInputMgr = CGameLibApplication::Get().GetInputManager();

	if(m_sName.length() == 0)
	{
		m_sName = sName;
		m_pName->SetText(m_sName.c_str());
	}
	else
	{
		assert(m_sName == sName);
		if(m_sName != sName)
		{
			return;
		}
	}

	pxInputMgr->Map(sCondtion, sName, fUserValue);

	int iY = (int) m_aConditions.size() * ms_iLineHeight;

	TCondition tCnd;
	tCnd.m_pCondition = CLabel::Create();
	tCnd.m_pCondition->SetText(sCondtion.c_str());
	tCnd.m_pCondition->SetTextAlign(CLabel::TA_Left);
	tCnd.m_pCondition->SetPos(210, iY);
	tCnd.m_pCondition->SetSize(450, ms_iLineHeight);
	tCnd.m_pCondition->SetBackground(false);
	AddChild(tCnd.m_pCondition->GetWHDL());

	tCnd.m_pUserValue= CLabel::Create();
	tCnd.m_pUserValue->SetText(CStr::Create("%.2f", fUserValue));
	tCnd.m_pUserValue->SetTextAlign(CLabel::TA_Left);
	tCnd.m_pUserValue->SetPos(560, iY);
	tCnd.m_pUserValue->SetSize(40, ms_iLineHeight);
	tCnd.m_pUserValue->SetBackground(false);
	AddChild(tCnd.m_pUserValue->GetWHDL());

	m_aConditions.push_back(tCnd);

	SetSize(GetSize().cx, iY + ms_iLineHeight);
}
//----------------------------------------------------------------------------------------------------------------------
void	
CEventDisplayPanel::Update()
{
	CInputManager* pxInputMgr = CGameLibApplication::Get().GetInputManager();

	int iEventsThisTime = 0;
	float fValue = 0.0f;

	while(pxInputMgr->ConsumeEvent(m_sName))
	{
		iEventsThisTime++;
	}
	if(iEventsThisTime > 0)
	{
		fValue = pxInputMgr->GetAxisValue(m_sName);
		SetColor(CColor(220, 180, 180, 200));
	}
	else
	{
		SetColor(CColor(200, 200, 200, 200));
	}
	m_iTotalEvents += iEventsThisTime;

	m_pTotalCount->SetText(CStr::Create("%d", m_iTotalEvents));
	m_pLastCount->SetText(CStr::Create("%d", iEventsThisTime));
	m_pValue->SetText(CStr::Create("%.2f", fValue));
}
//----------------------------------------------------------------------------------------------------------------------

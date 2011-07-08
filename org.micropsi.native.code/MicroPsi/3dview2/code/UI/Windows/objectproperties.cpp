#include "Application/stdinc.h"
#include "UI/Windows/objectproperties.h"

using namespace UILib;

//------------------------------------------------------------------------------------------------------------------------------------------
CObjectProperties::CObjectProperties()
{
	SetColor(CColor(200, 200, 200, 150));

	SetSize(200, 150);
	SetPos(0, 0);

	m_pxX = CSpinControlNumber::Create();		
	m_pxY = CSpinControlNumber::Create();	
	m_pxZ = CSpinControlNumber::Create();
	m_pxXZAngle		= CSpinControlNumber::Create();

	CLabel* pxXLabel  = CLabel::Create();
	CLabel* pxYLabel  = CLabel::Create();
	CLabel* pxZLabel  = CLabel::Create();
	CLabel* pxXZLabel = CLabel::Create();

	pxXLabel->SetText("Position X:");
	pxXLabel->SetBackground(false);
	pxXLabel->SetTextAlign(CLabel::TA_Right);
	pxXLabel->SetSize(85, 20);
	pxXLabel->SetPos(10, 10);
	AddChild(pxXLabel->GetWHDL());
	m_pxX->SetSize(CSize(85, 20));
	m_pxX->SetPos(105, 10);
	m_pxX->SetDecimals(2);
	m_pxX->SetLimits(-10000.0, 10000.0, 0.05);
	AddChild(m_pxX->GetWHDL());

	pxYLabel->SetText("Position Y:");
	pxYLabel->SetBackground(false);
	pxYLabel->SetTextAlign(CLabel::TA_Right);
	pxYLabel->SetSize(85, 20);
	pxYLabel->SetPos(10, 40);
	AddChild(pxYLabel->GetWHDL());
	m_pxY->SetSize(CSize(85, 20));
	m_pxY->SetPos(105, 40);
	m_pxY->SetDecimals(2);
	m_pxY->SetLimits(-10000.0, 10000.0, 0.05);
	AddChild(m_pxY->GetWHDL());

	pxZLabel->SetText("Position Z:");
	pxZLabel->SetBackground(false);
	pxZLabel->SetTextAlign(CLabel::TA_Right);
	pxZLabel->SetSize(85, 20);
	pxZLabel->SetPos(10, 70);
	AddChild(pxZLabel->GetWHDL());
	m_pxZ->SetSize(CSize(85, 20));
	m_pxZ->SetPos(105, 70);
	m_pxZ->SetDecimals(2);
	m_pxZ->SetLimits(-10000.0, 10000.0, 0.05);
	AddChild(m_pxZ->GetWHDL());

	pxXZLabel->SetText("Rot. XZ (Grad):");
	pxXZLabel->SetBackground(false);
	pxXZLabel->SetTextAlign(CLabel::TA_Right);
	pxXZLabel->SetSize(85, 20);
	pxXZLabel->SetPos(10, 100);
	AddChild(pxXZLabel->GetWHDL());
	m_pxXZAngle->SetSize(CSize(85, 20));
	m_pxXZAngle->SetPos(105, 100);
	m_pxXZAngle->SetDecimals(1);
	m_pxXZAngle->SetLimits(-10000.0, 10000.0, 0.5);
	AddChild(m_pxXZAngle->GetWHDL());
}


//------------------------------------------------------------------------------------------------------------------------------------------
CObjectProperties::~CObjectProperties()
{
}


//------------------------------------------------------------------------------------------------------------------------------------------
CObjectProperties*	
CObjectProperties::Create()
{
	return new CObjectProperties();
}


//------------------------------------------------------------------------------------------------------------------------------------------
void 
CObjectProperties::DeleteNow()
{
	delete this;
}

//------------------------------------------------------------------------------------------------------------------------------------------

bool 
CObjectProperties::HandleMsg(const CMessage& p_krxMessage)
{
	if(p_krxMessage == msgSpinControlChanged)
	{
	}
	return __super::HandleMsg(p_krxMessage);
}

//------------------------------------------------------------------------------------------------------------------------------------------

void	
CObjectProperties::Tick()
{
}

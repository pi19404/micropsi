#include "Application/stdinc.h"
#include "UI/Controls/SliderPanel.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/label.h"
#include "uilib/controls/panel.h"
#include "uilib/controls/sliderspincontrolcombo.h"

#include "Application/3demotion.h"
#include "Application/Face.h"

using std::map;
using std::string;

//---------------------------------------------------------------------------------------------------------------------
CSliderPanel::CSliderPanel()
{
	CSize xSize = UILib::CWindowMgr::Get().GetDesktop()->GetSize();
	SetSize(400, xSize.cy);
	SetPos(xSize.cx - GetSize().cx, 0);
	SetColor(CColor(200, 200, 200, 200)); 

	// master controls

	m_pxAllCheckBox = UILib::CCheckBox::Create();
	m_pxAllCheckBox->SetSize(20, 20);
	m_pxAllCheckBox->SetPos(10, 10);
	m_pxAllCheckBox->SetText("");
	m_pxAllCheckBox->SetTristate(true);
	m_pxAllCheckBox->SetBackground(false);
	m_pxAllCheckBox->SetChecked(UILib::CCheckBox::CB_Checked);
	AddChild(m_pxAllCheckBox->GetWHDL());

	m_eLastAllCheckBoxState = m_pxAllCheckBox->GetChecked();
	
	m_pxStartButton = UILib::CButton::Create();
	m_pxStartButton->SetPos(30, 10);
	m_pxStartButton->SetSize(40, 20);
	m_pxStartButton->SetText("Start");
	AddChild(m_pxStartButton->GetWHDL());

	m_pxMiddleButton = UILib::CButton::Create();
	m_pxMiddleButton->SetPos(80, 10);
	m_pxMiddleButton->SetSize(40, 20);
	m_pxMiddleButton->SetText("Middle");
	AddChild(m_pxMiddleButton->GetWHDL());

	m_pxEndButton = UILib::CButton::Create();
	m_pxEndButton->SetPos(130, 10);
	m_pxEndButton->SetSize(40, 20);
	m_pxEndButton->SetText("End");
	AddChild(m_pxEndButton->GetWHDL());

	m_pxSlider = UILib::CSlider::Create();
	m_pxSlider->SetSize(170, 20);
	m_pxSlider->SetPos(230, 10);
	m_pxSlider->SetSliderRange(100000);
	m_pxSlider->SetBackground(false);
	AddChild(m_pxSlider->GetWHDL());

	// individual controls per bone

	m_pxScrollWindow = UILib::CScrollWindow::Create();
	m_pxScrollWindow->SetPos(0, 35);
	m_pxScrollWindow->SetSize(GetSize().cx, GetSize().cy - m_pxScrollWindow->GetPos().y);

	UILib::CPanel* pxInterior = UILib::CPanel::Create();
	pxInterior->SetColor(CColor(200, 200, 200, 200)); 
	m_pxScrollWindow->SetInteriorWindow(pxInterior);
	m_pxScrollWindow->SetHScrollType(UILib::CScrollWindow::ST_NEVER);
	AddChild(m_pxScrollWindow->GetWHDL());

	CFace* pxFace = C3DEmotion::Get()->GetFace();

	const map<string, CFace::CBone>& axBones = pxFace->GetBones();
	map<string, CFace::CBone>::const_iterator i;
	if(!axBones.empty())
	{
		const int iElementHeight = 25;
		m_pxScrollWindow->SetClientAreaSize(CSize(370, (int) axBones.size() * iElementHeight + 30));

		int iYPos = 5;
		for(i = axBones.begin(); i != axBones.end(); i++)
		{
			UILib::CSliderSpinControlCombo* pxControl = UILib::CSliderSpinControlCombo::Create();
			pxControl->SetLabelText((i->second.m_sName + ":").c_str());
			pxControl->SetLimits(0.0f, 1.0f, 0.01f);
			pxControl->SetSize(350, iElementHeight);
			pxControl->SetPos(30, iYPos);
			pxControl->SetDecimals(2);
			pxControl->SetValue(0.5f);
			pxControl->SetTextAlign(UILib::CLabel::TA_Left);
			pxControl->SetBackground(false);
			m_pxScrollWindow->AddChild(pxControl->GetWHDL());

			m_apxAllSliders[i->second.m_sName].m_pxSliderSpinCombo = pxControl;

			UILib::CCheckBox* pxCheckBox = UILib::CCheckBox::Create();
			pxCheckBox->SetText("");
			pxCheckBox->SetPos(10, iYPos);
			pxCheckBox->SetSize(iElementHeight, iElementHeight);
			pxCheckBox->SetBackground(false);
			pxCheckBox->SetChecked(UILib::CCheckBox::CB_Checked);
			m_pxScrollWindow->AddChild(pxCheckBox->GetWHDL());
			
			m_apxAllSliders[i->second.m_sName].m_pxCheckBox = pxCheckBox;

			iYPos += iElementHeight;
		}
	}

}

//---------------------------------------------------------------------------------------------------------------------
CSliderPanel::~CSliderPanel()
{
}


//---------------------------------------------------------------------------------------------------------------------
CSliderPanel*		
CSliderPanel::Create()
{
	return new CSliderPanel();
}

//---------------------------------------------------------------------------------------------------------------------
void
CSliderPanel::DeleteNow()
{
	delete this;
}

//---------------------------------------------------------------------------------------------------------------------
bool			
CSliderPanel::HandleMsg(const UILib::CMessage& p_rxMessage)
{
	if(p_rxMessage == UILib::msgButtonClicked)
	{
		float fSetting = -1.0f;
		if(p_rxMessage.GetWindow() == m_pxStartButton->GetWHDL())
		{
			m_pxSlider->SetSliderPos(0);
		}
		else if(p_rxMessage.GetWindow() == m_pxMiddleButton->GetWHDL())
		{
			m_pxSlider->SetSliderPos(m_pxSlider->GetSliderRange() / 2);
		}
		else if(p_rxMessage.GetWindow() == m_pxEndButton->GetWHDL())
		{
			m_pxSlider->SetSliderPos(m_pxSlider->GetSliderRange());
		}
	}
	else if(p_rxMessage == UILib::msgCheckBoxChanged)
	{
		if(p_rxMessage.GetWindow() == m_pxAllCheckBox->GetWHDL())
		{
			if(m_pxAllCheckBox->GetChecked() != UILib::CCheckBox::CB_Default)
			{
				if(m_eLastAllCheckBoxState == UILib::CCheckBox::CB_Default)
				{
					// checked / unchecked --> safe state first
					map<string, BoneGuiElement>::iterator i;
					for(i=m_apxAllSliders.begin(); i!=m_apxAllSliders.end(); i++)
					{
						i->second.m_bOldState = i->second.m_pxCheckBox->GetCheckMark();
					}
				}

				// set new state
				map<string, BoneGuiElement>::iterator i;
				for(i=m_apxAllSliders.begin(); i!=m_apxAllSliders.end(); i++)
				{
					i->second.m_pxCheckBox->SetChecked(m_pxAllCheckBox->GetCheckMark());
				}
			}
			else
			{
				// default --> restore state
				map<string, BoneGuiElement>::const_iterator i;
				for(i=m_apxAllSliders.begin(); i!=m_apxAllSliders.end(); i++)
				{
					if(i->second.m_bOldState)
					{
						i->second.m_pxCheckBox->SetChecked(UILib::CCheckBox::CB_Checked); 
					}
					else
					{
						i->second.m_pxCheckBox->SetChecked(UILib::CCheckBox::CB_Unchecked); 
					}
				}
			}

			m_eLastAllCheckBoxState = m_pxAllCheckBox->GetChecked();
			return true;
		}
		else
		{ 
			map<string, BoneGuiElement>::iterator i;
			for(i=m_apxAllSliders.begin(); i!=m_apxAllSliders.end(); i++)
			{
				if(p_rxMessage.GetWindow() == i->second.m_pxCheckBox->GetWHDL())
				{
					// if any checkbox was clicked, set the master checkbox to default
					// before we do that, save the state of the checkbox we just clicked
					// because the master check box will try to restore all check box states
					if(m_pxAllCheckBox->GetChecked() != UILib::CCheckBox::CB_Default)
					{
						map<string, BoneGuiElement>::iterator i;
						for(i=m_apxAllSliders.begin(); i!=m_apxAllSliders.end(); i++)
						{
							i->second.m_bOldState = i->second.m_pxCheckBox->GetCheckMark();
						}

						m_pxAllCheckBox->SetChecked(UILib::CCheckBox::CB_Default);
						m_eLastAllCheckBoxState = UILib::CCheckBox::CB_Default;
					}
					return true;
				}
			}
		}
	}
	else if(p_rxMessage == UILib::msgSliderChanged)
	{
		if(p_rxMessage.GetWindow() == m_pxSlider->GetWHDL())
		{
			SetAllSelectedSliders((float) m_pxSlider->GetSliderPos() / (float) m_pxSlider->GetSliderRange());
			return true;
		}
	}

	return __super::HandleMsg(p_rxMessage);
}

//---------------------------------------------------------------------------------------------------------------------
float
CSliderPanel::GetCurrentValue(std::string p_sBoneName) const
{
	if(m_apxAllSliders.empty())
	{
		assert(false);
		return 0.0f;
	}

	map<string, BoneGuiElement>::const_iterator i;
	i = m_apxAllSliders.find(p_sBoneName);
	if(i == m_apxAllSliders.end())
	{
		assert(false);
		return 0.0f;
	}

	return i->second.m_pxSliderSpinCombo->GetValue();
}
//---------------------------------------------------------------------------------------------------------------------
void
CSliderPanel::SetAllSelectedSliders(float p_fValue)
{
	map<string, BoneGuiElement>::const_iterator i;
	for(i=m_apxAllSliders.begin(); i!=m_apxAllSliders.end(); i++)
	{
		if(i->second.m_pxCheckBox->GetCheckMark())
		{
			i->second.m_pxSliderSpinCombo->SetValue(p_fValue);
		}
	}
}
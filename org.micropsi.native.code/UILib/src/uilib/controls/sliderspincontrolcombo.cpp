#include "stdafx.h"
#include <stdio.h>
#include "uilib/controls/sliderspincontrolcombo.h"
#include "uilib/controls/spincontrolnumber.h"
#include "uilib/controls/label.h"
#include "uilib/controls/slider.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"

namespace UILib
{


//---------------------------------------------------------------------------------------------------------------------
/**
	Default Konstruktor
*/
CSliderSpinControlCombo::CSliderSpinControlCombo()
{
	m_bDrawBackGround = true;
	SetSize(100, 100);

	m_pxLabel	 = CLabel::Create();
	m_pxSlider	 = CSlider::Create();
	m_pxSpinCtrl = CSpinControlNumber::Create();

	AddChild(m_pxLabel->GetWHDL());
	AddChild(m_pxSlider->GetWHDL());
	AddChild(m_pxSpinCtrl->GetWHDL());

	m_fLabelSpacePercent = 0.4f;
	m_fSliderSpacePercent = 0.4f;

	DoLayout();
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Destruktor
*/
CSliderSpinControlCombo::~CSliderSpinControlCombo()
{
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	Erzeugt ein neues Panel
*/
CSliderSpinControlCombo* 
CSliderSpinControlCombo::Create()
{
	return new CSliderSpinControlCombo();
}

//---------------------------------------------------------------------------------------------------------------------
void	
CSliderSpinControlCombo::SetLimits(float p_fMin, float p_fMax, float p_fStep)
{
	m_pxSpinCtrl->SetLimits(p_fMin, p_fMax, p_fStep);

	float fMin, fMax, fStep;
	m_pxSpinCtrl->GetLimitsFloat(fMin, fMax, fStep);
	assert(fStep > 0.0f);
	int iPositions = (int) ((fMax - fMin) / fStep) + 1;
	if(fMin + (float) iPositions * fStep < fMax)
	{
		iPositions++;
	}

	m_pxSlider->SetSliderRange(iPositions-1);
}

//---------------------------------------------------------------------------------------------------------------------
void	
CSliderSpinControlCombo::SetValue(float p_fValue)
{
	m_pxSpinCtrl->SetValue(p_fValue);
	UpdateSliderPos();
}

//---------------------------------------------------------------------------------------------------------------------
float	
CSliderSpinControlCombo::GetValue() const
{
	return m_pxSpinCtrl->GetValueFloat();
}

//---------------------------------------------------------------------------------------------------------------------
void	
CSliderSpinControlCombo::SetDecimals(int p_iDecimals)
{
	m_pxSpinCtrl->SetDecimals(p_iDecimals);
}

//---------------------------------------------------------------------------------------------------------------------
void	
CSliderSpinControlCombo::SetBackground(bool p_bBackground)
{
	SetTransparent(!p_bBackground);
	m_bDrawBackGround = p_bBackground;
	m_pxLabel->SetBackground(p_bBackground);
	m_pxSlider->SetBackground(p_bBackground);
}

//---------------------------------------------------------------------------------------------------------------------
void	
CSliderSpinControlCombo::SetLabelText(const CStr& p_rsText)
{
	m_pxLabel->SetText(p_rsText);
}
//---------------------------------------------------------------------------------------------------------------------
void	
CSliderSpinControlCombo::SetTextAlign(CLabel::HorizontalTextAlignment p_eHTextAlignment, CLabel::VerticalTextAlignment p_eVTextAlignment)
{
	m_pxLabel->SetTextAlign(p_eHTextAlignment, p_eVTextAlignment);
}
//---------------------------------------------------------------------------------------------------------------------
void	
CSliderSpinControlCombo::SetLayout(float p_fLabelWidth, float p_fSliderWidth, float p_fSpinControlWidth)
{
	float fMagnitude = p_fLabelWidth + p_fSliderWidth + p_fSpinControlWidth;

	m_fLabelSpacePercent = p_fLabelWidth / fMagnitude;
	m_fSliderSpacePercent = p_fSliderWidth / fMagnitude;
}

//------------------------------------------------------------------------------------------------------------------------------------------------------------
void 
CSliderSpinControlCombo::DoLayout()
{
	CSize xSize = GetSize();
	m_pxLabel->SetSize((int) ((float) xSize.cx * m_fLabelSpacePercent), xSize.cy);
	m_pxLabel->SetPos(0, 0);
	m_pxSlider->SetSize(CSize((int) ((float) xSize.cx * m_fSliderSpacePercent), xSize.cy));
	m_pxSlider->SetPos(m_pxLabel->GetSize().cx, (xSize.cy - m_pxSlider->GetSize().cy) / 2);
	m_pxSpinCtrl->SetSize(CSize((int) ((float) xSize.cx * (1.0f - m_fLabelSpacePercent - m_fSliderSpacePercent)), xSize.cy));
	m_pxSpinCtrl->SetPos(m_pxLabel->GetSize().cx + m_pxSlider->GetSize().cx, (xSize.cy - m_pxSpinCtrl->GetSize().cy) / 2);
}

//---------------------------------------------------------------------------------------------------------------------
void		 
CSliderSpinControlCombo::UpdateSliderPos()
{
	float fMin, fMax, fStep;
	m_pxSpinCtrl->GetLimitsFloat(fMin, fMax, fStep);
	assert(fStep > 0.0f);
	float fValue = m_pxSpinCtrl->GetValueFloat();

	int iPos = (int) ((fValue - fMin) / fStep);
	m_pxSlider->SetSliderPos(iPos);
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CSliderSpinControlCombo::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgSliderChanged)
	{
		int iSliderPos = m_pxSlider->GetSliderPos();
		float fMin, fMax, fStep;
		m_pxSpinCtrl->GetLimitsFloat(fMin, fMax, fStep);
		assert(fStep > 0.0f);
	
		float fNewValue = fMin + (float) iSliderPos * fStep;
		m_pxSpinCtrl->SetValue(fNewValue);

		return true;
	}
	if(p_rxMessage == msgSpinControlChanged)
	{
		UpdateSliderPos();
		return true;
	}
	else
	{
		return __super::HandleMsg(p_rxMessage);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSliderSpinControlCombo::Paint(const CPaintContext& p_rxCtx)
{	
	if(m_bDrawBackGround)
	{
		CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());
		v->DrawBackground(p_rxCtx, GetRect());
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CSliderSpinControlCombo::OnVisualizationChange()
{
	DoLayout();
	return __super::OnVisualizationChange();
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CSliderSpinControlCombo::OnResize()
{
	DoLayout();
	return this->__super::OnResize();
}

//---------------------------------------------------------------------------------------------------------------------
CStr 
CSliderSpinControlCombo::GetDebugString() const					
{ 
	return "CSliderSpinControlCombo"; 
}
//---------------------------------------------------------------------------------------------------------------------

} // namespace UIlib


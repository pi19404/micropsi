
#include "stdafx.h"
#include "uilib/controls/spincontrolnumber.h"
#include "uilib/core/windowmanager.h"

namespace UILib
{
	
//---------------------------------------------------------------------------------------------------------------------
CSpinControlNumber::CSpinControlNumber()
{
	m_dValue	= 0.0;
	m_iDecimals	= 0;
	m_dMinValue	= 0.0;
	m_dMaxValue = 1.0;
    m_dStep		= 1.0;
	Update();
}


//---------------------------------------------------------------------------------------------------------------------
CSpinControlNumber::~CSpinControlNumber()
{
}


//---------------------------------------------------------------------------------------------------------------------
CSpinControlNumber* CSpinControlNumber::Create()
{
	return new CSpinControlNumber();
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSpinControlNumber::SetValue(double p_dValue)
{
	p_dValue = max(m_dMinValue, p_dValue);
	p_dValue = min(m_dMaxValue, p_dValue);

	if(m_dValue != p_dValue)
	{
		m_dValue = p_dValue;
		Update();
		OnChange();
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSpinControlNumber::SetLimits(double p_dMin, double p_dMax, double p_dStep)
{
	m_dMinValue = p_dMin;
	m_dMaxValue = p_dMax;
	if(m_dMaxValue < m_dMinValue)
	{
		m_dMaxValue = m_dMinValue;
	}
	m_dStep = p_dStep;

	SetValue(GetValue());  // brings value into range if nessecary
}

//---------------------------------------------------------------------------------------------------------------------
void	
CSpinControlNumber::GetLimitsFloat(float& p_rfMin, float& p_rfMax, float& p_rfStep)
{
	p_rfMin		= (float) m_dMinValue;
	p_rfMax		= (float) m_dMaxValue;
	p_rfStep	= (float) m_dStep;
}

//---------------------------------------------------------------------------------------------------------------------
void
CSpinControlNumber::SetDecimals(int p_iDecimals)	
{ 
	m_iDecimals = p_iDecimals; Update(); 
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CSpinControlNumber::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgEditControlUpdated)
	{
		if(p_rxMessage.GetWindow() == m_pxEditCtrl->GetWHDL())
		{
			char* stopstring;
			SetValue(strtod(m_pxEditCtrl->GetText().c_str(), &stopstring));
			Update();
			return true;
		}
	}

	return __super::HandleMsg(p_rxMessage);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSpinControlNumber::Update()
{
	CStr sFormatString;
	sFormatString.Format("%%.%df", m_iDecimals);
	CStr sString;
	sString.Format(sFormatString.c_str(), m_dValue);
	m_pxEditCtrl->SetText(sString);
	InvalidateWindow();
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSpinControlNumber::Up()
{
	SetValue(GetValue() + m_dStep);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSpinControlNumber::Down()
{
	SetValue(GetValue() - m_dStep);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt den Wert eines benannten Attributes. Siehe UIlib Doku für eine vollständige Liste der Attribute.
	\param	p_rsName	Name des Attributes
	\param	p_rsValue	neuer Wert
	\return	true, wenn erfolgreich
*/
bool
CSpinControlNumber::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if(p_rsName == "value")	{ SetValue(p_rsValue.ToDouble());  return true; }
	if(p_rsName == "min")		{ SetLimits(p_rsValue.ToDouble(), m_dMaxValue, m_dStep); return true; }
	if(p_rsName == "max")		{ SetLimits(m_dMinValue, p_rsValue.ToDouble(), m_dStep); return true; }
	if(p_rsName == "step")		{ SetLimits(m_dMinValue, m_dMaxValue, p_rsValue.ToDouble()); return true; }
	if(p_rsName == "decimals")	{ SetDecimals(p_rsValue.ToInt()); return true; }
	return __super::SetAttrib(p_rsName,p_rsValue);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	liefert den Wert eines benannten Attributes. Siehe UILib Doku für eine vollständige Liste der Attribute. ues

	\param	p_rsName		Name des Attributes
	\param	po_srValue		Ausgabe-Parameter: String, der den Wert entgegennimmt
	\return true, wenn Wert zurückgeliefert wurde; false, wenn der Attributnamen nicht bekannt ist
*/
bool 
CSpinControlNumber::GetAttrib(const CStr& p_rsName, CStr& po_srValue) const
{
	if(p_rsName == "value")	{ po_srValue.Format("%.2f",GetValue()); return true;}
	if(p_rsName == "min")		{ po_srValue.Format("%.2f",(float)m_dMinValue); return true;}
	if(p_rsName == "max")		{ po_srValue.Format("%.2f",(float)m_dMaxValue); return true;}
	if(p_rsName == "step")		{ po_srValue.Format("%.2f",(float)m_dStep); return true;}
	if(p_rsName == "decimals")	{ po_srValue.Format("%d",m_iDecimals); return true;}	

	return __super::GetAttrib(p_rsName,po_srValue);
}


} //namespace UILib


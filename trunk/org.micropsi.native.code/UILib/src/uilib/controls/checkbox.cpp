#include "stdafx.h"
#include "uilib/controls/checkbox.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"


namespace UILib
{


//---------------------------------------------------------------------------------------------------------------------
CCheckBox::CCheckBox()
{
	m_pxLabel = CLabel::Create();
    m_pxLabel->SetBackground(false);
	AddChild(m_pxLabel->GetWHDL());
	m_bTristate = false;
	m_eState = CB_Unchecked;
    m_bBackground = true;
}


//---------------------------------------------------------------------------------------------------------------------
CCheckBox::~CCheckBox()
{
}


//---------------------------------------------------------------------------------------------------------------------
CCheckBox* 
CCheckBox::Create()
{
	return new CCheckBox();
}


//---------------------------------------------------------------------------------------------------------------------
int 
CCheckBox::SetChecked(int p_eChecked)
{
	if(m_bTristate  ||  p_eChecked != CB_Default)		  
	{
		m_eState = p_eChecked;
		InvalidateWindow();
	}

	return m_eState;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CCheckBox::SetTristate(bool p_bTristate)
{ 
	m_bTristate = p_bTristate; 
	if(!m_bTristate  &&  m_eState == CB_Default)
	{
		m_eState = CB_Unchecked;
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CCheckBox::SetText(CStr p_sText)
{
	m_pxLabel->SetText(p_sText);
	this->OnResize();
}


//---------------------------------------------------------------------------------------------------------------------
int 
CCheckBox::NextState()
{
	switch(m_eState) 
	{
	case CB_Unchecked:
		m_eState = CB_Checked; break;

	case CB_Checked:
		if(m_bTristate)
		{
			m_eState = CB_Default;
		}
		else
		{
			m_eState = CB_Unchecked;
		}
		break;

	case CB_Default:
	default:
		m_eState = CB_Unchecked;
	}

	this->OnStateChange();
	InvalidateWindow();

	return m_eState;
}


//---------------------------------------------------------------------------------------------------------------------
/// schaltet den Hintergrund ein/aus
void 
CCheckBox::SetBackground(bool p_bBackground)	
{ 
	if(m_bBackground != p_bBackground) 
	{
		m_bBackground = p_bBackground; 
        if(!m_bBackground)
        {
            SetTransparent(true);
        }
		InvalidateWindow(); 
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CCheckBox::Paint(const CPaintContext& p_rxCtx)
{	
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());

	CVisualization::CheckBoxState eState = CVisualization::CB_Unchecked;
	switch(m_eState) {
	case CB_Unchecked:	eState = CVisualization::CB_Unchecked;	break;
	case CB_Checked:	eState = CVisualization::CB_Checked;	break;
	case CB_Default:	eState = CVisualization::CB_Default;	break;
	}

	if(m_bBackground)
	{
		v->DrawBackground(p_rxCtx, GetRect());
	}
	v->DrawCheckBox(p_rxCtx, CPnt(0, (GetSize().cy - m_xCheckBoxSize.cy) / 2), eState, GetButtonDown());
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CCheckBox::OnResize()
{
	m_pxLabel->AutoSize();
	m_pxLabel->SetPos(2*m_xCheckBoxSize.cx, (GetSize().cy - m_pxLabel->GetSize().cy) / 2);
	return this->__super::OnResize();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CCheckBox::OnVisualizationChange()
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		CVisualization* v	= CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());

		m_pxLabel->AutoSize();
		m_xCheckBoxSize = v->GetMetrics()->m_xCheckBoxSize;
		int iHeight = max(m_pxLabel->GetSize().cy, m_xCheckBoxSize.cy);
		
		SetMinSize(CSize(0, iHeight));
		AutoSize();
	}

	this->OnResize();
	return this->__super::OnVisualizationChange();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CCheckBox::OnClick()
{ 
	NextState();
	return true; 
}
//---------------------------------------------------------------------------------------------------------------------
bool 
CCheckBox::OnStateChange()						
{ 
	CWindowMgr::Get().PostMsg(CCheckBoxChangedMsg(GetWHDL()), GetParent());
	if(m_xOnStateChangeCallback)
	{
		m_xOnStateChangeCallback(this);
	}

	return true; 
}
//---------------------------------------------------------------------------------------------------------------------
CStr 
CCheckBox::GetDebugString() const		
{ 
	return CStr("CCheckBox Label = ") + m_pxLabel->GetText(); 
}
//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt den Wert eines benannten Attributes. Siehe UIlib Doku für eine vollständige Liste der Attribute.
	\param	p_rsName	Name des Attributes
	\param	p_rsValue	neuer Wert
	\return	true, wenn erfolgreich
*/
bool 
CCheckBox::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if(p_rsName=="state")
	{
		if(p_rsValue == "checked")		{ SetChecked(CB_Checked); return true; }
		if(p_rsValue == "unchecked")	{ SetChecked(CB_Unchecked); return true; }
		if(p_rsValue == "default")		{ SetChecked(CB_Default); return true; }		
		return false;
	}

	if(p_rsName == "tristate")	{ SetTristate(p_rsValue.ToInt() !=0 ); return true; }
	if(p_rsName == "caption")	{ SetText(p_rsValue); return true; }
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
CCheckBox::GetAttrib(const CStr& p_rsName, CStr& po_srValue) const
{
	if(p_rsName=="state")
	{
		switch(m_eState)
		{
			case CB_Checked:	po_srValue = "checked";		break;
			case CB_Unchecked:	po_srValue = "unchecked";	break;
			case CB_Default:	po_srValue = "default";		break;
			default:			po_srValue = "invalid";		return false;
		}
		return true;
	}
	if(p_rsName == "tristate")	{ po_srValue = (GetTristate()?"1":"0"); return true;}	
	if(p_rsName == "caption")	{ po_srValue = m_pxLabel->GetText(); return true;}
	return __super::GetAttrib(p_rsName,po_srValue);
}
//---------------------------------------------------------------------------------------------------------------------


} //namespace UILib



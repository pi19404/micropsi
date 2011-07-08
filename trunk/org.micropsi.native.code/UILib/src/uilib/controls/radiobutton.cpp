#include "stdafx.h"
#include "uilib/controls/radiobutton.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CRadioButton::CRadioButton()
{
	m_pxLabel = CLabel::Create();
    m_pxLabel->SetBackground(false);
	AddChild(m_pxLabel->GetWHDL());
	m_bSelected = false;
    m_bBackground = true;
}


//---------------------------------------------------------------------------------------------------------------------
CRadioButton::~CRadioButton()
{
}


//---------------------------------------------------------------------------------------------------------------------
CRadioButton* 
CRadioButton::Create()
{
	return new CRadioButton();
}


//---------------------------------------------------------------------------------------------------------------------
void 
CRadioButton::AutoSize(bool p_bMayShrink)
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		m_pxLabel->AutoSize();

		CVisualization* v = CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());
		CSize xSize = CSize(m_pxLabel->GetSize().cx + 2*v->GetMetrics()->m_xRadioButtonSize.cx, GetSize().cy);

		if(p_bMayShrink == false)
		{
			AssureMinSize(xSize);
		}
		else
		{
			SetSize(xSize);
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CRadioButton::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgClearRadioButton)
	{
		SetSelected(false);
		return true;
	}

	return __super::HandleMsg(p_rxMessage);
}  



//---------------------------------------------------------------------------------------------------------------------
bool 
CRadioButton::SetSelected(bool p_bSelected)
{
	if(m_bSelected != p_bSelected)
	{
		m_bSelected = p_bSelected;
		CWindowMgr& wm = CWindowMgr::Get();
		OnStateChange();

		if(m_bSelected)
		{
			// tell all other radio buttons with the same parent to clear themselves
			
			if(GetParent())
			{
				CWindow* pParent = wm.GetWindow(GetParent());
				int i, iC = pParent->NumChildWindows();
				for(i=0; i<iC; ++i)
				{
					if(GetWHDL() != pParent->GetChild(i))
					{
						wm.SendMsg(CClearRadioButtonMsg(), pParent->GetChild(i));
					}
				}
			}
		}

		InvalidateWindow();
	}

	return m_bSelected;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CRadioButton::SetText(CStr p_sText)
{
	m_pxLabel->SetText(p_sText);
	this->OnResize();
}


//---------------------------------------------------------------------------------------------------------------------
/// schaltet den Hintergrund ein/aus
void 
CRadioButton::SetBackground(bool p_bBackground)	
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
bool 
CRadioButton::OnClick()
{
	SetSelected(true);
	OnStateChange();
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CRadioButton::Paint(const CPaintContext& p_rxCtx)
{	
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());
    if(m_bBackground)
    {
        v->DrawBackground(p_rxCtx, GetRect());
    }
   	v->DrawRadioButton(p_rxCtx, CPnt(0, (GetSize().cy - v->GetMetrics()->m_xRadioButtonSize.cy) / 2), m_bSelected, GetButtonDown());
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CRadioButton::OnResize()
{
	m_pxLabel->AutoSize();
	m_pxLabel->SetPos(2*m_xRadioBtnSize.cx, (GetSize().cy - m_pxLabel->GetSize().cy) / 2);
	return this->__super::OnResize();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CRadioButton::OnVisualizationChange()
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		CVisualization* v	= CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());

		m_pxLabel->AutoSize();
		m_xRadioBtnSize = v->GetMetrics()->m_xRadioButtonSize;
		int iHeight = max(m_pxLabel->GetSize().cy, m_xRadioBtnSize.cy);
		
		SetMinSize(CSize(0, iHeight));
	}

	this->OnResize();
	return this->__super::OnVisualizationChange();
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt den Wert eines benannten Attributes. Siehe UIlib Doku für eine vollständige Liste der Attribute.
	\param	p_rsName	Name des Attributes
	\param	p_rsValue	neuer Wert
	\return	true, wenn erfolgreich
*/
bool 
CRadioButton::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if(p_rsName == "caption")	{ SetText(p_rsValue); return true; }
	if(p_rsName == "selected")	{ SetSelected(p_rsValue.ToInt()!=0); return true; }
	return __super::SetAttrib(p_rsName, p_rsValue);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	liefert den Wert eines benannten Attributes. Siehe UILib Doku für eine vollständige Liste der Attribute. ues

	\param	p_rsName		Name des Attributes
	\param	po_srValue		Ausgabe-Parameter: String, der den Wert entgegennimmt
	\return true, wenn Wert zurückgeliefert wurde; false, wenn der Attributnamen nicht bekannt ist
*/
bool 
CRadioButton::GetAttrib(const CStr& p_rsName, CStr& po_srValue) const
{
	if(p_rsName=="caption"){po_srValue=GetText();return true;}
	if(p_rsName=="selected"){po_srValue=(GetSelected()?"1":"0");return true;}
	return __super::GetAttrib(p_rsName,po_srValue);
}

//---------------------------------------------------------------------------------------------------------------------
CStr 
CRadioButton::GetDebugString() const		
{ 
	return CStr("CRadioButton Label = ") + GetText(); 
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CRadioButton::OnStateChange()			
{ 
	CWindowMgr::Get().PostMsg(CRadioButtonChangedMsg(GetWHDL()), GetParent());
	if(m_xOnStateChangeCallback)
	{
		m_xOnStateChangeCallback(this);
	}
	return true; 
}
//---------------------------------------------------------------------------------------------------------------------


} // namespace UILib



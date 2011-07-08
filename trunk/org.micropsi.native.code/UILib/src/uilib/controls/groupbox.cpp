
#include "stdafx.h"
#include "uilib/controls/groupbox.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CGroupBox::CGroupBox()
{
	m_sText = "GroupBox";

	m_pxClientArea = CWindow::Create();
	__super::AddChild(m_pxClientArea->GetWHDL());
}

//---------------------------------------------------------------------------------------------------------------------
CGroupBox::~CGroupBox()
{
}

//---------------------------------------------------------------------------------------------------------------------
CGroupBox*	
CGroupBox::Create()
{ 
	return new CGroupBox(); 
}


//---------------------------------------------------------------------------------------------------------------------
void 
CGroupBox::SetText(const CStr& p_rsText)			
{ 
	if(p_rsText != m_sText)
	{
		m_sText = p_rsText;
		InvalidateWindow();
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CGroupBox::AddChild(WHDL p_hWnd)
{
	return m_pxClientArea->AddChild(p_hWnd);
}

//---------------------------------------------------------------------------------------------------------------------
int 
CGroupBox::NumChildWindows() const
{ 
	return m_pxClientArea->NumChildWindows(); 
}

//---------------------------------------------------------------------------------------------------------------------
WHDL 
CGroupBox::GetChild(int p_iIndex) const
{ 
	return m_pxClientArea->GetChild(p_iIndex); 
}

//---------------------------------------------------------------------------------------------------------------------
void 
CGroupBox::RemoveChild(WHDL p_hWnd)
{
	m_pxClientArea->RemoveChild(p_hWnd);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CGroupBox::Paint(const CPaintContext& p_xrCtx)
{
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_xrCtx.GetDevice(), GetVisualizationType());	
	v->DrawGroupBox(p_xrCtx, GetRect(), m_sText);
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CGroupBox::OnResize()
{			
	m_pxClientArea->SetSize(GetSize().cx - m_xFrameSize.left - m_xFrameSize.right, 
							GetSize().cy - m_xFrameSize.top - m_xFrameSize.bottom);
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CGroupBox::OnVisualizationChange()
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		CVisualization* v	= CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());
		m_xFrameSize = v->GetMetrics()->m_xGroupBoxPadding;
		m_pxClientArea->SetPos(m_xFrameSize.left, m_xFrameSize.top);
	}

	OnResize();
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
CGroupBox::SetAttrib(const CStr& p_srName, const CStr& p_srValue)
{
	if(p_srName == "caption")	{ SetText(p_srValue); return true; }
	return __super::SetAttrib(p_srName, p_srValue);
}

//---------------------------------------------------------------------------------------------------------------------
/**
	liefert den Wert eines benannten Attributes. Siehe UILib Doku für eine vollständige Liste der Attribute. ues

	\param	p_rsName		Name des Attributes
	\param	po_srValue		Ausgabe-Parameter: String, der den Wert entgegennimmt
	\return true, wenn Wert zurückgeliefert wurde; false, wenn der Attributnamen nicht bekannt ist
*/
bool 
CGroupBox::GetAttrib(const CStr& p_srName, CStr& po_srValue) const
{
	if(p_srName == "caption")
	{
		po_srValue = m_sText; 
		return true;
	}
	return __super::GetAttrib(p_srName, po_srValue);
}

//---------------------------------------------------------------------------------------------------------------------
CStr
CGroupBox::GetDebugString() const		
{
	return CStr("CGroupBox Label = ") + m_sText; 
}
//---------------------------------------------------------------------------------------------------------------------

} // namespace UILib


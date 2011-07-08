#include "stdafx.h"
#include <math.h>
#include "uilib/controls/scrollbar.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
void 
CScrollBar::CScrollBarButton::Paint(const CPaintContext& p_rxCtx)
{
	CVisualization::FrameType eFt = CVisualization::FT_BtnUp;
	if(!GetDisabled())
	{
		if(GetButtonDown()) { eFt = CVisualization::FT_BtnDown; }
	}

	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());
	v->DrawScrollBarButton(p_rxCtx, CPnt(0, 0), m_eButtonType, eFt);
}


//---------------------------------------------------------------------------------------------------------------------
CScrollBar::CScrollBar()
{
	m_bSoftDrag		= true;
	m_bDragging		= false;
	m_pxButton1		= 0;
	m_pxButton2		= 0;
	m_iScrollPos	= 0;
	m_iScrollRange	= 0;
	m_iPageSize		= 1;
	m_iScrollSpeed  = 1;

	SetStyle(SB_Vertical);
	SetSize(CSize(100, 100));
}


//---------------------------------------------------------------------------------------------------------------------
CScrollBar::~CScrollBar()
{
}


//---------------------------------------------------------------------------------------------------------------------
CScrollBar* 
CScrollBar::Create()
{
	return new CScrollBar();
}


//---------------------------------------------------------------------------------------------------------------------
// löscht das Fenster sofort
void 
CScrollBar::DeleteNow()
{
	delete this;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CScrollBar::SetSize(const CSize& p_rxSize)
{
	CSize xSize = p_rxSize;
	if(m_iStyle &  SB_Horizontal)
	{
		if(m_xButtonSize.cy > 0)	{ xSize.cy = m_xButtonSize.cy; }
	}
	else
	{
		if(m_xButtonSize.cx > 0)	{ xSize.cx = m_xButtonSize.cx; }
	}
	__super::SetSize(xSize);
}



//---------------------------------------------------------------------------------------------------------------------
///	schaltet Buttons ein oder aus
void 
CScrollBar::SetButtons(bool p_bButtons)
{
	if (p_bButtons == true)
	{
		m_iStyle = m_iStyle & ~SB_NoButtons;
		m_xRealButtonSize = m_xButtonSize;

		if(m_pxButton1 == 0)
		{
			m_pxButton1 = new CScrollBarButton();
			AddChild(m_pxButton1->GetWHDL());
			m_pxButton1->SetSize(m_xButtonSize.cx, m_xButtonSize.cy);
		}
		if(m_pxButton2 == 0)
		{
			m_pxButton2 = new CScrollBarButton();
			AddChild(m_pxButton2->GetWHDL());
			m_pxButton2->SetSize(m_xButtonSize.cx, m_xButtonSize.cy);
		}
	}
	else
	{
		m_iStyle = m_iStyle | SB_NoButtons;
		m_xRealButtonSize = CSize(0, 0);

		if(m_pxButton1)
		{
			RemoveChild(m_pxButton1->GetWHDL());
			m_pxButton1->Destroy();
			m_pxButton1 = 0;
		}
		if(m_pxButton2)
		{
			RemoveChild(m_pxButton2->GetWHDL());
			m_pxButton2->Destroy();
			m_pxButton2 = 0;
		}
	}

	this->OnResize();
}


//---------------------------------------------------------------------------------------------------------------------
/// Verändert den Stil des Scrollbars
void 
CScrollBar::SetStyle(int p_iStyle)
{
	m_iStyle = p_iStyle;
	SetButtons(!(m_iStyle & SB_NoButtons));

	if(m_pxButton1  &&  m_pxButton2) 
	{
		if(m_iStyle & SB_Horizontal) 
		{
			m_pxButton1->SetButtonType(CVisualization::BT_LeftArrow);
			m_pxButton2->SetButtonType(CVisualization::BT_RightArrow);
		}
		else
		{
			m_pxButton1->SetButtonType(CVisualization::BT_UpArrow);
			m_pxButton2->SetButtonType(CVisualization::BT_DownArrow);
		}
	}

	InvalidateWindow();
}


//---------------------------------------------------------------------------------------------------------------------
//	setzt maximale Scrollbar-Position; muss >=0 sein
void 
CScrollBar::SetScrollRange(int p_iRange)
{
	if(m_iScrollRange == p_iRange)
	{
		return;
	}

	m_iScrollRange = max(p_iRange, 0);
	m_iScrollPos   = min(m_iScrollPos, GetScrollLimit());
	CalcKnobRect();
	InvalidateWindow();
}



//---------------------------------------------------------------------------------------------------------------------
int
CScrollBar::SetScrollPos(int p_iScrollPos)
{
	p_iScrollPos = clamp(p_iScrollPos, 0, GetScrollLimit());
	if(p_iScrollPos == m_iScrollPos)
	{
		return m_iScrollPos;
	}

	m_iScrollPos = p_iScrollPos;
	CalcKnobRect();
	InvalidateWindow();

	OnChange();

	return m_iScrollPos;
}



//---------------------------------------------------------------------------------------------------------------------
int	
CScrollBar::SetPageSize(int p_iPageSize)
{
	p_iPageSize = clamp(p_iPageSize, 1, m_iScrollRange+1);
	m_iPageSize = p_iPageSize;
	SetScrollPos(GetScrollPos()); // make sure scrollpos is in range
	CalcKnobRect();
	InvalidateWindow();

	return p_iPageSize;
}




//---------------------------------------------------------------------------------------------------------------------
int	
CScrollBar::SetScrollSpeed(int p_iSpeed)
{
	m_iScrollSpeed = max(p_iSpeed, 1);
	return m_iScrollSpeed;
}



//---------------------------------------------------------------------------------------------------------------------
/// berechnet Größe und Position des Griffes
void 
CScrollBar::CalcKnobRect()
{
	if(m_iStyle & SB_Horizontal)
	{
		int iDrawingArea = GetSize().cx - (m_xRealButtonSize.cx * 2);
		int iKnobSize = max((m_iPageSize  * iDrawingArea) / (m_iScrollRange+1), m_xRealButtonSize.cx);

		if(iKnobSize <= iDrawingArea)
		{
			int iKnobPos;
			if(GetScrollLimit() == 0)
			{
				iKnobPos = 0;
			}
			else
			{
				iKnobPos = (iDrawingArea - iKnobSize) * m_iScrollPos / GetScrollLimit();
			}
			m_xKnobRct = CRct(m_xRealButtonSize.cx + iKnobPos, 0, m_xRealButtonSize.cx + iKnobPos + iKnobSize, m_xButtonSize.cy);
		}
		else
		{
			m_xKnobRct = CRct(0, 0, 0, 0);
		}
	}
	else
	{
		int iDrawingArea = GetSize().cy - (m_xRealButtonSize.cy * 2);
		int iKnobSize = max((m_iPageSize  * iDrawingArea) / (m_iScrollRange+1), m_xRealButtonSize.cy);

		if(iKnobSize <= iDrawingArea)
		{
			int iKnobPos;
			if(GetScrollLimit() == 0)
			{
				iKnobPos = 0;
			}
			else
			{
				iKnobPos = (iDrawingArea - iKnobSize) * m_iScrollPos / GetScrollLimit();
			}
			m_xKnobRct = CRct(0, m_xRealButtonSize.cy + iKnobPos, m_xButtonSize.cx, m_xRealButtonSize.cy + iKnobPos + iKnobSize);
		}
		else
		{
			m_xKnobRct = CRct(0, 0, 0, 0);
		}
	}
}



//---------------------------------------------------------------------------------------------------------------------
bool 
CScrollBar::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgButtonDown)
	{
		if(p_rxMessage.GetWindow() == m_pxButton1->GetWHDL())
		{
			SetScrollPos(GetScrollPos() - m_iScrollSpeed);
		}
		if(p_rxMessage.GetWindow() == m_pxButton2->GetWHDL())
		{
			SetScrollPos(GetScrollPos() + m_iScrollSpeed);
		}
		return true;
	}
	else if(p_rxMessage == msgMouseLeftButtonDown)
	{
		return this->OnLButtonDown(p_rxMessage.GetPos());
	}
	else if(p_rxMessage == msgMouseLeftButtonUp)
	{
		return this->OnLButtonUp(p_rxMessage.GetPos());
	}
	else if(p_rxMessage == msgMouseLeftButtonDoubleClick)
	{
		return true;
	}
	else if(p_rxMessage == msgMouseRightButtonDoubleClick)
	{
		return true;
	}
	else if(p_rxMessage == msgMouseMove)
	{
		return this->OnMouseMove(p_rxMessage.GetPos());
	}
	else
	{
		return __super::HandleMsg(p_rxMessage);
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CScrollBar::OnLButtonDown(const CPnt& p_rxMousePos)
{
	if(GetDisabled())
	{
		return true;
	}

	CPnt pntMouse = p_rxMousePos;
	CWindowMgr::Get().ToClientPos(GetWHDL(), pntMouse);
	if(m_xKnobRct.Hit(pntMouse))
	{
		m_bDragging = true;
		CWindowMgr::Get().SetCapture(this);
		m_xDragPoint = CPnt(pntMouse.x - m_xKnobRct.left, pntMouse.y - m_xKnobRct.top);
	}
	else
	{
		if(pntMouse.y >= m_xKnobRct.bottom  ||  pntMouse.x >= m_xKnobRct.right)
		{
			SetScrollPos(GetScrollPos()+m_iPageSize);
		}
		else
		{
			SetScrollPos(GetScrollPos()-m_iPageSize);
		}
	}
	return true;
}



//---------------------------------------------------------------------------------------------------------------------
bool 
CScrollBar::OnLButtonUp(const CPnt& p_rxMousePos)
{
	if(m_bDragging)
	{
		m_bDragging = false;
		CWindowMgr::Get().ReleaseCapture(this);

		if(m_bSoftDrag)
		{
			CalcKnobRect();
			InvalidateWindow();  
		}
	}
	return true;
}



//---------------------------------------------------------------------------------------------------------------------
bool 
CScrollBar::OnMouseMove(const CPnt& p_rxMousePos)
{
	if(!m_bDragging)
	{
		return true;
	}

	CPnt pntMouse = p_rxMousePos;
	CWindowMgr::Get().ToClientPos(GetWHDL(), pntMouse);

	if(m_iStyle & SB_Horizontal)
	{
		// horizontaler Balken

		float f1 = (float) (pntMouse.x - m_xRealButtonSize.cx - m_xDragPoint.x) * GetScrollLimit();
		float f2 = (float) (GetSize().cx - 2*m_xRealButtonSize.cx - m_xKnobRct.Width());
		float fScrollPos = f1  / f2; 

		SetScrollPos( (int) floor(fScrollPos + 0.5) );

		if(m_bSoftDrag)
		{
			int iWidth = m_xKnobRct.Width();
			m_xKnobRct.left  = min(pntMouse.x - m_xDragPoint.x, GetSize().cx - m_xRealButtonSize.cx - iWidth);
			m_xKnobRct.left  = max(m_xRealButtonSize.cx, m_xKnobRct.left);
			m_xKnobRct.right = m_xKnobRct.left + iWidth;
			InvalidateWindow();
		}
	}
	else
	{
		// verticaler Balken

		float f1 = (float) (pntMouse.y - m_xRealButtonSize.cy - m_xDragPoint.y) * GetScrollLimit();
		float f2 = (float) (GetSize().cy - 2*m_xRealButtonSize.cy - m_xKnobRct.Height());
		float fScrollPos = f1  / f2; 

		SetScrollPos( (int) floor(fScrollPos + 0.5) );
 
		if(m_bSoftDrag)
		{
			int iHeight = m_xKnobRct.Height();
			m_xKnobRct.top =  min(pntMouse.y - m_xDragPoint.y, GetSize().cy - m_xRealButtonSize.cy - iHeight);
			m_xKnobRct.top =  max(m_xRealButtonSize.cy, m_xKnobRct.top);
			m_xKnobRct.bottom = m_xKnobRct.top + iHeight;
			InvalidateWindow();
		}
	}

	return true;
}



//---------------------------------------------------------------------------------------------------------------------
void 
CScrollBar::Paint(const CPaintContext& p_rxCtx)
{	
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());
	v->DrawBackground(p_rxCtx, GetRect(), CVisualization::BG_Scrollbar);

	if(!GetDisabled())
	{
		v->DrawScrollBarKnob(p_rxCtx, m_xKnobRct, false);
	}
}



//---------------------------------------------------------------------------------------------------------------------
bool 
CScrollBar::OnResize()
{
	if(m_pxButton1)
	{
		m_pxButton1->SetPos(CPnt(0,0));
	}
	if(m_pxButton2)
	{
		m_pxButton2->SetPos(CPnt(GetSize().cx - m_pxButton2->GetSize().cx, GetSize().cy - m_pxButton2->GetSize().cy));
	}
	CalcKnobRect();

	return this->__super::OnResize();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CScrollBar::OnVisualizationChange()
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		CVisualization* v	= CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());
		m_xButtonSize		= v->GetMetrics()->m_xScrollBarButtonSize;
		if(m_pxButton1  &&  m_pxButton2) 
		{ 
			m_pxButton1->SetSize(m_xButtonSize.cx, m_xButtonSize.cy);
			m_pxButton2->SetSize(m_xButtonSize.cx, m_xButtonSize.cy);
			m_xRealButtonSize = m_xButtonSize;
		}

		SetSize(GetSize());  // SetSize() will make sure there is enough space for the new button size
	}

	this->OnResize();

	return this->__super::OnVisualizationChange();
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CScrollBar::OnChange()			
{ 
	CWindowMgr::Get().PostMsg(CScrollBarChangedMsg(GetWHDL()), GetParent());
	if(m_xOnChangeCallback)
	{
		m_xOnChangeCallback(this);
	}	
	return true; 
} 

//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt den Wert eines benannten Attributes. Siehe UIlib Doku für eine vollständige Liste der Attribute.
	\param	p_rsName	Name des Attributes
	\param	p_rsValue	neuer Wert
	\return	true, wenn erfolgreich
*/
bool 
CScrollBar::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if(p_rsName=="style")
	{
		int iStyle;
		if(p_rsValue.Find("vertical") >= 0)	{ iStyle |= SB_Vertical; }
		if(p_rsValue.Find("horizontal") >= 0)	{ iStyle|=SB_Horizontal; }
		if(p_rsValue.Find("nobuttons") >= 0)	{ iStyle|=SB_NoButtons; }			
		SetStyle(iStyle);
		return true;
	}
	if(p_rsName == "scrollrange")	{ SetScrollRange(p_rsValue.ToInt()); return true; }
	if(p_rsName == "scrollpos")	{ SetScrollPos(p_rsValue.ToInt()); return true; }
	if(p_rsName == "pagesize")		{ SetPageSize(p_rsValue.ToInt()); return true; }
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
CScrollBar::GetAttrib(const CStr& p_rsName, CStr& po_srValue) const
{
	if(p_rsName == "style")
	{
		po_srValue.Clear();
		if(m_iStyle & SB_Vertical)		{ po_srValue =  "vertical"; }
		if(m_iStyle & SB_Horizontal)	{ po_srValue += "|horizontal"; }
		if(m_iStyle & SB_NoButtons)		{ po_srValue += "|nobuttons"; }
		return true;
	}
 	if(p_rsName == "scrollrange")	{ po_srValue.Format("%d", GetScrollRange()); return true; }
	if(p_rsName == "scrollpos")	{ po_srValue.Format("%d", GetScrollPos()); return true; }
	if(p_rsName == "pagesize")		{ po_srValue.Format("%d", GetPageSize()); return true; }
	return __super::GetAttrib(p_rsName,po_srValue);
}

//---------------------------------------------------------------------------------------------------------------------
CStr 
CScrollBar::GetDebugString() const		
{ 
	return "CScrollBar"; 
}
//---------------------------------------------------------------------------------------------------------------------

} //namespace UILib


#include "stdafx.h"
#include <math.h>
#include "uilib/controls/slider.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CSlider::CSlider()
{
	m_bSoftDrag		= true;
	m_bDragging		= false;
	m_iSliderPos	= 0;
	m_iSliderRange	= 0;
	m_xKnobSize		= CSize(0, 0);
	m_iSliderWidth  = 0;
	m_bBackground   = true;

	SetStyle(SL_Horizontal);
	SetSize(CSize(100, 100));
}


//---------------------------------------------------------------------------------------------------------------------
CSlider::~CSlider()
{
}


//---------------------------------------------------------------------------------------------------------------------
CSlider* CSlider::Create()
{
	return new CSlider();
}


//---------------------------------------------------------------------------------------------------------------------
// löscht das Fenster sofort
void 
CSlider::DeleteNow()
{
	delete this;
}



//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt die Fenstergröße. Wichtig: Höhe (bzw. Breite bei vertikalen Slidern) wird 
	von der Visualisierung festgelegt und kann nicht verändert werden.
*/
void 
CSlider::SetSize(const CSize& p_rxSize)
{
	CSize xSize = p_rxSize;

	// restrict size if visualization metrics are already known 
	if(m_iSliderWidth != 0  &&  m_xKnobSize.cx > 0  &&  m_xKnobSize.cy > 0)
	{
		if(m_iStyle & SL_Vertical)
		{
			xSize.cx = min(xSize.cx, max(m_iSliderWidth, m_xKnobSize.cx));
		}
		else
		{
			xSize.cy = min(xSize.cy, max(m_iSliderWidth, m_xKnobSize.cy));
		}
	}

	__super::SetSize(xSize);
}



//---------------------------------------------------------------------------------------------------------------------
/**
    Setzt die Fenstergröße. Diese Funktion ist ein ShortCut auf die Funktion
    SetSize(const CSize& p_rxSize) (wie in CWindow) und wird hier wiederholt,
    da die Funktion bei der Überladung verloren geht.
*/

void 
CSlider::SetSize(int p_iWidth, int p_iHeight)
{
	this->SetSize(CSize(p_iWidth, p_iHeight)); 
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSlider::SetStyle(int p_iStyle)
{
	m_iStyle = p_iStyle;
	InvalidateWindow();
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSlider::SetSliderRange(int p_iRange)
{
	if(m_iSliderRange == p_iRange)
	{
		return;
	}

	m_iSliderRange = max(p_iRange, 0);
	m_iSliderPos   = min(m_iSliderPos, m_iSliderRange);
	CalcKnobRect();
	InvalidateWindow();
}


//---------------------------------------------------------------------------------------------------------------------
int 
CSlider::SetSliderPos(int p_iSliderPos)
{
	p_iSliderPos = min(m_iSliderRange, p_iSliderPos);
	p_iSliderPos = max(p_iSliderPos, 0);

	if(p_iSliderPos == m_iSliderPos)
	{
		return m_iSliderPos;
	}

	m_iSliderPos = p_iSliderPos;
	CalcKnobRect();
	InvalidateWindow();

	this->OnChange();

	return m_iSliderPos;
}


//---------------------------------------------------------------------------------------------------------------------
/// schaltet das Zeichnen des Hintergrundes an oder aus; default ist an
void
CSlider::SetBackground(bool p_bBackground)	
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
CSlider::CalcKnobRect()
{
	if(m_iStyle & SL_Vertical)
	{
		if(m_xKnobSize.cy <= GetSize().cy)
		{
			int iKnobPos;
			if(m_iSliderRange == 0)
			{
				iKnobPos = 0;
			}
			else
			{
				iKnobPos = (GetSize().cy - m_xKnobSize.cy) * m_iSliderPos / (m_iSliderRange);
			}
			m_xKnobRct = CRct(0, iKnobPos, m_xKnobSize.cx, iKnobPos + m_xKnobSize.cy);
		}
		else
		{
			m_xKnobRct = CRct(0, 0, 0, 0);
		}
	}
	else
	{
		if(m_xKnobSize.cx <= GetSize().cx)
		{
			int iKnobPos;
			if(m_iSliderRange == 0)
			{
				iKnobPos = 0;
			}
			else
			{
				iKnobPos = (GetSize().cx - m_xKnobSize.cx) * m_iSliderPos / (m_iSliderRange);
			}
			m_xKnobRct = CRct(iKnobPos, 0, iKnobPos + m_xKnobSize.cx, m_xKnobSize.cy);
		}
		else
		{
			m_xKnobRct = CRct(0, 0, 0, 0);
		}
	}
}



//---------------------------------------------------------------------------------------------------------------------
bool 
CSlider::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgMouseLeftButtonDown)
	{
		return this->OnLButtonDown(p_rxMessage.GetPos());
	}
	else if(p_rxMessage == msgMouseLeftButtonUp)
	{
		return this->OnLButtonUp(p_rxMessage.GetPos());
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
CSlider::OnLButtonDown(const CPnt& p_rxMousePos)
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
			SetSliderPos(GetSliderPos()+1);
		}
		else
		{
			SetSliderPos(GetSliderPos()-1);
		}
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CSlider::OnLButtonUp(const CPnt& p_rxMousePos)
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
CSlider::OnMouseMove(const CPnt& p_rxMousePos)
{
	if(!m_bDragging)
	{
		return true;
	}

	CPnt pntMouse = p_rxMousePos;
	CWindowMgr::Get().ToClientPos(GetWHDL(), pntMouse);

	if(m_iStyle & SL_Vertical)
	{
		// vertikaler Slider

		float f1 = (float) (pntMouse.y - m_xDragPoint.y) * (m_iSliderRange);
		float f2 = (float) (GetSize().cy - m_xKnobRct.Height());
		float fSliderPos = f1  / f2; 

		SetSliderPos( (int) floor(fSliderPos + 0.5) );
 
		if(m_bSoftDrag)
		{
			int iHeight = m_xKnobRct.Height();
			m_xKnobRct.top =  min(pntMouse.y - m_xDragPoint.y, GetSize().cy - iHeight);
			m_xKnobRct.top =  max(0, m_xKnobRct.top);
			m_xKnobRct.bottom = m_xKnobRct.top + iHeight;
			InvalidateWindow();
		}
	}
	else
	{
		// horizontaler Slider

		float f1 = (float) (pntMouse.x - m_xDragPoint.x) * (m_iSliderRange);
		float f2 = (float) (GetSize().cx - m_xKnobRct.Width());
		float fSliderPos = f1  / f2; 

		SetSliderPos( (int) floor(fSliderPos + 0.5) );

		if(m_bSoftDrag)
		{
			int iWidth = m_xKnobRct.Width();
			m_xKnobRct.left  = min(pntMouse.x - m_xDragPoint.x, GetSize().cx - iWidth);
			m_xKnobRct.left  = max(0, m_xKnobRct.left);
			m_xKnobRct.right = m_xKnobRct.left + iWidth;
			InvalidateWindow();
		}
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSlider::Paint(const CPaintContext& p_rxCtx)
{	
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());
	if(m_bBackground)
	{
		v->DrawBackground(p_rxCtx, GetRect());
	}

	if(m_iStyle & SL_Vertical)
	{
		v->DrawSlider(p_rxCtx, GetRect(), CVisualization::AL_Vertical);
		v->DrawSliderKnob(p_rxCtx, CPnt(m_xKnobRct.left, m_xKnobRct.top), CVisualization::AL_Vertical, false, GetDisabled());
	}
	else
	{
		v->DrawSlider(p_rxCtx, GetRect(), CVisualization::AL_Horizontal);
		v->DrawSliderKnob(p_rxCtx, CPnt(m_xKnobRct.left, m_xKnobRct.top), CVisualization::AL_Horizontal, false, GetDisabled());
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CSlider::OnResize()
{
	CalcKnobRect();
	return this->__super::OnResize();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CSlider::OnVisualizationChange()
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		CVisualization* v = CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());

		if(m_iStyle & SL_Vertical)
		{
			m_xKnobSize    = v->GetMetrics()->m_xVSliderKnobSize;
		}
		else
		{
			m_xKnobSize    = v->GetMetrics()->m_xHSliderKnobSize;
		}
		m_iSliderWidth = v->GetMetrics()->m_iSliderWidth; 

		SetSize(GetSize());  // SetSize() passt die Größe an neue Metrik an
	}

	this->OnResize();

	return this->__super::OnVisualizationChange();
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CSlider::OnChange()			
{ 
	CWindowMgr::Get().PostMsg(CSliderChangedMsg(GetWHDL()), GetParent());
	if(m_xOnChangeCallback)
	{
		m_xOnChangeCallback(this);
	}	
	return true; 
} 

//---------------------------------------------------------------------------------------------------------------------
CStr	
CSlider::GetDebugString() const				
{ 
	return "CSlider"; 
}
//---------------------------------------------------------------------------------------------------------------------


} // namespace UILib


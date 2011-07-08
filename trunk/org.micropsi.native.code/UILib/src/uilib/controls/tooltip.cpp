#include "stdafx.h"
#include "uilib/controls/tooltip.h" 
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CToolTip::CToolTip(WHDL p_hOwnerWindow)
{
	m_hOwnerWindow = p_hOwnerWindow;

	SetText("Tooltip");
	CWindowMgr::Get().SetCapture(this);
	SetCanReceiveFocus(false);
	SetBackground(false);			// verhindert, dass Elternklasse CLabel grauen Hintergrund zeichnet :)
	SetTransparent(false);			// transparent ist dieses Fenster trotzdem nicht, wir malen selbst einen Hintergrund

	m_bDeleted = false;
}

//---------------------------------------------------------------------------------------------------------------------
CToolTip::~CToolTip()
{
}

//---------------------------------------------------------------------------------------------------------------------
CToolTip* 
CToolTip::Create(WHDL p_hOwnerWindow)
{
	return new CToolTip(p_hOwnerWindow);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CToolTip::AutoSize(bool p_bMayShrink)
{
	if(GetBitmap() == 0)
	{
		const COutputDevice* pxDev = CWindowMgr::Get().GetDeviceConst(GetWHDL());
		if(!pxDev)
		{
			return;
		}

		CVisualization* v	= CVisualizationFactory::Get().GetVisualization(pxDev, GetVisualizationType());
		CSize xOldSize = GetSize();
		int iSpaceWidth = v->GetTextWidth(CVisualization::FONT_ToolTip, " ");

		__super::AutoSize(true);
		CSize xNewSize = GetSize() + CSize(2*iSpaceWidth, 0);
        
		if(p_bMayShrink == false)
		{
			SetSize(max(xOldSize.cx, xNewSize.cx), max(xOldSize.cy, xNewSize.cy));
		}
		else
		{
			SetSize(xNewSize);
		}
	}
	else
	{
		__super::AutoSize(p_bMayShrink);
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CToolTip::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage.IsMouseMessage())
	{
		CPnt xPos = p_rxMessage.GetPos();
		SetVisible(false);
		CWindow* pxWndHit = CWindowMgr::Get().HitTest(xPos);
		SetVisible(true);
		
		if(p_rxMessage == msgMouseMove)
		{
			UpdatePosition(xPos);
		}

		if(	p_rxMessage != msgMouseMove  ||  
			(pxWndHit->GetWHDL() != m_hOwnerWindow  && !pxWndHit->IsChildOf(m_hOwnerWindow)))
		{
			if(!m_bDeleted)
			{
				CWindowMgr::Get().ReleaseCapture(this);
				Destroy();
				m_bDeleted = true;
			}
			CWindowMgr::Get().BringWindowToTop(m_hOwnerWindow, true);
			CWindowMgr::Get().SendMsg(p_rxMessage, pxWndHit->GetWHDL());
		}

		return true;
	}
	else
	{
		return this->__super::HandleMsg(p_rxMessage);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CToolTip::Paint(const CPaintContext& p_rxContext)
{	
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxContext.GetDevice(), GetVisualizationType());

	v->DrawBackground(p_rxContext, GetRect(), CVisualization::BG_ToolTip);
	__super::Paint(p_rxContext);
	v->DrawFrame(p_rxContext, GetRect(), CVisualization::FT_ToolTip, GetDisabled());
}

//---------------------------------------------------------------------------------------------------------------------
void
CToolTip::UpdatePosition(CPnt p_xMousePos)
{
	if(GetParent())
	{
		CWindow* pxParent = CWindowMgr::Get().GetWindow(GetParent());
		CSize xParentSize = pxParent->GetSize();

		CPnt xNewPos;

		// try upper right
		xNewPos = p_xMousePos + CPnt(2, 2 - GetSize().cy);
		if(!IsOnScreen(xParentSize, xNewPos))
		{
			// try upper left
			xNewPos = p_xMousePos + CPnt(-2 - GetSize().cx,  -2 - GetSize().cy);
			if(!IsOnScreen(xParentSize, xNewPos))
			{
				// try lower left
				xNewPos = p_xMousePos + CPnt(-2 - GetSize().cx,  2);
				if(!IsOnScreen(xParentSize, xNewPos))
				{
					// lower rigt
					xNewPos = p_xMousePos + CPnt(2, 2);
				}
			}
		}

		SetPos(xNewPos);
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CToolTip::OnDeviceChange()
{
	UpdatePosition(CWindowMgr::Get().GetMousePos());
	return __super::OnDeviceChange();
}

//---------------------------------------------------------------------------------------------------------------------
CStr	
CToolTip::GetDebugString() const
{ 
	return "CToolTip";
}
//---------------------------------------------------------------------------------------------------------------------

} //namespace UILib


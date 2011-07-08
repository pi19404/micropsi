#include "stdafx.h"
#include "uilib/controls/button.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"

namespace UILib
{


//---------------------------------------------------------------------------------------------------------------------
CButton::CButton()
{
	m_bFrame = true;
	m_bBackground = true;
	m_bHovered = false;
	m_pxLabel = CLabel::Create();
	AddChild(m_pxLabel->GetWHDL());
	m_pxLabel->SetText("Button");
	m_pxLabel->SetBackground(false);
	m_pxLabel->SetTransparent(true);

	CWindowMgr::Get().SetMouseEnterAndLeaveMsg(GetWHDL(), true);
	SetIndirectActivationMessages(true);
}


//---------------------------------------------------------------------------------------------------------------------
CButton::~CButton()
{
}


//---------------------------------------------------------------------------------------------------------------------
CButton* 
CButton::Create()
{
	return new CButton;
}



//---------------------------------------------------------------------------------------------------------------------
void 
CButton::AutoSize(bool p_bMayShrink)
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		m_pxLabel->AutoSize();

		CVisualization* v = CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());
		CRct xFrameSize = v->GetFrameSize(CVisualization::FT_BtnUp);
		CSize xLabelSize = m_pxLabel->GetSize();
		CSize xSize;
		if(m_bFrame)
		{
			xSize = CSize(xLabelSize.cx + xFrameSize.top + xFrameSize.bottom, xLabelSize.cy + xFrameSize.left + xFrameSize.right);
		}
		else
		{
			xSize = CSize(xLabelSize.cx, xLabelSize.cy);
		}

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
/// setzt den Text dieses Buttons
void 
CButton::SetText(CStr p_sText)
{
	m_pxLabel->SetText(p_sText);
	m_xNormalBmp.Clear();
	m_xHoveredBmp.Clear();
	m_xDownBmp.Clear();
	m_xDisabledBmp.Clear();
	this->OnResize();
}


//---------------------------------------------------------------------------------------------------------------------
/// schaltet den Hintergrund ein/aus
void 
CButton::SetBackground(bool p_bBackground)	
{ 
	if(m_bBackground!=p_bBackground) 
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
/// setzt die (normale) Bitmap
void 
CButton::SetBitmap(const CStr& p_rsBitmap)
{
	SetBitmap(p_rsBitmap, "", "", "");
}


//---------------------------------------------------------------------------------------------------------------------
/// setzt die (normale) Bitmap
void 
CButton::SetBitmap(const CBitmap* p_pxBitmap)
{
	SetBitmap(p_pxBitmap, 0, 0, 0);
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	bestimmt die zu verwendenden Bitmaps

	\param p_pxNormalBmp	normale bitmap, sollte nicht ""  sein (bitmap == "" zeigt Text an)
	\param p_pxDownBmp		Bitmap, wenn Button runtergedrückt ist; darf "" sein (dann wird normale Bitmap benutzt)
	\param p_pxHoveredBmp	Bitmap, wenn Maus über Button ist; darf "" sein (dann wird normale Bitmap benutzt)
	\param p_rsDisabledBmp	Bitmap, wenn Button disabled ist; darf "" sein (dann wird normale Bitmap benutzt)
*/
void 
CButton::SetBitmap(const CStr& p_rsNormalBmp, const CStr& p_rsDownBmp, const CStr& p_rsHoveredBmp, const CStr& p_rsDisabledBmp)
{
	m_xNormalBmp	= p_rsNormalBmp;
	m_xDownBmp		= p_rsDownBmp;
	m_xHoveredBmp	= p_rsHoveredBmp;
	m_xDisabledBmp	= p_rsDisabledBmp;

	UpdateBitmap();
	this->OnVisualizationChange();
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	bestimmt die zu verwendenden Bitmaps

	\param p_pxNormalBmp	normale bitmap, sollte nicht 0  sein (bitmap == 0 zeigt Text an)
	\param p_pxDownBmp		Bitmap, wenn Button runtergedrückt ist; darf 0 sein (dann wird normale Bitmap benutzt)
	\param p_pxHoveredBmp	Bitmap, wenn Maus über Button ist; darf 0 sein (dann wird normale Bitmap benutzt)
	\param p_rsDisabledBmp	Bitmap, wenn Button disabled ist; darf 0 sein (dann wird normale Bitmap benutzt)
*/
void 
CButton::SetBitmap(const CBitmap* p_pxNormalBmp, const CBitmap* p_pxDownBmp, const CBitmap* p_pxHoveredBmp, const CBitmap* p_pxDisabledBmp)
{
	m_xNormalBmp	= p_pxNormalBmp;
	m_xDownBmp		= p_pxDownBmp;
	m_xHoveredBmp	= p_pxHoveredBmp;
	m_xDisabledBmp	= p_pxDisabledBmp;

	UpdateBitmap();
	this->OnVisualizationChange();
}


//---------------------------------------------------------------------------------------------------------------------
void 
CButton::SetDisabled(bool p_bDisabled)
{
	__super::SetDisabled(p_bDisabled);
	UpdateBitmap();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CButton::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgMouseEnter)
	{
		m_bHovered = true;
		UpdateBitmap();
		return true;
	}
	else if(p_rxMessage == msgMouseLeave)
	{
		m_bHovered = false;
		UpdateBitmap();
		return true;
	}
	else
	{
		return __super::HandleMsg(p_rxMessage);
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CButton::Paint(const CPaintContext& p_rxCtx)
{	
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());

	if(m_bBackground)
	{
		v->DrawBackground(p_rxCtx, GetRect(), CVisualization::BG_Button);
	}

	if(m_bFrame)
	{
		CVisualization::FrameType eFt = CVisualization::FT_BtnUp;
		if(!GetDisabled())
		{
			if(GetButtonDown()) 
			{ 
				eFt = CVisualization::FT_BtnDown; 
			}
			else if(HasFocusOrChildHasFocus()  &&  !GetDisabled()) 
			{ 
				eFt = CVisualization::FT_BtnUpActive; 
			}
		}

		v->DrawFrame(p_rxCtx, GetRect(), eFt, GetDisabled());
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CButton::OnResize()
{
	if(m_bFrame)
	{
		m_pxLabel->SetSize(GetSize().cx - m_xFrameSize.left - m_xFrameSize.right, GetSize().cy - m_xFrameSize.top - m_xFrameSize.bottom);
	}
	else
	{
		m_pxLabel->SetSize(GetSize());
	}

	m_xLabelPos = CPnt((GetSize().cx - m_pxLabel->GetSize().cx) / 2, (GetSize().cy - m_pxLabel->GetSize().cy) / 2 );
	UpdateBitmap();

	return this->__super::OnResize();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CButton::OnVisualizationChange()
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		CVisualization* v	= CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());
		m_xFrameSize		= v->GetFrameSize(CVisualization::FT_BtnUp);
		if(m_xNormalBmp.IsEmpty())
		{
			CSize xSize = v->GetMetrics()->m_xButtonMinSize;
			ConstraintMinSize(xSize);
			xSize = v->GetMetrics()->m_xButtonMaxSize;
			ConstraintMaxSize(xSize);

			m_xTextDisplacement = v->GetMetrics()->m_xButtonDownTextDisplacement;
		}
		else
		{
			ConstraintMinSize(CSize(0, 0));
			ConstraintMaxSize(CSize(-1, -1));
		}
	}

	this->OnResize();

	return this->__super::OnVisualizationChange();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CButton::OnDeactivate()
{ 
	InvalidateWindow(); 
	return __super::OnDeactivate(); 
}



//---------------------------------------------------------------------------------------------------------------------
void 
CButton::SetButtonDown(bool p_bButtonDown)
{
	__super::SetButtonDown(p_bButtonDown);
	UpdateBitmap();
}


//---------------------------------------------------------------------------------------------------------------------
/// aktualisiert die Bitmap abhängig vom aktuellen Status (disabled? gedrückt? ...)
void 
CButton::UpdateBitmap()
{
	if(m_xNormalBmp.IsNotEmpty())
	{
		const CBitmap* pxBmp;
		if(GetDisabled()  &&  m_xDisabledBmp.IsNotEmpty())
		{
			pxBmp = m_xDisabledBmp;
		}
		else if(GetButtonDown()  &&  m_xDownBmp.IsNotEmpty())
		{
			pxBmp = m_xDownBmp;
		}
		else if(m_bHovered  &&  m_xHoveredBmp.IsNotEmpty())
		{
			pxBmp = m_xHoveredBmp;
		}
		else
		{
			pxBmp = m_xNormalBmp;
		}

		m_pxLabel->SetBitmap(pxBmp);
		m_pxLabel->SetPos(m_xLabelPos);
	}
	else
	{
		m_pxLabel->SetBitmap(0);

		if(GetButtonDown())
		{
			m_pxLabel->SetPos(m_xLabelPos + m_xTextDisplacement);
		}
		else
		{
			m_pxLabel->SetPos(m_xLabelPos);
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CButton::OnActivateIndirect()
{ 
	InvalidateWindow();
	return true; 
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CButton::OnDeactivateIndirect()
{ 
	InvalidateWindow();
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
CButton::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if(p_rsName == "caption")		{ SetText(p_rsValue); return true; }
	if(p_rsName == "frame")		{ SetFrame(p_rsValue.ToInt() != 0); return true; }
	if(p_rsName == "bitmap")		{ SetBitmap(p_rsValue); return true; }
	if(p_rsName == "background")	{ SetBackground(p_rsValue.ToInt() != 0); return true; }
	if(p_rsName == "writealpha")	{ SetWriteAlpha(p_rsValue.ToInt() != 0); return true; }
	if(p_rsName == "bitmapdown")
	{
		m_xDownBmp = p_rsValue; 
		UpdateBitmap();
		this->OnVisualizationChange();
		return true;
	}
	if(p_rsName == "bitmaphovered")
	{
		m_xHoveredBmp = p_rsValue; 
		UpdateBitmap();
		this->OnVisualizationChange();
		return true;
	}
	if(p_rsName == "bitmapdisabled")
	{
		m_xDisabledBmp = p_rsValue; 
		UpdateBitmap();
		this->OnVisualizationChange();
		return true;
	}

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
CButton::GetAttrib(const CStr& p_rsName, CStr& po_srValue) const
{
	if(p_rsName=="caption"){po_srValue=GetText();return true;}
	if(p_rsName=="frame"){po_srValue=(GetFrame()?"1":"0");return true;}
	return __super::GetAttrib(p_rsName,po_srValue);
}


//---------------------------------------------------------------------------------------------------------------------
CStr 
CButton::GetDebugString() const		
{ 
	return CStr("CButton Label = ") + GetText(); 
}


} //namespace UILIb


#include "stdafx.h"
#include "uilib/controls/label.h" 
#include "uilib/core/windowmanager.h"
#include "uilib/core/visualizationfactory.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
/// Default Konstruktor
CLabel::CLabel()
{
	m_bBackground		= true;
	m_eHTextAlignment	= TA_HCenter;
	m_eVTextAlignment	= TA_VCenter;
	m_iSpecial			= SP_Normal;
	m_bCustomTextColor	= false;
	m_eBackgroundType	= CVisualization::BG_Normal;

	SetText("Label");
	SetSize(1, 1);
}


//---------------------------------------------------------------------------------------------------------------------
/// Destruktor
CLabel::~CLabel()
{
}


//---------------------------------------------------------------------------------------------------------------------
/// erzeugt neues Label
CLabel* 
CLabel::Create()
{
	return new CLabel();
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt das Fenster auf die optimale Größe bzgl. Textgröße / Bitmapgröße
	\param bMayShrink	wenn false wird das Fenster nur bei Bedarf vergrößert, aber nicht verkleinert
*/
void 
CLabel::AutoSize(bool p_bMayShrink)
{
	CSize xSize;
	if(m_xBitmap.IsEmpty())
	{
		const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
		if(!pxDevice)
		{
			return;
		}

		CVisualization* v = CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());
		int iHeight = v->GetFontMetrics(CVisualization::FONT_Normal)->m_iHeight;

		CFontHandle hFont = m_xCustomFont.IsValid() ? m_xCustomFont : v->GetFont(CVisualization::FONT_Normal);
		if(m_xLineBreaks.GetNumLines() > 1)
		{
			int iMaxWidth = 0;
			iHeight *= m_xLineBreaks.GetNumLines();

			for(int i=0; i<m_xLineBreaks.GetNumLines(); ++i)
			{
				iMaxWidth = max(v->GetTextWidth(hFont, m_xLineBreaks.GetLine(i, m_sText)), iMaxWidth);
			}

			xSize = CSize(iMaxWidth, iHeight);
		}
		else
		{
			xSize = CSize(v->GetTextWidth(hFont, m_sText), iHeight);
		}
	}
	else
	{
		xSize = CSize(m_xBitmap.GetBitmap()->GetSize().cx, m_xBitmap.GetBitmap()->GetSize().cy);
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


//---------------------------------------------------------------------------------------------------------------------
///	setzt den darzustellenden Text - dies entfernt die Bitmap!
void 
CLabel::SetText(CStr p_sText)
{ 
	if(m_sText != p_sText)
	{
		m_sText = p_sText; 
		m_xBitmap.Clear();

		m_xLineBreaks.Update(p_sText);

		this->AutoSize(false);
		InvalidateWindow();
	}
}


//---------------------------------------------------------------------------------------------------------------------
///	setzt Bitmap - dies entfernt den Text!
void 
CLabel::SetBitmap(const CStr& p_sBitmap)
{
	if(m_xBitmap != p_sBitmap)
	{
		m_sText.Clear();
		m_xBitmap = p_sBitmap;
		m_sText = "";
		this->AutoSize(false);
		InvalidateWindow();
	}
}


//---------------------------------------------------------------------------------------------------------------------
///	setzt Bitmap - dies entfernt den Text!
void 
CLabel::SetBitmap(const CBitmap* p_pxBitmap)
{
	if(m_xBitmap != p_pxBitmap)
	{
		m_sText.Clear();
		m_xBitmap = p_pxBitmap;
		m_sText = "";
		this->AutoSize(false);
		InvalidateWindow();
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CLabel::SetBackground(bool p_bBackground)
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
CLabel::SetTextAlign(HorizontalTextAlignment p_eHTextAlignment, VerticalTextAlignment p_eVTextAlignment)
{
	if(p_eHTextAlignment != m_eHTextAlignment  ||  p_eVTextAlignment != m_eVTextAlignment)
	{
		m_eHTextAlignment = p_eHTextAlignment;
		m_eVTextAlignment = p_eVTextAlignment;
		InvalidateWindow();
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CLabel::SetSpecialProperty(int p_iSpecial)
{
	if(m_iSpecial != p_iSpecial)
	{
		m_iSpecial = p_iSpecial;
		InvalidateWindow();
	}
}


//---------------------------------------------------------------------------------------------------------------------
void
CLabel::SetFont(CFontHandle h)
{ 
	m_xCustomFont = h; 
	InvalidateWindow(); 
}


//---------------------------------------------------------------------------------------------------------------------
void 
CLabel::Paint(const CPaintContext& p_rxCtx)
{	
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());

	// wenn dies ein Bitmap-Label ist, zeichen wir den Hintergrund nur, wenn er nicht ohnehin von der Bitmap überschrieben wird
	if(m_bBackground  &&  
	   (!GetWriteAlpha()  ||  m_xBitmap.IsEmpty()  ||  m_xBitmap.GetBitmap()->GetSize().cx < GetSize().cx  ||
		m_xBitmap.GetBitmap()->GetSize().cy < GetSize().cy))
	{
		v->DrawBackground(p_rxCtx, GetRect(), m_eBackgroundType);
	}

	if(m_xBitmap.IsEmpty())
	{
		// wenn keine Bitmap gesetzt ist, zeichnen wir Text...

		CFontHandle hFont = m_xCustomFont.IsValid() ? m_xCustomFont : v->GetFont(CVisualization::FONT_Normal);
		int iLineHeight = p_rxCtx.GetDevice()->GetFontMetrics(hFont).m_iHeight;

		CVisualization::TextProperty eTP = CVisualization::TP_Normal;
		if(GetDisabled())						{ eTP = CVisualization::TP_Disabled; }
		else if(m_iSpecial == SP_Selected)		{ eTP = CVisualization::TP_Selected; }

		COutputDevice::VerticalTextAlignment eVAlign = COutputDevice::TA_VCenter;
		if(m_eVTextAlignment & TA_Top)			{ eVAlign = COutputDevice::TA_Top; }
		else if(m_eVTextAlignment & TA_Bottom)	{ eVAlign = COutputDevice::TA_Bottom;  }

		COutputDevice::HorizontalTextAlignment eHAlign = COutputDevice::TA_HCenter;
		if(m_eHTextAlignment & TA_Left)			{ eHAlign = COutputDevice::TA_Left; }
		else if(m_eHTextAlignment & TA_Right)	{ eHAlign = COutputDevice::TA_Right;  }

		if(m_xLineBreaks.GetNumLines() > 1)
		{
			// mehrere Zeilen

			CRct r = GetRect();
			if(eVAlign == TA_VCenter)
			{
				r.top = (GetSize().cy - iLineHeight * m_xLineBreaks.GetNumLines()) / 2;
			}
			else if(eVAlign == TA_Top)
			{
				r.top = 0;
			}
			else
			{
				r.top = GetSize().cy - iLineHeight * m_xLineBreaks.GetNumLines();
			}
			r.bottom = r.top + iLineHeight;
	
			int i;
			for(i=0; i<m_xLineBreaks.GetNumLines(); ++i)
			{
				v->DrawText(p_rxCtx, hFont, r, eHAlign, COutputDevice::TA_VCenter, m_xLineBreaks.GetLine(i, m_sText), eTP);
				r.top		+= iLineHeight;
				r.bottom	+= iLineHeight;
			}
		}
		else
		{
			// eine Zeile

			v->DrawText(p_rxCtx, hFont, GetRect(), eHAlign, eVAlign, m_sText, eTP);
		}
	}
	else
	{
		// ... sonst eine Bitmap

		p_rxCtx.Blit(CPnt((GetRect().Width() - m_xBitmap.GetBitmap()->GetSize().cx) / 2 , (GetRect().Height() - m_xBitmap.GetBitmap()->GetSize().cy) / 2), m_xBitmap, !GetWriteAlpha());
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CLabel::OnVisualizationChange()
{
	this->AutoSize(false);
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
CLabel::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if(p_rsName=="text")			{ SetText(p_rsValue); return true; }
	if(p_rsName=="background")		{ SetBackground(p_rsValue.ToInt() != 0); return true; }
	if(p_rsName=="bitmap")			{ SetBitmap(p_rsValue); return true; }
	if(p_rsName=="halignment")  
	{
		if(p_rsValue == "left")			{ SetTextAlign(TA_Left, GetVerticalTextAlignment()); return true;}
		else if(p_rsValue == "center")	{ SetTextAlign(TA_HCenter, GetVerticalTextAlignment()); return true;}
		else if(p_rsValue == "right")	{ SetTextAlign(TA_Right), GetVerticalTextAlignment(); return true;}
	}
	if(p_rsName=="valignment")  
	{
		if(p_rsValue == "top")			{ SetTextAlign(GetHorizontalTextAlignment(), TA_Top); return true;}
		else if(p_rsValue == "center")	{ SetTextAlign(GetHorizontalTextAlignment(), TA_VCenter); return true;}
		else if(p_rsValue == "bottom")	{ SetTextAlign(GetHorizontalTextAlignment(), TA_Bottom); return true;}
	}
	if(p_rsName=="textcolor")
	{
		int r, g, b, a;
		r = g = b = a = 0;
		sscanf(p_rsValue.c_str(), "%d,%d,%d,%d",r,g,b,a);
		SetTextColor(CColor(r,g,b,a));
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
CLabel::GetAttrib(const CStr& p_rsName, CStr& po_srValue) const
{
	if(p_rsName == "text")			{ po_srValue = GetText(); return true;}
	if(p_rsName == "background")	{ po_srValue = (GetBackground() ? "1" : "0"); return true; }
	if(p_rsName == "textcolor")	{ po_srValue.Format("%d,%d,%d,%d", m_xTextColor.GetRed(), 
										m_xTextColor.GetGreen(), m_xTextColor.GetBlue(), m_xTextColor.GetAlpha()); }
	if(p_rsName=="halignment")
	{
		if(GetHorizontalTextAlignment() == TA_Left)			{ po_srValue = "left"; return true; }
		else if(GetHorizontalTextAlignment() == TA_HCenter)	{ po_srValue = "center"; return true; }
		else if(GetHorizontalTextAlignment() == TA_Right)	{ po_srValue = "right"; return true;}
	}
	if(p_rsName=="valignment")
	{
		if(GetVerticalTextAlignment() == TA_Top)			{ po_srValue = "top"; return true; }
		else if(GetVerticalTextAlignment() == TA_VCenter)	{ po_srValue = "center"; return true; }
		else if(GetVerticalTextAlignment() == TA_Bottom)	{ po_srValue = "bottom"; return true;}
	}
	return __super::GetAttrib(p_rsName, po_srValue);
}


//---------------------------------------------------------------------------------------------------------------------
/// löscht das Fenster sofort
void 
CLabel::DeleteNow()
{
	delete this;
}


//---------------------------------------------------------------------------------------------------------------------
CStr	
CLabel::GetDebugString() const		
{ 
	return CStr("CLabel Label = ") + GetText(); 
}

//---------------------------------------------------------------------------------------------------------------------

} //namespace UILib


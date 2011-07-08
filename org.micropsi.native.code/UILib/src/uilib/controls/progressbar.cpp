#include "stdafx.h"
#include "uilib/controls/progressbar.h"
#include "uilib/core/windowmanager.h"
#include "math.h"

namespace UILib
{

	
//---------------------------------------------------------------------------------------------------------------------
/// Default Konstruktor
CProgressBar::CProgressBar()
{
	m_bVertical = false;
	m_fRange	= 100.0f;
	m_fProgress = 0.0f;
	m_xColor	= CColor(0, 0, 255, 255);
	m_xBKColor	= CColor(255, 255, 255, 255);
	SetWriteAlpha(true);
}


//---------------------------------------------------------------------------------------------------------------------
/// Default Destruktor
CProgressBar::~CProgressBar()
{
}


//---------------------------------------------------------------------------------------------------------------------
/// erstellt einen neuen Progressbar
CProgressBar* CProgressBar::Create()						
{ 
	return new CProgressBar(); 
}


//---------------------------------------------------------------------------------------------------------------------
/// ändert den Maximalwert des Balkens
float CProgressBar::SetRange(float p_fRange)
{
	if(m_fRange != p_fRange)
	{
		m_fRange = max(0.0f, p_fRange);
		InvalidateWindow();
	}
	return m_fRange;
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	set progress; must be >= 0 and <= Range
	\return		new progress value
*/
float CProgressBar::SetProgress(float p_fProgress)
{
	p_fProgress = clamp(p_fProgress, 0.0f, m_fRange);
	if(m_fProgress != p_fProgress)
	{
		m_fProgress = p_fProgress;
		InvalidateWindow();
	}
	return m_fProgress;
}


//---------------------------------------------------------------------------------------------------------------------
/// bestimmt die Vordergrundfarbe
void CProgressBar::SetBarColor(CColor p_xColor)			
{ 
	if(m_xColor != p_xColor)
	{
		m_xColor = p_xColor; 
		InvalidateWindow(); 
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// Bestimmt die Hintergrundfarbe
void  CProgressBar::SetBackgroundColor(CColor p_xColor)		
{ 
	if(m_xBKColor != p_xColor)
	{
		m_xBKColor = p_xColor; 
		InvalidateWindow(); 
	}
}



//---------------------------------------------------------------------------------------------------------------------
/// Paint
void CProgressBar::Paint(const CPaintContext& p_rxCtx)
{	
	CColor xBarColor = m_xColor;
	if(GetDisabled())
	{
		xBarColor = CColor(128, 128, 128, 255);
	}

	// Hintergrundbitmap gekachelt zeichnen (wenn vorhanden)
	if(m_xBKBitmap.IsNotEmpty())
	{
		for(int x=0; x<(int)(ceil((float) GetSize().cx / (float) m_xBKBitmap.GetBitmap()->GetWidth())); ++x)
		{
			for(int y=0; y<(int)(ceil((float) GetSize().cy / (float) m_xBKBitmap.GetBitmap()->GetHeight())); ++y)
			{
				CPnt p = CPnt(x * m_xBKBitmap.GetBitmap()->GetWidth(), y * m_xBKBitmap.GetBitmap()->GetHeight());
				p_rxCtx.Blit(p, m_xBKBitmap, !GetWriteAlpha());
			}
		}
	}


	// Rechteck des Bars berechnen; evtl. Hintergrund zeichen, falls keine Bitmap gesetzt 
	CRct xBarRect;
	if(m_bVertical)
	{
		int iSize = (int) (((float) GetSize().cy) / m_fRange * m_fProgress); 
		xBarRect = CRct(0, GetSize().cy - iSize, GetSize().cx, GetSize().cy);
		if(m_xBKBitmap.IsEmpty())
		{
			p_rxCtx.FillRect(CRct(0, 0, GetSize().cx, GetSize().cy - iSize), m_xBKColor);
		}
	}
	else
	{
		int iSize = (int) (((float) GetSize().cx) / m_fRange * m_fProgress); 
		xBarRect = CRct(0, 0, iSize, GetSize().cy);
		if(m_xBKBitmap.IsEmpty())
		{
			p_rxCtx.FillRect(CRct(iSize, 0, GetSize().cx, GetSize().cy), m_xBKColor);
		}
	}

	// Balken zeichnen
	if(m_xBarBitmap.IsNotEmpty())
	{
		for(int x=0; x<(int)(ceil((float) xBarRect.Width() / (float) m_xBarBitmap.GetBitmap()->GetWidth())); ++x)
		{
			for(int y=0; y<(int)(ceil((float) xBarRect.Height() / (float) m_xBarBitmap.GetBitmap()->GetHeight())); ++y)
			{
				CPnt p = CPnt(x * m_xBarBitmap.GetBitmap()->GetWidth(), y * m_xBarBitmap.GetBitmap()->GetHeight());
				p_rxCtx.Blit(p, m_xBarBitmap, xBarRect, !GetWriteAlpha());
			}
		}
	}
	else
	{
		p_rxCtx.FillRect(xBarRect, xBarColor);
	}


}


//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt den Wert eines benannten Attributes. Siehe UIlib Doku für eine vollständige Liste der Attribute.
	\param	p_rsName	Name des Attributes
	\param	p_rsValue	neuer Wert
	\return	true, wenn erfolgreich
*/
bool CProgressBar::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if(p_rsName=="vertical") { SetVertical(p_rsValue.ToInt()!=0); return true; }
	return __super::SetAttrib(p_rsName,p_rsValue);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	liefert den Wert eines benannten Attributes. Siehe UILib Doku für eine vollständige Liste der Attribute. ues

	\param	p_rsName		Name des Attributes
	\param	po_srValue		Ausgabe-Parameter: String, der den Wert entgegennimmt
	\return true, wenn Wert zurückgeliefert wurde; false, wenn der Attributnamen nicht bekannt ist
*/
bool CProgressBar::GetAttrib(const CStr& p_rsName, CStr& po_srValue) const
{
	if(p_rsName=="vertical"){po_srValue=(GetVertical()?"1":"0");return true;}
	return __super::GetAttrib(p_rsName,po_srValue);
}

	
} //namespace UILib


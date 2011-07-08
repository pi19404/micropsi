#include "stdafx.h"
#include "uilib/core/paintcontext.h"
#include "uilib/core/outputdevice.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CPaintContext::CPaintContext(COutputDevice* p_pxDevice)
{
	m_pxDevice = p_pxDevice;
}


//---------------------------------------------------------------------------------------------------------------------
CPaintContext::~CPaintContext()
{
}


//---------------------------------------------------------------------------------------------------------------------
/// setzt das Offset; alle Zeichenoperationen sind relativ zu diesem Offset
void 
CPaintContext::SetOffset(CPnt p_pntOffset)
{
	m_pntOffset = p_pntOffset;
}


//---------------------------------------------------------------------------------------------------------------------
/// setzt das Clipping-Rechteck
void 
CPaintContext::SetClip(CRct p_xClipRect)
{
	m_xClipRect = p_xClipRect;
}


//---------------------------------------------------------------------------------------------------------------------
/// verschiebt das Offset; alle Zeichenoperationen sind relativ zum Offset
void 
CPaintContext::AddOffset(CPnt p_pntOffset)
{
	m_pntOffset += p_pntOffset;
}


//---------------------------------------------------------------------------------------------------------------------
/// verschiebt das Offset; alle Zeichenoperationen sind relativ zu diesem Offset
void 
CPaintContext::AddOffset(int p_iOffset)
{
	m_pntOffset += p_iOffset;
}




//---------------------------------------------------------------------------------------------------------------------
/// liefert die Farbe eines einzelnen Pixels
CColor 
CPaintContext::GetPixel(const CPnt& p_xPnt) const
{
	return m_pxDevice->GetPixel(p_xPnt + m_pntOffset);
}



//---------------------------------------------------------------------------------------------------------------------
/// zeichnet einen einzelnen Pixel
void 
CPaintContext::SetPixel(const CPnt& p_xPnt, const CColor& p_xColor) const
{
	CPnt p = p_xPnt + m_pntOffset;
	if(m_xClipRect.Hit(p))
	{
		m_pxDevice->SetPixel(p, p_xColor);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// zeichnet eine Linie
void 
CPaintContext::DrawLine(const CPnt& p_rxPnt1, const CPnt& p_rxPnt2, CColor p_xColor) const
{
	CPnt pntA = p_rxPnt1+m_pntOffset;
	CPnt pntB = p_rxPnt2+m_pntOffset;
	if(m_xClipRect.ClipLine(pntA, pntB))
	{
		m_pxDevice->DrawLine(pntA, pntB, p_xColor);
	}	
}


//---------------------------------------------------------------------------------------------------------------------
/// zeicnet den nicht ausgefüllten Umriss eines Rechtecks
void 
CPaintContext::DrawRect(const CRct& p_rxRect, CColor p_xColor ) const
{
	CRct r = p_rxRect + m_pntOffset;
	CRct xDrawRect = m_xClipRect.Clip(r);
	if(xDrawRect.IsEmpty())	{ return; }

	if(xDrawRect.top == r.top)
	{
		m_pxDevice->DrawLine(xDrawRect.left, xDrawRect.top, xDrawRect.right-1, xDrawRect.top, p_xColor);
	}
	if(xDrawRect.bottom == r.bottom)
	{
		m_pxDevice->DrawLine(xDrawRect.left, xDrawRect.bottom-1, xDrawRect.right-1, xDrawRect.bottom-1, p_xColor);
	}
	if(xDrawRect.left == r.left)
	{
		m_pxDevice->DrawLine(xDrawRect.left, xDrawRect.top, xDrawRect.left, xDrawRect.bottom-1, p_xColor);
	}
	if(xDrawRect.right == r.right)
	{
		m_pxDevice->DrawLine(xDrawRect.right-1, xDrawRect.top, xDrawRect.right-1, xDrawRect.bottom-1, p_xColor);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// zeichnet ein ausgefülltest Rechteck
void 
CPaintContext::FillRect(const CRct& p_rxRect, CColor p_xColor) const
{
	CRct xDrawRect=m_xClipRect.Clip(p_rxRect+m_pntOffset);
	if(xDrawRect.IsEmpty())  { return; }

	m_pxDevice->FillRect(xDrawRect, p_xColor);

	// debug code, um  Screen Updates sichtbar zu machen
//	DebugPrint("Fillrect %d %d %d %d ", xDrawRect.left, xDrawRect.top, xDrawRect.right, xDrawRect.bottom);
//	m_pxDevice->FillRect(xDrawRect, CColor(rand() % 255, rand() % 255, rand() % 255, max(p_xColor.m_cAlpha, 100)));
}


//---------------------------------------------------------------------------------------------------------------------
void 
CPaintContext::DrawText(CFontHandle p_hFont, CPnt p_xPos, CStr p_sText, CColor p_xColor) const
{
	p_xPos += m_pntOffset;
	m_pxDevice->DrawText(p_hFont, p_xPos.x, p_xPos.y, p_sText, p_xColor, m_xClipRect);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CPaintContext::DrawText(CFontHandle p_xFontHandle, const CRct& p_rxRct, COutputDevice::HorizontalTextAlignment p_eHTextAlign, 
						COutputDevice::VerticalTextAlignment p_eVTextAlign, CStr p_sText, CColor p_xColor) const
{
	CRct r = p_rxRct + m_pntOffset;
	m_pxDevice->DrawTextRect(p_xFontHandle, r, p_eHTextAlign, p_eVTextAlign, p_sText, p_xColor, m_xClipRect);
}



//---------------------------------------------------------------------------------------------------------------------
/// zeichnet eine Bitmap
void 
CPaintContext::Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, bool p_bAlpha) const
{
	m_pxDevice->Blit(p_xPnt+m_pntOffset, p_pxBitmap, m_xClipRect, p_bAlpha);
}


//---------------------------------------------------------------------------------------------------------------------
/// zeichnet eine Bitmap
void 
CPaintContext::Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, const CRct& p_xClipRect, bool p_bAlpha) const
{
	CRct xClipRect = (p_xClipRect + m_pntOffset).Clip(m_xClipRect);
	m_pxDevice->Blit(p_xPnt+m_pntOffset, p_pxBitmap, xClipRect, p_bAlpha);
}


//---------------------------------------------------------------------------------------------------------------------
/// liefert das Clipping-Rechteck relativ zum Offset
CRct 
CPaintContext::GetRelClipRect() const
{
	return m_xClipRect - m_pntOffset;

}


} // namespace UILib


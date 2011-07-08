#include "stdafx.h"
#include <math.h>
#include <limits.h>
#include "uilib/core/outputdevice.h"
#include "baselib/debugprint.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
///	invalidate a rectangle
void 
COutputDevice::Invalidate(const CRct& p_xRect)
{
	if(!p_xRect.IsEmpty())
	{
		/*
		BeginPaint();
		this->FillRect(p_xRect,CColor(255,0,0,128));
		CRctList x = m_xInvalidRegions;
		EndPaint();
		m_xInvalidRegions = x;
		*/
		
		m_xInvalidRegions.Add(p_xRect);
	}
}


//---------------------------------------------------------------------------------------------------------------------
///	invalidate a list of rectangles - default implementation
void 
COutputDevice::Invalidate(const CRctList& p_xRectList)
{
	unsigned int i;
	CRct xRct;

	p_xRectList.StartIterate(i);
	while(p_xRectList.Iterate(i, xRct))
	{
		this->Invalidate(xRct);
	}
}



//---------------------------------------------------------------------------------------------------------------------
/// validate the whole context
void 
COutputDevice::Validate()
{
	m_xInvalidRegions.Clear();
}


//---------------------------------------------------------------------------------------------------------------------
///	validate a rectangle
void 
COutputDevice::Validate(const CRct& p_xRect)
{
	m_xInvalidRegions.Sub(p_xRect);
}


//---------------------------------------------------------------------------------------------------------------------
///	validate a list of rectangles - default implementation
void 
COutputDevice::Validate(const CRctList& p_xRectList)
{
	unsigned int i;
	CRct xRct;

	p_xRectList.StartIterate(i);
	while(p_xRectList.Iterate(i, xRct))
	{
		this->Validate(xRct);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// liefert die ungültigen (~neuzuzeichnenden) Regionen dieses Devices 
const CRctList& 
COutputDevice::GetInvalidRegions()
{
	return m_xInvalidRegions;
}


//---------------------------------------------------------------------------------------------------------------------
///	löscht das gesamte Device
void
COutputDevice::Clear(CColor p_xColor)
{
	this->FillRect(0, 0, INT_MAX, INT_MAX, p_xColor);
}


//---------------------------------------------------------------------------------------------------------------------
/// zeichnet eine Linie
void 
COutputDevice::DrawLine(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor)
{
	if((p_iX1 == p_iX2) &&  (p_iY1 == p_iY2))
	{
		this->SetPixel(p_iX1, p_iY1, p_xColor);
		return;
	}
	if(p_iX1 == p_iX2)
	{
        if(p_iY1 > p_iY2) {int swap=p_iY1; p_iY1=p_iY2; p_iY2=swap;}
		for(;p_iY1<=p_iY2;p_iY1++){this->SetPixel(p_iX1, p_iY1, p_xColor);}
		return;
	}
	if(p_iY1 == p_iY2)
	{
		if(p_iX1 > p_iX2){int swap=p_iX1; p_iX1=p_iX2; p_iX2=swap;}
		for(;p_iX1<=p_iX2;p_iX1++){this->SetPixel(p_iX1, p_iY1, p_xColor);}
		return;
	}

	float fX1 = (float) p_iX1;
	float fX2 = (float) p_iX2;
	float fY1 = (float) p_iY1;
	float fY2 = (float) p_iY2;

	float fDX,fDY;
	fDX=(float)(fX2-fX1);
	fDY=(float)(fY2-fY1);
	float fSteps;
	if(fabs(fDX)>fabs(fDY))
	{
		fSteps=(float)fabs(fDX);
	}
	else
	{
		fSteps=(float)fabs(fDY);
	}
	fDX/=fSteps;fDY/=fSteps;
	float fX=(float)fX1;
	float fY=(float)fY1;
	while(fSteps>=0.0f)
	{
		this->SetPixel((int) fX, (int) fY, p_xColor);
		fX+=fDX;
		fY+=fDY;
		fSteps--;
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	draws a rectangle (outline only) - default implementation
	Note that the second point is the first point that will NOT be in the rectangle, so (0, 0, 10, 10) will
	create a rectangle with a height and width of 9 and the first point being (0,0).
	This is compatible with the Microsoft Windows function.
*/
void 
COutputDevice::DrawRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor)
{
	p_iX2--;
	p_iY2--;
	this->DrawLine(p_iX1, p_iY1, p_iX2, p_iY1, p_xColor);
	this->DrawLine(p_iX2, p_iY1, p_iX2, p_iY2, p_xColor);
	this->DrawLine(p_iX2, p_iY2, p_iX1, p_iY2, p_xColor);
	this->DrawLine(p_iX1, p_iY2, p_iX1, p_iY1, p_xColor);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	draws a rectangle (outline only) - default implementation
	Note that the second point is the first point that will NOT be in the rectangle, so (0, 0, 10, 10) will
	create a rectangle with a height and width of 9 and the first point being (0,0).
	This is compatible with the Microsoft Windows function.
*/
void 
COutputDevice::DrawRect(const CPnt& p_xPnt1, const CPnt& p_xPnt2, const CColor& p_xColor)
{
	this->DrawRect(p_xPnt1.x, p_xPnt1.y, p_xPnt2.x, p_xPnt2.y, p_xColor);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	draws a rectangle (outline only) - default implementation
*/
void 
COutputDevice::DrawRect(const CRct& p_xRect, const CColor& p_xColor )
{
	this->DrawRect(p_xRect.left, p_xRect.top, p_xRect.right, p_xRect.bottom, p_xColor);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	draws a filled rectangle - default implementation
	Filling DOES NOT include the bottom and the right colum,
	i.e. FillRect(0, 10, 0, 10) will create a filled rectangle between (0,0) and (9,9).
	This is compatible with the behavior of the Windows FillRect() function.
*/
void 
COutputDevice::FillRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor)
{
	if(p_iX2 < p_iX1) {int swap = p_iX1; p_iX1 = p_iX2; p_iX2 = swap; }
	if(p_iY2 < p_iY1) {int swap = p_iY1; p_iY1 = p_iY2; p_iY2 = swap; }

	int x, y;
	for(x=p_iX1; x<p_iX2; ++x)
	{
		for(y=p_iY1; y<p_iY2; ++y)
		{
			this->SetPixel(x, y, p_xColor);
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	draws a filled rectangle - default implementation
	Filling DOES NOT include the bottom and the right colum,
	i.e. FillRect(0, 10, 0, 10) will create a filled rectangle between (0,0) and (9,9).
	This is compatible with the behavior of the Windows FillRect() function.
*/
void 
COutputDevice::FillRect(const CPnt& p_xPnt1, const CPnt& p_xPnt2, const CColor& p_xColor)
{
	this->FillRect(p_xPnt1.x, p_xPnt1.y, p_xPnt2.x, p_xPnt2.y, p_xColor);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	draws a filled rectangle - default implementation
	Filling DOES NOT include the bottom and the right colum,
	i.e. FillRect(0, 10, 0, 10) will create a filled rectangle between (0,0) and (9,9).
	This is compatible with the behavior of the Windows FillRect() function.
*/
void 
COutputDevice::FillRect(const CRct& p_xRect, const CColor& p_xColor)
{
	this->FillRect(p_xRect.left, p_xRect.top, p_xRect.right, p_xRect.bottom, p_xColor);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	draw bitmap 

	\param p_xPnt		position of upper left corner of bitmap in device coordinates; can be outside of device (negative, for instance)
	\param p_pxBitmap	pointer to bitmap to draw
	\param p_bAlpha		transparent blitting using alpha; not all devices support this
*/
void 
COutputDevice::Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, bool b_Alpha)
{
	if(p_pxBitmap->GetSize().cx==0 || p_pxBitmap->GetSize().cy==0){return;}
	const unsigned long*	pxBMData = p_pxBitmap->GetRawData();

	int x, y, iOffset = 0;
	for(y=p_xPnt.y; y<p_xPnt.y+p_pxBitmap->GetSize().cy; ++y)
	{
		for(x=p_xPnt.x; x<p_xPnt.x+p_pxBitmap->GetSize().cx; ++x)
		{
			this->SetPixel(x, y, CColor(pxBMData[iOffset++]));
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Füllt die übergebene Bitmap mit Bilddaten aus diesem Context.
	Die Bitmap muss vom User angelegt werden und ausreichend groß sein.
	Angegeben wird die linke obere Ecke des zu kopierenden Bereiches im Device
	und wahlweise die Größe des zu kopierenden Bereiches. Wird die Größe nicht
	angegeben, wird die Größe der Bitmap verwendet.
*/
bool			
COutputDevice::CaptureBitmap(const CPnt& p_xPnt, CBitmap& po_rxBitmap, const CSize& p_xSize)
{
	CSize xSize = p_xSize;
	if(xSize.cx < 0)  { xSize.cx = po_rxBitmap.GetSize().cx; }
	if(xSize.cy < 0)  { xSize.cy = po_rxBitmap.GetSize().cy; }
	xSize.cx = min(xSize.cx, GetSize().cx - p_xPnt.x);
	xSize.cy = min(xSize.cy, GetSize().cy - p_xPnt.y);
	
	unsigned long* piBmp = po_rxBitmap.GetRawDataForWriting();

	for(int y=p_xPnt.y; y<p_xPnt.y+xSize.cy; ++y)
	{
		for(int x=p_xPnt.x; x<p_xPnt.x+xSize.cx; ++x)
		{
			*piBmp = GetPixel(x, y).m_dwColor;
			piBmp++;
		}
	}

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	draw bitmap 

	\param p_xPnt		position of upper left corner of bitmap in device coordinates; can be outside of device (negative, for instance)
	\param p_pxBitmap	pointer to bitmap to draw
	\param p_xClipRect	clipping rectangle in device coordinates, blit() will not draw parts of the bitmap that are outside this rectangle	
	\param p_bAlpha		transparent blitting using alpha; not all devices support this
*/
void 
COutputDevice::Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, const CRct& p_xClipRect, bool b_Alpha)
{
	if(p_pxBitmap->GetSize().cx==0 || p_pxBitmap->GetSize().cy==0){return;}
	if(!p_xClipRect.Intersects(CRct(p_xPnt.x, p_xPnt.y, p_xPnt.x + p_pxBitmap->GetSize().cx, p_xPnt.y + p_pxBitmap->GetSize().cy))){return;}

	int x, y, iReadPitch, iReadOffset;
	int iXStart, iXEnd, iYStart, iYEnd;

	iReadPitch = 0;
	iReadOffset = 0;
	iXStart = p_xPnt.x;
	iYStart = p_xPnt.y;
	iXEnd = iXStart+p_pxBitmap->GetSize().cx;
	iYEnd = iYStart+p_pxBitmap->GetSize().cy;

	// clip left
	int iGap = p_xClipRect.left - p_xPnt.x;
	if(iGap > 0)
	{
		iReadOffset += iGap;
		iXStart += iGap;
		iReadPitch += iGap;
	}

	// clip right
	iGap = p_xPnt.x + p_pxBitmap->GetSize().cx - p_xClipRect.right;		
	if(iGap > 0)
	{
		iXEnd -= iGap;
		iReadPitch += iGap;
	}

	// clip top
	iGap = p_xClipRect.top - p_xPnt.y;
	if(iGap > 0)
	{
		iReadOffset += iGap * p_pxBitmap->GetSize().cx;  
		iYStart += iGap;
	}

	// clip bottom
	iGap = p_xPnt.y + p_pxBitmap->GetSize().cy - p_xClipRect.bottom;		
	if(iGap > 0)
	{
		iYEnd -= iGap;
	}

	const unsigned long* pxBMData=p_pxBitmap->GetRawData();

	for(y=iYStart; y<iYEnd; ++y)
	{
		for(x=iXStart; x<iXEnd; ++x)
		{
			this->SetPixel(x, y, CColor(pxBMData[iReadOffset++]));
		}
		iReadOffset += iReadPitch;
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// erzeugt einen Font, der mit diesem Device benutzt werden kann
CFontHandle	
COutputDevice::CreateFont(int p_iHeight, const CStr& p_rsFaceName, CFont::Locale p_Locale, CFont::Pitch p_ePitch, CFont::Weight p_eWeight, int p_iFlags) const
{
	return CAlphaBitmapFont::Create(p_iHeight, p_rsFaceName, p_Locale, p_ePitch, p_eWeight, p_iFlags);
}


//---------------------------------------------------------------------------------------------------------------------
/// erzeugt einen Font, der mit diesem Device benutzt werden kann
CFontHandle			
COutputDevice::CreateFont(const CStr& p_rsBitmapFilename, const CStr& p_rsCharacterOrder, int p_iSpaceWidth, int p_iBaseLine, int p_iKerningBias)
{
	return CBitmapFont::Create(p_rsBitmapFilename, p_rsCharacterOrder, p_iSpaceWidth, p_iBaseLine, p_iKerningBias);
}


//---------------------------------------------------------------------------------------------------------------------
/// gibt einen Font, der mit diesem Device erzeugt wurde, wieder frei
void 
COutputDevice::ReleaseFont(CFontHandle p_hHandle) const
{
	if(p_hHandle.GetFontType() == CFourCC("BMPA"))
	{
		CAlphaBitmapFont::Release(p_hHandle);
	}
	else if(p_hHandle.GetFontType() == CFourCC("BMPF"))
	{
		CBitmapFont::Release(p_hHandle);
	}
	else
	{
		assert(false);		// unbekannter Fonttyp!
	}
}


//---------------------------------------------------------------------------------------------------------------------
///	zeichnet eine Zeile Text
void 
COutputDevice::DrawText(CFontHandle p_xFontHandle, int p_iX, int p_iY, CStr p_sText, CColor p_xColor)
{
	CSize xSize = GetSize();
	this->DrawText(p_xFontHandle, p_iX, p_iY, p_sText, p_xColor, CRct(0, 0, xSize.cx, xSize.cy));
}


//---------------------------------------------------------------------------------------------------------------------
///	zeichnet eine Zeile Text
void 
COutputDevice::DrawText(CFontHandle p_xFontHandle, int p_iX, int p_iY, CStr p_sText, CColor p_xColor, const CRct& p_xClipRect)
{
	if(p_xFontHandle.GetFontType() == CFourCC("BMPA"))
	{
		DrawTextAlphaBitmapFont(p_xFontHandle, p_iX, p_iY, p_sText, p_xColor, p_xClipRect);
	}
	else if(p_xFontHandle.GetFontType() == CFourCC("BMPF"))
	{
		DrawTextBitmapFont(p_xFontHandle, p_iX, p_iY, p_sText, p_xClipRect);
	}
	else
	{
		assert(false);	// unbekannter Fonttyp
	}
}



//---------------------------------------------------------------------------------------------------------------------
// zeichnet Text in das angegebene Rechteck; Ausrichtung linksbündig, rechtsbündig oder zentriert
void 
COutputDevice::DrawTextRect(CFontHandle p_xFontHandle, const CRct& p_rxRct, HorizontalTextAlignment p_eHTextAlign, 
							VerticalTextAlignment p_eVTextAlign, CStr p_sText, CColor p_xColor)
{
	CSize xSize = GetSize();
	this->DrawTextRect(p_xFontHandle, p_rxRct, p_eHTextAlign, p_eVTextAlign, p_sText, p_xColor, CRct(0, 0, xSize.cx, xSize.cy));
}


//---------------------------------------------------------------------------------------------------------------------
// zeichnet Text in das angegebene Rechteck; Ausrichtung linksbündig, rechtsbündig oder zentriert
void 
COutputDevice::DrawTextRect(CFontHandle p_xFontHandle, const CRct& p_rxRct, HorizontalTextAlignment p_eHTextAlign, 
							VerticalTextAlignment p_eVTextAlign, CStr p_sText, CColor p_xColor, const CRct& p_xClipRect)
{
	const CFont::TFontMetrics& fm = this->GetFontMetrics(p_xFontHandle);
	CPnt xPos;

	if(p_eVTextAlign == TA_Top)
	{
		xPos.y = p_rxRct.top + fm.m_iAscent;
	}
	else if(p_eVTextAlign == TA_Bottom)
	{
		xPos.y = p_rxRct.bottom - fm.m_iDescent;
	}
	else 
	{
		xPos.y = p_rxRct.top + ((p_rxRct.Height() - fm.m_iHeight) / 2) + fm.m_iAscent;
	}
    
	if(p_eHTextAlign == TA_Left)
	{
		xPos.x = p_rxRct.left;
	}
	else if(p_eHTextAlign == TA_Right)
	{
		xPos.x = p_rxRct.right - this->GetTextWidth(p_xFontHandle, p_sText);
	}
	else 
	{
		xPos.x = p_rxRct.left + ((p_rxRct.Width() - this->GetTextWidth(p_xFontHandle, p_sText)) / 2);
	}

	this->DrawText(p_xFontHandle, xPos.x, xPos.y, p_sText, p_xColor, p_xClipRect);
}


//---------------------------------------------------------------------------------------------------------------------
/// liefert Breite des angegebenen Textes in Pixeln
int 
COutputDevice::GetTextWidth(CFontHandle p_xFontHandle, CStr p_sText) const
{
	int i, iKerning;
	int iWidth = 0;

	unsigned long iChar, iOldChar;
	i = 0; iOldChar = 0;
	while(p_sText.c_str()[i] != 0)
	{
		int iBytes = 1;
		iChar = (unsigned char) p_sText.GetAt(i);

		if(i>0)
		{
			iKerning = p_xFontHandle.m_pxFont->GetKerning(iOldChar, iChar);
		}
		else
		{
			iKerning=0;
		}
		iWidth += (p_xFontHandle.m_pxFont)->GetCharacterWidth(iChar) + iKerning;

		iOldChar = iChar;
		i+= iBytes;
	}
	return iWidth;
}


//---------------------------------------------------------------------------------------------------------------------
///	zeichnet ein einzelnes Textzeichen
void 
COutputDevice::DrawCharacter(CAlphaBitmapFontCharacter* p_pChar, int p_iX, int p_iY, CColor p_xColor, const CRct& p_xClipRect)
{
	if(p_pChar->m_iWidth==0 || p_pChar->m_iHeight==0){return;}

	if(!p_xClipRect.Intersects(CRct(p_iX, p_iY, p_iX + p_pChar->m_iWidth, p_iY + p_pChar->m_iHeight)))
	{
		return;
	}


	int x, y, iReadPitch, iReadOffset;
	int iXStart, iXEnd, iYStart, iYEnd;

	iReadPitch = 0;
	iReadOffset = 0;
	iXStart = p_iX;
	iYStart = p_iY;
	iXEnd = p_iX+p_pChar->m_iWidth;
	iYEnd = p_iY+p_pChar->m_iHeight;

	// clip left
	int iGap = p_xClipRect.left - p_iX;
	if(iGap > 0)
	{
		iReadOffset += iGap;
		iXStart += iGap;
		iReadPitch += iGap;
	}

	// clip right
	iGap = p_iX + p_pChar->m_iWidth - p_xClipRect.right;		
	if(iGap > 0)
	{
		iXEnd -= iGap;
		iReadPitch += iGap;
	}

	// clip top
	iGap = p_xClipRect.top - p_iY;
	if(iGap > 0)
	{
		iReadOffset += iGap * p_pChar->m_iWidth;  
		iYStart += iGap;
	}

	// clip bottom
	iGap = p_iY + p_pChar->m_iHeight - p_xClipRect.bottom;		
	if(iGap > 0)
	{
		iYEnd -= iGap;
	}


	for(y=iYStart; y<iYEnd; ++y)
	{
		for(x=iXStart; x<iXEnd; ++x)
		{
			int iAlpha = p_pChar->m_pBuffer[iReadOffset++];
			if(iAlpha != 0)
			{
				if(iAlpha < 255)
				{
					CColor col = this->GetPixel(x, y);

					int iF2 = iAlpha;
					int iF1 = 256 - iF2;
					col.m_cRed   = (unsigned char) ((col.m_cRed   * iF1 + p_xColor.m_cRed   * iF2) >> 8);
					col.m_cGreen = (unsigned char) ((col.m_cGreen * iF1 + p_xColor.m_cGreen * iF2) >> 8);
					col.m_cBlue  = (unsigned char) ((col.m_cBlue  * iF1 + p_xColor.m_cBlue  * iF2) >> 8);
					col.m_cAlpha = p_xColor.m_cAlpha;

					this->SetPixel(x, y, col);
				}
				else
				{
					this->SetPixel(x, y, p_xColor);
				}
			}
		}
		iReadOffset += iReadPitch;
	}
}


//---------------------------------------------------------------------------------------------------------------------
void						
COutputDevice::DrawTextAlphaBitmapFont(CFontHandle p_xFontHandle, int p_iX, int p_iY, CStr p_sText, CColor p_xColor, const CRct& p_xClipRect)
{
	CSize xSize = GetSize();
	CRct xClipRect = p_xClipRect.Clip(CRct(0, 0, xSize.cx, xSize.cy));

	int i, iKerning;
	int x = p_iX;
	int iMaxOverBaseLine  = 0;
	int iMaxUnderBaseLine = 0;
	for(i=0; i<p_sText.GetLength(); ++i)
	{
		if(i>0)
		{
			iKerning = ((CAlphaBitmapFont*) (p_xFontHandle.m_pxFont))->GetKerning(p_sText[i-1], p_sText[i]);
		}
		else
		{
			iKerning=0;
		}
		CAlphaBitmapFontCharacter* pChar = ((CAlphaBitmapFont*) (p_xFontHandle.m_pxFont))->GetChar(p_sText[i]);
		x += pChar->m_iSpacingBefore + iKerning;
		DrawCharacter(pChar, x, p_iY-pChar->m_iHeightOverBaseLine, p_xColor, xClipRect);
		x += pChar->m_iWidth + pChar->m_iSpacingAfter;
	
		if(x >= xClipRect.right) { break; }

		iMaxOverBaseLine = max(iMaxOverBaseLine, pChar->m_iHeightOverBaseLine);
		iMaxUnderBaseLine = max(iMaxUnderBaseLine, pChar->m_iHeight - pChar->m_iHeightOverBaseLine);
	}
}


//---------------------------------------------------------------------------------------------------------------------
void
COutputDevice::DrawTextBitmapFont(CFontHandle p_xFontHandle, int p_iX, int p_iY, CStr p_sText, const CRct& p_xClipRect)
{
	CSize xSize = GetSize();
	CRct xClipRect = p_xClipRect.Clip(CRct(0, 0, xSize.cx, xSize.cy));

	int i, iKerning;
	int x = p_iX;
	int iAscent  = ((CBitmapFont*) (p_xFontHandle.m_pxFont))->GetMetrics().m_iAscent;
	int iDescent = ((CBitmapFont*) (p_xFontHandle.m_pxFont))->GetMetrics().m_iDescent;
	
	if((p_iX >= xClipRect.right) || (p_iY - iAscent > xClipRect.bottom) || (p_iY + iDescent < xClipRect.top))
	{
		return;
	}

	for(i=0; i<p_sText.GetLength(); ++i)
	{
		if(i>0)
		{
			iKerning = ((CBitmapFont*) (p_xFontHandle.m_pxFont))->GetKerning(p_sText[i-1], p_sText[i]);
		}
		else
		{
			iKerning=0;
		}
		CBitmap* pChar = ((CBitmapFont*) (p_xFontHandle.m_pxFont))->GetChar(p_sText[i]);
		if(pChar)
		{
			Blit(CPnt(x+iKerning, p_iY-iAscent), pChar, xClipRect, true);
			x += pChar->GetWidth() + iKerning;
		}
		else
		{
			DebugPrint("Warning: BitmapFont: unsupported char %c (%d)", p_sText[i], p_sText[i]);
		}
	
		if(x >= xClipRect.right) { break; }
	}
}

//---------------------------------------------------------------------------------------------------------------------

} // namespace UILib


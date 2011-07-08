#include "stdafx.h"
#include <math.h>
#include "uilib/core/bitmapdevice.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
/**
	constructor
	\param p_pxBitmap		Bitmap you want to draw in
*/
CBitmapDevice::CBitmapDevice(CBitmap* p_pxBitmap)
{
	m_iWidth	= p_pxBitmap->GetSize().cx;
	m_iHeight	= p_pxBitmap->GetSize().cy;
	m_rctSize	= CRct(0,0,m_iWidth,m_iHeight);
	m_iSize		= m_iHeight*m_iWidth;
	m_bPaint	= false;
	m_pdwPixels	= p_pxBitmap->GetRawDataForWriting();
	m_iPitch	= m_iWidth;
	m_xInvalidRegions.Clear();
}


//---------------------------------------------------------------------------------------------------------------------
/**
	constructor

	\param p_piPixels	Pointer auf Speicher, der die Bitmap darstellt; jeder Pixel ist 32 bit groß
	\param p_iWidth		width of buffer in pixels	
	\param p_iHeight	height of buffer in pixels
	\param p_iPitch		Pitch in Bytes (Anzahl zusätzlicher Pixel zwischen den Zeilen)
*/
CBitmapDevice::CBitmapDevice(unsigned long* p_piPixels, int p_iWidth, int p_iHeight, int p_iPitch)
{
	m_iWidth	= p_iWidth;
	m_iHeight	= p_iHeight;
	m_rctSize	= CRct(0,0,m_iWidth,m_iHeight);
	m_iSize		= m_iHeight*m_iWidth;
	m_bPaint	= false;
	m_pdwPixels	= p_piPixels;
	m_iPitch	= m_iWidth + p_iPitch;
}


//---------------------------------------------------------------------------------------------------------------------
CBitmapDevice::~CBitmapDevice()
{
}


//---------------------------------------------------------------------------------------------------------------------
/**
	You must call BeginPaint before you start painting. You must call EndPaint() when you 
	are done painting.

	\return false if failed
*/
bool 
CBitmapDevice::BeginPaint()
{
	m_bPaint=true;
	return true; 
}


//---------------------------------------------------------------------------------------------------------------------
/**
	You must call BeginPaint before you start painting. You must call EndPaint() when you 
	are done painting.
*/
void 
CBitmapDevice::EndPaint(bool p_bValidateAll)
{
	m_bPaint=false;
	if(p_bValidateAll) 
	{
		m_xInvalidRegions.Clear();
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	invalidate complete context
*/
void 
CBitmapDevice::Invalidate()
{	
	CRct rect;
	rect.top    = 0;
	rect.left   = 0;
	rect.bottom	= m_iHeight;
	rect.right  = m_iWidth;
	COutputDevice::Invalidate(rect);
}


//---------------------------------------------------------------------------------------------------------------------
CColor 
CBitmapDevice::GetPixel(int p_iX, int p_iY)
{
	assert(m_bPaint);

	if(p_iX >= m_iWidth  ||  p_iY >= m_iHeight  ||  p_iX < 0  ||  p_iY < 0)		{return CColor(0);}
	CColor xRetCol = CColor(m_pdwPixels[p_iX + p_iY*m_iPitch]);

	return xRetCol;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CBitmapDevice::SetPixel(int p_iX, int p_iY, const CColor& p_xColor)
{
	assert(m_bPaint);

	if(p_iX >= m_iWidth  ||  p_iY >= m_iHeight  ||  p_iX < 0  ||  p_iY < 0) {return;}
	m_pdwPixels[p_iY * m_iPitch + p_iX] = p_xColor.m_dwColor; 
}


//---------------------------------------------------------------------------------------------------------------------
void 
CBitmapDevice::DrawLine(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor)
{
	assert(m_bPaint);

	if((p_iX1 == p_iX2) && (p_iY1 == p_iY2))
	{
		SetPixel(p_iX1, p_iY1, p_xColor);
		return;
	}

	unsigned long iColor = p_xColor.m_dwColor;

	if(p_iX1 == p_iX2)
	{
		if(p_iX1 < 0  ||  p_iX1 >= m_iWidth) { return; }

		int iHeight=m_iHeight-1;
		p_iY1 = clamp(p_iY1,0,iHeight);
		p_iY2 = clamp(p_iY2,0,iHeight);

		if(p_iY1==p_iY2)
		{
			SetPixel(p_iX1, p_iY1, p_xColor);
			return;
		}
		if(p_iY1 > p_iY2){Swap(p_iY1,p_iY2);}

		int iOffset = p_iY1 * m_iPitch + p_iX1;
		for(;p_iY1<=p_iY2;p_iY1++){m_pdwPixels[iOffset] = iColor; iOffset+=m_iWidth;}
		return;
	}
	if(p_iY1 == p_iY2)
	{
		if(p_iY1 < 0  ||  p_iY1 >= m_iHeight) { return; }

		int iWidth=m_iWidth-1;
		p_iX1 = clamp(p_iX1,0,iWidth);
		p_iX2 = clamp(p_iX2,0,iWidth);

		if(p_iX1==p_iX2)
		{
			SetPixel(p_iX1, p_iY1, p_xColor);
			return;
		}
		if(p_iX1 > p_iX2){Swap(p_iX1,p_iX2);}

		int iOffset = p_iY1 * m_iPitch + p_iX1;
		for(;p_iX1<=p_iX2;p_iX1++){m_pdwPixels[iOffset++] = iColor; }
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
		m_pdwPixels[(int) fY * m_iPitch + (int) fX] = iColor; 
		fX+=fDX;
		fY+=fDY;
		fSteps--;
	}
}



//---------------------------------------------------------------------------------------------------------------------
void 
CBitmapDevice::DrawRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor)
{
	assert(m_bPaint);

	if(p_iX1 >= p_iX2  ||  p_iY1 >= p_iY2)		
	{ 
		return; 
	}
	COutputDevice::DrawRect(p_iX1, p_iY1, p_iX2, p_iY2, p_xColor);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CBitmapDevice::FillRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor)
{
	assert(m_bPaint);

	p_iX1 = max(0, p_iX1);
	p_iX2 = min(m_iWidth, p_iX2);
	p_iY1 = max(0, p_iY1);
	p_iY2 = min(m_iHeight, p_iY2);

	if(p_iX1 >= p_iX2  ||  p_iY1 >= p_iY2)		
	{ 
		return; 
	}


	unsigned long iColor = p_xColor.m_dwColor;
	int iOffset = p_iY1 * m_iPitch + p_iX1;
	int iRowWidth = p_iX2 - p_iX1;
	int iRestWidth = m_iPitch - iRowWidth; 
	int y;
	for(y=p_iY1; y<p_iY2; ++y)
	{
		int iEndRowOffset = iOffset + iRowWidth;
		while (iOffset < iEndRowOffset)
		{
			m_pdwPixels[iOffset++] = iColor; 
		}
		iOffset += iRestWidth;
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CBitmapDevice::DrawCharacter(CAlphaBitmapFontCharacter* p_pChar, int p_iX, int p_iY, CColor p_xColor, const CRct& p_xClipRect)
{
	assert(m_bPaint);

	if(p_pChar->m_iWidth==0 && p_pChar->m_iHeight==0){return;}

	if(!p_xClipRect.Intersects(CRct(p_iX, p_iY, p_iX + p_pChar->m_iWidth, p_iY + p_pChar->m_iHeight))){return;}

	int iReadIdx		= 0;
	int iReadPitch		= 0;
	int iEndReadIdx		= p_pChar->m_iWidth * p_pChar->m_iHeight;
	int iWriteIdx		= p_iY * m_iPitch + p_iX;
	int iWriteLineEnd	= iWriteIdx + p_pChar->m_iWidth;
	int iWritePitch		= m_iPitch - p_pChar->m_iWidth;

	// clip left

	int iGap = p_xClipRect.left - p_iX;			
	if(iGap > 0)
	{
		iReadPitch		+= iGap;
		iReadIdx		+= iGap;
		iWriteIdx		+= iGap;
		iWritePitch		+= iGap;
	}

	// clip right

	iGap = p_iX + p_pChar->m_iWidth - p_xClipRect.right;		
	if(iGap > 0)
	{
		iReadPitch     += iGap;
		iWriteLineEnd  -= iGap;
		iWritePitch    += iGap;
	}

	// clip top

	iGap = p_xClipRect.top - p_iY;		
	if(iGap > 0)
	{
		iReadIdx		+= iGap * p_pChar->m_iWidth;
		iWriteIdx		+= iGap * m_iWidth;
		iWriteLineEnd	+= iGap * m_iWidth;
	}

	// clip bottom

	iGap = p_iY + p_pChar->m_iHeight - p_xClipRect.bottom;		
	if(iGap > 0)
	{
		iEndReadIdx -= iGap * p_pChar->m_iWidth;
	}

	unsigned long iColor = p_xColor.m_dwColor;
	while (iReadIdx < iEndReadIdx)
	{
		int iAlpha = p_pChar->m_pBuffer[iReadIdx++];
		if(iAlpha != 0)
		{
			if(iAlpha < 255)
			{
				CColor col = CColor(m_pdwPixels[iWriteIdx]);

				int iF2=iAlpha;
				int iF1=256-iF2;
				col.m_cRed   = (unsigned char) ((col.m_cRed   * iF1 + p_xColor.m_cRed   * iF2) >> 8);
				col.m_cGreen = (unsigned char) ((col.m_cGreen * iF1 + p_xColor.m_cGreen * iF2) >> 8);
				col.m_cBlue  = (unsigned char) ((col.m_cBlue  * iF1 + p_xColor.m_cBlue  * iF2) >> 8);
				col.m_cAlpha = p_xColor.m_cAlpha;

				m_pdwPixels[iWriteIdx] = col.m_dwColor;
			}
			else
			{
				m_pdwPixels[iWriteIdx] = iColor;
			}
		}

		iWriteIdx++;
		if(iWriteIdx >= iWriteLineEnd)
		{
			iWriteLineEnd += m_iWidth;
			iWriteIdx += iWritePitch;
			iReadIdx  += iReadPitch;
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CBitmapDevice::Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, bool p_bAlpha)
{
	return this->Blit(p_xPnt, p_pxBitmap, m_rctSize, p_bAlpha);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CBitmapDevice::Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, const CRct& p_xClipRect, bool p_bAlpha)
{
	assert(m_bPaint);

	CRct rctBitmap = CRct(p_xPnt.x, p_xPnt.y, p_xPnt.x + p_pxBitmap->GetSize().cx, p_xPnt.y + p_pxBitmap->GetSize().cy);
	CRct xClipRect = p_xClipRect.Clip(m_rctSize);
	if(!xClipRect.Intersects(rctBitmap))
	{
		return;
	}

	int iReadIdx		= 0;
	int iReadPitch		= 0;
	int iEndReadIdx		= p_pxBitmap->GetNumPixels();
	int iWriteIdx		= p_xPnt.y * m_iPitch + p_xPnt.x;
	int iWriteLineEnd	= iWriteIdx + p_pxBitmap->GetSize().cx;
	int iWritePitch		= m_iPitch - p_pxBitmap->GetSize().cx;

	const unsigned long*	pxBMData=p_pxBitmap->GetRawData();

	// clip left

	int iGap = xClipRect.left - p_xPnt.x;			
	if(iGap > 0)
	{
		iReadPitch		+= iGap;
		iReadIdx		+= iGap;
		iWriteIdx		+= iGap;
		iWritePitch		+= iGap;
	}

	// clip right

	iGap = p_xPnt.x + p_pxBitmap->GetSize().cx - xClipRect.right;		
	if(iGap > 0)
	{
		iReadPitch     += iGap;
		iWriteLineEnd  -= iGap;
		iWritePitch    += iGap;
	}

	// clip top

	iGap = xClipRect.top - p_xPnt.y;		
	if(iGap > 0)
	{
		iReadIdx		+= iGap * p_pxBitmap->GetSize().cx;
		iWriteIdx		+= iGap * m_iWidth;
		iWriteLineEnd	+= iGap * m_iWidth;
	}

	// clip bottom

	iGap = p_xPnt.y + p_pxBitmap->GetSize().cy - xClipRect.bottom;		
	if(iGap > 0)
	{
		iEndReadIdx -= iGap * p_pxBitmap->GetSize().cx;
	}

	// blitting loop

	if(!p_bAlpha)
	{
		// version without alpha
		
		while (iReadIdx < iEndReadIdx)
		{
			m_pdwPixels[iWriteIdx] = pxBMData[iReadIdx++];

			iWriteIdx++;
			if(iWriteIdx >= iWriteLineEnd)
			{
				iWriteLineEnd += m_iWidth;
				iWriteIdx += iWritePitch;
				iReadIdx  += iReadPitch;
			}
		}
	}
	else
	{
		// version with alpha

		while (iReadIdx < iEndReadIdx)
		{
				
			unsigned long iAlpha = (pxBMData[iReadIdx] & 0xFF000000) >> 24;
			if(iAlpha != 0)
			{
				if(iAlpha < 255)
				{
					CColor col = CColor(m_pdwPixels[iWriteIdx]);
					col *= CColor(pxBMData[iReadIdx]);
					m_pdwPixels[iWriteIdx] = col.m_dwColor;
				}
				else
				{
					m_pdwPixels[iWriteIdx] = pxBMData[iReadIdx];
				}
			}
			iReadIdx++;
			iWriteIdx++;
			if(iWriteIdx >= iWriteLineEnd)
			{
				iWriteLineEnd += m_iPitch;
				iWriteIdx += iWritePitch;
				iReadIdx  += iReadPitch;
			}
		}
	}
}


} // namespace UILib


#include "stdafx.h"
#include <assert.h>
#include "uilib/core/alphabitmapfontcharacter.h"

namespace UILib 
{

//---------------------------------------------------------------------------------------------------------------------
CAlphaBitmapFontCharacter::CAlphaBitmapFontCharacter(HFONT p_hFont, unsigned long p_iCharacter)
{
	m_iCharacter = p_iCharacter;

	HDC hDispDC = CreateCompatibleDC(NULL);
	assert(hDispDC != 0);

	GLYPHMETRICS	xMetrics;
	::ZeroMemory(&xMetrics,sizeof(xMetrics));

	MAT2 xTMat;
	xTMat.eM11.fract=0;	xTMat.eM11.value=1;
	xTMat.eM12.fract=0;	xTMat.eM12.value=0;
	xTMat.eM21.fract=0;	xTMat.eM21.value=0;
	xTMat.eM22.fract=0;	xTMat.eM22.value=1;

	// herausfinden, wie groß die Bitmap sein wird
	HFONT hFntOld = (HFONT)::SelectObject(hDispDC, p_hFont);
	unsigned long iRet;
	if(p_iCharacter > 0xFF) 
	{
		iRet=::GetGlyphOutlineW(hDispDC, (UINT) p_iCharacter, GGO_GRAY8_BITMAP, &xMetrics, 0, 0, &xTMat);
	}
	else
	{
		iRet=::GetGlyphOutlineA(hDispDC, (UINT) p_iCharacter, GGO_GRAY8_BITMAP, &xMetrics, 0, 0, &xTMat);
	}
	assert(iRet >= 0);

	if(iRet > 0)
	{
		unsigned char* pTmpBuffer = new unsigned char[iRet];

		// Zeichen rendern lassen
		if(p_iCharacter > 0xFF)
		{
			iRet=::GetGlyphOutlineW(hDispDC, (UINT) p_iCharacter, GGO_GRAY8_BITMAP, &xMetrics, iRet, pTmpBuffer, &xTMat);
		}
		else
		{
			iRet=::GetGlyphOutlineA(hDispDC, (UINT) p_iCharacter, GGO_GRAY8_BITMAP, &xMetrics, iRet, pTmpBuffer, &xTMat);
		}
		assert(iRet > 0);
		::SelectObject(hDispDC, hFntOld);
		DeleteDC(hDispDC);

		int iPitch			= (xMetrics.gmBlackBoxX + 3) & (~3);
		m_iWidth			= xMetrics.gmBlackBoxX;
		m_iHeight			= xMetrics.gmBlackBoxY;
		m_iSpacingBefore	= xMetrics.gmptGlyphOrigin.x;
		m_iSpacingAfter		= xMetrics.gmCellIncX - m_iWidth - m_iSpacingBefore;

		m_iHeightOverBaseLine = xMetrics.gmptGlyphOrigin.y;

		m_pBuffer = new unsigned char[m_iWidth * m_iHeight];

		int x, y;
		for(x=0; x<m_iWidth; ++x)
		{
			for(y=0; y<m_iHeight; ++y)
			{
				int iColor = (pTmpBuffer[y*iPitch + x] * 255) / 64;
				m_pBuffer[y*m_iWidth+x] = (unsigned char) iColor;
			}
		}

		delete pTmpBuffer;
	}
	else
	{
		m_iWidth				= 0;
		m_iHeight				= 0;
		m_iHeightOverBaseLine	= 0;
		m_iSpacingBefore		= 0;
		m_iSpacingAfter			= 0;
		m_pBuffer				= 0;
	}
}


//---------------------------------------------------------------------------------------------------------------------
CAlphaBitmapFontCharacter::~CAlphaBitmapFontCharacter()
{
	delete m_pBuffer;
}


} // namespace UILib


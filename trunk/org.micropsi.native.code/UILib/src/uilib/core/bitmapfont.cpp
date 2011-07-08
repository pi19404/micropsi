/**
	Bitmap Font class. Manages bitmaps of single characters (CBitmapFontCharacter); these bitmaps are cached based
	on a least-recently-used algorithm.
*/

#include "stdafx.h"
#include "uilib/core/bitmap.h"
#include "uilib/core/bitmapfont.h"
#include "uilib/core/bitmapdevice.h"
#include "baselib/debugprint.h"

using std::hash_map;
#pragma warning (disable : 4996)

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------

CDynArray<CBitmapFont::TFontChacheEntry> CBitmapFont::m_axFontCache;	

//---------------------------------------------------------------------------------------------------------------------
/**
	\param	p_rsBitmapFilename		Name einer Bitmapdatei, die alle Zeichen enthält (getrennt von midestens einem Leerpixel)
	\param	p_rsCharacterOrder		String mit der Zeichenfolge, wie sie in der Bitmap steht, z.B. "abcdefghijkl..."
	\param	p_iSpaceWidth			Breite eines Leerzeichens in Pixeln
*/
CBitmapFont::CBitmapFont(const CStr& p_rsBitmapFilename, const CStr& p_rsCharacterOrder, int p_iSpaceWidth, int p_iBaseLine, int p_iKerningBias) 
						 : CFont(0, p_rsBitmapFilename, L_STANDARD, PITCH_VARIABLE, W_NORMAL, 0)
{
	m_sBitmapFilename = p_rsBitmapFilename;

	CBitmap xFontBmp(p_rsBitmapFilename);
	CBitmapDevice xDev(&xFontBmp);
	xDev.BeginPaint();

	// erste und letzte benutzte Zeile im Bitmap suchen
	int iFirstY = -1;
	for(int y=0; y<xFontBmp.GetSize().cy  && iFirstY < 0; ++y)
	{
		for(int x=0; x<xFontBmp.GetSize().cx; ++x)
		{
			if(xDev.GetPixel(x, y).m_cAlpha > 0)
			{
				iFirstY = y; 
//				DebugPrint("FirstRow = %d", y);
				break;
			}
		}
	}
	int iLastY = -1;
	for(int y=xFontBmp.GetSize().cy; y>=0  && iLastY < 0; --y)
	{
		for(int x=0; x<xFontBmp.GetSize().cx; ++x)
		{
			if(xDev.GetPixel(x, y).m_cAlpha > 0)
			{
				iLastY = y; 
//				DebugPrint("LastRow = %d", y);
				break;
			}
		}
	}

	m_xFontMetrics.m_iHeight = iLastY - iFirstY + 1;
	m_xFontMetrics.m_iAscent = p_iBaseLine - iFirstY;
	m_xFontMetrics.m_iDescent = m_xFontMetrics.m_iHeight - p_iBaseLine - iFirstY;

	// Zeichen suchen und ausschneiden
	int iCurrentCharacterIdx = 0;
	int iCharacterStartX = 0;
	int iCurrentX = 0;
	bool bInCharacter = false;
	while (iCurrentX < xFontBmp.GetSize().cx)
	{
		if(!bInCharacter)
		{
			// Leerraum überspringen
			for(int y=iFirstY; y<=iLastY; ++y)
			{
				if(xDev.GetPixel(iCurrentX, y).m_cAlpha > 0)
				{
					bInCharacter = true;
					iCharacterStartX = iCurrentX;
					break;
				}
			}
			if(!bInCharacter)   { iCurrentX++; }
		}
		else
		{
			// Ende des Zeichens suchen
			for(int y=iFirstY; y<=iLastY; ++y)
			{
				if(xDev.GetPixel(iCurrentX, y).m_cAlpha != 0)
				{
					break;
				}
			}
			if(y>iLastY  ||  iCurrentX == xFontBmp.GetSize().cx -1)
			{
				// Zeichen zu Ende
				if(iCurrentX == xFontBmp.GetSize().cx -1)  { iCurrentX++; }
				bInCharacter = false;
//				DebugPrint("char von %d bis %d", iCharacterStartX, iCurrentX-1);

	            hash_map<int, CBitmap*>::iterator i;
	            if(m_xCharacters.size() != 0)
	            {
		            i = m_xCharacters.find(p_rsCharacterOrder[iCurrentCharacterIdx]);
                    assert(i == m_xCharacters.end());   // doppeltes Zeichen!
                }

				CBitmap* p = new CBitmap(iCurrentX - iCharacterStartX, iLastY - iFirstY + 1, false);
				xDev.CaptureBitmap(CPnt(iCharacterStartX, iFirstY), *p);
				m_xCharacters[p_rsCharacterOrder[iCurrentCharacterIdx]] = p;

				iCurrentCharacterIdx++;
			}
			iCurrentX++;
		}
	}

	// spezielle Bitmap für Leerzeichen anlegen
	CBitmap* p = new CBitmap(p_iSpaceWidth, 1, true);
	m_xCharacters[32] = p;


	if(p_rsCharacterOrder.GetLength() != iCurrentCharacterIdx)
	{
		DebugPrint("Warning: bitmapfont: %d characters expected, %d characters found", 
			p_rsCharacterOrder.GetLength(), 
			iCurrentCharacterIdx);
	}
	
	xDev.EndPaint();

	m_iKerningBias = p_iKerningBias;
}


//---------------------------------------------------------------------------------------------------------------------
CBitmapFont::~CBitmapFont()
{
	hash_map<int, CBitmap*>::iterator i;
	if(m_xCharacters.size() != 0)
	{
		for(i=m_xCharacters.begin(); i!=m_xCharacters.end(); i++)
		{
			delete i->second;
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
CFontHandle	
CBitmapFont::Create(const CStr& p_rsBitmapFilename, const CStr& p_rsCharacterOrder, int p_iSpaceWidth, int p_iBaseLine, int p_iKerningBias)
{
	for(unsigned int i=0; i<m_axFontCache.Size(); ++i)
	{
		if(m_axFontCache[i].m_pxFont->GetBitmapFilename() == p_rsBitmapFilename)
		{
			m_axFontCache[i].m_iRefCount++;
			return CFontHandle(m_axFontCache[i].m_pxFont);
		}
	}
	
	CBitmapFont* p = new CBitmapFont(p_rsBitmapFilename, p_rsCharacterOrder, p_iSpaceWidth, p_iBaseLine, p_iKerningBias);
	int j = m_axFontCache.PushEntry();
	m_axFontCache[j].m_iRefCount = 1;
	m_axFontCache[j].m_pxFont = p;

	return CFontHandle(p);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CBitmapFont::Release(CFontHandle& p_xrHandle)
{
	assert(p_xrHandle.GetFontType() == "BMPF");

	for(unsigned int i=0; i<m_axFontCache.Size(); ++i)
	{
		if(m_axFontCache[i].m_pxFont == p_xrHandle.m_pxFont)
		{
			m_axFontCache[i].m_iRefCount--;
			if(m_axFontCache[i].m_iRefCount == 0)
			{
				delete p_xrHandle.m_pxFont;
				m_axFontCache.DeleteEntry(i);
			}
			break;
		}
	}

	p_xrHandle.m_pxFont = 0;
}


//---------------------------------------------------------------------------------------------------------------------
CBitmap*				
CBitmapFont::GetChar(int p_iChar)
{
	hash_map<int, CBitmap*>::iterator i;
	if(m_xCharacters.size() != 0)
	{
		i = m_xCharacters.find(p_iChar);
		if(i!=m_xCharacters.end())
		{
			return i->second;
		}
	}
	return 0;
}	

//---------------------------------------------------------------------------------------------------------------------
/// liefer Breite eines Zeichens in Pixeln
int		
CBitmapFont::GetCharacterWidth(int p_iChar)
{
	CBitmap* p = GetChar(p_iChar);
	return p ? p->GetWidth() : 0;
}

//---------------------------------------------------------------------------------------------------------------------

} // namespace UILib

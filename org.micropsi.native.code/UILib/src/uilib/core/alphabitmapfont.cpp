/**
	Bitmap Font class. Manages bitmaps of single characters (CAlphaBitmapFontCharacter); these bitmaps are cached based
	on a least-recently-used algorithm.
*/

#include "stdafx.h"
#include "windows.h"
#include "uilib/core/alphabitmapfont.h"

using std::map;
using std::pair;

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------

CDynArray<CAlphaBitmapFont::TFontChacheEntry> CAlphaBitmapFont::m_axFontCache;	
const unsigned long CAlphaBitmapFont::CACHESIZE		= 400;
const unsigned long CAlphaBitmapFont::COLLISIONRANGE	= 10;

//---------------------------------------------------------------------------------------------------------------------
CAlphaBitmapFont::CAlphaBitmapFont(int p_iHeight, const CStr& p_rsFaceName, CFont::Locale p_eLocale, CFont::Pitch p_ePitch, Weight p_eWeight, int p_iFlags) 
						 : CFont(p_iHeight, p_rsFaceName, p_eLocale, p_ePitch, p_eWeight, p_iFlags)
{
	m_iRequests		= 0;
	m_iCacheHits	= 0;			
	m_ePitch = p_ePitch;

	m_pCharacters = new CAlphaBitmapFontCharacter* [CACHESIZE];
	m_piLastRequestNumber = new int [CACHESIZE];
	int i;
	for (i=0; i<CACHESIZE; i++)
	{
		m_pCharacters[i]			= 0;
		m_piLastRequestNumber[i]	= 0;
	}

	// now create the font 

	LOGFONT logfont;
	memset(&logfont, 0, sizeof(logfont));
	
	logfont.lfHeight		= p_iHeight; 
//	logfont.lfWidth			= 0; 
//	logfont.lfEscapement	= 0;		// 900 = up, 1800 = from right to left, 2700 = down
//	logfont.lfOrientation	= 0; 

	switch(p_eWeight) 
	{
		case W_DONTCARE:	logfont.lfWeight = FW_DONTCARE;		break;
		case W_THIN:		logfont.lfWeight = FW_THIN;			break;
		case W_EXTRALIGHT:	logfont.lfWeight = FW_EXTRALIGHT;	break;
		case W_ULTRALIGHT:	logfont.lfWeight = FW_ULTRALIGHT;	break;
		case W_LIGHT:		logfont.lfWeight = FW_LIGHT;		break;
		case W_NORMAL:		logfont.lfWeight = FW_NORMAL;		break;
		case W_REGULAR:		logfont.lfWeight = FW_REGULAR;		break;
		case W_MEDIUM:		logfont.lfWeight = FW_MEDIUM;		break;
		case W_SEMIBOLD:	logfont.lfWeight = FW_SEMIBOLD;		break;
		case W_DEMIBOLD:	logfont.lfWeight = FW_DEMIBOLD;		break;
		case W_BOLD:		logfont.lfWeight = FW_BOLD;			break;
		case W_EXTRABOLD:	logfont.lfWeight = FW_EXTRABOLD;	break;
		case W_ULTRABOLD:	logfont.lfWeight = FW_ULTRABOLD;	break;
		case W_HEAVY:		logfont.lfWeight = FW_HEAVY;		break;
		case W_BLACK:		logfont.lfWeight = FW_BLACK;		break;
		default:			logfont.lfWeight = FW_DONTCARE;		break;
	}	

	logfont.lfItalic		= (p_iFlags & F_ITALIC)    ? TRUE : FALSE; 
	logfont.lfUnderline		= (p_iFlags & F_UNDERLINE) ? TRUE : FALSE; 
	logfont.lfStrikeOut		= (p_iFlags & F_STRIKEOUT) ? TRUE : FALSE;

	switch (p_eLocale)
	{
		case L_JAPANESE:	logfont.lfCharSet = SHIFTJIS_CHARSET;		break;
		case L_KOREAN:		logfont.lfCharSet = HANGUL_CHARSET;			break;
		case L_CHINESE:		logfont.lfCharSet = CHINESEBIG5_CHARSET;	break;
		case L_RUSSIAN:		logfont.lfCharSet = RUSSIAN_CHARSET;		break;
		case L_THAI:		logfont.lfCharSet = THAI_CHARSET;			break;
		case L_ARABIC:		logfont.lfCharSet = ARABIC_CHARSET;			break;
		case L_HEBREW:		logfont.lfCharSet = HEBREW_CHARSET;			break;
		case L_VIETNAMESE:	logfont.lfCharSet = VIETNAMESE_CHARSET;		break;
		default:			logfont.lfCharSet = 0;
	}

	logfont.lfOutPrecision	= OUT_TT_ONLY_PRECIS; 
//	logfont.lfOutPrecision  = OUT_RASTER_PRECIS;

//	logfont.lfClipPrecision	; 

//	logfont.lfQuality		= ANTIALIASED_QUALITY; 
	logfont.lfQuality		= NONANTIALIASED_QUALITY;

	if(p_ePitch == PITCH_FIXED)
	{
		logfont.lfPitchAndFamily = FIXED_PITCH; 
	}
	else
	{
		logfont.lfPitchAndFamily = VARIABLE_PITCH; 
	}

	strncpy(logfont.lfFaceName, p_rsFaceName.c_str(), LF_FACESIZE); 

	m_hFont = CreateFontIndirect(&logfont);
	assert(m_hFont != 0);


	// get required size of kerning table, then allocate it

	HDC hDC = ::CreateCompatibleDC(NULL);
	assert(hDC > 0);
	HFONT hOldFont = (HFONT) ::SelectObject(hDC, m_hFont);
	unsigned long iRet = ::GetKerningPairs(hDC, 0, NULL);
	if(iRet > 0)
	{
		// Kerning table vorhanden!

		KERNINGPAIR* pKerningTable = new KERNINGPAIR[iRet];
		iRet = ::GetKerningPairs(hDC, iRet, pKerningTable);
		assert(iRet > 0);

		for(unsigned int i=0; i<iRet; ++i)
		{
			pair<unsigned long, unsigned long> p;
			p.first = pKerningTable[i].wFirst;
			p.second = pKerningTable[i].wSecond;
			m_xKerningTable[p] = pKerningTable[i].iKernAmount;
		}

		delete [] pKerningTable;
	}

	// get text metrics for this font

	LPTEXTMETRIC pTM = new TEXTMETRIC;
	BOOL b = GetTextMetrics(hDC, pTM);
	assert(b);
	if(b)
	{
	    m_xFontMetrics.m_iAscent  = pTM->tmAscent;
	    m_xFontMetrics.m_iDescent = pTM->tmDescent;
	    m_xFontMetrics.m_iHeight  = pTM->tmHeight;
	}
	delete pTM;

	::SelectObject(hDC, hOldFont);
	::DeleteDC(hDC);
}


//---------------------------------------------------------------------------------------------------------------------
CAlphaBitmapFont::~CAlphaBitmapFont()
{
	int i;
	for (i=0; i<CACHESIZE; i++)
	{
		FreeSlot(i);
	}

	delete [] m_pCharacters;
	delete [] m_piLastRequestNumber;
}


//---------------------------------------------------------------------------------------------------------------------
CFontHandle	
CAlphaBitmapFont::Create(int p_iHeight, const CStr& p_rsFaceName, Locale p_eLocale, Pitch p_ePitch, Weight p_eWeight, int p_iFlags)
{
	CStr sInfo = "BMPA";
	sInfo += CFont::GetInfoString(p_iHeight, p_rsFaceName, p_eLocale, p_ePitch, p_eWeight, p_iFlags);

	for(unsigned int i=0; i<m_axFontCache.Size(); ++i)
	{
		if(m_axFontCache[i].m_pxFont->GetInfoString() == sInfo)
		{
			m_axFontCache[i].m_iRefCount++;
			return CFontHandle(m_axFontCache[i].m_pxFont);
		}
	}
	
	CAlphaBitmapFont* p = new CAlphaBitmapFont(p_iHeight, p_rsFaceName, p_eLocale, p_ePitch, p_eWeight, p_iFlags);
	i = m_axFontCache.PushEntry();
	m_axFontCache[i].m_iRefCount = 1;
	m_axFontCache[i].m_pxFont = p;

	return CFontHandle(p);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CAlphaBitmapFont::Release(CFontHandle& p_xrHandle)
{
	assert(p_xrHandle.GetFontType() == "BMPA");

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
CAlphaBitmapFontCharacter* 
CAlphaBitmapFont::GetChar(unsigned long iChar)
{
	m_iRequests++;
	int i = FindSlot(iChar);
	if(i < 0)
	{
		i = FindEmptySlot(iChar);
		m_pCharacters[i] = new CAlphaBitmapFontCharacter(m_hFont, iChar);
		if(m_pCharacters[i]->m_pBuffer == 0)
		{
			// empty character ---> space
			if(m_ePitch == PITCH_FIXED)
			{
				// fixed size font: space must have same width like any other character
				CAlphaBitmapFontCharacter* pChar = GetChar('e');
				m_pCharacters[i]->m_iSpacingBefore = pChar->m_iWidth + pChar->m_iSpacingBefore;
				m_pCharacters[i]->m_iSpacingAfter = pChar->m_iSpacingAfter;
			}
			else
			{
				// variable size font: find a nice proportion for spaces
				m_pCharacters[i]->m_iSpacingBefore = m_xFontMetrics.m_iHeight / 4;
				m_pCharacters[i]->m_iSpacingAfter = 0;
			}
		}
	}
	else
	{
		m_iCacheHits++;
	}
	
	m_piLastRequestNumber[i] = m_iRequests;
	return m_pCharacters[i];
}


//---------------------------------------------------------------------------------------------------------------------
/// liefer Breite eines Zeichens in Pixeln
int		
CAlphaBitmapFont::GetCharacterWidth(int p_iChar)
{
	CAlphaBitmapFontCharacter* p = GetChar(p_iChar);
	return p->m_iWidth + p->m_iSpacingBefore + p->m_iSpacingAfter;
}


//---------------------------------------------------------------------------------------------------------------------
/// liefert Kerning zwischen zwei Zeichen (d.h. Pixeldifferenz für Über/Unterschneidung)
int	
CAlphaBitmapFont::GetKerning(unsigned long iFirstChar, unsigned long iSecondChar) const 
{
	if(m_xKerningTable.size() == 0)
	{
		return 0;
	}

	pair<unsigned long, unsigned long> p;
	p.first = iFirstChar;
	p.second = iSecondChar;

	map< pair<unsigned long, unsigned long>, int>::const_iterator iter = m_xKerningTable.find(p);
	if(iter != m_xKerningTable.end())
	{
		int iKernAmout = iter->second;
		return iKernAmout;
	}
	else
	{
		return 0;
	}
}


//---------------------------------------------------------------------------------------------------------------------
int	
CAlphaBitmapFont::FindSlot(unsigned long iChar)
{
	unsigned long i	= iChar % CACHESIZE;
	unsigned long iEnd	= (iChar + COLLISIONRANGE) % CACHESIZE;

	while (i != iEnd)
	{
		if(m_pCharacters[i])
		{
			if(m_pCharacters[i]->m_iCharacter == iChar)
			{
				return i;
			}
		}

		i++;
		if(i>=CACHESIZE)
		{
			i = 0;
		}
	}
	
	return -1;
}


//---------------------------------------------------------------------------------------------------------------------
/// sucht einen leeren Slot im Cache für ein Zeichen; schafft notfalls Platz
int	
CAlphaBitmapFont::FindEmptySlot(unsigned long iChar)
{
	unsigned long i		= iChar % CACHESIZE;
	unsigned long iEnd	= (iChar + COLLISIONRANGE) % CACHESIZE;

	while (i != iEnd)
	{
		if(m_pCharacters[i] == 0)
		{
			return i;
		}
		i++;
		if (i>=CACHESIZE)
		{
			i=0;
		}
	}
	
	unsigned long iLRU = iChar % CACHESIZE;
	i =	iLRU + 1;
	while (i != iEnd)
	{
		if(m_piLastRequestNumber[i] < m_piLastRequestNumber[iLRU])
		{
			iLRU = i;
		}
		i++;
		if(i>=CACHESIZE)
		{
			i=0;
		}
	}

	FreeSlot(iLRU);
	return iLRU;
}



//---------------------------------------------------------------------------------------------------------------------
/// leert einen Slot im Cache
void 
CAlphaBitmapFont::FreeSlot(unsigned long iSlot)
{
//	DebugPrint("CharacterCache : freeing slot %d", iSlot);

	delete m_pCharacters[iSlot];
	m_pCharacters[iSlot]			= 0;
	m_piLastRequestNumber[iSlot]	= 0;
}


} // namespace UILib

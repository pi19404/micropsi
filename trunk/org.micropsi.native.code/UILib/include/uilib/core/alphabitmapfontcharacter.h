#ifndef UILIB_ALPHABITMAPFONTCHARACTER_H_INCLUDED
#define UILIB_ALPHABITMAPFONTCHARACTER_H_INCLUDED

#include "windows.h"

namespace UILib 
{

class CAlphaBitmapFontCharacter
{
public:
		CAlphaBitmapFontCharacter(HFONT p_hFont, unsigned long p_iCharacter);
		~CAlphaBitmapFontCharacter();

		unsigned long	m_iCharacter;			///< Unicode dieses Zeichnes
		unsigned char*	m_pBuffer;				///< Pixelbuffer dieses Zeichens
		int				m_iWidth;				///< Breite in Pixeln
		int				m_iHeight;				///< Höhe in Pixeln
		int				m_iHeightOverBaseLine;	///< Höhe in Pixeln über der Grundlinie
		int				m_iSpacingBefore;		///< Anzahl zusätzlicher Pixel vor dem Zeichen
		int				m_iSpacingAfter;		///< Anzahl zusätzlicher Zeichen hinter dem Zeichen
};

} // namespace UILib

#endif // ifndef UILIB_ALPHABITMAPFONTCHARACTER_H_INCLUDED

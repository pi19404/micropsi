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
		int				m_iHeight;				///< H�he in Pixeln
		int				m_iHeightOverBaseLine;	///< H�he in Pixeln �ber der Grundlinie
		int				m_iSpacingBefore;		///< Anzahl zus�tzlicher Pixel vor dem Zeichen
		int				m_iSpacingAfter;		///< Anzahl zus�tzlicher Zeichen hinter dem Zeichen
};

} // namespace UILib

#endif // ifndef UILIB_ALPHABITMAPFONTCHARACTER_H_INCLUDED

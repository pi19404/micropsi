#ifndef UILIB_ALPHABITMAPFONT_H_INCLUDED
#define UILIB_ALPHABITMAPFONT_H_INCLUDED

#include "baselib/dynarray.h"
#include <map>
#include "font.h"
#include "alphabitmapfontcharacter.h"

namespace UILib
{

class CAlphaBitmapFont : public ::UILib::CFont
{
public:
	static CFontHandle		Create(int p_iHeight, const CStr& p_rsFaceName = "", Locale p_eLocale = L_STANDARD, Pitch p_ePitch = PITCH_VARIABLE, Weight p_eWeight = W_DONTCARE, int p_iFlags = F_NONE);
	static void				Release(CFontHandle& p_xrHandle);

	CAlphaBitmapFontCharacter*	GetChar(unsigned long);

	/// get kerning info for two characters (= distance correction between these two chars)
	int						GetKerning(unsigned long iFirstChar, unsigned long iSecondChar) const;

	/// liefert Informationen über diesen Font als String
	virtual CStr			GetInfoString() const		{ return CStr("BMPA ") + __super::GetInfoString(); }

	/// liefert Fonttyp
	virtual CFourCC			GetFontType() const			{ return CFourCC("BMPA"); } 

	/// liefer Breite eines Zeichens in Pixeln
	virtual int				GetCharacterWidth(int p_iChar);

protected:

	CAlphaBitmapFont(int p_iHeight, const CStr& p_rsFaceName = "", CFont::Locale p_eLocale = L_STANDARD, 
				CFont::Pitch p_ePitch = PITCH_VARIABLE, Weight p_eWeight = W_DONTCARE, int p_iFlags = F_NONE);
	~CAlphaBitmapFont();

	HDC						m_hDC;					///< device context in which characters are rendered
	HFONT					m_hFont;				///< windows font handle of my font

	std::map< std::pair<unsigned long, unsigned long>, int>	m_xKerningTable;	///< Kerning Table: char x char --> Kerning Amount

	::UILib::CFont::Pitch	m_ePitch;				///< pitch of this font (fixed / variable)

	int						m_iRequests;			///< number of characters requested so far
	int						m_iCacheHits;			///< number of characters actually found in cache

	static const unsigned long	CACHESIZE;			///< max number of different characters to cache
	static const unsigned long	COLLISIONRANGE;		///< how many slots should I consider to avoid hash collision?

	CAlphaBitmapFontCharacter**	m_pCharacters;		///< array with characters
	int	*					m_piLastRequestNumber;	///< for LRU-algorithm

	/// cache entry of font cache
	struct TFontChacheEntry
	{
		CAlphaBitmapFont*		m_pxFont;			///< pointer to a font
		int						m_iRefCount;		///< reference count for that font
	};

	static CDynArray<TFontChacheEntry> m_axFontCache;	///< cache with all instances of this class created so far

	int		FindSlot(unsigned long iChar);
	int		FindEmptySlot(unsigned long iChar);
	void	FreeSlot(unsigned long Slot);

};

} // namespace UILib

#endif  // ifndef UILIB_ALPHABITMAPFONT_H_INCLUDED

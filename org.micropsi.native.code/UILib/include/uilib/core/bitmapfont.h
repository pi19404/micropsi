#ifndef UILIB_BITMAPFONT_H_INCLUDED
#define UILIB_BITMAPFONT_H_INCLUDED

#include "baselib/dynarray.h"
#include "font.h"
#include <hash_map>

namespace UILib
{

class CBitmap;

class CBitmapFont : public ::UILib::CFont
{
public:
	static CFontHandle		Create(const CStr& p_rsBitmapFilename, const CStr& p_rsCharacterOrder, int p_iSpaceWidth, int p_iBaseLine, int p_iKerningBias = 0);
	static void				Release(CFontHandle& p_xrHandle);

	/// liefert alle Informationen über diesen Font als String
	virtual CStr			GetInfoString() const		{ return CStr("BMPF ") + __super::GetInfoString(); }

	/// liefert Fonttyp
	virtual CFourCC			GetFontType() const			{ return CFourCC("BMPF"); } 

	/// liefert Kerning zwischen zwei Zeichen - ist immer 0; dieser Fonttyp unterstützt kein Kerning
	virtual	int				GetKerning(unsigned long iFirstChar, unsigned long iSecondChar) const	{ return m_iKerningBias; }
	
	/// liefert Zeichen als Bitmap
	CBitmap*				GetChar(int p_iChar);		

	/// liefert Breite eines Zeichens in Pixeln
	virtual int				GetCharacterWidth(int p_iChar);

	/// liefert den Bitmap-Dateinamen, der zur Erzeugung dieses Fonts benutzt wurde
	const CStr&				GetBitmapFilename() const	{ return m_sBitmapFilename; }

	/// Eintrag für Font Cache
	struct TFontChacheEntry
	{
		CBitmapFont*		m_pxFont;					///< Zeiger auf Font
		int					m_iRefCount;				///< Referenzzähler
	};

	static CDynArray<TFontChacheEntry> m_axFontCache;	///< cache mit allen bisherigen Instanzen dieser Klasse

protected:

	CBitmapFont(const CStr& p_rsBitmapFilename, const CStr& p_rsCharacterOrder, int p_iSpaceWidth, int p_iBaseLine, int p_iKerningBias = 0);
	~CBitmapFont();

	std::hash_map<int, CBitmap*>	m_xCharacters;		///< alle Zeichen dieses Fonts
	int								m_iKerningBias;		///< Kerningdelta für alle Paare
	CStr							m_sBitmapFilename;	///< Dateiname der Bitmap
};

} // namespace UILib

#endif  // ifndef UILIB_BITMAPFONT_H_INCLUDED

#ifndef UILIB_FONT_H_INCLUDED
#define UILIB_FONT_H_INCLUDED

#include "baselib/fourcc.h"

namespace UILib 
{

class CFont
{
public:
	enum Locale
	{
		L_STANDARD		= 0,			///< standard westlicher Zeichensatz
		L_JAPANESE		= 1,			///< japanischer Zeichensatz
		L_KOREAN		= 2,			///< koreanischer Zeichensatz
		L_CHINESE		= 3,			///< chinesischer Zeichensatz
		L_RUSSIAN		= 4,			///< russischer Zeichensatz
		L_THAI			= 5,			///< Thailändischer Zeichensatz
		L_ARABIC		= 6,			///< Arabischer Zeichensatz
		L_HEBREW		= 7,			///< Hebräischer Zeichensatz
		L_VIETNAMESE	= 8				///< Vietnamesischer Zeichensatz
	};

	enum Pitch
	{
		PITCH_VARIABLE  = 0,			///< variable pitch (Alle Buchstaben können unterschiedlich breit sein)
		PITCH_FIXED		= 1				///< fixed pitch (Alle Buchstaben haben die gleiche Breite)
	};

	enum Flags
	{
		F_NONE,
		F_ITALIC,
		F_UNDERLINE,
		F_STRIKEOUT,
	};

	enum Weight
	{
		W_DONTCARE, 
		W_THIN,
		W_EXTRALIGHT, 
		W_ULTRALIGHT, 
		W_LIGHT,
		W_NORMAL, 
		W_REGULAR, 
		W_MEDIUM, 
		W_SEMIBOLD, 
		W_DEMIBOLD, 
		W_BOLD,
		W_EXTRABOLD, 
		W_ULTRABOLD,  
		W_HEAVY,
		W_BLACK,	
	};

	struct TFontMetrics
	{
		int			m_iHeight;			///< Texthöhe in Pixeln (insgesamt)
		int			m_iAscent;			///< Höhe in Pixeln über der Grundlinie
		int			m_iDescent;			///< Höhe in Pixeln unter der Grundlinie
		Weight		m_eWeight;			///< Schriftdicke
		int			m_iFlags;			///< Flags (siehe Flags enum)
		Pitch		m_ePitch;			///< Variable oder feste Zeichenbreite
	};

	/// liefert Kerning zwischen zwei Zeichen (Pixeldifferenz für Über-/Unterschneidung)
	virtual	int		GetKerning(unsigned long iFirstChar, unsigned long iSecondChar) const = 0;

	/// liefert einen Informationsstring über diesen Font
	virtual CStr	GetInfoString() const = 0;

	/// Erzeugt einen Informationsstring aus den Parametern dieses Fonts
	static CStr		GetInfoString(int p_iHeight, const CStr& p_rsFaceName, Locale p_eLocale, Pitch p_ePitch, Weight p_eWeight, int p_iFlags);

	/// liefert Fonttyp
	virtual			CFourCC GetFontType() const = 0;

	/// liefer Breite eines Zeichens in Pixeln
	virtual int		GetCharacterWidth(int p_iChar) = 0;

	/// liefert Metrik dieses Fonts
	const TFontMetrics&		GetMetrics() const		{ return m_xFontMetrics; }
	
	virtual ~CFont();

	bool operator== (const CFont& p_rxOther) const;

protected:

	CFont(int p_iHeight, const CStr& p_rsFaceName = "", Locale p_eLocale = L_STANDARD, Pitch p_ePitch = PITCH_VARIABLE, Weight p_eWeight = W_DONTCARE, int p_iFlags = F_NONE);


	TFontMetrics	m_xFontMetrics;		///< font metrics
	Locale		    m_eLocale;			///< font locale
	CStr			m_sFaceName;		///< font face name

	friend class CDevAbstraction;
};


class CFontHandle
{
public:

	CFont*			m_pxFont;				///< Zeiger auf Font

public:

	CFontHandle(CFont* p_pxFont)				{ m_pxFont = p_pxFont; }
	CFontHandle()								{ m_pxFont = 0; }
	CFontHandle(const CFontHandle& p_rxOther)	{ m_pxFont = p_rxOther.m_pxFont; };
	~CFontHandle()	{}

	/// liefert Fonttyp
	CFourCC GetFontType() const					{ return m_pxFont ? m_pxFont->GetFontType() : CFourCC("NONE"); } 

	bool IsValid()								{ return m_pxFont != 0; }


	bool operator==(const CFontHandle& p_rxOther)		{ return p_rxOther.m_pxFont == m_pxFont; }
	bool operator!=(const CFontHandle& p_rxOther)		{ return p_rxOther.m_pxFont != m_pxFont; }

	friend class CFont;
};


} // namespace UILib

#endif // UILIB_FONT_H_INCLUDED


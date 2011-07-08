#ifndef UILIB_LABEL_H_INCLUDED 
#define UILIB_LABEL_H_INCLUDED

#include "uilib/core/window.h"
#include "uilib/core/bitmapreference.h"
#include "uilib/core/visualization.h"
#include "uilib/core/linebreaks.h"


namespace UILib
{

/** 
	\class CLabel
	\brief ein Fenster, dass einen mehrzeiligen Text oder eine Bitmap anzeigen kann

	Labels können wahlweise einen einen mehrzeiligen Text oder eine Bitmap anzeigen, jedoch nicht beides gleichzeitig.
	Wenn eine Bitmap zugewiesen wird, wird ein evtl. gesetzter Text verworfen und umgekehrt.

	Text wird nicht automatisch umgebrochen, sondern es werden nur harte Zeilenumbrüche im Text ('\n') berücksichtigt.
	Text kann linksbündig, rechtsbündig oder zentriert dargestellt werden. Es kann eine Textfarbe und ein Font angegeben 
	werden. Standardeinstellung ist linksbündig, Farbe und Font werden von der Visualisierung bestimmt.

	Normalerweise hat ein Label einen Hintergrund, d.h. das gesamte Fenster wird mit einer von der Visualisierung definierten
	Hintergrundfarbe gefüllt, bevor Bitmap oder Text darüber gezeichnet werden. Man kann dieses Verhalten abschalten, dadurch
	wird das Fenster transparent, d.h. das dahinter liegende Fenster wird sichtbar.
*/
class CLabel : public CWindow
{
public:

	/// Möglichkeiten der Textausrichtung
	enum HorizontalTextAlignment
	{
		TA_HCenter	= COutputDevice::TA_HCenter,
		TA_Left		= COutputDevice::TA_Left,
		TA_Right	= COutputDevice::TA_Right
	};

	enum VerticalTextAlignment
	{
		TA_VCenter	= COutputDevice::TA_VCenter,
		TA_Top		= COutputDevice::TA_Top,
		TA_Bottom	= COutputDevice::TA_Bottom
	};

	enum Specials
	{
		SP_Normal	  = 0,
		SP_Selected
	};

	/// erzeugt ein neues Label
	static CLabel*	Create();

	virtual void	AutoSize(bool p_bMayShrink = true);

	/// bestimmt den darzustellenden Text, entfernt eine evtl. vorhandene Bitmap
	void			SetText(CStr p_sText);

	/// liefert den aktuell dargestellten Text
	CStr			GetText() const;

	/// setzt eine Bitmap zum Anzeigen
	void			SetBitmap(const CStr& p_sBitmap);

	/// setzt eine Bitmap zum Anzeigen - meist sollte man SetBitmap(const CStr&) verwenden, weil man so das Anlegen / Löschen der Bitmap vermeidet
	void			SetBitmap(const CBitmap* p_pxBitmap);

	/// liefert die aktuell angezeigte Bitmap; 0, wenn keine einstellt ist
	const CBitmap*	GetBitmap() const;

	/// bestimmt, ob der Hintergrund des Fensters gezeichnet werden muss,  
	void			SetBackground(bool p_bBackground = true);

	/// liefert true, wenn der Hintergrund abgeschaltet ist
	bool			GetBackground() const;

	/// bestimmt Textausrichtung 
	void			SetTextAlign(HorizontalTextAlignment p_eHTextAlignment, VerticalTextAlignment p_eVTextAlignment = TA_VCenter);

	/// liefert aktuelle horizontale Textausrichtung
	HorizontalTextAlignment GetHorizontalTextAlignment() const;

	/// liefert aktuelle horizontale Textausrichtung
	VerticalTextAlignment GetVerticalTextAlignment() const;

	/// setzt spezielle Darstellungseigenschaften, siehe enum Specials
	void			SetSpecialProperty(int p_iSpecial);

	/// setzt eine Farbe zur Textdarstellung
	void			SetTextColor(CColor p_xColor);

	/// setzt die Textfarbe zurück auf die von der Visualisierung 
	void			RestoreDefaultTextColor();

	/// setzt einen bestimmten Font zur Textdarstellung
	void			SetFont(CFontHandle h);

	virtual	bool	SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);
	virtual	bool	GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;

protected:

	CLabel();
	~CLabel();

	virtual void	DeleteNow();
	virtual CStr	GetDebugString() const;

	virtual void	Paint(const CPaintContext& p_rxContext);
	virtual	bool	OnVisualizationChange();

	CVisualization::BackgroundType m_eBackgroundType;  ///< Hintergrundtyp

private:
	CStr					m_sText;				///< Text (wenn es keine Bitmap gibt)
	CBitmapRef				m_xBitmap;				///< Bitmap
	bool					m_bBackground;			///< Hintergrund zeichnen ein/aus
	HorizontalTextAlignment m_eHTextAlignment;		///< Horizontale Textausrichtung
	VerticalTextAlignment	m_eVTextAlignment;		///< Vertikale Textausrichtung

	int						m_iSpecial;				///< spezielle Texteigenschaften (normal, selected - sieht enum Specials)
	bool					m_bCustomTextColor;		///< true: individuelle Textfarbe wird benutzt
	CColor					m_xTextColor;			///< individuelle Textfarbe
	CFontHandle				m_xCustomFont;			///< individueller Font
	CLineBreaks				m_xLineBreaks;			///< Zeilenumbrüche
};

#include "uilib/controls/label.inl"

} //namespace UILib


#endif // ifndef UILIB_LABEL_H_INCLUDED


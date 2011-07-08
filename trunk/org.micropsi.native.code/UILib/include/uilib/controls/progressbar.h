#ifndef UILIB_PROGRESSBAR_H_INCLUDED 
#define UILIB_PROGRESSBAR_H_INCLUDED

#include "uilib/core/window.h"
#include "uilib/core/bitmapreference.h"

namespace UILib
{

class CProgressBar : public CWindow
{
public:
	/// erstellt einen neuen Progressbar
	static CProgressBar*	Create();	

	/// setzt den Maximalwert des Balkens; muss >= 0 sein
	float			SetRange(float p_fRange);

	/// liefert den aktuellen Maximalwert
	float			GetRange() const;

	/// setzt den aktuellen Fortschrittswert; sollte >= 0 und <= Range sein
	float			SetProgress(float p_fProgress);

	/// liefert den aktuellen Fortschrittswert
	float			GetProgress() const;

	/// bestimmt die Vordergrundfarbe
	void			SetBarColor(CColor p_xColor);			

	/// Bestimmt die Hintergrundfarbe
	void			SetBackgroundColor(CColor p_xColor);	

	/// Bestimmt eine Bitmap für den Balken (Farbe wird nicht benutzt, wenn Bitmap gesetzt ist)
	void			SetBarBitmap(const CStr& p_rsBitmap);

	/// Bestimmt eine Bitmap für den Hintergrund (Farbe wird nicht benutzt, wenn Bitmap gesetzt ist)
	void			SetBackgroundBitmap(const CStr& p_rsBitmap);

	/// liefert die Bitmap für den Balken
	const CBitmap*	GetBarBitmap() const;

	/// liefert die Hintergrundbitmap
	const CBitmap*	GetBackgroundBitmap() const;

	/// bestimmt, ob dies ein vertikaler Balken ist (im Gegensatz zum horizontalen)
	void			SetVertical(bool p_bVertical);

	/// liefert true, wenn dies ein vertikaler Balken ist (sonst: horizontaler Balken)
	bool			GetVertical() const;

	/// setzt den Wert eines genannten Attributes
	virtual bool	SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);

	/// liefert den Wert eines benannten Attributes
	virtual bool	GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;


protected:
	CProgressBar();
	~CProgressBar();

	virtual void		Paint(const CPaintContext& p_rxCtx);
	virtual CStr		GetDebugString() const		{ return "CProgressBar"; }

private:

	float				m_fRange;					///< Maximalwert des Balkens; muss >= 0 sein
	float				m_fProgress;				///< Aktueller Wert der Balkens; muss  >= 0 und <= m_fRange sein
	bool				m_bVertical;				///< true: vertikaler Balken; false: horizontaler Balken
	CColor				m_xColor;					///< Farbe des Balkens
	CColor				m_xBKColor;					///< Hintergrundfarbe
	CBitmapRef			m_xBKBitmap;				///< Hintergrundbitmap (Alternative zu Farbe)
	CBitmapRef			m_xBarBitmap;				///< Bitmap für den Balken (Alternative zu Farbe)

	CProgressBar(const CProgressBar&);
	operator=(const CProgressBar&);
};

#include "progressbar.inl"

}	//namespace UILib


#endif // ifndef UILIB_PROGRESSBAR_H_INCLUDED


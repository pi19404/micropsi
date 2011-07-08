#ifndef UILIB_BUTTON_H_INCLUDED 
#define UILIB_BUTTON_H_INCLUDED

#include "basicbutton.h"
#include "label.h"

namespace UILib
{

class CButton : public CBasicButton
{
public:
	/// erzeugt einen neuen Button
	static CButton* Create();

	/// setzt den Button automatisch auf die optimale Größe
	virtual void	AutoSize(bool p_bMayShrink = false);

	/// bestimmt den Text auf dem Button (Text und Bitmaps schließen sich aus!)
	void			SetText(CStr p_sText);

	/// liefert den Text auf dem Button
	CStr			GetText() const;

	/// setzt Bitmap
	void			SetBitmap(const CStr& p_rsBitmap);

	/// setzt Bitmaps
	void			SetBitmap(const CStr& p_rsNormalBmp, const CStr& p_rsDownBmp, const CStr& p_rsHoveredBmp, const CStr& p_rsDisabledBmp);

	/// setzt Bitmap
	void			SetBitmap(const CBitmap* p_pxBitmap);

	/// setzt Bitmaps
	void			SetBitmap(const CBitmap* p_pxNormalBmp, const CBitmap* p_pxDownBmp, const CBitmap* p_pxHoveredBmp, const CBitmap* p_pxDisabledBmp);

	/// liefert die aktuell gesetzte Bitmap (oder 0 falls keine gesetzt ist)
	const CBitmap*	GetBitmap() const;

	/// schaltet den Rahmen ein oder aus
	void			SetFrame(bool p_bFrame = true);

	/// liefert die aktuelle Einstellung für den Rahmen (ein oder aus)
	bool			GetFrame() const;

	/// schaltet den Hintergrund ein oder aus
	void			SetBackground(bool p_bBackground = true);

	/// liefert die aktuelle Einstellung für den Hintergrund (ein oder aus)
	bool			GetBackground() const;

	/// Alphawerte der Bitmaps schreiben ein / aus
	void			SetWriteAlpha(bool p_bWriteAlpha = true);

	/// setzt den Wert eines benannten Attributes
	virtual bool	SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);

	/// liefert den Wert eines benannten Attributes
	virtual bool	GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;

	/// setzt das Fenster auf disabled/enabled
	virtual void	SetDisabled(bool p_bDisabled = true);

protected:

	CButton();
	virtual ~CButton();

	virtual CStr GetDebugString() const;

	virtual bool HandleMsg(const CMessage& p_rxMessage);
	virtual void Paint(const CPaintContext& p_rxCtx);

	virtual bool OnResize();
	virtual bool OnVisualizationChange();
	virtual bool OnDeactivate();
	virtual bool OnActivateIndirect();
	virtual bool OnDeactivateIndirect();
	virtual	void UpdateBitmap();

	/// setzt den *logischen* Gedrückt-Status des Buttons - siehe Basisklasse CBasicButton
	virtual void SetButtonDown(bool p_bButtonDown);

	CLabel*		 	m_pxLabel;				///< Label, dass den Text bzw. die Bitmap anzeigt
	bool			m_bFrame;				///< button has a frame
	CRct			m_xFrameSize;			///< width of frame; updated on device- or visualization change
	CPnt			m_xTextDisplacement;	///< text displacement while button is down; updated on device- or visualization change
	CPnt			m_xLabelPos;			///< correct position of static control (centered)
	bool			m_bBackground;			///< draw button background
	bool			m_bHovered;				///< hovered yes/no

	CBitmapRef		m_xNormalBmp;			///< normale Bitmap
	CBitmapRef		m_xDownBmp;				///< alternative Bitmap wenn der Button gedrückt ist; darf 0 sein
	CBitmapRef		m_xHoveredBmp;			///< alternative Bitmap wenn die Maus über dem Button ist; darf 0 sein
	CBitmapRef		m_xDisabledBmp;			///< alternative Bitmap wenn der Button disabled ist; darf 0 sein

private:

	CButton(const CButton&) {};
	operator=(const CButton&) {};
};

#include "uilib/controls/button.inl"


} //namespace UILib


#endif  // ifndef UILIB_BUTTON_H_INCLUDED


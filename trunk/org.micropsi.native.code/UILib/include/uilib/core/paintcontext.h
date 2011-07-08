#ifndef UILIB_PAINTCONTEXT_H_INCLUDED
#define UILIB_PAINTCONTEXT_H_INCLUDED

#include "baselib/pnt.h"
#include "baselib/rect.h"
#include "outputdevice.h"

namespace UILib
{

/*
	Ein CPaintContext kapselt ein COutputDevice. Er erlaubt nur Zugriff auf einen rechteckigen Ausschnitt (ClipRect) des 
	Devices, d.h. alle Ausgabeoperationen werden an diesem Rechteck abgeschnitten.
	Außerdem sind die Koordinaten um ein bestimmtest Offset verschoben.
	Ein CPaintContext wird Fenstern für ihre Paintmethode übergeben. Fenster zeichnen in lokalen Koordinaten, d.h. 
	das Offset des PaintContext sorgt dafür, dass die Zeichenoperation an die richtige Stelle des Devices gelangt. Außerdem
	kann das Fenster nicht außerhalb seiner Fläche zeichnen, dafür sorgt das ClipRect.
*/


class CPaintContext
{
protected:
	CPnt			m_pntOffset;				///< Offset, um das alle Koordinaten verschoben werden
	CRct			m_xClipRect;				///< Rechteck, außerhalb dessen alle Zeichenoperationen abgeschnitten werden
	
	COutputDevice*	m_pxDevice;					///< das zugrundeliegende Ausgabegerät
	
	CPaintContext(COutputDevice* p_pxDevice);
	~CPaintContext();
	
	void SetOffset(CPnt p_pntOffset);
	void AddOffset(CPnt p_pntOffset);
	void AddOffset(int p_iOffset);
	void SetClip(CRct p_xClipRect);

	friend class CWindowMgr;

public:

	/// liefert den Typ des zugrundeliegendes Devices
	CFourCC GetDevType() const	{ return m_pxDevice->GetType(); }

	/// liefert einen Pointer auf das zugrundeliegende Device; der sollte möglichst nicht zum Zeichnen verwendet werden
	const COutputDevice* GetDevice() const  { return m_pxDevice; }

	/// liefert die Farbe eines einzelnen Pixels
	CColor GetPixel(const CPnt& p_xPnt) const;

	/// Zeichnet einen einzelnen Pixel
	void SetPixel(const CPnt& p_xPnt, const CColor& p_xColor) const;

	/// Zeichnet einen einzelnen Pixel
	void SetPixel(int p_iX, int p_iY, const CColor& p_xColor) const;

	/// Zeichnet eine Linie
	void DrawLine(const CPnt& p_rxPnt1, const CPnt& p_rxPnt2, CColor p_xColor) const;

	/// Zeichnet eine Linie
	void DrawLine(int p_iX1, int p_iY1, int p_iX2, int p_iY2, CColor p_xColor) const;

	/// Zeichnet ein nicht-ausgefülltest Rechteck, also nur den Umriss
	void DrawRect(const CRct& p_rxRect, CColor p_xColor ) const;

	/// Zeichnet ein nicht-ausgefülltest Rechteck, also nur den Umriss
	void DrawRect(const CPnt& p_rxPnt1, const CPnt& p_rxPnt2, CColor p_xColor) const;

	/// Zeichnet ein nicht-ausgefülltest Rechteck, also nur den Umriss
	void DrawRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, CColor p_xColor) const;

	/// Zeichnet ein ausgefülltes Rechteck
	void FillRect(const CRct& p_rxRect, CColor p_xColor) const;

	/// Zeichnet ein ausgefülltes Rechteck
	void FillRect(const CPnt& p_rxPnt1, const CPnt& p_rxPnt2, CColor p_xColor) const;

	/// Zeichnet ein ausgefülltes Rechteck
	void FillRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, CColor p_xColor) const;

	/// Zeichnet einen Textstring
	void DrawText(CFontHandle p_hFont, CPnt p_xPos, CStr p_sText, CColor p_xColor) const;

	/// Zeichnet einen Textstring
	void DrawText(CFontHandle p_xFontHandle, const CRct& p_rxRct, COutputDevice::HorizontalTextAlignment p_eHTextAlign, 
				  COutputDevice::VerticalTextAlignment p_eVTextAlign, CStr p_sText, CColor p_xColor) const;

	/// Zeichnet eine Bitmap
	void Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, bool p_bAlpha = false) const;

	/// Zeichnet eine Bitmap
	void Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, const CRct& p_xClipRect, bool p_bAlpha = false) const;


	/// liefert das relative Clipping-Rechteck (relativ zum Offset)
	CRct GetRelClipRect() const;

	/// liefert das absolute Clipping-Rechteck
	CRct GetAbsClipRect() const				{ return m_xClipRect; }

	/// liefert das Offset
	CPnt GetOffset() const					{ return m_pntOffset; }

};

#include "paintcontext.inl"

} // namespace UILib

#endif // ifndef UILIB_PAINTCONTEXT_H_INCLUDED


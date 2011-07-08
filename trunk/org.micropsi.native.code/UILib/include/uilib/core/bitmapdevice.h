#ifndef UILIB_BITMAPDEVICE_H_INCLUDED
#define UILIB_BITMAPDEVICE_H_INCLUDED

#include "outputdevice.h"

namespace UILib 
{

class CAlphaBitmapFontCharacter;

class CBitmapDevice : public COutputDevice
{
public:

	CBitmapDevice(CBitmap* p_pxBitmap);
	CBitmapDevice(unsigned long* p_piPixels, int p_iWidth, int p_iHeight, int p_iPitch = 0);
	~CBitmapDevice();

	virtual CFourCC				GetType() const				{ return CFourCC("BMPD"); }

	virtual CSize				GetSize() const				{ return CSize(m_iWidth, m_iHeight); };

	virtual bool				BeginPaint();
	virtual void				EndPaint(bool p_bValidateAll = true);

	virtual void				Invalidate();

	virtual CColor				GetPixel(int p_iX, int p_iY);
	virtual void				SetPixel(int p_iX, int p_iY, const CColor& p_xColor);

	virtual void				DrawLine(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor);

	virtual void				DrawRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor);

	virtual void				FillRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor);

	virtual void				Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, bool p_bAlpha = false);
	virtual void				Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, const CRct& p_xClipRect, bool p_bAlpha = false);

protected:
	void						DrawCharacter(CAlphaBitmapFontCharacter* p_pChar, int p_iX, int p_iY, CColor p_xColor, const CRct& p_xClipRect);

	int							m_iWidth;				///< Breite der Zeichenfläche
	int							m_iHeight;				///< Höhe der Zeichenfläche
	int							m_iPitch;				///< reale Zeilenlänge in Pixeln im Speicher (inklusive Auffüllung)
	CRct						m_rctSize;				///< Clipping Rechteck; = (0, 0, Breit, Höhe)
	int							m_iSize;				///< Anzahl Pixel gesamt
	bool						m_bPaint;				///< true zwischen BeginPaint() und EndPaint()
	unsigned long*				m_pdwPixels;			///< Pointer auf eigentliche Pixel
}; 

} // namespace UILib

#endif // ifndef UILIB_BITMAPDEVICE_H_INCLUDED


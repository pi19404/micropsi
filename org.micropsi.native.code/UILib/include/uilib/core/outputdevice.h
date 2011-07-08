#ifndef UILIB_OUTPUTDEVICE_H_INCLUDED
#define UILIB_OUTPUTDEVICE_H_INCLUDED

#include "baselib/size.h"
#include "font.h"
#include "bitmap.h"
#include "alphabitmapfont.h"
#include "bitmapfont.h"
#include "baselib/rectlist.h"
#include "baselib/fourcc.h"
#include "baselib/color.h"

// damit Windows meine Funktionen nicht in CreateFontA und DrawTextA umbenennt... 
#undef CreateFont
#undef DrawText

namespace UILib 
{

class COutputDevice
{
protected:
	CRctList	 m_xInvalidRegions;		///< ungültige Regionen (d.h. müssen neu gezeichnet werden )	

public:
	
	enum HorizontalTextAlignment
	{
		TA_HCenter,
		TA_Left,
		TA_Right
	};

	enum VerticalTextAlignment
	{
		TA_VCenter,
		TA_Top,
		TA_Bottom
	};

	COutputDevice()			 {};
	virtual ~COutputDevice() {};
	
	virtual CFourCC				GetType() const  = 0;
	virtual CSize				GetSize() const  = 0;

	virtual bool				BeginPaint() = 0;
	virtual void				EndPaint(bool p_bValidateAll = true) = 0;

	virtual void				Invalidate() = 0;
	virtual void				Invalidate(const CRct& p_xRect);
	virtual void				Invalidate(const CRctList& p_xRectList);
	virtual void				Validate();
	virtual void				Validate(const CRct& p_xRect);
	virtual void				Validate(const CRctList& p_xRectList);
	virtual const CRctList&		GetInvalidRegions();

	virtual void				Clear(CColor p_xColor = CColor(0, 0, 0)); 

	virtual CColor				GetPixel(int p_iX, int p_iY) = 0;
	virtual CColor				GetPixel(const CPnt& p_xPnt)
								{
									return this->GetPixel(p_xPnt.x, p_xPnt.y);
								};

	virtual void				SetPixel(int p_iX, int p_iY, const CColor& p_xColor)	= 0;
	virtual void				SetPixel(const CPnt& p_xPnt, const CColor& p_xColor)
								{
									this->SetPixel(p_xPnt.x, p_xPnt.y, p_xColor);
								};

	virtual void				DrawLine(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor);
	virtual void				DrawLine(const CPnt& p_xPnt1, const CPnt& p_xPnt2, const CColor& p_xColor)
								{
									this->DrawLine(p_xPnt1.x, p_xPnt1.y, p_xPnt2.x, p_xPnt2.y, p_xColor);
								};

	virtual void				DrawRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor);
	virtual void				DrawRect(const CPnt& p_xPnt1, const CPnt& p_xPnt2, const CColor& p_xColor);
	virtual void				DrawRect(const CRct& p_xRect, const CColor& p_xColor);

	virtual void				FillRect(int p_iX1, int p_iY1, int p_iX2, int p_iY2, const CColor& p_xColor);
	virtual void				FillRect(const CPnt& p_xPnt1, const CPnt& p_xPnt2, const CColor& p_xColor);
	virtual void				FillRect(const CRct& p_xRect, const CColor& p_xColor);

	virtual CFontHandle			CreateFont(int p_iHeight = 12, 
										   const CStr& p_rsFaceName = "", 
										   CFont::Locale p_Locale = CFont::L_STANDARD, 
										   CFont::Pitch p_ePitch = CFont::PITCH_VARIABLE, 
										   CFont::Weight p_eWeight = CFont::W_NORMAL, 
										   int p_iFlags = 0) const;

	virtual CFontHandle			CreateFont(	const CStr& p_rsBitmapFilename, 
											const CStr& p_rsCharacterOrder, 
											int p_iSpaceWidth, 
											int p_iBaseLine,
											int p_iKerningBias = 0);

	virtual void				ReleaseFont(CFontHandle p_hHandle) const;
	virtual void				DrawText(CFontHandle p_xFontHandle, int p_iX, int p_iY, CStr p_sText, CColor p_xColor);
	virtual void				DrawText(CFontHandle p_xFontHandle, int p_iX, int p_iY, CStr p_sText, CColor p_xColor, const CRct& p_xClipRect);
	virtual void				DrawTextRect(CFontHandle p_xFontHandle, const CRct& p_rxRct, HorizontalTextAlignment p_eHTextAlign, 
											 VerticalTextAlignment p_eVTextAlign, CStr p_sText, CColor p_xColor);
	virtual void				DrawTextRect(CFontHandle p_xFontHandle, const CRct& p_rxRct, HorizontalTextAlignment p_eHTextAlign, 
											 VerticalTextAlignment p_eVTextAlign, CStr p_sText, CColor p_xColor, const CRct& p_xClipRect);
	virtual int					GetTextWidth(CFontHandle p_xFontHandle, CStr p_sText) const;

	const CFont::TFontMetrics&	GetFontMetrics(CFontHandle p_xFontHandle) const
								{
									assert (p_xFontHandle.m_pxFont != 0);
									return p_xFontHandle.m_pxFont->GetMetrics();
								};

	int							GetKerning(CFontHandle p_xFontHandle, unsigned long p_iFirstChar, unsigned long p_iSecondChar) const
								{
									assert (p_xFontHandle.m_pxFont != 0);
									return p_xFontHandle.m_pxFont->GetKerning(p_iFirstChar, p_iSecondChar);
								};

	virtual void				Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, bool p_bAlpha = false);
	virtual void				Blit(const CPnt& p_xPnt, const CBitmap* p_pxBitmap, const CRct& p_xClipRect, bool p_bAlpha = false);

	/// füllt die übergebene Bitmap mit Bilddaten aus diesem Context
	virtual bool				CaptureBitmap(const CPnt& p_xPnt, CBitmap& po_rxBitmap, const CSize& p_xSize = CSize(-1, -1));

protected:
	virtual void				DrawCharacter(CAlphaBitmapFontCharacter* p_pChar, int p_iX, int p_iY, CColor p_xColor, const CRct& p_xClipRect);
	virtual void				DrawTextAlphaBitmapFont(CFontHandle p_xFontHandle, int p_iX, int p_iY, CStr p_sText, CColor p_xColor, const CRct& p_xClipRect);
	virtual void				DrawTextBitmapFont(CFontHandle p_xFontHandle, int p_iX, int p_iY, CStr p_sText, const CRct& p_xClipRect);
}; 

} // namespace UILib

#endif	// ifndef UILIB_OUTPUTDEVICE_H_INCLUDED


#ifndef UILIB_STANDARDVISUALIZATION_H_INCLUDED
#define UILIB_STANDARDVISUALIZATION_H_INCLUDED

#include "visualization.h"
#include "outputdevice.h"
#include "paintcontext.h"

namespace UILib
{

///	Implementation of CVisualization; dispassionate and practical look. 
class CStandardVisualization : public CVisualization
{
public:
	///	Create Method
	static CVisualization*	Create(const COutputDevice* p_pxDev);
	virtual void			Destroy();


	/// liefert den Typ dieser Visualisierung als FourCC
	virtual CFourCC	GetType() const			{ return CFourCC("STND"); }

	void SetDefaultColors();

	virtual const CFont::TFontMetrics* GetFontMetrics(Font p_eFont) const;

	int				GetTextWidth(Font p_eFont, const CStr& p_rsText) const;

	/// liefert das Fonthandle für den Schrifttyp
	virtual CFontHandle	GetFont(Font p_eFont) const			{ return m_ahFonts[p_eFont]; }

	/// get frame width / height
	virtual CRct   GetFrameSize(FrameType p_eFrameType) const;

	virtual void	DrawText(const CPaintContext& p_rxCtx, Font p_eFont, const CPnt& p_rxPos, const CStr& p_rsText, int p_iTextProperty = TP_Normal);
	virtual void	DrawText(const CPaintContext& p_rxCtx, Font p_eFont, const CRct& p_rxRct, COutputDevice::HorizontalTextAlignment p_eHTextAlign, 
							 COutputDevice::VerticalTextAlignment p_eVTextAlign, const CStr& p_rsText, int p_iTextProperty = TP_Normal);
	virtual void	DrawText(const CPaintContext& p_rxCtx, CFontHandle p_hFont, const CPnt& p_rxPos, const CStr& p_rsText, int p_iTextProperty);
	virtual void	DrawText(const CPaintContext& p_rxCtx, CFontHandle p_hFont, const CRct& p_rxRct, COutputDevice::HorizontalTextAlignment p_eHTextAlign, 
							 COutputDevice::VerticalTextAlignment p_eVTextAlign, const CStr& p_rsText, int p_iTextProperty = TP_Normal);
	virtual	void	DrawBackground(const CPaintContext& p_rxCtx, const CRct& p_xRct, BackgroundType p_eType = BG_Normal);
	virtual void	DrawGroupBox(const CPaintContext& p_rxCtx, const CRct& p_xRct, const CStr& p_rsText);
	virtual void	DrawFrame(const CPaintContext& p_rxCtx, const CRct& p_xRct, FrameType p_eFrameType, bool p_bDisabled = false);
	virtual void	DrawCheckBox(const CPaintContext& p_rxCtx, const CPnt& p_pntPos, CheckBoxState p_eState, bool p_bPushed);
	virtual void	DrawRadioButton(const CPaintContext& p_rxCtx, const CPnt& p_pntPos, bool p_bSelected, bool p_bPushed);
	virtual void	DrawScrollBarButton(const CPaintContext& p_rxCtx, const CPnt& p_pntPos, ButtonType p_eButtonType, FrameType p_eFrameType);
	virtual void	DrawScrollBarKnob(const CPaintContext&  p_rxCtx, const CRct& p_xRct, bool p_bMoving);
	virtual void	DrawSliderKnob(const CPaintContext&  p_rxCtx, const CPnt& p_rxPos, Alignment p_eAlignment, bool p_bMoving, bool p_bDisabled);
	virtual void	DrawSlider(const CPaintContext&  p_rxCtx, const CRct& p_xRct, Alignment p_eAlignment);
	virtual void	DrawTextCursor(const CPaintContext& p_rxCtx, Font p_eFont, const CPnt& p_rxPos);
	virtual void	DrawSpinCtrlButton(const CPaintContext& p_rxCtx, const CPnt& p_pntPos, ButtonType p_eButtonType, FrameType p_eFrameType);
	virtual void	DrawComboBoxButton(const CPaintContext& p_rxCtx, const CRct& p_rxRct, FrameType p_eFrameType);
	virtual void	DrawCloseButton(const CPaintContext& p_rxCtx, const CRct& p_rxRct, FrameType p_eFrameType);
	virtual void	DrawTitleBar(const CPaintContext& p_rxCtx, const CPnt& p_rxPos, int p_iWidth, const CStr& p_rsText, bool p_bActive);

protected:
	CStandardVisualization(const COutputDevice* p_pxDevice);
	~CStandardVisualization();

	enum Colors
	{
		COL_Background,
		COL_BackgroundEditable,
		COL_BackgroundDisabledEditable,
		COL_BackgroundSelection,
		COL_BackgroundScrollBar,
		COL_BackgroundScrollBarClicked,
		COL_BackgroundToolTip,
		COL_Line,
		COL_Text,
		COL_TextDisabled,
		COL_TextSelected,
		COL_TitleBar,
		COL_TitleBarInactive,
		COL_TitleBarText,
		COL_FrameLight,
		COL_FrameMedium,
		COL_FrameShadow,
		COL_FrameDark,
		COL_NumColors
	};

	CColor			m_axColors[COL_NumColors];			///< aktuelles Farbschema
	CFontHandle		m_ahFonts[FONT_NumFonts];			///< default fonts

	CBitmap*		m_pxCheckMark;
	CBitmap*		m_pxDefaultCheckMark;

	CBitmap*		m_pxArrowUp;
	CBitmap*		m_pxArrowDown;
	CBitmap*		m_pxArrowRight;
	CBitmap*		m_pxArrowLeft;

	CBitmap*		m_pxRadioSelected;
	CBitmap*		m_pxRadioClear;
	CBitmap*		m_pxRadioSelectedBtnDown;
	CBitmap*		m_pxRadioClearBtnDown;

	CBitmap*		m_pxSpinArrowUp;
	CBitmap*		m_pxSpinArrowDown;
	CBitmap*		m_pxSpinArrowRight;
	CBitmap*		m_pxSpinArrowLeft;

	CBitmap*		m_pxClose;
};


} // namespace UILib

#endif // ifndef UILIB_STANDARDVISUALIZATION_H_INCLUDED


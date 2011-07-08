#ifndef UILIB_VISUALIZATION_H_INCLUDED
#define UILIB_VISUALIZATION_H_INCLUDED

#include "paintcontext.h"

namespace UILib
{

class CVisualization
{
public:

	enum State
	{
		STATE_Normal,
		STATE_Hovered, 
		STATE_Active,
		STATE_Disabled,
		STATE_NumStates
	};

	enum Font
	{
		FONT_Normal,
		FONT_Fixed,
		FONT_Small,
		FONT_TextBox,
		FONT_Button,
		FONT_ToolTip,
		FONT_Group,
		FONT_Title,
		FONT_NumFonts
	};


	enum TextProperty
	{
		TP_Normal    = 0,
		TP_Disabled  = 1<<0,
		TP_Selected  = 1<<1
	};

	enum FrameType
	{
		FT_ToolTip,
		FT_TextBox,
		FT_BtnUp,
		FT_BtnUpActive,
		FT_BtnUpHovered,
		FT_BtnDown,
		FT_WindowSizable,
		FT_WindowFixed,
		FT_GroupBox,
		FT_NumFrameTypes,
	};

	enum BackgroundType 
	{
		BG_Normal,
		BG_Button,
		BG_Editable,
		BG_DisabledEditable,
		BG_Selection,
		BG_Scrollbar,
		BG_ScrollbarClicked,
		BG_ToolTip
	};

	enum CheckBoxState
	{
		CB_Unchecked,
		CB_Checked,
		CB_Default
	};

	enum ButtonType
	{
		BT_UpArrow,
		BT_DownArrow,
		BT_LeftArrow,
		BT_RightArrow
	};

	enum Alignment
	{
		AL_Horizontal,
		AL_Vertical
	};

	/// die Metrik enthält allerlei Maßzahlen für diese Visualisierung
	struct TVisualizationMetrics
	{
		bool	m_bAllWindowsTransparent;			///< alle Fenster müssen transparent sein
		int		m_iTitleBarHeight;					///< Höhe einer Titelleiste
		CPnt	m_xTitleBarPos;						///< Position der Titelleiste; x = Entfernung rechter/linker Rand, y = Entfernung oberer Rand
		CSize	m_xCheckBoxSize;					///< Größe einer Checkbox
		CSize	m_xRadioButtonSize;					///< Größe eines Radio Button
		CSize	m_xScrollBarButtonSize;				///< Größe eines Scrollbar-Buttons; bestimmt auch Höhe / Breite des Scrollbars
		CSize	m_xHSliderKnobSize;					///< Größe eines horizontalen Slider-Griffs
		CSize	m_xVSliderKnobSize;					///< Größe eines vertikalen Slider-Griffs
		CSize	m_xButtonMinSize;					///< minimale Buttongröße
		CSize	m_xButtonMaxSize;					///< maximale Buttongröße
   		CPnt	m_xButtonDownTextDisplacement;		///< Versatz des Textes auf einem Button, wenn er gedrückt ist
		int		m_iSliderWidth;						///< Breite eines Sliders
		int		m_iCursorWidth;						///< Breite eines Textcursors
		CRct	m_xGroupBoxPadding;					///< Abstand zwischen CGroupBox und Innenraum (inkl. Rahmen)
		CSize	m_xStdButtonSize;					///< Standardgröße eines Buttons
		CSize	m_xVSpinBtnSize;					///< Größe eines vertikalen SpinControl-Buttons (Hoch- oder RunterButton)
		CSize	m_xHSpinBtnSize;					///< Größe eines horizontalen SpinControl-Buttons (Links- oder RechtsButton)
	};


	static CVisualization*	Create(const COutputDevice* p_pxDev)	{ return 0;}
	virtual void			Destroy() = 0;


	/// liefert den Typ dieser Visualisierung als FourCC
	virtual CFourCC	GetType() const	= 0;

	///	liefert Metrik eines Fonts
	virtual const CFont::TFontMetrics* GetFontMetrics(Font p_eFont) const = 0;

	/// liefert die Breite eines Textes in Pixeln
	virtual int		GetTextWidth(Font p_eFont, const CStr& p_rsText) const = 0;

	/// liefert die Breite eines Textes in Pixeln
	virtual int		GetTextWidth(CFontHandle p_hFont, const CStr& p_rsText)
    {
        return m_pxDevice->GetTextWidth(p_hFont, p_rsText);
    }

	/// liefert das Fonthandle für den Schrifttyp
	virtual CFontHandle	GetFont(Font p_eFont) const = 0;

	///	liefert Metrik dieser Visualisierung
	virtual const TVisualizationMetrics* GetMetrics() const		{ return &m_xVizMetrics; }


	/// liefert Breiten eines Rahmentyps
	virtual CRct GetFrameSize(FrameType p_eFrameType) const = 0;


	/// liefert Summe der Rahmenbreite (left + right, top + bottom) 
	CSize GetFrameSizeTotal(FrameType p_eFrameType)	const
	{ 
		CRct r = GetFrameSize(p_eFrameType);
		return CSize(r.left + r.right, r.top + r.bottom); 
	}

	/// zeichnet Text
	virtual void	DrawText(const CPaintContext& p_rxCtx, Font p_eFont, const CPnt& p_rxPos, const CStr& p_rsText, int p_iTextProperty = TP_Normal) = 0;

	/// zeichnet Text ausgerichtet in ein Rechteck
	virtual void	DrawText(const CPaintContext& p_rxCtx, Font p_eFont, const CRct& p_rxRct, COutputDevice::HorizontalTextAlignment p_eHTextAlign, 
							 COutputDevice::VerticalTextAlignment p_eVTextAlign, const CStr& p_rsText, int p_iTextProperty = TP_Normal) = 0;

	/// zeichnet Text
	virtual void	DrawText(const CPaintContext& p_rxCtx, CFontHandle p_hFont, const CPnt& p_rxPos, const CStr& p_rsText, int p_iTextProperty) = 0;

	/// zeichnet Text ausgerichtet in ein Rechteck
	virtual void	DrawText(const CPaintContext& p_rxCtx, CFontHandle p_hFont, const CRct& p_rxRct, COutputDevice::HorizontalTextAlignment p_eHTextAlign, 
							 COutputDevice::VerticalTextAlignment p_eVTextAlign, const CStr& p_rsText, int p_iTextProperty = TP_Normal) = 0;

	///	füllt einen rechteckigen Bereich mit einem Hintergrundmuster
	virtual	void	DrawBackground(const CPaintContext& p_rxCtx, const CRct& p_xRct, BackgroundType p_eType = BG_Normal) = 0;

	/// zeichnet einen Groupbox-Rahmen mit Überschrift
	virtual	void	DrawGroupBox(const CPaintContext& p_rxCtx, const CRct& p_xRct, const CStr& p_rsText) = 0;

	/// zeichnet einen Rahmen
	virtual void	DrawFrame(const CPaintContext& p_rxCtx, const CRct& p_xRct, FrameType p_eFrameType, bool p_bDisabled = false) = 0;


	/// zeichnet eine Checkbox
	virtual void	DrawCheckBox(const CPaintContext& p_rxCtx, const CPnt& p_pntPos, CheckBoxState p_eState, bool p_bPushed) = 0;


	/// zeichnet einen Radiobutton
	virtual void	DrawRadioButton(const CPaintContext& p_rxCtx, const CPnt& p_pntPos, bool p_bSelected, bool p_bPushed) = 0;


	/// zeichnet einen Scrollbar-Knopf
	virtual void	DrawScrollBarButton(const CPaintContext& p_rxCtx, const CPnt& p_pntPos, ButtonType p_eButtonType, FrameType p_eFrameType) = 0;


	/// zeichnet den "Griff" eines Scrollbars (dieses Ding, das sich hoch und runter bewegt und dass man ziehen kann)
	virtual void	DrawScrollBarKnob(const CPaintContext&  p_rxCtx, const CRct& p_xRct, bool p_bMoving) = 0;


	/// zeichnet den "Griff" eines Sliders
	virtual void	DrawSliderKnob(const CPaintContext&  p_rxCtx, const CPnt& p_rxPos, Alignment p_eAlignment, bool p_bMoving, bool p_bDisabled) = 0;

	/// zeichnet einen Slider
	virtual void	DrawSlider(const CPaintContext&  p_rxCtx, const CRct& p_xRct, Alignment p_eAlignment) = 0;

	/// zeichnet einen TextCursor
	virtual void	DrawTextCursor(const CPaintContext& p_rxCtx, Font p_eFont, const CPnt& p_rxPos) = 0;

	/// zeichnet den Button eines Spincontrols
	virtual void	DrawSpinCtrlButton(const CPaintContext& p_rxCtx, const CPnt& p_pntPos, ButtonType p_eButtonType, FrameType p_eFrameType) = 0;

	/// zeichnet den Button einer ComboBox
	virtual void	DrawComboBoxButton(const CPaintContext& p_rxCtx, const CRct& p_rxRct, FrameType p_eFrameType) = 0;

	/// zeichnet den Close-Button eines Dialoges
	virtual void	DrawCloseButton(const CPaintContext& p_rxCtx, const CRct& p_rxRct, FrameType p_eFrameType) = 0;

	/// zeichnet die Titelleiste eines Fensters
	virtual void	DrawTitleBar(const CPaintContext& p_rxCtx, const CPnt& p_rxPos, int p_iWidth, const CStr& p_rsText, bool p_bActive) = 0;


protected:

	CVisualization(const COutputDevice* p_pxDevice)	: m_pxDevice(p_pxDevice) {};
	virtual ~CVisualization()	{}

	TVisualizationMetrics		m_xVizMetrics;		///< Metrik
	const COutputDevice*		m_pxDevice;			///< unser Device (um die Fonts wieder freizugeben)
};

} // namespace UILib

#endif // ifndef UILIB_VISUALIZATION_H_INCLUDED


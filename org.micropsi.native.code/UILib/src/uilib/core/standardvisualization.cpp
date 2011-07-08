#include "stdafx.h"
#include "uilib/core/standardvisualization.h"
#include "uilib/core/visualizationfactory.h"

namespace UILib 
{

//---------------------------------------------------------------------------------------------------------------------
CVisualization*	CStandardVisualization::Create(const COutputDevice* p_pxDev)
{ 
	return new CStandardVisualization(p_pxDev);
}


//---------------------------------------------------------------------------------------------------------------------
void CStandardVisualization::Destroy()		
{ 
	delete this; 
}


//---------------------------------------------------------------------------------------------------------------------
CStandardVisualization::CStandardVisualization(const COutputDevice* p_pxDevice) : CVisualization(p_pxDevice)
{
	m_ahFonts[FONT_Normal]	= p_pxDevice->CreateFont(14, "", CFont::L_STANDARD, CFont::PITCH_VARIABLE, CFont::W_NORMAL, CFont::F_NONE);
	m_ahFonts[FONT_Fixed]	= p_pxDevice->CreateFont(14, "", CFont::L_STANDARD, CFont::PITCH_FIXED, CFont::W_NORMAL, CFont::F_NONE);
	m_ahFonts[FONT_TextBox] = m_ahFonts[FONT_Normal];
	m_ahFonts[FONT_Small]	= m_ahFonts[FONT_Normal];
	m_ahFonts[FONT_Button]	= m_ahFonts[FONT_Normal];
	m_ahFonts[FONT_ToolTip]	= m_ahFonts[FONT_Normal];
	m_ahFonts[FONT_Group]	= m_ahFonts[FONT_Normal];
	m_ahFonts[FONT_Title]	= m_ahFonts[FONT_Normal];

	SetDefaultColors();

	// Bitmaps
	m_pxCheckMark			 = new CBitmap("std_check.png");
	m_pxDefaultCheckMark	 = new CBitmap("std_checkdefault.png");
	assert(m_pxCheckMark->GetSize() == m_pxDefaultCheckMark->GetSize());

	m_pxArrowUp				 = new CBitmap("std_scrollarrowup.png");
	m_pxArrowDown			 = new CBitmap("std_scrollarrowdown.png");
	m_pxArrowRight			 = new CBitmap("std_scrollarrowright.png");
	m_pxArrowLeft			 = new CBitmap("std_scrollarrowleft.png");
	assert(m_pxArrowUp->GetSize() == m_pxArrowDown->GetSize() &&
			 m_pxArrowRight->GetSize() == m_pxArrowLeft->GetSize() &&
			 m_pxArrowUp->GetSize() == m_pxArrowRight->GetSize());


	m_pxRadioSelected		 = new CBitmap("std_radiobuttonselected.png");
	m_pxRadioClear			 = new CBitmap("std_radiobuttonclear.png");
	m_pxRadioSelectedBtnDown = new CBitmap("std_radiobuttonselectedanddown.png");
	m_pxRadioClearBtnDown	 = new CBitmap("std_radiobuttonclearanddown.png");
	assert(m_pxRadioSelected->GetSize() == m_pxRadioClear->GetSize() &&
			 m_pxRadioSelectedBtnDown->GetSize() == m_pxRadioClearBtnDown->GetSize() &&
			 m_pxRadioSelected->GetSize() == m_pxRadioSelectedBtnDown->GetSize());


	m_pxSpinArrowUp			 = new CBitmap("std_spinbuttonup.png");
	m_pxSpinArrowDown		 = new CBitmap("std_spinbuttondown.png");
	m_pxSpinArrowRight		 = new CBitmap("std_spinbuttonright.png");
	m_pxSpinArrowLeft		 = new CBitmap("std_spinbuttonleft.png");
	assert(m_pxSpinArrowUp->GetSize() == m_pxSpinArrowDown->GetSize());
	assert(m_pxSpinArrowRight->GetSize() == m_pxSpinArrowLeft->GetSize());

	m_pxClose				 = new CBitmap("std_close.png");

	// Metrics

	m_xVizMetrics.m_bAllWindowsTransparent		= false;
	m_xVizMetrics.m_iTitleBarHeight				= 16;
	m_xVizMetrics.m_xTitleBarPos				= CPnt(2, 2);
	m_xVizMetrics.m_xCheckBoxSize				= m_pxCheckMark->GetSize() + GetFrameSizeTotal(FT_WindowFixed);
	m_xVizMetrics.m_xRadioButtonSize			= m_pxRadioSelected->GetSize();
	m_xVizMetrics.m_xScrollBarButtonSize		= m_pxArrowUp->GetSize() + GetFrameSizeTotal(FT_WindowFixed);
	m_xVizMetrics.m_xHSliderKnobSize			= CSize(10, 17);
	m_xVizMetrics.m_xVSliderKnobSize			= CSize(17, 10);
	m_xVizMetrics.m_xButtonMinSize				= CSize(0, 0);
	m_xVizMetrics.m_xButtonMaxSize				= CSize(-1, -1);	
	m_xVizMetrics.m_xButtonDownTextDisplacement = CPnt(1,1);
	m_xVizMetrics.m_iSliderWidth				= 4;
	m_xVizMetrics.m_iCursorWidth				= 5;
	m_xVizMetrics.m_xGroupBoxPadding			= CRct(5,20,5,5);					
	m_xVizMetrics.m_xStdButtonSize				= CSize(90,21);
	m_xVizMetrics.m_xHSpinBtnSize				= m_pxSpinArrowLeft->GetSize() + GetFrameSizeTotal(FT_WindowFixed);
	m_xVizMetrics.m_xVSpinBtnSize				= m_pxSpinArrowUp->GetSize() + GetFrameSizeTotal(FT_WindowFixed);
}


//---------------------------------------------------------------------------------------------------------------------
CStandardVisualization::~CStandardVisualization()
{
	m_pxDevice->ReleaseFont(m_ahFonts[FONT_Normal]);
	m_pxDevice->ReleaseFont(m_ahFonts[FONT_Fixed]);

	delete m_pxCheckMark;
	delete m_pxDefaultCheckMark;

	delete m_pxArrowUp;
	delete m_pxArrowDown;
	delete m_pxArrowRight;
	delete m_pxArrowLeft;

	delete m_pxRadioSelected;
	delete m_pxRadioClear;
	delete m_pxRadioSelectedBtnDown;
	delete m_pxRadioClearBtnDown;

	delete m_pxSpinArrowUp;			 
	delete m_pxSpinArrowDown;		 
	delete m_pxSpinArrowRight;		 
	delete m_pxSpinArrowLeft;		

	delete m_pxClose;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::SetDefaultColors()
{
	m_axColors[COL_Background]					= CColor(200, 200, 200, 255);
	m_axColors[COL_BackgroundEditable]			= CColor(255, 255, 255, 255);
	m_axColors[COL_BackgroundDisabledEditable]  = CColor(200, 200, 200, 255);
	m_axColors[COL_BackgroundScrollBar]			= CColor(230, 230, 230, 255);
	m_axColors[COL_BackgroundScrollBarClicked]	= CColor(  0,   0,   0, 255);
	m_axColors[COL_BackgroundSelection]			= CColor( 50,  50, 160, 255);
	m_axColors[COL_BackgroundToolTip]			= CColor(255, 255, 225, 255);

	m_axColors[COL_Line]			= CColor(  0,   0,   0, 255);
	m_axColors[COL_Text]			= CColor(  0,   0,   0, 255);
	m_axColors[COL_TextDisabled]	= CColor(140, 140, 140, 255);
	m_axColors[COL_TextSelected]	= CColor(255, 255, 255, 255);

	m_axColors[COL_TitleBar]		= CColor( 50,  50, 160, 255);
	m_axColors[COL_TitleBarInactive]= CColor( 50,  50,  50, 255);
	m_axColors[COL_TitleBarText]	= CColor(255, 255, 255, 255);

	m_axColors[COL_FrameLight]	= CColor(240, 240, 240, 255);
	m_axColors[COL_FrameMedium]	= CColor(210, 210, 200, 255);
	m_axColors[COL_FrameShadow] = CColor(150, 150, 150, 255);	
	m_axColors[COL_FrameDark]	= CColor( 70,  70,  70, 255); 
}


//---------------------------------------------------------------------------------------------------------------------
const 
CFont::TFontMetrics* CStandardVisualization::GetFontMetrics(Font p_eFont) const
{
	return &(m_pxDevice->GetFontMetrics(m_ahFonts[p_eFont]));
}


//---------------------------------------------------------------------------------------------------------------------
int 
CStandardVisualization::GetTextWidth(Font p_eFont, const CStr& p_rsText) const
{
	return m_pxDevice->GetTextWidth(m_ahFonts[p_eFont], p_rsText);
}


//---------------------------------------------------------------------------------------------------------------------
CRct 
CStandardVisualization::GetFrameSize(FrameType p_eFrameType) const
{
	return CRct(2,2,2,2);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawText(const CPaintContext& p_rxCtx, Font p_eFont, const CPnt& p_rxPos, const CStr& p_rsText, int p_iTextProperty)
{
	DrawText(p_rxCtx, m_ahFonts[p_eFont], p_rxPos, p_rsText, p_iTextProperty);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawText(const CPaintContext& p_rxCtx, CFontHandle p_hFont, const CPnt& p_rxPos, const CStr& p_rsText, int p_iTextProperty)
{
	CColor xColor;

	if(p_iTextProperty & TP_Disabled)			{ xColor = m_axColors[COL_TextDisabled]; }
	else if(p_iTextProperty & TP_Selected)		{ xColor = m_axColors[COL_TextSelected]; } 
	else										{ xColor = m_axColors[COL_Text]; };

	if(p_iTextProperty & TP_Selected)
	{
		const CFont::TFontMetrics& fm = p_rxCtx.GetDevice()->GetFontMetrics(p_hFont);

		CRct r = CRct(p_rxPos.x, p_rxPos.y - fm.m_iAscent, 
			p_rxPos.x + m_pxDevice->GetTextWidth(p_hFont, p_rsText), p_rxPos.y + fm.m_iDescent);

		this->DrawBackground(p_rxCtx, r, BG_Selection);
	}

	p_rxCtx.DrawText(p_hFont, p_rxPos, p_rsText, xColor);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawText(const CPaintContext& p_rxCtx, Font p_eFont, const CRct& p_rxRct, COutputDevice::HorizontalTextAlignment p_eHTextAlign, 
								 COutputDevice::VerticalTextAlignment p_eVTextAlign, const CStr& p_rsText, int p_iTextProperty)
{
	DrawText(p_rxCtx, m_ahFonts[p_eFont], p_rxRct, p_eHTextAlign, p_eVTextAlign, p_rsText, p_iTextProperty);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawText(const CPaintContext& p_rxCtx, CFontHandle p_hFont, const CRct& p_rxRct, COutputDevice::HorizontalTextAlignment p_eHTextAlign, 
								 COutputDevice::VerticalTextAlignment p_eVTextAlign, const CStr& p_rsText, int p_iTextProperty)
{
	CColor xColor;

	if(p_iTextProperty & TP_Disabled)			{ xColor = m_axColors[COL_TextDisabled]; }
	else if(p_iTextProperty & TP_Selected)		{ xColor = m_axColors[COL_TextSelected]; } 
	else										{ xColor = m_axColors[COL_Text]; };

	if(p_iTextProperty & TP_Selected)
	{
		this->DrawBackground(p_rxCtx, p_rxRct, BG_Selection);
	}

	p_rxCtx.DrawText(p_hFont, p_rxRct, p_eHTextAlign, p_eVTextAlign, p_rsText, xColor);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawBackground(const CPaintContext& p_rxCtx, const CRct& p_xRct, BackgroundType p_eType)
{
	Colors c;
	switch(p_eType) {
		case BG_Editable:
			c = COL_BackgroundEditable;
			break;
		case BG_DisabledEditable:
			c = COL_BackgroundDisabledEditable;
			break;
		case BG_Selection:
			c = COL_BackgroundSelection;
			break;
		case BG_Scrollbar:
			c = COL_BackgroundScrollBar;
			break;
		case BG_ScrollbarClicked:
			c = COL_BackgroundScrollBarClicked;
			break;
		case BG_ToolTip:
			c = COL_BackgroundToolTip;
			break;
		case BG_Normal:	
		case BG_Button:
		default:			
			c = COL_Background;
	}

	p_rxCtx.FillRect(p_xRct, m_axColors[c]);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawGroupBox(const CPaintContext& p_rxCtx, const CRct& p_xRct, const CStr& p_rsText)
{
	const CFont::TFontMetrics& fm = p_rxCtx.GetDevice()->GetFontMetrics(m_ahFonts[FONT_Group]);
	int iTextHeight = fm.m_iHeight;

	this->DrawBackground(p_rxCtx,p_xRct, BG_Normal);
	p_rxCtx.DrawLine(p_xRct.left,p_xRct.top+iTextHeight/2,				// top
					 p_xRct.right-2,
					 p_xRct.top+iTextHeight/2,
					 m_axColors[COL_FrameDark]);
	p_rxCtx.DrawLine(p_xRct.left,										// left
					 p_xRct.top+iTextHeight/2,
					 p_xRct.left,
					 p_xRct.bottom-2,
					 m_axColors[COL_FrameDark]);
	p_rxCtx.DrawLine(p_xRct.right-2,									// right
					 p_xRct.top+iTextHeight/2,
					 p_xRct.right-2,
					 p_xRct.bottom-2,
					 m_axColors[COL_FrameDark]);
	p_rxCtx.DrawLine(p_xRct.left,										// bottom
					 p_xRct.bottom-2,
					 p_xRct.right-2,
					 p_xRct.bottom-2,
					 m_axColors[COL_FrameDark]);
	p_rxCtx.DrawLine(p_xRct.left+1,										// top
					 p_xRct.top+iTextHeight/2+1,
					 p_xRct.right-3,p_xRct.top+iTextHeight/2+1,
					 m_axColors[COL_FrameLight]);
	p_rxCtx.DrawLine(p_xRct.left+1,										// left
					 p_xRct.top+iTextHeight/2+1,
					 p_xRct.left+1,
					 p_xRct.bottom-3,
					 m_axColors[COL_FrameLight]);
	p_rxCtx.DrawLine(p_xRct.right-1,									// right
					 p_xRct.top+iTextHeight/2+1,
					 p_xRct.right-1,
					 p_xRct.bottom-1,
					 m_axColors[COL_FrameLight]);
	p_rxCtx.DrawLine(p_xRct.left+1,										// bottom
					 p_xRct.bottom-1,
					 p_xRct.right-1,
					 p_xRct.bottom-1,
					 m_axColors[COL_FrameLight]);

	int iTextWidth = p_rxCtx.GetDevice()->GetTextWidth(m_ahFonts[FONT_Group], p_rsText);
	CRct r = CRct(10, 0, min(p_xRct.right - 10, 10 + iTextWidth), iTextHeight);
	if(!r.IsEmpty())
	{
		this->DrawBackground(p_rxCtx, r, BG_Normal);
		this->DrawText(p_rxCtx, FONT_Group, r, COutputDevice::TA_Left, COutputDevice::TA_VCenter, p_rsText, TP_Normal);
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawFrame(const CPaintContext& p_rxCtx, const CRct& p_xRct, FrameType p_eFrameType, bool p_bDisabled)
{
	switch(p_eFrameType)
	{
		case FT_ToolTip:
			p_rxCtx.DrawRect(p_xRct,m_axColors[COL_Line]);
			break;

		case FT_BtnDown:
			p_rxCtx.DrawRect(p_xRct, m_axColors[COL_Line]);
			p_rxCtx.DrawRect(CRct(p_xRct.left+1, p_xRct.top+1, p_xRct.right-1, p_xRct.bottom-1), m_axColors[COL_FrameShadow]);
			break;

		case FT_BtnUpActive:
			p_rxCtx.DrawRect(p_xRct, m_axColors[COL_Line]);
			p_rxCtx.DrawLine(p_xRct.left+1, p_xRct.top+1, p_xRct.left+1, p_xRct.bottom-2, m_axColors[COL_FrameLight]);
			p_rxCtx.DrawLine(p_xRct.left+1, p_xRct.top+1, p_xRct.right-2, p_xRct.top+1, m_axColors[COL_FrameLight]);
			p_rxCtx.DrawLine(p_xRct.right-2, p_xRct.top+1, p_xRct.right-2, p_xRct.bottom-2, m_axColors[COL_FrameShadow]);
			p_rxCtx.DrawLine(p_xRct.left+1, p_xRct.bottom-2, p_xRct.right-2, p_xRct.bottom-2, m_axColors[COL_FrameShadow]);
			break;

		case FT_WindowSizable:
		case FT_WindowFixed:
		case FT_BtnUp:
			p_rxCtx.DrawLine(p_xRct.left, p_xRct.top, p_xRct.left, p_xRct.bottom-1, m_axColors[COL_FrameLight]);
			p_rxCtx.DrawLine(p_xRct.left, p_xRct.top, p_xRct.right-1, p_xRct.top, m_axColors[COL_FrameLight]);
			p_rxCtx.DrawLine(p_xRct.left+1, p_xRct.top+1, p_xRct.left+1, p_xRct.bottom-2, m_axColors[COL_FrameMedium]);
			p_rxCtx.DrawLine(p_xRct.left+1, p_xRct.top+1, p_xRct.right-2, p_xRct.top+1, m_axColors[COL_FrameMedium]);

			p_rxCtx.DrawLine(p_xRct.right-2, p_xRct.top+1, p_xRct.right-2, p_xRct.bottom-2, m_axColors[COL_FrameShadow]);
			p_rxCtx.DrawLine(p_xRct.left+1, p_xRct.bottom-2, p_xRct.right-2, p_xRct.bottom-2, m_axColors[COL_FrameShadow]);
			p_rxCtx.DrawLine(p_xRct.right-1, p_xRct.top, p_xRct.right-1, p_xRct.bottom-1, m_axColors[COL_FrameDark]);
			p_rxCtx.DrawLine(p_xRct.left, p_xRct.bottom-1, p_xRct.right-1, p_xRct.bottom-1, m_axColors[COL_FrameDark]);
			break;

		case FT_TextBox:
			p_rxCtx.DrawLine(p_xRct.left, p_xRct.top, p_xRct.left, p_xRct.bottom-1, m_axColors[COL_FrameShadow]);
			p_rxCtx.DrawLine(p_xRct.left, p_xRct.top, p_xRct.right-1, p_xRct.top, m_axColors[COL_FrameShadow]);
			p_rxCtx.DrawLine(p_xRct.left+1, p_xRct.top+1, p_xRct.left+1, p_xRct.bottom-2, m_axColors[COL_FrameDark]);
			p_rxCtx.DrawLine(p_xRct.left+1, p_xRct.top+1, p_xRct.right-2, p_xRct.top+1, m_axColors[COL_FrameDark]);

			p_rxCtx.DrawLine(p_xRct.right-2, p_xRct.top+1, p_xRct.right-2, p_xRct.bottom-2, m_axColors[COL_FrameMedium]);
			p_rxCtx.DrawLine(p_xRct.left+1, p_xRct.bottom-2, p_xRct.right-2, p_xRct.bottom-2, m_axColors[COL_FrameMedium]);
			p_rxCtx.DrawLine(p_xRct.right-1, p_xRct.top, p_xRct.right-1, p_xRct.bottom-1, m_axColors[COL_FrameLight]);
			p_rxCtx.DrawLine(p_xRct.left, p_xRct.bottom-1, p_xRct.right-1, p_xRct.bottom-1, m_axColors[COL_FrameLight]);
			break;

		case FT_GroupBox:
			p_rxCtx.DrawRect(CRct(p_xRct.left, p_xRct.top, p_xRct.right-1, p_xRct.bottom-1), m_axColors[COL_FrameDark]);
			p_rxCtx.DrawLine(p_xRct.left+1, p_xRct.top+1, p_xRct.right-3, p_xRct.top+1, m_axColors[COL_FrameLight]);
			p_rxCtx.DrawLine(p_xRct.left+1, p_xRct.top+1, p_xRct.left+1, p_xRct.bottom-3, m_axColors[COL_FrameLight]);
			p_rxCtx.DrawLine(p_xRct.right-1, p_xRct.top, p_xRct.right-1, p_xRct.bottom-1, m_axColors[COL_FrameLight]);
			p_rxCtx.DrawLine(p_xRct.left, p_xRct.bottom-1, p_xRct.right-1, p_xRct.bottom-1, m_axColors[COL_FrameLight]);
			break;
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawCheckBox(const CPaintContext& p_rxCtx, const CPnt& p_pntPos, CheckBoxState p_eState, bool p_bPushed)
{
	// frame

	CRct rctOutline = CRct(0, 0, m_xVizMetrics.m_xCheckBoxSize.cx, m_xVizMetrics.m_xCheckBoxSize.cy) + p_pntPos;
	this->DrawFrame(p_rxCtx, rctOutline, FT_TextBox);
	CRct rctInterior = 	CRct(GetFrameSize(FT_WindowFixed).left, GetFrameSize(FT_WindowFixed).top, 
							m_xVizMetrics.m_xCheckBoxSize.cx - GetFrameSize(FT_WindowFixed).right, 
							m_xVizMetrics.m_xCheckBoxSize.cy - GetFrameSize(FT_WindowFixed).bottom);
	rctInterior += p_pntPos;

	// background

	if(p_bPushed)
	{
		this->DrawBackground(p_rxCtx, rctInterior, BG_DisabledEditable);
	}
	else
	{
		this->DrawBackground(p_rxCtx, rctInterior, BG_Editable);
	}

	// checkmark

	const CBitmap* pxBmp = 0;
	switch(p_eState) 
	{
	case CB_Checked: 
		pxBmp = m_pxCheckMark; break;
	case CB_Default:
		pxBmp = m_pxDefaultCheckMark; break;
	default:
		pxBmp = 0;
	}
	if(pxBmp)
	{
		p_rxCtx.Blit(CPnt(rctInterior.left, rctInterior.top), pxBmp, true); 
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawRadioButton(const CPaintContext& p_rxCtx, const CPnt& p_pntPos, bool p_bSelected, bool p_bPushed)
{
	CBitmap* pxBmp;
	if(p_bSelected)
	{
		if(p_bPushed)
		{
			pxBmp = m_pxRadioSelectedBtnDown;
		}
		else
		{
			pxBmp = m_pxRadioSelected;
		}
	}
	else
	{
		if(p_bPushed)
		{
			pxBmp = m_pxRadioClearBtnDown;
		}
		else
		{
			pxBmp = m_pxRadioClear;
		}
	}

	p_rxCtx.Blit(CPnt(p_pntPos.x, p_pntPos.y), pxBmp, true); 
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawScrollBarButton(const CPaintContext& p_rxCtx, const CPnt& p_pntPos, ButtonType p_eButtonType, FrameType p_eFrameType)
{
	// frame

	CRct rctOutline = CRct(0, 0, m_xVizMetrics.m_xScrollBarButtonSize.cx, m_xVizMetrics.m_xScrollBarButtonSize.cy) + p_pntPos;
	this->DrawFrame(p_rxCtx, rctOutline, p_eFrameType);
	CRct rctInterior = 	CRct(GetFrameSize(FT_WindowFixed).left, GetFrameSize(FT_WindowFixed).top, 
							m_xVizMetrics.m_xScrollBarButtonSize.cx - GetFrameSize(FT_WindowFixed).right, 
							m_xVizMetrics.m_xScrollBarButtonSize.cy - GetFrameSize(FT_WindowFixed).bottom);
	rctInterior += p_pntPos;

	// background

	this->DrawBackground(p_rxCtx, rctInterior, BG_Normal);

	// Arrow

	const CBitmap* pxBmp = 0;
	switch(p_eButtonType) 
	{
	case BT_UpArrow: 
		pxBmp = m_pxArrowUp; break;
	case BT_DownArrow: 
		pxBmp = m_pxArrowDown; break;
	case BT_LeftArrow: 
		pxBmp = m_pxArrowLeft; break;
	case BT_RightArrow: 
		pxBmp = m_pxArrowRight; break;
	default:
		pxBmp = 0;
	}
	if(pxBmp)
	{
		p_rxCtx.Blit(p_pntPos + CPnt(GetFrameSize(FT_WindowFixed).left, GetFrameSize(FT_WindowFixed).top), pxBmp, true); 
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawScrollBarKnob(const CPaintContext&  p_rxCtx, const CRct& p_xRct, bool p_bMoving)
{
	this->DrawFrame(p_rxCtx, p_xRct, FT_BtnUp);
	CRct rctInterior = 	CRct(p_xRct.left + GetFrameSize(FT_WindowFixed).left,
							 p_xRct.top +  GetFrameSize(FT_WindowFixed).top,
							 p_xRct.right - GetFrameSize(FT_WindowFixed).right,
							 p_xRct.bottom -  GetFrameSize(FT_WindowFixed).bottom);
	this->DrawBackground(p_rxCtx, rctInterior, BG_Normal);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawSliderKnob(const CPaintContext&  p_rxCtx, const CPnt& p_rxPos, Alignment p_eAlignment, bool p_bMoving, bool p_bDisabled)
{
	CRct xRct;
	if(p_eAlignment == AL_Horizontal)
	{
		xRct = CRct(p_rxPos, CPnt(p_rxPos.x + m_xVizMetrics.m_xHSliderKnobSize.cx, p_rxPos.y + m_xVizMetrics.m_xHSliderKnobSize.cy));
	}
	else
	{
		xRct = CRct(p_rxPos, CPnt(p_rxPos.x + m_xVizMetrics.m_xVSliderKnobSize.cx, p_rxPos.y + m_xVizMetrics.m_xVSliderKnobSize.cy));
	}

	this->DrawFrame(p_rxCtx, xRct, FT_BtnUp);
	CRct rctInterior = 	CRct(xRct.left + GetFrameSize(FT_WindowFixed).left,
							 xRct.top +  GetFrameSize(FT_WindowFixed).top,
							 xRct.right - GetFrameSize(FT_WindowFixed).right,
							 xRct.bottom -  GetFrameSize(FT_WindowFixed).bottom);
	if(p_bDisabled)
	{
		this->DrawBackground(p_rxCtx, rctInterior, BG_Scrollbar);
	}
	else
	{
		this->DrawBackground(p_rxCtx, rctInterior, BG_Normal);
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawSlider(const CPaintContext&  p_rxCtx, const CRct& p_xRct, Alignment p_eAlignment)
{
	CRct r = p_xRct;

	if(p_eAlignment == AL_Horizontal)
	{
		r.top	 = p_xRct.top + ((p_xRct.Height() - m_xVizMetrics.m_iSliderWidth ) / 2);
		r.bottom = r.top + m_xVizMetrics.m_iSliderWidth;
	}
	else
	{
		r.left	= p_xRct.left + ((p_xRct.Width() - m_xVizMetrics.m_iSliderWidth ) / 2);
		r.right = r.left + m_xVizMetrics.m_iSliderWidth;
	}

	this->DrawFrame(p_rxCtx, r, FT_TextBox);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CStandardVisualization::DrawTextCursor(const CPaintContext& p_rxCtx, Font p_eFont, const CPnt& p_rxPos)
{
	int iHeight = p_rxCtx.GetDevice()->GetFontMetrics(m_ahFonts[p_eFont]).m_iHeight;

	CPnt xTop = p_rxPos + CPnt(0, -iHeight);
	p_rxCtx.DrawLine(p_rxPos, xTop, m_axColors[COL_Line]);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Zeichnet einen Spin control Button

	\param p_rxCtx			Paint context
	\param p_pntPos			Position 
	\param p_eButtonType	Buttontyp (icon)
	\param p_eFrameType		Rahmentyp (normal, pushed...)
*/	
void 
CStandardVisualization::DrawSpinCtrlButton(const CPaintContext& p_rxCtx, const CPnt& p_pntPos, ButtonType p_eButtonType, FrameType p_eFrameType)
{
	// Rahmen

	CSize xButtonSize;
	if(p_eButtonType == BT_UpArrow || p_eButtonType == BT_DownArrow)
	{
		xButtonSize = m_xVizMetrics.m_xVSpinBtnSize;
	}
	else
	{
		xButtonSize = m_xVizMetrics.m_xHSpinBtnSize;
	};

	CRct rctOutline = CRct(0, 0, xButtonSize.cx, xButtonSize.cy) + p_pntPos;
	this->DrawFrame(p_rxCtx, rctOutline, p_eFrameType);
	CRct rctInterior = 	CRct(GetFrameSize(FT_WindowFixed).left, GetFrameSize(FT_WindowFixed).top, 
							xButtonSize.cx - GetFrameSize(FT_WindowFixed).right, 
							xButtonSize.cy - GetFrameSize(FT_WindowFixed).bottom);
	rctInterior += p_pntPos;

	// Hintergrund

	this->DrawBackground(p_rxCtx, rctInterior, BG_Normal);

	// Pfeil

	const CBitmap* pxBmp = 0;
	switch(p_eButtonType) 
	{
	case BT_UpArrow: 
		pxBmp = m_pxSpinArrowUp; break;
	case BT_DownArrow: 
		pxBmp = m_pxSpinArrowDown; break;
	case BT_LeftArrow: 
		pxBmp = m_pxSpinArrowLeft; break;
	case BT_RightArrow: 
		pxBmp = m_pxSpinArrowRight; break;
	default:
		pxBmp = 0;
	}

	if(pxBmp)
	{
		p_rxCtx.Blit(p_pntPos + CPnt(GetFrameSize(FT_WindowFixed).left, GetFrameSize(FT_WindowFixed).top), pxBmp, true); 
	}
}


//---------------------------------------------------------------------------------------------------------------------
/// zeichnet den Button einer ComboBox
void	
CStandardVisualization::DrawComboBoxButton(const CPaintContext& p_rxCtx, const CRct& p_rxRct, FrameType p_eFrameType)
{
	this->DrawFrame(p_rxCtx, p_rxRct, p_eFrameType);
	CRct xInterior = 	p_rxRct;
	xInterior.left		+= GetFrameSize(FT_WindowFixed).left;
	xInterior.right		-= GetFrameSize(FT_WindowFixed).right;
	xInterior.top		+= GetFrameSize(FT_WindowFixed).top;
	xInterior.bottom	-= GetFrameSize(FT_WindowFixed).bottom;
	this->DrawBackground(p_rxCtx, xInterior, BG_Normal);

	CPnt xPos = CPnt((p_rxRct.right-m_pxArrowDown->GetWidth())/2,(p_rxRct.bottom-m_pxArrowDown->GetHeight())/2);
	p_rxCtx.Blit(xPos, m_pxArrowDown, true);
}


//---------------------------------------------------------------------------------------------------------------------
void	
CStandardVisualization::DrawCloseButton(const CPaintContext& p_rxCtx, const CRct& p_rxRct, FrameType p_eFrameType)
{
	this->DrawFrame(p_rxCtx, p_rxRct, p_eFrameType);
	CRct xInterior = 	p_rxRct;
	xInterior.left		+= GetFrameSize(FT_WindowFixed).left;
	xInterior.right		-= GetFrameSize(FT_WindowFixed).right;
	xInterior.top		+= GetFrameSize(FT_WindowFixed).top;
	xInterior.bottom	-= GetFrameSize(FT_WindowFixed).bottom;
	this->DrawBackground(p_rxCtx, xInterior, BG_Normal);

	CPnt xPos = CPnt((p_rxRct.right-m_pxClose->GetWidth())/2,(p_rxRct.bottom-m_pxClose->GetHeight())/2);
	p_rxCtx.Blit(xPos, m_pxClose, true);
}

//---------------------------------------------------------------------------------------------------------------------
void
CStandardVisualization::DrawTitleBar(const CPaintContext& p_rxCtx, const CPnt& p_rxPos, int p_iWidth, const CStr& p_rsText, bool p_bActive)
{
 	CRct r = CRct(p_rxPos, p_rxPos + CPnt(p_iWidth, m_xVizMetrics.m_iTitleBarHeight));
	if(p_bActive)
	{
		p_rxCtx.FillRect(r, m_axColors[COL_TitleBar]);
	}
	else
	{
		p_rxCtx.FillRect(r, m_axColors[COL_TitleBarInactive]);
	};
	
	const CFont::TFontMetrics& fm = p_rxCtx.GetDevice()->GetFontMetrics(m_ahFonts[FONT_Normal]);
	CPnt p = CPnt(10, 0);
	p.y = (m_xVizMetrics.m_iTitleBarHeight - fm.m_iHeight) / 2 + fm.m_iAscent + 1;
	p_rxCtx.DrawText(m_ahFonts[FONT_Normal], p, p_rsText, m_axColors[COL_TitleBarText]);
}


//---------------------------------------------------------------------------------------------------------------------

} // namespace UILib


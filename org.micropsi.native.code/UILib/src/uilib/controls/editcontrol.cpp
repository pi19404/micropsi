#include "stdafx.h"
#include "uilib/controls/editcontrol.h"
#include "uilib/core/windowmanager.h"
#include "uilib/core/virtualkeycodes.h"
#include "uilib/core/visualizationfactory.h"


namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CEditControl::CEditControl()
{
	m_xText.SetTextLimit(16383);
	m_xText = "MultiLineEdit";
	m_bReadOnly  = false;
	m_bHideNonPrintables = true;
	m_bWordWrap	 = true;
	m_bMultiLine = false;
	m_bFrame	 = true;
	m_bSelecting = false;

	m_iFontHeight = 0;
	m_iFontAscend = 0;
	m_iTextOffset = 0;
	m_iFirstVisibleLine = 0;

	m_iTimer = CTimer::InvalidHandle();
	m_bCursorVisible = true;
	m_eCursor = CMouseCursor::CT_IBeam;
	m_eFont = CVisualization::FONT_TextBox;
}


//---------------------------------------------------------------------------------------------------------------------
CEditControl::~CEditControl()
{
	if(m_iTimer != CTimer::InvalidHandle())
	{
		CWindowMgr::Get().UnsetTimer(m_iTimer);
		m_iTimer = CTimer::InvalidHandle();
	}
}


//---------------------------------------------------------------------------------------------------------------------
CEditControl*
CEditControl::Create()								
{ 
	return new CEditControl(); 
}


//---------------------------------------------------------------------------------------------------------------------
/**
	set button text
*/
void 
CEditControl::SetText(CStr p_sText)
{
	if(m_xText.GetText() != p_sText)
	{
		m_xText = p_sText;
		OnTextChange();
		InvalidateWindow();
	}
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	enable / disable frame around control
*/
void 
CEditControl::SetFrame(bool p_bFrame)
{ 
	m_bFrame = p_bFrame; 
	InvalidateWindow(); 
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	enable / disable multi line
*/
void 
CEditControl::SetMultiLine(bool p_bMultiLine)	
{ 
	m_bMultiLine = p_bMultiLine; 
	m_xText.SetCursorPos(0);
	CalculateLineBreaks();
	this->OnVisualizationChange();
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	force use of a fixed font true / false
*/
void 
CEditControl::SetForceFixedFont(bool p_bFixed)
{
	if(p_bFixed)
	{
		m_eFont = CVisualization::FONT_Fixed;
	}
	else
	{
		m_eFont = CVisualization::FONT_TextBox;
	}
	this->OnVisualizationChange();
}
	

//---------------------------------------------------------------------------------------------------------------------
/** 
	set wordwrap property; true = words wrap around at end of line
*/
void 
CEditControl::SetWordWrap(bool p_bWordWrap)
{
	if(p_bWordWrap != m_bWordWrap)
	{
		m_bWordWrap = p_bWordWrap; 
		m_iTextOffset = 0;
		m_iFirstVisibleLine = 0;
		CalculateLineBreaks();
		InvalidateWindow(); 
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CEditControl::PasteText()
{
	InsertTextAtCursor(CWindowMgr::Get().GetClipBoardContents());
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CEditControl::InsertTextAtCursor(CStr p_sText)
{
	if(!m_bReadOnly)
	{
		m_xText.AddString(p_sText);
		OnTextChange();
		InvalidateWindow();	
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CEditControl::CutText()
{
	if(!m_bReadOnly)
	{
		CWindowMgr::Get().FillClipBoard(m_xText.GetSelectedText());
		m_xText.Delete();
		OnTextChange();
		InvalidateWindow();
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CEditControl::CopyText()
{
	CWindowMgr::Get().FillClipBoard(m_xText.GetSelectedText());	
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CEditControl::ChangeWordWrap()
{
	if ( m_bWordWrap )
	{
		m_bWordWrap = false;
	}
	else
	{
		m_bWordWrap = true;
	}
	m_iTextOffset = 0;
	m_iFirstVisibleLine = 0;
	CalculateLineBreaks();
	InvalidateWindow(); 
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	enable / disable hiding of non-printable characters (line breaks and such)
*/
void 
CEditControl::SetHideNonPrintables(bool p_bHideNonPrintables)
{
	m_bHideNonPrintables = p_bHideNonPrintables; 
	InvalidateWindow();
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	automatically resize edit control to best size
*/
void 
CEditControl::AutoSize(bool p_bMayShrink)
{
	int iLines = m_bMultiLine ? 6 : 1;

	CSize xSize = CSize(max(GetSize().cx, 100), m_iFontHeight * iLines);
	if(m_bFrame)
	{
		xSize.cy += m_xFrameSize.top + m_xFrameSize.bottom;
	}
	if(p_bMayShrink)
	{
		SetSize(xSize);
	}
	else
	{
		AssureMinSize(xSize);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	handle messages
*/
bool 
CEditControl::HandleMsg(const CMessage& p_rxMessage)
{
	if(p_rxMessage == msgTimer)
	{
		if(p_rxMessage.GetTimerID() == m_iTimer)
		{
			m_bCursorVisible = !m_bCursorVisible;
			InvalidateWindow();
			return true;
		}
		else
		{
			return __super::HandleMsg(p_rxMessage);
		}
	}
	else if(p_rxMessage == msgMouseLeftButtonDown)
	{
		return this->OnLButtonDown(p_rxMessage.GetPos());
	}
	else if(p_rxMessage == msgMouseLeftButtonUp)
	{
		return this->OnLButtonUp(p_rxMessage.GetPos());
	}
	else if(p_rxMessage == msgMouseLeftButtonDoubleClick)
	{
		return this->OnLButtonDoubleClick(p_rxMessage.GetPos());
	}
	else if(p_rxMessage == msgKeyDown)
	{
		// this control does key processing on its own; keyup and keydown messages should not bubble to parent

		char cModifier = p_rxMessage.GetKeyModifier();
		int iKey = p_rxMessage.GetKey();
		if(cModifier & KM_CONTROL)
		{
			if(iKey == 'C')
			{
				CWindowMgr::Get().FillClipBoard(m_xText.GetSelectedText());
				return true;
			}
			else if(iKey == 'X')
			{
				if(!m_bReadOnly)
				{
					CWindowMgr::Get().FillClipBoard(m_xText.GetSelectedText());
					m_xText.Delete();
					OnTextChange();
					InvalidateWindow();
				}
				return true;
			}
			if(iKey == 'V')
			{
				if(!m_bReadOnly)
				{
					m_xText.AddString(CWindowMgr::Get().GetClipBoardContents());
					OnTextChange();
					InvalidateWindow();
				}
				return true;
			}
		}

		return true;		
	}
	else if(p_rxMessage == msgKeyUp)
	{
		return true;
	}
	else if(p_rxMessage == msgMouseMove)
	{
		return this->OnMouseMove(p_rxMessage.GetPos());
	}
	else
	{
		return __super::HandleMsg(p_rxMessage);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	left mouse button pressed
*/
bool 
CEditControl::OnLButtonDown(const CPnt& p_rxMousePos)
{
	if(GetDisabled())
	{
		return true;
	}

	CPnt pntMouse = p_rxMousePos;
	CWindowMgr::Get().ToClientPos(GetWHDL(), pntMouse);


	// start select operation	
	m_bSelecting = true;
	CWindowMgr::Get().SetCapture(this);

	// set cursor position
	SetCursorPos(pntMouse, false);

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	left mouse button released
*/
bool
CEditControl::OnLButtonUp(const CPnt& p_rxMousePos)
{
	if(m_bSelecting)
	{
		m_bSelecting = false;
		CWindowMgr::Get().ReleaseCapture(this);
		InvalidateWindow();
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	left button double clicked
*/
bool 
CEditControl::OnLButtonDoubleClick(const CPnt& p_rxMousePos)
{
	m_xText.SelectWordAtCursor();
	InvalidateWindow();
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	mouse moved
*/
bool 
CEditControl::OnMouseMove(const CPnt& p_rxMousePos)
{
	if(m_bSelecting)
	{
		CPnt pntMouse = p_rxMousePos;
		CWindowMgr::Get().ToClientPos(GetWHDL(), pntMouse);
		SetCursorPos(pntMouse, true);
	}
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	character key has been pressed	
*/
bool 
CEditControl::OnCharacterKey(int p_iKey, unsigned char p_iModifier)
{
	if(!m_bReadOnly)
	{
		m_xText.AddChar(p_iKey);
		OnTextChange();
		ResetCursorBlinking();
		InvalidateWindow();
	}

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	non-character key has been pressed
*/
bool 
CEditControl::OnControlKey(int p_iKey, unsigned char p_iModifier)
{
	switch(p_iKey) 
	{
		case VKey_Insert:		
			m_xText.ToggleInsert();		
			break;

		case VKey_Delete:		
			{
				if(!m_bReadOnly)
				{
					m_xText.Delete();
					OnTextChange();
					ResetCursorBlinking();
					InvalidateWindow();
				}
			}
			break;

		case VKey_Backspace:
			{
				if(!m_bReadOnly)
				{
					m_xText.Backspace();	
					OnTextChange();
					ResetCursorBlinking();
					InvalidateWindow();
				}
			}
			break;

		case VKey_Left:			
			{
				if(p_iModifier & KM_CONTROL)
				{
					m_xText.MoveToPreviousWord(p_iModifier & KM_SHIFT);
				}
				else
				{
					m_xText.MoveCursor(-1, p_iModifier & KM_SHIFT);
				}
				ResetCursorBlinking();
				InvalidateWindow();
			}
			break;

		case VKey_Right:		
			{
				if(p_iModifier & KM_CONTROL)
				{
					m_xText.MoveToNextWord(p_iModifier & KM_SHIFT);
				}
				else
				{
					m_xText.MoveCursor(1, p_iModifier & KM_SHIFT);		
				}
				ResetCursorBlinking();
				InvalidateWindow();
			}
			break;

		case VKey_Up:
			{
				if(!m_bMultiLine)
				{
					// give parent the chance to handle key
					return false;
				}
				int i = GetCursorLine();
				if(i > 0)
				{
					if(m_xLineBreaks.GetLineStart(i) == m_xText.GetCursorPos())
					{
						// if cursor is at beginning of line, move to beginning of previous line
						m_xText.MoveCursor(m_xLineBreaks.GetLineLength(i-1), p_iModifier & KM_SHIFT);
					}
					else
					{
						// otherwise, try to find approximately same position in previous line
						SetCursorPos(GetCursorPos() - CPnt(0, m_iFontHeight + (m_iFontHeight / 2)), p_iModifier & KM_SHIFT);
					}
					ResetCursorBlinking();
					InvalidateWindow();
				}
			}
			break;

		case VKey_Down:
			{
				if(!m_bMultiLine)
				{
					// give parent the chance to handle key
					return false;
				}
				int i = GetCursorLine();
				if(m_xLineBreaks.GetLineStart(i) == m_xText.GetCursorPos())
				{
					// if cursor is at beginning of line, move to beginning of next line
					m_xText.MoveCursor(m_xLineBreaks.GetLineLength(i), p_iModifier & KM_SHIFT);
				}
				else
				{
					// otherwise, try to find approximately same position in next line
					SetCursorPos(GetCursorPos() + CPnt(0, m_iFontHeight / 2), p_iModifier & KM_SHIFT);
				}
				ResetCursorBlinking();
				InvalidateWindow();

			}
			break;

		case VKey_Home:
			{
				if(p_iModifier & KM_CONTROL)
				{
					m_xText.Home(p_iModifier & KM_SHIFT); 
				}
				else
				{
					int iLine = GetCursorLine();
					int iDelta = m_xLineBreaks.GetLineStart(iLine) - m_xText.GetCursorPos(); 
					m_xText.MoveCursor(iDelta, p_iModifier & KM_SHIFT);
				}
				ResetCursorBlinking();
				InvalidateWindow();
			}
			break;

		case VKey_End:
			{
				if(p_iModifier & KM_CONTROL)
				{
					m_xText.End(p_iModifier & KM_SHIFT);
				}
				else
				{
					int iLine = GetCursorLine();
					if(iLine<m_xLineBreaks.GetNumLines())
					{
						int iDelta = m_xLineBreaks.GetLineStart(iLine+1) - m_xText.GetCursorPos(); 
						m_xText.MoveCursor(iDelta, p_iModifier & KM_SHIFT);
						while(GetCursorLine() > iLine)
						{
							m_xText.MoveCursor(-1, p_iModifier & KM_SHIFT);
						}
					}
				}
				ResetCursorBlinking();
				InvalidateWindow();
			}
			break;

		case VKey_Return:
			if(m_bMultiLine)
			{
				if(!m_bReadOnly)
				{
					m_xText.NewLine();
					OnTextChange();
					ResetCursorBlinking();
					InvalidateWindow();
				}
			}
			else
			{
				OnUpdate();
			}
			break;

		default:
			// unknown key - give parent the chance to handle it
			return false;
	}

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	paint method
*/
void 
CEditControl::Paint(const CPaintContext& p_rxCtx)
{	
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(p_rxCtx.GetDevice(), GetVisualizationType());

	CRct xInterior = GetRect();
	if(m_bFrame)
	{
		xInterior.top	 += m_xFrameSize.top;		
		xInterior.bottom -= m_xFrameSize.bottom;
		xInterior.left   += m_xFrameSize.left;
		xInterior.right  -= m_xFrameSize.right;
	}

	// adjust text offset (~ visible part of text)
	int	 iCursorLine = GetCursorLine();
	CStr sTextBeforeCursor = m_xText.GetText().Mid(m_xLineBreaks.GetLineStart(iCursorLine), m_xText.GetCursorPos() - m_xLineBreaks.GetLineStart(iCursorLine));
	if(m_bHideNonPrintables)
	{
		sTextBeforeCursor.Remove('\n');
		sTextBeforeCursor.Remove('\r');
	}
	int	 iCursorPos = xInterior.left + m_iBorderSpace + m_iTextOffset + v->GetTextWidth(m_eFont, sTextBeforeCursor);
	int	 iTextScrollStep = GetSize().cx / 4;
	if(iTextScrollStep > 0)
	{
		while(iCursorPos >= GetSize().cx - m_iBorderSpace)
		{
			iCursorPos    -= iTextScrollStep; 
			m_iTextOffset -= iTextScrollStep;
		}
		while(iCursorPos < m_iBorderSpace)
		{
			iCursorPos    += iTextScrollStep; 
			m_iTextOffset += iTextScrollStep;
		}
	}

	if(iCursorLine < m_iFirstVisibleLine)
	{
		m_iFirstVisibleLine = iCursorLine;
	}
	else if(iCursorLine >= m_iFirstVisibleLine + m_iNumVisibleLines)
	{
		m_iFirstVisibleLine = iCursorLine - m_iNumVisibleLines +1;
	}

	// draw everything 
	CVisualization::BackgroundType eBGType   = CVisualization::BG_Editable;
	CVisualization::TextProperty   eTextProp = CVisualization::TP_Normal;

	if(GetDisabled())
	{
		eBGType   = CVisualization::BG_DisabledEditable;
		eTextProp = CVisualization::TP_Disabled;		
	}

	v->DrawBackground(p_rxCtx, xInterior, eBGType);
	if(m_bFrame)
	{
		v->DrawFrame(p_rxCtx, GetRect(), CVisualization::FT_TextBox, GetDisabled());
	}

	CPnt pntTextPos;
	if(m_bMultiLine)
	{
		pntTextPos.y = m_iFontAscend + xInterior.top;
	}
	else
	{
		pntTextPos.y = xInterior.top + ((xInterior.Height() - m_iFontHeight) / 2) + m_iFontAscend;
	}
	int i, iC = min(m_iFirstVisibleLine + m_iNumVisibleLines + (m_bHalfLine ? 1 : 0), m_xLineBreaks.GetNumLines());
	for(i=m_iFirstVisibleLine; i<iC; ++i)
	{
		// Drawing text with a selection:
		// Drawing is done in 3 substrings: text before selection, selection (has a different text style)
		// and text after selection.
		// However, kerning must be considered between these 3 substrings!

		pntTextPos.x = xInterior.left + m_iBorderSpace + m_iTextOffset;

		int iLineStart = m_xLineBreaks.GetLineStart(i);
		int iLineEnd   = iLineStart + m_xLineBreaks.GetLineLength(i);
		CStr sBefore    = m_xText.GetTextBeforeSelection(iLineStart, iLineEnd);
		CStr sSelection = m_xText.GetSelectedText(iLineStart, iLineEnd);
		CStr sAfter	    = m_xText.GetTextAfterSelection(iLineStart, iLineEnd);
		if(m_bHideNonPrintables)
		{
			sBefore.Remove('\n');
			sBefore.Remove('\r');
			sSelection.Remove('\n');
			sSelection.Remove('\r');
			sAfter.Remove('\n');
			sAfter.Remove('\r');
		}

		if(!sBefore.IsEmpty())
		{
			v->DrawText(p_rxCtx, m_eFont, pntTextPos, sBefore, eTextProp);
		}

		if(!sSelection.IsEmpty())
		{
			if(!sBefore.IsEmpty())
			{
				pntTextPos.x += v->GetTextWidth(m_eFont, sBefore);
//							  + v->GetKerning(m_eFont, sBefore.GetAt(sBefore.GetLength()-1), sSelection.GetAt(0));
			}

			v->DrawText(p_rxCtx, m_eFont, pntTextPos, sSelection, eTextProp | CVisualization::TP_Selected);
		}

		if(!sAfter.IsEmpty())
		{
			pntTextPos.x += v->GetTextWidth(m_eFont, sSelection);
//	  					  + v->GetKerning(p_rxCtx.GetDevice(), m_eFont, sSelection.GetAt(sSelection.GetLength()-1), sAfter.GetAt(0));

			v->DrawText(p_rxCtx, m_eFont, pntTextPos, sAfter, eTextProp);
		}

		pntTextPos.y += m_iFontHeight;
	}

	
	if(HasFocusOrChildHasFocus()  &&  m_bCursorVisible  &&  !GetDisabled())
	{
		int iCursorPosY;
		if(m_bMultiLine)
		{ 
			iCursorPosY = xInterior.top + (iCursorLine-m_iFirstVisibleLine+1)*m_iFontHeight;
		}
		else
		{
			iCursorPosY = ((GetSize().cy - m_iFontHeight) / 2) + m_iFontHeight;
		}
		v->DrawTextCursor(p_rxCtx, m_eFont, CPnt(iCursorPos, iCursorPosY));
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CEditControl::OnActivate()
{
	m_iTimer = CWindowMgr::Get().SetTimer(GetWHDL(), m_iCursorBlinkInterval, true);
	m_bChanged = false;
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CEditControl::OnDeactivate()
{
	if(m_iTimer != CTimer::InvalidHandle())
	{
		CWindowMgr::Get().UnsetTimer(m_iTimer);
		m_iTimer = CTimer::InvalidHandle();
	}
	InvalidateWindow();

	m_xText.RemoveSelection();

	if(m_bChanged)
	{
		OnUpdate();
	}

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	called when window size has changed
*/
bool 
CEditControl::OnResize()
{
	m_iMaxLineLength = GetSize().cx - 2* m_iBorderSpace;
	if(m_bFrame)
	{
		m_iMaxLineLength -= (m_xFrameSize.left + m_xFrameSize.right);
	}
	
	int iHeight = GetSize().cy;
	if(m_bFrame)
	{
		iHeight -= (m_xFrameSize.top + m_xFrameSize.bottom);
	}

	if(m_iFontHeight > 0)
	{
		m_iNumVisibleLines = max(1, iHeight / m_iFontHeight);
		m_bHalfLine = (m_iNumVisibleLines * m_iFontHeight != iHeight);
	}

	CalculateLineBreaks();

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	called when visualization has changed
*/
bool 
CEditControl::OnVisualizationChange()
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(pxDevice)
	{
		CVisualization* v = CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());

		m_iFontHeight = v->GetFontMetrics(m_eFont)->m_iHeight;
		m_iFontAscend = v->GetFontMetrics(m_eFont)->m_iAscent;
		m_xFrameSize  = v->GetFrameSize(CVisualization::FT_TextBox);
		m_iTextOffset = 0;

		this->AutoSize(false);
		this->OnResize();
	}

	return this->__super::OnVisualizationChange();
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Send notification message to parent that edit control contents has changed
*/
void 
CEditControl::OnTextChange()
{				
	m_bChanged = true;
	CalculateLineBreaks();
	OnChange();
}


//---------------------------------------------------------------------------------------------------------------------
/**
	The cursor blinks, that means it is visisble for a certain time interval and then invisible for 
	the same interval. This function causes the cursor to start (or restart) a visible interval immediateley.
	This is done when the user types a character of otherwise changes the cursor position.
*/
void 
CEditControl::ResetCursorBlinking()
{
	if(m_iTimer != CTimer::InvalidHandle())
	{
		m_bCursorVisible = true;
		CWindowMgr::Get().ResetTimer(m_iTimer, m_iCursorBlinkInterval, true);
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CEditControl::CalculateLineBreaks()
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(!pxDevice)	{ return; }
	CVisualization* v = CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());

	m_xLineBreaks.Update(m_xText.GetText(), m_iMaxLineLength, pxDevice, v->GetFont(m_eFont));
}	


//---------------------------------------------------------------------------------------------------------------------
/**
	find out in which line the cursor is at the moment	
*/
int 
CEditControl::GetCursorLine() const
{
	if(!m_bMultiLine)
	{
		return 0;
	}

	int i, iCursorPos = m_xText.GetCursorPos();
	for(i=1; i<m_xLineBreaks.GetNumLines(); ++i)
	{
		if(m_xLineBreaks.GetLineStart(i) > iCursorPos)
		{
			return i-1;
		}
	}

	return i-1;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Get line number i as string
*/
CStr 
CEditControl::GetLine(int p_iNumber) const
{
	assert(p_iNumber >= 0  &&  p_iNumber < m_xLineBreaks.GetNumLines());
	return m_xText.GetText().Mid(m_xLineBreaks.GetLineStart(p_iNumber), m_xLineBreaks.GetLineLength(p_iNumber));
}


//---------------------------------------------------------------------------------------------------------------------
/**
	 Get cursor position in pixel coordinates
*/
CPnt 
CEditControl::GetCursorPos() const
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(!pxDevice)
	{
		return CPnt(0, 0);
	}

	CVisualization* v	= CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());

	// adjust text offset (~ visible part of text)
	int	 iCursorLine = GetCursorLine();
	CStr sTextBeforeCursor = m_xText.GetText().Mid(m_xLineBreaks.GetLineStart(iCursorLine), m_xText.GetCursorPos() - m_xLineBreaks.GetLineStart(iCursorLine));
	if(m_bHideNonPrintables)
	{
		sTextBeforeCursor.Remove('\n');
		sTextBeforeCursor.Remove('\r');
	}
	int	 iCursorPos = m_xFrameSize.left + m_iBorderSpace + m_iTextOffset + v->GetTextWidth(m_eFont, sTextBeforeCursor);

	return CPnt(iCursorPos, m_xFrameSize.top + (iCursorLine-m_iFirstVisibleLine+1)*m_iFontHeight);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	 Set cursor position based on pixel coordinates. May be used for position the cursor with a mouse 
	 click and such.

	\param p_rxPos coordinates in pixels; cursor will be positioned as close as possible
	\param p_bSelecting true, if text between old and new cursor position should be selected
*/
void 
CEditControl::SetCursorPos(const CPnt& p_rxPos, bool p_bSelecting)
{
	const COutputDevice* pxDevice = CWindowMgr::Get().GetDeviceConst(GetWHDL());
	if(!pxDevice)
	{
		return;
	}

	int iTop = GetRect().top;
	if(m_bFrame) { iTop += m_xFrameSize.top; }

	// first, determine what line we're in
	int  iLine;
	if(p_rxPos.y >= 0)
	{
		iLine = m_iFirstVisibleLine + ((p_rxPos.y - iTop) / m_iFontHeight);
	}
	else
	{
		iLine = m_iFirstVisibleLine -1;
	}	
	iLine = max(iLine, 0);
	iLine = min(iLine, m_xLineBreaks.GetNumLines() - 1);

	CVisualization* v	= CVisualizationFactory::Get().GetVisualization(pxDevice, GetVisualizationType());
		
	// move cursor to correct line
	m_xText.MoveCursor(m_xLineBreaks.GetLineStart(iLine) - m_xText.GetCursorPos(), p_bSelecting);

	// now position cursor within that line:
	// move cursor forward while x coordinate is not reached and end of line is not reached
	CStr sTextBeforeCursor = m_xText.GetText().Mid(m_xLineBreaks.GetLineStart(iLine), m_xText.GetCursorPos() - m_xLineBreaks.GetLineStart(iLine));
	while(m_iBorderSpace + m_iTextOffset + v->GetTextWidth(m_eFont, sTextBeforeCursor) < p_rxPos.x
		&&  !m_xText.IsCursorAtEnd() &&  (m_xText.GetCursorPos() < m_xLineBreaks.GetLineStart(iLine+1)-1  ||  iLine == m_xLineBreaks.GetNumLines()-1))
	{
		m_xText.MoveCursor(1, p_bSelecting);
		sTextBeforeCursor = m_xText.GetText().Mid(m_xLineBreaks.GetLineStart(iLine), m_xText.GetCursorPos() - m_xLineBreaks.GetLineStart(iLine));
	}

	// move cursor backwards while x coordinate is too large and beginning of line is not reached 
	while(m_iBorderSpace + m_iTextOffset + v->GetTextWidth(m_eFont, sTextBeforeCursor) > p_rxPos.x
		&&  !m_xText.IsCursorAtBeginning()  &&  m_xText.GetCursorPos() > m_xLineBreaks.GetLineStart(iLine))
	{
		m_xText.MoveCursor(-1, p_bSelecting);
		sTextBeforeCursor = m_xText.GetText().Mid(m_xLineBreaks.GetLineStart(iLine), m_xText.GetCursorPos() - m_xLineBreaks.GetLineStart(iLine));
	}

	while(GetCursorLine() > iLine)
	{
		m_xText.MoveCursor(-1, p_bSelecting);
	}

	ResetCursorBlinking();
	InvalidateWindow();
}


//---------------------------------------------------------------------------------------------------------------------
bool
CEditControl::OnChange() 
{ 
	CWindowMgr::Get().PostMsg(CEditControlControlChangedMsg(GetWHDL()), GetParent());
	if(m_xOnChangeCallback)
	{
		m_xOnChangeCallback(this);
	}	
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/// invoked when the control has changed and loses focus
bool 
CEditControl::OnUpdate() 
{ 
	CWindowMgr::Get().PostMsg(CEditControlControlUpdatedMsg(GetWHDL()), GetParent());
	if(m_xOnUpdateCallback)
	{
		m_xOnUpdateCallback(this);
	}	
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	Setzt den Wert eines benannten Attributes. Siehe UIlib Doku für eine vollständige Liste der Attribute.
	\param	p_rsName	Name des Attributes
	\param	p_rsValue	neuer Wert
	\return	true, wenn erfolgreich
*/
bool
CEditControl::SetAttrib(const CStr& p_rsName, const CStr& p_rsValue)
{
	if( p_rsName == "text")				{ SetText(p_rsValue); return true;}
	if( p_rsName == "readonly")			{ SetReadOnly(p_rsValue.ToInt()!=0); return true;}
	if( p_rsName == "frame")				{ SetFrame(p_rsValue.ToInt()!=0); return true;}
	if( p_rsName == "multiline")			{ SetMultiLine(p_rsValue.ToInt()!=0); return true;}
	if( p_rsName == "hidenonprintables")	{ SetHideNonPrintables(p_rsValue.ToInt()!=0); return true;}
	if( p_rsName == "textlimit")			{ SetTextLimit(p_rsValue.ToInt()); return true;}
	return __super::SetAttrib(p_rsName,p_rsValue);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	liefert den Wert eines benannten Attributes. Siehe UILib Doku für eine vollständige Liste der Attribute. ues

	\param	p_rsName		Name des Attributes
	\param	po_srValue		Ausgabe-Parameter: String, der den Wert entgegennimmt
	\return true, wenn Wert zurückgeliefert wurde; false, wenn der Attributnamen nicht bekannt ist
*/
bool 
CEditControl::GetAttrib(const CStr& p_rsName, CStr& po_srValue) const
{
	if(p_rsName == "text")					{ po_srValue=GetText(); return true;}
	if(p_rsName == "readonly")				{ po_srValue=(GetReadOnly()?"1":"0"); return true;}
	if(p_rsName == "frame")				{ po_srValue=(m_bFrame?"1":"0"); return true;}
	if(p_rsName == "multiline")			{ po_srValue=(GetMultiLine()?"1":"0"); return true;}
	if(p_rsName == "hidenonprintables")	{ po_srValue=(m_bHideNonPrintables?"1":"0"); return true;}
	if(p_rsName == "textlimit")			{ po_srValue.Format("%d",m_xText.GetTextLimit()); return true;}
	return __super::GetAttrib(p_rsName,po_srValue);
}


} //namespace UILib


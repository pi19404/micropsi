#include "stdafx.h"
#include "uilib/core/editstring.h"
#include "baselib/macros.h"

namespace UILib
{

//---------------------------------------------------------------------------------------------------------------------
CEditString::CEditString()
{
	m_sText = "";
	m_iTextLimit = 255;
	m_iCursorPos = 0;
	m_bInsert = true;
}


//---------------------------------------------------------------------------------------------------------------------
CEditString::CEditString(CStr p_sText)
{
	m_sText = p_sText;
	m_iTextLimit = 255;
	m_iCursorPos = 0;
	m_bInsert = true;
}


//---------------------------------------------------------------------------------------------------------------------
CEditString::~CEditString()
{
}



//---------------------------------------------------------------------------------------------------------------------
CEditString* 
CEditString::operator=(CStr p_sText)
{
	m_sText = p_sText;
	Crop(m_iTextLimit);
	RemoveSelection();
	SetCursorPos(GetCursorPos()); // validate cursor position

	return this;
}



//---------------------------------------------------------------------------------------------------------------------
/** 
	Add char at current cursor position.
	If there is a selection, the selected text is replaced by the new character.
	Otherwise, the char is inserted at the current cursor position. It overwrites the character
	after the cursor if insert mode is on.
*/
void 
CEditString::AddChar(int p_iChar)
{
	assert(p_iChar != 0);
	if(p_iChar < 32)
	{
		return;
	}
	
	if(IsTextSelected())
	{
		m_sText.Delete(m_iSelectionStart, m_iSelectionLength);
		m_iSelectionLength = 0;
		m_iCursorPos = m_iSelectionStart;
	}

	if(m_bInsert)
	{
		m_sText.Insert(m_iCursorPos, (char) p_iChar);
	}
	else
	{
		if(m_sText.GetLength() > m_iCursorPos)
		{
			m_sText.SetAt(m_iCursorPos, (char) p_iChar);
		}
		else
		{
			m_sText += (char) p_iChar;
		}
	}
	m_iCursorPos++;
	Crop(m_iTextLimit);
}



//---------------------------------------------------------------------------------------------------------------------
/** 
	Insert string at current cursor position. If there is a selection, the
	selected text is replaced by the new string. 
*/
void 
CEditString::AddString(const CStr& p_sString)
{
	if(IsTextSelected())
	{
		m_sText.Delete(m_iSelectionStart, m_iSelectionLength);
		m_iSelectionLength = 0;
		m_iCursorPos = m_iSelectionStart;
	}

	m_sText.Insert(m_iCursorPos, p_sString);
	m_iCursorPos += p_sString.GetLength();
	Crop(m_iTextLimit);
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	delete character after cursor (just like delete key on keyboard)
*/
void 
CEditString::Delete()
{
	if(IsTextSelected())
	{
		m_sText.Delete(m_iSelectionStart, m_iSelectionLength);
		m_iSelectionLength = 0;
		m_iCursorPos = m_iSelectionStart;
	} 
	else if(m_sText.GetLength() > m_iCursorPos)
	{
		m_sText.Delete(m_iCursorPos);
	}
}



//---------------------------------------------------------------------------------------------------------------------
/** 
	delete character before cursor (just like backspace key on keyboard)
*/
void 
CEditString::Backspace()
{
	if(IsTextSelected())
	{
		m_sText.Delete(m_iSelectionStart, m_iSelectionLength);
		m_iSelectionLength = 0;
		m_iCursorPos = m_iSelectionStart;
	}
	else if(m_iCursorPos > 0)
	{
		m_iCursorPos--;
		m_sText.Delete(m_iCursorPos);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	Insert line break at current cursor position.
	If there is a selection, the selected text is removed.
*/
void 
CEditString::NewLine()
{
	if(m_sText.GetLength() >= m_iTextLimit)
	{
		return;
	}

	if(IsTextSelected())
	{
		m_sText.Delete(m_iSelectionStart, m_iSelectionLength);
		m_iSelectionLength = 0;
		m_iCursorPos = m_iSelectionStart;
	}

	m_sText.Insert(m_iCursorPos, '\n');
	m_iCursorPos++;
	Crop(m_iTextLimit);
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	Set position of cursor (befor character number x).
	This removes any selection in the text. 

	\return new cursor pos (guaranteed to be valid)	
*/
int	
CEditString::SetCursorPos(int p_iPos)
{
	p_iPos = min(p_iPos, m_sText.GetLength());
	p_iPos = max(p_iPos, 0);

	if(m_iCursorPos == p_iPos)
	{
		return m_iCursorPos;
	}

	m_iCursorPos = p_iPos;

	// moving the cursor removes any selection
	m_iSelectionLength = 0;

	return m_iCursorPos;
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	\return selected text of substring; empty string if nothing is selected

	\param p_iStartIdx start of substring
	\param p_iEndIdx end of substring (exclusive)
*/
CStr 
CEditString::GetSelectedText(int p_iStartIdx, int p_iEndIdx) const
{ 
	int iStart = max(p_iStartIdx, m_iSelectionStart);
	int iEnd   = min(m_iSelectionStart + m_iSelectionLength, p_iEndIdx);
	if(iEnd > iStart)
	{
		return m_sText.Mid(iStart, iEnd - iStart); 
	}
	else
	{
		return "";
	}
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	\return text before selection in substring; returns complete text if nothing is selected

	\param p_iStartIdx start of substring
	\param p_iEndIdx end of substring (exclusive)
*/
CStr 
CEditString::GetTextBeforeSelection(int p_iStartIdx, int p_iEndIdx) const		
{ 
	int iStart = max(p_iStartIdx, 0);
	int iEnd;
	if(m_iSelectionLength > 0)
	{
		iEnd = min(m_iSelectionStart, p_iEndIdx);
	}
	else
	{
		iEnd = p_iEndIdx;
	}

	if(iEnd > iStart)
	{
		return m_sText.Mid(iStart, iEnd - iStart); 
	}
	else
	{
		return "";
	}
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	\return text after selection in substring; empty string if nothing is selected

	\param p_iStartIdx start of substring
	\param p_iEndIdx end of substring (exclusive)
*/
CStr 
CEditString::GetTextAfterSelection(int p_iStartIdx, int p_iEndIdx) const		
{ 
	if(m_iSelectionLength == 0)
	{
		return "";
	}

	int iStart = max(p_iStartIdx, m_iSelectionStart + m_iSelectionLength);
	int iEnd   = min(m_sText.GetLength(), p_iEndIdx);
	if(iEnd > iStart)
	{
		return m_sText.Mid(iStart, iEnd - iStart); 
	}
	else
	{
		return "";
	}
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	Move cursor; positive width = to the right, negative = to the left.
	New cursor position is guaranteed to be valid even if the width parameter is not. 
	You can select text by moving the cursor; this is usually done by holding the shift key and moving the
	cursor in most programs.
	Moving the cursor without selecting removes any selection.

	\param p_iWidth width to move cursor
	\param p_bSelection true, if the user is selecting text at the moment (i.e. holding the shift key)
	\return new cursor position
*/
int	
CEditString::MoveCursor(int p_iWidth, bool p_bSelecting)
{
	if(!p_bSelecting)
	{
		// normal cursor movement removes selection
		m_iSelectionLength = 0;
	}

	int iNewCursorPos = m_iCursorPos + p_iWidth;
	iNewCursorPos = min(iNewCursorPos, m_sText.GetLength());
	iNewCursorPos = max(iNewCursorPos, 0);
	
	if(m_iCursorPos == iNewCursorPos)	{ return m_iCursorPos; }

	if(p_bSelecting)
	{
		if(IsCharSelected(m_iCursorPos - 1))
		{
			// cursor is right of selection
			m_iSelectionLength = iNewCursorPos - m_iSelectionStart;
		}
		else if(IsCharSelected(m_iCursorPos))
		{
			// cursor is left of selection 
			m_iSelectionLength = m_iSelectionStart - iNewCursorPos + m_iSelectionLength;
			m_iSelectionStart = iNewCursorPos;
		}
		else
		{
			// text around cursor is not selected; start a new selection
			if(iNewCursorPos >= m_iCursorPos)
			{
				m_iSelectionStart = m_iCursorPos;
				m_iSelectionLength = iNewCursorPos - m_iCursorPos;
			}
			else
			{
				m_iSelectionStart = iNewCursorPos;
				m_iSelectionLength = m_iCursorPos - iNewCursorPos;
			}
		}

		if(m_iSelectionLength < 0)
		{
			m_iSelectionLength = -m_iSelectionLength;
			m_iSelectionStart -= m_iSelectionLength;
		}
	}

	m_iCursorPos = iNewCursorPos;
	return m_iCursorPos;
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	Select text.
	See also MoveCursor() for an alternative way of selecting text.

	\param p_iStart index of first character to be selected
	\param p_iLength number of characters to be selected
*/
void 
CEditString::SetSelection(int p_iStart, int p_iLength)
{
	if(p_iStart < 0  ||  p_iStart >= m_sText.GetLength())
	{
		m_iSelectionLength = 0;
	}
	else
	{
		m_iSelectionStart = p_iStart;
		m_iSelectionLength = min(p_iLength, m_sText.GetLength() - m_iSelectionStart);
	}
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	select whole word at (or around) cursor position (like double-clicking in the middle of a word)
*/
void 
CEditString::SelectWordAtCursor()
{
	int i = m_iCursorPos;
	while(i > 0  &&  !IsWhitespace(m_sText.GetAt(i-1)))
	{
		i--;
	}
	m_iSelectionStart = i;

	i = m_iCursorPos;
	while(i < m_sText.GetLength()  &&  !IsWhitespace(m_sText.GetAt(i)))
	{
		i++;
	}
	m_iSelectionLength = i - m_iSelectionStart;

	m_iCursorPos = m_iSelectionStart + m_iSelectionLength;
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	move cursor to beginning of next word
*/
void 
CEditString::MoveToNextWord(bool p_bSelecting)
{
	int i = m_iCursorPos-1;
	while(i < m_sText.GetLength()  &&  !IsWhitespace(m_sText.GetAt(++i)));
	while(i < m_sText.GetLength()  &&  IsWhitespace(m_sText.GetAt(++i)));
	MoveCursor(i - m_iCursorPos, p_bSelecting);
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	move cursor to beginning of previous word
*/
void 
CEditString::MoveToPreviousWord(bool p_bSelecting)
{
	int i = m_iCursorPos;

	while(i > 0  &&  IsWhitespace(m_sText.GetAt(i-1)))
	{
		i--;
	}
	while(i > 0  &&  !IsWhitespace(m_sText.GetAt(i-1)))
	{
		i--;
	}
	MoveCursor(i - m_iCursorPos, p_bSelecting);
}


//---------------------------------------------------------------------------------------------------------------------
/**
	crops text to desired length 
*/
void 
CEditString::Crop(int p_iLength)
{
	if(m_sText.GetLength() > p_iLength)
	{
		m_sText = m_sText.Left(p_iLength);
		m_iCursorPos = min(m_iCursorPos, m_sText.GetLength());
		if(m_iSelectionLength > 0)
		{
			m_iSelectionLength = min(m_iSelectionLength, m_sText.GetLength() - m_iSelectionStart);
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	Starting from index p_iStartIndex, find the beginning of the next word. A word consists of non-whitespace
	characters. 	

	\return index of first character of the next word.
*/
int	
CEditString::FindBeginningOfNextWord(int p_iStartIdx) const
{
	int i = p_iStartIdx;
	
	while (i < m_sText.GetLength()  &&  !IsWhitespace(m_sText[i]))
	{
		i++;
	}

	if(i == m_sText.GetLength())
	{
		return i;
	}

	while (i < m_sText.GetLength()  &&  IsWhitespace(m_sText[i]))
	{
		i++;
	}

	return i;
}


}	// namespace UILib



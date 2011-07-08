#ifndef UILIB_EDITSTRING_H_INCLUDED
#define UILIB_EDITSTRING_H_INCLUDED

namespace UILib
{

class CEditString
{
public:

	CEditString();
	CEditString(CStr p_sText);

	~CEditString();

	CEditString* operator=(CStr p_sText);

	/// add char at current cursor position
	void		AddChar(int p_iChar);
	
	/// add string at current cursor position
	void		AddString(const CStr& p_sString);

	/// delete character after cursor (just like delete key on keyboard)
	void		Delete();

	/// delete character before cursor (just like backspace key on keyboard)
	void		Backspace();

	/// insert line break
	void		NewLine();

	/// move cursor to beginning of text (like home key)
	void		Home(bool p_bSelecting = false)		{ MoveCursor(- GetCursorPos(), p_bSelecting); };

	/// move cursor to end of text (like end key)
	void		End(bool p_bSelecting = false)		{ MoveCursor(m_sText.GetLength() - GetCursorPos(), p_bSelecting); };

	/// toggle between insert / overwrite mode, \return new insert status
	bool		ToggleInsert()						{ return (m_bInsert = !m_bInsert); };

	/// turn insert mode on or off
	void		SetInsert(bool p_bInsert)			{ m_bInsert = p_bInsert; };

	/// get insert mode status
	bool		GetInsert()	const					{ return m_bInsert; };

	/// set position of cursor (befor character number x)
	int			SetCursorPos(int p_iPos);

	/// get position of cursor (before character number x)
	int			GetCursorPos() const				{ return m_iCursorPos; };

	/// move cursor; positive width = to the right, negative = to the left; \return new cursor pos
	int			MoveCursor(int p_iWidth, bool p_bSelecting = false);

	/// get text
	const CStr&	GetText() const						{ return m_sText; };

	/// get length
	int			GetLength() const					{ return m_sText.GetLength(); };

	/// get part of string that is before cursor
	CStr		GetTextBeforeCursor()				{ return m_sText.Left(m_iCursorPos); };

	/// \return true if cursor is at the very beginning of the string
	bool		IsCursorAtBeginning() const			{ return m_iCursorPos == 0; };

	/// \return true if cursor is at the very end of the string
	bool		IsCursorAtEnd() const				{ return m_iCursorPos == m_sText.GetLength(); };

	/// \return true if text is selected; false if nothing is selected
	bool		IsTextSelected() const				{ return m_iSelectionLength != 0; };

	/// \return index of first selected character
	int			GetSelectionStart() const			{ return m_iSelectionStart; };

	/// \return number of selected characters
	int			GetSelectionLength() const			{ return m_iSelectionLength; };

	/// \return selected text; empty string if nothing is selected
	CStr		GetSelectedText() const				{ return m_sText.Mid(m_iSelectionStart, m_iSelectionLength); };

	/// \return text before selection; returns complete text if nothing is selected
	CStr		GetTextBeforeSelection() const		{ return IsTextSelected() ? m_sText.Mid(0, m_iSelectionStart) : m_sText; };

	/// \return text after selection; empty string if nothing is selected
	CStr		GetTextAfterSelection() const		{ return IsTextSelected() ? m_sText.Mid(m_iSelectionStart + m_iSelectionLength) : ""; };

	/// \return selected text of substring; empty string if nothing is selected
	CStr		GetSelectedText(int p_iStartIdx, int p_iEndIdx) const;

	///	\return text before selection in substring; returns complete text if nothing is selected
	CStr		GetTextBeforeSelection(int p_iStartIdx, int p_iEndIdx) const;

	///	\return text after selection in substring; empty string if nothing is selected
	CStr		GetTextAfterSelection(int p_iStartIdx, int p_iEndIdx) const;

	/// select text; see also MoveCursor() for an alternative way of selecting text
	void		SetSelection(int p_iStart, int p_iLength);

	/// remove selection (but not the selected itself text)
	void		RemoveSelection()					{ SetSelection(0, 0); };

	/// select whole word at (or around) cursor position (like double-clicking in the middle of a word)
	void		SelectWordAtCursor();

	/// move cursor to beginning of next word
	void		MoveToNextWord(bool p_bSelecting = false);

	/// move cursor to beginning of previous word
	void		MoveToPreviousWord(bool p_bSelecting = false);
	
	/// \return true if character x is inside the selected area
	bool		IsCharSelected(int p_iIndex)		{ return p_iIndex >= m_iSelectionStart  &&  
													 	 	 p_iIndex <  m_iSelectionStart + m_iSelectionLength; };

	/// crops text to desired length 
	void		Crop(int p_iLength);

	/// set maximum string length
	void		SetTextLimit(int p_iLimit)			{ m_iTextLimit = p_iLimit; Crop(m_iTextLimit); };
	int			GetTextLimit()	const				{ return m_iTextLimit;};

	/// starting from index p_iStartIndex, find the beginning of the next word
	int			FindBeginningOfNextWord(int p_iStartIdx) const;

	/// index operator	
	char		operator[](int p_iIndex)			{ return m_sText[p_iIndex]; };

protected:
	
	/// \return true if given character is a whitespace character
	bool		IsWhitespace(int p_iChar) const		{ return p_iChar == ' ' || p_iChar == '\t' || p_iChar == '\r' ||
														 	 p_iChar == '\n'; };

	CStr		m_sText;			///< actual text
	int			m_iTextLimit;		///< maximum number of characters allowed in text
	int			m_iCursorPos;		///< cursor position - before character number x
	int			m_iSelectionStart;	///< index of first selected character
	int			m_iSelectionLength;	///< number of characters selected
	bool		m_bInsert;			///< insert mode on or off
};


}; // namespace UILib

#endif // ifndef UILIB_EDITSTRING_H_INCLUDED


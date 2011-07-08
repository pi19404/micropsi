#ifndef UILIB_EDITCONTROL_H_INCLUDED 
#define UILIB_EDITCONTROL_H_INCLUDED

#include "uilib/core/visualization.h"
#include "uilib/core/window.h"
#include "uilib/core/editstring.h"
#include "uilib/core/linebreaks.h"

namespace UILib
{

class CEditControl : public CWindow
{
public:
	/// create new DropDirList
	static CEditControl*	Create();

	/// set edit control text
	void			SetText(CStr p_sText);

	///insert text at cursor
	void			InsertTextAtCursor(CStr p_sText);

	/// get edit control text
	CStr			GetText() const;

	/// enable / disable frame around control
	void			SetFrame(bool p_bFrame = true);

	/// enable / disable read-only mode
	void			SetReadOnly(bool p_bReadOnly = true);

	/// get read-only status of control 
	bool			GetReadOnly() const;

	/// enable / disable multi line
	void			SetMultiLine(bool p_bMultiLine = true);
	
	/// get multi line status of control 
	bool			GetMultiLine()	const;

	/// force use of a fixed font true / false
	void			SetForceFixedFont(bool p_bFixed = true);
	
	/// get fixed-font-property (true = edit control uses a fixed font)
	bool			GetForceFixedFont();

	/// set wordwrap property; true = words wrap around at end of line
	void			SetWordWrap(bool p_bWordWrap = true);
	bool			ChangeWordWrap();
	
	virtual bool PasteText();
	virtual bool CutText();
	virtual bool CopyText();
	
	/// get wordwrap property; true = words wrap around at end of line
	bool			GetWordWrap() const;

	/// enable / disable hiding of non-printable characters (line breaks and such)
	void			SetHideNonPrintables(bool p_bHideNonPrintables = true);

	/// limit text to a certain length
	void			SetTextLimit(int p_iLimit);

	/// invoked when the control changes (after every character typed) - overload me!
	virtual bool	OnChange();

	/// invoked when the control has changed and loses focus OR Return Key is pressed in a single line edit ctrl - overload me!
	virtual bool	OnUpdate();

	/// setzt Callbackfunktion für "Change"
	void			SetOnChangeCallback(CFunctionPointer1<CEditControl*>& rxCallback);

	/// setzt Callbackfunktion für "Update"
	void			SetOnUpdateCallback(CFunctionPointer1<CEditControl*>& rxCallback);

	/// set value of named attribute
	virtual bool	SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);

	/// get value of named attribute
	virtual bool	GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;

protected:
	CEditControl();
	virtual ~CEditControl();

	/// get debug info string
	virtual CStr GetDebugString() const;

	virtual void AutoSize(bool p_bMayShrink);
	virtual bool HandleMsg(const CMessage& p_rxMessage);
	virtual void Paint(const CPaintContext& p_rxCtx);

	virtual bool OnActivate();
	virtual bool OnDeactivate();

	virtual bool OnResize();
	virtual bool OnVisualizationChange();

	virtual bool OnLButtonDown(const CPnt& p_rxMousePos);
	virtual bool OnLButtonUp(const CPnt& p_rxMousePos);
	virtual bool OnLButtonDoubleClick(const CPnt& p_rxMousePos);
	virtual bool OnMouseMove(const CPnt& p_rxMousePos);

	virtual bool OnCharacterKey(int p_iKey, unsigned char p_iModifier);
	virtual bool OnControlKey(int p_iKey, unsigned char p_iModifier);

	virtual void OnTextChange();

	void		ResetCursorBlinking();
	void		CalculateLineBreaks();
	int			GetCursorLine() const;
	CStr		GetLine(int p_iNumber) const;
	void		SetCursorPos(const CPnt& p_rxPos, bool p_bSelecting);
	CPnt		GetCursorPos() const;

	static const int	m_iCursorBlinkInterval = 500;		///< cursor blink interval in milliseconds ( = time cursor is visible / invisible)
	static const int	m_iBorderSpace = 5;					///< additional space left and right of text

	CEditString		m_xText;					///< text in this control
	bool			m_bReadOnly;				///< true if this control is read-only
	bool			m_bHideNonPrintables;		///< hide non-printable characters (line breaks and such)
	bool			m_bWordWrap;				///< true: wrap words to next line at end of line (insert "soft" line breaks)
	bool			m_bMultiLine;				///< true, if multiple lines of text are allowed
	CVisualization::Font m_eFont;				///< font type; normal or fixed font

	bool			m_bSelecting;				///< true if user is currently selecting text with the mouse (LMB down)
	int				m_iFontHeight;				///< height of font in pixels
	int				m_iFontAscend;				///< ascend (height over baseline) of font in pixels
	CRct			m_xFrameSize;				///< size of frame around control

	bool			m_bFrame;					///< display frame around control on / off
	bool			m_bChanged;					///< true if control contents has been changed since control received focus

	unsigned long	m_iTimer;					///< id of timer for cursor blinking
	bool			m_bCursorVisible;			///< this bool is turned on and off by a timer to make the cursor blink

	CLineBreaks		m_xLineBreaks;				///< Zeilenumbrüche
	int				m_iTextOffset;	
	int				m_iMaxLineLength;			///< max. visible length of a line in pixels
	int				m_iFirstVisibleLine;		///< first visible line (index)
	int				m_iNumVisibleLines;			///< number of completely visible lines (excluding a possible half line); may be > than number of actually used lines; see also m_bHalfLine
	bool			m_bHalfLine;				///< true, if there is a half-visible line at the bottom of the control

	CFunctionPointer1<CEditControl*>		m_xOnUpdateCallback;	///< Callbackfunktion bei Update, d.h. Return oder Verlassen nach Änderung
	CFunctionPointer1<CEditControl*>		m_xOnChangeCallback;	///< Callbackfunktion bei jeder Veränderung
};

#include "editcontrol.inl"

static const char* msgEditControlChanged = "EditChng";
class CEditControlControlChangedMsg : public CMessage
{ public: CEditControlControlChangedMsg(WHDL hWnd) : CMessage(msgEditControlChanged, false, true, hWnd)	{} };

static const char* msgEditControlUpdated = "EditUpdt";
class CEditControlControlUpdatedMsg : public CMessage
{ public: CEditControlControlUpdatedMsg(WHDL hWnd) : CMessage(msgEditControlUpdated, false, true, hWnd)	{} };


} // namespace UILib


#endif // ifndef UILIB_EDITCONTROL_H_INCLUDED


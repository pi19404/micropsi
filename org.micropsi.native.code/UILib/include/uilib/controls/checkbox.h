#ifndef UILIB_CHECKBOX_H_INCLUDED 
#define UILIB_CHECKBOX_H_INCLUDED

#include "basicbutton.h"
#include "label.h"


namespace UILib
{

class CCheckBox : public CBasicButton
{
public:

	static CCheckBox*	Create();

	enum CheckBoxState
	{
		CB_Unchecked = 0,
		CB_Checked   = 1,
		CB_Default	 = 2
	};

	/// set checked state
	int SetChecked(int p_eChecked = CB_Checked);

	/// get checked state
	int GetChecked() const;

	/// \return true, if checked state is a check mark; false otherwise (unchecked or default)
	bool GetCheckMark() const;

	/// set tristate 
	void SetTristate(bool p_bTristate = true);

	/// get tristate
	bool GetTristate() const;

	/// set checkbox text
	void SetText(CStr p_sText);

	/// move to next state; same effekt as a click
	int NextState();

	/// called when button is clicked; toggles state
	virtual bool OnClick();

	/// called every time checkbox state changes - overload me!
	virtual bool OnStateChange();

	/// turn background on / off
	void SetBackground(bool p_bBackground = true);

	/// get status of background (on or off)
	bool GetBackground() const;

	/// set value of named attribute
	virtual bool SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);

	/// get value of named attribute
	virtual bool GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;

	/// setzt Callbackfunktion für "State Change"
	void		 SetOnStateChangeCallback(CFunctionPointer1<CCheckBox*>& rxCallback);

protected:
	CCheckBox();
	virtual ~CCheckBox();

	virtual CStr GetDebugString() const;
	
	virtual void Paint(const CPaintContext& p_rxCtx);

	virtual bool OnResize();
	virtual bool OnVisualizationChange();

	CLabel*			m_pxLabel;				///< static control for the text or the bitmap
	int				m_eState;				///< current checked state
	bool			m_bTristate;			///< true if this is a checkbox with three states
	CSize			m_xCheckBoxSize;		///< size of checkbox; depends on visualization
    bool            m_bBackground;			///< true, wenn der Hintergrund gezeichnet werden soll

	CFunctionPointer1<CCheckBox*>	m_xOnStateChangeCallback;	///< Callbackfunktion wenn Zustand sich ändert

private:
	CCheckBox(const CCheckBox&);
	operator=(const CCheckBox&);
};

#include "uilib/controls/checkbox.inl"

static const char* msgCheckBoxChanged = "CBoxChng";
class CCheckBoxChangedMsg : public CMessage
{ public: CCheckBoxChangedMsg(WHDL hWnd) : CMessage(msgCheckBoxChanged, false, true, hWnd)	{} };



} // namespace UILib


#endif // ifndef UILIB_CHECKBOX_H_INCLUDED


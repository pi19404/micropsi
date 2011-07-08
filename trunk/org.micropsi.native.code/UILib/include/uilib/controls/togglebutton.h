
#ifndef UILIB_TOGGLEBUTTON_H_INCLUDED 
#define UILIB_TOGGLEBUTTON_H_INCLUDED

#include "uilib/controls/button.h"

namespace UILib
{

class CToggleButton : public CButton
{
public:
	/// erzeugt einen neuen ToggleButton
	static CToggleButton* Create();

	/// liefert den Zustand; true = gedrückt; false = nicht gedrückt
	bool	GetToggleButtonState();

	/// setzt den Zustand; true = gedrückt; false = nicht gedrückt
	void	SetToggleButtonState(bool p_bState);

	/// schaltet Gruppierung ein/aus (gruppierter ToggleButton verhält sich wie RadioButton)
	void	SetGrouped(bool p_bGrouped = true);

	/// liefert Gruppierungsstatus (ein/aus) (gruppierter ToggleButton verhält sich wie RadioButton)
	bool	GetGrouped() const;

	/// bestimmt, ob der Button wieder herausgedruckt werden darf (default = true)
	void	SetAllowUntoggle(bool p_bUntoggle= true);

	/// liefert, ob der Button wieder herausgedruckt werden darf (default = true)
	bool	GetAllowUntoggle() const;

	/// called every time radiobutton state changes - overload me!
	virtual bool OnStateChange();

	/// setzt Callbackfunktion für "State Change"
	void	SetOnStateChangeCallback(CFunctionPointer1<CToggleButton*>& rxCallback);

protected:
	CToggleButton();
	virtual ~CToggleButton();

	virtual CStr GetDebugString() const;

	virtual bool HandleMsg(const CMessage& p_rxMessage);
	virtual void Paint(const CPaintContext& p_rxCtx);
	virtual bool OnClick();

	virtual	void UpdateBitmap();

private:

	bool			m_bState;			///< Status; true = gedrückt; false = nicht gedrückt
	bool			m_bInGroup;			///< true, wenn Button in Gruppe ist (funktioniert dann wie CRadioButton)
	bool			m_bAllowUntoggle;	///< true, wenn wieder herausdrücken erlaubt ist (default: true)

	CFunctionPointer1<CToggleButton*>	m_xOnStateChangeCallback;	///< Callbackfunktion wenn Zustand sich ändert

	CToggleButton(const CToggleButton&) {}
	operator=(const CToggleButton&) {}
};

#include "uilib/controls/togglebutton.inl"

static const char* msgToggleButtonChanged = "TBtnChng";
class CToggleButtonChangedMsg : public CMessage
{ public: CToggleButtonChangedMsg(WHDL hWnd) : CMessage(msgToggleButtonChanged, false, true, hWnd)	{} };


static const char* msgClearToggleButton = "TBtnClr";
class CClearToggleButtonMsg : public CMessage
{ public: CClearToggleButtonMsg() : CMessage(msgClearToggleButton, false, false)	{} };


} // namespace UILib

#endif // ifndef UILIB_TOGGLEBUTTON_H_INCLUDED


#ifndef UILIB_BASICBUTTON_H_INCLUDED 
#define UILIB_BASICBUTTON_H_INCLUDED

#include "uilib/core/window.h"

namespace UILib
{

class CBasicButton : public CWindow
{
public:

	/// create new basic window
	static CBasicButton* Create();
 
	static const int	m_iDelayBeforeAutoRepeat = 700;		///< delay between first evtButtonDown message and first repeatition in milliseconds
	static const int	m_iAutoRepeatDelay		 = 30;		///< delay between auto-repeated messages

	/// invoked every time the button is clicked - overload me!
	virtual bool OnClick();

	/// invoked over and over while the button is down - overload me!
	virtual bool OnButtonDown();
	
	/// setzt Callbackfunktion für "Button Down"
	void			SetOnButtonDownCallback(CFunctionPointer1<CBasicButton*>& rxCallback);

	/// setzt Callbackfunktion für "Button Clicked"
	void			SetOnClickCallback(CFunctionPointer1<CBasicButton*>& rxCallback);

protected:

	CBasicButton();
	virtual ~CBasicButton();

	virtual void	DeleteNow();

	virtual bool	HandleMsg(const CMessage& p_rxEvent);
	virtual bool	OnDeactivate();

	virtual bool	OnLButtonDown(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier);
	virtual bool	OnLButtonUp(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier);
	virtual bool	OnMouseMove(const CPnt& p_rxRelativeMousePos, unsigned char p_iModifier);

	///set logical button down status - see class description
	virtual void	SetButtonDown(bool p_bButtonDown);

	/// get logical button down status - see class description
	bool			GetButtonDown() const;

	/// get mouse button status (pressed or not)
	bool			GetMouseButtonPressed() const;

	/// get debug info string
	virtual CStr	GetDebugString() const;

private:

	CFunctionPointer1<CBasicButton*>		m_xOnButtonDownCallback;	///< Callbackfunktion wenn Button unten gehalten
	CFunctionPointer1<CBasicButton*>		m_xOnClickCallback;			///< Callbackfunktion bei Click

	bool				m_bMouseButtonPressed;		///< mouse button pressed over button, but not released yet --> mouse is captured
	bool				m_bSpacePressed;			///< space bar ist pressed and not released yet
	bool				m_bButtonDown;				///< button is down at the moment
	int					m_iBtnDownTimer;			///< timer id for autorepeat of button-down-messages
};

#include "basicbutton.inl"

// Button-Nachrichten:

static const char* msgButtonClicked = "BtnClick";
class CButtonClickedMsg : public CMessage
{ public: CButtonClickedMsg(WHDL hWnd) : CMessage(msgButtonClicked, false, true, hWnd)	{} };

static const char* msgButtonDown = "BtnDown";
class CButtonDownMsg : public CMessage
{ public: CButtonDownMsg(WHDL hWnd) : CMessage(msgButtonDown, false, true, hWnd)	{} };



} // namespace UILib

#endif // ifndef UILIB_BASICBUTTON_H_INCLUDED 


#ifndef UILIB_RADIOBUTTON_H_INCLUDED 
#define UILIB_RADIOBUTTON_H_INCLUDED

#include "basicbutton.h"
#include "label.h"


namespace UILib
{


class CRadioButton : public CBasicButton
{
public:
	/// create new CRadioButton
	static CRadioButton* Create();

	/// automatically resize radiobutton to best size
	virtual void	AutoSize(bool p_bMayShrink = true);

	/// set selected state
	bool SetSelected(bool p_bSelected = true);

	/// get selected state
	bool GetSelected() const;
 
	/// set radio button text
	void SetText(CStr p_sText);

	/// set radio button text
	CStr GetText() const;

	/// turn background on / off
	void SetBackground(bool p_bBackground = true);

	/// get status of background (on or off)
	bool GetBackground() const;
    
	/// set value of named attribute
	virtual bool SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);

	/// get value of named attribute
	virtual bool GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;

	/// called every time radiobutton state changes - overload me!
	virtual bool OnStateChange();

	/// setzt Callbackfunktion für "State Change"
	void		 SetOnStateChangeCallback(CFunctionPointer1<CRadioButton*>& rxCallback);

protected:

	CRadioButton();
	virtual ~CRadioButton();

	/// handle message
	virtual bool HandleMsg(const CMessage& p_rxMessage);

	virtual void Paint(const CPaintContext& p_rxCtx);
	virtual bool OnClick();

	virtual bool OnResize();
	virtual bool OnVisualizationChange();

	virtual CStr GetDebugString() const;

	CLabel*			m_pxLabel;				///< static control for the text or the bitmap
	bool			m_bSelected;			///< true for selected, false for clear
	CSize			m_xRadioBtnSize;		///< size of radio button; depends on visualization
    bool            m_bBackground;			///< true, wenn der Hintergrund gezeichnet werden soll

	CFunctionPointer1<CRadioButton*>	m_xOnStateChangeCallback;	///< Callbackfunktion wenn Zustand sich ändert

private:
	CRadioButton(const CRadioButton&);
	operator=(const CRadioButton&);
};

#include "radiobutton.inl"

static const char* msgRadioButtonChanged = "RBtnChng";
class CRadioButtonChangedMsg : public CMessage
{ public: CRadioButtonChangedMsg(WHDL hWnd) : CMessage(msgRadioButtonChanged, false, true, hWnd)	{} };


static const char* msgClearRadioButton = "RBtnClr";
class CClearRadioButtonMsg : public CMessage
{ public: CClearRadioButtonMsg() : CMessage(msgClearRadioButton, false, false)	{} };


} //namespace UILib


#endif // ifndef UILIB_RADIOBUTTON_H_INCLUDED


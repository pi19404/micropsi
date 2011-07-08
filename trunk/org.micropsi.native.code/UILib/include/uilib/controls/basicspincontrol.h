
#ifndef UILIB_BASICSPINCONTROL_H_INCLUDED 
#define UILIB_BASICSPINCONTROL_H_INCLUDED

#include "uilib/core/window.h"
#include "uilib/controls/button.h"
#include "uilib/controls/editcontrol.h"
#include "uilib/core/windowmanager.h"

namespace UILib
{

class CBasicSpinControl : public CWindow
{
public:

	static CBasicSpinControl* Create();

	enum SpinControlStyles
	{
		SC_UpDown	 = 0,		///< die Kn�pfe zeigen nach oben und unten
		SC_LeftRight = 1		///< die Kn�pfe zeigen nach links und rechts
	};

	/// Texteingabe im Editcontrol erlauben oder verbieten
	void			SetEditable(bool p_bEditable);

	/// ver�ndert die Fenstergr��e
	virtual void	SetSize(const CSize& p_rxSize);

	/// �ndert den Stil (Buttonrichtungen)
	void			SetStyle(SpinControlStyles p_eStyle);

	/// wird aufgerufen, sobald sich der Controlinhalt �ndert, d.h. nach jedem getippten Zeichen
	virtual bool	OnChange();
	
	/// setzt Callbackfunktion f�r "Change"
	void			SetOnChangeCallback(CFunctionPointer1<CBasicSpinControl*>& rxCallback);

protected:

	CBasicSpinControl();
	virtual ~CBasicSpinControl();

	/// lokale Klasse f�r die Buttons
	class CBasicSpinControlButton : public CBasicButton
	{
	public:		
		static CBasicSpinControlButton* Create() { return new CBasicSpinControlButton(); }
		void SetButtonType(CVisualization::ButtonType p_eButtonType)	{m_eButtonType = p_eButtonType; }
	protected:
		CBasicSpinControlButton() { m_eButtonType = CVisualization::BT_UpArrow; }
		virtual void Paint(const CPaintContext& p_rxCtx);

		CVisualization::ButtonType		m_eButtonType;			
	private:
		CBasicSpinControlButton(const CBasicSpinControlButton&) {}
		operator=(const CBasicSpinControlButton&) {}
	};

	virtual CStr GetDebugString() const;

	virtual void Paint(const CPaintContext& p_rxCtx);
	virtual bool HandleMsg(const CMessage& p_rxMessage);
	virtual bool OnControlKey(int p_iKey);

	virtual bool OnResize();
	virtual bool OnVisualizationChange();
	virtual bool OnActivate();


	/// wird aufgerufen, wenn der "Hoch"-Button gedr�ckt wird - wird von abgeleiteten Klassen �berladen 
	virtual void Up() {}

	/// wird aufgerufen, wenn der "Runter"-Button gedr�ckt wird - wird von abgeleiteten Klassen �berladen 
	virtual void Down() {}

	SpinControlStyles	m_eStyle;					///< Stil
	CSize				m_xButtonSize;				///< Gr��e der Buttons
	CRct				m_xFrameSize;				///< Gr��e des Rahmens

	CBasicSpinControlButton*	m_pxButton1;		///< Left- oder UpButton
	CBasicSpinControlButton*	m_pxButton2;		///< Right- oder DownButton
	CEditControl*				m_pxEditCtrl;		///< EditControl

	CFunctionPointer1<CBasicSpinControl*>	m_xOnChangeCallback;	///< Callbackfunktion bei jeder Ver�nderung

private:
	CBasicSpinControl(const CBasicSpinControl&) {}
	operator=(const CBasicSpinControl&) {}
};

#include "basicspincontrol.inl"

static const char* msgSpinControlChanged = "SpinChng";
class CSpinControlChangedMsg : public CMessage
{ public: CSpinControlChangedMsg(WHDL hWnd) : CMessage(msgSpinControlChanged, false, true, hWnd)	{} };


} //namespace UILib

#endif // ifndef UILIB_BASICSPINCONTROL_H_INCLUDED


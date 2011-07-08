#ifndef SLIDERPANEL_H_INCLUDED
#define SLIDERPANEL_H_INCLUDED

#include "uilib/controls/panel.h"
#include "uilib/controls/scrollwindow.h"
#include "uilib/controls/checkbox.h"
#include "uilib/controls/button.h"
#include "uilib/controls/slider.h"

namespace UILib
{
	class CSliderSpinControlCombo;
};

class CSliderPanel: public UILib::CPanel
{
public:

	static CSliderPanel*		Create();
	float						GetCurrentValue(std::string p_sBoneName) const;

protected:

	CSliderPanel();
	virtual ~CSliderPanel();	

	virtual bool			HandleMsg(const UILib::CMessage& p_rxMessage);
	virtual void			DeleteNow();

	void					SetAllSelectedSliders(float p_fValue);


	UILib::CCheckBox*		m_pxAllCheckBox;
	int						m_eLastAllCheckBoxState;

	UILib::CButton*			m_pxStartButton;
	UILib::CButton*			m_pxMiddleButton;
	UILib::CButton*			m_pxEndButton;
	UILib::CSlider*			m_pxSlider;

	UILib::CScrollWindow*	m_pxScrollWindow;

	struct BoneGuiElement
	{
		UILib::CSliderSpinControlCombo*		m_pxSliderSpinCombo;
		UILib::CCheckBox*					m_pxCheckBox;
		bool								m_bOldState;					///< old state of checkbox; restored when "all"-Checkbox returns to default state
	};

	std::map<std::string, BoneGuiElement>		m_apxAllSliders;
};

#endif // SLIDERPANEL_H_INCLUDED


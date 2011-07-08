#ifndef UILIBTEST_SLIDERTESTWINDOW_H_INCLUDED 
#define UILIBTEST_SLIDERTESTWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"

namespace UILib	{ class CSlider; }
namespace UILib	{ class CCheckBox; }
namespace UILib	{ class CSpinControlNumber; }
namespace UILib	{ class CBasicSpinControl; }


class CSliderTestWindow : public UILib::CDialogWindow
{
public:

	static CSliderTestWindow * Create();

protected:

	CSliderTestWindow ();
	virtual ~CSliderTestWindow ();

	virtual void	DeleteNow();
	virtual bool	OnClose();

	void	OnSliderChange(UILib::CSlider* pxSlider);
	void	OnHPositionChange(UILib::CBasicSpinControl* pxSpinner);
	void	OnVPositionChange(UILib::CBasicSpinControl* pxSpinner);
	void	OnHRangeChange(UILib::CBasicSpinControl* pxSpinner);
	void	OnVRangeChange(UILib::CBasicSpinControl* pxSpinner);
	void	OnToggleBackground(UILib::CCheckBox* pxCheckBox);

	UILib::CSlider*		m_pxHSlider;
	UILib::CSlider*		m_pxVSlider;

	UILib::CSpinControlNumber*	m_pxHPosition;
	UILib::CSpinControlNumber*	m_pxVPosition;
};

#endif // ifndef UILIBTEST_SLIDERTESTWINDOW_H_INCLUDED 


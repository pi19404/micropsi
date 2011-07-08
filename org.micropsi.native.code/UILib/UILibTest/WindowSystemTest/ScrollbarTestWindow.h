#ifndef UILIBTEST_SCROLLBARTESTWINDOW_H_INCLUDED 
#define UILIBTEST_SCROLLBARTESTWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"

namespace UILib	{ class CScrollBar; }
namespace UILib	{ class CCheckBox; }
namespace UILib	{ class CSpinControlNumber; }
namespace UILib	{ class CBasicSpinControl; }


class CScrollBarTestWindow : public UILib::CDialogWindow
{
public:

	static CScrollBarTestWindow * Create();

protected:

	CScrollBarTestWindow ();
	virtual ~CScrollBarTestWindow ();

	virtual void	DeleteNow();
	virtual bool	OnClose();

	void	OnScrollBarChange(UILib::CScrollBar* pxScrollBar);
	void	OnHPositionChange(UILib::CBasicSpinControl* pxSpinner);
	void	OnVPositionChange(UILib::CBasicSpinControl* pxSpinner);
	void	OnHRangeChange(UILib::CBasicSpinControl* pxSpinner);
	void	OnVRangeChange(UILib::CBasicSpinControl* pxSpinner);
	void	OnHPageSizeChange(UILib::CBasicSpinControl* pxSpinner);
	void	OnVPageSizeChange(UILib::CBasicSpinControl* pxSpinner);
	void	OnToggleButtons(UILib::CCheckBox* pxCheckBox);

	UILib::CScrollBar*		m_pxHScrollBar;
	UILib::CScrollBar*		m_pxVScrollBar;

	UILib::CSpinControlNumber*	m_pxHPosition;
	UILib::CSpinControlNumber*	m_pxVPosition;
};

#endif // ifndef UILIBTEST_SCROLLBARTESTWINDOW_H_INCLUDED 


#ifndef UILIBTEST_LABELTESTWINDOW_H_INCLUDED 
#define UILIBTEST_LABELTESTWINDOW_H_INCLUDED

#include "uilib/controls/dialogwindow.h"
#include "uilib/core/font.h"

namespace UILib	{ class CRadioButton; }
namespace UILib	{ class CLabel; }
namespace UILib	{ class CCheckBox; }
namespace UILib	{ class CEditControl; }


class CLabelTestWindow : public UILib::CDialogWindow
{
public:

	static CLabelTestWindow * Create();

protected:

	CLabelTestWindow ();
	virtual ~CLabelTestWindow ();

	virtual void	DeleteNow();
	virtual bool	OnClose();

	void	OnAlignmentChange(UILib::CRadioButton* pxRadioButton);
	void	OnToggleBackground(UILib::CCheckBox* pxCheckBox);
	void	OnToggleSelected(UILib::CCheckBox* pxCheckBox);
	void	OnTextChanged(UILib::CEditControl* pxEdit);

	UILib::CFontHandle	m_hAlphaBitmapFont;
	UILib::CFontHandle	m_hBitmapfontFont;

	UILib::CLabel*		m_pxLabel1;
	UILib::CLabel*		m_pxLabel2;
	UILib::CLabel*		m_pxLabel3;

	UILib::CRadioButton*	m_pxHCenter;
	UILib::CRadioButton*	m_pxHLeft;
	UILib::CRadioButton*	m_pxHRight;
	UILib::CRadioButton*	m_pxVCenter;
	UILib::CRadioButton*	m_pxVTop;
	UILib::CRadioButton*	m_pxVBottom;
};

#endif // ifndef UILIBTEST_LABELTESTWINDOW_H_INCLUDED 


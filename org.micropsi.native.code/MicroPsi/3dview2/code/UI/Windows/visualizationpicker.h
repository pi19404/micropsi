#pragma once

#ifndef VISUALIZATIONPICKER_H_INCLUDED
#define VISUALIZATIONPICKER_H_INCLUDED

#include "uilib/controls/groupbox.h"
#include "uilib/controls/listbox.h"
#include "uilib/controls/editcontrol.h"
#include "uilib/controls/radiobutton.h"

class CVisualizationPicker : public UILib::CGroupBox
{
public:

	enum WrapState
	{
		WS_MapDefault,
		WS_ForceWrapAround,
		WS_ForceNoWrapAround
	};

	static CVisualizationPicker*	Create();
	void					UpdateList();
	std::string				GetSelectedFile() const;
	WrapState				GetWrapAround() const;

protected:

    CVisualizationPicker();
    virtual ~CVisualizationPicker();

	virtual void			DeleteNow();
	virtual bool			HandleMsg(const UILib::CMessage& p_rxMessage);

private:

	UILib::CListBox*		m_pxFileList;
	UILib::CEditControl*	m_pxFileDescription;
	UILib::CRadioButton*	m_pxDefaultWrap;
	UILib::CRadioButton*	m_pxForceWrap;
	UILib::CRadioButton*	m_pxForceNoWrap;
};

#endif // VISUALIZATIONPICKER_H_INCLUDED

#pragma once

#ifndef OBJECTPROPERTIES_H_INCLUDED
#define OBJECTPROPERTIES_H_INCLUDED

#include "Application/stdinc.h"
#include "uilib/controls/label.h"
#include "uilib/controls/panel.h"
#include "uilib/controls/spincontrolnumber.h"

class CObjectProperties : public UILib::CPanel
{
public:
	static	CObjectProperties*	Create();

	void	Tick();

protected:
    CObjectProperties();
    virtual ~CObjectProperties();

	virtual void DeleteNow();
	virtual	bool HandleMsg(const UILib::CMessage& p_krxMessage);

	UILib::CSpinControlNumber*	m_pxX;	
	UILib::CSpinControlNumber*	m_pxY;
	UILib::CSpinControlNumber*	m_pxZ;

	UILib::CSpinControlNumber*	m_pxXZAngle;
};

#endif // ifndef OBJECTPROPERTIES_H_INCLUDED

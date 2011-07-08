/**
	Zustand eines Buttons (z.B. Gamepad) über eine gewisse Zeit. Dient zum Erkennen von Flanken (drücken, loslassen)
*/

#pragma once
#ifndef BUTTONSTATE_H_INCLUDED
#define BUTTONSTATE_H_INCLUDED

#include "windows.h"
#include <vector>

class CButtonState
{
public:
    CButtonState();
	~CButtonState();

	void			Update(bool p_bDownNow);

	bool			ButtonDownEvent() const;
	bool			ButtonUpEvent() const;

private:

	bool			m_bDownNow;						///< ist der Button bei diesem Update down?
	bool			m_bDownLastTime;				///< war der Button beim letzten Update auch down?

};

#include "ButtonState.inl"

#endif // BUTTONSTATE_H_INCLUDED


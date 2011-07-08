#pragma once
#ifndef MOUSESTATE_H_INCLUDED
#define MOUSESTATE_H_INCLUDED

struct IDirectInputDevice8A;

#include <dinput.h>
#include "GameLib/UserInterface/Input/ButtonState.h"

class CMouseState
{
public:

	enum MouseButtons
	{
		MOUSE_LEFTBUTTON = 0,
		MOUSE_RIGHTBUTTON = 1,
		MOUSE_MIDDLEBUTTON = 2
	};

    CMouseState(IDirectInputDevice8A* pDIDKeyboard);
    ~CMouseState();

    void		RetrieveFromDevice();

	int			GetNumButtons() const;
	int			GetNumAxis() const;
	bool		IsWheelAvailable() const;

	bool		IsAnyButtonDown() const;
	bool		IsButtonDown(int p_iButton) const;
	bool		ButtonDownEvent(int p_iButton) const;
	bool		ButtonUpEvent(int p_iButton) const;

	int			GetXAxisRelativeValue() const;
	int			GetYAxisRelativeValue() const;
	int			GetWheelRelativeValue() const;
	int			GetAxisRelativeValue(int p_iAxis) const;

private:

    IDirectInputDevice8A*	m_pDIDMouse;
	DIMOUSESTATE2			m_MouseState;

	int						m_iNumAxes;				///< Anzahl Achsen 
	int						m_iNumButtons;			///< Anzahl Knöpfe

	CButtonState*			m_pxButtonStates;		///< Array mit States
};

#include "MouseState.inl"

#endif // KEYBOARDSTATE_H_INCLUDED

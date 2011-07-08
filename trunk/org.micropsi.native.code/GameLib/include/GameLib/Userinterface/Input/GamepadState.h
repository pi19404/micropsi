/*******************************************************************************
 GamepadState.h - Klasse CGamepadState liest den Zustand des Gamepads ein, 
    wird vom Interface verwendet und von CUserGameCtrlEvts.
    Soll auf keinen Fall direkt vom Gameplay benutzt werden!
*******************************************************************************/
#pragma once

#ifndef GAMEPADSTATE_H_INCLUDED
#define GAMEPADSTATE_H_INCLUDED

#include <dinput.h>
#include "GameLib/UserInterface/Input/ButtonState.h"

class CGamepadState
{
public:

    // Nintendo Game Cube Pad Buttons
    enum NGCPadButtons
    {
        BTN_A = 0,
        BTN_B = 1,
        BTN_X = 2,
        BTN_Y = 3,
        BTN_L = 4,
        BTN_R = 5,
        BTN_Z = 6,
        BTN_START_PAUSE = 7,
        BTN_CROSS_LEFT = 8,
        BTN_CROSS_RIGHT = 9,
        BTN_CROSS_DOWN = 10,
        BTN_CROSS_UP = 11
    };

	enum Axis
	{
		AX_X = 0,
		AX_Y = 1,
		AX_Z = 2,
		AX_XROT = 3,
		AX_YROT = 4,
		AX_ZROT = 5,
		AX_SLIDER1 = 6,
		AX_SLIDER2 = 7
	};

	enum AxisMode
	{
		AM_UNSIGNED			= 1,					///< Achse ist unsigned, d.h. liefert 0..1 statt -1..1
		AM_AUTOCALIBRATE	= 2
	};
    
    CGamepadState(IDirectInputDevice8A* pDIDGamepad);
    ~CGamepadState();

    void	RetrieveFromDevice();

	int		GetNumButtons() const;
	int		GetNumAxis() const;

	bool	IsAnyButtonDown() const;
	bool	IsButtonDown(int p_iButton) const;
	bool	ButtonDownEvent(int p_iButton) const;
	bool	ButtonUpEvent(int p_iButton) const;

	float   GetXAxisValue() const;
	float   GetYAxisValue() const;
	float   GetZAxisValue() const;

	float   GetXAxisRotation() const;
	float   GetYAxisRotation() const;
	float   GetZAxisRotation() const;

	float   GetSlider1Value() const;
	float   GetSlider2Value() const;

	float	GetAxisValue(int p_iAxis) const;

	void	SetAxisMode(int p_iAxis, int p_iFlags);

private:

    IDirectInputDevice8A* m_pDIDGamepad;

	int				m_iNumAxes;					///< Anzahl Axen
	int				m_iNumButtons;				///< Azahl Buttons
	DIJOYSTATE2	    m_JoystickState;

	/// Daten, die pro Achse gehalten werden müssen
	class CAxis
	{
	public:
		CAxis() : m_iFlags(0), m_bHasLeftCenter(false) {}

		int			m_iFlags;					///< Flags für diese Achse
		bool		m_bHasLeftCenter;			///< true, wenn diese Achse schon einmal die Mittelstellung verlassen hat
	};

	CAxis*			m_pxAxes;

    float           m_fAxisThreshold;
	CButtonState*	m_pxButtonStates;			///< Array mit States

	static BOOL CALLBACK	EnumObjectsCallback(const DIDEVICEOBJECTINSTANCE* pdidoi, VOID* pContext);
	float					GetNormalizedAxisValue(int iAxis, LONG iValue) const;
};

#include "GamepadState.inl"

#endif // GAMEPADSTATE_H_INCLUDED

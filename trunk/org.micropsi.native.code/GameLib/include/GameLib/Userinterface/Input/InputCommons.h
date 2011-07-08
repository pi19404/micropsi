#pragma once
#ifndef INPUTCOMMONS_H_INCLUDED
#define INPUTCOMMONS_H_INCLUDED

enum InputDevice {
	IDEV_KEYBOARD,
	IDEV_MOUSE,
	IDEV_JOYSTICK0,
	IDEV_JOYSTICK1,
	IDEV_JOYSTICK2,
	IDEV_JOYSTICK3,
};

enum MouseButton {
	MBTN_LEFT,
	MBTN_RIGHT,
	MBTN_MIDDLE
};


enum KeyCode {
	KC_NONE,

	KC_LBUTTON,
	KC_RBUTTON,
	KC_MBUTTON,
	KC_BACK,
	KC_TAB,
	KC_RETURN,
	KC_LSHIFT,
	KC_RSHIFT,
	KC_LCONTROL,
	KC_RCONTROL,
	KC_MENU,
	KC_PAUSE,
	KC_CAPITAL,
	KC_ESCAPE,
	KC_SPACE,
	KC_PRIOR,
	KC_NEXT,
	KC_END,
	KC_HOME,
	KC_LEFT,
	KC_UP,
	KC_RIGHT,
	KC_DOWN,
	KC_SELECT,
	KC_PRINT,
	KC_EXECUTE,
	KC_SNAPSHOT,
	KC_INSERT,
	KC_DELETE,
	KC_HELP,

	KC_1,
	KC_2,
	KC_3,
	KC_4,
	KC_5,
	KC_6,
	KC_7,
	KC_8,
	KC_9,
	KC_0,

	KC_A,
	KC_B,
	KC_C,
	KC_D,
	KC_E,
	KC_F,
	KC_G,
	KC_H,
	KC_I,
	KC_J,
	KC_K,
	KC_L,
	KC_M,
	KC_N,
	KC_O,
	KC_P,
	KC_Q,
	KC_R,
	KC_S,
	KC_T,
	KC_U,
	KC_V,
	KC_W,
	KC_X,
	KC_Y,
	KC_Z,

	KC_LWIN,
	KC_RWIN,
	KC_APPS,

	KC_NUMPAD0,
	KC_NUMPAD1,
	KC_NUMPAD2,
	KC_NUMPAD3,
	KC_NUMPAD4,
	KC_NUMPAD5,
	KC_NUMPAD6,
	KC_NUMPAD7,
	KC_NUMPAD8,
	KC_NUMPAD9,

	KC_MULTIPLY,
	KC_ADD,
	KC_SEPARATOR,
	KC_SUBTRACT,
	KC_DECIMAL,
	KC_DIVIDE,
	KC_F1,
	KC_F2,
	KC_F3,
	KC_F4,
	KC_F5,
	KC_F6,
	KC_F7,
	KC_F8,
	KC_F9,
	KC_F10,
	KC_F11,
	KC_F12,
	KC_F13,
	KC_F14,
	KC_F15,
	KC_F16,
	KC_F17,
	KC_F18,
	KC_F19,
	KC_F20,
	KC_F21,
	KC_F22,
	KC_F23,
	KC_F24,

	KC_NUMLOCK,
	KC_SCROLL,

	KC_MINUS,
	KC_EQUALS,
	KC_LBRACKET,
	KC_RBRACKET,
	KC_SEMICOLON,
	KC_APOSTROPHE,
	KC_GRAVE,
	KC_BACKSLASH,
	KC_COMMA,
	KC_PERIOD,
	KC_SLASH,
	KC_LALT,
	KC_NUMPADENTER,
	KC_RALT,
	
	KC_NumKeys
};

class CKeyMapping
{
public:
	KeyCode			m_eGameLibCode;			///< GameLib KeyCode
	const char*		m_pcName;				///< Name als String
	int				m_iWindowsKeyCode;		///< KeyCode unter Window (VK_irgendwas)
	int				m_iDirectInputKeyCode;	///< KeyCode unter Direct Input (DIK_irgendwas)

	static CKeyMapping  ms_axTable[KC_NumKeys];
};

#endif // #ifdef INPUTCOMMONS_H_INCLUDED

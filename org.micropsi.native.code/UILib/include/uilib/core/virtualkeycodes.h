#ifndef UILIB_VIRTUALKEYCODES_H_INCLUDED
#define UILIB_VIRTUALKEYCODES_H_INCLUDED

enum KeyModifierStates 
{
	KM_NONE		= 0,
	KM_SHIFT	= 1<<0,
	KM_CONTROL	= 1<<1,
	KM_ALT		= 1<<2		
};

enum VirtualKeyCodes
{
	VKey_NULL		= 0, 
	Vkey_Cancel		= 256,	/// VK_CANCEL

	VKey_Backspace,			/// VK_BACK
	VKey_Tab,				/// VK_TAB
	VKey_Clear,				/// VK_CLEAR
	VKey_Return,			/// VK_RETURN	
	VKey_Shift,				/// VK_SHIFT
	VKey_Control,			/// VK_CONTROL
	VKey_Alt,				/// VK_MENU
	VKey_Pause,				/// VK_PAUSE
	VKey_Capital,			/// VK_CAPITAL
	VKey_Escape,			/// VK_ESCAPE
	VKey_Space,				/// VK_SPACE

	VKey_PageUp,			/// VK_PRIOR
	VKey_PageDown,			/// VK_NEXT
	VKey_End,				/// VK_END
	VKey_Home,				/// VK_HOME
	VKey_Left,				/// VK_LEFT
	VKey_Up,				/// VK_UP
	VKey_Right,				/// VK_RIGHT
	VKey_Down,				/// VK_DOWN

	VKey_Select,			/// VK_SELECT
	VKey_Print,				/// VK_PRINT
	VKey_Execute,			/// VK_EXECUTE
	VKey_Snapshot,			/// VK_SNAPSHOT
	VKey_Insert,			/// VK_INSERT
	VKey_Delete,			/// VK_DELETE
	VKey_Help,				/// VK_HELP

	VKey_LeftWin,			/// VK_LWIN
	VKey_RightWin,			/// VK_RWIN
	VKey_Apps,				/// VK_APPS

	VKey_NumPad0,			/// VK_NUMPAD0
	VKey_NumPad1,			/// VK_NUMPAD1
	VKey_NumPad2,			/// VK_NUMPAD2
	VKey_NumPad3,			/// VK_NUMPAD3
	VKey_NumPad4,			/// VK_NUMPAD4
	VKey_NumPad5,			/// VK_NUMPAD5
	VKey_NumPad6,			/// VK_NUMPAD6
	VKey_NumPad7,			/// VK_NUMPAD7
	VKey_NumPad8,			/// VK_NUMPAD8
	VKey_NumPad9,			/// VK_NUMPAD9
	VKey_Mul,				/// VK_MULITPLY
	VKey_Add,				/// VK_ADD
	VKey_Separator,			/// VK_SEPARATOR
	Vkey_Sub,				/// VK_SUBTRACT
	VKey_DecimalPad,		/// VK_DECIMAL
	VKey_Div,				/// VK_DIVIDE

	VKey_F1,				/// VK_F1
	VKey_F2,				/// VK_F2
	VKey_F3,				/// VK_F3
	VKey_F4,				/// VK_F4
	VKey_F5,				/// VK_F5
	VKey_F6,				/// VK_F6
	VKey_F7,				/// VK_F7
	VKey_F8,				/// VK_F8
	VKey_F9,				/// VK_F9
	VKey_F10,				/// VK_F10
	VKey_F11,				/// VK_F11
	VKey_F12,				/// VK_F12
	VKey_Numlock,			/// VK_NUMLOCK
	VKey_Scroll,			/// VK_SCROLL
	VKey_Paragraph			/// paragraph sign; appears to be strange on microsoft keyboard
};

#endif // ifndef UILIB_VIRTUALKEYCODES_H_INCLUDED


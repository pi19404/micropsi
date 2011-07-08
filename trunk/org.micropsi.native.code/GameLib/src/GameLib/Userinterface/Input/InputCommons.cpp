#include "stdafx.h"
#include "GameLib/UserInterface/Input/InputCommons.h"

#include "windows.h"
#include "dinput.h"

CKeyMapping CKeyMapping::ms_axTable[KC_NumKeys] = { 
	{ KC_NONE,		"none",			-1,				-1 },
	{ KC_LBUTTON,	"lbutton",		VK_LBUTTON,		-1 },
	{ KC_RBUTTON,	"rbutton",		VK_RBUTTON,		-1 },
	{ KC_MBUTTON,	"mbutton",		VK_MBUTTON,		-1 },
	{ KC_BACK,		"backspace",	VK_BACK,		DIK_BACKSPACE },	// == DIK_BACK
	{ KC_TAB,		"tab",			VK_TAB,			DIK_TAB },
	{ KC_RETURN,	"return",		VK_RETURN,		DIK_RETURN },
	{ KC_LSHIFT,	"lshift",		VK_SHIFT,		DIK_LSHIFT },
	{ KC_RSHIFT,	"rshift",		VK_SHIFT,		DIK_RSHIFT },
	{ KC_LCONTROL,	"lcontrol",		VK_CONTROL,		DIK_LCONTROL },
	{ KC_RCONTROL,	"rcontrol",		VK_CONTROL,		DIK_RCONTROL },
	{ KC_MENU,		"menu",			VK_MENU,		-1 },
	{ KC_PAUSE,		"pause",		VK_PAUSE,		DIK_PAUSE },
	{ KC_CAPITAL,	"capslock",		VK_CAPITAL,		DIK_CAPSLOCK },		// == DIK_CAPITAL
	{ KC_ESCAPE,	"escape",		VK_ESCAPE,		DIK_ESCAPE },
	{ KC_SPACE,		"space",		VK_SPACE,		DIK_SPACE },
	{ KC_PRIOR,		"pageup",		VK_PRIOR,		DIK_PGUP },			// == DIK_PRIOR 
	{ KC_NEXT,		"pagedown",		VK_NEXT,		DIK_PGDN },			// == DIK_NEXT
	{ KC_END,		"end",			VK_END	,		DIK_END },
	{ KC_HOME,		"home",			VK_HOME,		DIK_HOME },
	{ KC_LEFT,		"left",			VK_LEFT,		DIK_LEFT },			// == DIK_LEFTARROW
	{ KC_UP,		"up",			VK_UP,			DIK_UP },			// == DIK_UPARROW
	{ KC_RIGHT,		"right",		VK_RIGHT,		DIK_RIGHT },		// == DIK_RIGHTARROW
	{ KC_DOWN,		"down",			VK_DOWN,		DIK_DOWN },			// == DIK_DOWNARROW
	{ KC_SELECT,	"select",		VK_SELECT,		-1 },
	{ KC_PRINT,		"print",		VK_PRINT,		DIK_SYSRQ },
	{ KC_EXECUTE,	"execute",		VK_EXECUTE,		-1 },
	{ KC_SNAPSHOT,	"snapshot",		VK_SNAPSHOT,	DIK_SYSRQ },
	{ KC_INSERT,	"insert",		VK_INSERT,		DIK_INSERT },
	{ KC_DELETE,	"delete",		VK_DELETE,		DIK_DELETE },
	{ KC_HELP,		"help",			VK_HELP,		-1 },

	{ KC_1,			"1",			'1',			DIK_1 },		
	{ KC_2,			"2",			'2',			DIK_2 },
	{ KC_3,			"3",			'3',			DIK_3 },
	{ KC_4,			"4",			'4',			DIK_4 },
	{ KC_5,			"5",			'5',			DIK_5 },
	{ KC_6,			"6",			'6',			DIK_6 },
	{ KC_7,			"7",			'7',			DIK_7 },
	{ KC_8,			"8",			'8',			DIK_8 },
	{ KC_9,			"9",			'9',			DIK_9 },
	{ KC_0,			"0",			'0',			DIK_0 },
									
	{ KC_A,			"a",			'A',			DIK_A },
	{ KC_B,			"b",			'B',			DIK_B },
	{ KC_C,			"c",			'C',			DIK_C },
	{ KC_D,			"d",			'D',			DIK_D },
	{ KC_E,			"e",			'E',			DIK_E },
	{ KC_F,			"f",			'F',			DIK_F },
	{ KC_G,			"g",			'G',			DIK_G },
	{ KC_H,			"h",			'H',			DIK_H },
	{ KC_I,			"i",			'I',			DIK_I },
	{ KC_J,			"j",			'J',			DIK_J },
	{ KC_K,			"k",			'K',			DIK_K },
	{ KC_L,			"l",			'L',			DIK_L },
	{ KC_M,			"m",			'M',			DIK_M },
	{ KC_N,			"n",			'N',			DIK_N },
	{ KC_O,			"o",			'O',			DIK_O },
	{ KC_P,			"p",			'P',			DIK_P },
	{ KC_Q,			"q",			'Q',			DIK_Q },
	{ KC_R,			"r",			'R',			DIK_R },
	{ KC_S,			"s",			'S',			DIK_S },
	{ KC_T,			"t",			'T',			DIK_T },
	{ KC_U,			"u",			'U',			DIK_U },
	{ KC_V,			"v",			'V',			DIK_V },
	{ KC_W,			"w",			'W',			DIK_W },
	{ KC_X,			"x",			'X',			DIK_X },
	{ KC_Y,			"y",			'Y',			DIK_Y },
	{ KC_Z,			"z",			'Z',			DIK_Z },

	{ KC_LWIN,		"lwin",			VK_LWIN,		DIK_LWIN },
	{ KC_RWIN,		"rwin",			VK_RWIN,		DIK_RWIN },
	{ KC_APPS,		"apps",			VK_APPS,		DIK_APPS },
		
	{ KC_NUMPAD0,	"numpad0",		KC_NUMPAD0,		DIK_NUMPAD0 },
	{ KC_NUMPAD1,	"numpad1",		KC_NUMPAD1,		DIK_NUMPAD1 },
	{ KC_NUMPAD2,	"numpad2",		KC_NUMPAD2,		DIK_NUMPAD2 },
	{ KC_NUMPAD3,	"numpad3",		KC_NUMPAD3,		DIK_NUMPAD3 },
	{ KC_NUMPAD4,	"numpad4",		KC_NUMPAD4,		DIK_NUMPAD4 },
	{ KC_NUMPAD5,	"numpad5",		KC_NUMPAD5,		DIK_NUMPAD5 },
	{ KC_NUMPAD6,	"numpad6",		KC_NUMPAD6,		DIK_NUMPAD6 },
	{ KC_NUMPAD7,	"numpad7",		KC_NUMPAD7,		DIK_NUMPAD7 },
	{ KC_NUMPAD8,	"numpad8",		KC_NUMPAD8,		DIK_NUMPAD8 },
	{ KC_NUMPAD9,	"numpad9",		KC_NUMPAD9,		DIK_NUMPAD9 },

	{ KC_MULTIPLY,	"numpadstar",	VK_MULTIPLY,	DIK_NUMPADSTAR },		// == DIK_MULTIPLY 
	{ KC_ADD,		"numpadplus",	VK_ADD,			DIK_NUMPADPLUS },		// == DIK_ADD 
	{ KC_SEPARATOR,	"separator",	VK_SEPARATOR,	-1 },
	{ KC_SUBTRACT,	"numpadminus",	VK_SUBTRACT,	DIK_NUMPADMINUS },		// == DIK_SUBTRACT
	{ KC_DECIMAL,	"numpadperiod",	VK_DECIMAL,		DIK_NUMPADPERIOD },		// == DIK_DECIMAL 
	{ KC_DIVIDE,	"numpadslash",	VK_DIVIDE,		DIK_NUMPADSLASH },		// == DIK_DIVIDE 
	{ KC_F1,		"f1",			VK_F1,			DIK_F1 },
	{ KC_F2,		"f2",			VK_F2,			DIK_F2 },
	{ KC_F3,		"f3",			VK_F3,			DIK_F3 },
	{ KC_F4,		"f4",			VK_F4,			DIK_F4 },
	{ KC_F5,		"f5",			VK_F5,			DIK_F5 },
	{ KC_F6,		"f6",			VK_F6,			DIK_F6 },
	{ KC_F7,		"f7",			VK_F7,			DIK_F7 },
	{ KC_F8,		"f8",			VK_F8,			DIK_F8 },
	{ KC_F9,		"f9",			VK_F9,			DIK_F9 },
	{ KC_F10,		"f10",			VK_F10,			DIK_F10 },
	{ KC_F11,		"f11",			VK_F11,			DIK_F11 },
	{ KC_F12,		"f12",			VK_F12,			DIK_F12 },
	{ KC_F13,		"f13",			VK_F13,			DIK_F13 },
	{ KC_F14,		"f14",			VK_F14,			DIK_F14 },
	{ KC_F15,		"f15",			VK_F15,			DIK_F15 },
	{ KC_F16,		"f16",			VK_F16,			-1 },
	{ KC_F17,		"f17",			VK_F17,			-1 },
	{ KC_F18,		"f18",			VK_F18,			-1 },
	{ KC_F19,		"f19",			VK_F19,			-1 },
	{ KC_F20,		"f20",			VK_F20,			-1 },
	{ KC_F21,		"f21",			VK_F21,			-1 },
	{ KC_F22,		"f22",			VK_F22,			-1 },
	{ KC_F23,		"f23",			VK_F23,			-1 },
	{ KC_F24,		"f24",			VK_F24,			-1 },

	{ KC_NUMLOCK,	"numlock",		VK_NUMLOCK,		DIK_NUMLOCK },
	{ KC_SCROLL,	"scroll",		VK_SCROLL,		DIK_SCROLL },

	{ KC_MINUS,		"minus",		VK_SUBTRACT,	DIK_MINUS },  
	{ KC_EQUALS,	"equals",		-1,				DIK_EQUALS },  
	{ KC_LBRACKET,	"lbracket",		-1,				DIK_LBRACKET },  
	{ KC_RBRACKET,	"rbracket",		-1,				DIK_RBRACKET },  
	{ KC_SEMICOLON,	"semicolon",	-1,				DIK_SEMICOLON },  
	{ KC_APOSTROPHE,"apostrophe",	-1,				DIK_APOSTROPHE },  
	{ KC_GRAVE,		"grave",		-1,				DIK_GRAVE },
	{ KC_BACKSLASH,	"backslash",	-1,				DIK_BACKSLASH },  
	{ KC_COMMA,		"comma",		-1,				DIK_COMMA },  
	{ KC_PERIOD,	"period",		-1,				DIK_PERIOD },  
	{ KC_SLASH,		"slash",		-1,				DIK_SLASH }, 
	{ KC_LALT,		"lalt",			-1,				DIK_LALT },  		// == DIK_LMENU
	{ KC_NUMPADENTER,"numpadenter",	-1,				DIK_NUMPADENTER }, 
	{ KC_RALT,		"ralt",			-1,				DIK_RALT } };  		// == DIK_RMENU


// bisher nicht unterstützt:

//#define DIK_OEM_102         0x56    /* <> or \| on RT 102-key keyboard (Non-U.S.) */
//#define DIK_KANA            0x70    /* (Japanese keyboard)            */
//#define DIK_ABNT_C1         0x73    /* /? on Brazilian keyboard */
//#define DIK_CONVERT         0x79    /* (Japanese keyboard)            */
//#define DIK_NOCONVERT       0x7B    /* (Japanese keyboard)            */
//#define DIK_YEN             0x7D    /* (Japanese keyboard)            */
//#define DIK_ABNT_C2         0x7E    /* Numpad . on Brazilian keyboard */
//#define DIK_NUMPADEQUALS    0x8D    /* = on numeric keypad (NEC PC98) */
//#define DIK_PREVTRACK       0x90    /* Previous Track (DIK_CIRCUMFLEX on Japanese keyboard) */
//#define DIK_AT              0x91    /*                     (NEC PC98) */
//#define DIK_COLON           0x92    /*                     (NEC PC98) */
//#define DIK_UNDERLINE       0x93    /*                     (NEC PC98) */
//#define DIK_KANJI           0x94    /* (Japanese keyboard)            */
//#define DIK_STOP            0x95    /*                     (NEC PC98) */
//#define DIK_AX              0x96    /*                     (Japan AX) */
//#define DIK_UNLABELED       0x97    /*                        (J3100) */

//#define DIK_NEXTTRACK       0x99    /* Next Track */
//#define DIK_MUTE            0xA0    /* Mute */
//#define DIK_CALCULATOR      0xA1    /* Calculator */
//#define DIK_PLAYPAUSE       0xA2    /* Play / Pause */
//#define DIK_MEDIASTOP       0xA4    /* Media Stop */
//#define DIK_VOLUMEDOWN      0xAE    /* Volume - */
//#define DIK_VOLUMEUP        0xB0    /* Volume + */
//#define DIK_WEBHOME         0xB2    /* Web home */
//#define DIK_NUMPADCOMMA     0xB3    /* , on numeric keypad (NEC PC98) */
//#define DIK_SYSRQ           0xB7
//#define DIK_POWER           0xDE    /* System Power */
//#define DIK_SLEEP           0xDF    /* System Sleep */
//#define DIK_WAKE            0xE3    /* System Wake */
//#define DIK_WEBSEARCH       0xE5    /* Web Search */
//#define DIK_WEBFAVORITES    0xE6    /* Web Favorites */
//#define DIK_WEBREFRESH      0xE7    /* Web Refresh */
//#define DIK_WEBSTOP         0xE8    /* Web Stop */
//#define DIK_WEBFORWARD      0xE9    /* Web Forward */
//#define DIK_WEBBACK         0xEA    /* Web Back */
//#define DIK_MYCOMPUTER      0xEB    /* My Computer */
//#define DIK_MAIL            0xEC    /* Mail */
//#define DIK_MEDIASELECT     0xED    /* Media Select */


#include "stdafx.h"
#include "GameLib/UserInterface/Input/InputCondition.h"
#include "GameLib/Userinterface/Input/GamepadState.h"

#include "baselib/str.h"
#include "GameLib/UserInterface/Input/InputManager.h"

//---------------------------------------------------------------------------------------------------------------------
CInputCondition::CInputCondition()
{
	m_eCondition = CD_INVALID;
	m_iButton = -1;
	m_iAxis = -1;
	m_fComparisionValue = 0.0f;
	m_fUserDefinesAxisValue = 0.0f;
}
//---------------------------------------------------------------------------------------------------------------------
CInputCondition::CInputCondition(const char* p_pcCondition, float p_fUserDefinesAxisValue)
{
	m_eCondition = CD_INVALID;
	m_iButton = -1;
	m_iAxis = -1;
	m_fComparisionValue = 0.0f;
	m_fUserDefinesAxisValue = p_fUserDefinesAxisValue;

	CStr s = p_pcCondition;
	int iColonPos = s.Find(":");
	if(iColonPos < 0)	{ return; }		// ungültiges Format :(
	CStr sDevice = s.Left(iColonPos);
	s = s.Right(s.GetLength() - 1 - sDevice.GetLength());

	if(sDevice == "keyboard")
	{
		m_eDevice = IDEV_KEYBOARD;
	}
	else if(sDevice == "mouse")
	{
		m_eDevice = IDEV_MOUSE;
	}
	else if(sDevice == "joystick0"  ||  sDevice == "gamepad0")
	{
		m_eDevice = IDEV_JOYSTICK0;
	}
	else if(sDevice == "joystick1"  ||  sDevice == "gamepad1")
	{
		m_eDevice = IDEV_JOYSTICK1;
	}
	else if(sDevice == "joystick2"  ||  sDevice == "gamepad2")
	{
		m_eDevice = IDEV_JOYSTICK2;
	}
	else if(sDevice == "joystick3"  ||  sDevice == "gamepad3")
	{
		m_eDevice = IDEV_JOYSTICK3;
	}
	else
	{
		DebugPrint("Error: InputManager Condition '%s' - unknown device, must be 'keyboard', 'mouse', 'gamepad0', 'joystick0' etc.", p_pcCondition);
		return; // unbekanntes Device :(
	}

	int iPointPos = s.Find(".");
	
	// sObject: die Taste bzw. Achse, z.B. "Escape", "w" oder "button0"
	CStr sObject = s.Left(iPointPos);

	// state: was soll das objekt machen? z.B. "down", "up", "value > 0.0f" --> darf u.U. auch leer sein!
	CStr sState;
	if(iPointPos >= 0) 
	{
		sState = s.Right(s.GetLength() - 1 - sObject.GetLength());
	}

	if(m_eDevice == IDEV_JOYSTICK0  ||  m_eDevice == IDEV_JOYSTICK1  ||  m_eDevice == IDEV_JOYSTICK2 ||  m_eDevice == IDEV_JOYSTICK3)
	{
		if(sObject.Left(6) == "button")
		{
			sObject.Delete(0, 6);
			m_iButton = sObject.ToInt();
		}
		else if(sObject.Left(4) == "axis")
		{
			sObject.Delete(0, 4);
			m_iAxis = sObject.ToInt();
		}
		else if(sObject == "x")
		{
			m_iAxis = CGamepadState::AX_X;
		}
		else if(sObject == "y")
		{
			m_iAxis = CGamepadState::AX_Y;
		}
		else if(sObject == "z")
		{
			m_iAxis = CGamepadState::AX_Z;
		}
		else if(sObject == "rx")
		{
			m_iAxis = CGamepadState::AX_XROT;
		}
		else if(sObject == "ry")
		{
			m_iAxis = CGamepadState::AX_YROT;
		}
		else if(sObject == "rz")
		{
			m_iAxis = CGamepadState::AX_ZROT;
		}
		else if(sObject == "slider1")
		{
			m_iAxis = CGamepadState::AX_SLIDER1;
		}
		else if(sObject == "slider2")
		{
			m_iAxis = CGamepadState::AX_SLIDER2;
		}
		else if(sObject == "anykey"  ||  sObject == "anybutton")
		{
			m_iButton = -1;
			m_eCondition = CD_ANYBUTTON;
		}
		else
		{
			DebugPrint("Error: InputManager Condition '%s' - '%s' is not a valid object for gamepads", p_pcCondition, sObject.c_str());
			return;  // ungültiges Format :(
		}
	}
	else if(m_eDevice == IDEV_KEYBOARD)
	{
		if(sObject == "anykey"  ||  sObject == "anybutton")
		{
			m_iButton = -1;
			m_eCondition = CD_ANYBUTTON;
		}
		else if(sObject == "scrolllock")
		{
			m_iButton = -1;
			m_eCondition = CD_SCROLLLOCKACTIVE;
		}
		else
		{
			KeyCode eKey = CInputManager::TransLateStringToKey(sObject.c_str());
			if(eKey == KC_NONE)
			{
				DebugPrint("Error: InputManager Condition '%s' - unknown keyboard key '%s'", p_pcCondition, sObject.c_str());
				return;	// unbekannte Taste :(
			}
			m_iButton = eKey;
			if(CKeyMapping::ms_axTable[eKey].m_iDirectInputKeyCode < 0)
			{
				DebugPrint("Error: InputManager Condition '%s' - keyboard key '%s' does not have a directinput mapping", p_pcCondition, sObject.c_str());
				return; // hat keine DirectInput-Entsprechung :)
			}
		}
	}
	else if(m_eDevice == IDEV_MOUSE)
	{
		if(sObject.Left(6) == "button")
		{
			sObject.Delete(0, 6);
			m_iButton = sObject.ToInt();
		}
		else if(sObject == "leftbutton")		
		{
			m_iButton = 0;
		}
		else if(sObject == "rightbutton")
		{
			m_iButton = 1;
		}
		else if(sObject == "middlebutton")
		{
			m_iButton = 2;
		}
		else if(sObject.Left(4) == "axis")
		{
			sObject.Delete(0, 4);
			m_iAxis = sObject.ToInt();
		}
		else if(sObject == "x")
		{
			m_iAxis = 0;
		}
		else if(sObject == "y")
		{
			m_iAxis = 1;
		}
		else if(sObject == "wheel")
		{
			m_iAxis = 2;
		}
		else if(sObject == "anykey"  ||  sObject == "anybutton")
		{
			m_iButton = -1;
			m_eCondition = CD_ANYBUTTON;
		}
		else
		{
			DebugPrint("Error: InputManager Condition '%s' - '%s' is illegal for mouse device", p_pcCondition, sObject.c_str());
			return;  // ungültiges Format :(
		}
	}
	else
	{
		assert(false);	// unbekanntes device .... sollte vorher schon abgefangen sein ...
	}

	if(sState == "up" || sState == "down" || sState == "repeatevent" || sState == "held" || sState == "notheld")
	{
		if(m_iButton == -1)	
		{
			DebugPrint("Error: InputManager Condition '%s' - '%s' can only be used with buttons", p_pcCondition, sState.c_str());  
			return;
		}

		if(sState == "up")					m_eCondition = CD_BUTTONUP;
		else if(sState == "down")			m_eCondition = CD_BUTTONDOWN;
		else if(sState == "repeatevent")	m_eCondition = CD_BUTTONREPEATEVENT;
		else if(sState == "held")			m_eCondition = CD_BUTTONHELD;
		else if(sState == "notheld")		m_eCondition = CD_BUTTONNOTHELD;
	}
	else if(sState.Left(5) == "value")
	{
		if(m_iAxis == -1)
		{
			DebugPrint("Error: InputManager Condition '%s' - 'value' can only be used with axis", p_pcCondition);  
			return;
		}

		CStr sOperator;
		if(sState[6] == '=')
		{
			sOperator = sState.Mid(5, 2);
		}
		else
		{
			sOperator = sState.Mid(5, 1);
		}
		sState.Delete(0, 5 + sOperator.GetLength());
		m_fComparisionValue = sState.ToFloat();

		if(sOperator == "=")		{ m_eCondition = CD_AXISEQUAL; }
		else if(sOperator == "<")	{ m_eCondition = CD_AXISLESS; }
		else if(sOperator == ">")	{ m_eCondition = CD_AXISGREATER; }
		else if(sOperator == "<=")	{ m_eCondition = CD_AXISLESSOREQUAL; }
		else if(sOperator == ">=")	{ m_eCondition = CD_AXISGREATEROREQUAL; }
		else if(sOperator == "==")	{ m_eCondition = CD_AXISEQUAL; }
		else if(sOperator == "!=")	{ m_eCondition = CD_AXISNOTEQUAL; }	
		else 
		{
			DebugPrint("Error: InputManager Condition '%s' - unknown comparison operator; must be <, >, <=, >=, == or !=", p_pcCondition);  
			return;
		};
	}
	else if(sState.GetLength() == 0)
	{
		// kein state ist erlaubt, wenn es eine Anykey-Condition oder eine ScrollLock-Condition oder eine Achse ist
		if(m_eCondition != CD_ANYBUTTON  &&  m_eCondition != CD_SCROLLLOCKACTIVE  &&  m_iAxis == -1)
		{
			DebugPrint("Error: InputManager Condition '%s' - you must specify a state like for instance '.down' or '.up'", p_pcCondition);  
			return;
		}
		if(m_iAxis != -1)
		{
			m_eCondition = CD_ALWAYSTRUE; 
		}
	}
	else
	{
		DebugPrint("Error: InputManager Condition '%s' - unknown state '%s'", p_pcCondition, sState.c_str());  
		return;	
	}
}
//---------------------------------------------------------------------------------------------------------------------
CInputCondition::~CInputCondition()
{
}
//---------------------------------------------------------------------------------------------------------------------

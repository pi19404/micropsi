#include "stdafx.h"
#include "GameLib/UserInterface/Input/InputManager.h"

#include "baselib/str.h"

#include "GameLib/UserInterface/Input/GamepadState.h"
#include "GameLib/UserInterface/Input/KeyboardState.h"
#include "GameLib/UserInterface/Input/MouseState.h"

#include "windowsx.h"

using std::string;
using std::map;

//---------------------------------------------------------------------------------------------------------------------
CInputManager::CInputManager()
{
	m_pxKeyboardState = 0;
	m_pxMouseState = 0;

	m_fKeyStrokeDelay = 0.25f;          
    m_fKeyStrokeRepeatTime = 0.05f;

	m_bUseEventQueue = false;
}

//---------------------------------------------------------------------------------------------------------------------
CInputManager::~CInputManager()
{
	ClearMappings();

	for(unsigned int i=0; i<m_apxGamepadStates.size(); ++i)
	{
		delete m_apxGamepadStates[i];
	}
	delete m_pxMouseState;
	delete m_pxKeyboardState;
}

//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::TGroup::Clear()
{
	if (!m_xInputConditions.empty())
	{
		map<const string, TCondition>::iterator xConditionIterator;
		for (xConditionIterator = m_xInputConditions.begin(); xConditionIterator != m_xInputConditions.end(); xConditionIterator++)
		{
			delete xConditionIterator->second.m_pxCondition;
		}
	
		m_xInputConditions.clear();
	}
}
//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::ClearMappings()
{
	if (!m_xGroups.empty())
	{
		map<const string, TGroup>::iterator xGroupIterator;
		for (xGroupIterator = m_xGroups.begin(); xGroupIterator != m_xGroups.end(); xGroupIterator++)
		{
			xGroupIterator->second.Clear();
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::ClearMappings(const string& sGroup)
{
	if (!m_xGroups.empty())
	{
		map<const string, TGroup>::iterator xGroupIterator = m_xGroups.find(sGroup);

		if (xGroupIterator != m_xGroups.end())
		{
			xGroupIterator->second.Clear();
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
CPnt 
CInputManager::GetMousePos(HWND p_hWnd, LPARAM p_lParam) const
{
	CPnt p; 
	p.x = GET_X_LPARAM(p_lParam);
	p.y = GET_Y_LPARAM(p_lParam);
	return p;
}

//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::SetAxisMode(int iGamePad, int iAxis, int iModeFlags)
{
	if(iGamePad >= 0  && iGamePad < (int) m_apxGamepadStates.size())
	{
		m_apxGamepadStates[iGamePad]->SetAxisMode(iAxis, iModeFlags);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::HandleWindowsMessage(HWND p_hWnd, UINT p_uiMsg, WPARAM p_wParam, LPARAM p_lParam)
{
	if(!m_bUseEventQueue)
	{
		return;
	}

	switch(p_uiMsg)
	{				
		case WM_MOUSEMOVE:
			m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_MOUSE_MOVE, 0, GetMousePos(p_hWnd, p_lParam)));
			break;

		case WM_LBUTTONDOWN: 
			::SetCapture(p_hWnd);
			m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_MOUSE_BUTTON_DOWN, MBTN_LEFT, GetMousePos(p_hWnd, p_lParam)));
			break;

		case WM_RBUTTONDOWN: 
			::SetCapture(p_hWnd);
			m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_MOUSE_BUTTON_DOWN, MBTN_RIGHT, GetMousePos(p_hWnd, p_lParam)));
			break;

		case WM_MBUTTONDOWN: 
			::SetCapture(p_hWnd);
			m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_MOUSE_BUTTON_DOWN, MBTN_MIDDLE, GetMousePos(p_hWnd, p_lParam)));
			break;

		case WM_LBUTTONUP:
			::SetCapture(p_hWnd);
			m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_MOUSE_BUTTON_UP, MBTN_LEFT, GetMousePos(p_hWnd, p_lParam)));
			break;

		case WM_RBUTTONUP:
			::SetCapture(p_hWnd);
			m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_MOUSE_BUTTON_UP, MBTN_RIGHT, GetMousePos(p_hWnd, p_lParam)));
			break;

		case WM_MBUTTONUP:
			::SetCapture(p_hWnd);
			m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_MOUSE_BUTTON_UP, MBTN_MIDDLE, GetMousePos(p_hWnd, p_lParam)));
			break;

		case WM_LBUTTONDBLCLK:
			::SetCapture(p_hWnd);
			m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_MOUSE_BUTTON_DBLCLCK, MBTN_LEFT, GetMousePos(p_hWnd, p_lParam)));
			break;

		case WM_RBUTTONDBLCLK:
			::SetCapture(p_hWnd);
			m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_MOUSE_BUTTON_DBLCLCK, MBTN_RIGHT, GetMousePos(p_hWnd, p_lParam)));
			break;
		
		case WM_CHAR:
			m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_KEY_CHAR, (unsigned char) p_wParam)); 
			break;

		case WM_SYSKEYDOWN:			
		case WM_KEYDOWN: 
			m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_KEY_DOWN, (int) TranslateWindowsKey(p_wParam))); 
			break;

		case WM_SYSKEYUP:
		case WM_KEYUP:
			m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_KEY_UP, (int) TranslateWindowsKey(p_wParam))); 
			break;
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::RemoveGamepads()
{
	m_apxGamepadStates.clear();
}

//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::AddGamePad(IDirectInputDevice8A* pDIDGamepad)
{
	m_apxGamepadStates.push_back(new CGamepadState(pDIDGamepad));
}

//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::SetKeyboard(IDirectInputDevice8A* pDIDKeyBoard)
{
	m_pxKeyboardState = new CKeyboardState(pDIDKeyBoard);
}

//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::SetMouse(IDirectInputDevice8A* pDIDMouse)
{
	assert(pDIDMouse);
	if (pDIDMouse)
	{
		m_pxMouseState = new CMouseState(pDIDMouse);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::StartIterateEvents(EventIterator& p_xIterator) const
{
	p_xIterator = 0;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	liefert das nächste Event aus der Queue oder 0, wenn keine weiteren vorhanden sind
*/
const CInputEvent*
CInputManager::IterateEvents(EventIterator& p_xIterator) const
{
	if(p_xIterator >= m_axEventQueue.size())
	{
		return 0;
	}

	const CInputEvent* pxEvent = &m_axEventQueue[p_xIterator];
	p_xIterator++;
	return pxEvent;
}

//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::FlushEventQueue()
{
	m_axEventQueue.clear();
}

//---------------------------------------------------------------------------------------------------------------------
/**
	liefert true bei Erfolg, false sonst
*/
bool
CInputManager::Map(string p_sCondition, string p_sName, string p_sGroup)
{
	return Map(p_sCondition, p_sName, 0.0f, p_sGroup);
}

//---------------------------------------------------------------------------------------------------------------------
/**
	liefert true bei Erfolg, false sonst
*/
bool					
CInputManager::Map(std::string p_sCondition, std::string p_sName, float p_fAxisValue, std::string p_sGroup)
{
	CStr sCondition = p_sCondition.c_str();
	if(sCondition.Find(":control.") >= 0)
	{
		if(sCondition.Find("keyboard:control.notheld") >= 0)
		{
			sCondition.Replace("keyboard:control.notheld", "keyboard:rcontrol.notheld && keyboard:lcontrol.notheld");
		}
		else
		{
			sCondition.Replace(":control.", ":lcontrol.");
			if(!Map(sCondition.c_str(), p_sName, p_fAxisValue, p_sGroup))
			{
				return false;
			}
			sCondition = p_sCondition.c_str();
			sCondition.Replace(":control.", ":rcontrol.");
			return Map(sCondition.c_str(), p_sName, p_fAxisValue, p_sGroup); 
		}
	}
	if(sCondition.Find(":shift.") >= 0)
	{
		if(sCondition.Find("keyboard:shift.notheld") >= 0)
		{
			sCondition.Replace("keyboard:shift.notheld", "keyboard:rshift.notheld && keyboard:lshift.notheld");
		}
		else
		{
			sCondition.Replace(":shift.", ":lshift.");
			if(!Map(sCondition.c_str(), p_sName, p_fAxisValue, p_sGroup))
			{
				return false;
			}
			sCondition = p_sCondition.c_str();
			sCondition.Replace(":shift.", ":rshift.");
			return Map(sCondition.c_str(), p_sName, p_fAxisValue, p_sGroup); 
		}
	}
	if(sCondition.Find(":alt.") >= 0)
	{
		if(sCondition.Find("keyboard:alt.notheld") >= 0)
		{
			sCondition.Replace("keyboard:alt.notheld", "keyboard:ralt.notheld && keyboard:lalt.notheld");
		}
		else
		{
			sCondition.Replace(":alt.", ":lalt.");
			if(!Map(sCondition.c_str(), p_sName, p_fAxisValue, p_sGroup))
			{
				return false;
			}
			sCondition = p_sCondition.c_str();
			sCondition.Replace(":alt.", ":ralt.");
			return Map(sCondition.c_str(), p_sName, p_fAxisValue, p_sGroup); 
		}
	}
	if(sCondition.Find(":win.") >= 0)
	{
		if(sCondition.Find("keyboard:win.notheld") >= 0)
		{
			sCondition.Replace("keyboard:win.notheld", "keyboard:rwin.notheld && keyboard:lwin.notheld");
		}
		else
		{
			sCondition.Replace(":win.", ":lwin.");
			if(!Map(sCondition.c_str(), p_sName, p_fAxisValue, p_sGroup))
			{
				return false;
			}
			sCondition = p_sCondition.c_str();
			sCondition.Replace(":win.", ":rwin.");
			return Map(sCondition.c_str(), p_sName, p_fAxisValue, p_sGroup); 
		}
	}

	// Gruppe suchen; anlegen wenn nicht gefunden
	map<const string, TGroup>::iterator xGroupIterator;
	xGroupIterator = m_xGroups.find(p_sGroup);
	if(m_xGroups.size() == 0  ||  xGroupIterator == m_xGroups.end())
	{
		TGroup xGroup;
		m_xGroups[p_sGroup] = xGroup;
		xGroupIterator = m_xGroups.find(p_sGroup);
		assert(xGroupIterator != m_xGroups.end());
	}
	TGroup& rxGroup = xGroupIterator->second;

	// Condition suchen; anlegen wenn nicht gefunden
	map<const string, TCondition>::iterator xConditionIterator;
	xConditionIterator = rxGroup.m_xInputConditions.find(p_sName);
	if(rxGroup.m_xInputConditions.size() == 0  ||  xConditionIterator == rxGroup.m_xInputConditions.end())
	{
		TCondition xCondition;
		xCondition.m_pxCondition = new CComplexInputCondition(p_sCondition);
		xCondition.m_iEventsWaiting = 0;
		xCondition.m_fAxisValue = 0.0f;

		rxGroup.m_xInputConditions[p_sName] = xCondition;
		xConditionIterator = rxGroup.m_xInputConditions.find(p_sName);
		assert(xConditionIterator != rxGroup.m_xInputConditions.end());
	}

	// der komplexen Condition eine neue Alternative hinzufügen - nämlich das übergebene Mapping! 
	CComplexInputCondition* pxComplexCondition = xConditionIterator->second.m_pxCondition;
	int iAlternative = pxComplexCondition->AddAlternative();

	sCondition.Remove(' ');
	while(sCondition.GetLength() > 0)
	{
		int iSplitPos = sCondition.Find("&&");
		CStr sConditionAtom;
		if(iSplitPos >=0)
		{
			sConditionAtom = sCondition.Left(iSplitPos);
			sCondition = sCondition.Mid(iSplitPos+2, sCondition.GetLength());
		}
		else
		{
			sConditionAtom = sCondition;
			sCondition.Clear();
		}

		CInputCondition xCondition = CInputCondition(sConditionAtom.c_str(), p_fAxisValue);
		if(!xCondition.IsValid())
		{
			assert(false);	// condition syntax ungültig :(  -- SIEHE LOGGER FÜR FEHLERMELDUNG
			return false;
		}
		pxComplexCondition->AddCondition(iAlternative, xCondition);
	}

	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CInputManager::IsFullfilled(string p_sName, string p_sGroup) const
{
	const TCondition* pxCondition = GetCondition(p_sName, p_sGroup);
	assert(pxCondition);
	if(!pxCondition)
	{
		return false;
	}

	float fAxisValue;
	return NumConditionEvents(*(pxCondition->m_pxCondition), fAxisValue) > 0;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	liefert die Anzahl Events, die seit dem letzten Tick für eine Condition erzeugt werden müssen
	liefert ausserdem den (absolut) größten Achsenwert, der von einer erfüllten Bedingung erreicht wird
*/
int
CInputManager::NumConditionEvents(const CComplexInputCondition& p_xrCondition, float& po_fAxisValue) const
{
	float fAxisValue = 0.0f;
	int iTotalEvents = 0;

	for(int iAlternative=0; iAlternative<p_xrCondition.NumAlternatives(); ++iAlternative)
	{
		int iEventsForThisAlternative = 0;
		float fAxisValueforThisAlternative = 0.0f;
		for(int iCondition=0; iCondition<p_xrCondition.NumConjunctions(iAlternative); ++iCondition)
		{
			// eine Alternative besteht aus einer Konjuktion von Einzelbedingungen -- alle ansehen!
			
			const CInputCondition& rxCondition =  p_xrCondition.GetCondition(iAlternative, iCondition);
			int iEventsForThisConditionPart = ConditionFullfilledCount(rxCondition);

			if(iEventsForThisConditionPart > 0)
			{
				fAxisValueforThisAlternative = GetAxisValue(rxCondition);
			}

			if(iCondition == 0)
			{
				// erste Kondition in dieser Konjuktion
				iEventsForThisAlternative = iEventsForThisConditionPart;
			}
			else
			{
				// nicht die erste Kondition in dieser Konjunktion
				iEventsForThisAlternative = min(iEventsForThisAlternative, iEventsForThisConditionPart);
			}
		}

		// die Zahl der generierten Events kann "unendlich" sein --> nur eines Zählen
		if(iEventsForThisAlternative == INT_MAX)
		{
			iEventsForThisAlternative = 1;
		}
	
		if(iEventsForThisAlternative > 0)
		{
			iTotalEvents += iEventsForThisAlternative;
			if(fabs(fAxisValueforThisAlternative) > fabs(fAxisValue))
			{
				fAxisValue = fAxisValueforThisAlternative;
			}
		}
	}

	po_fAxisValue = fAxisValue;
	return iTotalEvents;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	Liefert den Achsenwert einer Bedingung. Der zurückgelieferte Wert ist der letzte, der für diese Bedingung 
	beobachtet wurde. ACHTUNG: sagt nichts darüber aus, ob die Bedingung erfüllt ist!!!
	Sprich: wenn einer Taste auf dem Keyboard der Achsenwert 0.5 zugewiesen wird, liefert diese Funktion immer 0.5,
	ob die Taste tatsächlich gedrückt ist (~ die Bedingung erfüllt ist) muss separat erfragt werden
*/
float
CInputManager::GetAxisValue(string p_sName, string p_sGroup) const
{
	const TCondition* pxCondition = GetCondition(p_sName, p_sGroup);
	assert(pxCondition);
	if(!pxCondition)
	{
		return 0.0f;
	}
	
	return pxCondition->m_fAxisValue;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	Liefert den Achsenwert eine Bedingung. Wenn in der Bedingung keine Achse vorkommt ist der Wert der nutzerdefinierte
*/
float
CInputManager::GetAxisValue(const CInputCondition& xrCondition) const
{
	if(xrCondition.IsAxisCondition())
	{
		switch(xrCondition.m_eDevice)
		{
			case IDEV_KEYBOARD:
				assert(false);		// keyboards haben keine achsen!
				return 0.0f;

			case IDEV_MOUSE:
				return (float) m_pxMouseState->GetAxisRelativeValue(xrCondition.m_iAxis);

			case IDEV_JOYSTICK0:
			case IDEV_JOYSTICK1:
			case IDEV_JOYSTICK2:
			case IDEV_JOYSTICK3:
				{
					unsigned int iJoyStickNumber = (int) xrCondition.m_eDevice - (int) IDEV_JOYSTICK0;
					if(m_apxGamepadStates.size() <= iJoyStickNumber)
					{
						return 0.0f;
					}
					const CGamepadState* pxGamePadState = m_apxGamepadStates[iJoyStickNumber];
					return pxGamePadState->GetAxisValue(xrCondition.m_iAxis);
				}

			default:	
				assert(false); // unbekanntes device
				return 0.0f;
		}
	}
	else
	{
		return xrCondition.GetUserDefinedAxisValue();
	}
}

//---------------------------------------------------------------------------------------------------------------------
int
CInputManager::ConditionFullfilledCount(const CInputCondition& p_xrCondition) const
{
	assert(p_xrCondition.IsValid());

	if(p_xrCondition.m_eCondition == CInputCondition::CD_ALWAYSTRUE)  return true;
	if(p_xrCondition.m_eCondition == CInputCondition::CD_ALWAYSFALSE) return false;

	switch(p_xrCondition.m_eDevice)
	{
		case IDEV_KEYBOARD:
		{
			// Keyboard

			if(!m_pxKeyboardState)
			{
				assert(false);
				return 0;
			}

			int iDirectInputKey = CKeyMapping::ms_axTable[p_xrCondition.m_iButton].m_iDirectInputKeyCode;

            if (iDirectInputKey == -1)
            {
                assert(false);
                return 0;
            }

			switch(p_xrCondition.m_eCondition)
			{
				case CInputCondition::CD_BUTTONUP:
					return m_pxKeyboardState->KeyUpEvent(iDirectInputKey) ? 1 : 0;
					break;
				case CInputCondition::CD_BUTTONDOWN:
					return m_pxKeyboardState->KeyDownEvent(iDirectInputKey) ? 1 : 0;
					break;
				case CInputCondition::CD_BUTTONREPEATEVENT:
					return NumKeyRepeatEvents(	m_pxKeyboardState->GetKeyDownTime(iDirectInputKey), 
												m_pxKeyboardState->KeyDownEvent(iDirectInputKey)); 
					break;
				case CInputCondition::CD_BUTTONHELD:
					return m_pxKeyboardState->IsKeyDown(iDirectInputKey) ? INT_MAX : 0;
					break;
				case CInputCondition::CD_BUTTONNOTHELD:
					return !m_pxKeyboardState->IsKeyDown(iDirectInputKey) ? INT_MAX : 0;
					break;
				case CInputCondition::CD_ANYBUTTON:
					return m_pxKeyboardState->IsAnyKeyDown() ? INT_MAX : 0;
				case CInputCondition::CD_SCROLLLOCKACTIVE:
					return m_pxKeyboardState->ScrollLock() ? INT_MAX : 0;
				default:
					assert(false);		// unbekannte Bedinung für dieses Device!
					return 0;
			}			

			break;
		}
		case IDEV_MOUSE:
		{
			// Mouse

			if(!m_pxMouseState)
			{
				assert(false);
				return 0;
			}
			switch(p_xrCondition.m_eCondition)
			{
				case CInputCondition::CD_BUTTONUP:
					return m_pxMouseState->ButtonUpEvent(p_xrCondition.m_iButton) ? 1 : 0;

				case CInputCondition::CD_BUTTONDOWN:
					return m_pxMouseState->ButtonDownEvent(p_xrCondition.m_iButton) ? 1 : 0;

				case CInputCondition::CD_BUTTONREPEATEVENT:
					assert(false); // TODO: implement
					return 0;

				case CInputCondition::CD_BUTTONHELD:
					return m_pxMouseState->IsButtonDown(p_xrCondition.m_iButton) ? INT_MAX : 0;

				case CInputCondition::CD_BUTTONNOTHELD:
					return !m_pxMouseState->IsButtonDown(p_xrCondition.m_iButton) ? INT_MAX : 0;

				case CInputCondition::CD_ANYBUTTON:
					return m_pxMouseState->IsAnyButtonDown() ? INT_MAX : 0;

				case CInputCondition::CD_AXISEQUAL:
				case CInputCondition::CD_AXISNOTEQUAL:
				case CInputCondition::CD_AXISLESS:
				case CInputCondition::CD_AXISGREATER:
				case CInputCondition::CD_AXISLESSOREQUAL:
				case CInputCondition::CD_AXISGREATEROREQUAL:
					{
						float fValue = (float) m_pxMouseState->GetAxisRelativeValue(p_xrCondition.m_iAxis);
						return p_xrCondition.CheckAxisCondition(fValue) ? INT_MAX : 0;
					}

				default:
					assert(false);		// unbekannte Bedinung für dieses Device!
					return 0;
			}
			break;
		}
		case IDEV_JOYSTICK0:
		case IDEV_JOYSTICK1:
		{
			// Joystick / Gamepad

			unsigned int iJoyStickNumber = p_xrCondition.m_eDevice == IDEV_JOYSTICK0 ? 0 : 1;
			if(m_apxGamepadStates.size() <= iJoyStickNumber)
			{
				return 0;
			}
			const CGamepadState* pxGamePadState = m_apxGamepadStates[iJoyStickNumber];
			switch(p_xrCondition.m_eCondition)
			{
				case CInputCondition::CD_BUTTONUP:
					return pxGamePadState->ButtonUpEvent(p_xrCondition.m_iButton) ? 1 : 0;

				case CInputCondition::CD_BUTTONDOWN:
					return pxGamePadState->ButtonDownEvent(p_xrCondition.m_iButton) ? 1 : 0;

				case CInputCondition::CD_BUTTONREPEATEVENT:
					assert(false); // TODO: implement
					return 0;

				case CInputCondition::CD_BUTTONHELD:
					return pxGamePadState->IsButtonDown(p_xrCondition.m_iButton) ? INT_MAX : 0;

				case CInputCondition::CD_BUTTONNOTHELD:
					return !pxGamePadState->IsButtonDown(p_xrCondition.m_iButton) ? INT_MAX : 0;

				case CInputCondition::CD_ANYBUTTON:
					return pxGamePadState->IsAnyButtonDown() ? INT_MAX : 0;

				case CInputCondition::CD_AXISEQUAL:
				case CInputCondition::CD_AXISNOTEQUAL:
				case CInputCondition::CD_AXISLESS:
				case CInputCondition::CD_AXISGREATER:
				case CInputCondition::CD_AXISLESSOREQUAL:
				case CInputCondition::CD_AXISGREATEROREQUAL:
					{
						float fValue = pxGamePadState->GetAxisValue(p_xrCondition.m_iAxis);	
						return p_xrCondition.CheckAxisCondition(fValue) ? INT_MAX : 0;
					}

				default:
					assert(false);		// unbekannte Bedinung für dieses Device!
					return 0;
			}
			break;
		}
	}

	assert(false);		// unbekanntes Device...
	return 0;
}

//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::RemoveMapping(string p_sName, string p_sGroup)
{
	if(m_xGroups.size() == 0)
	{
		assert(false);
		return;
	}

	map<const string, TGroup>::iterator xGroupIterator;
	xGroupIterator = m_xGroups.find(p_sGroup);
	if(xGroupIterator == m_xGroups.end())
	{
		assert(false);
		return;
	}

	if(xGroupIterator->second.m_xInputConditions.size() == 0)
	{
		assert(false);
		return;
	}

	map<const string, TCondition>::iterator i;
	i = xGroupIterator->second.m_xInputConditions.find(p_sName);
	if(i == xGroupIterator->second.m_xInputConditions.end())
	{
		assert(false);
		return;
	}

	delete i->second.m_pxCondition;
	xGroupIterator->second.m_xInputConditions.erase(i);
}

//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::UpdateFromDevice(float p_fDeltaSeconds)
{
	// retrieve device states; push events to normal queue

	if(m_pxKeyboardState)
	{
		m_pxKeyboardState->RetrieveFromDevice(p_fDeltaSeconds);
	}
	if(m_pxMouseState)
	{
		m_pxMouseState->RetrieveFromDevice();
	}

	for(unsigned int iPad=0; iPad<m_apxGamepadStates.size(); ++iPad)
	{
		m_apxGamepadStates[iPad]->RetrieveFromDevice();

		for(int iButton=0; iButton<m_apxGamepadStates[iPad]->GetNumButtons(); iButton++)
		{
			if(m_bUseEventQueue)
			{
				if(m_apxGamepadStates[iPad]->ButtonDownEvent(iButton))
				{
					m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_JOYSTICK_BUTTON_DOWN, iButton)); 
				}
				else if(m_apxGamepadStates[iPad]->ButtonUpEvent(iButton))
				{
					m_axEventQueue.push_back(CInputEvent(CInputEvent::IT_JOYSTICK_BUTTON_UP, iButton)); 
				}
			}
		}
	}

	// check all mappings for events and increase counters if necessary

	if(m_xGroups.size() != 0)
	{
		map<const string, TGroup>::iterator xGroupIterator;
		for(xGroupIterator = m_xGroups.begin(); xGroupIterator != m_xGroups.end(); xGroupIterator++)
		{
			if(xGroupIterator->second.m_xInputConditions.size() == 0)
			{
				continue;
			}

			map<const string, TCondition>::iterator i;
			for(i=xGroupIterator->second.m_xInputConditions.begin(); i!=xGroupIterator->second.m_xInputConditions.end(); i++)
			{
				float fAxisValue;
				int iNewEvents = NumConditionEvents(*(i->second.m_pxCondition), fAxisValue);
				if(iNewEvents > 0)
				{
					i->second.m_iEventsWaiting += iNewEvents;
					i->second.m_fAxisValue = fAxisValue;
				}
			}			
		}
	}

	m_fLastTickDuration = p_fDeltaSeconds;
}

//---------------------------------------------------------------------------------------------------------------------
KeyCode 
CInputManager::TranslateWindowsKey(WPARAM p_iVirtualKey)
{
    KeyCode eKey;
    switch(p_iVirtualKey) 
    {
        case VK_LBUTTON:    eKey = KC_LBUTTON;		break;
        case VK_RBUTTON:    eKey = KC_RBUTTON;		break;
        case VK_MBUTTON:    eKey = KC_MBUTTON;		break;
        case VK_BACK:       eKey = KC_BACK;			break;
        case VK_TAB:        eKey = KC_TAB;			break;
        case VK_RETURN:     eKey = KC_RETURN;		break;
        case VK_SHIFT:      eKey = KC_LSHIFT;		break;
        case VK_CONTROL:    eKey = KC_LCONTROL;		break;
        case VK_MENU:       eKey = KC_MENU;			break;
        case VK_PAUSE:      eKey = KC_PAUSE;		break;
        case VK_CAPITAL:    eKey = KC_CAPITAL;		break;
        case VK_ESCAPE:     eKey = KC_ESCAPE;		break;
        case VK_SPACE:      eKey = KC_SPACE;		break;
        case VK_PRIOR:      eKey = KC_PRIOR;		break;
        case VK_NEXT:       eKey = KC_NEXT;			break;
        case VK_END:        eKey = KC_END;			break;
        case VK_HOME:       eKey = KC_HOME;			break;
        case VK_LEFT:       eKey = KC_LEFT;			break;
        case VK_UP:         eKey = KC_UP;			break;
        case VK_RIGHT:      eKey = KC_RIGHT;		break;
        case VK_DOWN:       eKey = KC_DOWN;			break;
        case VK_SELECT:     eKey = KC_SELECT;		break;
        case VK_PRINT:      eKey = KC_PRINT;		break;
        case VK_EXECUTE:    eKey = KC_EXECUTE;		break;
        case VK_SNAPSHOT:   eKey = KC_SNAPSHOT;		break;
        case VK_INSERT:     eKey = KC_INSERT;		break;
        case VK_DELETE:     eKey = KC_DELETE;		break;
        case VK_HELP:       eKey = KC_HELP;			break;
        case '0':           eKey = KC_0;			break;
        case '1':           eKey = KC_1;			break;
        case '2':           eKey = KC_2; 			break;
        case '3':           eKey = KC_3; 			break;
        case '4':           eKey = KC_4; 			break;
        case '5':           eKey = KC_5; 			break;
        case '6':           eKey = KC_6; 			break;
        case '7':           eKey = KC_7; 			break;
        case '8':           eKey = KC_8; 			break;
        case '9':           eKey = KC_9; 			break;
        case 'A':           eKey = KC_A; 			break;
        case 'B':           eKey = KC_B; 			break;
        case 'C':           eKey = KC_C; 			break;
        case 'D':           eKey = KC_D; 			break;
        case 'E':           eKey = KC_E; 			break;
        case 'F':           eKey = KC_F; 			break;
        case 'G':           eKey = KC_G; 			break;
        case 'H':           eKey = KC_H; 			break;
        case 'I':           eKey = KC_I; 			break;
        case 'J':           eKey = KC_J; 			break;
        case 'K':           eKey = KC_K; 			break;
        case 'L':           eKey = KC_L; 			break;
        case 'M':           eKey = KC_M; 			break;
        case 'N':           eKey = KC_N; 			break;
        case 'O':           eKey = KC_O; 			break;
        case 'P':           eKey = KC_P; 			break;
        case 'Q':           eKey = KC_Q; 			break;
        case 'R':           eKey = KC_R; 			break;
        case 'S':           eKey = KC_S; 			break;
        case 'T':           eKey = KC_T; 			break;
        case 'U':           eKey = KC_U; 			break;
        case 'V':           eKey = KC_V; 			break;
        case 'W':           eKey = KC_W; 			break;
        case 'X':           eKey = KC_X; 			break;
        case 'Y':           eKey = KC_Y; 			break;
        case 'Z':           eKey = KC_Z; 			break;
        case VK_LWIN:       eKey = KC_LWIN; 		break;
        case VK_RWIN:       eKey = KC_RWIN; 		break;
        case VK_APPS:       eKey = KC_APPS; 		break;
        case VK_NUMPAD0:    eKey = KC_NUMPAD0; 		break;
        case VK_NUMPAD1:    eKey = KC_NUMPAD1; 		break;
        case VK_NUMPAD2:    eKey = KC_NUMPAD2; 		break;
        case VK_NUMPAD3:    eKey = KC_NUMPAD3; 		break;
        case VK_NUMPAD4:    eKey = KC_NUMPAD4; 		break;
        case VK_NUMPAD5:    eKey = KC_NUMPAD5; 		break;
        case VK_NUMPAD6:    eKey = KC_NUMPAD6; 		break;
        case VK_NUMPAD7:    eKey = KC_NUMPAD7; 		break;
        case VK_NUMPAD8:    eKey = KC_NUMPAD8; 		break;
        case VK_NUMPAD9:    eKey = KC_NUMPAD9; 		break;
        case VK_MULTIPLY:   eKey = KC_MULTIPLY;		break;
        case VK_ADD:        eKey = KC_ADD;			break;
        case VK_SEPARATOR:  eKey = KC_SEPARATOR;	break;
        case VK_SUBTRACT:   eKey = KC_SUBTRACT;		break;
        case VK_DECIMAL:    eKey = KC_DECIMAL;		break;
        case VK_DIVIDE:     eKey = KC_DIVIDE;		break;
        case VK_F1:         eKey = KC_F1; 			break;
        case VK_F2:         eKey = KC_F2; 			break;
        case VK_F3:         eKey = KC_F3; 			break;
        case VK_F4:         eKey = KC_F4; 			break;
        case VK_F5:         eKey = KC_F5; 			break;
        case VK_F6:         eKey = KC_F6; 			break;
        case VK_F7:         eKey = KC_F7; 			break;
        case VK_F8:         eKey = KC_F8; 			break;
        case VK_F9:         eKey = KC_F9; 			break;
        case VK_F10:        eKey = KC_F10; 			break;
        case VK_F11:        eKey = KC_F11; 			break;
        case VK_F12:        eKey = KC_F12; 			break;
        case VK_F13:        eKey = KC_F13; 			break;
        case VK_F14:        eKey = KC_F14; 			break;
        case VK_F15:        eKey = KC_F15; 			break;
        case VK_F16:        eKey = KC_F16; 			break;
        case VK_F17:        eKey = KC_F17; 			break;
        case VK_F18:        eKey = KC_F18; 			break;
        case VK_F19:        eKey = KC_F19; 			break;
        case VK_F20:        eKey = KC_F20; 			break;
        case VK_F21:        eKey = KC_F21; 			break;
        case VK_F22:        eKey = KC_F22; 			break;
        case VK_F23:        eKey = KC_F23; 			break;
        case VK_F24:        eKey = KC_F24; 			break;
        case VK_NUMLOCK:    eKey = KC_NUMLOCK;		break;
        case VK_SCROLL:     eKey = KC_SCROLL;		break;
        default:            eKey = KC_NONE;			break;
    }
    return eKey;
}
//---------------------------------------------------------------------------------------------------------------------
KeyCode	
CInputManager::TransLateStringToKey(string p_sKey)
{
	for(int i=0; i<KC_NumKeys; ++i)
	{
		if(p_sKey == CKeyMapping::ms_axTable[i].m_pcName)
		{
			return CKeyMapping::ms_axTable[i].m_eGameLibCode;
		}
	}
	return KC_NONE;
}
//---------------------------------------------------------------------------------------------------------------------
/**
	liefert die Anzahl der Key-Repeat-Events seit dem letzten Update
	(Key-Repeat-Events entstehen durch das festhalten einer Taste)
*/
int
CInputManager::NumKeyRepeatEvents(float p_fButtonDownTime, bool p_bButtonDownEventNow) const
{
	if(p_bButtonDownEventNow)
	{
		return 1;
	}

	if(p_fButtonDownTime < m_fKeyStrokeDelay)
	{
		return 0;
	}

	int iNumEvents = (int)((p_fButtonDownTime - m_fKeyStrokeDelay) / m_fKeyStrokeRepeatTime) + 2;
	int iNumEventsLastTick = (int)((p_fButtonDownTime - m_fKeyStrokeDelay - m_fLastTickDuration) / m_fKeyStrokeRepeatTime) + 2;

	return iNumEvents - iNumEventsLastTick;
}
//---------------------------------------------------------------------------------------------------------------------
/**
	überprüft, ob für die angegebene Gruppe ein entsprechendes Event vorliegt und konsumiert es auf Wunsch
*/
bool
CInputManager::CheckEvent(string p_sName, string p_sGroup, bool p_bConsume)
{
	TCondition* pxCondition = GetCondition(p_sName, p_sGroup);
	assert(pxCondition);
	if(pxCondition)
	{
		if(pxCondition->m_iEventsWaiting > 0)
		{
			if(p_bConsume)
			{
				pxCondition->m_iEventsWaiting--;
			}
			return true;
		}
	}
	return false;
}
//---------------------------------------------------------------------------------------------------------------------
const CInputManager::TCondition*
CInputManager::GetCondition(std::string p_sName, std::string p_sGroup) const
{
	if(m_xGroups.size() == 0)
	{
		return 0;
	}

	map<const string, TGroup>::const_iterator xGroupIterator;
	xGroupIterator = m_xGroups.find(p_sGroup);
	if(xGroupIterator == m_xGroups.end())
	{
		assert(false);
		return 0;
	}

	if(xGroupIterator->second.m_xInputConditions.size() == 0)
	{
		return 0;
	}

	map<const string, TCondition>::const_iterator i;
	i = xGroupIterator->second.m_xInputConditions.find(p_sName);
	if(i == xGroupIterator->second.m_xInputConditions.end())
	{
		return 0;
	}

	return &(i->second);
}
//---------------------------------------------------------------------------------------------------------------------
CInputManager::TCondition*
CInputManager::GetCondition(std::string p_sName, std::string p_sGroup)
{
	if(m_xGroups.size() == 0)
	{
		return 0;
	}

	map<const string, TGroup>::iterator xGroupIterator;
	xGroupIterator = m_xGroups.find(p_sGroup);
	if(xGroupIterator == m_xGroups.end())
	{
		assert(false);
		return 0;
	}

	if(xGroupIterator->second.m_xInputConditions.size() == 0)
	{
		return 0;
	}

	map<const string, TCondition>::iterator i;
	i = xGroupIterator->second.m_xInputConditions.find(p_sName);
	if(i == xGroupIterator->second.m_xInputConditions.end())
	{
		return 0;
	}

	return &(i->second);
}
//---------------------------------------------------------------------------------------------------------------------
void
CInputManager::FlushEvents(std::string p_sGroup)
{
	if(m_xGroups.size() == 0)
	{
		assert(false);
		return;
	}

	map<const string, TGroup>::iterator xGroupIterator;
	xGroupIterator = m_xGroups.find(p_sGroup);
	if(xGroupIterator == m_xGroups.end())
	{
		assert(false);
		return;
	}

	map<const string, TCondition>::iterator i;
	for(i=xGroupIterator->second.m_xInputConditions.begin(); i!=xGroupIterator->second.m_xInputConditions.end(); i++)
	{
		i->second.m_iEventsWaiting = 0;
	}
}
//---------------------------------------------------------------------------------------------------------------------


#pragma once
#ifndef INPUTEVENT_H_INCLUDED
#define INPUTEVENT_H_INCLUDED

#include <string>
#include "baselib/pnt.h"

#include "InputCommons.h"


class CInputEvent
{
public:

	enum EventType {
		IT_NONE,
		IT_KEY_DOWN,           
		IT_KEY_UP,             
		IT_KEY_CHAR,           
		IT_MOUSE_MOVE,				
		IT_AXIS_MOTION,				
		IT_JOYSTICK_BUTTON_DOWN,
		IT_JOYSTICK_BUTTON_UP,
		IT_MOUSE_BUTTON_DOWN,       
		IT_MOUSE_BUTTON_UP,         
		IT_MOUSE_BUTTON_DBLCLCK,
		IT_NAMED_EVENT
	};

	CInputEvent();
	CInputEvent(EventType p_eType, int p_iMouseButton, CPnt p_xMousePosition);
	CInputEvent(EventType p_eType, unsigned char p_cChar);
	CInputEvent(EventType p_eType, int p_iKeyOrButton);
	~CInputEvent();

	EventType			GetEventType() const;
	float				GetAxisValue() const;
	CPnt				GetMousePosition() const;

private:

	EventType			m_eEventType;	///< Typ des Ereignisses

	union
    {
        unsigned char	m_cChar;		///< Charakter, wenn Typ == IT_KEY_CHAR,
		int				m_iKey;			///< Key, wenn Typ == IT_KEY_DOWN || IT_KEY_UP
        int				m_iButton;		///< ButtonNr, JOYSTICK oder MOUSE
		int				m_iAxis;		///< Achsennummer, wenn Typ == IT_AXIS_MOTION
		std::string*	m_pxEventName;	///< pointer to event name
	};

	float				m_fAxisValue;	///< Wert einer Achse
	CPnt				m_xMousePos;	///< Mausposition (bei Mausevent)
};

#include "InputEvent.inl"

#endif // INPUTEVENT_H_INCLUDED


#include "stdafx.h"
#include "GameLib/UserInterface/Input/InputEvent.h"

//---------------------------------------------------------------------------------------------------------------------
CInputEvent::CInputEvent()
{
}

//---------------------------------------------------------------------------------------------------------------------
CInputEvent::CInputEvent(EventType p_eType, int p_iMouseButton, CPnt p_xMousePosition)
{
	m_eEventType = p_eType;
	m_iButton = p_iMouseButton;
	m_xMousePos = p_xMousePosition;
}

//---------------------------------------------------------------------------------------------------------------------
CInputEvent::CInputEvent(EventType p_eType, unsigned char p_cChar)
{
	m_eEventType = p_eType;
	m_cChar = p_cChar;
}

//---------------------------------------------------------------------------------------------------------------------
CInputEvent::CInputEvent(EventType p_eType, int p_iKeyOrButton)
{
	m_eEventType = p_eType;
	m_iButton = p_iKeyOrButton;
}

//---------------------------------------------------------------------------------------------------------------------
CInputEvent::~CInputEvent()
{
}
//---------------------------------------------------------------------------------------------------------------------

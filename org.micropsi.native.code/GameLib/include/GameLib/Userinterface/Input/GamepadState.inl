//---------------------------------------------------------------------------------------------------------------------
/**
	\param p_iButton	index of button; first button = 0 
	\return true if button is down; false otherwise
*/
inline
bool  
CGamepadState::IsButtonDown(int p_iButton) const
{
	return (m_JoystickState.rgbButtons[p_iButton] & 128) != 0;
}

//---------------------------------------------------------------------------------------------------------------------
inline
bool	
CGamepadState::ButtonDownEvent(int p_iButton) const
{
	if (m_pxButtonStates)
	{
		return m_pxButtonStates[p_iButton].ButtonDownEvent();
	}
	else
	{
		return false;
	}
}

//---------------------------------------------------------------------------------------------------------------------

inline
bool	
CGamepadState::ButtonUpEvent(int p_iButton) const
{
	if (m_pxButtonStates)
	{
		return m_pxButtonStates[p_iButton].ButtonUpEvent();
	}
	else
	{
		return false;
	}
}
//---------------------------------------------------------------------------------------------------------------------

inline
int
CGamepadState::GetNumButtons() const				
{ 
	return m_iNumButtons; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CGamepadState::GetNumAxis() const					
{ 
	return m_iNumAxes; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
float 
CGamepadState::GetAxisValue(int p_iAxis) const
{
	switch (p_iAxis)
	{
		case AX_X:	
			return (float) GetXAxisValue();
		case AX_Y:	
			return (float) GetYAxisValue();
		case AX_Z:	
			return (float) GetZAxisValue();
		case AX_XROT:	
			return (float) GetXAxisRotation();
		case AX_YROT:	
			return (float) GetYAxisRotation();
		case AX_ZROT:	
			return (float) GetZAxisRotation();
		case AX_SLIDER1:	
			return (float) GetSlider1Value();
		case AX_SLIDER2:	
			return (float) GetSlider2Value();
		default:	
			assert(false);		// ungültiger Achsenwert	
			return 0.0f;
	}
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
float
CGamepadState::GetXAxisValue() const
{
	return GetNormalizedAxisValue(AX_X, m_JoystickState.lX);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
float
CGamepadState::GetYAxisValue() const
{
	return GetNormalizedAxisValue(AX_Y, m_JoystickState.lY);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
float
CGamepadState::GetZAxisValue() const
{
	return GetNormalizedAxisValue(AX_Z, m_JoystickState.lZ);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
float
CGamepadState::GetXAxisRotation() const
{
	return GetNormalizedAxisValue(AX_XROT, m_JoystickState.lRx);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
float
CGamepadState::GetYAxisRotation() const
{
	return GetNormalizedAxisValue(AX_YROT, m_JoystickState.lRy);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
float
CGamepadState::GetZAxisRotation() const
{
	return GetNormalizedAxisValue(AX_ZROT, m_JoystickState.lRz);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
float   
CGamepadState::GetSlider1Value() const
{
	return GetNormalizedAxisValue(AX_SLIDER1, m_JoystickState.rglSlider[0]);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
inline
float   
CGamepadState::GetSlider2Value() const
{
	return GetNormalizedAxisValue(AX_SLIDER2, m_JoystickState.rglSlider[1]);
}
//---------------------------------------------------------------------------------------------------------------------

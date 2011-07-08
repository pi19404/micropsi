//---------------------------------------------------------------------------------------------------------------------
inline
bool		
CMouseState::IsWheelAvailable() const
{
	return m_iNumAxes > 2;
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CMouseState::GetNumButtons() const				
{ 
	return m_iNumButtons; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CMouseState::GetNumAxis() const					
{ 
	return m_iNumAxes; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool  
CMouseState::IsButtonDown(int p_iButton) const
{
	return (m_MouseState.rgbButtons[p_iButton] & 128) != 0;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool	
CMouseState::ButtonDownEvent(int p_iButton) const
{
	return m_pxButtonStates[p_iButton].ButtonDownEvent();
}

//---------------------------------------------------------------------------------------------------------------------
inline
bool	
CMouseState::ButtonUpEvent(int p_iButton) const
{
	return m_pxButtonStates[p_iButton].ButtonUpEvent();
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CMouseState::GetXAxisRelativeValue() const
{
    return  m_MouseState.lX;
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CMouseState::GetYAxisRelativeValue() const
{
    return  m_MouseState.lY;
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CMouseState::GetWheelRelativeValue() const
{
    return  m_MouseState.lZ;
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CMouseState::GetAxisRelativeValue(int p_iAxis) const
{
	switch (p_iAxis)
	{
		case 0:		
			return GetXAxisRelativeValue();
		case 1:		
			return GetYAxisRelativeValue();
		case 2:		
			return GetWheelRelativeValue(); 
		default:	
			assert(false);		// ungültiger Achsenwert	
			return 0;
	}
}
//---------------------------------------------------------------------------------------------------------------------


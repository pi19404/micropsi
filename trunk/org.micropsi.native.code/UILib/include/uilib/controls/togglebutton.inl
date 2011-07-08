//---------------------------------------------------------------------------------------------------------------------
inline
bool	
CToggleButton::GetToggleButtonState()
{ 
	return m_bState; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CToggleButton::SetGrouped(bool p_bGrouped)		
{ 
	m_bInGroup = p_bGrouped; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool	
CToggleButton::GetGrouped() const						
{ 
	return m_bInGroup; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CToggleButton::SetAllowUntoggle(bool p_bUntoggle)
{
	m_bAllowUntoggle = p_bUntoggle;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool	
CToggleButton::GetAllowUntoggle() const
{
	return m_bAllowUntoggle; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CToggleButton::SetOnStateChangeCallback(CFunctionPointer1<CToggleButton*>& rxCallback)
{
	m_xOnStateChangeCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------


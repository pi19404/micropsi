//---------------------------------------------------------------------------------------------------------------------
inline
bool		 
CBasicButton::GetButtonDown() const			
{ 
	return m_bButtonDown; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool		 
CBasicButton::GetMouseButtonPressed() const	
{ 
	return m_bMouseButtonPressed; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CBasicButton::SetOnButtonDownCallback(CFunctionPointer1<CBasicButton*>& rxCallback)
{
	m_xOnButtonDownCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CBasicButton::SetOnClickCallback(CFunctionPointer1<CBasicButton*>& rxCallback)
{
	m_xOnClickCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------

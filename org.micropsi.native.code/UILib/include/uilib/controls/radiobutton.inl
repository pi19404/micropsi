//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CRadioButton::GetSelected() const							
{ 
	return m_bSelected; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CStr 
CRadioButton::GetText() const								
{ 
	return m_pxLabel->GetText(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CRadioButton::GetBackground() const							
{ 
	return m_bBackground; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CRadioButton::SetOnStateChangeCallback(CFunctionPointer1<CRadioButton*>& rxCallback)
{
	m_xOnStateChangeCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------

//---------------------------------------------------------------------------------------------------------------------
inline
bool
CScrollBar::GetHasButtons() const
{
	return 	(m_iStyle & SB_NoButtons) == 0;
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CScrollBar::GetScrollPos() const
{ 
	return m_iScrollPos; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CScrollBar::GetScrollRange() const
{ 
	return m_iScrollRange; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CScrollBar::GetPageSize() const				
{ 
	return m_iPageSize; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CScrollBar::GetScrollLimit() const
{ 
	return m_iScrollRange - (m_iPageSize - 1); 
} 
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CScrollBar::CScrollBarButton::SetButtonType(CVisualization::ButtonType p_eButtonType)	
{
	m_eButtonType = p_eButtonType; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CScrollBar::SetOnChangeCallback(CFunctionPointer1<CScrollBar*>& rxCallback)
{
	m_xOnChangeCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------

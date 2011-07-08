//---------------------------------------------------------------------------------------------------------------------
inline
int				
CSlider::GetSliderPos() const			
{ 
	return m_iSliderPos; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int				
CSlider::GetSliderRange() const			
{
	return m_iSliderRange; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CSlider::GetBackground() const
{
	return m_bBackground;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CSlider::SetOnChangeCallback(CFunctionPointer1<CSlider*>& rxCallback)
{
	m_xOnChangeCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------

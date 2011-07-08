//---------------------------------------------------------------------------------------------------------------------
inline
CStr			
CLabel::GetText() const
{ 
	return m_sText; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CBitmap*	
CLabel::GetBitmap() const								
{ 
	return m_xBitmap.GetBitmap(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool			
CLabel::GetBackground() const							
{ 
	return m_bBackground; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CLabel::HorizontalTextAlignment 
CLabel::GetHorizontalTextAlignment() const
{
	return m_eHTextAlignment;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CLabel::VerticalTextAlignment 
CLabel::GetVerticalTextAlignment() const
{
	return m_eVTextAlignment;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CLabel::SetTextColor(CColor p_xColor)					
{ 
	m_xTextColor = p_xColor; 
	m_bCustomTextColor = true; 
	InvalidateWindow(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CLabel::RestoreDefaultTextColor()						
{ 
	m_bCustomTextColor = false; 
}
//---------------------------------------------------------------------------------------------------------------------

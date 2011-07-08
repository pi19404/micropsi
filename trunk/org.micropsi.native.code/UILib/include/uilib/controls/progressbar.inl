//-----------------------------------------------------------------------------
inline
float			
CProgressBar::GetRange() const								
{ 
	return m_fRange; 
}
//-----------------------------------------------------------------------------
inline
float			
CProgressBar::GetProgress() const								
{ 
	return m_fProgress; 
}
//-----------------------------------------------------------------------------
inline
void			
CProgressBar::SetBarBitmap(const CStr& p_rsBitmap)			
{ 
	m_xBarBitmap = p_rsBitmap; 
}
//-----------------------------------------------------------------------------
inline
void			
CProgressBar::SetBackgroundBitmap(const CStr& p_rsBitmap)	
{ 
	m_xBKBitmap = p_rsBitmap; 
}
//-----------------------------------------------------------------------------
inline
void			
CProgressBar::SetVertical(bool p_bVertical)					
{ 
	m_bVertical = p_bVertical; InvalidateWindow(); 
}
//-----------------------------------------------------------------------------
inline
bool			
CProgressBar::GetVertical() const								
{
	return m_bVertical; 
}
//-----------------------------------------------------------------------------
inline
const CBitmap*	
CProgressBar::GetBarBitmap() const
{
	return m_xBarBitmap.GetBitmap(); 
}
//-----------------------------------------------------------------------------
inline
const CBitmap*	
CProgressBar::GetBackgroundBitmap() const
{
	return m_xBKBitmap.GetBitmap(); 
}
//-----------------------------------------------------------------------------

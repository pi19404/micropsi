//---------------------------------------------------------------------------------------------------------------------
inline
CStr			
CButton::GetText() const								
{ 
	return m_pxLabel->GetText(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
const UILib::CBitmap*		
CButton::GetBitmap() const
{
	return m_pxLabel->GetBitmap();
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CButton::SetFrame(bool p_bFrame)				
{ 
	m_bFrame = p_bFrame; 
	OnResize(); 
	InvalidateWindow(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool			
CButton::GetFrame() const							
{ 
	return m_bFrame; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool			
CButton::GetBackground() const						
{ 
	return m_bBackground; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CButton::SetWriteAlpha(bool p_bWriteAlpha)	
{ 
	m_pxLabel->SetWriteAlpha(p_bWriteAlpha); 
	InvalidateWindow(); 
}
//---------------------------------------------------------------------------------------------------------------------

//---------------------------------------------------------------------------------------------------------------------
inline
CStr			
CEditControl::GetText() const							
{ 
	return m_xText.GetText(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CEditControl::SetReadOnly(bool p_bReadOnly)	
{ 
	m_bReadOnly = p_bReadOnly; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool			
CEditControl::GetReadOnly() const						
{ 
	return m_bReadOnly; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool			
CEditControl::GetMultiLine()	const					
{ 
	return m_bMultiLine; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool			
CEditControl::GetForceFixedFont()						
{ 
	return m_eFont == CVisualization::FONT_Fixed; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool			
CEditControl::GetWordWrap() const						
{ 
	return m_bWordWrap; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CEditControl::SetTextLimit(int p_iLimit)				
{ 
	return m_xText.SetTextLimit(p_iLimit); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CStr 
CEditControl::GetDebugString() const		
{ 
	return CStr("CEditControl Content = ") + GetText(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CEditControl::SetOnChangeCallback(CFunctionPointer1<CEditControl*>& rxCallback)
{
	m_xOnChangeCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CEditControl::SetOnUpdateCallback(CFunctionPointer1<CEditControl*>& rxCallback)
{
	m_xOnUpdateCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------

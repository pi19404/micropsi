//---------------------------------------------------------------------------------------------------------------------
inline
void	
CSpinControlNumber::SetValue(int p_iValue)				
{ 
	SetValue((double) p_iValue); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CSpinControlNumber::SetValue(float p_fValue)			
{ 
	SetValue((double) p_fValue); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CSpinControlNumber::SetLimits(int p_iMin, int p_iMax, int p_iStep)
{ 
	SetLimits((double) p_iMin, (double) p_iMax, (double) p_iStep); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CSpinControlNumber::SetLimits(float p_fMin, float p_fMax, float p_fStep) 
{ 
	SetLimits((double) p_fMin, (double) p_fMax, (double) p_fStep); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
double	
CSpinControlNumber::GetValue() const					
{ 
	return m_dValue; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
float	
CSpinControlNumber::GetValueFloat() const				
{ 
	return (float) m_dValue; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int		
CSpinControlNumber::GetValueInt() const						
{ 
	return (int) m_dValue; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CStr 
CSpinControlNumber::GetDebugString() const		
{ 
	return CStr("CSpinControlNumber Contents = ") + CStr::Create("%f", GetValue()); 
}
//---------------------------------------------------------------------------------------------------------------------

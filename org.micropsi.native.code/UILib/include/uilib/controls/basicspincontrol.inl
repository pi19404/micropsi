//---------------------------------------------------------------------------------------------------------------------
inline
void			
CBasicSpinControl::SetEditable(bool p_bEditable)		
{ 
	m_pxEditCtrl->SetReadOnly(!p_bEditable); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CBasicSpinControl::SetOnChangeCallback(CFunctionPointer1<CBasicSpinControl*>& rxCallback)
{
	m_xOnChangeCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------

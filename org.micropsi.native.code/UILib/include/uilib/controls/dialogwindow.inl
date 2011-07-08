//---------------------------------------------------------------------------------------------------------------------
inline
CStr
CDialogWindow::GetCaption() const
{ 
	return m_sCaption; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CDialogWindow::OnActivate()
{
	InvalidateTitleBar(); 
	return true; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CDialogWindow::OnDeactivate()	
{
	InvalidateTitleBar(); 
	return true; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CStr 
CDialogWindow::GetDebugString() const
{ 
	return "CDialogWindow"; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CDialogWindow::SetDraggable(bool p_bDraggable)
{
	m_bDraggable = p_bDraggable;
} 
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CDialogWindow::GetDraggable() const
{
	return m_bDraggable;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CDialogWindow::GetHasCloseButton() const
{
	return m_pxCloseButton != 0;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CDialogWindow::SetOnCloseCallback(CFunctionPointer1<CDialogWindow*>& rxCallback)
{
	m_xOnCloseCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------

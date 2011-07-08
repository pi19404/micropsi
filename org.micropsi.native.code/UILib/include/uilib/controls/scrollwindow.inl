//---------------------------------------------------------------------------------------------------------------------
inline
void
CScrollWindow::SetInteriorWindow(CWindow* p_pxNewInterior)
{
	m_pxClientArea->SetInteriorWindow(p_pxNewInterior);
}
//---------------------------------------------------------------------------------------------------------------------
inline
int	
CScrollWindow::GetHSize() const 			
{ 
	return m_pxClientArea->GetHSize(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int				
CScrollWindow::GetVSize() const			
{
	return m_pxClientArea->GetVSize(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int				
CScrollWindow::GetVisibleVSize() const		
{ 
	return GetSize().cy - (HasHScrollbar() ? m_pxHScrollBar->GetSize().cy : 0); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int				
CScrollWindow::GetHScrollRange() const		
{ 
	return m_pxClientArea->GetHScrollRange(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int				
CScrollWindow::GetVScrollRange() const		
{ 
	return m_pxClientArea->GetVScrollRange(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int				
CScrollWindow::GetHScrollPos() const		
{ 
	return m_pxClientArea->GetHScrollPos(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int				
CScrollWindow::GetVScrollPos() const		
{ 
	return m_pxClientArea->GetVScrollPos(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CScrollWindow::SetScrollPos(int iHPos, int iVPos)		
{ 
	m_pxClientArea->SetScrollPos(iHPos, iVPos); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CSize
CScrollWindow::GetClientAreaSize() const				
{ 
	return m_pxClientArea->GetClientAreaSize(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CScrollWindow::HasHScrollbar()	const					
{ 
	return m_iHScrollType == ST_ALWAYS || (m_iHScrollType == ST_AUTO && GetHScrollRange() > 0); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CScrollWindow::HasVScrollbar()	const					
{ 
	return m_iVScrollType == ST_ALWAYS || (m_iVScrollType == ST_AUTO && GetVScrollRange() > 0); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CScrollWindow::SetHScrollType(int p_iScrollType)		
{ 
	m_iHScrollType = clamp(p_iScrollType, 0, ST_NUMTYPES); 
	OnResize(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CScrollWindow::SetVScrollType(int p_iScrollType)		
{ 
	m_iVScrollType = clamp(p_iScrollType, 0, ST_NUMTYPES); 
	OnResize(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CWindow*		
CScrollWindow::GetClientWindow()
{
	return m_pxClientArea->GetClientWindow(); 
}
//---------------------------------------------------------------------------------------------------------------------

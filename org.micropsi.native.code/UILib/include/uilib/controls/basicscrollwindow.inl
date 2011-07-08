//---------------------------------------------------------------------------------------------------------------------
inline
int		
CBasicScrollWindow::GetHSize() const 			
{ 
	return m_pxInnerWindow->GetSize().cx; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int		
CBasicScrollWindow::GetVSize() const			
{ 
	return m_pxInnerWindow->GetSize().cy; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int		
CBasicScrollWindow::GetHScrollRange() const		
{ 
	int i = m_pxInnerWindow->GetSize().cx - GetSize().cx; 
	return i > 0 ? i : 0; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int		
CBasicScrollWindow::GetVScrollRange() const		
{ 
	int i = m_pxInnerWindow->GetSize().cy - GetSize().cy; 
	return i > 0 ? i : 0; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int		
CBasicScrollWindow::GetHScrollPos() const		
{ 
	return - m_pxInnerWindow->GetPos().x; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int	
CBasicScrollWindow::GetVScrollPos() const		
{
	return - m_pxInnerWindow->GetPos().y; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int	
CBasicScrollWindow::SetHScrollPos(int iHPos)	
{ 
	SetScrollPos(iHPos, GetVScrollPos()); 
	return GetHScrollPos();	
}
//---------------------------------------------------------------------------------------------------------------------
inline
int		
CBasicScrollWindow::SetVScrollPos(int iVPos)	
{ 
	SetScrollPos(GetHScrollPos(), iVPos); 
	return GetVScrollPos();	
}
//---------------------------------------------------------------------------------------------------------------------
inline
CSize
CBasicScrollWindow::GetClientAreaSize() const						
{ 
	return m_pxInnerWindow->GetSize(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CWindow*
CBasicScrollWindow::GetClientWindow()								
{ 
	return m_pxInnerWindow; 
}
//---------------------------------------------------------------------------------------------------------------------

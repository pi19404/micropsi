//---------------------------------------------------------------------------------------------------------------------
inline
WHDL				
CToolTip::GetOwnerWindow() const							
{ 
	return m_hOwnerWindow; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool			
CToolTip::IsOnScreen(CSize p_xParentSize, CPnt p_xWindowPos)
{
	return	p_xWindowPos.x + GetSize().cx < p_xParentSize.cx  &&
			p_xWindowPos.y + GetSize().cy < p_xParentSize.cy  &&
			p_xWindowPos.x >= 0 && 
			p_xWindowPos.y >= 0;
}
//---------------------------------------------------------------------------------------------------------------------

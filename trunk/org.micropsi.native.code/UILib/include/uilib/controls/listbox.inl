//---------------------------------------------------------------------------------------------------------------------
inline
CItemData*
CListBox::GetItemData(int p_iIndex) const
{ 
	return (CItemData*) GetItemDataEx(p_iIndex); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CListBox::GetNumberOfSelectedItems() const
{ 
	return m_iNumSelectedItems; 
}
//---------------------------------------------------------------------------------------------------------------------
inline	
bool
CListBox::GetAllowMultiSelection() const
{
	return m_bAllowMultiSelection; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CListBox::GetAllowScrollBar() const
{
	return m_bAllowScrollbar;
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CListBox::GetNeededHeight(int p_iLines)	
{ 
	return m_xFrameSize.top + m_xFrameSize.bottom + p_iLines * m_iLineHeight; 
}
//---------------------------------------------------------------------------------------------------------------------
inline	
bool
CListBox::GetMouseDown() const		
{ 
	return m_bMouseDown; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CScrollBar*
CListBox::GetCurrentScrollBar() const 
{
	return m_pxScrollbar;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CListBox::SetOnSelectCallback(CFunctionPointer1<CListBox*>& rxCallback)
{
	m_xOnSelectCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------

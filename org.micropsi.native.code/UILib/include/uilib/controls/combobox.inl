//---------------------------------------------------------------------------------------------------------------------
inline
int			
CComboBox::NumItems()							
{ 
	return m_xListCtrl.NumItems(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CStr			
CComboBox::GetItem(int p_iIndex)				
{ 
	return m_xListCtrl.GetItem(p_iIndex); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int			
CComboBox::FindItem(const CStr& p_sString)		
{ 
	return m_xListCtrl.FindItem(p_sString); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void* 
CComboBox::GetItemDataEx(int p_iIndex) const					
{ 
	return m_xListCtrl.GetItemDataEx(p_iIndex); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CComboBox::SetItemDataEx(int p_iIndex,void* p_pItemData)		
{ 
	m_xListCtrl.SetItemDataEx(p_iIndex,p_pItemData); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CItemData*			
CComboBox::GetItemData(int p_iIndex) const		
{ 
	return (CItemData*) GetItemDataEx(p_iIndex); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CComboBox::Clear()									
{ 
	m_xListCtrl.Clear(); m_xEditCtrl.SetText(""); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CStr			
CComboBox::GetText() const						
{ 
	return m_xEditCtrl.GetText(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int			
CComboBox::GetSelectedItem() const				
{ 
	return m_xListCtrl.GetSelectedItem(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CStr			 
CComboBox::GetSelectedItemAsString() const		
{ 
	return m_xListCtrl.GetSelectedItemAsString(); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool			
CComboBox::GetAllowAnyText() const				
{ 
	return m_bAllowAnyText; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CComboBox::SetMaxPopUpListHeight(int p_iH)		
{ 
	m_iMaxListHeight = p_iH; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int			
CComboBox::GetMaxPopUpListHeight() const		
{ 
	return m_iMaxListHeight; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void			
CComboBox::SetOnChangeCallback(CFunctionPointer1<CComboBox*>& rxCallback)
{
	m_xOnChangeCallback = rxCallback;
}
//---------------------------------------------------------------------------------------------------------------------

//---------------------------------------------------------------------------------------------------------------------
inline
int					
CObjectManager::GetNumberOfObjects() const
{
	return (int) m_AllObjects.size();
}
//---------------------------------------------------------------------------------------------------------------------
inline
int					
CObjectManager::GetNumberOfCurrentlyVisibleObjects() const
{
	return m_iObjectsVisible;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CObjectManager::SetMaxVisibilityDistance(float p_fDistance)
{
	m_fMaxVisibilityDistance = p_fDistance;
}
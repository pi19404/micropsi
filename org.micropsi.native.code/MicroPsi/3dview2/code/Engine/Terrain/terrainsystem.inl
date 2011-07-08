//---------------------------------------------------------------------------------------------------------------------
inline
CVec3		
CTerrainSystem::GetScale() const
{ 
	return m_vScale; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CVec3		
CTerrainSystem::GetOffset() const
{ 
	return m_vOffset; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int			
CTerrainSystem::GetNumberOfCurrentlyVisibleChunks() const
{
	return m_iChunksVisible;
}	
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CTerrainSystem::GetTerrainWrapAround() const
{
	return m_bWrapAround;
}
//---------------------------------------------------------------------------------------------------------------------
inline
float
CTerrainSystem::GetWidth() const
{
	return (float) m_xMaterialMap.GetWidth() * m_vScale.x();
}
//---------------------------------------------------------------------------------------------------------------------
inline
float
CTerrainSystem::GetHeight() const
{
	return (float) m_xMaterialMap.GetHeight() * m_vScale.z();
}
//---------------------------------------------------------------------------------------------------------------------

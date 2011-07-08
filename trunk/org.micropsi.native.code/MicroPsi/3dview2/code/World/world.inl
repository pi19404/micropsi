//---------------------------------------------------------------------------------------------------------------------
inline
CObjectManager*		
CWorld::GetObjectManager() const
{
	return m_pxObjMgr;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CTerrainSystem*		
CWorld::GetTerrain() const
{
	return m_pxTerrain;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void				
CWorld::SetRenderTerrain(bool p_bRenderTerrain)
{
	m_bRenderTerrain = p_bRenderTerrain;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void				
CWorld::SetRenderObjects(bool p_bRenderObjects)
{
	m_bRenderObjects = p_bRenderObjects;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void				
CWorld::SetRenderWater(bool p_bRenderWater)
{
	m_bRenderWater = p_bRenderWater;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWorld::GetRenderObjects() const
{
	return m_bRenderObjects;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWorld::GetRenderTerrain() const
{
	return m_bRenderTerrain;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWorld::GetRenderWater() const
{
	return m_bRenderWater;
}
//---------------------------------------------------------------------------------------------------------------------
inline
int	
CWorld::GetWaterHeight() const
{
	return m_iWaterHeight;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CVisualization&	
CWorld::GetVisualization() const
{
	return m_xVisualization;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CLevelEditor*
CWorld::GetEditor() const
{
	return m_pxLevelEditor;
} 
//---------------------------------------------------------------------------------------------------------------------
inline
std::string	
CWorld::GetCurrentWorldFileName() const
{
	return m_sFilename;
}
//---------------------------------------------------------------------------------------------------------------------
inline
float
CWorld::GetTerrainBorder() const
{
	return m_fTerrainBorder;
}
//---------------------------------------------------------------------------------------------------------------------

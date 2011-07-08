//---------------------------------------------------------------------------------------------------------------------
inline
CSplatChunkManager* 
CSplatChunkManager::GetInstance()
{
	return ms_pxInstance;
}
//---------------------------------------------------------------------------------------------------------------------
inline
int			
CSplatChunkManager::GetMapXSize() const
{ 
	return m_iMapXSize; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
int			
CSplatChunkManager::GetMapZSize() const
{ 
	return m_iMapZSize; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CVec3		
CSplatChunkManager::GetScale() const
{ 
	return m_vScale; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CSplatChunkManager::CConfig&	
CSplatChunkManager::GetConfig() const
{
	return m_xConfig;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CEngineController*
CSplatChunkManager::GetEngineController() const
{
	return m_pxEngineController;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CSplatChunkManager::SetRenderBasePassOnly(bool p_bBasePassOnly)
{
	m_xConfig.m_bBasePassOnly = p_bBasePassOnly;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CSplatChunkManager::SetRenderBlendMaps(int p_iMaterialIndex)
{
	m_xConfig.m_iMaterialOfBlendMapToRender = p_iMaterialIndex;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CSplatChunkManager::SetRenderLowestGeometryLODOnly(bool p_bLowestLOD)
{
	m_xConfig.m_bRenderLowestLODOnly = p_bLowestLOD;
}
//---------------------------------------------------------------------------------------------------------------------
inline
int			
CSplatChunkManager::GetNumberOfChunks() const
{
	return m_iTotalChunks;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CVec3		
CSplatChunkManager::GetMapVertex(int p_iX, int p_iZ) const
{
	return CVec3(	(float) p_iX * m_vScale.x(),  
					(float) m_pxHeightMap->GetHeight(p_iX, p_iZ) * m_vScale.y(),
					(float) p_iZ * m_vScale.z()) + m_vOffset; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CVec3
CSplatChunkManager::GetMapVertexLocal(int p_iLocalX, int p_iLocalZ, int p_iGlobalX, int p_iGlobalZ) const
{
	return CVec3(	(float) p_iLocalX * m_vScale.x(),  
					(float) m_pxHeightMap->GetHeight(p_iGlobalX, p_iGlobalZ) * m_vScale.y(),
					(float) p_iLocalZ * m_vScale.z()); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
TEffectHandle
CSplatChunkManager::GetTerrainShader() const
{
	return m_hTerrainShader;
//---------------------------------------------------------------------------------------------------------------------
}
inline
TIndexBufferHandle		
CSplatChunkManager::GetSharedIndexBuffer() const
{
	return m_hSharedIndexBuffer;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CMaterialMap*	
CSplatChunkManager::GetMaterialMap() const
{
	return m_pxMaterialMap;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CSplatChunkManager::SetRenderShadows(bool p_bShadows)
{
	m_xConfig.m_bShadows = p_bShadows;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CSplatChunkManager::SetRenderSkirts(bool p_bSkirts)
{
	m_xConfig.m_bRenderSkirts = p_bSkirts;
}

//---------------------------------------------------------------------------------------------------------------------
inline
void
CSplatChunkManager::SetDoubleSidedRendering(bool p_bDoubleSided)
{
	m_xConfig.m_bDoubleSided = p_bDoubleSided;
}

//---------------------------------------------------------------------------------------------------------------------


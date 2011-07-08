//---------------------------------------------------------------------------------------------------------------------
inline
int							
CVisualization::NumTerrainMaterials() const
{
	return (int) m_axTerrainMaterials.size();
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CVisualization::CMaterial&			
CVisualization::GetTerrainMaterial(int p_iMaterial) const
{
	return m_axTerrainMaterials[p_iMaterial];
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CMapParameters&
CVisualization::GetDefaultMapParameters() const
{
	return m_xDefaultMapParameters;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const std::string&
CVisualization::GetSkyBoxFileName() const
{
	return m_sSkyBoxFile;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const std::string&
CVisualization::GetDescription() const
{
	return m_sDescription;
}
//---------------------------------------------------------------------------------------------------------------------

//---------------------------------------------------------------------------------------------------------------------
inline
const CAxisAlignedBoundingBox&	
CWaterTile::GetLocalAABB() const
{
	return m_xAABB;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CWaterTile::SetWaterReflectionTexture(TTextureHandle p_hWaterReflectionTexture)
{
	m_hWaterReflectionMap = p_hWaterReflectionTexture;
}
//---------------------------------------------------------------------------------------------------------------------

//---------------------------------------------------------------------------------------------------------------------
inline
int			
CMaterialMap::GetMaterial(int p_iX, int p_iZ) const
{
	if(p_iX < 0  ||  p_iZ < 0  ||  p_iX >= m_iWidth  ||  p_iZ >= m_iHeight)
	{
		return -1;
	}
	else
	{
		return m_pcMaterialData[p_iZ*m_iWidth + p_iX];
	}
}
//---------------------------------------------------------------------------------------------------------------------
inline
int			
CMaterialMap::IsMaterialOrNeighborIsMaterial(int p_iX, int p_iZ, int p_iMaterial) const
{
	return	GetMaterial(p_iX  , p_iZ  ) == p_iMaterial	||
			GetMaterial(p_iX-1, p_iZ-1) == p_iMaterial   ||
			GetMaterial(p_iX  , p_iZ-1) == p_iMaterial   ||
			GetMaterial(p_iX+1, p_iZ-1) == p_iMaterial   ||
			GetMaterial(p_iX-1, p_iZ  ) == p_iMaterial   ||
			GetMaterial(p_iX+1, p_iZ  ) == p_iMaterial   ||
			GetMaterial(p_iX-1, p_iZ+1) == p_iMaterial   ||
			GetMaterial(p_iX  , p_iZ+1) == p_iMaterial   ||
			GetMaterial(p_iX+1, p_iZ+1) == p_iMaterial;
}
//---------------------------------------------------------------------------------------------------------------------
inline
int
CMaterialMap::GetHeight() const
{
	return m_iHeight;
}
//---------------------------------------------------------------------------------------------------------------------
inline
int				
CMaterialMap::GetWidth() const
{
	return m_iWidth;
}
//---------------------------------------------------------------------------------------------------------------------

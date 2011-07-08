//---------------------------------------------------------------------------------------------------------------------
inline
float 
CSplatChunkTerrainTile::Weight(float p_fX1, float p_fY1, float p_fX2, float p_fY2)
{
	float fXDist = p_fX2 - p_fX1;
	float fYDist = p_fY2 - p_fY1;
	float fWeight = 1.0f - ((fXDist * fXDist + fYDist * fYDist) / (1.25f * 1.25f));
	return fWeight > 0.0f ? fWeight : 0.0f;
}
//---------------------------------------------------------------------------------------------------------------------

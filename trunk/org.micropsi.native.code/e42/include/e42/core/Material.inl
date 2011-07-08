//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
TEffectHandle
CMaterial::GetEffect() const
{
    return m_hEffect;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
void 
CMaterial::SetDiffuseMap(TTextureHandle& hndDiffuseMap)
{
	SetTexture(SHADERCONSTANT_NAME_DIFFUSEMAP, hndDiffuseMap);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
void 
CMaterial::SetSpecularMap(TTextureHandle& hndSpecularMap)
{
	SetTexture(SHADERCONSTANT_NAME_SPECULARMAP, hndSpecularMap);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
void
CMaterial::SetNormalMap(TTextureHandle& hndNormalMap)
{
	SetTexture(SHADERCONSTANT_NAME_NORMALMAP, hndNormalMap);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
void 
CMaterial::SetBumpMap(TTextureHandle& hndBumpMap)
{
	SetTexture(SHADERCONSTANT_NAME_BUMPMAP, hndBumpMap);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
void 
CMaterial::SetDetailMap(TTextureHandle& hndDetailMap)
{
	SetTexture(SHADERCONSTANT_NAME_DETAILMAP, hndDetailMap);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
void 
CMaterial::SetEnvironmentMap(TTextureHandle& hndEnvironmentMap)
{
	SetTexture(SHADERCONSTANT_NAME_ENVIRONMENTMAP, hndEnvironmentMap);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
void 
CMaterial::SetShadowMap(TTextureHandle& hndShadowMap)
{
	SetTexture(SHADERCONSTANT_NAME_SHADOWMAP, hndShadowMap);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
void 
CMaterial::SetLightMap(TTextureHandle& hndLightMap)
{
	SetTexture(SHADERCONSTANT_NAME_LIGHTMAP, hndLightMap);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline 
int 
CMaterial::GetMaterialSortingID() const
{
    union
    {
        void*           pxAdress;
        unsigned int    uiAdress; 
    };

	pxAdress = m_hEffect.GetPtr();
    return (uiAdress & 0xffff) ^ 0xffff;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

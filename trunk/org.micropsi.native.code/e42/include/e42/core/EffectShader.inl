//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetNumBones(const int i) const
{
    if (m_hndNumBones) 
    {
        HRESULT hr = m_pd3dEffect->SetInt(m_hndNumBones, i);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetWorldMatrixArray(const CMat4S* pM, const int iCount) const
{
    if (m_hndWorldMatrixArray) 
    {
        HRESULT hr = m_pd3dEffect->SetMatrixArray(m_hndWorldMatrixArray, (D3DXMATRIX*)pM, iCount);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetWorldInverseMatrix(const CMat4S& rM) const
{
    if (m_hndWorld)
    {
        HRESULT hr = m_pd3dEffect->SetMatrix(m_hndWorldInverse, (D3DXMATRIX*)&rM);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetWorldMatrix(const CMat4S& rM) const
{
    if (m_hndWorld)
    {
        HRESULT hr = m_pd3dEffect->SetMatrix(m_hndWorld, (D3DXMATRIX*)&rM);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetViewMatrix(const CMat4S& rM) const
{
    if (m_hndView)
    {
        HRESULT hr = m_pd3dEffect->SetMatrix(m_hndView, (D3DXMATRIX*)&rM);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetProjectionMatrix(const CMat4S& rM) const
{
    if (m_hndProjection)
    {
        HRESULT hr = m_pd3dEffect->SetMatrix(m_hndProjection, (D3DXMATRIX*)&rM);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetWorldViewMatrix(const CMat4S& rM) const
{
    if (m_hndWorldView) 
    {
        HRESULT hr = m_pd3dEffect->SetMatrix(m_hndWorldView, (D3DXMATRIX*)&rM);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetViewProjectionMatrix(const CMat4S& rM) const
{
    if (m_hndViewProjection) 
    {
        HRESULT hr = m_pd3dEffect->SetMatrix(m_hndViewProjection, (D3DXMATRIX*)&rM);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetWorldViewProjectionMatrix(const CMat4S& rM) const
{
    if (m_hndWorldViewProjection) 
    {
        HRESULT hr = m_pd3dEffect->SetMatrix(m_hndWorldViewProjection, (D3DXMATRIX*)&rM);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetViewInverseMatrix(const CMat4S& rM) const
{
    if (m_hndViewInverse)
    {
        HRESULT hr = m_pd3dEffect->SetMatrix(m_hndViewInverse, (D3DXMATRIX*)&rM);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetMaterialDiffuseVector(const CVec4& rV) const
{
    if (m_hndMaterialDiffuse)
    {
        HRESULT hr = m_pd3dEffect->SetVector(m_hndMaterialDiffuse, (D3DXVECTOR4*)&rV);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetMaterialAmbientVector(const CVec4& rV) const
{
    if (m_hndMaterialAmbient) 
    {
        HRESULT hr = m_pd3dEffect->SetVector(m_hndMaterialAmbient, (D3DXVECTOR4*)&rV);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetLightDirVector(const CVec4& rV) const
{
    if (m_hndLightDir) 
    {
        HRESULT hr = m_pd3dEffect->SetVector(m_hndLightDir, (D3DXVECTOR4*)&rV);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetEyePosition(const CVec4& rV) const
{
    if (m_hndEyePosition) 
    {
        HRESULT hr = m_pd3dEffect->SetVector(m_hndEyePosition, (D3DXVECTOR4*)&rV);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CEffectShader::SetDiffuseMap(const TTextureHandle& hndTexture) const
{
    if (m_hndDiffuseMap)
    {
        HRESULT hr = m_pd3dEffect->SetTexture(m_hndDiffuseMap, hndTexture.GetPtr());
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CEffectShader::SetSpecularMap(const TTextureHandle& hndTexture) const
{
    if (m_hndSpecularMap)
    {
        HRESULT hr = m_pd3dEffect->SetTexture(m_hndSpecularMap, hndTexture.GetPtr());
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CEffectShader::SetBumpMap(const TTextureHandle& hndTexture) const
{
    if (m_hndBumpMap)
    {
        HRESULT hr = m_pd3dEffect->SetTexture(m_hndBumpMap, hndTexture.GetPtr());
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CEffectShader::SetNormalMap(const TTextureHandle& hndTexture) const
{
    if (m_hndNormalMap)
    {
        HRESULT hr = m_pd3dEffect->SetTexture(m_hndNormalMap, hndTexture.GetPtr());
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CEffectShader::SetDetailMap(const TTextureHandle& hndTexture) const
{
    if (m_hndDetailMap)
    {
        HRESULT hr = m_pd3dEffect->SetTexture(m_hndDetailMap, hndTexture.GetPtr());
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CEffectShader::SetEnvironmentMap(const TTextureHandle& hndTexture) const
{
    if (m_hndEnvironmentMap)
    {
        HRESULT hr = m_pd3dEffect->SetTexture(m_hndEnvironmentMap, hndTexture.GetPtr());
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CEffectShader::SetLightMap(const TTextureHandle& hndTexture) const
{
    if (m_hndLightMap)
    {
        HRESULT hr = m_pd3dEffect->SetTexture(m_hndLightMap, hndTexture.GetPtr());
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CEffectShader::SetShadowMap(const TTextureHandle& hndTexture) const
{
    if (m_hndShadowMap)
    {
        HRESULT hr = m_pd3dEffect->SetTexture(m_hndShadowMap, hndTexture.GetPtr());
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetTime(const float f) const
{
    if (m_hndTime) 
    {
        HRESULT hr = m_pd3dEffect->SetFloat(m_hndTime, f);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetTechnique(const std::string& sName) const
{
    HRESULT hr = m_pd3dEffect->SetTechnique(sName.c_str());
    assert(SUCCEEDED(hr));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetDefaultTechnique_StaticMesh() const
{
    assert(m_hndDefaultTechnique_StaticMesh);
    if (m_hndDefaultTechnique_StaticMesh) 
    {
        HRESULT hr = m_pd3dEffect->SetTechnique(m_hndDefaultTechnique_StaticMesh);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::SetDefaultTechnique_SkinnedMesh() const
{
    assert(m_hndDefaultTechnique_SkinnedMesh);
    if (m_hndDefaultTechnique_SkinnedMesh)
    {
        HRESULT hr = m_pd3dEffect->SetTechnique(m_hndDefaultTechnique_SkinnedMesh);
        assert(SUCCEEDED(hr));
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::BeginPass(const int iPass) const
{
    HRESULT hr = m_pd3dEffect->BeginPass(iPass);
    assert(SUCCEEDED(hr));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEffectShader::EndPass() const
{
    HRESULT hr = m_pd3dEffect->EndPass();
    assert(SUCCEEDED(hr));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
ID3DXEffect* 
CEffectShader::GetD3DXEffect() const
{
    return m_pd3dEffect;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
const CD3DXEffectInstance&
CEffectShader::GetInitialParameterValues() const
{
	return m_xInitialParameterValues;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

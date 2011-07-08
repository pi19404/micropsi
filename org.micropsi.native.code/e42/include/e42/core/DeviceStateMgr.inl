//-----------------------------------------------------------------------------------------------------------------------------------------
inline
HRESULT 
CDeviceStateMgr::SetVertexDeclaration(LPDIRECT3DVERTEXDECLARATION9 pDecl)
{
	// immer dran denken: beim Rendern von DirectX-Meshes wird dieser Parameter ohne StateManager gesetzt !
    if (m_xDeviceState.m_pVertexDeclaration != pDecl)
    {
        m_xDeviceState.m_FVF = -1;

        m_xDeviceState.m_pVertexDeclaration = pDecl;
        return m_pd3dDevice->SetVertexDeclaration(pDecl);
    }

    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
HRESULT 
CDeviceStateMgr::SetStreamSource(UINT StreamNumber, LPDIRECT3DVERTEXBUFFER9 pStreamData, UINT OffsetInBytes, UINT Stride)
{
	// immer dran denken: beim Rendern von DirectX-Meshes wird dieser Parameter ohne StateManager gesetzt !

    CDeviceState::CStreamSourceState& rxStreamSourceState = m_xDeviceState.m_axStreamSourceStates[StreamNumber];

    if ((rxStreamSourceState.m_pStreamData != pStreamData) ||
        (rxStreamSourceState.m_OffsetInBytes != OffsetInBytes) ||
        (rxStreamSourceState.m_Stride != Stride))
    {
        rxStreamSourceState.m_pStreamData = pStreamData;
        rxStreamSourceState.m_OffsetInBytes = OffsetInBytes;
        rxStreamSourceState.m_Stride = Stride;

        return m_pd3dDevice->SetStreamSource(StreamNumber, pStreamData, OffsetInBytes, Stride);
    }

    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
HRESULT 
CDeviceStateMgr::SetIndices(LPDIRECT3DINDEXBUFFER9 pIndexData)
{
	// immer dran denken: beim Rendern von DirectX-Meshes wird dieser Parameter ohne StateManager gesetzt !
    if (m_xDeviceState.m_pIndexData != pIndexData)
    {
        m_xDeviceState.m_pIndexData = pIndexData;
        return m_pd3dDevice->SetIndices(pIndexData);
    }

    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
HRESULT 
CDeviceStateMgr::SetRenderTarget(DWORD RenderTargetIndex, LPDIRECT3DSURFACE9 pRenderTarget)
{
    if (m_xDeviceState.m_apRenderTargets[RenderTargetIndex] != pRenderTarget)
    {
        m_xDeviceState.m_apRenderTargets[RenderTargetIndex] = pRenderTarget;
        return m_pd3dDevice->SetRenderTarget(RenderTargetIndex, pRenderTarget);
    }

    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
HRESULT 
CDeviceStateMgr::SetSoftwareVertexProcessing(BOOL bSoftware)
{
    if (m_xDeviceState.m_bSoftwareVertexProcessing != bSoftware)
    {
        m_xDeviceState.m_bSoftwareVertexProcessing = bSoftware;
        return m_pd3dDevice->SetSoftwareVertexProcessing(bSoftware);
    }

    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
HRESULT 
CDeviceStateMgr::SetClipPlane(DWORD Index, const float *pPlane)
{
    if (m_xDeviceState.m_axfClipPlane[Index] != *(D3DXVECTOR4*)(pPlane))
    {
        m_xDeviceState.m_axfClipPlane[Index] = *(D3DXVECTOR4*)(pPlane);
        return m_pd3dDevice->SetClipPlane(Index, pPlane);
    }

    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
BOOL 
CDeviceStateMgr::GetSoftwareVertexProcessing() const
{
    return m_xDeviceState.m_bSoftwareVertexProcessing;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

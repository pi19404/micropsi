//-----------------------------------------------------------------------------------------------------------------------------------------
inline
IDirect3D9*
CEngineController::GetD3D() const
{
    assert(m_pd3d);
    return m_pd3d;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
IDirect3DDevice9*
CEngineController::GetDevice() const
{
    assert(m_pd3dDevice);
    return m_pd3dDevice;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
const _D3DCAPS9*
CEngineController::GetDeviceCaps() const
{
    assert(m_pxDeviceCaps);
    return m_pxDeviceCaps;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CDeviceStateMgr*
CEngineController::GetDeviceStateMgr() const
{
    assert(m_pd3dDeviceStateMgr);
    return m_pd3dDeviceStateMgr;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CFileLocator*
CEngineController::GetFileLocator() const
{
    assert(m_pxFileLocator);
    return m_pxFileLocator;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CXFileLoader*
CEngineController::GetXFileLoader() const
{
    assert(m_pxXFileLoader);
    return m_pxXFileLoader;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CEngineStats*
CEngineController::GetEngineStats() const
{
    assert(m_pxEngineStats);
    return m_pxEngineStats;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CPixelShaderFactory*
CEngineController::GetPixelShaderFactory() const
{
    assert(m_pxPixelShaderFactory);
    return m_pxPixelShaderFactory;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CVertexShaderFactory*
CEngineController::GetVertexShaderFactory() const
{
    assert(m_pxVertexShaderFactory);
    return m_pxVertexShaderFactory;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CTextureFactory*
CEngineController::GetTextureFactory() const
{
    assert(m_pxTextureFactory);
    return m_pxTextureFactory;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CVertexDeclarationFactory*
CEngineController::GetVertexDeclarationFactory() const
{
    assert(m_pxVertexDeclarationFactory);
    return m_pxVertexDeclarationFactory;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CIndexBufferFactory*
CEngineController::GetIndexBufferFactory() const
{
    assert(m_pxIndexBufferFactory);
    return m_pxIndexBufferFactory;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CVertexBufferFactory*
CEngineController::GetVertexBufferFactory() const
{
    assert(m_pxVertexBufferFactory);
    return m_pxVertexBufferFactory;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CSurfaceFactory*
CEngineController::GetSurfaceFactory() const
{
    assert(m_pxSurfaceFactory);
    return m_pxSurfaceFactory;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CEffectFactory*
CEngineController::GetEffectFactory() const
{
    assert(m_pxEffectFactory);
    return m_pxEffectFactory;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CModelFactory*
CEngineController::GetModelFactory() const
{
    assert(m_pxModelFactory);
    return m_pxModelFactory;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CAnimationFactory*
CEngineController::GetAnimationFactory() const
{
    assert(m_pxAnimationFactory);
    return m_pxAnimationFactory;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CMeshFactory*
CEngineController::GetMeshFactory() const
{
    assert(m_pxMeshFactory);
    return m_pxMeshFactory;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CEngineController::SetEngineTime(double dTime)
{
    m_dEngineTime = dTime;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
double
CEngineController::GetEngineTime() const
{
    return m_dEngineTime;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
const D3DSURFACE_DESC&
CEngineController::GetBackbufferDesc() const
{
    return m_xBackbufferDesc;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CEngineController::GetErrorMessagesEnabled() const
{
    return m_bErrorMessagesEnabled;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CEngineController::SetErrorMessagesEnabled(bool bEnable)
{
    m_bErrorMessagesEnabled = bEnable;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CEngineController& 
CEngineController::Get()
{
	assert(ms_iNumEngineControllerInstances == 1);
	return *ms_pxEngineControllerSingleton;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

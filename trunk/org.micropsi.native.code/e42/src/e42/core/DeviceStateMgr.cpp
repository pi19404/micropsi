#include "stdafx.h"

#include "e42/core/DeviceStateMgr.h"

#include "e42/core/EngineController.h"
#include "e42/core/EngineStats.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CDeviceStateMgr::CDeviceStateMgr(const CEngineController* pxEngineController)
:   m_uiRefCount(1),
    m_pxEngineController(pxEngineController),
    m_pd3dDevice(pxEngineController->GetDevice())
{
    Reset();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CDeviceStateMgr::~CDeviceStateMgr()
{
    assert(m_uiRefCount == 0);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CDeviceStateMgr::Reset()
{
    memset(&m_xDeviceState, 139, sizeof(m_xDeviceState));        // <- magic cookie - nicht als Renderstate o. ä. setzen :/ (FIXME)
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CDeviceStateMgr::ResetGeometryStates()
{
    m_xDeviceState.m_FVF = 0xFFFFFFFF;
    m_xDeviceState.m_pVertexDeclaration = (LPDIRECT3DVERTEXDECLARATION9)0xFFFFFFFFFFFFFFFF;
    m_xDeviceState.m_pIndexData = (LPDIRECT3DINDEXBUFFER9)0xFFFFFFFFFFFFFFFF;

    for (int i = 0; i < 16; i++)
    {
        m_xDeviceState.m_axStreamSourceStates[i].m_pStreamData = (LPDIRECT3DVERTEXBUFFER9)0xFFFFFFFFFFFFFFFF;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::QueryInterface(REFIID iid, void** ppvObject) 
{
    if (iid == IID_ID3DXEffectStateManager) 
    {
        *ppvObject = this;
        return S_OK;
    }
    *ppvObject = 0;
    return E_NOINTERFACE;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
ULONG 
__stdcall CDeviceStateMgr::AddRef()
{
    m_uiRefCount++;
    return m_uiRefCount;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
ULONG 
__stdcall CDeviceStateMgr::Release()
{
    if ((--m_uiRefCount) == 0) 
    {
        delete this; 
        return 0;
    } 
    return m_uiRefCount;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CDeviceStateMgr* 
CDeviceStateMgr::Create(const CEngineController* pxEngineController)
{
    return new CDeviceStateMgr(pxEngineController);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetNPatchMode(FLOAT nSegments)
{
    return m_pd3dDevice->SetNPatchMode(nSegments);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::LightEnable(DWORD Index, BOOL Enable)
{
    return m_pd3dDevice->LightEnable(Index, Enable);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetRenderState(D3DRENDERSTATETYPE State, DWORD Value)
{
    if (m_xDeviceState.m_axRenderStates[State] != Value)
    {
        m_xDeviceState.m_axRenderStates[State] = Value;
        return m_pd3dDevice->SetRenderState(State, Value);
    }
    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetLight(DWORD Index, CONST D3DLIGHT9* pLight)
{
    return m_pd3dDevice->SetLight(Index, pLight);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetMaterial(CONST D3DMATERIAL9* pMaterial)
{
    return m_pd3dDevice->SetMaterial(pMaterial);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetTransform(D3DTRANSFORMSTATETYPE State, CONST D3DMATRIX* pMatrix)
{
    return m_pd3dDevice->SetTransform(State, pMatrix);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetFVF(DWORD FVF)
{
	// immer dran denken: beim Rendern von DirectX-Meshes wird dieser Parameter ohne StateManager gesetzt !
    if (m_xDeviceState.m_FVF != FVF)
    {
        m_xDeviceState.m_pVertexDeclaration = (LPDIRECT3DVERTEXDECLARATION9)(-1);

        m_xDeviceState.m_FVF = FVF;
        return m_pd3dDevice->SetFVF(FVF);
    }
    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetVertexShader(LPDIRECT3DVERTEXSHADER9 pShader)
{
    if (m_xDeviceState.m_pxVertexShader != pShader)
    {
        m_xDeviceState.m_pxVertexShader = pShader;
        return m_pd3dDevice->SetVertexShader(pShader);
    }
    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetVertexShaderConstantB(UINT StartRegister, CONST BOOL* pConstantData, UINT RegisterCount)
{
    return m_pd3dDevice->SetVertexShaderConstantB(StartRegister, pConstantData, RegisterCount);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetVertexShaderConstantF(UINT StartRegister, CONST FLOAT* pConstantData, UINT RegisterCount)
{
    if (memcmp(&m_xDeviceState.m_avVertexShaderConstantF[StartRegister], pConstantData, sizeof(D3DXVECTOR4) * RegisterCount))
    {
        memcpy(&m_xDeviceState.m_avVertexShaderConstantF[StartRegister], pConstantData, sizeof(D3DXVECTOR4) * RegisterCount);
        return m_pd3dDevice->SetVertexShaderConstantF(StartRegister, pConstantData, RegisterCount);
    }
    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetVertexShaderConstantI(UINT StartRegister, CONST INT* pConstantData, UINT RegisterCount)
{
    return m_pd3dDevice->SetVertexShaderConstantI(StartRegister, pConstantData, RegisterCount);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetTexture(DWORD Stage, LPDIRECT3DBASETEXTURE9 pTexture)
{
    if (m_xDeviceState.m_apTexture[Stage] != pTexture)
    {
        m_pxEngineController->GetEngineStats()->m_iNumTextureSwitches++;

        m_xDeviceState.m_apTexture[Stage] = pTexture;
        return m_pd3dDevice->SetTexture(Stage, pTexture);
    }
    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetTextureStageState(DWORD Stage, D3DTEXTURESTAGESTATETYPE Type, DWORD Value)
{
    if (m_xDeviceState.m_axTextureStageStates[Stage].m_Types[Type] != Value)
    {
        m_xDeviceState.m_axTextureStageStates[Stage].m_Types[Type] = Value;
        return m_pd3dDevice->SetTextureStageState(Stage, Type, Value);
    }
    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetSamplerState(DWORD Sampler, D3DSAMPLERSTATETYPE Type, DWORD Value)
{
    if (m_xDeviceState.m_axSamplerStates[Sampler].m_Types[Type] != Value)
    {
        m_xDeviceState.m_axSamplerStates[Sampler].m_Types[Type] = Value;
        return m_pd3dDevice->SetSamplerState(Sampler, Type, Value);
    }
    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetPixelShader(LPDIRECT3DPIXELSHADER9 pShader)
{
    if (m_xDeviceState.m_pxPixelShader != pShader)
    {
        m_xDeviceState.m_pxPixelShader = pShader;
        return m_pd3dDevice->SetPixelShader(pShader);
    }
    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetPixelShaderConstantB(UINT StartRegister, CONST BOOL* pConstantData, UINT RegisterCount)
{
    return m_pd3dDevice->SetPixelShaderConstantB(StartRegister, pConstantData, RegisterCount);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetPixelShaderConstantF(UINT StartRegister, CONST FLOAT* pConstantData, UINT RegisterCount)
{
    if (memcmp(&m_xDeviceState.m_avPixelShaderConstantF[StartRegister], pConstantData, sizeof(D3DXVECTOR4) * RegisterCount))
    {
        memcpy(&m_xDeviceState.m_avPixelShaderConstantF[StartRegister], pConstantData, sizeof(D3DXVECTOR4) * RegisterCount);
        return m_pd3dDevice->SetPixelShaderConstantF(StartRegister, pConstantData, RegisterCount);
    }
    return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
__stdcall CDeviceStateMgr::SetPixelShaderConstantI(UINT StartRegister, CONST INT* pConstantData, UINT RegisterCount)
{
    return m_pd3dDevice->SetPixelShaderConstantI(StartRegister, pConstantData, RegisterCount);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

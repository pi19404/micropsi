#pragma once

#ifndef E42_DEVICESTATEMGR_H_INCLUDED
#define E42_DEVICESTATEMGR_H_INCLUDED

#include "baselib/array.h"

#include "e42/core/DeviceState.h"
#include <d3dx9effect.h>

class CEngineController;

class CDeviceStateMgr : public ID3DXEffectStateManager
{
private:
    ULONG m_uiRefCount;

    CDeviceState m_xDeviceState;

    const CEngineController*    m_pxEngineController;
    IDirect3DDevice9*           m_pd3dDevice;

    CDeviceStateMgr(const CEngineController* pxEngineController);
    ~CDeviceStateMgr();

public:

    void Reset();
    void ResetGeometryStates();     // Setzt das zurück, was beim Meshzeichnen verändert wird

    HRESULT __stdcall QueryInterface(REFIID iid, void** ppvObject);
    ULONG __stdcall AddRef();
    ULONG __stdcall Release();

    static CDeviceStateMgr* Create(const CEngineController* pxEngineController);


    HRESULT __stdcall SetNPatchMode(FLOAT nSegments);

    HRESULT __stdcall LightEnable(DWORD Index, BOOL Enable);
    HRESULT __stdcall SetRenderState(D3DRENDERSTATETYPE State, DWORD Value);

    HRESULT __stdcall SetLight(DWORD Index, CONST D3DLIGHT9* pLight);
    HRESULT __stdcall SetMaterial(CONST D3DMATERIAL9* pMaterial);
    HRESULT __stdcall SetTransform(D3DTRANSFORMSTATETYPE State, CONST D3DMATRIX* pMatrix);

    HRESULT __stdcall SetFVF(DWORD FVF);

    HRESULT __stdcall SetVertexShader(LPDIRECT3DVERTEXSHADER9 pShader);
    HRESULT __stdcall SetVertexShaderConstantB(UINT StartRegister, CONST BOOL* pConstantData, UINT RegisterCount);
    HRESULT __stdcall SetVertexShaderConstantF(UINT StartRegister, CONST FLOAT* pConstantData, UINT RegisterCount);
    HRESULT __stdcall SetVertexShaderConstantI(UINT StartRegister, CONST INT* pConstantData, UINT RegisterCount);


    HRESULT __stdcall SetTexture(DWORD Stage, LPDIRECT3DBASETEXTURE9 pTexture);
    HRESULT __stdcall SetTextureStageState(DWORD Stage, D3DTEXTURESTAGESTATETYPE Type, DWORD Value);
    HRESULT __stdcall SetSamplerState(DWORD Sampler, D3DSAMPLERSTATETYPE Type, DWORD Value);

    HRESULT __stdcall SetPixelShader(LPDIRECT3DPIXELSHADER9 pShader);
    HRESULT __stdcall SetPixelShaderConstantB(UINT StartRegister, CONST BOOL* pConstantData, UINT RegisterCount);
    HRESULT __stdcall SetPixelShaderConstantF(UINT StartRegister, CONST FLOAT* pConstantData, UINT RegisterCount);
    HRESULT __stdcall SetPixelShaderConstantI(UINT StartRegister, CONST INT* pConstantData, UINT RegisterCount);


    HRESULT __fastcall SetVertexDeclaration(LPDIRECT3DVERTEXDECLARATION9 pDecl);
    HRESULT __fastcall SetStreamSource(UINT StreamNumber, LPDIRECT3DVERTEXBUFFER9 pStreamData, UINT OffsetInBytes, UINT Stride);
    HRESULT __fastcall SetIndices(LPDIRECT3DINDEXBUFFER9 pIndexData);
    HRESULT __fastcall SetRenderTarget(DWORD RenderTargetIndex, LPDIRECT3DSURFACE9 pRenderTarget);
    HRESULT __fastcall SetSoftwareVertexProcessing(BOOL bSoftware);
    HRESULT __fastcall SetClipPlane(DWORD Index, const float *pPlane);

    BOOL __fastcall GetSoftwareVertexProcessing() const;
};

#include "e42/core/DeviceStateMgr.inl"

#endif // E42_DEVICESTATEMGR_H_INCLUDED
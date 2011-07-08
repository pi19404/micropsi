#pragma once

#ifndef E42_DEVICESTATE_H_INCLUDED
#define E42_DEVICESTATE_H_INCLUDED

#include "e42/stdinc.h"

#include <d3d9.h>
#include <d3dx9.h>
#include <d3d9types.h>

class CDeviceState
{
public:
    class CTextureStageState
    {
    public:
        CInlineArray<DWORD, D3DTSS_CONSTANT + 1>        m_Types;
    };


    class CSamplerState
    {
    public:
        CInlineArray<DWORD, D3DSAMP_DMAPOFFSET + 1>     m_Types;
    };


    class CStreamSourceState
    {
    public:
        LPDIRECT3DVERTEXBUFFER9                         m_pStreamData;
        UINT                                            m_OffsetInBytes;
        UINT                                            m_Stride;
    };

    CInlineArray<DWORD, D3DRS_BLENDOPALPHA + 1>     m_axRenderStates;
    DWORD                                           m_FVF;

    LPDIRECT3DVERTEXSHADER9                         m_pxVertexShader;
    LPDIRECT3DPIXELSHADER9                          m_pxPixelShader;

    CInlineArray<LPDIRECT3DBASETEXTURE9, 16>        m_apTexture;
    CInlineArray<CTextureStageState, 16>            m_axTextureStageStates;
    CInlineArray<CSamplerState, 16>                 m_axSamplerStates;

    D3DXVECTOR4                                     m_avVertexShaderConstantF[256];
    D3DXVECTOR4                                     m_avPixelShaderConstantF[64];

    CInlineArray<LPDIRECT3DSURFACE9, 4>             m_apRenderTargets;

    BOOL                                            m_bSoftwareVertexProcessing;

    CInlineArray<D3DXVECTOR4, 8>                    m_axfClipPlane;


    LPDIRECT3DVERTEXDECLARATION9                    m_pVertexDeclaration;
    CInlineArray<CStreamSourceState, 16>            m_axStreamSourceStates;

    LPDIRECT3DINDEXBUFFER9                          m_pIndexData;
};

#endif E42_DEVICESTATE_H_INCLUDED

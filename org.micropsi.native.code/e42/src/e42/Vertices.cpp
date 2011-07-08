#include "stdafx.h"

#include "e42/Vertices.h"

//-------------------------------------------------------------------------------------------------------------------------------------------
const D3DVERTEXELEMENT9 Vertices::g_VEDTable[][8] =
{
    {   // VT_4
        { 0, 0,     D3DDECLTYPE_FLOAT4,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,  0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_4T2
        { 0, 0,     D3DDECLTYPE_FLOAT4,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,  0 },
        { 0, 16,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,  0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_4T2T2
        { 0, 0,     D3DDECLTYPE_FLOAT4,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,  0 },
        { 0, 16,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,  0 },
        { 0, 24,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,  1 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_4NT2
        { 0, 0,     D3DDECLTYPE_FLOAT4,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,  0 },
        { 0, 16,    D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_NORMAL,    0 },
        { 0, 28,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,  0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_4D
        { 0, 0,     D3DDECLTYPE_FLOAT4,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,  0 },
        { 0, 16,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,     0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_4DS
        { 0, 0,     D3DDECLTYPE_FLOAT4,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,  0 },
        { 0, 16,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,     0 },         
        { 0, 20,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,     1 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_4DS
        { 0, 0,     D3DDECLTYPE_FLOAT4,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,      0 },
        { 0, 16,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,         0 },         
        { 0, 20,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,         1 },         
        { 0, 24,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,      0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END()
    },
    
    {   // VT_3
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,      0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_3N
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,  0 },
        { 0, 12,    D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_NORMAL,    0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_3T2
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,  0 },
        { 0, 12,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,  0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_3T2B3
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,      0 },
        { 0, 12,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,      0 },
        { 0, 20,    D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_BLENDWEIGHT,   0 },
        { 0, 24,    D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_BLENDWEIGHT,   1 },
        { 0, 28,    D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_BLENDWEIGHT,   2 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_3T2T2
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,  0 },
        { 0, 12,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,  0 },
        { 0, 20,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,  1 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_3NT2
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,  0 },
        { 0, 12,    D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_NORMAL,    0 },
        { 0, 24,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,  0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_3NT2T2
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,  0 },
        { 0, 12,    D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_NORMAL,    0 },
        { 0, 24,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,  0 },
        { 0, 32,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,  1 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },

    {   // VT_3NDT2T2
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,  0 },
        { 0, 12,    D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_NORMAL,    0 },
        { 0, 24,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,     0 },         
        { 0, 28,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,  0 },
        { 0, 36,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,  1 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },

    {   // VT_3D
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,      0 },
        { 0, 12,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,         0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END()
    },
    
    {   // VT_3DS
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,      0 },
        { 0, 12,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,         0 },         
        { 0, 16,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,         1 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_3DSB3
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,      0 },
        { 0, 12,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,         0 },         
        { 0, 16,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,         1 },
        { 0, 20,    D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_BLENDWEIGHT,   0 },
        { 0, 24,    D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_BLENDWEIGHT,   1 },
        { 0, 28,    D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_BLENDWEIGHT,   2 },
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_3DST2
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,      0 },
        { 0, 12,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,         0 },
        { 0, 16,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,         1 },
        { 0, 20,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,      0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_3DT2B2,
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,      0 },
        { 0, 12,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,         0 },
        { 0, 16,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,      0 },
        { 0, 24,    D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_BLENDWEIGHT,   0 },
        { 0, 28,    D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_BLENDWEIGHT,   1 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_3ND,
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,      0 },
        { 0, 12,    D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_NORMAL,        0 },
        { 0, 24,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,         0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_3NDT2
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,      0 },
        { 0, 12,    D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_NORMAL,        0 },
        { 0, 24,    D3DDECLTYPE_D3DCOLOR,   D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_COLOR,         0 },
        { 0, 28,    D3DDECLTYPE_FLOAT2,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TEXCOORD,      0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END()
    },
    
    {   // VT_3B1N
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,      0 },
        { 0, 12,    D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_BLENDWEIGHT,   0 },
        { 0, 16,    D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_NORMAL,        0 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
    
    {   // VT_B2
        { 0, 0,     D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_BLENDWEIGHT,   0 },
        { 0, 4,     D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_BLENDWEIGHT,   1 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },

    {   // VT_3B1t1t1
        { 0, 0,     D3DDECLTYPE_FLOAT3,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_POSITION,      0 },
        { 0, 12,    D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_BLENDWEIGHT,   0 },
        { 0, 16,    D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TANGENT,       0 },
        { 0, 20,    D3DDECLTYPE_FLOAT1,     D3DDECLMETHOD_DEFAULT,  D3DDECLUSAGE_TANGENT,       1 },
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
        D3DDECL_END(),
    },
};
//-------------------------------------------------------------------------------------------------------------------------------------------
const VertexFVF Vertices::g_FVFTable[]=
{
    D3DFVF_VT4,
    D3DFVF_VT4T2,
    D3DFVF_VT4T2T2,
    D3DFVF_VT4NT2,
    D3DFVF_VT4D,
    D3DFVF_VT4DS,
    D3DFVF_VT4DST2,
    
    D3DFVF_VT3,
    D3DFVF_VT3N,
    D3DFVF_VT3T2,
    D3DFVF_VT3T2B3,
    D3DFVF_VT3T2T2,
    D3DFVF_VT3NT2,
    D3DFVF_VT3NT2T2,
	D3DFVF_VT3NDT2T2,
    D3DFVF_VT3D,
    D3DFVF_VT3DS,
    D3DFVF_VT3DSB3,
    D3DFVF_VT3DST2,
    D3DFVF_VT3DT2B2,
    
    D3DFVF_VT3ND,
    D3DFVF_VT3NDT2,
    
    D3DFVF_VT3B1N,
    D3DFVF_VTB2,

    D3DFVF_VT3B1t1t1,
};
//-------------------------------------------------------------------------------------------------------------------------------------------
const int Vertices::g_VSizeTable[] = 
{
    sizeof(Vertex4),
    sizeof(Vertex4T2),
    sizeof(Vertex4T2T2),
    sizeof(Vertex4NT2),
    sizeof(Vertex4D),
    sizeof(Vertex4DS),
    sizeof(Vertex4DST2),
    
    sizeof(Vertex3),
    sizeof(Vertex3N),
    sizeof(Vertex3T2),
    sizeof(Vertex3T2B3),
    sizeof(Vertex3T2T2),
    sizeof(Vertex3NT2),
    sizeof(Vertex3NT2T2),
	sizeof(Vertex3NDT2T2),
    sizeof(Vertex3D),
    sizeof(Vertex3DS),
    sizeof(Vertex3DSB3),
    sizeof(Vertex3DST2),
    sizeof(Vertex3DT2B2),
    
    sizeof(Vertex3ND),
    sizeof(Vertex3NDT2),
    
    sizeof(Vertex3B1N),
    sizeof(VertexB2),

    sizeof(Vertex3B1t1t1),
};
//-------------------------------------------------------------------------------------------------------------------------------------------
VertexType
Vertices::FVF2VT(unsigned int uiFVF)
{
    for (int i = 0; i < VT_COUNT; i++)
    {
        if (g_FVFTable[i] == uiFVF)
        {
            return (VertexType)(i);
        }
    }
    
    assert(false);
    return VT_UNDEFINED;
}
//-------------------------------------------------------------------------------------------------------------------------------------------

#pragma once

#ifndef E42_RESOURCEHANDLES_H_INCLUDED
#define E42_RESOURCEHANDLES_H_INCLUDED

#include "e42/core/ResourceHandle.h"

//-------------------------------------------------------------------------------------------------------------------------------------------
class CModel;
typedef CResourceHandle<CModel*>                        TModelHandle;
//-------------------------------------------------------------------------------------------------------------------------------------------
struct ID3DXAnimationSet;
typedef CResourceHandle<ID3DXAnimationSet*>             TAnimationHandle;
//-------------------------------------------------------------------------------------------------------------------------------------------
struct ID3DXMesh;
typedef CResourceHandle<ID3DXMesh*>                     TMeshHandle;
//-------------------------------------------------------------------------------------------------------------------------------------------
struct IDirect3DBaseTexture9;
typedef CResourceHandle<IDirect3DBaseTexture9*>         TTextureHandle;
//-------------------------------------------------------------------------------------------------------------------------------------------
struct IDirect3DSurface9;
typedef CResourceHandle<IDirect3DSurface9*>             TSurfaceHandle;
//-------------------------------------------------------------------------------------------------------------------------------------------
struct IDirect3DPixelShader9;
typedef CResourceHandle<IDirect3DPixelShader9*>         TPixelShaderHandle;
//-------------------------------------------------------------------------------------------------------------------------------------------
struct IDirect3DVertexShader9;
typedef CResourceHandle<IDirect3DVertexShader9*>        TVertexShaderHandle;
//-------------------------------------------------------------------------------------------------------------------------------------------
struct IDirect3DVertexBuffer9;
typedef CResourceHandle<IDirect3DVertexBuffer9*>        TVertexBufferHandle;
//-------------------------------------------------------------------------------------------------------------------------------------------
struct IDirect3DIndexBuffer9;
typedef CResourceHandle<IDirect3DIndexBuffer9*>         TIndexBufferHandle;
//-------------------------------------------------------------------------------------------------------------------------------------------
struct IDirect3DVertexDeclaration9;
typedef CResourceHandle<IDirect3DVertexDeclaration9*>   TVertexDeclarationHandle;
//-------------------------------------------------------------------------------------------------------------------------------------------
class CEffectShader;
typedef CResourceHandle<CEffectShader*>                 TEffectHandle;
//-------------------------------------------------------------------------------------------------------------------------------------------


#endif // E42_RESOURCEHANDLES_H_INCLUDED
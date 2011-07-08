#pragma once

#ifndef E42_ENGINECONTROLLER_H_INCLUDED
#define E42_ENGINECONTROLLER_H_INCLUDED

#include "e42/stdinc.h"

#include <windows.h>
#include <d3d9types.h>

typedef enum _D3DFORMAT D3DFORMAT;

struct IDirect3D9;
struct IDirect3DDevice9;
struct _D3DCAPS9;

class CDeviceStateMgr;

class CFileLocator;
class CXFileLoader;
class CEngineStats;

class CPixelShaderFactory;
class CVertexShaderFactory;
class CTextureFactory;
class CVertexDeclarationFactory;
class CIndexBufferFactory;
class CVertexBufferFactory;
class CSurfaceFactory;
class CEffectFactory;
class CModelFactory;
class CAnimationFactory;
class CMeshFactory;


class CEngineController
{
private:
    IDirect3D9*                     m_pd3d;
    IDirect3DDevice9*               m_pd3dDevice;

    CDeviceStateMgr*                m_pd3dDeviceStateMgr;

    _D3DCAPS9*                      m_pxDeviceCaps;
    D3DSURFACE_DESC                 m_xBackbufferDesc;

    CFileLocator*                   m_pxFileLocator;
    CXFileLoader*                   m_pxXFileLoader;
    CEngineStats*                   m_pxEngineStats;

    CPixelShaderFactory*            m_pxPixelShaderFactory;
    CVertexShaderFactory*           m_pxVertexShaderFactory;
    CTextureFactory*                m_pxTextureFactory;
    CVertexDeclarationFactory*      m_pxVertexDeclarationFactory;
    CIndexBufferFactory*            m_pxIndexBufferFactory;
    CVertexBufferFactory*           m_pxVertexBufferFactory;
    CSurfaceFactory*                m_pxSurfaceFactory;
    CEffectFactory*                 m_pxEffectFactory;
    CModelFactory*                  m_pxModelFactory;
    CAnimationFactory*              m_pxAnimationFactory;
    CMeshFactory*                   m_pxMeshFactory;

    double                          m_dEngineTime;
    bool                            m_bEffectDebuggingEnabled;
    bool                            m_bErrorMessagesEnabled;

	static CEngineController*		ms_pxEngineControllerSingleton;
	static unsigned int				ms_iNumEngineControllerInstances;

public:

    CEngineController();
    ~CEngineController();

    IDirect3D9*                     GetD3D() const;
    IDirect3DDevice9*               GetDevice() const;
    const _D3DCAPS9*                GetDeviceCaps() const;
    CDeviceStateMgr*                GetDeviceStateMgr() const;
    const D3DSURFACE_DESC&          GetBackbufferDesc() const;

    CFileLocator*                   GetFileLocator() const;
    CXFileLoader*                   GetXFileLoader() const;
    CEngineStats*                   GetEngineStats() const;


    CPixelShaderFactory*            GetPixelShaderFactory() const;
    CVertexShaderFactory*           GetVertexShaderFactory() const;
    CTextureFactory*                GetTextureFactory() const;
    CVertexDeclarationFactory*      GetVertexDeclarationFactory() const;
    CIndexBufferFactory*            GetIndexBufferFactory() const;
    CVertexBufferFactory*           GetVertexBufferFactory() const;
    CSurfaceFactory*                GetSurfaceFactory() const;
    CEffectFactory*                 GetEffectFactory() const;
    CModelFactory*                  GetModelFactory() const;
    CAnimationFactory*              GetAnimationFactory() const;
    CMeshFactory*                   GetMeshFactory() const;


    bool                            GetPixelShaderDebuggingEnabled() const;
    bool                            GetVertexShaderDebuggingEnabled() const;
    bool                            GetEffectDebuggingEnabled() const;
    void                            SetEffectDebuggingEnabled(bool bEnabled);
    bool                            GetErrorMessagesEnabled() const;
    void                            SetErrorMessagesEnabled(bool bEnable);

    void                            SetEngineTime(double dTime);
    double                          GetEngineTime() const;

	virtual float					GetAspectRatio(int iHead = 0) const;

    void                            PrecacheData(bool bOpenScene = false) const;


    void InitEngineController(IDirect3D9* pd3d, IDirect3DDevice9* pd3dDevice);
    void ShutEngineController();

	static CEngineController& Get();
};

#include "e42/core/EngineController.inl"

#endif E42_ENGINECONTROLLER_H_INCLUDED
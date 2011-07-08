#pragma once
#ifndef E42_RENDERTARGETTEXTURE_H_INCLUDED
#define E42_RENDERTARGETTEXTURE_H_INCLUDED

#include "e42/core/EngineController.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/ResourceHandles.h"

class CRenderTargetTexture
{
private:

    CEngineController*  m_pxEngineController;

    TVertexBufferHandle m_hndScreenAlignedQuadVertices;
    TVertexBufferHandle m_hndScreenAlignedQuadVertices_Small;

    TSurfaceHandle      m_hndOldRenderTargetSurface;
    TTextureHandle      m_hndTexture;

public:
    CRenderTargetTexture(CEngineController* pxEngineController = NULL, int iWidth = -1, int iHeight = -1, D3DFORMAT eFormat = D3DFMT_A8R8G8B8, float fSmallWindowSize = 0.0f, bool bCreatePlaneVertices = true);
    ~CRenderTargetTexture();

    enum Channel
    {
        CH_COLOR,
        CH_ALPHA,
    };

    void Push();
    void Pop();

    void DrawToBackBuffer(Channel ch = CH_COLOR, bool bSmall = false) const;
    void DrawSmall(Channel ch) const;

    TTextureHandle GetTexture() const;
};

#endif // E42_RENDERTARGETTEXTURE_H_INCLUDED

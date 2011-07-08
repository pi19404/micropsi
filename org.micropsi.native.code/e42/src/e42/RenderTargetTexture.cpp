#include "stdafx.h"

#include "e42/RenderTargetTexture.h"

#include "e42/core/EngineController.h"
#include "e42/core/DeviceStateMgr.h"

#include "e42/core/TextureFactory.h"
#include "e42/core/SurfaceFactory.h"
#include "e42/core/VertexBufferFactory.h"
#include "e42/Vertices.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CRenderTargetTexture::CRenderTargetTexture(CEngineController* pxEngineController, int iWidth, int iHeight, D3DFORMAT eFormat, float fSmallWindowSize, bool bCreatePlaneVertices)
:   m_pxEngineController(pxEngineController)
{
	if (pxEngineController == NULL)
		pxEngineController = &CEngineController::Get();

    if (iWidth == -1)
    {
        iWidth = m_pxEngineController->GetBackbufferDesc().Width;
        iHeight = m_pxEngineController->GetBackbufferDesc().Height;
    }

    m_hndTexture = m_pxEngineController->GetTextureFactory()->
        CreateTexture(iWidth, iHeight, 1, D3DUSAGE_RENDERTARGET, eFormat, D3DPOOL_DEFAULT);

    if (bCreatePlaneVertices)
    {
        float fWidth = (float)iWidth;
        float fHeight = (float)iHeight;

        Vertex4T2 axQuadPlaneVertices[] = {
            { CVec4(   0.0f - 0.5f,    0.0f - 0.5f, 0.5f, 0.5f),   CVec2(0.0f, 0.0f) },
            { CVec4( fWidth - 0.5f,    0.0f - 0.5f, 0.5f, 0.5f),   CVec2(1.0f, 0.0f) },
            { CVec4( fWidth - 0.5f, fHeight - 0.5f, 0.5f, 0.5f),   CVec2(1.0f, 1.0f) },
            { CVec4(   0.0f - 0.5f, fHeight - 0.5f, 0.5f, 0.5f),   CVec2(0.0f, 1.0f) }
        };

        m_hndScreenAlignedQuadVertices = m_pxEngineController->GetVertexBufferFactory()->
            CreateVertexBufferFVF(4, D3DFVF_VT4T2, D3DUSAGE_WRITEONLY, 
                    D3DPOOL_DEFAULT, axQuadPlaneVertices);

        float fSizeFact = max(0.25f, 128 / fWidth);
        if(fSmallWindowSize > 0.0f)
        {
            fSizeFact = fSmallWindowSize;
        }

        Vertex4T2 axQuadPlaneVertices_Small[] = {
            { CVec4(               0.0f,                0.0f, 0.5f, 0.5f),   CVec2(0.0f, 0.0f) },
            { CVec4( fWidth * fSizeFact,                0.0f, 0.5f, 0.5f),   CVec2(1.0f, 0.0f) },
            { CVec4( fWidth * fSizeFact, fHeight * fSizeFact, 0.5f, 0.5f),   CVec2(1.0f, 1.0f) },
            { CVec4(               0.0f, fHeight * fSizeFact, 0.5f, 0.5f),   CVec2(0.0f, 1.0f) }
        };

        m_hndScreenAlignedQuadVertices_Small = m_pxEngineController->GetVertexBufferFactory()->
            CreateVertexBufferFVF(4, D3DFVF_VT4T2, D3DUSAGE_WRITEONLY, 
                    D3DPOOL_DEFAULT, axQuadPlaneVertices_Small);
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CRenderTargetTexture::~CRenderTargetTexture()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CRenderTargetTexture::Push()
{
    assert(m_hndOldRenderTargetSurface.GetPtr() == NULL);

    m_hndOldRenderTargetSurface = m_pxEngineController->GetSurfaceFactory()->GetRenderTarget(0);

    TSurfaceHandle hndRenderTargetSurface = m_pxEngineController->GetSurfaceFactory()->GetSurfaceLevel((IDirect3DTexture9*)m_hndTexture.GetPtr(), 0);

    m_pxEngineController->GetDevice()->SetRenderTarget(0, hndRenderTargetSurface.GetPtr());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CRenderTargetTexture::Pop()
{
    assert(m_hndOldRenderTargetSurface.GetPtr() != NULL);

    m_pxEngineController->GetDevice()->SetRenderTarget(0, m_hndOldRenderTargetSurface.GetPtr());

    m_hndOldRenderTargetSurface.Release();

	m_pxEngineController->GetDeviceStateMgr()->Reset();		// ?FIX? for ATI
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TTextureHandle 
CRenderTargetTexture::GetTexture() const
{
    return m_hndTexture;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CRenderTargetTexture::DrawToBackBuffer(Channel ch, bool bSmall) const
{
    IDirect3DVertexBuffer9* pd3dVertexBuffer = 
        bSmall  ? m_hndScreenAlignedQuadVertices_Small.GetPtr()
                : m_hndScreenAlignedQuadVertices.GetPtr();

    if (pd3dVertexBuffer == NULL)
    {
        assert(false);
        return;
    }


    CDeviceStateMgr*    pxDeviceStateMgr = m_pxEngineController->GetDeviceStateMgr();
    IDirect3DDevice9*   pxD3DDevice = m_pxEngineController->GetDevice();

    pxDeviceStateMgr->SetRenderState(D3DRS_ALPHABLENDENABLE, FALSE);

    pxDeviceStateMgr->SetVertexShader(NULL);
    pxDeviceStateMgr->SetPixelShader(NULL);

    pxD3DDevice->SetStreamSource(0, pd3dVertexBuffer, 0, sizeof(Vertex4T2));
    pxD3DDevice->SetFVF(D3DFVF_VT4T2);

    pxDeviceStateMgr->SetTexture(0, m_hndTexture.GetPtr());


    if (ch == CH_ALPHA)
    {
        pxDeviceStateMgr->SetRenderState(D3DRS_TEXTUREFACTOR, 0);
        pxDeviceStateMgr->SetTextureStageState(0, D3DTSS_COLOROP, D3DTOP_BLENDTEXTUREALPHA);
        pxDeviceStateMgr->SetTextureStageState(0, D3DTSS_COLORARG1, D3DTA_TFACTOR | D3DTA_COMPLEMENT);
        pxDeviceStateMgr->SetTextureStageState(0, D3DTSS_COLORARG2, D3DTA_TFACTOR);
    }
    else
    {
        pxDeviceStateMgr->SetTextureStageState(0, D3DTSS_COLOROP, D3DTOP_SELECTARG1);
        pxDeviceStateMgr->SetTextureStageState(0, D3DTSS_COLORARG1, D3DTA_TEXTURE);
    }

    pxDeviceStateMgr->SetTextureStageState(1, D3DTSS_COLOROP, D3DTOP_DISABLE);

    pxDeviceStateMgr->SetTextureStageState(0, D3DTSS_ALPHAOP, D3DTOP_DISABLE);

    pxDeviceStateMgr->SetRenderState(D3DRS_CULLMODE, D3DCULL_CCW);
    pxDeviceStateMgr->SetRenderState(D3DRS_ZENABLE, FALSE);
    pxDeviceStateMgr->SetRenderState(D3DRS_ALPHATESTENABLE, FALSE);

    pxDeviceStateMgr->SetSamplerState(0, D3DSAMP_MAGFILTER, D3DTEXF_POINT);
    pxDeviceStateMgr->SetSamplerState(0, D3DSAMP_MINFILTER, D3DTEXF_POINT);
    pxDeviceStateMgr->SetSamplerState(0, D3DSAMP_MIPFILTER, D3DTEXF_NONE);
    pxDeviceStateMgr->SetRenderState(D3DRS_ALPHATESTENABLE, FALSE);

    m_pxEngineController->GetDevice()->DrawPrimitive(D3DPT_TRIANGLEFAN, 0, 2);

    pxDeviceStateMgr->SetSamplerState(0, D3DSAMP_MAGFILTER, D3DTEXF_LINEAR);
    pxDeviceStateMgr->SetSamplerState(0, D3DSAMP_MINFILTER, D3DTEXF_LINEAR);
    pxDeviceStateMgr->SetSamplerState(0, D3DSAMP_MIPFILTER, D3DTEXF_LINEAR);
    pxDeviceStateMgr->SetRenderState(D3DRS_ZENABLE, TRUE);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CRenderTargetTexture::DrawSmall(Channel ch) const
{
    DrawToBackBuffer(ch, true);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

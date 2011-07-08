#include "stdafx.h"

#include "e42/ShadowTexture.h"

#include "e42/core/Model.h"
#include "e42/core/EngineController.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/SurfaceFactory.h"
#include "e42/core/TextureFactory.h"
#include "e42/core/EffectFactory.h"
#include "e42/core/EffectShader.h"

#include "e42/Camera.h"
#include "e42/core/RenderContext.h"
#include "e42/Vertices.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CShadowTexture::CShadowTexture(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController)
{
    m_vLightDir = CVec3(-3, -2, 0.5f).GetNormalized();

    m_pxCamera = new CCamera();

    m_xShadowColor.m_dwColor = 0x7F000000;

    m_vShadowBase.Clear();
    m_fShadowFadeFactor = 0.25f;

    m_vModelCenter.Clear();

    m_fShadowFactor = 0;

    m_bIsClear = false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CShadowTexture::~CShadowTexture()
{
    delete m_pxCamera;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CShadowTexture::Init(int iTextureSize, bool bSceneMultiSampling)
{
    m_hndShadowTexture = 
        m_pxEngineController->GetTextureFactory()->
            CreateTexture(
                iTextureSize,
                iTextureSize,
                1,
                D3DUSAGE_RENDERTARGET,
                D3DFMT_A8R8G8B8,
                D3DPOOL_DEFAULT);

    m_bSceneMultiSampling = bSceneMultiSampling;

    m_bIsClear = false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CShadowTexture::Shut()
{
    m_hndShadowTexture.Release();
    m_hndShadowFadeTexture.Release();
    m_hndVB.Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CShadowTexture::SetModel(TModelHandle hndModel)
{
    m_hndModel = hndModel;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CShadowTexture::SetLightDir(const CVec3& rvDir)
{
    m_vLightDir = rvDir.GetNormalized();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CShadowTexture::SetShadowColor(const CColor& rxShadowColor)
{
    m_xShadowColor = rxShadowColor;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TTextureHandle 
CShadowTexture::GetShadowTexture() const
{
    return m_hndShadowTexture;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TTextureHandle 
CShadowTexture::GetShadowFadeTexture() const
{
    return m_hndShadowFadeTexture;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CShadowTexture::UpdateShadow(const CMat4S& matModelTransform)
{
    SetupCamera();
    Render(matModelTransform);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CShadowTexture::Clear()
{
    if (!m_bIsClear)
    {
        CDeviceStateMgr* pd3dDeviceStateMgr = 
            m_pxEngineController->GetDeviceStateMgr();

        // depthbuffer setzen
        TSurfaceHandle hndOldDepthBuffer;
        if (m_bSceneMultiSampling)
        {
            hndOldDepthBuffer = 
                m_pxEngineController->GetSurfaceFactory()->GetDepthStencilSurface();
            m_pxEngineController->GetDevice()->SetDepthStencilSurface(0);
        }

        // backbuffer setzen
        TSurfaceHandle hndOldBackBuffer = 
            m_pxEngineController->GetSurfaceFactory()->GetRenderTarget(0);

        pd3dDeviceStateMgr->SetRenderTarget(
            0, 
            m_pxEngineController->GetSurfaceFactory()->
                GetSurfaceLevel((IDirect3DTexture9*)m_hndShadowTexture.GetPtr(), 0).GetPtr());


        // clear
        m_pxEngineController->GetDevice()->Clear(0, 0, D3DCLEAR_TARGET, 0, 0, 0);


        // depthbuffer wiederherstellen
        if (m_bSceneMultiSampling)
        {
            m_pxEngineController->GetDevice()->SetDepthStencilSurface(hndOldDepthBuffer.GetPtr());
        }

        // backbuffer wiederherstellen
        pd3dDeviceStateMgr->SetRenderTarget(0, hndOldBackBuffer.GetPtr());


        m_bIsClear = true;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CShadowTexture::SetupCamera()
{
    CVec3 vUpVec = CVec3(1, 0, 0) ^ m_vLightDir;
    if (vUpVec.Abs() < 0.5f)
        vUpVec = CVec3(0, 0, 1) ^ m_vLightDir;
    vUpVec.Normalize();


    CBoundingSphere xBoundingSphere = m_hndModel->GetBoundingSphere();
    xBoundingSphere.m_fRadius *= 1.75f;      // wegen Animationen; ist nicht in BSphere drin
    xBoundingSphere.m_vCenter = m_vModelCenter;



    m_pxCamera->SetUpVec(vUpVec);
    m_pxCamera->SetOrientation(m_vLightDir);
    m_pxCamera->SetPos(xBoundingSphere.m_vCenter - m_vLightDir * xBoundingSphere.m_fRadius);
    m_pxCamera->SetFieldOfView(xBoundingSphere.m_fRadius, 1.0f, false);


    m_pxCamera->SetupMatrices();

    m_matWorld2ShadowTransform = 
        m_pxCamera->GetViewProjectionMatrix() *
        CMat4S( +0.5f,  0,      0,  0,
                0,      -0.5f,  0,  0,
                0,      0,      1,  0,
                0.5f,   0.5f,   0,  1);

    m_vShadowBase = xBoundingSphere.m_vCenter;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CShadowTexture::Render(const CMat4S& matModelTransform)
{
    CDeviceStateMgr* pd3dDeviceStateMgr = 
        m_pxEngineController->GetDeviceStateMgr();

    // depthbuffer switchen
    TSurfaceHandle hndOldDepthBuffer;
    if (m_bSceneMultiSampling)
    {
        hndOldDepthBuffer = 
            m_pxEngineController->GetSurfaceFactory()->GetDepthStencilSurface();

        m_pxEngineController->GetDevice()->SetDepthStencilSurface(0);
    }


    // backbuffer switchen
    TSurfaceHandle hndOldBackBuffer = 
        m_pxEngineController->GetSurfaceFactory()->GetRenderTarget(0);

    pd3dDeviceStateMgr->SetRenderTarget(
        0, 
        m_pxEngineController->GetSurfaceFactory()->
            GetSurfaceLevel((IDirect3DTexture9*)m_hndShadowTexture.GetPtr(), 0).GetPtr());


    pd3dDeviceStateMgr->SetRenderState(D3DRS_CLIPPLANEENABLE, 0);

    // clearen
    m_pxEngineController->GetDevice()->Clear(0, 0, D3DCLEAR_TARGET, 0x00FF0000, 0, 0);


    // model rendern
    TEffectHandle hndEffect = m_pxEngineController->GetEffectFactory()->GetSharedVarsEffect();
    assert(hndEffect.GetPtr() != NULL);
    ID3DXEffect* pd3dEffect = hndEffect->GetD3DXEffect();
    assert(pd3dEffect != NULL);

    pd3dEffect->SetVector("c_vShadowColor", &D3DXVECTOR4(
         (m_xShadowColor.GetRed() / 255.0f * (1 - m_fShadowFactor)),
         (m_xShadowColor.GetGreen() / 255.0f * (1 - m_fShadowFactor)),
         (m_xShadowColor.GetBlue() / 255.0f * (1 - m_fShadowFactor)),
         (m_xShadowColor.GetAlpha() / 255.0f * (1 - m_fShadowFactor))));


    TRenderContextPtr spxRenderContext;
    spxRenderContext.Create();
    spxRenderContext->m_sTechnique = "castShadow";      // sollte Z disabled haben, da der Z-Buffer NULL ist !!
    m_pxCamera->SetupRenderContext(spxRenderContext);

    m_hndModel->Render(spxRenderContext, matModelTransform);


    // backbuffer wiederherstellen
    pd3dDeviceStateMgr->SetRenderTarget(0, hndOldBackBuffer.GetPtr());

    // depthbuffer wiederherstellen
    if (m_bSceneMultiSampling)
    {
        m_pxEngineController->GetDevice()->SetDepthStencilSurface(hndOldDepthBuffer.GetPtr());
    }


    pd3dDeviceStateMgr->SetRenderState(D3DRS_ZENABLE, TRUE);

    m_bIsClear = false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CShadowTexture::RenderTexture()
{
    if (m_hndVB.GetPtr() == 0)
    {
        Vertex4T2 axVertices[4];
        
        axVertices[0].p = CVec4(0, 0, 0.5f, 1);         axVertices[0].t0 = CVec2(0, 0);    
        axVertices[1].p = CVec4(128, 0, 0.5f, 1);       axVertices[1].t0 = CVec2(1, 0);  
        axVertices[2].p = CVec4(128, 128, 0.5f, 1);     axVertices[2].t0 = CVec2(1, 1);
        axVertices[3].p = CVec4(0, 128, 0.5f, 1);       axVertices[3].t0 = CVec2(0, 1);  

        m_hndVB = 
            m_pxEngineController->GetVertexBufferFactory()->
                CreateVertexBufferFVF(4, D3DFVF_VT4T2, D3DUSAGE_WRITEONLY, D3DPOOL_DEFAULT, &axVertices);
    }


    CDeviceStateMgr* pd3dDeviceStateMgr = 
        m_pxEngineController->GetDeviceStateMgr();

    pd3dDeviceStateMgr->SetVertexShader(NULL);
    pd3dDeviceStateMgr->SetPixelShader(NULL);
    pd3dDeviceStateMgr->SetFVF(D3DFVF_VT4T2);
    pd3dDeviceStateMgr->SetStreamSource(0, m_hndVB.GetPtr(), 0, sizeof(Vertex4T2));
    pd3dDeviceStateMgr->SetIndices(NULL);
    pd3dDeviceStateMgr->SetTexture(0, m_hndShadowTexture.GetPtr());

    pd3dDeviceStateMgr->SetRenderState(D3DRS_ZENABLE, FALSE);
    pd3dDeviceStateMgr->SetRenderState(D3DRS_CULLMODE, D3DCULL_NONE);


    pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_COLOROP, D3DTOP_SELECTARG1);
    pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_COLORARG1, D3DTA_TEXTURE);
    pd3dDeviceStateMgr->SetTextureStageState(1, D3DTSS_COLOROP, D3DTOP_DISABLE);

    pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_ALPHAOP, D3DTOP_SELECTARG1);
    pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_ALPHAARG1, D3DTA_TEXTURE);
    pd3dDeviceStateMgr->SetTextureStageState(1, D3DTSS_ALPHAOP, D3DTOP_DISABLE);


    pd3dDeviceStateMgr->SetRenderState(D3DRS_ALPHABLENDENABLE, TRUE);
    pd3dDeviceStateMgr->SetRenderState(D3DRS_SRCBLEND, D3DBLEND_SRCALPHA);
    pd3dDeviceStateMgr->SetRenderState(D3DRS_DESTBLEND, D3DBLEND_ZERO);

    m_pxEngineController->GetDevice()->DrawPrimitive(D3DPT_TRIANGLEFAN, 0, 2);

    pd3dDeviceStateMgr->SetRenderState(D3DRS_ZENABLE, TRUE);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const CMat4S& 
CShadowTexture::GetWorld2ShadowTransform() const
{
    return m_matWorld2ShadowTransform;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const CVec3& 
CShadowTexture::GetShadowBase() const
{
    return m_vShadowBase;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
float 
CShadowTexture::GetShadowFadeFactor() const
{
    return m_fShadowFadeFactor;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CShadowTexture::SetShadowFadeTexture(const TTextureHandle& hndFadeTexture)
{
    m_hndShadowFadeTexture = hndFadeTexture;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CShadowTexture::SetShadowFadeDistance(float fFadeDistance)
{
    m_fShadowFadeFactor = 1 / fFadeDistance;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CShadowTexture::SetModelCenter(const CVec3& vCenter)
{
    m_vModelCenter = vCenter;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CShadowTexture::SetShadowFactor(float fFactor)
{
    m_fShadowFactor = fFactor;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

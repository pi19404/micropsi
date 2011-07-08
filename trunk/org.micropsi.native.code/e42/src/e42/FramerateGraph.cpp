#include "stdafx.h"

#include "baselib/ComObjectPtr.h"

#include "e42/FramerateGraph.h"

#include "e42/core/DeviceStateMgr.h"
#include "e42/core/EngineController.h"
#include "e42/core/TextureFactory.h"
#include "e42/core/VertexShaderFactory.h"
#include "e42/core/VertexBufferFactory.h"
#include "e42/core/VertexDeclarationFactory.h"
#include "e42/Vertices.h"

//-------------------------------------------------------------------------------------------------------------------------------------------
CFramerateGraph::CFramerateGraph(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController)
{
	if (m_pxEngineController == NULL)
		m_pxEngineController = &CEngineController::Get();
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CFramerateGraph::~CFramerateGraph()
{
    Shut();
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CFramerateGraph::Init(int iWidth, int iHeight)
{
    m_iTextureWidth = iWidth;
    m_iTextureHeight = iHeight;


	CComObjectPtr<IDirect3DSurface9> spxBackBuffer;
	m_pxEngineController->GetDevice()->GetBackBuffer(0, 0, D3DBACKBUFFER_TYPE_MONO, &spxBackBuffer);
	D3DSURFACE_DESC xDesc;
	spxBackBuffer->GetDesc(&xDesc);
	spxBackBuffer = 0;

    m_iXWritePos = 0;
	m_iScreenWidth = xDesc.Width;
	m_iScreenHeight = xDesc.Height;


    m_hndGraphTextureSysMem = 
        m_pxEngineController->GetTextureFactory()->
            CreateTexture(
                m_iTextureWidth, m_iTextureHeight,
                1, 0, D3DFMT_A4R4G4B4, D3DPOOL_SYSTEMMEM);

    m_hndGraphTexture = 
        m_pxEngineController->GetTextureFactory()->
            CreateTexture(
                m_iTextureWidth, m_iTextureHeight,
                1, D3DUSAGE_DYNAMIC, D3DFMT_A4R4G4B4, D3DPOOL_DEFAULT);


    float fW = (float)m_iTextureWidth;
    float fH = (float)m_iTextureHeight;

    Vertex3T2 axVertices[4];
    axVertices[0].p = CVec3(0,  (float)m_iScreenHeight,      0.5f);       axVertices[0].t0 = CVec2(0, 0);    
    axVertices[1].p = CVec3(fW, (float)m_iScreenHeight,      0.5f);       axVertices[1].t0 = CVec2(1, 0);  
    axVertices[2].p = CVec3(fW, (float)m_iScreenHeight - fH, 0.5f);       axVertices[2].t0 = CVec2(1, 1);
    axVertices[3].p = CVec3(0,  (float)m_iScreenHeight - fH, 0.5f);       axVertices[3].t0 = CVec2(0, 1);  

    m_hndVB = 
        m_pxEngineController->GetVertexBufferFactory()->
            CreateVertexBufferFVF(4, D3DFVF_VT3T2, D3DUSAGE_WRITEONLY, D3DPOOL_DEFAULT, &axVertices,  "CFramerateGraph-Vertices");


    const char* pcShader = 
        "vs_1_1\n"
        "dcl_position	v0                          \n"
        "dcl_texcoord	v1                          \n"
        "def    c4,     -1.0f, 1.0f, 0.0f, 0.0f     \n"
        "mov    r4,	    c4                          \n"
        "mad    oPos,	v0,     c1,     r4          \n"     // c1: auflösung
        "add    oT0.xy,	v1,     c0                  \n";    // c0: Texturverschiebung

    m_hndVSh = 
        m_pxEngineController->GetVertexShaderFactory()->CreateVertexShaderFromString(pcShader, false, "CFramerateGraph-VertexShader");
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CFramerateGraph::Shut()
{
    m_hndGraphTextureSysMem.Release();
    m_hndGraphTexture.Release();
    m_hndVB.Release();
    m_hndVSh.Release();
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CFramerateGraph::Update(float fDurationOfLastFrame)
{
    float fMaxTime = 0.1f;

    int iGraphHeight = (int)(m_iTextureHeight / fMaxTime * fDurationOfLastFrame);

    RECT rctUpdate;
    rctUpdate.left = m_iXWritePos;
    rctUpdate.right = m_iXWritePos + 1;
    rctUpdate.top = 0;
    rctUpdate.bottom = m_iTextureHeight;


    _D3DLOCKED_RECT xLockedRect;
    ((IDirect3DTexture9*)m_hndGraphTextureSysMem.GetPtr())->LockRect(0, &xLockedRect, &rctUpdate, 0);
    unsigned short* pBitmap = (unsigned short*)xLockedRect.pBits;
    int iPitch = xLockedRect.Pitch / sizeof(unsigned short);


    for (int iY = 0; iY < m_iTextureHeight; iY++)
    {
        pBitmap[iY * iPitch + 0] =
            (iY < iGraphHeight) ? 0x8F00 : 0x40F0;
    }

    for (float f = 0; f < fMaxTime; f += 0.01f)
    {
        int iY = (int)(m_iTextureHeight / fMaxTime * f);
        if ((iY >= 0) && (iY < m_iTextureHeight))
        {
            if (pBitmap[iY * iPitch + 0] != 0x8F00)
                pBitmap[iY * iPitch + 0] = 0x8000;
        }
    }

    ((IDirect3DTexture9*)m_hndGraphTextureSysMem.GetPtr())->UnlockRect(0);


	IDirect3DSurface9* pSrcSurface;
	IDirect3DSurface9* pDstSurface;
    ((IDirect3DTexture9*)m_hndGraphTextureSysMem.GetPtr())->GetSurfaceLevel(0,&pSrcSurface);
	((IDirect3DTexture9*)m_hndGraphTexture.GetPtr())->GetSurfaceLevel(0, &pDstSurface);

	POINT pntDest; 
    pntDest.x = rctUpdate.left;
    pntDest.y = rctUpdate.top;
    HRESULT hr = m_pxEngineController->GetDevice()->UpdateSurface(pSrcSurface, &rctUpdate, pDstSurface, &pntDest);
	assert(SUCCEEDED(hr));

	pSrcSurface->Release();
	pDstSurface->Release();


    m_iXWritePos++;
    m_iXWritePos %= m_iTextureWidth;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CFramerateGraph::Render()
{
    CDeviceStateMgr* pd3dDeviceStateMgr = 
        m_pxEngineController->GetDeviceStateMgr();

    pd3dDeviceStateMgr->SetVertexShader(m_hndVSh.GetPtr());
    pd3dDeviceStateMgr->SetFVF(D3DFVF_VT3T2);
    pd3dDeviceStateMgr->SetStreamSource(0, m_hndVB.GetPtr(), 0, sizeof(Vertex3T2));
    pd3dDeviceStateMgr->SetIndices(NULL);
    pd3dDeviceStateMgr->SetTexture(0, m_hndGraphTexture.GetPtr());

    CVec4 avConstants[2];
    avConstants[0] = CVec4(fmodf((float)m_iXWritePos / m_iTextureWidth, 1.0f), 0, 0, 0);
    avConstants[1] = CVec4(2.0f / m_iScreenWidth, -2.0f / m_iScreenHeight, 0, 1);
    pd3dDeviceStateMgr->SetVertexShaderConstantF(0, (float*)avConstants, 2);

    pd3dDeviceStateMgr->SetRenderState(D3DRS_ZENABLE, FALSE);
    pd3dDeviceStateMgr->SetRenderState(D3DRS_CULLMODE, D3DCULL_NONE);


    pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_COLOROP, D3DTOP_SELECTARG1);
    pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_COLORARG1, D3DTA_TEXTURE);
    pd3dDeviceStateMgr->SetTextureStageState(1, D3DTSS_COLOROP, D3DTOP_DISABLE);

    pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_ALPHAOP, D3DTOP_SELECTARG1);
    pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_ALPHAARG1, D3DTA_TEXTURE);
    pd3dDeviceStateMgr->SetTextureStageState(1, D3DTSS_ALPHAOP, D3DTOP_DISABLE);


    pd3dDeviceStateMgr->SetRenderState(D3DRS_CLIPPLANEENABLE, 0);
    pd3dDeviceStateMgr->SetRenderState(D3DRS_ALPHATESTENABLE, FALSE);
    pd3dDeviceStateMgr->SetRenderState(D3DRS_ALPHABLENDENABLE, TRUE);
    pd3dDeviceStateMgr->SetRenderState(D3DRS_SRCBLEND, D3DBLEND_SRCALPHA);
    pd3dDeviceStateMgr->SetRenderState(D3DRS_DESTBLEND, D3DBLEND_INVSRCALPHA);
    pd3dDeviceStateMgr->SetSamplerState(0, D3DSAMP_ADDRESSU, D3DTADDRESS_WRAP);

    m_pxEngineController->GetDevice()->DrawPrimitive(D3DPT_TRIANGLEFAN, 0, 2);
}
//-------------------------------------------------------------------------------------------------------------------------------------------

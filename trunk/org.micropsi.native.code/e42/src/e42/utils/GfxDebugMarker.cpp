#include "stdafx.h"

#include "e42/utils/GfxDebugMarker.h"

#include "baselib/geometry/Matrix.h"
#include "e42/core/EngineController.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/Model.h"
#include "e42/core/ModelFactory.h"
#include "e42/core/MeshFactory.h"
#include "e42/Camera.h"

#include "baselib/geometry/Matrix.h"

//---------------------------------------------------------------------------------------------------------------------
CGfxDebugMarker::CGfxDebugMarker()
{
    assert(ms_pxGfxDebugMarker == 0);
    ms_pxGfxDebugMarker = this;

    CreateModels();
}
//---------------------------------------------------------------------------------------------------------------------
CGfxDebugMarker::~CGfxDebugMarker()
{
    ms_pxGfxDebugMarker = 0;
}
//---------------------------------------------------------------------------------------------------------------------
CGfxDebugMarker* CGfxDebugMarker::ms_pxGfxDebugMarker;
//---------------------------------------------------------------------------------------------------------------------
CGfxDebugMarker& 
CGfxDebugMarker::Get()
{
    if (!ms_pxGfxDebugMarker)
    {
        ms_pxGfxDebugMarker = new CGfxDebugMarker();
    }
    return *ms_pxGfxDebugMarker;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CGfxDebugMarker::Shut()
{
    delete ms_pxGfxDebugMarker;
    ms_pxGfxDebugMarker = 0;
}
//---------------------------------------------------------------------------------------------------------------------
void
CGfxDebugMarker::CreateModels()
{
    m_hndSphere = CEngineController::Get().GetModelFactory()->CreateModelFromFile("model>model.debugsphere.x");
    m_hndSphere->SetFrameCullingTest(false);
    m_hndBox = CEngineController::Get().GetModelFactory()->CreateModelFromFile("model>model.debugbox.x");
    m_hndBox->SetFrameCullingTest(false);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CGfxDebugMarker::DrawSphere(const CVec3& vPos, const CCamera* pCamera, float fScale, CColor color)
{
    CDeviceStateMgr* pd3dDeviceStateMgr = CEngineController::Get().GetDeviceStateMgr();
    pd3dDeviceStateMgr->SetRenderState(D3DRS_FILLMODE, D3DFILL_WIREFRAME);
    pd3dDeviceStateMgr->SetRenderState(D3DRS_TEXTUREFACTOR, color.m_dwColor);


    CMat4S matTransform;
    matTransform.SetScale(fScale);
    matTransform(3, 3) = 1;
    matTransform.Translate(vPos);


    TRenderContextPtr spxRenderContext;
    spxRenderContext.Create();
    pCamera->SetupRenderContext(spxRenderContext);

    m_hndSphere->Render(spxRenderContext, matTransform);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CGfxDebugMarker::DrawBox(const CVec3& vPos, const CCamera* pCamera, float fScale, CColor color)
{
    CDeviceStateMgr* pd3dDeviceStateMgr = CEngineController::Get().GetDeviceStateMgr();
    pd3dDeviceStateMgr->SetRenderState(D3DRS_FILLMODE, D3DFILL_WIREFRAME);
    pd3dDeviceStateMgr->SetRenderState(D3DRS_TEXTUREFACTOR, color.m_dwColor);


    CMat4S matTransform;
    matTransform.SetScale(fScale);
    matTransform(3, 3) = 1;
    matTransform.Translate(vPos);


    TRenderContextPtr spxRenderContext;
    spxRenderContext.Create();
    pCamera->SetupRenderContext(spxRenderContext);

    m_hndBox->Render(spxRenderContext, matTransform);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CGfxDebugMarker::SetRenderStates(CMat4S matTransform, const CCamera* pCamera, CColor color, float fScale, bool bWireframe)
{
    CDeviceStateMgr* pd3dDeviceStateMgr = CEngineController::Get().GetDeviceStateMgr();

	CMat4S matScale(fScale, 0, 0, 0,
					0, fScale, 0, 0,
					0, 0, fScale, 0, 
					0, 0, 0, 1);


	pd3dDeviceStateMgr->SetPixelShader(NULL);
	pd3dDeviceStateMgr->SetVertexShader(NULL);

	pd3dDeviceStateMgr->SetTransform(D3DTS_WORLD, (D3DXMATRIX*)&(matScale * matTransform));
	pd3dDeviceStateMgr->SetTransform(D3DTS_VIEW, (D3DXMATRIX*)&(pCamera->GetViewMatrix()));
	pd3dDeviceStateMgr->SetTransform(D3DTS_PROJECTION, (D3DXMATRIX*)&(pCamera->GetProjectionMatrix()));

	pd3dDeviceStateMgr->SetRenderState(D3DRS_STENCILENABLE, FALSE);
	
	pd3dDeviceStateMgr->SetRenderState(D3DRS_ALPHATESTENABLE, FALSE);
	pd3dDeviceStateMgr->SetRenderState(D3DRS_ZENABLE, TRUE);
	pd3dDeviceStateMgr->SetRenderState(D3DRS_ZWRITEENABLE, FALSE);

	pd3dDeviceStateMgr->SetRenderState(D3DRS_TEXTUREFACTOR, color.m_dwColor);

	if (pCamera->GetLeftHandedWorldCoordinateSystem())
	{
		pd3dDeviceStateMgr->SetRenderState(D3DRS_CULLMODE, D3DCULL_CCW);
	}
	else
	{
		pd3dDeviceStateMgr->SetRenderState(D3DRS_CULLMODE, D3DCULL_CW);
	}


	pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_COLOROP, D3DTOP_SELECTARG1);
	pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_COLORARG1, D3DTA_TFACTOR);

	pd3dDeviceStateMgr->SetTextureStageState(1, D3DTSS_COLOROP, D3DTOP_DISABLE);


	pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_ALPHAOP, D3DTOP_SELECTARG1);
	pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_ALPHAARG1, D3DTA_TFACTOR);

	pd3dDeviceStateMgr->SetTextureStageState(1, D3DTSS_ALPHAOP, D3DTOP_DISABLE);


	if (bWireframe)
	{
		pd3dDeviceStateMgr->SetRenderState(D3DRS_FILLMODE, D3DFILL_WIREFRAME);
		pd3dDeviceStateMgr->SetRenderState(D3DRS_ALPHABLENDENABLE, FALSE);
	}
	else
	{
		pd3dDeviceStateMgr->SetRenderState(D3DRS_FILLMODE, D3DFILL_SOLID);
		pd3dDeviceStateMgr->SetRenderState(D3DRS_ALPHABLENDENABLE, TRUE);
		pd3dDeviceStateMgr->SetRenderState(D3DRS_SRCBLEND, D3DBLEND_SRCALPHA);
		pd3dDeviceStateMgr->SetRenderState(D3DRS_DESTBLEND, D3DBLEND_ONE);
	}
}
//---------------------------------------------------------------------------------------------------------------------
void 
CGfxDebugMarker::DrawBox(const CMat4S& matTransform, const CCamera* pCamera, float fWidth, float fHeight, float fDepth, CColor color, bool bWireframe)
{
	TMeshHandle hndMesh = CEngineController::Get().GetMeshFactory()->CreateBox(fWidth, fHeight, fDepth);

	if (bWireframe)
	{
		SetRenderStates(matTransform, pCamera, CColor(color.m_dwColor | 0xff000000), 1, true);
		hndMesh->DrawSubset(0);
	}

	SetRenderStates(matTransform, pCamera, color, 1, false);
	hndMesh->DrawSubset(0);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CGfxDebugMarker::DrawSphere(const CMat4S& matTransform, const CCamera* pCamera, float fRadius, int iSlices, int iStacks, CColor color, bool bWireframe)
{
	TMeshHandle hndMesh = CEngineController::Get().GetMeshFactory()->CreateSphere(fRadius, iSlices, iStacks);

	if (bWireframe)
	{
		SetRenderStates(matTransform, pCamera, CColor(color.m_dwColor | 0xff000000), 1, true);
		hndMesh->DrawSubset(0);
	}

	SetRenderStates(matTransform, pCamera, color, 1, false);
	hndMesh->DrawSubset(0);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CGfxDebugMarker::DrawCylinder(const CMat4S& matTransform, const CCamera* pCamera, float fRadius1, float fRadius2, float fLength, int iSlices, int iStacks, CColor color, bool bWireframe)
{
	TMeshHandle hndMesh = CEngineController::Get().GetMeshFactory()->CreateCylinder(fRadius1, fRadius2, fLength, iSlices, iStacks);

	if (bWireframe)
	{
		SetRenderStates(matTransform, pCamera, CColor(color.m_dwColor | 0xff000000), 1, true);
		hndMesh->DrawSubset(0);
	}

	SetRenderStates(matTransform, pCamera, color, 1, false);
	hndMesh->DrawSubset(0);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CGfxDebugMarker::DrawTorus(const CMat4S& matTransform, const CCamera* pCamera, float fInnerRadius, float fOuterRadius, int iSides, int iRings, CColor color, bool bWireframe)
{
	TMeshHandle hndMesh = CEngineController::Get().GetMeshFactory()->CreateTorus(fInnerRadius, fOuterRadius, iSides, iRings);

	if (bWireframe)
	{
		SetRenderStates(matTransform, pCamera, CColor(color.m_dwColor | 0xff000000), 1, true);
		hndMesh->DrawSubset(0);
	}

	SetRenderStates(matTransform, pCamera, color, 1, false);
	hndMesh->DrawSubset(0);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CGfxDebugMarker::DrawTeapot(const CMat4S& matTransform, const CCamera* pCamera, float fScale, CColor color, bool bWireframe)
{
	TMeshHandle hndMesh = CEngineController::Get().GetMeshFactory()->CreateTeapot();

	if (bWireframe)
	{
		SetRenderStates(matTransform, pCamera, CColor(color.m_dwColor | 0xff000000), 1, true);
		hndMesh->DrawSubset(0);
	}

	SetRenderStates(matTransform, pCamera, color, 1, false);
	hndMesh->DrawSubset(0);
}
//---------------------------------------------------------------------------------------------------------------------

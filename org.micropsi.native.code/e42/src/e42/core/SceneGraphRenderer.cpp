#include "stdafx.h"

#include "e42/core/SceneGraphRenderer.h"

#include "baselib/geometry/BoundingSphere.h"

#include "e42/core/D3DXFrame.h"
#include "e42/core/ViewFrustum.h"
#include "e42/core/D3DXMeshContainer.h"
#include "e42/core/MeshRenderer.h"

#include <d3dx9math.h>

//-----------------------------------------------------------------------------------------------------------------------------------------
CSceneGraphRenderer::CSceneGraphRenderer()
:	m_pViewFrustum(NULL)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
__fastcall 
CSceneGraphRenderer::RecursiveRenderMeshContainer(
		const CD3DXMeshContainer* pMeshContainer)
{
	if (pMeshContainer->bIsVisible)
	{
		if (pMeshContainer->UsesSkinning())
		{
			CMeshRenderer::Render(
				pMeshContainer,
				m_matRootWorldTransform,
				m_spxRenderContext);
		}
		else
		{
			CMeshRenderer::Render(
				pMeshContainer,
				m_matRootWorldTransform,
				m_spxRenderContext);
		}
	}

	if (pMeshContainer->NextMeshContainer())
	{
		RecursiveRenderMeshContainer(pMeshContainer->NextMeshContainer());
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool 
__fastcall 
CSceneGraphRenderer::ScalingIsZero(const CMat4S& matTransform)
{
	return 
	   ((fabsf(matTransform.m_af[0]) < 1e-3f) &&
		(fabsf(matTransform.m_af[1]) < 1e-3f) &&
		(fabsf(matTransform.m_af[2]) < 1e-3f));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
__fastcall 
CSceneGraphRenderer::RecursiveRenderFrame(CD3DXFrame* pFrame)
{
	// user definiertes culling
	bool bRenderFrame = pFrame->bIsVisible;


	// culling durch Viewfrustum / Kamera
	if (bRenderFrame &&
		m_pViewFrustum)
	{
		CBoundingSphere xBoundingSphere;
		xBoundingSphere.m_vCenter = pFrame->xBoundingSphere.m_vCenter ^ pFrame->matCombinedTransformation ^ m_matRootWorldTransform;
		xBoundingSphere.m_fRadius = pFrame->xBoundingSphere.m_fRadius;
		bRenderFrame = m_pViewFrustum->SphereIntersects(xBoundingSphere);
	}


	// animierte Visibility
	if (bRenderFrame &&
		pFrame->pVisibilitySwitchFrame &&
		ScalingIsZero(pFrame->pVisibilitySwitchFrame->TransformationMatrix()))
	{
		bRenderFrame = false;
	}


	// (rekursives) Rendering des Frame-Inhaltes
	if (bRenderFrame)
	{
		if (pFrame->GetFirstMeshContainer())
		{
			RecursiveRenderMeshContainer(pFrame->GetFirstMeshContainer());
		}

		if (pFrame->GetFirstChild()) 
			RecursiveRenderFrame(pFrame->GetFirstChild());
	}


	// Geschwisternode rendern
	if (pFrame->GetSibling()) 
		RecursiveRenderFrame(pFrame->GetSibling());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CSceneGraphRenderer::RenderFrameHierarchy(
	CD3DXFrame* pFrame, TRenderContextPtr spxRenderContext, 
	const CMat4S& matWorldTransform, bool bFrameCullingEnable)
{
	CSceneGraphRenderer xSceneGraphRenderer;

	xSceneGraphRenderer.m_spxRenderContext =       spxRenderContext;
	xSceneGraphRenderer.m_matRootWorldTransform =  matWorldTransform;

	if (bFrameCullingEnable)
	{
		xSceneGraphRenderer.m_pViewFrustum = &spxRenderContext->m_xViewFrustum;
	}


	xSceneGraphRenderer.RecursiveRenderFrame(pFrame);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphRenderer::RecursiveUpdateCombinedFrameMatrices(CD3DXFrame* pFrame, const CMat4S& matParentTransform)
{
	//if (m_bFrameCullingEnable)
	//{
	//    if ((pFrame->Name == 0) ||
	//        (strstr(pFrame->Name, "groundPlane_transform") == 0))
	//    {
	//        float fDet = D3DXMatrixDeterminant(&pFrame->TransformationMatrix);
	//        assert(fabsf(fDet - 1) < 0.01f);
	//    }
	//}

	pFrame->matCombinedTransformation = pFrame->TransformationMatrix() * matParentTransform;

	if (pFrame->GetFirstChild()) RecursiveUpdateCombinedFrameMatrices(pFrame->GetFirstChild(), pFrame->matCombinedTransformation);
	if (pFrame->GetSibling())    RecursiveUpdateCombinedFrameMatrices(pFrame->GetSibling(), matParentTransform);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphRenderer::UpdateCombinedFrameMatrices(CD3DXFrame* pFrame)
{
	RecursiveUpdateCombinedFrameMatrices(pFrame, CMat4S::mIdentity);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

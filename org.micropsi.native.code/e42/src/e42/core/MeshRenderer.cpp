#include "stdafx.h"

#include "e42/core/MeshRenderer.h"

#include "e42/core/D3DXMeshContainer.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/EngineController.h"
#include "e42/core/EffectShader.h"
#include "e42/core/EngineStats.h"
#include "e42/Camera.h"

#include "e42/core/D3DXMaterial.h"


using std::string;
//-----------------------------------------------------------------------------------------------------------------------------------------
CMat4S                      CMeshRenderer::ms_amatBoneMatrices[MAX_BONE_MATRICES];
CMat4S                      CMeshRenderer::ms_matWorldMatrix;
const CD3DXMeshContainer*   CMeshRenderer::ms_pMeshContainer;
TRenderContextPtr           CMeshRenderer::ms_spxRenderContext;
CDeviceStateMgr*            CMeshRenderer::ms_pd3dDeviceStateMgr;
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMeshRenderer::SetupMatrices_Skinning(int iAttributeGroup, const CEffectShader* pEffect)
{
	assert(ms_pMeshContainer->dwNumPaletteEntries <= MAX_BONE_MATRICES);

	_D3DXBONECOMBINATION* pBoneComb = 
		reinterpret_cast<_D3DXBONECOMBINATION*>(ms_pMeshContainer->spxBoneCombinationBuffer->GetBufferPointer());

	for (unsigned int iBonePaletteEntry = 0; iBonePaletteEntry < ms_pMeshContainer->dwNumPaletteEntries; iBonePaletteEntry++)
	{
		int iMatrixIndex = pBoneComb[iAttributeGroup].BoneId[iBonePaletteEntry];
		if (iMatrixIndex != UINT_MAX)
		{
			ms_amatBoneMatrices[iBonePaletteEntry] =
				*(const CMat4S*)ms_pMeshContainer->SkinInfo()->GetBoneOffsetMatrix(iMatrixIndex) * 
				*ms_pMeshContainer->ppBoneMatrices[iMatrixIndex] * ms_matWorldMatrix;
		}
	}

	pEffect->SetWorldMatrixArray(ms_amatBoneMatrices, ms_pMeshContainer->dwNumPaletteEntries);
	pEffect->SetViewProjectionMatrix(ms_spxRenderContext->m_matViewProjectionTransform);
	pEffect->SetNumBones(ms_pMeshContainer->dwMaxBoneInfluencesPerVertex);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMeshRenderer::SetupMatrices_Standard(const CEffectShader* pEffect)
{
	CMat4S matWorld = 
		ms_pMeshContainer->ppBoneMatrices[0] != NULL 
			?	*ms_pMeshContainer->ppBoneMatrices[0] * ms_matWorldMatrix
			:	ms_matWorldMatrix;

	pEffect->SetWorldMatrix(matWorld);
	

	if (pEffect->m_hndWorldInverse)
	{
		pEffect->SetWorldInverseMatrix(matWorld.GetInverse());
	}


	pEffect->SetViewProjectionMatrix(ms_spxRenderContext->m_matViewProjectionTransform);
	pEffect->SetViewInverseMatrix(ms_spxRenderContext->m_matViewInverseTransform);

	pEffect->SetWorldViewProjectionMatrix(matWorld * ms_spxRenderContext->m_matViewProjectionTransform);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMeshRenderer::SetupLights(int iAttribID, const CEffectShader* pEffect)
{
	pEffect->SetLightDirVector(ms_spxRenderContext->m_vLightDir.GetExtended(0));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMeshRenderer::SetTechnique(const CEffectShader* pEffect)
{
	if (ms_spxRenderContext->m_sTechnique.empty())
	{
		if (!ms_pMeshContainer->UsesSkinning())
		{
			pEffect->SetDefaultTechnique_StaticMesh();
		}
		else
		{
			pEffect->SetDefaultTechnique_SkinnedMesh();
		}
	}
	else
	{
		if (!ms_pMeshContainer->UsesSkinning())
		{
			pEffect->SetTechnique((ms_spxRenderContext->m_sTechnique + "_staticmesh").c_str());
		}
		else
		{
			pEffect->SetTechnique((ms_spxRenderContext->m_sTechnique + "_skinnedmesh").c_str());
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMeshRenderer::SetConstants(const CEffectShader* pEffect)
{
	pEffect->SetTime(ms_spxRenderContext->m_fTime);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMeshRenderer::RenderMeshSubset(int iSubset, CEffectShader* pEffect)
{
	string sTechnique = 
		ms_pMeshContainer->UsesSkinning() 
			? ms_spxRenderContext->m_sTechnique + "_skinnedmesh"
			: ms_spxRenderContext->m_sTechnique + "_staticmesh";



	// Effekt starten
	unsigned int uiNumPasses = 0;
	pEffect->Begin(&uiNumPasses, D3DXFX_DONOTSAVESTATE);


	// alle pässe rendern
	for (UINT iPass = 0; iPass < uiNumPasses; iPass++)
	{
		pEffect->BeginPass(iPass);
		HRESULT hr = ms_pMeshContainer->GetMesh().GetPtr()->DrawSubset(iSubset);
		assert(SUCCEEDED(hr));

		pEffect->EndPass();
	}


	pEffect->End();

	
	ms_spxRenderContext->m_pxEngineController->GetEngineStats()->m_iRenderedVertexGroups++;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CMeshRenderer::RenderMesh_Skinning()
{
	for (unsigned int iBoneCombination = 0; iBoneCombination < ms_pMeshContainer->dwNumBoneCombinations; iBoneCombination++)
	{
		_D3DXBONECOMBINATION* pBoneComb = 
			reinterpret_cast<_D3DXBONECOMBINATION*>(ms_pMeshContainer->spxBoneCombinationBuffer->GetBufferPointer());

		int iMaterial = pBoneComb[iBoneCombination].AttribId;

		CMaterial* pxMaterial = ms_pMeshContainer->GetMaterials() + iMaterial;
		CEffectShader* pEffect = pxMaterial->GetEffect().GetPtr();

		pxMaterial->UpdateEffect();

		SetupMatrices_Skinning(iBoneCombination, pEffect);
		SetupLights(iMaterial, pEffect);
		SetTechnique(pEffect);
		SetConstants(pEffect);

		RenderMeshSubset(iBoneCombination, pEffect);
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CMeshRenderer::RenderMesh_Standard()
{
	for (unsigned int iMaterial = 0; iMaterial < ms_pMeshContainer->GetNumMaterials(); iMaterial++)
	{
		CMaterial* pxMaterial = ms_pMeshContainer->GetMaterials() + iMaterial;
		CEffectShader* pEffect = pxMaterial->GetEffect().GetPtr();

		pxMaterial->UpdateEffect();

		SetupMatrices_Standard(pEffect);
		SetupLights(iMaterial, pEffect);
		SetTechnique(pEffect);
		SetConstants(pEffect);

		RenderMeshSubset(iMaterial, pEffect);
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMeshRenderer::Render(
	const CD3DXMeshContainer* pMeshContainer, const CMat4S& matWorldTransform, TRenderContextPtr spxRenderContext)
{
	ms_pMeshContainer =		pMeshContainer;
	ms_spxRenderContext =	spxRenderContext;
	ms_pd3dDeviceStateMgr =	spxRenderContext->m_pxEngineController->GetDeviceStateMgr();

	ms_matWorldMatrix =		matWorldTransform;


	{
		if (ms_pMeshContainer->bUsesSoftwareVertexProcessing)
		{
			ms_pd3dDeviceStateMgr->SetSoftwareVertexProcessing(TRUE);
		}


		if (ms_pMeshContainer->UsesSkinning())
		{
			RenderMesh_Skinning();
		}
		else
		{
			RenderMesh_Standard();
		}

		CEngineStats* pEngineStats = ms_spxRenderContext->m_pxEngineController->GetEngineStats();
		pEngineStats->m_iRenderedPolys += ms_pMeshContainer->GetMesh().GetPtr()->GetNumFaces();
		pEngineStats->m_iRenderedMeshes++;

		
		if (ms_pMeshContainer->bUsesSoftwareVertexProcessing)
		{
			ms_pd3dDeviceStateMgr->SetSoftwareVertexProcessing(FALSE);
		}
	}

	ms_spxRenderContext = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

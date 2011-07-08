#include "stdafx.h"

#include "baselib/utils.h"
#include "e42/core/D3DXMeshContainer.h"
#include "e42/core/D3DXMaterial.h"
#include "e42/core/D3DXEffectInstance.h"
#include "e42/core/D3DVertexDeclaration.h"
#include "e42/core/EngineController.h"
#include "e42/core/D3DXFrame.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMeshContainer::CD3DXMeshContainer()
{
    ZeroMemory(this, sizeof(CD3DXMeshContainer));

    this->Name = NULL;
    this->pxMaterials = NULL;
    this->pMaterials = NULL;
    this->pEffects = NULL;

    this->dwMaxBoneInfluencesPerVertex = NULL;
    this->ppBoneMatrices = NULL;
    this->pxMaterials = NULL;

    this->dwNumBoneCombinations = 0;
    this->dwNumPaletteEntries = 0;

	this->bUsesSoftwareVertexProcessing = false;
    this->bIsVisible = true;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMeshContainer::~CD3DXMeshContainer()
{
    this->hndMesh.Release();
    this->SkinInfo() = NULL;
    this->spxBoneCombinationBuffer = NULL;

    delete [] this->Name;

    SetNumMaterials(0);

    DeleteAdjacencyArray();
    DeleteBoneMatrixPtrArray();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMeshContainer* 
CD3DXMeshContainer::Create()
{
    return new CD3DXMeshContainer();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXMeshContainer::Destroy(CD3DXMeshContainer* pxMeshContainer)
{
    delete pxMeshContainer;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXMeshContainer::RecursiveDestroy(CD3DXMeshContainer* pxMeshContainer)
{
	if (pxMeshContainer->pNextMeshContainer)
	{
		RecursiveDestroy(pxMeshContainer->NextMeshContainer());
		pxMeshContainer->pNextMeshContainer = NULL;
	}

    delete pxMeshContainer;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXMeshContainer::SetName(const char* pcName)
{
    if (pcName)
    {
        assert(this->Name == 0);
        Name = new char[strlen(pcName) + 1];
        strcpy(this->Name, pcName);
    }
    else
    {
        delete [] this->Name;
        this->Name = NULL;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int
CD3DXMeshContainer::CalcMeshRank() const
{
    if (__super::NumMaterials > 0)
    {
        return this->pxMaterials[0].GetMaterialSortingID();
    }

    return 0;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXMeshContainer::SetNumMaterials(DWORD dwNumMaterials)
{
    // reallokation?
    if (__super::NumMaterials != dwNumMaterials)
    {
        if (__super::NumMaterials != 0)
        {
            delete [] GetD3DXMaterials();
            __super::pMaterials = NULL;

            delete [] GetD3DXEffectInstances();
            __super::pEffects = NULL;

            delete [] GetMaterials();
            pxMaterials = NULL;
        }
        if (dwNumMaterials != 0)
        {
            assert(__super::pMaterials == NULL && __super::pEffects == NULL && pxMaterials == NULL);

            __super::pMaterials = (D3DXMATERIAL*)new CD3DXMaterial[dwNumMaterials];
            __super::pEffects = (D3DXEFFECTINSTANCE*)new CD3DXEffectInstance[dwNumMaterials];

            pxMaterials = new CMaterial[dwNumMaterials];
        }
        __super::NumMaterials = dwNumMaterials;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXMeshContainer::SetMaterials(DWORD dwNumMaterials, const CMaterial* pxMaterials)
{
    SetNumMaterials(dwNumMaterials);

    // Zuweisung
    if (pxMaterials)
    {
        for (DWORD dwMaterial = 0; dwMaterial < dwNumMaterials; dwMaterial++)
        {
            GetMaterials()[dwMaterial] = pxMaterials[dwMaterial];
        }
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXMeshContainer::SetD3DXMaterials(DWORD dwNumMaterials, const CD3DXMaterial* pxD3DXMaterials)
{
    SetNumMaterials(dwNumMaterials);

    // Zuweisung
    if (pxD3DXMaterials)
    {
        for (DWORD dwMaterial = 0; dwMaterial < dwNumMaterials; dwMaterial++)
        {
            GetD3DXMaterials()[dwMaterial] = pxD3DXMaterials[dwMaterial];
        }
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXMeshContainer::SetD3DXEffectInstances(DWORD dwNumMaterials, const CD3DXEffectInstance* pxD3DXEffectInstance)
{
    SetNumMaterials(dwNumMaterials);

    // Zuweisung
    if (pxD3DXEffectInstance)
    {
        for (DWORD dwMaterial = 0; dwMaterial < dwNumMaterials; dwMaterial++)
        {
            GetD3DXEffectInstances()[dwMaterial] = pxD3DXEffectInstance[dwMaterial];
        }
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXMeshContainer::CreateBoneMatrixPtrArray()
{
    assert(this->ppBoneMatrices == NULL);

	int iSize = 1;

	if (this->pSkinInfo)
	{
		iSize = this->pSkinInfo->GetNumBones();
	}

	this->ppBoneMatrices = new CMat4S*[iSize];
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXMeshContainer::DeleteBoneMatrixPtrArray()
{
    delete [] this->ppBoneMatrices;
    this->ppBoneMatrices = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXMeshContainer::GetVertexDeclaration(CD3DVertexDeclaration* pxVertexDeclarationOut) const
{
	this->MeshData.pMesh->GetDeclaration(pxVertexDeclarationOut->m_axVertexElements);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int
CD3DXMeshContainer::CalcVertexElementOffset(D3DDECLUSAGE eUsage, int iUsageIdx) const
{
	CD3DVertexDeclaration xDeclaration;
	this->MeshData.pMesh->GetDeclaration(xDeclaration.m_axVertexElements);

	CD3DVertexElement9* pxElement = xDeclaration.FindElement(eUsage, iUsageIdx);

	if (pxElement)
	{
		return pxElement->Offset;
	}

	return -1;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
float
CD3DXMeshContainer::CalcVolume() const
{
	// arbeitet nur mit geschlossenen Meshes!!!

	int iPositionOffset = CalcVertexElementOffset(D3DDECLUSAGE_POSITION);

	if (iPositionOffset == -1)
	{
		assert(false);
		return 0;
	}


	unsigned char* pVertices;
	this->MeshData.pMesh->LockVertexBuffer(D3DLOCK_READONLY, (void**)&pVertices);
	unsigned char* pIndices;
	this->MeshData.pMesh->LockIndexBuffer(D3DLOCK_READONLY, (void**)&pIndices);

	bool b32BitIndices = (this->MeshData.pMesh->GetOptions() & D3DXMESH_32BIT) != 0;
	int iVertexSize = this->MeshData.pMesh->GetNumBytesPerVertex();


	float fVolume = 0;

	for (int iFace = 0; iFace < (int)this->MeshData.pMesh->GetNumFaces(); iFace++)
	{
		int iIdx0, iIdx1, iIdx2;
		if (b32BitIndices)
		{
			iIdx0 = ((unsigned int*)pIndices)[iFace * 3 + 0];
			iIdx1 = ((unsigned int*)pIndices)[iFace * 3 + 1];
			iIdx2 = ((unsigned int*)pIndices)[iFace * 3 + 2];
		}
		else
		{
			iIdx0 = ((unsigned short*)pIndices)[iFace * 3 + 0];
			iIdx1 = ((unsigned short*)pIndices)[iFace * 3 + 1];
			iIdx2 = ((unsigned short*)pIndices)[iFace * 3 + 2];
		}

		const CVec3 *pvPos0, *pvPos1, *pvPos2;
		pvPos0 = (CVec3*)(pVertices + iIdx0 * iVertexSize + iPositionOffset);
		pvPos1 = (CVec3*)(pVertices + iIdx1 * iVertexSize + iPositionOffset);
		pvPos2 = (CVec3*)(pVertices + iIdx2 * iVertexSize + iPositionOffset);


		fVolume += ((pvPos1->y() - pvPos0->y()) * (pvPos2->z() - pvPos0->z()) -
					(pvPos1->z() - pvPos0->z()) * (pvPos2->y() - pvPos0->y())) * (pvPos0->x() + pvPos1->x() + pvPos2->x());
	}


	this->MeshData.pMesh->UnlockVertexBuffer();
	this->MeshData.pMesh->UnlockIndexBuffer();

	return fabsf(fVolume / 6);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CBoundingSphere	
CD3DXMeshContainer::CalcBoundingSphere() const
{
	unsigned char* pVertices;
	this->MeshData.pMesh->LockVertexBuffer(D3DLOCK_READONLY, (void**)&pVertices);
	unsigned char* pIndices;
	this->MeshData.pMesh->LockIndexBuffer(D3DLOCK_READONLY, (void**)&pIndices);

	D3DXVECTOR3* pFirstPosition = (D3DXVECTOR3*)(pVertices + CalcVertexElementOffset(D3DDECLUSAGE_POSITION));

	CBoundingSphere xResult;

	D3DXComputeBoundingSphere(
		pFirstPosition,
		this->MeshData.pMesh->GetNumVertices(),
		this->MeshData.pMesh->GetNumBytesPerVertex(),
		(D3DXVECTOR3*)&xResult.m_vCenter,
		&xResult.m_fRadius);
	

	this->MeshData.pMesh->UnlockVertexBuffer();
	this->MeshData.pMesh->UnlockIndexBuffer();

	return xResult;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXMeshContainer::SetupBoneMatrixPointers(const CD3DXFrame* pxRootFrame)
{
	// verbindet die TransformationsMatrizen des Meshes mit den Matrizen des Skeletons

	if (!this->ppBoneMatrices)
	{
		CreateBoneMatrixPtrArray();
	}


	if (this->UsesSkinning())
	{
		int iNumBones = this->pSkinInfo->GetNumBones();

		for (int iBone = 0; iBone < iNumBones; iBone++)
		{
			const char* pcBoneName = this->pSkinInfo->GetBoneName(iBone);

			CD3DXFrame* pxFrame = (CD3DXFrame*)D3DXFrameFind(&pxRootFrame->D3DXFrame(), pcBoneName);
			assert(pxFrame);

			this->ppBoneMatrices[iBone] = &pxFrame->matCombinedTransformation;
		}
	}
	else
	{
		if (pFrameParent)
		{
			this->ppBoneMatrices[0] = &pFrameParent->matCombinedTransformation;
		}
		else
		{
			this->ppBoneMatrices[0] = NULL;
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMeshContainer*
CD3DXMeshContainer::CloneMeshContainerList() const
{
	CD3DXMeshContainer* pxClonedMeshContainer = CD3DXMeshContainer::Create();


	pxClonedMeshContainer->SetName(GetName());
	pxClonedMeshContainer->SetMesh(GetMesh());


	if (this->pMaterials)
	{	// (D3DX-Materials)
		pxClonedMeshContainer->SetD3DXMaterials(this->NumMaterials, GetD3DXMaterials());
	}

	if (this->pEffects)
	{	// (Effect-Parameter)
		pxClonedMeshContainer->SetD3DXEffectInstances(this->NumMaterials, GetD3DXEffectInstances());
	}

	if (this->pAdjacency)
	{
		pxClonedMeshContainer->CreateAdjacencyArray();
		memcpy(pxClonedMeshContainer->pAdjacency, this->pAdjacency, GetMesh()->GetNumFaces() * 3);
	}

	if (this->pxMaterials)
	{	// (E42-Materials)
		pxClonedMeshContainer->SetMaterials(this->NumMaterials, this->pxMaterials);
	}

	if (this->pSkinInfo)
	{
		pxClonedMeshContainer->SkinInfo() = this->SkinInfo();		// wird geshared
		//pSkinInfo->Clone(pxClonedMeshContainer->pSkinInfo);
	}

	if (NextMeshContainer())
	{
		pxClonedMeshContainer->NextMeshContainer() = NextMeshContainer()->CloneMeshContainerList();
	}

	pxClonedMeshContainer->NumMaterials = NumMaterials;

	pxClonedMeshContainer->dwMaxBoneInfluencesPerVertex = this->dwMaxBoneInfluencesPerVertex;
	pxClonedMeshContainer->dwNumBoneCombinations = this->dwNumBoneCombinations;
	pxClonedMeshContainer->spxBoneCombinationBuffer = this->spxBoneCombinationBuffer;
	pxClonedMeshContainer->dwNumPaletteEntries = this->dwNumPaletteEntries;
	pxClonedMeshContainer->bUsesSoftwareVertexProcessing = this->bUsesSoftwareVertexProcessing;
	pxClonedMeshContainer->bIsVisible = this->bIsVisible;

	// ppBoneMatrices;	// kommt erst später


	return pxClonedMeshContainer;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

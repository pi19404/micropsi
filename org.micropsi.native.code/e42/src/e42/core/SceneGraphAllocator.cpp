#include "stdafx.h"

#include "e42/core/SceneGraphAllocator.h"

#include <string>

#include "e42/core/EngineController.h"
#include "e42/core/XFileLoader.h"
#include "e42/core/MaterialConverter.h"
#include "e42/core/MeshLoaderOptions.h"
#include "e42/core/MeshFactory.h"

#include "baselib/Filelocator.h"

#include "e42/core/D3DXMaterial.h"
#include "e42/core/D3DXEffectInstance.h"
#include "e42/core/D3DXEffectDefault.h"

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CSceneGraphAllocator::CSceneGraphAllocator(CEngineController* pxEngineController)
:	m_pxEngineController	(pxEngineController),
	m_pxMaterialConverter	(NULL),
	m_dwMeshLoaderOptions	(0),
	m_dwMeshOptions			(0)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CSceneGraphAllocator::~CSceneGraphAllocator()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
CSceneGraphAllocator::CreateFrame(LPCTSTR pcName, _D3DXFRAME* *ppNewFrame)
{
	CD3DXFrame* pFrame = CD3DXFrame::Create();

	pFrame->SetName(pcName);

	// Output setzen
	*ppNewFrame = (_D3DXFRAME*)pFrame;
	return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphAllocator::SetMeshContainerMaterials(CD3DXMeshContainer* pMeshContainer, const D3DXMATERIAL* pMaterials) const
{
	char acFilename[256];

	for (unsigned int iMaterial = 0; iMaterial < (int)pMeshContainer->GetNumMaterials(); iMaterial++)
	{
		// ein Material konstruieren, dass wie das übergebene ist, aber bei dem der Filename angepasst wurde
		// -> das Temporäre Material muss erzeugt werden, da das Original nicht geändert werden darf (gehört zu D3DX)
		//    der MeshContainer möchte seine Daten aber selbst Allozieren
		D3DXMATERIAL xMaterialTmp;
		xMaterialTmp.MatD3D = pMaterials[iMaterial].MatD3D;
		xMaterialTmp.pTextureFilename = NULL;

		if (pMaterials[iMaterial].pTextureFilename)
		{
			sprintf(acFilename, "%s%s", "xfl-texture>", 
				CFileLocator::ExtractFilename(pMaterials[iMaterial].pTextureFilename).c_str());
			xMaterialTmp.pTextureFilename = acFilename;
		}

		pMeshContainer->GetD3DXMaterials()[iMaterial] = xMaterialTmp;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphAllocator::SetMeshContainerEffectInstances(CD3DXMeshContainer* pMeshContainer, const _D3DXEFFECTINSTANCE* pEffectInstances) const
{
	for (int iMaterial = 0; iMaterial < (int)pMeshContainer->GetNumMaterials(); iMaterial++)
	{
		CD3DXEffectInstance& rxEffectInstanceDest = pMeshContainer->GetD3DXEffectInstances()[iMaterial];

		if (pEffectInstances &&
			pEffectInstances[iMaterial].pEffectFilename)
		{
			// übergebene EffectInstance verwenden
			rxEffectInstanceDest = *(pEffectInstances + iMaterial);

			// EffecInstance muss um virtuelle Texturpfade erweitert werden
			rxEffectInstanceDest.AddFilenamePrefixes("xfl-shader>", "xfl-texture>");
		}
		else
		if (m_pxMaterialConverter)
		{
			CD3DXMaterial xMaterial = pMeshContainer->GetD3DXMaterials()[iMaterial];
			xMaterial.AddFilenamePrefixes("xfl-texture>");

			// EffectInstance aus dem Material erzeugen
			m_pxMaterialConverter->ConvertMaterialToEffectInstance(xMaterial, &rxEffectInstanceDest);

			rxEffectInstanceDest.AddFilenamePrefixes("xfl-shader>", NULL);
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
CSceneGraphAllocator::CreateMeshContainer(LPCTSTR pcName, 
	const D3DXMESHDATA* pMeshData, const D3DXMATERIAL* pMaterials, 
	const _D3DXEFFECTINSTANCE* pEffectInstances, DWORD dwNumMaterials, 
	const DWORD *pAdjacency, ID3DXSkinInfo* pSkinInfo, _D3DXMESHCONTAINER* *ppNewMeshContainer) 
{
	assert(pMeshData->Type == D3DXMESHTYPE_MESH);

#ifdef _DEBUG
	D3DVERTEXELEMENT9 axDeclaration[MAX_FVF_DECL_SIZE];
	pMeshData->pMesh->GetDeclaration(axDeclaration);
	int iElementIdx = 0;
	bool bStreamContainesPosition = false;
	while (axDeclaration[iElementIdx].Stream != 0xff)
	{
		if (axDeclaration[iElementIdx].Usage == D3DDECLUSAGE_POSITION) bStreamContainesPosition = true;
		iElementIdx++;
	}
	assert(bStreamContainesPosition);
#endif // _DEBUG


	CD3DXMeshContainer* pMeshContainer = CD3DXMeshContainer::Create();

	pMeshContainer->SetName(pcName);

	pMeshContainer->SetMeshType(pMeshData->Type);
	pMeshContainer->SetMesh(m_pxEngineController->GetMeshFactory()->RegisterMesh(pMeshData->pMesh));

	pMeshContainer->SkinInfo() = pSkinInfo;


	pMeshContainer->bIsVisible = true;


	pMeshContainer->SetNumMaterials(dwNumMaterials);

	SetMeshContainerMaterials(pMeshContainer, pMaterials);
	SetMeshContainerEffectInstances(pMeshContainer, pEffectInstances);


	if (pAdjacency)
	{
		pMeshContainer->CreateAdjacencyArray();
		memcpy(pMeshContainer->Adjacency(), pAdjacency, sizeof(DWORD) * pMeshData->pMesh->GetNumFaces() * 3);
	}

	// Ende
	*ppNewMeshContainer = &pMeshContainer->D3DXMeshContainer();

	return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
CSceneGraphAllocator::DestroyFrame(_D3DXFRAME* pFrameToFree) 
{
	CD3DXFrame::Destroy((CD3DXFrame*)pFrameToFree);
	return S_OK; 
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
CSceneGraphAllocator::DestroyMeshContainer(_D3DXMESHCONTAINER* pMeshContainerBase)
{
	CD3DXMeshContainer::Destroy((CD3DXMeshContainer*)pMeshContainerBase);
	return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
CSceneGraphAllocator::CloneFrame(_D3DXFRAME* pFrameToClone, _D3DXFRAME* *ppNewFrame)
{
	assert(false);  // TODO: CloneFrame implementieren
	return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
HRESULT 
CSceneGraphAllocator::CloneMeshContainer(_D3DXMESHCONTAINER* pMeshContainerToClone, _D3DXMESHCONTAINER* *ppNewMeshContainer)
{
	assert(false);  // TODO: CloneMeshContainer implementieren
	return S_OK;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphAllocator::SetOptions(CMeshLoaderOptions* pxOptions)
{
	m_pxOptions = pxOptions;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CSceneGraphAllocator::SetMaterialConverter(CMaterialConverter* pxMaterialConverter)
{
	m_pxMaterialConverter = pxMaterialConverter;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

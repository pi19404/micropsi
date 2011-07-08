#include "stdafx.h"

#include "e42/core/XFileLoader.h"

#include "baselib/filelocator.h"

#include "e42/core/MaterialConverter.h"
#include "e42/core/SceneGraphAllocator.h"
#include "e42/core/EngineController.h"
#include "e42/core/MeshLoaderOptions.h"
#include "e42/core/MeshFactory.h"
#include "e42/core/D3DXMaterial.h"

using std::string;
//-----------------------------------------------------------------------------------------------------------------------------------------
CXFileLoader::CXFileLoader(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController)
{
    m_pxMaterialConverter = new CMaterialConverter(m_pxEngineController);

    m_pxFrameRoot = NULL;
    m_spxAnimationController = NULL;
    m_spxDefaultAnimationSet = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CXFileLoader::~CXFileLoader()
{
    assert(m_pxFrameRoot == NULL);
    assert(m_spxAnimationController == NULL);
    assert(m_spxDefaultAnimationSet == NULL);

    delete m_pxMaterialConverter;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CXFileLoader::BeginLoad(const string& sXFile, CSceneGraphAllocator* pxAllocator, CMeshLoaderOptions* pxOptions)
{
    assert(m_pxFrameRoot == NULL);
    assert(m_spxAnimationController == NULL);
    assert(m_spxDefaultAnimationSet == NULL);

    pxAllocator->SetOptions(pxOptions);
    pxAllocator->SetMaterialConverter(m_pxMaterialConverter);

    _D3DXFRAME* pxFrameRoot;

    string sFullName = m_pxEngineController->GetFileLocator()->GetPath(sXFile);

    HRESULT hr = D3DXLoadMeshHierarchyFromX(
        sFullName.c_str(), D3DXMESH_SYSTEMMEM | D3DXMESH_DYNAMIC, m_pxEngineController->GetDevice(), 
        pxAllocator, NULL, &pxFrameRoot, &m_spxAnimationController);

    m_pxFrameRoot = (CD3DXFrame*)pxFrameRoot;


    if (FAILED(hr) || !m_pxFrameRoot)
    {
        assert(false);
        MessageBox(NULL, sXFile.c_str(), "failed to load mesh from x-file", MB_ICONERROR | MB_OK);
        return false;
    }


    if (m_spxAnimationController)
    {
        CComObjectPtr<ID3DXAnimationController> spxAnimationControllerTmp = m_spxAnimationController;
        m_spxAnimationController = NULL;

        hr = spxAnimationControllerTmp->CloneAnimationController(
                max(1,  spxAnimationControllerTmp->GetMaxNumAnimationOutputs()),
                max(64, spxAnimationControllerTmp->GetMaxNumAnimationSets()), 
                max(1,  spxAnimationControllerTmp->GetMaxNumTracks()), 
                max(1,  spxAnimationControllerTmp->GetMaxNumEvents()), 
                &m_spxAnimationController);
        assert(SUCCEEDED(hr));
    }


    if ((m_spxAnimationController) &&
        (m_spxAnimationController->GetNumAnimationSets() > 0))
    {
        m_spxAnimationController->GetAnimationSet(0, &m_spxDefaultAnimationSet);

        if (m_spxAnimationController->GetNumAnimationSets() != 1)
        {
            DebugPrint("warn: x-files containing multiple animations are not supported! %s\n", sXFile.c_str());
            assert(false);
        }
    }

    return true;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXFrame*
CXFileLoader::GetFrameRoot() const
{
    assert(m_pxFrameRoot);
    return m_pxFrameRoot;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CComObjectPtr<ID3DXAnimationController>
CXFileLoader::GetAnimationController() const
{
    return m_spxAnimationController;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CComObjectPtr<ID3DXAnimationSet>
CXFileLoader::GetDefaultAnimationSet() const
{
    return m_spxDefaultAnimationSet;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CXFileLoader::EndLoad()
{
    assert(m_pxFrameRoot);
    m_pxFrameRoot = NULL;
    m_spxAnimationController = NULL;
    m_spxDefaultAnimationSet = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CComObjectPtr<ID3DXAnimationSet>
CXFileLoader::LoadAnimation(const string& sXFile)
{
    _D3DXFRAME*                             pxFrameRoot = 0;
    CComObjectPtr<ID3DXAnimationController> spxAnimationController;
    CComObjectPtr<ID3DXAnimationSet>        spxAnimationSet;


    CSceneGraphAllocator xAllocator(m_pxEngineController);

	CMeshLoaderOptions xMeshLoaderOptions;
	xMeshLoaderOptions.m_bLoadResources = false;
	xMeshLoaderOptions.m_bOptimizeMesh = false;
	xMeshLoaderOptions.m_dwMeshOptions = D3DXMESH_SYSTEMMEM | D3DXMESH_DYNAMIC;

    xAllocator.SetOptions(&xMeshLoaderOptions);

    string sFullName = m_pxEngineController->GetFileLocator()->GetPath(sXFile);

    HRESULT hr = D3DXLoadMeshHierarchyFromX(
        sFullName.c_str(), D3DXMESH_SYSTEMMEM | D3DXMESH_DYNAMIC, m_pxEngineController->GetDevice(), 
        &xAllocator, NULL, &pxFrameRoot, &spxAnimationController);
    assert(SUCCEEDED(hr));


    if (FAILED(hr) || !spxAnimationController)
    {
        MessageBox(NULL, sXFile.c_str(), "failed to load anim from x-file", MB_ICONERROR | MB_OK);
    }


    D3DXFrameDestroy(pxFrameRoot, &xAllocator);


    if (spxAnimationController->GetNumAnimationSets() == 0)
    {
        DebugPrint("warn: x-file doesn't contain an animation! %s\n", sXFile.c_str());
        assert(false);
    }
    if (spxAnimationController->GetNumAnimationSets() > 1)
    {
        DebugPrint("warn: x-file containing multiple animations are not supported! %s\n", sXFile.c_str());
        assert(false);
    }


    hr = spxAnimationController->GetAnimationSet(0, &spxAnimationSet);
    assert(SUCCEEDED(hr));


    return spxAnimationSet;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMeshContainer*
CXFileLoader::LoadSingleMesh(const char* pcFilename, DWORD dwMeshOptions)
{
    CD3DXMeshContainer* pxMeshContainer = CD3DXMeshContainer::Create();


	CComObjectPtr<ID3DXBuffer> spxMaterialBuffer = NULL;
    CComObjectPtr<ID3DXBuffer> spxEffectInstanceBuffer = NULL;
    DWORD dwNumMaterials = 0;


    m_pxEngineController->GetMeshFactory()->CreateMeshFromFile(
        pcFilename, dwMeshOptions, &spxMaterialBuffer, &spxEffectInstanceBuffer, &dwNumMaterials);


	// Materials/EffectInstances setzen
    const CD3DXMaterial* pxD3DXMaterials = (CD3DXMaterial*)spxMaterialBuffer->GetBufferPointer();
    const CD3DXEffectInstance* pxD3DXEffectInstancesSrc = (CD3DXEffectInstance*)spxEffectInstanceBuffer->GetBufferPointer();

	CD3DXEffectInstance* pxD3DXEffectInstancesDst = new CD3DXEffectInstance[dwNumMaterials];

    for (int iMaterial = 0; iMaterial < (int)dwNumMaterials; iMaterial++)
    {
		if (pxD3DXEffectInstancesSrc[iMaterial].GetEffectFilename() == NULL)
		{
			m_pxMaterialConverter->ConvertMaterialToEffectInstance(pxD3DXMaterials[iMaterial], &pxD3DXEffectInstancesDst[iMaterial]);
		}
		else
		{
			pxD3DXEffectInstancesDst[iMaterial] = pxD3DXEffectInstancesSrc[iMaterial];
		}

		pxD3DXEffectInstancesDst[iMaterial].AddFilenamePrefixes("xfl-texture>", "xfl-shader>");
	}
	

	pxMeshContainer->SetD3DXMaterials(dwNumMaterials, pxD3DXMaterials);
	pxMeshContainer->SetD3DXEffectInstances(dwNumMaterials, pxD3DXEffectInstancesDst);

	delete [] pxD3DXEffectInstancesDst;

    pxMeshContainer->SetMaterials(dwNumMaterials);



	// CMaterials createn
    for (int iMaterial = 0; iMaterial < (int)dwNumMaterials; iMaterial++)
    {
		CMaterial* pxMaterial = pxMeshContainer->GetMaterials() + iMaterial;

		pxMaterial->Init(pxD3DXEffectInstancesDst[iMaterial].GetEffectFilename(), m_pxEngineController);

		pxMaterial->AddParameters(pxD3DXEffectInstancesDst[iMaterial].GetDefaults(), pxD3DXEffectInstancesDst[iMaterial].GetNumDefaults());
    }

	
	// TODO: MeshOptimierung für SingleMeshes müsste irgendwo eingebaut werden (aber nicht hier!!)


    return pxMeshContainer;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CXFileLoader::LoadFXMapping(const std::string& sXMLFile)
{
    m_pxMaterialConverter->LoadMapping(sXMLFile);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CXFileLoader::SetDefaultEffect(const CD3DXEffectInstance& rxEffectInstance)
{
    m_pxMaterialConverter->SetDefaultEffect(rxEffectInstance);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

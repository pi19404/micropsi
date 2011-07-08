#include "stdafx.h"

#include "e42/core/ModelFactory.h"

#include "e42/core/Model.h"

#include "e42/core/MeshLoaderOptions.h"
#include "e42/core/EngineController.h"
#include "e42/core/XFileLoader.h"
#include "e42/core/AnimationFactory.h"

#include "e42/core/SceneGraphAllocator.h"
#include "e42/core/SceneGraphInitializer.h"
#include "e42/core/SceneGraphOptimizer.h"

#include "baselib/FileLocator.h"

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CModelFactory::CModelFactory(CEngineController* pxEngineController)
:	m_pxEngineController(pxEngineController)
{
	m_pxSceneGraphAllocator = new CSceneGraphAllocator(m_pxEngineController);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CModelFactory::~CModelFactory()
{
	__super::ReleaseOwnResources();
	delete m_pxSceneGraphAllocator;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CModelFactory::DestroyResource(void* pxResource)
{
	CD3DXFrame* pxRootFrame = NULL;
	CComObjectPtr<ID3DXAnimationController> spxAnimationController;

	((CModel*)pxResource)->Shut(pxRootFrame, spxAnimationController);

	if (pxRootFrame)
		D3DXFrameDestroy(&pxRootFrame->D3DXFrame(), m_pxSceneGraphAllocator);

	spxAnimationController = NULL;

	delete (CModel*)pxResource;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CResourceProxy*
CModelFactory::CloneResourceProxy(CResourceProxy* pxResourceProxy)
{
	CModel* pxPrototypeModel = (CModel*)pxResourceProxy->GetResource();

	CModel* pxClonedModel = new CModel();
		
	pxClonedModel->Init(pxPrototypeModel);

	return __super::AddResource("", pxClonedModel);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CModelFactory::InitModelFromX(CModel* pModel, const string& rsFilename, CMeshLoaderOptions* pxOptions)
{
	CXFileLoader* pxXFileLoader =
		m_pxEngineController->GetXFileLoader();

	if (pxXFileLoader->BeginLoad(rsFilename, m_pxSceneGraphAllocator, pxOptions))
	{
		CD3DXFrame* pxFrameRoot = 
			pxXFileLoader->GetFrameRoot();

		CComObjectPtr<ID3DXAnimationController> spxAnimationController = 
			pxXFileLoader->GetAnimationController();

		CComObjectPtr<ID3DXAnimationSet> spxDefaultAnimationSet = 
			pxXFileLoader->GetDefaultAnimationSet();

		pxXFileLoader->EndLoad();


		CSceneGraphInitializer::InitializeFrameHierarchy(pxFrameRoot, m_pxEngineController, pxOptions);

		if (pxOptions->m_bOptimizeMesh)
			CSceneGraphOptimizer::OptimizeFrameHierarchy(&pxFrameRoot, m_pxEngineController, pxOptions);
	    

		TAnimationHandle hndDefaultAnimationSet = 
			m_pxEngineController->GetAnimationFactory()->RegisterAnimation(spxDefaultAnimationSet, rsFilename);
	    
		pModel->Init(pxFrameRoot, spxAnimationController, hndDefaultAnimationSet);

		pModel->SetName(rsFilename);

		return true;
	}
	else
	{
		assert(false);
		return false;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TModelHandle 
CModelFactory::CreateModelFromFile(const string& sFilename, CMeshLoaderOptions* pxOptions, bool bCloneModel)
{
	if (pxOptions == 0)
		pxOptions = &m_xDefaultOptions;

	string sName = 
		m_pxEngineController->GetFileLocator()->GetPath(sFilename) + 
		pxOptions->GetIDString();

	CResourceProxy* pxResourceProxy = __super::LookUpResource(sName);

	if (pxResourceProxy == NULL)
	{
		CModel* pModel = 
			new CModel(m_pxEngineController);

		if (InitModelFromX(pModel, sFilename, pxOptions))
		{
			pxResourceProxy = __super::AddResource(sName, pModel, false);
		}
		else
		{
			delete pModel;
			return TModelHandle(0);
		}
	}


	if (bCloneModel)
	{
		pxResourceProxy = CloneResourceProxy(pxResourceProxy);
	}


	return TModelHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

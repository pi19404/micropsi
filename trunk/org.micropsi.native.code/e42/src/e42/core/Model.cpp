#include "stdafx.h"

#include "e42/core/Model.h"

#include "e42/core/XFileLoader.h"
#include "e42/core/D3DXFrame.h"
#include "e42/core/D3DXMeshContainer.h"
#include "e42/core/SceneGraphRenderer.h"
#include "e42/core/MeshRenderer.h"
#include "e42/core/AnimationFactory.h"

#include "e42/core/EngineController.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/EngineStats.h"
#include "e42/core/SceneGraphIterator.h"

using std::string;
using std::map;
//-----------------------------------------------------------------------------------------------------------------------------------------
CModel::CModel(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController)
{
	if (m_pxEngineController == NULL)
	{
		m_pxEngineController = &CEngineController::Get();
	}

	m_pxFrameRoot = NULL;
	m_spxAnimationController = NULL;

	m_fAnimationSpeedFactor = 0.8f;
	m_bCombinedFrameMatrizesInvalid = true;
	m_bFrameCullingEnabled = true;
	m_bInterpolateAnims = true;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CModel::~CModel()
{
	assert(m_mAnimations.empty());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CModel::Init(
	CD3DXFrame* pxFrameRoot, 
	CComObjectPtr<ID3DXAnimationController> spxAnimationController, 
	TAnimationHandle hndDefaultAnimation)
{
	assert(m_pxFrameRoot == NULL);
	assert(pxFrameRoot);
	assert(pxFrameRoot->GetSibling() == NULL);


	m_pxFrameRoot = pxFrameRoot;
	m_spxAnimationController = spxAnimationController;

	if (hndDefaultAnimation)
		m_mAnimations[".default"] = hndDefaultAnimation;


	m_xBoundingSphere.m_vCenter = m_pxFrameRoot->xBoundingSphere.m_vCenter ^ m_pxFrameRoot->TransformationMatrix();
	m_xBoundingSphere.m_fRadius = m_pxFrameRoot->xBoundingSphere.m_fRadius;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CModel::Shut(CD3DXFrame*& rpxFrameRoot, CComObjectPtr<ID3DXAnimationController>& rspxAnimationController)
{
	DeleteAnimations();

	rspxAnimationController = m_spxAnimationController;
	m_spxAnimationController = NULL;

	rpxFrameRoot = m_pxFrameRoot;
	m_pxFrameRoot = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CModel::SetAnimation(const std::string& sAnimationName, float fTime)
{
	TAnimationHandle hndAnimationSet = GetAnimation(sAnimationName);
	assert(hndAnimationSet);

	if (hndAnimationSet)
	{
		HRESULT hr;
		hr = m_spxAnimationController->SetTrackAnimationSet(0, hndAnimationSet.GetPtr());
		assert(SUCCEEDED(hr));

		float fAnimTime = fTime * m_fAnimationSpeedFactor;
		if (!m_bInterpolateAnims)
		{
			fAnimTime -= fmodf(fAnimTime, 1 / 30.0f);
		}

		hr = m_spxAnimationController->SetTrackPosition(0, fAnimTime);
		assert(SUCCEEDED(hr));

		hr = m_spxAnimationController->AdvanceTime(0, NULL);
		assert(SUCCEEDED(hr));
	}
	else
	{
		char acErrorMsg[1024];
		sprintf(acErrorMsg, "model: %s\nanim: %s", m_sName.c_str(), sAnimationName.c_str());
		MessageBox(NULL, acErrorMsg, "anim not declared:" , MB_ICONERROR | MB_OK);
		return;
	}

	m_bCombinedFrameMatrizesInvalid = true; // FIXME: nur wenn Animation / Zeit geändert wurde
}
//-----------------------------------------------------------------------------------------------------------------------------------------
float 
CModel::GetAnimationLength(const std::string& sAnimationName) const
{
	TAnimationHandle hndAnimationSet = GetAnimation(sAnimationName);

	if (!hndAnimationSet)
	{
		assert(false);
		char acErrorMsg[1024];
		sprintf(acErrorMsg, "model: %s\nanim: %s", m_sName.c_str(), sAnimationName.c_str());
		MessageBox(NULL, acErrorMsg, "anim not declared:" , MB_ICONERROR | MB_OK);
		return 0;
	}

	return (float)hndAnimationSet->GetPeriod() / m_fAnimationSpeedFactor;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CModel::Render(TRenderContextPtr spxRenderContext, const CMat4S& matWorldTransform)
{
	assert(spxRenderContext);
	assert((spxRenderContext->m_pxEngineController == 0) ||
		(spxRenderContext->m_pxEngineController == m_pxEngineController));


	// EngineController setzen
	if (spxRenderContext->m_pxEngineController == NULL)
		spxRenderContext->m_pxEngineController = m_pxEngineController;


	if (m_bCombinedFrameMatrizesInvalid)
	{
		UpdateCombinedFrameMatrizes();
	}


	CSceneGraphRenderer::RenderFrameHierarchy(
		m_pxFrameRoot, spxRenderContext, 
		matWorldTransform, (m_spxAnimationController == NULL) && m_bFrameCullingEnabled);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CModel::DeleteAnimations()
{
	m_mAnimations.clear();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TAnimationHandle
CModel::GetAnimation(const string& sAnimationName) const
{
	if (m_mAnimations.empty() || sAnimationName.empty()) 
	{
		return TAnimationHandle(NULL);
	}

	const map<const string, TAnimationHandle>::const_iterator iter = m_mAnimations.find(sAnimationName);

	if (iter != m_mAnimations.end())
	{
		assert(iter->first == sAnimationName);
		return iter->second;
	}

	return TAnimationHandle(NULL);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CModel::IsAnimationRegistered(TAnimationHandle hndAnimation) const
{
	map<const string, TAnimationHandle>::const_iterator iter = m_mAnimations.begin(); 

	for (unsigned int i = 0; i < m_mAnimations.size(); i++)
	{
		if (iter->second == hndAnimation)
		{
			return true;
		}
		iter++;
	}

	return false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CModel::AddAnimation(const string& sAnimationFile, const string& sAnimationName)
{
	if (GetAnimation(sAnimationName).GetPtr() == NULL)
	{
		TAnimationHandle hndAnimationSet = 
			m_pxEngineController->GetAnimationFactory()->CreateAnimationFromFile(sAnimationFile);

		if (!IsAnimationRegistered(hndAnimationSet))
		{
			HRESULT hr = m_spxAnimationController->RegisterAnimationSet(hndAnimationSet.GetPtr());
			assert(SUCCEEDED(hr));
		}

		m_mAnimations[sAnimationName] = hndAnimationSet;
		assert(hndAnimationSet == GetAnimation(sAnimationName));
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const CBoundingSphere&
CModel::GetBoundingSphere() const
{
	return m_xBoundingSphere;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CModel::GetFrameTransform(CMat4S* pmOutTransform, const CMat4S& mModelTransform, const char* pcFrameName)
{
	CD3DXFrame* pFrame = (CD3DXFrame*)D3DXFrameFind(&m_pxFrameRoot->D3DXFrame(), pcFrameName);
	if (!pFrame)
	{
		return false;
	}

	if (m_bCombinedFrameMatrizesInvalid)
	{
		UpdateCombinedFrameMatrizes();
	}

	*pmOutTransform = pFrame->matCombinedTransformation * mModelTransform;

	return true;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXFrame*
CModel::GetFrameByName(const char* pcFrameName) const
{
	return (CD3DXFrame*)D3DXFrameFind(&m_pxFrameRoot->D3DXFrame(), pcFrameName);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool 
CModel::FrameIsKeyedInAnimation(const string& sAnimationName, const char* pcFrameName)
{
	TAnimationHandle hndAnimationSet = GetAnimation(sAnimationName);
	if (!hndAnimationSet) return false;

	UINT iIdx = 0;
	HRESULT hr = hndAnimationSet->GetAnimationIndexByName(pcFrameName, &iIdx);

	return (SUCCEEDED(hr));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CModel::SetAnimationSpeedFactor(float fFactor)
{
	m_fAnimationSpeedFactor = fFactor;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CModel::UpdateCombinedFrameMatrizes()
{
	CSceneGraphRenderer::UpdateCombinedFrameMatrices(m_pxFrameRoot);
	m_bCombinedFrameMatrizesInvalid = false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CModel::InvalidateCombinedFrameMatrizes()
{
	m_bCombinedFrameMatrizesInvalid = true;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CModel::SetFrameCullingTest(bool bEnable)
{
	m_bFrameCullingEnabled = bEnable;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CModel::SetInterpolateAnims(bool bInterpolateAnims)
{
	m_bInterpolateAnims = bInterpolateAnims; 
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CModel::HasAnimations() const
{
	return m_spxAnimationController != 0;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXFrame* 
CModel::GetRootFrame() const
{
	return m_pxFrameRoot;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CModel::SetupAnimationOutputs()		// CreateAnimationController?
{
	CSceneGraphIterator xSceneGraphIterator(m_pxFrameRoot);

	CD3DXFrame* pxFrame;
	while (pxFrame = xSceneGraphIterator.GetNextFrame())
	{
		if (pxFrame->GetName())
		{
			m_spxAnimationController->RegisterAnimationOutput(pxFrame->GetName(), (D3DXMATRIX*)&pxFrame->TransformationMatrix(), NULL, NULL, NULL);
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CModel::Init(CModel* pxPrototype)
{
	// Framehierarchie klonen
	m_pxFrameRoot = pxPrototype->m_pxFrameRoot->CloneFrameHierarchy();


	// Bonematrixpointer bei Skinning aktualisieren
	CSceneGraphIterator xSceneGraphIterator(m_pxFrameRoot);

	CD3DXMeshContainer* pxMeshContainer;
	while (pxMeshContainer = xSceneGraphIterator.GetNextMeshContainer())
	{
		pxMeshContainer->SetupBoneMatrixPointers(m_pxFrameRoot);
	}


	if (pxPrototype->m_spxAnimationController)
	{
		pxPrototype->m_spxAnimationController->CloneAnimationController(
			pxPrototype->m_spxAnimationController->GetMaxNumAnimationOutputs(),
			pxPrototype->m_spxAnimationController->GetMaxNumAnimationSets(),
			pxPrototype->m_spxAnimationController->GetMaxNumTracks(),
			pxPrototype->m_spxAnimationController->GetMaxNumEvents(),
			&m_spxAnimationController);


		SetupAnimationOutputs();

		m_mAnimations = pxPrototype->m_mAnimations;
	}


	m_xBoundingSphere = pxPrototype->m_xBoundingSphere;

	m_bFrameCullingEnabled = pxPrototype->m_bFrameCullingEnabled;
	m_bInterpolateAnims = pxPrototype->m_bInterpolateAnims;

	m_bCombinedFrameMatrizesInvalid = pxPrototype->m_bCombinedFrameMatrizesInvalid;
	m_fAnimationSpeedFactor = pxPrototype->m_fAnimationSpeedFactor;

	m_sName = pxPrototype->m_sName + "_clone";
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CModel::SetName(const std::string& sName)
{
	m_sName = sName;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const std::string& 
CModel::GetName() const
{
	return m_sName;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

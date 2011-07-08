/*******************************************************************************
Model.h - Klasse CModel kapselt ein DirectX-Model, das aus einem X-File
	geladen werden kann. Modellen können nach dem Laden Animationen hinzugefügt 
	werden, die ebenfalls aus X-Files geladen werden.
*******************************************************************************/
#pragma once

#ifndef MODEL_H_INCLUDED
#define MODEL_H_INCLUDED

#include "e42/stdinc.h"

#include "baselib/dynarray.h"
#include "baselib/comobjectptr.h"

#include <map>
#include <string>
#include <d3dx9math.h>

#include "baselib/geometry/Matrix.h"
#include "baselib/geometry/CVector.h"
#include "baselib/geometry/BoundingSphere.h"
#include "e42/core/RenderContext.h"
#include "e42/core/ResourceHandles.h"

class CCollisionGeometry;
class CEngineController;

class CD3DXFrame;

//-----------------------------------------------------------------------------------------------------------------------------------------
class CModel
{
	friend class CModelFactory;
	friend class CXFileLoader;

private:
	CEngineController*  m_pxEngineController;


	CModel(CEngineController* pxEngineController = NULL);
	~CModel();

	void	Init(CD3DXFrame* pxFrameRoot, 
				CComObjectPtr<ID3DXAnimationController> spxAnimationController, 
				TAnimationHandle hndDefaultAnimation);

	void	Init(CModel* pxPrototype);

	void	Shut(CD3DXFrame*& rpxFrameRoot, 
				CComObjectPtr<ID3DXAnimationController>& rspxAnimationController);


	CD3DXFrame*										m_pxFrameRoot;					///< Framehierarchie
	CComObjectPtr<ID3DXAnimationController>			m_spxAnimationController;
	std::map<const std::string, TAnimationHandle>	m_mAnimations;


	CBoundingSphere		m_xBoundingSphere;

	bool				m_bFrameCullingEnabled;
	bool				m_bInterpolateAnims;

	bool				m_bCombinedFrameMatrizesInvalid;
	float				m_fAnimationSpeedFactor;

	std::string			m_sName;


	void				DeleteAnimations();
	TAnimationHandle	GetAnimation(const std::string& sAnimationName) const;
	bool				IsAnimationRegistered(TAnimationHandle hndAnimation) const;

	void				SetupAnimationOutputs();


public:

	void Render(TRenderContextPtr spxRenderContext, const CMat4S& matWorldTransform);


	void AddAnimation(const std::string& pcAnimationFile, const std::string& pcAnimationName);    // muss auch damit klarkommen, wenn Animation schon geaddet ist
	void SetAnimation(const std::string& sAnimationName, float fTime);
	float GetAnimationLength(const std::string& sAnimationName) const;

	bool HasAnimations() const;
	void SetAnimationSpeedFactor(float fFactor);
	void SetInterpolateAnims(bool bInterpolateAnims);


	CD3DXFrame* GetRootFrame() const;
	CD3DXFrame* GetFrameByName(const char* pcFrameName) const;
	bool GetFrameTransform(CMat4S* pmOutTransform, const CMat4S& mModelTransform, const char* pcFrameName);
	bool FrameIsKeyedInAnimation(const std::string& sAnimationName, const char* pcFrameName);


	void InvalidateCombinedFrameMatrizes();
	void UpdateCombinedFrameMatrizes();


	void SetFrameCullingTest(bool bEnable);

	const CBoundingSphere& GetBoundingSphere() const;       // Achtung: ist die Boundingsphere zum Zeitpunkt des Objektladens (im Modelspace versteht sich)


	void SetName(const std::string& sName);					///< fürs debugging
	const std::string& GetName() const;						///< fürs debugging
};
//-----------------------------------------------------------------------------------------------------------------------------------------

#endif // MODEL_H_INCLUDED

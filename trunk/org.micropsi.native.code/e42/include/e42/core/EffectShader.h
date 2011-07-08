#pragma once

#ifndef E42_EFFECTSHADER_H_INCLUDED
#define E42_EFFECTSHADER_H_INCLUDED

#include "e42/stdinc.h"
#include <d3dx9effect.h>
#include <string>

#include "baselib/geometry/matrix.h"
#include "baselib/geometry/cvector.h"
#include "e42/core/resourcehandles.h"
#include "e42/core/D3DXEffectInstance.h"


/*
	CEffectShader - kapselt ID3DXEffect-Interface und dient dazu Handles auf häufig verwendete 
		Effect-Paramter zu cachen. Kann durch Ableitung erweitert werden; EffectShader werden
		von der EffectFactory erzeugt. Um dafür zu sorgen, dass die EffectFactory Instanzen 
		abgeleiteter Klassen erzeugt stehen Funktionen bereit, mit denen sich Create- und 
		Destroy- Funktionen für Wrapper überschreiben lassen.
*/

class CEffectShader
{
private:

	CEffectShader(ID3DXEffect* pd3dEffect);
	~CEffectShader();

	void AddParameters(D3DXHANDLE hParentParam, std::string sParentParam);
	void SaveInitialParameterValues();


	ID3DXEffect* m_pd3dEffect;

	bool					m_bExecuteRestorePassOnEnd;
	UINT					m_uiRestorePassIdx;

	CD3DXEffectInstance		m_xInitialParameterValues;		///< Werte der Effektparameter, die der Effekt direkt nach Laden hatte


public:

	// shortcuts auf Standard-Parameter
	const D3DXHANDLE  m_hndNumBones;

	const D3DXHANDLE  m_hndWorld;
	const D3DXHANDLE  m_hndWorldInverse;
	const D3DXHANDLE  m_hndView;
	const D3DXHANDLE  m_hndProjection;
	const D3DXHANDLE  m_hndWorldView;
	const D3DXHANDLE  m_hndViewProjection;
	const D3DXHANDLE  m_hndWorldViewProjection;
	const D3DXHANDLE  m_hndViewInverse;

	const D3DXHANDLE  m_hndWorldMatrixArray;

	const D3DXHANDLE  m_hndMaterialDiffuse;
	const D3DXHANDLE  m_hndMaterialAmbient;
	const D3DXHANDLE  m_hndLightDir;

	const D3DXHANDLE  m_hndEyePosition;

	const D3DXHANDLE  m_hndDiffuseMap;
	const D3DXHANDLE  m_hndSpecularMap;
	const D3DXHANDLE  m_hndBumpMap;
	const D3DXHANDLE  m_hndNormalMap;
	const D3DXHANDLE  m_hndDetailMap;
	const D3DXHANDLE  m_hndEnvironmentMap;
	const D3DXHANDLE  m_hndLightMap;
	const D3DXHANDLE  m_hndShadowMap;

	const D3DXHANDLE  m_hndTime;

	const D3DXHANDLE  m_hndDefaultTechnique_StaticMesh;
	const D3DXHANDLE  m_hndDefaultTechnique_SkinnedMesh;


	static CEffectShader* Create(ID3DXEffect* pd3dEffect);
	static void Destroy(CEffectShader* pxEffectShader);


	ID3DXEffect* GetD3DXEffect() const;

	void ApplyEffectInstanceDefaults(D3DXEFFECTINSTANCE* pInstance);

	void SetNumBones(const int i) const;

	void SetWorldMatrix(const CMat4S& rM) const;
	void SetWorldInverseMatrix(const CMat4S& rM) const;
	void SetViewMatrix(const CMat4S& rM) const;
	void SetProjectionMatrix(const CMat4S& rM) const;
	void SetWorldViewMatrix(const CMat4S& rM) const;
	void SetViewProjectionMatrix(const CMat4S& rM) const;
	void SetWorldViewProjectionMatrix(const CMat4S& rM) const;
	void SetViewInverseMatrix(const CMat4S& rM) const;
	void SetWorldMatrixArray(const CMat4S* pM, const int iCount) const;

	void SetMaterialDiffuseVector(const CVec4& rV) const;
	void SetMaterialAmbientVector(const CVec4& rV) const;
	void SetLightDirVector(const CVec4& rV) const;

	void SetEyePosition(const CVec4& rV) const;

	void SetDiffuseMap(const TTextureHandle& hndTexture) const;
	void SetSpecularMap(const TTextureHandle& hndTexture) const;
	void SetBumpMap(const TTextureHandle& hndTexture) const;
	void SetNormalMap(const TTextureHandle& hndTexture) const;
	void SetDetailMap(const TTextureHandle& hndTexture) const;
	void SetEnvironmentMap(const TTextureHandle& hndTexture) const;
	void SetLightMap(const TTextureHandle& hndTexture) const;
	void SetShadowMap(const TTextureHandle& hndTexture) const;


	void SetTime(const float f) const;

	void SetTechnique(const std::string& sName) const;
	void SetDefaultTechnique_StaticMesh() const;
	void SetDefaultTechnique_SkinnedMesh() const;

	void Begin(UINT* puiNumPasses, const DWORD dwFlags);
	void BeginPass(const int iPass) const;
	void EndPass() const;
	void End();

	bool IsRestorePass(int iPass, const char* pcTechnique = NULL) const;

	const CD3DXEffectInstance& GetInitialParameterValues() const;
};

#include "e42/core/EffectShader.inl"

#endif // EFFECTWRAPPER_H_INCLUDED
#pragma once

#ifndef SHADOWTEXTURE_H_INCLUDED
#define SHADOWTEXTURE_H_INCLUDED

#include "e42/stdinc.h"
#include "e42/core/ResourceHandles.h"
#include "e42/core/VertexBufferFactory.h"
#include "baselib/geometry/CVector.h"
#include "baselib/geometry/Matrix.h"
#include "baselib/color.h"

class CCamera;
class CEngineController;

class CShadowTexture
{
private:
	CEngineController*		m_pxEngineController;

	TVertexBufferHandle		m_hndVB;  // um die textur links oben anzuzeigen

	TTextureHandle			m_hndShadowTexture;
	TTextureHandle			m_hndShadowFadeTexture;

	TModelHandle			m_hndModel;
	CCamera*				m_pxCamera;
	CVec3					m_vLightDir;
	CColor					m_xShadowColor;
	float					m_fShadowFactor;

	CMat4S					m_matWorld2ShadowTransform;
	CVec3					m_vShadowBase;					// da wo der Schatten losgehen soll (Mittelpunkt des Objektes)
	float					m_fShadowFadeFactor;			// 1/länge des Schattens
	CVec3					m_vModelCenter;
	bool					m_bIsClear;

	bool					m_bSceneMultiSampling;

	void SetupCamera();
	void Render(const CMat4S& matModelTransform);

public:
	CShadowTexture(CEngineController* pxEngineController);
	~CShadowTexture();

	void Init(int iTextureSize, bool bSceneMultiSampling);
	void Shut();

	void SetModel(TModelHandle hndModel);
	void SetLightDir(const CVec3& rvDir);
	void SetShadowColor(const CColor& rxShadowColor);
	void SetShadowFactor(float fFactor);


	void UpdateShadow(const CMat4S& matModelTransform);
	void Clear();

	TTextureHandle GetShadowTexture() const;
	TTextureHandle GetShadowFadeTexture() const;
	const CMat4S& GetWorld2ShadowTransform() const;
	const CVec3& GetShadowBase() const;
	float GetShadowFadeFactor() const;


	void SetShadowFadeTexture(const TTextureHandle& hndFadeTexture);
	void SetShadowFadeDistance(float fFadeDistance);

	void SetModelCenter(const CVec3& vCenter);


	// TODO >
	void FadeIn();
	void FadeOut();

	void Enable();
	void Disable();
	// < TODO


	void RenderTexture();
};

#endif // SHADOWTEXTURE_H_INCLUDED
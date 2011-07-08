#pragma once

#ifndef PARTICLESOURCE_H_INCLUDED
#define PARTICLESOURCE_H_INCLUDED

#include <string>

#include "e42/stdinc.h"

#include "baselib/dynarray.h"
#include "e42/core/ResourceHandles.h"
#include "e42/core/RenderContext.h"

#include "baselib/geometry/Matrix.h"
#include "baselib/geometry/BoundingSphere.h"

class CCamera;
class CParticleSystem;

class CParticleSource
{
private:
	bool                    m_bInitialized;

protected:
	CParticleSystem*        m_pxParticleSystem;

	struct ParticleVertex
	{
		CVec3   vPos;
		DWORD   dwDiffuse;
		CVec2   vUV;
		float   fSize;
		float   fRotation;
	};

	enum { ParticleVertexFVF = D3DFVF_XYZ | D3DFVF_DIFFUSE | D3DFVF_TEX3 | D3DFVF_TEXCOORDSIZE2(0) | D3DFVF_TEXCOORDSIZE1(1) | D3DFVF_TEXCOORDSIZE1(2) };


	class CParticle
	{
	public:
		CVec3 m_vPos;
		CVec3 m_vDir;
		float m_fSize;
		float m_fRotation;
		float m_fOpacity;
		float m_fAge;
		int   m_iPhase;

		float m_fParam0;
	};

	CDynArray<CParticle, 8, false>    m_axParticles;

	float                   m_fRestTimeFromLastSimulation;

	TVertexBufferHandle       m_hndVertexBuffer;
	TIndexBufferHandle        m_hndIndexBuffer;
	TVertexDeclarationHandle  m_hndVertexDeclaration;
	TEffectHandle             m_hndEffect;
	TTextureHandle            m_hndTexture;

	DWORD	m_dwVertexFVF;


	virtual void AddNewParticles(float fDeltaTime);
	virtual void Simulate(float fDeltaTime);
	virtual void UpdateVertexBuffer();
	virtual void SetupShaderConstants(TRenderContextPtr spxRenderContext);
	virtual void Draw();

	virtual void InitParameters();
	virtual void Init();
	virtual void Shut();

	CMat4S  CalcTransform();

	CVec3                   m_vSourcePos;
	CMat3S                  m_mSourceRot;
	CVec3                   m_vSourceDir;
	float                   m_fParticleSizeFact;

	int                     m_iMaxParticles;
	std::string             m_sParticleTextureFile;
	std::string             m_sParticleEffectFile;
	int                     m_iTextureTilesX;
	int                     m_iTextureTilesY;
	float                   m_fNewParticlesPerSecond;

	bool                    m_bActive;
	bool                    m_bSimulateSynchronToGameplay;

public:

	CParticleSource(CParticleSystem* pParticleSystem);
	virtual ~CParticleSource();

	void SetSharedIndexBuffer(TIndexBufferHandle hndIndexBuffer, int iMaxIndices);

	virtual void Render(TRenderContextPtr spxRenderContext);
	virtual void DoGameplayStep(float fDeltaTime);
	virtual void GetBoundingSphere(CBoundingSphere* pxSphereOut) const;


	const CVec3& GetPos() const;
	void SetPos(const CVec3& vPos);

	const CMat3S& GetRot() const;
	void SetRot(const CMat3S& mRot);

	const CVec3& GetDir() const;
	void SetDir(const CVec3& vDir);

	void SetParticleSizeFact(float fParticleSizeFact);
	void SetNewParticlesPerSecond(float fNewParticlesPerSecond);

	void SetActive(bool bActive);
	void SetSimulateSynchronToGameplay(bool bSimulateSynchronToGameplay);

	virtual void Reset();
};

#endif // PARTICLESOURCE_H_INCLUDED
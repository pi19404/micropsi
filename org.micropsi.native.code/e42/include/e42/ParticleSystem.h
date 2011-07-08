#pragma once

#ifndef PARTICLESYSTEM_H_INCLUDED
#define PARTICLESYSTEM_H_INCLUDED

#include "e42/stdinc.h"

#include "baselib/dynarray.h"
#include "e42/Core/ResourceHandles.h"
#include "e42/Core/RenderContext.h"

class CCamera;
class CParticleSource;
class CEngineController;

class CParticleSystem
{
private:
	CDynArray<CParticleSource*>		m_apParticleSources;

	int								m_iMaxParticlesPerSource;
	CEngineController*				m_pxEngineController;
	TIndexBufferHandle				m_hndSharedIndexBuffer;

	void CreateSharedIndexBuffer();

public:
	enum
	{
		MAX_SHARED_IB_SIZE = 341 * 12,
	};

	CParticleSystem(CEngineController* pxEngineController = NULL);
	~CParticleSystem();

	CEngineController* GetEngineController() const;

	void AddSource(CParticleSource* pxParticleSource);
	void RemSource(CParticleSource* pxParticleSource);
	TIndexBufferHandle GetSharedIndexBuffer() const;

	void Render(TRenderContextPtr spxRenderContext);

	void DoGameplayStep(float fDeltaTime, const CCamera* pxCamera);
};

#endif // PARTICLESYSTEM_H_INCLUDED
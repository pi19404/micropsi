#include "stdafx.h"

#include "e42/ParticleSystem.h"
#include "e42/Camera.h"
#include "e42/ParticleSource.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/EngineController.h"
#include "e42/core/IndexBufferFactory.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CParticleSystem::CParticleSystem(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController)
{
	if (m_pxEngineController == NULL)
		m_pxEngineController = &CEngineController::Get();

    CreateSharedIndexBuffer();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CParticleSystem::~CParticleSystem()
{
    assert((m_apParticleSources.Size() == 0) && "particle source not freed !");
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CParticleSystem::CreateSharedIndexBuffer()
{
    CArray<unsigned short> pIndices;
    pIndices.New(MAX_SHARED_IB_SIZE);

    for (int i = 0; i < MAX_SHARED_IB_SIZE / 12; i++)
    {
        pIndices[6 * i + 0] = i * 4 + 0;        pIndices[6 * i + 1] = i * 4 + 1;        pIndices[6 * i + 2] = i * 4 + 2;
        pIndices[6 * i + 3] = i * 4 + 1;        pIndices[6 * i + 4] = i * 4 + 3;        pIndices[6 * i + 5] = i * 4 + 2;
    }

    m_hndSharedIndexBuffer = 
        m_pxEngineController->GetIndexBufferFactory()->
            CreateIndexBuffer(MAX_SHARED_IB_SIZE / 2, D3DFMT_INDEX16, D3DUSAGE_WRITEONLY, D3DPOOL_DEFAULT, pIndices.m_pArray);

    pIndices.Delete();
}   
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSystem::Render(TRenderContextPtr spxRenderContext)
{
    // TODO (performance): Partikelquellen vor dem Rendern nach Typ sortieren

    const CViewFrustum& viewFrustum = spxRenderContext->m_xViewFrustum;

    for (unsigned int uiSource = 0; uiSource < m_apParticleSources.Size(); uiSource++)
    {
        CBoundingSphere xSphere;
        m_apParticleSources[uiSource]->GetBoundingSphere(&xSphere);

        if (viewFrustum.SphereIntersects(xSphere))
        {
            m_apParticleSources[uiSource]->Render(spxRenderContext);
        }
    }

    m_pxEngineController->GetDeviceStateMgr()->SetRenderState(D3DRS_ZWRITEENABLE, TRUE);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSystem::DoGameplayStep(float fTimeDelta, const CCamera* pxCamera)
{
	const CViewFrustum* pViewFrustum = NULL;

	if (pxCamera)
		pxCamera->GetViewFrustum();

    for (unsigned int uiSource = 0; uiSource < m_apParticleSources.Size(); uiSource++)
    {
        CBoundingSphere xSphere;
        m_apParticleSources[uiSource]->GetBoundingSphere(&xSphere);

		if (pViewFrustum == NULL ||
			pViewFrustum->SphereIntersects(xSphere))
        {
            m_apParticleSources[uiSource]->DoGameplayStep(fTimeDelta);
        }
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TIndexBufferHandle 
CParticleSystem::GetSharedIndexBuffer() const
{
    return m_hndSharedIndexBuffer;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSystem::AddSource(CParticleSource* pxParticleSource)
{
    m_apParticleSources.Push() = pxParticleSource;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSystem::RemSource(CParticleSource* pxParticleSource)
{
    m_apParticleSources.RemoveEntry(pxParticleSource);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CEngineController* 
CParticleSystem::GetEngineController() const
{
    return m_pxEngineController;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

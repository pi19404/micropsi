#include "stdafx.h"

#include "e42/ParticleSource.h"

#include <d3dx9math.h>

#include "e42/Camera.h"
#include "e42/ParticleSystem.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/EngineController.h"
#include "e42/core/EffectFactory.h"
#include "e42/core/TextureFactory.h"
#include "e42/core/VertexBufferFactory.h"
#include "e42/core/VertexDeclarationFactory.h"
#include "e42/core/EffectShader.h"
//#include "e42/DebugUtils.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CParticleSource::CParticleSource(CParticleSystem* pxParticleSystem)
:   m_pxParticleSystem(pxParticleSystem)
{
	m_vSourcePos.Clear();
	m_vSourceDir.Clear();
	m_mSourceRot.SetIdentity();

	m_fRestTimeFromLastSimulation = 0;


	m_pxParticleSystem->AddSource(this);

	m_bInitialized = false;

	m_bActive = true;
	m_bSimulateSynchronToGameplay = false;

	m_fParticleSizeFact = 1.0f;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CParticleSource::~CParticleSource()
{
	m_pxParticleSystem->RemSource(this);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::InitParameters()
{
	m_iMaxParticles = 341;
	m_fNewParticlesPerSecond = 20.0f;
	m_vSourceDir = CVec3(0, 1, 0);
	m_fParticleSizeFact = 1.0f;

	m_iTextureTilesX = 2;
	m_iTextureTilesY = 2;

	m_sParticleTextureFile = "prt-texture>texture.particleTest.001.png";
	m_sParticleEffectFile = "prt-shader>particle.default.fx";

	m_dwVertexFVF = ParticleVertexFVF;

//    SetDebugWatch("i dustparticles: %d", (int*)(&m_axParticles)+1);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::Init()
{
	// Vertexbuffer
	CEngineController* pxEngineController = m_pxParticleSystem->GetEngineController();

	m_hndVertexBuffer = 
		pxEngineController->GetVertexBufferFactory()->CreateVertexBufferFVF(
		m_iMaxParticles * 4, m_dwVertexFVF, D3DUSAGE_DYNAMIC | D3DUSAGE_WRITEONLY, D3DPOOL_DEFAULT);


	// Indexbuffer
	m_hndIndexBuffer = m_pxParticleSystem->GetSharedIndexBuffer();
	if (m_iMaxParticles * 12> CParticleSystem::MAX_SHARED_IB_SIZE)
	{
		assert(false);
		m_iMaxParticles = CParticleSystem::MAX_SHARED_IB_SIZE / 12;
	}

	// Textur
	m_hndTexture = 
		pxEngineController->GetTextureFactory()->CreateTextureFromFile(m_sParticleTextureFile);


	// VertexDeclaration
	m_hndVertexDeclaration = 
		pxEngineController->GetVertexDeclarationFactory()->CreateVertexDeclaration(m_dwVertexFVF);


	// Effect
	m_hndEffect = 
		pxEngineController->GetEffectFactory()->CreateEffect(m_sParticleEffectFile);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::Shut()
{
	m_hndIndexBuffer.Release();
	m_hndVertexBuffer.Release();
	m_hndTexture.Release();
	m_hndVertexDeclaration.Release();
	m_hndEffect.Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMat4S  
CParticleSource::CalcTransform()
{
	return CMat4S::mIdentity;
	// keine Transformation, da die Partikel im Worldspace simuliert werden (und auch so im VertexBuffer stehen)
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::AddNewParticles(float fDeltaTime)
{
	if (m_fNewParticlesPerSecond > 0)
	{
		fDeltaTime += m_fRestTimeFromLastSimulation;

		while (fDeltaTime >= 1 / m_fNewParticlesPerSecond)
		{
			fDeltaTime -= 1 / m_fNewParticlesPerSecond;

			if ((int)m_axParticles.Size() != m_iMaxParticles)
			{
				CParticle& particle = m_axParticles.Push();
				particle.m_fAge = fDeltaTime;

				// neuen Partikel initialisieren
				particle.m_vPos = m_vSourcePos;
				particle.m_fSize = 0.1f;
				particle.m_fRotation = 0;
				particle.m_fOpacity = 1.0f;
				particle.m_iPhase = 0;
			}
		}

		m_fRestTimeFromLastSimulation = fDeltaTime;
	}
	else
	{
		m_fRestTimeFromLastSimulation = 0;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::Simulate(float fDeltaTime)
{
	for (unsigned int uiParticle = 0; uiParticle < m_axParticles.Size(); uiParticle++)
	{
		CParticle& particle = m_axParticles[uiParticle];

		// Partikel simulieren
		particle.m_fAge += fDeltaTime;

		if (particle.m_fAge < 17.0f)
		{
			// Partikel simulieren
			//particle.m_vPos += m_vSourceDir * fDeltaTime;
			particle.m_iPhase = int(particle.m_fAge / 2) % 4;
			particle.m_fRotation = particle.m_fAge;
		}
		else
		{
			// Partikel löschen
			m_axParticles.DeleteEntry(uiParticle);
			uiParticle--;
			continue;
		}
	}

	AddNewParticles(fDeltaTime);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CParticleSource::UpdateVertexBuffer()
{
	ParticleVertex* pVertices = 0;
	HRESULT hr = m_hndVertexBuffer.GetPtr()->Lock(0, sizeof(ParticleVertex) * 4 * m_axParticles.Size(), (void**)&pVertices, D3DLOCK_DISCARD);

	if (hr != D3DERR_WASSTILLDRAWING)
	{
		assert(SUCCEEDED(hr));

		for (unsigned int i = 0; i < m_axParticles.Size(); i++)
		{
			for (int j = 0; j < 4; j++)
			{
				pVertices[i * 4 + j].vPos = m_axParticles[i].m_vPos;
				pVertices[i * 4 + j].vUV.x() =  (m_axParticles[i].m_iPhase % m_iTextureTilesX                     + (~j & 1)) / (float)m_iTextureTilesX;
				pVertices[i * 4 + j].vUV.y() = ((m_axParticles[i].m_iPhase / m_iTextureTilesX) % m_iTextureTilesY + (j >> 1)) / (float)m_iTextureTilesY;
				pVertices[i * 4 + j].fSize = m_axParticles[i].m_fSize;

				int iAng = j;
				if (iAng & 2) iAng ^= 1;

				pVertices[i * 4 + j].fRotation = m_axParticles[i].m_fRotation - (iAng - 0.5f) * 0.5f * PIf;
				pVertices[i * 4 + j].dwDiffuse = int(255 * m_axParticles[i].m_fOpacity) << 24;
			}
		}

		m_hndVertexBuffer.GetPtr()->Unlock();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CParticleSource::SetupShaderConstants(TRenderContextPtr spxRenderContext)
{
	CMat4S matWorldView = 
		CalcTransform() * spxRenderContext->m_matViewTransform;

	m_hndEffect.GetPtr()->SetWorldViewMatrix(matWorldView);
	m_hndEffect.GetPtr()->SetProjectionMatrix(spxRenderContext->m_matProjectionTransform);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CParticleSource::Draw()
{
	CEngineController* pxEngineController = m_pxParticleSystem->GetEngineController();

	CDeviceStateMgr* pd3dDeviceStateMgr = pxEngineController->GetDeviceStateMgr();

	pd3dDeviceStateMgr->SetStreamSource(0, m_hndVertexBuffer.GetPtr(), 0, sizeof(ParticleVertex));
	pd3dDeviceStateMgr->SetIndices(m_hndIndexBuffer.GetPtr());
	pd3dDeviceStateMgr->SetVertexDeclaration(m_hndVertexDeclaration.GetPtr());

	m_hndEffect.GetPtr()->
		SetDiffuseMap(m_hndTexture);

	UINT uiNumPasses;
	m_hndEffect.GetPtr()->Begin(&uiNumPasses, D3DXFX_DONOTSAVESTATE);

	for (UINT uiPass = 0; uiPass < uiNumPasses; uiPass++)
	{
		// rendern
		m_hndEffect.GetPtr()->BeginPass(uiPass);

		HRESULT hr = pxEngineController->GetDevice()->DrawIndexedPrimitive(
			D3DPT_TRIANGLELIST, 
			0, 0, 
			4 * m_axParticles.Size(), 
			0, 
			2 * m_axParticles.Size());

		assert(SUCCEEDED(hr));

		m_hndEffect.GetPtr()->EndPass();
	}

	m_hndEffect.GetPtr()->End();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::Render(TRenderContextPtr spxRenderContext)
{
	if (m_bActive)
	{
		if (!m_bInitialized)
		{
			m_bInitialized = true;
			InitParameters();
			Init();
		}

		if (!m_bSimulateSynchronToGameplay)
		{
			Simulate(spxRenderContext->m_fDeltaTime);
		}

		if (m_axParticles.Size() == 0)
		{
			return;
		}

		UpdateVertexBuffer();
		SetupShaderConstants(spxRenderContext);

		Draw();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::DoGameplayStep(float fDeltaTime)
{
	if (m_bSimulateSynchronToGameplay)
	{
		Simulate(fDeltaTime);
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::GetBoundingSphere(CBoundingSphere* pxSphereOut) const
{
	pxSphereOut->m_vCenter = m_vSourcePos + m_vSourceDir * 0.7f;
	pxSphereOut->m_fRadius = 1.0f;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const CVec3& 
CParticleSource::GetPos() const
{
	return m_vSourcePos;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::SetPos(const CVec3& vPos)
{
	m_vSourcePos = vPos;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const CMat3S& 
CParticleSource::GetRot() const
{
	return m_mSourceRot;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::SetRot(const CMat3S& mRot)
{
	m_mSourceRot = mRot;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const CVec3& 
CParticleSource::GetDir() const
{
	return m_vSourceDir;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::SetDir(const CVec3& vDir)
{
	m_vSourceDir = vDir;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::SetNewParticlesPerSecond(float fNewParticlesPerSecond)
{
	m_fNewParticlesPerSecond = fNewParticlesPerSecond;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::Reset()
{
	m_fRestTimeFromLastSimulation = 0;
	m_axParticles.Clear();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::SetActive(bool bActive)
{
	m_bActive = bActive;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::SetSimulateSynchronToGameplay(bool bSimulateSynchronToGameplay)
{
	m_bSimulateSynchronToGameplay = bSimulateSynchronToGameplay;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSource::SetParticleSizeFact(float fParticleSizeFact)
{
	m_fParticleSizeFact = fParticleSizeFact;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

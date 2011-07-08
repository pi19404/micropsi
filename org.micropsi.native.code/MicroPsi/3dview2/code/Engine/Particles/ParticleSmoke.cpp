#include "ParticleSmoke.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CParticleSmoke::CParticleSmoke(CParticleSystem* pParticleSystem)
:   CParticleSource(pParticleSystem)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CParticleSmoke::~CParticleSmoke()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSmoke::InitParameters()
{
    CParticleSource::InitParameters();

    m_iMaxParticles = 341;
    m_sParticleTextureFile = "prt-texture>particle_smoke.tga";
    m_iTextureTilesX = 1;
    m_iTextureTilesY = 1;
    m_fNewParticlesPerSecond = 5.0f;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CParticleSmoke::AddNewParticles(float fDeltaTime)
{
    if ((m_fNewParticlesPerSecond > 0) &&
        ((int)m_axParticles.Size() != m_iMaxParticles))
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
                CVec3 vRand(((rand() / 32768.0f) - 0.5f) * 0.2f,
                             (rand() / 32768.0f) * 0.1f,
                            ((rand() / 32768.0f) - 0.5f) * 0.2f);
                particle.m_vPos = m_vSourcePos + vRand;
				particle.m_vDir = vRand * 4.0f + m_vSourceDir;
                particle.m_fSize = 0;
                particle.m_fRotation = rand() / 16384.0f * PIf;
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
CParticleSmoke::Simulate(float fDeltaTime)
{
    for (unsigned int uiParticle = 0; uiParticle < m_axParticles.Size(); uiParticle++)
    {
        CParticle& particle = m_axParticles[uiParticle];

        // Partikel simulieren
        particle.m_fAge += fDeltaTime;

        if (particle.m_fAge < 10.0f)
        {
            // Partikel simulieren
            particle.m_vPos += particle.m_vDir * fDeltaTime;

            particle.m_fRotation += fDeltaTime / (1.0f + particle.m_fAge);
            particle.m_fSize = sqrtf(particle.m_fAge);
            particle.m_fOpacity = 0.15f / (0.25f + particle.m_fSize);
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
CParticleSmoke::GetBoundingSphere(CBoundingSphere& rxSphere) const
{    
	rxSphere.m_vCenter = m_vSourcePos + m_vSourceDir * 10.0f;
    rxSphere.m_fRadius = 20.0f;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

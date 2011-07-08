#pragma once

#ifndef PARTICLESMOKE_H_INCLUDED
#define PARTICLESMOKE_H_INCLUDED

#include "Application/stdinc.h"
#include "e42/ParticleSource.h"

class CParticleSmoke : public CParticleSource
{
private:
    void AddNewParticles(float fDeltaTime);
    void Simulate(float fDeltaTime);

public:
    CParticleSmoke(CParticleSystem* pParticleSystem);
    ~CParticleSmoke();

    void InitParameters();
    virtual void GetBoundingSphere(CBoundingSphere& rxSphere) const;
};

#endif // PARTICLESMOKE_H_INCLUDED
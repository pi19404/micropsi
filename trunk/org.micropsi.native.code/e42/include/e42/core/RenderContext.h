#pragma once

#ifndef RENDERCONTEXT_H_INCLUDED
#define RENDERCONTEXT_H_INCLUDED

#include "e42/stdinc.h"

#include "baselib/refcountedobject.h"

#include <string>
#include "baselib/geometry/Matrix.h"
#include "e42/Core/ViewFrustum.h"
#include "e42/Core/ResourceHandles.h"
#include "baselib/color.h"

class CMeshCombiner;
class CEngineController;

class CRenderContext : public CRefCountedObject
{
private:
    CRenderContext();
    ~CRenderContext();

public:

    static CRenderContext*  Create();
    void                    Destroy();

    void SetViewMatrix(const CMat4S& matWorldTransform, bool bUpdateCombinedMatrices = true);
    void SetProjectionMatrix(const CMat4S& matWorldTransform, bool bUpdateCombinedMatrices = true);


    CEngineController*      m_pxEngineController;

    CMat4S                  m_matViewTransform;
    CMat4S                  m_matViewInverseTransform;

    CMat4S                  m_matProjectionTransform;
    CMat4S                  m_matProjectionInverseTransform;

    CMat4S                  m_matViewProjectionTransform;


    CViewFrustum            m_xViewFrustum;


    std::string             m_sTechnique;

    CVec3                   m_vLightDir;

    float                   m_fTime;
    float                   m_fDeltaTime;
};

typedef CSmartPointer<CRenderContext> TRenderContextPtr;

#endif // RENDERCONTEXT_H_INCLUDED

#include "stdafx.h"

#include "e42/core/RenderContext.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CRenderContext::CRenderContext()
{
    m_pxEngineController = NULL;

    m_matViewTransform.SetIdentity();
    m_matProjectionTransform.SetIdentity();

    m_matViewInverseTransform.SetIdentity();
    m_matProjectionInverseTransform.SetIdentity();

    m_matViewProjectionTransform.SetIdentity();

    m_vLightDir = CVec3(0, -1, 0);

    m_fTime = 0;
    m_fDeltaTime = 0;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CRenderContext::~CRenderContext()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CRenderContext*
CRenderContext::Create()
{
    return new CRenderContext();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CRenderContext::Destroy()
{
    delete this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CRenderContext::SetViewMatrix(const CMat4S& matViewTransform, bool bUpdateCombinedMatrices)
{
    m_matViewTransform = matViewTransform;

    if (bUpdateCombinedMatrices)
    {
        m_matViewInverseTransform = m_matViewTransform.GetInverse();
        m_matViewProjectionTransform = m_matViewTransform * m_matProjectionTransform;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CRenderContext::SetProjectionMatrix(const CMat4S& matProjectionTransform, bool bUpdateCombinedMatrices)
{
    m_matProjectionTransform = matProjectionTransform;

    if (bUpdateCombinedMatrices)
    {
        m_matProjectionInverseTransform = m_matProjectionTransform.GetInverse();
        m_matViewProjectionTransform = m_matViewTransform * m_matProjectionTransform;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------

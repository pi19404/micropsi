#include "stdafx.h"

#include "e42/Camera.h"

#include "e42/core/RenderContext.h"

//-------------------------------------------------------------------------------------------------------------------------------------------
CCamera::CCamera()
{
	m_bLeftHandedWorldCoordinateSystem = false;

    // Defaults für Matrizen
    m_matView.SetIdentity();
    m_matViewInverse.SetIdentity();

    m_matProjection.SetIdentity();
    m_matProjectionInverse.SetIdentity();

    m_matViewProjection.SetIdentity();
    m_matViewProjectionInverse.SetIdentity();


    // Defaults für ViewMatrix
    m_vUpVector = CVec3(0, 1, 0).GetNormalized();              // by default
    m_vLookAtDir = CVec3(0, 0, 1).GetNormalized();              // by default 
    m_vPos = CVec3(0, 0, -10.0f);


    // Defaults für Projektion
    m_fFieldOfViewHeight = 60.0f / 180.0f * PIf;                 // 60° FieldOfView
    m_fAspectRatio = 4.0f / 3.0f;                               // 4/3

    m_fFarPlaneDistance = 100.0f;                               // maximal 100 Meter!
    m_fNearPlaneDistance = 0.1f;                                // 10 cm

    m_bPerspective = true;


	SetupMatrices();
	UpdateViewFrustum();
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CCamera::~CCamera()
{
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CCamera::SetupMatrices()
{
    SetupViewMatrix();
    SetupProjectionMatrix();

    SetupCombinedMatrices();
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CCamera::SetupViewMatrix()
{
	assert( m_vUpVector != m_vLookAtDir && m_vUpVector != -m_vLookAtDir);

    CVec3 vRightVec = GetRightVec();
	CVec3 vCamUpVec = CalcOrthogonalUpVec(vRightVec);
    CVec3 vLookAtDir = m_vLookAtDir;

    // fiese Vektoren können dazu führen, dass die Matrix nicht invertierbar ist, daher werden sie gefixt
    vRightVec.Fix();
    vLookAtDir.Fix();
    vCamUpVec.Fix();

    m_matViewInverse(0, 0) = vRightVec.x();     m_matViewInverse(0, 1) = vRightVec.y();     m_matViewInverse(0, 2) = vRightVec.z();     m_matViewInverse(0, 3) = 0;
    m_matViewInverse(1, 0) = vCamUpVec.x();     m_matViewInverse(1, 1) = vCamUpVec.y();     m_matViewInverse(1, 2) = vCamUpVec.z();     m_matViewInverse(1, 3) = 0;
    m_matViewInverse(2, 0) = vLookAtDir.x();    m_matViewInverse(2, 1) = vLookAtDir.y();    m_matViewInverse(2, 2) = vLookAtDir.z();    m_matViewInverse(2, 3) = 0;
    m_matViewInverse(3, 0) = m_vPos.x();        m_matViewInverse(3, 1) = m_vPos.y();        m_matViewInverse(3, 2) = m_vPos.z();        m_matViewInverse(3, 3) = 1;

    m_matView = m_matViewInverse.GetInverse();
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CCamera::SetupProjectionMatrix()
{
    // müsste eigentlich nicht jedes mal gemacht werden

    if (m_bPerspective)
    {
        float h = 1.0f / tanf(m_fFieldOfViewHeight * 0.5f);   // 1/tan(x) == cot(x)
        float w = h / m_fAspectRatio;
        float Q = m_fFarPlaneDistance / (m_fFarPlaneDistance - m_fNearPlaneDistance);

        m_matProjection.Clear();
        m_matProjection(0, 0) = w;
        m_matProjection(1, 1) = h;
        m_matProjection(2, 2) = Q;
        m_matProjection(3, 2) = -Q * m_fNearPlaneDistance;
        m_matProjection(2, 3) = 1;
    }
    else
    {
        float h = 1.0f / m_fFieldOfViewHeight;
        float w = h / m_fAspectRatio;

        m_matProjection.Clear();
        m_matProjection(0, 0) = w;
        m_matProjection(1, 1) = h;
        m_matProjection(2, 2) = 1 / (m_fFarPlaneDistance - m_fNearPlaneDistance);
        m_matProjection(3, 2) = -m_fNearPlaneDistance / (m_fFarPlaneDistance - m_fNearPlaneDistance);

        m_matProjection(3, 3) = 1;
    }
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CCamera::SetupCombinedMatrices()
{
    m_matViewInverse = m_matView.GetInverse();
    m_matProjectionInverse = m_matProjection.GetInverse();

    m_matViewProjection = m_matView * m_matProjection;
    m_matViewProjectionInverse = m_matProjectionInverse * m_matViewInverse;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CCamera::SetupRenderContext(const TRenderContextPtr& pxRenderContext) const
{
    pxRenderContext->m_xViewFrustum = m_ViewFrustum;


    pxRenderContext->m_matViewTransform = m_matView;
    pxRenderContext->m_matViewInverseTransform = m_matViewInverse;

    pxRenderContext->m_matProjectionTransform = m_matProjection;
    pxRenderContext->m_matProjectionInverseTransform = m_matProjectionInverse;

    pxRenderContext->m_matViewProjectionTransform = m_matViewProjection;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CVec3
CCamera::CalcOrthogonalUpVec() const
{
    CVec3 vRightVec = GetRightVec();

    CVec3 vOrthognalUpVec = vRightVec ^ m_vLookAtDir;
    vOrthognalUpVec.Normalize();

	if (m_bLeftHandedWorldCoordinateSystem)
	{
		vOrthognalUpVec = -vOrthognalUpVec;
	}

	return vOrthognalUpVec;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CVec3
CCamera::CalcOrthogonalUpVec(CVec3& vRightVec) const
{
	CVec3 vOrthognalUpVec = vRightVec ^ m_vLookAtDir;
    vOrthognalUpVec.Normalize();

	if (m_bLeftHandedWorldCoordinateSystem)
	{
		vOrthognalUpVec = -vOrthognalUpVec;
	}

	return vOrthognalUpVec;
}
//-------------------------------------------------------------------------------------------------------------------------------------------

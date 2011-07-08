#include "stdafx.h"

#include "baselib/geometry/Plane.h"

//---------------------------------------------------------------------------------------------------------------------
bool
CPlane::HitTest(const CRay& p_rxRay, CVec3& po_rvHitPoint) const
{
	//		    t = n*base - offset / n*dir;
	CVec3 vRayDir = p_rxRay.m_vDirection.GetNormalized();
	float ndir = m_vNormal * vRayDir;
	if(fabs(ndir) < 0.0001f)
	{
		return false;
	}
	float t = -(m_vNormal * p_rxRay.m_vBase + m_fOffset) / ndir;
	po_rvHitPoint = p_rxRay.m_vBase + vRayDir * t;
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
CRay
CPlane::CalcInterseciontWithPlane(const CPlane& rxPlane) const
{
	CVec3 vPlaneBase = m_vNormal * m_fOffset;

	CRay xRay;
	xRay.m_vDirection = (rxPlane.m_vNormal ^ m_vNormal).GetNormalized();
	xRay.m_vBase = rxPlane.CalcInterseciontWithRay(CRay(vPlaneBase, (xRay.m_vDirection ^ m_vNormal).GetNormalized()));

	return xRay;
}
//---------------------------------------------------------------------------------------------------------------------
CVec3
CPlane::CalcInterseciontWithRay(const CRay& rxRay) const
{
	float fRayBaseDistanceToPlane = rxRay.m_vBase * m_vNormal - m_fOffset;
	return rxRay.m_vBase - rxRay.m_vDirection * (fRayBaseDistanceToPlane / (rxRay.m_vDirection * m_vNormal));
}
//---------------------------------------------------------------------------------------------------------------------

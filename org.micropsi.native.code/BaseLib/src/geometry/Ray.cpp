#include "stdafx.h"

#include "baselib/geometry/Ray.h"

//---------------------------------------------------------------------------------------------------------------------
CVec3
CRay::CalcNearestPointOnRay(const CVec3& vPos) const
{
	return m_vBase + m_vDirection * ((vPos - m_vBase) * m_vDirection);
}
//---------------------------------------------------------------------------------------------------------------------
float
CRay::CalcDistanceToPoint(const CVec3& vPos) const
{
	CVec3 vRayToPoint = vPos - m_vBase;
	return (vRayToPoint - m_vDirection * (vRayToPoint * m_vDirection)).Abs();
}
//---------------------------------------------------------------------------------------------------------------------

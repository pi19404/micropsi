#pragma once

#ifndef ENIGNE_GEOMETRY_RAY_H_INCLUDED
#define ENIGNE_GEOMETRY_RAY_H_INCLUDED

#include "baselib/geometry/CVector.h"

class CRay
{
public:
	CRay();
	CRay(const CVec3& rvBase, const CVec3& rvDir);

    CVec3   m_vBase;
    CVec3   m_vDirection;

	CVec3	CalcNearestPointOnRay(const CVec3& vPos) const;
	float	CalcDistanceToPoint(const CVec3& vPos) const;
};

#include "baselib/geometry/Ray.inl"


#endif // ENIGNE_GEOMETRY_RAY_H_INCLUDED

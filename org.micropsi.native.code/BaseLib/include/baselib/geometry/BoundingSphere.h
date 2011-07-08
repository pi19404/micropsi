#pragma once

#ifndef BOUNDINGSPHERE_H_INCLUDED
#define BOUNDINGSPHERE_H_INCLUDED

#include <math.h>

#include "baselib/geometry/Ray.h"

class CBoundingSphere
{
public:
    CVec3   m_vCenter;
    float   m_fRadius;

    void Clear();

    void AddPoint(const CVec3& p_rvPoint);
	void AddSphere(const CBoundingSphere& p_rvSphere);
    bool PointIsInside(const CVec3& p_rvPoint) const;
    bool PointIsInside(const CVec3& p_rvPoint, float p_fTolerance) const;
	bool Contains(const CBoundingSphere& p_xrSphere) const;
	bool Overlaps(const CBoundingSphere& p_rxBSphere) const;
    bool Overlaps(const CRay& p_rxRay) const;
};

#include "baselib/geometry/BoundingSphere.inl"

#endif BOUNDINGSPHERE_H_INCLUDED

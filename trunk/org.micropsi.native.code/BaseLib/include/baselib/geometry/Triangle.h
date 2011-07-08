#pragma once

#ifndef TRIANGLE_H_INCLUDED
#define TRIANGLE_H_INCLUDED

#include "e42/stdinc.h"

#include "baselib/geometry/CVector.h"
#include "baselib/geometry/Ray.h"
#include "baselib/geometry/Plane.h"

//-------------------------------------------------------------------
//  CTriangle points are tri(s,t)=b + s*e0 + t*e1 where
//  0<=s<=1, 0<=t<=1 and 0<=s+t<=1
//-------------------------------------------------------------------
class CTriangle {
public:
    CVec3 b,e0,e1;

    CTriangle();
	CTriangle(const CVec3& v0, const CVec3& v1, const CVec3& v2); 
    CTriangle(const CTriangle& t); 
    
    void	Set(const CVec3& v0, const CVec3& v1, const CVec3& v2);
    CVec3	GetNormal() const;
    CVec3	GetCenterOfGravity() const;
    CPlane	GetPlane() const;
    CVec3	GetCorner(int i) const;
    bool	IntersectsSingleSided(const CRay& line, float& ipos);
    bool	IntersectsDoubleSided(const CRay& line, float& ipos);
};

#include "baselib/geometry/Triangle.inl"

#endif // ifdef TRIANGLE_H_INCLUDED


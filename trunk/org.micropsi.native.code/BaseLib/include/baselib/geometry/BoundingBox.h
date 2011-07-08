#pragma once

#ifndef E42_BOUNDINGBOX_H_INCLUDED
#define E42_BOUNDINGBOX_H_INCLUDED

#include "baselib/geometry/Matrix.h"
#include "baselib/geometry/CVector.h"
#include "baselib/geometry/Ray.h"
#include "baselib/geometry/BoundingSphere.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
class CBoundingBox
{
public:
    CVec3 m_vBase;
    CVec3 m_vEdge1;
    CVec3 m_vEdge2;
    CVec3 m_vEdge3;

    bool PointIsInside(CVec3 p_vPoint) const;
    bool Overlaps(const CRay& p_rxRay) const;
};
//-----------------------------------------------------------------------------------------------------------------------------------------
class CAxisAlignedBoundingBox
{
public:
    CVec3 m_vMin;
    CVec3 m_vMax;

    void Clear();
    void AddPoint(const CVec3& p_rvPoint);
    void AddBBox(const CAxisAlignedBoundingBox& p_rxAABBox);

	CVec3 GetCenter() const;
	CVec3 GetNearestCorner(const CVec3& p_rvReferencePoint) const; 
	CVec3 GetSize() const;

	void Translate(const CVec3& p_rvDistance);
	CAxisAlignedBoundingBox GetTranslated(const CVec3& p_rvDistance) const;

    bool PointIsInside(const CVec3& p_rvPoint) const;
    bool PointIsInside(const CVec3& p_rvPoint, float p_fTolerance) const;
    bool Overlaps(const CAxisAlignedBoundingBox& p_rxAABBox) const;
	bool Overlaps(const CRay& p_rxRay) const;
	bool Overlaps(const CBoundingSphere& p_rxSphere) const;
	bool Contains(const CAxisAlignedBoundingBox& p_xrOther) const;
	bool Contains(const CBoundingSphere& p_xrSphere) const;
};
//-----------------------------------------------------------------------------------------------------------------------------------------

typedef CBoundingBox                BBox;
typedef CAxisAlignedBoundingBox     AABBox;

#include "baselib/geometry/BoundingBox.inl"

#endif // E42_BOUNDINGBOX_H_INCLUDED
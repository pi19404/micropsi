#pragma once

#ifndef COLLISIONOBJECTS_H_INCLUDED
#define COLLISIONOBJECTS_H_INCLUDED

#include "baselib/geometry/BoundingSphere.h"
#include "baselib/geometry/BoundingBox.h"

class CCollisionObject
{
public: 
    virtual bool PointIsInside(const CVec3& p_rvPoint) const = 0;
	virtual bool Overlaps(const CBoundingSphere& p_rxSphere) const = 0;
	virtual bool Contains(const CBoundingSphere& p_xrSphere) const = 0;
};

class CCollisionObjectSphere : public CCollisionObject
{
public:
	CCollisionObjectSphere(const CBoundingSphere& p_rxSphere);

    virtual bool PointIsInside(const CVec3& p_rvPoint) const;
	virtual bool Overlaps(const CBoundingSphere& p_rxSphere) const;
	virtual bool Contains(const CBoundingSphere& p_xrSphere) const;

	CBoundingSphere m_xSphere;
};


class CCollisionObjectAABox : public CCollisionObject
{
public:
	CCollisionObjectAABox(const CAxisAlignedBoundingBox& p_rxAABB);

	virtual bool PointIsInside(const CVec3& p_rvPoint) const;
	virtual bool Overlaps(const CBoundingSphere& p_rxSphere) const;
	virtual bool Contains(const CBoundingSphere& p_rxSphere) const;

	CAxisAlignedBoundingBox m_xAABB;
};

#include "baselib/geometry/CollisionObjects.inl"

#endif // COLLISIONOBJECTS_H_INCLUDED


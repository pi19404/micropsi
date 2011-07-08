#pragma once

#ifndef E42_VIEWFRUSTUM_H_INCLUDED
#define E42_VIEWFRUSTUM_H_INCLUDED

#include "baselib/geometry/CVector.h"
#include "baselib/geometry/Matrix.h"
#include "baselib/geometry/BoundingSphere.h"
#include "baselib/geometry/BoundingBox.h"
#include "baselib/geometry/Plane.h"

class CViewFrustum
{
private:
    CVec3   m_vEyePos;

	enum Planes
	{
		Left,
		Right,
		Top,
		Bottom,
		Far,
		Near,
		NumPlanes
	};

	CPlane	m_axPlanes[NumPlanes];

    // die Limits können wie folgt genutzt werden:
    //  m_vPlaneNormal * vTestPoint <  m_fPlaneLimit   -> Testpunkt ist auf der "Innenseite" der Plane
    //  m_vPlaneNormal * vTestPoint == m_fPlaneLimit   -> Testpunkt liegt genau auf der Plane
    //  m_vPlaneNormal * vTestPoint >  m_fPlaneLimit   -> Testpunkt ist auf der "Außenseite" der Plane
    // siehe SphereIntersects() und PointIsInside()

	static void GetPositiveFarthestPoint(const CAxisAlignedBoundingBox& p_rxAABB, const CVec3& p_rvNormal, CVec3& po_rvPosFarPoint);
	static void GetNegativeFarthestPoint(const CAxisAlignedBoundingBox& p_rxAABB, const CVec3& p_rvNormal, CVec3& po_vrNegFarPoint);

public:
    CViewFrustum();
    ~CViewFrustum();

	void SetNearPlaneDistance(float p_fDistance);
	void SetFarPlaneDistance(float p_fDistance);

	float GetNearPlaneDistance() const;
	float GetFarPlaneDistance() const;

	CVec3 GetEyePoint() const;

    void Update(const CMat4S& matViewProjectionInverse, float fNearPlaneDistance, float fFarPlaneDistance, bool p_bPerspective, bool bLeftHandedWorldCoordinateSystem, float fFrustumVertShrink = 0);

    bool SphereIntersects(const CBoundingSphere& rxSphere) const;
    bool SphereIsInside(const CBoundingSphere& rxSphere) const;
    bool PointIsInside(const CVec3& vPointCenter) const;

	enum Intersection
	{
		Inside,
		Outside,
		Intersects
	};

	Intersection CheckAABBIntersection(const CAxisAlignedBoundingBox& p_rxBox) const;
	Intersection CheckAABBIntersection(const CAxisAlignedBoundingBox& p_rxBox, int& po_LastDecisivePlane) const;

	bool	operator==(const CViewFrustum& p_rxOther) const;
	bool	operator!=(const CViewFrustum& p_rxOther) const;
};

#include "e42/core/ViewFrustum.inl"

#endif // E42_VIEWFRUSTUM_H_INCLUDED

#pragma once

#ifndef E42_PLANE_H_INCLUDED
#define E42_PLANE_H_INCLUDED

#include "baselib/geometry/CVector.h"
#include "baselib/geometry/Ray.h"

class CPlane
{
public:
	CPlane();
	CPlane(const CPlane& xrOther);

	CPlane(float fA, float fB, float fC, float fD);
	CPlane(const CVec3& vrP1, const CVec3& vrP2, const CVec3& vrP3);
	CPlane(const CVec3& vrNormal, float fDistance);
	CPlane(const CVec3& vrNormal, const CVec3& vrPoint);

	void	Normalize();
	float	GetDistanceAndDirection(const CVec3& vrPoint) const;
	float	GetAbsoluteDistance(const CVec3& vrPoint) const;

	float	PointX(float fY, float fZ); 
	float	PointY(float fX, float fZ); 
	float	PointZ(float fX, float fY); 

	bool	HitTest(const CRay& rxRay, CVec3& po_rvHitPoint) const;

	CRay	CalcInterseciontWithPlane(const CPlane& rxPlane) const;		///< berechnet die Schnittgerade zwischen zwei Ebenen
	CVec3	CalcInterseciontWithRay(const CRay& rxRay) const;			///< berechnet den Schnittpunkt zwischen Ebene und Gerade (gegeben als Ray)


	CVec3 m_vNormal;					///< Normalenvektor
	float m_fOffset;					///< Entfernung von Ursprung 0/0/0

	bool	operator==(const CPlane& rxOther) const;
	bool	operator!=(const CPlane& rxOther) const;
};

#include "baselib/geometry/Plane.inl"

#endif // E42_PLANE_H_INCLUDED

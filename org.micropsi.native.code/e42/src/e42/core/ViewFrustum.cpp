#include "stdafx.h"

#include "e42/core/ViewFrustum.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CViewFrustum::CViewFrustum()
{
    m_vEyePos.Clear();

    for (int iPlaneIdx = 0; iPlaneIdx < NumPlanes; ++iPlaneIdx)
    {
        m_axPlanes[iPlaneIdx].m_vNormal.Clear();
        m_axPlanes[iPlaneIdx].m_fOffset = FLT_MAX;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CViewFrustum::~CViewFrustum()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CViewFrustum::Update(const CMat4S& matViewProjectionInverse, float fNearPlaneDistance, float fFarPlaneDistance, bool p_bPerspective, bool bLeftHandedWorldCoordinateSystem, float fFrustumVertShrink)
{
    // Eckpunkte des Bildschirms in der Welt bestimmen
    const float fFrustumFoVFact = 1.0f; // damit kann man dem ViewFrustum ein kleineres Field of View geben -> praktisch zu Debugzwecken, weil man dann sieht, ob Objekte richtig geclipt werden (die Objekte müssten dann schon verschwinden, bevor sie vollständig aus der Szene sind)
    const CVec4 vTopLeftScreen     (-fFrustumFoVFact,  fFrustumFoVFact - fFrustumVertShrink, 1.0f, 1.0f);
    const CVec4 vTopRightScreen    ( fFrustumFoVFact,  fFrustumFoVFact - fFrustumVertShrink, 1.0f, 1.0f);
    const CVec4 vBottomLeftScreen  (-fFrustumFoVFact, -fFrustumFoVFact + fFrustumVertShrink, 1.0f, 1.0f);
    const CVec4 vBottomRightScreen ( fFrustumFoVFact, -fFrustumFoVFact + fFrustumVertShrink, 1.0f, 1.0f);
    const CVec4 vNearPlaneScreen   ( 0.0f,  0.0f,  0.0f,   1.0f);
    const CVec4 vFarPlaneScreen    ( 0.0f,  0.0f,  1.0f,   1.0f);

    const CMat4S& matInv = matViewProjectionInverse;

    CVec4 vTmp; 
    vTmp = vNearPlaneScreen * matInv;     vTmp /= vTmp(3);    const CVec3 vNearPlaneWorld =   vTmp.GetReduced();
    vTmp = vFarPlaneScreen * matInv;      vTmp /= vTmp(3);    const CVec3 vFarPlaneWorld =    vTmp.GetReduced();

    const CVec3 vEyeRayWorld = (vFarPlaneWorld - vNearPlaneWorld).GetNormalized();
    m_vEyePos = vNearPlaneWorld - (vEyeRayWorld * fNearPlaneDistance);

    vTmp = vTopLeftScreen * matInv;       vTmp /= vTmp(3);    const CVec3 vTopLeftWorld =     vTmp.GetReduced();
    vTmp = vTopRightScreen * matInv;      vTmp /= vTmp(3);    const CVec3 vTopRightWorld =    vTmp.GetReduced();
    vTmp = vBottomLeftScreen * matInv;    vTmp /= vTmp(3);    const CVec3 vBottomLeftWorld =  vTmp.GetReduced();
    vTmp = vBottomRightScreen * matInv;   vTmp /= vTmp(3);    const CVec3 vBottomRightWorld = vTmp.GetReduced();

    // aus den Eckpunkten und der Eyepos Richtungsvektoren für die Ecken aufbauen
    const CVec3 vTopLeftWorldDir = vTopLeftWorld - m_vEyePos;            // die Richtung des Blicks in der linken oberen Ecke des Screens
    const CVec3 vTopRightWorldDir = vTopRightWorld - m_vEyePos;          // ...
    const CVec3 vBottomLeftWorldDir = vBottomLeftWorld - m_vEyePos;
    const CVec3 vBottomRightWorldDir = vBottomRightWorld - m_vEyePos;

	if (p_bPerspective)
	{
		// mit den Richtungsvektoren können die Normalen der Frustum-Planes bestimmt werden
		// Normale müssen nach außen zeigen
		m_axPlanes[Left].m_vNormal = (vTopLeftWorld - vBottomLeftWorld) ^ vTopLeftWorldDir;
		m_axPlanes[Left].m_vNormal.Normalize();
		m_axPlanes[Left].m_fOffset = m_axPlanes[Left].m_vNormal * m_vEyePos;

		m_axPlanes[Right].m_vNormal = (vBottomRightWorld - vTopRightWorld) ^ vBottomRightWorldDir;
		m_axPlanes[Right].m_vNormal.Normalize();
		m_axPlanes[Right].m_fOffset = m_axPlanes[Right].m_vNormal * m_vEyePos;

		m_axPlanes[Top].m_vNormal = (vTopRightWorld - vTopLeftWorld) ^ vTopRightWorldDir;
		m_axPlanes[Top].m_vNormal.Normalize();
		m_axPlanes[Top].m_fOffset = m_axPlanes[Top].m_vNormal * m_vEyePos;

		m_axPlanes[Bottom].m_vNormal = (vBottomLeftWorld - vBottomRightWorld) ^ vBottomLeftWorldDir;
		m_axPlanes[Bottom].m_vNormal.Normalize();
		m_axPlanes[Bottom].m_fOffset =  m_axPlanes[Bottom].m_vNormal * m_vEyePos;
	}
	else
	{
		m_axPlanes[Left].m_vNormal = (vTopLeftWorld - vTopRightWorld);
		m_axPlanes[Left].m_vNormal.Normalize();
		m_axPlanes[Left].m_fOffset = m_axPlanes[Left].m_vNormal * vTopLeftWorld;

		m_axPlanes[Right].m_vNormal = (vTopRightWorld - vTopLeftWorld);
		m_axPlanes[Right].m_vNormal.Normalize();
		m_axPlanes[Right].m_fOffset = m_axPlanes[Right].m_vNormal * vTopRightWorld;

		m_axPlanes[Top].m_vNormal = (vTopLeftWorld - vBottomLeftWorld);
		m_axPlanes[Top].m_vNormal.Normalize();
		m_axPlanes[Top].m_fOffset = m_axPlanes[Top].m_vNormal * vTopLeftWorld;

		m_axPlanes[Bottom].m_vNormal = (vBottomLeftWorld - vTopLeftWorld);;
		m_axPlanes[Bottom].m_vNormal.Normalize();
		m_axPlanes[Bottom].m_fOffset =  m_axPlanes[Bottom].m_vNormal * vBottomLeftWorld;
	}

	if (bLeftHandedWorldCoordinateSystem) 
	{
 		for (int iPlaneIdx = 0; iPlaneIdx < Planes::NumPlanes; ++iPlaneIdx)
		{
			m_axPlanes[iPlaneIdx].m_vNormal = -m_axPlanes[iPlaneIdx].m_vNormal;
			m_axPlanes[iPlaneIdx].m_fOffset = -m_axPlanes[iPlaneIdx].m_fOffset;
		}
	}


	m_axPlanes[Far].m_vNormal = vEyeRayWorld;
    m_axPlanes[Far].m_fOffset = vEyeRayWorld * vFarPlaneWorld;

	m_axPlanes[Near].m_vNormal = -vEyeRayWorld;
    m_axPlanes[Near].m_fOffset = -vEyeRayWorld * vNearPlaneWorld;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
/** 
	dies ist ein Näherungsverfahren - evtl. wird Box als schneidend gemeldet, obwohl sie außerhalb liegt
	dafür isses schneller als ein korrekter Test
*/
CViewFrustum::Intersection 
CViewFrustum::CheckAABBIntersection(const CAxisAlignedBoundingBox& p_rxBox) const
{
	CVec3 vNVertex;
	CVec3 vPVertex;
	bool bIntersects = false;

	for(int i=0; i<Planes::NumPlanes; ++i)
	{
		GetNegativeFarthestPoint(p_rxBox, m_axPlanes[i].m_vNormal, vNVertex);
		if(m_axPlanes[i].GetDistanceAndDirection(vNVertex) > 0.0f)		{ return Intersection::Outside; }
		GetPositiveFarthestPoint(p_rxBox, m_axPlanes[i].m_vNormal, vPVertex);
		bIntersects |= (m_axPlanes[i].GetDistanceAndDirection(vPVertex) > 0.0f);
	}

	return bIntersects ? Intersection::Intersects : Intersection::Inside;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
/** 
	dies ist ein Näherungsverfahren - evtl. wird Box als schneidend gemeldet, obwohl sie außerhalb liegt
	dafür isses schneller als ein korrekter Test
*/
CViewFrustum::Intersection 
CViewFrustum::CheckAABBIntersection(const CAxisAlignedBoundingBox& p_rxBox, int& po_LastDecisivePlane) const
{
//	assert(po_LastDecisivePlane >= 0  &&  po_LastDecisivePlane < Planes::NumPlanes);

	CVec3 vNVertex;
	CVec3 vPVertex;
	bool bIntersects = false;

	for(int i=po_LastDecisivePlane; i<Planes::NumPlanes; ++i)
	{
		GetNegativeFarthestPoint(p_rxBox, m_axPlanes[i].m_vNormal, vNVertex);
		if(m_axPlanes[i].GetDistanceAndDirection(vNVertex) > 0.0f)		
		{ 
			po_LastDecisivePlane = i;
			return Intersection::Outside; 
		}
		GetPositiveFarthestPoint(p_rxBox, m_axPlanes[i].m_vNormal, vPVertex);
		bIntersects |= ((m_axPlanes[i].GetDistanceAndDirection(vPVertex) > 0.0f));
	}

	for(int i=0; i<po_LastDecisivePlane; ++i)
	{
		GetNegativeFarthestPoint(p_rxBox, m_axPlanes[i].m_vNormal, vNVertex);
		if(m_axPlanes[i].GetDistanceAndDirection(vNVertex) > 0.0f)		
		{ 
			po_LastDecisivePlane = i;
			return Intersection::Outside; 
		}
		GetPositiveFarthestPoint(p_rxBox, m_axPlanes[i].m_vNormal, vPVertex);
		bIntersects |= ((m_axPlanes[i].GetDistanceAndDirection(vPVertex) > 0.0f));
	}

	return bIntersects ? Intersection::Intersects : Intersection::Inside;
}
//-------------------------------------------------------------------------------------------------------------------------------------------

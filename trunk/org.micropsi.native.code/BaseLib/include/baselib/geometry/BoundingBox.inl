//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CBoundingBox::PointIsInside(CVec3 p_vPoint) const
{
    CMat4S mBoxTransform(
        m_vEdge1.x(),   m_vEdge1.y(),   m_vEdge1.z(),   0,
        m_vEdge2.x(),   m_vEdge2.y(),   m_vEdge2.z(),   0,
        m_vEdge3.x(),   m_vEdge3.y(),   m_vEdge3.z(),   0,
        m_vBase.x(),    m_vBase.y(),    m_vBase.z(),    1);

    mBoxTransform.Invert();

    p_vPoint = p_vPoint ^ mBoxTransform;

    return ((p_vPoint.x() >= 0) && (p_vPoint.x() <= 1) && 
            (p_vPoint.y() >= 0) && (p_vPoint.y() <= 1) && 
            (p_vPoint.z() >= 0) && (p_vPoint.z() <= 1));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CBoundingBox::Overlaps(const CRay& p_rxRay) const
{
    CMat4S mBoxTransform(
        m_vEdge1.x(),   m_vEdge1.y(),   m_vEdge1.z(),   0,
        m_vEdge2.x(),   m_vEdge2.y(),   m_vEdge2.z(),   0,
        m_vEdge3.x(),   m_vEdge3.y(),   m_vEdge3.z(),   0,
        m_vBase.x(),    m_vBase.y(),    m_vBase.z(),    1);

    mBoxTransform.Invert();

    CVec3 vRayBase = p_rxRay.m_vBase ^ mBoxTransform;
    CVec3 vRayDir = (p_rxRay.m_vDirection.GetExtended(0) * mBoxTransform).GetReduced();

    // jetzt testen, ob der Ray die AA-Box zwischen (0,0,0) und (1,1,1) schneidet
    // es reicht aus, die faces zu testen, deren Normale in Richtung RayDir zeigt.

    if (vRayDir.x() != 0)
    {
        if (vRayDir.x() > 0) 
        {
            vRayBase.x() = 1.0f - vRayBase.x();
            vRayDir.x() = -vRayDir.x();
        }

        CVec3 vIntersection = vRayBase - vRayDir * (vRayBase.x() / vRayDir.x());
        assert(vIntersection.x() == 0);
        if ((vIntersection.y() >= 0) && (vIntersection.y() <= 1) &&
            (vIntersection.z() >= 0) && (vIntersection.z() <= 1))
        {
            return true;
        }
    }

    if (vRayDir.y() != 0)
    {
        if (vRayDir.y() > 0) 
        {
            vRayBase.y() = 1.0f - vRayBase.y();
            vRayDir.y() = -vRayDir.y();
        }

        CVec3 vIntersection = vRayBase - vRayDir * (vRayBase.y() / vRayDir.y());
        assert(vIntersection.y() == 0);
        if ((vIntersection.x() >= 0) && (vIntersection.x() <= 1) &&
            (vIntersection.z() >= 0) && (vIntersection.z() <= 1))
        {
            return true;
        }
    }

    if (vRayDir.z() != 0)
    {
        if (vRayDir.z() > 0) 
        {
            vRayBase.z() = 1.0f - vRayBase.z();
            vRayDir.z() = -vRayDir.z();
        }

        CVec3 vIntersection = vRayBase - vRayDir * (vRayBase.z() / vRayDir.z());
        assert(vIntersection.z() == 0);
        if ((vIntersection.x() >= 0) && (vIntersection.x() <= 1) &&
            (vIntersection.y() >= 0) && (vIntersection.y() <= 1))
        {
            return true;
        }
    }

    return false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CAxisAlignedBoundingBox::Clear()
{
    m_vMin.CVector3::CVector3(+FLT_MAX, +FLT_MAX, +FLT_MAX);
    m_vMax.CVector3::CVector3(-FLT_MAX, -FLT_MAX, -FLT_MAX);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CAxisAlignedBoundingBox::AddPoint(const CVec3& p_rvPoint)
{
    if (m_vMin.x() > p_rvPoint.x()) m_vMin.x() = p_rvPoint.x();
    if (m_vMax.x() < p_rvPoint.x()) m_vMax.x() = p_rvPoint.x();
    if (m_vMin.y() > p_rvPoint.y()) m_vMin.y() = p_rvPoint.y();
    if (m_vMax.y() < p_rvPoint.y()) m_vMax.y() = p_rvPoint.y();
    if (m_vMin.z() > p_rvPoint.z()) m_vMin.z() = p_rvPoint.z();
    if (m_vMax.z() < p_rvPoint.z()) m_vMax.z() = p_rvPoint.z();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CAxisAlignedBoundingBox::AddBBox(const CAxisAlignedBoundingBox& p_rxAABBox)
{
    if (m_vMin.x() > p_rxAABBox.m_vMin.x()) m_vMin.x() = p_rxAABBox.m_vMin.x();
    if (m_vMin.y() > p_rxAABBox.m_vMin.y()) m_vMin.y() = p_rxAABBox.m_vMin.y();
    if (m_vMin.z() > p_rxAABBox.m_vMin.z()) m_vMin.z() = p_rxAABBox.m_vMin.z();

    if (m_vMax.x() < p_rxAABBox.m_vMax.x()) m_vMax.x() = p_rxAABBox.m_vMax.x();
    if (m_vMax.y() < p_rxAABBox.m_vMax.y()) m_vMax.y() = p_rxAABBox.m_vMax.y();
    if (m_vMax.z() < p_rxAABBox.m_vMax.z()) m_vMax.z() = p_rxAABBox.m_vMax.z();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CVec3
CAxisAlignedBoundingBox::GetCenter() const
{
	return (m_vMax + m_vMin) * 0.5f;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CVec3
CAxisAlignedBoundingBox::GetNearestCorner(const CVec3& p_rvReferencePoint) const
{
	CVec3 vCenter = GetCenter();
	CVec3 vResult;
	vResult.x() = p_rvReferencePoint.x() < vCenter.x() ? m_vMin.x() : m_vMax.x();
	vResult.y() = p_rvReferencePoint.y() < vCenter.y() ? m_vMin.y() : m_vMax.y();
	vResult.z() = p_rvReferencePoint.z() < vCenter.z() ? m_vMin.z() : m_vMax.z();
} 
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CVec3 
CAxisAlignedBoundingBox::GetSize() const
{
	return m_vMax - m_vMin;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CAxisAlignedBoundingBox::Translate(const CVec3& p_rvDistance)
{
	m_vMin += p_rvDistance;
	m_vMax += p_rvDistance;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CAxisAlignedBoundingBox 
CAxisAlignedBoundingBox::GetTranslated(const CVec3& p_rvDistance) const
{
	CAxisAlignedBoundingBox b = *this;
	b.Translate(p_rvDistance);
	return b;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CAxisAlignedBoundingBox::PointIsInside(const CVec3& p_rvPoint) const
{
    return ((p_rvPoint.x() >= m_vMin.x()) && (p_rvPoint.x() <= m_vMax.x()) &&
            (p_rvPoint.y() >= m_vMin.y()) && (p_rvPoint.y() <= m_vMax.y()) &&
            (p_rvPoint.z() >= m_vMin.z()) && (p_rvPoint.z() <= m_vMax.z()));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CAxisAlignedBoundingBox::PointIsInside(const CVec3& p_rvPoint, float p_fTolerance) const
{
    return ((p_rvPoint.x() >= m_vMin.x() - p_fTolerance) && (p_rvPoint.x() <= m_vMax.x() + p_fTolerance) &&
            (p_rvPoint.y() >= m_vMin.y() - p_fTolerance) && (p_rvPoint.y() <= m_vMax.y() + p_fTolerance) &&
            (p_rvPoint.z() >= m_vMin.z() - p_fTolerance) && (p_rvPoint.z() <= m_vMax.z() + p_fTolerance));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CAxisAlignedBoundingBox::Overlaps(const CAxisAlignedBoundingBox& p_rxAABBox) const
{
    return ((m_vMax.x() >= p_rxAABBox.m_vMin.x()) && (p_rxAABBox.m_vMax.x() >= m_vMin.x()) &&
            (m_vMax.z() >= p_rxAABBox.m_vMin.z()) && (p_rxAABBox.m_vMax.z() >= m_vMin.z()) &&
            (m_vMax.y() >= p_rxAABBox.m_vMin.y()) && (p_rxAABBox.m_vMax.y() >= m_vMin.y()));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CAxisAlignedBoundingBox::Contains(const CAxisAlignedBoundingBox& p_xrOther) const
{
	return 	(p_xrOther.m_vMin.z() >= m_vMin.z())  &&  
			(p_xrOther.m_vMax.z() <= m_vMax.z())  &&
			(p_xrOther.m_vMin.y() >= m_vMin.y())  &&  
			(p_xrOther.m_vMax.y() <= m_vMax.y())  &&
			(p_xrOther.m_vMin.x() >= m_vMin.x())  &&  
			(p_xrOther.m_vMax.x() <= m_vMax.x());
}	
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CAxisAlignedBoundingBox::Overlaps(const CRay& p_rxRay) const
{
    float fXScale = 1.0f / (m_vMax.x() - m_vMin.x());
    float fYScale = 1.0f / (m_vMax.y() - m_vMin.y());
    float fZScale = 1.0f / (m_vMax.z() - m_vMin.z());

    CVec3 vRayBase( (p_rxRay.m_vBase.x() - m_vMin.x()) * fXScale,
                    (p_rxRay.m_vBase.y() - m_vMin.y()) * fYScale,
                    (p_rxRay.m_vBase.z() - m_vMin.z()) * fZScale);

    CVec3 vRayDir(  p_rxRay.m_vDirection.x() * fXScale,
                    p_rxRay.m_vDirection.y() * fYScale,
                    p_rxRay.m_vDirection.z() * fZScale);

    // jetzt testen, ob der Ray die AA-Box zwischen (0,0,0) und (1,1,1) schneidet
    // es reicht aus, die faces zu testen, deren Normale in Richtung RayDir zeigt.

    if (vRayDir.x() != 0)
    {
        if (vRayDir.x() > 0) 
        {
            vRayBase.x() = 1.0f - vRayBase.x();
            vRayDir.x() = -vRayDir.x();
        }

        CVec3 vIntersection = vRayBase - vRayDir * (vRayBase.x() / vRayDir.x());
//        assert(vIntersection.x() == 0);	// wird leider nicht genau 0 :( - David
        if ((vIntersection.y() >= 0) && (vIntersection.y() <= 1) &&
            (vIntersection.z() >= 0) && (vIntersection.z() <= 1))
        {
            return true;
        }
    }

    if (vRayDir.y() != 0)
    {
        if (vRayDir.y() > 0) 
        {
            vRayBase.y() = 1.0f - vRayBase.y();
            vRayDir.y() = -vRayDir.y();
        }

        CVec3 vIntersection = vRayBase - vRayDir * (vRayBase.y() / vRayDir.y());
//        assert(vIntersection.y() == 0);
        if ((vIntersection.x() >= 0) && (vIntersection.x() <= 1) &&
            (vIntersection.z() >= 0) && (vIntersection.z() <= 1))
        {
            return true;
        }
    }

    if (vRayDir.z() != 0)
    {
        if (vRayDir.z() > 0) 
        {
            vRayBase.z() = 1.0f - vRayBase.z();
            vRayDir.z() = -vRayDir.z();
        }

        CVec3 vIntersection = vRayBase - vRayDir * (vRayBase.z() / vRayDir.z());
//        assert(vIntersection.z() == 0);
        if ((vIntersection.x() >= 0) && (vIntersection.x() <= 1) &&
            (vIntersection.y() >= 0) && (vIntersection.y() <= 1))
        {
            return true;
        }
    }

    return false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CAxisAlignedBoundingBox::Contains(const CBoundingSphere& p_xrSphere) const
{
	return (p_xrSphere.m_vCenter.x() - m_vMin.x() >= p_xrSphere.m_fRadius)	&&
		   (m_vMax.x() - p_xrSphere.m_vCenter.x() >= p_xrSphere.m_fRadius)  &&
		   (p_xrSphere.m_vCenter.y() - m_vMin.y() >= p_xrSphere.m_fRadius)	&&
		   (m_vMax.y() - p_xrSphere.m_vCenter.y() >= p_xrSphere.m_fRadius)  &&
	       (p_xrSphere.m_vCenter.z() - m_vMin.z() >= p_xrSphere.m_fRadius)	&&
		   (m_vMax.z() - p_xrSphere.m_vCenter.z() >= p_xrSphere.m_fRadius);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CAxisAlignedBoundingBox::Overlaps(const CBoundingSphere& p_rxSphere) const
{
	// Arvo's algorithm:

	float fSquareDistance = 0;		// Quadrat der Entfernung Kugelmittelpunkt <--> Box
	float fTmpDist;

	fTmpDist = p_rxSphere.m_vCenter.x() - m_vMin.x();
	if(fTmpDist < 0.0f)	 { fSquareDistance += fTmpDist * fTmpDist; }
	fTmpDist = p_rxSphere.m_vCenter.x() - m_vMax.x();
	if(fTmpDist > 0.0f)	 { fSquareDistance += fTmpDist * fTmpDist; }
	
	fTmpDist = p_rxSphere.m_vCenter.y() - m_vMin.y();
	if(fTmpDist < 0.0f)	 { fSquareDistance += fTmpDist * fTmpDist; }
	fTmpDist = p_rxSphere.m_vCenter.y() - m_vMax.y();
	if(fTmpDist > 0.0f)	 { fSquareDistance += fTmpDist * fTmpDist; }

	fTmpDist = p_rxSphere.m_vCenter.z() - m_vMin.z();
	if(fTmpDist < 0.0f)	 { fSquareDistance += fTmpDist * fTmpDist; }
	fTmpDist = p_rxSphere.m_vCenter.z() - m_vMax.z();
	if(fTmpDist > 0.0f)	 { fSquareDistance += fTmpDist * fTmpDist; }

	return fSquareDistance <= p_rxSphere.m_fRadius * p_rxSphere.m_fRadius;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

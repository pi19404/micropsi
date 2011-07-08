//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CBoundingSphere::Clear()
{
    m_fRadius = 0;
    m_vCenter.CVector3::CVector3(FLT_MAX, FLT_MAX, FLT_MAX);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CBoundingSphere::AddPoint(const CVec3& p_rvPoint)
{
    float fDistanceSqr = (m_vCenter - p_rvPoint).AbsSquare();

    if (fDistanceSqr > m_fRadius * m_fRadius)
    {
        if (m_vCenter.x() == FLT_MAX)
        {
            // sphere war vorher leer
            m_vCenter = p_rvPoint;
            m_fRadius = 0;
        }
        else
        {
            //*
            float fDistance = sqrtf(fDistanceSqr);
            m_vCenter = (p_rvPoint * (fDistance - m_fRadius) +
                         m_vCenter * (fDistance + m_fRadius)) / (2 * fDistance);
            m_fRadius = (m_fRadius + fDistance) * 0.5f;
            /*/
            m_fRadius = sqrtf(fDistanceSqr);
            /**/
        }
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CBoundingSphere::AddSphere(const CBoundingSphere& p_rvSphere)
{
    if (m_vCenter.x() == FLT_MAX)
    {
        // sphere war vorher leer
		m_vCenter = p_rvSphere.m_vCenter;
		m_fRadius = p_rvSphere.m_fRadius;
		return;
    }

	CVec3 vDir = (p_rvSphere.m_vCenter - m_vCenter);
	float fDist = vDir.Abs();
	if(fDist + p_rvSphere.m_fRadius <= m_fRadius)
	{
		return; // completely contained
	}

	float fDiameter = fDist + m_fRadius + p_rvSphere.m_fRadius;
	float fNewRadius = fDiameter * 0.5f;
	m_vCenter = m_vCenter + (vDir.GetNormalized() * (fNewRadius - m_fRadius));
	m_fRadius = fNewRadius;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CBoundingSphere::PointIsInside(const CVec3& p_rvPoint) const
{
    return (m_vCenter - p_rvPoint).AbsSquare() < m_fRadius * m_fRadius;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CBoundingSphere::PointIsInside(const CVec3& p_rvPoint, float p_fTolerance) const
{
    return (m_vCenter - p_rvPoint).AbsSquare() <=
        (m_fRadius + p_fTolerance) * (m_fRadius + p_fTolerance);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CBoundingSphere::Contains(const CBoundingSphere& p_xrSphere) const
{
	if(p_xrSphere.m_fRadius < m_fRadius)
	{
		return false;
	}
	else
	{
		return (m_vCenter - p_xrSphere.m_vCenter).AbsSquare() <= (m_fRadius - p_xrSphere.m_fRadius) * (m_fRadius - p_xrSphere.m_fRadius);
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CBoundingSphere::Overlaps(const CBoundingSphere& p_rxBSphere) const
{
    return (m_vCenter - p_rxBSphere.m_vCenter).AbsSquare() <
        (m_fRadius + p_rxBSphere.m_fRadius) * (m_fRadius + p_rxBSphere.m_fRadius);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
bool 
CBoundingSphere::Overlaps(const CRay& p_rxRay) const
{
    CVec3 vDir = p_rxRay.m_vDirection.GetNormalized();
	CVec3 vBaseDiff = m_vCenter - p_rxRay.m_vBase;
    CVec3 vLineToCenter = vBaseDiff - vDir * (vBaseDiff * vDir);

    return (vLineToCenter.AbsSquare() < m_fRadius * m_fRadius);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

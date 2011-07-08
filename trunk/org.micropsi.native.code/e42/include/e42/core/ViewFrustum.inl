//---------------------------------------------------------------------------------------------------------------------
inline
void 
CViewFrustum::SetNearPlaneDistance(float p_fDistance)
{
	CVec3 vPoint = m_vEyePos + (m_axPlanes[Far].m_vNormal * p_fDistance);
	m_axPlanes[Near].m_fOffset = vPoint * m_axPlanes[Near].m_vNormal;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CViewFrustum::SetFarPlaneDistance(float p_fDistance)
{
	CVec3 vPoint = m_vEyePos + (m_axPlanes[Far].m_vNormal * p_fDistance);
	m_axPlanes[Far].m_fOffset = vPoint * m_axPlanes[Far].m_vNormal;
}
//---------------------------------------------------------------------------------------------------------------------
inline
float 
CViewFrustum::GetNearPlaneDistance() const
{
	return 	m_axPlanes[Near].GetAbsoluteDistance(m_vEyePos);
}
//---------------------------------------------------------------------------------------------------------------------
inline
float 
CViewFrustum::GetFarPlaneDistance() const
{
	return 	m_axPlanes[Far].GetAbsoluteDistance(m_vEyePos);
}
//---------------------------------------------------------------------------------------------------------------------

inline
bool 
CViewFrustum::SphereIntersects(const CBoundingSphere& rxBoundingSphere) const
{
 	for (int iPlaneIdx = 0; iPlaneIdx < Planes::NumPlanes; ++iPlaneIdx)
	{
        if (rxBoundingSphere.m_vCenter * m_axPlanes[iPlaneIdx].m_vNormal - rxBoundingSphere.m_fRadius > m_axPlanes[iPlaneIdx].m_fOffset)
		{
			return false;
		}
	}
	return true;
}
//---------------------------------------------------------------------------------------------------------------------

inline
bool 
CViewFrustum::SphereIsInside(const CBoundingSphere& rxBoundingSphere) const
{
 	for (int iPlaneIdx = 0; iPlaneIdx < Planes::NumPlanes; ++iPlaneIdx)
	{
		if (m_axPlanes[iPlaneIdx].GetDistanceAndDirection(rxBoundingSphere.m_vCenter) < rxBoundingSphere.m_fRadius)
		{
			return false;
		}
	}
	return true;
}
//---------------------------------------------------------------------------------------------------------------------

inline
bool 
CViewFrustum::PointIsInside(const CVec3& vPoint) const
{
 	for (int iPlaneIdx = 0; iPlaneIdx < Planes::NumPlanes; ++iPlaneIdx)
	{
		if (m_axPlanes[iPlaneIdx].GetDistanceAndDirection(vPoint) > 0.0f)
		{
			return false;
		}
	}
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CViewFrustum::GetPositiveFarthestPoint(const CAxisAlignedBoundingBox& p_rxAABB, const CVec3& p_rvNormal, CVec3& po_rvPosFarPoint)
{		
	po_rvPosFarPoint = p_rxAABB.m_vMin;
	if (p_rvNormal.x() > 0.0f) { po_rvPosFarPoint.x() = p_rxAABB.m_vMax.x(); }
	if (p_rvNormal.y() > 0.0f) { po_rvPosFarPoint.y() = p_rxAABB.m_vMax.y(); }
	if (p_rvNormal.z() > 0.0f) { po_rvPosFarPoint.z() = p_rxAABB.m_vMax.z(); }
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CViewFrustum::GetNegativeFarthestPoint(const CAxisAlignedBoundingBox& p_rxAABB, const CVec3& p_rvNormal, CVec3& po_vrNegFarPoint)
{		
	po_vrNegFarPoint = p_rxAABB.m_vMin;
	if (p_rvNormal.x() < 0.0f) { po_vrNegFarPoint.x() = p_rxAABB.m_vMax.x(); }
	if (p_rvNormal.y() < 0.0f) { po_vrNegFarPoint.y() = p_rxAABB.m_vMax.y(); }
	if (p_rvNormal.z() < 0.0f) { po_vrNegFarPoint.z() = p_rxAABB.m_vMax.z(); }
}
//---------------------------------------------------------------------------------------------------------------------
inline
CVec3 
CViewFrustum::GetEyePoint() const
{
	return m_vEyePos;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool	
CViewFrustum::operator==(const CViewFrustum& p_rxOther) const
{
	if(!(m_vEyePos == p_rxOther.m_vEyePos))
	{
		return false;
	}
 	for (int i = 0; i < Planes::NumPlanes; ++i)
	{
		if(m_axPlanes[i] != p_rxOther.m_axPlanes[i])
		{
			return false;
		}
	}
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool	
CViewFrustum::operator!=(const CViewFrustum& p_rxOther) const
{
	return !operator==(p_rxOther);
}
//---------------------------------------------------------------------------------------------------------------------

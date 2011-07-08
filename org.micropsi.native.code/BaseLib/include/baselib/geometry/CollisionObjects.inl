//---------------------------------------------------------------------------------------------------------------------
inline
CCollisionObjectSphere::CCollisionObjectSphere(const CBoundingSphere& p_rxSphere)
{
	m_xSphere = p_rxSphere;
}

//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CCollisionObjectSphere::PointIsInside(const CVec3& p_rvPoint) const
{
	return m_xSphere.PointIsInside(p_rvPoint);
}

//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CCollisionObjectSphere::Overlaps(const CBoundingSphere& p_rxSphere) const
{
	return m_xSphere.Overlaps(p_rxSphere);
}

//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CCollisionObjectSphere::Contains(const CBoundingSphere& p_xrSphere) const
{
	return m_xSphere.Contains(p_xrSphere);
}

//---------------------------------------------------------------------------------------------------------------------
inline
CCollisionObjectAABox::CCollisionObjectAABox(const CAxisAlignedBoundingBox& p_rxAABB)
{
	m_xAABB = p_rxAABB;
}

//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CCollisionObjectAABox::PointIsInside(const CVec3& p_rvPoint) const
{
	return m_xAABB.PointIsInside(p_rvPoint);
}

//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CCollisionObjectAABox::Overlaps(const CBoundingSphere& p_rxSphere) const
{
	return m_xAABB.Overlaps(p_rxSphere);
}

//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CCollisionObjectAABox::Contains(const CBoundingSphere& p_rxSphere) const
{
	return m_xAABB.Contains(p_rxSphere);
}

//---------------------------------------------------------------------------------------------------------------------

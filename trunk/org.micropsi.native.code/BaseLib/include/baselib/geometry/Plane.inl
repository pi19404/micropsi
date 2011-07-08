//---------------------------------------------------------------------------------------------------------------------
inline
CPlane::CPlane()
{
	m_fOffset = 0.0f;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CPlane::CPlane(float fA, float fB, float fC, float fD)
{
	m_vNormal = CVec3(fA, fB, fC);
	m_fOffset = fD;
	Normalize();
}
//---------------------------------------------------------------------------------------------------------------------
inline
CPlane::CPlane(const CVec3& rvNormal, float fDistance)
{
	m_vNormal = rvNormal;
	m_fOffset = fDistance;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CPlane::CPlane(const CVec3& rvP1, const CVec3& rvP2, const CVec3& rvP3)
{
	CVec3 xA = rvP2 - rvP1;
    CVec3 xB = rvP3 - rvP1;
    m_vNormal = (xA ^ xB).GetNormalized();
    m_fOffset = m_vNormal * rvP1;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CPlane::CPlane(const CVec3& rvNormal, const CVec3& rvPoint)
{
	m_vNormal = rvNormal;
	m_fOffset = rvNormal * rvPoint;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CPlane::CPlane(const CPlane& rxOther)
{
	m_vNormal = rxOther.m_vNormal;
	m_fOffset = rxOther.m_fOffset;
}
//---------------------------------------------------------------------------------------------------------------------
/// normalisiert die Ebenengleichung
inline
void 
CPlane::Normalize()
{	
	m_fOffset /= m_vNormal.Abs();
	m_vNormal.Normalize();	
}
//---------------------------------------------------------------------------------------------------------------------
inline
float 
CPlane::GetAbsoluteDistance(const CVec3& rvPoint) const
{
	return fabs(m_vNormal * rvPoint - m_fOffset);
}
//---------------------------------------------------------------------------------------------------------------------
inline
float 
CPlane::GetDistanceAndDirection(const CVec3& rvPoint) const
{
	return m_vNormal * rvPoint - m_fOffset;
}
//---------------------------------------------------------------------------------------------------------------------
inline
float	
CPlane::PointX(float fY, float fZ)
{
	return - (m_vNormal.y() * fY + m_vNormal.z() * fZ - m_fOffset) / m_vNormal.x();
} 
//---------------------------------------------------------------------------------------------------------------------
inline
float	
CPlane::PointY(float fX, float fZ)
{
	return - (m_vNormal.x() * fX + m_vNormal.z() * fZ - m_fOffset) / m_vNormal.y();
} 
//---------------------------------------------------------------------------------------------------------------------
inline
float
CPlane::PointZ(float fX, float fY)
{
	return - (m_vNormal.x() * fX + m_vNormal.y() * fY - m_fOffset) / m_vNormal.z();
} 
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CPlane::operator==(const CPlane& rxOther) const
{
	return (m_fOffset == rxOther.m_fOffset  &&  m_vNormal == rxOther.m_vNormal);
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CPlane::operator!=(const CPlane& rxOther) const
{
	return (m_fOffset != rxOther.m_fOffset  &&  m_vNormal != rxOther.m_vNormal);
}
//---------------------------------------------------------------------------------------------------------------------

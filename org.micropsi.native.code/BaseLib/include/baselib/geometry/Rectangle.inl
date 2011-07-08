//---------------------------------------------------------------------------------------------------------------------
inline
CAARectangle::CAARectangle()
{
}
//---------------------------------------------------------------------------------------------------------------------
inline
CAARectangle::CAARectangle(CVec2 p_vNWCorner, CVec2 p_vSECorner)
{
	m_vMin = p_vNWCorner;
	m_vMax = p_vSECorner;
	assert(m_vMin.x() <= m_vMax.x());
	assert(m_vMin.y() <= m_vMax.y());
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CAARectangle::Contains(const CVec2& p_xrPoint) const
{
	return  p_xrPoint.x() >= m_vMin.x()  &&
			p_xrPoint.x() <= m_vMax.x()  &&
			p_xrPoint.y() >= m_vMin.y()  &&
			p_xrPoint.y() <= m_vMax.y();
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CAARectangle::Contains(const CAARectangle& p_xrOther) const
{
	return 	(p_xrOther.m_vMin.y() >= m_vMin.y())  &&  
			(p_xrOther.m_vMax.y() <= m_vMax.y())  &&
			(p_xrOther.m_vMin.x() >= m_vMin.x())  &&  
			(p_xrOther.m_vMax.x() <= m_vMax.x());
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CAARectangle::Intersects(const CAARectangle& p_xrOther) const
{
	return (m_vMin.x() < p_xrOther.m_vMax.x() &&  
			m_vMax.x() > p_xrOther.m_vMin.x() &&
			m_vMin.y() < p_xrOther.m_vMax.y() &&  
			m_vMax.y() > p_xrOther.m_vMin.y());
}
//---------------------------------------------------------------------------------------------------------------------



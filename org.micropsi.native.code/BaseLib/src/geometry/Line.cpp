#include "stdafx.h"

#include "baselib/geometry/Line.h"

//---------------------------------------------------------------------------------------------------------------------
float 
CLine2::GetClockDirection(const CVec2& v01, const CVec2& v02) const
{
	return v01.y() * v02.x() - v01.x() * v02.y();
}
//---------------------------------------------------------------------------------------------------------------------
bool 
CLine2::CalcIntersection(const CLine2& rxLine, CVec2* pvIntersectionPointOut) const
{
	const CVec2 vDir = m_vEnd - m_vStart;
	const CVec2 vLineDir = rxLine.m_vEnd - rxLine.m_vStart;

	if (GetClockDirection(vDir, rxLine.m_vStart - m_vStart) *
		GetClockDirection(vDir, rxLine.m_vEnd - m_vStart) < 0 &&
		GetClockDirection(vLineDir, m_vStart - rxLine.m_vStart) *
		GetClockDirection(vLineDir, m_vEnd - rxLine.m_vStart) < 0)
	{
		if (pvIntersectionPointOut)
		{
			CVec2 vNrm(vDir.y(), -vDir.x());
			vNrm.Normalize();

			*pvIntersectionPointOut = 
				rxLine.m_vStart + vLineDir * ((m_vStart - rxLine.m_vStart) * vNrm) / (vLineDir * vNrm);

			//assert(fabsf(vDir.GetNormalized() * (*pvIntersectionPointOut - m_vStart).GetNormalized()) > 0.999f);
			//assert(fabsf(vLineDir.GetNormalized() * (*pvIntersectionPointOut - rxLine.m_vStart).GetNormalized()) > 0.999f);
		}

		return true;
	}

	return false;
}
//---------------------------------------------------------------------------------------------------------------------

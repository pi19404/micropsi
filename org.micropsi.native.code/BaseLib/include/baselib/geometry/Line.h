#pragma once

#ifndef ENIGNE_GEOMETRY_LINE_H_INCLUDED
#define ENIGNE_GEOMETRY_LINE_H_INCLUDED

#include "baselib/geometry/CVector.h"

template<typename VectorType>
class CLine
{
public:
	CLine();
	CLine(const VectorType& rvStart, const VectorType& rvEnd);

	float GetLength() const;
	VectorType GetDirection() const;

    VectorType	m_vStart;
    VectorType	m_vEnd;
};

class CLine2 : public CLine<CVec2>
{
private:
	float GetClockDirection(const CVec2& v01, const CVec2& v02) const;

public:
	CLine2() {};
	CLine2(const CVec2& rvStart, const CVec2& rvEnd) : CLine<CVec2>(rvStart, rvEnd) {};

	bool CalcIntersection(const CLine2& vLine, CVec2* pvIntersectionPointOut = NULL) const;
};

class CLine3 : public CLine<CVec3>
{
public:
	CLine3() {};
	CLine3(const CVec3& rvStart, const CVec3& rvEnd) : CLine<CVec3>(rvStart, rvEnd) {};
};

#include "baselib/geometry/Line.inl"

#endif // ENIGNE_GEOMETRY_LINE_H_INCLUDED

#pragma once
#ifndef E42_RECTANGLE_H_INCLUDED
#define E42_RECTANGLE_H_INCLUDED

#include "baselib/geometry/CVector.h"

class CAARectangle
{
public:
	CVec2	m_vMin;
	CVec2	m_vMax;

	CAARectangle();
	CAARectangle(CVec2 p_vMin, CVec2 p_vMax);

	bool Contains(const CVec2& p_xrPoint) const;
	bool Contains(const CAARectangle& p_xrOther) const;
	bool Intersects(const CAARectangle& p_xrOther) const;
};

#include "baselib/geometry/Rectangle.inl"

#endif // #ifndef E42_RECTANGLE_H_INCLUDED



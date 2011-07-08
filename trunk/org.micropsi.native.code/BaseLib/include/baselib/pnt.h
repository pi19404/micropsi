// pnt.h
//
// author: David.Salz@snafu.de
// created: April 11, 2004


#ifndef BASELIB_PNT_H_INCLUDED
#define BASELIB_PNT_H_INCLUDED


/*
	Eine einfache Klasse für Punkte; kompatibel zu Windows-POINT-Strukur
*/
class CPnt
{
public:
	int x, y;

	/// default ctor
	CPnt()											{ x=y=0; }

	/// ctor with args
	CPnt(int p_iX, int p_iY)						{ x=p_iX; y=p_iY; }

	/// comparision
	bool operator!=(const CPnt& p_krxOther)			{ return (p_krxOther.x!=x) || (p_krxOther.y!=y); }

	/// comparision
	bool operator==(const CPnt& p_krxOther)			{ return (p_krxOther.x==x) && (p_krxOther.y==y); }

	/// add
	CPnt operator+(const CPnt& p_krxOther) const	{ return CPnt(x+p_krxOther.x, y+p_krxOther.y); }

	/// add
	CPnt operator+(int p_iVal) const				{ return CPnt(x+p_iVal, y+p_iVal); }

	/// add
	CPnt& operator+=(const CPnt& p_krxOther)		{ x+=p_krxOther.x; y+=p_krxOther.y; return *this; }

	/// add
	CPnt& operator+=(int p_iVal)					{ x+=p_iVal; y+=p_iVal; return *this; }

	/// subtract
	CPnt operator-(const CPnt& p_krxOther) const	{ return CPnt(x-p_krxOther.x, y-p_krxOther.y); }

	/// subtract
	CPnt operator-(int p_iVal) const				{ return CPnt(x-p_iVal, y-p_iVal); }

	/// subtract
	CPnt& operator-=(const CPnt& p_krxOther)		{ x-=p_krxOther.x; y-=p_krxOther.y; return *this; }

	/// subtract
	CPnt& operator-=(int p_iVal)					{ x-=p_iVal; y-=p_iVal; return *this; }

	/// assign
	CPnt& operator=(const CPnt& p_krxOther)			{ x=p_krxOther.x; y=p_krxOther.y; return *this; }
};

#endif	// BASELIB_PNT_H_INCLUDED


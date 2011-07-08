// size.h
//
// author: David.Salz@snafu.de
// created: April 11, 2004


#ifndef SIZE_H_INCLUDED
#define SIZE_H_INCLUDED

class CSize
{
public:
	int cx, cy;

	/// default ctor
	CSize()												{ cx=cy=0; }

	/// ctor
	CSize(int p_iCX, int p_iCY)							{ cx=p_iCX; cy=p_iCY; }

	/// addition
	CSize operator+(const CSize&p_krxOther)				{ return CSize(cx + p_krxOther.cx, cy + p_krxOther.cy); }

	/// addition
	CSize operator*(int p_iFactor)						{ return CSize(cx * p_iFactor, cy * p_iFactor); }

	/// comparision
	bool operator!=(const CSize& p_krxOther) const		{ return ((p_krxOther.cx!=cx) || (p_krxOther.cy!=cy)); }

	/// comparision
	bool operator==(const CSize& p_krxOther) const		{ return ((p_krxOther.cx==cx) && (p_krxOther.cy==cy)); }

};

#endif	// SIZE_H_INCLUDED


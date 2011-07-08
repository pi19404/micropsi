// common.h
//
// author: David.Salz@snafu.de
// created: November 12, 2003 

#ifndef BASELIB_MACROS_H_INCLUDED
#define BASELIB_MACROS_H_INCLUDED

#define max(a,b)            (((a) > (b)) ? (a) : (b))
#define min(a,b)			(((a) < (b)) ? (a) : (b))
#define clamp(value, minvalue, maxvalue)	((value) < (minvalue) ? (minvalue) : ((value) > (maxvalue) ? (maxvalue) : (value)))


template<class CObj> void Swap(CObj& p_xrObj1, CObj& p_xrObj2)
{
	CObj xTemp = p_xrObj1;
	p_xrObj1 = p_xrObj2;
	p_xrObj2 = xTemp;
}

#endif	// BASELIB_MACROS_H_INCLUDED


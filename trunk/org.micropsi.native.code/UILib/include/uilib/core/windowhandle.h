#ifndef UILIB_WINDOWHANDLE_H_INCLUDED
#define UILIB_WINDOWHANDLE_H_INCLUDED

#include "baselib/handledset.h"

namespace UILib
{

class CWindow;

/*
	UILib Window Handle. 
*/
class WHDL
{
private: 

	unsigned long m_iData;

public:
	WHDL()										{ m_iData = 0; }
	WHDL(const WHDL& p_rxHwnd)					{ m_iData = p_rxHwnd.m_iData; }
	WHDL(unsigned long p_iData)					{ m_iData=p_iData; }
	
	static WHDL	InvalidHandle()					{ return WHDL(); }

	operator unsigned long() const				{ return m_iData; }
	unsigned long GetAsInt() const				{ return m_iData; }

	WHDL&	operator=(unsigned long p_iData)	{ m_iData = p_iData; return *this; }

	bool	operator==(int p_iData) const		{ return (m_iData == (unsigned long) p_iData);}
	bool	operator==(WHDL p_xHwnd) const		{ return (m_iData == p_xHwnd.m_iData); }
	bool	operator!=(int p_iData) const		{ return (m_iData != (unsigned long) p_iData); }
	bool	operator!=(WHDL p_xHwnd) const		{ return (m_iData != p_xHwnd.m_iData); }
};

} // namespace UILib

#endif // ifndef UILIB_WINDOWHANDLE_H_INCLUDED


#include "stdafx.h"
#include "uilib/core/timer.h"
#include "windows.h"

namespace UILib
{

__int64 CTimer::m_iTimeOverride = -1;

//---------------------------------------------------------------------------------------------------------------------
__int64	
CTimer::GetSystemTimeInMS()
{
	if(m_iTimeOverride >= 0)
	{
		return m_iTimeOverride;
	}

	static __int64 g_iFreq = 0;
	if(g_iFreq == 0)
	{
		::QueryPerformanceFrequency((LARGE_INTEGER*) &g_iFreq);
		assert(g_iFreq);
		g_iFreq /= 1000;
		assert(g_iFreq);
	}

	__int64 iTime;
	::QueryPerformanceCounter((LARGE_INTEGER*) &iTime);
	return iTime / g_iFreq;

}


//---------------------------------------------------------------------------------------------------------------------
float	
CTimer::GetSystemTimeInS()
{
	return (float) ((double) GetSystemTimeInMS() / 1000.0);
}

//---------------------------------------------------------------------------------------------------------------------
void		
CTimer::OverrideSystemTimeInMS(__int64 p_iTimeOverride)
{
	m_iTimeOverride = p_iTimeOverride;
}

//---------------------------------------------------------------------------------------------------------------------
bool 
TimerPtrLess(const CTimer* p_pxTimer1, const CTimer* p_pxTimer2)
{
	bool b = p_pxTimer1->m_iActivationTime > p_pxTimer2->m_iActivationTime;
	return b;
}

//---------------------------------------------------------------------------------------------------------------------


} // namespace UILib


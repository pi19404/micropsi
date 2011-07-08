#ifndef UILIB_TIMER_H_INCLUDED
#define UILIB_TIMER_H_INCLUDED

#include "windowhandle.h"
#include "baselib/handledset.h"

namespace UILib
{

//Timerstruktur
class CTimer
{
public:
	CTimer(WHDL h, int id, __int64 p_iInterval, bool p_bRepeating) : m_hWindow(h), m_iID(id)
	{
		Reset(p_iInterval, p_bRepeating);
	}

	void		Reset(__int64 p_iInterval, bool p_bRepeating)
	{
		m_iActivationTime	= GetSystemTimeInMS() + p_iInterval;
		m_iInterval			= p_bRepeating ? p_iInterval : -1;
	}

	///< setzt die aktuelle Zeit; wird nicht automatisch weitergezählt, d.h. muss man immer wieder rufen und eigenes Tempo vorgeben
	static void		OverrideSystemTimeInMS(__int64 p_iTimeOverride);

	static __int64	GetSystemTimeInMS(); 
	static float	GetSystemTimeInS();


	WHDL		m_hWindow;				///< Fenster, dem dieser Timer gehört
	int			m_iID;					///< Timer id (= Handle aus m_apxTimers)
	__int64		m_iActivationTime;		///< Zeitpunkt der nächsten Aktivierung
	__int64		m_iInterval;			///< Intervall; <= 0 falls dies ein einmaliger Timer ist

	static unsigned long InvalidHandle()
	{
		return CHandledSet<CTimer*>::InvalidHandle();
	}

private:

	static __int64	m_iTimeOverride;		///< Zeit, die anstelle der Systemzeit zurückgeliefert wird
};

bool TimerPtrLess(const CTimer* p_pxTimer1, const CTimer* p_pxTimer2);


} // namespace UILib

#endif // ifndef UILIB_TIMER_H_INCLUDED


#ifndef BASELIB_CRITICALSECTION_H_INCLUDED
#define BASELIB_CRITICALSECTION_H_INCLUDED

#include <windows.h>

/** 
	Wrapper for Win32 Critical Sections
*/
class CCriticalSection
{
public:

	CCriticalSection();
	~CCriticalSection();

	void		Enter();
	void		Leave();

#if(_WIN32_WINNT >= 0x0400)
	bool		TryEnter();
#endif // #if(_WIN32_WINNT >= 0x0400)

private:

	CRITICAL_SECTION			m_xCriticalSection;
};

/** 
	Lock für eine Critical Section
*/
class CLock
{
public:

	CLock(CCriticalSection* p_pxCriticalSection);
	~CLock();

private:

	CCriticalSection*		m_pxCriticalSection;
};

#include "baselib/CriticalSection.inl"

#endif // ifndef BASELIB_CRITICALSECTION_H_INCLUDED
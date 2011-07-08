#include "stdafx.h"

#include <stdio.h>
#include <fstream>
#include "GameLib/Utilities/SystemUtils.h"

// für filetime:
#include <sys/types.h>
#include <sys/stat.h>
#include <io.h>
#include <fcntl.h>
#include <windows.h>

namespace Utils
{

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
#include <time.h>
__int64
GetFileLastWriteTimeStamp(const char* pcName)
{
#ifdef _MSC_VER
    struct _stati64 fileTime;
#else
    struct stati64 fileTime;
#endif

    int iResult = _stati64(pcName, &fileTime);
    assert(iResult == 0);

    return fileTime.st_mtime;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
double 
GetSeconds()
{
    static __int64 s_i64CounterLast = 0;
    static double s_dTime = 0;

    if (s_i64CounterLast == 0)
    {
    	QueryPerformanceCounter((LARGE_INTEGER*)&s_i64CounterLast);
    }

	__int64 i64Counter;
    BOOL b;
    b = QueryPerformanceCounter((LARGE_INTEGER*)&i64Counter);
    assert(b);

	__int64 i64CounterFrq;
	b = QueryPerformanceFrequency((LARGE_INTEGER*)&i64CounterFrq);
    assert(b);
	
    s_dTime += ((double)(i64Counter - s_i64CounterLast)) / ((double)i64CounterFrq);

    s_i64CounterLast = i64Counter;
    
    return s_dTime;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
double 
GetDeltaSeconds(double &d)
{
   if (d == 0)
   {
      d = GetSeconds();
      return 0;
   }
   
   double time = GetSeconds();
   double delta = time - d;
   d = time;

   return delta;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

} // namespace Utils
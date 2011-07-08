// debugprint.cpp
//
// author: David.Salz@snafu.de

#include "stdafx.h"
#include <stdio.h>
#include <stdarg.h>
#include <windows.h>


void DebugPrint(const char* pcFormat,...)
{
	va_list argList;
	va_start(argList, pcFormat);
	char cB[4096];
	int iMaxLen=0;
	iMaxLen=_vsnprintf(cB,4096,pcFormat,argList);
	va_end(argList);

	OutputDebugString(cB);
};
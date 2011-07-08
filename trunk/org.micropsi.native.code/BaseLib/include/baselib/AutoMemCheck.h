#pragma once

#ifndef AUTOMEMCHECK_H_INCLUDED
#define AUTOMEMCHECK_H_INCLUDED

#include <crtdbg.h>

class CAutoMemCheck
{
private:
    static int AllocHook( 
        int allocType, void *userData, size_t size, int blockType, 
        long requestNumber, const unsigned char *filename, int lineNumber);

    _CrtMemState m_xMemState;

public:
    CAutoMemCheck(int iBreakAllocID = -1, _CRT_ALLOC_HOOK fpAllocHoock = 0);
    ~CAutoMemCheck();
};

#endif // AUTOMEMCHECK_H_INCLUDED
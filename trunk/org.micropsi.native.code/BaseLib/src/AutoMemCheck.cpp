#include "stdafx.h"

#include "baselib/AutoMemCheck.h"

#include <assert.h>

//-------------------------------------------------------------------------------------------------------------------------------------------
int 
CAutoMemCheck::AllocHook( 
    int allocType, void *userData, size_t size, int blockType, 
    long requestNumber, const unsigned char *filename, int lineNumber)
{
    return true;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CAutoMemCheck::CAutoMemCheck(int iBreakAllocID, _CRT_ALLOC_HOOK fpAllocHoock)
{
    _CrtMemCheckpoint(&m_xMemState);

    if (iBreakAllocID != -1)
    {
        _CrtSetBreakAlloc(iBreakAllocID);
    }

    if (fpAllocHoock)
    {
        _CrtSetAllocHook(fpAllocHoock);
    }
    else
    {
        _CrtSetAllocHook(AllocHook);
    }
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CAutoMemCheck::~CAutoMemCheck()
{
    _CrtMemDumpAllObjectsSince(&m_xMemState);
    assert(_CrtCheckMemory());
}
//-------------------------------------------------------------------------------------------------------------------------------------------

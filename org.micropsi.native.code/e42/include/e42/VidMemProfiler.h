#pragma once

#ifndef E42_VIDMEMPROFILER_H_INCLUDED
#define E42_VIDMEMPROFILER_H_INCLUDED

#include "e42/stdinc.h"

struct IDirectDraw;
typedef struct _DDCAPS_DX7 DDCAPS_DX7;
typedef DDCAPS_DX7 DDCAPS;

class CVidMemProfiler
{
private:
	static IDirectDraw* ms_pDD;

    DDCAPS* m_pxDriverCaps;
    DDCAPS* m_pxHelCaps;

public:
    CVidMemProfiler();
    ~CVidMemProfiler();

    void Update();

    int GetTotalMemory();
    int GetFreeMemory();
    int GetUsedMemory();
    float GetUsageRatio();
};

#endif // E42_VIDMEMPROFILER_H_INCLUDED
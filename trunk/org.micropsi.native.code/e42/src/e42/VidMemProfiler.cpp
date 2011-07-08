#include "stdafx.h"

#include "e42/VidMemProfiler.h"

#include <ddraw.h>

IDirectDraw* CVidMemProfiler::ms_pDD = NULL;

//-----------------------------------------------------------------------------------------------------------------------------------------
CVidMemProfiler::CVidMemProfiler()
{
    if (ms_pDD == NULL)
    {
	    HRESULT hr;

        hr = DirectDrawCreate(NULL, &ms_pDD, NULL);
        assert(SUCCEEDED(hr));

        hr = ms_pDD->SetCooperativeLevel(NULL, DDSCL_NORMAL);
        assert(SUCCEEDED(hr));
    }
    else
    {
        ms_pDD->AddRef();
    }


    m_pxDriverCaps = new DDCAPS;
	ZeroMemory(m_pxDriverCaps, sizeof(DDCAPS));

    m_pxHelCaps = new DDCAPS;
	ZeroMemory(m_pxHelCaps, sizeof(DDCAPS));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CVidMemProfiler::~CVidMemProfiler()
{
    delete m_pxHelCaps;
    delete m_pxDriverCaps;

    if (ms_pDD->Release() == 0)
    {
        ms_pDD = NULL;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CVidMemProfiler::Update()
{
	ZeroMemory(m_pxDriverCaps, sizeof(DDCAPS));
	m_pxDriverCaps->dwSize = sizeof(DDCAPS);

	ZeroMemory(m_pxHelCaps, sizeof(DDCAPS));
	m_pxHelCaps->dwSize = sizeof(DDCAPS);

    ms_pDD->GetCaps(m_pxDriverCaps, m_pxHelCaps);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int 
CVidMemProfiler::GetTotalMemory()
{
    return m_pxDriverCaps->dwVidMemTotal;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int 
CVidMemProfiler::GetFreeMemory()
{
    return m_pxDriverCaps->dwVidMemFree;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int 
CVidMemProfiler::GetUsedMemory()
{
    return m_pxDriverCaps->dwVidMemTotal - m_pxDriverCaps->dwVidMemFree;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
float
CVidMemProfiler::GetUsageRatio()
{
    return (float)GetUsedMemory() / GetTotalMemory();
}
//-----------------------------------------------------------------------------------------------------------------------------------------

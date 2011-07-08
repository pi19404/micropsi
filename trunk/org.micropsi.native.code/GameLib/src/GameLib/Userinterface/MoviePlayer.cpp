#include "stdafx.h"
#include "GameLib/Userinterface/MoviePlayer.h"

#include <dshow.h>
#include "baselib/debugprint.h"
//#include "Utilities/Utils.h"

//-------------------------------------------------------------------------------------------------------------------------------------------
CMoviePlayer::CMoviePlayer()
{
	m_bPlaying = false;

    // Initialize the COM library.
    HRESULT hr = CoInitialize(NULL);
    if (FAILED(hr))
    {
        DebugPrint("ERROR - Could not initialize COM library");
        return;
    }
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CMoviePlayer::~CMoviePlayer()
{
    CoUninitialize();
}
//-------------------------------------------------------------------------------------------------------------------------------------------
/**
	\return		true if successfull; false otherwise (see logger for error message)
*/
bool 
CMoviePlayer::PlayFile(HWND hndWindow, const char* pcFilename)
{
    if (m_spxGraph)
    {
        Stop();
    }
    
    // Create the filter graph manager and query for interfaces.
    HRESULT hr = CoCreateInstance(CLSID_FilterGraph, NULL, CLSCTX_INPROC_SERVER, 
                        IID_IGraphBuilder, (void**)&m_spxGraph);
    if (FAILED(hr))
    {
        DebugPrint("ERROR - Could not create the Filter Graph Manager.");
        return false;
    }

	CComObjectPtr<IMediaControl> spxEventdiaControl;
	CComObjectPtr<IMediaEvent>   spxEvent;
	CComObjectPtr<IVideoWindow>  spxVideoWindow;

	wchar_t widePath[4096];
    mbstowcs(widePath, pcFilename, max(strlen(pcFilename) + 1, 4096));

    hr = m_spxGraph->RenderFile(widePath, NULL);
    

	// Create Control Interfaces
    hr = m_spxGraph->QueryInterface(IID_IMediaControl, (void**)&spxEventdiaControl);
    assert(SUCCEEDED(hr));

    hr = m_spxGraph->QueryInterface(IID_IMediaEvent, (void**)&spxEvent);
    assert(SUCCEEDED(hr));

    hr = m_spxGraph->QueryInterface(IID_IVideoWindow, (void**)&spxVideoWindow);
    assert(SUCCEEDED(hr));

	// make our application window the output window
	hr = spxVideoWindow->put_Owner((OAHWND)hndWindow);
    assert(SUCCEEDED(hr));

    hr = spxVideoWindow->put_WindowStyle(WS_CHILD | WS_CLIPSIBLINGS | WS_CLIPCHILDREN);
    assert(SUCCEEDED(hr));


	ShowWindow(hndWindow, SW_SHOWNORMAL);
    UpdateWindow(hndWindow);

	RECT rect;
    GetClientRect(hndWindow, &rect);
    hr = spxVideoWindow->SetWindowPosition(rect.left, rect.top, rect.right, rect.bottom);
    assert(SUCCEEDED(hr));

	// Run the graph.
    hr = spxEventdiaControl->Run();
    if (SUCCEEDED(hr))
    {
		m_bPlaying = true;
	}
    else
    {
        return false;
    }

    return true;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
bool 
CMoviePlayer::IsPlaying()
{
	if (!m_bPlaying)	
	{
		return false;
	}
	else
	{
		// check if video has ended

		CComObjectPtr<IMediaEvent>  spxEvent;
	    HRESULT hr = m_spxGraph->QueryInterface(IID_IMediaEvent, (void**)&spxEvent);

		if(SUCCEEDED(hr))
		{
			LONG evCode, evParam1, evParam2;
			while(SUCCEEDED(spxEvent->GetEvent(&evCode, (LONG_PTR *) &evParam1,
								(LONG_PTR *) &evParam2, 0)))
			{
				// Free memory associated with callback, since we're not using it
				hr = spxEvent->FreeEventParams(evCode, evParam1, evParam2);

				// If this is the end of the clip, reset to beginning
				if(EC_COMPLETE == evCode)
				{
					Stop();
				}
			}
		}

		return m_bPlaying;
	}
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CMoviePlayer::Stop()
{
	if(m_bPlaying)
	{
		m_bPlaying = false;

        CComObjectPtr<IMediaControl> spxEventdiaControl;
		HRESULT hr = m_spxGraph->QueryInterface(IID_IMediaControl, (void**)&spxEventdiaControl);
		if (SUCCEEDED(hr))
		{
			spxEventdiaControl->Stop();
            m_spxGraph = NULL;
        }
	}
}
//-------------------------------------------------------------------------------------------------------------------------------------------

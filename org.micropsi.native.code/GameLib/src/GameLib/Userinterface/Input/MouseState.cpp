#include "stdafx.h"
#include "GameLib/UserInterface/Input/MouseState.h"

#include <windows.h>
#include <dinput.h>

#include "e42/E42Application.h"


//-------------------------------------------------------------------------------------------------------------------------------------------
CMouseState::CMouseState(IDirectInputDevice8A* pDIDMouse)
:   m_pDIDMouse(pDIDMouse)
{
	DIDEVCAPS  DIMouseCaps; 
	HRESULT    hr; 

	DIMouseCaps.dwSize = sizeof(DIDEVCAPS); 
	hr = pDIDMouse->GetCapabilities(&DIMouseCaps);
	m_iNumAxes = DIMouseCaps.dwAxes;
	m_iNumButtons = DIMouseCaps.dwButtons;

	m_pxButtonStates = new CButtonState[m_iNumButtons];
    ZeroMemory(&m_MouseState, sizeof(m_MouseState));
}

//-------------------------------------------------------------------------------------------------------------------------------------------
CMouseState::~CMouseState()
{
	delete [] m_pxButtonStates;
}

//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CMouseState::RetrieveFromDevice()
{
    ZeroMemory(&m_MouseState, sizeof(m_MouseState));

    if ((m_pDIDMouse) && 
        (CE42Application::Get().WindowIsActive()))
    {
	    HRESULT     hr;

        // Poll the device to read the current state
        hr = m_pDIDMouse->Poll(); 
        if (FAILED(hr))  
        {
	        hr = m_pDIDMouse->Acquire();
	        while (hr == DIERR_INPUTLOST) 
            {
		        hr = m_pDIDMouse->Acquire();
            }

            if (SUCCEEDED(hr))  // evtl. DIERR_OTHERAPPHASPRIO
            {
	            hr = m_pDIDMouse->Poll(); 
                assert(SUCCEEDED(hr));
            }
        }

        if (SUCCEEDED(hr))      // das ist kein else zu oben!
        {
            hr = m_pDIDMouse->GetDeviceState(sizeof(DIMOUSESTATE2), &m_MouseState);
            assert(SUCCEEDED(hr));
        }

		for(int i=0; i<m_iNumButtons; ++i)
		{
			m_pxButtonStates[i].Update(IsButtonDown(i));
		}
	}
}
//-------------------------------------------------------------------------------------------------------------------------------------------
bool	
CMouseState::IsAnyButtonDown() const
{
	for (int i = 0; i < m_iNumButtons; ++i)
	{
		if (m_MouseState.rgbButtons[i] & 128)
		{
			return true;
		}
	}
	return false;
}
//-------------------------------------------------------------------------------------------------------------------------------------------

#include "stdafx.h"
#include "GameLib/Userinterface/Input/GamepadState.h"

#include <math.h>
#include "e42/E42Application.h"

//-------------------------------------------------------------------------------------------------------------------------------------------
CGamepadState::CGamepadState(IDirectInputDevice8A* pDIDGamepad)
:   m_pDIDGamepad(pDIDGamepad)
{
	m_iNumAxes = 0;
	m_iNumButtons = 0;
    m_fAxisThreshold = 0.03f;
	m_pxButtonStates = 0;
	m_pxAxes = 0;

    ZeroMemory(&m_JoystickState, sizeof(DIJOYSTATE2));

    if (m_pDIDGamepad)
    {
	    DIDEVCAPS diDevCaps;
	    diDevCaps.dwSize = sizeof(DIDEVCAPS);

	    HRESULT hr = m_pDIDGamepad->GetCapabilities(&diDevCaps);
        assert(SUCCEEDED(hr));

	    m_iNumButtons = diDevCaps.dwButtons;
	    m_iNumAxes = diDevCaps.dwAxes;

		m_pxAxes = new CAxis[8];

        hr = m_pDIDGamepad->EnumObjects(EnumObjectsCallback, m_pDIDGamepad, DIDFT_AXIS);
        assert(SUCCEEDED(hr));

		m_pxButtonStates = new CButtonState[m_iNumButtons];
    }
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CGamepadState::~CGamepadState()
{
	delete [] m_pxButtonStates;
	delete [] m_pxAxes;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CGamepadState::RetrieveFromDevice()
{
    ZeroMemory(&m_JoystickState, sizeof(m_JoystickState));

    if ((m_pDIDGamepad) && 
        (CE42Application::Get().WindowIsActive()))
    {
	    HRESULT     hr;

        // Poll the device to read the current state
        hr = m_pDIDGamepad->Poll(); 
        if (FAILED(hr))  
        {
	        hr = m_pDIDGamepad->Acquire();
	        while (hr == DIERR_INPUTLOST) 
            {
		        hr = m_pDIDGamepad->Acquire();
            }

            if (SUCCEEDED(hr))  // evtl. DIERR_OTHERAPPHASPRIO
            {
	            hr = m_pDIDGamepad->Poll(); 
                assert(SUCCEEDED(hr));
            }
        }

        if (SUCCEEDED(hr))      // das ist kein else zu oben!
        {
            hr = m_pDIDGamepad->GetDeviceState(sizeof(DIJOYSTATE2), &m_JoystickState);
            assert(SUCCEEDED(hr));
        }

		for (int i = 0; i < m_iNumButtons; ++i)
		{
			m_pxButtonStates[i].Update(IsButtonDown(i));
		}
	}
}
//-------------------------------------------------------------------------------------------------------------------------------------------
bool	
CGamepadState::IsAnyButtonDown() const
{
	for (int i = 0; i < m_iNumButtons; ++i)
	{
		if (m_JoystickState.rgbButtons[i] & 128)
		{
			return true;
		}
	}
	return false;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void	
CGamepadState::SetAxisMode(int p_iAxis, int p_iFlags)
{
	assert(p_iAxis >= 0  &&  p_iAxis < 8);
	m_pxAxes[p_iAxis].m_iFlags = p_iFlags;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
BOOL CALLBACK 
CGamepadState::EnumObjectsCallback(const DIDEVICEOBJECTINSTANCE* pdidoi, VOID* pContext)
{
    IDirectInputDevice8A* pDIDGamepad = (IDirectInputDevice8A*)pContext;

    if (pdidoi->dwType & DIDFT_AXIS)
    {
        DIPROPRANGE diprg; 
        diprg.diph.dwSize       = sizeof(DIPROPRANGE); 
        diprg.diph.dwHeaderSize = sizeof(DIPROPHEADER); 
        diprg.diph.dwHow        = DIPH_BYID; 
        diprg.diph.dwObj        = pdidoi->dwType;
        diprg.lMin              = -129;         // ergibt range ]-129; +128]
        diprg.lMax              = +128; 
    
        pDIDGamepad->SetProperty(DIPROP_RANGE, &diprg.diph);
    }

	return DIENUM_CONTINUE;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
float
CGamepadState::GetNormalizedAxisValue(int iAxis, LONG iValue) const
{
    if (!m_pDIDGamepad)
    {
		return 0;
	}

	// Workaround: solange eine Achse nicht zumindest einmal eine Wert != 0 geliefert hat, können wir den Werten nicht trauen
	// passiert bei diesen tollen Logitech-Lenkrädern, deren Pedale 0 liefern, bis man sie berührt und dann auf 128 springen
	if (m_pxAxes[iAxis].m_bHasLeftCenter == false)
	{
		if(iValue != 0)
		{
			m_pxAxes[iAxis].m_bHasLeftCenter = true;
		}
		else
		{
			return 0.0f;
		}
	}

	float fAxis;
	if (m_pxAxes[iAxis].m_iFlags & AM_UNSIGNED)
	{
		fAxis = 1.0f - (iValue + 128.0f) / 256.0f;
	}
	else
	{
		fAxis = iValue / 128.0f;
	}

	if (fabsf(fAxis) < m_fAxisThreshold) fAxis = 0;

	return fAxis;
}
//-------------------------------------------------------------------------------------------------------------------------------------------

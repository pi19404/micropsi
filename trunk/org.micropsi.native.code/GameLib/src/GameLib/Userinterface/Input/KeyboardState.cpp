#include "stdafx.h"
#include "GameLib/UserInterface/Input/KeyboardState.h"

#include <windows.h>
#include <dinput.h>

#include "e42/E42Application.h"


//-------------------------------------------------------------------------------------------------------------------------------------------
CKeyboardState::CKeyboardState(IDirectInputDevice8A* pDIDKeyboard)
:   m_pDIDKeyboard(pDIDKeyboard)
{
    ZeroMemory(&m_acKeyState1, sizeof(m_acKeyState1));
    ZeroMemory(&m_acKeyState1, sizeof(m_acKeyState2));
    ZeroMemory(&m_afKeyDownTime, sizeof(m_afKeyDownTime));

	m_pacCurrentKeyState = &m_acKeyState1;
    m_pacLastKeyState = &m_acKeyState2;	

    m_bScrollLock = false;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CKeyboardState::~CKeyboardState()
{
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void
CKeyboardState::UpdateKeyDownTimes(float fTimeDelta)
{
    for (int i = 0; i < NUM_KEYS; i++)
    {
        if (IsKeyDown(i))
        {
            m_afKeyDownTime[i] += fTimeDelta;
        }
        else
        {
            m_afKeyDownTime[i] = 0;
        }
    }
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CKeyboardState::RetrieveFromDevice(float fTimeDelta)
{
	CInlineArray<char, NUM_KEYS>*	m_pacSwap = m_pacLastKeyState;
	m_pacLastKeyState = m_pacCurrentKeyState;
	m_pacCurrentKeyState = m_pacSwap;

    ZeroMemory(m_pacCurrentKeyState, sizeof(m_acKeyState1));

    if (CE42Application::Get().WindowIsActive())
    {
        // Achtung: unbuffered Device, Tastendrücke zwischen den Calls können verlorengehen
        HRESULT hr = m_pDIDKeyboard->GetDeviceState(sizeof(m_acKeyState1), m_pacCurrentKeyState);

        if (FAILED(hr))  
        {
            ZeroMemory(m_pacCurrentKeyState, sizeof(m_acKeyState1));

	        hr = m_pDIDKeyboard->Acquire();
	        while (hr == DIERR_INPUTLOST) 
            {
		        hr = m_pDIDKeyboard->Acquire();
            }

            if (SUCCEEDED(hr))  // evtl. DIERR_OTHERAPPHASPRIO
            {
                HRESULT hr = m_pDIDKeyboard->GetDeviceState(sizeof(m_acKeyState1), m_pacCurrentKeyState);
                assert(SUCCEEDED(hr));
            }
        }
    }

    m_bScrollLock = ((GetKeyState(VK_SCROLL) & 1) != 0);

    UpdateKeyDownTimes(fTimeDelta);
}
//-------------------------------------------------------------------------------------------------------------------------------------------
bool 
CKeyboardState::IsAnyKeyDown() const
{
    for (int i = 0; i < NUM_KEYS; i++)
    {
        if (((*m_pacCurrentKeyState)[i] & 0x80) == 0x80)
        {
            return true;
        }
    }

    return false;
}
//-------------------------------------------------------------------------------------------------------------------------------------------

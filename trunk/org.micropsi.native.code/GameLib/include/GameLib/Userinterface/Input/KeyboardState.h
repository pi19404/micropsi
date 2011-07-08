/*******************************************************************************
 KeyboardState.h - Klasse CKeyboardState liest den Zustand des Keyboards ein, 
    wird vom Interface verwendet und von CUserGameCtrlEvts.
    Soll auf keinen Fall direkt vom Gameplay benutzt werden!
*******************************************************************************/
#pragma once

#ifndef KEYBOARDSTATE_H_INCLUDED
#define KEYBOARDSTATE_H_INCLUDED

#include "baselib/array.h"

struct IDirectInputDevice8A;

class CKeyboardState
{
private:
    IDirectInputDevice8A* m_pDIDKeyboard;

    enum { NUM_KEYS = 256 };

    CInlineArray<float, NUM_KEYS>   m_afKeyDownTime;

	CInlineArray<char, NUM_KEYS>    m_acKeyState1;
    CInlineArray<char, NUM_KEYS>    m_acKeyState2;

	CInlineArray<char, NUM_KEYS>*	m_pacCurrentKeyState;	
	CInlineArray<char, NUM_KEYS>*	m_pacLastKeyState;	

	bool                            m_bScrollLock;

    void UpdateKeyDownTimes(float fTimeDelta);

public:

    CKeyboardState(IDirectInputDevice8A* pDIDKeyboard);
    ~CKeyboardState();

    void RetrieveFromDevice(float fTimeDelta);
    
	/// testen, ob der Any-Key gedrückt ist
	bool IsAnyKeyDown() const;					

	/// Bsp: IsKeyDown(DIK_SPACE)
	bool IsKeyDown(int iDIKey) const;			

	bool KeyDownEvent(int p_iDIKey) const;
	bool KeyUpEvent(int p_iDIKey) const;

	float GetKeyDownTime(int iDIKey) const;

    bool ScrollLock() const;
};

#include "KeyboardState.inl"

#endif // KEYBOARDSTATE_H_INCLUDED
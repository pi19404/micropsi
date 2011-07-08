#pragma once

#ifndef FULLGUISCREEN_H_INCLUDED
#define FULLGUISCREEN_H_INCLUDED

#include "Application/stdinc.h"
#include "GameLib/UserInterface/UIScreen.h"

namespace UILib
{
	class CPanel;
}

class CSliderPanel;

class CFullGUIScreen : public CUIScreen
{
public:

    CFullGUIScreen();
    virtual ~CFullGUIScreen();

    virtual void Init();
    virtual void Update();
    
    virtual void Render();

    virtual void OnEnter();
    virtual void OnLeave();

	static CUIScreen* __cdecl Create();
	virtual void Destroy() const;

private:
	void	CreateUIElements();

	UILib::CPanel*			m_pxBackgroundPanel;
	CSliderPanel*			m_pxSliderPanel;
};

#endif // FULLGUISCREEN_H_INCLUDED

#pragma once

#ifndef MINIMALGUISCREEN_H_INCLUDED
#define MINIMALGUISCREEN_H_INCLUDED

#include "Application/stdinc.h"
#include "GameLib/UserInterface/UIScreen.h"

namespace UILib
{
	class CPanel;
}

class CMinimalGUIScreen : public CUIScreen
{
public:

    CMinimalGUIScreen();
    virtual ~CMinimalGUIScreen();

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

};

#endif // MINIMALGUISCREEN_H_INCLUDED

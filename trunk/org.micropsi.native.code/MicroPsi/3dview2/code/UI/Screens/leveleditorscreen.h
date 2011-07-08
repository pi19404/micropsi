#pragma once

#ifndef LEVELEDITORSCREEN_H_INCLUDED
#define LEVELEDITORSCREEN_H_INCLUDED

#include "Application/stdinc.h"
#include "GameLib/UserInterface/UIScreen.h"

#include "uilib/controls/panel.h"

namespace UILib
{
	class CPanel;
}

class CUIScreenStateMachine;
class CCompass;
class CEditorPanel;
class CConnectionStatusPanel;

class CLevelEditorScreen : public CUIScreen
{
public:

    CLevelEditorScreen();
    virtual ~CLevelEditorScreen();

    virtual void Init();
    virtual void Update();
    
    virtual void Render();

    virtual void OnEnter();
    virtual void OnLeave();

	static CUIScreen* __cdecl Create();
	virtual void Destroy() const; 

private:

	class CClickPanel : public UILib::CPanel
    {
        typedef void (__cdecl* ClickCallback)(void* pUserData, const CPnt& xPos, bool bLeft);
        ClickCallback   m_fpClickCallback;
        void*           m_pUserData;
    public:
        CClickPanel(ClickCallback p_fpClickCallback, void* p_pUserData);
        ~CClickPanel();
        bool HandleMsg(const UILib::CMessage& p_krxEvent);
		virtual void DeleteNow();
    };

    static void __cdecl ClickCallback(void* pUserData, const CPnt& xPos, bool bLeft);
    void WorldClick(const CPnt& xPos, bool bLeft);

	void	CreateUIElements();

	CClickPanel*			m_pxBackgroundPanel;
	CCompass*				m_pxCompass;
	CEditorPanel*			m_pxEditorPanel;
	CConnectionStatusPanel*	m_pxConnectionStatusPanel;
};

#endif // LEVELEDITORSCREEN_H_INCLUDED

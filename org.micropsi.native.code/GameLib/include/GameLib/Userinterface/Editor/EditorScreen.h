#pragma once

#ifndef GAMELIB_EDITORSCREEN_H_INCLUDED
#define GAMELIB_EDITORSCREEN_H_INCLUDED

#include "baselib/configfile.h"

#include "GameLib/UserInterface/UIScreen.h"
#include "uilib/controls/panel.h"

class CUIScreenStateMachine;
class CLevelEditor;
class CEditorToolBar;
class CObjectsListWindow;
class COptionsWindow;
class CObjectPropertiesWindow;

class CEditorScreen : public CUIScreen
{
public:

	enum ClickMode 
	{
		CM_Select,
		CM_CreateSoundMarker,
		CM_CreateMusicMarker,
		CM_CreateEnvironmentMarker,
		CM_CreateGameObjMarker
	};

	enum ControlMode
	{
		CM_Maya,
		CM_Max
	};

    CEditorScreen();
    ~CEditorScreen();

    void Update();

    void OnEnter();
    void OnLeave();

	static CUIScreen* __cdecl Create();
	virtual void Destroy() const; 

	CObjectsListWindow*				GetObjectsListWindow() const;
	COptionsWindow*					GetOptionsWindow() const;
	CObjectPropertiesWindow*		GetObjectPropertiesWindow() const;

	void							SetControlMode(ControlMode p_eMode);

private:

    class CClickPanel : public UILib::CPanel
    {
        typedef void (__cdecl* ClickCallback)(void* pUserData, const CPnt& xPos, bool p_bLeft);
        ClickCallback   m_fpClickCallback;
        void*           m_pUserData;
    public:
        CClickPanel(ClickCallback p_fpClickCallback, void* p_pUserData);
        ~CClickPanel();
        bool HandleMsg(const UILib::CMessage& p_krxEvent);
		virtual void DeleteNow();
    };

    static void __cdecl ClickCallback(void* pUserData, const CPnt& xPos, bool p_bLeft);
    void WorldClick(const CPnt& xPos, bool p_bLeft);

    void CreateUIElements();
	void CreateConfig();
	void CreateKeyMapping();
	void MoveCamera();

    CClickPanel*					m_pxWorldClickPanel;
	CEditorToolBar*					m_pxEditorToolBar;
	CObjectsListWindow*				m_pxObjectListWindow;
	COptionsWindow*					m_pxOptionsWindow;
	CObjectPropertiesWindow*		m_pxObjectPropertiesWindow;

//    CLevelEditor*					m_pLevelEditor;

	CConfigFile						m_xConfiguration;			///< Editorkonfiguration

};

#include "EditorScreen.inl"

#endif // GAMELIB_EDITORSCREEN_H_INCLUDED

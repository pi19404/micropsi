#pragma once

#ifndef GAMELIB_UISCREEN_H_INCLUDED
#define GAMELIB_UISCREEN_H_INCLUDED

class CUIScreenStateMachine;

/**
	Basisklasse für UIScreens

	UIScreens bilden eine Statemachine

	Hinweise zum Ableiten:		Der Konstruktor ist protected und sollte es auch bleiben
								Der Screen sollte über eine statische Create()-Funktion erzeugt werden
								Jeder Screen muss Destroy() überschreiben und sich in dieser Methode selbst löschen.
								Der UIScreenStateMachine benutzt Destroy() um die Screens am Ende automatisch zu löschen
*/

class CUIScreen
{
	friend class CUIScreenStateMachine;

protected:

    CUIScreenStateMachine*    m_pUIScreenStateMachine;

    CUIScreen();
    virtual ~CUIScreen();

	void SetUIScreenStateMachine(CUIScreenStateMachine* pUISM);

public:

    virtual void Update() = 0;
    
    virtual void Render() {};
    virtual void Render(int iHead) {Render();};

    virtual void OnEnter() {};
    virtual void OnLeave() {};

	/// Opaque bedeutet, dass dieser Screen den gesamten Bildschirm abdeckt und verdeckt; d.h. kein anderes Rendering ist nötig
    virtual bool IsOpaque() const { return false; };

	/// Screen löscht sich selbst
	virtual void Destroy() const = 0; 
};

#endif // GAMELIB_UISCREEN_H_INCLUDED

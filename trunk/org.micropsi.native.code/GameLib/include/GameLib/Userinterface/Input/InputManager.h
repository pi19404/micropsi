
#pragma once
#ifndef INPUTMANAGER_H_INCLUDED
#define INPUTMANAGER_H_INCLUDED

/**
	Beispiele Keyboard:

		Map("keyboard:anykey", "...");								// irgendeine Taste gedrück
		Map("keyboard:w.down", "...");								// w wurde heruntergedrückt (Flanke)
		Map("keyboard:w.up", "...");								// w wurde losgelassen (Flanke)
		Map("keyboard:w.held", "...");								// w ist gerade gedrückt (Zustand)
		Map("keyboard:w.notheld", "...");							// w ist gerade nicht gedrückt (Zustand)
		Map("keyboard:w.repeatevent", "...");						// w wird gedrückt; liefert in regelmäßigen Abständen true (key-repeat)
		Map("keyboard:scrolllock", "...");							// scrolllock ist an

	Beispiele Gamepad:

		Map("gamepad0:anykey", "...");											// irgendeine taste ist gedrückt
		Map("gamepad0:button1.down", "...");									// button1 wurde heruntergedrückt (Flanke)
		Map("gamepad0:axis1.value<0.2",	"...");									// achse 1 wert ist < 0.2  (Bereich -1 .. 1)
		Map("gamepad0:axis1",	"...");											// ist immer true, sinnvoll nur in Verbindung mit GetAxisValue()
		Map("gamepad0:button6.held  &&  gamepad0:axis1.value<0.2", "...");		// kombiniert Bedingung

	Beispiele Mouse:

		Map("mouse.button1.down", "...")							///< button1 Flanke nach unten
		Map("mouse.anybutton", "...")								///< irgendeine taste ist gedrückt

*/

#include "windows.h"
#include <vector>
#include <map>

#include "GameLib/UserInterface/Input/InputEvent.h"
#include "GameLib/UserInterface/Input/InputCondition.h"

struct IDirectInputDevice8A;
class CGamepadState;
class CKeyboardState;
class CMouseState;

class CInputManager
{
public:
    CInputManager();
	~CInputManager();

	void					HandleWindowsMessage(HWND p_hWnd, UINT p_uiMsg, WPARAM p_wParam, LPARAM p_lParam);
	void					RemoveGamepads();
	void					AddGamePad(IDirectInputDevice8A* pDIDGamepad);
	void					SetKeyboard(IDirectInputDevice8A* pDIDKeyBoard);
	void					SetMouse(IDirectInputDevice8A* pDIDMouse);

    const CKeyboardState*   GetKeyboardState() const;
	const CGamepadState*	GetGamePadState(int iPad) const;
    const CMouseState*      GetMouseState() const;

	int						GetNumGamepads() const;

	/// setzt den Modus einer Gamepad-Achse; Flags siehe CGamePadState
	void					SetAxisMode(int iGamePad,  int iAxis, int iModeFlags);

	typedef unsigned int EventIterator;
	
	void					StartIterateEvents(EventIterator& p_xIterator) const;
	const CInputEvent*		IterateEvents(EventIterator& p_xIterator) const;
	void					FlushEventQueue();

	/// erzeugt ein Mapping, der Mappingname wird an bestimmte Bedingungen geknüpft
	bool					Map(std::string p_sCondition, std::string p_sName, std::string p_sGroup = ".default");

	/// erzeugt ein Mapping, der Mappingname wird an bestimmte Bedingungen geknüpft; es wird ein Achsenwert definiert, der gilt, wenn die Bedingung erfüllt ist
	bool					Map(std::string p_sCondition, std::string p_sName, float p_fAxisValue, std::string p_sGroup = ".default");

	/// überprüft, ob die Bedingungen für ein bestimmtes Mapping erfüllt sind
	bool					IsFullfilled(std::string p_sName, std::string p_sGroup = ".default") const;
	
	/// überprüft, ob ein mit der Condition übereinstimmendes Event ansteht; konsumiert das Event
	bool					ConsumeEvent(std::string p_sCondition, std::string p_sGroup = ".default");

	/// überprüft, ob ein passendes Event vorliegt, konsumiert das Event *nicht*
	bool					HasEvent(std::string p_sCondition, std::string p_sGroup = ".default");

	/// löscht alle Events der Gruppe aus der Queue
	void					FlushEvents(std::string p_sGroup);

	/// entfernt ein Mapping aus der Liste - Achtung: *alle* conditions für dieses Mapping werden gelöscht
	void					RemoveMapping(std::string p_sName, std::string p_sGroup = ".default");

	/// löscht alle gemappten Events
	void					ClearMappings();

	/// löscht alle gemappten Events einer Gruppe
	void					ClearMappings(const std::string& sGroup);

	/// liefert den Axenwert einer Condition
	float					GetAxisValue(std::string p_sName, std::string p_sGroup = ".default") const;

	/// regelmäßiges Update (Simulationstakt)
	void					UpdateFromDevice(float p_fDeltaSeconds);

	static KeyCode			TranslateWindowsKey(WPARAM p_iVirtualKey);
	static KeyCode			TransLateStringToKey(std::string p_sKey) ;

private:

	struct TCondition;

	bool					CheckEvent(std::string p_sName, std::string p_sGroup, bool p_bConsume);
	int						NumConditionEvents(const CComplexInputCondition& p_xrCondition, float& po_fAxisValue) const;
	int						NumKeyRepeatEvents(float p_fButtonDownTime, bool p_bButtonDownEventNow) const;
	int 					ConditionFullfilledCount(const CInputCondition& p_xrCondition) const;
	float					GetAxisValue(const CInputCondition& xrCondition) const;
	CPnt					GetMousePos(HWND p_hWnd, LPARAM p_lParam) const;
	const TCondition*		GetCondition(std::string p_sName, std::string p_sGroup) const;
	TCondition*				GetCondition(std::string p_sName, std::string p_sGroup);

	std::vector<CInputEvent>		m_axEventQueue;
	std::vector<CGamepadState*>		m_apxGamepadStates;
	CKeyboardState*					m_pxKeyboardState;
	CMouseState*					m_pxMouseState;
	bool							m_bUseEventQueue;

	float	m_fLastTickDuration;
	float	m_fKeyStrokeDelay;
	float	m_fKeyStrokeRepeatTime;

	struct TCondition
	{
		CComplexInputCondition*		m_pxCondition;							///< die eigentliche Condition
		int							m_iEventsWaiting;						///< Anzahl bisher angestauter Events
		float						m_fAxisValue;							///< Achsenwert der zuletzt erfüllten Bedingung
	};

	struct TGroup
	{
		std::map<const std::string, TCondition>	m_xInputConditions;			///< Mapping von Name einer Condition auf Condition-Objekt
		void Clear();
	};

	std::map<const std::string, TGroup>			m_xGroups;					///< Mapping GruppenNamen --> Gruppen
};

#include "InputManager.inl"

#endif // INPUTMANAGER_H_INCLUDED


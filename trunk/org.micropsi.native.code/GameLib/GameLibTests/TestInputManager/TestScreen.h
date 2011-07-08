#ifndef TESTSCREEN_H_INCLUDED
#define TESTSCREEN_H_INCLUDED

#include "GameLib/UserInterface/UIScreen.h"
#include "GameLib/UserInterface/UIScreenStateMachine.h"
#include <string>
#include <map>
#include <vector>

namespace UILib
{
	class CPanel;
}

class CUIScreenStateMachine;
class CEventDisplayPanel;

class CTestScreen : public CUIScreen
{
private:
    CTestScreen();
    ~CTestScreen();
	
	UILib::CPanel*          m_pxBackgroundPanel;

    void CreateUIElements();
	void Layout();

	std::map<std::string, CEventDisplayPanel*>				m_mPanels;			///< map mit allen Panels - um zu suchen, ob ein Event schon gemappt war
	std::vector<CEventDisplayPanel*>						m_aPanels;			///< vector mit allen Panels - um eine definierte Reihenfolge zu haben

public:

	void Init();
    void Update();

    void OnEnter();
    void OnLeave();

	static CUIScreen* Create();
	virtual void Destroy() const; 

	void Map(std::string sCondition, std::string sName, float fAxisValue = 0.0f);
	void AddSeparator();
};


#endif // TESTSCREEN_H_INCLUDED
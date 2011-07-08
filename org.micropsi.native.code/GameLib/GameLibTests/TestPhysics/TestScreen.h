#ifndef TESTSCREEN_H_INCLUDED
#define TESTSCREEN_H_INCLUDED

#include "GameLib/UserInterface/UIScreen.h"
#include "GameLib/UserInterface/UIScreenStateMachine.h"

namespace UILib
{
	class CPanel;
	class CLabel;
}

class CTestScreen : public CUIScreen
{
private:
    CTestScreen();
    ~CTestScreen();
	
	UILib::CPanel*          m_pxBackgroundPanel;

    void CreateUIElements();
	void Layout();

public:

	void Init();
    void Update();

    void OnEnter();
    void OnLeave();

	static CUIScreen* Create();
	virtual void Destroy() const; 


// da dies ein Test ist, machen wir es uns einfach und die controls public:

	UILib::CLabel*		m_pCarSpeedLabel;
};


#endif // TESTSCREEN_H_INCLUDED

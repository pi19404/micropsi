#ifndef TESTINPUTMANAGER_H_INCLUDED
#define TESTINPUTMANAGER_H_INCLUDED

#include "stdinc.h"
#include "GameLib/GameLibApplication.h"
class CTestScreen;

//-----------------------------------------------------------------------------------------------------------------------
class CTestInputManager : public CGameLibApplication
{
private:
    void InitFileLocator();

    // virtuelle Funktionen von E42Application:
    void CreateScene();
    void Terminate();

	void Input();
	void Update();
	void Output();

protected:

	void CreateInputManagerMapping(CTestScreen* pTestScreen);

public:
    CTestInputManager(HINSTANCE hInstance);
    ~CTestInputManager();
};
//-----------------------------------------------------------------------------------------------------------------------

#endif // TESTINPUTMANAGER_H_INCLUDED
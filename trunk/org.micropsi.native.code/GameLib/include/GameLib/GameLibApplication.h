#ifndef GAMELIB_GAMELIBAPPLICATION_H_INCLUDED
#define GAMELIB_GAMELIBAPPLICATION_H_INCLUDED

#include "e42/E42Application.h"

class CUIScreenStateMachine;
class CInputManager;

class CGameLibApplication : public CE42Application
{
public:

    static          CGameLibApplication& Get();

	CInputManager*				GetInputManager() const;
	CUIScreenStateMachine*		GetUIScreenStateMachine() const;

	double						GetTimeInSecondsSinceLastUpdate();

protected:

    CGameLibApplication(HINSTANCE hInstance);
    ~CGameLibApplication();

	void			SetNumUIScreenDesktops(int iNumUIScreenDesktops);

	virtual void	Input();
	virtual void	Update();
	virtual void	Output();

	virtual void    CreateScene();
    virtual void    Terminate();
    virtual void    OnIdle();
	virtual bool	OnWindowMessage(UINT msg, WPARAM wParam, LPARAM lParam);

private:

	CUIScreenStateMachine*		m_pxUIScreenStateMachine;
	CInputManager*				m_pxInputManager;

	int							m_iNumUIScreenDesktops;

	double						m_dTime;
	double						m_dTimeSinceLastUpdate;
};

#include "GameLibApplication.inl"

#endif // GAMELIB_GAMELIBAPPLICATION_H_INCLUDED
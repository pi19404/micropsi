
#pragma once
#ifndef OBJECTFOCUSOBSERVERCONTROLLER_H_INCLUDED
#define OBJECTFOCUSOBSERVERCONTROLLER_H_INCLUDED

#include "Observers/observercontroller.h"

class CWorld;

class CObjectFocusObserverController : public CObserverController
{
public:

	CObjectFocusObserverController();
	virtual ~CObjectFocusObserverController();

	virtual void	OnActivate(CWorld* p_pxWorld);
	virtual void	OnDeactivate(CWorld* p_pxWorld);
	virtual void	Tick(CInputManager* p_pxInputManager, CWorld* p_pxWorld);

	void			SetFocusObject(__int64 p_iFocusObject);
	__int64			GetFocusObject() const;
	bool			IsWatchingValidObject() const;

private:

    __int64			m_iFocusObject;
	CWorld*			m_pxWorld;
};

#endif // OBJECTFOCUSOBSERVERCONTROLLER_H_INCLUDED



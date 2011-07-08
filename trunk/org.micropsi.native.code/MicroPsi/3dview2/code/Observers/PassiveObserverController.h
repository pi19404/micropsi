
#pragma once
#ifndef PASSIVEOBSERVERCONTROLLER_H_INCLUDED
#define PASSIVEOBSERVERCONTROLLER_H_INCLUDED

#include "Observers/observercontroller.h"

class CPassiveObserverController : public CObserverController
{
public:

	CPassiveObserverController();
	virtual ~CPassiveObserverController();

	virtual void	OnActivate(CWorld* p_pxWorld);
	virtual void	OnDeactivate(CWorld* p_pxWorld);
	virtual void	Tick(CInputManager* p_pxInputManager, CWorld* p_pxWorld);
};

#endif // PASSIVEOBSERVERCONTROLLER_H_INCLUDED




#pragma once
#ifndef WALKINGOBSERVERCONTROLLER_H_INCLUDED
#define WALKINGOBSERVERCONTROLLER_H_INCLUDED

#include "Observers/observercontroller.h"

class CWalkingObserverController : public CObserverController
{
public:

	CWalkingObserverController();
	virtual ~CWalkingObserverController();

	virtual void	OnActivate(CWorld* p_pxWorld);
	virtual void	OnDeactivate(CWorld* p_pxWorld);
	virtual void	Tick(CInputManager* p_pxInputManager, CWorld* p_pxWorld);

private:

	float		m_fMovementSpeed;
	float		m_fFastMovementSpeed;
};

#endif // WALKINGOBSERVERCONTROLLER_H_INCLUDED



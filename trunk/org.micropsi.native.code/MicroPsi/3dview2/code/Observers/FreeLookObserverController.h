
#pragma once
#ifndef FREELOOKOBSERVERCONTROLLER_H_INCLUDED
#define FREELOOKOBSERVERCONTROLLER_H_INCLUDED

#include "Observers/observercontroller.h"

class CFreeLookObserverController : public CObserverController
{
public:

	CFreeLookObserverController();
	virtual ~CFreeLookObserverController();

	virtual void	OnActivate(CWorld* p_pxWorld);
	virtual void	OnDeactivate(CWorld* p_pxWorld);
	virtual void	Tick(CInputManager* p_pxInputManager, CWorld* p_pxWorld);

private:

	float		m_fMovementSpeed;
	float		m_fFastMovementSpeed;
};

#endif // FREELOOKOBSERVERCONTROLLER_H_INCLUDED



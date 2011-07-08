
#pragma once
#ifndef OBSERVERCONTROLLERSWITCHER_H_INCLUDED
#define OBSERVERCONTROLLERSWITCHER_H_INCLUDED

#include <string>
#include <map>

#include "Observers/observer.h"
#include "Observers/observercontroller.h"

class CInputManager;
class CWorld;

class CObserverControllerSwitcher
{
public:

	CObserverControllerSwitcher(CWorld* p_pxWorld);
	~CObserverControllerSwitcher();

	void		SwitchObserver(std::string p_sNewObserverName);

	void		Tick(CInputManager* p_pxInputManager);

	void		SetCurrentObserver(CObserver* p_pxObserver);
	CObserver*	GetCurrentObserver() const;

	CObserverController*	GetCurrentObserverController() const;

private:

	void		AddObserver(std::string p_sName, CObserverController* p_pxObserver);

	CObserverController*	m_pxCurrentController;
	CObserver*				m_pxObserver;
	CWorld*					m_pxWorld;
	std::map< std::string, CObserverController* >		m_mxAllControllers;
};


#include "ObserverControllerSwitcher.inl"

#endif // OBSERVERCONTROLLERSWITCHER_H_INCLUDED




#pragma once
#ifndef OBSERVERCONTROLLER_H_INCLUDED
#define OBSERVERCONTROLLER_H_INCLUDED

#include "Observers/observer.h"

class CInputManager;
class CWorld;

class CObserverController
{
public:

	CObserverController();
	virtual ~CObserverController();

	virtual void	OnActivate(CWorld* p_pxWorld) = 0;
	virtual void	OnDeactivate(CWorld* p_pxWorld) = 0;
	virtual void	Tick(CInputManager* p_pxInputManager, CWorld* p_pxWorld) = 0;

	void			SetCurrentObserver(CObserver* p_pxObserver);
	CObserver*		GetCurrentObserver() const;

	static void		FixHeightAboveTerrain(CVec3& po_rvPos, CWorld* p_pxWorld, float p_fExactHeight);
	static void		FixHeightAboveTerrain(CVec3& po_rvPos, CWorld* p_pxWorld, float p_fMinHeight, float p_fMaxHeight);
	static void		FixMapBorders(CVec3& po_rvPos, CWorld* p_pxWorld);

protected:

	CObserver*	m_pxObserver;
};

#include "observercontroller.inl"

#endif // OBSERVERCONTROLLER_H_INCLUDED




#pragma once
#ifndef HELICOPTEROBSERVERCONTROLLER_H_INCLUDED
#define HELICOPTEROBSERVERCONTROLLER_H_INCLUDED

#include "Observers/observercontroller.h"

class CHelicopterObserverController : public CObserverController
{
public:

	CHelicopterObserverController();
	virtual ~CHelicopterObserverController();

	virtual void	OnActivate(CWorld* p_pxWorld);
	virtual void	OnDeactivate(CWorld* p_pxWorld);
	virtual void	Tick(CInputManager* p_pxInputManager, CWorld* p_pxWorld);

private:

	float		m_fSpeed;
	float		m_fPitch;					///< Pitch zwischen 0 und 1
	float		m_fRoll;					///< Roll zwischen 0 und 1
	float		m_fHeight;					///< Höhe (absolut, nicht über dem Boden)

	int			m_iSoundChannel;			///< Soundkanal für Rotor
};

#endif // HELICOPTEROBSERVERCONTROLLER_H_INCLUDED




#include "Application/stdinc.h"
#include "Observers/PassiveObserverController.h"

//---------------------------------------------------------------------------------------------------------------------
CPassiveObserverController::CPassiveObserverController()
{
}



//---------------------------------------------------------------------------------------------------------------------
CPassiveObserverController::~CPassiveObserverController()
{
}

//---------------------------------------------------------------------------------------------------------------------
void
CPassiveObserverController::OnActivate(CWorld* p_pxWorld)
{
}

//---------------------------------------------------------------------------------------------------------------------
void
CPassiveObserverController::OnDeactivate(CWorld* p_pxWorld)
{
}

//---------------------------------------------------------------------------------------------------------------------
void 
CPassiveObserverController::Tick(CInputManager* p_pxInputManager, CWorld* p_pxWorld)
{
	if(!m_pxObserver)
	{
		return;
	}

	CVec3 v = m_pxObserver->GetPos();
	FixHeightAboveTerrain(v, p_pxWorld, 1.0f, 100.0f);
	FixMapBorders(v, p_pxWorld);
	m_pxObserver->SetPos(v);
}
//---------------------------------------------------------------------------------------------------------------------

#include "Application/stdinc.h"
#include "Observers/ObjectFocusObserverController.h"

#include "World/world.h"
#include "World/ObjectManager.h"
#include "World/WorldObject.h"

//---------------------------------------------------------------------------------------------------------------------
CObjectFocusObserverController::CObjectFocusObserverController()
{
	m_iFocusObject = -1;
}

//---------------------------------------------------------------------------------------------------------------------
CObjectFocusObserverController::~CObjectFocusObserverController()
{
}

//---------------------------------------------------------------------------------------------------------------------
void
CObjectFocusObserverController::OnActivate(CWorld* p_pxWorld)
{
	m_pxWorld = p_pxWorld;
}

//---------------------------------------------------------------------------------------------------------------------
void
CObjectFocusObserverController::OnDeactivate(CWorld* p_pxWorld)
{
}

//---------------------------------------------------------------------------------------------------------------------
void			
CObjectFocusObserverController::SetFocusObject(__int64 p_iFocusObject)
{
	m_iFocusObject = p_iFocusObject;
}
//---------------------------------------------------------------------------------------------------------------------
__int64			
CObjectFocusObserverController::GetFocusObject() const
{
	return m_iFocusObject;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CObjectFocusObserverController::Tick(CInputManager* p_pxInputManager, CWorld* p_pxWorld)
{
	if(!m_pxObserver)
	{
		return;
	}

	if(m_iFocusObject < 0)
	{
		return;
	}

	CWorldObject* pxObj = m_pxWorld->GetObjectManager()->FindObj(m_iFocusObject);
	if(pxObj)
	{
		CVec3 vPos = (CVec3(-4.0f, 3.0f, 0.0f) * pxObj->GetWorldTransformation()).GetReduced();
		CVec3 vLookAt = (CVec3(5.0f, 0.0f, 0.0f) * pxObj->GetWorldTransformation()).GetReduced();

		FixHeightAboveTerrain(vPos, p_pxWorld, 2.0f, 10000.0f);
		FixHeightAboveTerrain(vLookAt, p_pxWorld, 0.0f, 10000.0f);

		m_pxObserver->SetPos(vPos);
		m_pxObserver->LookAt(vLookAt);
	}
	else
	{
		// it used to be there, but is no more
		m_iFocusObject = -1;
	}
}
//---------------------------------------------------------------------------------------------------------------------
bool			
CObjectFocusObserverController::IsWatchingValidObject() const
{
	return m_iFocusObject >= 0;
}
//---------------------------------------------------------------------------------------------------------------------

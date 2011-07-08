
#include "Application/stdinc.h"
#include "Observers/FreelookObserverController.h"

#include "GameLib/UserInterface/Input/InputManager.h"

//---------------------------------------------------------------------------------------------------------------------
CFreeLookObserverController::CFreeLookObserverController()
{
	m_fMovementSpeed = 2.0f;
	m_fFastMovementSpeed = 5.0f;
}



//---------------------------------------------------------------------------------------------------------------------
CFreeLookObserverController::~CFreeLookObserverController()
{
}

//---------------------------------------------------------------------------------------------------------------------
void
CFreeLookObserverController::OnActivate(CWorld* p_pxWorld)
{
}

//---------------------------------------------------------------------------------------------------------------------
void
CFreeLookObserverController::OnDeactivate(CWorld* p_pxWorld)
{
}

//---------------------------------------------------------------------------------------------------------------------
void 
CFreeLookObserverController::Tick(CInputManager* p_pxInputManager, CWorld* p_pxWorld)
{
	if(!m_pxObserver)
	{
		return;
	}

	float fSpeed = p_pxInputManager->IsFullfilled("walk_run")? m_fFastMovementSpeed : m_fMovementSpeed;

	if(p_pxInputManager->IsFullfilled("walk_straferight"))
	{
		m_pxObserver->MoveSideward(fSpeed);
	}
	if(p_pxInputManager->IsFullfilled("walk_strafeleft"))
	{
		m_pxObserver->MoveSideward(-fSpeed);
	}
	if(p_pxInputManager->IsFullfilled("walk_forward"))
	{
		m_pxObserver->MoveForwardInXZPane(fSpeed);
	}
	if(p_pxInputManager->IsFullfilled("walk_backward"))
	{
		m_pxObserver->MoveForwardInXZPane(-fSpeed);
	}
	if(p_pxInputManager->IsFullfilled("walk_jump"))
	{
		m_pxObserver->MoveY(fSpeed);
	}
	if(p_pxInputManager->IsFullfilled("walk_duck"))
	{
		m_pxObserver->MoveY(-fSpeed);
	}
	if(p_pxInputManager->IsFullfilled("walk_turnleft"))
	{
		m_pxObserver->RotateY(-10.0f / 100.0f);
	}
	if(p_pxInputManager->IsFullfilled("walk_turnright"))
	{
		m_pxObserver->RotateY(10.0f / 100.0f);	
	}
	if(p_pxInputManager->IsFullfilled("walk_turnup"))
	{
		m_pxObserver->Pitch(-10.0f / 100.0f);
	}
	if(p_pxInputManager->IsFullfilled("walk_turndown"))
	{
		m_pxObserver->Pitch(10.0f / 100.0f);
	}
	if(p_pxInputManager->IsFullfilled("walk_mouseturnleftright"))
	{
		float fDX = p_pxInputManager->GetAxisValue("walk_mouseturnleftright");
		m_pxObserver->RotateY(fDX / 100.0f);
	}
	if(p_pxInputManager->IsFullfilled("walk_mouseturnupdown"))
	{
		float fDY = p_pxInputManager->GetAxisValue("walk_mouseturnupdown");
		m_pxObserver->Pitch(fDY / 100.0f);
	}

	CVec3 v = m_pxObserver->GetPos();
	FixHeightAboveTerrain(v, p_pxWorld, 1.5f, 120.0f);
	FixMapBorders(v, p_pxWorld);
	m_pxObserver->SetPos(v);



//	DebugPrint("cam %.2f %.2f %.2f", m_pxObserver->GetPos().x, m_pxObserver->GetPos().y, m_pxObserver->GetPos().z);
}
//---------------------------------------------------------------------------------------------------------------------

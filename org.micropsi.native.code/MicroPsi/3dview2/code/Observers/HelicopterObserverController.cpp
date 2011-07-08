
#include "Application/stdinc.h"
#include "Observers/HelicopterObserverController.h"

#include "SoundLib/SoundSystem.h"

#include "GameLib/UserInterface/Input/InputManager.h"
#include "baselib/macros.h"

//---------------------------------------------------------------------------------------------------------------------
CHelicopterObserverController::CHelicopterObserverController()
{
	m_iSoundChannel = 0;
}

//---------------------------------------------------------------------------------------------------------------------
CHelicopterObserverController::~CHelicopterObserverController()
{
}

//---------------------------------------------------------------------------------------------------------------------
void
CHelicopterObserverController::OnActivate(CWorld* p_pxWorld)
{
	m_fPitch = 0.0f;
	m_fRoll = 0.0f;
	m_fHeight = 20.0f;

	m_iSoundChannel = SoundLib::CSoundSystem::Get().FindFreeVoice(10000);
	if(m_iSoundChannel >= 0)
	{
		SoundLib::CDSoundBuffer* pxBuffer = SoundLib::CSoundSystem::Get().GetVoice(m_iSoundChannel);
		pxBuffer->SetFile("heli.ogg");
		pxBuffer->FadeIn(1000);
		pxBuffer->Play(true);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CHelicopterObserverController::OnDeactivate(CWorld* p_pxWorld)
{
	if(m_iSoundChannel >= 0)
	{
		SoundLib::CSoundSystem::Get().GetVoice(m_iSoundChannel)->FadeOut(1000);
	}
	m_pxObserver->SetPitch(0.0f);
	m_pxObserver->SetRoll(0.0f);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CHelicopterObserverController::Tick(CInputManager* p_pxInputManager, CWorld* p_pxWorld)
{
	if(!m_pxObserver)
	{
		return;
	}

	const float fPitchDelta = 0.03f;
	const float fRollDelta = 0.03f;
	const float fMaxForwardAngle = 0.35f;
	const float fMaxBackwardAngle = 0.2f;
	const float fMaxRollAngle = 0.4f;

	const float fMaxForwardSpeed  = 150000.0f / (60.0f*60.0f) / 30.0f;
	const float fMaxBackwardSpeed =  30000.0f / (60.0f*60.0f) / 30.0f;
	const float fMaxTurnSpeed = 0.03f;
	const float fClimbSpeed = 0.2f;

	if(p_pxInputManager->IsFullfilled("walk_straferight"))
	{
		m_fRoll = min(m_fRoll + fRollDelta, 1.0f);
	}
	if(p_pxInputManager->IsFullfilled("walk_strafeleft"))
	{
		m_fRoll = max(m_fRoll - fRollDelta, -1.0f);
	}
	if(!p_pxInputManager->IsFullfilled("walk_straferight")  &&
	   !p_pxInputManager->IsFullfilled("walk_strafeleft"))
	{
		if(m_fRoll < 0)	
		{ 
			m_fRoll = min(m_fRoll + fRollDelta, 0.0f);
		}
		else if(m_fRoll > 0)
		{
			m_fRoll = max(m_fRoll - fRollDelta, 0.0f); 
		}
	}

	if(p_pxInputManager->IsFullfilled("walk_forward"))
	{
		m_fPitch = max(m_fPitch - fPitchDelta, -1.0f);
	}
	else if(p_pxInputManager->IsFullfilled("walk_backward"))
	{
		m_fPitch = min(m_fPitch + fPitchDelta, 1.0f);
	}
	else
	{
		if(m_fPitch < 0)	
		{ 
			m_fPitch = min(m_fPitch + fPitchDelta, 0.0f);
		}
		else if(m_fPitch > 0)
		{
			m_fPitch = max(m_fPitch - fPitchDelta, 0.0f); 
		}
	}

	float fPitchAngle;
	if(m_fPitch > 0.0f)
	{
		m_fSpeed = fMaxBackwardSpeed * -m_fPitch;
		fPitchAngle = fMaxBackwardAngle * -m_fPitch;
	}
	else
	{
		m_fSpeed = fMaxForwardSpeed * -m_fPitch;
		fPitchAngle = fMaxForwardAngle * -m_fPitch;
	}

	float fRollAngle = fMaxRollAngle * m_fRoll;
	float fTurnSpeed = fMaxTurnSpeed * m_fRoll;

	m_pxObserver->MoveForwardInXZPane(m_fSpeed);
	m_pxObserver->RotateY(fTurnSpeed);


	float fAdditionalSoundPitch = 0.0f;
	if(p_pxInputManager->IsFullfilled("walk_jump"))
	{
		m_fHeight += fClimbSpeed;
		fAdditionalSoundPitch = 0.1f;
	}
	if(p_pxInputManager->IsFullfilled("walk_duck"))
	{
		m_fHeight -= fClimbSpeed;
		fAdditionalSoundPitch = -0.1f;
	}

	if(m_iSoundChannel >= 0)
	{
		SoundLib::CDSoundBuffer* pxBuffer = SoundLib::CSoundSystem::Get().GetVoice(m_iSoundChannel);
		if(m_fSpeed != 0)
		{
			pxBuffer->SetPitch(1.0f + fAdditionalSoundPitch + ((m_fSpeed / fMaxForwardSpeed) * 0.25f));
		}
		else
		{
			pxBuffer->SetPitch(1.0f + fAdditionalSoundPitch);
		}
	}

//	DebugPrint("pitch %.2f, speed %.2f, pitchangle %.2f, rollangle %.2f", m_fPitch, m_fSpeed, fPitchAngle, fRollAngle);



	//if(p_pxInputManager->IsFullfilled("walk_turnup"))
	//{
	//	m_pxObserver->Pitch(-10.0f / 100.0f);
	//}
	//if(p_pxInputManager->IsFullfilled("walk_turndown"))
	//{
	//	m_pxObserver->Pitch(10.0f / 100.0f);
	//}
	//if(p_pxInputManager->IsFullfilled("walk_mouseturnleftright"))
	//{
	//	float fDX = p_pxInputManager->GetAxisValue("walk_mouseturnleftright");
	//	m_pxObserver->RotateY(fDX / 100.0f);
	//}
	//if(p_pxInputManager->IsFullfilled("walk_mouseturnupdown"))
	//{
	//	float fDY = p_pxInputManager->GetAxisValue("walk_mouseturnupdown");
	//	m_pxObserver->Pitch(fDY / 100.0f);
	//}

	CVec3 v = m_pxObserver->GetPos();
	v.y() = m_fHeight;
	FixHeightAboveTerrain(v, p_pxWorld, 5.0f, 80.0f);
	v.y() = max(v.y(), 5.0f);   // make sure were above water, too :)
	m_fHeight = v.y();
	FixMapBorders(v, p_pxWorld);
	m_pxObserver->SetPos(v);
	m_pxObserver->SetPitch(fPitchAngle);
	m_pxObserver->SetRoll(fRollAngle);


//	DebugPrint("cam %.2f %.2f %.2f", m_pxObserver->GetPos().x, m_pxObserver->GetPos().y, m_pxObserver->GetPos().z);
}
//---------------------------------------------------------------------------------------------------------------------

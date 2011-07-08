#include "stdafx.h"

#include "e42/AnimationCtrl.h"
#include "e42/core/Model.h"
#include "e42/core/EngineController.h"
#include "e42/AnimationSoundCtrl.h"

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CAnimationCtrl::CAnimationCtrl(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController)
{
	if (m_pxEngineController == NULL)
		m_pxEngineController = &CEngineController::Get();

    m_fAnimationStartTime = -1;
    m_fAnimationLength = -1;
    m_bLoopAnimation = false;
	m_pSoundCtrl = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CAnimationCtrl::~CAnimationCtrl()
{
	delete m_pSoundCtrl;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CAnimationCtrl::Reset()
{
    m_sCurrentAnimationName.clear();
    m_fAnimationStartTime = -1;
    m_fAnimationLength = -1;
    m_bLoopAnimation = false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
float
CAnimationCtrl::StartAnimation(const string& sAnimationName, float fTime, bool bLoop)
{
    // [DMA] -> muss nicht unbedingt ein Fehler sein, kann aber; wenn das nervt, dann bitte auskommentieren
    assert(m_sCurrentAnimationName != sAnimationName || 
        ((float)m_pxEngineController->GetEngineTime() - m_fAnimationStartTime >= 3 / 48.0f));
    // es sollte geprüft werden, ob diese Animation wirklich mehrfach gestartet werden sollte

    m_sCurrentAnimationName = sAnimationName;
    m_fAnimationStartTime = (float)m_pxEngineController->GetEngineTime() - fTime;
    m_bLoopAnimation = bLoop;

    if (sAnimationName.empty())
    {
        m_fAnimationLength = 0.0f;
    }
    else
    {
        m_fAnimationLength = m_hndModel->GetAnimationLength(sAnimationName);
    }

    return m_fAnimationLength;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CAnimationCtrl::SetAnimation(const std::string& sAnimationName, float fTime, bool bLoop)
{
    if (sAnimationName != m_sCurrentAnimationName)
    {
        StartAnimation(sAnimationName, fTime, bLoop);
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CAnimationCtrl::SwitchAnimation(const std::string& sAnimationName)
{
    m_sCurrentAnimationName = sAnimationName;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
float
CAnimationCtrl::CalcAnimationTime(float fTime) const
{
    if (m_fAnimationLength == 0)
    {
        return 0;
    }

    assert(m_fAnimationLength >= 0);

    if (fTime >= m_fAnimationLength - 1e-4f)
    {
        if (m_bLoopAnimation)
        {
            fTime = fmodf(fTime, m_fAnimationLength);
        }
        else
        {
            fTime = min(fTime, m_fAnimationLength - 1e-4f);
        }
    }
    return fTime;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CAnimationCtrl::SetupSound(const CVec3& vSourcePos, const string& sSoundType)
{
	if (!m_pSoundCtrl) return;
	if (m_sCurrentAnimationName.empty()) return;

	float fTime = (float)m_pxEngineController->GetEngineTime() - m_fAnimationStartTime;
    fTime = CalcAnimationTime(fTime);

	m_pSoundCtrl->Trigger(m_sCurrentAnimationName, fTime, vSourcePos, sSoundType);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CAnimationCtrl::SetupModel()
{
    if (m_sCurrentAnimationName.empty()) return;

    float fTime = (float)m_pxEngineController->GetEngineTime() - m_fAnimationStartTime;

    fTime = CalcAnimationTime(fTime);

    m_hndModel->SetAnimation(m_sCurrentAnimationName, fTime);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CAnimationCtrl::GetFrameTransform(
        CMat4S* pmOutTransform, const CMat4S&  mModelTransform, 
        const char* pcFrameName, float fTime, bool bMustBeKeyed) const
{
    if (m_sCurrentAnimationName.empty())
    {
        return false;
    }

    if ((bMustBeKeyed) &&
        (!m_hndModel->FrameIsKeyedInAnimation(m_sCurrentAnimationName, pcFrameName)))
    {
        return false;
    }

    fTime = CalcAnimationTime(fTime - m_fAnimationStartTime);
    
    m_hndModel->SetAnimation(m_sCurrentAnimationName, fTime);

    return m_hndModel->GetFrameTransform(pmOutTransform, mModelTransform, pcFrameName);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CAnimationCtrl::AddSound(std::string p_sAnimationName, const TiXmlElement* p_pXmlElement)
{
	if(!m_pSoundCtrl)
	{
		m_pSoundCtrl = new CAnimationSoundCtrl();
	}
	m_pSoundCtrl->AddSound(p_sAnimationName, p_pXmlElement);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CAnimationCtrl::FrameIsKeyed(const char* pcFrameName) const
{
    return m_hndModel->FrameIsKeyedInAnimation(m_sCurrentAnimationName, pcFrameName);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

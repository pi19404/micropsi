#pragma once

#ifndef ANIMATIONCTRL_H_INCLUDED
#define ANIMATIONCTRL_H_INCLUDED

#include <string>

#include "e42/stdinc.h"
#include "baselib/geometry/Matrix.h"

#include "e42/core/ResourceHandles.h"
#include "e42/core/Model.h"
#include "e42/core/EngineController.h"

class CAnimationSoundCtrl;
class CEngineController;
class TiXmlElement;

class CAnimationCtrl
{
private:

    CEngineController*  m_pxEngineController;
    std::string         m_sCurrentAnimationName;
    float               m_fAnimationStartTime;
    float               m_fAnimationLength;
    bool                m_bLoopAnimation;

    TModelHandle        m_hndModel;

	CAnimationSoundCtrl* m_pSoundCtrl;

    float CalcAnimationTime(float fTime) const;

public:
    CAnimationCtrl(CEngineController* pxEngineCOntroller = NULL);
    ~CAnimationCtrl();

    void Reset();

    void SetModel(TModelHandle hndModel);

    // startet neue Animation
    float StartAnimation(const std::string& sAnimationName, float fTime = 0, bool bLoop = true);

    // wenn die übergebene Animation die aktuelle ist ergibt sich keine Änderung, sonst wird die Animation gestartet
    void SetAnimation(const std::string& sAnimationName, float fTime = 0, bool bLoop = true);

    // Schaltet die Animation um, ohne die Wiedergabezeit zurückzusetzen (track wechseln)
    void SwitchAnimation(const std::string& sAnimationName);


    float GetCurrentAnimationTime() const;
    float GetCurrentAnimationRemainingTime() const;
    float GetCurrentAnimationLength() const;
    const std::string& GetCurrentAnimationName() const;

    float GetAnimationLength(const std::string& sAnimationName) const;

    bool AnimationFinished() const;

    bool GetFrameTransform(CMat4S* pmOutTransform, const CMat4S&  mModelTransform, const char* pcFrameName, float fTime, bool bMustBeKeyed = false) const;
    bool FrameIsKeyed(const char* pcFrameName) const;

	void AddSound(std::string p_sAnimationName, const TiXmlElement* p_pXmlElement);

    void SetupSound(const CVec3& vSourcePos, const std::string& sSoundType = "");
    void SetupModel();
};

#include "e42/AnimationCtrl.inl"

#endif // ANIMATIONCTRL_H_INCLUDED
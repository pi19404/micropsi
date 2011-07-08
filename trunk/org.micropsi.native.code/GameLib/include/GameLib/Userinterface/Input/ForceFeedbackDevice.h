#pragma once

#ifndef GAMELIB_FORCEFEEDBACKDEVICE_H_INCLUDED
#define GAMELIB_FORCEFEEDBACKDEVICE_H_INCLUDED

#include <dinput.h>

#include <map>
#include <string>
#include "baselib/comobjectptr.h"

#ifndef DIRECTINPUT_VERSION
#define DIRECTINPUT_VERSION    0x0800
#endif

struct IDirectInputDevice8A;
struct IDirectInputEffect;
struct DIEFFECT;


class CFFDevice
{
public:

	enum EFFECT_PARAM
	{
		EP_AXES			= DIEP_AXES,
		EP_DIRECTION	= DIEP_DIRECTION,
		EP_DURATION		= DIEP_DURATION,
		EP_ENVELOPE		= DIEP_ENVELOPE,
		EP_GAIN			= DIEP_GAIN,
		EP_NODOWNLOAD	= DIEP_NODOWNLOAD,
		EP_NORESTART	= DIEP_NORESTART,
		EP_SAMPLEPERIOD	= DIEP_SAMPLEPERIOD,
		EP_START		= DIEP_START,
		EP_STARTDELAY	= DIEP_STARTDELAY,
		EP_TRIGGERBUTTON= DIEP_TRIGGERBUTTON,
		EP_TRIGGERREPEATINTERVAL= DIEP_TRIGGERREPEATINTERVAL,
		EP_TYPESPECIFICPARAMS	= DIEP_TYPESPECIFICPARAMS
	};

private:

    IDirectInputDevice8A* m_pDIDevice;

    struct EffectPair
    {
        CComObjectPtr<IDirectInputEffect>   m_spxEffect1;
        CComObjectPtr<IDirectInputEffect>   m_spxEffect2;
    };

    std::map<const std::string, EffectPair> m_mxEffects;

    EffectPair  m_xCurrentEffectPair;
    std::string m_sCurrentEffectName;
    bool        m_bUpdateDevice;

    float       m_fEffectLength;


    EffectPair& GetEffectPair(const std::string& sEffectName);

    void StartEffectOnDevice(const EffectPair& rxPair);
    void StopEffectOnDevice();

    //static CFFDevice* ms_pxFFDevice;

public:

    CFFDevice(IDirectInputDevice8A* pDIDevice);
    ~CFFDevice();

    void StartEffect(const std::string& sEffectName, float fEffectLength = -1, float fSpeedFactor = 1.0f);
    void StopEffect();
    void SetEffect(const std::string& sEffectName, float fSpeedFactor = 1.0f);
    void LoadEffect(const std::string& sFile, const std::string& sEffectName);

	void GetEffectParameter(const std::string& sEffectName, 
							DWORD flags,
							DIEFFECT* effect1, 
							DIEFFECT* effect2);

	void SetEffectParameter(const std::string& sEffectName, 
							DWORD flags,
							DIEFFECT* effect1, 
							DIEFFECT* effect2);

    void UpdateDevice(float fFrameDuration);

    //static CFFDevice& Get();


    struct CallbackUserData
    {
        IDirectInputDevice8A*   m_pDIDevice;
        EffectPair              m_Effects;
    };
};

#endif // GAMELIB_FORCEFEEDBACKDEVICE_H_INCLUDED

#include "stdafx.h"
#include "GameLib/UserInterface/Input/ForceFeedbackDevice.h"

#include <dinput.h>

using std::map;
using std::string;
//---------------------------------------------------------------------------------------------------------------------
CFFDevice::CFFDevice(IDirectInputDevice8A* pDIDevice)
{
 //   assert(ms_pxFFDevice == 0);
 //   ms_pxFFDevice = this;

    m_pDIDevice = pDIDevice;

    m_fEffectLength = -1;
}
//---------------------------------------------------------------------------------------------------------------------
CFFDevice::~CFFDevice()
{
}
//---------------------------------------------------------------------------------------------------------------------
//CFFDevice* CFFDevice::ms_pxFFDevice = 0;
//---------------------------------------------------------------------------------------------------------------------
/*CFFDevice&
CFFDevice::Get()
{
    assert(ms_pxFFDevice);
	return *ms_pxFFDevice;
}
*/
//---------------------------------------------------------------------------------------------------------------------
BOOL 
CALLBACK CFFDevice_EnumEffectsInFileCallback(LPCDIFILEEFFECT lpdife, LPVOID pvRef)
{
    CFFDevice::CallbackUserData* pCbud = (CFFDevice::CallbackUserData*)pvRef;

    IDirectInputEffect** ppDIEffect = 
        pCbud->m_Effects.m_spxEffect1 ? 
            &pCbud->m_Effects.m_spxEffect2 : 
            &pCbud->m_Effects.m_spxEffect1;


    HRESULT hr = pCbud->m_pDIDevice->CreateEffect(
        lpdife->GuidEffect, 
        lpdife->lpDiEffect, 
        ppDIEffect, NULL);

    if (FAILED(hr))
        return DIENUM_STOP;


    if (pCbud->m_Effects.m_spxEffect2)
        return DIENUM_STOP;

    return DIENUM_CONTINUE;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CFFDevice::LoadEffect(const string& sFile, const string& sEffectName)
{
	if (!m_pDIDevice) return;

    CallbackUserData cbud;
    cbud.m_pDIDevice = m_pDIDevice;
    cbud.m_Effects.m_spxEffect1 = 0;
    cbud.m_Effects.m_spxEffect2 = 0;

    HRESULT hr =
        m_pDIDevice->EnumEffectsInFile( sFile.c_str(), 
                                        CFFDevice_EnumEffectsInFileCallback,
                                        &cbud, 
                                        DIFEF_MODIFYIFNEEDED);

    assert(SUCCEEDED(hr));
    m_mxEffects[sEffectName] = cbud.m_Effects;
}
//---------------------------------------------------------------------------------------------------------------------
CFFDevice::EffectPair&
CFFDevice::GetEffectPair(const string& sEffectName)
{
    assert(!m_mxEffects.empty() && !sEffectName.empty());

    const map<const string, EffectPair>::iterator iter = m_mxEffects.find(sEffectName);
    
    assert((iter != m_mxEffects.end()) && (iter->first == sEffectName));

    return iter->second;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CFFDevice::StartEffect(const string& sEffectName, float fEffectLength, float fSpeedFactor)
{
    m_sCurrentEffectName = sEffectName;
    m_bUpdateDevice = true;

    m_fEffectLength = fEffectLength / fSpeedFactor;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CFFDevice::StopEffect()
{
    if (!m_sCurrentEffectName.empty())
    {
        m_sCurrentEffectName.clear();
        m_bUpdateDevice = true;
    }
}
//---------------------------------------------------------------------------------------------------------------------
void 
CFFDevice::SetEffect(const string& sEffectName, float fSpeedFactor)
{
	if (!m_pDIDevice)
	{
		return;
	}

    if (sEffectName != m_sCurrentEffectName)
    {
		DIEFFECT xFFEffect;
		memset(&xFFEffect, 0, sizeof(DIEFFECT));
		GetEffectParameter( sEffectName, EFFECT_PARAM::EP_DURATION, &xFFEffect, NULL);
		
		if (xFFEffect.dwDuration != ULONG_MAX)
		{
			m_fEffectLength = ((float)xFFEffect.dwDuration / 1000000.0f);
		}
		else
		{
			m_fEffectLength = -1.0f;
		}

        StartEffect(sEffectName, m_fEffectLength, fSpeedFactor);
    }
}
//---------------------------------------------------------------------------------------------------------------------
void
CFFDevice::UpdateDevice(float fFrameDuration)
{
    if (!m_pDIDevice) return;

    if (m_fEffectLength != -1)
    {
        m_fEffectLength -= fFrameDuration;
        if (m_fEffectLength <= 0)
        {
            m_fEffectLength = -1;
            StopEffect();
        }
    }

    assert(m_fEffectLength == -1 || m_fEffectLength > 0);


    if (m_bUpdateDevice)
    {
        m_bUpdateDevice = false;


        HRESULT hr = DIERR_INPUTLOST;
        while (hr == DIERR_INPUTLOST) 
        {
            hr = m_pDIDevice->Acquire();
        }


        if (SUCCEEDED(hr))
        {
            if (!m_sCurrentEffectName.empty())
            {
                StopEffectOnDevice();
                StartEffectOnDevice(GetEffectPair(m_sCurrentEffectName));
            }
            else
            {
                StopEffectOnDevice();
            }
        }
    }
}
//---------------------------------------------------------------------------------------------------------------------
void
CFFDevice::StartEffectOnDevice(const EffectPair& rxPair)
{
    m_xCurrentEffectPair = rxPair;

    if (m_xCurrentEffectPair.m_spxEffect1)
    {
        HRESULT hr = m_xCurrentEffectPair.m_spxEffect1->Start(1, 0);
        assert(SUCCEEDED(hr));
    }

    if (m_xCurrentEffectPair.m_spxEffect2)
    {
        HRESULT hr = m_xCurrentEffectPair.m_spxEffect2->Start(1, 0);
        assert(SUCCEEDED(hr));
    }
}
//---------------------------------------------------------------------------------------------------------------------
void
CFFDevice::StopEffectOnDevice()
{
    if (m_xCurrentEffectPair.m_spxEffect1)
    {
        HRESULT hr = m_xCurrentEffectPair.m_spxEffect1->Stop();
        //assert(SUCCEEDED(hr));
        m_xCurrentEffectPair.m_spxEffect1 = 0;
    }

    if (m_xCurrentEffectPair.m_spxEffect2)
    {
        HRESULT hr = m_xCurrentEffectPair.m_spxEffect2->Stop();
        //assert(SUCCEEDED(hr));
        m_xCurrentEffectPair.m_spxEffect2 = 0;
    }
}
//---------------------------------------------------------------------------------------------------------------------
void
CFFDevice::GetEffectParameter(const std::string& sEffectName, 
							  DWORD flags,
							  DIEFFECT* effect1, 
							  DIEFFECT* effect2)
{
	HRESULT hr = S_OK;

	std::map<const std::string, EffectPair>::iterator it;

	it = m_mxEffects.find(sEffectName);
	assert(it != m_mxEffects.end());

	EffectPair effectPair = it->second;

	if (effectPair.m_spxEffect1 && effect1)
	{
		memset(effect1, 0, sizeof(DIEFFECT));
		effect1->dwSize = sizeof(DIEFFECT);

		hr = effectPair.m_spxEffect1->GetParameters(effect1,flags);
	}

	if (effectPair.m_spxEffect2 && effect2)
	{
		memset(effect2, 0, sizeof(DIEFFECT));
		effect2->dwSize = sizeof(DIEFFECT);

		hr = effectPair.m_spxEffect2->GetParameters(effect2,flags);
	}

	assert(SUCCEEDED(hr));
}
//---------------------------------------------------------------------------------------------------------------------
void
CFFDevice::SetEffectParameter(const std::string& sEffectName, 
							  DWORD flags,
							  DIEFFECT* effect1,
							  DIEFFECT* effect2)
{
	HRESULT hr = S_OK;

	std::map<const std::string, EffectPair>::iterator it;

	it = m_mxEffects.find(sEffectName);
	EffectPair effectPair = it->second;

	if (effectPair.m_spxEffect1 && effect1)
	{
		hr = effectPair.m_spxEffect1->SetParameters(effect1, flags);
	}

	if (effectPair.m_spxEffect2 && effect2)
	{
		hr = effectPair.m_spxEffect2->SetParameters(effect2, flags);
	}

	assert(SUCCEEDED(hr));
}
//---------------------------------------------------------------------------------------------------------------------

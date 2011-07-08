#include "soundlib/stdincludes.h"
#include "soundlib/soundsystem.h"
#include <assert.h>
#include "eax.h"

namespace SoundLib
{

CSoundSystem* CSoundSystem::ms_pxInstance = 0;
HWND		  CSoundSystem::ms_hWnd = 0;
__int64		  CSoundSystem::ms_iCounterFreq = 0;
bool		  CSoundSystem::ms_b3DSound = true;
bool		  CSoundSystem::ms_bEAXEnabled = true;


//---------------------------------------------------------------------------------------------------------------------
CSoundSystem::CListener::CListener()
{
	m_vPos	 = CVec3(0.0f, 0.0f, 0.0f);
	m_vFront = CVec3(0.0f, 0.0f, 1.0f);
	m_vUp	 = CVec3(0.0f, 1.0f, 0.0f);
	m_Matrix.SetIdentity();
}

//---------------------------------------------------------------------------------------------------------------------
CSoundSystem::CSoundSystem()
{
	assert(ms_hWnd);			// Init() vielleicht nicht vorher aufgerufen?

	HRESULT hr;
	hr = DirectSoundCreate8(NULL, &m_pxDS, NULL);
	assert(SUCCEEDED(hr));

	DetermineCaps();

	::QueryPerformanceCounter((LARGE_INTEGER*) &m_iCurrentTimeInMs);
	m_iCurrentTimeInMs /= ms_iCounterFreq;

	m_bSoundEnabled = true;
	m_iMaxVoices = 16;
	for(int i=0; i<m_iMaxVoices; ++i)
	{
		m_apxVoices.push_back(new CDSoundBuffer(m_pxDS, i));
	}


	if(!ms_b3DSound)
	{
		hr = m_pxDS->SetCooperativeLevel(ms_hWnd, DSSCL_NORMAL);
		assert(SUCCEEDED(hr));
		m_pxDSListener = 0;
		m_pPropertyset = 0;
		m_iEAXSupport = 0;
	}
	else
	{
		hr = m_pxDS->SetCooperativeLevel(ms_hWnd, DSSCL_PRIORITY);
		assert(SUCCEEDED(hr));

		DSBUFFERDESC        dsbdesc;
		LPDIRECTSOUNDBUFFER pDSBPrimary = 0;

		ZeroMemory(&dsbdesc, sizeof(DSBUFFERDESC));
		dsbdesc.dwSize = sizeof(DSBUFFERDESC);
		dsbdesc.dwFlags = DSBCAPS_CTRL3D | DSBCAPS_PRIMARYBUFFER;
		if(FAILED(m_pxDS->CreateSoundBuffer(&dsbdesc, &pDSBPrimary, NULL)))
		{
			DebugPrint("Creation of Primary Buffer failed");
			assert(false);
			return;
		}

		WAVEFORMATEX wfx;
		ZeroMemory( &wfx, sizeof(WAVEFORMATEX) );
		wfx.wFormatTag      = (WORD) WAVE_FORMAT_PCM;
		wfx.nChannels       = (WORD) 2;
		wfx.nSamplesPerSec  = (DWORD) 44100;
		wfx.wBitsPerSample  = (WORD) 16;
		wfx.nBlockAlign     = (WORD) (wfx.wBitsPerSample / 8 * wfx.nChannels);
		wfx.nAvgBytesPerSec = (DWORD) (wfx.nSamplesPerSec * wfx.nBlockAlign);

		if(FAILED(pDSBPrimary->SetFormat(&wfx)))
		{
			pDSBPrimary->Release();
			DebugPrint("Could not set primary buffer format");
			assert(false);
			return;
		}

		if(FAILED(pDSBPrimary->QueryInterface(IID_IDirectSound3DListener8,(VOID**)&m_pxDSListener)))
		{
			pDSBPrimary->Release();
			DebugPrint("InitListener(): QueryInterface() failed");        
			assert(false);
			return;
		}

		DS3DLISTENER dsListenerParams;
		dsListenerParams.dwSize = sizeof(DS3DLISTENER);
		m_pxDSListener->GetAllParameters( &dsListenerParams );
		
		dsListenerParams.flDopplerFactor = DS3D_DEFAULTDOPPLERFACTOR;
		dsListenerParams.flRolloffFactor = DS3D_DEFAULTROLLOFFFACTOR;
		
		m_pxDSListener->SetAllParameters( &dsListenerParams, DS3D_IMMEDIATE);

		pDSBPrimary->Release();


		// EAX: Property set von neuem Secondary Buffer holen

		m_iEAXSupport = 0;

		WAVEFORMATEX xWave;
		xWave.cbSize = sizeof(WAVEFORMATEX);
		xWave.nAvgBytesPerSec = 44100 * 2;
		xWave.nBlockAlign = 2;
		xWave.nChannels = 1;
		xWave.nSamplesPerSec = 44100;
		xWave.wBitsPerSample = 16;
		xWave.wFormatTag = WAVE_FORMAT_PCM;

		memset(&dsbdesc, 0, sizeof(DSBUFFERDESC)); 
		dsbdesc.dwSize = sizeof(DSBUFFERDESC); 
		dsbdesc.dwFlags = DSBCAPS_CTRLVOLUME | DSBCAPS_CTRLFREQUENCY | DSBCAPS_CTRL3D; 
		dsbdesc.dwBufferBytes = 100;
		dsbdesc.lpwfxFormat = (LPWAVEFORMATEX) &xWave; 
		dsbdesc.guid3DAlgorithm = GUID_NULL;

		LPDIRECTSOUNDBUFFER pDsb;
		hr = m_pxDS->CreateSoundBuffer(&dsbdesc, &pDsb, NULL); 
		if (SUCCEEDED(hr)) 
		{ 
			hr = pDsb->QueryInterface(IID_IKsPropertySet, (LPVOID*) &m_pPropertyset); 
			pDsb->Release();
			if(!m_pPropertyset)
			{
				DebugPrint("Error: Query for Propertyset failed!, Reason: %s", CSoundSystem::GetDSError(hr));
				return;
			}
		}
		else
		{
			DebugPrint("failed to create dummy sound buffer for property set interface...");
			return;
		}
	}

	if(ms_bEAXEnabled)
	{
		// unterstützte EAX-Version ermitteln

		ULONG iSupport = 555;
		hr = m_pPropertyset->QuerySupport(	DSPROPSETID_EAX_ListenerProperties, 
											DSPROPERTY_EAXLISTENER_ALLPARAMETERS, &iSupport);
		if(SUCCEEDED(hr))
		{
			if((iSupport & (KSPROPERTY_SUPPORT_GET | KSPROPERTY_SUPPORT_SET)) ==
						(KSPROPERTY_SUPPORT_GET | KSPROPERTY_SUPPORT_SET))
			{
				m_iEAXSupport = 2;
			}
		}
	}
	else
	{
		m_iEAXSupport = 0;
	}

	// Listeners erstellen

	SetNumListeners(1);
}


//---------------------------------------------------------------------------------------------------------------------
CSoundSystem::~CSoundSystem()
{
	for(int i=0; i<m_iMaxVoices; ++i)
	{
		delete m_apxVoices[i];
	}

	if(m_pPropertyset)		{ m_pPropertyset->Release(); }
	if(m_pxDSListener)		{ m_pxDSListener->Release(); }
	if(m_pxDS)				{ m_pxDS->Release(); }
	CWaveFactory::Shut();
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSoundSystem::Init(HWND p_hWnd, bool p_b3DSound, bool p_bEAX)
{
	ms_hWnd = p_hWnd;

	::QueryPerformanceFrequency((LARGE_INTEGER*) &ms_iCounterFreq);
	assert(ms_iCounterFreq);
	ms_iCounterFreq /= 1000;
	assert(ms_iCounterFreq);
	ms_b3DSound = p_b3DSound;
	ms_bEAXEnabled = p_bEAX;
}


//---------------------------------------------------------------------------------------------------------------------
CSoundSystem& 
CSoundSystem::Get()
{
	if(!ms_pxInstance)
	{
		ms_pxInstance = new CSoundSystem();
	}
	return *ms_pxInstance;
}


//---------------------------------------------------------------------------------------------------------------------
void CSoundSystem::Shut()
{
	if(ms_pxInstance)
	{
		delete ms_pxInstance;
		ms_pxInstance = 0;
	}
}


//---------------------------------------------------------------------------------------------------------------------
CDSoundBuffer* 
CSoundSystem::GetVoice(int p_iVoice)
{
	assert(p_iVoice >= 0  &&  p_iVoice < m_iMaxVoices);
	return m_apxVoices[p_iVoice];
}


//---------------------------------------------------------------------------------------------------------------------
void 
CSoundSystem::Tick()
{
	::QueryPerformanceCounter((LARGE_INTEGER*) &m_iCurrentTimeInMs);
	m_iCurrentTimeInMs /= ms_iCounterFreq;

	for(unsigned int i=0; i<m_apxVoices.size(); ++i)
	{
//        DebugPrint("Tick %d", i);
		m_apxVoices[i]->Tick(m_iCurrentTimeInMs);
	}

	if(GetNumListeners() > 1)
	{
		UpdateMultipleListeners();
	}

	if(m_pxDSListener)
	{
		CommitDeferredSettings();
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSoundSystem::DetermineCaps()
{
	m_xDSoundCaps.dwSize = sizeof(DSCAPS);
	m_pxDS->GetCaps(&m_xDSoundCaps);
	m_xHWCaps.m_iHW3DAllBuffers				= m_xDSoundCaps.dwMaxHw3DAllBuffers;
	m_xHWCaps.m_iHW3DStaticBuffers			= m_xDSoundCaps.dwMaxHw3DStaticBuffers;
	m_xHWCaps.m_iHW3DStreamingBuffers		= m_xDSoundCaps.dwMaxHw3DStreamingBuffers;
	m_xHWCaps.m_iHWMixingAllBuffers			= m_xDSoundCaps.dwMaxHwMixingAllBuffers;
	m_xHWCaps.m_iHWMixingStaticBuffers		= m_xDSoundCaps.dwMaxHwMixingStaticBuffers;
	m_xHWCaps.m_iHWMixingStreamingBuffers	= m_xDSoundCaps.dwMaxHwMixingStreamingBuffers;
	m_xHWCaps.m_iHWMemorySize				= m_xDSoundCaps.dwTotalHwMemBytes;
}

//---------------------------------------------------------------------------------------------------------------------
void
CSoundSystem::UpdateMultipleListeners()
{
	// wenn mehr als 1 Listener aktiv ist, müssen wir ein bisschen selbst nachrechnen und alle auf den einen
	// Hardware-Listener abbilden

	if(GetNumListeners() < 2)
	{
		return;
	}

	for(unsigned int iVoice = 0; iVoice < m_apxVoices.size(); ++iVoice)
	{
		CDSoundBuffer* pxVoice = m_apxVoices[iVoice];
		if(pxVoice->Is3DSound())
		{
//			DebugPrint("%s:", pxVoice->GetFilename().c_str());

			float fDistanceSum = 0.0f;

			for(unsigned int iListener = 0; iListener < m_aAllListeners.size(); ++iListener)
			{
				CVec3 vListenerRelativePos = (pxVoice->m_vPosition * m_aAllListeners[iListener].m_Matrix).GetReduced();

				m_aRelativeSounds[iListener].m_vRelPos = vListenerRelativePos;

				float fDistance = (pxVoice->m_vPosition - m_aAllListeners[iListener].m_vPos).AbsSquare();
				fDistanceSum += fDistance;
				m_aRelativeSounds[iListener].m_vRelDistance = fDistance;
			}


			CVec3 vFinalPos = CVec3(0.0f, 0.0f, 0.0f);
			for(unsigned int iListener = 0; iListener < m_aAllListeners.size(); ++iListener)
			{
				float fWeight = 1.0f - (m_aRelativeSounds[iListener].m_vRelDistance / fDistanceSum);
				vFinalPos = vFinalPos + (m_aRelativeSounds[iListener].m_vRelPos * fWeight);

				//DebugPrint("pos relative to listener %d, (%.2f; %.2f; %.2f) dist %.2f, weight %.2f", iListener, 
				//	m_aRelativeSounds[iListener].m_vRelPos.x(), 
				//	m_aRelativeSounds[iListener].m_vRelPos.y(), 
				//	m_aRelativeSounds[iListener].m_vRelPos.z(), 
				//	m_aRelativeSounds[iListener].m_vRelDistance,
				//	fWeight);
			}

			//DebugPrint("pos final, (%.2f; %.2f; %.2f) dist %.2f", 
			//	vFinalPos.x(), vFinalPos.y(), vFinalPos.z(), vFinalPos.AbsSquare());

			pxVoice->SetHardwarePosition(vFinalPos.x(), vFinalPos.y(), vFinalPos.z());
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CSoundSystem::SetSoundEnabled(bool p_bSound)
{
	if(p_bSound != m_bSoundEnabled)
	{
		m_bSoundEnabled = p_bSound;
		if(!p_bSound)
		{
			for(unsigned int i=0; i<m_apxVoices.size(); ++i)
			{
				if(m_apxVoices[i]->IsPlaying())
				{
					m_apxVoices[i]->Stop();
				}
			}
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
int	
CSoundSystem::FindFreeVoice(float p_fPriority)
{
	if(!m_bSoundEnabled)	{ return -1; }

	int iUseVoice = -1;
	for(unsigned int i=0; i<m_apxVoices.size(); ++i)
	{
		if(m_apxVoices[i]->IsFree())
		{
			iUseVoice = i;
			break;
		}
		else
		{
			if(m_apxVoices[i]->GetPriority() < p_fPriority)
			{
				iUseVoice = i;
				p_fPriority = m_apxVoices[i]->GetPriority();
			}
		}
	}

//	DebugPrint("FindFreeVoice = %d", iUseVoice);

	if(iUseVoice < 0)
	{
		// nix gefunden... mal gucken, warum:

		DebugPrint("Warning: no free voice found!!!");
		for(unsigned int i=0; i<m_apxVoices.size(); ++i)
		{
			if(m_apxVoices[i]->m_bLocked)
			{
				DebugPrint("voice %d is locked (last file was %s)", i, m_apxVoices[i]->GetFilename().c_str());
			}
			else if(m_apxVoices[i]->IsPlaying())
			{
				DebugPrint("voice %d is playing %s, fading: %d", i, m_apxVoices[i]->GetFilename().c_str(), m_apxVoices[i]->IsFading());
			}
		}
	}

	return iUseVoice;
}


//---------------------------------------------------------------------------------------------------------------------
int	
CSoundSystem::Play(const char* p_pcFilename, float p_fVolume, float p_fPriority, bool p_bLoop)
{
	int iVoice = FindFreeVoice(p_fPriority);
	if(iVoice >= 0)
	{
		CDSoundBuffer* c = GetVoice(iVoice);
		c->SetFile(p_pcFilename, false);
		c->SetVolume(p_fVolume);
		c->Play(p_bLoop);
	}
	return iVoice;
}

//---------------------------------------------------------------------------------------------------------------------
int							
CSoundSystem::Play3D(const char* p_pcFilename, float p_fX, float p_fY, float p_fZ, float p_fPriority, bool p_bLoop)
{
	int iVoice = FindFreeVoice(p_fPriority);
	if(iVoice >= 0)
	{
		CDSoundBuffer* c = GetVoice(iVoice);
		c->SetFile(p_pcFilename, true);
		c->SetPosition(p_fX, p_fY, p_fZ);
		c->SetMinDistance(10.0f);
		c->Play(p_bLoop);
	}
	return iVoice;
}

//---------------------------------------------------------------------------------------------------------------------
int
CSoundSystem::Play(const CMultiChannelSound* p_pxSound, bool p_bLoop)
{
	if(!p_pxSound) { return -1; }

	int iVoice = FindFreeVoice(p_pxSound->m_fPriority);
	if(iVoice >= 0)
	{
		GetVoice(iVoice)->Play(p_pxSound, p_bLoop);
	}
	return iVoice;
}


//---------------------------------------------------------------------------------------------------------------------
int							
CSoundSystem::Play(const C3DSound* p_pxSound, float p_fX, float p_fY, float p_fZ, bool p_bLoop)
{
	if(!p_pxSound) { return -1; }

	int iVoice = FindFreeVoice(p_pxSound->m_fPriority);
	if(iVoice >= 0)
	{
		bool bSuccess = GetVoice(iVoice)->Play(p_pxSound, p_fX, p_fY, p_fZ, p_bLoop);
		return bSuccess ? iVoice : -1;
	}
	else
	{
		return -1;
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CSoundSystem::SetNumListeners(int iNumberOfListeners)
{
	assert(iNumberOfListeners > 0);
	if(iNumberOfListeners > 0)
	{
		m_aAllListeners.resize(iNumberOfListeners);
		m_aRelativeSounds.resize(iNumberOfListeners);

		if(iNumberOfListeners > 1)
		{
			// Multi-Listener-Modus!!! d.h. Hardware-Listener auf 0 und in Zukunft alles selbst rechnen!

			assert(m_pxDSListener);
			if(!m_pxDSListener)		{ return false; }

			HRESULT hr1 = m_pxDSListener->SetPosition(0.0f, 0.0f, 0.0f, DS3D_DEFERRED);
			assert(SUCCEEDED(hr1));
			HRESULT hr2 = m_pxDSListener->SetOrientation(0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, DS3D_DEFERRED);
			assert(SUCCEEDED(hr2));

			return SUCCEEDED(hr1) && SUCCEEDED(hr2);
		}

		return true;
	}

	return false;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CSoundSystem::SetHardwareListenerPos(	float p_fX, float p_fY, float p_fZ, 
										float p_fFrontX, float p_fFrontY, float p_fFrontZ,
										float p_fUpX, float p_fUpY, float p_fUpZ)
{
		assert(m_pxDSListener);
		if(!m_pxDSListener)		{ return false; }

		HRESULT hr1 = m_pxDSListener->SetPosition(p_fX, p_fY, p_fZ, DS3D_DEFERRED);
		assert(SUCCEEDED(hr1));
		HRESULT hr2 = m_pxDSListener->SetOrientation(p_fFrontX, p_fFrontY, p_fFrontZ, p_fUpX, p_fUpY, p_fUpZ, DS3D_DEFERRED);
		assert(SUCCEEDED(hr2));

		return SUCCEEDED(hr1) && SUCCEEDED(hr2);
}

//---------------------------------------------------------------------------------------------------------------------
bool
CSoundSystem::SetListenerPos(	int iListener,
								float p_fX, float p_fY, float p_fZ, 
								float p_fFrontX, float p_fFrontY, float p_fFrontZ,
								float p_fUpX, float p_fUpY, float p_fUpZ)
{
	m_aAllListeners[iListener].m_vPos = CVec3(p_fX, p_fY, p_fZ);
	m_aAllListeners[iListener].m_vFront = CVec3(p_fFrontX, p_fFrontY, p_fFrontZ);
	m_aAllListeners[iListener].m_vUp = CVec3(p_fUpX, p_fUpY, p_fUpZ);

	if(iListener == 0  &&  GetNumListeners() == 1)
	{
		// 1-Listener-System (default) - direkt setzen

		return SetHardwareListenerPos(p_fX, p_fY, p_fZ, p_fFrontX, p_fFrontY, p_fFrontZ, p_fUpX, p_fUpY, p_fUpZ);
	}
	else
	{
		CVec3 vFront = CVec3(p_fFrontX, p_fFrontY, p_fFrontZ);
		vFront.Normalize();
		CVec3 vUp = CVec3(p_fUpX, p_fUpY, p_fUpZ);

	    CVec3 vRightVec = vFront ^ vUp; 
		vRightVec.Normalize();
		CVec3 vOrthognalUpVec = vRightVec ^ vFront;
		vOrthognalUpVec.Normalize();

		m_aAllListeners[iListener].m_vPos = CVec3(p_fX, p_fY, p_fZ);
		m_aAllListeners[iListener].m_vFront = vFront;
		m_aAllListeners[iListener].m_vUp = vOrthognalUpVec;

		CMat4S& mat = m_aAllListeners[iListener].m_Matrix;
	    mat(0, 0) = vRightVec.x();			mat(0, 1) = vRightVec.y();			mat(0, 2) = vRightVec.z();			mat(0, 3) = 0;
		mat(1, 0) = vOrthognalUpVec.x();	mat(1, 1) = vOrthognalUpVec.y();	mat(1, 2) = vOrthognalUpVec.z();    mat(1, 3) = 0;
		mat(2, 0) = vFront.x();				mat(2, 1) = vFront.y();				mat(2, 2) = vFront.z();				mat(2, 3) = 0;
		mat(3, 0) = p_fX;					mat(3, 1) = p_fY;					mat(3, 2) = p_fZ;					mat(3, 3) = 1;
		mat.Invert();

		return true;
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CSoundSystem::GetListenerPos( float& po_rfX, float& po_rfY, float& po_rfZ) const
{
	if(GetNumListeners() < 2)
	{
		assert(m_pxDSListener);
		if(!m_pxDSListener)		{ return false; }

		D3DVECTOR v;
		HRESULT hr = m_pxDSListener->GetPosition(&v);
		assert(SUCCEEDED(hr));

		if(SUCCEEDED(hr))
		{
			po_rfX = v.x;
			po_rfY = v.y;
			po_rfZ = v.z;
			return true;
		}
		else
		{
			return false;
		}
	}
	else
	{
		return GetListenerPos(0, po_rfX, po_rfY, po_rfZ);
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CSoundSystem::GetListenerPos(int iListener, float& po_rfX, float& po_rfY, float& po_rfZ) const
{
	po_rfX = m_aAllListeners[iListener].m_vPos.x();
	po_rfY = m_aAllListeners[iListener].m_vPos.y();
	po_rfZ = m_aAllListeners[iListener].m_vPos.z();
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool						
CSoundSystem::SetDistanceFactor(float p_fDistFactor)
{
	if(!m_pxDSListener)
	{
		return false;
	}
	HRESULT hr = m_pxDSListener->SetDistanceFactor(p_fDistFactor, DS3D_IMMEDIATE);
	return SUCCEEDED(hr);
}


//---------------------------------------------------------------------------------------------------------------------
bool						
CSoundSystem::SetRolloffFactor(float p_fRolloffFactor)
{
	if(!m_pxDSListener)
	{
		return false;
	}
	HRESULT hr = m_pxDSListener->SetRolloffFactor(p_fRolloffFactor, DS3D_IMMEDIATE);
	assert(SUCCEEDED(hr));

	return SUCCEEDED(hr);
}

//---------------------------------------------------------------------------------------------------------------------
int
CSoundSystem::GetEAXSupport() const
{
	return m_iEAXSupport;
}

//---------------------------------------------------------------------------------------------------------------------
bool						
CSoundSystem::SetSoundEnvironment(int p_iEnv)
{
	if(m_iEAXSupport < 1)
	{
		return false;
	}

	DWORD envID = GetEAXEnvironmentID(p_iEnv);
	HRESULT hr =  m_pPropertyset->Set(DSPROPSETID_EAX_ListenerProperties, 
									  DSPROPERTY_EAXLISTENER_ENVIRONMENT, 0, 0, &envID, sizeof(DWORD));
	assert(SUCCEEDED(hr));	

	return true;
}

//---------------------------------------------------------------------------------------------------------------------
DWORD	
CSoundSystem::GetEAXEnvironmentID(int p_iSoundEnvironment)
{
	DWORD envID;
	switch(p_iSoundEnvironment)
	{
		case ENV_Generic:			envID = EAX_ENVIRONMENT_GENERIC;		break;
		case ENV_PaddedCell:		envID = EAX_ENVIRONMENT_PADDEDCELL;		break;
		case ENV_Room:				envID = EAX_ENVIRONMENT_ROOM;			break;
		case ENV_BathRoom:			envID = EAX_ENVIRONMENT_BATHROOM;		break;
		case ENV_LivingRoom:		envID = EAX_ENVIRONMENT_LIVINGROOM;		break;
		case ENV_StoneRoom:			envID = EAX_ENVIRONMENT_STONEROOM;		break;
		case ENV_Auditorium:		envID = EAX_ENVIRONMENT_AUDITORIUM;		break;
		case ENV_ConcertHall:		envID = EAX_ENVIRONMENT_CONCERTHALL;	break;
		case ENV_Cave:				envID = EAX_ENVIRONMENT_CAVE;			break;
		case ENV_Arena:				envID = EAX_ENVIRONMENT_ARENA;			break;
		case ENV_Hangar:			envID = EAX_ENVIRONMENT_HANGAR;			break;
		case ENV_CarpetedHallway:	envID = EAX_ENVIRONMENT_CARPETEDHALLWAY;break;
		case ENV_Hallway:			envID = EAX_ENVIRONMENT_HALLWAY;		break;
		case ENV_StoneCorridor:		envID = EAX_ENVIRONMENT_STONECORRIDOR;	break;
		case ENV_Alley:				envID = EAX_ENVIRONMENT_ALLEY;			break;
		case ENV_Forest:			envID = EAX_ENVIRONMENT_FOREST;			break;
		case ENV_City:				envID = EAX_ENVIRONMENT_CITY;			break;
		case ENV_Mountains:			envID = EAX_ENVIRONMENT_MOUNTAINS;		break;
		case ENV_Quarry:			envID = EAX_ENVIRONMENT_QUARRY;			break;
		case ENV_Plain:				envID = EAX_ENVIRONMENT_PLAIN;			break;
		case ENV_ParkingLot:		envID = EAX_ENVIRONMENT_PARKINGLOT;		break;
		case ENV_SewerPipe:			envID = EAX_ENVIRONMENT_SEWERPIPE;		break;
		case ENV_Underwater:		envID = EAX_ENVIRONMENT_UNDERWATER;		break;
		case ENV_Drugged:			envID = EAX_ENVIRONMENT_DRUGGED;		break;
		case ENV_Dizzy:				envID = EAX_ENVIRONMENT_DIZZY;			break;
		case ENV_Psychotic:			envID = EAX_ENVIRONMENT_PSYCHOTIC;		break;
		case ENV_Count:
		default: 
			assert (false); 
			envID = 0;
	}

	return envID;
}	

//---------------------------------------------------------------------------------------------------------------------
void						
CSoundSystem::ClearSoundEnvironment()
{
	SetSoundEnvironment(ENV_Generic);
}


//---------------------------------------------------------------------------------------------------------------------
void						
CSoundSystem::CommitDeferredSettings()
{
	assert(m_pxDSListener);
	if(m_pxDSListener)
	{
		m_pxDSListener->CommitDeferredSettings();
	}
}


//---------------------------------------------------------------------------------------------------------------------
const char* CSoundSystem::GetDSError(HRESULT p_iErrorCode)
{
	switch(p_iErrorCode) 
	{
	case DS_OK:						return "DS_OK: The method succeeded.";
	case DS_NO_VIRTUALIZATION:		return "DS_NO_VIRTUALIZATION: The buffer was created, but another 3-D algorithm was substituted.";
//	case DS_INCOMPLETE:				return "DS_INCOMPLETE: The method succeeded, but not all the optional effects were obtained.";
	case DSERR_ACCESSDENIED:		return "DSERR_ACCESSDENIED: The request failed because access was denied.";
	case DSERR_ALLOCATED:			return "DSERR_ALLOCATED: The request failed because resources, such as a priority level, were already in use by another caller.";
	case DSERR_ALREADYINITIALIZED:	return "DSERR_ALREADYINITIALIZED: The object is already initialized.";
	case DSERR_BADFORMAT:			return "DSERR_BADFORMAT: The specified wave format is not supported.";
	case DSERR_BADSENDBUFFERGUID:	return "DSERR_BADSENDBUFFERGUID: The GUID specified in an audiopath file does not match a valid mix-in buffer."; 
	case DSERR_BUFFERLOST:			return "DSERR_BUFFERLOST: The buffer memory has been lost and must be restored.";
	case DSERR_BUFFERTOOSMALL:		return "DSERR_BUFFERTOOSMALL: The buffer size is not great enough to enable effects processing.";
	case DSERR_CONTROLUNAVAIL:		return "DSERR_CONTROLUNAVAIL: The buffer control (volume, pan, and so on) requested by the caller is not available. Controls must be specified when the buffer is created, using the dwFlags member of DSBUFFERDESC.";
	case DSERR_DS8_REQUIRED:		return "DSERR_DS8_REQUIRED: A DirectSound object of class CLSID_DirectSound8 or later is required for the requested functionality. For more information, see IDirectSound8 Interface.";
	case DSERR_FXUNAVAILABLE:		return "DSERR_FXUNAVAILABLE: The effects requested could not be found on the system, or they are in the wrong order or in the wrong location; for example, an effect expected in hardware was found in software.";
	case DSERR_GENERIC:				return "DSERR_GENERIC: An undetermined error occurred inside the DirectSound subsystem."; 
	case DSERR_INVALIDCALL:			return "DSERR_INVALIDCALL: This function is not valid for the current state of this object."; 
	case DSERR_INVALIDPARAM:		return "DSERR_INVALIDPARAM: An invalid parameter was passed to the returning function.";
	case DSERR_NOAGGREGATION:		return "DSERR_NOAGGREGATION: The object does not support aggregation."; 
	case DSERR_NODRIVER:			return "DSERR_NODRIVER: No sound driver is available for use, or the given GUID is not a valid DirectSound device ID.";
	case DSERR_NOINTERFACE:			return "DSERR_NOINTERFACE: The requested COM interface is not available.";
	case DSERR_OBJECTNOTFOUND:		return "DSERR_OBJECTNOTFOUND: The requested object was not found."; 
	case DSERR_OTHERAPPHASPRIO:		return "DSERR_OTHERAPPHASPRIO: Another application has a higher priority level, preventing this call from succeeding."; 
	case DSERR_OUTOFMEMORY:			return "DSERR_OUTOFMEMORY: The DirectSound subsystem could not allocate sufficient memory to complete the caller's request.";
	case DSERR_PRIOLEVELNEEDED:		return "DSERR_PRIOLEVELNEEDED: A cooperative level of DSSCL_PRIORITY or higher is required."; 
	case DSERR_SENDLOOP:			return "DSERR_SENDLOOP: A circular loop of send effects was detected.";
	case DSERR_UNINITIALIZED:		return "DSERR_UNINITIALIZED: The IDirectSound8::Initialize method has not been called or has not been called successfully before other methods were called.";
	case DSERR_UNSUPPORTED:			return "DSERR_UNSUPPORTED: The function called is not supported at this time."; 
	default:						return "unknown error code";
	}
}

//---------------------------------------------------------------------------------------------------------------------

} //namespace SoundLib

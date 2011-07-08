#include "soundlib/stdincludes.h"
#include <math.h>
#include "soundlib/soundsystem.h"
#include "soundlib/dsoundbuffer.h"
#include "soundlib/wavefile.h"
#include "soundlib/oggfile.h"
#include "baselib/macros.h"
#include "baselib/debugprint.h"
#include "baselib/str.h"

namespace SoundLib
{


//---------------------------------------------------------------------------------------------------------------------
CDSoundBuffer::CDSoundBuffer(LPDIRECTSOUND8 p_pxDS, int p_iVoiceIndex)
{
	m_iVoiceIndex	= p_iVoiceIndex;
	m_pxDS			= p_pxDS;
	m_pxDS8Buffer	= 0;
	m_pxDS83DBuffer = 0;
	m_pPropertyset	= 0;
	m_pxSoundFile	= 0;
	m_bLocked		= false;

	ResetMembers();
}


//---------------------------------------------------------------------------------------------------------------------
CDSoundBuffer::~CDSoundBuffer()
{
	ReleaseBuffer();
	ResetMembers();
}


//---------------------------------------------------------------------------------------------------------------------
void 
CDSoundBuffer::ResetMembers()
{
	m_fVolume	= 100.0f;
	m_fPitch	= 1.0f;
	m_bFading	= false;
	m_fPriority = 0.0f;
	m_bLooping  = false;

	m_bBufferInitialized = false;
	m_bStreamingBuffer = false;

	if(m_pxSoundFile)
	{
		delete m_pxSoundFile;
		m_pxSoundFile = 0;
	}
}


//---------------------------------------------------------------------------------------------------------------------
// interne Funktion: verwirft den DirectSound-Buffer
void	
CDSoundBuffer::ReleaseBuffer()
{
	if(m_pxDS8Buffer)
	{
		m_pxDS8Buffer->Release();
		m_pxDS8Buffer = 0;
	}
	if(m_pxDS83DBuffer)
	{
		m_pxDS83DBuffer->Release();
		m_pxDS83DBuffer = 0;
	}
	if(m_pPropertyset)
	{
		m_pPropertyset->Release();
		m_pPropertyset = 0;
	}
}	

//---------------------------------------------------------------------------------------------------------------------
// interne Funktion: erzeugt einen neuen DirectSound-Buffer
bool
CDSoundBuffer::CreateBuffer(LPWAVEFORMATEX pWaveFormat, int iSizeInBytes, bool p_b3DSound, bool p_bAllowStreaming)
{
	assert(m_pxDS8Buffer == 0);
	assert(m_pxDS83DBuffer == 0);
	assert(m_pPropertyset == 0);

	LPDIRECTSOUNDBUFFER pDsb = NULL;
	HRESULT hr; 

	// DSBUFFERDESC füllen. 

	DSBUFFERDESC dsbdesc; 
	memset(&dsbdesc, 0, sizeof(DSBUFFERDESC)); 
	dsbdesc.dwSize = sizeof(DSBUFFERDESC); 
	if(p_b3DSound)
	{
		if(pWaveFormat->nChannels != 1)
		{
			DebugPrint("Error: sample cannot be a 3D-Sound; it has more than one cannel!");
			return false;
		}
		if(!CSoundSystem::Get3DSound())
		{
			return false;
		}
		dsbdesc.dwFlags = DSBCAPS_CTRLVOLUME | DSBCAPS_CTRLFREQUENCY | DSBCAPS_GETCURRENTPOSITION2 | DSBCAPS_CTRL3D; 
	}
	else
	{
		dsbdesc.dwFlags = DSBCAPS_CTRLPAN | DSBCAPS_CTRLVOLUME | DSBCAPS_GETCURRENTPOSITION2 | DSBCAPS_CTRLFREQUENCY; 
	}
	dsbdesc.lpwfxFormat = pWaveFormat; 
	dsbdesc.guid3DAlgorithm = GUID_NULL;


	/// maximale größe für einen statischen Buffer sind x Sekunden (von mir willkürlich gewählt)
	int iMaxStaticSize = 2 * pWaveFormat->nAvgBytesPerSec;
	if(p_bAllowStreaming  &&  iSizeInBytes > iMaxStaticSize)
	{
		// Wavedaten sind zu groß, wir machen einen Streaming-Buffer
		m_iBufferSizeInBytes = (int) (1.0f * (float) pWaveFormat->nAvgBytesPerSec);
		dsbdesc.dwBufferBytes = m_iBufferSizeInBytes;
		m_bStreamingBuffer = true;
		m_dwBufferWriteOffset = 0;
	}
	else
	{
		// Buffergröße ist ok so, wir machen einen statischen Buffer
		m_iBufferSizeInBytes = iSizeInBytes;
		dsbdesc.dwBufferBytes = iSizeInBytes;
		m_bStreamingBuffer = false;
	}


	// Buffer erzeugen  

	hr = m_pxDS->CreateSoundBuffer(&dsbdesc, &pDsb, NULL); 
	if (SUCCEEDED(hr)) 
	{ 
		hr = pDsb->QueryInterface(IID_IDirectSoundBuffer8, (LPVOID*) &m_pxDS8Buffer); 
		if(!m_pxDS8Buffer)
		{
			DebugPrint("Error: QueryInterface for IID_IDirectSoundBuffer8 failed!, Reason: %s", CSoundSystem::GetDSError(hr));
			pDsb->Release();
			return false;
		}

		m_pxDS83DBuffer = 0;
		m_pPropertyset = 0;
		if(p_b3DSound)
		{
			hr = pDsb->QueryInterface(IID_IDirectSound3DBuffer8, (LPVOID*) &m_pxDS83DBuffer); 
			assert(SUCCEEDED(hr));
			if(!SUCCEEDED(hr)  ||  !m_pxDS83DBuffer)
			{
				DebugPrint("Error: QueryInterface for IID_IDirectSound3DBuffer8 failed!, Reason: %s", CSoundSystem::GetDSError(hr));
				pDsb->Release();
				ReleaseBuffer();
				return false;
			}
			hr = pDsb->QueryInterface(IID_IKsPropertySet, (void**) &m_pPropertyset);
			assert(SUCCEEDED(hr));
			if(!SUCCEEDED(hr)  ||  !m_pPropertyset)
			{
				DebugPrint("Error: QueryInterface for IID_IKsPropertySet failed!, Reason: %s", CSoundSystem::GetDSError(hr));
				pDsb->Release();
				ReleaseBuffer();
				return false;
			}
		}
		pDsb->Release();
	} 
	else
	{
		DebugPrint("Error: CreateSoundBuffer failed!, Reason: %s", CSoundSystem::GetDSError(hr));
		return false;
	}

	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CDSoundBuffer::FillEntireBufferFromFile()
{
	// Buffer mit Sound füllen
	LPVOID lpvWrite;
	DWORD  dwLength;

	HRESULT hr = m_pxDS8Buffer->Lock(0, 0, &lpvWrite, &dwLength, NULL, NULL, DSBLOCK_ENTIREBUFFER);
	assert(SUCCEEDED(hr));

	if (SUCCEEDED(hr))
	{
		m_pxSoundFile->FillSoundBuffer((char*) lpvWrite, dwLength, m_bLooping);
		HRESULT hr = m_pxDS8Buffer->Unlock(lpvWrite, dwLength, NULL, 0);
		assert(SUCCEEDED(hr));
		return true;
	}
	else
	{
		return false;
	}

}

//---------------------------------------------------------------------------------------------------------------------
bool		
CDSoundBuffer::SetFile(const char* p_pcFile, bool p_b3DSound)
{
	ReleaseBuffer();
	ResetMembers();
	bool bAllowStreaming = false;

	assert(m_pxSoundFile == 0);

	CStr s = p_pcFile;
	if(s.Right(4).ToLower() == ".wav"  ||  s.Right(5).ToLower() == ".wave")
	{
		m_pxSoundFile = new CWaveFile(p_pcFile);
	}
	else if(s.Right(4).ToLower() == ".ogg")
	{
		m_pxSoundFile = new COggFile(p_pcFile);
		bAllowStreaming = true;
	}
	else
	{
		// keine richtige Endung -> keine Bedienung :)
		return false;
	}

	if(!m_pxSoundFile->IsValid())
	{
		return false;
	}

	if(!CreateBuffer((LPWAVEFORMATEX) m_pxSoundFile->GetWaveFormat(), m_pxSoundFile->GetSizeInBytes(), p_b3DSound, bAllowStreaming))
	{
		return false;
	}

	// Buffer wird hier noch nicht gefüllt, um dem User vor dem Play() noch die Chance zu einem Seek() 
	// zu geben :)

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CDSoundBuffer::Play(bool p_bLoop)
{
	if(!m_pxDS8Buffer)	{ return false; }
	if(!CSoundSystem::Get().GetSoundEnabled())	{ return false; }

	m_bLooping = p_bLoop;

	if(!m_bBufferInitialized)
	{
		if(FillEntireBufferFromFile())
		{
			m_bBufferInitialized = true;
		}
		else
		{
			ReleaseBuffer();
			return false;
		}
	}

	// streaming buffer müssen immer loopen...
	DWORD dwFlags = (m_bLooping || m_bStreamingBuffer) ? DSBPLAY_LOOPING : 0;
	HRESULT hr = m_pxDS8Buffer->Play(0, 0, dwFlags);
	assert(SUCCEEDED(hr));

	return SUCCEEDED(hr);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CDSoundBuffer::Stop()
{
	if(!m_pxDS8Buffer)	{ return; }
	HRESULT hr = m_pxDS8Buffer->Stop();
	assert(SUCCEEDED(hr));
	m_bFading = false;
//	DebugPrint("stopped voice %d", m_iVoiceIndex);
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CDSoundBuffer::IsPlaying() const
{
	if(!m_pxDS8Buffer)	{ return false; }
	DWORD dwS;
	HRESULT hr = m_pxDS8Buffer->GetStatus(&dwS);
	assert(SUCCEEDED(hr));
	return (dwS&DSBSTATUS_PLAYING)!=0;
}


//---------------------------------------------------------------------------------------------------------------------
void        
CDSoundBuffer::SeekPosition(unsigned long p_iPosition)
{
	if (!m_pxDS8Buffer) { return; }
    
    HRESULT hr = m_pxDS8Buffer->SetCurrentPosition(p_iPosition);
	assert(SUCCEEDED(hr));
}


//---------------------------------------------------------------------------------------------------------------------
void		
CDSoundBuffer::Seek(double p_dTimeInSeconds)
{
	assert(m_pxSoundFile);
	if(m_pxSoundFile)
	{
		m_pxSoundFile->Seek(p_dTimeInSeconds);
	}
}


//---------------------------------------------------------------------------------------------------------------------
double
CDSoundBuffer::GetCurrentPlayTimeInSeconds()
{
	if(!m_pxDS8Buffer  ||  !IsPlaying())
	{
		return 0.0;
	}

	DWORD dwCurrentPlayCursor = 666;
	DWORD dwCurrentWriteCursor = 666;
	HRESULT hr;

	if(!m_bStreamingBuffer)
	{
		hr = m_pxDS8Buffer->GetCurrentPosition(&dwCurrentPlayCursor, &dwCurrentWriteCursor);
		assert(SUCCEEDED(hr));
		if(SUCCEEDED(hr))
		{
			return (double) dwCurrentPlayCursor / (double) m_pxSoundFile->GetBytesPerSecond();
		}
		else
		{
			return 0.0;
		}
	}
	else
	{
		int iUnplayedBytes = 0;
		hr = m_pxDS8Buffer->GetCurrentPosition(&dwCurrentPlayCursor, &dwCurrentWriteCursor);
		assert(SUCCEEDED(hr));
		if(SUCCEEDED(hr))
		{
			if(m_dwBufferWriteOffset > dwCurrentPlayCursor)
			{
				// --r--w--
				iUnplayedBytes = (int) (m_dwBufferWriteOffset - dwCurrentPlayCursor);
			}
			else
			{
				// --w--r-- oder beide gleich
				iUnplayedBytes = m_iBufferSizeInBytes - (dwCurrentPlayCursor - m_dwBufferWriteOffset);
			}

		}

		return m_pxSoundFile->GetReadCursorTime() - ((double) iUnplayedBytes / (double) m_pxSoundFile->GetBytesPerSecond());
	}
}

//---------------------------------------------------------------------------------------------------------------------
double
CDSoundBuffer::GetRemainingPlayTimeInSeconds()
{
	return GetTotalTimeInSeconds() - GetCurrentPlayTimeInSeconds();
}

//---------------------------------------------------------------------------------------------------------------------
double		
CDSoundBuffer::GetTotalTimeInSeconds() const
{
	if(m_pxSoundFile)
	{
		return m_pxSoundFile->GetTotalTimeInSeconds();
	}
	else
	{
		return 0.0;
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CDSoundBuffer::SetVolume(float p_fVolume)
{
	if (!m_pxDS8Buffer) { return; }
	p_fVolume = clamp(p_fVolume, 0.0f, 100.0f);

	if(m_fVolume != p_fVolume)
	{
		if(m_bFading)
		{
			DebugPrint("Warning: voice %d was fading, fade stopped because SetVolume was called!", m_iVoiceIndex);
		}

		m_bFading = false;
		SetVolumeInternal(p_fVolume);
	}
}


//---------------------------------------------------------------------------------------------------------------------
// interne funktion; ändert wirklich nur die Lautstärke auf dem Buffer
void 
CDSoundBuffer::SetVolumeInternal(float p_fVolume)
{
	assert(m_pxDS8Buffer);

	p_fVolume = clamp(p_fVolume, 0.0f, 100.0f);
	m_fVolume = p_fVolume;

	long lVol = -10000;
	if(p_fVolume > 0) 
	{ 
		float fPercent = p_fVolume * 0.01f; 
		float fLog     = (float) log (fPercent); 
	   lVol = long(fLog*1000.f); 
	   lVol = max(-10000, lVol);
	} 
	HRESULT hr = m_pxDS8Buffer->SetVolume(lVol);
	assert(SUCCEEDED(hr));
//	DebugPrint("set vol %d", (int) fVol);
}

//---------------------------------------------------------------------------------------------------------------------
void		
CDSoundBuffer::SetMinDistance(float p_fMinDistance)
{
	assert(m_pxDS83DBuffer);
	if(!m_pxDS83DBuffer)  { return; }
	HRESULT hr = m_pxDS83DBuffer->SetMinDistance(p_fMinDistance, DS3D_DEFERRED);
	assert(SUCCEEDED(hr));
}
//---------------------------------------------------------------------------------------------------------------------
float
CDSoundBuffer::GetMinDistance() const
{
	assert(m_pxDS83DBuffer);
	if(!m_pxDS83DBuffer)  { return -1.0f; }

	float f;
	HRESULT hr = m_pxDS83DBuffer->GetMinDistance(&f);
	assert(SUCCEEDED(hr));
	return SUCCEEDED(hr) ? f : -1.0f;
}
//---------------------------------------------------------------------------------------------------------------------
void		
CDSoundBuffer::SetMaxDistance(float p_fMaxDistance)
{
	assert(m_pxDS83DBuffer);
	if(!m_pxDS83DBuffer)  { return; }
	HRESULT hr = m_pxDS83DBuffer->SetMaxDistance(p_fMaxDistance, DS3D_DEFERRED);
	assert(SUCCEEDED(hr));
}
//---------------------------------------------------------------------------------------------------------------------
float
CDSoundBuffer::GetMaxDistance() const
{
	assert(m_pxDS83DBuffer);
	if(!m_pxDS83DBuffer)  { return -1.0f; }

	float f;
	HRESULT hr = m_pxDS83DBuffer->GetMaxDistance(&f);
	assert(SUCCEEDED(hr));
	return SUCCEEDED(hr) ? f : -1.0f;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CDSoundBuffer::SetPitch(float p_fPitch)
{
	assert(m_pxDS8Buffer);
	if (!m_pxDS8Buffer) { return; }
	
	assert(m_pxSoundFile);
	if (!m_pxSoundFile) { return; }

	if(m_fPitch != p_fPitch)
	{
		m_fPitch = p_fPitch;
		HRESULT hr = m_pxDS8Buffer->SetFrequency((DWORD) (m_fPitch * m_pxSoundFile->GetWaveFormat()->nSamplesPerSec));
		assert(SUCCEEDED(hr));
	}
}
//---------------------------------------------------------------------------------------------------------------------
bool		
CDSoundBuffer::SetPosition(float p_fX, float p_fY, float p_fZ)
{
	m_vPosition = CVec3(p_fX, p_fY, p_fZ);
	if(CSoundSystem::Get().GetNumListeners() < 2)
	{
		return SetHardwarePosition(p_fX, p_fY, p_fZ);
	}
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
bool		
CDSoundBuffer::SetHardwarePosition(float p_fX, float p_fY, float p_fZ)
{
	assert(m_pxDS83DBuffer);
	if(!m_pxDS83DBuffer) { return false; }

	HRESULT hr = m_pxDS83DBuffer->SetPosition(p_fX, p_fY, p_fZ, DS3D_DEFERRED);
	assert(SUCCEEDED(hr));

	return SUCCEEDED(hr);
}


//---------------------------------------------------------------------------------------------------------------------
bool		
CDSoundBuffer::GetHardwarePosition(float& po_fX, float& po_fY, float& po_fZ) const
{
	assert(m_pxDS83DBuffer);
	if(!m_pxDS83DBuffer) { return false; }

	D3DVECTOR v;
	HRESULT hr = m_pxDS83DBuffer->GetPosition(&v);
	assert(SUCCEEDED(hr));

	if(SUCCEEDED(hr))
	{
		po_fX = v.x;
		po_fY = v.y;
		po_fZ = v.z;
		return true;
	}
	else
	{
		return false;
	}
}


//---------------------------------------------------------------------------------------------------------------------
void 
CDSoundBuffer::Fade(float p_fTargetVolume, int p_iTimeInMs)
{
	if(p_fTargetVolume == 0.0f  &&  m_fVolume == 0.0f)
	{
		// Fadeout angeordnet; Lautstärke ist schon 0

		Stop();
	}
	else if(p_fTargetVolume != m_fVolume  ||  m_bFading)
	{
		m_iFadeStartTime	= CSoundSystem::Get().GetTimeInMs();
		m_fFadeStartVolume  = m_fVolume;
		m_fFadeTargetVolume	= p_fTargetVolume;
		m_iFadeDurationInMs	= p_iTimeInMs;
		m_bFading = true;
	}
}

//---------------------------------------------------------------------------------------------------------------------
/**
	Tick-Methode; wird vom SoundSystem::Tick aufgerufen
	p_iCurrentTime ist eine Uhrzeit in Millisekunden
*/
void 
CDSoundBuffer::Tick( __int64 p_iCurrentTimeInMs)
{
	if(!m_pxDS8Buffer) { return; }
	if(!IsPlaying())   { return; }

	if(m_bFading)
	{
		__int64 iTimeElapsed = p_iCurrentTimeInMs - m_iFadeStartTime;
		if(iTimeElapsed >= m_iFadeDurationInMs)
		{
			// fade beendet
			if(m_fFadeTargetVolume == 0.0f)
			{
//				DebugPrint("fade down complete for voice %d", m_iVoiceIndex);
				Stop();
			}
			else
			{
				SetVolumeInternal(m_fFadeTargetVolume);
			}
			m_bFading = false;
		}
		else
		{
			// fade noch nicht beendet
			float fFadePercent = (float) iTimeElapsed / (float) m_iFadeDurationInMs;
			float fCurrentVolume = m_fFadeStartVolume + fFadePercent * (m_fFadeTargetVolume - m_fFadeStartVolume);
			SetVolumeInternal(fCurrentVolume);
//			DebugPrint("fade: set volume %f", fCurrentVolume);
		}
	}

	if(m_bStreamingBuffer)
	{
		DWORD dwCurrentPlayCursor = 666;
		DWORD dwCurrentWriteCursor = 666;
		HRESULT hr;
			
		hr = m_pxDS8Buffer->GetCurrentPosition(&dwCurrentPlayCursor, &dwCurrentWriteCursor);
		assert(SUCCEEDED(hr));

		if(m_dwBufferWriteOffset != dwCurrentPlayCursor)
		{
			DWORD iBytesToWrite;
			if(dwCurrentPlayCursor > m_dwBufferWriteOffset)
			{
				iBytesToWrite = dwCurrentPlayCursor - m_dwBufferWriteOffset;
			}
			else
			{
				iBytesToWrite = m_iBufferSizeInBytes - m_dwBufferWriteOffset + dwCurrentPlayCursor;
			}
			
			DWORD dwStatus;
			hr = m_pxDS8Buffer->GetStatus(&dwStatus);
			assert(SUCCEEDED(hr));
			if(dwStatus & DSBSTATUS_BUFFERLOST)
			{
				assert(false);
			}


			char *pcWrite1, *pcWrite2;
			DWORD  dwLength1, dwLength2;
			hr = m_pxDS8Buffer->Lock( m_dwBufferWriteOffset, iBytesToWrite, 
									  (LPVOID*) &pcWrite1, &dwLength1, (LPVOID*) &pcWrite2, &dwLength2, 0);
			assert(SUCCEEDED(hr));

//            DebugPrint("buf:%s, playcursor %d, writecursor %d, writepos %d, locklen %d", m_sFilename.c_str(), dwCurrentPlayCursor, 
//				dwCurrentWriteCursor, m_dwBufferWriteOffset, iBytesToWrite);

			if(SUCCEEDED(hr))
			{
                assert(dwLength1 + dwLength2 > 0);

				bool bEof = !m_pxSoundFile->FillSoundBuffer(pcWrite1, dwLength1, m_bLooping);
				if(pcWrite2)
				{
					m_pxSoundFile->FillSoundBuffer(pcWrite2, dwLength2, m_bLooping);	
				}

				m_dwBufferWriteOffset = dwCurrentPlayCursor;
				HRESULT hr = m_pxDS8Buffer->Unlock(pcWrite1, dwLength1, pcWrite2, dwLength2);
				assert(SUCCEEDED(hr));

				if(bEof  &&  (int) (m_dwBufferWriteOffset + dwLength1) >= m_iBufferSizeInBytes)
				{
					// Daten sind zu ende und Rest des Buffers ist voll mit 0 --> loop ausschalten
					// D.h. Buffer spielt noch bis zum Ende und stoppt dann automatisch
					HRESULT hr = m_pxDS8Buffer->Play(0, 0, 0);
					assert(SUCCEEDED(hr));
					m_bStreamingBuffer = false;
					//DebugPrint("streaming buffer: stop looping");
				}
			}
			else
			{
				DebugPrint("Error: Lock of streaming IDirectSoundBuffer8 failed!, Reason: %s", CSoundSystem::GetDSError(hr));
			}
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool		
CDSoundBuffer::Play(const CMultiChannelSound* p_pxSound, bool p_bLoop)
{
	bool bSucceeded = SetFile(p_pxSound->m_sWaveFile.c_str(), false);
	if(!bSucceeded)
	{
		return false;
	}

	SetVolume(p_pxSound->m_fVolume);
	SetPitch(p_pxSound->m_fPitch);
	SetPriority(p_pxSound->m_fPriority);
	Play(p_bLoop);
	return true;
}


//---------------------------------------------------------------------------------------------------------------------
bool		
CDSoundBuffer::Play(const C3DSound* p_pxSound, float p_fX, float p_fY, float p_fZ, bool p_bLoop)
{
	bool bSucceeded = SetFile(p_pxSound->m_sWaveFile.c_str(), true);
	if(!bSucceeded)
	{
		return false;
	}

//	SetVolume(p_pxSound->m_fVolume);
	SetPitch(p_pxSound->m_fPitch);
	SetPosition(p_fX, p_fY, p_fZ);
	SetMinDistance(p_pxSound->m_fMinimumFadeDistance);
	SetMaxDistance(p_pxSound->m_fMaximumFadeDistance);
	SetPriority(p_pxSound->m_fPriority);
	CSoundSystem::Get().CommitDeferredSettings();
	Play(p_bLoop);
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
float		
CDSoundBuffer::GetVolume() const								
{ 
	assert(m_pxDS8Buffer);

	long lVol;
	HRESULT hr = m_pxDS8Buffer->GetVolume(&lVol);
	assert(SUCCEEDED(hr));

	float fVol = (float) (exp((double) lVol / 1000.0) / 0.01);
	return fVol;
}
//---------------------------------------------------------------------------------------------------------------------


} // namespace SoundLib


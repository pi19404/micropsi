#ifndef SOUNDLIB_DSOUNDBUFFER_H_INCLUDED 
#define SOUNDLIB_DSOUNDBUFFER_H_INCLUDED

#include "stdincludes.h"
#include "multichannelsound.h"
#include "genericsoundfile.h"
#include "3dsound.h"

struct OggVorbis_File;

namespace SoundLib
{

class CDSoundBuffer
{
public:

	bool		Play(const CMultiChannelSound* p_pxSound, bool p_bLoop = false);
	bool		Play(const C3DSound* p_pxSound, float p_fX, float p_fY, float p_fZ, bool p_bLoop = false);

	/// setzt eine Quelldatei für diesen Kanal; versucht, an der Endung den Typ (wav/ogg) zu erkennen 
	bool		SetFile(const char* p_pcFile, bool p_b3DSound = false);
	std::string GetFilename() const;

	bool		Play(bool p_bLoop = false);
	void		Stop();
	bool		IsPlaying() const;
	bool		IsLooping() const;
	bool		IsStreamingBuffer() const;

    void        ResetPosition();
    void        SeekPosition(unsigned long p_iPosition);
	void		Seek(double p_dTimeInSeconds);

	/// liefert true wenn dieser Buffer einen 3D-Sound enthält
	bool		Is3DSound() const;

	/// liefert die bisherige Spielzeit in Sekunden
	double		GetCurrentPlayTimeInSeconds();

	/// liefert die verbleibende Spielzeit in Sekunden
	double		GetRemainingPlayTimeInSeconds();

	/// liefert die Gesamtlänge des Buffers in Sekunden
	double		GetTotalTimeInSeconds() const;

	/// setzt die Lautstärke; Wertebereich 0.0f - 100.0f 
	void		SetVolume(float p_fVolume);
	float		GetVolume() const;

	void		SetMinDistance(float p_fMinDistance);
	float		GetMinDistance() const;

	void		SetMaxDistance(float p_fMaxDistance);
	float		GetMaxDistance() const;

	/// setzt die Priorität des Kanals
	void		SetPriority(float p_fPriority);
	float		GetPriority() const;

	/// setzt den Pitch; normal ist 1.0f
	void		SetPitch(float p_fPitch);
	float		GetPitch() const;

	/// setzt die räumliche Position der Tonquelle
	bool		SetPosition(float p_fX, float p_fY, float p_fZ);

	/// liefert die räumliche Position der Tonquelle
	bool		GetPosition(float& po_fX, float& po_fY, float& po_fZ) const;

	/// liefert die räumliche Position der Tonquelle wie sie die Hardware sieht
	bool		GetHardwarePosition(float& po_fX, float& po_fY, float& po_fZ) const;

	void		Fade(float p_fTargetVolume, int p_iTimeInMs);
	void		FadeOut(int p_iTimeInMs);
	void		FadeIn(int p_iTimeInMs, float p_fEndVol = 100.0f);

	/// liefert true wenn der Soundbuffer gerade einen Fade macht
	bool		IsFading() const;

	/// liefert true, wenn dieser Buffer im Moment nichts tut und zur Vergabe zur Verfügung steht (d.h. darf nicht "locked" sein)
	bool		IsFree() const;

	/// liefert true wenn der Soundbuffer "locked" ist, d.h. nicht per FindFreeVoice() vergeben werden sollte
	bool		IsLocked() const;

	/// setzt den Soundbuffer auf "locked", d.h. er darf nicht per FindFreeVoice() vergeben werden 
	void		Lock();

	/// hebt ein Lock() auf
	void		Unlock();

private:

	CDSoundBuffer(LPDIRECTSOUND8 p_pxDS, int p_iVoiceIndex);
	~CDSoundBuffer();

	/// setzt die räumliche Position der Tonquelle wie sie die Hardware sieht -- für internen Gebrauch --
	bool	SetHardwarePosition(float p_fX, float p_fY, float p_fZ);

	void	SetVolumeInternal(float p_fVolume);
	void	Tick(__int64 p_iCurrentTimeInMs);
	void	ResetMembers();
	void	ReleaseBuffer();	
	bool	CreateBuffer(LPWAVEFORMATEX pWaveFormat, int iSizeInBytes, bool p_b3DSound, bool p_bAllowStreaming);
	bool	FillEntireBufferFromFile();

	int							m_iVoiceIndex;				///< Voice # im CSoundSystem; für Debugzwecke

	LPDIRECTSOUND8				m_pxDS;						///< Direct Sound Interface
	LPDIRECTSOUNDBUFFER8		m_pxDS8Buffer;				///< Direct Sound Buffer Interface
	LPDIRECTSOUND3DBUFFER8		m_pxDS83DBuffer;			///< Direct Sound 3D Buffer Interface
	LPKSPROPERTYSET				m_pPropertyset;				///< Property Set Interface

	CGenericSoundFile*			m_pxSoundFile;				///< Eingabedatei	

	int							m_iBufferSizeInBytes;		///< Buffergröße in Bytes
	bool						m_bStreamingBuffer;			///< true, wenn dies ein Streaming-Buffer ist
	DWORD						m_dwBufferWriteOffset;		///< Schreibposition in den Buffer (bei Streaming)

	float						m_fVolume;					///< aktuelle Lautstärke 0.0f - 100.0f
	float						m_fPitch;					///< aktueller Pitch; 1.0f = normal
	float						m_fPriority;				///< Priorität des Kanals
	bool						m_bLooping;					///< true, wenn dieser Buffer loopt
	bool						m_bBufferInitialized;		///< true, wenn der Buffer mit Daten gefüllt wurde

	bool						m_bLocked;					///< "locked": Buffer darf nicht per FindFreeVoice vergeben werden
	bool						m_bFading;					///< Faded im Moment ja/nein
	__int64						m_iFadeStartTime;			///< Start Time of Fade
	float						m_fFadeStartVolume;			///< Ziellautstärke des Fades
	float						m_fFadeTargetVolume;		///< Ziellautstärke des Fades
	int							m_iFadeDurationInMs;		///< Dauer des Fades in ms

	CVec3						m_vPosition;				///< räumliche Position

	friend class CSoundSystem;
};

#include "dsoundbuffer.inl"

} //namespace SoundLib


#endif  // ifndef SOUNDLIB_DSOUNDBUFFER_H_INCLUDED


#ifndef SOUNDLIB_SOUNDSYSTEM_H_INCLUDED 
#define SOUNDLIB_SOUNDSYSTEM_H_INCLUDED

#include "stdincludes.h"
#include <vector>
#include "wavefactory.h"
#include "dsoundbuffer.h"
#include "multichannelsound.h"
#include "3dsound.h"

namespace SoundLib
{


class CSoundSystem
{
public:
	
	/// Initialisiert das Soundsystem, muss vor dem ersten Zugriff (Get()) einmal gerufen werden
	static  void				Init(HWND p_hWnd, bool p_b3DSound = true, bool p_bEAX = true);

	/// liefert das SoundSystem zurück (Singleton)
	static	CSoundSystem&		Get(); 

	/// zerstört das Soundsystem-Singleton
	static	void				Shut();

	/// muss so oft wie möglich gerufen werden; am besten, nachdem alle Settings geupdated wurden
	void						Tick();

	/// liefert einen bestimmte Stimme zurück
	CDSoundBuffer*				GetVoice(int p_iVoice);
	__int64						GetTimeInMs();

	void						SetSoundEnabled(bool p_bSound);
	bool						GetSoundEnabled() const;

	int							FindFreeVoice(float p_fPriority = 0.0f);
	int							Play(const char* p_pcFilename, float p_fVolume = 100.0f, float p_fPriority = 0.0f, bool p_bLoop = false);
	int							Play3D(const char* p_pcFilename, float p_fX, float p_fY, float p_fZ, float p_fPriority = 0.0f, bool p_bLoop = false);
	int							Play(const CMultiChannelSound* p_pxSound, bool p_bLoop = false);
	int							Play(const C3DSound* p_pxSound, float p_fX, float p_fY, float p_fZ, bool p_bLoop = false);

	/// setzt die Anzahl von Listeners - default (und hardwaremäßig unterstützt) ist 1; ab zwei rechnet die SoundLib den räumlichen Durchschnitt aus
	bool						SetNumListeners(int iNumberOfListeners);

	/// liefert die Anzahl von Listeners
	int							GetNumListeners() const;

	/// setzt die Position des Listeners
	bool						SetListenerPos(	float p_fX, float p_fY, float p_fZ, 
												float p_fFrontX, float p_fFrontY, float p_fFrontZ,
												float p_fUpX, float p_fUpY, float p_fUpZ);

	/// setzt die Position eines bestimmten Listeners (falls es mehr als einen gibt)
	bool						SetListenerPos(	int iListener,
												float p_fX, float p_fY, float p_fZ, 
												float p_fFrontX, float p_fFrontY, float p_fFrontZ,
												float p_fUpX, float p_fUpY, float p_fUpZ);

	/// liefert die Position des Listeners
	bool						GetListenerPos(float& po_rfX, float& po_rfY, float& po_rfZ) const;

	/// liefert die Position eines bestimmten Listeners (falls es mehrere gibt)
	bool						GetListenerPos(int iListener, float& po_rfX, float& po_rfY, float& po_rfZ) const;

	bool						SetDistanceFactor(float p_fDistFactor);
	bool						SetRolloffFactor(float p_fRolloffFactor);

	struct TCaps
	{
		int			m_iHWMixingAllBuffers;
		int			m_iHWMixingStaticBuffers;
		int			m_iHWMixingStreamingBuffers;
		int			m_iHW3DAllBuffers;
		int			m_iHW3DStaticBuffers;
		int			m_iHW3DStreamingBuffers;
		int			m_iHWMemorySize;
	};

	/// liefert die Unterstützte EAX-Version (0 = keine, 2 = EAX2)
	int							GetEAXSupport() const;

	/// liefert die Device-Caps
	const TCaps&				GetCaps() const;

	/// liefert true wenn 3D-Sound eingeschaltet ist
	static bool					Get3DSound();

	/// liefert die die textuelle Fehlermeldung zu einem DirectSound-Errorcode
	static const char*			GetDSError(HRESULT p_iErrorCode);

	enum SoundEnvironment
	{
		ENV_Generic,
		ENV_PaddedCell,
		ENV_Room,
		ENV_BathRoom,
		ENV_LivingRoom,
		ENV_StoneRoom,
		ENV_Auditorium,
		ENV_ConcertHall,
		ENV_Cave,
		ENV_Arena,
		ENV_Hangar,
		ENV_CarpetedHallway,
		ENV_Hallway,
		ENV_StoneCorridor,
		ENV_Alley,
		ENV_Forest,
		ENV_City,
		ENV_Mountains,
		ENV_Quarry,
		ENV_Plain,
		ENV_ParkingLot,
		ENV_SewerPipe,
		ENV_Underwater,
		ENV_Drugged,
		ENV_Dizzy,
		ENV_Psychotic,
		ENV_Count
	};

	/// schaltet ein bestimmtes EAX-Soundenvironment ein
	bool						SetSoundEnvironment(int p_eEnv);

	/// schaltet ein evtl. gesetztes EAX-Soundenvironment ab
	void						ClearSoundEnvironment();

private:

	CSoundSystem();
	~CSoundSystem();

	void						DetermineCaps();
	void						UpdateMultipleListeners();
	void						CommitDeferredSettings();
	static DWORD				GetEAXEnvironmentID(int p_iSoundEnvironment);						

	/// setzt die Position des Hardware-Listeners
	bool						SetHardwareListenerPos(	float p_fX, float p_fY, float p_fZ, 
														float p_fFrontX, float p_fFrontY, float p_fFrontZ,
														float p_fUpX, float p_fUpY, float p_fUpZ);

	TCaps						m_xHWCaps;				///< Hardware-Caps
	std::vector<CDSoundBuffer*>	m_apxVoices;			///< Array mit Ausgabekanälen

	static	CSoundSystem*		ms_pxInstance;			///< singleton Instanz
	static 	HWND				ms_hWnd;				///< Handle to Window
	static  __int64				ms_iCounterFreq;		///< Frequenz des Performance-Counters				
	static bool					ms_b3DSound;			///< 3DSound verfügbar
	static bool					ms_bEAXEnabled;			///< EAX global ein- oder ausgeschaltet

	LPDIRECTSOUND8				m_pxDS;					///< Direct Sound Objekt 
	LPDIRECTSOUND3DLISTENER8	m_pxDSListener;			///< 3D Listener Objekt
	LPKSPROPERTYSET				m_pPropertyset;			///< Property Set Interface

	DSCAPS						m_xDSoundCaps;			///< Device Caps

	__int64						m_iCurrentTimeInMs;		///< aktuelle Uhrzeit in Millisekunden
	int							m_iMaxVoices;			///< Maximalanzahl Stimmen

	bool						m_bSoundEnabled;		///< Sound global ein- oder ausgeschaltet
	int							m_iEAXSupport;			///< EAXSupport: 0=keiner, sonst Version 1 .. 4

	class CListener
	{
	public:
		CListener();

		CVec3	m_vPos;				///< Position des Listeners
		CVec3	m_vFront;			///< Front-Vector des Listeners
		CVec3	m_vUp;				///< Up-Vector des Listeners
		CMat4S	m_Matrix;			///< Matrix, die in das Koordinatensystem des Listeners transformiert
	};

	/// Daten für einen Sound, relativ zu einem Listener
	class CListenerRelativeSound
	{
	public:
		CVec3	m_vRelPos;			///< relative Position zum Listener
		float	m_vRelDistance;		///< relative Distanz zum Listener
	};

	std::vector<CListener>				m_aAllListeners;		///< alle Listeners
	std::vector<CListenerRelativeSound>	m_aRelativeSounds;		///< nur von UpdateMultipleListeners() benutzt, soll nicht immer neu angelegt werden

	friend class CDSoundBuffer;
};

#include "soundsystem.inl"

} //namespace SoundLib


#endif  // ifndef SOUNDLIB_SOUNDSYSTEM_H_INCLUDED


#ifndef SOUNDLIB_JUKEBOX_H_INCLUDED 
#define SOUNDLIB_JUKEBOX_H_INCLUDED

#include <string>
#include <vector>

namespace SoundLib
{

/**
	Jukebox: spielt eine Kollektion von Songs ab, wahlweise in Reihenfolge oder in zufälliger Folge

	Voraussetzungen:	- Soundsystem muss initialisiert sein
						- Tick() muss regelmäßig (pro Frame oder Gameplay-Update z.B.) aufgerufen werden
*/
class CJukebox
{
public:

	CJukebox(); 
	virtual ~CJukebox();

	/// löscht alle Songs
	void	Clear();

	/// fügt einen einzelnen Song hinzu
	void	AddSong(const char* pcNameAndPath);

	/// fügt einen Ordner mit Songs hinzu (d.h. alle Songs in diesen Ordner)
	void	AddFolder(const char* pcPath, const char* pcSearchPattern = "*.ogg");

	/// startet die Jukebox
	bool	Play();

	/// stoppt die Jukebox
	void	Stop(bool bFadeout = true);

	/// liefert true wenn die Jukebox gerade spielt
	bool	IsPlaying(); 

	/// beendet den aktuellen Song und startet den nächsten
	void	Skip();

	/// bestimmt, ob die Songs in zufälliger Folge abgespielt werden sollen
	void	SetShuffleMode(bool bShuffle);

	/// schaltet Crossfading ein oder aus
	void	SetCrossFading(bool bCrossfading);

	/// setzt die Lautstärke; Wertebereich 0.0f - 100.0f (Prozent)
	void	SetVolume(float fVolume); 

	/// schaltet die Jukebox ein oder aus (im ausgeschalteten zustand ignoriert sie Play()-Kommandos)
	void	SetEnabled(bool bEnabled);

	/// liefert true wenn die Jukebox eingeschaltet ist
	bool	GetEnabled() const;

	/// muss regelmäßig aufgerufen werden
	void	Tick();


private:

	/// erzeugt eine neue zufällige Abspielreihenfolge
	void			Reshuffle();

	/// liefert den Namen des nächsten zu spielenden Songs
	std::string		GetNextSong();

	bool	m_bShuffle;							///< zufällige Abspielreihenfolge ja oder nein
	bool	m_bCrossFading;						///< Crossfading ja oder nein
	float	m_fVolume;							///< gewünschte Lautstärke
	bool	m_bEnabled;							///< eingeschaltet?

	int		m_iVoice;							///< Ausgabekanal des Songs

	class CSong
	{
	public:
		CSong(std::string sFile) : m_sFileAndPath(sFile) {}

		std::string				m_sFileAndPath;			///< Dateiname und Pfad
		int						m_iRandomNumber;		///< Zufallszahl

		bool operator<(const CSong& rxOther) const 
		{
			return m_iRandomNumber < rxOther.m_iRandomNumber;
		}
	};

	std::vector<CSong>			m_aOrderedSongs;		///< Songs in "richtiger" Reihenfolge
	std::vector<CSong>			m_aRandomSongs;			///< Songs in "zufälliger" Reihenfolge

	int							m_iCurrentSong;			///< aktueller Titel (Index arrays)

};

} // namespace SoundLib

#endif // ifndef SOUNDLIB_JUKEBOX_H_INCLUDED 

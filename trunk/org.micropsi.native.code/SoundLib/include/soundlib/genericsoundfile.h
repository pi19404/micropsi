#ifndef SOUNDLIB_GENERICSOUNDFILE_H_INCLUDED 
#define SOUNDLIB_GENERICSOUNDFILE_H_INCLUDED

#include <string>
#include "stdincludes.h"

namespace SoundLib
{

class CGenericSoundFile
{
public: 

	CGenericSoundFile(const char* p_pcFilename);
	virtual ~CGenericSoundFile();
		
	/// liefert den Dateinamen
	std::string		GetFilename() const;

	/// liefert true, wenn diese Datei eine g�ltige Datenquelle ist (false bedeutet: Datei nicht gefunden o.�.)
	virtual bool	IsValid() const = 0;

	/// liefert die Gesamtl�nge der Datei in Sekunden
	virtual double	GetTotalTimeInSeconds() const = 0;

	/// liefert Anzahl Samples in der Datei
	virtual int		GetNumSamples() const = 0;

	/// liefert Gr��e der unkomprimierten Sounddaten in Bytes
	virtual int		GetSizeInBytes() const = 0;

	/// setzt den Lesecursor an eine bestimmte Position in der Datei
	virtual bool	Seek(double p_dTimeInSeconds) = 0;

	/// liefert eine Beschreibung des Waveformates
	virtual const WAVEFORMATEX*	GetWaveFormat() const = 0;

	/// f�llt einen Soundbuffer mit Daten; Lesecursor r�ckt weiter : liefert false, wenn EOF (und kein Looping)
	virtual bool	FillSoundBuffer(char* p_pcWrite, DWORD p_dwLength, bool p_bLoopData) = 0;

	/// liefert die Anzahl Bytes die pro Sekunde abgespielt werden
	int				GetBytesPerSecond() const;

	/// liefert den Zeitpunkt des aktuellen Lesecursors
	virtual double	GetReadCursorTime() const = 0;
		
protected:

	std::string		m_sFilename;					///< Dateiname
};

} // namespace SoundLib


#endif  // ifndef SOUNDLIB_GENERICSOUNDFILE_H_INCLUDED


#ifndef SOUNDLIB_OGGFILE_H_INCLUDED 
#define SOUNDLIB_OGGFILE_H_INCLUDED

#include "genericsoundfile.h"

struct OggVorbis_File;

namespace SoundLib
{

class COggFile: public CGenericSoundFile 
{
public:
	COggFile(const char* p_pcFilename);
	~COggFile();
	
	virtual bool	IsValid() const;
	virtual double	GetTotalTimeInSeconds() const;
	virtual int		GetNumSamples() const;
	virtual int		GetSizeInBytes() const;
	virtual bool	Seek(double p_dTimeInSeconds);
	virtual double	GetReadCursorTime() const;

	virtual const WAVEFORMATEX*	GetWaveFormat() const;
	virtual bool	FillSoundBuffer(char* p_pcWrite, DWORD p_dwLength, bool p_bLoopData);

private:
	OggVorbis_File*		m_pxOggFile;		///< OggFile; 0, wenn Datei nicht gefunden / nicht gültig
	WAVEFORMATEX		m_xWaveFormat;		///< Datenstruktur für Wave-Format
};

}

#endif  // ifndef SOUNDLIB_OGGFILE_H_INCLUDED


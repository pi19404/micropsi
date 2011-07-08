#pragma once
#ifndef SOUNDLIB_WAVEFILE_H_INCLUDED 
#define SOUNDLIB_WAVEFILE_H_INCLUDED

#include "soundlib/genericsoundfile.h"
#include "soundlib/wavefactory.h"

namespace SoundLib
{

class CWaveFile : public CGenericSoundFile
{
public:
	CWaveFile(const char* p_pcFile);
	~CWaveFile();

	virtual bool	IsValid() const;
	virtual double	GetTotalTimeInSeconds() const;
	virtual int		GetNumSamples() const;
	virtual int		GetSizeInBytes() const;
	virtual bool	Seek(double p_dTimeInSeconds);
	virtual double	GetReadCursorTime() const;

	virtual const WAVEFORMATEX*	GetWaveFormat() const;
	virtual bool	FillSoundBuffer(char* p_pcWrite, DWORD p_dwLength, bool p_bLoopData);

private:

	CWaveFactory::CWaveFileData*	m_pxData;			///< Zeiger auf Wave-Daten
	static int						ms_iWaveFiles;		///< globaler WaveFile-Zähler; eigentlich nur für Debug-Zwecke
	int								m_iReadCursor;		///< Lese-Cursor (für FillSoundBuffer)

	friend class CWaveFactory;
};


} //namespace SoundLib


#endif  // ifndef SOUNDLIB_WAVEFILE_H_INCLUDED

#ifndef SOUNDLIB_WAVEFACTORY_H_INCLUDED
#define SOUNDLIB_WAVEFACTORY_H_INCLUDED

#include "stdincludes.h"
#include <map>
#include <string>
#include <vector>
#include "baselib/str.h"

namespace SoundLib
{

class CWaveFile;

class CWaveFactory
{
public:
	
	static CWaveFactory& Get();
    static void Shut();
	
	void RegisterWaveFile(const CStr& p_ksrFilename, CWaveFile& po_xrWaveFile);
	void UnregisterWaveFile(const CWaveFile& p_krsWaveFile);

    void Prefetch(const CStr& p_krsWaveFile);
    void ClearPrefetch();

	void AddSearchPath(const CStr& p_krsPath);
	const std::vector<CStr>& GetSearchPaths() const			{ return m_asSearchPaths; }

	class CWaveFileData
	{
	public:
		CWaveFileData();
		~CWaveFileData();

		CStr			m_sFilename;			///< ursprünglicher Dateiname dieses Waves
		unsigned int	m_iNumChannels;			///< Anzahl Kanäle
		unsigned int	m_iSamplesPerSec;		///< Samples pro Sekunde (z.B. 44100 für 44.1 kHz)
		unsigned int	m_iBitsPerSample;		///< Bits pro Sample; normalerweise 8 oder 16
		unsigned int	m_iNumSamples;			///< Anzahl Samples gesamt
		unsigned int	m_iSoundDataSize;		///< Größe der Daten in Bytes

		void*			m_pSoundData;			///< eigentliche Sounddaten
		WAVEFORMATEX	m_xWaveFormat;			///< WAVE FORMAT Struktur; wird beim Laden gefüllt und kann weiterverwendet werden
	};

	static void FillWaveFormatStructure(WAVEFORMATEX& po_rxFormat, int p_iChannels, int p_iSampleRate, int p_iBits);

protected:

	CWaveFactory();
	~CWaveFactory();

	static CWaveFactory*		ms_pxInst;
	std::vector<CStr>			m_asSearchPaths;	    ///< Suchpfade für WaveFiles
    std::vector<CStr>           m_asPrefetchedFiles;    ///< direkt vom Wavemanager geladene Waves (prefetch)

	class CWaveFileEntry
	{
	public:
		int					m_iRefCount;		///< reference counter 
		CWaveFileData		m_xData;			///< Daten für diese WaveFile

		CWaveFileEntry() : m_iRefCount(0) {};
	};


    CWaveFileEntry*             CreateOrFindEntry(const CStr& p_krsWaveFile);
    bool                        ReadWaveFile(const char* p_pcName, CWaveFileData& po_rxData);


	std::map<std::string, CWaveFileEntry*> m_xFilenameMap;	///< Map zwischen Dateinamen und eigentlichen Daten

	friend class CWaveFile;

private:
	CWaveFactory(const CWaveFactory&);				
	CWaveFactory& operator=(const CWaveFactory&);
};

}	//namespace SoundLib

#endif // ifndef  SOUNDLIB_WAVEFACTORY_H_INCLUDED

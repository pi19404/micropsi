/*
	Die Klasse CMultiChannelSound enthält alle Parameter für einen Mehrkanalsound. Sie kann von Anwendungen verwendet 
	werden, um Sounds zu verwalten. Das Soundsystem kann CMultiChannelSounds direkt abspielen.
	CMultiChannelSound enthält Methoden, um die Parameter aus XML-Dateien zu lesen (per tinyxml)
*/

#pragma once
#ifndef SOUNDLIB_MULTICHANNELSOUND_H_INCLUDED 
#define SOUNDLIB_MULTICHANNELSOUND_H_INCLUDED

#include <string>
#include "soundlib/soundsystementity.h"

class TiXmlElement;

namespace SoundLib
{

class CMultiChannelSound : public CSoundSystemEntity
{
public:
	CMultiChannelSound();
	
	std::string		m_sWaveFile;				///< Dateiname des Waves
	float			m_fVolume;					///< Lautstärke im Bereich 0.0f - 100.0f
	float			m_fPitch;					///< Pitchfaktor des Samples; 1.0f = normale Geschwindigkeit
	float			m_fPriority;				///< Priorität dieses Sounds (falls Kanäle knapp sind)

	virtual void	FromXMLElement(const TiXmlElement* p_pXmlElement);
	virtual void	ToXMLElement(TiXmlElement* p_pXmlElement) const;

	bool operator==(const CMultiChannelSound& p_rxEnv);
	bool operator!=(const CMultiChannelSound& p_rxEnv);
};

} //namespace SoundLib

#endif  // ifndef SOUNDLIB_MULTICHANNELSOUND_H_INCLUDED


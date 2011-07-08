/*
	Die Klasse CSoundEnvironment enthält alle Parameter für eine EAX-Klangumgebung. Sie kann von Anwendungen 
	verwendet werden, diese zu verwalten. Das Soundsystem kann Environments direkt setzen.
	CSoundEnvironment enthält Methoden, um die Parameter aus XML-Dateien zu lesen (per tinyxml)
*/

#pragma once
#ifndef SOUNDLIB_SOUNDENVIRONMENT_H_INCLUDED 
#define SOUNDLIB_SOUNDENVIRONMENT_H_INCLUDED

#include <string>
#include "soundlib/soundsystementity.h"

class TiXmlElement;

namespace SoundLib
{

class CSoundEnvironment : public CSoundSystemEntity
{
public:
	CSoundEnvironment();
	
	int			m_iPreset;				///< vordefinierter Klangraum
	float		m_fRadius;				///< Radius


	virtual void	FromXMLElement(const TiXmlElement* p_pXmlElement);
	virtual void	ToXMLElement(TiXmlElement* p_pXmlElement) const;

	bool operator==(const CSoundEnvironment& p_rxEnv);
	bool operator!=(const CSoundEnvironment& p_rxEnv);
};

} //namespace SoundLib

#endif  // ifndef SOUNDLIB_SOUNDENVIRONMENT_H_INCLUDED


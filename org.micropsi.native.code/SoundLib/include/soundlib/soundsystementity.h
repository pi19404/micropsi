/*
	Die Klasse CSoundSystemEntity ist die Basisklasse für verschiedene Arten von Sounds und Klangumgebungen.
	CSoundSystemEntity enthält Methoden, um die Parameter aus XML-Dateien zu lesen (per tinyxml)
*/

#pragma once
#ifndef SOUNDLIB_SOUNDSYSTEMENTITY_H_INCLUDED 
#define SOUNDLIB_SOUNDSYSTEMENTITY_H_INCLUDED

#include <string>

class TiXmlElement;

namespace SoundLib
{

class CSoundSystemEntity
{
public:

	CSoundSystemEntity() {};
	virtual ~CSoundSystemEntity() {}; 

	virtual void	FromXMLElement(const TiXmlElement* p_pXmlElement) = 0;
	virtual void	ToXMLElement(TiXmlElement* p_pXmlElement) const = 0;
};

} //namespace SoundLib

#endif  // ifndef SOUNDLIB_SOUNDSYSTEMENTITY_H_INCLUDED


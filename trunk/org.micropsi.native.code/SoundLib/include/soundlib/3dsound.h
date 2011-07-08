/*
	Die Klasse C3DSound enthält alle Parameter für einen 3D-Sound. Sie kann von Anwendungen verwendet 
	werden, um Sounds zu verwalten. Das Soundsystem kann C3DSounds direkt abspielen.
	C3DSound enthält Methoden, um die Parameter aus XML-Dateien zu lesen (per tinyxml)
*/

#pragma once
#ifndef SOUNDLIB_3DSOUND_H_INCLUDED 
#define SOUNDLIB_3DSOUND_H_INCLUDED

#include "multichannelsound.h"

namespace SoundLib
{

class C3DSound : public CMultiChannelSound
{
public:
	C3DSound();
	
	float	m_fMinimumFadeDistance;				///< Entfernung, ab der der Sound beginnt, leiser zu werden
	float	m_fMaximumFadeDistance;				///< Entfernung, ab der der Sound aufhört, leiser zu werden
	float	m_fMaximumHearingDistance;			///< Entfernung, ab der der Sound völlig gestoppt wird

	virtual void	FromXMLElement(const TiXmlElement* p_pXmlElement);
	virtual void	ToXMLElement(TiXmlElement* p_pXmlElement) const;

	bool operator==(const C3DSound& p_rxEnv);
	bool operator!=(const C3DSound& p_rxEnv);
};

} //namespace SoundLib

#endif  // ifndef SOUNDLIB_3DSOUND_H_INCLUDED


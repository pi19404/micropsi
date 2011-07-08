#pragma once

#ifndef ANIMATIONSOUNDCTRL_H_INCLUDED
#define ANIMATIONSOUNDCTRL_H_INCLUDED

#include "e42/stdinc.h"
#include <map>
#include <vector>
#include "soundlib/3dsound.h"
#include "baselib/geometry/CVector.h"

class CAnimationSoundCtrl
{
public:
	CAnimationSoundCtrl();
    void Trigger(std::string p_sCurrentAnimationName, float p_fAnimTime, const CVec3& vSourcePos, std::string sSoundType = "");
	void AddSound(std::string p_sAnimationName, const TiXmlElement* p_pXmlElement);
    

private:

	class CAnimSound : public SoundLib::C3DSound
	{
	public:
		CAnimSound(); 
		void FromXMLElement(const TiXmlElement* p_pXmlElement);

		float	        m_fTriggerTime;
        std::string     m_sSoundType;				///< Soundtyp
	};


	/// Liste mit Soundalternativen
	class CSoundEvent
	{
	public: 
		float		                m_fTriggerTime;         ///< Zeitpunkt, dieses Event auszulösen (identisch für alle Sounds)
        std::string                 m_sSoundType;		    ///< Bodentyp für dieses Event
		std::vector<CAnimSound>	    m_xSoundAlternatives;	///< Array mit (alternativen) Sounds für dieses Event
	};


	/// Animationen 
	class CAnimation
	{
	public: 
		std::vector<CSoundEvent> m_xSounds;				///< alle Sounds für diese Animation
	};
    

	/// map von Animationsname auf Liste der Sounds
	std::map<std::string, CAnimation>	m_xAnimations;

	float			m_fLastTriggerTime;				///< Animationszeit des letzten Tigger()-Aufrufes
	std::string		m_fLastAnimationName;			///< Animationsname des letzten Trigger()-Aufrufes

    CVec3           m_vCurSoundSourcePos;              ///< Position der Quelle
    std::string     m_sCurSoundType;                   ///< aktueller SoundTyp
};

#endif // ANIMATIONSOUNDCTRL_H_INCLUDED


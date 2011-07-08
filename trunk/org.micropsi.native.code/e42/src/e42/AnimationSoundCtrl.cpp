#include "stdafx.h"

#include "e42/AnimationSoundCtrl.h"
#include "soundlib/soundsystem.h"

#include "baselib/xmlutils.h"

//using std::list;
using std::map;
using std::string;
using std::vector;


//------------------------------------------------------------------------------------------------------------------------------------------
CAnimationSoundCtrl::CAnimSound::CAnimSound()
{
	m_fTriggerTime = 0.0f;
} 

//------------------------------------------------------------------------------------------------------------------------------------------
void 
CAnimationSoundCtrl::CAnimSound::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
	if(!p_pXmlElement) { return; }
	m_fTriggerTime = XMLUtils::GetXMLTagFloat(p_pXmlElement, "triggertime");

	string sMaterial;
 	sMaterial = XMLUtils::GetXMLTagString(p_pXmlElement, "material");

    m_sSoundType = sMaterial;

	__super::FromXMLElement(p_pXmlElement);
}


//------------------------------------------------------------------------------------------------------------------------------------------
CAnimationSoundCtrl::CAnimationSoundCtrl()
{
	m_fLastTriggerTime = 0.0f;
}
//------------------------------------------------------------------------------------------------------------------------------------------
void 
CAnimationSoundCtrl::Trigger(string p_sCurrentAnimationName, float p_fAnimTime, const CVec3& vSourcePos, string sSoundType)
{
//	DebugPrint("Trigger %s, animtime %.5f, lasttrigger %.5f", p_sCurrentAnimationName.c_str(), p_fAnimTime, m_fLastTriggerTime);
	if(m_fLastAnimationName != p_sCurrentAnimationName)
	{
		m_fLastTriggerTime = 1000000.0f;
		m_fLastAnimationName = p_sCurrentAnimationName;
	}

	if(m_xAnimations.empty())
	{
		return;
	}

	map<string, CAnimation>::iterator iter = m_xAnimations.find(p_sCurrentAnimationName);
	if(iter == m_xAnimations.end())
	{
		return;
	}

	// Animation gefunden; anscheinend hat sie Sounds
	// jetzt alle Sounds dieser Anim durchlaufen und gucken, ob einer davon getriggert werden muss

	CAnimation& rxAnim = iter->second;
	vector<CSoundEvent>::iterator i;
	for(i=rxAnim.m_xSounds.begin(); i!=rxAnim.m_xSounds.end(); i++)
	{
		CSoundEvent& rxEvent = *i;
	
		if (rxEvent.m_sSoundType.empty() ||
		    rxEvent.m_sSoundType == sSoundType)
		{
		    // es gibt 6 mögliche Reihenfolgen für p_fAnimTime, m_fLastTriggerTime und m_fTriggerTime...
		    // ... in 3 davon liegt der m_fTriggerTime  zwischen m_fLastTriggerTime und m_fTriggerTime
		    // ... oder es tritt der glückliche Fall ein, dass wir die TriggerTime *genau* erwischen

		    float fTriggerTime = rxEvent.m_fTriggerTime;
		    if((p_fAnimTime < m_fLastTriggerTime	&&  m_fLastTriggerTime < fTriggerTime)  ||
			    (fTriggerTime < p_fAnimTime			&&  p_fAnimTime < m_fLastTriggerTime) ||
			    (m_fLastTriggerTime < fTriggerTime	&&  fTriggerTime < p_fAnimTime)  ||
			    (p_fAnimTime == fTriggerTime		&&  p_fAnimTime != m_fLastTriggerTime))
		    {
			    // offenbar muss der Sound jetzt getriggert werden; falls mehrere Alternativen zur Auswahl sind,
			    // wählen wir irgend eine
    			
			    int iSnd = 0;
			    if(rxEvent.m_xSoundAlternatives.size() > 1)
			    {
				    iSnd = rand() % (int) rxEvent.m_xSoundAlternatives.size();
    //				DebugPrint("Alt %d", iSnd);
				}
				CVec3 vListenerPos;
				SoundLib::CSoundSystem::Get().GetListenerPos(vListenerPos.x(), vListenerPos.y(), vListenerPos.z());
				float fDist = (vListenerPos - vSourcePos).Abs();

				if(true)
				{
					// als 2D spielen
	
					if(fDist > 7.0f)
					{
						// zu weit weg, kein Sound
						return;
					}
					SoundLib::CSoundSystem::Get().Play( &(rxEvent.m_xSoundAlternatives[iSnd]));
					//DebugPrint("play %s (triggertime %.5f, animtime %.5f, lasttrigger %.5f", 
			  //  		rxEvent.m_xSoundAlternatives[iSnd].m_sWaveFile.c_str(),
			  //  		fTriggerTime, p_fAnimTime, m_fLastTriggerTime);
				}
				else
				{
					// als 3D spielen

					SoundLib::CSoundSystem::Get().Play( &(rxEvent.m_xSoundAlternatives[iSnd]), vSourcePos.x(), vSourcePos.y(), vSourcePos.z());
					DebugPrint("play %s (triggertime %.5f, animtime %.5f, lasttrigger %.5f", 
			    		rxEvent.m_xSoundAlternatives[iSnd].m_sWaveFile.c_str(),
			    		fTriggerTime, p_fAnimTime, m_fLastTriggerTime);

					DebugPrint("Loc: %.2f %.2f %.2f", vSourcePos.x(), vSourcePos.y(), vSourcePos.z());
					DebugPrint("listener @ %.2f %.2f %.2f   - dist is %.2f", vListenerPos.x(), vListenerPos.y(), vListenerPos.z(), fDist);
				}
			}
		}
	}

	m_fLastTriggerTime = p_fAnimTime;
}


//------------------------------------------------------------------------------------------------------------------------------------------
void 
CAnimationSoundCtrl::AddSound(string p_sAnimationName, const TiXmlElement* p_pXmlElement)
{
	CAnimation& rxAnim = m_xAnimations[p_sAnimationName];
	CAnimSound xSound;
	xSound.FromXMLElement(p_pXmlElement);

	// suchen, ob wir schon Sounds mit gleicher TriggerTime und gleichen Bodentypen haben 
	// (dann wären diese Sounds Alternativen, von denen jedes mal eine zufällig ausgewählt werden soll)

	vector<CSoundEvent>::iterator i;
	for(i=rxAnim.m_xSounds.begin(); i!=rxAnim.m_xSounds.end(); i++)
	{
		if((*i).m_fTriggerTime == xSound.m_fTriggerTime  &&
			(*i).m_sSoundType  ==  xSound.m_sSoundType)
		{
			(*i).m_xSoundAlternatives.push_back(xSound);
			return;
		}
	}

	// nichts gefunden? Dann neues Listenelement anlegen!
    CSoundEvent xEvent;
	xEvent.m_fTriggerTime = xSound.m_fTriggerTime;
	xEvent.m_sSoundType = xSound.m_sSoundType;
	xEvent.m_xSoundAlternatives.push_back(xSound);
    rxAnim.m_xSounds.push_back(xEvent);
}
//------------------------------------------------------------------------------------------------------------------------------------------

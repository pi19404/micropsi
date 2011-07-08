#pragma once
#ifndef SCRIPTEDWORLDOBJECT_H_INCLUDED
#define SCRIPTEDWORLDOBJECT_H_INCLUDED

#include <string>
#include <vector>

#include "World/WorldObject.h"

class CWorld;

class CScriptedWorldObject : public CWorldObject
{
public:

	CScriptedWorldObject(	CWorld* p_pxWorld);
	CScriptedWorldObject(	CWorld* p_pxWorld, 
							const char* p_pcClassName, 
							float p_fX, float p_fY, float p_fZ, 
							float p_fHeight, float p_fOrientationAngle = 0.0f, __int64 p_iID = INVALID_OBJID);
	virtual ~CScriptedWorldObject();

	virtual	bool			Tick(); 

	virtual void			FromXMLElement(const TiXmlElement* p_pXmlElement);
	virtual void			ToXMLElement(TiXmlElement* p_pXmlElement) const;

	void					AddWayPoint(const CVec3& p_vrPos);

private: 

	std::vector<CVec3>	m_avMovementQueue;
	int					m_iMovementQueuePos;
};

#endif // SCRIPTEDWORLDOBJECT_H_INCLUDED

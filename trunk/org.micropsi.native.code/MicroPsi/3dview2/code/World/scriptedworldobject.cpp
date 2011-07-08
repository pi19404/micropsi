
#include "Application/stdinc.h"
#include "World/scriptedworldobject.h"

#include "Application/3dview2.h"

#include "baselib/xmlutils.h"

#include "World/world.h"
#include "World/objectmanager.h"

#include "Utilities/micropsiutils.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
CScriptedWorldObject::CScriptedWorldObject(CWorld* p_pxWorld) : CWorldObject(p_pxWorld)
{
	m_iMovementQueuePos = 0;
}

//---------------------------------------------------------------------------------------------------------------------
CScriptedWorldObject::CScriptedWorldObject(CWorld* p_pxWorld, 
										   const char* p_pcClassName, 
										   float p_fX, float p_fY, float p_fZ, 
										   float p_fHeight, float p_fOrientationAngle, __int64 p_iID) : 
	CWorldObject(p_pxWorld, p_pcClassName, p_fX, p_fY, p_fZ, p_fHeight, p_fOrientationAngle, p_iID)
{
	m_iMovementQueuePos = 0;
}


//---------------------------------------------------------------------------------------------------------------------
/**
*/
CScriptedWorldObject::~CScriptedWorldObject()
{
}


//---------------------------------------------------------------------------------------------------------------------
void					
CScriptedWorldObject::AddWayPoint(const CVec3& p_vrPos)
{
	m_avMovementQueue.push_back(p_vrPos);
	SetPSIPos(m_avMovementQueue[0]);
	GetWorld()->GetObjectManager()->StartToTick(GetID());
}


//---------------------------------------------------------------------------------------------------------------------
/**
	tick method; performs movement of objects
	\return		false if this object does not want to tick any more; true otherwise 
*/
bool 
CScriptedWorldObject::Tick()
{
	if(GetPSIPos() == m_avMovementQueue[m_iMovementQueuePos])
	{
		m_iMovementQueuePos++;
		if(m_iMovementQueuePos >= (int) m_avMovementQueue.size())
		{
			m_iMovementQueuePos = 0;
		}
	}

	CVec3 vPos = GetPSIPos();
	CVec3 vTarget = m_avMovementQueue[m_iMovementQueuePos];
	if(vPos == vTarget)
	{
		return true;
	}

	float fSpeed = 0.05f;

	CVec3 vDir = vTarget - vPos;
	float fDist = vDir.Abs();

	float fAngle;
	CVec3 q = vDir; 
	float fDist2=q.x() * q.x() + q.y() * q.y();
	if (fDist2 == 0.0f) {fAngle = 0.0f;}
	if (q.y() > 0)	{fAngle = (acosf(q.x() / sqrtf(fDist2)));}
			else	{fAngle = -(acosf(q.x() / sqrtf(fDist2)));}

	if(fDist < fSpeed)
	{
		vPos = vTarget;
	}
	else
	{
		vPos += (vDir.GetNormalized() * fSpeed);
	}

	SetPSIPos(vPos, Utils::EngineAngle2Psi(fAngle));

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
void					
CScriptedWorldObject::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
	__super::FromXMLElement(p_pXmlElement);
}


//---------------------------------------------------------------------------------------------------------------------
void					
CScriptedWorldObject::ToXMLElement(TiXmlElement* p_pXmlElement) const
{
	__super::ToXMLElement(p_pXmlElement);
}

//---------------------------------------------------------------------------------------------------------------------

#include "stdafx.h"
#include "GameLib/Physics/DOPlane.h"
#include "GameLib/Physics/DynamicsSimulation.h"
#include "GameLib/Physics/DynamicsWorld.h"
#include "GameLib/Physics/ODEUtils.h"

//---------------------------------------------------------------------------------------------------------------------
CDOPlane::CDOPlane(CDynamicsSimulation* pSim, float a, float b, float c, float d) : CDynamicsObject(pSim)
{
	m_pPlane = new dPlane(*pSim->GetCollisionSpace(), a, b, c, d);
}

//---------------------------------------------------------------------------------------------------------------------
CDOPlane::~CDOPlane()
{
	delete m_pPlane;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CDOPlane::SetPosition(const CVec3& vPos)
{
	m_pPlane->setPosition(vPos.x(), vPos.y(), vPos.z());
}
//---------------------------------------------------------------------------------------------------------------------
CVec3	
CDOPlane::GetPosition() const
{
	const dReal* p = m_pPlane->getPosition();
	return CVec3((float) p[0], (float) p[1], (float) p[2]);
}
//---------------------------------------------------------------------------------------------------------------------
void	
CDOPlane::SetRotation(const CQuat& vQuat)
{
	dQuaternion q;
	m_pPlane->setRotation(ODEUtils::ToODEQuaternion(q, vQuat));
}
//---------------------------------------------------------------------------------------------------------------------
CQuat	
CDOPlane::GetRotation() const
{
	dQuaternion q;
	m_pPlane->getQuaternion(q);
	return ODEUtils::ToQuaternion(q);
}
//---------------------------------------------------------------------------------------------------------------------

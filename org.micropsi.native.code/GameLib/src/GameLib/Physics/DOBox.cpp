#include "stdafx.h"
#include "GameLib/Physics/DOBox.h"
#include "GameLib/Physics/DynamicsSimulation.h"
#include "GameLib/Physics/DynamicsWorld.h"
#include "GameLib/Physics/ODEUtils.h"
#include "e42/utils/GfxDebugMarker.h"

//---------------------------------------------------------------------------------------------------------------------
CDOBox::CDOBox(CDynamicsSimulation* pSim, float lx, float ly, float lz) : CDynamicsObject(pSim)
{
	m_pBody = new dBody(*pSim->GetWorld());
	m_pBody->setData(this);
	m_pBody->setPosition(0, 0, 0);
	dMatrix3 mRotation;
	dRSetIdentity(mRotation);
	m_pBody->setRotation(mRotation);

	m_pCollisionBox = new dBox(*pSim->GetCollisionSpace(), lx, ly, lz);
	m_pCollisionBox->setBody(*m_pBody);

	dMass m; 
	dMassSetBoxTotal(&m, 0.5f, lx, ly, lz);
	m_pBody->setMass(&m);
}

//---------------------------------------------------------------------------------------------------------------------
CDOBox::~CDOBox()
{
	delete m_pBody;
	delete m_pCollisionBox;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CDOBox::SetPosition(const CVec3& vPos)
{
	m_pBody->setPosition(vPos.x(), vPos.y(), vPos.z());
}
//---------------------------------------------------------------------------------------------------------------------
CVec3	
CDOBox::GetPosition() const
{
	const dReal* p = m_pBody->getPosition();
	return CVec3((float) p[0], (float) p[1], (float) p[2]);
}
//---------------------------------------------------------------------------------------------------------------------
void	
CDOBox::SetRotation(const CQuat& vQuat)
{
	dQuaternion q;
	m_pBody->setRotation(ODEUtils::ToODEQuaternion(q, vQuat));
}
//---------------------------------------------------------------------------------------------------------------------
CQuat	
CDOBox::GetRotation() const
{
	return ODEUtils::ToQuaternion(m_pBody->getQuaternion());
}
//---------------------------------------------------------------------------------------------------------------------
CVec3
CDOBox::GetEdgeLengths() const
{
	dVector3 vLen;
	m_pCollisionBox->getLengths(vLen);
	return CVec3((float) vLen[0], (float) vLen[1], (float) vLen[2]);
}
//---------------------------------------------------------------------------------------------------------------------
void
CDOBox::Render(const CCamera& camera)
{
	CMat4S mat;
	GetMatrix(mat);
	CVec3 vLengths = GetEdgeLengths();
	CGfxDebugMarker::Get().DrawBox( mat, &camera, vLengths.x(), vLengths.y(), vLengths.z());
}
//---------------------------------------------------------------------------------------------------------------------

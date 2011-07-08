#include "stdafx.h"
#include "GameLib/Physics/DOSimpleBuggy.h"
#include "GameLib/Physics/DynamicsSimulation.h"
#include "GameLib/Physics/DynamicsWorld.h"
#include "GameLib/Physics/ODEUtils.h"
#include "e42/utils/GfxDebugMarker.h"

//---------------------------------------------------------------------------------------------------------------------
CDOSimpleBuggy::CDOSimpleBuggy(CDynamicsSimulation* pSim) : CDynamicsObject(pSim)
{
	// globale Parameter
	m_vChassisSize = CVec3(1.0f, 0.5, 2.0f);
	m_fWheelRadius = 0.25f;
	m_fWheelWidth  = 0.1f;
	m_fWheelMass   = 0.3f;
	m_fChassisMass = 1.0f;

	// Chassis erzeugen:
	
	// -- Chassis Body
	m_pChassisBody = new dBody(*pSim->GetWorld());
	m_pChassisBody->setData(this);

	dMass m; 
	dMassSetBoxTotal(&m, m_fChassisMass, m_vChassisSize.x(), m_vChassisSize.y(), m_vChassisSize.z());
	m_pChassisBody->setMass(&m);
	m_pChassisBody->setAutoDisableFlag(0);

	// -- Chassis Geometrie
	m_pChassisGeometry = new dBox(*pSim->GetCollisionSpace(), m_vChassisSize.x(), m_vChassisSize.y(), m_vChassisSize.z());
	m_pChassisGeometry->setBody(*m_pChassisBody);

	m_pChassisBody->setPosition(0.0, 0.5, 0.0);

	// Räder erzeugen

	for(int i=0; i<W_NumWheels; ++i)
	{
		bool bLeftWheel  = (i == W_FrontLeft || i == W_RearLeft);
		bool bFrontWheel = (i == W_FrontLeft || i == W_FrontRight);

		// -- Rad Body
		m_axWheels[i].m_pWheelBody = new dBody(*pSim->GetWorld());
		m_axWheels[i].m_pWheelBody->setAutoDisableFlag(0);
//		m_axWheels[i].m_pWheelBody->setFiniteRotationMode(1);

		dMass m; 
		dMassSetCylinderTotal(&m, m_fWheelMass, 1, m_fWheelRadius, m_fWheelWidth);		// wheelachse = x (1) ??

		// -- Rad Geometrie
		m_axWheels[i].m_pWheelGeometry = new dSphere(*pSim->GetCollisionSpace(), m_fWheelRadius);
		m_axWheels[i].m_pWheelGeometry->setBody(*m_axWheels[i].m_pWheelBody);
	
		m_axWheels[i].m_pWheelBody->setPosition(m_vChassisSize.x() / 2 * (bLeftWheel ? -1 : 1),
												1.0f,
												m_vChassisSize.z() / 2 * (bFrontWheel ? 1 : -1));

		// -- Joint 
		
		m_axWheels[i].m_pJoint = new dHinge2Joint(*pSim->GetWorld());
		m_axWheels[i].m_pJoint->attach(*m_pChassisBody, *m_axWheels[i].m_pWheelBody);

		m_axWheels[i].m_pJoint->setAnchor(	m_vChassisSize.x() / 4 * (bLeftWheel ? -1 : 1),
											1.0f,
											m_vChassisSize.z() / 2 * (bFrontWheel ? 1 : -1));
		m_axWheels[i].m_pJoint->setAxis1(0, 1, 0);
		m_axWheels[i].m_pJoint->setAxis2((bLeftWheel ? 1.0f : -1.0f), 0, 0);
	}
}

//---------------------------------------------------------------------------------------------------------------------
CDOSimpleBuggy::~CDOSimpleBuggy()
{
	delete m_pChassisBody;
	delete m_pChassisGeometry;
	for(int i=0; i<W_NumWheels; ++i)
	{
		delete m_axWheels[i].m_pWheelBody;
		delete m_axWheels[i].m_pWheelGeometry;
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CDOSimpleBuggy::Update()
{
	// Achse für den finite Rotation Mode setzen --> ist die Drehachse des Rades, also immer Achse2 des Joints 
	for(int i=0; i<W_NumWheels; ++i) 
	{
		dVector3 vAxis;
		m_axWheels[i].m_pJoint->getAxis2(vAxis);
		m_axWheels[i].m_pWheelBody->setFiniteRotationAxis(vAxis[0], vAxis[1], vAxis[2]);
	}

	double dSpeed = 1.5;
	for(int i=0; i<W_NumWheels; ++i) 
	{
		double dDirection = (i == W_FrontLeft || i == W_RearLeft) ? -1.0 : 1.0;

//		m_axWheels[i].m_pJoint->setParam(dParamFMax2, dSpeed);
		m_axWheels[i].m_pJoint->setParam(dParamVel2, dDirection * dSpeed);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CDOSimpleBuggy::SetPosition(const CVec3& vPos)
{
//	m_pBody->setPosition(vPos.x(), vPos.y(), vPos.z());
}
//---------------------------------------------------------------------------------------------------------------------
CVec3	
CDOSimpleBuggy::GetPosition() const
{
	const dReal* p = m_pChassisBody->getPosition();
	return CVec3((float) p[0], (float) p[1], (float) p[2]);
}
//---------------------------------------------------------------------------------------------------------------------
void	
CDOSimpleBuggy::SetRotation(const CQuat& vQuat)
{
}
//---------------------------------------------------------------------------------------------------------------------
CQuat	
CDOSimpleBuggy::GetRotation() const
{
	return ODEUtils::ToQuaternion(m_pChassisBody->getQuaternion());
}
//---------------------------------------------------------------------------------------------------------------------
void
CDOSimpleBuggy::Render(const CCamera& camera)
{
	// Draw Chassis

	const dReal* q = m_pChassisBody->getQuaternion();
	const dReal* v = m_pChassisBody->getPosition();

	CMat4S mat = CMat4S::CalcMatrix( CQuat((float) q[1], (float) q[2], (float) q[3], (float) q[0]), 
									 CVec3((float) v[0], (float) v[1], (float) v[2] ) );

	CGfxDebugMarker::Get().DrawBox( mat, &camera, m_vChassisSize.x(), m_vChassisSize.y(), m_vChassisSize.z());

	// Draw Wheels

	for(int i=0; i<W_NumWheels; ++i)
	{
		const dReal* q = m_axWheels[i].m_pWheelBody->getQuaternion();
		const dReal* v = m_axWheels[i].m_pWheelBody->getPosition();

		CMat4S matWheel = CMat4S::CalcMatrix( CQuat((float) q[1], (float) q[2], (float) q[3], (float) q[0]), 
											  CVec3((float) v[0], (float) v[1], (float) v[2]) );

		CGfxDebugMarker::Get().DrawSphere(matWheel, &camera, m_fWheelRadius);
	}

	// Draw Wheel Anchors and Axis

	for(int i=0; i<W_NumWheels; ++i)
	{
		ODEUtils::DrawHinge2(m_axWheels[i].m_pJoint, &camera);
	}


}
//---------------------------------------------------------------------------------------------------------------------
float 
CDOSimpleBuggy::GetVelocityInKmH() const
{
	dReal const * cv = dBodyGetLinearVel(*m_pChassisBody);
	float v2 = (float) (cv[0]*cv[0] + cv[1]*cv[1] + cv[2]*cv[2]);
	float v = sqrtf(v2) / 1000 * 3600;
	return v;	
}
//---------------------------------------------------------------------------------------------------------------------

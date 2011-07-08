#include "stdafx.h"
#include "GameLib/Physics/DynamicsWorld.h"

//---------------------------------------------------------------------------------------------------------------------
CDynamicsWorld::CDynamicsWorld()
{
	m_pWorld		= new dWorld();
	m_bQuickStep	= false;

	SetGravity(CVec3(0.0f, -9.81f, 0.0f));
	SetErrorReductionParameter(0.8);
	SetConstraintForceMixing(1e-10);
	SetContactSurfaceLayer(0.001);

	m_pWorld->setAutoDisableFlag(1);

	dWorldSetContactMaxCorrectingVel(*m_pWorld, 0.1);

	SetUseQuickStep(true);
	SetQuickStepNumIterations(20);		// sollte ohnehin das default sein
}
//---------------------------------------------------------------------------------------------------------------------
CDynamicsWorld::~CDynamicsWorld()
{
	delete m_pWorld;
}
//---------------------------------------------------------------------------------------------------------------------
void
CDynamicsWorld::SetGravity(CVec3 vGravity)
{
	m_pWorld->setGravity(vGravity.x(), vGravity.y(), vGravity.z());
}
//---------------------------------------------------------------------------------------------------------------------
CVec3	
CDynamicsWorld::GetGravity() const
{
	dVector3 g;
	m_pWorld->getGravity(g);
	return CVec3((float) g[0], (float) g[1], (float) g[2]);
}
//---------------------------------------------------------------------------------------------------------------------
void	
CDynamicsWorld::Update(double dDeltaTimeInSeconds)
{
	if(m_bQuickStep)
	{
		dWorldQuickStep(*m_pWorld, dDeltaTimeInSeconds); 
	}
	else
	{
		dWorldStep(*m_pWorld, dDeltaTimeInSeconds);
	}
}
//---------------------------------------------------------------------------------------------------------------------

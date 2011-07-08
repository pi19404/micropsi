#ifndef GAMELIB_DYNAMICSOBJECTPLANE_H_INCLUDED
#define GAMELIB_DYNAMICSOBJECTPLANE_H_INCLUDED

#include "GameLib/Physics/DynamicsObject.h"
class dPlane;

class CDOPlane : public CDynamicsObject
{
public:

	CDOPlane(CDynamicsSimulation* pSim, float a, float b, float c, float d);
	~CDOPlane();

	virtual void	SetPosition(const CVec3& vPos);	
	virtual CVec3	GetPosition() const;
	virtual void	SetRotation(const CQuat& vQuat);
	virtual CQuat	GetRotation() const;

private:

	dPlane* m_pPlane;
};

#endif // GAMELIB_DYNAMICSOBJECTPLANE_H_INCLUDED
#ifndef GAMELIB_DYNAMICSOBJECTBOX_H_INCLUDED
#define GAMELIB_DYNAMICSOBJECTBOX_H_INCLUDED

#include "GameLib/Physics/DynamicsObject.h"
class dBox;

class CDOBox : public CDynamicsObject
{
public:

	CDOBox(CDynamicsSimulation* pSim, float lx, float ly, float lz);
	~CDOBox();

	virtual void	SetPosition(const CVec3& vPos);	
	virtual CVec3	GetPosition() const;
	virtual void	SetRotation(const CQuat& vQuat);
	virtual CQuat	GetRotation() const;
	
	CVec3			GetEdgeLengths() const;

	virtual	void	Render(const CCamera& camera);

private:

	dBody*	m_pBody;
	dBox*	m_pCollisionBox;
};


#endif // GAMELIB_DYNAMICSOBJECTBOX_H_INCLUDED


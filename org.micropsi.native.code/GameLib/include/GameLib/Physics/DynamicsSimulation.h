#ifndef GAMELIB_DYNAMICSSIMULATION_H_INCLUDED
#define GAMELIB_DYNAMICSSIMULATION_H_INCLUDED

class CDynamicsWorld;
class CDynamicsObject;
class dSpace;
class dJointGroup;

struct dxGeom;
typedef dxGeom* dGeomID;

class CDynamicsSimulation
{
public:

	CDynamicsSimulation();
	~CDynamicsSimulation();

	void	Update(double dDeltaTime);

	CDynamicsWorld*			GetWorld() const;
	dSpace*					GetCollisionSpace() const; 

private:

	CDynamicsWorld*			m_pWorld;							///< Simulationswelt
	dSpace*					m_pSpace;							///< globaler Space für alle Objekte
	dJointGroup*			m_pContactJointGroup;				///< enthält alle Contact-Joints, die das Kollisionssystem erzeugt

	static const int		MAXCONTACTS = 16;

	static void		PotentialCollisionCallback (void *data, dGeomID o1, dGeomID o2);
};

#include "DynamicsSimulation.inl"

#endif // GAMELIB_DYNAMICSSIMULATION_H_INCLUDED
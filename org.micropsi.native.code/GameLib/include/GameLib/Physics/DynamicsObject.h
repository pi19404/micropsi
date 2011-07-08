#ifndef GAMELIB_DYNAMICSOBJECT_H_INCLUDED
#define GAMELIB_DYNAMICSOBJECT_H_INCLUDED

#include "baselib/geometry/CVector.h"
#include "baselib/geometry/Quaternion.h"

class dBody;
class dGeom;
class CDynamicsSimulation;
class CSquareMatrix3; 
class CSquareMatrix4; 
class CCamera;

typedef CSquareMatrix3 CMat3S;
typedef CSquareMatrix4 CMat4S;

class CDynamicsObject
{
public:

	enum ContactAction
	{
		CA_CreateDefaultContacts,					///< die Checkfunktion möchte nichts besonderes tun; bitte defaults generieren
		CA_DontCollide,								///< die Checkfunktion hat entschieden, dass die Objekte nicht kollidieren sollen
		CA_ConfiguredContacts,						///< die Checkfunktion hat die Kontakte konfiguriert
	};

	CDynamicsObject(CDynamicsSimulation* pSim) : m_pSimulation(pSim) {};
	virtual ~CDynamicsObject() {};

	virtual void	SetPosition(const CVec3& vPos) = 0;	
	virtual CVec3	GetPosition() const = 0;

	virtual void	SetRotation(const CQuat& vQuat) = 0;
	virtual CQuat	GetRotation() const = 0;

	
	/// get position and rotation as a combined matrix
	void			GetMatrix(CMat4S& rMatrix) const;

	/// set the individual contacts
	virtual ContactAction CheckContact( dContact* contact, 
										DWORD nbContacts,
										dBodyID idMyBody, dGeomID idMyGeom, dBodyID idOtherBody, dGeomID idOtherGeom);

	/// Renderfunktion; für Debuggingzwecke
	virtual	void	Render(const CCamera& camera);

protected:
	
	CDynamicsSimulation* m_pSimulation;

private:

	/// not allowed
	CDynamicsObject(const CDynamicsObject&);
	CDynamicsObject& operator=(const CDynamicsObject&);
};

#endif // GAMELIB_DYNAMICSOBJECT_H_INCLUDED
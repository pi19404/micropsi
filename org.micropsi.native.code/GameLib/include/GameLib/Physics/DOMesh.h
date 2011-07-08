#ifndef GAMELIB_DYNAMICSOBJECTMESH_H_INCLUDED
#define GAMELIB_DYNAMICSOBJECTMESH_H_INCLUDED

#include "GameLib/Physics/DynamicsObject.h"
class COpcodeMesh;
class CModel;

class CDOMesh : public CDynamicsObject
{
public:

	CDOMesh(CDynamicsSimulation* pSim, COpcodeMesh* pOpcodeMesh);
	virtual ~CDOMesh();

	virtual void	SetPosition(const CVec3& vPos);	
	virtual CVec3	GetPosition() const;
	virtual void	SetRotation(const CQuat& vQuat);
	virtual CQuat	GetRotation() const;

private:

	// TODO: eigentlich bräuchte man einen Ressource-Manager für Opcode-Meshes...!

	COpcodeMesh*		m_pOpcodeMesh;			///< OpcodeMesh

	dTriMeshDataID		m_TriangleMeshData;		///< Data for ODE Triangle Mesh
	dGeomID				m_TriangleMesh;			///< ODE Triangle Mesh
	dBody*				m_pBody;				///< ODE Dynamics Body
};

#endif // GAMELIB_DYNAMICSOBJECTMESH_H_INCLUDED
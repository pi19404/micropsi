#include "stdafx.h"
#include "GameLib/Physics/DOMesh.h"
#include "GameLib/Physics/DynamicsSimulation.h"
#include "GameLib/Physics/DynamicsWorld.h"
#include "GameLib/Physics/ODEUtils.h"
#include "GameLib/Collision/OpcodeMesh.h"

//---------------------------------------------------------------------------------------------------------------------
CDOMesh::CDOMesh(CDynamicsSimulation* pSim, COpcodeMesh* pOpcodeMesh) : CDynamicsObject(pSim)
{
	m_pOpcodeMesh = pOpcodeMesh;
	m_TriangleMeshData = dGeomTriMeshDataCreate();
	
	CVec3* pVertices;
	int iNumVertices;
	unsigned long* pIndices;
	int iNumIndices;
	m_pOpcodeMesh->GetVertices(pVertices, iNumVertices);
	m_pOpcodeMesh->GetIndices(pIndices, iNumIndices);

	dGeomTriMeshDataBuildSingle(m_TriangleMeshData, pVertices, sizeof(CVec3), iNumVertices, pIndices, iNumIndices, sizeof(unsigned int) * 3);

	m_TriangleMesh = dCreateTriMesh(*pSim->GetCollisionSpace(), m_TriangleMeshData, 0, 0, 0);
}
//---------------------------------------------------------------------------------------------------------------------
CDOMesh::~CDOMesh()
{
	dGeomTriMeshDataDestroy(m_TriangleMeshData);
}
//---------------------------------------------------------------------------------------------------------------------
void	
CDOMesh::SetPosition(const CVec3& vPos)
{
}
//---------------------------------------------------------------------------------------------------------------------
CVec3	
CDOMesh::GetPosition() const
{
	return CVec3();
}
//---------------------------------------------------------------------------------------------------------------------
void	
CDOMesh::SetRotation(const CQuat& vQuat)
{
}
//---------------------------------------------------------------------------------------------------------------------
CQuat	
CDOMesh::GetRotation() const
{
	return CQuat();
}
//---------------------------------------------------------------------------------------------------------------------

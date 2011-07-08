#ifndef OPCODEMESH_H_INCLUDED
#define OPCODEMESH_H_INCLUDED

#include "baselib/dynarray.h"

namespace Opcode 
{ 
	class Model;
	class MeshInterface; 
}

class CRay;
class CLine3;

class CSquareMatrix4;
typedef CSquareMatrix4 CMat4S;

class CVector3;
typedef CVector3 CVec3;

class CModel;
class CD3DXFrame;
class CD3DXMeshContainer;

/**
	OpcodeMesh - Mesh für das Kollisionssystem OPCODE. Aus einem OpcodeMesh kann auch ein PhysikObjekt für ODE erzeugt werden, siehe DOMesh.
*/
class COpcodeMesh
{
public:
	COpcodeMesh(CModel* p_pxSourceModel); 
	COpcodeMesh(CD3DXFrame* p_pxFrameHierarchy); 
	~COpcodeMesh();

	const Opcode::Model*	GetOpcodeModel() const;
	bool					CollideWithRay(const CMat4S& mWorldTransform, const CRay& xRay) const;
	int						CalcNumIntersections(const CMat4S& mWorldTransform, const CLine3& rxLine) const;
	bool					CalcFirstIntersectionWithRay(const CMat4S& mWorldTransform, const CRay& xRay, CVec3* pxPositionOut = NULL, CVec3* pxNormalOut = NULL) const;
	bool					CalcFirstIntersectionWithLine(const CMat4S& mWorldTransform, const CLine3& xLine, CVec3* pxPositionOut = NULL, CVec3* pxNormalOut = NULL) const;

	void					GetVertices(const CVec3*& po_rpBuffer, int& po_iNumVertices);
	void					GetIndices(const unsigned long*& po_rpBuffer, int& po_iNumIndices);

private:

	// TODO: Funktion, die die Normals berechnet --> könnte ODE die Arbeit erleichtern

	void	Initialize(CModel* p_pxSourceModel);
	void	Initialize(CD3DXFrame* p_pxFrameHierarchy);

	void	RecursiveGetFrameCollisionGeometry(CD3DXFrame* pFrame, const CMat4S& matWorldTransform, bool bExpandSibling = true);
	void	RecursiveGetMeshContainerCollisionGeometry(CD3DXMeshContainer* pMeshContainer, const CMat4S& matWorldTransform, const CD3DXFrame* pFrameParent);
	void	GetMeshContainerCollisionGeometry(CD3DXMeshContainer* pMeshContainer, const CMat4S& matWorldTransform, const CD3DXFrame* pFrameParent);

	Opcode::Model*					m_pOpcodeModel;
	Opcode::MeshInterface*			m_pOpcodeMeshInterface;

	CDynArray< CVec3 >				m_avVertices;
	CDynArray< unsigned long >		m_aiIndices;

	// not implemented
	COpcodeMesh(const COpcodeMesh&);
	COpcodeMesh& operator=(const COpcodeMesh&);
};

#endif // OPCODEMESH_H_INCLUDED

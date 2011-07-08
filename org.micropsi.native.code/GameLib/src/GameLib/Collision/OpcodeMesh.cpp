#include "stdafx.h"
#include "GameLib/Collision/OpcodeMesh.h"

#pragma warning( push )
#pragma warning( disable : 4312 )
#undef SAFE_RELEASE
#include "Opcode.h"
#pragma warning( pop )

#include "e42/core/D3DXFrame.h"
#include "e42/core/D3DXMeshContainer.h"
#include "e42/core/Model.h"
#include "baselib/geometry/Line.h"

Opcode::RayCollider		g_xRayCollider;


//---------------------------------------------------------------------------------------------------------------------
COpcodeMesh::COpcodeMesh(CModel* p_pxSourceModel)
{
	m_pOpcodeMeshInterface = new Opcode::MeshInterface();
	m_pOpcodeModel = new Opcode::Model;
	Initialize(p_pxSourceModel);
}

//---------------------------------------------------------------------------------------------------------------------
COpcodeMesh::COpcodeMesh(CD3DXFrame* p_pxFrameHierarchy)
{
	m_pOpcodeMeshInterface = new Opcode::MeshInterface();
	m_pOpcodeModel = new Opcode::Model;
	Initialize(p_pxFrameHierarchy);
}

//---------------------------------------------------------------------------------------------------------------------
COpcodeMesh::~COpcodeMesh()
{
	delete m_pOpcodeModel;
	delete m_pOpcodeMeshInterface;
}

//---------------------------------------------------------------------------------------------------------------------
void	
COpcodeMesh::Initialize(CModel* p_pxSourceModel)
{
	RecursiveGetFrameCollisionGeometry(p_pxSourceModel->GetRootFrame(), CMat4S::mIdentity);

	Opcode::OPCODECREATE xCreateStruct;
	xCreateStruct.mIMesh = m_pOpcodeMeshInterface;
	xCreateStruct.mIMesh->SetPointers((const IndexedTriangle*) m_aiIndices.GetArrayPointer(), (const Point*) m_avVertices.GetArrayPointer());
	xCreateStruct.mIMesh->SetNbTriangles(m_aiIndices.Size() / 3);
	xCreateStruct.mIMesh->SetNbVertices(m_avVertices.Size());
	xCreateStruct.mNoLeaf			= false;
	xCreateStruct.mQuantized		= false;
	xCreateStruct.mKeepOriginal		= false;
	xCreateStruct.mCanRemap			= true;

	m_pOpcodeModel->Build(xCreateStruct);
}

//---------------------------------------------------------------------------------------------------------------------
void	
COpcodeMesh::Initialize(CD3DXFrame* p_pxFrameHierarchy)
{
	if (p_pxFrameHierarchy == NULL) 
	{
		return;
	}

	CMat4S xRootMatrix;
	if (p_pxFrameHierarchy->pFrameParent)
	{
		xRootMatrix = p_pxFrameHierarchy->pFrameParent->matCombinedTransformation;
	}
	else
	{
		xRootMatrix.SetIdentity();
	}

	RecursiveGetFrameCollisionGeometry(p_pxFrameHierarchy, xRootMatrix, false);

	Opcode::OPCODECREATE xCreateStruct;
	xCreateStruct.mIMesh = m_pOpcodeMeshInterface;
	xCreateStruct.mIMesh->SetPointers((const IndexedTriangle*) m_aiIndices.GetArrayPointer(), (const Point*) m_avVertices.GetArrayPointer());
	xCreateStruct.mIMesh->SetNbTriangles(m_aiIndices.Size() / 3);
	xCreateStruct.mIMesh->SetNbVertices(m_avVertices.Size());
	xCreateStruct.mNoLeaf		= false;
	xCreateStruct.mQuantized	= false;
	xCreateStruct.mKeepOriginal	= false;
	xCreateStruct.mCanRemap		= true;

	m_pOpcodeModel->Build(xCreateStruct);
}

//---------------------------------------------------------------------------------------------------------------------
void 
COpcodeMesh::RecursiveGetFrameCollisionGeometry(CD3DXFrame* pFrame, const CMat4S& matParentTransform, bool bExpandSibling)
{
	if (bExpandSibling)
	{
		if (pFrame->GetSibling()) 
			RecursiveGetFrameCollisionGeometry(pFrame->GetSibling(), matParentTransform);
	}

    if ((pFrame->bIsVisible) && (pFrame->GetFirstChild())) 
		RecursiveGetFrameCollisionGeometry(pFrame->GetFirstChild(), pFrame->TransformationMatrix() * matParentTransform);

    if ((pFrame->bIsVisible) && (pFrame->GetFirstMeshContainer()))   
        RecursiveGetMeshContainerCollisionGeometry(pFrame->GetFirstMeshContainer(), pFrame->TransformationMatrix() * matParentTransform, pFrame);
}

//---------------------------------------------------------------------------------------------------------------------
void 
COpcodeMesh::RecursiveGetMeshContainerCollisionGeometry(CD3DXMeshContainer* pMeshContainer, const CMat4S& matParentTransform, const CD3DXFrame* pFrameParent)
{
    GetMeshContainerCollisionGeometry(pMeshContainer, matParentTransform, pFrameParent);

    if (pMeshContainer->NextMeshContainer()) 
    {
        RecursiveGetMeshContainerCollisionGeometry(pMeshContainer->NextMeshContainer(), matParentTransform, pFrameParent);
    }
}

//---------------------------------------------------------------------------------------------------------------------
void 
COpcodeMesh::GetMeshContainerCollisionGeometry(CD3DXMeshContainer* pMeshContainer, const CMat4S& matParentTransform, const CD3DXFrame* pFrameParent)
{
    CMat4S matTransform = matParentTransform;

    TMeshHandle hndMesh = pMeshContainer->GetMesh();

    unsigned int iNumVertices = hndMesh->GetNumVertices();
    unsigned int iNumFaces = hndMesh->GetNumFaces();
	unsigned int iNumBytesPerVertex = hndMesh->GetNumBytesPerVertex();
	unsigned int iPositionOffset = pMeshContainer->CalcVertexElementOffset(D3DDECLUSAGE_POSITION);
	int iBaseIndex = m_avVertices.Size();


	// Vertices kopieren
    BYTE* pVertexBufferMemory = NULL;
    hndMesh->LockVertexBuffer(D3DLOCK_READONLY, (void**)&pVertexBufferMemory);

	for (unsigned int iVertexIdx = 0; iVertexIdx < iNumVertices; ++iVertexIdx)
	{
		const CVec3* const pvPosition = (const CVec3* const)(pVertexBufferMemory + (iVertexIdx * iNumBytesPerVertex + iPositionOffset));
		m_avVertices.PushEntry(*pvPosition ^ matTransform);
	}

	hndMesh->UnlockVertexBuffer();

	
	// Indices kopieren
    BYTE* pIndexBufferMemory = NULL;
    hndMesh->LockIndexBuffer(D3DLOCK_READONLY, (void**)&pIndexBufferMemory);

	if (hndMesh->GetOptions() & D3DXMESH_32BIT)
	{
		for (int i = 0; i < (int)iNumFaces * 3; ++i)
		{
			unsigned long iIdx = *((unsigned long*)(pIndexBufferMemory + i * sizeof(unsigned long))); 
			assert(iIdx < iNumVertices);
			m_aiIndices.PushEntry(iIdx + iBaseIndex);
		}
	}
	else 
	{
		for (int i = 0; i < (int)iNumFaces * 3; ++i)
		{
            unsigned long iIdx = *((unsigned short*)(pIndexBufferMemory + i * sizeof(unsigned short))); 
			assert(iIdx < iNumVertices);
			m_aiIndices.PushEntry(iIdx + iBaseIndex);
		}
	}

    hndMesh->UnlockIndexBuffer();
}

//---------------------------------------------------------------------------------------------------------------------
const Opcode::Model*			
COpcodeMesh::GetOpcodeModel() const
{
	return m_pOpcodeModel;
}

//---------------------------------------------------------------------------------------------------------------------
bool
COpcodeMesh::CollideWithRay(const CMat4S& mWorldTransform, const CRay& xRay) const
{
	g_xRayCollider.SetClosestHit(true);
	g_xRayCollider.SetCulling(true);
	g_xRayCollider.SetMaxDist();
	assert(g_xRayCollider.ValidateSettings() == NULL);

	Ray xOpcodeRay((IceMaths::Point&) xRay.m_vBase, (IceMaths::Point&) xRay.m_vDirection);
	bool bSuccess = g_xRayCollider.Collide(xOpcodeRay, *m_pOpcodeModel, (Matrix4x4*) &mWorldTransform);
	assert(bSuccess);
	if(!g_xRayCollider.GetContactStatus())
	{
		return true;
	}
	else
	{
		return false;
	}
}
//---------------------------------------------------------------------------------------------------------------------
void
COpcodeMesh::GetVertices(const CVec3*& po_rpBuffer, int& po_iNumVertices)
{
	po_rpBuffer = m_avVertices.GetArrayPointer();
	po_iNumVertices = (int) m_avVertices.Size();
}
//---------------------------------------------------------------------------------------------------------------------
void
COpcodeMesh::GetIndices(const unsigned long*& po_rpBuffer, int& po_iNumIndices)
{
	po_rpBuffer = m_aiIndices.GetArrayPointer();
	po_iNumIndices = (int) m_aiIndices.Size();
}
//---------------------------------------------------------------------------------------------------------------------
int
COpcodeMesh::CalcNumIntersections(const CMat4S& mWorldTransform, const CLine3& rxLine) const
{
	g_xRayCollider.SetClosestHit(false);
	g_xRayCollider.SetCulling(false);
	g_xRayCollider.SetMaxDist(rxLine.GetLength());
	assert(g_xRayCollider.ValidateSettings() == NULL);

	Ray xOpcodeRay((IceMaths::Point&) rxLine.m_vStart, (IceMaths::Point&) rxLine.GetDirection());
	g_xRayCollider.Collide(xOpcodeRay, *m_pOpcodeModel, (Matrix4x4*)&mWorldTransform);

	return g_xRayCollider.GetNbIntersections();
}
//---------------------------------------------------------------------------------------------------------------------
bool
COpcodeMesh::CalcFirstIntersectionWithRay(
					const CMat4S& mWorldTransform, 
					const CRay& xRay, 
					CVec3* pxPositionOut, CVec3* pxNormalOut) const
{
	Opcode::CollisionFaces xFaces;

	g_xRayCollider.SetClosestHit(true);
	g_xRayCollider.SetCulling(false);
	g_xRayCollider.SetMaxDist();
	g_xRayCollider.SetDestination(&xFaces);
	assert(g_xRayCollider.ValidateSettings() == NULL);

	Ray xOpcodeRay((IceMaths::Point&) xRay.m_vBase, (IceMaths::Point&) xRay.m_vDirection);
	bool bSuccess = g_xRayCollider.Collide(xOpcodeRay, *m_pOpcodeModel, (Matrix4x4*)&mWorldTransform);

	g_xRayCollider.SetDestination(NULL);


	if (!bSuccess ||
		xFaces.GetNbFaces() == 0)
	{
		return false;
	}

	if (pxPositionOut)
	{
		assert(xFaces.GetNbFaces() == 1);
		*pxPositionOut = xRay.m_vBase + xRay.m_vDirection * xFaces.GetFaces()[0].mDistance;
	}

	if (pxNormalOut)
	{
		int iFaceCorner0 = m_aiIndices[xFaces.GetFaces()[0].mFaceID * 3 + 0];
		int iFaceCorner1 = m_aiIndices[xFaces.GetFaces()[0].mFaceID * 3 + 1];
		int iFaceCorner2 = m_aiIndices[xFaces.GetFaces()[0].mFaceID * 3 + 2];

		const CVec3& rvFaceCorner0 = m_avVertices[iFaceCorner0];
		const CVec3& rvFaceCorner1 = m_avVertices[iFaceCorner1];
		const CVec3& rvFaceCorner2 = m_avVertices[iFaceCorner2];

		*pxNormalOut = ((rvFaceCorner1 - rvFaceCorner0) ^ (rvFaceCorner2 - rvFaceCorner0)).GetNormalized();
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------
bool
COpcodeMesh::CalcFirstIntersectionWithLine(
					const CMat4S& mWorldTransform, 
					const CLine3& xLine, 
					CVec3* pxPositionOut, CVec3* pxNormalOut) const
{
	Opcode::CollisionFaces xFaces;

	g_xRayCollider.SetClosestHit(true);
	g_xRayCollider.SetCulling(true);
	g_xRayCollider.SetMaxDist(xLine.GetLength());
	g_xRayCollider.SetDestination(&xFaces);
	assert(g_xRayCollider.ValidateSettings() == NULL);

	Ray xOpcodeRay((IceMaths::Point&) xLine.m_vStart, (IceMaths::Point&) xLine.GetDirection());
	bool bSuccess = g_xRayCollider.Collide(xOpcodeRay, *m_pOpcodeModel, (Matrix4x4*)&mWorldTransform);

	g_xRayCollider.SetDestination(NULL);


	if (!bSuccess ||
		xFaces.GetNbFaces() == 0)
	{
		return false;
	}

	if (pxPositionOut)
	{
		assert(xFaces.GetNbFaces() == 1);
		*pxPositionOut = xLine.m_vStart + xLine.GetDirection() * xFaces.GetFaces()[0].mDistance;
		assert(xFaces.GetFaces()[0].mDistance <= xLine.GetLength());
	}

	if (pxNormalOut)
	{
		int iFaceCorner0 = m_aiIndices[xFaces.GetFaces()[0].mFaceID * 3 + 0];
		int iFaceCorner1 = m_aiIndices[xFaces.GetFaces()[0].mFaceID * 3 + 1];
		int iFaceCorner2 = m_aiIndices[xFaces.GetFaces()[0].mFaceID * 3 + 2];

		const CVec3& rvFaceCorner0 = m_avVertices[iFaceCorner0];
		const CVec3& rvFaceCorner1 = m_avVertices[iFaceCorner1];
		const CVec3& rvFaceCorner2 = m_avVertices[iFaceCorner2];

		*pxNormalOut = ((rvFaceCorner1 - rvFaceCorner0) ^ (rvFaceCorner2 - rvFaceCorner0)).GetNormalized();

		assert(*pxNormalOut * xLine.GetDirection() < 0);
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------

#include "stdafx.h"

#include "e42/core/SceneGraphOptimizer.h"

#include "e42/core/SceneGraphIterator.h"

#include "e42/core/MeshLoaderOptions.h"
#include "e42/core/EngineController.h"
#include "e42/core/D3DXFrame.h"
#include "e42/core/D3DXMeshContainer.h"
#include "e42/core/D3DVertexElement9.h"

#include "e42/core/MeshFactory.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CMeshLoaderOptions*	CSceneGraphOptimizer::ms_pxMeshLoaderOptions;
CEngineController*	CSceneGraphOptimizer::ms_pxEngineController;
//-----------------------------------------------------------------------------------------------------------------------------------------
int
CSceneGraphOptimizer::GetFrameRank(const CD3DXFrame* pxFrame)
{
	int iPriority = 50;

	// RenderOrder
	if (pxFrame->GetName())
	{
		const char* pcRenderOrder = strstr(pxFrame->GetName(), "renderpriority");
		if (pcRenderOrder)
		{
			int iResult = sscanf(pcRenderOrder, "renderpriority%d", &iPriority);
			assert(iResult != EOF);
		}
	}

	int iValue = (100 - iPriority) * 0x100000;

	// MeshRank
	if (pxFrame->GetFirstMeshContainer())
	{
		iValue += 0x10000 | pxFrame->GetFirstMeshContainer()->CalcMeshRank();
	}
	else
	if ((pxFrame->GetFirstChild()) &&
		(pxFrame->GetFirstChild()->GetFirstMeshContainer()))
	{
		iValue += 0x10000 | pxFrame->GetFirstChild()->GetFirstMeshContainer()->CalcMeshRank();
	}

	return iValue;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CSceneGraphOptimizer::RecursiveSortFrameHierarchy(CD3DXFrame** ppxFirstChildFrame)
{
	// übergeben wird eine Referenz auf den Anfang der Liste von Childframes
	// die Childframe-Liste wird sortiert und zurückgeschrieben
	// da jeder Frame in der Childframe-Liste selbst eine Liste mit 
	// Childframes enthält, wird die Funktion für alle Frames der Liste erneut aufgerufen

	assert(ppxFirstChildFrame);

	if (*ppxFirstChildFrame)
	{
		CD3DXFrame* pChildFrame;


		// 1.) MeshContainer sortieren
		pChildFrame = *ppxFirstChildFrame;
		while (pChildFrame)
		{
			SortMeshContainerList(pChildFrame);
			pChildFrame = pChildFrame->GetSibling();
		}


		// 2.) Array mit Frames aufbauen um diese zu sortieren
		CDynArray<CD3DXFrame*, 4> axChildFrames;

		pChildFrame = *ppxFirstChildFrame;
		while (pChildFrame)
		{
			axChildFrames.Push() = pChildFrame;
			pChildFrame = pChildFrame->GetSibling();
		}


		// 3.) Array mit Frames sortieren
		for (int i = 0; i < (int)axChildFrames.Size() - 1; i++)
		{
			for (int j = i + 1; j < (int)axChildFrames.Size(); j++)
			{
				if (GetFrameRank(axChildFrames[i]) > GetFrameRank(axChildFrames[j]))
				{
					CD3DXFrame* pSwap = axChildFrames[i];
					axChildFrames[i] = axChildFrames[j];
					axChildFrames[j] = pSwap;
				}
			}
		}


		// 4.) Frames zurückschreiben
		*ppxFirstChildFrame = axChildFrames[0];

		int iLastChild = (int)axChildFrames.Size() - 1;
		for (int i = 0; i < iLastChild; i++)
		{
			axChildFrames[i]->D3DXFrame().pFrameSibling = &axChildFrames[i + 1]->D3DXFrame();
		}
		axChildFrames[iLastChild]->D3DXFrame().pFrameSibling = NULL;


		// 5.) für alle Childs aufrufen
		pChildFrame = *ppxFirstChildFrame;
		while (pChildFrame)
		{
			RecursiveSortFrameHierarchy((CD3DXFrame**)&(pChildFrame->D3DXFrame().pFrameFirstChild));
			pChildFrame = pChildFrame->GetSibling();
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphOptimizer::SortMeshContainerList(CD3DXFrame* pxFrame)
{
	assert(pxFrame);

	if (pxFrame->GetFirstMeshContainer())
	{
		// 1.) Array mit Meshes aufbauen um diese zu sortieren
		CDynArray<CD3DXMeshContainer*, 4> axMeshContainers;

		CD3DXMeshContainer* pNextMeshContainer = pxFrame->GetFirstMeshContainer();
		pxFrame->SetFirstMeshContainer(NULL);

		while (pNextMeshContainer)
		{
			CD3DXMeshContainer* pMeshContainer = pNextMeshContainer;
			axMeshContainers.Push() = pMeshContainer;
			pNextMeshContainer = pMeshContainer->NextMeshContainer();
			pMeshContainer->NextMeshContainer() = NULL;
		}


		// 2.) Array mit Meshes sortieren
		for (int i = 0; i < (int)axMeshContainers.Size() - 1; i++)
		{
			for (int j = i + 1; j < (int)axMeshContainers.Size(); j++)
			{
				if (axMeshContainers[i]->CalcMeshRank() < axMeshContainers[j]->CalcMeshRank())
				{
					CD3DXMeshContainer* pSwap = axMeshContainers[i];
					axMeshContainers[i] = axMeshContainers[j];
					axMeshContainers[j] = pSwap;
				}
			}
		}


		// 3.) Meshes zurückschreiben
		pxFrame->SetFirstMeshContainer(axMeshContainers[0]);

		int iLastMesh = (int)axMeshContainers.Size() - 1;
		for (int i = 0; i < iLastMesh; i++)
		{
			axMeshContainers[i]->NextMeshContainer() = axMeshContainers[i + 1];
		}

		assert(axMeshContainers[iLastMesh]->NextMeshContainer() == NULL);
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphOptimizer::OptimizeMesh(CD3DXMeshContainer* pxMeshContainer)
{
	BOOL bDebugOutputMuted = D3DXDebugMute(TRUE);	// Ausgabe der Optimierungsergebnisse unterdrücken

	pxMeshContainer->GetMesh().GetPtr()->OptimizeInplace(
		D3DXMESHOPT_VERTEXCACHE | D3DXMESHOPT_COMPACT | D3DXMESHOPT_ATTRSORT,
		pxMeshContainer->Adjacency(), NULL, NULL, NULL);

	D3DXDebugMute(bDebugOutputMuted);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphOptimizer::ApplyMeshUsageAndPool(CD3DXMeshContainer* pMeshContainer)
{
	// Buffer ins gewünschte Format konvertieren
	DWORD dwMeshOptions = ms_pxMeshLoaderOptions->m_dwMeshOptions;
	if (pMeshContainer->bUsesSoftwareVertexProcessing)
	{
		dwMeshOptions &= ~D3DXMESH_MANAGED;
		dwMeshOptions |= D3DXMESH_SYSTEMMEM;
	}

	if (dwMeshOptions != pMeshContainer->GetMesh().GetPtr()->GetOptions())
	{
		HRESULT hr;

		CComObjectPtr<ID3DXMesh> spClonedMesh;

		D3DVERTEXELEMENT9 axVertexElements[MAX_FVF_DECL_SIZE];
		hr = pMeshContainer->GetMesh().GetPtr()->GetDeclaration(axVertexElements);
		assert(SUCCEEDED(hr));

		hr = pMeshContainer->GetMesh().GetPtr()->CloneMesh(
			dwMeshOptions, axVertexElements, 
			ms_pxEngineController->GetDevice(), &spClonedMesh);
		assert(SUCCEEDED(hr));

		pMeshContainer->SetMesh(ms_pxEngineController->GetMeshFactory()->RegisterMesh(spClonedMesh));
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CSceneGraphOptimizer::SkipNormals(CD3DXMeshContainer* pMeshContainer)
{
/*
	D3DVERTEXELEMENT9 axVertexElements[MAX_FVF_DECL_SIZE];
	pMeshContainer->GetMesh().GetPtr()->GetDeclaration(axVertexElements);

	bool bVertexDeclarationChanged = false;
	
	int iElementIdx = 0;
	while (axVertexElements[iElementIdx].Stream != 0xff)
	{
		if (axVertexElements[iElementIdx].Usage == D3DDECLUSAGE_NORMAL)					// FIXME: beliebige dinger rauswerfen können
		{
			int iNormalElementSize = (int)CD3DVertexElement9::GetSizeOfType(axVertexElements[iElementIdx].Type);
			int iNormalElementOffset = axVertexElements[iElementIdx].Offset;


			// restliche VertexDeclarations aufrücken lassen
			int iShiftElementIdx = iElementIdx;
			do
			{
				axVertexElements[iShiftElementIdx] = axVertexElements[iShiftElementIdx + 1];

				iShiftElementIdx++;
			}
			while (axVertexElements[iShiftElementIdx].Stream != 0xff);


			// offsets der Elemente hinter dem Normal runtersetzen
			iShiftElementIdx = 0;
			while (axVertexElements[iShiftElementIdx].Stream != 0xff)
			{
				if (axVertexElements[iShiftElementIdx].Offset > iNormalElementOffset)
					axVertexElements[iShiftElementIdx].Offset -= iNormalElementSize;

				iShiftElementIdx++;
			}

			bVertexDeclarationChanged = true;
		}
		else
		{
			iElementIdx++;
		}
	}


	if (bVertexDeclarationChanged)
	{
		CComObjectPtr<ID3DXMesh> spNewMesh;

		HRESULT hr = pMeshContainer->GetMesh().GetPtr()->CloneMesh(
			D3DXMESH_SYSTEMMEM | D3DXMESH_DYNAMIC, axVertexElements, 
			ms_pxEngineController->GetDevice(), &spNewMesh);

		assert(SUCCEEDED(hr));

		pMeshContainer->SetMesh(ms_pxEngineController->GetMeshFactory()->RegisterMesh(spNewMesh));
	}
/*/


	// FIXME: VertexDeclarations statt FVF nehmen   (funktioniert nur solange kein Skinning benutzt wird)
	DWORD dwNewFVF = 
		pMeshContainer->GetMesh().GetPtr()->GetFVF() & ~D3DFVF_NORMAL;
//        pMeshContainer->hndMesh.GetPtr()->GetFVF() & ~(D3DFVF_POSITION_MASK | D3DFVF_TEXCOUNT_MASK);

	if (dwNewFVF != pMeshContainer->GetMesh().GetPtr()->GetFVF())
	{
		CComObjectPtr<ID3DXMesh> spNewMesh;

		HRESULT hr = pMeshContainer->GetMesh().GetPtr()->CloneMeshFVF(
			D3DXMESH_SYSTEMMEM | D3DXMESH_DYNAMIC, dwNewFVF, 
			ms_pxEngineController->GetDevice(), &spNewMesh);

		assert(SUCCEEDED(hr));

		pMeshContainer->SetMesh(ms_pxEngineController->GetMeshFactory()->RegisterMesh(spNewMesh));
	}
/**/
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphOptimizer::CleanMeshContainer(CD3DXMeshContainer* pxMeshContainer)
{
	pxMeshContainer->DeleteAdjacencyArray();

	if (ms_pxMeshLoaderOptions->m_bSkipNormals)
		SkipNormals(pxMeshContainer);

	ApplyMeshUsageAndPool(pxMeshContainer);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSceneGraphOptimizer::RecursiveRemoveEmptyFrames(CD3DXFrame* pxFrame)
{
	CD3DXFrame* pxFrameChild = pxFrame->GetFirstChild();

	if (pxFrameChild)
	{
		RecursiveRemoveEmptyFrames(pxFrameChild);

		if (pxFrameChild->GetFirstChild() == 0 &&
			pxFrameChild->GetFirstMeshContainer() == 0)
		{
			pxFrame->RemoveChild(pxFrameChild);
			CD3DXFrame::Destroy(pxFrameChild);
		}
	}


	CD3DXFrame* pxFrameSibling = pxFrame->GetSibling();

	if (pxFrameSibling)
	{
		RecursiveRemoveEmptyFrames(pxFrameSibling);

		if (pxFrameSibling->GetFirstChild() == 0 &&
			pxFrameSibling->GetFirstMeshContainer() == 0)
		{
			pxFrame->RemoveSibling(pxFrameSibling);
			CD3DXFrame::Destroy(pxFrameSibling);
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CSceneGraphOptimizer::OptimizeFrameHierarchy(CD3DXFrame** ppxFrameRoot, CEngineController* pxEngineController, CMeshLoaderOptions* pxOptions)
{
	if (*ppxFrameRoot)
	{
		ms_pxEngineController = pxEngineController;
		ms_pxMeshLoaderOptions = pxOptions;


		RecursiveSortFrameHierarchy(ppxFrameRoot);


		CD3DXMeshContainer* pxMeshContainer;
		CSceneGraphIterator xSGI(*ppxFrameRoot);
		while (pxMeshContainer = xSGI.GetNextMeshContainer())
		{
			OptimizeMesh(pxMeshContainer);
			CleanMeshContainer(pxMeshContainer);
		};


		ms_pxMeshLoaderOptions = NULL;
		ms_pxEngineController = NULL;


		if (pxOptions->m_bRemoveEmptyFrames)
		{
			RecursiveRemoveEmptyFrames(*ppxFrameRoot);
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------

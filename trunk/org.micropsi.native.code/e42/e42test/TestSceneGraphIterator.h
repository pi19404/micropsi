#ifndef TESTSCENEGRAPHITERATOR
#define TESTSCENEGRAPHITERATOR

#include "e42/core/D3DXFrame.h"
#include "e42/core/SceneGraphAllocator.h"
#include "e42/core/SceneGraphIterator.h"

int g_iNumFrames;
int g_iNumMeshContainer;

CD3DXFrame*
CreateFrameHierarchy(int iMaxChilds)
{
	CD3DXFrame* pxFrame = CD3DXFrame::Create();
	g_iNumFrames++;


	int iNumMeshContainer = rand() % (iMaxChilds / 2 + 1);

	for (int i = 0; i < iNumMeshContainer; i++)
	{
		pxFrame->AddMeshContainer(CD3DXMeshContainer::Create());
		g_iNumMeshContainer++;
	}


	if (iMaxChilds > 0)
	{
		int iNumChilds = rand() % iMaxChilds;

		for (int i = 0; i < iNumChilds; i++)
		{
			pxFrame->AddChild(CreateFrameHierarchy(iMaxChilds - 1));
		}
	}

	return pxFrame;
}


inline
bool TestSceneGraphIterator()
{
	{
		g_iNumFrames = 0;
		g_iNumMeshContainer = 0;
		CD3DXFrame* pxRoot = CreateFrameHierarchy(11);

		int iNumFrames = 0;
		int iNumMeshContainer = 0;

		CSceneGraphIterator xIterator(pxRoot);
		CD3DXFrame* pxFrame;
		while (pxFrame = xIterator.GetNextFrame())
		{
			assert(pxFrame->bIsVisible == true);
			pxFrame->bIsVisible = false;
			iNumFrames++;
		}
		CD3DXMeshContainer* pxMeshContainer;
		while (pxMeshContainer = xIterator.GetNextMeshContainer())
		{
			assert(pxMeshContainer->bIsVisible == true);
			pxMeshContainer->bIsVisible = false;
			iNumMeshContainer++;
		}

		assert(iNumFrames == g_iNumFrames);
		if (iNumFrames != g_iNumFrames) return false;
		assert(iNumMeshContainer == g_iNumMeshContainer);
		if (iNumMeshContainer != g_iNumMeshContainer) return false;

		CD3DXFrame::RecursiveDestroy(pxRoot);
	}

	{
		g_iNumFrames = 0;
		g_iNumMeshContainer = 0;
		CD3DXFrame* pxRoot = CD3DXFrame::Create();

		int iNumFrames = 0;
		int iNumMeshContainer = 0;

		CSceneGraphIterator xIterator(pxRoot);
		while (xIterator.GetNextFrame())
		{
			iNumFrames++;
		}
		while (xIterator.GetNextMeshContainer())
		{
			iNumMeshContainer++;
		}

		assert(iNumFrames == 1);
		if (iNumFrames != 1) return false;
		assert(iNumMeshContainer == 0);
		if (iNumMeshContainer != 0) return false;


		pxRoot->AddMeshContainer(CD3DXMeshContainer::Create());

		iNumMeshContainer = 0;
		while (xIterator.GetNextMeshContainer())
		{
			iNumMeshContainer++;
		}

		assert(iNumMeshContainer == 1);
		if (iNumMeshContainer != 1) return false;
	}

	return true;
};

#endif // TESTSCENEGRAPHITERATOR
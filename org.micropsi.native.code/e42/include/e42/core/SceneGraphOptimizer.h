/*
	CSceneGraphOptimizer
	- führt statische Sortierung der Framehierarchie durch
	- Optimiert die Meshes (ID3DXMesh::Optimize())
	- Cleant MeshContainer (zB. Adjacency, Normale entfernen)
	- setzt Pool und Usage der MeshContainer; kopiert diese auf die Graka
*/
#pragma once

#ifndef E42_SCENEGRAPHOPTIMIZER_H_INCLUDED
#define E42_SCENEGRAPHOPTIMIZER_H_INCLUDED

#include "e42/stdinc.h"

class CD3DXFrame;
class CD3DXMeshContainer;
class CEngineController;
class CMeshLoaderOptions;

class CSceneGraphOptimizer
{
private:
	static CMeshLoaderOptions*	ms_pxMeshLoaderOptions;
	static CEngineController*	ms_pxEngineController;

	static int		GetFrameRank(const CD3DXFrame* pxFrame);
	static void		RecursiveSortFrameHierarchy(CD3DXFrame** ppxFirstChildFrame);
	static void		SortMeshContainerList(CD3DXFrame* pxFrame);


	static void		OptimizeMesh(CD3DXMeshContainer* pxMeshContainer);
	static void		ApplyMeshUsageAndPool(CD3DXMeshContainer* pMeshContainer);
	static void		SkipNormals(CD3DXMeshContainer* pMeshContainer);

	static void		CleanMeshContainer(CD3DXMeshContainer* pxMeshContainer);
	static void		RecursiveOptimizeMeshes(CD3DXMeshContainer* pxMeshContainer);
	static void		RecursiveOptimizeMeshes(CD3DXFrame* pxFrame);

	static void		RecursiveRemoveEmptyFrames(CD3DXFrame* pxFrame);

public:
	static void OptimizeFrameHierarchy(CD3DXFrame** ppxFrameRoot, CEngineController* pxEngineController, CMeshLoaderOptions* pxOptions);
};

#endif // E42_SCENEGRAPHOPTIMIZER_H_INCLUDED
/*
	CSceneGraphAllocator
	- leitet aus ID3DXAllocateHierarchy ab und kann in D3DXLoadHierarchyFromX verwendet werden
	- legt Resourcen an und führt Mapping von Texturen auf Effects durch
*/
#pragma once

#ifndef E42_SCENEGRAPHALLOCATOR_H_INCLUDED
#define E42_SCENEGRAPHALLOCATOR_H_INCLUDED

#include "e42/stdinc.h"

#include <string>

#include "e42/core/D3DXFrame.h"
#include "e42/core/D3DXMeshContainer.h"

class CEngineController;
class CMaterialConverter;
class CMeshLoaderOptions;

class CSceneGraphAllocator : public ID3DXAllocateHierarchy
{
private:
	static char* ClonePCharString(const char* pcSource);


	void SetMeshContainerMaterials(CD3DXMeshContainer* pMeshContainer, const D3DXMATERIAL* pMaterials) const;
	void SetMeshContainerEffectInstances(CD3DXMeshContainer* pMeshContainer, const _D3DXEFFECTINSTANCE* pEffectInstances) const;


	DWORD               m_dwMeshLoaderOptions;          ///< Optionen für den Loader (siehe MeshLoaderOptions.h)
	DWORD               m_dwMeshOptions;                ///< D3DXMESH-Options, mit denen die Meshes angelegt werden sollen
	CMeshLoaderOptions*	m_pxOptions;

	CEngineController*  m_pxEngineController;
	CMaterialConverter* m_pxMaterialConverter;

public:

	CSceneGraphAllocator(CEngineController* pxEngineController);
	~CSceneGraphAllocator();


	HRESULT __stdcall CreateFrame(LPCTSTR pcName, _D3DXFRAME* *ppNewFrame);
	HRESULT __stdcall CreateMeshContainer(LPCTSTR pcName, 
		const D3DXMESHDATA* pMeshData, const D3DXMATERIAL* pMaterials, 
		const _D3DXEFFECTINSTANCE* pEffectInstances, DWORD NumMaterials, 
		const DWORD *pAdjacency, ID3DXSkinInfo* pSkinInfo, _D3DXMESHCONTAINER* *ppNewMeshContainer);
	

	HRESULT __stdcall DestroyFrame(_D3DXFRAME* pFrameToFree);
	HRESULT __stdcall DestroyMeshContainer(_D3DXMESHCONTAINER* pMeshContainerBase);


	HRESULT __stdcall CloneFrame(_D3DXFRAME* pFrameToClone, _D3DXFRAME* *ppNewFrame);
	HRESULT __stdcall CloneMeshContainer(_D3DXMESHCONTAINER* pMeshContainerToClone, _D3DXMESHCONTAINER* *ppNewMeshContainer);


	void SetOptions(CMeshLoaderOptions* pxOptions);
	void SetMaterialConverter(CMaterialConverter* pxMaterialConverter);
};

#endif // E42_SCENEGRAPHALLOCATOR_H_INCLUDED
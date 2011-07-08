/*
    CSceneGraphInitializer
    - lädt Texturen und Effect-Files
    - konvertiert die Meshes, damit sie fürs Skinning funktionieren
    - setzt die BoneMatrixPtr vom Mesh zum Skeleton
    - berechnet Boundingspheres
    - sucht die VisibilitySwitchFrames
*/

#pragma once

#ifndef E42_SCENEGRAPHINITIALIZER_H_INCLUDED
#define E42_SCENEGRAPHINITIALIZER_H_INCLUDED

#include "baselib/dynarray.h"
#include "e42/stdinc.h"
#include "e42/core/D3DXSemantic.h"

class CD3DXFrame;
class CD3DXMeshContainer;
class CEngineController;
class CMeshLoaderOptions;

class CVector3;
typedef CVector3 CVec3;

class CSceneGraphInitializer
{
private:
    static CD3DXFrame*          ms_pxRootFrame;
    static CEngineController*   ms_pxEngineController;
    static CMeshLoaderOptions*	ms_pxMeshLoaderOptions;

    static void CreateMaterials(CD3DXMeshContainer* pxMeshContainer);


    static void ConvertToIndexedBlendedMesh(CD3DXMeshContainer* pMeshContainer);
    static void ConvertVertexFormatForIndexedSkinning(CD3DXMeshContainer* pMeshContainer);
    static void ConvertVertexDeclarationsForIndexedSkinning(CD3DXMeshContainer* pMeshContainer);
    static void SetupBoneMatrixPointersOnMesh(CD3DXMeshContainer* pxMeshContainer);
    static void SetupSkinningData(CD3DXMeshContainer* pMeshContainer);

	static void CleanMesh(CD3DXMeshContainer* pxMeshContainer);
	static void SimplifyMesh(CD3DXMeshContainer* pxMeshContainer, float fFactor);

	static void CreateEdgeQuads(CD3DXMeshContainer* pxMeshContainer, float fShadowVolumeShrink);
	static void ExtrudeShadowVolume(CD3DXMeshContainer* pxMeshContainer, CVec3 vVolumeExtrusion, float fShadowVolumeShrink);

    static bool ReadSemantic(const char* pcSemantic, const char* pcUsage, D3DDECLUSAGE usage, D3DXSEMANTIC* pxSemantic);
    static bool GetSemanticFromString(const char* pcSemantic, D3DXSEMANTIC* pxSemantic);
    static void ReadShaderInputElements(CD3DXMeshContainer* pxMeshContainer, CDynArray<CD3DXSemantic>* paxRequiredSemantics);


    static void AddVertexComponents(CD3DXMeshContainer* pxMeshContainer, int iUsageIdx, 
                                    bool bRequireNormals, bool bRequireTangents, bool bRequireBinormals, 
                                    bool bRequireTexCoord, bool bRequirePosition, bool bRequireColor);
    static void SetupVertexStreams(CD3DXMeshContainer* pxMeshContainer);

	static void SetupShaderTextures(CD3DXMeshContainer* pxMeshContainer);		///< lädt die Texturen, die der Shader spezifiert hat

	static void InitializeMeshContainer(CD3DXMeshContainer* pxMeshContainer);


	static void CalcBoundingSphere(CD3DXFrame* pxFrame);
    static void InitVisibilitySwitchFrame(CD3DXFrame* pxFrame);

    static void RecursiveSetupParentPointers(CD3DXFrame* pxFrame);


public:

    static void InitializeFrameHierarchy(CD3DXFrame* pxFrameRoot, CEngineController* pxEngineController, CMeshLoaderOptions* pxOptions);
};

#endif // E42_SCENEGRAPHINITIALIZER_H_INCLUDED
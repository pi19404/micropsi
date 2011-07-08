/*******************************************************************************
 CMeshRenderer - Rendert MeshContainer bzw. DXMeshes bzw. Attributgruppen von
    DXMeshes. Hier soll auch mal die DirectX-State-Engine implementiert werden.
*******************************************************************************/
#pragma once

#ifndef MESHRENDERER_H_INCLUDED
#define MESHRENDERER_H_INCLUDED

#include "e42/stdinc.h"

#include "baselib/geometry/Matrix.h"
#include "e42/core/RenderContext.h"

class CD3DXMeshContainer;
class CEffectShader;
class CEngineController;
class CDeviceStateMgr;

class CMeshRenderer
{
private:
    static void SetupMatrices_Skinning(int iAttributeGroup, const CEffectShader* pEffect);
    static void SetupMatrices_Standard(const CEffectShader* pEffect);

    static void SetupLights(int iAttribID, const CEffectShader* pEffect);
    static void SetupTextures(int iAttribID, const CEffectShader* pEffect);
    static void SetTechnique(const CEffectShader* pEffect);
    static void SetConstants(const CEffectShader* pEffect);
    static void RenderMeshSubset(int iSubset, CEffectShader* pEffect);

    static void RenderMesh_Skinning();
    static void RenderMesh_Standard();


    enum {MAX_BONE_MATRICES = 26};
    static CMat4S                       ms_amatBoneMatrices[MAX_BONE_MATRICES];
    static CMat4S                       ms_matWorldMatrix;

    static const CD3DXMeshContainer*    ms_pMeshContainer;
    static TRenderContextPtr            ms_spxRenderContext;

    static CDeviceStateMgr*             ms_pd3dDeviceStateMgr;


public:

/*
    CMeshRenderer(CEngineController* pxEngineController);
    ~CMeshRenderer();
    void Render(
        const CMat4S& matViewProjectionTransform, const CMat4S& matWorldTransform,
        const CD3DXMeshContainer* pMeshContainer, TRenderContextPtr spxRenderContext);
*/
    static void Render(
        const CD3DXMeshContainer* pMeshContainer,
        const CMat4S& matWorldTransform,
        TRenderContextPtr spxRenderContext);

};

#endif // MESHRENDERER_H_INCLUDED


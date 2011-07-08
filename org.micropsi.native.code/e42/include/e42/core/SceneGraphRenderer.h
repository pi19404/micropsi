#pragma once

#ifndef E42_SCENEGRAPHRENDERER_H_INCLUDED
#define E42_SCENEGRAPHRENDERER_H_INCLUDED

#include "e42/stdinc.h"
#include "e42/core/RenderContext.h"
#include "baselib/geometry/Matrix.h"

class CD3DXFrame;
class CD3DXMeshContainer;
class CViewFrustum;

class CSceneGraphRenderer
{
private:

    TRenderContextPtr    m_spxRenderContext;
    CMat4S               m_matRootWorldTransform;
    const CViewFrustum*  m_pViewFrustum;

	CSceneGraphRenderer();

    bool __fastcall ScalingIsZero(const CMat4S& matTransform);
    void __fastcall RecursiveRenderMeshContainer(const CD3DXMeshContainer* pMeshContainer);
    void __fastcall RecursiveRenderFrame(CD3DXFrame* pFrame);

	static void __fastcall RecursiveUpdateCombinedFrameMatrices(CD3DXFrame* pFrame, const CMat4S& matParentTransform);

public:
    static void RenderFrameHierarchy(
        CD3DXFrame* pFrame, TRenderContextPtr spxRenderContext, 
        const CMat4S& matWorldTransform, bool bFrameCullingEnable);

    static void UpdateCombinedFrameMatrices(CD3DXFrame* pFrame);
};

#endif // E42_SCENEGRAPHRENDERER_H_INCLUDED
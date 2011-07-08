#pragma once

#ifndef GFXDEBUGMARKER_H_INCLUDED
#define GFXDEBUGMARKER_H_INCLUDED

#include "baselib/geometry/CVector.h"
#include "baselib/color.h"

#include "e42/Core/ResourceHandles.h"

class CSquareMatrix4;
typedef CSquareMatrix4 CMat4S;
class CCamera;

class CGfxDebugMarker
{
private:
    TModelHandle m_hndSphere;
    TModelHandle m_hndBox;

    void CreateModels();

    CGfxDebugMarker();
    ~CGfxDebugMarker();

    static CGfxDebugMarker* ms_pxGfxDebugMarker;

	void SetRenderStates(CMat4S matTransform, const CCamera* pCamera, CColor color, float fScale, bool bWireframe);

public:
    static CGfxDebugMarker& Get();
    static void Shut();

    void DrawSphere(const CVec3& vPos, const CCamera* pCamera, float fScale = 0.05f, CColor color = 0xFFFFFFFF);
    void DrawBox(const CVec3& vPos, const CCamera* pCamera, float fScale = 0.05f, CColor color = 0xFFFFFFFF);


	void DrawBox(const CMat4S& matTransform, const CCamera* pCamera, float fWidth = 0.5f, float fHeight = 0.5f, float fDepth = 0.5f, CColor color = 0x3FFFFFFF, bool bWireframe = true);
	void DrawSphere(const CMat4S& matTransform, const CCamera* pCamera, float fRadius = 0.5f, int iSlices = 16, int iStacks = 16, CColor color = 0x3FFFFFFF, bool bWireframe = true);
	void DrawCylinder(const CMat4S& matTransform, const CCamera* pCamera, float fRadius1 = 0.5f, float fRadius2 = 0.5f, float fLength = 1.0f, int iSlices = 16 , int iStacks = 16, CColor color = 0x3FFFFFFF, bool bWireframe = true);
	void DrawTorus(const CMat4S& matTransform, const CCamera* pCamera, float fInnerRadius = 0.75f, float fOuterRadius = 1.25f, int iSides = 16, int iRings = 32, CColor color = 0x3FFFFFFF, bool bWireframe = true);
	void DrawTeapot(const CMat4S& matTransform, const CCamera* pCamera, float fScale = 1.0f, CColor color = 0x3FFFFFFF, bool bWireframe = true);
};

#endif // GFXDEBUGMARKER_H_INCLUDED

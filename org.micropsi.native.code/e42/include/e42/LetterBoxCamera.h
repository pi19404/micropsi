#pragma once

#ifndef LETTERBOXCAMERA_H_INCLUDED
#define LETTERBOXCAMERA_H_INCLUDED

#include "e42/stdinc.h"

#include "Camera.h"

class CEngineController;

class CLetterBoxCamera : public CCamera
{
private:
    float               m_fViewFrustumShrink;
    CEngineController*  m_pxEngineController;

    void UpdateClipPlanes();

public:
    CLetterBoxCamera(CEngineController* pxEngineController);
    ~CLetterBoxCamera();

    void UpdateViewFrustum();
    void SetScreenShrink(float fShrink);
};

#endif // LETTERBOXCAMERA_H_INCLUDED
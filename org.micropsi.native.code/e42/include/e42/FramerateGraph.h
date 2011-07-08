#pragma once

#ifndef FRAMERATEGRAPH_H_INCLUDED
#define FRAMERATEGRAPH_H_INCLUDED

#include "e42/stdinc.h"

#include "e42/core/ResourceHandles.h"

class CEngineController;

class CFramerateGraph
{
private:
    CEngineController*          m_pxEngineController;

    TTextureHandle            m_hndGraphTextureSysMem;
    TTextureHandle            m_hndGraphTexture;
    TVertexBufferHandle       m_hndVB;
    TVertexShaderHandle       m_hndVSh;


    int m_iTextureWidth;
    int m_iTextureHeight;
    int m_iXWritePos;
    int m_iScreenWidth;
    int m_iScreenHeight;

public:

    CFramerateGraph(CEngineController* pxEngineController = NULL);
    ~CFramerateGraph();

    void Init(int iWidth, int iHeight);
    void Shut();

    void Update(float fDurationOfLastFrame);
    void Render();
};

#endif // FRAMERATEGRAPH_H_INCLUDED

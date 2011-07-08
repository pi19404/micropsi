#pragma once

#ifndef E42_VERTEXSHADERFACTORY_H_INCLUDED
#define E42_VERTEXSHADERFACTORY_H_INCLUDED

#include "e42/stdinc.h"

#include "e42/core/ResourceHandles.h"
#include "e42/core/ResourceFactory.h"

class CEngineController;

class CVertexShaderFactory : public CResourceFactory
{
private:
    CEngineController*  m_pxEngineController;

    void    DestroyResource(void* pxResource);

public:
    CVertexShaderFactory(CEngineController* pxEngineController);
    ~CVertexShaderFactory();

    TVertexShaderHandle CreateVertexShader(const std::string& sFilename, bool bDebug = true);
    TVertexShaderHandle CreateVertexShaderFromString(const std::string& sVertexShaderSource, bool bDebug = true, const std::string& sResourceID = std::string());
};


#endif // E42_VERTEXSHADERFACTORY_H_INCLUDED
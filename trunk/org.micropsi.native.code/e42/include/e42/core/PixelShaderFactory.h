#pragma once

#ifndef E42_PIXELSHADERFACTORY_H_INCLUDED
#define E42_PIXELSHADERFACTORY_H_INCLUDED

#include "e42/stdinc.h"

#include "e42/core/ResourceHandles.h"
#include "e42/core/ResourceFactory.h"

class CEngineController;

class CPixelShaderFactory : public CResourceFactory
{
private:
    CEngineController*  m_pxEngineController;

    void    DestroyResource(void* pxResource);

public:
    CPixelShaderFactory(CEngineController* pxEngineController);
    ~CPixelShaderFactory();

    TPixelShaderHandle CreatePixelShader(const std::string& sFilename, bool bDebug = true);
    TPixelShaderHandle CreatePixelShaderFromString(const std::string& sPixelShaderSource, bool bDebug = true, const std::string& sResourceID = std::string());
};


#endif // E42_PIXELSHADERFACTORY_H_INCLUDED
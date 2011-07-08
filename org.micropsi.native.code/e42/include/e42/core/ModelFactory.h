#pragma once

#ifndef E42_MODELFACTORY_H_INCLUDED
#define E42_MODELFACTORY_H_INCLUDED

#include "e42/stdinc.h"

#include <d3dx9mesh.h>

#include "e42/core/ResourceHandles.h"
#include "e42/core/ResourceFactory.h"
#include "e42/core/MeshLoaderOptions.h"

#include "baselib/dynarray.h"

class CEngineController;
class CSceneGraphAllocator;

class CModelFactory : public CResourceFactory
{
private:
    CEngineController*      m_pxEngineController;
    CSceneGraphAllocator*   m_pxSceneGraphAllocator;

	CMeshLoaderOptions		m_xDefaultOptions;


    bool                InitModelFromX(CModel* pModel, const std::string& rsFilename, CMeshLoaderOptions* pxOptions);

    void                DestroyResource(void* pxResource);                      ///< löschen einer Resource (Implementation der virtuellen Funktion der Basisklasse)
    CResourceProxy*     CloneResourceProxy(CResourceProxy* pxResourceProxy);    ///< clont einen ResourceProxy und sein Modell

public:
    CModelFactory(CEngineController* pxEngineController);
    ~CModelFactory();

	TModelHandle CreateModelFromFile(const std::string& sFilename, CMeshLoaderOptions* pxOptions = NULL, bool bCloneModel = false);
};

#endif // E42_MODELFACTORY_H_INCLUDED
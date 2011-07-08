#pragma once

#ifndef XFILELOADER_H_INCLUDED
#define XFILELOADER_H_INCLUDED

#include "e42/stdinc.h"
#include "baselib/comobjectptr.h"

#include <string>

class CD3DXFrame;
class CD3DXMeshContainer;
struct ID3DXAnimationController;
struct ID3DXAnimationSet;

class CEngineController;
class CMaterialConverter;
class CSceneGraphAllocator;
class CD3DXEffectInstance;
class CMeshLoaderOptions;

class CXFileLoader
{
private:

    CEngineController*                      m_pxEngineController;
    CMaterialConverter*                            m_pxMaterialConverter;

    CD3DXFrame*                             m_pxFrameRoot;
    CComObjectPtr<ID3DXAnimationController> m_spxAnimationController;
    CComObjectPtr<ID3DXAnimationSet>        m_spxDefaultAnimationSet;


public:

    CXFileLoader(CEngineController* pxEngineController);
    ~CXFileLoader();


    // Laden von Meshes (mit Hierarchie)
    bool                                    BeginLoad(const std::string& sXFile, CSceneGraphAllocator* pxAllocator, CMeshLoaderOptions* pxOptions);

    CD3DXFrame*                             GetFrameRoot() const;
    CComObjectPtr<ID3DXAnimationController> GetAnimationController() const;
    CComObjectPtr<ID3DXAnimationSet>        GetDefaultAnimationSet() const;

    void                                    EndLoad();


	// Laden eines Einzelnen Meshes
	CD3DXMeshContainer*						LoadSingleMesh(const char* pcFilename, DWORD dwMeshOptions);

	
	// Laden einer Animationen
    CComObjectPtr<ID3DXAnimationSet>        LoadAnimation(const std::string& sXFile);


    // Meshes löschen
    void                                    FreeHierarchy(CD3DXFrame* pxXFrameRoot);


    // textur->fx-mapping für XAllocator laden
    void                                    LoadFXMapping(const std::string& sXMLFile);
    void                                    SetDefaultEffect(const CD3DXEffectInstance& rxEffectInstance);
};

#endif // XFILELOADER_H_INCLUDED
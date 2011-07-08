#pragma once

#ifndef E42_SURFACEFACTORY_H_INCLUDED
#define E42_SURFACEFACTORY_H_INCLUDED

#include "e42/stdinc.h"

#include <windows.h>
#include <d3d9types.h>

#include "e42/core/ResourceHandles.h"
#include "e42/core/ResourceFactory.h"

struct IDirect3DTexture9;

class CEngineController;

class CSurfaceFactory : public CResourceFactory
{
private:
	CEngineController*	m_pxEngineController;

	void	DestroyResource(void* pResource);

public:
	CSurfaceFactory(CEngineController* pxEngineController);
	~CSurfaceFactory();

	TSurfaceHandle CreateDepthStencilSurface(int iWidth, int iHeight, D3DFORMAT Format, D3DMULTISAMPLE_TYPE MultiSample, DWORD MultisampleQuality, const std::string& sResourceID = std::string());
	TSurfaceHandle GetRenderTarget(DWORD dwRenderTargetIndex, const std::string& sResourceID = std::string());
	TSurfaceHandle GetDepthStencilSurface(const std::string& sResourceID = std::string());
	TSurfaceHandle GetSurfaceLevel(IDirect3DTexture9* pd3dTexture, int iLevel, const std::string& sResourceID = std::string());

	TSurfaceHandle CreateOffscreenPlainSurfaceFromFile(const std::string sFilename, bool bHaltOnError = true);
};


#endif // E42_SURFACEFACTORY_H_INCLUDED
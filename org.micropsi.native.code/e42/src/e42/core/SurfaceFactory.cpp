#include "stdafx.h"

#include "e42/core/SurfaceFactory.h"

#include "e42/core/EngineController.h"

#include "baselib/filelocator.h"

#include <d3d9.h>
#include <d3dx9core.h>

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CSurfaceFactory::CSurfaceFactory(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CSurfaceFactory::~CSurfaceFactory()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CSurfaceFactory::DestroyResource(void* pxResource)
{
	((IDirect3DSurface9*)pxResource)->Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TSurfaceHandle 
CSurfaceFactory::CreateDepthStencilSurface(
	int iWidth, int iHeight, D3DFORMAT Format, D3DMULTISAMPLE_TYPE MultiSample, 
	DWORD MultisampleQuality, const std::string& sResourceID)
{
	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

	if (pxResourceProxy == NULL)
	{
		IDirect3DSurface9* pd3dSurface = NULL;

		HRESULT hr = m_pxEngineController->GetDevice()->
			CreateDepthStencilSurface(iWidth, iHeight, Format, MultiSample, MultisampleQuality, FALSE, &pd3dSurface, NULL);
		assert(SUCCEEDED(hr));

		pxResourceProxy = __super::AddResource(sResourceID, pd3dSurface);
	}

	return TSurfaceHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TSurfaceHandle 
CSurfaceFactory::GetRenderTarget(DWORD dwRenderTargetIndex, const std::string& sResourceID)
{
	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

	if (pxResourceProxy == NULL)
	{
		IDirect3DSurface9* pd3dSurface = NULL;

		HRESULT hr = m_pxEngineController->GetDevice()->GetRenderTarget(dwRenderTargetIndex, &pd3dSurface);
		assert(SUCCEEDED(hr));

		pxResourceProxy = __super::AddResource(sResourceID, pd3dSurface);
	}

	return TSurfaceHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TSurfaceHandle 
CSurfaceFactory::GetDepthStencilSurface(const std::string& sResourceID)
{
	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

	if (pxResourceProxy == NULL)
	{
		IDirect3DSurface9* pd3dSurface = NULL;

		HRESULT hr = m_pxEngineController->GetDevice()->GetDepthStencilSurface(&pd3dSurface);
		assert(SUCCEEDED(hr) || (hr == D3DERR_NOTFOUND));

		pxResourceProxy = __super::AddResource(sResourceID, pd3dSurface);
	}

	return TSurfaceHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TSurfaceHandle 
CSurfaceFactory::GetSurfaceLevel(IDirect3DTexture9* pd3dTexture, int iLevel, const std::string& sResourceID)
{
	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

	if (pxResourceProxy == NULL)
	{
		IDirect3DSurface9* pd3dSurface = NULL;

		HRESULT hr = pd3dTexture->GetSurfaceLevel(iLevel, &pd3dSurface);
		assert(SUCCEEDED(hr));

		pxResourceProxy = __super::AddResource(sResourceID, pd3dSurface);
	}

	return TSurfaceHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TSurfaceHandle 
CSurfaceFactory::CreateOffscreenPlainSurfaceFromFile(const string sFilename, bool bHaltOnError)
{
	string sFullName = m_pxEngineController->GetFileLocator()->GetPath(sFilename);

	const string sResourceID = sFullName;
	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

	if (pxResourceProxy == NULL)
	{
		D3DXIMAGE_INFO xImageInfo;
		HRESULT hr = D3DXGetImageInfoFromFile(sFullName.c_str(), &xImageInfo);

		if (bHaltOnError &&
			FAILED(hr))
		{
			if (!m_pxEngineController->GetErrorMessagesEnabled() ||
				(IDCANCEL == MessageBox(NULL, sFilename.c_str(), "surface load failed!", MB_ICONERROR | MB_OKCANCEL)))
			{
				exit(-1);
			}
		}


		IDirect3DSurface9* pd3dSurface = NULL;
		
		hr = m_pxEngineController->GetDevice()->CreateOffscreenPlainSurface(
			xImageInfo.Width,
			xImageInfo.Height,
			D3DFMT_A8R8G8B8,
			D3DPOOL_DEFAULT,
			&pd3dSurface,
			NULL);


		assert(SUCCEEDED(hr));

		if (pd3dSurface)
		{
			hr = D3DXLoadSurfaceFromFile(
				pd3dSurface, NULL,
				NULL,
				sFullName.c_str(),
				NULL,
				D3DX_FILTER_NONE,
				0,
				NULL);

			assert(SUCCEEDED(hr));
		}
		
		pxResourceProxy = __super::AddResource(sResourceID, pd3dSurface);
	}

	return TSurfaceHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

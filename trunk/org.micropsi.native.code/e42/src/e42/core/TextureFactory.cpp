#include "stdafx.h"

#include "e42/core/TextureFactory.h"

#include "e42/core/EngineController.h"
#include "e42/core/DeviceStateMgr.h"
#include "baselib/filelocator.h"
#include "baselib/utils.h"

#include <d3d9.h>
#include <d3dx9tex.h>

#include "tinyxml.h"

using std::string;
using std::map;

//-----------------------------------------------------------------------------------------------------------------------------------------
CTextureFactory::CTextureFactory(CEngineController* pxEngineController)
:	m_pxEngineController(pxEngineController)
{
	m_xTexturePool = D3DPOOL_MANAGED;

	m_iTextureLoDLevel = 0;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CTextureFactory::~CTextureFactory()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CTextureFactory::DestroyResource(void* pxResource)
{
	((IDirect3DBaseTexture9*)pxResource)->Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CTextureFactory::ValidateTextureSetting(D3DRESOURCETYPE rtype, DWORD usage, CTextureSettings::CLodSetting* pxOutLodSettings) const
{
	// prüfen, ob das gewünschte Format unterstützt wird
	if (pxOutLodSettings->m_fmtTextureFormat != D3DFMT_UNKNOWN &&
		!IsTextureFormatValidForRendering(pxOutLodSettings->m_fmtTextureFormat, rtype, usage))
	{
		DebugPrint("warning: texture format not supported!");
		pxOutLodSettings->m_fmtTextureFormat = D3DFMT_A8R8G8B8;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CTextureFactory::CheckImageSize(const string& sFilename, int iWidth, int iHeight, int iDepth) const
{
	if (!Utils::IsPowerOf2(iWidth) ||
		!Utils::IsPowerOf2(iHeight) ||
		!Utils::IsPowerOf2(iDepth))
	{
		if (IDCANCEL == MessageBox(NULL, sFilename.c_str(), "Error: texture size is not power of 2!", MB_ICONERROR | MB_OKCANCEL))
		{
			exit(-1);
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CTextureFactory::GetTextureSettings(string sFilename, CTextureLoaderOptions* pxOptions) const
{
	if (!m_mTextureSettings.empty())
	{
		sFilename = CFileLocator::ExtractFilename(sFilename);
		sFilename = CFileLocator::RemoveFileExtension(sFilename);
		sFilename = Utils::StrToLower(sFilename);
	    
		map<const string, CTextureSettings>::const_iterator iter;


		iter = m_mTextureSettings.find(sFilename);

		if (iter == m_mTextureSettings.end())
		{
			iter = m_mTextureSettings.find(".default");
		}


		if (iter != m_mTextureSettings.end())
		{
			const CTextureSettings::CLodSetting& rxSetting = iter->second.m_axLodLevels[m_iTextureLoDLevel];

			if (pxOptions->m_iWidth == D3DX_DEFAULT)
			{
				pxOptions->m_iWidth = rxSetting.m_xSize.cx;
			}

			if (pxOptions->m_iHeight == D3DX_DEFAULT)
			{
				pxOptions->m_iHeight = rxSetting.m_xSize.cy;
			}

			if (pxOptions->m_iMipLevels = D3DX_DEFAULT)
			{
				pxOptions->m_iMipLevels = rxSetting.m_iMaxMipMapLevel;
			}

			if (pxOptions->m_eFormat == D3DFMT_UNKNOWN)
			{
				pxOptions->m_eFormat = rxSetting.m_fmtTextureFormat;
			}
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TTextureHandle 
CTextureFactory::CreateTextureFromFile(const string& sFilename, const CTextureLoaderOptions* pxOptions)
{
	CTextureLoaderOptions xOptions = 
		pxOptions	? *pxOptions
					: m_xDefaultOptions;


	CTextureSettings::CLodSetting xTextureLodSettings;
	GetTextureSettings(sFilename, &xOptions);


	string sFullName = m_pxEngineController->GetFileLocator()->GetPath(sFilename);
	sFullName = CFileLocator::InsertBeforeFilename(sFullName, xTextureLodSettings.m_sSubDirectory);

	const string sResourceID = sFullName;
	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);


	if (pxResourceProxy == NULL)
	{
		ValidateTextureSetting(D3DRTYPE_TEXTURE, 0, &xTextureLodSettings);

		IDirect3DBaseTexture9* pd3dTexture = NULL;

		if (CFileLocator::FileExists(sFullName))
		{
			HRESULT hr;
				
			D3DXIMAGE_INFO xImageInfo;
			hr = D3DXGetImageInfoFromFile(sFullName.c_str(), &xImageInfo);
			assert(SUCCEEDED(hr));

			CheckImageSize(sFullName, xImageInfo.Width, xImageInfo.Height, xImageInfo.Depth);


			switch (xImageInfo.ResourceType)
			{
			case D3DRTYPE_TEXTURE :
				hr = D3DXCreateTextureFromFileEx(
						m_pxEngineController->GetDevice(), 
						sFullName.c_str(), 
						xOptions.m_iWidth, xOptions.m_iHeight,
						xOptions.m_iMipLevels,
						xOptions.m_dwUsage, xOptions.m_eFormat, xOptions.m_ePool,
						xOptions.m_dwFilter, xOptions.m_dwMipFilter,
						xOptions.m_xColorKey,
						&xImageInfo,
						NULL,	//palette
						(IDirect3DTexture9**)&pd3dTexture);
				break;

			case D3DRTYPE_CUBETEXTURE :
				hr = D3DXCreateCubeTextureFromFileEx(
						m_pxEngineController->GetDevice(), 
						sFullName.c_str(), 
						xOptions.m_iWidth,
						xOptions.m_iMipLevels,
						xOptions.m_dwUsage, xOptions.m_eFormat, xOptions.m_ePool,
						xOptions.m_dwFilter, xOptions.m_dwMipFilter,
						xOptions.m_xColorKey,
						&xImageInfo,
						NULL,	//palette
						(IDirect3DCubeTexture9**)&pd3dTexture);
				break;

			case D3DRTYPE_VOLUMETEXTURE :
				hr = D3DXCreateVolumeTextureFromFileEx(
						m_pxEngineController->GetDevice(), 
						sFullName.c_str(), 
						xOptions.m_iWidth, xOptions.m_iHeight, xOptions.m_iDepth,
						xOptions.m_iMipLevels,
						xOptions.m_dwUsage, xOptions.m_eFormat, xOptions.m_ePool,
						xOptions.m_dwFilter, xOptions.m_dwMipFilter,
						xOptions.m_xColorKey,
						&xImageInfo, 
						NULL,	//palette
						(IDirect3DVolumeTexture9**)&pd3dTexture);
				break;

			default:
				hr = -1;
				assert(false);
			}

			assert(SUCCEEDED(hr));
		}

		if (!xOptions.m_bIgnoreErrors &&
			pd3dTexture == 0)
		{
			assert(false);

			if (!m_pxEngineController->GetErrorMessagesEnabled())
			{
				exit(-1);
			}

			if (IDCANCEL == MessageBox(NULL, sFullName.c_str(), "texture load failed!", MB_ICONERROR | MB_OKCANCEL))
			{
				exit(-1);
			}

			pd3dTexture = (IDirect3DTexture9*)m_hndErrorTexture.GetPtr();

			if (pd3dTexture)
				pd3dTexture->AddRef();
		}

		pxResourceProxy = __super::AddResource(sResourceID, pd3dTexture);
	}

	return TTextureHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TTextureHandle 
CTextureFactory::CreateTexture(
		int iWidth, int iHeight, int iLevels, DWORD Usage, 
		D3DFORMAT Format, D3DPOOL pool, const string& sResourceID)
{
	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

	if (pxResourceProxy == NULL)
	{
		IDirect3DTexture9* pd3dTexture = NULL;

		HRESULT hr = m_pxEngineController->GetDevice()->
			CreateTexture(iWidth, iHeight, iLevels, Usage, Format, pool, &pd3dTexture, NULL);

		assert(SUCCEEDED(hr));

		pxResourceProxy = __super::AddResource(sResourceID, pd3dTexture);
	}

	return TTextureHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TTextureHandle  
CTextureFactory::RegisterTexture(IDirect3DTexture9* pd3dTexture, const string& sResourceID)
{
	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

	if (pxResourceProxy == NULL)
	{
		pd3dTexture->AddRef();

		pxResourceProxy = __super::AddResource(sResourceID, pd3dTexture);
	}

	return TTextureHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool		
CTextureFactory::IsTextureFormatValidForRendering(D3DFORMAT eTextureFormat, D3DRESOURCETYPE rtype, DWORD usage) const
{
	D3DDEVICE_CREATION_PARAMETERS   xDeviceCreationParameters;
	m_pxEngineController->GetDevice()->GetCreationParameters(&xDeviceCreationParameters);

	HRESULT hr = 
		m_pxEngineController->GetD3D()->CheckDeviceFormat(
			xDeviceCreationParameters.AdapterOrdinal,
			xDeviceCreationParameters.DeviceType,
			m_pxEngineController->GetBackbufferDesc().Format,
			usage,
			rtype,
			eTextureFormat);

	return (hr != S_FALSE && SUCCEEDED(hr));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CTextureFactory::PrecacheTextures()
{
	if (GetNumResources() == 0)
	{
		return;
	}

	struct XYZWUVVERTEX
	{
		float x, y, z, w;
		float u, v;
	};

	for (int iPower = 0; iPower <= 10; iPower++)
	{
		float fSize = (float)(1 << iPower);

		XYZWUVVERTEX axVertices[] =
		{
			{0,     0,      1.0f, 1.0f,     0, 0},
			{fSize, 0,      1.0f, 1.0f,     1, 0},
			{0,     fSize,  1.0f, 1.0f,     0, 1},
			{fSize, fSize,  1.0f, 1.0f,     1, 1}
		};

		IDirect3DDevice9* pd3dDevice = m_pxEngineController->GetDevice();
		CDeviceStateMgr* pd3dDeviceStateMgr = m_pxEngineController->GetDeviceStateMgr();
		CResourceProxy* pResourceProxy;


		pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_COLORARG1, D3DTA_TEXTURE);
		pd3dDeviceStateMgr->SetTextureStageState(0, D3DTSS_COLOROP, D3DTOP_SELECTARG1);
		pd3dDeviceStateMgr->SetTextureStageState(1, D3DTSS_COLOROP, D3DTOP_DISABLE);
		pd3dDeviceStateMgr->SetRenderState(D3DRS_CULLMODE, D3DCULL_NONE);
		pd3dDeviceStateMgr->SetRenderState(D3DRS_ALPHABLENDENABLE, FALSE);
		pd3dDeviceStateMgr->SetRenderState(D3DRS_ALPHATESTENABLE, FALSE);
		pd3dDeviceStateMgr->SetRenderState(D3DRS_ZENABLE, FALSE);

		pd3dDeviceStateMgr->SetVertexShader(NULL);
		pd3dDeviceStateMgr->SetPixelShader(NULL);
		pd3dDeviceStateMgr->SetFVF(D3DFVF_XYZRHW | D3DFVF_TEX1 | D3DFVF_TEXCOORDSIZE2(0));


		TResourceIterator iter;
		__super::StartIterateResources(iter);

		while (pResourceProxy = __super::IterateResources(iter))
		{
			((IDirect3DBaseTexture9*)pResourceProxy->GetResource())->PreLoad();
			pd3dDeviceStateMgr->SetTexture(0, (IDirect3DBaseTexture9*)(pResourceProxy->GetResource()));

			pd3dDevice->DrawPrimitiveUP(D3DPT_TRIANGLESTRIP, 2, axVertices, sizeof(XYZWUVVERTEX));
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CTextureFactory::SetTexturePool(D3DPOOL xTexturePool)
{
	m_xDefaultOptions.m_ePool = xTexturePool;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CTextureFactory::LoadTextureSettings(const std::string& sTextureSettingFile)
{
	string sFullName = m_pxEngineController->GetFileLocator()->GetPath(sTextureSettingFile.c_str());

	TiXmlDocument* pxXMLDoc = new TiXmlDocument(sFullName.c_str());
	if (!pxXMLDoc->LoadFile()) 
	{ 
		DebugPrint("could not load TextureSetting file %s", sTextureSettingFile.c_str());
		assert(false);
		delete pxXMLDoc;
		return; 
	}

	TiXmlNode* pxRootNode = pxXMLDoc->FirstChild("texturesettings");
	if(!pxRootNode) return;


	TiXmlElement* pxTextureElement = pxRootNode->FirstChildElement("texture");

	while (pxTextureElement)
	{
		// Namen der Textur aus dem Attribut auslesen
		const char* pcTextureName = pxTextureElement->Attribute("name");
		if (pcTextureName == NULL)
		{
			assert(false);
			break;
		}

		string sTextureName = pcTextureName;
		sTextureName = Utils::StrToLower(sTextureName);
		m_mTextureSettings[sTextureName].InitFromXML(pxTextureElement);

		pxTextureElement = pxTextureElement->NextSiblingElement("texture");
	}

	delete pxXMLDoc;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CTextureFactory::SetTextureLoDLevel(int iLoDLevel)
{
	assert(iLoDLevel >= 0 && iLoDLevel < CTextureSettings::NUM_TEXTURE_LOD_LEVELS);
	m_iTextureLoDLevel = iLoDLevel;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CTextureFactory::SetErrorTexture(const std::string& sFilename)
{
	m_hndErrorTexture = CreateTextureFromFile(sFilename);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

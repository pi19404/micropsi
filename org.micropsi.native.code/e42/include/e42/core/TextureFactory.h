#pragma once

#ifndef E42_TEXTUREFACTORY_H_INCLUDED
#define E42_TEXTUREFACTORY_H_INCLUDED

#include "e42/stdinc.h"

#include <windows.h>
#include <d3d9types.h>

#include "e42/core/ResourceHandles.h"
#include "e42/core/ResourceFactory.h"
#include "e42/core/TextureSettings.h"
#include "e42/core/TextureLoaderOptions.h"

class CEngineController;

struct IDirect3DTexture9;

class CTextureFactory : public CResourceFactory
{
private:
	CEngineController*				m_pxEngineController;


	void	DestroyResource(void* pxResource);

	void	ValidateTextureSetting(D3DRESOURCETYPE rtype, DWORD usage, CTextureSettings::CLodSetting* pxOutLodSettings) const;
	void	CheckImageSize(const std::string& sFilename, int iWidth, int iHeight, int iDepth) const;
	void	GetTextureSettings(std::string sFilename, CTextureLoaderOptions* pxOptions) const;

	std::map<const std::string, CTextureSettings> m_mTextureSettings;	///< Zuordnung von Texturnamen auf TexturSettings

	D3DPOOL					m_xTexturePool;								///< Speicherort, an den Texturen, die aus Files geladen werden abgelegt werden
	int						m_iTextureLoDLevel;							///< Level of Detail für die Texturen

	CTextureLoaderOptions	m_xDefaultOptions;

	TTextureHandle			m_hndErrorTexture;


public:
	
	CTextureFactory(CEngineController* pxEngineController);
	~CTextureFactory();


	TTextureHandle	CreateTextureFromFile(const std::string& sFilename, const CTextureLoaderOptions* pxOptions = NULL);
	TTextureHandle	CreateTexture(int iWidth, int iHeight, int iLevels, DWORD Usage, D3DFORMAT Format, D3DPOOL pool, const std::string& sResourceID = std::string());
	
	TTextureHandle	RegisterTexture(IDirect3DTexture9* pd3dTexture, const std::string& sName = std::string());


	bool			IsTextureFormatValidForRendering(D3DFORMAT eTextureFormat, D3DRESOURCETYPE rtype = D3DRTYPE_TEXTURE, DWORD usage = 0) const;

	void			PrecacheTextures();			// rendert für jede Textur eine Primitive; muss innerhalb von BeginScene/EndScene aufgerufen werden

	void			LoadTextureSettings(const std::string& sTextureSettingFile);

	void			SetTextureLoDLevel(int iLoDLevel);
	void			SetTexturePool(D3DPOOL xTexturePool);


	void			SetErrorTexture(const std::string& sFilename);
};


#endif // E42_TEXTUREFACTORY_H_INCLUDED
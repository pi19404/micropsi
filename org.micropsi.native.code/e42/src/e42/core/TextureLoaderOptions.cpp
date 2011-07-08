#include "stdafx.h"

#include "e42/core/TextureLoaderOptions.h"

#include <d3dx9.h>

//-----------------------------------------------------------------------------------------------------------------------------------------
CTextureLoaderOptions::CTextureLoaderOptions()
{
	m_iWidth = D3DX_DEFAULT;
	m_iHeight = D3DX_DEFAULT;
	m_iDepth = D3DX_DEFAULT;

	m_iMipLevels = D3DX_DEFAULT;
	m_dwUsage = 0;
	m_eFormat = D3DFMT_UNKNOWN;
	m_ePool = D3DPOOL_MANAGED;

	m_dwFilter = D3DX_DEFAULT;
	m_dwMipFilter = D3DX_DEFAULT;
	m_xColorKey = 0;

	m_bIgnoreErrors = false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

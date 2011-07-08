#ifndef TEXTURELOADEROPTIONS_H_INCLUDED
#define TEXTURELOADEROPTIONS_H_INCLUDED

#include "e42/stdinc.h"

#include <d3d9.h>

class CTextureLoaderOptions
{
public:
	CTextureLoaderOptions();

	int			m_iWidth;
	int			m_iHeight;
	int			m_iDepth;

	int			m_iMipLevels;
	DWORD		m_dwUsage;
	D3DFORMAT	m_eFormat;
	D3DPOOL		m_ePool;

	DWORD		m_dwFilter;
	DWORD		m_dwMipFilter;
	D3DCOLOR	m_xColorKey;

	bool		m_bIgnoreErrors;
};

#endif // TEXTURELOADEROPTIONS_H_INCLUDED

#pragma once

#ifndef E42_INDEXBUFFERFACTORY_H_INCLUDED
#define E42_INDEXBUFFERFACTORY_H_INCLUDED

#include "e42/stdinc.h"

#include <windows.h>
#include <d3d9types.h>

#include "e42/core/ResourceHandles.h"
#include "e42/core/ResourceFactory.h"

class CEngineController;

class CIndexBufferFactory : public CResourceFactory
{
private:
	CEngineController*	m_pxEngineController;

	void	DestroyResource(void* pResource);

public:
	CIndexBufferFactory(CEngineController* pxEngineController);
	~CIndexBufferFactory();

	TIndexBufferHandle CreateIndexBuffer(
		int iIndexCount, D3DFORMAT xIndexType, 
		DWORD usage = D3DUSAGE_WRITEONLY, D3DPOOL pool = D3DPOOL_DEFAULT, 
		const void* const pIndices = 0, const std::string& sResourceID = std::string());
};


#endif // E42_INDEXBUFFERFACTORY_H_INCLUDED
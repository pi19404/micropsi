#pragma once

#ifndef E42_VERTEXBUFFERFACTORY_H_INCLUDED
#define E42_VERTEXBUFFERFACTORY_H_INCLUDED

#include "e42/stdinc.h"

#include <d3d9.h>

#include "e42/core/ResourceHandles.h"
#include "e42/core/ResourceFactory.h"

class CEngineController;

class CVertexBufferFactory : public CResourceFactory
{
private:
	CEngineController*  m_pxEngineController;

	void	DestroyResource(void* pxResource);

	void	CopyVertices(IDirect3DVertexBuffer9* pd3dVertexBuffer, const void* pVertices, int iBufferSize);


public:

	CVertexBufferFactory(CEngineController* pxEngineController);
	~CVertexBufferFactory();


	// Buffer mit FVF-Kombination anlegen
	TVertexBufferHandle CreateVertexBufferFVF(
		int iVertexCount, DWORD xVertexFVF, 
		DWORD usage = D3DUSAGE_WRITEONLY, D3DPOOL pool = D3DPOOL_DEFAULT, 
		const void* const pVertices = NULL, const std::string& sResourceID = std::string());

	// Buffer mit Declarator anlegen
	TVertexBufferHandle CreateVertexBuffer(
		int iVertexCount, int iVertexSize, 
		DWORD usage = D3DUSAGE_WRITEONLY, D3DPOOL pool = D3DPOOL_DEFAULT, 
		const void* const pVertices = NULL, const std::string& sResourceID = std::string());
};


#endif // E42_VERTEXBUFFERFACTORY_H_INCLUDED
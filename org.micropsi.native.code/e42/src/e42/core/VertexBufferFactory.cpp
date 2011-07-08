#include "stdafx.h"

#include "e42/core/VertexBufferFactory.h"

#include "e42/core/EngineController.h"

#include <d3d9.h>
#include <d3dx9mesh.h>

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CVertexBufferFactory::CVertexBufferFactory(CEngineController* pxEngineController)
:	m_pxEngineController(pxEngineController)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CVertexBufferFactory::~CVertexBufferFactory()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CVertexBufferFactory::DestroyResource(void* pxResource)
{
	((IDirect3DVertexBuffer9*)pxResource)->Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CVertexBufferFactory::CopyVertices(IDirect3DVertexBuffer9* pd3dVertexBuffer, const void* pVertices, int iBufferSize)
{
	if (pVertices == NULL)
	{
		return;
	}

	void* pVertexMem = NULL;
	HRESULT hr = pd3dVertexBuffer->Lock(
			0, iBufferSize, &pVertexMem, 0);

	if (SUCCEEDED(hr))
	{
		memcpy(pVertexMem, pVertices, iBufferSize);
		hr = pd3dVertexBuffer->Unlock();
		assert(SUCCEEDED(hr));
	}
	else
	{
		assert(false);
		if (pd3dVertexBuffer) 
		{
			pd3dVertexBuffer->Release(); 
			pd3dVertexBuffer = 0;
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TVertexBufferHandle 
CVertexBufferFactory::CreateVertexBufferFVF(
	int iVertexCount, DWORD xVertexFVF, 
	DWORD usage, D3DPOOL pool, const void* const pVertices, const std::string& sResourceID)
{
	assert(xVertexFVF != 0);

	CResourceProxy* pxResourceProxy = LookUpResource(sResourceID);

	if (pxResourceProxy == NULL)
	{
		IDirect3DVertexBuffer9* pd3dVertexBuffer = NULL;

		int iBufferSize = 
			iVertexCount * D3DXGetFVFVertexSize(xVertexFVF);

		HRESULT hr =
			m_pxEngineController->GetDevice()->
				CreateVertexBuffer(
					iBufferSize, usage, xVertexFVF,
					pool, &pd3dVertexBuffer, NULL);

		if (SUCCEEDED(hr))
		{
			CopyVertices(pd3dVertexBuffer, pVertices, iBufferSize);
		}
		else
		{
			assert(false);
		}

		pxResourceProxy = AddResource(sResourceID, pd3dVertexBuffer);
	}

	return TVertexBufferHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TVertexBufferHandle 
CVertexBufferFactory::CreateVertexBuffer(
	int iVertexCount, int iVertexSize, 
	DWORD usage, D3DPOOL pool, const void* const pVertices, const std::string& sResourceID)
{
	CResourceProxy* pxResourceProxy = LookUpResource(sResourceID);

	if (pxResourceProxy == NULL)
	{
		IDirect3DVertexBuffer9* pd3dVertexBuffer = NULL;

		int iBufferSize = iVertexCount * iVertexSize;

		HRESULT hr =
			m_pxEngineController->GetDevice()->
				CreateVertexBuffer(
					iBufferSize, usage, 0,
					pool, &pd3dVertexBuffer, NULL);

		if (SUCCEEDED(hr))
		{
			CopyVertices(pd3dVertexBuffer, pVertices, iBufferSize);
		}
		else
		{
			assert(false);
		}

		pxResourceProxy = AddResource(sResourceID, pd3dVertexBuffer);
	}

	return TVertexBufferHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

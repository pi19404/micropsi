#include "stdafx.h"

#include "e42/core/VertexShaderFactory.h"

#include "baselib/filelocator.h"
#include "baselib/comobjectptr.h"
#include "e42/core/EngineController.h"

#include <d3d9.h>
#include <d3dx9core.h>

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CVertexShaderFactory::CVertexShaderFactory(CEngineController* pxEngineController)
:	m_pxEngineController(pxEngineController)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CVertexShaderFactory::~CVertexShaderFactory()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CVertexShaderFactory::DestroyResource(void* pxResource)
{
	((IDirect3DVertexShader9*)pxResource)->Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TVertexShaderHandle 
CVertexShaderFactory::CreateVertexShader(const string& sFilename, bool bDebug)
{
	string sFullName = m_pxEngineController->GetFileLocator()->GetPath(sFilename);

	const string sResourceID = sFullName;
	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);


	if (pxResourceProxy == NULL)
	{
		IDirect3DVertexShader9* pd3dVertexShader = NULL;

		bDebug &= m_pxEngineController->GetVertexShaderDebuggingEnabled();

		CComObjectPtr<ID3DXBuffer> spxCode;
		CComObjectPtr<ID3DXBuffer> spxErrorMsgs;

		HRESULT hr = D3DXAssembleShaderFromFile(
			sFullName.c_str(),
			NULL, NULL, 
			bDebug ? D3DXSHADER_DEBUG | D3DXSHADER_SKIPOPTIMIZATION : 0, 
			&spxCode, &spxErrorMsgs);


		if (spxErrorMsgs)
		{
			if (m_pxEngineController->GetErrorMessagesEnabled())
			{
				MessageBox(NULL, (LPCSTR)spxErrorMsgs->GetBufferPointer(), "vsh-error", MB_ICONERROR | MB_OK);
			}

			exit(-1);
			return TVertexShaderHandle();
		}

		assert(SUCCEEDED(hr));
		assert(spxCode);

		if (spxCode)
		{
			HRESULT hr = m_pxEngineController->GetDevice()->CreateVertexShader((DWORD*)spxCode->GetBufferPointer(), &pd3dVertexShader);
			assert(SUCCEEDED(hr));
		}


		pxResourceProxy = __super::AddResource(sResourceID, pd3dVertexShader);
	}

	return TVertexShaderHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TVertexShaderHandle 
CVertexShaderFactory::CreateVertexShaderFromString(const std::string& sVertexShaderSource, bool bDebug, const string& sResourceID)
{
	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

	if (pxResourceProxy == NULL)
	{
		IDirect3DVertexShader9* pd3dVertexShader = NULL;

		bDebug &= m_pxEngineController->GetVertexShaderDebuggingEnabled();

		CComObjectPtr<ID3DXBuffer> spxCode;
		CComObjectPtr<ID3DXBuffer> spxErrorMsgs;

		HRESULT hr = D3DXAssembleShader(
			sVertexShaderSource.c_str(), (UINT)strlen(sVertexShaderSource.c_str()), NULL, NULL,
			bDebug ? D3DXSHADER_DEBUG | D3DXSHADER_SKIPOPTIMIZATION : 0, 
			&spxCode, &spxErrorMsgs);


		if (spxErrorMsgs)
		{
			if (m_pxEngineController->GetErrorMessagesEnabled())
			{
				MessageBox(NULL, (LPCSTR)spxErrorMsgs->GetBufferPointer(), "vsh-error", MB_ICONERROR | MB_OK);
			}

			exit(-1);
			return TVertexShaderHandle();
		}

		assert(SUCCEEDED(hr));
		assert(spxCode);

		if (spxCode)
		{
			HRESULT hr = m_pxEngineController->GetDevice()->CreateVertexShader((DWORD*)spxCode->GetBufferPointer(), &pd3dVertexShader);
			assert(SUCCEEDED(hr));
		}


		pxResourceProxy = __super::AddResource(sResourceID, pd3dVertexShader);
	}

	return TVertexShaderHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

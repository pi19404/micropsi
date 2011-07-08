#include "stdafx.h"

#include "e42/core/PixelShaderFactory.h"

#include "baselib/filelocator.h"
#include "baselib/comobjectptr.h"
#include "e42/core/EngineController.h"

#include <d3d9.h>
#include <d3dx9core.h>

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CPixelShaderFactory::CPixelShaderFactory(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CPixelShaderFactory::~CPixelShaderFactory()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CPixelShaderFactory::DestroyResource(void* pxResource)
{
    ((IDirect3DPixelShader9*)pxResource)->Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TPixelShaderHandle 
CPixelShaderFactory::CreatePixelShader(const string& sFilename, bool bDebug)
{
    string sFullName = m_pxEngineController->GetFileLocator()->GetPath(sFilename);

	const string sResourceID = sFullName;
	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

    if (pxResourceProxy == NULL)
    {
        IDirect3DPixelShader9* pd3dPixelShader = NULL;

        bDebug &= m_pxEngineController->GetPixelShaderDebuggingEnabled();

        CComObjectPtr<ID3DXBuffer> spxCode;
        CComObjectPtr<ID3DXBuffer> spxErrorMsgs;

        HRESULT hr = D3DXAssembleShaderFromFile(
            sFullName.c_str(),
            NULL, NULL, 
            bDebug ? D3DXSHADER_DEBUG | D3DXSHADER_SKIPOPTIMIZATION : 0, 
            &spxCode, &spxErrorMsgs);


        if (spxErrorMsgs)
        {
            MessageBox(NULL, (LPCSTR)spxErrorMsgs->GetBufferPointer(), "psh-error", MB_ICONERROR | MB_OK);
            exit(-1);
            return TPixelShaderHandle();
        }

        assert(SUCCEEDED(hr));
        assert(spxCode);

        if (spxCode)
        {
            HRESULT hr = m_pxEngineController->GetDevice()->CreatePixelShader((DWORD*)spxCode->GetBufferPointer(), &pd3dPixelShader);
            assert(SUCCEEDED(hr));
        }


        pxResourceProxy = __super::AddResource(sResourceID, pd3dPixelShader);
    }

    return TPixelShaderHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TPixelShaderHandle 
CPixelShaderFactory::CreatePixelShaderFromString(const std::string& sPixelShaderSource, bool bDebug, const string& sResourceID)
{
    CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

    if (pxResourceProxy == NULL)
    {
        IDirect3DPixelShader9* pd3dPixelShader = NULL;

        bDebug &= m_pxEngineController->GetPixelShaderDebuggingEnabled();

        CComObjectPtr<ID3DXBuffer> spxCode;
        CComObjectPtr<ID3DXBuffer> spxErrorMsgs;

        HRESULT hr = D3DXAssembleShader(
            sPixelShaderSource.c_str(), (UINT)strlen(sPixelShaderSource.c_str()), NULL, NULL,
            bDebug ? D3DXSHADER_DEBUG | D3DXSHADER_SKIPOPTIMIZATION : 0, 
            &spxCode, &spxErrorMsgs); 


        if (spxErrorMsgs)
        {
            MessageBox(NULL, (LPCSTR)spxErrorMsgs->GetBufferPointer(), "psh-error", MB_ICONERROR | MB_OK);
            exit(-1);
            return TPixelShaderHandle();
        }

        assert(SUCCEEDED(hr));
        assert(spxCode);

        if (spxCode)
        {
            HRESULT hr = m_pxEngineController->GetDevice()->CreatePixelShader((DWORD*)spxCode->GetBufferPointer(), &pd3dPixelShader);
            assert(SUCCEEDED(hr));
        }


        pxResourceProxy = __super::AddResource(sResourceID, pd3dPixelShader);
    }

    return TPixelShaderHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

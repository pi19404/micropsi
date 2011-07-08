#include "stdafx.h"

#include "e42/core/EffectFactory.h"

#include "baselib/filelocator.h"
#include "baselib/comobjectptr.h"

#include "e42/core/EngineController.h"
#include "e42/core/EffectShader.h"
#include "e42/core/D3DXMacro.h"

#include <d3d9.h>
#include <d3dx9effect.h>

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CEffectFactory::CEffectFactory(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController),
    m_fpCreateWrapper(CEffectShader::Create),
    m_fpDestroyWrapper(CEffectShader::Destroy)
{
    HRESULT hr = D3DXCreateEffectPool(&m_pd3dEffectPool);
    assert(SUCCEEDED(hr));

    m_bCloneEffects = false;

	CreateStandardEffectMacros();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CEffectFactory::~CEffectFactory()
{
    __super::ReleaseOwnResources();

    m_hndSharedVarsEffect.Release();

    if (m_pd3dEffectPool)
        m_pd3dEffectPool->Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CEffectFactory::CreateStandardEffectMacros()
{
	char acBuffer[256];
	{
		sprintf(acBuffer, "%04x", m_pxEngineController->GetDeviceCaps()->VertexShaderVersion & 0xffff);

		CD3DXMacro& rxMacro = m_axEffectMacros.Push();
		rxMacro.SetName("VERTEXSHADERVERSION");
		rxMacro.SetDefinition(acBuffer);
	}

	{
		sprintf(acBuffer, "%04x", m_pxEngineController->GetDeviceCaps()->PixelShaderVersion & 0xffff);

		CD3DXMacro& rxMacro = m_axEffectMacros.Push();
		rxMacro.SetName("PIXELSHADERVERSION");
		rxMacro.SetDefinition(acBuffer);
	}

	{
		sprintf(acBuffer, "%d", m_pxEngineController->GetDeviceCaps()->MaxVertexShaderConst);

		CD3DXMacro& rxMacro = m_axEffectMacros.Push();
		rxMacro.SetName("MAXVERTEXSHADERCONST");
		rxMacro.SetDefinition(acBuffer);
	}

	{
		CD3DXMacro& rxMacro = m_axEffectMacros.Push();
		rxMacro.SetName("E42");
		rxMacro.SetDefinition("1");
	}

	if (m_pxEngineController->GetDeviceCaps()->StencilCaps & D3DSTENCILCAPS_TWOSIDED)
	{
		CD3DXMacro& rxMacro = m_axEffectMacros.Push();
		rxMacro.SetName("D3DSTENCILCAPS_TWOSIDED");
		rxMacro.SetDefinition("1");
	}

	m_axEffectMacros.Push();	// leeres Makro als Endmarker für Nullterminiertes Array
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CEffectFactory::DestroyResource(void* pxResource)
{
    CEffectShader* pxEffectShader = (CEffectShader*)pxResource;

    pxEffectShader->GetD3DXEffect()->Release();       // (wurde hier erzeugt; wird auch hier released)

    m_fpDestroyWrapper(pxEffectShader);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CResourceProxy*
CEffectFactory::CloneResourceProxy(CResourceProxy* pxResourceProxy)
{
    ID3DXEffect* pd3dEffect = ((CEffectShader*)pxResourceProxy->GetResource())->GetD3DXEffect();

    HRESULT hr;

    IDirect3DDevice9* pd3dDevice = NULL;
    hr = pd3dEffect->GetDevice(&pd3dDevice);
    assert(SUCCEEDED(hr));

    ID3DXEffect* pd3dClonedEffect = NULL;
    hr = pd3dEffect->CloneEffect(pd3dDevice, &pd3dClonedEffect);
    assert(SUCCEEDED(hr));

    hr = pd3dDevice->Release();
    assert(SUCCEEDED(hr));


    return pxResourceProxy = 
        __super::AddResource("", m_fpCreateWrapper(pd3dClonedEffect));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TEffectHandle 
CEffectFactory::CreateEffect(const string sFilename, bool bDebug)
{
    string sName = m_pxEngineController->GetFileLocator()->GetPath(sFilename);
    CResourceProxy* pxResourceProxy = __super::LookUpResource(sName);

    if (pxResourceProxy == NULL)
    {
        ID3DXEffect* pd3dEffect = NULL;

        bDebug |= m_pxEngineController->GetEffectDebuggingEnabled();

        CComObjectPtr<ID3DXBuffer> spxErrorMsgs;

        string sFullName = m_pxEngineController->GetFileLocator()->GetPath(sFilename);

        HRESULT hr;

        do
        {
            hr = D3DXCreateEffectFromFileEx(
                m_pxEngineController->GetDevice(),                                                              // pDevice
                sFullName.c_str(),                                                                              // pSrcFile
				(D3DXMACRO*)m_axEffectMacros.GetArrayPointer(),                                                 // pDefines
                NULL,                                                                                           // pInclude
                NULL,                                                                                           // pSkipConstants
                D3DXSHADER_PARTIALPRECISION | 
				    (bDebug ? D3DXSHADER_DEBUG : 0) |
				    (!m_bCloneEffects ? D3DXFX_NOT_CLONEABLE : 0),												// Flags
                m_pd3dEffectPool,                                                                               // pPool
                &pd3dEffect,                                                                                    // ppEffect
                &spxErrorMsgs);                                                                                 // ppCompilationErrors


			if (spxErrorMsgs || FAILED(hr))
            {
                if (m_pxEngineController->GetErrorMessagesEnabled())
                {
                    ShowCursor(true);
                    int iResult = 
						spxErrorMsgs
							? MessageBox(NULL, (LPCSTR)spxErrorMsgs->GetBufferPointer(), sFilename.c_str(), MB_ICONERROR | MB_ABORTRETRYIGNORE | MB_DEFBUTTON2)
							: MessageBox(NULL, sName.c_str(), "failed to load effect file", MB_ICONERROR | MB_ABORTRETRYIGNORE);

                    ShowCursor(false);

                    if (iResult == IDABORT)  exit(-1);
                    if (iResult == IDRETRY) continue;
                    if (iResult == IDIGNORE) break;
                }
                else
                {
                    exit(-1);
                }
            }
        }
        while (FAILED(hr));

        assert(SUCCEEDED(hr));
        assert(pd3dEffect);


        if (pd3dEffect)
            pd3dEffect->SetStateManager((ID3DXEffectStateManager*)m_pxEngineController->GetDeviceStateMgr());


		CEffectShader* pEffectShader = m_fpCreateWrapper(pd3dEffect);


        pxResourceProxy = __super::AddResource(sName, pEffectShader, !m_bCloneEffects);
    }


    if (m_bCloneEffects)
    {
        pxResourceProxy = CloneResourceProxy(pxResourceProxy);
    }


    return TEffectHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CEffectFactory::SetSharedVarsEffect(TEffectHandle hndSharedVarsEffect)
{
    m_hndSharedVarsEffect = hndSharedVarsEffect;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TEffectHandle 
CEffectFactory::GetSharedVarsEffect() const
{
    return m_hndSharedVarsEffect;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CEffectFactory::SetWrapperCreateFunction(CreateWrapperFunction fpCreateWrapper)
{
    // die Callbacks müssen vor der Erzeugung des ersten Effektes gesetzt werden,
    //  sonst werden bereits angelegt effekte falsch destructed
    assert(GetNumResources() == 0);
    m_fpCreateWrapper = fpCreateWrapper;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CEffectFactory::SetWrapperDestroyFunction(DestroyWrapperFunction fpDestroyWrapper)
{
    // die Callbacks müssen vor der Erzeugung des ersten Effektes gesetzt werden,
    //  sonst werden bereits angelegt effekte falsch destructed
    assert(GetNumResources() == 0);
    m_fpDestroyWrapper = fpDestroyWrapper;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CEffectFactory::SetCloneEffects(bool bEnable)
{
    assert(GetNumResources() == 0);
    m_bCloneEffects = bEnable;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

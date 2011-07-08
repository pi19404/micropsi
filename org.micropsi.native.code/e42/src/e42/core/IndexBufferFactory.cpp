#include "stdafx.h"

#include "e42/core/IndexBufferFactory.h"

#include "e42/core/EngineController.h"

#include <d3d9.h>

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CIndexBufferFactory::CIndexBufferFactory(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CIndexBufferFactory::~CIndexBufferFactory()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CIndexBufferFactory::DestroyResource(void* pxResource)
{
    ((IDirect3DIndexBuffer9*)pxResource)->Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TIndexBufferHandle 
CIndexBufferFactory::CreateIndexBuffer(
    int iIndexCount, D3DFORMAT xIndexType, DWORD usage, D3DPOOL pool, 
    const void* const pIndices, const std::string& sName)
{
    CResourceProxy* pxResourceProxy = __super::LookUpResource(sName);

    if (pxResourceProxy == NULL)
    {
        IDirect3DIndexBuffer9* pd3dIndexBuffer = NULL;

        int iBufferSize = 
            iIndexCount * (xIndexType == D3DFMT_INDEX16 ? 2 : 4);

        HRESULT hr =
            m_pxEngineController->GetDevice()->
                CreateIndexBuffer(iBufferSize, usage, xIndexType, pool, &pd3dIndexBuffer, NULL);

        if (SUCCEEDED(hr))
        {
            if (pIndices)
            {
                // übergebenes Index-Array in den Buffer kopieren
                void* pIndexMem = NULL;

                hr = pd3dIndexBuffer->Lock(
                        0, iBufferSize, &pIndexMem, 0);

                if (SUCCEEDED(hr))
                {
                    memcpy(pIndexMem, pIndices, iBufferSize);
                    hr = pd3dIndexBuffer->Unlock();
                    assert(SUCCEEDED(hr));
                }
                else
                {
                    assert(false);
                    if (pd3dIndexBuffer) 
                    {
                        pd3dIndexBuffer->Release(); 
                        pd3dIndexBuffer = 0;
                    }
                }
            }
        }
        else
        {
            assert(false);
        }

        pxResourceProxy = __super::AddResource(sName, pd3dIndexBuffer);
    }

    return TIndexBufferHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

#include "stdafx.h"

#include "e42/core/VertexDeclarationFactory.h"

#include "e42/core/EngineController.h"
#include "e42/core/D3DVertexDeclaration.h"

#include <d3d9.h>
#include <d3dx9mesh.h>

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CVertexDeclarationFactory::CVertexDeclarationFactory(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CVertexDeclarationFactory::~CVertexDeclarationFactory()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CVertexDeclarationFactory::DestroyResource(void* pxResource)
{
    ((IDirect3DVertexDeclaration9*)pxResource)->Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
string
CVertexDeclarationFactory::GetResourceID(DWORD dwFVF) const
{
    char acName[16];
    sprintf(acName, ".FVF_%d", (int)dwFVF);

	return acName;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TVertexDeclarationHandle 
CVertexDeclarationFactory::CreateVertexDeclaration(DWORD dwFVF)
{
	const string sResourceID = GetResourceID(dwFVF);

	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

	if (pxResourceProxy == NULL)
	{
        IDirect3DVertexDeclaration9* pd3dVertexDeclaration = NULL;

		CD3DVertexDeclaration xDeclaration(dwFVF);

		HRESULT hr = m_pxEngineController->GetDevice()->CreateVertexDeclaration(xDeclaration.m_axVertexElements, &pd3dVertexDeclaration);
        assert(SUCCEEDED(hr));

        pxResourceProxy = __super::AddResource(sResourceID, pd3dVertexDeclaration);
	}
	
	return TVertexDeclarationHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TVertexDeclarationHandle 
CVertexDeclarationFactory::CreateVertexDeclaration(const CD3DVertexDeclaration& rxVertexDeclaration, const std::string& sName)
{
    CResourceProxy* pxResourceProxy = __super::LookUpResource(sName);

    if (pxResourceProxy == NULL)
    {
        IDirect3DVertexDeclaration9* pd3dVertexDeclaration = NULL;

		HRESULT hr = m_pxEngineController->GetDevice()->CreateVertexDeclaration(rxVertexDeclaration.m_axVertexElements, &pd3dVertexDeclaration);
        assert(SUCCEEDED(hr));

        pxResourceProxy = __super::AddResource(sName, pd3dVertexDeclaration);
    }

    return TVertexDeclarationHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

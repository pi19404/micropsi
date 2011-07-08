#include "stdafx.h"

#include "baselib/Filelocator.h"
#include "e42/core/D3DXMaterial.h"

#include "baselib/utils.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMaterial::CD3DXMaterial()
{
    assert(sizeof(CD3DXMaterial) == sizeof(D3DXMATERIAL));
    __super::MatD3D.Ambient = D3DXCOLOR(0, 0, 0, 0);
    __super::MatD3D.Diffuse = D3DXCOLOR(0, 0, 0, 0);
    __super::MatD3D.Emissive = D3DXCOLOR(0, 0, 0, 0);
    __super::MatD3D.Power = 0;
    __super::MatD3D.Specular = D3DXCOLOR(0, 0, 0, 0);
    __super::pTextureFilename = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMaterial::CD3DXMaterial(const D3DXMATERIAL& rxD3DXMaterial)
{
	this->CD3DXMaterial::CD3DXMaterial();
    *this = rxD3DXMaterial;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMaterial::CD3DXMaterial(const CD3DXMaterial& rxD3DXMaterial)
{
	this->CD3DXMaterial::CD3DXMaterial();
    *this = (D3DXMATERIAL)rxD3DXMaterial;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMaterial::~CD3DXMaterial()
{
    Clear();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMaterial& 
CD3DXMaterial::operator=(const D3DXMATERIAL& rxD3DXMaterial)
{
    __super::MatD3D = rxD3DXMaterial.MatD3D;
    SetTextureFilename(rxD3DXMaterial.pTextureFilename);
    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMaterial& 
CD3DXMaterial::operator=(const CD3DXMaterial& rxD3DXMaterial)
{
    return *this = (D3DXMATERIAL)rxD3DXMaterial;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
D3DMATERIAL9& 
CD3DXMaterial::MatD3D()
{
    return __super::MatD3D;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const D3DMATERIAL9 
CD3DXMaterial::MatD3D() const
{
    return __super::MatD3D;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXMaterial::AddFilenamePrefixes(const char* pcTexturePrefix)
{
	if (GetTextureFilename() != NULL)
	{
		const int iBufferSize = 512;
		char acBuffer[iBufferSize];

		int iCount = _snprintf(acBuffer, iBufferSize - 1, "%s%s", pcTexturePrefix, CFileLocator::ExtractFilename(GetTextureFilename()).c_str());
		assert(iCount != -1);

		SetTextureFilename(acBuffer);
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXMaterial::SetTextureFilename(const char* pcTextureFilename)
{
    Utils::DeletePCharString(__super::pTextureFilename);
    __super::pTextureFilename = 
        Utils::ClonePCharString(pcTextureFilename);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const char* 
CD3DXMaterial::GetTextureFilename() const
{
    return __super::pTextureFilename;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXMaterial::Clear()
{
    SetTextureFilename(NULL);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXMaterial::operator const D3DXMATERIAL() const
{
    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

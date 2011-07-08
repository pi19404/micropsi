#pragma once

#ifndef E42_D3DXMATERIAL_H_INCLUDED
#define E42_D3DXMATERIAL_H_INCLUDED

#include "e42/stdinc.h"

#include <d3dx9mesh.h>

//-----------------------------------------------------------------------------------------------------------------------------------------
class CD3DXMaterial : private D3DXMATERIAL
{
public:
    CD3DXMaterial();
    CD3DXMaterial(const D3DXMATERIAL& rxD3DXMaterial);
    CD3DXMaterial(const CD3DXMaterial& rxD3DXMaterial);
    ~CD3DXMaterial();
    CD3DXMaterial& operator=(const D3DXMATERIAL& rxD3DXMaterial);
    CD3DXMaterial& operator=(const CD3DXMaterial& rxD3DXMaterial);

    void Clear();


    D3DMATERIAL9& MatD3D();
    const D3DMATERIAL9 MatD3D() const;

    void SetTextureFilename(const char* pcTextureFilename);
    const char* GetTextureFilename() const;

    void AddFilenamePrefixes(const char* pcTexturePrefix);

	
	operator const D3DXMATERIAL() const;
};
//-----------------------------------------------------------------------------------------------------------------------------------------

#endif // E42_D3DXMATERIAL_H_INCLUDED

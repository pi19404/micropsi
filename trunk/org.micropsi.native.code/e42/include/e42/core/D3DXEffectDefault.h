#pragma once

#ifndef E42_D3DXEFFECTDEFAULT_H_INCLUDED
#define E42_D3DXEFFECTDEFAULT_H_INCLUDED

#include "e42/stdinc.h"

#include <d3dx9mesh.h>

//-----------------------------------------------------------------------------------------------------------------------------------------
class CD3DXEffectDefault : private D3DXEFFECTDEFAULT
{
public:
    CD3DXEffectDefault();
    CD3DXEffectDefault(const D3DXEFFECTDEFAULT& rxD3DEffectDefault);
    CD3DXEffectDefault(const CD3DXEffectDefault& rxD3DEffectDefault);
    ~CD3DXEffectDefault();
    CD3DXEffectDefault& operator=(const D3DXEFFECTDEFAULT& rxD3DEffectDefault);
    CD3DXEffectDefault& operator=(const CD3DXEffectDefault& rxD3DEffectDefault);

    void Clear();


    void SetParamName(const char* pcParamName);
    const char* GetParamName() const;

    D3DXEFFECTDEFAULTTYPE& Type();
    D3DXEFFECTDEFAULTTYPE Type() const;

    void SetValue(DWORD dwNumBytes, const void* pNewValue);
    DWORD GetNumBytes() const;
    void* GetValue() const;
};
//-----------------------------------------------------------------------------------------------------------------------------------------

#endif // E42_D3DXEFFECTDEFAULT_H_INCLUDED

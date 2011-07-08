#include "stdafx.h"

#include "e42/core/D3DXEffectDefault.h"

#include "baselib/utils.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectDefault::CD3DXEffectDefault()
{
    assert(sizeof(CD3DXEffectDefault) == sizeof(D3DXEFFECTDEFAULT));
    __super::NumBytes = 0;
    __super::pParamName = NULL;
    __super::pValue = NULL;
    __super::Type = D3DXEDT_FORCEDWORD;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectDefault::CD3DXEffectDefault(const D3DXEFFECTDEFAULT& rxD3DXEffectDefault)
{
    *this = rxD3DXEffectDefault;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectDefault::CD3DXEffectDefault(const CD3DXEffectDefault& rxD3DXEffectDefault)
{
    *this = (D3DXEFFECTDEFAULT)rxD3DXEffectDefault;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectDefault::~CD3DXEffectDefault()
{
    Clear();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectDefault& 
CD3DXEffectDefault::operator=(const D3DXEFFECTDEFAULT& rxD3DXEffectDefault)
{
    __super::Type = rxD3DXEffectDefault.Type;
    SetParamName(rxD3DXEffectDefault.pParamName);
    SetValue(rxD3DXEffectDefault.NumBytes, rxD3DXEffectDefault.pValue);

    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectDefault& 
CD3DXEffectDefault::operator=(const CD3DXEffectDefault& rxD3DXEffectDefault)
{
    return *this = (D3DXEFFECTDEFAULT)rxD3DXEffectDefault;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXEffectDefault::Clear()
{
    SetParamName(NULL);
    SetValue(NULL, 0);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXEffectDefault::SetParamName(const char* pcParamName)
{
    Utils::DeletePCharString(__super::pParamName);
    __super::pParamName = 
        Utils::ClonePCharString(pcParamName);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const char*
CD3DXEffectDefault::GetParamName() const
{
    return __super::pParamName;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
D3DXEFFECTDEFAULTTYPE& 
CD3DXEffectDefault::Type()
{
    return __super::Type;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
D3DXEFFECTDEFAULTTYPE 
CD3DXEffectDefault::Type() const
{
    return __super::Type;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXEffectDefault::SetValue(DWORD dwNumBytes, const void* pNewValue)
{
    if (__super::NumBytes != 0 && dwNumBytes != 0)
    {
        if (__super::NumBytes != dwNumBytes)
        {
            __super::pValue = realloc(__super::pValue, dwNumBytes);
            __super::NumBytes = dwNumBytes;
        }

		if (pNewValue)
		{
			memcpy(__super::pValue, pNewValue, dwNumBytes);
		}
    }
    else if (__super::NumBytes == 0 && dwNumBytes != 0)
    {
        __super::pValue = malloc(dwNumBytes);
        __super::NumBytes = dwNumBytes;

		if (pNewValue)
		{
			memcpy(__super::pValue, pNewValue, dwNumBytes);
		}
    }
    else if (__super::NumBytes != 0 && dwNumBytes == 0)
    {
        free(__super::pValue);
        __super::NumBytes = 0;

        __super::pValue = NULL;
    }
 

    assert(( __super::pValue &&  __super::NumBytes) || 
           (!__super::pValue && !__super::NumBytes));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
DWORD 
CD3DXEffectDefault::GetNumBytes() const
{
    return __super::NumBytes;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void* 
CD3DXEffectDefault::GetValue() const
{
    return __super::pValue;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

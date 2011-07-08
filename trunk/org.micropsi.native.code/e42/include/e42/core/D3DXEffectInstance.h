#pragma once

#ifndef E42_D3DXEFFECTINSTANCE_H_INCLUDED
#define E42_D3DXEFFECTINSTANCE_H_INCLUDED

#include "e42/stdinc.h"

#include <d3dx9mesh.h>

class CD3DXEffectDefault;

//-----------------------------------------------------------------------------------------------------------------------------------------
class CD3DXEffectInstance : private D3DXEFFECTINSTANCE
{
private:

	void SetNumDefaults(int iNumDefaults);			///< setzt die Größe des EffectInstance-Arrays


public:

	CD3DXEffectInstance();
    CD3DXEffectInstance(const char* pcEffectFilename);
    CD3DXEffectInstance(const D3DXEFFECTINSTANCE& rxD3DEffectInstance);
    CD3DXEffectInstance(const CD3DXEffectInstance& rxD3DEffectInstance);
    ~CD3DXEffectInstance();
    CD3DXEffectInstance& operator=(const D3DXEFFECTINSTANCE& rxD3DEffectInstance);
    CD3DXEffectInstance& operator=(const CD3DXEffectInstance& rxD3DEffectInstance);


    void Clear();

    void SetEffectFilename(const char* pcEffectFilename);
    const char* GetEffectFilename() const;

    void SetDefaults(DWORD dwNumDefaults, D3DXEFFECTDEFAULT* pxDefaults = NULL);
    CD3DXEffectDefault* GetDefaults() const;
    DWORD GetNumDefaults() const;

	CD3DXEffectDefault* FindEffectDefault(const char* pcParamName, bool bAddIfNew = false);
	CD3DXEffectDefault* AddEffectDefault(const char* pcParamName);

    void AddFilenamePrefixes(const char* pcTexturePrefix, const char* pcShaderPrefix);


	D3DXEFFECTINSTANCE& D3DXEffectInstance();
    const D3DXEFFECTINSTANCE& D3DXEffectInstance() const;
};
//-----------------------------------------------------------------------------------------------------------------------------------------

#endif // E42_D3DXEFFECTINSTANCE_H_INCLUDED

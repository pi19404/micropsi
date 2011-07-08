#include "stdafx.h"

#include "e42/core/D3DXEffectInstance.h"
#include "e42/core/D3DXEffectDefault.h"

#include "baselib/FileLocator.h"
#include "baselib/utils.h"

#include <stdio.h>

//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectInstance::CD3DXEffectInstance()
{
    assert(sizeof(CD3DXEffectInstance) == sizeof(D3DXEFFECTINSTANCE));
    __super::pEffectFilename = NULL;
    __super::NumDefaults = 0;
    __super::pDefaults = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectInstance::CD3DXEffectInstance(const char* pcEffectFilename)
{
	this->CD3DXEffectInstance::CD3DXEffectInstance();

	SetEffectFilename(pcEffectFilename);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectInstance::CD3DXEffectInstance(const D3DXEFFECTINSTANCE& rxD3DXEffectInstance)
{
	this->CD3DXEffectInstance::CD3DXEffectInstance();
    
	*this = rxD3DXEffectInstance;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectInstance::CD3DXEffectInstance(const CD3DXEffectInstance& rxD3DXEffectInstance)
{
	this->CD3DXEffectInstance::CD3DXEffectInstance();

	*this = (D3DXEFFECTINSTANCE)rxD3DXEffectInstance;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectInstance::~CD3DXEffectInstance()
{
    Clear();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectInstance& 
CD3DXEffectInstance::operator=(const D3DXEFFECTINSTANCE& rxD3DXEffectInstance)
{
    SetEffectFilename(rxD3DXEffectInstance.pEffectFilename);
    SetDefaults(rxD3DXEffectInstance.NumDefaults, rxD3DXEffectInstance.pDefaults);

    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectInstance& 
CD3DXEffectInstance::operator=(const CD3DXEffectInstance& rxD3DXEffectInstance)
{
    return *this = (D3DXEFFECTINSTANCE)rxD3DXEffectInstance;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXEffectInstance::Clear()
{
	SetNumDefaults(0);
    SetEffectFilename(NULL);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXEffectInstance::AddFilenamePrefixes(const char* pcShaderPrefix, const char* pcTexturePrefix)
{
    // shadernamen mit virtuellem Directory versehen
    const int iBufferSize = 512;
    char acBuffer[iBufferSize];

	if (GetEffectFilename() != NULL &&
		pcShaderPrefix != NULL)
	{
		int iCount = _snprintf(acBuffer, iBufferSize - 1, "%s%s", pcShaderPrefix, CFileLocator::ExtractFilename(GetEffectFilename()).c_str());
		assert(iCount != -1);

		SetEffectFilename(acBuffer);
	}


	if (pcTexturePrefix != NULL)
	{
		for (int iDefault = 0; iDefault < (int)GetNumDefaults(); iDefault++)
		{
			CD3DXEffectDefault& rxDefaultDest = GetDefaults()[iDefault];
			if (rxDefaultDest.Type() == D3DXEDT_STRING)
			{
				// texturnamen mit virtuellem Directory versehen
				int iCount = _snprintf(acBuffer, iBufferSize - 1, "%s%s", pcTexturePrefix, CFileLocator::ExtractFilename((char*)rxDefaultDest.GetValue()).c_str());
				assert(iCount != -1);

				rxDefaultDest.SetValue((DWORD)strlen(acBuffer) + 1, acBuffer);
			}
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXEffectInstance::SetEffectFilename(const char* pcEffectFilename)
{
    Utils::DeletePCharString(__super::pEffectFilename);
    __super::pEffectFilename = 
        Utils::ClonePCharString(pcEffectFilename);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const char*
CD3DXEffectInstance::GetEffectFilename() const
{
    return __super::pEffectFilename;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CD3DXEffectInstance::SetDefaults(DWORD dwNumDefaults, D3DXEFFECTDEFAULT* pxDefaults)
{
    // Reallokation
	SetNumDefaults(dwNumDefaults);

    // Zuweisung
    if (pxDefaults)
    {
        for (DWORD dwDefault = 0; dwDefault < dwNumDefaults; dwDefault++)
        {
            GetDefaults()[dwDefault] = pxDefaults[dwDefault];
        }
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CD3DXEffectInstance::SetNumDefaults(int iNumDefaults)
{
	if (this->NumDefaults == iNumDefaults)
	{
		return;
	}


	if ((int)this->NumDefaults > iNumDefaults)
	{
		// destruct
		for (int i = this->NumDefaults - 1; i >= iNumDefaults; --i)
		{
			GetDefaults()[i].CD3DXEffectDefault::~CD3DXEffectDefault();
		}
	}


	const int GRANULARITY_BITS = 31;
	assert(Utils::IsPowerOf2(GRANULARITY_BITS + 1));
	int iCurrentArraySize = (this->NumDefaults + GRANULARITY_BITS) & ~GRANULARITY_BITS;
	int iNewArraySize = (iNumDefaults + GRANULARITY_BITS) & ~GRANULARITY_BITS;


	if (iCurrentArraySize != iNewArraySize)
	{
		if (iCurrentArraySize == 0 && iNewArraySize > 0)
		{
			this->pDefaults = (D3DXEFFECTDEFAULT*)malloc(iNewArraySize * sizeof(CD3DXEffectDefault));
		}
		else
		if (iCurrentArraySize > 0 && iNewArraySize == 0)
		{
			free(this->pDefaults);
			this->pDefaults = NULL;
		}
		else
		{
			this->pDefaults = (D3DXEFFECTDEFAULT*)realloc(this->pDefaults, iNewArraySize * sizeof(CD3DXEffectDefault));
		}
	}


	if ((int)this->NumDefaults < iNumDefaults)
	{
		// construct
		for (int i = this->NumDefaults; i < iNumDefaults; ++i)
		{
			GetDefaults()[i].CD3DXEffectDefault::CD3DXEffectDefault();
		}
	}


	this->NumDefaults = iNumDefaults;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectDefault* 
CD3DXEffectInstance::FindEffectDefault(const char* pcParamName, bool bAddIfNew)
{
	for (int iDefault = 0; iDefault < (int)this->NumDefaults; iDefault++)
	{
		if (strcmp(GetDefaults()[iDefault].GetParamName(), pcParamName) == 0)
		{
			return GetDefaults() + iDefault;
		}
	}

	if (bAddIfNew)
	{
		return AddEffectDefault(pcParamName);
	}

	return NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectDefault* 
CD3DXEffectInstance::AddEffectDefault(const char* pcParamName)
{
	assert(FindEffectDefault(pcParamName) == NULL);

	SetNumDefaults(this->NumDefaults + 1);

	CD3DXEffectDefault* pxDefault = GetDefaults() + this->NumDefaults - 1;
	pxDefault->SetParamName(pcParamName);

	return pxDefault;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CD3DXEffectDefault*
CD3DXEffectInstance::GetDefaults() const
{
    return (CD3DXEffectDefault*)__super::pDefaults;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
DWORD 
CD3DXEffectInstance::GetNumDefaults() const
{
    return __super::NumDefaults;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
D3DXEFFECTINSTANCE& 
CD3DXEffectInstance::D3DXEffectInstance()
{
    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const D3DXEFFECTINSTANCE& 
CD3DXEffectInstance::D3DXEffectInstance() const
{
    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

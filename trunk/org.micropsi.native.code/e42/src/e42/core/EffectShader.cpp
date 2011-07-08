#include "stdafx.h"

#include "e42/core/EffectShader.h"
#include "e42/core/ShaderConstants.h"
#include "e42/core/D3DXEffectDefault.h"

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CEffectShader::CEffectShader(ID3DXEffect* pd3dEffect)
:   m_pd3dEffect(pd3dEffect),

    m_hndNumBones                       (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_NUMBONES)),

    m_hndWorld                          (pd3dEffect->GetParameterBySemantic    (0, SHADERCONSTANT_SEMANTIC_WORLD)),
    m_hndWorldInverse                   (pd3dEffect->GetParameterBySemantic    (0, SHADERCONSTANT_SEMANTIC_WORLDINVERSE)),
    m_hndView                           (pd3dEffect->GetParameterBySemantic    (0, SHADERCONSTANT_SEMANTIC_VIEW)),
    m_hndProjection                     (pd3dEffect->GetParameterBySemantic    (0, SHADERCONSTANT_SEMANTIC_PROJECTION)),
    m_hndWorldView                      (pd3dEffect->GetParameterBySemantic    (0, SHADERCONSTANT_SEMANTIC_WORLDVIEW)),
    m_hndViewProjection                 (pd3dEffect->GetParameterBySemantic    (0, SHADERCONSTANT_SEMANTIC_VIEWPROJECTION)),
    m_hndWorldViewProjection            (pd3dEffect->GetParameterBySemantic    (0, SHADERCONSTANT_SEMANTIC_WORLDVIEWPROJECTION)),
    m_hndViewInverse                    (pd3dEffect->GetParameterBySemantic    (0, SHADERCONSTANT_SEMANTIC_VIEWINVERSE)),

    m_hndWorldMatrixArray               (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_WORLDMATRIXARRAY)),

    m_hndMaterialDiffuse                (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_MATERIALDIFFUSE)),
    m_hndMaterialAmbient                (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_MATERIALAMBIENT)),
    m_hndLightDir                       (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_LIGHTDIRECTION)),

    m_hndEyePosition                    (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_EYEPOSITION)),

    m_hndDiffuseMap                     (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_DIFFUSEMAP)),
    m_hndSpecularMap                    (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_SPECULARMAP)),
    m_hndBumpMap                        (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_BUMPMAP)),
    m_hndNormalMap                      (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_NORMALMAP)),
    m_hndDetailMap                      (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_DETAILMAP)),
    m_hndEnvironmentMap                 (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_ENVIRONMENTMAP)),
    m_hndLightMap                       (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_LIGHTMAP)),
    m_hndShadowMap                      (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_SHADOWMAP)),

    m_hndTime                           (pd3dEffect->GetParameterByName        (0, SHADERCONSTANT_NAME_TIME)),

    m_hndDefaultTechnique_StaticMesh    (pd3dEffect->GetTechniqueByName("_staticmesh")),
    m_hndDefaultTechnique_SkinnedMesh   (pd3dEffect->GetTechniqueByName("_skinnedmesh")),

	m_bExecuteRestorePassOnEnd			(false),
	m_uiRestorePassIdx					(-1)
{
	SaveInitialParameterValues();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CEffectShader::~CEffectShader()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CEffectShader* 
CEffectShader::Create(ID3DXEffect* pd3dEffect)
{
    return new CEffectShader(pd3dEffect);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CEffectShader::Destroy(CEffectShader* pEffectShader)
{
    delete pEffectShader;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CEffectShader::ApplyEffectInstanceDefaults(D3DXEFFECTINSTANCE* pInstance)
{
    if (pInstance)
    {
        for (int iDefault = 0; iDefault < (int)pInstance->NumDefaults; iDefault++)
        {
            const D3DXEFFECTDEFAULT& rxDefault = pInstance->pDefaults[iDefault];

            D3DXHANDLE hndParam = m_pd3dEffect->GetParameterByName(0, rxDefault.pParamName);

            if (hndParam)
            {
                switch (rxDefault.Type)
                {
                case D3DXEDT_STRING :
                    m_pd3dEffect->SetString(hndParam, (const char*)rxDefault.pValue);
                    break;

                case D3DXEDT_FLOATS :
                    m_pd3dEffect->SetFloatArray(hndParam, (float*)rxDefault.pValue, rxDefault.NumBytes / 4);
                    break;

                case D3DXEDT_DWORD :
                    m_pd3dEffect->SetIntArray(hndParam, (int*)rxDefault.pValue, rxDefault.NumBytes / 4);
                    break;

                default:
                    assert(false);
                }
            }
            else
            {
                DebugPrint("warning: EffectInstance-DefaultParameter %s does not exist on Effect!", rxDefault.pParamName);
            }
        }
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CEffectShader::Begin(UINT* puiNumPasses, const DWORD dwFlags)
{
	UINT uiNumPasses;
    HRESULT hr = m_pd3dEffect->Begin(&uiNumPasses, dwFlags);
    assert(SUCCEEDED(hr));

	assert(!m_bExecuteRestorePassOnEnd);

	if (uiNumPasses > 0 &&
		IsRestorePass(uiNumPasses - 1))
	{
		uiNumPasses--;
		m_bExecuteRestorePassOnEnd = true;
		m_uiRestorePassIdx = uiNumPasses;
	}

	*puiNumPasses = uiNumPasses;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CEffectShader::End()
{
	HRESULT hr;

	if (m_bExecuteRestorePassOnEnd)
	{
		hr = m_pd3dEffect->BeginPass(m_uiRestorePassIdx);
		assert(SUCCEEDED(hr));
		hr = m_pd3dEffect->EndPass();
		assert(SUCCEEDED(hr));

		m_bExecuteRestorePassOnEnd = false;
	}

    hr = m_pd3dEffect->End();
    assert(SUCCEEDED(hr));
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool 
CEffectShader::IsRestorePass(int iPass, const char* pcTechnique) const
{
	if (iPass < 0) 
	{
		return false;
	}

    D3DXHANDLE hndPass;
    if (pcTechnique)
    {
        hndPass = m_pd3dEffect->GetPass(pcTechnique, iPass);
    }
    else
    {
        D3DXHANDLE hndCurrentTechnique = m_pd3dEffect->GetCurrentTechnique();
        hndPass = m_pd3dEffect->GetPass(hndCurrentTechnique, iPass);
    }

    if (!hndPass)
    {
        assert(false);
        return false;
    }

    D3DXPASS_DESC xPassDesc;
    xPassDesc.Name = 0;
    m_pd3dEffect->GetPassDesc(hndPass, &xPassDesc);

    return (xPassDesc.Name && 
            strcmp(xPassDesc.Name, "restore") == 0);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CEffectShader::AddParameters(D3DXHANDLE hParentParam, string sParentParam)
{
	// Parameter werden solange runtergebrochen, bis es sich nur noch um 
	// Elementartypen oder um Arrays von Elementartypen handelt. Texturen,
	// Sampler und Shader werden nicht als Parameter gespeichert.


	D3DXPARAMETER_DESC xParentParamDesc;
	m_pd3dEffect->GetParameterDesc(hParentParam, &xParentParamDesc);

	if (xParentParamDesc.Flags & D3DX_PARAMETER_SHARED)
	{
		return;
	}

	if (xParentParamDesc.Class == D3DXPC_STRUCT)
	{
		if (xParentParamDesc.Elements > 0)
		{
			for (int iElement = 0; iElement < (int)xParentParamDesc.Elements; iElement++)
			{
				D3DXHANDLE hElement = m_pd3dEffect->GetParameterElement(hParentParam, iElement);

				D3DXPARAMETER_DESC xElementDesc;
				m_pd3dEffect->GetParameterDesc(hElement, &xElementDesc);

				char acIndex[12];
				sprintf(acIndex, "%i", iElement);
				AddParameters(hElement, sParentParam + "[" + acIndex + "]");
			}
		}
		else
		{
			for (int iMember = 0; iMember < (int)xParentParamDesc.StructMembers; iMember++)
			{
				D3DXHANDLE hMember = m_pd3dEffect->GetParameter(hParentParam, iMember);

				D3DXPARAMETER_DESC xMemberDesc;
				m_pd3dEffect->GetParameterDesc(hMember, &xMemberDesc);

				AddParameters(hMember, sParentParam + "." + xMemberDesc.Name);
			}
		}
	}
	else
	if (xParentParamDesc.Type == D3DXPT_VOID ||
		xParentParamDesc.Type == D3DXPT_BOOL ||
		xParentParamDesc.Type == D3DXPT_INT ||
		xParentParamDesc.Type == D3DXPT_FLOAT ||
		xParentParamDesc.Type == D3DXPT_STRING)
	{
		// Default zur EffectInstance hinzufügen
		CD3DXEffectDefault* pxDefault =
			m_xInitialParameterValues.AddEffectDefault(sParentParam.c_str());

		pxDefault->SetValue(xParentParamDesc.Bytes, NULL);
		m_pd3dEffect->GetValue(hParentParam, pxDefault->GetValue(), xParentParamDesc.Bytes);

		switch (xParentParamDesc.Type)
		{
		case D3DXPT_FLOAT :		pxDefault->Type() = D3DXEDT_FLOATS;		break;
		case D3DXPT_STRING :	pxDefault->Type() = D3DXEDT_STRING;		break;
		default :				pxDefault->Type() = D3DXEDT_DWORD;		break;
		}

		assert(m_pd3dEffect->GetParameterByName(NULL, sParentParam.c_str()) != NULL);
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CEffectShader::SaveInitialParameterValues()
{
	D3DXEFFECT_DESC xEffectDesc;
	m_pd3dEffect->GetDesc(&xEffectDesc);

	for (int iParam = 0; iParam < (int)xEffectDesc.Parameters; iParam++)
	{
		D3DXHANDLE hParam = m_pd3dEffect->GetParameter(NULL, iParam);

		D3DXPARAMETER_DESC xParamDesc;
		m_pd3dEffect->GetParameterDesc(hParam, &xParamDesc);
		
		AddParameters(hParam, xParamDesc.Name);
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------

#include "stdafx.h"

#include "e42/core/Material.h"
#include "e42/core/EffectShader.h"

#include "e42/core/EngineController.h"
#include "e42/core/EffectFactory.h"
#include "e42/core/TextureFactory.h"
#include "e42/core/ShaderConstants.h"

#include "baselib/FileLocator.h"

#include "e42/core/D3DXMaterial.h"
#include "e42/core/D3DXEffectDefault.h"
#include "e42/core/D3DXEffectInstance.h"

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CMaterial::CEffectParameter::CEffectParameter()
:	m_hParameter(NULL),
	m_eParameterType(D3DXPT_FORCE_DWORD),
	m_iParameterSize(0)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMaterial::CEffectParameter::CEffectParameter(const CEffectParameter& rxEffectParameter)
{
	this->CEffectParameter::CEffectParameter();
	*this = rxEffectParameter;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMaterial::CEffectParameter::~CEffectParameter()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CMaterial::CEffectParameter::ParameterTypeIsTexture() const
{
	return
		(m_eParameterType == D3DXPT_TEXTURE ||
		 m_eParameterType == D3DXPT_TEXTURE1D ||
		 m_eParameterType == D3DXPT_TEXTURE2D ||
		 m_eParameterType == D3DXPT_TEXTURE3D ||
		 m_eParameterType == D3DXPT_TEXTURECUBE);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMaterial::CEffectParameter& 
CMaterial::CEffectParameter::operator=(const CEffectParameter& rxEffectParameter)
{
	assert(rxEffectParameter.m_xParameterValue.Get_Void() == NULL ||
		   rxEffectParameter.m_xParameterValue.Get_Texture().GetPtr() == NULL);


	m_hParameter = rxEffectParameter.m_hParameter;
	m_eParameterType = rxEffectParameter.m_eParameterType;
	m_sParameterName = rxEffectParameter.m_sParameterName;


	if (ParameterTypeIsTexture())
	{
		m_xParameterValue.Set_Texture(rxEffectParameter.m_xParameterValue.Get_Texture());
	}
	else
	{
		m_xParameterValue.Set_Void(
			rxEffectParameter.m_xParameterValue.Get_Void(), 
			rxEffectParameter.m_xParameterValue.GetSize_Void());
	}


	return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMaterial::CEffectParameter::CEffectParameterValue::CEffectParameterValue()
:	m_pValue	(NULL),
	m_iSize		(0)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMaterial::CEffectParameter::CEffectParameterValue::~CEffectParameterValue()
{
	Set_Void(NULL, 0);
	m_hTexture.Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const void*
CMaterial::CEffectParameter::CEffectParameterValue::Get_Void() const
{
	if (m_iSize == 0)
	{
		return NULL;
	}
	else
	if (m_iSize <= MAX_INPLACE_VALUESIZE)
	{
		return &m_iValue;
	}
	else
	{
		return m_pValue;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void*
CMaterial::CEffectParameter::CEffectParameterValue::Get_Void()
{
	if (m_iSize == 0)
	{
		return NULL;
	}
	else
	if (m_iSize <= MAX_INPLACE_VALUESIZE)
	{
		return &m_iValue;
	}
	else
	{
		return m_pValue;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
size_t
CMaterial::CEffectParameter::CEffectParameterValue::GetSize_Void() const
{
	return m_iSize;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TTextureHandle 
CMaterial::CEffectParameter::CEffectParameterValue::Get_Texture() const
{
	return m_hTexture;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CMaterial::CEffectParameter::CEffectParameterValue::SetSize(size_t iNewValueSize)
{
	if (m_iSize == iNewValueSize)
	{
		return;
	}
	
	if (iNewValueSize <= MAX_INPLACE_VALUESIZE &&
		m_iSize > MAX_INPLACE_VALUESIZE)
	{
		free(m_pValue);
		m_pValue = NULL;
	}
	else
	if (iNewValueSize <= MAX_INPLACE_VALUESIZE &&
		m_iSize <= MAX_INPLACE_VALUESIZE)
	{
		m_iValue = 0;
	}
	else
	if (iNewValueSize > MAX_INPLACE_VALUESIZE &&
		m_iSize <= MAX_INPLACE_VALUESIZE)
	{
		m_pValue = malloc(iNewValueSize);
	}
	else
	if (iNewValueSize > MAX_INPLACE_VALUESIZE &&
		m_iSize > MAX_INPLACE_VALUESIZE)
	{
		m_pValue = realloc(m_pValue, iNewValueSize);
	}
	else
	{
		assert(false);
	}

	m_iSize = iNewValueSize;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterial::CEffectParameter::CEffectParameterValue::Set_Void(const void* pNewValue, size_t iValueSize)
{
	m_hTexture.Release();

	SetSize(iValueSize);

	void* pValue = Get_Void();

	if (pValue)
	{
		switch (m_iSize)
		{
		case 64:	*(D3DXMATRIX*)pValue = *(D3DXMATRIX*)pNewValue;		break;

		case 16:	((int*)pValue)[3] = ((int*)pNewValue)[3];
		case 12:	((int*)pValue)[2] = ((int*)pNewValue)[2];
		case 8:		((int*)pValue)[1] = ((int*)pNewValue)[1];
		case 4:		((int*)pValue)[0] = ((int*)pNewValue)[0];			break;

		default:	memcpy(pValue, pNewValue, m_iSize);
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterial::CEffectParameter::CEffectParameterValue::Set_Texture(const TTextureHandle& hTexture)
{
	SetSize(0);

	m_hTexture = hTexture;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMaterial::CMaterial()
:	m_pxEngineController(NULL),
	m_hParameterBlock(NULL)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMaterial::~CMaterial()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMaterial::CMaterial(const CMaterial& rxMaterial)
{
	this->CMaterial::CMaterial();

	*this = rxMaterial;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMaterial& 
CMaterial::operator=(const CMaterial& rxMaterial)
{
	m_axEffectParameters.SetSize(rxMaterial.m_axEffectParameters.Size());

	for (int i = 0; i < (int)m_axEffectParameters.Size(); i++)
	{
		m_axEffectParameters[i] = rxMaterial.m_axEffectParameters[i]; 
	}

	m_hEffect = rxMaterial.m_hEffect;

	InvalidateParameterBlock();

#ifdef _DEBUG
	_m_sEffectFilename = rxMaterial._m_sEffectFilename;
#endif // _DEBUG

	return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CMaterial::ApplyEffectParameters()
{
	ID3DXEffect* pd3dEffect = m_hEffect.GetPtr()->GetD3DXEffect();


	for (int iParameter = 0; iParameter < (int)m_axEffectParameters.Size(); iParameter++)
	{
		CEffectParameter& rxParam = m_axEffectParameters[iParameter];

		if (rxParam.m_hParameter &&
			rxParam.m_eParameterType != D3DXPT_STRING)
		{
			const CEffectParameter::CEffectParameterValue& rxValue = rxParam.m_xParameterValue;

			if (rxParam.ParameterTypeIsTexture())
			{
				pd3dEffect->SetTexture(rxParam.m_hParameter, rxValue.Get_Texture().GetPtr());
			}
			else
			{
				int iNumBytes = (UINT)rxValue.GetSize_Void();
				assert(iNumBytes >= rxParam.m_iParameterSize && "parameter size in effect does not value size in material");

				if (iNumBytes > rxParam.m_iParameterSize)
					iNumBytes = rxParam.m_iParameterSize;

				pd3dEffect->SetValue(rxParam.m_hParameter, rxValue.Get_Void(), (UINT)rxValue.GetSize_Void());
			}
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterial::UpdateEffect()
{
	assert(m_hEffect.GetPtr());

	if (m_axEffectParameters.Size() > 0)
	{
		ID3DXEffect* pd3dEffect = m_hEffect.GetPtr()->GetD3DXEffect();

		// Parameterblock erstellen
		if (m_hParameterBlock == NULL)
		{
			HRESULT hr = pd3dEffect->BeginParameterBlock();
			assert(SUCCEEDED(hr));

			ApplyEffectParameters();

			m_hParameterBlock = pd3dEffect->EndParameterBlock();
			assert(m_hParameterBlock != NULL);
		}


		// Parameterblock setzen
		HRESULT hr = pd3dEffect->ApplyParameterBlock(m_hParameterBlock);
		assert(SUCCEEDED(hr));
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CMaterial::IsTextureParameter(const CD3DXEffectDefault& rxDefault) const
{
	if (strcmp(rxDefault.GetParamName(), SHADERCONSTANT_NAME_DIFFUSEMAP) == 0 ||
		strcmp(rxDefault.GetParamName(), SHADERCONSTANT_NAME_SPECULARMAP) == 0 ||
		strcmp(rxDefault.GetParamName(), SHADERCONSTANT_NAME_NORMALMAP) == 0 ||
		strcmp(rxDefault.GetParamName(), SHADERCONSTANT_NAME_DETAILMAP) == 0 ||
		strcmp(rxDefault.GetParamName(), SHADERCONSTANT_NAME_ENVIRONMENTMAP) == 0 ||
		strcmp(rxDefault.GetParamName(), SHADERCONSTANT_NAME_SHADOWMAP) == 0 ||
		strcmp(rxDefault.GetParamName(), SHADERCONSTANT_NAME_LIGHTMAP) == 0)
	{
		return true;
	}


	ID3DXEffect* pxEffect = m_hEffect.GetPtr()->GetD3DXEffect();

	D3DXHANDLE hParameter = pxEffect->GetParameterByName(NULL, rxDefault.GetParamName());

	if (hParameter)
	{
		D3DXPARAMETER_DESC xParameterDesc;
		pxEffect->GetParameterDesc(hParameter, &xParameterDesc);

		return 
			xParameterDesc.Type == D3DXPT_TEXTURE ||
			xParameterDesc.Type == D3DXPT_TEXTURE1D ||
			xParameterDesc.Type == D3DXPT_TEXTURE2D ||
			xParameterDesc.Type == D3DXPT_TEXTURE3D ||
			xParameterDesc.Type == D3DXPT_TEXTURECUBE;
	}

	return false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterial::AddParameters(const CD3DXEffectDefault* pxDefaults, int iNumDefaults)
{
	CTextureFactory* pxTextureFactory = m_pxEngineController->GetTextureFactory();


	// alle Werte aus der EffectInstances auslesen
	for (int iDefault = 0; iDefault < (int)iNumDefaults; iDefault++)
	{
		const CD3DXEffectDefault& rxDefault = pxDefaults[iDefault];

		int iParameterIdx = GetParameterIdx(rxDefault.GetParamName());

		if (iParameterIdx != -1)
		{
			CEffectParameter& rxParam = m_axEffectParameters[iParameterIdx];

			assert((int)rxDefault.GetNumBytes() >= rxParam.m_iParameterSize && "parameter size in effect does not value size in material");

			switch (rxDefault.Type())
			{
			case D3DXEDT_DWORD :

				rxParam.m_xParameterValue.Set_Void(rxDefault.GetValue(), rxDefault.GetNumBytes());
				assert(rxParam.m_eParameterType == D3DXPT_INT);
				break;

			case D3DXEDT_FLOATS :

				rxParam.m_xParameterValue.Set_Void(rxDefault.GetValue(), rxDefault.GetNumBytes());
				assert(rxParam.m_eParameterType == D3DXPT_FLOAT);
				break;

			case D3DXEDT_STRING :

				// prüfen, ob eine Textur gesetzt wird
				if (IsTextureParameter(rxDefault))
				{
					TTextureHandle hTexture = pxTextureFactory->CreateTextureFromFile((char*)rxDefault.GetValue());
					rxParam.m_xParameterValue.Set_Texture(hTexture);

					rxParam.m_eParameterType = D3DXPT_TEXTURE;
				}
				else
				{
					rxParam.m_xParameterValue.Set_Void(rxDefault.GetValue(), rxDefault.GetNumBytes());
					assert(rxParam.m_xParameterValue.Get_Void() != NULL);

					rxParam.m_eParameterType = D3DXPT_STRING;
				}
				break;

			default :

				assert(false);
			}
		}
	}

	InvalidateParameterBlock();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterial::Init(const char* pcEffectFilename, CEngineController* pxEngineController)
{
	assert(m_axEffectParameters.Size() == 0);
	assert(m_pxEngineController == NULL);


	m_pxEngineController = pxEngineController;

	if (m_pxEngineController == NULL)
	{
		m_pxEngineController = &CEngineController::Get();
	}


#ifdef _DEBUG
	_m_sEffectFilename = pcEffectFilename;
#endif // _DEBUG


	m_hEffect = m_pxEngineController->GetEffectFactory()->CreateEffect(pcEffectFilename);
	assert(m_hEffect.GetPtr());

	AddParameters(
		m_hEffect->GetInitialParameterValues().GetDefaults(),
		m_hEffect->GetInitialParameterValues().GetNumDefaults());

	InvalidateParameterBlock();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int
CMaterial::FindParameterIdx(const string& sParameterName)
{
	// suchen, ob der Parameter schon in der Liste steht
	for (int iParamIdx = 0; iParamIdx < (int)m_axEffectParameters.Size(); iParamIdx++)
	{
		if (m_axEffectParameters[iParamIdx].m_sParameterName == sParameterName)
		{
			return iParamIdx;
		}
	}

	return -1;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CMaterial::IsTopLevelParameter(const std::string& sParameterName) const
{
	if (sParameterName.find('.') != string::npos ||
		sParameterName.find('[') != string::npos)
	{
		return false;
	}

	return true;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CMaterial::ParameterContextIsObject(const string& sParameterName) const
{
/*
	float  c_fSpecularity : Direction < string context = "object"; >;
*/

	if (!IsTopLevelParameter(sParameterName))
	{
		return false;
	}

	ID3DXEffect* pd3dEffect = m_hEffect->GetD3DXEffect();

	D3DXHANDLE hParameter = pd3dEffect->GetParameterByName(NULL, sParameterName.c_str());

	if (hParameter)
	{
		D3DXHANDLE hContextAnnotation = pd3dEffect->GetAnnotationByName(hParameter, SHADERCONSTANT_ANNOTATIONKEY_CONTEXT);

		if (hContextAnnotation != NULL)
		{
			D3DXPARAMETER_DESC xAnnotationDesc;
			pd3dEffect->GetParameterDesc(hContextAnnotation, &xAnnotationDesc);

			assert(xAnnotationDesc.Type == D3DXPT_STRING && "type of Context-Annotation must be string!");

			if (xAnnotationDesc.Type == D3DXPT_STRING)
			{
				const char* pcContext;
				pd3dEffect->GetString(hContextAnnotation, &pcContext);

				if (strcmp(pcContext, SHADERCONSTANT_ANNOTATIONVALUE_CONTEXT_OBJECT) != 0)
				{
					return false;
				}
			}
		}


		D3DXPARAMETER_DESC xParameterDesc;
		pd3dEffect->GetParameterDesc(hParameter, &xParameterDesc);

		if ((xParameterDesc.Flags & D3DX_PARAMETER_SHARED) == 0)	// gesharete Parameter igonrieren, diese sollen nicht pro Material gesetzt werden
		{
			return true;
		}
	}

	return false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int
CMaterial::GetParameterIdx(const string& sParameterName)
{
	int iParamIdx = FindParameterIdx(sParameterName);

	if (iParamIdx != -1)
	{
		return iParamIdx;
	}

	// Parameter in die Liste aufnehmen
	if (ParameterContextIsObject(sParameterName))
	{
		ID3DXEffect* pd3dEffect = m_hEffect->GetD3DXEffect();

		D3DXHANDLE hParameter = pd3dEffect->GetParameterByName(NULL, sParameterName.c_str());

		if (hParameter)
		{
			D3DXPARAMETER_DESC xParameterDesc;
			pd3dEffect->GetParameterDesc(hParameter, &xParameterDesc);

			CEffectParameter& rxNewParam = m_axEffectParameters.Push();
			rxNewParam.m_eParameterType = xParameterDesc.Type;
			rxNewParam.m_sParameterName = sParameterName;
			rxNewParam.m_hParameter = hParameter;
			rxNewParam.m_iParameterSize = xParameterDesc.Bytes;

			return m_axEffectParameters.TopOfStack();
		}
	}


	// nicht gefunden
	return -1;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterial::SetParameter(const string& sParameterName, void* pValue, size_t iValueSize)
{
	int iParamIdx = GetParameterIdx(sParameterName);
	assert(iParamIdx != -1);

	if (iParamIdx != -1)
	{
		m_axEffectParameters[iParamIdx].m_xParameterValue.Set_Void(pValue, iValueSize);
		InvalidateParameterBlock();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterial::SetParameterF(const string& sParameterName, float fValue)
{
	int iParamIdx = GetParameterIdx(sParameterName);
	assert(iParamIdx != -1);

	if (iParamIdx != -1)
	{
		assert(m_axEffectParameters[iParamIdx].m_eParameterType == D3DXPT_FLOAT);
		m_axEffectParameters[iParamIdx].m_xParameterValue.Set_Void(&fValue, sizeof(float));
		InvalidateParameterBlock();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterial::SetParameterI(const string& sParameterName, int iValue)
{
	int iParamIdx = GetParameterIdx(sParameterName);
	assert(iParamIdx != -1);

	if (iParamIdx != -1)
	{
		assert(m_axEffectParameters[iParamIdx].m_eParameterType == D3DXPT_INT);
		m_axEffectParameters[iParamIdx].m_xParameterValue.Set_Void(&iValue, sizeof(int));
		InvalidateParameterBlock();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterial::SetParameterV2(const string& sParameterName, const CVec2& vValue)
{
	int iParamIdx = GetParameterIdx(sParameterName);
	assert(iParamIdx != -1);

	if (iParamIdx != -1)
	{
		assert(m_axEffectParameters[iParamIdx].m_eParameterType == D3DXPT_FLOAT);
		m_axEffectParameters[iParamIdx].m_xParameterValue.Set_Void(&vValue, sizeof(CVec2));
		InvalidateParameterBlock();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterial::SetParameterV3(const string& sParameterName, const CVec3& vValue)
{
	int iParamIdx = GetParameterIdx(sParameterName);
	assert(iParamIdx != -1);

	if (iParamIdx != -1)
	{
		assert(m_axEffectParameters[iParamIdx].m_eParameterType == D3DXPT_FLOAT);
		m_axEffectParameters[iParamIdx].m_xParameterValue.Set_Void(&vValue, sizeof(CVec3));
		InvalidateParameterBlock();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterial::SetParameterV4(const string& sParameterName, const CVec4& vValue)
{
	int iParamIdx = GetParameterIdx(sParameterName);
	assert(iParamIdx != -1);

	if (iParamIdx != -1)
	{
		assert(m_axEffectParameters[iParamIdx].m_eParameterType == D3DXPT_FLOAT);
		m_axEffectParameters[iParamIdx].m_xParameterValue.Set_Void(&vValue, sizeof(CVec4));
		InvalidateParameterBlock();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterial::SetParameterM44(const string& sParameterName, const CMat4S& mValue)
{
	int iParamIdx = GetParameterIdx(sParameterName);
	assert(iParamIdx != -1);

	if (iParamIdx != -1)
	{
		assert(m_axEffectParameters[iParamIdx].m_eParameterType == D3DXPT_FLOAT);
		m_axEffectParameters[iParamIdx].m_xParameterValue.Set_Void(&mValue, sizeof(CMat4S));
		InvalidateParameterBlock();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterial::SetTexture(const std::string& sParameterName, TTextureHandle& hTexture)
{
	int iParamIdx = GetParameterIdx(sParameterName);

	if (iParamIdx != -1)
	{
		assert(m_axEffectParameters[iParamIdx].ParameterTypeIsTexture());

		m_axEffectParameters[iParamIdx].m_xParameterValue.Set_Texture(hTexture);

		InvalidateParameterBlock();
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool 
CMaterial::GetParameterS(const std::string& sParameterName, std::string* pValueOut)
{
	int iParamIdx = GetParameterIdx(sParameterName);

	if (iParamIdx == -1 ||
		m_axEffectParameters[iParamIdx].m_xParameterValue.Get_Void() == NULL)
	{
		return false;
	}

	assert(m_axEffectParameters[iParamIdx].m_eParameterType == D3DXPT_STRING);

	if (pValueOut)
	{
		*pValueOut = (const char*)m_axEffectParameters[iParamIdx].m_xParameterValue.Get_Void();
	}

	return true;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CMaterial::InvalidateParameterBlock()
{
	if (m_hParameterBlock != NULL)
	{
		m_hEffect->GetD3DXEffect()->DeleteParameterBlock(m_hParameterBlock);
		m_hParameterBlock = NULL;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------

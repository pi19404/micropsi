#include "stdafx.h"

#include "e42/core/MaterialConverter.h"

#include "baselib/filelocator.h"

#include "e42/core/EngineController.h"
#include "e42/core/ShaderConstants.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/EffectFactory.h"
#include "e42/core/D3DXEffectDefault.h"
#include "e42/core/D3DXMaterial.h"

#include "tinyxml.h"
#include "baselib/debugprint.h"
#include "baselib/utils.h"

using std::map;
using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CMaterialConverter::CRefCountedD3DXEffectInstance* 
CMaterialConverter::CRefCountedD3DXEffectInstance::Create()
{
    return new CRefCountedD3DXEffectInstance;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterialConverter::CRefCountedD3DXEffectInstance::Destroy()
{
    delete this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMaterialConverter::CMaterialConverter(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController),
	m_iMaxDXVersion(7)
{
	m_spxUntexturedEffectInstance.Create();
	m_spxUntexturedEffectInstance->m_xEI.SetDefaults(0, NULL);
	m_spxUntexturedEffectInstance->m_xEI.SetEffectFilename("unknown-untextured");

	m_spxDefaultEffectInstance.Create();
	m_spxDefaultEffectInstance->m_xEI.SetDefaults(0, NULL);
	m_spxDefaultEffectInstance->m_xEI.SetEffectFilename("unknown-default");
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMaterialConverter::~CMaterialConverter()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const CD3DXEffectInstance&
CMaterialConverter::GetEffectByTexture(const char* pcTextureName) const
{
    if (pcTextureName == NULL)
    {
        return m_spxUntexturedEffectInstance->m_xEI;
    }

    string sTextureFilename = pcTextureName;
    sTextureFilename = CFileLocator::RemoveFileExtension(sTextureFilename);
    sTextureFilename = CFileLocator::ExtractFilename(sTextureFilename);
    sTextureFilename = Utils::StrToLower(sTextureFilename);

    TEffectInstanceLookUp::const_iterator iter = m_mEffectInstances.find(sTextureFilename);

    if (!m_mEffectInstances.empty() &&
        iter != m_mEffectInstances.end())
    {
        assert(iter->first == sTextureFilename);
        return iter->second->m_xEI;
    }
    else
    {
        return m_spxDefaultEffectInstance->m_xEI;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const CD3DXEffectInstance& 
CMaterialConverter::GetEffectUntextured() const
{
    if (m_spxUntexturedEffectInstance->m_xEI.GetEffectFilename() != NULL)
    {
        return m_spxUntexturedEffectInstance->m_xEI;
    }
    else
    {
        assert(false);
        return m_spxDefaultEffectInstance->m_xEI;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CSmartPointer<CMaterialConverter::CRefCountedD3DXEffectInstance>
CMaterialConverter::GetEffect(TiXmlElement* pxShaderElement)
{
    CSmartPointer<CMaterialConverter::CRefCountedD3DXEffectInstance> spxEffectInstance;

    spxEffectInstance.Create();

    // EffectNamen setzen
    TiXmlAttribute* pxAttribute = pxShaderElement->FirstAttribute();
    assert(pxAttribute);
    assert((strcmp(pxAttribute->Name(), "file") == 0));
    spxEffectInstance->m_xEI.SetEffectFilename(pxAttribute->Value());


	// Parameter setzen
	TiXmlElement* pxParameterElement = pxShaderElement->FirstChildElement("parameter");
    while (pxParameterElement)
    {
        string sKey, sValue;
        DWORD dwNumBytes = 0;
        int iNumParams = 1;
        D3DXEFFECTDEFAULTTYPE eType = D3DXEDT_FORCEDWORD;

        TiXmlAttribute* pxAttribute = pxParameterElement->FirstAttribute();
        while (pxAttribute)
        {
            if (strcmp(pxAttribute->Name(), "key") == 0)
            {
                sKey = pxAttribute->Value();
            }
            if (strcmp(pxAttribute->Name(), "value") == 0)
            {
                sValue = pxAttribute->Value();
            }
            if (strcmp(pxAttribute->Name(), "type") == 0)
            {
                if (sscanf(pxAttribute->Value(), "float[%d]", iNumParams) == 1) eType = D3DXEDT_FLOATS;
                if (sscanf(pxAttribute->Value(), "dword[%d]", iNumParams) == 1) eType = D3DXEDT_DWORD;
                if (strcmp(pxAttribute->Value(), "string") == 0) eType = D3DXEDT_STRING;
                if (strcmp(pxAttribute->Value(), "float") == 0) eType = D3DXEDT_FLOATS;
                if (strcmp(pxAttribute->Value(), "dword") == 0) eType = D3DXEDT_DWORD;
            }

            pxAttribute = pxAttribute->Next();
        }

        assert(eType != D3DXEDT_FORCEDWORD);
        assert(!sKey.empty());
        assert(!sValue.empty());
        
		CD3DXEffectDefault* pxDefault = spxEffectInstance->m_xEI.AddEffectDefault(sKey.c_str());
		pxDefault->Type() = eType;

        union
        {
            int aiBuffer[4];
            int afBuffer[4];
        };

        switch (eType)
        {
        case D3DXEDT_FLOATS:
            switch (iNumParams)
            {
                case 1 : 
                    iNumParams = sscanf(sValue.c_str(), " %f ", afBuffer + 0);
                    assert(iNumParams == 1);
                    break;
                case 2 :  
                    iNumParams = sscanf(sValue.c_str(), " %f %f ", afBuffer + 0, afBuffer + 1);
                    assert(iNumParams == 2);
                    break;
                case 3 :
                    iNumParams = sscanf(sValue.c_str(), " %f %f %f ", afBuffer + 0, afBuffer + 1, afBuffer + 2);
                    assert(iNumParams == 3);
                    break;
                case 4 :
                    iNumParams = sscanf(sValue.c_str(), " %f %f %f %f ", afBuffer + 0, afBuffer + 1, afBuffer + 2, afBuffer + 3);
                    assert(iNumParams == 4);
                    break;
                default :
                    assert(false);
            }
            pxDefault->SetValue(sizeof(float) * iNumParams, afBuffer);
            break;

        case D3DXEDT_DWORD:
            switch (iNumParams)
            {
                case 1 : 
                    iNumParams = sscanf(sValue.c_str(), " %i ", aiBuffer + 0);
                    assert(iNumParams == 1);
                    break;
                case 2 :  
                    iNumParams = sscanf(sValue.c_str(), " %i %i ", aiBuffer + 0, aiBuffer + 1);
                    assert(iNumParams == 2);
                    break;
                case 3 :
                    iNumParams = sscanf(sValue.c_str(), " %i %i %i ", aiBuffer + 0, aiBuffer + 1, aiBuffer + 2);
                    assert(iNumParams == 3);
                    break;
                case 4 :
                    iNumParams = sscanf(sValue.c_str(), " %i %i %i %i ", aiBuffer + 0, aiBuffer + 1, aiBuffer + 2, aiBuffer + 3);
                    assert(iNumParams == 4);
                    break;
                default :
                    assert(false);
            }
            pxDefault->SetValue(sizeof(int) * iNumParams, afBuffer);
            break;

        case D3DXEDT_STRING:
            pxDefault->SetValue((DWORD)sValue.length() + 1, (void*)sValue.c_str());
            break;

        default:
            assert(false);
        }

        pxParameterElement = pxParameterElement->NextSiblingElement("parameter");
    }

    return spxEffectInstance;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterialConverter::LoadMapping(const string& sMappingFile)
{
    string sFullName = m_pxEngineController->GetFileLocator()->GetPath(sMappingFile.c_str());

	TiXmlDocument* pxXMLDoc = new TiXmlDocument(sFullName.c_str());
	if (!pxXMLDoc->LoadFile()) 
	{ 
		DebugPrint("could not load ShaderMappingFile file %s", sMappingFile.c_str());
        assert(false);
        delete pxXMLDoc;
		return; 
	}

    TiXmlNode* pxRootNode = pxXMLDoc->FirstChild("fxselection");
	if(!pxRootNode) return;

    TiXmlElement* pxShaderElement;

    // default-shader
	pxShaderElement = pxRootNode->FirstChildElement("shader_default");
	if (pxShaderElement) 
    { 
        m_spxDefaultEffectInstance = GetEffect(pxShaderElement);
    }


    // untextured-shader
	pxShaderElement = pxRootNode->FirstChildElement("shader_untextured");
	if (pxShaderElement) 
    { 
        m_spxUntexturedEffectInstance = GetEffect(pxShaderElement);
    }
   
    
    // restliche shader
    pxShaderElement = pxRootNode->FirstChildElement("shader");
    while (pxShaderElement)
    { 
        CSmartPointer<CRefCountedD3DXEffectInstance> m_spxEffectInstance;
        m_spxEffectInstance = GetEffect(pxShaderElement);
            
        TiXmlElement* pxTextureElement = pxShaderElement->FirstChildElement("texture");
        while (pxTextureElement)
        {
            string sTextureName = pxTextureElement->FirstChild()->ToText()->Value();
            sTextureName = Utils::StrToLower(sTextureName);
            m_mEffectInstances[sTextureName] = m_spxEffectInstance;

            pxTextureElement = pxTextureElement->NextSiblingElement("texture");
        }

        pxShaderElement = pxShaderElement->NextSiblingElement("shader");
    }

    delete pxXMLDoc;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CMaterialConverter::SetDefaultEffect(const CD3DXEffectInstance& rxEffectInstance)
{
    if (!m_spxDefaultEffectInstance)
    {
        m_spxDefaultEffectInstance.Create();
    }

    m_spxDefaultEffectInstance->m_xEI = rxEffectInstance;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CMaterialConverter::ConvertMaterialToEffectInstance(const CD3DXMaterial& rxMaterial, CD3DXEffectInstance* pxEffectInstanceOut) const
{
	pxEffectInstanceOut->Clear();

	if (rxMaterial.GetTextureFilename() == NULL)
	{
		*pxEffectInstanceOut = GetEffectUntextured();
	}
	else
	{
		string sTextureBaseFilename = rxMaterial.GetTextureFilename();

		*pxEffectInstanceOut = GetEffectByTexture(sTextureBaseFilename.c_str());


		const int iNumMaps = 8;

		string asFilenames[iNumMaps] =
		{
			sTextureBaseFilename,
			CFileLocator::InsertBeforeFileExtension(sTextureBaseFilename, "_specular"),
			CFileLocator::InsertBeforeFileExtension(sTextureBaseFilename, "_bump"),
			CFileLocator::InsertBeforeFileExtension(sTextureBaseFilename, "_normal"),
			CFileLocator::InsertBeforeFileExtension(sTextureBaseFilename, "_detail"),
			CFileLocator::InsertBeforeFileExtension(sTextureBaseFilename, "_environment"),
			CFileLocator::InsertBeforeFileExtension(sTextureBaseFilename, "_shadow"),
			CFileLocator::InsertBeforeFileExtension(sTextureBaseFilename, "_light")
		};

		const char* apcShaderConstants[iNumMaps] =
		{
			SHADERCONSTANT_NAME_DIFFUSEMAP,
			SHADERCONSTANT_NAME_SPECULARMAP,
			SHADERCONSTANT_NAME_BUMPMAP,
			SHADERCONSTANT_NAME_NORMALMAP,
			SHADERCONSTANT_NAME_DETAILMAP,
			SHADERCONSTANT_NAME_ENVIRONMENTMAP,
			SHADERCONSTANT_NAME_SHADOWMAP,
			SHADERCONSTANT_NAME_LIGHTMAP
		};


		for (int iMap = 0; iMap < iNumMaps; iMap++)
		{
			const string sFilename = m_pxEngineController->GetFileLocator()->GetPath(asFilenames[iMap]);

			if (iMap == 0 ||
				CFileLocator::FileExists(sFilename))
			{
				CD3DXEffectDefault* pxDefault = pxEffectInstanceOut->FindEffectDefault(apcShaderConstants[iMap], true);
				pxDefault->Type() = D3DXEDT_STRING;
				pxDefault->SetValue((DWORD)asFilenames[iMap].length() + 1, asFilenames[iMap].c_str());
			}
		}
	}


	
	{	// Ambient
		CD3DXEffectDefault* pxDefault = pxEffectInstanceOut->FindEffectDefault(SHADERCONSTANT_NAME_MATERIALAMBIENT, true);
		pxDefault->Type() = D3DXEDT_FLOATS;
		pxDefault->SetValue(sizeof(rxMaterial.MatD3D().Ambient), &rxMaterial.MatD3D().Ambient);
	}

	{	// Diffuse
		CD3DXEffectDefault* pxDefault = pxEffectInstanceOut->FindEffectDefault(SHADERCONSTANT_NAME_MATERIALDIFFUSE, true);
		pxDefault->Type() = D3DXEDT_FLOATS;
		pxDefault->SetValue(sizeof(rxMaterial.MatD3D().Diffuse), &rxMaterial.MatD3D().Diffuse);
	}

	{	// Specular
		CD3DXEffectDefault* pxDefault = pxEffectInstanceOut->FindEffectDefault(SHADERCONSTANT_NAME_MATERIALSPECULAR, true);
		pxDefault->Type() = D3DXEDT_FLOATS;
		pxDefault->SetValue(sizeof(rxMaterial.MatD3D().Specular), &rxMaterial.MatD3D().Specular);
	}

	{	// Emissive
		CD3DXEffectDefault* pxDefault = pxEffectInstanceOut->FindEffectDefault(SHADERCONSTANT_NAME_MATERIALEMISSIVE, true);
		pxDefault->Type() = D3DXEDT_FLOATS;
		pxDefault->SetValue(sizeof(rxMaterial.MatD3D().Emissive), &rxMaterial.MatD3D().Emissive);
	}

	{	// Power
		CD3DXEffectDefault* pxDefault = pxEffectInstanceOut->FindEffectDefault(SHADERCONSTANT_NAME_SPECULARPOWER, true);
		pxDefault->Type() = D3DXEDT_FLOATS;
		pxDefault->SetValue(sizeof(rxMaterial.MatD3D().Power), &(rxMaterial.MatD3D().Power));
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------

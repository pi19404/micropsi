#include "stdafx.h"

#include "e42/core/TextureSettings.h"

#include <d3dx9.h>
#include <windows.h>
#include <d3d9types.h>
#include "baselib/utils.h"
#include "tinyxml.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
struct TextureFormatTableElement
{
    const char* pcName;
    D3DFORMAT   xFormat;
} g_axTextureFormatTable[] =
{
    // !!!! bei der Reihenfolge auf Substrings achten! die längsten Bezeichnungen nach vorne !!!!
    {"A8R8G8B8",    D3DFMT_A8R8G8B8},
    {"A1R5G5B5",    D3DFMT_A1R5G5B5},
    {"A4R4G4B4",    D3DFMT_A4R4G4B4},
    {"R8G8B8",      D3DFMT_R8G8B8},
    {"R5G6B5",      D3DFMT_R5G6B5},
    {"R5G6B5",      D3DFMT_R5G6B5},
    {"DXT1",        D3DFMT_DXT1},
    {"DXT2",        D3DFMT_DXT2},
    {"DXT3",        D3DFMT_DXT3},
    {"DXT3",        D3DFMT_DXT3},
    {"DXT4",        D3DFMT_DXT4},
    {"DXT5",        D3DFMT_DXT5},
    {"A8",          D3DFMT_A8},
};

//-----------------------------------------------------------------------------------------------------------------------------------------
CTextureSettings::CLodSetting::CLodSetting()
{
    m_sSubDirectory = "";
    m_fmtTextureFormat = D3DFMT_UNKNOWN;
	m_xSize.cx = D3DX_DEFAULT;
    m_xSize.cy = D3DX_DEFAULT;
    m_iMaxMipMapLevel = D3DX_DEFAULT;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CTextureSettings::CLodSetting::~CLodSetting()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CTextureSettings::CLodSetting::InitFromXML(const TiXmlElement* pxLodSettingsElement)
{
    // SubDir
    const TiXmlNode* pxSubDirNode = pxLodSettingsElement->FirstChild("subdir");
    if (pxSubDirNode)
    {
        assert(pxSubDirNode->Type() == TiXmlNode::ELEMENT);
        assert(pxSubDirNode->ToElement()->FirstChild());
        assert(pxSubDirNode->ToElement()->FirstChild()->Type() == TiXmlNode::TEXT);

        m_sSubDirectory = pxSubDirNode->ToElement()->FirstChild()->ToText()->Value();
    }


    // Size
    const TiXmlNode* pxSizeNode = pxLodSettingsElement->FirstChild("size");
    if (pxSizeNode)
    {
        assert(pxSizeNode->Type() == TiXmlNode::ELEMENT);
        assert(pxSizeNode->ToElement()->FirstChild());
        assert(pxSizeNode->ToElement()->FirstChild()->Type() == TiXmlNode::TEXT);

        const char* pcSize = pxSizeNode->ToElement()->FirstChild()->ToText()->Value();
        int iNumFields = sscanf(pcSize, " %d x %d ", &m_xSize.cx, &m_xSize.cy);

        assert(iNumFields == 2);
        assert(Utils::IsPowerOf2(m_xSize.cx));
        assert(Utils::IsPowerOf2(m_xSize.cy));
    }


    // Format
    const TiXmlNode* pxFormatNode = pxLodSettingsElement->FirstChild("format");
    if (pxFormatNode)
    {
        assert(pxFormatNode->Type() == TiXmlNode::ELEMENT);
        assert(pxFormatNode->ToElement()->FirstChild());
        assert(pxFormatNode->ToElement()->FirstChild()->Type() == TiXmlNode::TEXT);

        const char* pcFormat = pxFormatNode->ToElement()->FirstChild()->ToText()->Value();
        for (int iTableEntry = 0; iTableEntry < sizeof(g_axTextureFormatTable) / sizeof(TextureFormatTableElement); iTableEntry++)
        {
            if (strstr(pcFormat, g_axTextureFormatTable[iTableEntry].pcName) != NULL)
            {
                m_fmtTextureFormat = g_axTextureFormatTable[iTableEntry].xFormat;
                break;
            }
        }

        assert(m_fmtTextureFormat != D3DFMT_UNKNOWN);
    }

    
    // MipMapLevel
    const TiXmlNode* pxMipMapLevelNode = pxLodSettingsElement->FirstChild("maxmipmaplevel");
    if (pxMipMapLevelNode)
    {
        assert(pxMipMapLevelNode->Type() == TiXmlNode::ELEMENT);
        assert(pxMipMapLevelNode->ToElement()->FirstChild());
        assert(pxMipMapLevelNode->ToElement()->FirstChild()->Type() == TiXmlNode::TEXT);

        const char* pcMaxMipMapLevel = pxMipMapLevelNode->ToElement()->FirstChild()->ToText()->Value();
        int iNumFields = sscanf(pcMaxMipMapLevel, " %d ", &m_iMaxMipMapLevel);
        
        assert(iNumFields == 1);
    }

    return true;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool 
CTextureSettings::InitFromXML(const TiXmlElement* pxTextureSettingElement)
{
    const TiXmlElement* pxLodElement = pxTextureSettingElement->FirstChildElement("lod");

    while (pxLodElement)
    {
        const char* pcLodIndex = pxLodElement->Attribute("index");
        if (pcLodIndex == NULL)
        {
            assert(false);
            return false;
        }


        if (strcmp(pcLodIndex, "default") == 0)
        {
            CLodSetting xDefaultLodSetting;
            xDefaultLodSetting.InitFromXML(pxLodElement);

            for (int iLodIdx = 0; iLodIdx < NUM_TEXTURE_LOD_LEVELS; iLodIdx++)
            {
                m_axLodLevels[iLodIdx] = xDefaultLodSetting;
            }
        }
        else
        {
            int iLodIdx = atoi(pcLodIndex);
            assert(iLodIdx < NUM_TEXTURE_LOD_LEVELS);
            m_axLodLevels[iLodIdx].InitFromXML(pxLodElement);
        }


        pxLodElement = pxLodElement->NextSiblingElement("lod");
    }

    return true;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

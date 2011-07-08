#pragma once

#ifndef E42_TEXTURESETTINGS_H_INCLUDED
#define E42_TEXTURESETTINGS_H_INCLUDED

#include "e42/stdinc.h"

#include "baselib/array.h"
#include "baselib/size.h"

#include <string>

class TiXmlElement;
typedef enum _D3DFORMAT D3DFORMAT;

class CTextureSettings
{
public:
    enum {NUM_TEXTURE_LOD_LEVELS = 8};

    class CLodSetting
    {
    public:
        CLodSetting();
        ~CLodSetting();

        std::string     m_sSubDirectory;
        D3DFORMAT       m_fmtTextureFormat;     ///< Pixelformat/Kompression
        CSize           m_xSize;
        int             m_iMaxMipMapLevel;      ///< Anzahl der zu generierenden MipMaps (0 == alle, 1 == nur höchste Stufe)

        bool InitFromXML(const TiXmlElement* pxLodSettingElement);
    };

    CInlineArray<CLodSetting, NUM_TEXTURE_LOD_LEVELS>    m_axLodLevels;     ///< Verschiedene Texturgrößen für unterschiedliche Texturqualität-Einstellungen

    bool InitFromXML(const TiXmlElement* pxTextureSettingElement);
};

#endif // E42_TEXTURESETTINGS_H_INCLUDED

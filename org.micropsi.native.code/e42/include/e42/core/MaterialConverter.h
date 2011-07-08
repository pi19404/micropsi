#pragma once

/*
	CMaterialConverter:
		konvertiert Fixed-Function-Materialparameter (D3DXMATERIAL) in Shader-Parameter (D3DXEFFECTINSTANCE)
		kann eine XML-Datei auslesen, die Texturnamen auf Shadernamen mappt.
*/

#ifndef MATERIALCONVERTER_H_INCLUDED
#define MATERIALCONVERTER_H_INCLUDED

#include "e42/stdinc.h"

#include "e42/core/ResourceHandles.h"
#include "e42/core/D3DXEffectInstance.h"
#include "baselib/RefCountedObject.h"

#include <string>
#include <map>

class TiXmlElement;
class CEngineController;
class CD3DXMaterial;

class CMaterialConverter
{
private:
    class CRefCountedD3DXEffectInstance : public CRefCountedObject
    {
    public:
        CD3DXEffectInstance m_xEI;

        static CRefCountedD3DXEffectInstance* Create();
        void Destroy();
    };

    typedef std::map<const std::string, CSmartPointer<CRefCountedD3DXEffectInstance> >   TEffectInstanceLookUp;

    const CEngineController*                        m_pxEngineController;

    TEffectInstanceLookUp                           m_mEffectInstances;
    CSmartPointer<CRefCountedD3DXEffectInstance>    m_spxDefaultEffectInstance;
    CSmartPointer<CRefCountedD3DXEffectInstance>    m_spxUntexturedEffectInstance;

    int m_iMaxDXVersion;

    CSmartPointer<CRefCountedD3DXEffectInstance> GetEffect(TiXmlElement* pxShaderElement);

	const CD3DXEffectInstance& GetEffectByTexture(const char* pcTextureName) const;
	const CD3DXEffectInstance& GetEffectUntextured() const;


public:

    CMaterialConverter(CEngineController* pxEngineController);
    ~CMaterialConverter();

    void LoadMapping(const std::string& sMappingFile);
    void SetDefaultEffect(const CD3DXEffectInstance& rxEffectInstance);

	void ConvertMaterialToEffectInstance(const CD3DXMaterial& rxMaterial, CD3DXEffectInstance* pxEffectInstanceOut) const;
};

#endif //MATERIALCONVERTER_H_INCLUDED
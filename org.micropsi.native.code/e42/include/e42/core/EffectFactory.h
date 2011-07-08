#pragma once

#ifndef E42_EFFECTFACTORY_H_INCLUDED
#define E42_EFFECTFACTORY_H_INCLUDED

#include "e42/stdinc.h"

#include "e42/core/ResourceHandles.h"
#include "e42/core/ResourceFactory.h"

class CEngineController;
struct ID3DXEffectPool;
struct ID3DXEffect;
class CD3DXMacro;

class CEffectFactory : public CResourceFactory
{
private:
    CEngineController*  m_pxEngineController;

    ID3DXEffectPool*    m_pd3dEffectPool;                                       ///< EffectPool für alle Effekte dieser Factory

    TEffectHandle       m_hndSharedVarsEffect;                                  ///< globaler Effekt, der dem Zugriff auf die Shared-Parameter im EffectPool dient

	CDynArray<CD3DXMacro> m_axEffectMacros;										///< makros, die für die Effekte definiert wurden

    bool                m_bCloneEffects;

    void    DestroyResource(void* pxResource);

    CResourceProxy*     CloneResourceProxy(CResourceProxy* pxResourceProxy);    ///< legt eine Kopie des ResourceProxys an

	void				CreateStandardEffectMacros();


    typedef CEffectShader* (__cdecl* CreateWrapperFunction)(ID3DXEffect* pd3dEffect);
    typedef void (__cdecl* DestroyWrapperFunction)(CEffectShader* pElement);

    CreateWrapperFunction     m_fpCreateWrapper;                                ///< Pointer auf die Funktion die dem Erzeugen von Effect-Wrappern dient
    DestroyWrapperFunction    m_fpDestroyWrapper;                               ///< Pointer auf die Funktion die dem Erzeugen von Effect-Wrappern dient

public:
    CEffectFactory(CEngineController* pxEngineController);
    ~CEffectFactory();

    TEffectHandle CreateEffect(const std::string sFilename, bool bDebug = false); ///< erzeugt einen Effekt aus einem FX-File

    void SetSharedVarsEffect(TEffectHandle hndSharedVarsEffect);                ///< setzt den Effekt, der dem Zugriff auf die Shared-Parameter im EffectPool dient
    TEffectHandle GetSharedVarsEffect() const;                                  ///< gibt den Effekt zurück, der dem Zugriff auf die Shared-Parameter im EffectPool dient

    void SetWrapperCreateFunction(CreateWrapperFunction fpCreateWrapper);       ///< setzt die Funktion, die den Wrapper für DirectX-Effekte erzeugt
    void SetWrapperDestroyFunction(DestroyWrapperFunction fpDestroyWrapper);    ///< setzt die Funktion, die den Wrapper für DirectX-Effekte löscht

    void SetCloneEffects(bool bEnable);                                         ///< wenn disabled, wird immer die selbe ID3XEffect-Instanz zurückgegeben, wenn ein bestimmtes File mehrfach geladen wird, andernfalls werden die Effekte gecloned -> hat Einfluss auf Gültigkeit der Variablen
};


#endif // E42_EFFECTFACTORY_H_INCLUDED
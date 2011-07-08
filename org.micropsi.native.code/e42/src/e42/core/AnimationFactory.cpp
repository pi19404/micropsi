#include "stdafx.h"

#include "e42/core/AnimationFactory.h"

#include "e42/core/EngineController.h"
#include "e42/core/XFileLoader.h"

#include "baselib/FileLocator.h"

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CAnimationFactory::CAnimationFactory(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CAnimationFactory::~CAnimationFactory()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CAnimationFactory::DestroyResource(void* pxResource)
{
    ((ID3DXAnimationSet*)pxResource)->Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TAnimationHandle 
CAnimationFactory::CreateAnimationFromFile(const string& sFilename)
{
    string sResourceID = m_pxEngineController->GetFileLocator()->GetPath(sFilename);
    CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

    if (pxResourceProxy == NULL)
    {
        CComObjectPtr<ID3DXAnimationSet> spxAnimationSet = 
            m_pxEngineController->GetXFileLoader()->LoadAnimation(sResourceID);

        if (spxAnimationSet)
            spxAnimationSet->AddRef(); // ResourceFactory hält nun eine weitere Ref.

        pxResourceProxy = __super::AddResource(sResourceID, spxAnimationSet);
    }

    return TAnimationHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TAnimationHandle 
CAnimationFactory::RegisterAnimation(CComObjectPtr<ID3DXAnimationSet> spxAnimationSet, const std::string& sResourceID)
{
    CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

    if (pxResourceProxy == NULL)
    {
        if (spxAnimationSet)
            spxAnimationSet->AddRef(); // ResourceFactory hält nun eine weitere Ref.

        pxResourceProxy = __super::AddResource(sResourceID, spxAnimationSet);
    }

    return TAnimationHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

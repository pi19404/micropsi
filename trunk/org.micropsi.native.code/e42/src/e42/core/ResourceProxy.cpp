#include "stdafx.h"

#include "e42/core/ResourceProxy.h"

/*  
-------> ist jetzt alles im Inline-File <-------

#include "e42/ResourceFactory.h"
//-----------------------------------------------------------------------------------------------------------------------------------------
CResourceProxy::CResourceProxy(void* pxResource, CResourceFactory* pxResourceFactory)
:   m_iReferenceCount                   (0),
    m_pxResource                         (pxResource),
    m_pxResourceFactory                 (pxResourceFactory)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CResourceProxy::~CResourceProxy()
{
    assert(m_iReferenceCount == 0);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void* 
CResourceProxy::LookUpResource() const
{
    return m_pxResource;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CResourceProxy::AddRef()
{
    assert(m_pxResource);
    ++m_iReferenceCount;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int 
CResourceProxy::Release()
{
    assert(m_iReferenceCount > 0);

    if (--m_iReferenceCount == 0)
    {
        m_pxResourceFactory->DestroyResourceProxy(this);
        return 0;
    }
    else
    {
        return m_iReferenceCount;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
*/

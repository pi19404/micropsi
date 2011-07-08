/*
    ein ResourceProxy merkt sich, wie oft eine Resource verwendet wird (ReferenceCount) 
    und kann der ResourceFactory mitteilen, wenn die Resource nichtmehr benötigt wird
*/

#pragma once

#ifndef E42_RESOURCEPROXY_H_INCLUDED
#define E42_RESOURCEPROXY_H_INCLUDED

#include "e42/stdinc.h"

#include "e42/core/ResourceFactory.h"

class CResourceProxy
{
private:
    int                 m_iReferenceCount;
    void*               m_pxResource;

    CResourceFactory*   m_pxResourceFactory;

public:

    CResourceProxy(void* pxResource, CResourceFactory* pxResourceFactory);
    ~CResourceProxy();

    void*   GetResource() const;

    int     GetRefCount() const;

    void    AddRef();
    int     Release();
};

#include "e42/core/ResourceProxy.inl"

#endif // E42_RESOURCEPROXY_H_INCLUDED

#pragma once

#ifndef E42_RESOURCEHANDLE_H_INCLUDED
#define E42_RESOURCEHANDLE_H_INCLUDED

#include "e42/stdinc.h"

#include "e42/core/ResourceProxy.h"

template<typename TResourcePtr>
class CResourceHandle
{
private:
    CResourceProxy*     m_pxResourceProxy;

public:
    CResourceHandle();
    CResourceHandle(const CResourceHandle& rxHnd);
    ~CResourceHandle();

    explicit CResourceHandle(CResourceProxy* pxResourceProxy);

    CResourceHandle& operator=(const CResourceHandle& rxHnd);

    void Release();

    TResourcePtr GetPtr() const;
    TResourcePtr operator->() const;

    operator bool() const;

    bool operator==(const CResourceHandle& hndResource) const;
    bool operator!=(const CResourceHandle& hndResource) const;
};

#include "e42/core/ResourceHandle.inl"

#endif // E42_RESOURCEHANDLE_H_INCLUDED
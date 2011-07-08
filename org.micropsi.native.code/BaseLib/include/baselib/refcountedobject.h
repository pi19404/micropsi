#pragma once

#ifndef BASELIB_REFCOUNTEDOBJECT_H_INCLUDED
#define BASELIB_REFCOUNTEDOBJECT_H_INCLUDED

#include <assert.h>

//------------------------------------------------------------------------------
class CRefCountedObject
{
    friend class CSmartPointerBase;

private:
    int m_iRefCount;

    void AddRef();
    void Release();
    virtual void Destroy() = 0;

protected:
    CRefCountedObject(const CRefCountedObject& rco);
    CRefCountedObject& operator=(const CRefCountedObject& rco);

    CRefCountedObject();
    virtual ~CRefCountedObject();
};
//------------------------------------------------------------------------------
class CSmartPointerBase
{
protected:
    CSmartPointerBase();
    CSmartPointerBase(CRefCountedObject* pObject);
    virtual ~CSmartPointerBase();

    CRefCountedObject*  m_pObject;

    void AddRef() const;
    void Release() const;

public:
    operator bool() const;
};
//------------------------------------------------------------------------------
template<class T>
class CSmartPointer : public CSmartPointerBase
{
public:
    CSmartPointer() {};
    virtual ~CSmartPointer() {};

    CSmartPointer(const T* pObject);
    CSmartPointer(const CSmartPointer& sp);
    CSmartPointer(const int i);
    CSmartPointer& operator=(const T* pObject);
    CSmartPointer& operator=(const CSmartPointer& sp);
    CSmartPointer& operator=(const int i);

    void Create();
    T* operator->() const;
    T& operator*() const;
};
//------------------------------------------------------------------------------

#include "baselib/refcountedobject.inl"

#endif // BASELIB_REFCOUNTEDOBJECT_H_INCLUDED
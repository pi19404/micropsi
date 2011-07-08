/*******************************************************************************
 Array.h - Array, das in der Debug-Version Bounds-Checking macht.
    CInlineArray / CArray

    dieses Array ist da allerdings noch etwas pingeliger:
    es erlaubt immer nur einen Pointer, der auf das Array zeigt, bei Zuweisung
    wird der alte Pointer auf NULL gesetzt! Dadurch kann im Destruktor des
    Pointers geprüft werden, ob vergessen wurde das Array freizugeben.
    Andererseits wird die Verwendbarkeit an einigen Stellen erschwert, deshalb
    fliegt diese Einschränkung vielleicht bald raus.
*******************************************************************************/
#pragma once

#ifndef BL_ARRAY_H_INCLUDED
#define BL_ARRAY_H_INCLUDED

#include <assert.h>
#include <malloc.h>

//------------------------------------------------------------------------------
template<typename ElementType, unsigned int uiSize>
class CInlineArray
{
public:
    typename ElementType m_Array[uiSize];

    ElementType& operator[](unsigned int uiIndex);
    const ElementType& operator[](unsigned int uiIndex) const;

    unsigned int Size() const;
};
//------------------------------------------------------------------------------
template<typename _ElementType, bool bMalloc = false>
class CArray
{
public:
    typedef _ElementType ElementType;

    #ifdef _DEBUG
        unsigned int m_uiSize;
    #endif

    typename ElementType* m_pArray;


    // default constructor / destructor
    #ifdef _DEBUG
        CArray<ElementType, bMalloc>() :  m_uiSize(0) {};
        ~CArray<ElementType, bMalloc>() {assert(m_uiSize == 0);};
    #else
        CArray<ElementType, bMalloc>() {};
        ~CArray<ElementType, bMalloc>() {};
    #endif

    
    CArray<ElementType, bMalloc>(CArray<ElementType, bMalloc>& array);                  // copy constructor ("move constructor")
    CArray<ElementType, bMalloc>& operator= (CArray<ElementType, bMalloc>& array);      // operator= ("move operator")
    CArray<ElementType, bMalloc>(ElementType* pArray, unsigned int uiSize);             // construct from pointer & size
    CArray<ElementType, bMalloc>(unsigned int uiSize);                                  // construct from size (creating array)


    void Assign(ElementType* pArray, unsigned int uiSize);                              // assign from pointer & size
    void Assign(CArray<ElementType, bMalloc>& array);                                   // assign from other Array

    void UnAssign();                                                                    // unassign (no delete)


    void New(unsigned int uiSize);
    void Delete();

    void Malloc(unsigned int uiBytes);
    void Free();
    void ReAlloc(unsigned int uiBytes);


    ElementType& operator[](unsigned int uiIndex) const;

    operator bool() const;

    CArray<ElementType, bMalloc> operator+(int i);

    void memset(int iValue, unsigned int uiBytes);
};

//------------------------------------------------------------------------------
#include "baselib/Array.inl"
//------------------------------------------------------------------------------
typedef CArray<int>             IntArray;
typedef CArray<short>           ShortArray;
typedef CArray<unsigned int>    UnsignedIntArray;
typedef CArray<unsigned short>  UnsignedShortArray;
typedef CArray<unsigned char>   UnsignedCharArray;
typedef CArray<float>           FloatArray;
typedef CArray<short, true>     ShortArrayM;
typedef CArray<float, true>     FloatArrayM;
typedef CArray<unsigned short,true>  UnsignedShortArrayM;
//------------------------------------------------------------------------------

#endif // BL_ARRAY_H_INCLUDED

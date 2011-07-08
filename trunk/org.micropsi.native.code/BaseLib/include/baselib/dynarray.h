/*******************************************************************************
 DynArray.h - Array mit dynamischer Größe

    verwendet C-Allokation für Vergrößerung/Verkleinerung,
    Achtung: Objekte bekommen ein realloc nicht mit, dadurch kann es zu einer
    unbemerkten Verschiebung im Speicher kommen. Pointer auf Objekte sind also
    nur solange gültig, wie man nichts am Array ändert, Objekte, die Pointer
    auf sich selbst halten, werden nicht unterstützt (also Achtung bei Objekten,
    aus Mehrfachableitung !!)
*******************************************************************************/
#pragma once

#ifndef BL_DYNARRAY_H_INCLUDED
#define BL_DYNARRAY_H_INCLUDED

#include "baselib/Array.h"
#include <assert.h>

template<typename _ElementType, unsigned int uiGrowthExp = 5, bool bSorted = true>
class CDynArray
{
private:
    typedef _ElementType ElementType;

#pragma pack(push, 1)
    class CContainer
    {
    private:
        CContainer(const CContainer& c) : m_e(c.m_e) {};
        const CContainer& operator=(const CContainer& c) {m_e = c.m_e; return *this;};
        const CContainer& operator=(const ElementType& e) {m_e = e; return *this;};

    public:
        CContainer() {};
        CContainer(const ElementType& e) : m_e(e) {};
        ~CContainer() {};

        ElementType m_e;
    };
#pragma pack(pop)


    CArray<CContainer, true>    m_pElements;
    unsigned int                m_uiSize;


    // private=
    CDynArray<_ElementType, uiGrowthExp, bSorted> operator=(const CDynArray<_ElementType, uiGrowthExp, bSorted>& a);


    void IncArraySize();
    void DecArraySize();
    
public:

    CDynArray<_ElementType, uiGrowthExp, bSorted>(const CDynArray<_ElementType, uiGrowthExp, bSorted>& a);

	CDynArray();
    ~CDynArray();

    ElementType& operator[](unsigned int uiIndex) const;
    const ElementType& operator()(unsigned int uiIndex) const;
    unsigned int PushEntry();
    ElementType& Push();

    unsigned int PushEntry(const ElementType& e);
    void PopEntry();

    int Find(const ElementType& e);
	int Include(const ElementType& e);
    void RemoveEntry(const ElementType& e);
    void DeleteEntry(unsigned int uiIndex);

	void MoveEntry(unsigned int uiIndexNow, unsigned int uiIndexThen);

    unsigned int TopOfStack() const;
    unsigned int Size() const;

    void SetSize(unsigned int uiNewSize);

    const CDynArray<_ElementType, uiGrowthExp, bSorted>& operator++(int);
    const CDynArray<_ElementType, uiGrowthExp, bSorted>& operator--(int);

    void Clear();

    void RemoveDuplicates();

    void Sort();

	ElementType* GetArrayPointer();

};

#include "baselib/DynArray.inl"

#endif // BL_DYNARRAY_H_INCLUDED
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
template<typename ElementType, unsigned int uiSize>
ElementType& 
CInlineArray<ElementType, uiSize>::operator[](unsigned int uiIndex)
{
    assert(uiIndex < uiSize);
    return m_Array[uiIndex];
};
//------------------------------------------------------------------------------
template<typename ElementType, unsigned int uiSize>
const ElementType& 
CInlineArray<ElementType, uiSize>::operator[](unsigned int uiIndex) const
{
    assert(uiIndex < uiSize);
    return m_Array[uiIndex];
};
//------------------------------------------------------------------------------
template<typename ElementType, unsigned int uiSize>
unsigned int
CInlineArray<ElementType, uiSize>::Size() const
{
    return uiSize;
};
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
// copy constructor ("move constructor")
template<typename _ElementType, bool bMalloc>
CArray<_ElementType, bMalloc>::CArray<_ElementType, bMalloc>(CArray<_ElementType, bMalloc>& array)   :
    #ifdef _DEBUG
        m_uiSize(array.m_uiSize),
    #endif
    m_pArray(array.m_pArray)
{
    #ifdef _DEBUG
        array.m_uiSize = 0;
    #endif
};
//------------------------------------------------------------------------------
// operator= ("move operator")
template<typename _ElementType, bool bMalloc>
CArray<_ElementType, bMalloc>& 
CArray<_ElementType, bMalloc>::operator= (CArray<_ElementType, bMalloc>& array)
{
    m_pArray = array.m_pArray;
    #ifdef _DEBUG
        assert(m_uiSize == 0);
        m_uiSize = array.m_uiSize;
        array.m_uiSize = 0;
    #endif
    return *this;
};
//------------------------------------------------------------------------------
// construct from pointer & size
template<typename _ElementType, bool bMalloc>
CArray<_ElementType, bMalloc>::CArray<_ElementType, bMalloc>(ElementType* pArray, unsigned int uiSize)  :
    #ifdef _DEBUG
        m_uiSize(uiSize),
    #endif
    m_pArray(pArray)
{
};
//------------------------------------------------------------------------------
// construct from size (creating array)
template<typename _ElementType, bool bMalloc>
CArray<_ElementType, bMalloc>::CArray<_ElementType, bMalloc>(unsigned int uiSize)
{
    if (!bMalloc)
    {
        m_pArray = new ElementType[uiSize];
        #ifdef _DEBUG
            m_uiSize = uiSize;
        #endif
    }
    else
    {
        m_pArray = (ElementType*)malloc(uiSize);
        #ifdef _DEBUG
            m_uiSize = uiSize / sizeof(ElementType);
        #endif
    }
};
//------------------------------------------------------------------------------
// assign from pointer & size
template<typename _ElementType, bool bMalloc>
void 
CArray<_ElementType, bMalloc>::Assign(ElementType* pArray, unsigned int uiSize)
{
    m_pArray = pArray;
    #ifdef _DEBUG
        assert(m_uiSize == 0);
        m_uiSize = uiSize;
    #endif
};
//------------------------------------------------------------------------------
// assign from other Array
template<typename _ElementType, bool bMalloc>
void 
CArray<_ElementType, bMalloc>::Assign(CArray<_ElementType, bMalloc>& array)
{
    m_pArray = array.m_pArray;
    #ifdef _DEBUG
        assert(m_uiSize == 0);
        m_uiSize = array.m_uiSize;
        array.m_uiSize = 0;
    #endif
};
//------------------------------------------------------------------------------
// unassign (no delete)
template<typename _ElementType, bool bMalloc>
void 
CArray<_ElementType, bMalloc>::UnAssign()
{
    #ifdef _DEBUG
        m_uiSize = 0;
    #endif
};
//------------------------------------------------------------------------------
template<typename _ElementType, bool bMalloc>
void 
CArray<_ElementType, bMalloc>::New(unsigned int uiSize)
{
    assert(!bMalloc);
    m_pArray = new ElementType[uiSize];
    #ifdef _DEBUG
        assert(m_uiSize == 0);
        m_uiSize = uiSize;
    #endif
};
//------------------------------------------------------------------------------
template<typename _ElementType, bool bMalloc>
void 
CArray<_ElementType, bMalloc>::Delete()
{
    assert(!bMalloc);
    delete [] m_pArray;
    #ifdef _DEBUG
        m_uiSize = 0;
    #endif
};
//------------------------------------------------------------------------------
template<typename _ElementType, bool bMalloc>
void 
CArray<_ElementType, bMalloc>::Malloc(unsigned int uiBytes)
{
    assert(bMalloc);
    assert(uiBytes % sizeof(ElementType) == 0);
    m_pArray = (ElementType*)malloc(uiBytes);
    #ifdef _DEBUG
        assert(m_uiSize == 0);
        m_uiSize = uiBytes / sizeof(ElementType);
    #endif
};
//------------------------------------------------------------------------------
template<typename _ElementType, bool bMalloc>
void 
CArray<_ElementType, bMalloc>::Free()
{
    assert(bMalloc);
    free(m_pArray);
    #ifdef _DEBUG
        m_uiSize = 0;
    #endif
};
//------------------------------------------------------------------------------
template<typename _ElementType, bool bMalloc>
void 
CArray<_ElementType, bMalloc>::ReAlloc(unsigned int uiBytes)
{
    assert(bMalloc);
    assert(uiBytes % sizeof(ElementType) == 0);
    m_pArray = (ElementType*)realloc(m_pArray, uiBytes);
    #ifdef _DEBUG
        m_uiSize = uiBytes / sizeof(ElementType);
    #endif
}
//------------------------------------------------------------------------------
template<typename _ElementType, bool bMalloc>
typename CArray<_ElementType, bMalloc>::ElementType& 
CArray<_ElementType, bMalloc>::operator[](unsigned int uiIndex) const
{
    #ifdef _DEBUG
        assert(uiIndex < m_uiSize);
    #endif
    return m_pArray[uiIndex];
};
//------------------------------------------------------------------------------
template<typename _ElementType, bool bMalloc>
CArray<_ElementType, bMalloc>::operator bool() const
{
    return (m_pArray != NULL);
}
//------------------------------------------------------------------------------
template<typename _ElementType, bool bMalloc>
CArray<_ElementType, bMalloc> 
CArray<_ElementType, bMalloc>::operator+(int i)
{
    CArray<_ElementType, bMalloc> ret;
    #ifdef _DEBUG
        assert(i <= (int)m_uiSize);
        ret.Assign(m_pArray + i, m_uiSize - i);
    #else
        ret.Assign(m_pArray + i, 0);
    #endif
    return ret;
}
//------------------------------------------------------------------------------
template<typename _ElementType, bool bMalloc>
void 
CArray<_ElementType, bMalloc>::memset(int iValue, unsigned int uiBytes)
{
    #ifdef _DEBUG
        assert(uiBytes <= m_uiSize * sizeof(ElementType));
    #endif
    ::memset(m_pArray, iValue, uiBytes);
}
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

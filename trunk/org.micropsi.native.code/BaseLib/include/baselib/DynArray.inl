//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
CDynArray<_ElementType, uiGrowthExp, bSorted>::CDynArray<_ElementType, uiGrowthExp, bSorted>(const CDynArray<_ElementType, uiGrowthExp, bSorted>& a) :   
    m_pElements(0, 0),
    m_uiSize(0)
{
    assert(uiGrowthExp < 32);
	SetSize(a.Size());
    for (unsigned int i = 0; i < a.m_uiSize; ++i)
        m_pElements[i].m_e = a.m_pElements[i].m_e;
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
void 
CDynArray<_ElementType, uiGrowthExp, bSorted>::IncArraySize()
{
    if ((m_uiSize & ((1 << uiGrowthExp) - 1)) == 0)
        if (m_uiSize == 0)
            m_pElements.Malloc(sizeof(CContainer) << uiGrowthExp);
        else
            m_pElements.ReAlloc(sizeof(CContainer) * (m_uiSize + (1 << uiGrowthExp)));
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
void 
CDynArray<_ElementType, uiGrowthExp, bSorted>::DecArraySize()
{
    if ((m_uiSize & ((1 << uiGrowthExp) - 1)) == 0)
        if (m_uiSize == 0)
            m_pElements.Free();
        else
            m_pElements.ReAlloc(sizeof(CContainer) * m_uiSize);
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
CDynArray<_ElementType, uiGrowthExp, bSorted>::CDynArray() :   
    m_pElements(0, 0),
    m_uiSize(0)
{
    assert(uiGrowthExp < 32);
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
CDynArray<_ElementType, uiGrowthExp, bSorted>::~CDynArray()
{
    SetSize(0);
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
typename CDynArray<_ElementType, uiGrowthExp, bSorted>::ElementType& 
CDynArray<_ElementType, uiGrowthExp, bSorted>::operator[](unsigned int uiIndex) const
{
    assert(uiIndex < m_uiSize);
    return m_pElements[uiIndex].m_e;
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
const typename CDynArray<_ElementType, uiGrowthExp, bSorted>::ElementType& 
CDynArray<_ElementType, uiGrowthExp, bSorted>::operator()(unsigned int uiIndex) const
{
    assert(uiIndex < m_uiSize);
    return m_pElements[uiIndex].m_e;
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
unsigned int 
CDynArray<_ElementType, uiGrowthExp, bSorted>::PushEntry()
{
    (*this)++;
    return m_uiSize - 1;
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
typename CDynArray<_ElementType, uiGrowthExp, bSorted>::ElementType& 
CDynArray<_ElementType, uiGrowthExp, bSorted>::Push()
{
    (*this)++;
    return m_pElements[m_uiSize - 1].m_e;
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
unsigned int
CDynArray<_ElementType, uiGrowthExp, bSorted>::PushEntry(const ElementType& e)
{
    IncArraySize();
    m_pElements[m_uiSize++].CContainer::CContainer(e);
    return m_uiSize - 1;
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
void 
CDynArray<_ElementType, uiGrowthExp, bSorted>::PopEntry()
{
    (*this)--;
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
void 
CDynArray<_ElementType, uiGrowthExp, bSorted>::DeleteEntry(unsigned int uiIndex)
{
    assert(uiIndex < m_uiSize);
    if (!bSorted)
        m_pElements[uiIndex].m_e = m_pElements[m_uiSize - 1].m_e;
    else
        for (unsigned int i = uiIndex; i < m_uiSize - 1; i++)
            m_pElements[i].m_e = m_pElements[i + 1].m_e;

    (*this)--;
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
void
CDynArray<_ElementType, uiGrowthExp, bSorted>::MoveEntry(unsigned int uiIndexNow, unsigned int uiIndexThen)
{
    assert(uiIndexNow < m_uiSize);
	assert(uiIndexThen < m_uiSize);
	if(uiIndexNow==uiIndexThen)
	{
		return;
	}
	if(!bSorted)
	{
		_ElementType xSwap = m_pElements[uiIndexThen].m_e;
		m_pElements[uiIndexThen].m_e = m_pElements[uiIndexNow].m_e;
		m_pElements[uiIndexNow].m_e = xSwap; 
	}
	else
	{
		_ElementType xSwap = m_pElements[uiIndexNow].m_e;
		if(uiIndexNow < uiIndexThen)
		{
	        for (unsigned int i = uiIndexNow; i < uiIndexThen; ++i)
	            m_pElements[i].m_e = m_pElements[i + 1].m_e;
		}
		else
		{
	        for (unsigned int i = uiIndexNow; i > uiIndexThen; --i)
	            m_pElements[i].m_e = m_pElements[i - 1].m_e;
		}
		m_pElements[uiIndexThen].m_e = xSwap;
	}
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
unsigned int 
CDynArray<_ElementType, uiGrowthExp, bSorted>::TopOfStack() const
{
    return m_uiSize - 1;
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
unsigned int 
CDynArray<_ElementType, uiGrowthExp, bSorted>::Size() const
{
    return m_uiSize;
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
void 
CDynArray<_ElementType, uiGrowthExp, bSorted>::SetSize(unsigned int uiNewSize)
{
    if (uiNewSize == m_uiSize) return;

    const unsigned int uiSizeAllocated =     (m_uiSize + (1 << uiGrowthExp) - 1) & ~((1 << uiGrowthExp) - 1);
    const unsigned int uiNewSizeAllocated = (uiNewSize + (1 << uiGrowthExp) - 1) & ~((1 << uiGrowthExp) - 1);

    if (uiNewSize > m_uiSize)
    {
        // allocate
        if (uiSizeAllocated != uiNewSizeAllocated)
            if (m_uiSize == 0)
                m_pElements.Malloc(sizeof(CContainer) * uiNewSizeAllocated);
            else
                m_pElements.ReAlloc(sizeof(CContainer) * uiNewSizeAllocated);

        // construction
        for (unsigned int uiIdx = m_uiSize; uiIdx < uiNewSize; uiIdx++)
            m_pElements[uiIdx].CContainer::CContainer();
    }
    else if (uiNewSize < m_uiSize)
    {
        // destruction
        for (int uiIdx = m_uiSize - 1; uiIdx >= (int)uiNewSize; uiIdx--)
            m_pElements[uiIdx].CContainer::~CContainer();

        // free
        if (uiSizeAllocated != uiNewSizeAllocated)
            if (uiNewSize == 0)
                m_pElements.Free();
            else
                m_pElements.ReAlloc(sizeof(CContainer) * uiNewSizeAllocated);
    }


    // copy size
    m_uiSize = uiNewSize;
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
const CDynArray<_ElementType, uiGrowthExp, bSorted>& 
CDynArray<_ElementType, uiGrowthExp, bSorted>::operator++(int)
{
    IncArraySize();
    m_pElements[m_uiSize++].CContainer::CContainer();
    return *this;
};
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
const CDynArray<_ElementType, uiGrowthExp, bSorted>& 
CDynArray<_ElementType, uiGrowthExp, bSorted>::operator--(int)
{
    assert(m_uiSize != 0);
    m_pElements[--m_uiSize].CContainer::~CContainer();
    DecArraySize();
    return *this;
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
void 
CDynArray<_ElementType, uiGrowthExp, bSorted>::Clear()
{
    SetSize(0);
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
int 
CDynArray<_ElementType, uiGrowthExp, bSorted>::Find(const ElementType& e)
{
    for (int iIdx = 0; iIdx < (int)m_uiSize; iIdx++)
    {
        if (m_pElements[iIdx].m_e == e) return iIdx;
    }

    return -1;
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
int 
CDynArray<_ElementType, uiGrowthExp, bSorted>::Include(const ElementType& e)
{
	int iIdx = Find(e);
	if (iIdx < 0)
	{
		return PushEntry(e);
	}
	else
	{
		return iIdx;
	}
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
void 
CDynArray<_ElementType, uiGrowthExp, bSorted>::RemoveEntry(const ElementType& e)
{
    int iIdx = Find(e);
    assert(iIdx != -1);

    DeleteEntry(iIdx);
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
void 
CDynArray<_ElementType, uiGrowthExp, bSorted>::RemoveDuplicates()
{
    for (int iIdx = (int)m_uiSize - 1; iIdx >= 0; iIdx--)
    {
        int iSearchIdx = iIdx - 1;
        while (iSearchIdx >= 0)
        {
            if (m_pElements[iSearchIdx].m_e == m_pElements[iIdx].m_e)
            {
                DeleteEntry(iIdx);
                break;
            }

            iSearchIdx--;
        }
    }
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
void 
CDynArray<_ElementType, uiGrowthExp, bSorted>::Sort()
{
    // TODO: Implementation eines Sortierverfahrens fürs DynArray
    assert(false);
}
//------------------------------------------------------------------------------
template<typename _ElementType, unsigned int uiGrowthExp, bool bSorted>
typename CDynArray<_ElementType, uiGrowthExp, bSorted>::ElementType* 
CDynArray<_ElementType, uiGrowthExp, bSorted>::GetArrayPointer()
{
	return &(m_pElements.m_pArray->m_e);
}
//------------------------------------------------------------------------------

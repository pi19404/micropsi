
//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
unsigned int	
CHandledSet<_ElementType, uiGrowthExp>::Size() const		
{ 
	return m_axElements.Size() -  m_aiEmptySlots.Size();
}

//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
CHandledSet<_ElementType, uiGrowthExp>::CHandledSet(
    CreateElementFunction p_fpCreateElement, 
    DestroyElementFunction p_fpDestroyElement)
:   m_fpCreateElement(p_fpCreateElement),
    m_fpDestroyElement(p_fpDestroyElement)
{
}
	

    
//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
CHandledSet<_ElementType, uiGrowthExp>::~CHandledSet()
{
	for(unsigned int i=0; i<m_axElements.Size(); ++i)
	{
		if(m_axElements[i].m_pxElementPtr)
		{
            m_fpDestroyElement(m_axElements[i].m_pxElementPtr);
		}
	}
}


//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
void	
CHandledSet<_ElementType, uiGrowthExp>::StartIterate(unsigned long& p_iIterator) const
{
	p_iIterator = 0;
}


//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
bool	
CHandledSet<_ElementType, uiGrowthExp>::Iterate(unsigned long& p_iIterator, _ElementType& po_rxElement) const
{
	while(p_iIterator < m_axElements.Size()  &&  m_axElements[p_iIterator].m_pxElementPtr == 0)
	{
		p_iIterator++;
	}
	if(p_iIterator >= m_axElements.Size())
	{
		return false;
	}
	po_rxElement = *(m_axElements[p_iIterator].m_pxElementPtr); 
	p_iIterator++;
	return true;
}


//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
bool	
CHandledSet<_ElementType, uiGrowthExp>::Iterate(unsigned long& p_iIterator, _ElementType*& po_rpxElement) const
{
	while(p_iIterator < m_axElements.Size()  &&  m_axElements[p_iIterator].m_pxElementPtr == 0)
	{
		p_iIterator++;
	}
	if(p_iIterator >= m_axElements.Size())
	{
		po_rpxElement = 0;
		return false;
	}
	po_rpxElement = m_axElements[p_iIterator].m_pxElementPtr; 
	p_iIterator++;
	return true;
}

//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
int
CHandledSet<_ElementType, uiGrowthExp>::GetFreeSlotIdx()
{
	int iSlotIdx;
	if(m_aiEmptySlots.Size() > 0)
	{
		// leerer Slot im Array vorhanden --> benutzen
		iSlotIdx = m_aiEmptySlots[m_aiEmptySlots.TopOfStack()];
		m_aiEmptySlots.PopEntry();
		assert(m_axElements[iSlotIdx].m_pxElementPtr == 0);
	}
	else
	{
		// kein leerer Slot vorhanden --> Array vergrößern
		iSlotIdx = m_axElements.PushEntry();
	}

    return iSlotIdx;
}
//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
unsigned long	
CHandledSet<_ElementType, uiGrowthExp>::PushEntry()
{
	int iSlotIdx = GetFreeSlotIdx();
	
    m_axElements[iSlotIdx].m_pxElementPtr = m_fpCreateElement();
	THandle h;
	h.m_uiIndex		 = (unsigned short) iSlotIdx;
	h.m_uiGeneration = m_axElements[iSlotIdx].m_uiGeneration;
	return h.m_iHandle;
}


//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
unsigned long	
CHandledSet<_ElementType, uiGrowthExp>::PushEntry(const _ElementType& p_rxElement)
{
    int iSlotIdx = GetFreeSlotIdx();
	
    m_axElements[iSlotIdx].m_pxElementPtr = m_fpCreateElement();
	*(m_axElements[iSlotIdx].m_pxElementPtr) = p_rxElement;
	THandle h;
	h.m_uiIndex		 = (unsigned short) iSlotIdx;
	h.m_uiGeneration = m_axElements[iSlotIdx].m_uiGeneration;
	return h.m_iHandle;
}


//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
bool			
CHandledSet<_ElementType, uiGrowthExp>::DeleteEntry(unsigned long p_uiHandle)
{
	if(!IsValid(p_uiHandle))		{ return false; }
	THandle h;
	h.m_iHandle = p_uiHandle;
	unsigned long iIdx = (unsigned long) h.m_uiIndex;
	m_fpDestroyElement(m_axElements[iIdx].m_pxElementPtr);
	m_axElements[iIdx].m_pxElementPtr = 0;
	m_axElements[iIdx].m_uiGeneration++;
	m_aiEmptySlots.PushEntry(iIdx);

	return true;
}


//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
void			
CHandledSet<_ElementType, uiGrowthExp>::Clear()
{
	m_aiEmptySlots.SetSize(m_axElements.Size());
	for(unsigned int i=0; i<m_axElements.Size(); ++i)
	{
		if(m_axElements[i].m_pxElementPtr)
		{
			m_fpDestroyElement(m_axElements[i].m_pxElementPtr);
			m_axElements[i].m_pxElementPtr = 0;
			m_axElements[i].m_uiGeneration++;
		}
		m_aiEmptySlots[i] = i;
	}
}


//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
bool			
CHandledSet<_ElementType, uiGrowthExp>::IsValid(unsigned long p_uiHandle) const
{
	THandle h;
	h.m_iHandle = p_uiHandle;
	if(h.m_uiIndex > m_axElements.Size())  { return false; }
	if(h.m_uiGeneration != m_axElements[(unsigned long) (h.m_uiIndex)].m_uiGeneration) { return false; }
	assert(m_axElements[(unsigned long) (h.m_uiIndex)].m_pxElementPtr != 0);
	return true;
}


//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
_ElementType&	
CHandledSet<_ElementType, uiGrowthExp>::Element(unsigned long p_uiHandle) const
{
	assert(IsValid(p_uiHandle));

	THandle h;
	h.m_iHandle = p_uiHandle;
	return *(m_axElements[(unsigned long) h.m_uiIndex].m_pxElementPtr);
}

//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
_ElementType*	
CHandledSet<_ElementType, uiGrowthExp>::ElementPtr(unsigned long p_uiHandle) const
{
	if(!IsValid(p_uiHandle))		{ return 0; }
	THandle h;
	h.m_iHandle = p_uiHandle;
	return m_axElements[(unsigned long) h.m_uiIndex].m_pxElementPtr;
}



//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
bool			
CHandledSet<_ElementType, uiGrowthExp>::SetElement(unsigned long p_uiHandle, const _ElementType& p_krxElement) const
{
	_ElementType* p = ElementPtr(p_uiHandle);
	if(!p)		{ return false; }
	*p = p_krxElement;
	return true;
}


//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
_ElementType*
__cdecl CHandledSet<_ElementType, uiGrowthExp>::StdCreateElement()
{
    return new _ElementType;
}


//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
void 
__cdecl CHandledSet<_ElementType, uiGrowthExp>::StdDestroyElement(_ElementType* pElement)
{
    delete pElement;
}

//------------------------------------------------------------------------------
template <class _ElementType, unsigned int uiGrowthExp>
unsigned long
CHandledSet<_ElementType, uiGrowthExp>::InvalidHandle()
{
	return 0xFFFFFFFF;
}
//------------------------------------------------------------------------------


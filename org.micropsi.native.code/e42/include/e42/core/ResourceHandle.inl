//-----------------------------------------------------------------------------------------------------------------------------------------
template<typename TResourcePtr>
CResourceHandle<TResourcePtr>::CResourceHandle()
:   m_pxResourceProxy   (NULL)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
template<typename TResourcePtr>
CResourceHandle<TResourcePtr>::CResourceHandle(const CResourceHandle& rxHnd)
:   m_pxResourceProxy   (rxHnd.m_pxResourceProxy)
{
    if (m_pxResourceProxy)
        m_pxResourceProxy->AddRef();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
template<typename TResourcePtr>
CResourceHandle<TResourcePtr>::CResourceHandle(CResourceProxy* pxResourceProxy)
:   m_pxResourceProxy   (pxResourceProxy)
{
    if (m_pxResourceProxy)
        m_pxResourceProxy->AddRef();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
template<typename TResourcePtr>
CResourceHandle<TResourcePtr>::~CResourceHandle()
{
    if (m_pxResourceProxy)
        m_pxResourceProxy->Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
template<typename TResourcePtr>
CResourceHandle<TResourcePtr>& 
CResourceHandle<TResourcePtr>::operator=(const CResourceHandle& rxHnd)
{
    if (m_pxResourceProxy != rxHnd.m_pxResourceProxy)
    {
        if (m_pxResourceProxy)
            m_pxResourceProxy->Release();

        m_pxResourceProxy = rxHnd.m_pxResourceProxy;

        if (m_pxResourceProxy)
            m_pxResourceProxy->AddRef();
    }

    return *this;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
template<typename TResourcePtr>
void 
CResourceHandle<TResourcePtr>::Release()
{
    if (m_pxResourceProxy)
    {
        m_pxResourceProxy->Release();
        m_pxResourceProxy = NULL;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
template<typename TResourcePtr>
TResourcePtr
CResourceHandle<TResourcePtr>::GetPtr() const
{
    if (m_pxResourceProxy)
    {
        return (TResourcePtr)(m_pxResourceProxy->GetResource());
    }
    else
    {
        return NULL;
    }
}
//-----------------------------------------------------------------------------------------------------------------------------------------
template<typename TResourcePtr>
TResourcePtr
CResourceHandle<TResourcePtr>::operator->() const
{
    assert(m_pxResourceProxy);
    return (TResourcePtr)(m_pxResourceProxy->GetResource());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
template<typename TResourcePtr>
CResourceHandle<TResourcePtr>::operator bool() const
{
    return (m_pxResourceProxy != NULL);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
template<typename TResourcePtr>
bool 
CResourceHandle<TResourcePtr>::operator==(const CResourceHandle& hndResource) const
{
    return m_pxResourceProxy == hndResource.m_pxResourceProxy;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
template<typename TResourcePtr>
bool 
CResourceHandle<TResourcePtr>::operator!=(const CResourceHandle& hndResource) const
{
    return m_pxResourceProxy != hndResource.m_pxResourceProxy;
}
//-----------------------------------------------------------------------------------------------------------------------------------------

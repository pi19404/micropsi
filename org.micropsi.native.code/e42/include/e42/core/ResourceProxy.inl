//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CResourceProxy::CResourceProxy(void* pxResource, CResourceFactory* pxResourceFactory)
:   m_iReferenceCount                   (0),
    m_pxResource                        (pxResource),
    m_pxResourceFactory                 (pxResourceFactory)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
CResourceProxy::~CResourceProxy()
{
    assert(m_iReferenceCount == 0);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void* 
CResourceProxy::GetResource() const
{
    return m_pxResource;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
int 
CResourceProxy::GetRefCount() const
{
    return m_iReferenceCount;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CResourceProxy::AddRef()
{
    assert(m_pxResource);
    ++m_iReferenceCount;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
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

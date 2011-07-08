//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
CComObjectPtr<Type>::CComObjectPtr() 
:   m_pxComObject(NULL)
{
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
CComObjectPtr<Type>::CComObjectPtr(const CComObjectPtr<Type>& p_sprxOther)
{
    m_pxComObject = p_sprxOther.m_pxComObject;
    if(m_pxComObject)
    {
        m_pxComObject->AddRef();
    }
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
CComObjectPtr<Type>::CComObjectPtr(Type* p_pxOther)
{
    m_pxComObject = p_pxOther;
    if(m_pxComObject)
    {
        m_pxComObject->AddRef();
    }
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
CComObjectPtr<Type>::~CComObjectPtr()
{
    if(m_pxComObject)
    {
        m_pxComObject->Release();
    }
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
CComObjectPtr<Type>& 
CComObjectPtr<Type>::operator=(const CComObjectPtr<Type>& p_sprxOther)
{
    Type* pxObj = p_sprxOther.m_pxComObject;
    if(pxObj)
    {
        pxObj->AddRef();
    }
    if(m_pxComObject)
    {
        m_pxComObject->Release();
    }
    m_pxComObject = pxObj;
    return *this;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
CComObjectPtr<Type>& 
CComObjectPtr<Type>::operator=(Type* p_pxOther)
{
    if(p_pxOther)
    {
        p_pxOther->AddRef();
    }
    if(m_pxComObject)
    {
        m_pxComObject->Release();
    }
    m_pxComObject = p_pxOther;
    return *this;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
bool                 
CComObjectPtr<Type>::operator==(const CComObjectPtr<Type>& p_sprxOther) const
{
    return m_pxComObject == p_sprxOther.m_pxComObject;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
bool                 
CComObjectPtr<Type>::operator!=(const CComObjectPtr<Type>& p_sprxOther) const
{
    return m_pxComObject != p_sprxOther.m_pxComObject;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
bool                 
CComObjectPtr<Type>::operator==(Type* p_pxOther) const
{
    return m_pxComObject == p_pxOther;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
bool                 
CComObjectPtr<Type>::operator!=(Type* p_pxOther) const
{
    return m_pxComObject != p_pxOther;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
bool                 
CComObjectPtr<Type>::IsNull() const
{
    return m_pxComObject == 0;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
Type*                
CComObjectPtr<Type>::operator->() const
{
    assert(m_pxComObject);
    return m_pxComObject;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
Type&                
CComObjectPtr<Type>::operator*()
{
    assert(m_pxComObject);
    return *m_pxComObject;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
CComObjectPtr<Type>::operator Type*() const
{
    return m_pxComObject;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
Type** 
CComObjectPtr<Type>::operator&()
{
    if (m_pxComObject)
    {
        int iNewRefCount =
            m_pxComObject->Release();
        m_pxComObject = NULL;
    }
    return &m_pxComObject;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename Type>
const Type** 
CComObjectPtr<Type>::operator&() const
{
    return &m_pxComObject;
}
//---------------------------------------------------------------------------------------------------------------------

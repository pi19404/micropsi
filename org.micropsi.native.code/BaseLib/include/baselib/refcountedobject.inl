//------------------------------------------------------------------------------
inline
void
CRefCountedObject::AddRef()
{
	m_iRefCount++;
}
//------------------------------------------------------------------------------
inline
void
CRefCountedObject::Release()
{
	if (--m_iRefCount == 0)
	{
		Destroy();
	}
}
//------------------------------------------------------------------------------
inline
CRefCountedObject::CRefCountedObject()
:   m_iRefCount(0)
{
}
//------------------------------------------------------------------------------
inline
CRefCountedObject::~CRefCountedObject()
{
	assert(m_iRefCount == 0);   // anti   delete spxRefCountedObj;
}
//------------------------------------------------------------------------------
inline
CRefCountedObject::CRefCountedObject(const CRefCountedObject& rco)
:   m_iRefCount(0)
{
	// hier wird nichts kopiert, da diese Basisklasse noch keine Daten hat
}
//------------------------------------------------------------------------------
inline
CRefCountedObject& CRefCountedObject::operator=(const CRefCountedObject& rco)
{
	// hier wird nichts kopiert, da diese Basisklasse noch keine Daten hat
	// der ReferenceCount bleibt unverändert
	return *this;
}
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
inline
void 
CSmartPointerBase::AddRef() const
{
	if (m_pObject) 
		m_pObject->AddRef();
}
//------------------------------------------------------------------------------
inline
void 
CSmartPointerBase::Release() const
{
	if (m_pObject)
		m_pObject->Release();
}
//------------------------------------------------------------------------------
inline
CSmartPointerBase::CSmartPointerBase()
:	m_pObject(0)
{
}
//------------------------------------------------------------------------------
inline
CSmartPointerBase::CSmartPointerBase(CRefCountedObject* pObject)
:	m_pObject(pObject)
{
	AddRef();
}
//------------------------------------------------------------------------------
inline
CSmartPointerBase::~CSmartPointerBase()
{
	Release();
}
//------------------------------------------------------------------------------
inline
CSmartPointerBase::operator bool() const
{
	return (m_pObject != 0);
}
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
template<class T>
CSmartPointer<T>::CSmartPointer<T>(const T* pObject)
:   CSmartPointerBase((CRefCountedObject*)pObject)
{
}
//------------------------------------------------------------------------------
template<class T>
CSmartPointer<T>::CSmartPointer<T>(const CSmartPointer& sp)
:   CSmartPointerBase(sp.m_pObject)
{
}
//------------------------------------------------------------------------------
template<class T>
CSmartPointer<T>::CSmartPointer<T>(const int i)
{
	assert(i == 0);
}
//------------------------------------------------------------------------------
template<class T>
CSmartPointer<T>&
CSmartPointer<T>::operator=(const T* pObject)
{
	if (m_pObject != pObject)
	{
		Release();
		m_pObject = (CRefCountedObject*)pObject;
		AddRef();
	}
	return *this;
}
//------------------------------------------------------------------------------
template<class T>
CSmartPointer<T>&
CSmartPointer<T>::operator=(const CSmartPointer& sp)
{
	sp.AddRef();
	Release();
	m_pObject = sp.m_pObject;
	return *this;
}
//------------------------------------------------------------------------------
template<class T>
CSmartPointer<T>&
CSmartPointer<T>::operator=(const int i)
{
	assert(i == 0);
	Release();
	m_pObject = 0;
	return *this;
}
//------------------------------------------------------------------------------
template<class T>
void 
CSmartPointer<T>::Create()
{
	Release();
	m_pObject = T::Create();
	AddRef();
}
//------------------------------------------------------------------------------
template<class T>
T* 
CSmartPointer<T>::operator->() const
{
	assert(m_pObject);
	return (T*)m_pObject;
}
//------------------------------------------------------------------------------
template<class T>
T&
CSmartPointer<T>::operator*() const
{
	assert(m_pObject);
	return *(T*)m_pObject;
}
//------------------------------------------------------------------------------

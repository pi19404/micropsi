//---------------------------------------------------------------------------------------------------------------------
/// Cast-Operator mit automatic type discovery (zum Auslesen)
template<typename T>
CVariant::operator T()
{
	assert(typeid(T) == m_pxType->GetTypeId());
	return *(T*)GetValuePointer();
}
//---------------------------------------------------------------------------------------------------------------------
/// Zuweisungs-Operator mit automatic type discovery (zum Schreiben)
template<typename T>
CVariant& 
CVariant::operator=(const T& rxSource)
{
	assert(typeid(T) == m_pxType->GetTypeId());
	*(T*)GetValuePointer() = rxSource;
	m_pxType->Validate(GetValuePointer());

	return *this;
}
//---------------------------------------------------------------------------------------------------------------------

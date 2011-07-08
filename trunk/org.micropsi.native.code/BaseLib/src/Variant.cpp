#include "stdafx.h"

#include "baselib/Variant.h"


//---------------------------------------------------------------------------------------------------------------------
void* 
CVariant::GetValuePointer()
{
	return 
		m_pxType->GetTypeSize() <= sizeof(m_xValue) 
			? &m_xValue : m_xValue.p;
}
//---------------------------------------------------------------------------------------------------------------------
const void* 
CVariant::GetValuePointer() const
{
	return 
		m_pxType->GetTypeSize() <= sizeof(m_xValue) 
			? &m_xValue : m_xValue.p;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CVariant::Allocate()
{
	if (m_pxType->GetTypeSize() > sizeof(m_xValue))
	{
		m_xValue.p = malloc(m_pxType->GetTypeSize());
	}
}
//---------------------------------------------------------------------------------------------------------------------
void 
CVariant::Deallocate()
{
	if (m_xValue.p != NULL && 
		m_pxType->GetTypeSize() > sizeof(m_xValue))
	{
		free(m_xValue.p);
		m_xValue.p = NULL;
	}
}
//---------------------------------------------------------------------------------------------------------------------
const CVariantType* 
CVariant::GetDesc() const
{
	return m_pxType;
}
//---------------------------------------------------------------------------------------------------------------------
CVariant::CVariant(CVariantType* pxType)
:	m_pxType(pxType)
{
	m_xValue.p = NULL;
	Allocate();
	m_pxType->Construct(GetValuePointer());
}
//---------------------------------------------------------------------------------------------------------------------
CVariant::~CVariant()
{
	m_pxType->Destruct(GetValuePointer());
	Deallocate();
}
//---------------------------------------------------------------------------------------------------------------------
CVariant::CVariant(const CVariant& rxSource)
{
	CVariant::CVariant(rxSource.m_pxType);
	*this = rxSource;
}
//---------------------------------------------------------------------------------------------------------------------
/// Wertzuweisung (ohne Typwechsel)
CVariant& 
CVariant::operator=(const CVariant& rxSource)
{
	assert(m_pxType == rxSource.m_pxType);
	m_pxType->Assign(GetValuePointer(), rxSource.GetValuePointer());
	return *this;
}
//---------------------------------------------------------------------------------------------------------------------
bool 
CVariant::SaveToString(std::string* psStringOut) const
{
	return m_pxType->SaveToString(GetValuePointer(), psStringOut);
}
//---------------------------------------------------------------------------------------------------------------------
bool 
CVariant::LoadFromString(const std::string* psString)
{
	bool bSuccess = m_pxType->LoadFromString(GetValuePointer(), psString);

	if (bSuccess && psString)
	{
		m_pxType->Validate(GetValuePointer());
	}

	return bSuccess;
}
//---------------------------------------------------------------------------------------------------------------------
/// Zuweisung mit Typwechsel
void 
CVariant::ReAssign(const CVariant& rxSource)
{
	if (m_pxType != rxSource.m_pxType)
	{
		bool bSizeChange = m_pxType->GetTypeSize() != rxSource.m_pxType->GetTypeSize();

		m_pxType->Destruct(GetValuePointer());
		if (bSizeChange) Deallocate();

		m_pxType = rxSource.m_pxType;

		if (bSizeChange) Allocate();
		m_pxType->Construct(GetValuePointer());
	}

	m_pxType->Assign(GetValuePointer(), rxSource.GetValuePointer());
}
//---------------------------------------------------------------------------------------------------------------------

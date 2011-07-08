#include "stdafx.h"

#include "baselib/VariantType.h"
#include "baselib/macros.h"

//---------------------------------------------------------------------------------------------------------------------
void 
CVariantType::Validate(void* pValue) const
{
}
//---------------------------------------------------------------------------------------------------------------------
size_t
CVariantType::GetTypeSize() const
{
	return m_Size;
}
//---------------------------------------------------------------------------------------------------------------------
const type_info& 
CVariantType::GetTypeId() const 
{
	return *m_pType;
}
//---------------------------------------------------------------------------------------------------------------------
const std::string& 
CVariantType::GetName() const
{
	return m_sName;
}
//---------------------------------------------------------------------------------------------------------------------
const std::string& 
CVariantType::GetDescription() const
{
	return m_sDesciptionText;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CVariantType::SaveToString(const void* pValue, std::string* psStringOut) const
{
	return false;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CVariantType::LoadFromString(void* pValue, const std::string* psString) const
{
	return false;
}
//---------------------------------------------------------------------------------------------------------------------
CVariantType::CVariantType(const std::string sName, const std::string sDescriptionText, const type_info* pType, size_t stSize)
:	m_sName(sName),
	m_sDesciptionText(sDescriptionText),
	m_pType(pType),
	m_Size(stSize)
{
}
//---------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------
void 
CVariantTypeInt::Construct(void* pValue) const 
{
	*(int*)pValue = m_iDefault;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CVariantTypeInt::Destruct(void* pValue) const 
{
}
//---------------------------------------------------------------------------------------------------------------------
void 
CVariantTypeInt::Validate(void* pValue) const 
{
	*(int*)pValue = clamp(*(int*)pValue, m_iMin, m_iMax);	
}
//---------------------------------------------------------------------------------------------------------------------
void 
CVariantTypeInt::Assign(void* pDest, const void* pSource) const
{
	*(int*)pDest = *(int*)pSource;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CVariantTypeInt::SaveToString(const void* pValue, std::string* psStringOut) const
{
	if (psStringOut)
	{
		*psStringOut = *(int*)pValue;
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CVariantTypeInt::LoadFromString(void* pValue, const std::string* psString) const
{
	if (psString)
	{
		if (sscanf(psString->c_str(), "%i", pValue) != 1)
		{
			assert(false);
			return false;
		}
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------
CVariantTypeInt::CVariantTypeInt(const std::string sName, const std::string sDescriptionText, int iMin, int iMax, int iDefault)
:	CVariantType(sName, sDescriptionText, &typeid(int), sizeof(int)),
	m_iMin(iMin),
	m_iMax(iMax),
	m_iDefault(iDefault)
{
	assert(iMin <= iDefault  &&  iMax >= iDefault);
}
//---------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------
void 
CVariantTypeFloat::Construct(void* pValue) const 
{
	*(float*)pValue = m_fDefault;
}
//---------------------------------------------------------------------------------------------------------------------
void
CVariantTypeFloat::Destruct(void* pValue) const 
{
}
//---------------------------------------------------------------------------------------------------------------------
void 
CVariantTypeFloat::Validate(void* pValue) const 
{
	*(float*)pValue = clamp(*(float*)pValue, m_fMin, m_fMax);	
}
//---------------------------------------------------------------------------------------------------------------------
void 
CVariantTypeFloat::Assign(void* pDest, const void* pSource) const
{
	*(float*)pDest = *(float*)pSource;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CVariantTypeFloat::SaveToString(const void* pValue, std::string* psStringOut) const
{
	if (psStringOut)
	{
		char acBuffer[16];
		sprintf(acBuffer, "%g", pValue);
		*psStringOut = acBuffer;
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CVariantTypeFloat::LoadFromString(void* pValue, const std::string* psString) const
{
	if (psString)
	{
		if (sscanf(psString->c_str(), "%f", pValue) != 1)
		{
			assert(false);
			return false;
		}
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------
CVariantTypeFloat::CVariantTypeFloat(const std::string sName, const std::string sDescriptionText, float fMin, float fMax, float fDefault)
:	CVariantType(sName, sDescriptionText, &typeid(float), sizeof(float)),
	m_fMin(fMin),
	m_fMax(fMax),
	m_fDefault(fDefault)
{
	assert(fMin <= fDefault  &&  fMax >= fDefault);
}
//---------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------
void 
CVariantTypeVec3::Construct(void* pValue) const 
{
	*(CVec3*)pValue = m_vDefault;
}
//---------------------------------------------------------------------------------------------------------------------
void
CVariantTypeVec3::Destruct(void* pValue) const 
{
}
//---------------------------------------------------------------------------------------------------------------------
void 
CVariantTypeVec3::Validate(void* pValue) const 
{
}
//---------------------------------------------------------------------------------------------------------------------
void 
CVariantTypeVec3::Assign(void* pDest, const void* pSource) const
{
	*(CVec3*)pDest = *(CVec3*)pSource;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CVariantTypeVec3::SaveToString(const void* pValue, std::string* psStringOut) const
{
	if (psStringOut)
	{
		const CVec3& rvValue = *(const CVec3*)pValue;

		char acBuffer[64];
		sprintf(acBuffer, "(%g, %g, %g)", rvValue.x(), rvValue.y(), rvValue.z());
		*psStringOut = acBuffer;
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CVariantTypeVec3::LoadFromString(void* pValue, const std::string* psString) const
{
	if (psString)
	{
		CVec3& rvValue = *(CVec3*)pValue;

		if (sscanf(psString->c_str(), "(%f, %f, %f)", &rvValue.x(), &rvValue.y(), &rvValue.z()) != 3)
		{
			assert(false);
			return false;
		}
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------
CVariantTypeVec3::CVariantTypeVec3(const std::string sName, const std::string sDescriptionText, CVec3 vDefault)
:	CVariantType(sName, sDescriptionText, &typeid(CVec3), sizeof(CVec3)),
	m_vDefault(vDefault)
{
}
//---------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------
void
CVariantTypeString::Construct(void* pValue) const 
{
	((std::string*)pValue)->std::string::string(m_sDefault);
}
//---------------------------------------------------------------------------------------------------------------------
void
CVariantTypeString::Destruct(void* pValue) const 
{
	((std::string*)pValue)->std::string::~string();
}
//---------------------------------------------------------------------------------------------------------------------
void 
CVariantTypeString::Validate(void* pValue) const 
{
}
//---------------------------------------------------------------------------------------------------------------------
void 
CVariantTypeString::Assign(void* pDest, const void* pSource) const
{
	*(std::string*)pDest = *(std::string*)pSource;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CVariantTypeString::SaveToString(const void* pValue, std::string* psStringOut) const
{
	if (psStringOut)
	{
		*psStringOut = *(const std::string*)pValue;
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CVariantTypeString::LoadFromString(void* pValue, const std::string* psString) const
{
	if (psString)
	{
		*(std::string*)pValue = *psString;
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------
CVariantTypeString::CVariantTypeString(const std::string sName, const std::string sDescriptionText, std::string sDefault)
:	CVariantType(sName, sDescriptionText, &typeid(std::string), sizeof(std::string)),
	m_sDefault(sDefault)
{
}
//---------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------

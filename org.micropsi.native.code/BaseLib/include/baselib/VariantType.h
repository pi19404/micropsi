#ifndef VARIANTTYPE_H_INCLUDED
#define VARIANTTYPE_H_INCLUDED

#include <string>
#include <vector>
#include <float.h>
#include "geometry/CVector.h"

//---------------------------------------------------------------------------------------------------------------------
class CVariantType
{
	friend class CVariant;


protected:

	virtual void Construct(void* pValue) const = 0;
	virtual void Destruct(void* pValue) const = 0;
	virtual void Validate(void* pValue) const;
	virtual void Assign(void* pDest, const void* pSource) const = 0;


	virtual bool SaveToString(const void* pValue, std::string* psStringOut) const;
	virtual bool LoadFromString(void* pValue, const std::string* psString) const;


	std::string				m_sName;
	const type_info*		m_pType;
	size_t					m_Size;
	std::string				m_sDesciptionText;


public:

	size_t GetTypeSize() const;
	const type_info& GetTypeId() const;
	const std::string& GetName() const;
	const std::string& GetDescription() const;

	CVariantType(const std::string sName, const std::string sDescriptionText, const type_info* pType, size_t stSize);
};
//---------------------------------------------------------------------------------------------------------------------

class CVariantTypeInt : public CVariantType
{
private:

	int m_iMin;
	int m_iMax;
	int m_iDefault;

	virtual void Construct(void* pValue) const;
	virtual void Destruct(void* pValue) const;
	virtual void Validate(void* pValue) const;
	virtual void Assign(void* pDest, const void* pSource) const;


	virtual bool SaveToString(const void* pValue, std::string* psStringOut) const;
	virtual bool LoadFromString(void* pValue, const std::string* psString) const;


public:

	CVariantTypeInt(const std::string sName, const std::string sDescriptionText = std::string(), int iMin = INT_MIN, int iMax = INT_MAX, int iDefault = 0);
};
//---------------------------------------------------------------------------------------------------------------------

class CVariantTypeFloat : public CVariantType
{
private:

	float m_fMin;
	float m_fMax;
	float m_fDefault;

	virtual void Construct(void* pValue) const;
	virtual void Destruct(void* pValue) const;
	virtual void Validate(void* pValue) const; 
	virtual void Assign(void* pDest, const void* pSource) const;


	virtual bool SaveToString(const void* pValue, std::string* psStringOut) const;
	virtual bool LoadFromString(void* pValue, const std::string* psString) const;


public:

	CVariantTypeFloat(const std::string sName, const std::string sDescriptionText = std::string(), float fMin = -FLT_MAX, float fMax = FLT_MAX, float fDefault = 0);
};

//---------------------------------------------------------------------------------------------------------------------

class CVariantTypeVec3 : public CVariantType
{
private:

	CVec3 m_vDefault;

	virtual void Construct(void* pValue) const;
	virtual void Destruct(void* pValue) const;
	virtual void Validate(void* pValue) const; 
	virtual void Assign(void* pDest, const void* pSource) const;


	virtual bool SaveToString(const void* pValue, std::string* psStringOut) const;
	virtual bool LoadFromString(void* pValue, const std::string* psString) const;


public:

	CVariantTypeVec3(const std::string sName, const std::string sDescriptionText = std::string(), CVec3 vDefault = CVec3(0, 0, 0));
};

//---------------------------------------------------------------------------------------------------------------------
class CVariantTypeString : public CVariantType
{
private:

	std::string m_sDefault;

	virtual void Construct(void* pValue) const; 
	virtual void Destruct(void* pValue) const; 
	virtual void Validate(void* pValue) const;
	virtual void Assign(void* pDest, const void* pSource) const;


	virtual bool SaveToString(const void* pValue, std::string* psStringOut) const;
	virtual bool LoadFromString(void* pValue, const std::string* psString) const;


public:

	CVariantTypeString(const std::string sName, const std::string sDescriptionText, std::string sDefault);
};
//---------------------------------------------------------------------------------------------------------------------

class CVariantTypeEnum : public CVariantType
{
	std::vector<std::string> m_asPossibleValues;
	int iDefault;
};
//---------------------------------------------------------------------------------------------------------------------


#endif // VARIANTTYPE_H_INCLUDED
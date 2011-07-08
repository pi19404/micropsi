#include "stdafx.h"
#include "GameLib/Utilities/ExtendedConfigFile.h"

#include "BaseLib/str.h"
#include "BaseLib/macros.h"

#include "tinyxml.h"
#include "GameLib/Utilities/xmlutils.h"

//---------------------------------------------------------------------------------------------------------------------

const CFourCC CExtendedConfigFile::ID_VEC3		= CFourCC("VEC3");

//---------------------------------------------------------------------------------------------------------------------
CExtendedConfigFile::CExtendedConfigFile()
{
}

//---------------------------------------------------------------------------------------------------------------------
CExtendedConfigFile::~CExtendedConfigFile()
{
}

//---------------------------------------------------------------------------------------------------------------------
/**
	internal helper function - used by Load()
	reads the value with the given key from the given xml element and stores it in the entry
	how the entry must be read depends on the parameters data type; this function is virtual so derived classes can add 
	support for new parameter types
*/
void 
CExtendedConfigFile::ReadValue(TiXmlElement* p_pxXmlElement, const char* p_pcKey, CEntry* p_pxEntry)
{
	if(p_pxEntry->m_xType == ID_VEC3)
	{
		((CEntryVec3*) p_pxEntry)->m_vValue = XMLUtils::GetXMLTagVector(p_pxXmlElement, p_pcKey, ((CEntryVec3*) p_pxEntry)->m_vDefaultValue);
	}
	else 
	{ 
		__super::ReadValue(p_pxXmlElement, p_pcKey, p_pxEntry);
	}
}

//---------------------------------------------------------------------------------------------------------------------
/** 
	helper function; used by save
*/
void			
CExtendedConfigFile::WriteValueAndComment(TiXmlElement* p_pxXmlElement, const char* p_pcKey, CEntry* p_pxEntry)
{
	if(p_pxEntry->m_xType == ID_VEC3)
	{
		CEntryVec3* pxEntry = (CEntryVec3*) p_pxEntry;
		if(p_pxEntry->m_bWriteComment)
		{
			TiXmlComment xComment;
			xComment.SetValue(CStr::Create("Possible Values: (%.2f, %.2f, %.2f) - (%.2f, %.2f, %.2f)", 
				pxEntry->m_vMinValue.x(), pxEntry->m_vMinValue.y(),	pxEntry->m_vMinValue.z(), 
				pxEntry->m_vMaxValue.x(), pxEntry->m_vMaxValue.y(),	pxEntry->m_vMaxValue.z()).c_str());
	 		p_pxXmlElement->InsertEndChild(xComment);
			xComment.SetValue(CStr::Create("Default Value: (%.2f, %.2f, %.2f)", 
				pxEntry->m_vDefaultValue.x(), pxEntry->m_vDefaultValue.y(), pxEntry->m_vDefaultValue.z()).c_str());	
 			p_pxXmlElement->InsertEndChild(xComment);
		}
		XMLUtils::WriteXMLTagVector(p_pxXmlElement, p_pcKey, pxEntry->m_vValue); 
	}
	else 
	{
		WriteValueAndComment(p_pxXmlElement, p_pcKey, p_pxEntry);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CExtendedConfigFile::ReadValueFromString(const std::string& p_rsValue, CEntry* p_pxEntry)
{
	if(p_pxEntry->m_xType == ID_VEC3)
	{
		assert(false); // not supported (yet)
	}
	else 
	{
		ReadValueFromString(p_rsValue, p_pxEntry);
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool		
CExtendedConfigFile::AddParameterVec3(std::string p_sPath, std::string p_sDescription, CVec3 p_vDefaultValue, CVec3 p_vMinValue, CVec3 p_vMaxValue)
{
	CEntryVec3* pxEntry = new CEntryVec3();
	pxEntry->m_sPath = p_sPath;
	pxEntry->m_sDescription = p_sDescription;
	pxEntry->m_vValue = p_vDefaultValue;
	assert(p_vMinValue.x()	<= p_vMaxValue.x());
	assert(p_vMinValue.y()	<= p_vMaxValue.y());
	assert(p_vMinValue.z()  <= p_vMaxValue.z());
	pxEntry->m_vDefaultValue.x()	= clamp(p_vDefaultValue.x(),	p_vMinValue.x(),	p_vMaxValue.x());
	pxEntry->m_vDefaultValue.y()	= clamp(p_vDefaultValue.y(),	p_vMinValue.y(),	p_vMaxValue.y());
	pxEntry->m_vDefaultValue.z()	= clamp(p_vDefaultValue.z(),	p_vMinValue.z(),	p_vMaxValue.z());
	pxEntry->m_vMinValue = p_vMinValue;
	pxEntry->m_vMaxValue = p_vMaxValue;
	m_axParameters[p_sPath] = pxEntry;
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
bool		
CExtendedConfigFile::SetValueVec3(std::string p_sPath, const CVec3& p_rvValue)
{
	CEntryVec3* pxEntry = (CEntryVec3*) FindEntry(p_sPath, ID_VEC3);
	if(pxEntry == 0) { return false; }
	pxEntry->m_vValue = p_rvValue;
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
CVec3		
CExtendedConfigFile::GetValueVec3(std::string p_sPath) const
{
	const CEntryVec3* pxEntry = (const CEntryVec3*) FindEntry(p_sPath, ID_VEC3);
	if(pxEntry == 0) { return CVec3(0.0f, 0.0f, 0.0f); }
	return pxEntry->m_vValue;
}

//---------------------------------------------------------------------------------------------------------------------
void
CExtendedConfigFile::Validate(CEntry* p_pxEntry)
{
	if(p_pxEntry->m_xType == ID_VEC3)
	{
		CEntryVec3* pxEntry = (CEntryVec3*) p_pxEntry;
		pxEntry->m_vValue.x()	= clamp(pxEntry->m_vValue.x(),	pxEntry->m_vMinValue.x(),	pxEntry->m_vMaxValue.x());
		pxEntry->m_vValue.y()	= clamp(pxEntry->m_vValue.y(),	pxEntry->m_vMinValue.y(),	pxEntry->m_vMaxValue.y());
		pxEntry->m_vValue.z()	= clamp(pxEntry->m_vValue.z(),	pxEntry->m_vMinValue.z(),	pxEntry->m_vMaxValue.z());
	}
	else
	{
		__super::Validate(p_pxEntry);
	}
}
//---------------------------------------------------------------------------------------------------------------------

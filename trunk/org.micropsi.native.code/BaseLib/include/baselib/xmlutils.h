#pragma once

#ifndef BASELIB_XMLUTILS_H_INCLUDED
#define BASELIB_XMLUTILS_H_INCLUDED

#include <string>
#include "baselib/pnt.h"
#include "baselib/size.h"
#include "baselib/color.h"
#include "baselib/geometry/CVector.h"

class TiXmlElement;

namespace XMLUtils
{

	bool        XMLTagExists(const TiXmlElement* pXmlElement, const std::string& sTagName);

	const char* GetXMLTagString(const TiXmlElement* pXmlElement, const std::string& sTagName, const std::string& sDefault = "");
	float       GetXMLTagFloat(const TiXmlElement* pXmlElement, const std::string& sTagName, float fDefault = 0.0f);
	int         GetXMLTagInt(const TiXmlElement* pXmlElement, const std::string& sTagName, int iDefault = 0);
	bool        GetXMLTagBool(const TiXmlElement* pXmlElement, const std::string& sTagName, bool bDefault = false);
	CPnt        GetXMLTagPoint(const TiXmlElement* pXmlElement, const std::string& sTagName, CPnt xDefault = CPnt(0, 0));
	CPnt        GetXMLTagPoint(const TiXmlElement* pXmlElement);
	CSize       GetXMLTagSize(const TiXmlElement* pXmlElement, const std::string& sTagName, CSize xDefault = CSize(0, 0));
	CSize       GetXMLTagSize(const TiXmlElement* pXmlElement);
	CColor		GetXMLTagColor(const TiXmlElement* pXmlElement, const std::string& sTagName, CColor xDefault = CColor(0, 0, 0, 0));
	CColor		GetXMLTagColor(const TiXmlElement* pXmlElement);
	CVec3       GetXMLTagVector(const TiXmlElement* pXmlElement, const std::string& sTagName, CVec3& vDefault = CVec3(0.0f, 0.0f, 0.0f));
	CVec3       GetXMLTagVector(const TiXmlElement* pXmlElement);

	const char* GetElementText(const TiXmlElement* pXmlElement);


	void		WriteXMLTagString(TiXmlElement* pXmlElement, const std::string& sTagName, const std::string& sValue);
	void		WriteXMLTagFloat(TiXmlElement* pXmlElement, const std::string& sTagName, float fValue);
	void		WriteXMLTagInt(TiXmlElement* pXmlElement, const std::string& sTagName, int iValue);
	void		WriteXMLTagBool(TiXmlElement* pXmlElement, const std::string& sTagName, bool bValue);
	void		WriteXMLTagPoint(TiXmlElement* pXmlElement, const std::string& sTagName, CPnt xValue);
	void		WriteXMLTagPoint(TiXmlElement* pXmlElement, CPnt xValue);
	void		WriteXMLTagSize(TiXmlElement* pXmlElement, const std::string& sTagName, CSize xValue);
	void		WriteXMLTagSize(TiXmlElement* pXmlElement, CSize xValue);
	void		WriteXMLTagColor(TiXmlElement* pXmlElement, const std::string& sTagName, CColor xValue);
	void		WriteXMLTagColor(TiXmlElement* pXmlElement, CColor xValue);
	void		WriteXMLTagVector(TiXmlElement* pXmlElement, const std::string& sTagName, const CVec3& vValue);
	void		WriteXMLTagVector(TiXmlElement* pXmlElement, const CVec3& vValue);


} // namespace XMLUtils

#endif // BASELIB_XMLUTILS_H_INCLUDED

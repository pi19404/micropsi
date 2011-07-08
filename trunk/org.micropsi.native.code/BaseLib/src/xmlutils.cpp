#include "stdafx.h"

#include "baselib/xmlutils.h"

#include <tinyxml.h>

#include "baselib/str.h"

#include <string>
using std::string;

namespace XMLUtils
{

//-----------------------------------------------------------------------------------------------------------------------------------------
bool
XMLTagExists(const TiXmlElement* pXmlElement, const std::string& sTagName)
{
	const TiXmlElement* pxElement = pXmlElement->FirstChildElement(sTagName.c_str());
	return (pxElement != 0);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const char* 
GetXMLTagString(const TiXmlElement* pXmlElement, const std::string& sTagName, const std::string& sDefault)
{
	const TiXmlElement* pxElement = pXmlElement->FirstChildElement(sTagName.c_str());
	if(!pxElement) { return sDefault.c_str(); }
	const TiXmlText* pxText = pxElement->FirstChild()->ToText();
	if(!pxText) { return sDefault.c_str(); }
	return pxText->Value();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
float
GetXMLTagFloat(const TiXmlElement* pXmlElement, const std::string& sTagName, float fDefault)
{
	string s = GetXMLTagString(pXmlElement, sTagName);
	if(s == "") return fDefault;
	return (float)atof(s.c_str());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int
GetXMLTagInt(const TiXmlElement* pXmlElement, const std::string& sTagName, int iDefault)
{ 
	string s = GetXMLTagString(pXmlElement, sTagName);
	if(s == "") { return iDefault; }
	return atoi(s.c_str());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
GetXMLTagBool(const TiXmlElement* pXmlElement, const std::string& sTagName, bool bDefault)
{ 
	string s = GetXMLTagString(pXmlElement, sTagName);
	if(s == "") return bDefault;
	return s == "true" || s == "yes" || s == "1";
}

//-----------------------------------------------------------------------------------------------------------------------------------------
//bool
//GetXMLTagValue(const TiXmlElement* pXmlElement, const std::string& sTagName, std::string& po_sValue)
//{
//	const TiXmlElement* pxElement = pXmlElement->FirstChildElement(sTagName.c_str());
//	if(!pxElement)	{ return false; }
//	const TiXmlText* pxText = pxElement->FirstChild()->ToText();
//	if(!pxText)		{ return false; }
//	po_sValue = pxText->Value();
//	return true;
//}
//-----------------------------------------------------------------------------------------------------------------------------------------
CPnt        
GetXMLTagPoint(const TiXmlElement* pXmlElement, const std::string& sTagName, CPnt xDefault)
{
    CPnt xResult;

    const TiXmlElement* pxElement = pXmlElement->FirstChildElement(sTagName.c_str());
	if(!pxElement) { return xResult; }

    xResult.x = GetXMLTagInt(pxElement, "x", xDefault.x);
    xResult.y = GetXMLTagInt(pxElement, "y", xDefault.y);

    return xResult;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CPnt        
GetXMLTagPoint(const TiXmlElement* pXmlElement)
{
    CPnt xResult;

    xResult.x = GetXMLTagInt(pXmlElement, "x");
    xResult.y = GetXMLTagInt(pXmlElement, "y");

    return xResult;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CSize       
GetXMLTagSize(const TiXmlElement* pXmlElement, const std::string& sTagName, CSize xDefault)
{
    CSize xResult;

    const TiXmlElement* pxElement = pXmlElement->FirstChildElement(sTagName.c_str());
	if(!pxElement) { return xResult; }

    xResult.cx = GetXMLTagInt(pxElement, "cx", xDefault.cx);
    xResult.cy = GetXMLTagInt(pxElement, "cy", xDefault.cy);

    return xResult;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CSize       
GetXMLTagSize(const TiXmlElement* pXmlElement)
{
    CSize xResult;

    xResult.cx = GetXMLTagInt(pXmlElement, "cx");
    xResult.cy = GetXMLTagInt(pXmlElement, "cy");

    return xResult;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CColor		
GetXMLTagColor(const TiXmlElement* pXmlElement, const std::string& sTagName, CColor xDefault)
{
    CColor xResult;

    const TiXmlElement* pxElement = pXmlElement->FirstChildElement(sTagName.c_str());
	if(!pxElement) { return xResult; }

	xResult.m_cRed		= GetXMLTagInt(pxElement, "r", xDefault.m_cRed);
	xResult.m_cGreen	= GetXMLTagInt(pxElement, "g", xDefault.m_cGreen);
	xResult.m_cBlue		= GetXMLTagInt(pxElement, "b", xDefault.m_cBlue);
	xResult.m_cAlpha	= GetXMLTagInt(pxElement, "a", xDefault.m_cAlpha);

    return xResult;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CColor		
GetXMLTagColor(const TiXmlElement* pXmlElement)
{
    CColor xResult;

    xResult.m_cRed		= GetXMLTagInt(pXmlElement, "r");
    xResult.m_cGreen	= GetXMLTagInt(pXmlElement, "g");
    xResult.m_cBlue		= GetXMLTagInt(pXmlElement, "b");
    xResult.m_cAlpha	= GetXMLTagInt(pXmlElement, "a");

    return xResult;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
WriteXMLTagString(TiXmlElement* pXmlElement, const std::string& sTagName, const std::string& sValue)
{
	TiXmlElement xElement(sTagName.c_str());
	TiXmlText xText(sValue.c_str());
	
	TiXmlNode* pxElement = pXmlElement->InsertEndChild(xElement);
	pxElement->InsertEndChild(xText);
}

//------------------------------------------------------------------------------------------------------------------------------------------
void
WriteXMLTagFloat(TiXmlElement* pXmlElement, const std::string& sTagName, float fValue)
{
	WriteXMLTagString(pXmlElement, sTagName, CStr::Create("%f", fValue).c_str());
}

//------------------------------------------------------------------------------------------------------------------------------------------
void
WriteXMLTagInt(TiXmlElement* pXmlElement, const std::string& sTagName, int iValue)
{
	WriteXMLTagString(pXmlElement, sTagName, CStr::Create("%d", iValue).c_str());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void		
WriteXMLTagBool(TiXmlElement* pXmlElement, const std::string& sTagName, bool bValue)
{
	WriteXMLTagString(pXmlElement, sTagName, bValue ? "true" : "false");
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void		
WriteXMLTagPoint(TiXmlElement* pXmlElement, const std::string& sTagName, CPnt xValue)
{
	TiXmlElement xElement(sTagName.c_str());
	TiXmlNode* pxElement = pXmlElement->InsertEndChild(xElement);

	WriteXMLTagInt(pxElement->ToElement(), "x", xValue.x);
	WriteXMLTagInt(pxElement->ToElement(), "y", xValue.y);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void		
WriteXMLTagPoint(TiXmlElement* pXmlElement, CPnt xValue)
{
	WriteXMLTagInt(pXmlElement, "x", xValue.x);
	WriteXMLTagInt(pXmlElement, "y", xValue.y);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void		
WriteXMLTagSize(TiXmlElement* pXmlElement, const std::string& sTagName, CSize xValue)
{
	TiXmlElement xElement(sTagName.c_str());
	TiXmlNode* pxElement = pXmlElement->InsertEndChild(xElement);

	WriteXMLTagInt(pxElement->ToElement(), "cx", xValue.cx);
	WriteXMLTagInt(pxElement->ToElement(), "cy", xValue.cy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
WriteXMLTagSize(TiXmlElement* pXmlElement, CSize xValue)
{
	WriteXMLTagInt(pXmlElement, "cx", xValue.cx);
	WriteXMLTagInt(pXmlElement, "cy", xValue.cy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void		
WriteXMLTagColor(TiXmlElement* pXmlElement, const std::string& sTagName, CColor xValue)
{
	TiXmlElement xElement(sTagName.c_str());
	TiXmlNode* pxElement = pXmlElement->InsertEndChild(xElement);

	WriteXMLTagInt(pxElement->ToElement(), "r", xValue.m_cRed);
	WriteXMLTagInt(pxElement->ToElement(), "g", xValue.m_cGreen);
	WriteXMLTagInt(pxElement->ToElement(), "b", xValue.m_cBlue);
	WriteXMLTagInt(pxElement->ToElement(), "a", xValue.m_cAlpha);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void		
WriteXMLTagColor(TiXmlElement* pXmlElement, CColor xValue)
{
	WriteXMLTagInt(pXmlElement, "r", xValue.m_cRed);
	WriteXMLTagInt(pXmlElement, "g", xValue.m_cGreen);
	WriteXMLTagInt(pXmlElement, "b", xValue.m_cBlue);
	WriteXMLTagInt(pXmlElement, "a", xValue.m_cAlpha);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
const char* 
GetElementText(const TiXmlElement* pXmlElement)
{	
	if(!pXmlElement) return "";
	const TiXmlNode* pxNode = pXmlElement->FirstChild();
	if(!pxNode) return "";
	const TiXmlText* pxText = pxNode->ToText();
	if(!pxText) return "";
	return pxText->Value();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CVec3 
GetXMLTagVector(const TiXmlElement* pXmlElement, const std::string& sTagName, CVec3& vDefault)
{
    CVec3 vResult;
    vResult.Clear();

    const TiXmlElement* pxElement = pXmlElement->FirstChildElement(sTagName.c_str());
	if(!pxElement) { return vResult; }

    vResult.x() = GetXMLTagFloat(pxElement, "x", vDefault.x());
    vResult.y() = GetXMLTagFloat(pxElement, "y", vDefault.y());
    vResult.z() = GetXMLTagFloat(pxElement, "z", vDefault.z());

    return vResult;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CVec3 
GetXMLTagVector(const TiXmlElement* pXmlElement)
{
    CVec3 vResult;
    vResult.Clear();

    vResult.x() = GetXMLTagFloat(pXmlElement, "x");
    vResult.y() = GetXMLTagFloat(pXmlElement, "y");
    vResult.z() = GetXMLTagFloat(pXmlElement, "z");

    return vResult;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
WriteXMLTagVector(TiXmlElement* pXmlElement, const std::string& sTagName, const CVec3& vValue)
{
	TiXmlElement xElement(sTagName.c_str());
	TiXmlNode* pxElement = pXmlElement->InsertEndChild(xElement);

	WriteXMLTagFloat(pxElement->ToElement(), "x", vValue.x());
	WriteXMLTagFloat(pxElement->ToElement(), "y", vValue.y());
	WriteXMLTagFloat(pxElement->ToElement(), "z", vValue.z());
}
//------------------------------------------------------------------------------------------------------------------------------------------
void 
WriteXMLTagVector(TiXmlElement* pXmlElement, const CVec3& vValue)
{
	WriteXMLTagFloat(pXmlElement, "x", vValue.x());
	WriteXMLTagFloat(pXmlElement, "y", vValue.y());
	WriteXMLTagFloat(pXmlElement, "z", vValue.z());
}
//------------------------------------------------------------------------------------------------------------------------------------------

} // namespace XMLUtils
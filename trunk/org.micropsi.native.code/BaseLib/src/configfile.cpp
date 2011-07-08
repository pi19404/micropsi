#include "stdafx.h"

#include "baselib/configfile.h"

#include "baselib/str.h"
#include "baselib/dynarray.h"

#include <tinyxml.h>
#include "baselib/xmlutils.h"
#include "baselib/macros.h"

using std::string;
using std::vector;
using std::map;

//---------------------------------------------------------------------------------------------------------------------

const CFourCC CConfigFile::ID_INT		= CFourCC("INT");
const CFourCC CConfigFile::ID_FLOAT		= CFourCC("FLOA");
const CFourCC CConfigFile::ID_STRING	= CFourCC("STRI");
const CFourCC CConfigFile::ID_BOOL		= CFourCC("BOOL");
const CFourCC CConfigFile::ID_POINT		= CFourCC("POIN");
const CFourCC CConfigFile::ID_SIZE		= CFourCC("SIZE");
const CFourCC CConfigFile::ID_COLOR		= CFourCC("COLR");

//---------------------------------------------------------------------------------------------------------------------
CConfigFile::CConfigFile()
{
}

//---------------------------------------------------------------------------------------------------------------------
CConfigFile::~CConfigFile()
{
	Clear();
}

//---------------------------------------------------------------------------------------------------------------------
void
CConfigFile::Clear()
{
	map<string, CEntry*>::iterator i;
	for(i=m_axParameters.begin(); i!=m_axParameters.end(); ++i)
	{
		delete i->second;
	}

	m_axParameters.clear();
}

//---------------------------------------------------------------------------------------------------------------------
bool
CConfigFile::Load(const char* p_pcXMLFilename)
{
	if(m_axParameters.size() == 0)
	{
		return true;
	}

	TiXmlDocument* pxDoc = new TiXmlDocument(p_pcXMLFilename);
	bool bSuccess = pxDoc->LoadFile();
	if(!bSuccess)
	{
		return false;
	}

	map<string, CEntry*>::iterator i;
	for(i=m_axParameters.begin(); i!=m_axParameters.end(); i++)
	{
		CStr sPath = i->second->m_sPath.c_str();
		CDynArray<CStr> asPathParts;
		sPath.Split(asPathParts, "/");
        
		// den Pfad hochklettern, bis wir beim richtigen Element sind
		TiXmlElement* pxCurrentElement = pxDoc->RootElement();

		bool bFound = true;
		for(int j=0; j < (int) asPathParts.Size()-1; ++j)
		{
			TiXmlElement* pxNextChild = pxCurrentElement->FirstChildElement(asPathParts[j].c_str());
			if(!pxNextChild)
			{
				bFound = false;
				break;
			}
			pxCurrentElement = pxNextChild;
		}

		if(!bFound)
		{
			continue;
		}

		ReadValue(pxCurrentElement, asPathParts[asPathParts.Size()-1].c_str(), i->second);
	}

	Validate();
	delete pxDoc;

	return true;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	internal helper function - used by Load()
	reads the value with the given key from the given xml element and stores it in the entry
	how the entry must be read depends on the parameters data type; this function is virtual so derived classes can add 
	support for new parameter types
*/
void 
CConfigFile::ReadValue(TiXmlElement* p_pxXmlElement, const char* p_pcKey, CEntry* p_pxEntry)
{
	if(p_pxEntry->m_xType == ID_STRING)
	{
		((CEntryString*) p_pxEntry)->m_sValue = XMLUtils::GetXMLTagString(p_pxXmlElement, p_pcKey, ((CEntryString*) p_pxEntry)->m_sDefaultValue);
	}
	else if(p_pxEntry->m_xType == ID_INT)
	{ 
		((CEntryInt*) p_pxEntry)->m_iValue = XMLUtils::GetXMLTagInt(p_pxXmlElement, p_pcKey, ((CEntryInt*) p_pxEntry)->m_iDefaultValue);
	}
	else if(p_pxEntry->m_xType == ID_FLOAT)
	{    
		((CEntryFloat*) p_pxEntry)->m_fValue = XMLUtils::GetXMLTagFloat(p_pxXmlElement, p_pcKey, ((CEntryFloat*) p_pxEntry)->m_fDefaultValue);
	}
	else if(p_pxEntry->m_xType == ID_BOOL)
	{ 
		((CEntryBool*) p_pxEntry)->m_bValue = XMLUtils::GetXMLTagBool(p_pxXmlElement, p_pcKey, ((CEntryBool*) p_pxEntry)->m_bDefaultValue);
	}
	else if(p_pxEntry->m_xType == ID_POINT)
	{ 
		((CEntryPoint*) p_pxEntry)->m_xValue = XMLUtils::GetXMLTagPoint(p_pxXmlElement, p_pcKey, ((CEntryPoint*) p_pxEntry)->m_xDefaultValue);
	}
	else if(p_pxEntry->m_xType == ID_SIZE)
	{ 
		((CEntrySize*) p_pxEntry)->m_xValue = XMLUtils::GetXMLTagSize(p_pxXmlElement, p_pcKey, ((CEntrySize*) p_pxEntry)->m_xDefaultValue);
	}
	else if(p_pxEntry->m_xType == ID_COLOR)
	{ 
		((CEntryColor*) p_pxEntry)->m_xValue = XMLUtils::GetXMLTagColor(p_pxXmlElement, p_pcKey, ((CEntryColor*) p_pxEntry)->m_xDefaultValue);
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CConfigFile::Save(const char* p_pcXMLFilename)
{
	if(m_axParameters.size() == 0)
	{
		return true;
	}

	TiXmlDocument* pxDoc = new TiXmlDocument(p_pcXMLFilename);

	// <configuration> - root
	TiXmlElement xRootElement("configuration");
	pxDoc->InsertEndChild(xRootElement);

	map<string, CEntry*>::const_iterator i;
	for(i=m_axParameters.begin(); i!=m_axParameters.end(); i++)
	{
		CStr sPath = i->second->m_sPath.c_str();
		CDynArray<CStr> asPathParts;
		sPath.Split(asPathParts, "/\\");
        
		// den Pfad hochklettern, bis wir beim richtigen Element sind; wenn nötig Elemente anlegen
		TiXmlElement* pxCurrentElement = pxDoc->RootElement();
		for(int j=0; j < (int) asPathParts.Size()-1; ++j)
		{
			TiXmlElement* pxNextChild = pxCurrentElement->FirstChildElement(asPathParts[j].c_str());
			if(!pxNextChild)
			{
				TiXmlElement xElement(asPathParts[j].c_str());
				pxNextChild = pxCurrentElement->InsertEndChild(xElement)->ToElement();
			}
			pxCurrentElement = pxNextChild;
		}

		WriteValueAndComment(pxCurrentElement, asPathParts[asPathParts.Size()-1].c_str(), i->second);
	}

	bool bSuccess = pxDoc->SaveFile();
	delete pxDoc;

	return bSuccess;
}

//---------------------------------------------------------------------------------------------------------------------
/** 
	helper function; used by save
*/
void			
CConfigFile::WriteValueAndComment(TiXmlElement* p_pxXmlElement, const char* p_pcKey, CEntry* p_pxEntry)
{
	if(p_pxEntry->m_bWriteComment)
	{
		// Kommentar schreiben
		TiXmlComment xComment;
		xComment.SetValue(p_pxEntry->m_sDescription.c_str());
		p_pxXmlElement->InsertEndChild(xComment);
	}

	if(p_pxEntry->m_xType == ID_STRING)
	{ 
		CEntryString* pxEntry = (CEntryString*) p_pxEntry;
		if(p_pxEntry->m_bWriteComment)
		{
			TiXmlComment xComment;
			if(pxEntry->m_asPossibleValues.size() > 0)
			{
				std::string s = "Possible Values: "; 
				for(unsigned int j=0; j<pxEntry->m_asPossibleValues.size(); ++j)
				{
					s += pxEntry->m_asPossibleValues[j];
					if(j<pxEntry->m_asPossibleValues.size()-1)
					{
						s += ", ";
					}
				}
				xComment.SetValue(s.c_str());
				p_pxXmlElement->InsertEndChild(xComment);
			}
			xComment.SetValue(CStr::Create("Default Value: %s", pxEntry->m_sDefaultValue.c_str()).c_str());	
			p_pxXmlElement->InsertEndChild(xComment);
		}
		XMLUtils::WriteXMLTagString(p_pxXmlElement, p_pcKey, pxEntry->m_sValue); 
	}
	else if(p_pxEntry->m_xType == ID_INT)
	{
		CEntryInt* pxEntry = (CEntryInt*) p_pxEntry;
		if(p_pxEntry->m_bWriteComment)
		{
			TiXmlComment xComment;
			xComment.SetValue(CStr::Create("Possible Values: %d - %d", pxEntry->m_iMinValue, pxEntry->m_iMaxValue).c_str());
			p_pxXmlElement->InsertEndChild(xComment);
			xComment.SetValue(CStr::Create("Default Value: %d", pxEntry->m_iDefaultValue).c_str());	
			p_pxXmlElement->InsertEndChild(xComment);
		}
		XMLUtils::WriteXMLTagInt(p_pxXmlElement, p_pcKey, pxEntry->m_iValue); 
	}
	else if(p_pxEntry->m_xType == ID_FLOAT)
	{
		CEntryFloat* pxEntry = (CEntryFloat*) p_pxEntry;
		if(p_pxEntry->m_bWriteComment)
		{
			TiXmlComment xComment;
			xComment.SetValue(CStr::Create("Possible Values: %.2f - %.2f", pxEntry->m_fMinValue, pxEntry->m_fMaxValue).c_str());
 			p_pxXmlElement->InsertEndChild(xComment);
			xComment.SetValue(CStr::Create("Default Value: %.2f", pxEntry->m_fDefaultValue).c_str());	
 			p_pxXmlElement->InsertEndChild(xComment);
		}
		XMLUtils::WriteXMLTagFloat(p_pxXmlElement, p_pcKey, pxEntry->m_fValue); 
	}
	else if(p_pxEntry->m_xType == ID_BOOL)
	{
		CEntryBool* pxEntry = (CEntryBool*) p_pxEntry;
		if(p_pxEntry->m_bWriteComment)
		{
			TiXmlComment xComment;
			xComment.SetValue("Possible Values: true, false");
	 		p_pxXmlElement->InsertEndChild(xComment);
			xComment.SetValue(CStr::Create("Default Value: %s", pxEntry->m_bDefaultValue ? "true" : "false").c_str());	
 			p_pxXmlElement->InsertEndChild(xComment);
		}
		XMLUtils::WriteXMLTagBool(p_pxXmlElement, p_pcKey, pxEntry->m_bValue); 
	}
	else if(p_pxEntry->m_xType == ID_POINT)
	{
		CEntryPoint* pxEntry = (CEntryPoint*) p_pxEntry;
		if(p_pxEntry->m_bWriteComment)
		{
			TiXmlComment xComment;
			xComment.SetValue(CStr::Create("Possible Values: (%d, %d) - (%d, %d)", 
				pxEntry->m_xMinValue.x, pxEntry->m_xMinValue.x, pxEntry->m_xMaxValue.y, pxEntry->m_xMaxValue.y).c_str());
	 		p_pxXmlElement->InsertEndChild(xComment);
			xComment.SetValue(CStr::Create("Default Value: (%d, %d)", pxEntry->m_xDefaultValue.x, pxEntry->m_xDefaultValue.y).c_str());	
 			p_pxXmlElement->InsertEndChild(xComment);
		}
		XMLUtils::WriteXMLTagPoint(p_pxXmlElement, p_pcKey, pxEntry->m_xValue); 
	}
	else if(p_pxEntry->m_xType == ID_SIZE)
	{
		CEntrySize* pxEntry = (CEntrySize*) p_pxEntry;
		if(p_pxEntry->m_bWriteComment)
		{
			TiXmlComment xComment;
			xComment.SetValue(CStr::Create("Possible Values: (%d, %d) - (%d, %d)", 
				pxEntry->m_xMinValue.cx, pxEntry->m_xMinValue.cx, pxEntry->m_xMaxValue.cy, pxEntry->m_xMaxValue.cy).c_str());
	 		p_pxXmlElement->InsertEndChild(xComment);
			xComment.SetValue(CStr::Create("Default Value: (%d, %d)", pxEntry->m_xDefaultValue.cx, pxEntry->m_xDefaultValue.cy).c_str());	
 			p_pxXmlElement->InsertEndChild(xComment);
		}
		XMLUtils::WriteXMLTagSize(p_pxXmlElement, p_pcKey, pxEntry->m_xValue); 
	}
	else if(p_pxEntry->m_xType == ID_COLOR)
	{
		CEntryColor* pxEntry = (CEntryColor*) p_pxEntry;
		if(p_pxEntry->m_bWriteComment)
		{
			TiXmlComment xComment;
			xComment.SetValue(CStr::Create("Possible Values: (%d, %d, %d, %d) - (%d, %d, %d, %d)", 
				pxEntry->m_xMinValue.m_cRed, pxEntry->m_xMinValue.m_cGreen,	pxEntry->m_xMinValue.m_cBlue, pxEntry->m_xMinValue.m_cAlpha, 
				pxEntry->m_xMaxValue.m_cRed, pxEntry->m_xMaxValue.m_cGreen,	pxEntry->m_xMaxValue.m_cBlue, pxEntry->m_xMaxValue.m_cAlpha).c_str());
	 		p_pxXmlElement->InsertEndChild(xComment);
			xComment.SetValue(CStr::Create("Default Value: (%d, %d, %d, %d)", 
				pxEntry->m_xDefaultValue.m_cRed, pxEntry->m_xDefaultValue.m_cGreen, pxEntry->m_xDefaultValue.m_cBlue, pxEntry->m_xDefaultValue.m_cAlpha).c_str());	
 			p_pxXmlElement->InsertEndChild(xComment);
		}
		XMLUtils::WriteXMLTagColor(p_pxXmlElement, p_pcKey, pxEntry->m_xValue); 
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CConfigFile::ReadFromCommandLine(const char* p_pcCommandLine)
{
	if(m_axParameters.size() == 0)
	{
		return true;
	}

	map<string, CEntry*>::iterator i;
	for(i=m_axParameters.begin(); i!=m_axParameters.end(); i++)
	{
		CStr sKey = i->second->m_sPath.c_str();
		int iLastSlash = sKey.FindReverse('/');
		sKey = CStr("-") + sKey.Mid(iLastSlash+1);

		string sValue = GetCommandLineValue(sKey.c_str(), p_pcCommandLine);	
	    if (sValue != "")
		{
			ReadValueFromString(sValue, i->second);
		}
	}

	Validate();
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
void
CConfigFile::ReadValueFromString(const string& p_rsValue, CEntry* p_pxEntry)
{
	if(p_pxEntry->m_xType == ID_STRING)
	{
		((CEntryString*) p_pxEntry)->m_sValue = p_rsValue;
	}
	else if(p_pxEntry->m_xType == ID_INT)
	{
		((CEntryInt*) p_pxEntry)->m_iValue = atoi(p_rsValue.c_str());
	}
	else if(p_pxEntry->m_xType == ID_FLOAT)
	{
		((CEntryFloat*) p_pxEntry)->m_fValue = (float) atof(p_rsValue.c_str());
	}
	else if(p_pxEntry->m_xType == ID_BOOL)
	{
		((CEntryBool*) p_pxEntry)->m_bValue = p_rsValue == "true";
	}
	else if(p_pxEntry->m_xType == ID_POINT)
	{
		CEntryPoint* pxPoint = (CEntryPoint*) p_pxEntry;
		sscanf(p_rsValue.c_str(), "(%d;%d)", &pxPoint->m_xValue.x, &pxPoint->m_xValue.y);
	}
	else if(p_pxEntry->m_xType == ID_SIZE)
	{
		CEntrySize* pxSize = (CEntrySize*) p_pxEntry;
		sscanf(p_rsValue.c_str(), "%dx%d", &pxSize->m_xValue.cx, &pxSize->m_xValue.cy);
	}
	else if(p_pxEntry->m_xType == ID_COLOR)
	{
		CEntryColor* pxColor = (CEntryColor*) p_pxEntry;
		sscanf(p_rsValue.c_str(), "(%d;%d;%d;%d)", &pxColor->m_xValue.m_cRed, &pxColor->m_xValue.m_cGreen, pxColor->m_xValue.m_cBlue, pxColor->m_xValue.m_cAlpha);
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CConfigFile::AddParameterString(string p_sPath, string p_sDescription, string p_sDefaultValue, string p_sPossibleValues)
{
	CEntryString* pxEntry = new CEntryString();
	pxEntry->m_sPath = p_sPath;
	pxEntry->m_sDescription = p_sDescription;
	pxEntry->m_sValue = p_sDefaultValue;
	pxEntry->m_sDefaultValue = p_sDefaultValue;

	CStr s = p_sPossibleValues.c_str();
	CDynArray<CStr> asValues;
	s.Split(asValues, ", \t\n");

	bool bFound = asValues.Size() == 0;
	for(unsigned int i=0; i<asValues.Size(); ++i)
	{
		pxEntry->m_asPossibleValues.push_back(asValues[i].c_str());
		if(p_sDefaultValue == asValues[i].c_str())
		{
			bFound = true;
		}
	}

	if(bFound)
	{
		m_axParameters[p_sPath] = pxEntry;
		return true;
	}
	else
	{
		assert(false); // default ist nicht in der Liste zulässiger Werte!
		return false;
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CConfigFile::AddParameterInt(string p_sPath, string p_sDescription, int p_iDefaultValue, int p_iMinValue, int p_iMaxValue)
{
	CEntryInt* pxEntry = new CEntryInt();
	pxEntry->m_sPath = p_sPath;
	pxEntry->m_sDescription = p_sDescription;
	pxEntry->m_iValue = p_iDefaultValue;
	assert(p_iMinValue <= p_iMaxValue);
	pxEntry->m_iDefaultValue = clamp(p_iDefaultValue, p_iMinValue, p_iMaxValue);
	pxEntry->m_iMinValue = p_iMinValue;
	pxEntry->m_iMaxValue = p_iMaxValue;
	m_axParameters[p_sPath] = pxEntry;
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CConfigFile::AddParameterFloat(string p_sPath, string p_sDescription, float p_fDefaultValue, float p_fMinValue, float p_fMaxValue)
{
	CEntryFloat* pxEntry = new CEntryFloat();
	pxEntry->m_sPath = p_sPath;
	pxEntry->m_sDescription = p_sDescription;
	pxEntry->m_fValue = p_fDefaultValue;
	assert(p_fMinValue <= p_fMaxValue);
	pxEntry->m_fDefaultValue = clamp(p_fDefaultValue, p_fMinValue, p_fMaxValue);
	pxEntry->m_fMinValue = p_fMinValue;
	pxEntry->m_fMaxValue = p_fMaxValue;
	m_axParameters[p_sPath] = pxEntry;
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CConfigFile::AddParameterBool(std::string p_sPath, std::string p_sDescription, bool p_bDefaultValue)
{
	CEntryBool* pxEntry = new CEntryBool();
	pxEntry->m_sPath = p_sPath;
	pxEntry->m_sDescription = p_sDescription;
	pxEntry->m_bValue = p_bDefaultValue;
	pxEntry->m_bDefaultValue = p_bDefaultValue;
	m_axParameters[p_sPath] = pxEntry;
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool		
CConfigFile::AddParameterPoint(std::string p_sPath, std::string p_sDescription, CPnt p_xDefaultValue, CPnt p_xMinValue, CPnt p_xMaxValue)
{
	CEntryPoint* pxEntry = new CEntryPoint();
	pxEntry->m_sPath = p_sPath;
	pxEntry->m_sDescription = p_sDescription;
	pxEntry->m_xValue = p_xDefaultValue;
	assert(p_xMinValue.x <= p_xMaxValue.x);
	assert(p_xMinValue.y <= p_xMaxValue.y);
	pxEntry->m_xDefaultValue.x = clamp(p_xDefaultValue.x, p_xMinValue.x, p_xMaxValue.x);
	pxEntry->m_xDefaultValue.y = clamp(p_xDefaultValue.y, p_xMinValue.y, p_xMaxValue.y);
	pxEntry->m_xMinValue = p_xMinValue;
	pxEntry->m_xMaxValue = p_xMaxValue;
	m_axParameters[p_sPath] = pxEntry;
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool		
CConfigFile::AddParameterSize(std::string p_sPath, std::string p_sDescription, CSize p_xDefaultValue, CSize p_xMinValue, CSize p_xMaxValue)
{
	CEntrySize* pxEntry = new CEntrySize();
	pxEntry->m_sPath = p_sPath;
	pxEntry->m_sDescription = p_sDescription;
	pxEntry->m_xValue = p_xDefaultValue;
	assert(p_xMinValue.cx <= p_xMaxValue.cx);
	assert(p_xMinValue.cy <= p_xMaxValue.cy);
	pxEntry->m_xDefaultValue.cx = clamp(p_xDefaultValue.cx, p_xMinValue.cx, p_xMaxValue.cx);
	pxEntry->m_xDefaultValue.cy = clamp(p_xDefaultValue.cy, p_xMinValue.cy, p_xMaxValue.cy);
	pxEntry->m_xMinValue = p_xMinValue;
	pxEntry->m_xMaxValue = p_xMaxValue;
	m_axParameters[p_sPath] = pxEntry;
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool		
CConfigFile::AddParameterColor(std::string p_sPath, std::string p_sDescription, CColor p_xDefaultValue, CColor p_xMinValue, CColor p_xMaxValue)
{
	CEntryColor* pxEntry = new CEntryColor();
	pxEntry->m_sPath = p_sPath;
	pxEntry->m_sDescription = p_sDescription;
	pxEntry->m_xValue = p_xDefaultValue;
	assert(p_xMinValue.m_cRed   <= p_xMaxValue.m_cRed);
	assert(p_xMinValue.m_cGreen <= p_xMaxValue.m_cGreen);
	assert(p_xMinValue.m_cBlue  <= p_xMaxValue.m_cBlue);
	assert(p_xMinValue.m_cAlpha <= p_xMaxValue.m_cAlpha);
	pxEntry->m_xDefaultValue.m_cRed		= clamp(p_xDefaultValue.m_cRed,		p_xMinValue.m_cRed,		p_xMaxValue.m_cRed);
	pxEntry->m_xDefaultValue.m_cGreen	= clamp(p_xDefaultValue.m_cGreen,	p_xMinValue.m_cGreen,	p_xMaxValue.m_cGreen);
	pxEntry->m_xDefaultValue.m_cBlue	= clamp(p_xDefaultValue.m_cBlue,	p_xMinValue.m_cBlue,	p_xMaxValue.m_cBlue);
	pxEntry->m_xDefaultValue.m_cAlpha	= clamp(p_xDefaultValue.m_cAlpha,	p_xMinValue.m_cAlpha,	p_xMaxValue.m_cAlpha);
	pxEntry->m_xMinValue = p_xMinValue;
	pxEntry->m_xMaxValue = p_xMaxValue;
	m_axParameters[p_sPath] = pxEntry;
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CConfigFile::SetValueInt(std::string p_sPath, int p_iValue)
{
	CEntryInt* pxEntry = (CEntryInt*) FindEntry(p_sPath, ID_INT);
	if(pxEntry == 0) { return false; }
	pxEntry->m_iValue = clamp(p_iValue, pxEntry->m_iMinValue, pxEntry->m_iMaxValue);
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CConfigFile::SetValueFloat(std::string p_sPath, float p_fValue)
{
	CEntryFloat* pxEntry = (CEntryFloat*) FindEntry(p_sPath, ID_FLOAT);
	if(pxEntry == 0) { return false; }
	pxEntry->m_fValue = clamp(p_fValue, pxEntry->m_fMinValue, pxEntry->m_fMaxValue);
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CConfigFile::SetValueString(std::string p_sPath, std::string p_sValue)
{
	CEntryString* pxEntry = (CEntryString*) FindEntry(p_sPath, ID_STRING);
	if(pxEntry == 0) { return false; }

	bool bFound = pxEntry->m_asPossibleValues.size() == 0;
	for(unsigned int j=0; j<pxEntry->m_asPossibleValues.size(); ++j)
	{
		if(pxEntry->m_asPossibleValues[j] == p_sValue)
		{
			bFound = true;
			break;
		}
	}

	if(bFound)
	{
		pxEntry->m_sValue = p_sValue;
		return true;
	}
	else
	{
		assert(false); // ist nicht in der Liste zulässiger Werte!
		return false;
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------
bool
CConfigFile::SetValueBool(std::string p_sPath, bool p_bValue)
{
	CEntryBool* pxEntry = (CEntryBool*) FindEntry(p_sPath, ID_BOOL);
	if(pxEntry == 0) { return false; }
	pxEntry->m_bValue = p_bValue;
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
bool		
CConfigFile::SetValuePoint(std::string p_sPath, const CPnt& p_rxValue)
{
	CEntryPoint* pxEntry = (CEntryPoint*) FindEntry(p_sPath, ID_POINT);
	if(pxEntry == 0) { return false; }
	pxEntry->m_xValue = p_rxValue;
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
bool		
CConfigFile::SetValueSize(std::string p_sPath, const CSize& p_rxValue)
{
	CEntrySize* pxEntry = (CEntrySize*) FindEntry(p_sPath, ID_SIZE);
	if(pxEntry == 0) { return false; }
	pxEntry->m_xValue = p_rxValue;
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
bool		
CConfigFile::SetValueColor(std::string p_sPath, const CColor& p_rxValue)
{
	CEntryColor* pxEntry = (CEntryColor*) FindEntry(p_sPath, ID_COLOR);
	if(pxEntry == 0) { return false; }
	pxEntry->m_xValue = p_rxValue;
	return true;
}
//---------------------------------------------------------------------------------------------------------------------
int		
CConfigFile::GetValueInt(string p_sPath) const
{
	const CEntry* pxEntry = FindEntry(p_sPath);
	if(pxEntry == 0) { return 0; }

	if(pxEntry->m_xType == ID_INT)
	{
		return ((CEntryInt*) pxEntry)->m_iValue;
	}
	else if(pxEntry->m_xType == ID_FLOAT)
	{
		return (int) ((CEntryFloat*) pxEntry)->m_fValue;
	}
	else if(pxEntry->m_xType == ID_STRING)
	{
		for(unsigned int j=0; j<((CEntryString*) pxEntry)->m_asPossibleValues.size(); ++j)
		{
			if(((CEntryString*) pxEntry)->m_asPossibleValues[j] == ((CEntryString*) pxEntry)->m_sValue)
			{
				return j;
			}
		}
		return -1;
	}
	else
	{
		assert(false);
		return 0;
	}
}

//---------------------------------------------------------------------------------------------------------------------
float	
CConfigFile::GetValueFloat(string p_sPath) const
{
	const CEntry* pxEntry = FindEntry(p_sPath);
	if(pxEntry == 0) { return 0.0f; }


	if(pxEntry->m_xType == ID_FLOAT)
	{
		return ((CEntryFloat*) pxEntry)->m_fValue;
	}
	if(pxEntry->m_xType == ID_INT)
	{
		return (float) ((CEntryInt*) pxEntry)->m_iValue;
	}
	if(pxEntry->m_xType == ID_STRING)
	{
		return (float) atof(((CEntryString*) pxEntry)->m_sValue.c_str());
	}
	else
	{
		assert(false);
		return 0.0f;
	}
}

//---------------------------------------------------------------------------------------------------------------------
string	
CConfigFile::GetValueString(string p_sPath) const
{
	const CEntry* pxEntry = FindEntry(p_sPath);
	if(pxEntry == 0) { return ""; }


	if(pxEntry->m_xType == ID_STRING)
	{
		return ((CEntryString*) pxEntry)->m_sValue;
	}
	else if(pxEntry->m_xType == ID_INT)
	{
		return CStr::Create("%d", ((CEntryInt*) pxEntry)->m_iValue).c_str();
	}
	else if(pxEntry->m_xType == ID_FLOAT)
	{
		return CStr::Create("%f", ((CEntryFloat*) pxEntry)->m_fValue).c_str();
	}
	else
	{
		assert(false);
		return "";
	}
}
//---------------------------------------------------------------------------------------------------------------------
bool
CConfigFile::GetValueBool(std::string p_sPath) const
{
	const CEntry* pxEntry = FindEntry(p_sPath);
	if(pxEntry == 0) { return false; }


	if(pxEntry->m_xType == ID_BOOL)
	{
		return ((CEntryBool*) pxEntry)->m_bValue;
	}
	else if(pxEntry->m_xType == ID_STRING)
	{
		return ((CEntryString*) pxEntry)->m_sValue == "true";
	}
	else if(pxEntry->m_xType == ID_INT)
	{
		return ((CEntryInt*) pxEntry)->m_iValue != 0;
	}
	else if(pxEntry->m_xType == ID_FLOAT)
	{
		return ((CEntryFloat*) pxEntry)->m_fValue != 0.0f;
	}
	else
	{
		assert(false);
		return false;
	}
}
//---------------------------------------------------------------------------------------------------------------------
CPnt		
CConfigFile::GetValuePoint(std::string p_sPath) const
{
	const CEntryPoint* pxEntry = (const CEntryPoint*) FindEntry(p_sPath, ID_POINT);
	if(pxEntry == 0) { return CPnt(0, 0); }
	return pxEntry->m_xValue;
}
//---------------------------------------------------------------------------------------------------------------------
CSize		
CConfigFile::GetValueSize(std::string p_sPath) const
{
	const CEntrySize* pxEntry = (const CEntrySize*) FindEntry(p_sPath, ID_SIZE);
	if(pxEntry == 0) { return CSize(0, 0); }
	return pxEntry->m_xValue;
}
//---------------------------------------------------------------------------------------------------------------------
CColor		
CConfigFile::GetValueColor(std::string p_sPath) const
{
	const CEntryColor* pxEntry = (const CEntryColor*) FindEntry(p_sPath, ID_COLOR);
	if(pxEntry == 0) { return CColor(0, 0, 0, 0); }
	return pxEntry->m_xValue;
}
//---------------------------------------------------------------------------------------------------------------------
string
CConfigFile::GetCommandLineValue(const string& sKey, const char* pcCommandLine) const
{
	if(!pcCommandLine)
	{
		return "";
	}
    const char* pcKeyPos = strstr(pcCommandLine, (sKey + " ").c_str());
    if (!pcKeyPos) 
    {
        return "";
    }

    // search end of key
    while ((*pcKeyPos != ' ') && (*pcKeyPos)) {pcKeyPos++;}
    // search begin of value
    while ((*pcKeyPos == ' ') && (*pcKeyPos)) {pcKeyPos++;}

    string sValue;
    sValue.resize(strlen(pcCommandLine));

    int iPos = 0;
    while ((*pcKeyPos != ' ') && (*pcKeyPos)) 
    {
        sValue[iPos++] = *pcKeyPos++;
    }

    return sValue.substr(0, iPos);
}

//---------------------------------------------------------------------------------------------------------------------
void		
CConfigFile::Validate()
{
	if(m_axParameters.size() == 0)
	{
		return;
	}

	map<string, CEntry*>::iterator i;
	for(i=m_axParameters.begin(); i!=m_axParameters.end(); i++)
	{
		Validate(i->second);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CConfigFile::Validate(CEntry* p_pxEntry)
{
	if(p_pxEntry->m_xType == ID_STRING)
	{
		CEntryString* pxEntry = (CEntryString*) p_pxEntry;
		if(pxEntry->m_asPossibleValues.size() > 0)
		{
			bool bFound = false;
			for(unsigned int j=0; j<pxEntry->m_asPossibleValues.size(); ++j)
			{
				if(pxEntry->m_sValue == pxEntry->m_asPossibleValues[j])
				{
					bFound = true;
					break;
				}
			}
			if(!bFound)
			{
				pxEntry->m_sValue = pxEntry->m_sDefaultValue;
			}
		}
	}
	else if(p_pxEntry->m_xType == ID_INT)
	{
		CEntryInt* pxEntry = (CEntryInt*) p_pxEntry;
		pxEntry->m_iValue = clamp(pxEntry->m_iValue, pxEntry->m_iMinValue, pxEntry->m_iMaxValue);
	}
	else if(p_pxEntry->m_xType == ID_FLOAT)
	{
		CEntryFloat* pxEntry = (CEntryFloat*) p_pxEntry;
		pxEntry->m_fValue = clamp(pxEntry->m_fValue, pxEntry->m_fMinValue, pxEntry->m_fMaxValue);
	}
	else if(p_pxEntry->m_xType == ID_BOOL)
	{
		// ist immer korrekt
	}
	else if(p_pxEntry->m_xType == ID_POINT)
	{
		CEntryPoint* pxEntry = (CEntryPoint*) p_pxEntry;
		pxEntry->m_xValue.x = clamp(pxEntry->m_xValue.x, pxEntry->m_xMinValue.x, pxEntry->m_xMaxValue.x);
		pxEntry->m_xValue.y = clamp(pxEntry->m_xValue.y, pxEntry->m_xMinValue.y, pxEntry->m_xMaxValue.y);
	}
	else if(p_pxEntry->m_xType == ID_SIZE)
	{
		CEntrySize* pxEntry = (CEntrySize*) p_pxEntry;
		pxEntry->m_xValue.cx = clamp(pxEntry->m_xValue.cx, pxEntry->m_xMinValue.cx, pxEntry->m_xMaxValue.cx);
		pxEntry->m_xValue.cy = clamp(pxEntry->m_xValue.cy, pxEntry->m_xMinValue.cy, pxEntry->m_xMaxValue.cy);
	}
	else if(p_pxEntry->m_xType == ID_COLOR)
	{
		CEntryColor* pxEntry = (CEntryColor*) p_pxEntry;
		pxEntry->m_xValue.m_cRed	= clamp(pxEntry->m_xValue.m_cRed,	pxEntry->m_xMinValue.m_cRed,	pxEntry->m_xMaxValue.m_cRed);
		pxEntry->m_xValue.m_cGreen	= clamp(pxEntry->m_xValue.m_cGreen, pxEntry->m_xMinValue.m_cGreen,	pxEntry->m_xMaxValue.m_cGreen);
		pxEntry->m_xValue.m_cBlue	= clamp(pxEntry->m_xValue.m_cBlue,	pxEntry->m_xMinValue.m_cBlue,	pxEntry->m_xMaxValue.m_cBlue);
		pxEntry->m_xValue.m_cAlpha	= clamp(pxEntry->m_xValue.m_cAlpha, pxEntry->m_xMinValue.m_cAlpha,	pxEntry->m_xMaxValue.m_cAlpha);
	}
}

//---------------------------------------------------------------------------------------------------------------------
/**
	finds entry with given path and makes sure it is of given type
	returns 0 if entry was not found or is not of expected type
*/
const CConfigFile::CEntry*
CConfigFile::FindEntry(const string& p_sPath, CFourCC p_xType) const
{
	if(m_axParameters.size() == 0)
	{
		assert(false);
		return 0;
	}

	map<string, CEntry*>::const_iterator i;
    i = m_axParameters.find(p_sPath);
	if(i == m_axParameters.end())
	{
		assert(false);
        return 0;
	}

	if(i->second->m_xType != p_xType)
	{
		assert(false);
		return 0;
	}

	return i->second;
}
//---------------------------------------------------------------------------------------------------------------------
/**
	finds entry with given path and makes sure it is of given type
	returns 0 if entry was not found or is not of expected type
*/
CConfigFile::CEntry*
CConfigFile::FindEntry(const string& p_sPath, CFourCC p_xType)
{
	if(m_axParameters.size() == 0)
	{
		assert(false);
		return 0;
	}

	map<string, CEntry*>::iterator i;
    i = m_axParameters.find(p_sPath);
	if(i == m_axParameters.end())
	{
		assert(false);
        return 0;
	}

	if(i->second->m_xType != p_xType)
	{
		assert(false);
		return 0;
	}

	return i->second;
}
//---------------------------------------------------------------------------------------------------------------------
/**
	finds entry with given path; returns 0 if entry was not found 
*/
const CConfigFile::CEntry*
CConfigFile::FindEntry(const string& p_sPath) const
{
	if(m_axParameters.size() == 0)
	{
		assert(false);
		return 0;
	}

	map<string, CEntry*>::const_iterator i;
    i = m_axParameters.find(p_sPath);
	if(i == m_axParameters.end())
	{
		assert(false);
        return 0;
	}

	return i->second;
}
//---------------------------------------------------------------------------------------------------------------------

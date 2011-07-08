#include "Application/stdinc.h"
#include "MConsoleResponse.h"

#include "tinyxml.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
CMessage::MessageType		
CMConsoleResponse::GetMessageType() const
{
	return MTYPE_CONSOLE_RESP;
}

//---------------------------------------------------------------------------------------------------------------------
std::string		
CMConsoleResponse::ToXMLString() const
{
	return "";
}    

//---------------------------------------------------------------------------------------------------------------------
bool		
CMConsoleResponse::FromXMLElement(const TiXmlElement* p_pxXMLElement)
{
	if(string(p_pxXMLElement->Value()) != "m301")
	{
		assert(false);
		return false;
	}

	// parse attributes

	const TiXmlAttribute* pxAttrib = p_pxXMLElement->FirstAttribute();
	while(pxAttrib)
	{
		string sName = pxAttrib->Name();
		if(sName == "time")
		{
			m_iTime = _atoi64(pxAttrib->Value());
		}
		else if(sName == "text")
		{
			m_sText = pxAttrib->Value(); 
		}

		pxAttrib = pxAttrib->Next();
	}

	// parse children

	const TiXmlElement* pxElement = p_pxXMLElement->FirstChildElement();
	while(pxElement)
	{
		string sName = pxElement->Value();
		if(sName == "m303")		// answer
		{
			m_axAnswers.push_back(CMConsoleAnswer());
			m_axAnswers[m_axAnswers.size() - 1].FromXMLElement(pxElement);
		}
		else
		{
			DebugPrint("Warning: CMConsoleResponse::FromXMLElement(): Message Type %s - parsing not implemented", sName.c_str());
		}

		pxElement = pxElement->NextSiblingElement();
	}

	

	return true;
}

//---------------------------------------------------------------------------------------------------------------------

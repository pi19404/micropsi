#include "Application/stdinc.h"
#include "MAgentResponse.h"

#include "tinyxml.h"
#include "baselib/xmlutils.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
CMessage::MessageType		
CMAgentResponse::GetMessageType() const
{
	return CMessage::MTYPE_AGENT_RESP;
}

//---------------------------------------------------------------------------------------------------------------------
std::string		
CMAgentResponse::ToXMLString() const
{
	assert(false); // not implemented yet
	return "";
}

//---------------------------------------------------------------------------------------------------------------------
bool			
CMAgentResponse::FromXMLElement(const TiXmlElement* p_pxXMLElement)
{
	if(string(p_pxXMLElement->Value()) != "m201")
	{
		assert(false);
		return false;
	}

	const TiXmlAttribute* pxAttrib = p_pxXMLElement->FirstAttribute();
	while(pxAttrib)
	{
		string sName = pxAttrib->Name();
		if(sName == "time")
		{
			m_iTime = _atoi64(pxAttrib->Value());
		}
		else if(sName == "type")
		{
			m_iType = atoi(pxAttrib->Value());
		}
		else if(sName == "ctext")
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
		if(sName == "m203")		// answer
		{
			m_axActionResponses.push_back(CMAgentActionResponse());
			m_axActionResponses[m_axActionResponses.size() - 1].FromXMLElement(pxElement);
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
const std::string&		
CMAgentResponse::GetText() const
{
	return m_sText;
}
//---------------------------------------------------------------------------------------------------------------------
int	
CMAgentResponse::GetResponseType() const
{
	return m_iType;
}
//---------------------------------------------------------------------------------------------------------------------

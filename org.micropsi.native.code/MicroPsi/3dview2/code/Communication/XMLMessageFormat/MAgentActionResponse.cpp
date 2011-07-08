#include "Application/stdinc.h"
#include "MAgentActionResponse.h"

#include "baselib/str.h"
#include "tinyxml.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
CMAgentActionResponse::CMAgentActionResponse()
{
	m_iTicket = -1;
	m_sAgentName = "";
	m_fSuccess = -1;
}

//---------------------------------------------------------------------------------------------------------------------
CMAgentActionResponse::~CMAgentActionResponse()
{
}

//---------------------------------------------------------------------------------------------------------------------
void
CMAgentActionResponse::SetAgentName(const std::string& p_rsAgentName)
{
	m_sAgentName = p_rsAgentName;
}

//---------------------------------------------------------------------------------------------------------------------
std::string
CMAgentActionResponse::GetAgentName() const
{
	return m_sAgentName;
}

//---------------------------------------------------------------------------------------------------------------------
CMessage::MessageType		
CMAgentActionResponse::GetMessageType() const
{
	return MTYPE_AGENT_ACTIONRESPONSE;
}

//---------------------------------------------------------------------------------------------------------------------
std::string		
CMAgentActionResponse::ToXMLString() const
{
	CStr sRes = CStr::Create("<m%d agent=\"%s\" succ=\"%.1f\" ticket=\"%d\"/>", 
		MTYPE_AGENT_ACTION, m_sAgentName.c_str(), m_fSuccess, m_iTicket); 
	return sRes.c_str();
}

//---------------------------------------------------------------------------------------------------------------------
bool			
CMAgentActionResponse::FromXMLElement(const TiXmlElement* p_pxXMLElement)
{
	if(string(p_pxXMLElement->Value()) != "m203")
	{
		assert(false);
		return false;
	}

	// parse attributes

	const TiXmlAttribute* pxAttrib = p_pxXMLElement->FirstAttribute();
	while(pxAttrib)
	{
		string sName = pxAttrib->Name();
		if(sName == "agent")
		{
			m_sAgentName = pxAttrib->Value();
		}
		else if(sName == "succ")
		{
			m_fSuccess = (float) atof(pxAttrib->Value());
		}
		else if(sName == "ticket")
		{
			m_iTicket = atoi(pxAttrib->Value());
		}

		pxAttrib = pxAttrib->Next();
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------

#include "Application/stdinc.h"
#include "MAgentRequest.h"
#include "baselib/str.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
CMAgentRequest::CMAgentRequest(const std::string& p_rsAgentName, RequestType p_eRequest, CMAgentAction* p_pxAction, const std::string& p_rsAgentType)
{
	m_sAgentName	= p_rsAgentName;
	m_sAgentType	= p_rsAgentType;
	m_eRequestType	= p_eRequest;
	m_pxAgentAction = 0;

	SetAction(p_pxAction);
}

//---------------------------------------------------------------------------------------------------------------------
CMAgentRequest::CMAgentRequest(const CMAgentRequest& p_rxOther)
{
	m_sAgentName	= p_rxOther.m_sAgentName;
	m_eRequestType  = p_rxOther.m_eRequestType;
	m_sAgentType	= p_rxOther.m_sAgentType;
	if(p_rxOther.m_pxAgentAction)
	{
		m_pxAgentAction = new CMAgentAction(*(p_rxOther.m_pxAgentAction));
	}
	else
	{
		m_pxAgentAction = 0;
	}
}

//---------------------------------------------------------------------------------------------------------------------
CMAgentRequest::~CMAgentRequest()
{
	delete m_pxAgentAction;
}

//---------------------------------------------------------------------------------------------------------------------
void
CMAgentRequest::SetAction(CMAgentAction* p_pxAction)
{
	delete m_pxAgentAction;
	if(p_pxAction)
	{
		m_pxAgentAction = new CMAgentAction(*p_pxAction);
		m_pxAgentAction->SetAgentName(m_sAgentName);
	}
	else
	{
		m_pxAgentAction = 0;
	}
}

//---------------------------------------------------------------------------------------------------------------------
CMessage::MessageType		
CMAgentRequest::GetMessageType() const
{
	return MTYPE_AGENT_REQ;
}

//---------------------------------------------------------------------------------------------------------------------
std::string		
CMAgentRequest::ToXMLString() const
{
	string sAction;
	if(m_pxAgentAction)
	{
		sAction = m_pxAgentAction->ToXMLString();
	}

	CStr sRes;
	if(m_eRequestType != AGENTREQ_REGISTER)
	{
		sRes = CStr::Create("<m%d type=\"%d\" agent=\"%s\">%s</m%d>", 
			MTYPE_AGENT_REQ, m_eRequestType, m_sAgentName.c_str(), sAction.c_str(), MTYPE_AGENT_REQ);
	}
	else
	{
		sRes = CStr::Create("<m%d type=\"%d\" agent=\"%s\" atype=\"%s\">%s</m%d>", 
			MTYPE_AGENT_REQ, m_eRequestType, m_sAgentName.c_str(), m_sAgentType.c_str(), sAction.c_str(), MTYPE_AGENT_REQ);
	}
	return sRes.c_str();
}

//---------------------------------------------------------------------------------------------------------------------
bool			
CMAgentRequest::FromXMLElement(const TiXmlElement* p_pxXMLElement)
{
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
const std::string&		
CMAgentRequest::GetAgentName() const
{
	return m_sAgentName;
}
//---------------------------------------------------------------------------------------------------------------------

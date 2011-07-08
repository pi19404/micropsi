#include "Application/stdinc.h"
#include "MAgentAction.h"

#include "baselib/str.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
CMAgentAction::CMAgentAction(std::string p_sActionType, __int64 p_iTargetObject, long p_iTicket)
{
	m_sActionType = p_sActionType;
	m_iTargetObject = p_iTargetObject;
	m_iTicket = p_iTicket;
	m_sAgentName = "";
}

//---------------------------------------------------------------------------------------------------------------------
CMAgentAction::~CMAgentAction()
{
}

//---------------------------------------------------------------------------------------------------------------------
void
CMAgentAction::SetAgentName(const std::string& p_rsAgentName)
{
	m_sAgentName = p_rsAgentName;
}

//---------------------------------------------------------------------------------------------------------------------
std::string
CMAgentAction::GetAgentName() const
{
	return m_sAgentName;
}

//---------------------------------------------------------------------------------------------------------------------
CMessage::MessageType		
CMAgentAction::GetMessageType() const
{
	return MTYPE_AGENT_ACTION;
}

//---------------------------------------------------------------------------------------------------------------------
std::string		
CMAgentAction::ToXMLString() const
{
	string sParams = m_xParameters.ToXMLString();
	CStr sRes = CStr::Create("<m%d type=\"%s\" agent=\"%s\" oid=\"%I64d\" ticket=\"%d\"%s/>", 
		MTYPE_AGENT_ACTION, m_sActionType.c_str(), m_sAgentName.c_str(), m_iTargetObject, m_iTicket, sParams.c_str()); 
	return sRes.c_str();
}

//---------------------------------------------------------------------------------------------------------------------
bool			
CMAgentAction::FromXMLElement(const TiXmlElement* p_pxXMLElement)
{
	return false;
}

//---------------------------------------------------------------------------------------------------------------------
CParameterList&			
CMAgentAction::Parameters()
{
	return m_xParameters;
}

//---------------------------------------------------------------------------------------------------------------------
const CParameterList&	
CMAgentAction::Parameters() const
{
	return m_xParameters;
}

//---------------------------------------------------------------------------------------------------------------------
void
CMAgentAction::SetActionType(const std::string& p_rsActionName)
{
	m_sActionType = p_rsActionName;
}
//---------------------------------------------------------------------------------------------------------------------
void
CMAgentAction::SetTargetObject(__int64 p_iTargetObject)
{
	m_iTargetObject = p_iTargetObject;
}
//---------------------------------------------------------------------------------------------------------------------

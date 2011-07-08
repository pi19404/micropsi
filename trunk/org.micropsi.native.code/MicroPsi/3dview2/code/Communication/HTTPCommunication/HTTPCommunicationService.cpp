#include "Application/stdinc.h"
#include "Communication/HTTPCommunication/HTTPCommunicationService.h"

#include "Communication/HTTPCommunication/HTTPCommunicationLine.h"
#include "Communication/XMLMessageFormat/MAgentRequest.h"
#include "Communication/XMLMessageFormat/MConsoleResponse.h"
#include "Communication/XMLMessageFormat/MAgentResponse.h"
#include "Communication/XMLCommunication/XMLRemoteAgentController.h"
#include "Communication/XMLCommunication/XMLWorldController.h"

using std::string;
using std::vector;

//---------------------------------------------------------------------------------------------------------------------
CHTTPCommunicationService::CHTTPCommunicationService(CWorld* p_pxWorld, std::string p_sComponentName) : CXMLCommunicationService(p_sComponentName)
{
	m_pxAgentServiceComLine = 0;
	m_pxWorldComLine		= 0;
	m_pxXMLRemoteAgent		= 0;
	m_pxXMLWorldController	= 0;
	m_pxWorld = p_pxWorld;
} 

//---------------------------------------------------------------------------------------------------------------------
CHTTPCommunicationService::~CHTTPCommunicationService()
{
	CloseConnection();
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CHTTPCommunicationService::OpenConnection(std::string p_sServerIPorName, int p_iServerPort, std::string p_sConsoleServiceURL, std::string p_sAgentServiceURL)
{
	CloseConnection();

	m_sServerNameOrIP	= p_sServerIPorName;
	m_iServerPort		= p_iServerPort;

	if(p_sConsoleServiceURL.empty())
	{
		m_sConsoleServiceURL = "/micropsi/consoleservice";
	}
	else
	{
		m_sConsoleServiceURL = p_sConsoleServiceURL;
	}

	if(p_sAgentServiceURL.empty())
	{
		m_sAgentServiceURL = "/micropsi/avatarservice";
	}
	else
	{
		m_sAgentServiceURL = p_sAgentServiceURL;
	}

	m_pxWorldComLine		= new CHTTPCommunicationLine(m_sServerNameOrIP, m_iServerPort, m_sConsoleServiceURL, "console", GetComponentName());
	m_pxXMLWorldController	= new CXMLWorldController(this, m_pxWorld);

	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool				
CHTTPCommunicationService::CreateRemoteAgent()
{
	// you must have a connection to a world server in order to create an agent
	assert(m_pxWorldComLine);
	if(!m_pxWorldComLine)
	{
		return false;
	}

	m_pxAgentServiceComLine	= new CHTTPCommunicationLine(m_sServerNameOrIP, m_iServerPort, m_sAgentServiceURL, "agent", GetComponentName());
	m_pxXMLRemoteAgent		= new CXMLRemoteAgentController(this);
	
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CHTTPCommunicationService::CloseConnection()
{
	delete m_pxXMLRemoteAgent;
	m_pxXMLRemoteAgent = 0;

	delete m_pxXMLWorldController;
	m_pxXMLWorldController = 0;

	delete m_pxAgentServiceComLine;
	m_pxAgentServiceComLine = 0;

	delete m_pxWorldComLine;
	m_pxWorldComLine = 0;
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CHTTPCommunicationService::IsConnected()
{
	return m_pxAgentServiceComLine || m_pxWorldComLine;
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CHTTPCommunicationService::IsWorldResponding() const
{
	return m_pxWorldComLine && m_pxWorldComLine->IsResponding();
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CHTTPCommunicationService::IsAgentServiceResponding() const
{
	return m_pxAgentServiceComLine && m_pxAgentServiceComLine->IsResponding();
}

//---------------------------------------------------------------------------------------------------------------------
void	
CHTTPCommunicationService::Tick(double p_dCurrentTime)
{
	string sMessage;
	if(m_pxAgentServiceComLine)
	{
		while(m_pxAgentServiceComLine->PopMessage(sMessage))
		{
			ReceiveMessage(sMessage);
		}
	}
	if(m_pxWorldComLine)
	{
		while(m_pxWorldComLine->PopMessage(sMessage))
		{
			ReceiveMessage(sMessage);
		}
	}
	if(m_pxXMLRemoteAgent)
	{
		m_pxXMLRemoteAgent->Tick(p_dCurrentTime);
	}
	if(m_pxXMLWorldController)
	{
		m_pxXMLWorldController->Tick(p_dCurrentTime);
	}
}

//---------------------------------------------------------------------------------------------------------------------
/**
	sends a message
	if p_bDropIfBusy is set to true, the message can be dropped if the communication line is too busy
	returns true if the message was sent; false if it could not be sent or was dropped
*/
bool	
CHTTPCommunicationService::SendMsg(const CRootMessage& p_xrMessage, bool p_bDropIfBusy)
{
	if(p_xrMessage.GetMessageType() == CMessage::MTYPE_AGENT_REQ)
	{
		if(m_pxAgentServiceComLine)
		{
			if(!p_bDropIfBusy  ||  m_pxAgentServiceComLine->GetOutQueueSize() == 0)
			{
				return m_pxAgentServiceComLine->SendMsg(p_xrMessage.ToXMLString());
			}
			else
			{
				// dropped because line is busy
				return false;
			}
		}
		else
		{
			// no com line
			return false;
		}
	}
	else if(p_xrMessage.GetMessageType() == CMessage::MTYPE_CONSOLE_REQ)
	{
		if(m_pxWorldComLine)
		{
			if(!p_bDropIfBusy  ||  m_pxWorldComLine->GetOutQueueSize() == 0)
			{
				return m_pxWorldComLine->SendMsg(p_xrMessage.ToXMLString());
			}
			else
			{
				// dropped because line is busy
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	else
	{
		assert(false);		// cannot send that message type ...
		return false;
	}

	return false;
}
//---------------------------------------------------------------------------------------------------------------------
bool	
CHTTPCommunicationService::HandleMessage(const CRootMessage& p_xrMessage, const std::string& p_rsComponent, int p_iType)
{
	if(p_xrMessage.GetMessageType() == CMessage::MTYPE_AGENT_RESP)
	{
		if(p_xrMessage.GetMessageType() == CMessage::MTYPE_AGENT_RESP)
		{
			const CMAgentResponse& rxAgentResponse = (const CMAgentResponse&) p_xrMessage;
			if(rxAgentResponse.GetResponseType() == CMAgentResponse::AGENTRESP_REGISTRATION)
			{
				std::string sText = rxAgentResponse.GetText();
				DebugPrint("David: agent component has been renamed by server to %s", sText.c_str());
				m_pxAgentServiceComLine->SetSenderComponentID(sText);
			}
		}

		return m_pxXMLRemoteAgent->HandleMessage(p_xrMessage, p_rsComponent, p_iType);
	}
	else if(p_xrMessage.GetMessageType() == CMessage::MTYPE_CONSOLE_RESP)
	{
		std::string sText = ((const CMConsoleResponse&) p_xrMessage).GetText();
		if(!sText.empty())
		{
			DebugPrint("David: component has been renamed by server to %s", sText.c_str());
			m_pxWorldComLine->SetSenderComponentID(sText);
			SetComponentName(sText);
		}

		return m_pxXMLWorldController->HandleMessage(p_xrMessage, p_rsComponent, p_iType);
	}
	else
	{
		DebugPrint("Warning: CHTTPCommunicationService::ReceiveMessage: message type %d is not handled", p_xrMessage.GetMessageType());
		return false;
	}
}

//---------------------------------------------------------------------------------------------------------------------

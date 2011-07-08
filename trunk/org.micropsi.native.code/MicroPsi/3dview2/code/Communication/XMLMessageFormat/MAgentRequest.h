#ifndef MAGENTREQUEST_H_INCLUDED
#define MAGENTREQUEST_H_INCLUDED

#include "RootMessage.h"
#include "MAgentAction.h"
#include "Question.h"

class CMAgentRequest : public CRootMessage
{
public:
	enum RequestType
	{
		AGENTREQ_REGISTER	= 0,
		AGENTREQ_NORMALOP	= 1,
		AGENTREQ_UNREGISTER = 2
	};

	CMAgentRequest(	const std::string& p_rsAgentName, 
					RequestType p_eRequest = AGENTREQ_NORMALOP, 
					CMAgentAction* p_pxAction = 0,
					const std::string& p_rsAgentType = "SteamVehicleAgentObject");
	CMAgentRequest( const CMAgentRequest& p_rxOther);
	virtual ~CMAgentRequest();

	void					SetAction(CMAgentAction* p_pxAction);

	virtual MessageType		GetMessageType() const;
	virtual std::string		ToXMLString() const;

	virtual bool			FromXMLElement(const TiXmlElement* p_pxXMLElement);

	const std::string&		GetAgentName() const;

private:

	std::string				m_sAgentName;
	RequestType				m_eRequestType;
	CMAgentAction*			m_pxAgentAction;
	std::string				m_sAgentType;
};

#endif // MAGENTREQUEST_H_INCLUDED

#ifndef MAGENTRESPONSE_H_INCLUDED
#define MAGENTRESPONSE_H_INCLUDED

#include "RootMessage.h"
#include "MAgentActionResponse.h"

class CMAgentResponse : public CRootMessage
{
public:

	enum ResponseType
	{
		AGENTRESP_REGISTRATION = 0,
		AGENTRESP_ERROR = 1,
		AGENTRESP_KICK = 2,
		AGENTRESP_NORMALOP = 3
	};

	virtual MessageType		GetMessageType() const;
	virtual std::string		ToXMLString() const;

	virtual bool			FromXMLElement(const TiXmlElement* p_pxXMLElement);

	const std::string&		GetText() const;
	int						GetResponseType() const;

private:

	__int64			m_iTime;				///< time when message was processes
	int				m_iType;				///< type
	std::string		m_sText;				///< some textual response

	std::vector<CMAgentActionResponse>	m_axActionResponses;		///< list of responses to actions						
};

#endif // MAGENTRESPONSE_H_INCLUDED

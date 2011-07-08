#ifndef MAGENTACTIONRESPONSE_H_INCLUDED
#define MAGENTACTIONRESPONSE_H_INCLUDED

#include "Message.h"
#include "ParameterList.h"

class CMAgentActionResponse : public CMessage
{
public:    

	CMAgentActionResponse();
	virtual ~CMAgentActionResponse();

	virtual MessageType		GetMessageType() const;
	virtual std::string		ToXMLString() const;

	virtual bool			FromXMLElement(const TiXmlElement* p_pxXMLElement);

	void					SetAgentName(const std::string& p_rsAgentName);
	std::string				GetAgentName() const;

private:

	float			m_fSuccess;
	std::string		m_sAgentName;
	long			m_iTicket;
};

#endif // MAGENTACTIONRESPONSE_H_INCLUDED

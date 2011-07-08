#ifndef MAGENTACTION_H_INCLUDED
#define MAGENTACTION_H_INCLUDED

#include "Message.h"
#include "ParameterList.h"

class CMAgentAction : public CMessage
{
public:    

	CMAgentAction(std::string p_sActionType = "NOOP", __int64 p_iTargetObject = -1, long p_iTicket = -1);
	virtual ~CMAgentAction();

	virtual MessageType		GetMessageType() const;
	virtual std::string		ToXMLString() const;

	virtual bool			FromXMLElement(const TiXmlElement* p_pxXMLElement);

	void					SetActionType(const std::string& p_rsActionName);

	void					SetTargetObject(__int64 p_iTargetObject);

	void					SetAgentName(const std::string& p_rsAgentName);
	std::string				GetAgentName() const;

	CParameterList&			Parameters();
	const CParameterList&	Parameters() const;

private:

	CParameterList	m_xParameters;
	std::string		m_sActionType;
	__int64			m_iTargetObject;
	std::string		m_sAgentName;
	long			m_iTicket;
};

#endif // MAGENTACTION_H_INCLUDED

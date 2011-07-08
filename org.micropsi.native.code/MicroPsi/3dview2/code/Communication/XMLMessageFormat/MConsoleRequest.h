#ifndef MCONSOLEREQUEST_H_INCLUDED
#define MCONSOLEREQUEST_H_INCLUDED

#include "RootMessage.h"
#include "MConsoleQuestion.h"
#include <vector>

class CMConsoleRequest : public CRootMessage
{
public:

	enum RequestType
	{
		RT_FIRST	= 0,
		RT_NORMAL	= 1,
		RT_LAST		= 2
	};

	CMConsoleRequest(RequestType p_eRequestType = RT_NORMAL);
	~CMConsoleRequest();

	void					AddQuestion(const CMConsoleQuestion& p_rxQuestion);

	virtual MessageType		GetMessageType() const;
	virtual std::string		ToXMLString() const;   

	virtual bool			FromXMLElement(const TiXmlElement* p_pxXMLElement);

private:

	std::vector<CMConsoleQuestion>	m_axQuestions;			///< array with questions
	RequestType						m_eRequestType;			///< is this the first request we are making?
};

#endif // MCONSOLEREQUEST_H_INCLUDED

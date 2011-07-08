#ifndef MCONSOLERESPONSE_H_INCLUDED
#define MCONSOLERESPONSE_H_INCLUDED

#include "RootMessage.h"
#include "MConsoleAnswer.h"
#include <vector>

class CMConsoleResponse : public CRootMessage
{
public:

	virtual MessageType					GetMessageType() const;
	virtual std::string					ToXMLString() const; 

	virtual bool						FromXMLElement(const TiXmlElement* p_pxXMLElement);

	const std::vector<CMConsoleAnswer>&	GetAnswers() const;	
	const __int64						GetTime() const;
	const std::string&					GetText() const;

private:

	__int64							m_iTime;			///< time (simulation step) of the response
	std::string						m_sText;			///< text - contains a new name for the component if server had to rename us

	std::vector<CMConsoleAnswer>	m_axAnswers;		///< list of answers						
};

#include "MConsoleResponse.inl"

#endif // MCONSOLERESPONSE_H_INCLUDED

#ifndef ROOTMESSAGE_H_INCLUDED
#define ROOTMESSAGE_H_INCLUDED

#include "Message.h"

class CRootMessage : public CMessage
{
public:

	CRootMessage();
	virtual ~CRootMessage();

	virtual MessageType		GetMessageType() const = 0;
	virtual std::string		ToXMLString() const = 0;

	virtual bool			FromXMLElement(const TiXmlElement* p_pxXMLElement) = 0;
};

#endif // ROOTMESSAGE_H_INCLUDED

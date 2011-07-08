#pragma once
#ifndef MESSAGE_H_INCLUDED
#define MESSAGE_H_INCLUDED

#include <string>

class TiXmlElement;

class CMessage
{
public: 

	enum MessageType
	{
		MTYPE_COMMON_CONFIRMATION	= 100,
		MTYPE_COMMON_VERSION		= 101,
		MTYPE_COMMON_TOUCH			= 102,

		MTYPE_AGENT_REQ				= 200,
		MTYPE_AGENT_RESP			= 201,
		MTYPE_AGENT_ACTION			= 202,
		MTYPE_AGENT_ACTIONRESPONSE	= 203,
		MTYPE_AGENT_PERCEPTIONREQ	= 204,
		MTYPE_AGENT_PERCEPTIONRESP	= 205,
		MTYPE_AGENT_PERCEPTIONVALUE = 206,
		MTYPE_AGENT_PERCEPT			= 207,


		MTYPE_CONSOLE_REQ			= 300,	
		MTYPE_CONSOLE_RESP			= 301,
		MTYPE_CONSOLE_QUESTION		= 302,	
		MTYPE_CONSOLE_ANSWER		= 303,
		MTYPE_CONSOLE_TREENODE		= 304,
		
		MTYPE_SERVER_UPDATEWORLD	= 400,
		MTYPE_SERVER_AGENTREQUEST	= 401,

		MTYPE_TIMER_TICK			= 500,

		MTYPE_WORLD_RESPONSE		= 600
	};

	CMessage() {}
	virtual ~CMessage() {};

	virtual MessageType		GetMessageType() const = 0;

	virtual std::string		ToXMLString() const = 0;
	virtual bool			FromXMLElement(const TiXmlElement* p_pxXMLElement) = 0;
};

#endif // ifndef MESSAGE_H_INCLUDED


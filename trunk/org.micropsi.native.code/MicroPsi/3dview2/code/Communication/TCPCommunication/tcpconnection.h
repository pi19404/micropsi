// connection.h

#ifndef CONNECTION_H_INCLUDED
#define CONNECTION_H_INCLUDED

#include "TCPSocket.h"
#include "Communication/TCPCommunication/tcpmessagebuffer.h"

class CTCPCommunicationService;

class CTCPConnection
{
public:
	CTCPConnection(CTCPSocket& p_xrSocket, const char* p_pcServer, int p_iPort, CTCPCommunicationService* p_pxComService);
	virtual ~CTCPConnection();

	void	Tick();
	bool	IsConnected();

private:
	CTCPSocket&			m_xrSocket;							///< socket for communication
	CInMessageBuffer*	m_pxIncompleteMsg;					///< message currently being received
	int					m_iMsgWritePos;						///< write position in the message

	CTCPCommunicationService*	m_pxCommunicationService;	///< pointer to the communication service that receives all the messages
};

#endif // CONNECTION_H_INCLUDED


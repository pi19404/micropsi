#include "Application/stdinc.h"
#include "Communication/TCPCommunication/tcpconnection.h"
#include "Communication/TCPCommunication/tcpcommunicationservice.h"

#include <string.h>

//---------------------------------------------------------------------------------------------------------------------
CTCPConnection::CTCPConnection(CTCPSocket& p_xrSocket, const char* p_pcServer, int p_iPort, CTCPCommunicationService* p_pxComService) : m_xrSocket(p_xrSocket)
{
	m_xrSocket.Open(p_pcServer, p_iPort);
	m_pxIncompleteMsg = 0;
	m_pxCommunicationService = p_pxComService;
}


//---------------------------------------------------------------------------------------------------------------------
CTCPConnection::~CTCPConnection()
{
	m_xrSocket.Close();
	delete m_pxIncompleteMsg;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CTCPConnection::Tick()
{
	if(!m_xrSocket.IsConnected())
	{
		return;
	}

	if(m_pxIncompleteMsg)
	{
		int iMissingBytes = m_pxIncompleteMsg->GetBufferSize() - m_iMsgWritePos;
		int r = m_xrSocket.Receive(m_pxIncompleteMsg->GetBuffer() + m_iMsgWritePos, iMissingBytes);
		if(r == iMissingBytes)
		{
			// message complete!
			m_pxCommunicationService->ProcessMsg(*m_pxIncompleteMsg);
			delete m_pxIncompleteMsg;
			m_pxIncompleteMsg = 0;
		}
		else
		{
			m_iMsgWritePos += r;
		}
	}
	
	char acHeader[5];

	// while there is still a complete header...
	while(m_xrSocket.DataPending() >= sizeof(acHeader))
	{
		// read header
		int r = m_xrSocket.Receive(acHeader, sizeof(acHeader));
//		DebugPrint("%s", acHeader);

		// check if it is a header
		if(!strncmp(acHeader, "MSG", 3) == 0)
		{
			DebugPrint("lost in stream... terminating connection");
			m_xrSocket.Close();
			return;
		}

		int iMsgSize = ((acHeader[3] << 8) + acHeader[4]) - sizeof(acHeader);
//		DebugPrint("receiving message with size %d", iMsgSize);
		m_pxIncompleteMsg = new CInMessageBuffer(iMsgSize);


		// read as much as possible
		if(m_xrSocket.DataPending() > 0)
		{
			m_iMsgWritePos = m_xrSocket.Receive(m_pxIncompleteMsg->GetBuffer(), iMsgSize);
			if(m_iMsgWritePos == iMsgSize)
			{
				// message complete!
				m_pxCommunicationService->ProcessMsg(*m_pxIncompleteMsg);
				delete m_pxIncompleteMsg;
				m_pxIncompleteMsg = 0;
			}
		}
	}
}


//---------------------------------------------------------------------------------------------------------------------
/**
	\return true, if this connection is actually connected to a server
*/
bool 
CTCPConnection::IsConnected()
{
	return m_xrSocket.IsConnected();
}

//---------------------------------------------------------------------------------------------------------------------

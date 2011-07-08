#ifndef TCPCOMMUNICATIONSERVICE_H_INCLUDED
#define TCPCOMMUNICATIONSERVICE_H_INCLUDED

#include <string>
#include "Communication/TCPCommunication/tcpmessagebuffer.h"

class CTCPConnection;
class CTCPSocket;
class CWorld;

class CTCPCommunicationService
{
public:

    CTCPCommunicationService(CWorld* p_pxWorld); 
	virtual ~CTCPCommunicationService();

	bool		OpenWorldConnection(const std::string& p_rsWorldServer, int p_iWorldServerPort);
	void		CloseWorldConnection();
	bool		IsWorldServerConnected() const;

	void		Tick();

private:

	/// messages from our CTCPConnection get send here
	void				ProcessMsg(CInMessageBuffer& p_kxrMsg);

	CTCPConnection*		m_pxConnection;				///< connection to world server
	CTCPSocket*			m_pxSocket;					///< socket for connection to world server
	CWorld*				m_pxWorld;					///< the world we are controlling

	friend class CTCPConnection;
};

#endif // TCPCOMMUNICATIONSERVICE_H_INCLUDED

// tcpsocketwin.h
//
// author: David.Salz@snafu.de
// created: June 15, 2003 



#ifndef TCPSOCKETWIN_H_INCLUDED
#define TCPSOCKETWIN_H_INCLUDED

#include "TCPSocket.h"
#include "Winsock2.h"

class CTCPSocketWin : public CTCPSocket 
{
public:
	CTCPSocketWin();
	virtual ~CTCPSocketWin();

	virtual bool		Open(const char* p_pcServer, int p_iPort);
	virtual void		Close();
	virtual bool		Send(const char* p_pcData, int p_iDataSize);
	virtual int			Receive(char* po_pcBuffer, int p_iBufferSize);
	virtual int			DataPending() const;
	virtual bool		IsConnected() const			{ return m_bConnected; };
	virtual void		SetBlocking(bool p_bBlocking);

private:

	static const char*	GetErrorAsString(int p_iErrorCode);

	bool				m_bConnected;				///< true if connected
	SOCKET				m_xSocket;					///< socket for this connection

};

#endif // TCPSOCKETWIN_H_INCLUDED


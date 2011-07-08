// tcpsocket.h
//
// author: David.Salz@snafu.de
// created: June 15, 2003 


#ifndef TCPSOCKET_H_INCLUDED
#define TCPSOCKET_H_INCLUDED

class CTCPSocket  
{
public:

	virtual bool	Open(const char* p_pcServer, int p_iPort) = 0;
	virtual void	Close() = 0;
	virtual bool	Send(const char* p_pcData, int p_iDataSize) = 0;
	virtual int		Receive(char* po_pcBuffer, int p_iBufferSize) = 0;
	virtual int		DataPending() const = 0;
	virtual bool	IsConnected() const = 0;
	virtual void	SetBlocking(bool p_bBlocking) = 0;
};

#endif // TCPSOCKET_H_INCLUDED


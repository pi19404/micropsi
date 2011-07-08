
#include "Application/stdinc.h"
#include "stdio.h"
#include "Communication/TCPCommunication/tcpsocketwin.h"

//---------------------------------------------------------------------------------------------------------------------
CTCPSocketWin::CTCPSocketWin()
{
	m_bConnected = false;
}


//---------------------------------------------------------------------------------------------------------------------
CTCPSocketWin::~CTCPSocketWin()
{
	Close();
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CTCPSocketWin::Open(const char* p_pcServer, int p_iPort)
{
	Close();

	// init winsock

	WSADATA xWSAData;
	memset(&xWSAData, 0, sizeof(xWSAData));
	int iWSARet=WSAStartup(0x101,&xWSAData);
	if(iWSARet)
	{
		fprintf(stderr, "WinSock init failed - need at least V1.1");
		return false;
	}

	// create socket

	m_xSocket = socket(AF_INET,SOCK_STREAM,IPPROTO_TCP);
	if(m_xSocket == INVALID_SOCKET)
	{
		fprintf(stderr, "could not create socket");
		return false;
	}
	

	// create host info (DNS or IPv4 address)

	hostent* pxHostInfo;
	unsigned int uiHostAddress = inet_addr(p_pcServer);
	if(uiHostAddress == INADDR_NONE)
	{
		pxHostInfo = gethostbyname(p_pcServer);
	}
	else
	{
		pxHostInfo = gethostbyaddr((char*)&uiHostAddress,sizeof(uiHostAddress),AF_INET);
	}

	if(pxHostInfo == NULL)
	{
		closesocket(m_xSocket);
		fprintf(stderr, "could not resolve server");
		return false;
	}

	// try to connect

	struct sockaddr_in xServer;
	xServer.sin_addr.s_addr=*((unsigned long*)pxHostInfo->h_addr);
	xServer.sin_family=AF_INET;
	xServer.sin_port=htons(p_iPort);

	if(connect(m_xSocket,(struct sockaddr*)&xServer,sizeof(xServer)))
	{
		DebugPrint(GetErrorAsString(WSAGetLastError()));
		closesocket(m_xSocket);
		return false;	
	}


	m_bConnected = true;

	return true;
}


//---------------------------------------------------------------------------------------------------------------------
void 
CTCPSocketWin::Close()
{
	if(m_bConnected)
	{
		closesocket(m_xSocket);
		WSACleanup();
		m_bConnected = false;
	}
}


//---------------------------------------------------------------------------------------------------------------------
bool 
CTCPSocketWin::Send(const char* p_pcData, int p_iDataSize)
{
	if(m_bConnected)
	{
		int i = send(m_xSocket, p_pcData, p_iDataSize, 0);
		return i == p_iDataSize;
	}
	else
	{
		return false;
	}
}


//---------------------------------------------------------------------------------------------------------------------
int 
CTCPSocketWin::Receive(char* po_pcBuffer, int p_iBufferSize)
{
	int i = recv(m_xSocket, po_pcBuffer, p_iBufferSize, 0);
	return i;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	\return true if data is ready to be read
*/
int 
CTCPSocketWin::DataPending() const
{
	u_long i;
	ioctlsocket(m_xSocket, FIONREAD, &i);
	return i;
}



//---------------------------------------------------------------------------------------------------------------------
/**
	make socket blocking / non-blocking
*/
void 
CTCPSocketWin::SetBlocking(bool p_bBlocking)
{
	u_long i = p_bBlocking;
	ioctlsocket(m_xSocket, FIONBIO, &i);
}


//---------------------------------------------------------------------------------------------------------------------
const char* 
CTCPSocketWin::GetErrorAsString(int p_iErrorCode)
{
	switch (p_iErrorCode)
	{
		case WSAEINTR:				return "Interrupted system call";
		case WSAEBADF:              return "Bad file number";
//		case WSEACCESS:				return "Permission denied.";
		case WSAEFAULT:				return "Bad address.";
		case WSAEINVAL:             return "Invalid argument.";
		case WSAEMFILE:             return "Too many open files/sockets.";
		case WSAEWOULDBLOCK:        return "Operation would block.";
		case WSAEINPROGRESS:        return "Operation now in progress. This error is returned if any Windows Sockets API function is called while a blocking function is in progress.";
		case WSAEALREADY:           return "Operation already in progress.";
		case WSAENOTSOCK:           return "Socket operation on nonsocket.";
		case WSAEDESTADDRREQ:       return "Destination address required.";
		case WSAEMSGSIZE:           return "Message too long.";
		case WSAEPROTOTYPE:         return "Protocol wrong type for socket.";
		case WSAENOPROTOOPT:        return "Protocol not available/bad protocol option.";
		case WSAEPROTONOSUPPORT:    return "Protocol not supported.";
		case WSAESOCKTNOSUPPORT:    return "Socket type not supported.";
		case WSAEOPNOTSUPP:         return "Operation not supported on socket.";
		case WSAEPFNOSUPPORT:       return "Protocol family not supported.";
		case WSAEAFNOSUPPORT:       return "Address family not supported by protocol family.";
		case WSAEADDRINUSE:			return "Address already in use.";
		case WSAEADDRNOTAVAIL:      return "Cannot assign requested address.";
		case WSAENETDOWN:           return "Network is down. This error may be reported at any time if the Windows Sockets implementation detects an underlying failure.";
		case WSAENETUNREACH:		return "Network is unreachable.";
		case WSAENETRESET:          return "Network dropped connection on reset.";
		case WSAECONNABORTED:       return "Software caused connection abort.";
		case WSAECONNRESET:         return "Connection reset by peer.";
		case WSAENOBUFS:            return "No buffer space available.";
		case WSAEISCONN:            return "Socket is already connected.";
		case WSAENOTCONN:           return "Socket is not connected.";
		case WSAESHUTDOWN:          return "Cannot send after socket shutdown.";
		case WSAETOOMANYREFS:       return "Too many references: cannot splice.";
		case WSAETIMEDOUT:          return "Connection timed out.";
		case WSAECONNREFUSED:       return "Connection refused.";
		case WSAELOOP:              return "Too many levels of symbolic links.";
		case WSAENAMETOOLONG:       return "File name too long.";
		case WSAEHOSTDOWN:          return "Host is down.";
		case WSAEHOSTUNREACH:       return "No route to host.";
		case WSAENOTEMPTY:          return "Directory not empty.";
		case WSAEPROCLIM:           return "Too many processes.";
		case WSAEUSERS:             return "Too many users.";
		case WSAEDQUOT:             return "Disc quota exceeded.";
		case WSAESTALE:             return "Stale NFS file handle.";
		case WSAEREMOTE:            return "Too many levels of remote in path.";
		case WSASYSNOTREADY:        return "Network subsystem is unavailable.";
		case WSAVERNOTSUPPORTED:    return "Winsock version not supported.";
		case WSANOTINITIALISED:     return "Winsock not yet initialized.";
		case WSAEDISCON:            return "Graceful disconnect in progress.";
		case WSAENOMORE:            return "WSAENOMORE - (Winsock2)";
		case WSAECANCELLED:         return "WSAECANCELLED - (Winsock2)";
		case WSAEINVALIDPROCTABLE:  return "WSAEINVALIDPROCTABLE - (Winsock2)";
		case WSAEINVALIDPROVIDER:   return "WSAEINVALIDPROVIDER - (Winsock2)";
		case WSAEPROVIDERFAILEDINIT:return "WSAEPROVIDERFAILEDINIT - (Winsock2)";
		case WSASYSCALLFAILURE:     return "System call failure. (WS2)";
		case WSASERVICE_NOT_FOUND:  return "WSASERVICE_NOT_FOUND - (Winsock2)";
		case WSATYPE_NOT_FOUND:     return "WSATYPE_NOT_FOUND - (Winsock2)";
		case WSA_E_NO_MORE:         return "WSA_E_NO_MORE - (Winsock2)";
		case WSA_E_CANCELLED:       return "WSA_E_CANCELLED - (Winsock2)";
		case WSAEREFUSED:           return "WSAEREFUSED - (Winsock2)";
		case WSAHOST_NOT_FOUND:     return "Host not found. This message indicates that the key (name, address, and so on) was not found.";
		case WSATRY_AGAIN:          return "Non-authoritative host not found. This error may suggest that the name service itself is not functioning.";
		case WSANO_RECOVERY:        return "Non-recoverable error. This error may suggest that the name service itself is not functioning.";
		case WSANO_DATA:            return "Valid name, no data record of requested type. This error indicates that the key (name, address, and so on) was not found.";
		case WSA_NOT_ENOUGH_MEMORY: return "Insufficient memory available.";
		case WSA_OPERATION_ABORTED: return "Overlapped operation aborted.";
		case WSA_IO_INCOMPLETE:     return "Overlapped I/O object not signalled.";
		case WSA_IO_PENDING:        return "Overlapped I/O will complete later.";
		case WSA_INVALID_PARAMETER: return "One or more parameters are invalid.";
		case WSA_INVALID_HANDLE:    return "Event object handle not valid.";
		default:					return "unknown error.";
	}
}
//---------------------------------------------------------------------------------------------------------------------


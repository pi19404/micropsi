#ifndef HTTPCOMMUNICATIONLINE_H_INCLUDED
#define HTTPCOMMUNICATIONLINE_H_INCLUDED

#include <string>
#include <deque>
#include "baselib/Thread.h"
#include "baselib/CriticalSection.h"

class CHTTPConnection;

class CHTTPCommunicationLine
{
public:

    CHTTPCommunicationLine(std::string p_sServerIPorName, int p_iServerPort, std::string p_sServiceURL, std::string p_sTargetComponentID,
							std::string	p_sSenderComponentID); 
	virtual ~CHTTPCommunicationLine();

	bool			IsResponding() const;

	unsigned long	CommunicationThreadProc(int p_iUnused);
	bool			SendMsg(const std::string p_rsMessage);
	bool			PopMessage(std::string& po_rsMessage);

	void			SetXMLRequestEnvelopeBegin(std::string p_sXMLRequestEnvelopeBegin);

	void			SetSenderComponentID(std::string p_sSenderComponentID);

	/// returns the size of the queue of outbound messages
	int				GetOutQueueSize() const;

private:

	std::string					m_sServiceURL;
	bool						m_bResponding;

	CThread						m_xCommunicationThread;
	bool						m_bStopCommunicationThread;				///< set to true if you want to close the communication thread
	int							m_iNumMessagesToSendReforeShutdown;		///< when com line shuts down, x remaining messages are still sent
	CCriticalSection			m_xOutBufferAccess;						///< critical section that controls access to buffer for outbound messages
	CCriticalSection			m_xInBufferAccess;						///< critical section that controls access to buffer for inbound messages

	std::deque<std::string>		m_qsOutBuffer;							///< buffer with outbound messages
	std::deque<std::string>		m_qsInBuffer;							///< buffer with inbound messages

	std::string					m_sTargetComponentID;
	std::string					m_sSenderComponentID;

	CHTTPConnection*			m_pxHTTPConnection;
};

#endif // HTTPCOMMUNICATIONLINE_H_INCLUDED

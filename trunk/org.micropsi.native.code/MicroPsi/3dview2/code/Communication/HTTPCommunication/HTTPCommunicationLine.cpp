#include "Application/stdinc.h"
#include "Communication/HTTPCommunication/HTTPCommunicationLine.h"

#include "httplib/httpconnection.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
CHTTPCommunicationLine::CHTTPCommunicationLine(std::string p_sServerIPorName, int p_iServerPort, std::string p_sServiceURL, 
											   std::string p_sTargetComponentID, std::string p_sSenderComponentID)
{
	m_bResponding = false;

	m_pxHTTPConnection = new CHTTPConnection();
	if(!m_pxHTTPConnection->Open(p_sServerIPorName.c_str(), p_iServerPort))
	{
		delete m_pxHTTPConnection;
		m_pxHTTPConnection = 0;
		return;
	}

	m_sServiceURL = p_sServiceURL;
	m_pxHTTPConnection->SetVerboseDebugOutput(false);

	m_bStopCommunicationThread = false;
	m_iNumMessagesToSendReforeShutdown = 0;
	m_xCommunicationThread = StartNewThread(*this, CHTTPCommunicationLine::CommunicationThreadProc, 0);

	m_sTargetComponentID = p_sTargetComponentID;
	m_sSenderComponentID = p_sSenderComponentID;
} 

//---------------------------------------------------------------------------------------------------------------------
CHTTPCommunicationLine::~CHTTPCommunicationLine()
{
	if(m_pxHTTPConnection)
	{
		m_iNumMessagesToSendReforeShutdown = min(20, (int) m_qsOutBuffer.size());
		DebugPrint("David: Shutdown, %d messages left to send", m_iNumMessagesToSendReforeShutdown);
		m_bStopCommunicationThread = true;
		m_xCommunicationThread.Resume();
		m_xCommunicationThread.Wait();

		m_pxHTTPConnection->Close();
		delete m_pxHTTPConnection;
		m_pxHTTPConnection = 0;

	}
	m_bResponding = false;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CHTTPCommunicationLine::IsResponding() const
{
	return m_bResponding;
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CHTTPCommunicationLine::SendMsg(const std::string p_rsMessage)
{
	CLock xLock(&m_xOutBufferAccess);
	m_qsOutBuffer.push_back(p_rsMessage);
	m_xCommunicationThread.Resume();
//	DebugPrint("resuming thread");

	return true;
}

//---------------------------------------------------------------------------------------------------------------------
bool			
CHTTPCommunicationLine::PopMessage(std::string& po_rsMessage)
{
	CLock xLock(&m_xInBufferAccess);
	if(!m_qsInBuffer.empty())
	{
		po_rsMessage = m_qsInBuffer.front();
		m_qsInBuffer.pop_front();
		return true;
	}
	else
	{
		return false;
	}
}

//---------------------------------------------------------------------------------------------------------------------
unsigned long	
CHTTPCommunicationLine::CommunicationThreadProc(int p_iUnused)
{
	while(!(m_bStopCommunicationThread && m_iNumMessagesToSendReforeShutdown == 0))
	{
		string sMessage;
	
		if(!m_qsOutBuffer.empty())
		{
			sMessage = "<req id=\"" + m_sTargetComponentID + "\" sender=\"" + m_sSenderComponentID + "\">";

			CLock xLock(&m_xOutBufferAccess);
//			while(!m_qsOutBuffer.empty())
			{
				sMessage += m_qsOutBuffer.front();
				m_qsOutBuffer.pop_front();
			}

			sMessage += "</req>";
		}
		else
		{
			if(m_bStopCommunicationThread)
			{
				// should not be nessecary, but better safe than sorry
				break;
			}
			m_xCommunicationThread.Suspend();
//			DebugPrint("suspending thread");
		}

		if(!sMessage.empty())
		{
			string sOutput;

//			DebugPrint("Now Sending Message: %s", sMessage.c_str());

			if(m_pxHTTPConnection->PostRequest(m_sServiceURL.c_str(), sMessage.c_str()))
			{
				sOutput = m_pxHTTPConnection->GetOutput();
				m_bResponding = true;
				if(m_iNumMessagesToSendReforeShutdown > 0)
				{
					m_iNumMessagesToSendReforeShutdown--;
					DebugPrint("David: %d messages left", m_iNumMessagesToSendReforeShutdown);
				}
			}
			else
			{
				DebugPrint("Com Error: %s", m_pxHTTPConnection->GetOutput());  
				m_bResponding = false;
				// connection probably lost; stop trying to send something
				m_iNumMessagesToSendReforeShutdown = 0;
			}

			if(!sOutput.empty())
			{
				CLock xLock(&m_xInBufferAccess);
				m_qsInBuffer.push_back(sOutput);
			}
		}
	}

	DebugPrint("David: CHTTPCommunicationLine: leaving communication thread now");
	return 0;
}

//---------------------------------------------------------------------------------------------------------------------
void			
CHTTPCommunicationLine::SetSenderComponentID(std::string p_sSenderComponentID)
{
	m_sSenderComponentID = p_sSenderComponentID;
}
//---------------------------------------------------------------------------------------------------------------------
int				
CHTTPCommunicationLine::GetOutQueueSize() const
{
	return (int) m_qsOutBuffer.size();
}
//---------------------------------------------------------------------------------------------------------------------

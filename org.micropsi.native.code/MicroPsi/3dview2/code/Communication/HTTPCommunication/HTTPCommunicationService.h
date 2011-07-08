#ifndef HTTPCOMMUNICATIONSERVICE_H_INCLUDED
#define HTTPCOMMUNICATIONSERVICE_H_INCLUDED

#include <string>
#include "Communication/XMLCommunication/XMLCommunicationService.h"
#include "Communication/XMLCommunication/XMLRemoteAgentController.h"
#include "Communication/XMLCommunication/XMLWorldController.h"
class CHTTPCommunicationLine;
class CWorld;

class CHTTPCommunicationService : public CXMLCommunicationService
{
public:

    CHTTPCommunicationService(CWorld* p_pxWorld, std::string p_sComponentName); 
	virtual ~CHTTPCommunicationService();

	bool	OpenConnection(std::string p_sServerIPorName, int p_iServerPort = 8080, std::string p_sConsoleServiceURL = "", std::string p_sAgentServiceURL = "");
	bool	CreateRemoteAgent();
	void	CloseConnection();
	bool	IsConnected();

	/// return true if messages are being received from the micropsi world component 
	bool					IsWorldResponding() const;

	/// return true if messages are being received from the micropsi agent service
	bool					IsAgentServiceResponding() const;

	/// do updates; must be called once for each simulation step
	void					Tick(double p_dCurrentTime);

	/// sends a message to server
	virtual bool			SendMsg(const CRootMessage& p_xrMessage, bool p_bDropIfBusy = false);

	/// handles a received message
	virtual bool			HandleMessage(const CRootMessage& p_xrMessage, const std::string& p_rsComponent, int p_iType);

	/// get pointer to world controller
	CWorldController*		GetWorldController() const;

	/// get pointer to remote agent controller
	CRemoteAgentController*	GetRemoteAgentController() const;

private:

	std::string						m_sServerNameOrIP;
	int								m_iServerPort;
	std::string						m_sConsoleServiceURL;
	std::string						m_sAgentServiceURL;

	CWorld*							m_pxWorld;

	CHTTPCommunicationLine*			m_pxAgentServiceComLine;
	CHTTPCommunicationLine*			m_pxWorldComLine;

	CXMLRemoteAgentController*		m_pxXMLRemoteAgent;			///< an agent we control on the server; for xml over http connection
	CXMLWorldController*			m_pxXMLWorldController;		///< world controller
};

#include "HTTPCommunicationService.inl"

#endif // HTTPCOMMUNICATIONSERVICE_H_INCLUDED

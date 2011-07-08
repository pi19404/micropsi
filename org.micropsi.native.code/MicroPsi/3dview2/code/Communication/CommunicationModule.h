#ifndef CCOMMUNICATIONMODULE_H_INCLUDED
#define CCOMMUNICATIONMODULE_H_INCLUDED

#include <string>

class CTCPCommunicationService;
class CHTTPCommunicationService;
class CWorldController;
class CRemoteAgentController;
class COfflineWorldController;
class CWorld;

class CCommunicationModule
{
public:

	enum ConnectionMethod
	{
		CM_OFFLINE,					///< offline, not connected to a server
		CM_LEGACYTCP,				///< connected to a server using the old proprietary tcp protocol (deprecated)
		CM_XMLOVERHTTP				///< connected to server using XML over HTTP
	};

	CCommunicationModule(CWorld* p_pxWorld); 
	~CCommunicationModule();

	void					Tick(double p_dCurrentTime);

	bool					OpenHTTPConnection(std::string p_sServerIPorName, int p_iServerPort = 8080, std::string p_sConsoleServiceURL = "", std::string p_sAgentServiceURL = "");
	bool					OpenTCPConnection(const std::string& p_rsWorldServer, int p_iWorldServerPort);
	bool					CreateRemoteAgent();
	void					CloseConnection(); 

	ConnectionMethod		GetCurrentConnectionMethod() const;
	bool					IsConnectedToWorld() const;
	bool					IsConnectedToAgentService() const;

	/// get pointer to current world controller (depends on connection type)
	CWorldController*		GetWorldController() const;

	/// get pointer to current remote agent controller (depends on connection type)
	CRemoteAgentController*	GetRemoteAgentController() const;

private:

	std::string						m_s3DViewComponentName;			///< name under which our 3dview instance will be identified with the server
	CWorld*							m_pxWorld;						///< pointer to world; communication module will control this world
	ConnectionMethod				m_eConnectionMethod;			///< current connection method
	CTCPCommunicationService*		m_pxTCPCommunicationService;	///< legacy tcp communication service
	CHTTPCommunicationService*		m_pxHTTPCommunicationService;	///< network XML over HTTP communication service
	COfflineWorldController*		m_pxOfflineWorldController;		///< world controller for offline mode
};

#endif // ifndef CCOMMUNICATIONMODULE_H_INCLUDED
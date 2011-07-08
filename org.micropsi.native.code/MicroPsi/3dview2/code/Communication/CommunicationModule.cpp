#include "Application/stdinc.h"
#include "Communication/CommunicationModule.h"

#include "Windows.h"
#include "Communication/HTTPCommunication/HTTPCommunicationService.h"
#include "Communication/TCPCommunication/TCPCommunicationService.h"
#include "Communication/OfflineCommunication/OfflineWorldController.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
CCommunicationModule::CCommunicationModule(CWorld* p_pxWorld)
{
	m_pxWorld = p_pxWorld;
	m_pxTCPCommunicationService		= 0;	
	m_pxHTTPCommunicationService	= 0;
	m_pxOfflineWorldController = new COfflineWorldController(p_pxWorld);
	m_eConnectionMethod = CM_OFFLINE;

	char pcName[8192];
	unsigned long iSize = 8192;
	GetComputerName(pcName, &iSize);
	pcName[iSize] = 0;

	m_s3DViewComponentName = string("3dview2@") + pcName;
} 

//---------------------------------------------------------------------------------------------------------------------
CCommunicationModule::~CCommunicationModule()
{
	delete m_pxOfflineWorldController;
	CloseConnection();
}

//---------------------------------------------------------------------------------------------------------------------
void
CCommunicationModule::Tick(double p_dCurrentTime)
{
	if(m_pxTCPCommunicationService)
	{
		m_pxTCPCommunicationService->Tick();
	}
	if(m_pxHTTPCommunicationService)
	{
		m_pxHTTPCommunicationService->Tick(p_dCurrentTime);
	}
	if(m_pxOfflineWorldController)
	{
		m_pxOfflineWorldController->Tick(p_dCurrentTime);
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool					
CCommunicationModule::OpenHTTPConnection(std::string p_sServerIPorName, int p_iServerPort, std::string p_sConsoleServiceURL, std::string p_sAgentServiceURL)
{
	CloseConnection();

	m_pxHTTPCommunicationService = new CHTTPCommunicationService(m_pxWorld, m_s3DViewComponentName);
	if(m_pxHTTPCommunicationService->OpenConnection(p_sServerIPorName, p_iServerPort, p_sConsoleServiceURL, p_sAgentServiceURL))
	{
		m_eConnectionMethod = CM_XMLOVERHTTP;
		return true;
	}
	else
	{
		CloseConnection();
		return false;
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CCommunicationModule::OpenTCPConnection(const std::string& p_rsWorldServer, int p_iWorldServerPort)
{
	CloseConnection();

	m_pxTCPCommunicationService = new CTCPCommunicationService(m_pxWorld);
	if(m_pxTCPCommunicationService->OpenWorldConnection(p_rsWorldServer, p_iWorldServerPort))
	{
		m_eConnectionMethod = CM_LEGACYTCP;
		return true;
	}
	else
	{
		CloseConnection();
		return false;
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CCommunicationModule::CreateRemoteAgent()
{
	// only HTTP type connections can create agents
	assert(m_pxHTTPCommunicationService);
	if(!m_pxHTTPCommunicationService)
	{
		return false;
	}

	return m_pxHTTPCommunicationService->CreateRemoteAgent();
}

//---------------------------------------------------------------------------------------------------------------------
void					
CCommunicationModule::CloseConnection()
{
	delete m_pxHTTPCommunicationService;
	m_pxHTTPCommunicationService = 0;
	delete m_pxTCPCommunicationService;
	m_pxTCPCommunicationService = 0;
	m_eConnectionMethod = CM_OFFLINE;
} 

//---------------------------------------------------------------------------------------------------------------------
CCommunicationModule::ConnectionMethod
CCommunicationModule::GetCurrentConnectionMethod() const
{
	return m_eConnectionMethod;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CCommunicationModule::IsConnectedToWorld() const
{
	switch(m_eConnectionMethod)
	{
	case CM_LEGACYTCP:
			assert(m_pxTCPCommunicationService);
			return m_pxTCPCommunicationService->IsWorldServerConnected();

	case CM_XMLOVERHTTP:
			assert(m_pxHTTPCommunicationService);
			return m_pxHTTPCommunicationService->IsWorldResponding();
	
	default:	
			return false;
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CCommunicationModule::IsConnectedToAgentService() const
{
	switch(m_eConnectionMethod)
	{
	case CM_LEGACYTCP:
			assert(m_pxTCPCommunicationService);
			return false;

	case CM_XMLOVERHTTP:
			assert(m_pxHTTPCommunicationService);
			return m_pxHTTPCommunicationService->IsAgentServiceResponding();
	
	default:	
			return false;
	}
}

//---------------------------------------------------------------------------------------------------------------------
CWorldController*		
CCommunicationModule::GetWorldController() const
{
	switch(m_eConnectionMethod)
	{
		case CM_OFFLINE:		return m_pxOfflineWorldController;
		case CM_LEGACYTCP:		return 0;
		case CM_XMLOVERHTTP:	return m_pxHTTPCommunicationService->GetWorldController();
		default:				assert(false); return 0;
	}
}

//---------------------------------------------------------------------------------------------------------------------
CRemoteAgentController*	
CCommunicationModule::GetRemoteAgentController() const
{
	switch(m_eConnectionMethod)
	{
		case CM_OFFLINE:		return 0;
		case CM_LEGACYTCP:		return 0;
		case CM_XMLOVERHTTP:	return m_pxHTTPCommunicationService->GetRemoteAgentController();
		default:				assert(false); return 0;
	}
}
//---------------------------------------------------------------------------------------------------------------------

#include "Application/stdinc.h"
#include "Communication/XMLCommunication/XMLRemoteAgentController.h"

#include "Communication/XMLCommunication/XMLCommunicationService.h"
#include "Communication/XMLMessageFormat/MAgentRequest.h"
#include "Communication/XMLMessageFormat/MAgentResponse.h"

#include "GameLib/UserInterface/Input/InputManager.h"

//---------------------------------------------------------------------------------------------------------------------
CXMLRemoteAgentController::CXMLRemoteAgentController(CXMLCommunicationService* p_pxCommunicationService)
{
	m_pxCommunicationService = p_pxCommunicationService;
	m_sAgentName = p_pxCommunicationService->GetComponentName() + "Agent";

	Register();

	m_bStopUpdateThread = false;
	m_xUpdateThread = StartNewThread(*this, CXMLRemoteAgentController::UpdateThreadProc, 0);
}

//---------------------------------------------------------------------------------------------------------------------

CXMLRemoteAgentController::~CXMLRemoteAgentController()
{
	Unregister();

	m_bStopUpdateThread = true;
	m_xUpdateThread.Resume();
	m_xUpdateThread.Wait();
}    

//---------------------------------------------------------------------------------------------------------------------
void	
CXMLRemoteAgentController::Tick(double p_dTime)
{
}

//---------------------------------------------------------------------------------------------------------------------
void
CXMLRemoteAgentController::Register()
{
	CMAgentRequest xReq(m_sAgentName, CMAgentRequest::AGENTREQ_REGISTER);
	m_pxCommunicationService->SendMsg(xReq);
}

//---------------------------------------------------------------------------------------------------------------------
void
CXMLRemoteAgentController::Unregister()
{
	CMAgentRequest xReq(m_sAgentName, CMAgentRequest::AGENTREQ_UNREGISTER);
	m_pxCommunicationService->SendMsg(xReq);
}

//---------------------------------------------------------------------------------------------------------------------
bool
CXMLRemoteAgentController::HandleMessage(const CRootMessage& p_xrMessage, const std::string& p_rsComponent, int p_iType)
{
	if(p_xrMessage.GetMessageType() == CMessage::MTYPE_AGENT_RESP)
	{
		CMAgentResponse* pxResponse = (CMAgentResponse*) &p_xrMessage;
		switch(pxResponse->GetResponseType())
		{
			case CMAgentResponse::AGENTRESP_REGISTRATION:
				if(pxResponse->GetText().size() > 0)
				{
					m_sAgentName = pxResponse->GetText();
					DebugPrint("David: Agent renamed to %s", m_sAgentName.c_str());
				}
				break;
			case CMAgentResponse::AGENTRESP_ERROR:
				DebugPrint("Agent Response: ERROR: %s", pxResponse->GetText().c_str());
				break;
			case CMAgentResponse::AGENTRESP_KICK:
				DebugPrint("Agent Response: KICK: %s", pxResponse->GetText().c_str());
				break;
			case CMAgentResponse::AGENTRESP_NORMALOP:
//				DebugPrint("Agent Response: NormapOp: %s", pxResponse->GetText().c_str());
				break;
			default:
				DebugPrint("Warning: Agent Response: UNKNOWN TYPE: %d", pxResponse->GetResponseType());
				break;
		}

	}
	else
	{
		assert(false);
		return false;
	}

	return true;
}

//---------------------------------------------------------------------------------------------------------------------
unsigned long
CXMLRemoteAgentController::UpdateThreadProc(int p_iUnused)
{
	while(!m_bStopUpdateThread)
	{
		if(m_vDesiredPositionOffset.x() != 0.0f  ||  m_vDesiredPositionOffset.y() != 0.0f)
		{
			CMAgentRequest xReq(m_sAgentName);
			CMAgentAction xAction("MOVE");
			xAction.Parameters().Add(m_vDesiredPositionOffset.x());
			xAction.Parameters().Add(m_vDesiredPositionOffset.y());
			xAction.Parameters().Add(m_vDesiredPositionOffset.z());
			xReq.SetAction(&xAction);
			if(m_pxCommunicationService->SendMsg(xReq, true))
			{
				m_vDesiredPositionOffset = CVec3(0.0f, 0.0f, 0.0f);
			}
		}
		else
		{
			CMAgentRequest xReq(m_sAgentName);
			CMAgentAction xAction("NOOP");
			xReq.SetAction(&xAction);
			m_pxCommunicationService->SendMsg(xReq, true);
		}

		Sleep(100);
	}

	return 0;
}
//---------------------------------------------------------------------------------------------------------------------
void		
CXMLRemoteAgentController::Eat(__int64 p_iObject)
{
}
//---------------------------------------------------------------------------------------------------------------------
void		
CXMLRemoteAgentController::Drink(__int64 p_iObject)
{
}
//---------------------------------------------------------------------------------------------------------------------
void		
CXMLRemoteAgentController::Focus(__int64 p_iObject)
{
}
//---------------------------------------------------------------------------------------------------------------------
void		
CXMLRemoteAgentController::Move(float p_fDeltaX, float p_fDeltaY)
{
	m_vDesiredPositionOffset.x() += p_fDeltaX;
	m_vDesiredPositionOffset.y() += p_fDeltaY;
//	DebugPrint("moved agent to %.2f %.2f", m_vDesiredPositionOffset.x(), m_vDesiredPositionOffset.y());
}
//---------------------------------------------------------------------------------------------------------------------

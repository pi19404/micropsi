#include "Application/stdinc.h"
#include "Communication/XMLCommunication/XMLWorldController.h"

#include "baselib/filelocator.h"

#include "Communication/XMLMessageFormat/MConsoleRequest.h"
#include "Communication/XMLMessageFormat/MConsoleResponse.h"
#include "Communication/XMLMessageFormat/MTreeNode.h"
#include "Communication/XMLCommunication/XMLCommunicationService.h"

#include "World/World.h"
#include "World/ObjectManager.h"
#include "World/WorldObject.h"

#include "Utilities/micropsiutils.h"

#include "Application/3dview2.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
CXMLWorldController::CXMLWorldController(CXMLCommunicationService* p_pxCommunicationService, CWorld* p_pxWorld) : CWorldController(p_pxWorld)
{
	m_iCurrentWorldSimStep = 0;
	m_iGlobalsVersion = -1;
	m_bFirstMessage = true;

	m_pxCommunicationService = p_pxCommunicationService;

	RequestGlobalData();

	m_bStopTouchCreatorThread = false;
	m_xTouchCreatorThread = StartNewThread(*this, CXMLWorldController::TouchCreatorThreadProc, 0);
}

//---------------------------------------------------------------------------------------------------------------------
CXMLWorldController::~CXMLWorldController()
{
	m_bStopTouchCreatorThread = true;
	m_xTouchCreatorThread.Resume();
	m_xTouchCreatorThread.Wait();

	RequestStopObjectUpdates();
	Disconnect();
}    

//---------------------------------------------------------------------------------------------------------------------
unsigned long	
CXMLWorldController::TouchCreatorThreadProc(int p_iUnused)
{
	while(!m_bStopTouchCreatorThread)
	{
		SendEmptyRequest();
		Sleep(100);
	}

	return 0;
}

//---------------------------------------------------------------------------------------------------------------------
void			
CXMLWorldController::Tick(double p_dTime)
{
}

//---------------------------------------------------------------------------------------------------------------------
void
CXMLWorldController::RequestCompleteObjectList()
{
	SendQuestion(CQuestion::AM_ANSWER_ONCE, "getobjectlist");
	SendQuestion(CQuestion::AM_ANSWER_CONTINUOUSLY, "getobjectchangelist");
}

//---------------------------------------------------------------------------------------------------------------------
void 
CXMLWorldController::RequestStopObjectUpdates()
{
	SendQuestion(CQuestion::AM_STOP_ANSWERING, "getobjectchangelist");
	DebugPrint("David: requesting stop of object updates");
}

//---------------------------------------------------------------------------------------------------------------------
void
CXMLWorldController::RequestGlobalData()
{
	SendQuestion(CQuestion::AM_ANSWER_ONCE, "getglobaldata");
	DebugPrint("David: requesting global data");
}

//---------------------------------------------------------------------------------------------------------------------
void
CXMLWorldController::SendEmptyRequest()
{
	CMConsoleRequest xRequest;
	m_pxCommunicationService->SendMsg(xRequest, true);
}

//---------------------------------------------------------------------------------------------------------------------
void
CXMLWorldController::SendQuestion(CQuestion::AnswerMode p_eAnswerMode, string p_sQuestionName, CParameterList& p_xParameters)
{
	CMConsoleRequest xRequest(m_bFirstMessage ? CMConsoleRequest::RT_FIRST : CMConsoleRequest::RT_NORMAL);
	CMConsoleQuestion xQuestion("world", m_iCurrentWorldSimStep, p_eAnswerMode, m_pxCommunicationService->GetComponentName(), p_sQuestionName, p_xParameters);
	xRequest.AddQuestion(xQuestion);
	m_pxCommunicationService->SendMsg(xRequest);
	m_bFirstMessage = false;
}

//---------------------------------------------------------------------------------------------------------------------
void
CXMLWorldController::Disconnect()
{
	CMConsoleRequest xRequest(CMConsoleRequest::RT_LAST);
	m_pxCommunicationService->SendMsg(xRequest);
	Sleep(500);
}

//---------------------------------------------------------------------------------------------------------------------
bool	
CXMLWorldController::HandleMessage(const CRootMessage& p_xrMessage, const std::string& p_rsComponent, int p_iType)
{
	if(p_xrMessage.GetMessageType() == CMessage::MTYPE_CONSOLE_RESP)
	{
		CMConsoleResponse* pxResponse = (CMConsoleResponse*) &p_xrMessage;
		m_iCurrentWorldSimStep = pxResponse->GetTime();
		for(unsigned int i=0; i<pxResponse->GetAnswers().size(); ++i)
		{
			ProcessAnswer(pxResponse->GetAnswers()[i]);
		}
	}
	else
	{
		DebugPrint("Warning: CXMLWorldController::HandleMessage: unhandled message type %d", p_xrMessage.GetMessageType());
	}

	return false;
}

//---------------------------------------------------------------------------------------------------------------------
void			
CXMLWorldController::ProcessAnswer(const CMConsoleAnswer& p_rxAnswer)
{
	const CMTreeNode* pxTree = p_rxAnswer.GetDataTree();
	if(pxTree)
	{
		if(pxTree->GetKey() == "success")
		{
			for(unsigned int i=0; i<pxTree->GetChildren().size(); ++i)
			{
				const CMTreeNode* pxChild = &pxTree->GetChildren()[i];
				if(pxChild->GetKey() == "object list")
				{
					m_pxWorld->GetObjectManager()->ClearAllObjects();
					ProcessObjectList(*pxChild);
				}
				else if(pxChild->GetKey() == "object change list")
				{
					ProcessObjectList(*pxChild);
				}
				else if(pxChild->GetKey() == "globals version"  ||  pxChild->GetKey() == "version")
				{
					int iGlobalsVersion = atoi(pxChild->GetValue().c_str());
					if(m_iGlobalsVersion != iGlobalsVersion)
					{
						// global data version has changed
						// request global data from server UNLESS we are receiving this information as part of a global data message
						if(!p_rxAnswer.GetQuestion() || p_rxAnswer.GetQuestion()->GetQuestionName() != "getglobaldata")
						{
							RequestGlobalData();						
						}
						m_iGlobalsVersion = iGlobalsVersion;
					}
				}
				else if(pxChild->GetKey() == "filename")
				{
					// (xml) world file name on server
					m_sWorldFileName = pxChild->GetValue();
				}
				else if(pxChild->GetKey() == "groundmap")
				{
					// ground map information from server
					ProcessGroundMap(*pxChild);
				}
			}
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
/**
	we have receive a full object list; process it
*/
void			
CXMLWorldController::ProcessObjectList(const CMTreeNode& p_rxObjectListNode)
{
	for(unsigned int i=0; i<p_rxObjectListNode.GetChildren().size(); ++i)
	{
		const CMTreeNode& rxChild = p_rxObjectListNode.GetChildren()[i];
		if(rxChild.GetKey() == "object")
		{
			ProcessObject(rxChild);
		}
		else if(rxChild.GetKey() == "remove")
		{
			m_pxWorld->GetObjectManager()->DeleteObj(_atoi64(rxChild.GetValue().c_str()));
		}
		else
		{
			DebugPrint("Warning: CXMLWorldController::ProcessObjectList: unknown child '%s'", rxChild.GetKey().c_str());
		}

	}
}

//---------------------------------------------------------------------------------------------------------------------
void			
CXMLWorldController::ProcessGroundMap(const CMTreeNode& p_rxObjectListNode)
{
	for(unsigned int i=0; i<p_rxObjectListNode.GetChildren().size(); ++i)
	{
		const CMTreeNode& rxChild = p_rxObjectListNode.GetChildren()[i];
		if(rxChild.GetKey() == "area")
		{
			for(unsigned int j=0; j<rxChild.GetChildren().size(); ++j)
			{
				const CMTreeNode& rxAreaChild = rxChild.GetChildren()[j];
				if(rxAreaChild .GetKey() == "upper bound")
				{
					sscanf(rxAreaChild .GetValue().c_str(), "(%f, %f, %f)", 
						&m_vGroundMapUpperCorner.x(), &m_vGroundMapUpperCorner.y(), &m_vGroundMapUpperCorner.z());
					
				}
				else if(rxAreaChild .GetKey() == "lower bound")
				{
					sscanf(rxAreaChild .GetValue().c_str(), "(%f, %f, %f)", 
						&m_vGroundMapLowerCorner.x(), &m_vGroundMapLowerCorner.y(), &m_vGroundMapLowerCorner.z());
				}
				else
				{
					DebugPrint("Warning: CXMLWorldController::ProcessGroundMap(): unknown 'area' child '%s'", rxAreaChild .GetKey().c_str());
				}
			}
		}
		else if(rxChild.GetKey() == "image filename")
		{
			m_sGroundMapFileName = rxChild.GetValue();
		}
		else
		{
			DebugPrint("Warning: CXMLWorldController::ProcessGroundMap(): unknown child '%s'", rxChild.GetKey().c_str());
		}
	}

	string sWorldFile = C3DView2::Get()->GetFileLocator()->GetPath(string("onlineworlds>") + m_sGroundMapFileName + ".xml");
	CVec3 vOffset = Utils::PsiPos2Engine(m_vGroundMapLowerCorner);
	CVec3 vAbsSize = m_vGroundMapUpperCorner - m_vGroundMapLowerCorner;
	float fTmp = vAbsSize.y();
	vAbsSize.y() = vAbsSize.z();
	vAbsSize.z() = fTmp;
	vOffset.z() -= vAbsSize.z();

	m_pxWorld->LoadFromXML(sWorldFile.c_str(), 0, CWorld::WS_ForceNoWrapAround, &vOffset, &vAbsSize);
	RequestCompleteObjectList();
}

//---------------------------------------------------------------------------------------------------------------------
void			
CXMLWorldController::ProcessObject(const CMTreeNode& p_rxObjectListNode)
{
	std::string sClassName  = "*not initialzed*";
	std::string sObjName	= "*not initialzed*";
	float fX, fY, fZ, fAngle;
	fX = fY = fZ = fAngle = 0.0f;
	__int64 iObjID = CWorldObject::INVALID_OBJID;

	for(unsigned int i=0; i<p_rxObjectListNode.GetChildren().size(); ++i)
	{
		const CMTreeNode& rxChild = p_rxObjectListNode.GetChildren()[i];
		if(rxChild.GetKey() == "id")
		{
			iObjID = _atoi64(rxChild.GetValue().c_str());
		}
		else if(rxChild.GetKey() == "position")
		{
			sscanf(rxChild.GetValue().c_str(), "(%f, %f, %f)", &fX, &fY, &fZ);
		}
		else if(rxChild.GetKey() == "orientation")
		{
			fAngle = (float) atof(rxChild.GetValue().c_str());
		}
		else if(rxChild.GetKey() == "objectclass")
		{
			sClassName = rxChild.GetValue();
		}
		else if(rxChild.GetKey() == "subobjects")
		{
			// not handled at the moment
		}
		else if(rxChild.GetKey() == "objectname")
		{
			sObjName = rxChild.GetValue();
		}
	}
	
	CWorldObject* pxObj = m_pxWorld->GetObjectManager()->FindObj(iObjID);
	if(pxObj)
	{
		pxObj->SetPSIPos(CVec3(fX, fY, fZ), fAngle);
	}
	else
	{
		// FIXME: new hier und delete im Manager? nicht so toll...
		CWorldObject* pxObj = new CWorldObject(m_pxWorld, sClassName.c_str(), fX, fY, fZ, fAngle, fAngle, iObjID);
		pxObj->SetObjectName(sObjName);
		m_pxWorld->GetObjectManager()->AddObj(pxObj);
	}
}
//---------------------------------------------------------------------------------------------------------------------
void	
CXMLWorldController::ClearAllObjects()
{
	CObjectManager::ObjectIterator i;
	m_pxWorld->GetObjectManager()->StartIteration(i);
	CWorldObject* pxObj;
	while(pxObj = m_pxWorld->GetObjectManager()->Iterate(i))
	{
		DeleteObject(pxObj->GetID());
	}
} 

//---------------------------------------------------------------------------------------------------------------------
bool	
CXMLWorldController::SaveWorld()
{
	SendQuestion(CQuestion::AM_ANSWER_ONCE, "saveworld");
	return true;
} 

//---------------------------------------------------------------------------------------------------------------------
bool	
CXMLWorldController::SaveWorldAs(const std::string& p_rsFilename)
{
	CParameterList xParams;
	xParams.Add(p_rsFilename);
	SendQuestion(CQuestion::AM_ANSWER_ONCE, "saveworld", xParams);
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
std::string	
CXMLWorldController::GetCurrentWorldFileName() const
{
	return m_sWorldFileName;
}

//---------------------------------------------------------------------------------------------------------------------
void		
CXMLWorldController::CreateNewObject(const char* p_pcObjectClassName, const CVec3& p_vPos, float p_fOrientationAngle, float p_fHeight, int p_iVariation)
{
	CParameterList xParams;
	xParams.Add(p_pcObjectClassName);
	xParams.Add("");
	xParams.Add(CStr::Create("(%.2f,%.2f,%.2f)", p_vPos.x(), p_vPos.y(), p_vPos.z()).c_str());
	SendQuestion(CQuestion::AM_ANSWER_ONCE, "createobject", xParams);
}

//---------------------------------------------------------------------------------------------------------------------
bool		
CXMLWorldController::DeleteObject(__int64 p_iID)
{
	CParameterList xParams;
	xParams.Add(p_iID);
	SendQuestion(CQuestion::AM_ANSWER_ONCE, "removeobject", xParams);
	return false;
}
//---------------------------------------------------------------------------------------------------------------------

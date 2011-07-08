#include "Application/stdinc.h"
#include "Communication/TCPCommunication/TCPCommunicationService.h"

#include "baselib/filelocator.h"

#include "Communication/TCPCommunication/tcpsocketwin.h"
#include "Communication/TCPCommunication/tcpconnection.h"
#include "Communication/TCPCommunication/tcpworldmessages.h"

#include "Application/3dview2.h"
#include "World/world.h"
#include "world/objectmanager.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
CTCPCommunicationService::CTCPCommunicationService(CWorld* p_pxWorld)
{
	m_pxSocket = 0;
	m_pxConnection = 0;
	m_pxWorld = p_pxWorld;
} 

//---------------------------------------------------------------------------------------------------------------------
CTCPCommunicationService::~CTCPCommunicationService()
{
	CloseWorldConnection();
}

//---------------------------------------------------------------------------------------------------------------------
bool		
CTCPCommunicationService::OpenWorldConnection(const std::string& p_rsWorldServer, int p_iWorldServerPort)
{
	// create connection
	m_pxSocket = new CTCPSocketWin();
	m_pxConnection = new CTCPConnection(*m_pxSocket, p_rsWorldServer.c_str(), p_iWorldServerPort, this);

	if(!m_pxConnection->IsConnected())
	{
		CloseWorldConnection();
		return false;
	}
	else
	{
		m_pxWorld->GetObjectManager()->Clear();

		string sWorldFile = C3DView2::Get()->GetFileLocator()->GetPath(string("offlineworlds>") + "defaultisland.xml");
		if(!m_pxWorld->LoadFromXML(sWorldFile.c_str()))
		{
			m_pxWorld->InitDefaultTerrain();
		}
		return true;
	}
}

//---------------------------------------------------------------------------------------------------------------------
void		
CTCPCommunicationService::CloseWorldConnection()
{
	delete m_pxConnection;
	m_pxConnection = 0;
	delete m_pxSocket;
	m_pxSocket = 0;
}

//---------------------------------------------------------------------------------------------------------------------
bool		
CTCPCommunicationService::IsWorldServerConnected() const
{
	return m_pxConnection ? m_pxConnection->IsConnected() : false;
}

//---------------------------------------------------------------------------------------------------------------------
void		
CTCPCommunicationService::Tick()
{
	if(m_pxConnection)		
	{ 
		m_pxConnection->Tick();
	}
}

//---------------------------------------------------------------------------------------------------------------------
/**
*/
void 
CTCPCommunicationService::ProcessMsg(CInMessageBuffer& p_kxrMsg)
{
	CObjectManager* pxObjMgr = m_pxWorld->GetObjectManager();

	int iType;
	p_kxrMsg.ReadInt(iType);

	switch(iType)
	{
		case MESSAGE_CLEARALLOBJS:
			{
				pxObjMgr->ClearAllObjects();
			}
			break;
		    
		case MESSAGE_NEWOBJ: 
			{
				long iObjID;
				p_kxrMsg.ReadLong(iObjID);

				int iStrLen = p_kxrMsg.ReadStringSize();
				char* p = new char[iStrLen];
				p_kxrMsg.ReadString(p, iStrLen);
				
				float fX, fY, fZ, fH, fAngle;
				p_kxrMsg.ReadFloat(fX);
				p_kxrMsg.ReadFloat(fY);
				p_kxrMsg.ReadFloat(fZ);
				p_kxrMsg.ReadFloat(fAngle);
				p_kxrMsg.ReadFloat(fH);

				CWorldObject* pxObj = pxObjMgr->FindObj(iObjID);
				if(pxObj)
				{
					if(pxObj->GetClass() != string(p))
					{
						pxObjMgr->DeleteObj(iObjID);
						pxObjMgr->AddObj(new CWorldObject(m_pxWorld, p, fX, fY, fZ, fH, fAngle, iObjID));
						DebugPrint("replace id = %d, objclass = %s, pos = %.2f %.2f %.2f height = %.2f angle = %.2f", iObjID, p, fX, fY, fZ, fH);
					}
					else
					{
						pxObj->SetPSIPos(CVec3(fX, fY, fZ), fAngle);
						DebugPrint("update id = %d, objclass = %s, pos = %.2f %.2f %.2f height = %.2f angle = %.2f", iObjID, p, fX, fY, fZ, fH);
					}
				}
				else
				{
					pxObjMgr->AddObj(new CWorldObject(m_pxWorld, p, fX, fY, fZ, fH, fAngle, iObjID));
					DebugPrint("new id = %d, objclass = %s, pos = %.2f %.2f %.2f height = %.2f angle = %.2f", iObjID, p, fX, fY, fZ, fH);
				}

				delete p;
			}
			break;

		case MESSAGE_DELETEOBJ:
			{
				long iObjID;
				p_kxrMsg.ReadLong(iObjID);
				pxObjMgr->DeleteObj(iObjID);
				DebugPrint("delete object %d", iObjID);
			}
			break;

		case MESSAGE_OBJPOS:
			{
				long iObjID;
				p_kxrMsg.ReadLong(iObjID);

				float fX, fY, fZ, fAngle;
				p_kxrMsg.ReadFloat(fX);
				p_kxrMsg.ReadFloat(fY);
				p_kxrMsg.ReadFloat(fZ);
				p_kxrMsg.ReadFloat(fAngle);

				CWorldObject* p = pxObjMgr->FindObj(iObjID);
				if(p)
				{
					p->SetPSIPos(CVec3(fX, fY, fZ), fAngle);

				// *MOVEMENT-HACK*

//					m_axAllObjs[iObjIdx].MoveTo(fX, fY, fZ, 2.5f);
//					int i = m_aiTickObjs.NewEntry();
//					m_aiTickObjs[i] = iObjIdx;

				// *MOVEMENT-HACK*
				}
			}
			break;

		default:
			DebugPrint("Warning: Unknown Message Type = %d", iType);
			break;
	}
}
//---------------------------------------------------------------------------------------------------------------------



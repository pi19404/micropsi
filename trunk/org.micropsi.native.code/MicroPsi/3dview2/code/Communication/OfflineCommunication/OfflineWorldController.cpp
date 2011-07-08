#include "Application/stdinc.h"

#include "BaseLib/FileLocator.h"

#include "Communication/OfflineCommunication/OfflineWorldController.h"
#include "World/World.h"
#include "World/ObjectManager.h"
#include "World/WorldObject.h"
#include "Application/3DView2.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
COfflineWorldController::COfflineWorldController(CWorld* p_pxWorld) : CWorldController(p_pxWorld)
{
}

//---------------------------------------------------------------------------------------------------------------------
COfflineWorldController::~COfflineWorldController()
{
}

//---------------------------------------------------------------------------------------------------------------------
void			
COfflineWorldController::Tick(double p_dTime)
{
}
//---------------------------------------------------------------------------------------------------------------------
void	
COfflineWorldController::ClearAllObjects()
{
	m_pxWorld->GetObjectManager()->ClearAllObjects();
} 

//---------------------------------------------------------------------------------------------------------------------
bool	
COfflineWorldController::SaveWorld()
{
	string sCurrentName = GetCurrentWorldFileName();
	if(sCurrentName.size() > 0)
	{
		return m_pxWorld->SaveAsXML(sCurrentName.c_str());
	}
	else
	{
		return false;
	}
} 

//---------------------------------------------------------------------------------------------------------------------
bool	
COfflineWorldController::SaveWorldAs(const string& p_rsFilename)
{
	string sFile = C3DView2::Get()->GetFileLocator()->GetPath(string("offlineworlds>") + p_rsFilename.c_str());
	return m_pxWorld->SaveAsXML(sFile.c_str());
} 

//---------------------------------------------------------------------------------------------------------------------
string	
COfflineWorldController::GetCurrentWorldFileName() const
{
	return m_pxWorld->GetCurrentWorldFileName();
}

//---------------------------------------------------------------------------------------------------------------------
void		
COfflineWorldController::CreateNewObject(const char* p_pcObjectClassName, const CVec3& p_vPos, float p_fOrientationAngle, float p_fHeight, int p_iVariation)
{
	CWorldObject* pxObject = new CWorldObject(m_pxWorld, p_pcObjectClassName, 
		p_vPos.x(), p_vPos.y(), p_vPos.z(), p_fHeight, p_fOrientationAngle, CWorldObject::INVALID_OBJID, p_iVariation);
	m_pxWorld->GetObjectManager()->AddObj(pxObject);
}

//---------------------------------------------------------------------------------------------------------------------
bool		
COfflineWorldController::DeleteObject(__int64 p_iID)
{
	return m_pxWorld->GetObjectManager()->DeleteObj(p_iID);
}
//---------------------------------------------------------------------------------------------------------------------

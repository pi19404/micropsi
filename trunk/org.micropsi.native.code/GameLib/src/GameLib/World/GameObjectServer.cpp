#include "stdafx.h"
#include "GameLib/World/GameObjectServer.h"
#include "GameLib/World/GameObjectClassManager.h"
#include "GameLib/World/GameObject.h"

//---------------------------------------------------------------------------------------------------------------------
CGameObjectServer::CGameObjectServer()
{
}
//---------------------------------------------------------------------------------------------------------------------
CGameObjectServer::~CGameObjectServer()
{
    Clear();
}
//---------------------------------------------------------------------------------------------------------------------
void 
__cdecl CGameObjectServer::DestroyGameObj(CGameObj* pGameObj)
{
    CGameObjClassMgr::Get().DestroyGameObj(pGameObj);
}
//---------------------------------------------------------------------------------------------------------------------
CGameObj* 
CGameObjectServer::GetGameObject(GameObjID id) const
{
    assert(m_xGameObjs.IsValid(id));
    return m_xGameObjs.Element(id);
}
//---------------------------------------------------------------------------------------------------------------------
GameObjID 
CGameObjectServer::CreateObject(const std::string& sClassName)
{
    CGameObj* pGameObj = CGameObjClassMgr::Get().CreateGameObj(sClassName);
	if(!pGameObj)
	{
		return GameObjID::INVALID_GAMEOBJ_ID;
	}

    GameObjID id = m_xGameObjs.PushEntry(pGameObj);
    pGameObj->SetObjID(id);

    return id;
}
//---------------------------------------------------------------------------------------------------------------------
void 
CGameObjectServer::DestroyObject(GameObjID id)
{
    assert(m_xGameObjs.IsValid(id));
    CGameObjClassMgr::Get().DestroyGameObj(m_xGameObjs.Element(id));
    m_xGameObjs.DeleteEntry(id);
}
//---------------------------------------------------------------------------------------------------------------------
bool 
CGameObjectServer::IsValidGameObjID(GameObjID id) const
{
    return m_xGameObjs.IsValid(id);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CGameObjectServer::Clear()
{
	unsigned long i;
	CGameObj* pObj;
	m_xGameObjs.StartIterate(i);
	while(m_xGameObjs.Iterate(i, pObj))
	{
	    CGameObjClassMgr::Get().DestroyGameObj(pObj);
	}
    m_xGameObjs.Clear();
}
//---------------------------------------------------------------------------------------------------------------------
void		
CGameObjectServer::GetGameObjectsTouched(const CVec3& p_rvPos, float p_fXZRadius, float p_fYHeight, std::vector<CGameObj*>& po_rxTouchedObjs)
{
	// todo: quadtree :)

	ObjIterator i;
	StartIterate(i);
	CGameObj* pxObj;
	while(pxObj = Iterate(i))
	{
		if(pxObj->GetIsTouchable())
		{
			CVec3 vObjPos; 
			float fObjHeight, fXZRadius;
			pxObj->GetCollisionData(&vObjPos, &fObjHeight, &fXZRadius);

			// Höhenvergleich:

			float fHeightDifference = (float) fabs(pxObj->GetPos().y() - p_rvPos.y());
			fHeightDifference -= 0.5f * (p_fYHeight + fObjHeight);
			if(fHeightDifference >= 0.0f)
			{
				continue;
			}

			// Entfernungsvergleich:
			
			vObjPos.y() = p_rvPos.y();
			float fDistanceSquared = (vObjPos - p_rvPos).AbsSquare();
			float fMaxDist = p_fXZRadius + fXZRadius;
			fDistanceSquared -= fMaxDist * fMaxDist;
			if(fDistanceSquared >= 0.0f)
			{
				continue;
			}

			po_rxTouchedObjs.push_back(pxObj);
		}
	}
}
//---------------------------------------------------------------------------------------------------------------------

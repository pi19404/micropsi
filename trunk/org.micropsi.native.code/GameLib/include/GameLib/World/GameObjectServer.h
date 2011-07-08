#pragma once
#ifndef GAMELIB_GAMEOBJECTSERVER_H_INCLUDED
#define GAMELIB_GAMEOBJECTSERVER_H_INCLUDED

#include <string>
#include <vector>
#include "baselib/handledset.h"
#include "baselib/geometry/CVector.h"
#include "GameLib/World/GameObjectID.h"

class CGameObj;

class CGameObjectServer
{
private:
    CHandledSet<CGameObj*, 8>        m_xGameObjs;

    static void __cdecl DestroyGameObj(CGameObj* pGameObj);

public:

    CGameObjectServer();
    virtual ~CGameObjectServer();

	typedef unsigned long	ObjIterator;
    void					StartIterate(ObjIterator& iter) const;
    CGameObj*				Iterate(ObjIterator& iter) const;


    CGameObj*				GetGameObject(GameObjID id) const;        
	
    GameObjID				CreateObject(const std::string& sClassName);
    void					DestroyObject(GameObjID id);

    bool					IsValidGameObjID(GameObjID id) const;

    void					Clear();
	void					GetGameObjectsTouched(const CVec3& p_rvPos, float p_fXZRadius, float p_fYHeight, std::vector<CGameObj*>& po_rxTouchedObjs);
};

#include "GameObjectServer.inl"

#endif // GAMELIB_GAMEOBJECTSERVER_H_INCLUDED

//---------------------------------------------------------------------------------------------------------------------
inline
void 
CGameObj::SetPos(const CVec3& vNewPos)
{
    m_vPos = vNewPos;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CVec3& 
CGameObj::GetPos() const
{
    return m_vPos;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CGameObj::SetRot(const CMat3S& mNewRot)
{
    m_mRot = mNewRot;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CMat3S 
CGameObj::GetRot() const
{
    return m_mRot;
}
//---------------------------------------------------------------------------------------------------------------------
inline
GameObjID 
CGameObj::GetObjID() const
{
    return m_ObjID;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CGameObj::SetObjID(GameObjID id)
{
    assert(m_ObjID == 0);
    m_ObjID = id;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CGameObj::SetVisible(bool p_bVisible)
{
	m_bVisible = p_bVisible;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CGameObj::GetIsVisible() const
{
	return m_bVisible;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CGameObj::SetCollision(bool p_bCollision)
{
	m_bCollision = p_bCollision;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CGameObj::GetHasCollision() const
{
	return m_bCollision;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CGameObj::SetTouchable(bool p_bTouchable)
{
	m_bTouchable= p_bTouchable;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CGameObj::GetIsTouchable() const
{
	return m_bTouchable;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CCollisionObject*		
CGameObj::GetCollisionObject()
{
	UpdateCollisionObject(m_pxCollisionObject);
	return m_pxCollisionObject;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CGameObjClassMgr::CClassInfo*	
CGameObj::GetClassInfo()
{
	return m_pxClassInfo;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const std::string&
CGameObj::GetClassName() const
{
	return m_pxClassInfo->m_sClassName;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CGameObj::IsOfType(const std::string& p_rsClassName)
{
	const CGameObjClassMgr::CClassInfo* pxCurrentClass = m_pxClassInfo;
	do
	{
		if(pxCurrentClass->m_sClassName == p_rsClassName)
		{
			return true;
		}
		pxCurrentClass = pxCurrentClass->m_pxParentClass;
	} while(pxCurrentClass);

	return false;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CGameObj::IsOfExactType(const std::string& p_rsClassName)
{
	return m_pxClassInfo->m_sClassName == p_rsClassName;
}
//---------------------------------------------------------------------------------------------------------------------

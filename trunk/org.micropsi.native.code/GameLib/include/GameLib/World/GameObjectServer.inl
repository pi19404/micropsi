//---------------------------------------------------------------------------------------------------------------------
inline
void 
CGameObjectServer::StartIterate(ObjIterator& iter) const
{
    m_xGameObjs.StartIterate(iter);
}
//---------------------------------------------------------------------------------------------------------------------
inline
CGameObj*
CGameObjectServer::Iterate(ObjIterator& iter) const
{
    CGameObj* pGameObj;
    if(m_xGameObjs.Iterate(iter, pGameObj))
    {
        return pGameObj;
    }
    else
    {
        return 0;
    }
}
//---------------------------------------------------------------------------------------------------------------------

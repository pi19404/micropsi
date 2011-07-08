//---------------------------------------------------------------------------------------------------------------------
inline
__int64					
CWorldObject::GetID() const				
{ 
	return m_iID; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
const std::string&			
CWorldObject::GetClass() const			
{ 
	return m_pxObjTypeData->m_pxObjectVisualization->m_sClassName; 
} 
//---------------------------------------------------------------------------------------------------------------------
inline
float					
CWorldObject::GetHeight() const			
{ 
	return m_fHeight; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CVec3					
CWorldObject::GetPSIPos() const
{
	return m_vPSIPos; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
float	
CWorldObject::GetPSIOrientationAngle() const
{
	return m_fPSIOrientationAngle;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CBoundingSphere	
CWorldObject::GetBoundingSphere() const
{
	CBoundingSphere s = GetModelBoundingSphere();
	s.m_vCenter += GetEnginePos();
	return s;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CBoundingSphere&
CWorldObject::GetModelBoundingSphere() const
{
	return m_hModel->GetBoundingSphere();
}
//---------------------------------------------------------------------------------------------------------------------
inline
TModelHandle
CWorldObject::GetModel() const
{
	return m_hModel;
}
//---------------------------------------------------------------------------------------------------------------------
inline
COpcodeMesh*
CWorldObject::GetCollisionModel() const
{
	if(m_iCurrentLOD >= 0)
	{
		return m_pxObjTypeData->m_axSortedLODLevels[m_iCurrentLOD]->m_pxCollisionModel;
	}
	else
	{
		return 0;
	}
}
//---------------------------------------------------------------------------------------------------------------------
inline
const std::string&
CWorldObject::GetObjectName() const
{
	return m_sObjectName;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void					
CWorldObject::SetObjectName(std::string p_sName)
{
	m_sObjectName = p_sName;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CWorldObject::operator== (const CWorldObject& p_kxrObj) const
{
	return m_iID == p_kxrObj.m_iID;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CWorldObject::operator!= (const CWorldObject& p_kxrObj) const
{
	return m_iID == p_kxrObj.m_iID;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CWorldObject::operator< (const CWorldObject& p_kxrObj) const
{
	return m_iID < p_kxrObj.m_iID;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CWorldObject::operator> (const CWorldObject& p_kxrObj) const
{
	return m_iID > p_kxrObj.m_iID;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CWorldObject::operator<= (const CWorldObject& p_kxrObj) const
{
	return m_iID <= p_kxrObj.m_iID;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CWorldObject::operator>= (const CWorldObject& p_kxrObj) const
{
	return m_iID >= p_kxrObj.m_iID;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CWorldObject::GetVisible() const
{
	return m_bVisible;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CMat4S&			
CWorldObject::GetWorldTransformation() const
{
	return m_mTransform;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CWorld*					
CWorldObject::GetWorld() const
{
	return m_pxWorld;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CWorldObject::SetPSIPos(float p_fX, float p_fY, float p_fZ)
{
	SetPSIPos(CVec3(p_fX, p_fY, p_fZ), m_fPSIOrientationAngle);
}

//---------------------------------------------------------------------------------------------------------------------
inline
void					
CWorldObject::SetPSIPos(CVec3& p_vrPos)
{
	SetPSIPos(p_vrPos, m_fPSIOrientationAngle);
}
//---------------------------------------------------------------------------------------------------------------------

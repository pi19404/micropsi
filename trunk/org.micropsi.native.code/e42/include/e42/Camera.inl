//---------------------------------------------------------------------------------------------------------------------
inline
const CMat4S& 
CCamera::GetViewMatrix() const
{
    return m_matView;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CMat4S& 
CCamera::GetViewInverseMatrix() const
{
    return m_matViewInverse;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CMat4S& 
CCamera::GetProjectionMatrix() const
{
    return m_matProjection;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CMat4S& 
CCamera::GetProjectionInverseMatrix() const
{
    return m_matProjectionInverse;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CMat4S& 
CCamera::GetViewProjectionMatrix() const
{
    return m_matViewProjection;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CMat4S& 
CCamera::GetViewProjectionInverseMatrix() const
{
    return m_matViewProjectionInverse;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void
CCamera::UpdateViewFrustum()
{
	m_ViewFrustum.Update(m_matViewProjectionInverse, m_fNearPlaneDistance, m_fFarPlaneDistance, m_bPerspective, m_bLeftHandedWorldCoordinateSystem);
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CViewFrustum& 
CCamera::GetViewFrustum() const
{
    return m_ViewFrustum;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::SetFieldOfView(float fHeight, float fAspectRatio, bool bPerspective)
{
    m_fFieldOfViewHeight = fHeight;

	m_fAspectRatio = fAspectRatio;
    m_bPerspective = bPerspective;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::SetFieldOfViewHeight(float fHeight)
{
	m_fFieldOfViewHeight = fHeight;
}
//---------------------------------------------------------------------------------------------------------------------
inline
float
CCamera::GetFieldOfViewHeight() const
{
	return m_fFieldOfViewHeight;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::SetAspect(float fAspectRatio)
{
	m_fAspectRatio = fAspectRatio;
}
//---------------------------------------------------------------------------------------------------------------------
inline
float
CCamera::GetAspect() const
{
	return m_fAspectRatio;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::SetPos(const CVec3& vPos)
{
	assert(!_isnan(vPos.x()) && !_isnan(vPos.y()) && !_isnan(vPos.z()));
    m_vPos = vPos;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CVec3& 
CCamera::GetPos() const
{
    return m_vPos;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::SetOrientation(const CVec3& vDir)
{
    m_vLookAtDir = vDir.GetNormalized();
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::SetUpVec(const CVec3& vUpVec)
{
    m_vUpVector = vUpVec.GetNormalized();
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CVec3& 
CCamera::GetUpVec() const
{
    return m_vUpVector;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CVec3 
CCamera::GetRightVec() const
{
    CVec3 vRightVec = m_vLookAtDir ^ m_vUpVector; 
    if (vRightVec.AbsSquare() < 0.000001f) vRightVec = CVec3(-1, 0, 0);
    vRightVec.Normalize();

	if (m_bLeftHandedWorldCoordinateSystem)
	{
		vRightVec = -vRightVec;
	}

    return vRightVec;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CVec3& 
CCamera::GetOrientation() const
{
    return m_vLookAtDir;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::SetOrientationByLookAtPoint(const CVec3& vPoint)
{
	SetOrientation(vPoint - m_vPos);
}
//---------------------------------------------------------------------------------------------------------------------
inline
float 
CCamera::GetFarPlaneDistance() const
{
    return m_fFarPlaneDistance;
}
//---------------------------------------------------------------------------------------------------------------------
inline
float 
CCamera::GetNearPlaneDistance() const
{
    return m_fNearPlaneDistance;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::SetFarPlaneDistance(float fDist)
{
    m_fFarPlaneDistance = fDist;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::SetNearPlaneDistance(float fDist)
{
    m_fNearPlaneDistance = fDist;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::SetPerspective(bool bPerspective)
{
	m_bPerspective = bPerspective;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CCamera::GetPerspective() const
{
	return m_bPerspective;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::MoveRight(float fDistance)
{
	m_vPos += GetRightVec() * fDistance;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::MoveUp(float fDistance)
{
	m_vPos += CalcOrthogonalUpVec() * fDistance;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::MoveForward(float fDistance)
{
	m_vPos += m_vLookAtDir * fDistance;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::MoveWorldUp(float fDistance)
{
	m_vPos.y() += fDistance;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::RotateUp(float fAngle)
{
	if (m_bLeftHandedWorldCoordinateSystem) 
	{
		fAngle = -fAngle;
	}

	CMat3S matRotation = CMat3S::CalcRotationMatrix(CAxisAngle(GetRightVec(), fAngle));

	m_vLookAtDir = m_vLookAtDir * matRotation;
	m_vUpVector = m_vUpVector * matRotation;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::RotateRight(float fAngle)
{
	if (!m_bLeftHandedWorldCoordinateSystem) 
	{
		fAngle = -fAngle;
	}

	CMat3S matRotation = CMat3S::CalcRotationMatrix(CAxisAngle(CalcOrthogonalUpVec(), fAngle));

	m_vLookAtDir = m_vLookAtDir * matRotation;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::RotateCW(float fAngle)
{
	if (m_bLeftHandedWorldCoordinateSystem) 
	{
		fAngle = -fAngle;
	}

	CMat3S matRotation = CMat3S::CalcRotationMatrix(CAxisAngle(m_vLookAtDir, fAngle));

	m_vUpVector = m_vUpVector * matRotation;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::RotateWorldY(float fAngle)
{
	if (!m_bLeftHandedWorldCoordinateSystem) 
	{
		fAngle = -fAngle;
	}

	CMat3S matRotation = CMat3S::CalcRotationMatrix(CAxisAngle(CVec3::vYAxis, fAngle));

	m_vLookAtDir = m_vLookAtDir * matRotation;
	m_vUpVector = m_vUpVector * matRotation;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void 
CCamera::SetLeftHandedWorldCoordinateSystem(bool bRHCS)
{
	m_bLeftHandedWorldCoordinateSystem = bRHCS;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CCamera::GetLeftHandedWorldCoordinateSystem() const
{
	return m_bLeftHandedWorldCoordinateSystem;
}
//---------------------------------------------------------------------------------------------------------------------

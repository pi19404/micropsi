#include "Application/stdinc.h"
#include "Observers/observer.h"

#include "Application/3dview2.h"
#include "World/World.h"

//---------------------------------------------------------------------------------------------------------------------
CObserver::CObserver()
{
	m_vEye	= CVec3(0.0f, 0.0f, 0.0f);

	m_vU	= CVec3(1.0f, 0.0f,  0.0f);
	m_vV	= CVec3(0.0f, 1.0f,  0.0f);
	m_vN	= CVec3(0.0f, 0.0f,  1.0f);
}


//---------------------------------------------------------------------------------------------------------------------
CObserver::~CObserver()
{
}

//---------------------------------------------------------------------------------------------------------------------
/**
	set observer eye point
*/
void 
CObserver::SetPos(CVec3 p_vPos)
{
	m_vEye = p_vPos;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	move observer eye point forward in the direction the observer is facing
	negative distance values move the observer backwards
*/
void 
CObserver::MoveForward(float p_fDistance)
{
	CVec3 vMove = m_vN * p_fDistance;
	m_vEye -= vMove;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	move observer eye point sideward (relative to the observer)
	positive distance values move the observer to the right, negative distance values 
	to the left
*/
void 
CObserver::MoveSideward(float p_fDistance)
{
	CVec3 vMove = m_vU * p_fDistance;
	m_vEye += vMove;
}



//---------------------------------------------------------------------------------------------------------------------
/**
	move observer eye point forward (in the direction the observer is facing), but
	move only in the XZ pane (Y remains unchanged)
*/
void 
CObserver::MoveForwardInXZPane(float p_fDistance)
{
	CVec3 vMove = -m_vN;
	vMove.y() = 0.0f;
	vMove.Normalize();
	vMove *= p_fDistance;
	m_vEye += vMove;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	rotates the observer around its local y (up) axis

	\param p_fAngle		angle in radiant; positive values turn right, negative values turn left
*/
void 
CObserver::Yaw(float p_fAngle)
{
	CMat3S m = CMat3S::CalcRotationMatrix(CAxisAngle(m_vV, p_fAngle));

	m_vU = m * m_vU;
	m_vN = m * m_vN;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	rotates the observer around its local z (front-back) axis
*/
void 
CObserver::Roll(float p_fAngle)
{
	CMat3S m = CMat3S::CalcRotationMatrix(CAxisAngle(m_vN, p_fAngle));

	m_vU = m * m_vU;
	m_vN = m * m_vN;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	rotates the observer around its local x (left-right) axis

	\param p_fAngle		angle in radiant; positive values turn downward, negative values upward
*/
void 
CObserver::Pitch(float p_fAngle)
{
	CMat3S m = CMat3S::CalcRotationMatrix(CAxisAngle(m_vU, p_fAngle));

	m_vV = m * m_vV;
	m_vN = m * m_vN;
}

//---------------------------------------------------------------------------------------------------------------------
void
CObserver::SetPitch(float p_fAngle)
{
	CMat3S m = CMat3S::CalcRotationMatrix(CAxisAngle(m_vU, p_fAngle));

	m_vV	= m * CVec3(0.0f, 1.0f,  0.0f);
	m_vN	= m_vU ^ m_vV;
	m_vN.Normalize();
}

//---------------------------------------------------------------------------------------------------------------------
void
CObserver::SetRoll(float p_fAngle)
{
	CMat3S m = CMat3S::CalcRotationMatrix(CAxisAngle(m_vN, p_fAngle));

	m_vV	= m * CVec3(0.0f, 1.0f,  0.0f);
	m_vU	= m_vV ^ m_vN;
	m_vU.Normalize();
}


//---------------------------------------------------------------------------------------------------------------------
/**
	rotates the observer around the global x axis

	\param p_fAngle		angle in radiant; positive values turn left, negative values right
*/
void 
CObserver::RotateX(float p_fAngle)
{
	CMat3S m = CMat3S::CalcRotationMatrix(CAxisAngle(CVec3::vXAxis, p_fAngle));

	m_vU = m * m_vU;
	m_vV = m * m_vV;
	m_vN = m * m_vN;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	rotates the observer around the global y axis

	\param p_fAngle		angle in radiant
*/
void 
CObserver::RotateY(float p_fAngle)
{
	CMat3S m = CMat3S::CalcRotationMatrix(CAxisAngle(CVec3::vYAxis, p_fAngle));

	m_vU = m * m_vU;
	m_vV = m * m_vV;
	m_vN = m * m_vN;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	rotates the observer around the global z axis

	\param p_fAngle		angle in radiant
*/
void 
CObserver::RotateZ(float p_fAngle)
{
	CMat3S m = CMat3S::CalcRotationMatrix(CAxisAngle(CVec3::vZAxis, p_fAngle));

	m_vU = m * m_vU;
	m_vV = m * m_vV;
	m_vN = m * m_vN;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	move the observers eye point
*/
void 
CObserver::Move(CVec3 p_fVector)
{
	m_vEye += p_fVector;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	get observer angle in XZ plane
*/
float 
CObserver::GetAngleXZ() const
{
	CVec3 q = - m_vN; 
	float fDist2=q.x() * q.x() + q.z() * q.z();
	if (fDist2 == 0.0f) {return 0.0f;}
	if (q.z() > 0)	{return  (acosf(q.x() / sqrtf(fDist2)));}
			else	{return -(acosf(q.x() / sqrtf(fDist2)));}
}


//---------------------------------------------------------------------------------------------------------------------
void			
CObserver::UpdateCamera(CCamera& p_xrCamera) const
{
	p_xrCamera.SetPos(m_vEye);
	p_xrCamera.SetOrientation(-m_vN);
	p_xrCamera.SetUpVec(m_vV);
}

//---------------------------------------------------------------------------------------------------------------------
bool
CObserver::IsUnderWater() const
{
	return C3DView2::Get()->GetWorld()->GetWaterHeight() > m_vEye.y(); 
}
//---------------------------------------------------------------------------------------------------------------------

void
CObserver::LookAt(const CVec3& p_rvLookAtPoint, CVec3 p_vUp)
{
	m_vN = (m_vEye - p_rvLookAtPoint).GetNormalized();
	m_vU = (p_vUp ^ m_vN).GetNormalized();
	m_vV = (m_vN ^ m_vU).GetNormalized();
}

//---------------------------------------------------------------------------------------------------------------------

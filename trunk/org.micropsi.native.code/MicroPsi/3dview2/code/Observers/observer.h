
#pragma once
#ifndef OBSERVER_H_INCLUDED
#define OBSERVER_H_INCLUDED

#include "e42/Camera.h"

class CObserver
{
public:
	CObserver();
	virtual ~CObserver();

	void			SetPos(CVec3 p_vPos);
	const CVec3&	GetPos() const					{ return m_vEye; }
	
	void			MoveForward(float p_fDistance);
	void			MoveSideward(float p_fDistance);
	void			MoveForwardInXZPane(float p_fDistance);

	void			MoveX(float p_fDistance)		{ Move(CVec3(p_fDistance, 0.0f, 0.0f)); }
	void			MoveY(float p_fDistance)		{ Move(CVec3(0.0f, p_fDistance, 0.0f)); }
	void			MoveZ(float p_fDistance)		{ Move(CVec3(0.0f, 0.0f, p_fDistance)); }
	void			Move(CVec3 p_fVector);

	void			Yaw(float p_fAngle);
	void			Roll(float p_fAngle);
	void			Pitch(float p_fAngle);

	void			SetPitch(float p_fAngle);
	void			SetRoll(float p_fAngle);

	void			RotateX(float p_fAngle);
	void			RotateY(float p_fAngle);
	void			RotateZ(float p_fAngle);

	void			LookAt(const CVec3& p_rvLookAtPoint, CVec3 p_vUp = CVec3(0.0f, 1.0f, 0.0f));

	float			GetAngleXZ() const;
	bool			IsUnderWater() const;

	void			UpdateCamera(CCamera& p_xrCamera) const;

private:

	CVec3			m_vEye;				///< eye point of the camera

	CVec3			m_vN;				///< backward vector of camera (local z axis)
	CVec3			m_vU;				///< right vector of camera (local x axis)
	CVec3			m_vV;				///< up vector of camera (local up axis)
};

#include "observer.inl"

#endif // OBSERVER_H_INCLUDED

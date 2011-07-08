#ifndef E42_QUATERNION_H_INCLUDED
#define E42_QUATERNION_H_INCLUDED

#include "baselib/geometry/CVector.h"

class CQuat
{
public:

	CVector3	m_vA;		// vector part
	float		m_fW;		// 


	CQuat();
	CQuat(const CVector3& vA, const float fW);
	CQuat(float fX, float fY, float fZ, float fW);

	CQuat(const CQuat& qSource);
	CQuat& operator=(const CQuat& qSource);

	bool operator==(const CQuat& qSource);
	bool operator!=(const CQuat& qSource);



	void	Normalize();

    
	void	Conjugate(); 
	float	Magnitude() const;

	/// create quaternion from axis and angle
	void	FromAxisAngle(const CVector3& vAxis, const float fAngle);

	/// convert quaternion to axis and angle
	void	ToAxisAngle(CVector3& p_vAxis, float p_fAngle) const;

	/// spherical linear interpolation
	void	Slerp(const CQuat& q1, const CQuat& q2, float fInterpolationFactor);
};

class CAxisAngle
{
public:
	CVector3	m_vAxis;
	float		m_fAngle;

	CAxisAngle();
	CAxisAngle(const CVector3& vAxis, float fAngle);


	CAxisAngle(const CAxisAngle& xSource);
	CAxisAngle& operator=(const CAxisAngle& xSource);

	bool operator==(const CAxisAngle& xSource);
	bool operator!=(const CAxisAngle& xSource);


	operator CQuat();
};

#include "baselib/geometry/Quaternion.inl"

#endif // E42_QUATERNION_H_INCLUDED

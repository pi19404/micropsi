#ifndef GAMELIB_ODEUTILS_H_INCLUDED
#define GAMELIB_ODEUTILS_H_INCLUDED

#include "baselib/color.h"
#include "baselib/geometry/Matrix.h"

namespace ODEUtils
{
	/// konvertiert eine e42-3x3-Matrix (floats) in eine ODE-Matrix (evtl. doubles)
	dMatrix3&		ToODEMatrix(dMatrix3& rTargetMatrix, const CMat3S& rSourceMatrix);

	/// konvertiert ein e42-Quaternion (floats) in ein ODE-Quaternion (evtl. doubles)
	dQuaternion&	ToODEQuaternion(dQuaternion& rTargetQuat, const CQuat& rSourceQuat);

	/// konvertiert ein ODE-Quaternion in ein Engine-Quaternion 
	CQuat			ToQuaternion(const dReal* pSourceQuat);

	/// konvertiert einen e42-Vektor in einen ODE-Vektor
	void			ToODEVector(dVector3 rTargetVector, const CVec3 rSourceVector);

	/// konvertiert einen ODE-Vektor in einen e42-Vector
	CVec3			ToVec3(const dVector3& rSourceVector);

	/// reduziert die auf einen Körper wirkenden Kräft - Parameter müssen zwei negative Zahlen sein, z.B. -0.01, -0.01
	void			DampenBody(dBodyID body, float fLinearVelocityFactor = -0.01f, float fAngularVelocityFactor = -0.01f);

	/// visualizes a hinge2 joint
	void			DrawHinge2(	dHinge2Joint* pHinge, 
								const CCamera* pCamera, 
								CColor xAxis1Color = CColor(255, 0, 0, 255), 
								CColor xAxis2Color = CColor(255, 50, 50, 255));
		
#include "ODEUtils.inl"

} // namespace ODEUtils

#endif // GAMELIB_ODEUTILS_H_INCLUDED
#include "stdafx.h"
//#include "e42/Camera.h"
#include "e42/utils/GfxDebugMarker.h"

#include "ODEUtils.h"

namespace ODEUtils
{

//---------------------------------------------------------------------------------------------------------------------
void			
DrawHinge2(dHinge2Joint* pHinge, const CCamera* pCamera, CColor xAxis1Color, CColor xAxis2Color)
{
	// Ankerpunkt 1 rendern

	dVector3 vAnchor1;
	pHinge->getAnchor(vAnchor1);
	CVec3 vAnchorPoint1 = CVec3((float) vAnchor1[0], (float) vAnchor1[1], (float) vAnchor1[2]);			
	CMat4S matAnchor1 = CMat4S::CalcTranslationMatrix(vAnchorPoint1);
	CGfxDebugMarker::Get().DrawSphere(matAnchor1, pCamera, 0.05f, 16, 16, xAxis1Color);

	// Ankerpunkt 2 rendern (ist identisch mit Ankerpunkt 2, wenn das Contraint zu 100% erfüllt ist)

	dVector3 vAnchor2;
	pHinge->getAnchor(vAnchor2);
	CVec3 vAnchorPoint2 = CVec3((float) vAnchor2[0], (float) vAnchor2[1], (float) vAnchor2[2]);			
	CMat4S matAnchor2 = CMat4S::CalcTranslationMatrix(vAnchorPoint1);
	CGfxDebugMarker::Get().DrawSphere(matAnchor2, pCamera, 0.05f, 16, 16, xAxis2Color);


	// Achse 1 Rendern

	dVector3 vAxis1;
	pHinge->getAxis1(vAxis1);
	CVec3 vRotationAxis = CVec3((float) vAxis1[0], (float) vAxis1[1], (float) vAxis1[2]) ^ CVec3(0.0f, 0.0f, 1.0f);
	float fRotationAngle = acosf(CVec3((float) vAxis1[0], (float) vAxis1[1], (float) vAxis1[2]) * CVec3(0.0f, 0.0f, 1.0f));
	CQuat q; q.FromAxisAngle(vRotationAxis, -fRotationAngle);
	CMat4S matRotation = CMat4S::CalcMatrix(q, vAnchorPoint1);

	CGfxDebugMarker::Get().DrawCylinder(matRotation, pCamera, 0.01f, 0.01f, 1.0f, 16, 16, xAxis1Color);

	// Achse 2 Rendern

	dVector3 vAxis2;
	pHinge->getAxis2(vAxis2);
	vRotationAxis = CVec3((float) vAxis2[0], (float) vAxis2[1], (float) vAxis2[2]) ^ CVec3(0.0f, 0.0f, 1.0f);
	fRotationAngle = acosf(CVec3((float) vAxis2[0], (float) vAxis2[1], (float) vAxis2[2]) * CVec3(0.0f, 0.0f, 1.0f));
	q.FromAxisAngle(vRotationAxis, -fRotationAngle);
	matRotation = CMat4S::CalcMatrix(q, vAnchorPoint2);

	CGfxDebugMarker::Get().DrawCylinder(matRotation, pCamera, 0.01f, 0.01f, 1.0f, 16, 16, xAxis2Color);
}

//---------------------------------------------------------------------------------------------------------------------
void			
DampenBody(dBodyID body, float fLinearVelocityFactor, float fAngularVelocityFactor)
{
	assert( fLinearVelocityFactor <= 0 && fAngularVelocityFactor <= 0 );
	if(!dBodyIsEnabled( body )) 
	{
		return;
	}
	dReal const * V = dBodyGetLinearVel( body );
	dBodyAddForce( body, fLinearVelocityFactor*V[0], fLinearVelocityFactor*V[1], fLinearVelocityFactor*V[2] );
	dReal const * A = dBodyGetAngularVel( body );
	dBodyAddTorque( body, fAngularVelocityFactor*A[0], fAngularVelocityFactor*A[1], fAngularVelocityFactor*A[2] );
}
//---------------------------------------------------------------------------------------------------------------------


} // namespace ODEUtils
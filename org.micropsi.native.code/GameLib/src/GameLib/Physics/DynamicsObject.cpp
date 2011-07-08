#include "stdafx.h"
#include "GameLib/Physics/DynamicsObject.h"

#include "baselib/geometry/Matrix.h"

//---------------------------------------------------------------------------------------------------------------------
void			
CDynamicsObject::GetMatrix(CMat4S& rMatrix) const
{
	rMatrix.FromQuaternion(GetRotation());
	rMatrix.Translate(GetPosition());
}
//---------------------------------------------------------------------------------------------------------------------
void
CDynamicsObject::Render(const CCamera& camera)
{
}
//---------------------------------------------------------------------------------------------------------------------
CDynamicsObject::ContactAction	
CDynamicsObject::CheckContact(dContact* contact, DWORD nbContacts,
								dBodyID idMyBody, dGeomID idMyGeom, dBodyID idOtherBody, dGeomID idOtherGeom)
{
	return CA_CreateDefaultContacts;
}
//---------------------------------------------------------------------------------------------------------------------

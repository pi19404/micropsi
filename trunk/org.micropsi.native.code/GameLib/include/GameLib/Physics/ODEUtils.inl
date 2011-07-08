//---------------------------------------------------------------------------------------------------------------------
inline
dMatrix3&	
ToODEMatrix(dMatrix3& rTargetMatrix, const CMat3S& rSourceMatrix)
{
	for(int i=0; i<12; ++i)
	{
		rTargetMatrix[i] = (dReal) rSourceMatrix.m_af[i];
	}
	return rTargetMatrix;
}
//---------------------------------------------------------------------------------------------------------------------
inline
dQuaternion&	
ToODEQuaternion(dQuaternion& rTargetQuat, const CQuat& rSourceQuat)
{
	rTargetQuat[1] = (dReal)rSourceQuat.m_vA.x();
	rTargetQuat[2] = (dReal)rSourceQuat.m_vA.y();
	rTargetQuat[3] = (dReal)rSourceQuat.m_vA.z();
	rTargetQuat[0] = (dReal)rSourceQuat.m_fW;
	return rTargetQuat;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CQuat			
ToQuaternion(const dReal* pSourceQuat)
{
	CQuat rQuat;
	rQuat.m_vA.x()	= (float) pSourceQuat[1];
	rQuat.m_vA.y()	= (float) pSourceQuat[2];
	rQuat.m_vA.z()	= (float) pSourceQuat[3];
	rQuat.m_fW		= (float) pSourceQuat[0];
	return rQuat;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void		
ToODEVector(dVector3 rTargetVector, const CVec3 rSourceVector)
{
	rTargetVector[0] = (dReal) rSourceVector.x();
	rTargetVector[1] = (dReal) rSourceVector.y();
	rTargetVector[2] = (dReal) rSourceVector.z();
//	rTargetVector[3] = 1.0;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CVec3			
ToVec3(const dVector3& rSourceVector)
{
	return CVec3((float) rSourceVector[0], (float) rSourceVector[1], (float) rSourceVector[2]);
}
//---------------------------------------------------------------------------------------------------------------------

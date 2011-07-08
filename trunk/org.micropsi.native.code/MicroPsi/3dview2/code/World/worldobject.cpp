
#include "Application/stdinc.h"
#include "World/worldobject.h"

#include "baselib/geometry/Plane.h"
#include "baselib/xmlutils.h"
#include "e42/AnimationCtrl.h"
#include "e42/e42application.h"

#include "Engine/Terrain/terrainsystem.h"

#include "World/world.h"
#include "World/objectmanager.h"

#include "Utilities/micropsiutils.h"

#include "Application/3DView2.h"
#include "GameLib/World/SimTimeCtrl.h"

using std::string;
using namespace XMLUtils;

//---------------------------------------------------------------------------------------------------------------------
/**
*/
CWorldObject::CWorldObject(CWorld* p_pxWorld)
{
	m_pxWorld = p_pxWorld;
	m_bVisible = true;
	m_iCurrentLOD = 0;

	m_iID = INVALID_OBJID;
	m_fScale = 1.0f;

	m_pAnimationCtrl = 0;
}

//---------------------------------------------------------------------------------------------------------------------
/**
*/
CWorldObject::CWorldObject(CWorld* p_pxWorld, 
						   const char* p_pcClassName, 
						   float p_fX, float p_fY, float p_fZ, 
						   float p_fHeight, float p_fPSIOrientationAngle, __int64 p_iID, int p_iVariation)
{
	m_pxWorld = p_pxWorld;
	m_bVisible = true;
	m_bMoving  = false;
	m_iCurrentLOD = 0;

	if(p_iID != INVALID_OBJID)
	{
		m_iID = p_iID;
	}
	else
	{
		m_iID				= m_pxWorld->GetObjectManager()->GetFreeID();
	}

	m_vPSIPos = CVec3(p_fX, p_fY, p_fZ);
	m_fHeight = p_fHeight;
	m_fPSIOrientationAngle = p_fPSIOrientationAngle;

	
	int iVariation = p_iVariation >= 0 ? p_iVariation : (int) m_iID % m_pxWorld->GetVisualization().GetNumObjectVariations(p_pcClassName);
	m_pxObjTypeData = m_pxWorld->GetVisualization().GetObjectVisualization(p_pcClassName, iVariation);
	m_hModel = m_pxObjTypeData->m_axSortedLODLevels[0]->m_hModel;

	UpdateMatrix();

	if(m_hModel->HasAnimations())
	{
	    m_pAnimationCtrl = new CAnimationCtrl(&CE42Application::Get());
		m_pAnimationCtrl->SetModel(m_hModel);
		m_pAnimationCtrl->SetAnimation(".default");
	}
	else
	{
		m_pAnimationCtrl = 0;
	}

// TODO: object scaling
/*

	m_fScale = 1.0f;
	nShapeNode* p = (nShapeNode*) kernelServer->Lookup((sPath + "/shape").c_str());
	const bbox3& b = p->GetLocalBox();
	CVec3 size = b.extents() * 2.0f;
	m_fScale = m_fHeight / size.y;
*/
}

//---------------------------------------------------------------------------------------------------------------------
/**
*/
CWorldObject::~CWorldObject()
{
	delete m_pAnimationCtrl;
}

//---------------------------------------------------------------------------------------------------------------------
void					
CWorldObject::SetPSIPos(CVec3& p_vrPos, float p_fPSIOrientationAngle)
{
	m_fPSIOrientationAngle = p_fPSIOrientationAngle;

	if(m_pxObjTypeData->m_bInterpolatedMovement)
	{
		float fSpeed = (p_vrPos - GetPSIPos()).Abs() / 0.2f;
		MoveTo(p_vrPos.x(), p_vrPos.y(), p_vrPos.z(), fSpeed);
	}
	else
	{
		CVec3 vOldPos = GetEnginePos();
		m_vPSIPos = p_vrPos;
		UpdateMatrix();
		UpdateQuadTree(vOldPos);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CWorldObject::SetPSIOrientationAngle(float p_fAngle)
{
	m_fPSIOrientationAngle = p_fAngle;
	UpdateMatrix();
}


//---------------------------------------------------------------------------------------------------------------------
void
CWorldObject::UpdateMatrix()
{
	if(m_pxObjTypeData->m_pxGroundContactPoints)
	{
		CTerrainSystem* pxTerrain = m_pxWorld->GetTerrain();

		CVec3 vEnginePos =  Utils::PsiPos2Engine(m_vPSIPos);
		CVec3 vP[3];
		for(int i=0; i<3; ++i)
		{
			vP[i] = vEnginePos + m_pxObjTypeData->m_pxGroundContactPoints->m_vPoints[i];
			vP[i].y() = pxTerrain->GetTerrainHeight(vP[i].x(), vP[i].z());
		}

		CPlane xPlane(vP[0], vP[1], vP[2]);
		if(xPlane.m_vNormal.y() < 0.0f) 
		{
			xPlane.m_vNormal = -xPlane.m_vNormal;
		}

		CVec3 vX =  CVec3(0.0f, 0.0f, -1.0f) ^ xPlane.m_vNormal;
		vX.Normalize();
		CVec3 vZ =  vX ^ xPlane.m_vNormal;
		vZ.Normalize();

		CMat3S m;
		m.SetIdentity();
		m.SetRow(0, vX);
		m.SetRow(1, xPlane.m_vNormal);
		m.SetRow(2, vZ);

		m = m * CMat3S::CalcRotationMatrix(CAxisAngle(xPlane.m_vNormal, Utils::PsiAngle2Engine(m_fPSIOrientationAngle)));
		vEnginePos.y() += xPlane.PointY(vEnginePos.x(), vEnginePos.z());
		m_mTransform = CMat4S::CalcMatrix(m, vEnginePos);
//		m_mTransform = CMat4S::CalcMatrix(m, GetEnginePos());
	}
	else
	{
		m_mTransform = CMat4S::CalcRotationMatrix(CAxisAngle(CVec3::vYAxis, Utils::PsiAngle2Engine(m_fPSIOrientationAngle)));
		m_mTransform.Translate(GetEnginePos());
	}
}


//---------------------------------------------------------------------------------------------------------------------
void
CWorldObject::UpdateQuadTree(const CVec3& p_vOldPos)
{
	CBoundingSphere xOldSphere = GetModelBoundingSphere();
	xOldSphere.m_vCenter += p_vOldPos;
	m_pxWorld->GetObjectManager()->m_pxQuadTree->MoveItem(this, xOldSphere, GetBoundingSphere());
}

//---------------------------------------------------------------------------------------------------------------------
void 
CWorldObject::MoveTo(float p_fX, float p_fY, float p_fZ, float p_fSpeed)
{
	m_vTargetPos.x() = p_fX;
	m_vTargetPos.y() = p_fY;
	m_vTargetPos.z() = p_fZ;
	m_vOriginalPos = m_vPSIPos;
	m_fSpeed = p_fSpeed;
	m_dStartTime = C3DView2::Get()->GetSimTimeCtrl()->GetDiscreteSimTime();
	if(m_vTargetPos != GetPSIPos())
	{
		m_bMoving  = true;
		GetWorld()->GetObjectManager()->StartToTick(GetID());
	}
}

//---------------------------------------------------------------------------------------------------------------------
/**
	liefert true wenn sichtbar, false sonst
*/
bool
CWorldObject::UpdateLODModel(const CVec3& p_rvCameraPos)
{
	float fDistSquare = (GetEnginePos() - p_rvCameraPos).AbsSquare();

	assert(m_pxObjTypeData->m_axSortedLODLevels.size() != 0);
	int iLod;

	// objects with smallest range are last in array
	// therefore, iterate from end to start and stop as soon as we are in range of an lod level
	for(iLod=(int)m_pxObjTypeData->m_axSortedLODLevels.size() -1; iLod>=0; --iLod)
	{
		if(m_pxObjTypeData->m_axSortedLODLevels[iLod]->m_fMaxDistanceSquare >= fDistSquare)
		{
			break;
		}
	}

	m_iCurrentLOD = iLod;
	if(m_iCurrentLOD < 0)
	{
		m_bVisible = false;
	}
	else
	{
		m_bVisible = true;
		TModelHandle hNewModel = m_pxObjTypeData->m_axSortedLODLevels[m_iCurrentLOD]->m_hModel;
		if(hNewModel != m_hModel)
		{
			m_hModel = hNewModel;
			if(m_hModel->HasAnimations())
			{
				if(!m_pAnimationCtrl)	
				{ 
					m_pAnimationCtrl = new CAnimationCtrl(&CE42Application::Get()) ;
				}
				m_pAnimationCtrl->SetModel(m_hModel);
				m_pAnimationCtrl->StartAnimation(".default");
			}
			else
			{
				delete m_pAnimationCtrl;
				m_pAnimationCtrl = 0;
			}
		}
	}

	return m_bVisible;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	tick method
	\return		false if this object does not want to tick any more; true otherwise 
*/
bool 
CWorldObject::Tick()
{
	return 	InterpolateMovement();
}


//---------------------------------------------------------------------------------------------------------------------
/**
	tick method; performs movement of objects
	\return		false if this object does not need to be interpolated any more
*/
bool 
CWorldObject::InterpolateMovement()
{
	double dTime = C3DView2::Get()->GetSimTimeCtrl()->GetDiscreteSimTime();
	CVec3 vDir = m_vTargetPos - m_vOriginalPos;
	double dDist = vDir.Abs();
    double dTravelTimeTillNow = dTime - m_dStartTime;
	double dDistTraveled = (dTravelTimeTillNow * (double) m_fSpeed);

	bool bFinished = false;
	CVec3 vOldPos = GetEnginePos();
	if(dDist == 0 ||  dDistTraveled > dDist)
	{
		m_vPSIPos = m_vTargetPos;
		bFinished = true;
		m_bMoving = false;
	}
	else
	{
		vDir.Normalize();
		m_vPSIPos = m_vOriginalPos + (vDir * (float) dDistTraveled);
	}

	UpdateMatrix();
	UpdateQuadTree(vOldPos);

	return !bFinished;
}

//---------------------------------------------------------------------------------------------------------------------
void					
CWorldObject::Render(TRenderContextPtr spxRenderContext)
{
	//if(m_bMoving)
	//{
	//	InterpolateMovement();
	//}

	if (m_pAnimationCtrl)
    {
        m_pAnimationCtrl->SetupSound(GetEnginePos());
        m_pAnimationCtrl->SetupModel();
    }
	
	TRenderContextPtr spxNewRenderContext;
    spxNewRenderContext.Create();
    *spxNewRenderContext = *spxRenderContext;

	m_hModel->Render(spxNewRenderContext, m_mTransform);
}


//---------------------------------------------------------------------------------------------------------------------
void					
CWorldObject::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
	string sClass			= GetXMLTagString(p_pXmlElement, "class");
	m_vPSIPos				= GetXMLTagVector(p_pXmlElement, "pos");
	m_fHeight				= GetXMLTagFloat(p_pXmlElement, "height");
	m_fPSIOrientationAngle	= GetXMLTagFloat(p_pXmlElement, "angle");
	int iVariation			= GetXMLTagInt(p_pXmlElement, "variation");

	m_iID				= m_pxWorld->GetObjectManager()->GetFreeID();
	m_pxObjTypeData		= m_pxWorld->GetVisualization().GetObjectVisualization(sClass.c_str(), iVariation);
	m_hModel			= m_pxObjTypeData->m_axSortedLODLevels[0]->m_hModel;

	if(m_hModel->HasAnimations())
	{
	    m_pAnimationCtrl = new CAnimationCtrl(&CE42Application::Get());
		m_pAnimationCtrl->SetModel(m_hModel);
		m_pAnimationCtrl->StartAnimation(".default");
	}

	UpdateMatrix();
}


//---------------------------------------------------------------------------------------------------------------------
void					
CWorldObject::ToXMLElement(TiXmlElement* p_pXmlElement) const
{
	WriteXMLTagString(p_pXmlElement, "class", GetClass());
	WriteXMLTagVector(p_pXmlElement, "pos", m_vPSIPos);
	WriteXMLTagFloat(p_pXmlElement, "height", m_fHeight);
	WriteXMLTagFloat(p_pXmlElement, "angle", m_fPSIOrientationAngle);
	WriteXMLTagInt(p_pXmlElement, "variation", m_pxObjTypeData->m_iVariationNumber);
}

//---------------------------------------------------------------------------------------------------------------------
inline
CVec3					
CWorldObject::GetEnginePos() const
{
	CVec3 vRet = Utils::PsiPos2Engine(m_vPSIPos);
	vRet.y() += m_pxWorld->GetTerrain()->GetTerrainHeight(vRet.x(), vRet.z());
	return vRet;
}

//---------------------------------------------------------------------------------------------------------------------

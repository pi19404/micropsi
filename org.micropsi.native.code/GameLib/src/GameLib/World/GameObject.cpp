#include "stdafx.h"
#include "GameLib/World/GameObject.h"
#include "GameLib/World/GameObjectProperties.h"

#include "tinyxml.h"

#include "e42/E42Application.h"
#include "e42/core/Model.h"
#include "e42/core/D3DXFrame.h"
#include "e42/core/ModelFactory.h"
#include "e42/AnimationCtrl.h"

#include "GameLib/Utilities/XMLUtils.h"

using std::string;
using std::map;

//---------------------------------------------------------------------------------------------------------------------
CGameObj* 
__cdecl CGameObj::Create(const CGameObjClassMgr::CClassInfo* p_pxClassInfo)
{
    return new CGameObj(p_pxClassInfo);
}

//---------------------------------------------------------------------------------------------------------------------
void 
__cdecl CGameObj::Destroy(CGameObj* pGameObj)
{
    delete (CGameObj*)pGameObj;
}

//---------------------------------------------------------------------------------------------------------------------
CGameObj::CGameObj(const CGameObjClassMgr::CClassInfo* p_pxClassInfo)
:   m_ObjID(0),
    m_hndModel(0),
    m_pAnimationCtrl(0),
	m_pxClassInfo(p_pxClassInfo)
{
    m_vPos = CVec3(0, 0, 0);
    m_mRot.SetIdentity();

	m_bCollision = false;
	m_bTouchable = false;
	m_bVisible	 = true;

	int iNumProperties = (int) p_pxClassInfo->m_mpxProperties.size();
	m_axPropertyValues.resize(iNumProperties);
	if(iNumProperties > 0)
	{
		map<string, CGameObjProperty*>::const_iterator i;
		for(i=p_pxClassInfo->m_mpxProperties.begin(); i!=p_pxClassInfo->m_mpxProperties.end(); i++)
		{
			CGameObjProperty* pProp = i->second;
			m_axPropertyValues[pProp->m_iIndex] = pProp->CreateValue();
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
CGameObj::~CGameObj()
{
    delete m_pAnimationCtrl;
    m_pAnimationCtrl = 0;
    m_hndModel.Release();

	if(m_pxClassInfo->m_mpxProperties.size() > 0)
	{
		map<string, CGameObjProperty*>::const_iterator i;
		for(i=m_pxClassInfo->m_mpxProperties.begin(); i!=m_pxClassInfo->m_mpxProperties.end(); i++)
		{
			i->second->DestroyValue(m_axPropertyValues[i->second->m_iIndex]);
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CGameObj::LoadAnimations(const std::string& p_srXMLFile)
{
	TiXmlDocument* pxDoc = new TiXmlDocument(p_srXMLFile.c_str());
	if (pxDoc->LoadFile()) 
	{ 
		TiXmlNode* pxRootFrame = pxDoc->FirstChild("init");
		if(pxRootFrame) 
		{
            TiXmlNode* pxAnimationsFrame = pxRootFrame->FirstChild("animations");
            if (pxAnimationsFrame)
            {
                TiXmlElement* pxAnimationsElement = pxAnimationsFrame->ToElement();
                XMLUtils::ReadAnimationsFromXML(pxAnimationsElement, m_hndModel, m_pAnimationCtrl);
            }
            else
            {
                DebugPrint("couldn't find animations-Frame in file %s", p_srXMLFile.c_str());
            }
        }
        else
        {
            DebugPrint("couldn't find init-Frame in file %s", p_srXMLFile.c_str());
            assert(false);
        }
	}
    else
    {
        DebugPrint("couldn't parse file %s", p_srXMLFile.c_str());
    }

	delete pxDoc;
}

//---------------------------------------------------------------------------------------------------------------------
void
CGameObj::SetOrientation(const CVec3& vOrientation)
{
    CVec3 vUp(0, 1, 0);
    CVec3 vRight = vUp ^ vOrientation;
    if (vRight.Abs() < 1e-4f) return;
    vRight.Normalize();

    CVec3 vCurOrientation = vRight ^ vUp;
    if (vCurOrientation.Abs() < 1e-4f) return;
    vCurOrientation.Normalize();
    
    CMat3S mRot;
    mRot.SetRow(0, vRight);
    mRot.SetRow(1, vUp);
    mRot.SetRow(2, vOrientation);

    SetRot(mRot);
}
//---------------------------------------------------------------------------------------------------------------------
CMat4S
CGameObj::CalcTransform() const
{
    return CMat4S(
        m_mRot(0, 0),   m_mRot(0, 1),   m_mRot(0, 2),   0,
        m_mRot(1, 0),   m_mRot(1, 1),   m_mRot(1, 2),   0,
        m_mRot(2, 0),   m_mRot(2, 1),   m_mRot(2, 2),   0,
        m_vPos.x(),     m_vPos.y(),     m_vPos.z(),     1);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CGameObj::Render(CMeshCombiner* pMeshCombiner, TRenderContextPtr spxRenderContext)
{
    if (m_hndModel)
    {
        if (m_pAnimationCtrl)
        {
            m_pAnimationCtrl->SetupSound(GetPos());
            m_pAnimationCtrl->SetupModel();
        }

        TRenderContextPtr spxNewRenderContext;
        spxNewRenderContext.Create();
        *spxNewRenderContext = *spxRenderContext;

        m_hndModel->Render(spxNewRenderContext, CalcTransform());
    }
}
//---------------------------------------------------------------------------------------------------------------------
bool
CGameObj::GetFrameTransform(CMat4S* pOutTransform, const std::string& sFrameName, float p_fDiscreteGameTime) const
{
    if (!m_hndModel)
    {
        return false;
    }

    if (m_pAnimationCtrl)
    {
		if (m_pAnimationCtrl->GetFrameTransform(pOutTransform, CalcTransform(), sFrameName.c_str(), p_fDiscreteGameTime))
		{
			return true;
		}
    }

	if (m_hndModel->GetFrameTransform(pOutTransform, CalcTransform(), sFrameName.c_str()))
	{
		return true;
	}

	return false;
}
//---------------------------------------------------------------------------------------------------------------------
bool 
CGameObj::GetCollisionData(CVec3* pvOutCenter, float* pfOutHeight, float* pfOutXZRadius) const
{
    return false;
}
//---------------------------------------------------------------------------------------------------------------------
/**
	Factory-Methode; erstellt Kollisionsobjekt
	Ableitungen dieser Klasse können die Funktion überschreiben, um andere Typen von Kollisionsobjekten zu erzeugen
	als default erzeugen wir eine Kugel
*/
CCollisionObject*	
CGameObj::CreateCollisionObject()
{
	return new CCollisionObjectSphere(GetBoundingSphere());
}

//---------------------------------------------------------------------------------------------------------------------
/** 
	Analogon zur Factory-Methode; erstört das Kollisionsobjekt
	besteht normalerweise einfach aus einem "delete po_pxCollisionObject" - aber eine Ableitung, die ein Objekt 
	erzeugt, muss es eben selbst wieder löschen
*/
void
CGameObj::DeleteCollisionObject(CCollisionObject* po_pxCollisionObject)
{
	delete po_pxCollisionObject;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CGameObj::SetModel(const std::string& sModelFile)
{
	TModelHandle hModel = CE42Application::Get().GetModelFactory()->CreateModelFromFile(string("model>") + sModelFile);
	if(hModel)
	{
		m_hndModel = hModel; 
		if(m_hndModel->HasAnimations())
		{
			if (m_pAnimationCtrl)
			{
				m_pAnimationCtrl->Reset();
			}
			else
			{
				m_pAnimationCtrl = new CAnimationCtrl(&CE42Application::Get());
			}

			m_pAnimationCtrl->SetModel(m_hndModel);
			m_pAnimationCtrl->SetAnimation(".default"); // FIXME: kann raus, sobald Model klonen geht :)
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
/** 
	in dieser Methode sollte das Kollisionsobject aktualisiert werden
	Ableitungen, die eigenen Kollisionsobjekte implementieren, müssen diese Methode überschreiben
*/
void
CGameObj::UpdateCollisionObject(CCollisionObject* po_pxCollisionObject)
{
	((CCollisionObjectSphere*) po_pxCollisionObject)->m_xSphere = GetBoundingSphere();
}

//---------------------------------------------------------------------------------------------------------------------
/**
	liefert die BoundingSphere des Objektes, wird zum Culling / Suchen benutzt
*/
CBoundingSphere
CGameObj::GetBoundingSphere() const
{
	return m_hndModel->GetBoundingSphere();
}
//---------------------------------------------------------------------------------------------------------------------
bool						
CGameObj::GetPropertyValueBool(const std::string p_rsPropertyName) const
{
	const CGameObjProperty* p = m_pxClassInfo->FindProperty(p_rsPropertyName);
	if(p  &&  p->GetType() == CGameObjPropertyBool::GetTypeName())
	{
		return ((CGameObjPropertyBool::CValue*) m_axPropertyValues[p->m_iIndex])->m_bValue;
	}
	else
	{
		assert(false);
		return false;
	}
}

//---------------------------------------------------------------------------------------------------------------------
int			
CGameObj::GetPropertyValueInt(const std::string p_rsPropertyName) const
{
	const CGameObjProperty* p = m_pxClassInfo->FindProperty(p_rsPropertyName);
	if(p  &&  p->GetType() == CGameObjPropertyInt::GetTypeName())
	{
		return ((CGameObjPropertyInt::CValue*) m_axPropertyValues[p->m_iIndex])->m_iValue;
	}
	else
	{
		assert(false);
		return 0;
	}
}

//---------------------------------------------------------------------------------------------------------------------
std::string			
CGameObj::GetPropertyValueString(const std::string p_rsPropertyName) const
{
	const CGameObjProperty* p = m_pxClassInfo->FindProperty(p_rsPropertyName);
	if(p  &&  p->GetType() == CGameObjPropertyString::GetTypeName())
	{
		return ((CGameObjPropertyString::CValue*) m_axPropertyValues[p->m_iIndex])->m_sValue;
	}
	else
	{
		assert(false);
		return "";
	}
}

//---------------------------------------------------------------------------------------------------------------------

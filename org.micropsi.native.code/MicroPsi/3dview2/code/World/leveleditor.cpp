#include "Application/stdinc.h"
#include "World/leveleditor.h"

#include "baselib/debugprint.h"

#include "e42/Camera.h"
#include "baselib/geometry/Ray.h"
#include "baselib/geometry/BoundingSphere.h"
#include "e42/utils/GfxDebugMarker.h"

#include "Engine/Terrain/terrainsystem.h"

#include "Application/3dview2.h"

#include "World/world.h"
#include "World/worldobject.h"
#include "World/objectmanager.h"

#include "Communication/CommunicationModule.h"
#include "Communication/WorldController.h"

#include "Utilities/micropsiutils.h"

#include "GameLib/Collision/OpcodeMesh.h"

using std::vector;

//-------------------------------------------------------------------------------------------------------------------------------------------
CLevelEditor::CLevelEditor(CWorld* p_pxWorld)
{
	m_pxWorld						= p_pxWorld;

	m_eClickMode					= CM_Select;
	m_bRenderingEnabled				= false;
	m_pxSelectedObject				= 0;
	m_bRandomRotationForNewObjects	= true;
	m_iObjectVariationToCreate		= -1;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CLevelEditor::~CLevelEditor()
{
}

//-------------------------------------------------------------------------------------------------------------------------------------------
void		
CLevelEditor::ClearAllObjects()
{
	C3DView2::Get()->GetCommunicationModule()->GetWorldController()->ClearAllObjects();
}

//-------------------------------------------------------------------------------------------------------------------------------------------
bool		
CLevelEditor::SaveWorld()
{
	return C3DView2::Get()->GetCommunicationModule()->GetWorldController()->SaveWorld();
}
	
//-------------------------------------------------------------------------------------------------------------------------------------------
bool		
CLevelEditor::SaveWorldAs(const std::string& p_rsFilename)
{
	return C3DView2::Get()->GetCommunicationModule()->GetWorldController()->SaveWorldAs(p_rsFilename);
}

//-------------------------------------------------------------------------------------------------------------------------------------------
std::string		
CLevelEditor::GetCurrentWorldFileName() const
{
	return C3DView2::Get()->GetCommunicationModule()->GetWorldController()->GetCurrentWorldFileName();
}

//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CLevelEditor::SetClickMode(ClickMode p_eMode)
{
	m_eClickMode = p_eMode;
}

//-------------------------------------------------------------------------------------------------------------------------------------------
/** 
	invoke this function to notify the leveleditor of a mouse click
	Click-Pos is in transformed screen coordinates: upper left corner is (-1, 1), center is (0, 0)
*/
void 
CLevelEditor::OnClick(float p_fClickXPos, float p_fClickYPos)
{
	switch(m_eClickMode)
	{
	case CM_Select:
		{
			CRay xRay = GetMouseRay(p_fClickXPos, p_fClickYPos);

			// zuerst Trefferpunkt auf dem Boden suchen, um nicht durch den Boden hindurch ein Objekt zu treffen 
			CVec3 vHitPoint;
			float fMaxHitDist = FLT_MAX;
			if(m_pxWorld->GetTerrain()->HitTest(xRay, vHitPoint))
			{
				fMaxHitDist = (xRay.m_vBase - vHitPoint).Abs();
			}

			CWorldObject* pxNearestObject = 0;
			float fDistanceToNearestObjectSquared = FLT_MAX;

			bool bIgnoredSelected = false;
			CObjectManager::ObjectIterator i;
			m_pxWorld->GetObjectManager()->StartIteration(i);
			while(CWorldObject* pxObj = m_pxWorld->GetObjectManager()->Iterate(i))
			{
				// visible?
				if(!pxObj->GetVisible())
				{
					continue;
				}

				// check bounding sphere first
				if(!pxObj->GetBoundingSphere().Overlaps(xRay))
				{
					continue;
				}

				// check geometry
				if(!pxObj->GetCollisionModel()->CollideWithRay(pxObj->GetWorldTransformation(), xRay))
				{
//					DebugPrint("David: %s - no collision!", pxObj->GetClass().c_str());
					continue;
				}
				else
				{
//					DebugPrint("David: %s - collision!", pxObj->GetClass().c_str());
				}

				float fDistSquared = (xRay.m_vBase - pxObj->GetBoundingSphere().m_vCenter).AbsSquare();
				if(fDistSquared < fDistanceToNearestObjectSquared)
				{	
					// gucken, ob nicht vorher schon Boden geschnitten
					float fDist = sqrtf(fDistSquared);
					if(fDist - pxObj->GetBoundingSphere().m_fRadius < fMaxHitDist)
					{
						// ignore currently selected object - allows user to click again and select object behind it :)
						if(pxObj == m_pxSelectedObject)
						{
							bIgnoredSelected = true;
							continue;
						}
						else
						{
							pxNearestObject = pxObj;
							fDistanceToNearestObjectSquared = fDistSquared;
						}
					}
				}
			}

			if(pxNearestObject == 0  &&  bIgnoredSelected)
			{
				pxNearestObject = m_pxSelectedObject;
			}

			m_pxSelectedObject = pxNearestObject;
			if(m_pxSelectedObject)
			{
				//DebugPrint("selected %s at %.2f %.2f %.2f", m_pxSelectedObject->GetClass().c_str(), 
				//	m_pxSelectedObject->GetEnginePos().x(), m_pxSelectedObject->GetEnginePos().y(), m_pxSelectedObject->GetEnginePos().y()); 
			}
		}
		break;
	case CM_CreateObject:
		{
			CRay xRay = GetMouseRay(p_fClickXPos, p_fClickYPos);
			CVec3 vHitPoint;
			if(m_pxWorld->GetTerrain()->HitTest(xRay, vHitPoint))
			{
				vHitPoint = Utils::EnginePos2Psi(vHitPoint);
				C3DView2::Get()->GetCommunicationModule()->GetWorldController()->CreateNewObject(
					m_sObjectTypeToCreate.c_str(),
					CVec3(vHitPoint.x(), vHitPoint.y(), 0.0f),
					m_bRandomRotationForNewObjects ? (rand() % 36000) / 100.0f : 0.0f,
					1.0f, 
					m_iObjectVariationToCreate);
			}
		}
		break;
	}
}

//-------------------------------------------------------------------------------------------------------------------------------------------
void
CLevelEditor::DeleteSelectedObjects()
{
	if(m_pxSelectedObject)
	{
		C3DView2::Get()->GetCommunicationModule()->GetWorldController()->DeleteObject(m_pxSelectedObject->GetID());
		m_pxSelectedObject = 0;
	}
}

//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CLevelEditor::Tick()
{
}

//-------------------------------------------------------------------------------------------------------------------------------------------
void
CLevelEditor::Render()
{
	if(m_bRenderingEnabled)
	{
		if(m_pxSelectedObject)
		{
			CGfxDebugMarker::Get().DrawSphere(
				m_pxSelectedObject->GetBoundingSphere().m_vCenter,
				C3DView2::Get()->GetCamera(),
				m_pxSelectedObject->GetBoundingSphere().m_fRadius, CColor(255, 0, 0));
		}
	}
}

//-------------------------------------------------------------------------------------------------------------------------------------------
/**
	Click-Pos is in transformed screen coordinates: upper left corner is (-1, 1), center is (0, 0)
*/
CRay		
CLevelEditor::GetMouseRay(float p_fClickXPos, float p_fClickYPos) const
{
	CCamera* pCamera = (CCamera*) C3DView2::Get()->GetCamera();
	CVec4 vTmp;
	CRay  xRay;

    vTmp = CVec4(p_fClickXPos, p_fClickYPos, 1e-9f, 1.0f) * pCamera->GetViewProjectionInverseMatrix(); 
    vTmp /= vTmp(3);
	xRay.m_vBase = vTmp.GetReduced();

	vTmp = CVec4(p_fClickXPos, p_fClickYPos, 1.0f, 1.0f) * pCamera->GetViewProjectionInverseMatrix(); 
    vTmp /= vTmp(3);
	xRay.m_vDirection = (vTmp.GetReduced() - xRay.m_vBase) * 100.0f;

	return xRay;
}

//-------------------------------------------------------------------------------------------------------------------------------------------
void
CLevelEditor::Unselect()
{
	m_pxSelectedObject = 0;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
int
CLevelEditor::NumSelectedObjects() const
{
	return m_pxSelectedObject ? 1 : 0;
}
//-------------------------------------------------------------------------------------------------------------------------------------------

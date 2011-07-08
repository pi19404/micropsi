#include "Application/stdinc.h"
#include "Observers/observercontroller.h"

#include "World/World.h"
#include "Engine/Terrain/terrainsystem.h"

//---------------------------------------------------------------------------------------------------------------------
CObserverController::CObserverController()
{
	m_pxObserver	= 0;
}

//---------------------------------------------------------------------------------------------------------------------
CObserverController::~CObserverController()
{
}

//---------------------------------------------------------------------------------------------------------------------
void
CObserverController::FixHeightAboveTerrain(CVec3& po_rvPos, CWorld* p_pxWorld, float p_fExactHeight)
{
	FixHeightAboveTerrain(po_rvPos, p_pxWorld, p_fExactHeight, p_fExactHeight);
}

//---------------------------------------------------------------------------------------------------------------------
void
CObserverController::FixHeightAboveTerrain(CVec3& po_rvPos, CWorld* p_pxWorld, float p_fMinHeight, float p_fMaxHeight)
{
	CTerrainSystem* pxTerrain = p_pxWorld->GetTerrain();
	float fHeight = pxTerrain->GetTerrainHeight(po_rvPos.x(), po_rvPos.z());
	po_rvPos.y() = clamp(po_rvPos.y(), fHeight + p_fMinHeight, fHeight + p_fMaxHeight);
}

//---------------------------------------------------------------------------------------------------------------------
/**
	makes sure given x/z coordinate is within map boundaries
	takes wrap mode into account
*/
void
CObserverController::FixMapBorders(CVec3& po_rvPos, CWorld* p_pxWorld)
{
	CTerrainSystem* pxTerrain = p_pxWorld->GetTerrain();

	CVec3 vMinCorner = pxTerrain->GetOffset();
	float fHeight = pxTerrain->GetHeight();
	float fWidth  = pxTerrain->GetWidth();

	if(pxTerrain->GetTerrainWrapAround())
	{
		if(po_rvPos.x() >= vMinCorner.x() + fWidth)
		{
			po_rvPos.x() = vMinCorner.x() + fmodf(po_rvPos.x() - vMinCorner.x(), fWidth);
		}
		else if(po_rvPos.x() < vMinCorner.x())
		{
			po_rvPos.x() = vMinCorner.x() + fWidth + fmodf(po_rvPos.x() - vMinCorner.x(), fWidth);
		}

		if(po_rvPos.z() >= vMinCorner.z() + fHeight)
		{
			po_rvPos.z() = vMinCorner.z() + fmodf(po_rvPos.z() - vMinCorner.z(), fHeight);
		}
		else if(po_rvPos.z() < vMinCorner.z())
		{
			po_rvPos.z() = vMinCorner.z() + fHeight + fmodf(po_rvPos.z() - vMinCorner.z(), fHeight);
		}
	}
	else
	{
		float fBorder = p_pxWorld->GetTerrainBorder();

		po_rvPos.x() = clamp(po_rvPos.x(), vMinCorner.x() - fBorder, vMinCorner.x() + fWidth + fBorder);
		po_rvPos.z() = clamp(po_rvPos.z(), vMinCorner.z() - fBorder, vMinCorner.z() + fHeight + fBorder);
	}
}
//---------------------------------------------------------------------------------------------------------------------

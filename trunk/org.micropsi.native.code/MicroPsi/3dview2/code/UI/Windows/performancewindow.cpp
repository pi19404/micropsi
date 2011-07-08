#include "Application/stdinc.h"
#include "UI/Windows/performancewindow.h"

#include "uilib/core/windowmanager.h"
#include "uilib/controls/label.h"

#include "Engine/Terrain/terrainsystem.h"

#include "World/World.h"
#include "World/ObjectManager.h"

#include "Application/3dview2.h"

//---------------------------------------------------------------------------------------------------------------------
CPerformanceWindow::CPerformanceWindow()
{
	SetSize(230, 130);
	SetCaption("System");
	SetAlwaysOnTop(true);

	UILib::CLabel* pxPosLabel = UILib::CLabel::Create();
	pxPosLabel->SetPos(5, 5);
	pxPosLabel->SetText("Camera:");
	pxPosLabel->SetTextAlign(UILib::CLabel::TA_Left);
	AddChild(pxPosLabel->GetWHDL());
	m_pxPos = UILib::CLabel::Create();
	m_pxPos->SetPos(70, 5);
	m_pxPos->SetTextAlign(UILib::CLabel::TA_Left);
	AddChild(m_pxPos->GetWHDL());

	UILib::CLabel* pxFPSLabel = UILib::CLabel::Create();
	pxFPSLabel->SetPos(5, 20);
	pxFPSLabel->SetText("FPS:");
	pxFPSLabel->SetTextAlign(UILib::CLabel::TA_Left);
	AddChild(pxFPSLabel->GetWHDL());
	m_pxFPS = UILib::CLabel::Create();
	m_pxFPS->SetPos(70, 20);
	m_pxFPS->SetTextAlign(UILib::CLabel::TA_Left);
	AddChild(m_pxFPS->GetWHDL());
	
	UILib::CLabel* pxTerrainTilesVisibleLabel = UILib::CLabel::Create();
	pxTerrainTilesVisibleLabel->SetPos(5, 35);
	pxTerrainTilesVisibleLabel->SetText("Terrain:");
	pxTerrainTilesVisibleLabel->SetTextAlign(UILib::CLabel::TA_Left);
	AddChild(pxTerrainTilesVisibleLabel->GetWHDL());
	m_pxTerrainTileVisibleCount = UILib::CLabel::Create();
	m_pxTerrainTileVisibleCount->SetPos(70, 35);
	m_pxTerrainTileVisibleCount->SetTextAlign(UILib::CLabel::TA_Left);
	AddChild(m_pxTerrainTileVisibleCount->GetWHDL());
	
	UILib::CLabel* pxObjectVisibleCountLabel = UILib::CLabel::Create();
	pxObjectVisibleCountLabel->SetPos(5, 50);
	pxObjectVisibleCountLabel->SetText("Objects:");
	pxObjectVisibleCountLabel->SetTextAlign(UILib::CLabel::TA_Left);
	AddChild(pxObjectVisibleCountLabel->GetWHDL());
	m_pxObjectVisibleCount = UILib::CLabel::Create();
	m_pxObjectVisibleCount->SetPos(70, 50);
	m_pxObjectVisibleCount->SetTextAlign(UILib::CLabel::TA_Left);
	AddChild(m_pxObjectVisibleCount->GetWHDL());

	UILib::CLabel* pxObjectVisibleCountReflectionLabel = UILib::CLabel::Create();
	pxObjectVisibleCountReflectionLabel->SetPos(5, 65);
	pxObjectVisibleCountReflectionLabel->SetText("Water Obj:");
	pxObjectVisibleCountReflectionLabel->SetTextAlign(UILib::CLabel::TA_Left);
	AddChild(pxObjectVisibleCountReflectionLabel->GetWHDL());
	m_pxObjectVisibleCountReflection = UILib::CLabel::Create();
	m_pxObjectVisibleCountReflection->SetPos(70, 65);
	m_pxObjectVisibleCountReflection->SetTextAlign(UILib::CLabel::TA_Left);
	AddChild(m_pxObjectVisibleCountReflection->GetWHDL());
	
	UILib::CLabel* pxObjectVisibleCountShadowMapLabel = UILib::CLabel::Create();
	pxObjectVisibleCountShadowMapLabel->SetPos(5, 80);
	pxObjectVisibleCountShadowMapLabel->SetText("Shadow Obj:");
	pxObjectVisibleCountShadowMapLabel->SetTextAlign(UILib::CLabel::TA_Left);
	AddChild(pxObjectVisibleCountShadowMapLabel->GetWHDL());
	m_pxObjectVisibleCountShadowMap = UILib::CLabel::Create();
	m_pxObjectVisibleCountShadowMap->SetPos(70, 80);
	m_pxObjectVisibleCountShadowMap->SetTextAlign(UILib::CLabel::TA_Left);
	AddChild(m_pxObjectVisibleCountShadowMap->GetWHDL());
}

//---------------------------------------------------------------------------------------------------------------------
CPerformanceWindow::~CPerformanceWindow()
{
}


//---------------------------------------------------------------------------------------------------------------------
CPerformanceWindow*		
CPerformanceWindow::Create()
{
	return new CPerformanceWindow();
}

//---------------------------------------------------------------------------------------------------------------------
void
CPerformanceWindow::DeleteNow()
{
	delete this;
}
//---------------------------------------------------------------------------------------------------------------------
void
CPerformanceWindow::Update()
{
   C3DView2& xr3DView2 = *C3DView2::Get();
	CVec3 vPos = xr3DView2.GetCamera()->GetPos();
	const C3DView2::CPerformanceData& xPerfData = xr3DView2.GetPerformanceData();

	m_pxPos->SetText(CStr::Create("%.2f  %.2f  %.2f", vPos.x(), vPos.y(), vPos.z()));
	m_pxFPS->SetText(CStr::Create("%.2f", xPerfData.m_fAvgFPS));
	m_pxTerrainTileVisibleCount->SetText(CStr::Create("%d / %d", xPerfData.m_iFirstPassTerrainTiles, xPerfData.m_iSecondPassTerrainTiles));
	m_pxObjectVisibleCount->SetText(CStr::Create("%d / %d of %d", xPerfData.m_iFirstPassObjects, xPerfData.m_iSecondPassObjects, xr3DView2.GetWorld()->GetObjectManager()->GetNumberOfObjects()));
	m_pxObjectVisibleCountReflection->SetText(CStr::Create("%d", xPerfData.m_iWaterReflectionObjects));
	m_pxObjectVisibleCountShadowMap->SetText(CStr::Create("%d", xPerfData.m_iShadowMapObjects));
}
//---------------------------------------------------------------------------------------------------------------------

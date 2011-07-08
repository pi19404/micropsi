
#include "baselib/filelocator.h"

#include "e42/core/EffectShader.h"
#include "e42/core/DeviceStateMgr.h"
#include "baselib/geometry/Plane.h"
#include "e42/E42Application.h"

#include "Engine/Terrain/terrainsystem.h"

#include "Engine/Terrain/splatchunkmanager.h"
#include "Engine/Terrain/splatchunkterraintile.h"
#include "Engine/Terrain/flatterraintile.h"
#include "Engine/Terrain/flatwatertile.h"
#include "Engine/Terrain/sinuswatertile.h"

#include "GameLib/World/SimTimeCtrl.h"

#include "Application/3dview2.h"
#include "Observers/observer.h"

#include "Utilities/systemutils.h"

#include <algorithm>

using std::string;
using std::vector;

//---------------------------------------------------------------------------------------------------------------------
CTerrainSystem::CTerrainSystem(CEngineController* p_pxEngineController)
{
	m_pxEngineController = p_pxEngineController;

	m_pxTrivialChunk	= 0;
	m_pxWaterTile		= 0;
	m_pxSplatChunkManager = 0;

	m_pxQuadTree		= 0;
	m_pxVirtualGrid		= 0;
	
	m_bInTerrainDefinition = false;
	m_bReadyToRender = false;
	
	m_bFrustumCulling = true;
}

//---------------------------------------------------------------------------------------------------------------------
CTerrainSystem::~CTerrainSystem()
{
	Clear();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CTerrainSystem::Clear()
{
	delete m_pxTrivialChunk;
	m_pxTrivialChunk = 0;

	delete m_pxWaterTile;
	m_pxWaterTile = 0;

	delete m_pxQuadTree;
	m_pxQuadTree = 0;

	delete [] m_pxVirtualGrid;
	m_pxVirtualGrid = 0;

	delete m_pxSplatChunkManager;
	m_pxSplatChunkManager = 0;

	m_bReadyToRender = false;
}

//---------------------------------------------------------------------------------------------------------------------
void
CTerrainSystem::BeginTerrainDefinition()
{
	assert(m_bInTerrainDefinition == false);
	m_bInTerrainDefinition = true;

	Clear();

	m_sHeightMap			= "";			
	m_sMaterialMap			= "";			
	m_vOffset				= CVec3(0.0f, 0.0f, 0.0f);				
	m_vScale				= CVec3(1.0f, 0.1f, 1.0f);		
	m_vAbsoluteSize			= CVec3(-1.0f, -1.0f, -1.0f);
	m_fMaxRangeOfVision		= 1000;	
	m_fMapBorderWidth		= 500;		
	m_bWrapAround			= false;			
}

//---------------------------------------------------------------------------------------------------------------------
bool
CTerrainSystem::EndTerrainDefinition()
{
	assert(m_bInTerrainDefinition == true);
	if(!m_bInTerrainDefinition)
	{
		return false;
	}
	m_bInTerrainDefinition = false;

	
	if(!m_xHeightMap.LoadFromBitmap(m_sHeightMap.c_str()))
	{
		return false;
	}
	if(!m_xMaterialMap.LoadFromBitmap(m_sMaterialMap.c_str(), m_xHeightMap.GetWidth() -1, m_xHeightMap.GetHeight() -1))
	{
		return false;
	}

	// make small changes to heightmap to prevent gaps in geometry at map border
	if(m_bWrapAround)
	{
		m_xHeightMap.WrapBordersAround();
	}
	else
	{
		m_xHeightMap.SetAllBorderPointsToHeight(0);		
	}

	m_pxSplatChunkManager = new CSplatChunkManager(m_pxEngineController);
	m_pxSplatChunkManager->BeginTerrainDefinition();

	m_pxSplatChunkManager->SetHeightMap(&m_xHeightMap);
	m_pxSplatChunkManager->SetMaterialMap(&m_xMaterialMap);
	m_pxSplatChunkManager->SetOffset(m_vOffset);

	if(m_vAbsoluteSize.x() > 0.0f && m_vAbsoluteSize.y() >= 0.0f && m_vAbsoluteSize.z() > 0.0f)
	{
		float fH = (float) (m_xHeightMap.GetHeight() -1);
		float fW = (float) (m_xHeightMap.GetWidth() -1);

		m_vScale.x() = m_vAbsoluteSize.x() / fW;
		// psi does not give us a valid map height!
//		m_vScale.y() = m_vScale.x();
		m_vScale.z() = m_vAbsoluteSize.z() / fH;
	}
	m_pxSplatChunkManager->SetScale(m_vScale);
	m_pxSplatChunkManager->SetTextureTileSize(m_fTextureTileSize);

	for(int i=0; i<ms_iMaxMaterials; ++i)
	{
		if(m_asMaterials[i].size() > 0)
		{
			m_pxSplatChunkManager->SetMaterial(i, m_asMaterials[i]);
		}
	}

	if(!m_pxSplatChunkManager->EndTerrainDefinition())
	{
		return false;
	}

	GenerateTerrain();
	m_bReadyToRender = true;
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
void
CTerrainSystem::SetHeightMap(const string& p_rsHeightMap)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
	m_sHeightMap = p_rsHeightMap;
}

//---------------------------------------------------------------------------------------------------------------------
void
CTerrainSystem::SetMaterialMap(const string& p_rsMaterialMap)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
	m_sMaterialMap = p_rsMaterialMap;
}

//---------------------------------------------------------------------------------------------------------------------
void		
CTerrainSystem::SetOffset(CVec3 p_vOffset)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
	m_vOffset = p_vOffset;
}


//---------------------------------------------------------------------------------------------------------------------
void		
CTerrainSystem::SetScale(CVec3 p_vScale)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
	m_vScale = p_vScale;
}

//---------------------------------------------------------------------------------------------------------------------
void
CTerrainSystem::SetAbsoluteSize(CVec3 p_xAbsoluteSize)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
	m_vAbsoluteSize = p_xAbsoluteSize;
}

//---------------------------------------------------------------------------------------------------------------------
void
CTerrainSystem::SetTextureTileSize(float p_fTextureTileSize)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
	m_fTextureTileSize = p_fTextureTileSize;
}

//---------------------------------------------------------------------------------------------------------------------
void
CTerrainSystem::SetMaxRangeOfVision(float p_fMaxRangeOfVision)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
}

//---------------------------------------------------------------------------------------------------------------------
void
CTerrainSystem::SetMapBorderWidth(float p_fMapBorderWidth)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
	m_fMapBorderWidth = p_fMapBorderWidth;
}

//---------------------------------------------------------------------------------------------------------------------
void
CTerrainSystem::SetWrapAround(bool p_bWrapAround)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
	m_bWrapAround = p_bWrapAround;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	associates a texture with a material index
	\return		true: success, false: failed (because index is invalid)
*/
bool		
CTerrainSystem::SetMaterial(int p_iIndex, const string& p_srTextureName)
{
	if(p_iIndex < 0  ||  p_iIndex > ms_iMaxMaterials)
	{
		return false;
	}

	m_asMaterials[p_iIndex] = p_srTextureName;
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CTerrainSystem::GenerateTerrain()
{
	// create virtual grid

	float fBorder = m_fMaxRangeOfVision;
	if(!m_bWrapAround)
	{
		fBorder += m_fMapBorderWidth;
	}

	int iXChunks = 0;
	int iZChunks = 0;
 	const CTerrainTile** pxGrid;
	m_pxSplatChunkManager->GetChunks(pxGrid, iXChunks, iZChunks);
	assert(iXChunks > 0  &&  iZChunks > 0);
	float fChunkWidth = pxGrid[0]->GetLocalAABB().GetSize().x();

	int iBorderChunks = (int) ceil(fBorder / fChunkWidth);
	m_iVirtualGridHeight = iZChunks + 2*iBorderChunks; 
	m_iVirtualGridWidth  = iXChunks + 2*iBorderChunks; 

	// create Quadtree

	int iLevels = 1;
    int iSize = max(m_iVirtualGridHeight, m_iVirtualGridWidth);
	while(iSize > 1)
	{
		iSize /= 2;
		iLevels++;
	}
	iLevels++;

	assert(m_pxQuadTree == 0);
	m_pxQuadTree = new CQuadTree<CVirtualChunk*>(m_vOffset.x(), m_vOffset.z(), 
												 max(m_iVirtualGridHeight * fChunkWidth, m_iVirtualGridWidth * fChunkWidth) *2, 
												 m_vOffset.y(), m_vOffset.y() + (255.0f * m_vScale.y()),
												 iLevels);


	// create water

	m_pxWaterTile = new CFlatWaterTile(m_pxEngineController, CVec3(fChunkWidth, 1.0f, fChunkWidth), fChunkWidth / 32.0f);
//	m_pxWaterTile = new CSinusWaterTile(m_pxEngineController, 17, CVec3(fChunkWidth, 1.0f, fChunkWidth));
	m_pxWaterTile->SetWaterReflectionTexture(m_hWaterReflectionTexture);

	// fill virtual grid

	m_pxVirtualGrid = new CVirtualChunk[m_iVirtualGridHeight * m_iVirtualGridWidth];

	if(!m_bWrapAround)
	{
		assert(!m_pxTrivialChunk);
		m_pxTrivialChunk = new CFlatTerrainTile(m_pxEngineController, CVec3(fChunkWidth, 1.0f, fChunkWidth), 
			m_asMaterials[1].c_str(), (float) m_pxSplatChunkManager->GetConfig().m_iTextureRepeatitions);
	}

	for(int z=0; z<m_iVirtualGridWidth; ++z)
	{
		int iZ = (iZChunks - (iBorderChunks % iZChunks) + z) % iZChunks;
		for(int x=0; x<m_iVirtualGridWidth; ++x)
		{
			int iX = (iXChunks - (iBorderChunks % iXChunks) + x) % iXChunks;
			const CTerrainTile* pxChunk; 

			if(m_bWrapAround)
			{
				pxChunk = pxGrid[iZ*iXChunks+iX];
			}
			else
			{
				if(x >= iBorderChunks  &&  x < iBorderChunks + iXChunks  &&
					z >= iBorderChunks  &&  z < iBorderChunks + iZChunks)
				{
					pxChunk = pxGrid[(z - iBorderChunks) * iXChunks + (x - iBorderChunks)];
				}
				else
				{
					pxChunk = m_pxTrivialChunk;
				}
			}

			m_pxVirtualGrid[z*m_iVirtualGridWidth + x].m_pxChunk = pxChunk;

			CAxisAlignedBoundingBox xAABB = pxChunk->GetLocalAABB().GetTranslated(
				CVec3( (x-iBorderChunks) * fChunkWidth, 0.0f, (z-iBorderChunks) * fChunkWidth) + m_vOffset);
			m_pxVirtualGrid[z*m_iVirtualGridWidth + x].m_xTerrainAABB = xAABB;

			xAABB.m_vMin.y() = m_pxWaterTile->GetLocalAABB().m_vMin.y();
			xAABB.m_vMax.y() = m_pxWaterTile->GetLocalAABB().m_vMax.y();

			m_pxVirtualGrid[z*m_iVirtualGridWidth + x].m_xWaterAABB = xAABB;

			xAABB.m_vMin.y() = min(xAABB.m_vMin.y(), m_pxWaterTile->GetLocalAABB().m_vMin.y());
			xAABB.m_vMax.y() = max(xAABB.m_vMax.y(), m_pxWaterTile->GetLocalAABB().m_vMax.y());

			m_pxVirtualGrid[z*m_iVirtualGridWidth + x].m_xAABB = xAABB;
			m_pxQuadTree->AddItem(&m_pxVirtualGrid[z*m_iVirtualGridWidth + x], xAABB);
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void	
CTerrainSystem::UpdateVisibility(const CViewFrustum& p_rxFrustum)
{
	if(p_rxFrustum == m_xVisibleFrustum)
	{
		// no update necessary
		return;
	}

	m_xVisibleFrustum = p_rxFrustum;
	m_apxVisibleChunks.clear();

	if(!m_bFrustumCulling)
	{
		// naiv: alle Chunks zeichnen
		m_iChunksVisible = 0;
		for(int x=0; x<m_iVirtualGridWidth; ++x)
		{
			for(int z=0; z<m_iVirtualGridHeight; ++z)
			{
				m_apxVisibleChunks.push_back(&m_pxVirtualGrid[z*m_iVirtualGridWidth+x]);
				m_iChunksVisible++;
			}
		}
	} 
	else
	{
		assert(m_pxQuadTree);

		// collect all visible chunks using the quadtree
		m_iChunksVisible = 0;
		m_pxQuadTree->CollectVisibleItems(p_rxFrustum, m_apxVisibleChunks);
	}

//	DebugPrint("chunks in view %d", apxChunks.size());
}

//---------------------------------------------------------------------------------------------------------------------
void 
CTerrainSystem::RenderTerrain(TRenderContextPtr spxRenderContext)
{
	if(!m_bReadyToRender)
	{
		return;
	}

    CMat4S matWorldTransform;
    matWorldTransform.SetIdentity();        // FIXME / DMA

	CMat4S matViewProj;
	matViewProj = spxRenderContext->m_matViewTransform * spxRenderContext->m_matProjectionTransform;


	m_pxSplatChunkManager->GetTerrainShader()->SetWorldMatrix(matWorldTransform);
    m_pxSplatChunkManager->GetTerrainShader()->SetLightDirVector(spxRenderContext->m_vLightDir.GetExtended(0));

	const CViewFrustum& rxFrustum = C3DView2::Get()->GetCamera()->GetViewFrustum();
	UpdateVisibility(rxFrustum);

	// render all visible terrain chunks
	for(unsigned int i=0; i<m_apxVisibleChunks.size(); i++)
	{
		// check against correct bounding box before rendering
		if(rxFrustum.CheckAABBIntersection(m_apxVisibleChunks[i]->m_xTerrainAABB) != CViewFrustum::Intersection::Outside)
		{
			RenderChunk(m_apxVisibleChunks[i], spxRenderContext, matViewProj, matWorldTransform);
			m_iChunksVisible++;
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CTerrainSystem::RenderWater(TRenderContextPtr spxRenderContext)
{
    if(!m_bReadyToRender)
	{
		return;
	}

    CMat4S matWorldTransform;
    matWorldTransform.SetIdentity();        // FIXME / DMA

	m_pxWaterTile->Tick(C3DView2::Get()->GetSimTimeCtrl()->GetContinuousSimTime());

	CMat4S matViewProj;
	matViewProj = spxRenderContext->m_matViewTransform * spxRenderContext->m_matProjectionTransform;


	m_pxSplatChunkManager->GetTerrainShader()->SetWorldMatrix(matWorldTransform);
    m_pxSplatChunkManager->GetTerrainShader()->SetLightDirVector(spxRenderContext->m_vLightDir.GetExtended(0));

	const CViewFrustum& rxFrustum = C3DView2::Get()->GetCamera()->GetViewFrustum();
	UpdateVisibility(rxFrustum);

	// render all visible water tiles
	for(unsigned int i=0; i<m_apxVisibleChunks.size(); i++)
	{
		// check against correct bounding box before rendering
		if(rxFrustum.CheckAABBIntersection(m_apxVisibleChunks[i]->m_xWaterAABB) != CViewFrustum::Intersection::Outside)
		{
			// check if water is completely covered by land
			if(m_apxVisibleChunks[i]->m_xWaterAABB.m_vMax.y() >= m_apxVisibleChunks[i]->m_xTerrainAABB.m_vMin.y())
			{
				RenderWaterChunk(m_apxVisibleChunks[i], spxRenderContext, matViewProj, matWorldTransform);
			}
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CTerrainSystem::RenderChunk(CVirtualChunk* p_pxChunk, TRenderContextPtr spxRenderContext, const CMat4S& p_matViewProj, const CMat4S& p_matWorld)
{
	CVec3 vEye = C3DView2::Get()->GetCamera()->GetPos();
	float fDist = (p_pxChunk->m_xAABB.GetCenter() - vEye).Abs();

	CMat4S matWorld = p_matWorld;
    matWorld.SetTranslation(CVec3(p_pxChunk->m_xAABB.m_vMin.x(), m_vOffset.y(), p_pxChunk->m_xAABB.m_vMin.z()) ^ p_matWorld);

	p_pxChunk->m_pxChunk->Render(spxRenderContext, &p_matViewProj, &matWorld, fDist);
}

//---------------------------------------------------------------------------------------------------------------------
void
CTerrainSystem::RenderWaterChunk(CVirtualChunk* p_pxChunk, TRenderContextPtr spxRenderContext, const CMat4S& p_matViewProj, const CMat4S& p_matWorld)
{
	CMat4S matWorld = p_matWorld;
	matWorld.SetTranslation(CVec3(p_pxChunk->m_xAABB.m_vMin.x(), 0, p_pxChunk->m_xAABB.m_vMin.z()) ^ p_matWorld);

//	DebugPrint("chunk pos = %.2f %.2f %.2f", p_pxChunk->m_xAABB.m_vMin.x(), m_vOffset.y(), p_pxChunk->m_xAABB.m_vMin.z());

	CMat4S matWorldViewProj = matWorld * p_matViewProj;
	m_pxWaterTile->Render(spxRenderContext, &matWorldViewProj, &matWorld);

}

//---------------------------------------------------------------------------------------------------------------------
bool		
CTerrainSystem::HitTest(const CRay& p_rxRay, CVec3& po_rxCollisionPoint)
{
	// collect all visible chunks using the quadtree
	CViewFrustum xFrustum;
    xFrustum.Update(
		C3DView2::Get()->GetCamera()->GetViewProjectionInverseMatrix(), 
		0.0f, 1000.0f, 
		C3DView2::Get()->GetCamera()->GetPerspective(),
		C3DView2::Get()->GetCamera()->GetLeftHandedWorldCoordinateSystem());

	vector<CVirtualChunk*> apxChunks;
	m_pxQuadTree->CollectVisibleItems(xFrustum, apxChunks);

	vector<CDistanceSortedChunk> axSortedChunks;
	for(unsigned int i=0; i<apxChunks.size(); i++)
	{
		if(xFrustum.CheckAABBIntersection(apxChunks[i]->m_xAABB) != CViewFrustum::Intersection::Outside)
		{
			axSortedChunks.push_back(	CDistanceSortedChunk(apxChunks[i], 
										(apxChunks[i]->m_xAABB.GetCenter() - p_rxRay.m_vBase).AbsSquare()));
		}
	}

	std::sort(axSortedChunks.begin(), axSortedChunks.end());
//	DebugPrint("chunks in view: %d", axSortedChunks.size());

	for(int i=0; i<(int) axSortedChunks.size(); ++i)
	{
		CAxisAlignedBoundingBox& rxAABB = axSortedChunks[i].m_pxVirualChunk->m_xAABB;

		CVec3 vWorldPos = CVec3(rxAABB.m_vMin.x(), m_vOffset.y(), rxAABB.m_vMin.z());
			
		if(axSortedChunks[i].m_pxVirualChunk->m_pxChunk->HitTest(vWorldPos, p_rxRay, po_rxCollisionPoint))
		{
			return true;
		}
	}

	return false;
}
//---------------------------------------------------------------------------------------------------------------------
float
CTerrainSystem::GetTerrainHeight(float p_fX, float p_fZ)
{
	return m_pxSplatChunkManager->GetTerrainHeight(p_fX, p_fZ);		// TODO: warum?
}
//---------------------------------------------------------------------------------------------------------------------
void
CTerrainSystem::SetWaterReflectionTexture(TTextureHandle p_hWaterReflectionTexture)
{
	m_hWaterReflectionTexture = p_hWaterReflectionTexture;
	if(m_pxWaterTile)
	{
		m_pxWaterTile->SetWaterReflectionTexture(p_hWaterReflectionTexture);
	}
}
//---------------------------------------------------------------------------------------------------------------------

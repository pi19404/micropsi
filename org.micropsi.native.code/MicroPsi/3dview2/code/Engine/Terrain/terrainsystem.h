#pragma once
#ifndef TERRAINSYSTEM_H_INCLUDED
#define TERRAINSYSTEM_H_INCLUDED

#include <vector>

#include "baselib/geometry/CVector.h"
#include "e42/core/RenderContext.h"
#include "e42/core/TextureFactory.h"

#include "Engine/Terrain/heightmap.h"
#include "Engine/Terrain/materialmap.h"

#include "Engine/core/quadtree.h"

class CSplatChunkManager;
class CTerrainTile;
class CWaterTile;

class CTerrainSystem
{
public: 

	CTerrainSystem(CEngineController* p_pxEngineController);
	~CTerrainSystem();

	void					Clear();

	///< functions defining terrain parameters can only be called between BeginTerrainDefinition() and EndTerrainDefinition()
	void					BeginTerrainDefinition();

	void					SetHeightMap(const std::string& p_rsHeightMap);
	void					SetMaterialMap(const std::string& p_rsMaterialMap);

	void					SetOffset(CVec3 p_xOffset);
	CVec3					GetOffset() const;

	void					SetScale(CVec3 p_xScale);
	CVec3					GetScale() const;

	void					SetAbsoluteSize(CVec3 p_xAbsoluteSize);

	void					SetTextureTileSize(float p_fTextureTileSize);

	void					SetMaxRangeOfVision(float p_fMaxRangeOfVision);

	void					SetMapBorderWidth(float p_fMapBorderWidth);

	void					SetWrapAround(bool p_bWrapAround);
	bool					GetTerrainWrapAround() const;

	bool					SetMaterial(int p_iIndex, const std::string& p_srTextureName);

	///< functions defining terrain parameters can only be called between BeginTerrainDefinition() and EndTerrainDefinition()
	bool					EndTerrainDefinition();


	void					RenderTerrain(TRenderContextPtr spxRenderContext);
	void					RenderWater(TRenderContextPtr spxRenderContext);
	void					SetWaterReflectionTexture(TTextureHandle p_hWaterReflectionTexture);

	int						GetNumberOfCurrentlyVisibleChunks() const;

	float					GetWidth() const;
	float					GetHeight() const;

	bool					HitTest(const CRay& p_rxRay, CVec3& po_rxCollisionPoint);
	float					GetTerrainHeight(float p_fX, float p_fZ);

private:

	class CVirtualChunk
	{
	public:
		const CTerrainTile*			m_pxChunk;
		CAxisAlignedBoundingBox		m_xAABB;				///< AABB for both Terrain and Water
		CAxisAlignedBoundingBox		m_xTerrainAABB;			///< Terrain AABB
		CAxisAlignedBoundingBox		m_xWaterAABB;			///< Water AABB
	};

	///< chunks pointer that can be sorted by distance to a point; used for hit test with ray
	class CDistanceSortedChunk
	{
	public:
		CDistanceSortedChunk(CVirtualChunk* p_pxChunk, float p_fDist) : m_pxVirualChunk(p_pxChunk), m_fDistance(p_fDist) {}
		CVirtualChunk*			m_pxVirualChunk;
		float					m_fDistance;

		bool operator<(const CDistanceSortedChunk& p_xrOtherBox)		{ return m_fDistance < p_xrOtherBox.m_fDistance; }
	};

	void	UpdateVisibility(const CViewFrustum& p_rxFrustum);
	void	GenerateTerrain();
	void	RenderChunk(CVirtualChunk* p_pxChunk, TRenderContextPtr spxRenderContext, const CMat4S& p_matViewProj, const CMat4S& p_matWorld);
	void	RenderWaterChunk(CVirtualChunk* p_pxChunk, TRenderContextPtr spxRenderContext, const CMat4S& p_matViewProj, const CMat4S& p_matWorld);

	bool						m_bInTerrainDefinition;				///< true if terrain definition is in progress
	bool						m_bReadyToRender;					///< true if terrain is ready to be rendered
	
	std::string					m_sHeightMap;						///< height map file name
	std::string					m_sMaterialMap;						///< material map file name
	CVec3						m_vOffset;							///< coordinates of heightmap origin in 3d space
	CVec3						m_vScale;							///< scaling from heightmap to 3d space
	CVec3						m_vAbsoluteSize;					///< absolute size of heightmap in world space; overrides scaling if not negative
	float						m_fTextureTileSize;					///< size of terrain texture (meters)
	float						m_fMaxRangeOfVision;				///< maximum range of vision in meters
	float						m_fMapBorderWidth;					///< border around the map in meters (ignored if wrap-around)
	bool						m_bWrapAround;						///< true: map wraps around
	bool						m_bFrustumCulling;					///< frustum culling on or off

	static const int			ms_iMaxMaterials = 256;
	std::string					m_asMaterials[ms_iMaxMaterials];	///< array with materials

	CEngineController*			m_pxEngineController;				///< access to Engine

	CVirtualChunk*				m_pxVirtualGrid;					///< grid of virtual chunks (references to real chunks)
	int							m_iVirtualGridHeight;				///< height of virtual grid in chunks
	int							m_iVirtualGridWidth;				///< width of virtual grid in chunks

	int							m_iChunksVisible;					///< number of chunks drawn in last frame - for performance testing
	std::vector<CVirtualChunk*>	m_apxVisibleChunks;					///< vector with visible chunks
	CViewFrustum				m_xVisibleFrustum;					///< last frustum used to update visibility

	CSplatChunkManager*			m_pxSplatChunkManager;				///< Splat Chunk Manager
	CTerrainTile*				m_pxTrivialChunk;					///< the trivial chunk, if needed
	CWaterTile*					m_pxWaterTile;						///< water tile

	CQuadTree<CVirtualChunk*>*	m_pxQuadTree;						///< quadtree with all chunks

	CHeightMap					m_xHeightMap;						///< height map
	CMaterialMap				m_xMaterialMap;						///< material map
	TTextureHandle				m_hWaterReflectionTexture;
};

#include "terrainsystem.inl"

#endif // ifndef TERRAINSYSTEM_H_INCLUDED


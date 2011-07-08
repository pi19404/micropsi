#pragma once
#ifndef SPLATCHUNKMANAGER_H_INCLUDED
#define SPLATCHUNKMANAGER_H_INCLUDED

#include <vector>

#include "baselib/geometry/CVector.h"
#include "e42/core/TextureFactory.h"
#include "e42/core/EffectFactory.h"
#include "e42/core/RenderContext.h"

#include "Engine/Terrain/heightmap.h"
#include "Engine/Terrain/terrainmaterial.h"

class CTerrainTile;
class CSplatChunkTerrainTile;
class CMaterialMap;

class CSplatChunkManager
{
public: 

	CSplatChunkManager(CEngineController* p_pxEngineController);
	~CSplatChunkManager();

	static CSplatChunkManager* GetInstance();

	struct CConfig
	{
		bool			m_bForceRGBABlendMaps;				///< true: force blend maps to be in RGBA Format - this is only for debug purposes, default is false
		bool			m_b4BitBlendMapsAcceptable;			///< true: 4 bit for blendmap alpha are acceptable
		bool			m_DrawBlendMapBorders;				///< true: draw red borders around Blend Maps (rules out alpha-only texture :)	
		D3DFORMAT		m_eBlendTextureFormat;				///< format for blend textures
		bool			m_bBasePassOnly;					///< true: apply base texture only, no splatting
		bool			m_bShadows;							///< true: use shadow map to render shadows
		int				m_iMaterialOfBlendMapToRender;		///< material id of the blendmap you want to render (for debug purposes)
		float			m_fTextureLODFadeStart;				///< distance where splats start to fade out
		float			m_fTextureLODFadeEnd;				///< distance where splats are gone
		int				m_iLowResImageWidth;				///< width of the low res image in pixels
		int				m_iChunkWidth;						///< width of a chunk in map coordinates
		int				m_iTextureRepeatitions;				///< texture repeatitions per chunk
		int				m_iGeometryLODLevels;				///< number of geometry lod levels
		bool			m_bRenderLowestLODOnly;				///< render only lowest geometry lod level
		bool			m_bRenderSkirts;					///< render terrain tile skirts
		bool			m_bDoubleSided;						///< render terrain with back face culling disabled
	};


	void					Clear();

	///< functions defining terrain parameters can only be called between BeginTerrainDefinition() and EndTerrainDefinition()
	void					BeginTerrainDefinition();

	void					SetHeightMap(const CHeightMap* p_pxHeightMap);
	void					SetMaterialMap(const CMaterialMap* p_pxMaterialMap);

	void					SetOffset(CVec3 p_xOffset);
	CVec3					GetOffset() const;

	void					SetScale(CVec3 p_xScale);
	CVec3					GetScale() const;

	void					SetTextureTileSize(float p_fTextureTileSize);

	bool					SetMaterial(int p_iIndex, const std::string& p_srTextureName);
	const CTerrainMaterial*	GetMaterial(int p_iIndex) const;
	const CMaterialMap*		GetMaterialMap() const;

	///< functions defining terrain parameters can only be called between BeginTerrainDefinition() and EndTerrainDefinition()
	bool					EndTerrainDefinition();

	int						GetMapXSize() const;
	int						GetMapZSize() const;

	CVec3					GetMapVertex(int p_iX, int p_iZ) const;
	CVec3					GetMapVertexLocal(int p_iLocalX, int p_iLocalZ, int p_iGlobalX, int p_iGlobalZ) const;

	float					GetTerrainHeight(float p_fX, float p_fZ);

	int						GetNumberOfChunks() const;

	const CConfig&			GetConfig() const;
	CEngineController*		GetEngineController() const;
	
	void					SetRenderBasePassOnly(bool p_bBasePassOnly);
	void					SetRenderBlendMaps(int p_iMaterialIndex);
	void					SetRenderLowestGeometryLODOnly(bool p_bLowestLOD);
	void					SetRenderShadows(bool p_bShadows);
	void					SetRenderSkirts(bool p_bSkirts);
	void					SetDoubleSidedRendering(bool p_bDoubleSided);

	TEffectHandle			GetTerrainShader() const;
	TIndexBufferHandle		GetSharedIndexBuffer() const;

	bool					GetChunks(const CTerrainTile**& po_rpxGrid, int& po_riGrindWidth, int& po_riGridHeigth) const;

	/// contains SharedIndexBuffer Info for different LOD Levels
	class CIndexBufferLodLevel
	{
	public:
		int		m_iStartIndex;
		int		m_iNumPrimitivesWithOutSkirt;
		int		m_iNumPrimitivesWithSkirt;
	};

	const CIndexBufferLodLevel*	GetLodLevelInfo() const	{ return m_pxSharedIndexBufferLodLevels; }

private:

	void					DetermineBlendMapTextureFormat();
	void					GenerateChunks();
	void					CreateSharedIndexBuffer();
	void					AddQuad(unsigned short* p_piIndexBuffer, int& po_iIndex, int p_iA, int p_iB, int p_iC, int p_iD);

	static CSplatChunkManager*	ms_pxInstance;						///< one and only instance

	bool						m_bInTerrainDefinition;				///< true if terrain definition is in progress
	bool						m_bReadyToRender;					///< true if terrain is ready to be rendered

	CEngineController*			m_pxEngineController;				///< access to Engine
	TEffectHandle				m_hTerrainShader;					///< FX File for Terrain
	TIndexBufferHandle			m_hSharedIndexBuffer;				///< shared index buffer for all chunks
	CIndexBufferLodLevel*		m_pxSharedIndexBufferLodLevels;		///< lod info 

	CConfig						m_xConfig;							///< configuation

	int							m_iMapXSize;						///< x-size in quads
	int							m_iMapZSize;						///< z-size in quads
	
	CVec3						m_vScale;							///< scaling from heightmap to 3d space
	CVec3						m_vOffset;							///< coordinates of heightmap origin in 3d space
	float						m_fTextureTileSize;					///< texture tile size in meters;

	static const int			ms_iMaxMaterials = 256;
	CTerrainMaterial			m_axMaterials[ms_iMaxMaterials];	///< array with materials
	CTerrainMaterial			m_xDefaultMaterial;					///< default material if no other is defined
								
	CSplatChunkTerrainTile**	m_ppxChunks;						///< array with all chunks
	int							m_iXChunks;							///< x-size in splatchunks (width of array m_ppxChunks)
	int							m_iZChunks;							///< z-size in splatchunks (height of array m_ppxChunks)
	int							m_iTotalChunks;						///< total number of chunks (excluding trivial chunk)

	const CHeightMap*			m_pxHeightMap;						///< height map
	const CMaterialMap*			m_pxMaterialMap;					///< material map
};

#include "splatchunkmanager.inl"

#endif // ifndef SPLATCHUNKMANAGER_H_INCLUDED


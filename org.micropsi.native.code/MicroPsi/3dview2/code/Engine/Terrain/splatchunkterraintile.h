
#pragma once
#ifndef SPLATCHUNKTERRAINTILE_H_INCLUDED
#define SPLATCHUNKTERRAINTILE_H_INCLUDED

#include <vector>

#include "e42/core/TextureFactory.h"
#include "e42/core/IndexBufferFactory.h"
#include "e42/core/VertexBufferFactory.h"
#include "e42/core/VertexDeclarationFactory.h"
#include "terraintile.h"

class CSplatChunkManager;

class CSplatChunkTerrainTile : public CTerrainTile
{
public:
	CSplatChunkTerrainTile(CSplatChunkManager* p_pxChunkMgr, int p_iMapX, int p_iMapZ, int iMapWidth);
	virtual ~CSplatChunkTerrainTile();

	virtual void					Render(TRenderContextPtr spxRenderContext, const CMat4S* p_pmatViewProj, const CMat4S* p_pmatWorld, float p_fDistanceFromViewer) const;
	virtual bool					HitTest(const CVec3& p_rvWorldPos, const CRay& p_rxRay, CVec3& po_rxCollisionPoint) const;

private:

	class CSplat
	{
	public:
		CSplat(int p_iIndex) : m_iMaterialIndex(p_iIndex), m_iCount(0) {}

		int				m_iMaterialIndex;	///< material index
		int				m_iCount;			///< number of encounters
		int				m_iFirstIndex;		///< first index in index buffer
		int				m_iNumIndices;		///< number of indices in index buffer
		int				m_iNumPrimitives;	///< number of primitives in index buffer
		TTextureHandle	m_hBlendTexture;	///< texture used for smooth blending of materials

		operator<(const CSplat& p_xrMat)	
		{
			return m_iMaterialIndex < p_xrMat.m_iMaterialIndex;
		}
	};

	float	Weight(float p_fX1, float p_fY1, float p_fX2, float p_fY2);
	float*	CreateBlendWeights(int p_iMultiplier);
	CVec3*	CalculateTriangleNormals();
	void	DoDrawing(CEffectShader* p_pxShader, int p_iStartIndex, int p_iPrimitiveCount) const;

	void	FindMaterials();
	void	CreateVertexBuffer();
	void	CreateIndexBuffer();
	void	CreateBlendTextures();
	void	CreateBaseTexture();

	CSplatChunkManager*			m_pxChunkMgr;
	
	TVertexBufferHandle			m_hVertexBuffer;		///< vertex buffer
	TIndexBufferHandle			m_hIndexBuffer;			///< index buffer 
	TVertexDeclarationHandle	m_hVertexDeclaration;	///< vertex declaration 
	TTextureHandle				m_hBaseTexture;			///< base texture
	std::vector<CSplat>			m_axSplats;				///< Array with Materials in this chunk

	int							m_iNumVertices;			///< total number of vertices
};

#include "splatchunkterraintile.inl"

#endif // ifndef SPLATCHUNKTERRAINTILE_H_INCLUDED


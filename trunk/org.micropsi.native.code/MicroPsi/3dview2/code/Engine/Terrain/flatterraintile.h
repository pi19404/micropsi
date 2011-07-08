
#pragma once
#ifndef FLATTERRAINTILE_H_INCLUDED
#define FLATTERRAINTILE_H_INCLUDED

#include "e42/core/TextureFactory.h"
#include "e42/core/IndexBufferFactory.h"
#include "e42/core/VertexBufferFactory.h"
#include "e42/core/VertexDeclarationFactory.h"
#include "e42/core/EffectFactory.h"
#include "terraintile.h"

class CFlatTerrainTile : public CTerrainTile
{
public:

	CFlatTerrainTile(CEngineController* p_pxEngineController, CVec3 p_vWorldSpaceScaling, const char* p_pcTextureName, float p_fTextureRepeatitions);
	virtual ~CFlatTerrainTile();

	void							Render(TRenderContextPtr spxRenderContext, const CMat4S* p_pmatViewProj, const CMat4S* p_pmatWorld, float p_fDistanceFromViewer) const;
	bool							HitTest(const CVec3& p_rvWorldPos, const CRay& p_rxRay, CVec3& po_rxCollisionPoint) const;

private:

	void	CreateTrivialChunk(int p_iMaterialIndex);

	TVertexBufferHandle			m_hVertexBuffer;		///< vertex buffer
	TIndexBufferHandle			m_hIndexBuffer;			///< index buffer 
	TVertexDeclarationHandle	m_hVertexDeclaration;	///< vertex declaration 
	TTextureHandle				m_hTexture;				///< Texture
	TEffectHandle				m_hShader;				///< FX File

	int							m_iNumVertices;			///< total number of vertices
	int							m_iNumPrimitives;		///< total number of primitives (triangles)
	int							m_iNumIndices;			///< total number of indices in index buffer

	CEngineController*			m_pxEngineController;	///< Engine Controller
	CVec3						m_vWorldSpaceScaling;	///< width in world space units; remember this tile is a square
	float						m_fTextureRepeatitions;	///< number of texture repeatitions per chunk
};

#include "flatterraintile.inl"

#endif // ifndef FLATTERRAINTILE_H_INCLUDED


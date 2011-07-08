
#pragma once
#ifndef SINUSWATERTILE_H_INCLUDED
#define SINUSWATERTILE_H_INCLUDED

#include "e42/core/TextureFactory.h"
#include "e42/core/IndexBufferFactory.h"
#include "e42/core/VertexBufferFactory.h"
#include "e42/core/VertexDeclarationFactory.h"
#include "e42/core/EffectFactory.h"

#include "watertile.h"

class CSinusWaterTile : public CWaterTile
{
public:

	CSinusWaterTile(CEngineController* p_pxEngineController, int p_iWidthInVertices, CVec3 p_vWorldSpaceScaling);
	~CSinusWaterTile();

	virtual void				Tick(double p_dTimeInSeconds);
	virtual void				Render(TRenderContextPtr spxRenderContext, const CMat4S* p_pmatWorldViewProj, const CMat4S* p_pmatWorld);
	
private:

	void			CreateIndexBuffer();
	void			CreateVertexBuffer();
	void			FillVertexBuffer();

	void			CreateSinusTable();

	float*						m_pxSinusTable;
	int							m_iCycleDuration;

	TVertexBufferHandle			m_hVertexBuffer;		///< vertex buffer
	TIndexBufferHandle			m_hIndexBuffer;			///< index buffer 
	TVertexDeclarationHandle	m_hVertexDeclaration;	///< vertex declaration 
	TTextureHandle				m_hWaterTexture;		///< Texture
	TEffectHandle				m_hWaterShader;			///< FX File


	int							m_iNumVertices;			///< total number of vertices
	int							m_iVertexBufferSize;	///< size of vertex buffer; > number of vertices, because we use sliding window
	int							m_iVBWritePos;			///< offset of sliding window into the vertex buffer
	int							m_iVBReadPos;			///< offset of old sliding into vertex buffer
	int							m_iNumPrimitives;		///< total number of primitives (triangles)
	int							m_iNumIndices;			///< total number of indices in index buffer

	int							m_iWidthInVertices;		///< width in vertices; this number squared is the total number of vertices
	CVec3						m_vWorldSpaceScaling;	///< width in world space units; remember this tile is a square
	float						m_fTextureRepeatitions;	///< number of texture repeatitions per chunk
};

#include "sinuswatertile.inl"

#endif // ifndef SINUSWATERTILE_H_INCLUDED



#pragma once
#ifndef FLATWATERTILE_H_INCLUDED
#define FLATWATERTILE_H_INCLUDED


#include "e42/core/TextureFactory.h"
#include "e42/core/IndexBufferFactory.h"
#include "e42/core/VertexBufferFactory.h"
#include "e42/core/VertexDeclarationFactory.h"
#include "e42/core/EffectFactory.h"

#include "watertile.h"

class CFlatWaterTile : public CWaterTile
{
public:

	CFlatWaterTile(CEngineController* p_pxEngineController, CVec3 p_vWorldSpaceScaling, float p_fTextureRepeatitions);
	~CFlatWaterTile();

	virtual void				Tick(double p_dTimeInSeconds);
	virtual void				Render(TRenderContextPtr spxRenderContext, const CMat4S* p_pmatWorldViewProj, const CMat4S* p_pmatWorld);
	
private:

	TVertexBufferHandle			m_hVertexBuffer;		///< vertex buffer
	TIndexBufferHandle			m_hIndexBuffer;			///< index buffer 
	TVertexDeclarationHandle	m_hVertexDeclaration;	///< vertex declaration 
	TTextureHandle				m_hWaterDiffusemap;		///< Texture
	TTextureHandle				m_hWaterBumpMap;		///< Texture
	TTextureHandle				m_hWaterBumpMap2;		///< Texture
	TEffectHandle				m_hWaterShader;			///< FX File

	int							m_iNumVertices;			///< total number of vertices
	int							m_iNumPrimitives;		///< total number of primitives (triangles)
	int							m_iNumIndices;			///< total number of indices in index buffer

	CVec3						m_vWorldSpaceScaling;	///< width in world space units; remember this tile is a square
	float						m_fTextureRepeatitions;	///< number of texture repeatitions per chunk

	float						m_fCurrentTime;			///< current simulation time
};

#include "flatwatertile.inl"

#endif // ifndef FLATWATERTILE_H_INCLUDED


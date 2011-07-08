
#pragma once
#ifndef WATERTILE_H_INCLUDED
#define WATERTILE_H_INCLUDED

#include "baselib/geometry/BoundingBox.h"
#include "e42/core/EngineController.h"
#include "e42/core/TextureFactory.h"
#include "e42/core/RenderContext.h"

class CWaterTile
{
public:

	CWaterTile(CEngineController* p_pxEngineController);
	virtual ~CWaterTile();

	virtual void					Tick(double p_dTimeInSeconds) = 0;
	virtual void					Render(TRenderContextPtr spxRenderContext, const CMat4S* p_pmatWorldViewProj, const CMat4S* p_pmatWorld) = 0;
	const CAxisAlignedBoundingBox&	GetLocalAABB() const;
	void							SetWaterReflectionTexture(TTextureHandle p_hWaterReflectionTexture);

protected:

	CAxisAlignedBoundingBox		m_xAABB;						///< Bounding Box
	CEngineController*			m_pxEngineController;			///< Engine Controller
	TTextureHandle				m_hWaterReflectionMap;
};

#include "watertile.inl"

#endif // ifndef WATERTILE_H_INCLUDED


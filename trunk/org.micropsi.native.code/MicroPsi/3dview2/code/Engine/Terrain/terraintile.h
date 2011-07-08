
#pragma once
#ifndef TERRAINTILE_H_INCLUDED
#define TERRAINTILE_H_INCLUDED

#include "baselib/geometry/BoundingBox.h"
#include "e42/core/RenderContext.h"

class CTerrainTile
{
public:

	CTerrainTile();
	virtual ~CTerrainTile();

	virtual void					Render(TRenderContextPtr spxRenderContext, const CMat4S* p_pmatViewProj, const CMat4S* p_pmatWorld, float p_fDistanceFromViewer) const = 0;
	virtual bool					HitTest(const CVec3& p_rvWorldPos, const CRay& p_rxRay, CVec3& po_rxCollisionPoint) const = 0;

	const CAxisAlignedBoundingBox&	GetLocalAABB() const;
	int								GetMapXPos() const;
	int								GetMapZPos() const;
	int								GetMapWidht() const;
	

protected:

	int							m_iMapXPos;				///< x-Position of this chunk in the map (in map coordinates)
	int							m_iMapZPos;				///< y-Position of this chunk in the map (in map coordinates)
	int							m_iMapWidth;			///< width in map coordinates
	
	CAxisAlignedBoundingBox		m_xAABB;				///< Bounding Box
};

#include "terraintile.inl"

#endif // ifndef TERRAINTILE_H_INCLUDED


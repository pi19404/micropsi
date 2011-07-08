#ifndef QUADTREE_H_INCLUDED
#define QUADTREE_H_INCLUDED

#include "baselib/geometry/CVector.h"
#include "baselib/geometry/BoundingBox.h"
#include "baselib/geometry/Ray.h"
#include "e42/core/ViewFrustum.h"

#include <vector>

template<typename UserDataType>
class CQuadTree
{
public:

	enum ChildNode
	{
		NW		= 0,
		NE		= 1,
		SE		= 2,
		SW		= 3
	};

	CQuadTree<UserDataType>(float p_fCenterX, float p_fCenterZ, float p_fXZWidth, float p_fYMin, float p_fYMax, int p_iLevels);
	virtual ~CQuadTree<UserDataType>();

	void							AddItem(UserDataType p_xUserData, const CAxisAlignedBoundingBox& p_xrAABB);
	void							AddItem(UserDataType p_xUserData, const CBoundingSphere& p_xrBoundingSphere);

	void							EraseItem(UserDataType p_xUserData, const CAxisAlignedBoundingBox& p_xrAABB);
	void							EraseItem(UserDataType p_xUserData, const CBoundingSphere& p_xrBoundingSphere);

	void							MoveItem(UserDataType p_xUserData, const CBoundingSphere& p_xrOldSphere, const CBoundingSphere& p_xrNewSphere);

	CQuadTree<UserDataType>&		FindNode(CVec3 p_vPos);
	CQuadTree<UserDataType>&		FindNode(const CAxisAlignedBoundingBox& p_xrAABB);
	CQuadTree<UserDataType>&		FindNode(const CBoundingSphere& p_xrBoundingSphere);

	void							CollectVisibleItems(const CViewFrustum& p_xrFrustum, std::vector<UserDataType>& po_raxItems) const;
	void							CollectIntersectingItems(const CRay& p_xrRay, std::vector<UserDataType>& po_raxItems) const;
	void							CollectItems(std::vector<UserDataType>& po_raxItems) const;

	std::vector<UserDataType>&		GetItems();

	const CAxisAlignedBoundingBox&	GetAABB() const;
	CQuadTree<UserDataType>*		GetChild(ChildNode p_eChildNode) const;

private:

	void							EraseItem(UserDataType p_xUserData);

	CAxisAlignedBoundingBox			m_xAABB;
	int								m_iLastDecisivePlane;		///< for Plane Coherance Test: Plane, die zuletzt zum Ausschluß führte

	CQuadTree*						m_pParent;
	CQuadTree*						m_apSubs[4];

	std::vector<UserDataType>		m_axItems;
};

#include "quadtree.inl"

#endif // QUADTREE_H_INCLUDED


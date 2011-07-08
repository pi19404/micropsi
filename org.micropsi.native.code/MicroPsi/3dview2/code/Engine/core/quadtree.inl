//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
CQuadTree<UserDataType>::CQuadTree<UserDataType>(float p_fCenterX, float p_fCenterZ, float p_fXZWidth, float p_fYMin, float p_fYMax, int p_iLevels)
{
	m_iLastDecisivePlane = 0;
	m_pParent = 0;

	float fHalfWidth    = p_fXZWidth / 2.0f;
	float fQuarterWidth = p_fXZWidth / 4.0f;

	m_xAABB.m_vMin = CVec3(p_fCenterX - fHalfWidth, p_fYMin, p_fCenterZ - fHalfWidth);
	m_xAABB.m_vMax = CVec3(p_fCenterX + fHalfWidth, p_fYMax, p_fCenterZ + fHalfWidth);

	if(p_iLevels > 1)
	{
		m_apSubs[NW] = new CQuadTree<UserDataType>(p_fCenterX - fQuarterWidth, p_fCenterZ - fQuarterWidth, fHalfWidth, p_fYMin, p_fYMax, p_iLevels-1);
		m_apSubs[NE] = new CQuadTree<UserDataType>(p_fCenterX + fQuarterWidth, p_fCenterZ - fQuarterWidth, fHalfWidth, p_fYMin, p_fYMax, p_iLevels-1);
		m_apSubs[SE] = new CQuadTree<UserDataType>(p_fCenterX + fQuarterWidth, p_fCenterZ + fQuarterWidth, fHalfWidth, p_fYMin, p_fYMax, p_iLevels-1);
		m_apSubs[SW] = new CQuadTree<UserDataType>(p_fCenterX - fQuarterWidth, p_fCenterZ + fQuarterWidth, fHalfWidth, p_fYMin, p_fYMax, p_iLevels-1);
	}
	else
	{
		for(int i=0; i<4; ++i)
		{
			m_apSubs[i] = 0;
		}
	}
}
//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
CQuadTree<UserDataType>::~CQuadTree<UserDataType>()
{
	for(int i=0; i<4; ++i)
	{
		delete m_apSubs[i];
	}
}
//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
std::vector<UserDataType>&			
CQuadTree<UserDataType>::GetItems()
{
	return m_axItems;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
void 
CQuadTree<UserDataType>::AddItem(UserDataType p_xUserData, const CAxisAlignedBoundingBox& p_xrAABB)
{
	CQuadTree<UserDataType>& rxNode = FindNode(p_xrAABB);
	rxNode.m_axItems.push_back(p_xUserData);
		
}
//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
void							
CQuadTree<UserDataType>::AddItem(UserDataType p_xUserData, const CBoundingSphere& p_xrBoundingSphere)
{
	//DebugPrint("AddItem oldsphere %f %f %f %f",
	//p_xrBoundingSphere.m_vCenter.x(), p_xrBoundingSphere.m_vCenter.y(), p_xrBoundingSphere.m_vCenter.z(), p_xrBoundingSphere.m_fRadius);

	CQuadTree<UserDataType>& rxNode = FindNode(p_xrBoundingSphere);
	rxNode.m_axItems.push_back(p_xUserData);
}
//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
void							
CQuadTree<UserDataType>::EraseItem(UserDataType p_xUserData)
{
	unsigned int iSize = (unsigned int) m_axItems.size();
	for(unsigned int i=0; i<iSize; ++i)
	{
		if(m_axItems[i] == p_xUserData)
		{
			if(i != iSize-1)
			{
				m_axItems[i] = m_axItems[iSize -1];
			}
			m_axItems.pop_back();
			return;
		}
	}

	assert(false); // not found ?!?
}
//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
void
CQuadTree<UserDataType>::EraseItem(UserDataType p_xUserData, const CAxisAlignedBoundingBox& p_xrAABB)
{
	CQuadTree<UserDataType>& rxNode = FindNode(p_xrAABB);
	rxNode.EraseItem(p_xUserData);
}
//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
void
CQuadTree<UserDataType>::EraseItem(UserDataType p_xUserData, const CBoundingSphere& p_xrBoundingSphere)
{
	//DebugPrint("EraseItem sphere %f %f %f %f ",
	//p_xrBoundingSphere.m_vCenter.x(), p_xrBoundingSphere.m_vCenter.y(), p_xrBoundingSphere.m_vCenter.z(), p_xrBoundingSphere.m_fRadius);

	CQuadTree<UserDataType>& rxNode = FindNode(p_xrBoundingSphere);
	rxNode.EraseItem(p_xUserData);
}
//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
void
CQuadTree<UserDataType>::MoveItem(UserDataType p_xUserData, const CBoundingSphere& p_xrOldSphere, const CBoundingSphere& p_xrNewSphere)
{
	//DebugPrint("moveitem oldsphere %f %f %f %f newsphere %f %f %f %f ",
	//	p_xrOldSphere.m_vCenter.x(), p_xrOldSphere.m_vCenter.y(), p_xrOldSphere.m_vCenter.z(), p_xrOldSphere.m_fRadius,
	//	p_xrNewSphere.m_vCenter.x(), p_xrNewSphere.m_vCenter.y(), p_xrNewSphere.m_vCenter.z(), p_xrNewSphere.m_fRadius);

	CQuadTree<UserDataType>& rxNode = FindNode(p_xrOldSphere);
//	if(!rxNode.m_xAABB.Contains(p_xrNewSphere))
	{
		rxNode.EraseItem(p_xUserData);
		AddItem(p_xUserData, p_xrNewSphere);
	}
}
//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
CQuadTree<UserDataType>&
CQuadTree<UserDataType>::FindNode(CVec3 p_vPos)
{
	if(m_apSubs[0])
	{
		for(int i=0; i<4; ++i)
		{
			if(m_apSubs[i]->m_xAABB.PointIsInside(p_vPos))
			{
				return m_apSubs[i]->GetNode(p_vPos);
			}
		}
	}
	return *this;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
CQuadTree<UserDataType>&
CQuadTree<UserDataType>::FindNode(const CAxisAlignedBoundingBox& p_xrAABB)
{
	if(m_apSubs[0])
	{
		for(int i=0; i<4; ++i)
		{
			if(m_apSubs[i]->m_xAABB.Contains(p_xrAABB))
			{
				return m_apSubs[i]->FindNode(p_xrAABB);
			}
		}
	}
	return *this;
}
//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
CQuadTree<UserDataType>&
CQuadTree<UserDataType>::FindNode(const CBoundingSphere& p_xrBoundingSphere)
{
	if(m_apSubs[0])
	{
		for(int i=0; i<4; ++i)
		{
			if(m_apSubs[i]->m_xAABB.Contains(p_xrBoundingSphere))
			{
				return m_apSubs[i]->FindNode(p_xrBoundingSphere);
			}
		}
	}
	return *this;
}
//---------------------------------------------------------------------------------------------------------------------
/**
	uses the quadtree to find all items that are inside the view frustum and puts them into the user-provided array
*/
template<typename UserDataType>
void							
CQuadTree<UserDataType>::CollectVisibleItems(const CViewFrustum& p_xrFrustum, std::vector<UserDataType>& po_raxItems) const
{
	if(m_apSubs[0])
	{
		for(int i=0; i<4; ++i)
		{
			CViewFrustum::Intersection eIntersection = p_xrFrustum.CheckAABBIntersection(m_apSubs[i]->m_xAABB, m_apSubs[i]->m_iLastDecisivePlane);
			if(eIntersection == CViewFrustum::Intersection::Inside)
			{
				m_apSubs[i]->CollectItems(po_raxItems);
			}
			else if(eIntersection == CViewFrustum::Intersection::Intersects)
			{
				m_apSubs[i]->CollectVisibleItems(p_xrFrustum, po_raxItems);
			}
		}
	}
	
	for(unsigned int i=0; i<m_axItems.size(); ++i)
	{
		po_raxItems.push_back(m_axItems[i]);
	}
}

//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
void
CQuadTree<UserDataType>::CollectIntersectingItems(const CRay& p_xrRay, std::vector<UserDataType>& po_raxItems) const
{
	if(m_apSubs[0])
	{
		for(int i=0; i<4; ++i)
		{
			if(m_apSubs[i]->m_xAABB.Overlaps(p_xrRay))
			{
				m_apSubs[i]->CollectIntersectingItems(p_xrRay, po_raxItems);
			}
		}
	}
	
	for(unsigned int i=0; i<m_axItems.size(); ++i)
	{
		po_raxItems.push_back(m_axItems[i]);
	}
}

//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
void							
CQuadTree<UserDataType>::CollectItems(std::vector<UserDataType>& po_raxItems) const
{
	if(m_apSubs[0])
	{
		for(int i=0; i<4; ++i)
		{
			m_apSubs[i]->CollectItems(po_raxItems);
		}
	}

	for(unsigned int i=0; i<m_axItems.size(); ++i)
	{
		po_raxItems.push_back(m_axItems[i]);
	}
}

//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
const CAxisAlignedBoundingBox&	
CQuadTree<UserDataType>::GetAABB() const
{
	return m_xAABB;
}

//---------------------------------------------------------------------------------------------------------------------
template<typename UserDataType>
CQuadTree<UserDataType>*
CQuadTree<UserDataType>::GetChild(ChildNode p_eChildNode) const
{
	return m_apSubs[p_eChildNode];
}

//---------------------------------------------------------------------------------------------------------------------


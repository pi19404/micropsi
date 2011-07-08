#pragma once
#ifndef OBJECTMANAGER_H_INCLUDED
#define OBJECTMANAGER_H_INCLUDED

#include <map>
#include <vector>
#include <string>

#include "e42/core/Model.h"

#include "Engine/core/quadtree.h"

#include "world/worldobject.h"

class CWorld;

class CObjectManager
{
public: 

	CObjectManager(CWorld* p_pxWorld);
	virtual ~CObjectManager();

	void				Clear();

	void				CreateFakeAgents();

	void				Tick(); 
	void				Render(TRenderContextPtr spxRenderContext);
	void				UpdateLODLevels(const CViewFrustum& p_rxFrustum);

	void				FromXMLElement(const TiXmlElement* p_pXmlElement);
	void				ToXMLElement(TiXmlElement* p_pXmlElement) const;

	void				AddObj(CWorldObject* p_pxObj);
   	bool				DeleteObj(__int64 p_iID);
	CWorldObject*		FindObj(__int64 p_iID);

	/// find object by name; linear search - slow!
	CWorldObject*		FindObj(std::string p_sObjectName);

	void				ClearAllObjects();
	__int64				GetFreeID();
	
	/// add object to list of ticking objects; i.e. objects wants its Tick() method to be called in each simulation step
	void				StartToTick(__int64 p_iID);

	int					GetNumberOfObjects() const;
	int					GetNumberOfCurrentlyVisibleObjects() const;
			
	typedef std::map<__int64, CWorldObject*>::iterator ObjectIterator;

	void				StartIteration(ObjectIterator& p_xIterator);
	CWorldObject*		Iterate(ObjectIterator& p_xIterator);

	void				CollectVisibleItems(const CViewFrustum& p_rxFrustum, std::vector<CWorldObject*>& po_rapxObjects);

	void				SetMaxVisibilityDistance(float p_fDistance);

private:

	CWorld*								m_pxWorld;				///< my world

	__int64								m_iFirstFreeID;			///< first object id that has not been seen yet
	int									m_iObjectsVisible;		///< number of visisble objects in last frame - for statistics
	bool								m_bViewFrustumCulling;	///< cull objects on view frustum

	std::map<__int64, CWorldObject*>	m_AllObjects;			///< list of all objects
	CQuadTree<CWorldObject*>*			m_pxQuadTree;			///< quadtree with all objects
	std::vector<CWorldObject*>			m_apxTickingObjects;	///< list with ticking objects

	float								m_fMaxVisibilityDistance;

	friend class CWorldObject;
};

#include "objectmanager.inl"

#endif // OBJECTMANAGER_H_INCLUDED

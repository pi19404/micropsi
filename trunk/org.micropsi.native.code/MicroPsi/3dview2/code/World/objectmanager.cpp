
#include "Application/stdinc.h"
#include "World/objectmanager.h"

#include "tinyxml.h"

#include "e42/E42Application.h"
#include "e42/core/ModelFactory.h"

#include "Application/3DView2.h"

#include "World/world.h"
#include "World/scriptedworldobject.h"

using std::map;
using std::string;
using std::vector;

//---------------------------------------------------------------------------------------------------------------------
/**
*/
CObjectManager::CObjectManager(CWorld* p_pxWorld)
{
	m_pxWorld = p_pxWorld;

	m_iFirstFreeID = 0;
	m_iObjectsVisible = 0;

	m_pxQuadTree = 0;

	m_fMaxVisibilityDistance = FLT_MAX;
	m_bViewFrustumCulling	 = true;

	Clear();
}


//---------------------------------------------------------------------------------------------------------------------
CObjectManager::~CObjectManager()
{
	ClearAllObjects();
	delete m_pxQuadTree;
}

//---------------------------------------------------------------------------------------------------------------------
void
CObjectManager::Clear()
{
	ClearAllObjects();
}

//---------------------------------------------------------------------------------------------------------------------
/// hack for presentation
void
CObjectManager::CreateFakeAgents()
{
    CScriptedWorldObject* p;
	p = new CScriptedWorldObject(m_pxWorld, "SteamVehicleAgent", 0.0f, 0.0f, 0.0f, 1.0f);
	AddObj(p);
	p->AddWayPoint(CVec3(0.0f, 0.0f, 0.0f));
	p->AddWayPoint(CVec3(13.0f, -26.0f, 0.0f));
	p->AddWayPoint(CVec3(36.0f, -32.0f, 0.0f));
	p->AddWayPoint(CVec3(39.0f, 13.0f, 0.0f));

	p = new CScriptedWorldObject(m_pxWorld, "BraitenbergVehicleAgent", 0.0f, 0.0f, 0.0f, 1.0f);
	AddObj(p);
	p->AddWayPoint(CVec3(27.0f, 13.0f, 0.0f));
	p->AddWayPoint(CVec3(5.0f,  13.0f, 0.0f));
	p->AddWayPoint(CVec3(-10.0f, 14.0f, 0.0f));
	p->AddWayPoint(CVec3(33.0f, 13.0f, 0.0f));


	p = new CScriptedWorldObject(m_pxWorld, "BraitenbergVehicleAgent", 0.0f, 0.0f, 0.0f, 1.0f);
	AddObj(p);
	p->AddWayPoint(CVec3(38.0f, 62.0f, 0.0f));
	p->AddWayPoint(CVec3(65.0f,  -63.0f, 0.0f));
	p->AddWayPoint(CVec3(76.0f, 13.0f, 0.0f));
	p->AddWayPoint(CVec3(52.0f, -51.0f, 0.0f));
}

//---------------------------------------------------------------------------------------------------------------------
/**
*/
void 
CObjectManager::AddObj(CWorldObject* p_pxObj)
{
	assert(p_pxObj);
	if(p_pxObj->GetID() >= m_iFirstFreeID)
	{
		m_iFirstFreeID = p_pxObj->GetID() + 1; 
	}

#ifdef _DEBUG
	// in debug mode, check that object id is still free
	map<__int64, CWorldObject*>::iterator i;
	i = m_AllObjects.find(p_pxObj->GetID());
	assert(i == m_AllObjects.end()  ||  m_AllObjects.size() == 0);
#endif

	m_AllObjects[p_pxObj->GetID()] = p_pxObj;
	m_pxQuadTree->AddItem(p_pxObj, p_pxObj->GetBoundingSphere());
}


//---------------------------------------------------------------------------------------------------------------------
/**
	erase object with given ID
*/
bool 
CObjectManager::DeleteObj(__int64 p_iID)
{
	if(m_AllObjects.size() == 0)
	{
		return false;
	}

	map<__int64, CWorldObject*>::iterator i;
	i = m_AllObjects.find(p_iID);
	if(i != m_AllObjects.end())
	{
		m_pxQuadTree->EraseItem(i->second, i->second->GetBoundingSphere());

		delete i->second;

		for(unsigned int j=0; j<m_apxTickingObjects.size(); ++j)
		{
			if(m_apxTickingObjects[j] == i->second)
			{
				m_apxTickingObjects.erase(m_apxTickingObjects.begin() + j);
				break;
			}
		}

		m_AllObjects.erase(i);
		return true;
	}

	return false;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	\return	pointer to object with given ID or 0 if not found
*/
CWorldObject* 
CObjectManager::FindObj(__int64 p_iID)
{
	if(m_AllObjects.size() == 0)
	{
		return 0;
	}

	map<__int64, CWorldObject*>::iterator i;
	i = m_AllObjects.find(p_iID);
	if(i != m_AllObjects.end())
	{
		return i->second;
	}
	else
	{
		return 0;
	}
}

//---------------------------------------------------------------------------------------------------------------------
CWorldObject*
CObjectManager::FindObj(std::string p_sObjectName)
{
	if(m_AllObjects.size() == 0)
	{
		return 0;
	}

	map<__int64, CWorldObject*>::iterator i;
	for(i=m_AllObjects.begin(); i!=m_AllObjects.end(); i++)
	{
		if(i->second->GetObjectName() == p_sObjectName)
		{
			return i->second;
		}
	}

	return 0;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	discards all objects
*/
void
CObjectManager::ClearAllObjects()
{
	if(m_AllObjects.size() != 0)
	{
		map<__int64, CWorldObject*>::iterator i;
		for(i = m_AllObjects.begin(); i != m_AllObjects.end(); i++)
		{
			delete i->second;
		}
		m_AllObjects.clear();
	}

	m_apxTickingObjects.clear();
	delete m_pxQuadTree;

	// create Quadtree
	float fWorldWidth = 512.0f;
	int iLevels = 1;
	float fSize = fWorldWidth;
	while(fSize > 8.0f)
	{
		fSize /= 2.0f;
		iLevels++;
	}

	// TODO: Weltgröße irgendwie in CWorld festlegen 
	m_pxQuadTree = new CQuadTree<CWorldObject*>	(0.0f, 0.0f, fWorldWidth, - 10.0f, 50.0f, iLevels);
}

//---------------------------------------------------------------------------------------------------------------------
void
CObjectManager::Tick()
{
	unsigned int i=0;
	while(i < m_apxTickingObjects.size())
	{
		if(m_apxTickingObjects[i]->Tick())
		{
			++i;
		}
		else
		{
			m_apxTickingObjects.erase(m_apxTickingObjects.begin() + i);
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CObjectManager::UpdateLODLevels(const CViewFrustum& p_rxFrustum)
{
	// collect all visible objects using the quadtree

	vector<CWorldObject*> apxObjects;
	CViewFrustum xFrustum = p_rxFrustum;
	if(m_fMaxVisibilityDistance < xFrustum.GetFarPlaneDistance())
	{
		xFrustum.SetFarPlaneDistance(m_fMaxVisibilityDistance);
	}

	CVec3 vViewerPos = p_rxFrustum.GetEyePoint();
	m_pxQuadTree->CollectVisibleItems(xFrustum, apxObjects);
	for(unsigned int i=0; i<apxObjects.size(); i++)
	{
		apxObjects[i]->UpdateLODModel(vViewerPos);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CObjectManager::CollectVisibleItems(const CViewFrustum& p_rxFrustum, vector<CWorldObject*>& po_rapxObjects)
{
	m_pxQuadTree->CollectVisibleItems(p_rxFrustum, po_rapxObjects);
}

//---------------------------------------------------------------------------------------------------------------------
void
CObjectManager::Render(TRenderContextPtr spxRenderContext)
{
	if(!m_bViewFrustumCulling)
	{
		// very naive - simply render everything
		if(m_AllObjects.size() != 0)
		{
			map<__int64, CWorldObject*>::iterator i;
			for(i = m_AllObjects.begin(); i != m_AllObjects.end(); i++)
			{
				i->second->Render(spxRenderContext);
			}
		}
		m_iObjectsVisible = (int) m_AllObjects.size();

	}
	else
	{

		// collect all visible objects using the quadtree

		m_iObjectsVisible = 0;
		vector<CWorldObject*> apxObjects;
		CViewFrustum xFrustum = spxRenderContext->m_xViewFrustum;
		if(m_fMaxVisibilityDistance < xFrustum.GetFarPlaneDistance())
		{
			xFrustum.SetFarPlaneDistance(m_fMaxVisibilityDistance);
		}

		m_pxQuadTree->CollectVisibleItems(xFrustum, apxObjects);
		for(unsigned int i=0; i<apxObjects.size(); i++)
		{
			// check against correct bounding box before rendering
			if(apxObjects[i]->GetVisible())
			{
				if(xFrustum.SphereIntersects(apxObjects[i]->GetBoundingSphere()))
				{
					apxObjects[i]->Render(spxRenderContext);
					m_iObjectsVisible++;
				}
			}
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CObjectManager::FromXMLElement(const TiXmlElement* p_pXmlElement)
{
    const TiXmlNode* pxSubNode = p_pXmlElement->FirstChild();
    while (pxSubNode)
    {
	    if (pxSubNode->Type() == TiXmlNode::ELEMENT)
	    {
		    string sType = pxSubNode->ToElement()->Value();
		    if(sType == "object")
		    {
				CWorldObject* pxObj = new CWorldObject(m_pxWorld);
				pxObj->FromXMLElement(pxSubNode->ToElement());
				AddObj(pxObj);
			}
	    }
	    pxSubNode = pxSubNode->NextSibling();
    }
}


//---------------------------------------------------------------------------------------------------------------------
void
CObjectManager::ToXMLElement(TiXmlElement* p_pXmlElement) const
{
	if(m_AllObjects.size() != 0)
	{
		map<__int64, CWorldObject*>::const_iterator i;
		for(i = m_AllObjects.begin(); i != m_AllObjects.end(); i++)
		{
			TiXmlElement xElement("object");
			TiXmlNode* pxElement = p_pXmlElement->InsertEndChild(xElement);
			i->second->ToXMLElement(pxElement->ToElement());
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
__int64
CObjectManager::GetFreeID()
{
	__int64 i = m_iFirstFreeID;
	m_iFirstFreeID++;
	return i;
}

//---------------------------------------------------------------------------------------------------------------------
void
CObjectManager::StartIteration(ObjectIterator& p_xIterator)
{
	if(m_AllObjects.size() > 0)
	{
		p_xIterator = m_AllObjects.begin();
	}
}

//---------------------------------------------------------------------------------------------------------------------
CWorldObject*
CObjectManager::Iterate(ObjectIterator& p_xIterator)
{
	if(m_AllObjects.size() == 0  ||  p_xIterator == m_AllObjects.end())
	{
		return 0;
	}

	CWorldObject* pxObj = p_xIterator->second;
	p_xIterator++;
	return pxObj;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CObjectManager::StartToTick(__int64 p_iID)
{
	CWorldObject* pxObj = FindObj(p_iID);
	if(pxObj)
	{
		for(unsigned int i=0; i<m_apxTickingObjects.size(); ++i)
		{
			if(m_apxTickingObjects[i] == pxObj)
			{
				return;
			}
		}
		m_apxTickingObjects.push_back(pxObj);
	}
}
//---------------------------------------------------------------------------------------------------------------------


#include "stdafx.h"

#include "e42/core/ResourceFactory.h"

#include "e42/core/ResourceProxy.h"

//-----------------------------------------------------------------------------------------------------------------------------------------
CResourceFactory::CResourceFactory()
:	m_iNumResources		(0)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CResourceFactory::~CResourceFactory()
{
	if (!m_mResourceHash.empty())
	{
		DebugPrint("CResourceFactory::~CResourceFactory() - error: resources not released!\n");

		int iCount = 0;

		TResourceHash::iterator iter = 
			m_mResourceHash.begin();

		char acBuffer[256];

		while (iter != m_mResourceHash.end())
		{
			sprintf(acBuffer, "  %d: name=%s  refcount=%d\n", iCount, iter->first.c_str(), iter->second->GetRefCount());
			DebugPrint(acBuffer);
			iCount++;

			iter++;
		}
	}
	

	assert(m_iNumResources == 0);       
	// vielleicht vergessen "ReleaseOwnResources" aufzurufen?
	// muss aus dem Destruktor der abgeleiteten Factory aufgerufen werden, da sonst ein Pure-Virtual-Functioncall auftritt
	// falls der Wert negativ ist wurden die ResourceProxys vielleicht manuell genewt (nicht durch CResourceFactory::AddResource)

	assert(m_mResourceHash.empty());
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CResourceFactory::AddResourceToHash(const std::string& sResourceID, CResourceProxy* pxResourceProxy)
{
	if (!sResourceID.empty())
	{
		assert(pxResourceProxy);
		assert(LookUpResource(sResourceID) == NULL);

		m_mResourceHash[sResourceID] = pxResourceProxy;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CResourceFactory::RemoveResourceFromHash(CResourceProxy* pxResourceProxy)
{
	if (!m_mResourceHash.empty())
	{
		TResourceHash::iterator iter = 
			m_mResourceHash.begin();

		while (iter != m_mResourceHash.end())
		{
			if (iter->second == pxResourceProxy)
			{
				m_mResourceHash.erase(iter);
				break;
			}

			iter++;
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CResourceFactory::DestroyResourceProxy(CResourceProxy* pxResourceProxy)
{
	m_iNumResources--;

	RemoveResourceFromHash(pxResourceProxy);
	DestroyResource(pxResourceProxy->GetResource());

	delete pxResourceProxy;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CResourceProxy* 
CResourceFactory::LookUpResource(const std::string sResourceID)
{
	if (sResourceID.empty() ||
		m_mResourceHash.empty())
	{
		return NULL;
	}
	else
	{
		TResourceHash::const_iterator iter = 
			m_mResourceHash.find(sResourceID);

		if (iter == m_mResourceHash.end())
		{
			return NULL;
		}
		else
		{
			return iter->second;
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CResourceProxy* 
CResourceFactory::AddResource(const std::string sResourceID, void* pxResource, bool bAutoDestroy)
{
	if (pxResource)
	{
		m_iNumResources++;

		CResourceProxy* pxResourceProxy = new CResourceProxy(pxResource, this);
		AddResourceToHash(sResourceID, pxResourceProxy);

		if (!bAutoDestroy)
		{
			pxResourceProxy->AddRef();
			m_axOwnResourceProxys.Push() = pxResourceProxy;
		}

		return pxResourceProxy;
	}
	else
	{
		return NULL;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
int
CResourceFactory::GetNumResources() const
{
	return m_iNumResources;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CResourceFactory::ReleaseOwnResources()
{
	for (int i = 0; i < (int)m_axOwnResourceProxys.Size(); i++)
	{
		m_axOwnResourceProxys[i]->Release();
	}

	m_axOwnResourceProxys.Clear();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CResourceFactory::StartIterateResources(TResourceIterator& rxIterator)
{
	rxIterator = m_mResourceHash.begin();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CResourceProxy*
CResourceFactory::IterateResources(TResourceIterator& rxIterator)
{
	if (m_mResourceHash.empty() ||
		rxIterator == m_mResourceHash.end())
	{
		return NULL;
	}
	else
	{
		return (rxIterator++)->second;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------

/*
	die ResourceFactory erzeugt ResourceProxys und verwendet ein Hash um mehrfaches 
	Erzeugen gleicher Resourcen zu verhindern
*/

#pragma once 

#ifndef E42_RESOURCEFACTORY_H_INCLUDED
#define E42_RESOURCEFACTORY_H_INCLUDED

#include "e42/stdinc.h"

#include <map>
#include <string>

#include "baselib/dynarray.h"

class CResourceProxy;

class CResourceFactory
{
private:
	typedef std::map<const std::string, CResourceProxy*> TResourceHash;											///< Datenstruktur für Mapping von ResourcenNamen auf bereits erstellte Resource-Proxys

	TResourceHash	m_mResourceHash;																			///< Mapping von ResourcenNamen auf bereits erstellte Resource-Proxys
	int				m_iNumResources;																			///< Anzahl zur Zeit angelegten Resourcen

	void			AddResourceToHash(const std::string& sResourceID, CResourceProxy* pxResourceProxy);			///< fügt eine Resource zum Hash hinzu (falls Name != "")
	void			RemoveResourceFromHash(CResourceProxy* pxResourceProxy);									///< entfernt eine Resource aus dem Hash

	virtual void	DestroyResource(void* pxResource) = 0;														///< wird zum Löschen der Resource aufgerufen; muss von der abgeleiteten Klasse implementiert werden

	CDynArray<CResourceProxy*, 5, false> m_axOwnResourceProxys;													///< alle ReourceProxys, auf die die Factory eine interne Referenz hält, damit diese nicht gelöscht werden (bAutoDestroy)


protected:

	void			ReleaseOwnResources();																		///< löscht alle internen Referenzen auf Proxys (Proxys, die noch externe Referenzen haben werden nicht gelöscht)

	typedef TResourceHash::const_iterator TResourceIterator;													///< Iterator um über die ResourceProxys zu iterieren
	void			StartIterateResources(TResourceIterator& rxIterator);										///< Start der Iteration
	CResourceProxy*	IterateResources(TResourceIterator& rxIterator);											///< iteriert über Resourcen; gibt NULL zurück, falls Iteration beendet


public:

	void			DestroyResourceProxy(CResourceProxy* pxResourceProxy);										///< löscht einen ResourceProxy und entfernt ihn aus dem Hash; wird vom ResourceProxy aufgerufen, wenn der RefCount 0 ist

	CResourceProxy*	LookUpResource(const std::string sResourceID);												///< liefert den Proxy zu einem Namen oder NULL, falls noch keine Resource mit dem Namen existiert
	CResourceProxy*	AddResource(const std::string sResourceID, void* pxResource, bool bAutoDestroy = true);		///< erzeugt einen Neuen Proxy und legt den Namen im Hash ab, wenn AutoDestroy == false, bleibt die Resource alloziert, auch wenn keine externen Referenzen auf sie existieren

	int				GetNumResources() const;																	///< liefert die Anzahl der aktuell von der Factory verwalteten Resourcen

	CResourceFactory();
	~CResourceFactory();
};

#endif // E42_RESOURCEFACTORY_H_INCLUDED
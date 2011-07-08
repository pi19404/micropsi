#ifndef ZONEMGR_H_INCLUDED
#define ZONEMGR_H_INCLUDED

#include "baselib/geometry/CVector.h"
#include "e42/core/ResourceHandles.h"
#include <vector>

class CEngineController;
class COpcodeMesh;
class CCamera;
class CD3DXFrame;
class TiXmlNode;

class CZoneMgr
{
private:
	CEngineController*	m_pxEngineController;

	class CZone
	{
	public:
		CZone();
		~CZone();

		COpcodeMesh*				m_pVolume;								///< Mesh, dass das Volumen der Zone definiert
		std::vector<CD3DXFrame*>	m_axVisibleFrames;						///< Array aller Frames, die aus dieser Zone sichtbar sind

		bool	IsPointInside(const CVec3& vPos) const;						///< prüft ob der Vektor in der Zone liegt
	};

	std::vector<CZone*>			m_axZones;									///< Array aller Zonen


	std::map<const std::string, TModelHandle> m_mControlledModels;		///< Map mit den Handles der kontrollierten Modelle

	std::vector<CD3DXFrame*>	m_axControlledFrames;					///< Array mit allen Frames die vom ZoneManager gesteuert werden

	bool						m_bZoneCullingEnabled;

	int							m_iActiveZoneCount;						///< Anzahl der zur Zeit aktiven Zonen (für Profiling)


public:

	CZoneMgr(CEngineController* pxEngineController = NULL);
	~CZoneMgr();


	void	RegisterModel(const std::string& sName, TModelHandle hndModel);						///< meldet ein Modell an, dass vom ZoneMgr kontrolliert werden soll
	void	InitZones(const std::string& sZoneDefinitionFile, const std::string& sZBoxFile);	///< lädt Zonendefinitionen (muss nach dem Registrieren aller Modells erfolgen)
	void	DeleteZones();																		///< löscht alle Zonen
	
	void	SetZoneCullingEnable(bool bEnable = true);

	void	UpdateVisibility(CCamera* pxCamera);					///< aktualisiert die Visibility der kontrollierten Frames Anhand der aktuellen Position
};

#endif // ZONEMGR_H_INCLUDED
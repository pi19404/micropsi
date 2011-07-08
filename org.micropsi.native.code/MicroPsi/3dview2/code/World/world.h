#pragma once
#ifndef WORLD_H_INCLUDED
#define WORLD_H_INCLUDED

#include <map>
#include <string>

#include "World/worldobject.h"
#include "World/visualization.h"
#include "World/mapparameters.h"

#include "e42/core/RenderContext.h"


class CObjectManager;
class CTerrainSystem;
class CParticleSystem;
class CLevelEditor;
class CObserver;

class CWorld
{
public:

	CWorld(float p_fTerrainBorder, float p_fMaximumRangeOfVision);
	~CWorld();	

	enum WrapState
	{
		WS_MapDefault,
		WS_ForceWrapAround,
		WS_ForceNoWrapAround
	};

	CObjectManager*		GetObjectManager() const;
	CTerrainSystem*		GetTerrain() const;

	void				Tick(); 
	void				RenderSkybox(TRenderContextPtr spxRenderContext);
	void				Render(TRenderContextPtr spxRenderContext, const CMat4S& matWorldTransform);

	void				SetRenderTerrain(bool p_bRenderTerrain);
	bool				GetRenderTerrain() const;

	void				SetRenderObjects(bool p_bRenderObjects);
	bool				GetRenderObjects() const;

	void				SetRenderWater(bool p_bRenderWater);
	bool				GetRenderWater() const;

	void				InitDefaultTerrain();

	bool				SaveAsXML(const char* p_pcFile);
	bool				LoadFromXML(const char* p_pcFile, 
									const char* p_pcVisualizationFile = 0, 
									WrapState p_eWrapMode = WS_MapDefault,
									const CVec3* p_pvOffset = 0, 
									const CVec3* p_pvAbsoluteSize = 0);
	std::string			GetCurrentWorldFileName() const;

	/// sets position and roation of the given observer to starting values defined by the world configuration file 
	void				ResetObserver(CObserver* p_pxObserver) const;

	/// gets the description text from any world xml file
	static std::string	GetDescriptionFromXML(const char* p_pcXMLFilename);

	void				CreateFakeAgents();

	int					GetWaterHeight() const;

	const CVisualization&	GetVisualization() const;

	CLevelEditor*		GetEditor() const; 

	float				GetTerrainBorder() const;

private:

	void				RenderWater(TRenderContextPtr spxRenderContext);

	void				CreateTerrain();

	bool				m_bRenderObjects;			///< true: render world objects
	bool				m_bRenderTerrain;			///< true: render terrain
	bool				m_bRenderWater;				///< true: render water

	float				m_fTerrainBorder;			///< border around terrain, in meters
	float				m_fMaximumRangeOfVision;	///< maximum range of vision, in meters

	CParticleSystem*	m_pxParticleSystem;			///< Particle System Controller Object

	CObjectManager*		m_pxObjMgr;					///< owns all objects in the world
	CTerrainSystem*		m_pxTerrain;				///< terrain renderer	

	TModelHandle		m_hSkyboxModel;
	TModelHandle		m_hWaterModel;

	int					m_iWaterHeight;				///< height of water plane

	CVisualization		m_xVisualization;			///< current world visualization 
	CMapParameters		m_xMapParameters;			///< Map Settings

	CLevelEditor*		m_pxLevelEditor;			///< Editor

	std::string			m_sFilename;				///< filename this world was loaded with
	std::string			m_sDefaultVisualizationFile;///< visualization file name
	std::string			m_sDescription;				///< description for this world
};

#include "world.inl"

#endif // WORLD_H_INCLUDED


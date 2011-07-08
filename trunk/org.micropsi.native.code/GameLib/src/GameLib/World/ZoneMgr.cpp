#include "stdafx.h"

#include <tinyxml.h>

#include "Gamelib/Collision/OpcodeMesh.h"

#include "e42/core/Model.h"
#include "e42/core/D3DXFrame.h"
#include "baselib/xmlutils.h"
#include "e42/core/EngineController.h"
#include "e42/core/ModelFactory.h"
#include "e42/Camera.h"
#include "baselib/FileLocator.h"
#include "baselib/geometry/Line.h"

#include "Gamelib/World/ZoneMgr.h"

using std::string;
using std::vector;

extern bool g_bDevModeEnabled;

//-----------------------------------------------------------------------------------------------------------------------------------------
CZoneMgr::CZone::CZone()
{
	m_pVolume = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CZoneMgr::CZone::~CZone()
{
	delete m_pVolume;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CZoneMgr::CZone::IsPointInside(const CVec3& vPos) const
{
	CLine3 xLine(vPos, vPos + CVec3(0, 100000, 0));

	int iNumIntersections = m_pVolume->CalcNumIntersections(CMat4S::mIdentity, xLine);

	return iNumIntersections > 0;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CZoneMgr::CZoneMgr(CEngineController* pxEngineController)
:	m_pxEngineController(pxEngineController)
{
	if (m_pxEngineController == NULL)
		m_pxEngineController = &CEngineController::Get();

	m_bZoneCullingEnabled = true;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CZoneMgr::~CZoneMgr()
{
	DeleteZones();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CZoneMgr::RegisterModel(const string& sName, TModelHandle hndModel)
{
	assert(m_axZones.empty() && "models must be registered before calling InitZones");
	assert(m_mControlledModels.find(sName) == m_mControlledModels.end());

	m_mControlledModels[sName] = hndModel;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CZoneMgr::InitZones(const string& sZoneDefinitionFile, const std::string& sZBoxFile)
{
	DeleteZones();


	CMeshLoaderOptions xMeshLoaderOptions;
	xMeshLoaderOptions.m_bGenerateVertexElementsByShaderInput = false;
	xMeshLoaderOptions.m_dwMeshOptions = D3DXMESH_SYSTEMMEM | D3DXMESH_DYNAMIC;
	xMeshLoaderOptions.m_bLoadResources = ::g_bDevModeEnabled;		// fürs Rendern
	TModelHandle hndZBox = m_pxEngineController->GetModelFactory()->CreateModelFromFile(sZBoxFile, &xMeshLoaderOptions);


	TiXmlDocument* pxXMLDoc = 
		new TiXmlDocument(m_pxEngineController->GetFileLocator()->GetPath(sZoneDefinitionFile).c_str());

	if (!pxXMLDoc->LoadFile()) 
	{ 
		DebugPrint("could not load zone file %s", sZoneDefinitionFile.c_str());
		MessageBox(NULL, sZoneDefinitionFile.c_str(), "level xml - error", MB_ICONERROR | MB_OK);
		delete pxXMLDoc;
	}


	const TiXmlNode* pxZoneDefinitionNode = pxXMLDoc->FirstChild("zones");

	const TiXmlElement* pxZoneElement = pxZoneDefinitionNode->FirstChildElement("zone");
	while (pxZoneElement)
	{
		// <zone>
		CZone* pxZone = new CZone;
		m_axZones.push_back(pxZone);

		const TiXmlElement* pxZBoxFrameElement = pxZoneElement->FirstChildElement("zbox-frame");
		if (pxZBoxFrameElement)
		{
			// <zbox-frame>
			const char* pcName = pxZBoxFrameElement->Attribute("name");
			assert(pcName && "missing 'name'-attribute in 'zbox-frame'-element");

			if (pcName)
			{
				CD3DXFrame* pxFrame = hndZBox->GetFrameByName(pcName);
				assert(pxFrame && "zboxframe not found!");

				if (pxFrame)
				{
					pxZone->m_pVolume = new COpcodeMesh(pxFrame);
				}
				else
				{
					MessageBox(NULL, (string("zbox frame '") + string(pcName) + string("' not found!")).c_str(), "error in zone definitions!", MB_ICONERROR);
				}
			}

			assert(pxZBoxFrameElement->NextSiblingElement("zbox-frame") == NULL);
		}


		const TiXmlElement* pxVisibilityElement = pxZoneElement->FirstChildElement("visibility");
		if (pxVisibilityElement)
		{
			// <visibility>
			const TiXmlElement* pxFrameElement = pxVisibilityElement->FirstChildElement("frame");
			
			while (pxFrameElement)
			{
				// <frame>
				const char* pcVisibleFrameName = pxFrameElement->Attribute("name");
				assert(pcVisibleFrameName && "missing 'name'-attribute in 'frame'-element");

				const char* pcVisibleModelName = pxFrameElement->Attribute("model");
				assert(pcVisibleModelName && "missing 'model'-attribute in 'frame'-element");

				if (pcVisibleFrameName && pcVisibleModelName)
				{
					assert(m_mControlledModels.find(pcVisibleModelName) != m_mControlledModels.end());

					TModelHandle hndModel = m_mControlledModels[pcVisibleModelName];

					CD3DXFrame* pxFrame = hndModel->GetFrameByName(pcVisibleFrameName);
					assert(pxFrame && "frame not found!");

					if (pxFrame)
					{
						pxZone->m_axVisibleFrames.push_back(pxFrame);
						m_axControlledFrames.push_back(pxFrame);
					}
					else
					{
						MessageBox(NULL, (string("frame '") + string(pcVisibleFrameName) + 
										string("' not found in model") + string(pcVisibleModelName) + 
										string("!")).c_str(), "error in zone definitions!", MB_ICONERROR);
					}
				}

				pxFrameElement = pxFrameElement->NextSiblingElement("frame");
			}


			assert(pxVisibilityElement->NextSiblingElement("visibility") == NULL);
		}

		pxZoneElement = pxZoneElement->NextSiblingElement("zone");
	}


	delete pxXMLDoc;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CZoneMgr::DeleteZones()
{
	for (int iZoneIdx = 0; iZoneIdx < (int)m_axZones.size(); iZoneIdx++)
	{
		delete m_axZones[iZoneIdx];
	}
	m_axZones.clear();

	m_axControlledFrames.clear();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CZoneMgr::SetZoneCullingEnable(bool bEnable)
{
	m_bZoneCullingEnabled = bEnable;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CZoneMgr::UpdateVisibility(CCamera* pxCamera)
{
	if (m_bZoneCullingEnabled)
	{
		// alle kontrollierten frames auf invisible setzen
		for (int iFrameIdx = 0; iFrameIdx < (int)m_axControlledFrames.size(); iFrameIdx++)
		{
			m_axControlledFrames[iFrameIdx]->bIsVisible = false;
		}

		m_iActiveZoneCount = 0;

		for (int iZoneIdx = 0; iZoneIdx < (int)m_axZones.size(); iZoneIdx++)
		{
			const CZone& rxZone = *m_axZones[iZoneIdx];
			if (rxZone.IsPointInside(pxCamera->GetPos()))
			{
				m_iActiveZoneCount++;

				// alle sichtbaren Frames der Zone einschalten
				for (int iFrameIdx = 0; iFrameIdx < (int)rxZone.m_axVisibleFrames.size(); iFrameIdx++)
				{
					rxZone.m_axVisibleFrames[iFrameIdx]->bIsVisible = true;
				}
			}
		}
	}
	else
	{
		for (int iFrameIdx = 0; iFrameIdx < (int)m_axControlledFrames.size(); iFrameIdx++)
		{
			m_axControlledFrames[iFrameIdx]->bIsVisible = true;
		}
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------

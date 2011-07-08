
#include "Application/stdinc.h"
#include "Application/3dview2.h"

#include "tinyxml.h"

#include "baselib/filelocator.h"

#include "e42/E42Application.h"
#include "e42/core/ModelFactory.h"
#include "e42/ParticleSystem.h"
#include "baselib/xmlutils.h"
#include "e42/core/DeviceStateMgr.h"

#include "world/world.h"
#include "world/objectmanager.h"

#include "Observers/observer.h"

#include "Engine/Terrain/terrainsystem.h"
#include "Engine/Particles/ParticleSmoke.h"

#include "World/LevelEditor.h"

using std::map;
using std::string;
using namespace XMLUtils;

//---------------------------------------------------------------------------------------------------------------------
/**
*/
CWorld::CWorld(float p_fTerrainBorder, float p_fMaximumRangeOfVision)
{
	m_bRenderObjects = true;
	m_bRenderTerrain = true;
	m_bRenderWater	 = true;
	m_iWaterHeight   = 0;

	m_fTerrainBorder = p_fTerrainBorder;
	m_fMaximumRangeOfVision = p_fMaximumRangeOfVision;

	m_xVisualization.FromXML(CE42Application::Get().GetFileLocator()->GetPath("visualizations>standard.xml").c_str());

    m_pxParticleSystem = new CParticleSystem(&CE42Application::Get());
 //   CParticleSmoke* pxSmoke = new CParticleSmoke(m_pxParticleSystem);
	//pxSmoke->SetPos(CVec3(0.0f, 6.0f, 0.0f));
	//pxSmoke->SetDir(CVec3(0.05f, 10.0f, 0.1f));

	m_pxObjMgr	= new CObjectManager(this);
	m_pxTerrain = 0;

	m_hSkyboxModel = CE42Application::Get().GetModelFactory()->CreateModelFromFile(string("model>") + m_xVisualization.GetSkyBoxFileName());
	m_hWaterModel  = CE42Application::Get().GetModelFactory()->CreateModelFromFile(string("model>") + "waterplane.x");

	m_pxLevelEditor = new CLevelEditor(this);
}


//---------------------------------------------------------------------------------------------------------------------
/**
*/
CWorld::~CWorld()
{
	delete m_pxLevelEditor;
	delete m_pxTerrain;
	delete m_pxObjMgr;
    delete m_pxParticleSystem;
}

//---------------------------------------------------------------------------------------------------------------------
void				
CWorld::Tick()
{
	m_pxObjMgr->Tick();
    m_pxParticleSystem->DoGameplayStep(1.0f / 50.0f, C3DView2::Get()->GetCamera());
}

//---------------------------------------------------------------------------------------------------------------------
void				
CWorld::Render(TRenderContextPtr spxRenderContext, const CMat4S& matWorldTransform)
{
	if(m_bRenderTerrain)
	{
		m_pxTerrain->RenderTerrain(spxRenderContext);
	}
	if(m_bRenderObjects)
	{
		m_pxObjMgr->Render(spxRenderContext);
	}
	if(m_bRenderWater)
	{
			C3DView2::Get()->GetDeviceStateMgr()->Reset();
			m_pxTerrain->RenderWater(spxRenderContext);
	}
	m_pxParticleSystem->Render(spxRenderContext);

	m_pxLevelEditor->Render();
}

//---------------------------------------------------------------------------------------------------------------------
void
CWorld::InitDefaultTerrain()
{
	if(!m_pxTerrain)
	{
		m_xMapParameters = m_xVisualization.GetDefaultMapParameters();
		CreateTerrain();
	}
}

//---------------------------------------------------------------------------------------------------------------------
bool
CWorld::SaveAsXML(const char* p_pcFile)
{
	m_sFilename = p_pcFile;
	TiXmlDocument* pxDoc = new TiXmlDocument(p_pcFile);

	// <world> - root
	TiXmlElement xElement("world");
	TiXmlNode* pxRootElement = pxDoc->InsertEndChild(xElement);

	WriteXMLTagString(pxRootElement->ToElement(), "description", m_sDescription);
	WriteXMLTagString(pxRootElement->ToElement(), "defaultvisualization", m_sDefaultVisualizationFile);

    // <terrainmap>

	TiXmlElement xTerrainMapElement("terrainmap");
	TiXmlNode* pxTerrainMapElement = pxRootElement->InsertEndChild(xTerrainMapElement);
	m_xMapParameters.ToXMLElement(pxTerrainMapElement->ToElement());

    // <objects>

	TiXmlElement xObjsElement("objects");
	TiXmlNode* pxObjsElement = pxRootElement->InsertEndChild(xObjsElement);
	m_pxObjMgr->ToXMLElement(pxObjsElement->ToElement());

	bool bSuccess = pxDoc->SaveFile();
    delete pxDoc;

	return bSuccess;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	p_iWrapMode: -1 for default, 0 for force no wrap, 1 for force wrap
*/
bool
CWorld::LoadFromXML(const char* p_pcFile, const char* p_pcVisualizationFile, WrapState p_eWrapMode, const CVec3* p_pvOffset, const CVec3* p_pvAbsoluteSize)
{
	bool bSuccess = false;
	m_pxObjMgr->Clear();

    TiXmlDocument* pxDoc = new TiXmlDocument(p_pcFile);

	if (pxDoc->LoadFile())
	{ 
        TiXmlElement* pxRootNode = pxDoc->FirstChildElement("world");
	    if (pxRootNode) 
        {
			m_sFilename = p_pcFile;

			m_sDescription				= GetXMLTagString(pxRootNode->ToElement(), "description");
			m_sDefaultVisualizationFile = GetXMLTagString(pxRootNode->ToElement(), "defaultvisualization");

			// visualization must be loaded before terrain or objects can be created!
			
			if(p_pcVisualizationFile)
			{
				m_xVisualization.FromXML(CE42Application::Get().GetFileLocator()->GetPath(string("visualizations>") + p_pcVisualizationFile).c_str());
			}
			else
			{
				if(m_sDefaultVisualizationFile.size() > 0)
				{
					m_xVisualization.FromXML(CE42Application::Get().GetFileLocator()->GetPath("visualizations>" + m_sDefaultVisualizationFile).c_str());
				}
				else
				{
					assert(false);
					return false;
				}
			}

			TiXmlElement* pxSubNode = pxRootNode->FirstChildElement();
	        while (pxSubNode)
	        {
		        if (pxSubNode->Type() == TiXmlNode::ELEMENT)
		        {
			        string sType = pxSubNode->ToElement()->Value();
			        if(sType == "objects")
			        {
						m_pxObjMgr->FromXMLElement(pxSubNode->ToElement());
			        }
					else if(sType == "terrainmap")
					{
						m_xMapParameters.FromXMLElement(pxSubNode);
						if(p_eWrapMode != WS_MapDefault)
						{
							m_xMapParameters.m_bWrapAround = p_eWrapMode == WS_ForceWrapAround;
						}
						if(p_pvOffset)
						{
							// psi does not give us a valid height offset!
							m_xMapParameters.m_vOffset.x() = (*p_pvOffset).x();
							m_xMapParameters.m_vOffset.z() = (*p_pvOffset).z();
						}
						if(p_pvAbsoluteSize)
						{
							m_xMapParameters.m_vAbsoluteSize = *p_pvAbsoluteSize;
						}

						CreateTerrain();
						bSuccess = true;
					}
		        }
		        pxSubNode = pxSubNode->NextSiblingElement();
	        }
        }
    }

	delete pxDoc;
	return bSuccess;
}

//---------------------------------------------------------------------------------------------------------------------
string
CWorld::GetDescriptionFromXML(const char* p_pcXMLFilename)
{
	string sRet = "";

	TiXmlDocument*	pxDoc = new TiXmlDocument(p_pcXMLFilename);
	if(pxDoc->LoadFile()) 
	{ 
		TiXmlNode* pxRootNode = pxDoc->FirstChild("world");
		if(pxRootNode) 
		{
			sRet = GetXMLTagString(pxRootNode->ToElement(), "description");
		}
		else
		{
			sRet = "invalid file format";
		}
	}
	else
	{
		sRet = "could not load file";
	}

	delete pxDoc;
	return sRet;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CWorld::RenderSkybox(TRenderContextPtr spxRenderContext)
{ 
	TRenderContextPtr spxNewRenderContext;
    spxNewRenderContext.Create();
    *spxNewRenderContext = *spxRenderContext;

	static float fRotY = 0.0f;
	CMat4S mRot = CMat4S::CalcRotationMatrix(CAxisAngle(CVec3::vYAxis, fRotY));

	CMat4S mTransform;
	mTransform.SetTranslation(C3DView2::Get()->GetCamera()->GetPos());
	mTransform = mRot * mTransform;
	fRotY += 0.0003f;

	m_hSkyboxModel->Render(spxNewRenderContext, mTransform);
}


//---------------------------------------------------------------------------------------------------------------------
void 
CWorld::RenderWater(TRenderContextPtr spxRenderContext)
{ 
	TRenderContextPtr spxNewRenderContext;
    spxNewRenderContext.Create();
    *spxNewRenderContext = *spxRenderContext;

    CMat4S matID; matID.SetIdentity();

	m_hWaterModel->Render(spxNewRenderContext, matID);
}


//---------------------------------------------------------------------------------------------------------------------
void
CWorld::CreateFakeAgents()
{
	m_pxObjMgr->CreateFakeAgents();
}

//---------------------------------------------------------------------------------------------------------------------
/**
	Creates the terrain.
	Note: there must be a valid visualization when you do this!
*/
void
CWorld::CreateTerrain()
{
	if(!m_pxTerrain)
	{
		m_pxTerrain = new CTerrainSystem(&CE42Application::Get());
	}
	else
	{
		m_pxTerrain->Clear();
	}

	for(int i=0; i<m_xVisualization.NumTerrainMaterials(); ++i)
	{
		m_pxTerrain->SetMaterial(i, m_xVisualization.GetTerrainMaterial(i).m_sTextureName);
	}

	m_pxTerrain->BeginTerrainDefinition();
	m_pxTerrain->SetHeightMap(CE42Application::Get().GetFileLocator()->GetPath("terrain>" + m_xMapParameters.m_sHeightMap).c_str());
	m_pxTerrain->SetMaterialMap(CE42Application::Get().GetFileLocator()->GetPath("terrain>" + m_xMapParameters.m_sMaterialMap).c_str());
	m_pxTerrain->SetOffset(m_xMapParameters.m_vOffset);
	m_pxTerrain->SetScale(m_xMapParameters.m_vScaling);
	m_pxTerrain->SetAbsoluteSize(m_xMapParameters.m_vAbsoluteSize);
	m_pxTerrain->SetTextureTileSize(m_xVisualization.GetTextureTileSize());
	m_pxTerrain->SetMaxRangeOfVision(m_fMaximumRangeOfVision);
	m_pxTerrain->SetMapBorderWidth(m_fTerrainBorder);
	m_pxTerrain->SetWrapAround(m_xMapParameters.m_bWrapAround);
	m_pxTerrain->EndTerrainDefinition();
}

//---------------------------------------------------------------------------------------------------------------------
void				
CWorld::ResetObserver(CObserver* p_pxObserver) const
{
	p_pxObserver->SetPos(m_xMapParameters.m_vObserverStartPos);
	p_pxObserver->LookAt(m_xMapParameters.m_vObserverLookAt);
}

//---------------------------------------------------------------------------------------------------------------------

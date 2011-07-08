#include "World/objectvisualization.h"

#include "e42/core/meshloaderoptions.h"

#include "baselib/xmlutils.h"
#include "e42/e42application.h"
#include "e42/core/modelfactory.h"
#include "tinyxml.h"
#include "GameLib/Collision/OpcodeMesh.h"
#include <algorithm>

using std::string;
using namespace XMLUtils;


//---------------------------------------------------------------------------------------------------------------------
CObjectLODLevel::CObjectLODLevel()
{
	m_pxCollisionModel = 0;
}

//---------------------------------------------------------------------------------------------------------------------
CObjectLODLevel::~CObjectLODLevel()
{
	delete m_pxCollisionModel;
}

//---------------------------------------------------------------------------------------------------------------------
bool 
CObjectLODLevel::operator<(const CObjectLODLevel& p_rxOther) const
{
	return m_fMaxDistanceSquare > p_rxOther.m_fMaxDistanceSquare;
} 


//---------------------------------------------------------------------------------------------------------------------
void 
CObjectLODLevel::FromXMLElement(TiXmlElement* p_pxObjectNode, std::string& p_rsWarnings)
{
	string sModel = GetXMLTagString(p_pxObjectNode, "model", "");
	if(sModel.empty())
	{
		p_rsWarnings += "Error: found lod level without model tag!\n";
	}
	else
	{
		SetModel(sModel);
	}

	m_fMaxDistanceSquare = GetXMLTagFloat(p_pxObjectNode, "maxdistance", FLT_MAX);
	m_fMaxDistanceSquare *= m_fMaxDistanceSquare;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CObjectLODLevel::SetModel(const std::string& p_rsModelFile)
{
	m_sModelName = string("model>") + p_rsModelFile;

	CMeshLoaderOptions xOptions;
	xOptions.m_dwMeshOptions &= ~D3DXMESH_WRITEONLY;	// FIXME
	// ^ darf nicht WriteOnly sein, weil Buffer gelockt werden um Kollsionsgeometrie auszulesen
	// Alternative: Modell doppelt laden

	m_hModel  = CE42Application::Get().GetModelFactory()->CreateModelFromFile(m_sModelName, &xOptions);
}

//---------------------------------------------------------------------------------------------------------------------
void 
CObjectLODLevel::UpdateCollisionGeometry()
{
	delete m_pxCollisionModel;
	m_pxCollisionModel = new COpcodeMesh(m_hModel.GetPtr());
}

//---------------------------------------------------------------------------------------------------------------------
CObjectVariation::CObjectVariation(CObjectVisualization* p_pxObjectVisualization, int p_iVariationNumber)
{
	m_pxGroundContactPoints = 0;
	m_pxObjectVisualization = p_pxObjectVisualization;
	m_iVariationNumber = p_iVariationNumber;
	m_bInterpolatedMovement = false;
}

//---------------------------------------------------------------------------------------------------------------------
CObjectVariation::~CObjectVariation()
{
	delete m_pxGroundContactPoints;
	for(unsigned int i=0; i<m_axSortedLODLevels.size(); ++i)
	{
		delete m_axSortedLODLevels[i];
	}
}
//---------------------------------------------------------------------------------------------------------------------
void
CObjectVariation::SetGroundContactPoints(const CObjectVariation::CGroundContactPoints& p_rxPoints)
{
	if(!m_pxGroundContactPoints)
	{
		m_pxGroundContactPoints = new CGroundContactPoints();
	}
	*m_pxGroundContactPoints = p_rxPoints;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CObjectVariation::SortLODLevels()
{
	std::sort(m_axSortedLODLevels.begin(), m_axSortedLODLevels.end(), CObjectLODLevel::Pred);
}
//---------------------------------------------------------------------------------------------------------------------
void 
CObjectVariation::FromXMLElement(TiXmlElement* p_pxObjectNode, std::string& p_rsWarnings)
{
	// ground contact points?
	int iGroundContactPoints = 0;
	CObjectVariation::CGroundContactPoints xGroundContactPoints;
	TiXmlElement* pxGCPElement = p_pxObjectNode->FirstChildElement("groundcontactpoint");
	while(pxGCPElement)
	{
		if(iGroundContactPoints < 3)
		{
			xGroundContactPoints.m_vPoints[iGroundContactPoints] = GetXMLTagVector(pxGCPElement);
		}
		iGroundContactPoints++;

		pxGCPElement = pxGCPElement->NextSiblingElement("groundcontactpoint");

	}

	if(iGroundContactPoints == 3)
	{
		SetGroundContactPoints(xGroundContactPoints);
	}

	if(iGroundContactPoints != 3  &&  iGroundContactPoints != 0)
	{
		p_rsWarnings += "Warning: wrong number of 'groundcontactpoint's in object " + 
			m_pxObjectVisualization->m_sClassName + " (must be 3)\n";
	}

	/// interpolated movement?
	m_bInterpolatedMovement = XMLUtils::GetXMLTagBool(p_pxObjectNode, "interpolatemovement", false);

	// lod levels
	TiXmlElement* pxLOD = p_pxObjectNode->FirstChildElement("lodlevel");
	if(!pxLOD)
	{
		p_rsWarnings += "Error: object tag " + m_pxObjectVisualization->m_sClassName + " has no variation tags!\n";
	}

	while(pxLOD)
	{
		CObjectLODLevel* pxLODLevel = new CObjectLODLevel();
		pxLODLevel->FromXMLElement(pxLOD, p_rsWarnings);
		m_axSortedLODLevels.push_back(pxLODLevel);

		pxLOD = pxLOD->NextSiblingElement("lodlevel");
	}
	SortLODLevels();

	for(unsigned int i=0; i<m_axSortedLODLevels.size(); ++i)
	{
		m_axSortedLODLevels[i]->UpdateCollisionGeometry();
	}
}


//---------------------------------------------------------------------------------------------------------------------
CObjectVisualization::CObjectVisualization()
{
}

//---------------------------------------------------------------------------------------------------------------------
CObjectVisualization::CObjectVisualization(const std::string& p_rsClassName, const std::string& p_rsModelName)
{
	m_sClassName = p_rsClassName;
}

//---------------------------------------------------------------------------------------------------------------------
CObjectVisualization::~CObjectVisualization()
{
	for(unsigned int i=0; i<m_apxVariations.size(); ++i)
	{
		delete m_apxVariations[i];
	}
}

//---------------------------------------------------------------------------------------------------------------------
void	
CObjectVisualization::FromXMLElement(TiXmlElement* p_pxObjectNode, std::string& p_rsWarnings)
{
	if(string(p_pxObjectNode->Value()) == "unknownobject")
	{
		m_sClassName = "unknownobject";
	}
	
	m_sClassName = GetXMLTagString(p_pxObjectNode, "classname", string());
	if(m_sClassName.empty())
	{
		p_rsWarnings += "Error: object tag without classname found!\n";
	}

	TiXmlElement* pxVariation = p_pxObjectNode->FirstChildElement("variation");
	if(!pxVariation)
	{
		p_rsWarnings += "Error: object tag " + m_sClassName + " has no variation tags!\n";
	}

	while(pxVariation)
	{
		CObjectVariation* pxVar = AddVariation();
		pxVar->FromXMLElement(pxVariation, p_rsWarnings);

		pxVariation = pxVariation->NextSiblingElement("variation");
	}
}

//---------------------------------------------------------------------------------------------------------------------
CObjectVariation*
CObjectVisualization::GetVariation(int p_iVariation) const
{
	return m_apxVariations[p_iVariation];
}

//---------------------------------------------------------------------------------------------------------------------
int
CObjectVisualization::NumVariations() const
{
	return (int) m_apxVariations.size();
}

//---------------------------------------------------------------------------------------------------------------------
CObjectVariation*	
CObjectVisualization::AddVariation()
{
	CObjectVariation* v = new CObjectVariation(this, (int) m_apxVariations.size());
	m_apxVariations.push_back(v);
	return v;
}
//---------------------------------------------------------------------------------------------------------------------

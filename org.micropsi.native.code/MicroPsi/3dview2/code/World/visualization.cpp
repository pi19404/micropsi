#include "Application/stdinc.h"
#include "World/visualization.h"

#include "tinyxml.h"
#include "windows.h"

#include "baselib/str.h"
#include "baselib/xmlutils.h"

using std::string;
using std::map;
using namespace XMLUtils;

//---------------------------------------------------------------------------------------------------------------------
CVisualization::CVisualization()
{
	Clear();
}

//---------------------------------------------------------------------------------------------------------------------
CVisualization::~CVisualization()
{
	if(m_mpxObjects.size() > 0)
	{
		map<std::string, CObjectVisualization*>::iterator i;
		for(i=m_mpxObjects.begin(); i!=m_mpxObjects.end(); i++)
		{
			delete i->second;
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CVisualization::Clear()
{
	m_axTerrainMaterials.clear();

	if(m_mpxObjects.size() > 0)
	{
		map<std::string, CObjectVisualization*>::iterator i;
		for(i=m_mpxObjects.begin(); i!=m_mpxObjects.end(); i++)
		{
			delete i->second;
		}
	}

	m_mpxObjects.clear();
	m_asObjectTypes.clear();
	m_fTextureTileSize = 1.0f;


	CObjectVisualization* pxUnknown = new CObjectVisualization();
	m_mpxObjects["unknownobject"] = pxUnknown;
	pxUnknown->m_sClassName = "unknownobject";
	CObjectVariation* pxVar = pxUnknown->AddVariation();
	pxVar->m_axSortedLODLevels.push_back(new CObjectLODLevel());
	pxVar->m_axSortedLODLevels[0]->SetModel("unknownobject.x");
}

//---------------------------------------------------------------------------------------------------------------------
/**
*/
void 
CVisualization::FromXML(const char* p_pcXMLFilename)
{
	string sWarnings;

	TiXmlDocument*	pxDoc = new TiXmlDocument(p_pcXMLFilename);
	if(pxDoc->LoadFile()) 
	{ 
		TiXmlElement* pxRootNode = pxDoc->FirstChildElement("visualization");
		if(pxRootNode) 
		{
			Clear();

			TiXmlElement* pxNode = pxRootNode->FirstChildElement();
			while(pxNode)
			{
				string sTag = pxNode->Value(); 
				if(sTag == "objects")
				{
					ReadObjects(pxNode, sWarnings);
				}
				else if(sTag == "terrain")
				{
					TiXmlElement* pxMapNode = pxNode->FirstChildElement("mapdefaults");
					if(pxMapNode)
					{
						m_xDefaultMapParameters.FromXMLElement(pxMapNode);
					}
					TiXmlElement* pxMaterialsNode = pxNode->FirstChildElement("materials");
					if(pxMaterialsNode)
					{
						ReadTerrainMaterials(pxMaterialsNode, sWarnings);
					}
					else
					{
						sWarnings += "Warning: terrain material definitions are missing!";
					}

					m_fTextureTileSize = GetXMLTagFloat(pxNode, "texturetilesize", m_fTextureTileSize);
				}
				else if(sTag == "skybox")
				{
					m_sSkyBoxFile = GetXMLTagString(pxRootNode, "skybox");
				}
				else if(sTag == "description")
				{
					m_sDescription = GetXMLTagString(pxRootNode, "description");
				}
				else
				{
					sWarnings += "Warning: unrecognized tag '" + sTag + "' in visualization xml\n";
				}

				pxNode = pxNode->NextSiblingElement();
			}
		}
	}
	else
	{
		MessageBox(NULL, (string("failed to load visualization file '") + p_pcXMLFilename + "' \n\nReason: in line " + 
			CStr::Create("%d", pxDoc->ErrorRow()).c_str() + ": " + pxDoc->ErrorDesc()).c_str(), "Error", MB_OK);
	}

	if(sWarnings.length() != 0)
	{
		MessageBox(NULL, (string("There are Warnings during the parsing of visualization file ") + p_pcXMLFilename + "\n" + 
			sWarnings).c_str(), "Error", MB_OK);
	}

	delete pxDoc;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CVisualization::ReadObjects(TiXmlElement* p_pxObjectsNode, std::string& p_rsWarnings)
{
	TiXmlElement* pxNode = p_pxObjectsNode->FirstChildElement();
	while(pxNode)
	{
		string sTag = pxNode->Value(); 
		if(sTag == "object"  ||  sTag == "unknownobject")
		{
			ReadObjectVisualization(pxNode, p_rsWarnings);
		}
		else
		{
			p_rsWarnings += "Warning: unrecognized tag '" + sTag + "' in visualization xml\n";
		}

		pxNode = pxNode->NextSiblingElement();
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CVisualization::ReadObjectVisualization(TiXmlElement* p_pxObjectNode, string& p_rsWarnings)
{
	CObjectVisualization* pxVis = new CObjectVisualization();
	pxVis->FromXMLElement(p_pxObjectNode, p_rsWarnings);

	if(pxVis->m_sClassName.length() > 0)
	{
		map<std::string, CObjectVisualization*>::iterator i;
		i = m_mpxObjects.find(pxVis->m_sClassName);
		if(pxVis->m_sClassName == "unknownobject"  ||  m_mpxObjects.size() == 0  ||  i == m_mpxObjects.end())
		{
			m_mpxObjects[pxVis->m_sClassName] = pxVis;

			// TODO: müssen objekttypen in visualisierung definiert werden??? ist wohl eher der falsche ort...
			if(pxVis->m_sClassName != "unknownobject")
			{
				m_asObjectTypes.push_back(pxVis->m_sClassName);
			}
		}
		else
		{
			p_rsWarnings += "Warning: object '" + pxVis->m_sClassName + "' double definition - ignoring second definition\n";
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
const CObjectVariation* 
CVisualization::GetObjectVisualization(const char* p_pcClassName, int p_iVariationNumber) const
{
	assert(m_mpxObjects.size() != 0);

	map<string, CObjectVisualization*>::const_iterator i;
	i = m_mpxObjects.find(p_pcClassName);
	if(i == m_mpxObjects.end())
	{
		DebugPrint("Warning: no visualization defined for class %s", p_pcClassName);
		i = m_mpxObjects.find("unknownobject");
		assert(i != m_mpxObjects.end());
	}

	const CObjectVisualization* pxObjVis = i->second;
	if(p_iVariationNumber >= (int) pxObjVis->NumVariations()  ||  p_iVariationNumber < 0)
	{
		if(pxObjVis->NumVariations() == 0)
		{
			assert(false);		// must have been a malformed xml file. should already have caused an error during xml parsing...
			return 0;
		}
		
		int i = rand() % pxObjVis->NumVariations();
		return pxObjVis->GetVariation(i);
	}
	else
	{
		return pxObjVis->GetVariation(p_iVariationNumber);
	}
}

//---------------------------------------------------------------------------------------------------------------------
int	
CVisualization::GetNumObjectVariations(const char* p_pcClassName) const
{
	assert(m_mpxObjects.size() != 0);

	map<string, CObjectVisualization*>::const_iterator i;
	i = m_mpxObjects.find(p_pcClassName);
	if(i == m_mpxObjects.end())
	{
		DebugPrint("Warning: no visualization defined for class %s", p_pcClassName);
		i = m_mpxObjects.find("unknownobject");
		assert(i != m_mpxObjects.end());
	}
	return i->second->NumVariations();
}

//---------------------------------------------------------------------------------------------------------------------
string
CVisualization::GetDescriptionFromXML(const char* p_pcXMLFilename)
{
	string sRet = "";

	TiXmlDocument*	pxDoc = new TiXmlDocument(p_pcXMLFilename);
	if(pxDoc->LoadFile()) 
	{ 
		TiXmlNode* pxRootNode = pxDoc->FirstChild("visualization");
		if(pxRootNode) 
		{
			sRet = ::GetXMLTagString(pxRootNode->ToElement(), "description");
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
CVisualization::ReadTerrainMaterials(TiXmlElement* p_pxMaterialsNode, std::string& p_rsWarnings)
{
	TiXmlElement* pxNode = p_pxMaterialsNode->FirstChildElement("material");
	while(pxNode)
	{
		CMaterial s;
		s.m_sName = GetXMLTagString(pxNode, "name");
		s.m_sTextureName = GetXMLTagString(pxNode, "texture");
		m_axTerrainMaterials.push_back(s);

		pxNode = pxNode->NextSiblingElement("material");
	}
}

//---------------------------------------------------------------------------------------------------------------------
int	
CVisualization::GetNumberOfObjectTypes() const
{
	return (int) m_asObjectTypes.size();
}

//---------------------------------------------------------------------------------------------------------------------
std::string	
CVisualization::GetObjectTypeName(int p_iIndex) const
{
	assert(p_iIndex >= 0  &&  p_iIndex < (int) m_asObjectTypes.size());
	return m_asObjectTypes[p_iIndex];
}

//---------------------------------------------------------------------------------------------------------------------
float
CVisualization::GetTextureTileSize() const
{
	return m_fTextureTileSize;
}
//---------------------------------------------------------------------------------------------------------------------

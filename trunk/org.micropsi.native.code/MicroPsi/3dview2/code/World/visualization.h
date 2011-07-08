#pragma once
#ifndef VISUALIZATION_H_INCLUDED
#define VISUALIZATION_H_INCLUDED

#include <map>
#include <vector>
#include <string>

#include "world/objectvisualization.h"
#include "world/mapparameters.h"

class TiXmlElement;

class CVisualization
{
public: 

	CVisualization();
	~CVisualization();

	void						Clear();
	void						FromXML(const char* p_pcXMLFilename);
	const CObjectVariation*		GetObjectVisualization(const char* p_pcClassName, int p_iVariationNumber = -1) const;
	int							GetNumObjectVariations(const char* p_pcClassName) const;

	int							GetNumberOfObjectTypes() const;
	std::string					GetObjectTypeName(int p_iIndex) const;

	static std::string			GetDescriptionFromXML(const char* p_pcXMLFilename);

	class CMaterial
	{
	public:
		std::string		m_sName;				///< terrain material name
		std::string		m_sTextureName;			///< terrain texture file name
	};

	int							NumTerrainMaterials() const;
	const	CMaterial&			GetTerrainMaterial(int p_iMaterial) const;
	const   CMapParameters&		GetDefaultMapParameters() const;
	const   std::string&		GetSkyBoxFileName() const;
	const	std::string&		GetDescription() const;
	float						GetTextureTileSize() const;

private:

	void	ReadObjects(TiXmlElement* p_pxObjectsNode, std::string& p_rsWarnings);
	void	ReadObjectVisualization(TiXmlElement* p_pxObjectNode, std::string& p_rsWarnings);
	void	ReadTerrainMaterials(TiXmlElement* p_pxMaterialsNode, std::string& p_rsWarnings);

	std::map<std::string, CObjectVisualization*>	m_mpxObjects;			///< object visualizations
	std::vector<std::string>						m_asObjectTypes;		///< list of all object types
	std::vector<CMaterial>							m_axTerrainMaterials;	///< terrain materials

	std::string					m_sDescription;
	std::string					m_sSkyBoxFile;
	CMapParameters				m_xDefaultMapParameters;
	float						m_fTextureTileSize;
};

#include "visualization.inl"

#endif // VISUALIZATION_H_INCLUDED

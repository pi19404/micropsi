#pragma once
#ifndef MAPPARAMETERS_H_INCLUDED
#define MAPPARAMETERS_H_INCLUDED

#include <string>
#include "baselib/geometry/CVector.h"

class TiXmlElement;

class CMapParameters
{
public: 
	std::string			m_sHeightMap;				///< file name of the height map file
	std::string			m_sMaterialMap;				///< file name of the material map file
	CVec3				m_vOffset;					///< position of the lower left corner of the height in world space
	CVec3				m_vScaling;					///< scaling of the height map (1 pixel equals how many units?)
	CVec3				m_vAbsoluteSize;			///< absolute size of map - overrides scaling (unless negative)
	CVec3				m_vObserverStartPos;		///< start position of the observer in world space
	CVec3				m_vObserverLookAt;			///< initial look at point for the observer in world space
	bool				m_bWrapAround;				///< true: terrain wraps around (no boundaries; observer moves on a torus)

	CMapParameters();

	void	FromXMLElement(const TiXmlElement* p_pXmlElement);
	void	ToXMLElement(TiXmlElement* p_pXmlElement) const;
};


#endif // MAPPARAMETERS_H_INCLUDED

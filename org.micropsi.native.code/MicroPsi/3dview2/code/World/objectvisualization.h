#pragma once
#ifndef OBJECTVISUALIZATION_H_INCLUDED
#define OBJECTVISUALIZATION_H_INCLUDED

#include <string>
#include <vector>
#include "baselib/geometry/CVector.h"
#include "e42/core/Model.h"

class TiXmlElement;
class COpcodeMesh;

class CObjectLODLevel
{
public:
	CObjectLODLevel();
	~CObjectLODLevel();

	void FromXMLElement(TiXmlElement* p_pxObjectNode, std::string& p_rsWarnings);
	void SetModel(const std::string& p_rsModelFile);
	void UpdateCollisionGeometry();
	bool operator<(const CObjectLODLevel& p_rxOther) const; 

	static bool Pred(CObjectLODLevel* pxA, CObjectLODLevel* pxB)
	{
		return *pxA < *pxB;
	}

	std::string				m_sModelName;						///< model file name
	TModelHandle			m_hModel;							///< handle to model
	COpcodeMesh*			m_pxCollisionModel;					///< pointer to (OPCODE) collision geometry
	float					m_fMaxDistanceSquare;				///< maximum distance where object is still visible

private:
	// not implemented
	CObjectLODLevel(const CObjectLODLevel&); 
	CObjectLODLevel& operator=(const CObjectLODLevel&);
};


class CObjectVisualization;

class CObjectVariation
{
public:
	CObjectVariation(CObjectVisualization* p_pxObjectVisualization, int p_iVariationNumber);
	~CObjectVariation();

	void FromXMLElement(TiXmlElement* p_pxObjectNode, std::string& p_rsWarnings);

	/// points where an object touches the ground
	class CGroundContactPoints
	{
	public:
		CVec3		m_vPoints[3];
	};

	void SetGroundContactPoints(const CGroundContactPoints& p_rxPoints);
	void SortLODLevels();

	CObjectVisualization*			m_pxObjectVisualization;
	CGroundContactPoints*			m_pxGroundContactPoints;		///< pointer to ground contact points; 0 if not used
	bool							m_bInterpolatedMovement;		///< true if this object wants its movement to be interpolated
	std::vector<CObjectLODLevel*>	m_axSortedLODLevels;	
	int								m_iVariationNumber;

private:
	CObjectVariation(const CObjectVariation& p_rxOther);
	const CObjectVariation&	operator=  (const CObjectVariation& p_kxrObj); 
};


/// stores visualization data for an object type
class CObjectVisualization
{
public:
	CObjectVisualization();
	CObjectVisualization(const std::string& p_rsClassName, const std::string& p_rsModelName);
	~CObjectVisualization();

	void				FromXMLElement(TiXmlElement* p_pxObjectNode, std::string& p_rsWarnings);
	CObjectVariation*	GetVariation(int p_iVariation) const;
	int					NumVariations() const;
	CObjectVariation*	AddVariation();

	std::string							m_sClassName;				///< class name

private:
	std::vector<CObjectVariation*>		m_apxVariations;			///< variations of this object	

	CObjectVisualization(const CObjectVisualization& p_rxOther);
	const CObjectVisualization&	operator=  (const CObjectVisualization& p_kxrObj); 
};


#endif // OBJECTVISUALIZATION_H_INCLUDED

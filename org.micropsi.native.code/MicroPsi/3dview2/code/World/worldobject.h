#pragma once
#ifndef WORLDOBJECT_H_INCLUDED
#define WORLDOBJECT_H_INCLUDED

#include <string>

#include "e42/core/Model.h"
#include "baselib/geometry/CVector.h"
#include "baselib/geometry/BoundingSphere.h"

#include "World/objectvisualization.h"

class TiXmlElement;
class CWorld;
class CAnimationCtrl;
class COpcodeMesh;


class CWorldObject
{
public:

	enum 
	{
		INVALID_OBJID		= -1	
	};
	
	CWorldObject(	CWorld* p_pxWorld);
	CWorldObject(	CWorld* p_pxWorld, 
					const char* p_pcClassName, 
					float p_fX, float p_fY, float p_fZ, 
					float p_fHeight, float p_fOrientationAngle = 0.0f, __int64 p_iID = INVALID_OBJID, int p_iVariation = -1);
	virtual ~CWorldObject();

	__int64					GetID() const;
	const std::string&		GetClass() const;
	const std::string&		GetObjectName() const;

	CVec3					GetPSIPos() const;
	CVec3					GetEnginePos() const;
	float					GetHeight() const;
	float					GetPSIOrientationAngle() const;
	bool					GetVisible() const;
	const CMat4S&			GetWorldTransformation() const;

	void					SetPSIPos(float p_fX, float p_fY, float p_fZ);
	void					SetPSIPos(CVec3& p_vrPos);
	void					SetPSIPos(CVec3& p_vrPos, float p_fOrientationAngle);
	void					SetPSIOrientationAngle(float p_fAngle);
	void					SetObjectName(std::string p_sName);

	void					MoveTo(float p_fX, float p_fY, float p_fZ, float p_fSpeed);

	const CBoundingSphere&	GetModelBoundingSphere() const;
	CBoundingSphere			GetBoundingSphere() const;
	TModelHandle			GetModel() const;
	COpcodeMesh*			GetCollisionModel() const;

	bool					UpdateLODModel(const CVec3& p_rvCameraPos);
	virtual	bool			Tick(); 
	void					Render(TRenderContextPtr spxRenderContext);

	virtual void			FromXMLElement(const TiXmlElement* p_pXmlElement);
	virtual void			ToXMLElement(TiXmlElement* p_pXmlElement) const;

	CWorld*					GetWorld() const;

	bool					operator== (const CWorldObject& p_kxrObj) const;
	bool					operator!= (const CWorldObject& p_kxrObj) const; 
	bool					operator<  (const CWorldObject& p_kxrObj) const;
	bool					operator>  (const CWorldObject& p_kxrObj) const; 
	bool					operator<= (const CWorldObject& p_kxrObj) const;
	bool					operator>= (const CWorldObject& p_kxrObj) const; 

private: 

	void			UpdateMatrix();
	void			UpdateQuadTree(const CVec3& p_vOldPos);
	bool			InterpolateMovement();

	TModelHandle	m_hModel;					///< handle to model

	CWorld*			m_pxWorld;					///< this objects world
	CAnimationCtrl* m_pAnimationCtrl;			///< animation controller

	std::string		m_sObjectName;				///< object name; same as on server

	CVec3			m_vPSIPos;					///< current object position in microPSI
	__int64			m_iID;						///< object id, same as on server
	float			m_fPSIOrientationAngle;		///< orientation angle in the xz-plane
	float			m_fHeight;					///< height of object
	float			m_fScale;					///< scaling of object
	bool			m_bVisible;					///< object visible / invisible
	int				m_iCurrentLOD;				///< current LOD level

	bool			m_bMoving;					///< moving at the moment?
	CVec3			m_vTargetPos;				///< target position of movement
	CVec3			m_vOriginalPos;				///< Original position of movement
	double			m_dStartTime;				///< start time of movement
	float			m_fSpeed;					///< movement speed

	CMat4S			m_mTransform;				///< Transformation in Engine Space

	const CObjectVariation*	m_pxObjTypeData;	///< additional data for this object type

	const CWorldObject&		operator=  (const CWorldObject& p_kxrObj); 
	CWorldObject(const CWorldObject& p_kxrObj);
};

#include "worldobject.inl"

#endif // WORLDOBJECT_H_INCLUDED

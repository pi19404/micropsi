#pragma once
#ifndef GAMELIB_GAMEOBJECT_H_INCLUDED
#define GAMELIB_GAMEOBJECT_H_INCLUDED

#include "e42/core/ResourceHandles.h"
#include "e42/core/RenderContext.h"
#include "baselib/geometry/CVector.h"
#include "baselib/geometry/Matrix.h"
#include "baselib/geometry/CollisionObjects.h"

#include "GameLib/World/GameObjectID.h"
#include "GameLib/World/GameObjectClassManager.h"

class CModel;
class CMeshCombiner;
class CAnimationCtrl;

class CGameObj
{
public:
    
    CMat4S						CalcTransform() const;

    // position, rotation
    virtual void				SetPos(const CVec3& vNewPos);
    const						CVec3& GetPos() const;

    virtual void				SetRot(const CMat3S& mNewRot);
    const						CMat3S GetRot() const;

    void						SetOrientation(const CVec3& vOrientation);

    // management
    GameObjID					GetObjID() const;
    void						SetObjID(GameObjID id);		//FIXME: wieso denn sowas? ist das nicht gefährlich?

	const std::string&			GetClassName() const;
	bool						IsOfType(const std::string& p_rsClassName);
	bool						IsOfExactType(const std::string& p_rsClassName);

	void						SetModel(const std::string& sModelFile);

    // basics
    virtual void				Render(CMeshCombiner* pMeshCombiner, TRenderContextPtr spxRenderContext);
    virtual void				DoGameplayStep() {};

	void						SetVisible(bool p_bVisible);
	bool						GetIsVisible() const;

	void						SetCollision(bool p_bCollision);
	bool						GetHasCollision() const;

	void						SetTouchable(bool p_bTouchable);
	bool						GetIsTouchable() const;

    virtual bool				GetFrameTransform(CMat4S* pmOutTransform, const std::string& sFrameName, float p_fDiscreteGameTime = 0) const;

	/// liefert Kollisionsdaten für das Constraintsystem, d.h. man kann durch dieses Volumen nicht hindurchlaufen
    virtual bool				GetCollisionData(CVec3* pvOutCenter, float* pfOutHeight, float* pfOutXZRadius) const;

	/// liefert Kollisionsdaten für Berührungen (touch)
	const CCollisionObject*		GetCollisionObject();

	/// liefert die Bounding Sphere des Objektes; wird zum Culling beim Rendern und Suchen benutzt
	virtual CBoundingSphere		GetBoundingSphere() const;

    static CGameObj* __cdecl	Create(const CGameObjClassMgr::CClassInfo* p_pxClassInfo);
    static void __cdecl			Destroy(CGameObj* pGameObj);

	bool						GetPropertyValueBool(const std::string p_rsPropertyName) const;
	int							GetPropertyValueInt(const std::string p_rsPropertyName) const;
	std::string					GetPropertyValueString(const std::string p_rsPropertyName) const;

protected:
	
    CGameObj(const CGameObjClassMgr::CClassInfo* p_pxClassInfo);
    virtual ~CGameObj();

	/// Factory-Methode; erstellt Kollisionsobjekt
	virtual CCollisionObject*	CreateCollisionObject();

	/// Analogon zur Factory-Methode; erstört das Kollisionsobjekt
	virtual void				DeleteCollisionObject(CCollisionObject* po_pxCollisionObject);

	/// in dieser Methode sollte das Kollisionsobject aktualisiert werden
	virtual void				UpdateCollisionObject(CCollisionObject* po_pxCollisionObject);

	void						LoadAnimations(const std::string& p_srXMLFile);

	/// liefert Informationen über meine Klasse
	const CGameObjClassMgr::CClassInfo*	GetClassInfo();

	TModelHandle        m_hndModel;
	CAnimationCtrl*     m_pAnimationCtrl;

	struct 
	{
		unsigned	m_bVisible		: 1;			///< Objekt wird gerendert
		unsigned	m_bCollision	: 1;			///< Objekt hat "physikalische" Kollision, d.h. man kann nicht durchlaufen
		unsigned	m_bTouchable	: 1;			///< Objekt kann berührt werden (löst entsprechenden Callback aus)
	};

    CMat3S              m_mRot;							///< Rotation 
    CVec3               m_vPos;							///< Position

private:

    GameObjID           m_ObjID;						///< object id

	CCollisionObject*	m_pxCollisionObject;			///< mein Kollisionsobjekt

	const CGameObjClassMgr::CClassInfo*	m_pxClassInfo;			///< Informationen über meine Klasse
	std::vector<CGameObjProperty::CValue*> m_axPropertyValues;	///< Werte meiner Properties	

	friend class CGameObjClassMgr;
};

#include "GameObject.inl"

#endif // GAMELIB_GAMEOBJECT_H_INCLUDED

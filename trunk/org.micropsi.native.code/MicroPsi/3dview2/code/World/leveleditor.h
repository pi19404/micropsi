#pragma once

#ifndef LEVELEDITOR_H_INCLUDED
#define LEVELEDITOR_H_INCLUDED

#include "Application/stdinc.h"

#include "baselib/geometry/CVector.h"
#include "baselib/geometry/Ray.h"

#include <string>

class CWorld;
class CWorldObject;

class CLevelEditor
{
public:
    CLevelEditor(CWorld* p_pxWorld);
    ~CLevelEditor();

	enum ClickMode 
	{
		CM_Select,
		CM_CreateObject
	};


	void			ClearAllObjects();
	bool			SaveWorld(); 
	bool			SaveWorldAs(const std::string& p_rsFilename); 
	std::string		GetCurrentWorldFileName() const;

	void			SetRenderingEnabled(bool p_bRender);

	void			SetClickMode(ClickMode p_eMode);
	ClickMode		GetClickMode() const;

	void			SetObjectTypeToCreate(const std::string& p_rsType);
	void			SetVariationToCreate(int p_iVariation);
	void			SetRandomRotationForNewObjects(bool p_bRandomRotation);

	void			Unselect();

	int				NumSelectedObjects() const;

	void			OnClick(float p_fClickXPos, float p_fClickYPos);
	void			DeleteSelectedObjects();

	void			Tick();
	void			Render();

private:

	CRay			GetMouseRay(float p_fClickXPos, float p_fClickYPos) const;

	CWorld*					m_pxWorld;

	bool					m_bRenderingEnabled;			///< schaltet das Rendering (von Selektion u.ä.) ein oder aus

	ClickMode				m_eClickMode;					///< was soll beim nächsten Click passieren?
	std::string				m_sObjectTypeToCreate;			///< GameObjectType, der bei Click erzeugt werden soll
	int						m_iObjectVariationToCreate;

	CWorldObject*			m_pxSelectedObject;				///< aktuell angewähltes Objekt, kann 0 sein

	bool					m_bRandomRotationForNewObjects;	///< sollen vom Editor neu erzeugte Objeke eine zufällige Drehung bekommen?
};

#include "leveleditor.inl"

#endif // LEVELEDITOR_H_INCLUDED

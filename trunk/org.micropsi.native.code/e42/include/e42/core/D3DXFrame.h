/*******************************************************************************
Frame.h - DirectX bietet zur Repräsentation von Szenengraphen 
	Meshcontainer und Frames an, möchte man zusätzliche Informationen in 
	diesen Datenstrukturen speichern, muss man von ihnen ableiten. Das 
	geschieht hier. Damit die Allokation der Strukturen noch korrekt 
	funktioniert, wird die Klasse CSceneGraphAllocator fürs Laden übergeben
	(siehe SceneGraphAllocator.h)
	Zukünftige Erweiterungen für Frames werden falls nötig 
	in dieser Datei vorgenommen.
*******************************************************************************/
#pragma once

#ifndef E42_D3DXFRAME_H_INCLUDED
#define E42_D3DXFRAME_H_INCLUDED

#include "e42/stdinc.h"

#include <d3dx9mesh.h>
#include <d3dx9anim.h>
#include "baselib/geometry/CVector.h"
#include "baselib/geometry/Matrix.h"
#include "baselib/geometry/BoundingSphere.h"

#include "e42/core/D3DXMeshContainer.h"

class CD3DXFrame : private D3DXFRAME
{
/*	// Member der Basisklasse D3DXFRAME:
	LPSTR					Name;						///< Name des Frames
	D3DXMATRIX				TransformationMatrix;		///< Transformationsmatrix des Frames

	LPD3DXMESHCONTAINER		pMeshContainer;				///< Pointer auf den ersten MeshContainer des Frames (kein Array!)

	struct _D3DXFRAME		*pFrameSibling;				///< Pointer auf nächsten Frame in derselben Hierarchieebene
	struct _D3DXFRAME		*pFrameFirstChild;			///< Pointer auf den ersten ChildFrame (kein Array! -> sibling-frames)
*/
private:
	// dont!:
	CD3DXFrame(const CD3DXFrame&);
	CD3DXFrame(const D3DXFRAME&);
	CD3DXFrame& operator=(const CD3DXFrame&);
	CD3DXFrame& operator=(const D3DXFRAME&);


protected:

	CD3DXFrame();
	~CD3DXFrame();


public:

	CMat4S					matCombinedTransformation;	///< kumulierte Transformationsmatrix in der Hierarchie (wird zB. für Skinning benötigt, wenn FrameHierarchie das Skeletton ist)

	CD3DXFrame*				pFrameParent;				///< Pointer zum ParentFrame
	CD3DXFrame*				pVisibilitySwitchFrame;		///< Pointer zum ChildFrame mit dem Namen "visibility"; kann zum Animieren der Visibility verwendet werden (NULL, wenn der Frame nicht existiert)

	CBoundingSphere			xBoundingSphere;			///< BoundingShpere der FrameHierarchie

	bool					bIsVisible;					///< zum Cullen bestimmter Frames (+Childs)


	void					SetName(const char* pcName);
	const char*				GetName() const;


	CMat4S&					TransformationMatrix();
	const CMat4S&			TransformationMatrix() const;


	CD3DXMeshContainer*		GetFirstMeshContainer() const;
	void					SetFirstMeshContainer(CD3DXMeshContainer* pxMeshContainer);
	CD3DXMeshContainer*		FindMeshContainer(int iIdx) const;

	CD3DXFrame*				GetFirstChild() const;
	CD3DXFrame*				GetSibling() const;
	CD3DXFrame*				FindChild(int iIdx) const;
	int						FindChildIdx(const CD3DXFrame* pxFrame) const;


	void					AddChild(CD3DXFrame* pxFrame);
	void					RemoveChild(CD3DXFrame* pxFrame);

	void					AddSibling(CD3DXFrame* pxFrame);
	void					RemoveSibling(CD3DXFrame* pxFrame);

	void					AddMeshContainer(CD3DXMeshContainer* pxMeshContainer);
	void					RemoveMeshContainer(CD3DXMeshContainer* pxMeshContainer);


	D3DXFRAME&				D3DXFrame();
	const D3DXFRAME&		D3DXFrame() const;


	void					UpdateChildFrameParentPointers();
	void					UpdateChildMeshParentPointers();

	CD3DXFrame*				CloneFrameHierarchy() const;

	static CD3DXFrame*		Create();
	static void				Destroy(CD3DXFrame* pxFrame);
	static void				RecursiveDestroy(CD3DXFrame* pxFrame);
};

#include "D3DXFrame.inl"

#endif // E42_D3DXFRAME_H_INCLUDED

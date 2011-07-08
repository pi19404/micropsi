/*******************************************************************************
MeshContainer.h - DirectX bietet zur Repräsentation von 
	Szenengraphen Meshcontainer und Frames an, möchte man zusätzliche 
	Informationen in diesen Datenstrukturen speichern, muss man von ihnen 
	ableiten. Das geschieht hier. Damit die Allokation der Strukturen noch 
	korrekt funktioniert, wird die Klasse CSceneGraphAllocator fürs Laden 
	übergeben (siehe AllocateHierarchy.h)
	Zukünftige Erweiterungen für Meshcontainer werden falls nötig 
	in dieser Datei vorgenommen.
*******************************************************************************/
#pragma once

#ifndef E42_D3DXMESHCONTAINER_H_INCLUDED
#define E42_D3DXMESHCONTAINER_H_INCLUDED

#include "e42/stdinc.h"

#include <d3dx9mesh.h>
#include <d3dx9anim.h>

#include "e42/core/ResourceHandles.h"
#include "e42/core/material.h"
#include "baselib/dynarray.h"
#include "baselib/comobjectptr.h"
#include "baselib/geometry/BoundingSphere.h"

class CMaterial;
class CD3DXMaterial;
class CD3DXEffectInstance;
class CD3DVertexDeclaration;

class CD3DXMeshContainer : private D3DXMESHCONTAINER
{
	friend class CD3DXFrame;
/*  // Member der Basisklasse D3DXMESHCONTAINER:
	LPSTR					Name;                               ///< Name des MeshContainers

	D3DXMESHDATA			MeshData;                           ///< enthält Pointer auf ID3DXMesh-Interface

	LPD3DXMATERIAL			pMaterials;                         ///< Array mit Materialien (Count = NumMaterials)
	LPD3DXEFFECTINSTANCE	pEffects;                           ///< Effect und seine Default-Parameter
	DWORD					NumMaterials;                       ///< Anzahl der vorkommenden Materialien
	DWORD*					pAdjacency;                         ///< Information über benachbarte Dreiecke

	LPD3DXSKININFO			pSkinInfo;                          ///< ID3DXSkinInfo-Interface für Meshes, die Skinning verwenden

	struct _D3DXMESHCONTAINER *pNextMeshContainer;              ///< Referenz zum nächsten MeshContainer in der Hierarchie (Sibling)
*/
private:
	// dont!:
	CD3DXMeshContainer(const CD3DXMeshContainer&);
	CD3DXMeshContainer(const D3DXMESHCONTAINER&);
	CD3DXMeshContainer& operator=(const CD3DXMeshContainer&);
	CD3DXMeshContainer& operator=(const D3DXMESHCONTAINER&);

protected:
	CD3DXMeshContainer();
	~CD3DXMeshContainer();

	TMeshHandle					hndMesh;

public:

	DWORD						dwMaxBoneInfluencesPerVertex;	///< Anzahl der gleichzeitig verwendeten Bones
	DWORD						dwNumBoneCombinations;			///< Anzahl der Gruppen, in die das Mesh wegen den Bones geteilt wurde (-> max. Anzahl von BoneMatrices im Shader)
	CComObjectPtr<ID3DXBuffer>	spxBoneCombinationBuffer;		///< Array mit den Bone-Gruppen (MatrixPaletten)
	DWORD						dwNumPaletteEntries;			///< Anzahl der BoneMatrices, die in maximal pro Gruppe verwendet werden

	CMat4S**					ppBoneMatrices;					///< Pointer zu den Matrizen im Skeleton (CD3DXFrame::matCombinedTransformation)


	CMaterial*					pxMaterials;					///< erweiterte MaterialInformationen mit ResourceHandles (Array hat die Größe NumMaterials)

	bool						bUsesSoftwareVertexProcessing;	///< TRUE, wenn SoftwareVertexProcessing für dieses Mesh verwendet werden muss FIXME: warum muss das der MeshContainer bestimmen?

	bool						bIsVisible;						///< MeshContainer ist sichtbar oder unsichtbar (TODO: DWORD dwFlags);

	CD3DXFrame*					pFrameParent;					///< Pointer zum ParentFrame


	void								SetName(const char* pcName);		///< setzt den Namen des Meshes
	const char*							GetName() const;					///< liefert den Namen zurück


	// Acceessor-Funktionen
	CD3DXMeshContainer*&				NextMeshContainer();				///< Zugriff auf pNextMeshContainer der Basisklasse
	CD3DXMeshContainer* const&			NextMeshContainer() const;			///< Zugriff auf pNextMeshContainer der Basisklasse (const)

	void								SetMesh(const TMeshHandle& hndMesh);///< setzt das Mesh
	const TMeshHandle&					GetMesh() const;					///< Zugriff auf das Mesh

	bool								UsesSkinning() const;				///< gibt zurück, ob der MeshContainer mit Skinning arbeitet
	CComObjectPtr<ID3DXSkinInfo>&		SkinInfo();							///< Zugriff auf pSkinInfo der Basisklasse
	const CComObjectPtr<ID3DXSkinInfo>&	SkinInfo() const;					///< Zugriff auf pSkinInfo der Basisklasse (const)

	DWORD*&								Adjacency();						///< Zugriff auf pAdjacency der Basisklasse
	DWORD* const&						Adjacency() const;					///< Zugriff auf pAdjacency der Basisklasse (const)

	void								SetMeshType(D3DXMESHDATATYPE type);	///< setzt den Typ des Meshes


	DWORD								GetNumAttributeGroups() const;		///< liefert die Anzahl der Attributgruppen des Meshes zurück
	
	int									CalcMeshRank() const;				///< berechnet eine Zahl, die für die statische Sortierung der Meshes verwendet werden kann
	
	
	void								CreateBoneMatrixPtrArray();			///< erzeugt das Array für die BoneMatrixPointer (ppBoneMatrices) (Größe wird aus SkinInfo gelesen)
	void								DeleteBoneMatrixPtrArray();			///< löscht das Array für die BoneMatrixPointer (ppBoneMatrices) 
	
	void								CreateAdjacencyArray();				///< erzeugt das Adjacency-Array (pAdjacency) (Größe wird aus Mesh gelesen)
	void								DeleteAdjacencyArray();				///< löscht das Adjacency-Array (pAdjacency) 


	void								SetNumMaterials(DWORD dwNumMaterials);
	DWORD								GetNumMaterials() const;			///< Zugriff auf NumMaterials der Basisklasse

	void								SetMaterials(DWORD dwNumMaterials, const CMaterial* pxMaterials = NULL);    ///< setzt die Materials
	CMaterial*							GetMaterials() const;				///< Zugriff auf das Material-Array (pMaterials der Basisklasse)

	void								SetD3DXMaterials(DWORD dwNumMaterials, const CD3DXMaterial* pxD3DXMaterials = NULL);   ///< setzt die D3DXMaterials
	CD3DXMaterial*						GetD3DXMaterials() const;			///< Zugriff auf das D3DXMaterial-Array (pMaterials der Basisklasse)

	void								SetD3DXEffectInstances(DWORD dwNumMaterials, const CD3DXEffectInstance* pxEffectInstances = NULL);///< Zugriff auf D3DXEffectInstances
	CD3DXEffectInstance*				GetD3DXEffectInstances() const;		///< Zugriff auf das D3DXEffectInstance-Array (pEffects der Basisklasse)


	void								GetVertexDeclaration(CD3DVertexDeclaration* pxVertexDeclarationOut) const;
	int									CalcVertexElementOffset(D3DDECLUSAGE eUsage, int iUsageIdx = 0) const;


	float								CalcVolume() const;
	CBoundingSphere						CalcBoundingSphere() const;


	D3DXMESHCONTAINER&			D3DXMeshContainer();
	const D3DXMESHCONTAINER&	D3DXMeshContainer() const;


	void						SetupBoneMatrixPointers(const CD3DXFrame* pxRootFrame);

	CD3DXMeshContainer*			CloneMeshContainerList() const;


	static CD3DXMeshContainer*	Create();
	static void					Destroy(CD3DXMeshContainer* pxMeshContainer);
	static void					RecursiveDestroy(CD3DXMeshContainer* pxMeshContainer);
};

#include "D3DXMeshContainer.inl"

#endif // MESHCONTAINER_H_INCLUDED
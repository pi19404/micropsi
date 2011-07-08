#pragma once

#ifndef E42_MESHFACTORY_H_INCLUDED
#define E42_MESHFACTORY_H_INCLUDED

#include "e42/stdinc.h"

#include "e42/core/ResourceHandles.h"
#include "e42/core/ResourceFactory.h"

#include "baselib/comobjectptr.h"
#include <d3dx9mesh.h>

class CEngineController;
struct ID3DXBuffer;

class CMeshFactory : public CResourceFactory
{
private:
	CEngineController*	m_pxEngineController;

	void				DestroyResource(void* pxResource);

	std::string			GetResourceID(const std::string& sFilename, DWORD dwMeshOptions) const;

public:
	CMeshFactory(CEngineController* pxEngineController);
	~CMeshFactory();


	/// lädt ein Mesh aus einem X-File (ohne Hierarchie)
	TMeshHandle		CreateMeshFromFile(
						std::string sFilename,
						DWORD dwMeshOptions = D3DXMESH_MANAGED,
						ID3DXBuffer** ppd3dMaterialBuffer = NULL,
						ID3DXBuffer** ppd3dEffectInstanceBuffer = NULL,
						DWORD* pdwNumMaterialsOut = NULL);

	/// erzeugt einen Effekt aus einem FX-File
	TMeshHandle		RegisterMesh(CComObjectPtr<ID3DXMesh> spxMesh, std::string sFilename = "");

	/// vorgefertigte Objekte
	TMeshHandle		CreateBox(float fWidth, float fHeight, float fDepth);
	TMeshHandle		CreateSphere(float fRadius, int iSlices, int iStacks);
	TMeshHandle		CreateCylinder(float fRadius1, float fRadius2, float fLength, int iSlices, int iStacks);
	TMeshHandle		CreateTorus(float fInnerRadius, float fOuterRadius, int iSides, int iRings);
	TMeshHandle		CreateTeapot();
};


#endif // E42_MESHFACTORY_H_INCLUDED
#include "stdafx.h"

#include "e42/core/MeshFactory.h"

#include "baselib/filelocator.h"

#include "e42/core/EngineController.h"

#include <d3dx9mesh.h>
#include <d3dx9shape.h>

using std::string;
//-----------------------------------------------------------------------------------------------------------------------------------------
CMeshFactory::CMeshFactory(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController)
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CMeshFactory::~CMeshFactory()
{
	__super::ReleaseOwnResources();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CMeshFactory::DestroyResource(void* pxResource)
{
	((ID3DXMesh*)pxResource)->Release();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
string
CMeshFactory::GetResourceID(const std::string& sFilename, DWORD dwMeshOptions) const
{
	char acExtension[32];
	sprintf(acExtension, "_0x%x", dwMeshOptions);

	return sFilename + acExtension;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TMeshHandle
CMeshFactory::CreateMeshFromFile(
					std::string sFilename, 
					DWORD dwMeshOptions,
					ID3DXBuffer** ppd3dMaterialBuffer,
					ID3DXBuffer** ppd3dEffectInstanceBuffer,
					DWORD* pdwNumMaterialsOut)
{
	string sFullFilename = m_pxEngineController->GetFileLocator()->GetPath(sFilename);

	string sResourceID = GetResourceID(sFullFilename, dwMeshOptions);
	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);


	if (pxResourceProxy == NULL)
	{
		CComObjectPtr<ID3DXMesh>    spxMesh;

		HRESULT hr = 
			D3DXLoadMeshFromX(
				sFullFilename.c_str(), dwMeshOptions,
				m_pxEngineController->GetDevice(),
				NULL,
				ppd3dMaterialBuffer,
				ppd3dEffectInstanceBuffer,
				pdwNumMaterialsOut,
				&spxMesh);
		        

		pxResourceProxy = __super::AddResource(sResourceID, spxMesh);
	}

	return TMeshHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TMeshHandle
CMeshFactory::RegisterMesh(CComObjectPtr<ID3DXMesh> spxMesh, string sFilename)
{
	string sResourceID = m_pxEngineController->GetFileLocator()->GetPath(sFilename);
	CResourceProxy* pxResourceProxy = __super::LookUpResource(sResourceID);

	if (pxResourceProxy == NULL)
	{
		if (spxMesh)
			spxMesh->AddRef(); // ResourceFactory hält nun eine weitere Ref.

		pxResourceProxy = __super::AddResource(sResourceID, spxMesh);
	}

	return TMeshHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TMeshHandle
CMeshFactory::CreateBox(float fWidth, float fHeight, float fDepth)
{
	char acResourceID[256];
	sprintf(acResourceID, ".CMeshFactory::CreateBox(%f,%f,%f)", fWidth, fHeight, fDepth);

	CResourceProxy* pxResourceProxy = __super::LookUpResource(acResourceID);

	if (pxResourceProxy == NULL)
	{
		ID3DXMesh* pd3dMesh;

		D3DXCreateBox(
			m_pxEngineController->GetDevice(),
			fWidth, fHeight, fDepth, 
			&pd3dMesh, NULL);

		pxResourceProxy = __super::AddResource(acResourceID, pd3dMesh, false);
	}

	return TMeshHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TMeshHandle
CMeshFactory::CreateSphere(float fRadius, int iSlices, int iStacks)
{
	char acResourceID[256];
	sprintf(acResourceID, ".CMeshFactory::CreateSphere(%f,%d,%d)", fRadius, iSlices, iStacks);

	CResourceProxy* pxResourceProxy = __super::LookUpResource(acResourceID);

	if (pxResourceProxy == NULL)
	{
		ID3DXMesh* pd3dMesh;

		D3DXCreateSphere(
			m_pxEngineController->GetDevice(),
			fRadius, iSlices, iStacks,
			&pd3dMesh, NULL);

		pxResourceProxy = __super::AddResource(acResourceID, pd3dMesh, false);
	}

	return TMeshHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TMeshHandle
CMeshFactory::CreateCylinder(float fRadius1, float fRadius2, float fLength, int iSlices, int iStacks)
{
	char acResourceID[256];
	sprintf(acResourceID, ".CMeshFactory::CreateCylinder(%f,%f,%f,%d,%d)", fRadius1, fRadius2, fLength, iSlices, iStacks);

	CResourceProxy* pxResourceProxy = __super::LookUpResource(acResourceID);

	if (pxResourceProxy == NULL)
	{
		ID3DXMesh* pd3dMesh;

		D3DXCreateCylinder(
			m_pxEngineController->GetDevice(),
			fRadius1, fRadius2, fLength,
			iSlices, iStacks, 
			&pd3dMesh, NULL);

		pxResourceProxy = __super::AddResource(acResourceID, pd3dMesh, false);
	}

	return TMeshHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TMeshHandle
CMeshFactory::CreateTorus(float fInnerRadius, float fOuterRadius, int iSides, int iRings)
{
	char acResourceID[256];
	sprintf(acResourceID, ".CMeshFactory::CreateTorus(%f,%f,%d,%d)", fInnerRadius, fOuterRadius, iSides, iRings);

	CResourceProxy* pxResourceProxy = __super::LookUpResource(acResourceID);

	if (pxResourceProxy == NULL)
	{
		ID3DXMesh* pd3dMesh;

		D3DXCreateTorus(
			m_pxEngineController->GetDevice(),
			fInnerRadius, fOuterRadius,
			iSides, iRings,
			&pd3dMesh, NULL);

		pxResourceProxy = __super::AddResource(acResourceID, pd3dMesh, false);
	}

	return TMeshHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------
TMeshHandle
CMeshFactory::CreateTeapot()
{
	char acResourceID[256];
	sprintf(acResourceID, ".CMeshFactory::CreateTeapot()");

	CResourceProxy* pxResourceProxy = __super::LookUpResource(acResourceID);

	if (pxResourceProxy == NULL)
	{
		ID3DXMesh* pd3dMesh;

		D3DXCreateTeapot(
			m_pxEngineController->GetDevice(),
			&pd3dMesh, NULL);

		pxResourceProxy = __super::AddResource(acResourceID, pd3dMesh, false);
	}

	return TMeshHandle(pxResourceProxy);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

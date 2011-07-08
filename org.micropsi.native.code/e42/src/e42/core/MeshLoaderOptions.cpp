#include "stdafx.h"

#include "e42/core/MeshLoaderOptions.h"

#include <d3dx9.h>

using std::string;

//-----------------------------------------------------------------------------------------------------------------------------------------
CMeshLoaderOptions::CMeshLoaderOptions()
{
	m_sShaderPath = "xfl-shader>";
	m_sTexturePath = "xfl-texture>";

	m_vStaticShadowVolumeExtrusion.Clear();
	m_bCreateEdgeQuads = false;
	m_fShadowVolumeShrink = 1e-3f;

	m_fLevelOfDetailFactor = 1.0f;

	m_bGenerateNormals = false;
	m_bGenerateTangents = false;
	m_bGenerateBinormals = false;

	m_bGenerateVertexElementsByShaderInput = true;

	m_bRemoveEmptyFrames = false;

	m_bSkipNormals = false;
	m_bLoadResources = true;
	m_bOptimizeMesh = true;

	m_dwMeshOptions = D3DXMESH_MANAGED | D3DXMESH_WRITEONLY;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
string
CMeshLoaderOptions::GetIDString()
{
	DWORD dwFlags = 0;
	dwFlags <<= 1; if (m_bCreateEdgeQuads) dwFlags |= 1;
	dwFlags <<= 1; if (m_bGenerateNormals) dwFlags |= 1;
	dwFlags <<= 1; if (m_bGenerateTangents) dwFlags |= 1;
	dwFlags <<= 1; if (m_bGenerateBinormals) dwFlags |= 1;
	dwFlags <<= 1; if (m_bGenerateVertexElementsByShaderInput) dwFlags |= 1;
	dwFlags <<= 1; if (m_bSkipNormals) dwFlags |= 1;
	dwFlags <<= 1; if (m_bLoadResources) dwFlags |= 1;
	dwFlags <<= 1; if (m_bOptimizeMesh) dwFlags |= 1;

	DWORD dwExtrusionVec;
	dwExtrusionVec = *(DWORD*)(float*)&m_vStaticShadowVolumeExtrusion.x();
	dwExtrusionVec ^= *(DWORD*)(float*)&m_vStaticShadowVolumeExtrusion.y();
	dwExtrusionVec += *(DWORD*)(float*)&m_vStaticShadowVolumeExtrusion.z();

	dwExtrusionVec -= *(DWORD*)(float*)&m_fShadowVolumeShrink;

	char acBuffer[128];
	sprintf(acBuffer, "_0x%x_0x%x_0x%x", dwFlags, dwExtrusionVec, m_dwMeshOptions);

	return string(acBuffer);
}
//-----------------------------------------------------------------------------------------------------------------------------------------

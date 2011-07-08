
#include "baselib/filelocator.h"

#include "e42/core/EffectShader.h"
#include "e42/core/DeviceStateMgr.h"
#include "baselib/geometry/Plane.h"
#include "e42/E42Application.h"

#include "Engine/Terrain/splatchunkmanager.h"
#include "Engine/Terrain/splatchunkterraintile.h"
#include "Engine/Terrain/materialmap.h"

#include "il/il.h"
#include "il/ilu.h"

#include "Utilities/systemutils.h"

using std::string;
using std::vector;

//---------------------------------------------------------------------------------------------------------------------

CSplatChunkManager* CSplatChunkManager::ms_pxInstance = 0;

//---------------------------------------------------------------------------------------------------------------------
CSplatChunkManager::CSplatChunkManager(CEngineController* p_pxEngineController)
{
	assert(ms_pxInstance ==0);
	ms_pxInstance = this;

	m_pxEngineController = p_pxEngineController;

	m_iMapXSize			= 0;
	m_iMapZSize			= 0;
	m_iTotalChunks		= 0;
	m_ppxChunks			= 0;

	m_pxHeightMap		= 0;			
	m_pxMaterialMap		= 0;
	m_pxSharedIndexBufferLodLevels = 0;

	m_bInTerrainDefinition = false;
	m_bReadyToRender = false;


	m_vScale = CVec3(1.0f, 1.0f, 1.0f);
	m_vOffset = CVec3(0.0f, 0.0f, 0.0f);

	m_xConfig.m_bForceRGBABlendMaps = false;
	m_xConfig.m_b4BitBlendMapsAcceptable = true;
	m_xConfig.m_DrawBlendMapBorders = false;
	m_xConfig.m_bBasePassOnly = false;
	m_xConfig.m_bShadows = false;
	m_xConfig.m_iMaterialOfBlendMapToRender = -1;
	m_xConfig.m_fTextureLODFadeStart = 200.0f;
	m_xConfig.m_fTextureLODFadeEnd = 400.0f;
	m_xConfig.m_iChunkWidth = 128;
	m_xConfig.m_iTextureRepeatitions = 32;
	m_xConfig.m_iLowResImageWidth = m_xConfig.m_iChunkWidth * 4 / m_xConfig.m_iTextureRepeatitions;
	m_xConfig.m_iGeometryLODLevels = 4;
	m_xConfig.m_bRenderSkirts = true;
	m_xConfig.m_bDoubleSided = false;

	DetermineBlendMapTextureFormat();

	m_xDefaultMaterial.Load(p_pxEngineController, "terrain_notavailable.tga", m_xConfig.m_iLowResImageWidth);

    ilInit();
    iluInit();

	ilEnable(IL_ORIGIN_SET);
	ilOriginFunc(IL_ORIGIN_UPPER_LEFT);

	m_hTerrainShader = m_pxEngineController->GetEffectFactory()->CreateEffect("shader>terrain.fx");
}

//---------------------------------------------------------------------------------------------------------------------
CSplatChunkManager::~CSplatChunkManager()
{
	ms_pxInstance = 0;
	Clear();
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSplatChunkManager::Clear()
{
	if(m_ppxChunks)
	{
		for(int i=0; i<m_iXChunks * m_iZChunks; ++i)
		{
			delete m_ppxChunks[i];
		}

		delete [] m_ppxChunks;
		m_ppxChunks = 0;
	}

	delete [] m_pxSharedIndexBufferLodLevels;
	m_pxSharedIndexBufferLodLevels = 0;

	m_iMapXSize			= 0;
	m_iMapZSize			= 0;
	m_iTotalChunks		= 0;

	m_pxHeightMap		= 0;			
	m_pxMaterialMap		= 0;

	m_bReadyToRender = false;
}

//---------------------------------------------------------------------------------------------------------------------
void
CSplatChunkManager::BeginTerrainDefinition()
{
	assert(m_bInTerrainDefinition == false);
	m_bInTerrainDefinition = true;

	Clear();

	m_pxHeightMap			= 0;			
	m_pxMaterialMap			= 0;			
	m_vOffset				= CVec3(0.0f, 0.0f, 0.0f);				
	m_vScale				= CVec3(1.0f, 0.1f, 1.0f);				
	m_fTextureTileSize		= 1.0f;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CSplatChunkManager::EndTerrainDefinition()
{
	assert(m_bInTerrainDefinition == true);
	if(!m_bInTerrainDefinition)
	{
		return false;
	}
	m_bInTerrainDefinition = false;

	
	if(!m_pxHeightMap  ||  !m_pxMaterialMap)
	{
		return false;
	}

	m_iMapXSize = m_pxMaterialMap->GetWidth();
	m_iMapZSize = m_pxMaterialMap->GetHeight();

	if(	m_pxHeightMap->GetWidth() -1 != m_iMapXSize  ||
		m_pxHeightMap->GetHeight() -1 != m_iMapZSize)
	{
		assert(false);		// height map must be exactly 1 pixel larger than material map on both axis!
		return false;
	}

	m_xConfig.m_iTextureRepeatitions = (int) floor( ((min(m_vScale.x(), m_vScale.z()) * (float) m_xConfig.m_iChunkWidth) / m_fTextureTileSize) + 0.5f);
	m_xConfig.m_iLowResImageWidth = m_xConfig.m_iChunkWidth * 4 / m_xConfig.m_iTextureRepeatitions;

	for(int i=0; i<ms_iMaxMaterials; ++i)
	{
		if(m_axMaterials[i].m_sTextureName.size() > 0)
		{
			m_axMaterials[i].Load(m_pxEngineController, m_axMaterials[i].m_sTextureName, m_xConfig.m_iLowResImageWidth);
		}
	}

	GenerateChunks();
	CreateSharedIndexBuffer();

	m_bReadyToRender = true;
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
void		
CSplatChunkManager::SetOffset(CVec3 p_vOffset)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
	m_vOffset = p_vOffset;
}

//---------------------------------------------------------------------------------------------------------------------
void		
CSplatChunkManager::SetScale(CVec3 p_vScale)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
	m_vScale = p_vScale;
}

//---------------------------------------------------------------------------------------------------------------------
void	
CSplatChunkManager::SetTextureTileSize(float p_fTextureTileSize)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
	m_fTextureTileSize = p_fTextureTileSize;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	associates a texture with a material index

	\return		true: success, false: failed
*/
bool		
CSplatChunkManager::SetMaterial(int p_iIndex, const string& p_srTextureName)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return false; }

	if(p_iIndex < 0  ||  p_iIndex > ms_iMaxMaterials)
	{
		return false;
	}

	// todo: etwas unschön - load kann erst gerufen werden, wenn scaling feststeht, also am Ende der Definition
	m_axMaterials[p_iIndex].m_sTextureName = p_srTextureName;
	return true;
}

//---------------------------------------------------------------------------------------------------------------------
const CTerrainMaterial*	
CSplatChunkManager::GetMaterial(int p_iIndex) const
{
	if(p_iIndex < 0  ||  p_iIndex >= ms_iMaxMaterials  ||  m_axMaterials[p_iIndex].m_bIsValid == false)
	{
		return &m_xDefaultMaterial;
	}

	return &m_axMaterials[p_iIndex];
}

//---------------------------------------------------------------------------------------------------------------------
float		
CSplatChunkManager::GetTerrainHeight(float p_fX, float p_fZ)
{
	float fX = p_fX - m_vOffset.x();
	float fZ = p_fZ - m_vOffset.z();

	fX /= m_vScale.x();
	fZ /= m_vScale.z();

	if(fX < 0.0f  ||  fZ < 0.0f  ||  fX >= (m_iMapXSize + 1)  ||  fZ >= (m_iMapZSize +1))
	{
		return m_vOffset.y();
	}

	int x = (int) fX;
	int z = (int) fZ;

	CVec3 v0, v1, v2;

	v0 = GetMapVertex(x, z);
	v1 = GetMapVertex(x+1, z+1);
	if(fX - floorf(fX) > (fZ - floorf(fZ)))
	{
		v2 = GetMapVertex(x+1, z);
	}
	else
	{
		v2 = GetMapVertex(x, z+1);
	}

	CPlane xPlane(v0, v1, v2);
	return xPlane.PointY(p_fX, p_fZ);
}

//---------------------------------------------------------------------------------------------------------------------
void					
CSplatChunkManager::SetHeightMap(const CHeightMap* p_pxHeightMap)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
	m_pxHeightMap = p_pxHeightMap;
}

//---------------------------------------------------------------------------------------------------------------------
void
CSplatChunkManager::SetMaterialMap(const CMaterialMap* p_pxMaterialMap)
{
	assert(m_bInTerrainDefinition);
	if(!m_bInTerrainDefinition) { return; }
	m_pxMaterialMap = p_pxMaterialMap;
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSplatChunkManager::DetermineBlendMapTextureFormat()
{
	// determine texture formats

	if(!m_xConfig.m_bForceRGBABlendMaps  &&  
		m_pxEngineController->GetTextureFactory()->IsTextureFormatValidForRendering(D3DFMT_A8))
	{
		m_xConfig.m_eBlendTextureFormat = D3DFMT_A8;
		DebugPrint("terrain blend texture format is D3DFMT_A8");
	}
	else if(m_xConfig.m_b4BitBlendMapsAcceptable  &&
			!m_xConfig.m_DrawBlendMapBorders  &&
			m_pxEngineController->GetTextureFactory()->IsTextureFormatValidForRendering(D3DFMT_A4R4G4B4))
	{
		m_xConfig.m_eBlendTextureFormat = D3DFMT_A4R4G4B4;
		DebugPrint("terrain blend texture format is D3DFMT_A4R4G4B4");
	}
	else
	{
		m_xConfig.m_eBlendTextureFormat = D3DFMT_A8R8G8B8;
		DebugPrint("terrain blend texture format is D3DFMT_A8R8G8B8");
	}
}

//---------------------------------------------------------------------------------------------------------------------
void 
CSplatChunkManager::GenerateChunks()
{
	// create Chunks

	m_iXChunks = m_iMapXSize / m_xConfig.m_iChunkWidth;
	m_iZChunks = m_iMapZSize / m_xConfig.m_iChunkWidth;
	m_iTotalChunks = m_iXChunks * m_iZChunks;

	double dStartTime = Utils::GetSeconds();

	m_ppxChunks = new CSplatChunkTerrainTile*[m_iXChunks * m_iZChunks];
	for(int x=0; x<m_iXChunks; ++x)
	{
		for(int z=0; z<m_iZChunks; ++z)
		{
			CSplatChunkTerrainTile* p = new CSplatChunkTerrainTile(this, x*m_xConfig.m_iChunkWidth, z*m_xConfig.m_iChunkWidth, m_xConfig.m_iChunkWidth);
			m_ppxChunks[z*m_iXChunks+x] = p;
		}
	}

	double dEndTime = Utils::GetSeconds();
	DebugPrint("chunks: time needed %f", dEndTime - dStartTime);
}

//---------------------------------------------------------------------------------------------------------------------
/**
	Creates an index buffer which is shared by all chunks.
	Chunks need to define their own index buffers for their 'splats' of course. However, when rendering the base texture 
	(or a chunk with only one material), the whole geometry is used. In this case all chunks can actually use the same 
	index buffer. This buffer also includes different geometry lod levels. 
*/
void
CSplatChunkManager::CreateSharedIndexBuffer()
{
	int iBlockSize = m_xConfig.m_iChunkWidth;

	assert(m_xConfig.m_iGeometryLODLevels >= 1);

	// make sure block size can be divided by two often enough
	assert(((iBlockSize >> m_xConfig.m_iGeometryLODLevels) << m_xConfig.m_iGeometryLODLevels) == iBlockSize);

	int iNumIndices = 0;
	for(int iResolution=0; iResolution<m_xConfig.m_iGeometryLODLevels; ++iResolution)
	{
		int iLODLevelBlockSize = iBlockSize >> iResolution;
		iNumIndices += iLODLevelBlockSize * iLODLevelBlockSize * 6;  // for core
		iNumIndices += iLODLevelBlockSize * 4 * 6;
	}

	// iBase   iBase+1        x -->
	//  +------+
	//  | **   |          z
	//  |   ** |          |
	//  +------+	      |
	// iUp    iUp+1       v

	m_hSharedIndexBuffer  = m_pxEngineController->GetIndexBufferFactory()->CreateIndexBuffer(iNumIndices, D3DFMT_INDEX16, D3DUSAGE_WRITEONLY, D3DPOOL_MANAGED);

	unsigned short* piIndexBuffer;
	HRESULT hr = m_hSharedIndexBuffer->Lock(0, 0, (void**) &piIndexBuffer, 0);
	assert(SUCCEEDED(hr));
	assert(piIndexBuffer);

	m_pxSharedIndexBufferLodLevels = new CIndexBufferLodLevel[m_xConfig.m_iGeometryLODLevels];

	int iIndex = 0;
	for(int iResolution=0; iResolution<m_xConfig.m_iGeometryLODLevels; ++iResolution)
	{
		int iStep = 1 << iResolution;
		m_pxSharedIndexBufferLodLevels[iResolution].m_iStartIndex = iIndex;

		// normal primitives 

		for(int z=0; z<iBlockSize; z+=iStep)
		{
			for(int x=0; x<iBlockSize; x+=iStep)
			{
				int iBase = z*(iBlockSize+1) + x;
				int iUp = iBase + (iBlockSize + 1) * iStep;  // iStep rows higher
				AddQuad(piIndexBuffer, iIndex, iBase, iBase+iStep, iUp, iUp+iStep);
			}
		}

		// skirt primitives

		int iNumCoreVertices = (iBlockSize+1) * (iBlockSize+1);

		m_pxSharedIndexBufferLodLevels[iResolution].m_iNumPrimitivesWithOutSkirt = 
			(iIndex - m_pxSharedIndexBufferLodLevels[iResolution].m_iStartIndex) / 3;

		// upper border
		AddQuad(piIndexBuffer, iIndex, iNumCoreVertices + 4*iBlockSize -1, iNumCoreVertices-1 + iStep, 0, 0+iStep);
		for(int x=iStep; x<iBlockSize; x+=iStep)
		{
			int iBase = iNumCoreVertices - 1 + x;
			int iUp   = x;
			AddQuad(piIndexBuffer, iIndex, iBase, iBase+iStep, iUp, iUp+iStep);
		}

		// right border
		for(int z=0; z<iBlockSize; z+=iStep)
		{
			int iBase  = z*(iBlockSize+1)+iBlockSize;
			int iBase1 = iNumCoreVertices-1 + iBlockSize + z;
			AddQuad(piIndexBuffer, iIndex, iBase, iBase1, iBase + iStep*(iBlockSize+1), iBase1 + iStep);
		}

		// lower border
		for(int x=iBlockSize; x>=iStep; x-=iStep)
		{
			int iBase1 = iBlockSize * (iBlockSize+1) + x;
			int iUp1   = iNumCoreVertices - 1 + 3*iBlockSize - x;
			AddQuad(piIndexBuffer, iIndex, iBase1-iStep, iBase1, iUp1+iStep, iUp1);
		}

		// right border
		for(int z=iBlockSize; z>=iStep; z-=iStep)
		{
			int iUp1  = iNumCoreVertices + 4*iBlockSize -1 - z;
			int iBase1 = z*(iBlockSize+1);
			AddQuad(piIndexBuffer, iIndex, iBase1-iStep*(iBlockSize+1), iBase1, iUp1+iStep, iUp1);
		}


		m_pxSharedIndexBufferLodLevels[iResolution].m_iNumPrimitivesWithSkirt = 
			(iIndex - m_pxSharedIndexBufferLodLevels[iResolution].m_iStartIndex) / 3;
	}

	hr = m_hSharedIndexBuffer->Unlock();
	assert(SUCCEEDED(hr));
}

//---------------------------------------------------------------------------------------------------------------------
// p_iA   p_iB        x -->
//  +------+
//  | **   |          z
//  |   ** |          |
//  +------+	      |
// p_iC   p_iD        v
void
CSplatChunkManager::AddQuad(unsigned short* p_piIndexBuffer, int& po_iIndex, int p_iA, int p_iB, int p_iC, int p_iD)
{
	p_piIndexBuffer[po_iIndex++] = p_iD;
	p_piIndexBuffer[po_iIndex++] = p_iB;
	p_piIndexBuffer[po_iIndex++] = p_iA;

	p_piIndexBuffer[po_iIndex++] = p_iC;
	p_piIndexBuffer[po_iIndex++] = p_iD;
	p_piIndexBuffer[po_iIndex++] = p_iA;
}

//---------------------------------------------------------------------------------------------------------------------
bool
CSplatChunkManager::GetChunks(const CTerrainTile**& po_rpxGrid, int& po_riGrindWidth, int& po_riGridHeigth) const
{
	if(!m_bReadyToRender)
	{
		return false;
	}

	po_rpxGrid = (const CTerrainTile**) m_ppxChunks;
	po_riGridHeigth = m_iZChunks;
	po_riGrindWidth = m_iXChunks;

	return true;
}
//---------------------------------------------------------------------------------------------------------------------

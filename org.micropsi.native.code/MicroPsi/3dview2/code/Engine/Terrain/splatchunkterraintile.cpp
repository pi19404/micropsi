
#include "Application/stdinc.h"
#include "e42/core/EngineController.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/EffectShader.h"
#include "e42/Vertices.h"
#include "baselib/geometry/Triangle.h"

#include "Engine/Terrain/splatchunkterraintile.h"
#include "Engine/Terrain/splatchunkmanager.h"
#include "Engine/Terrain/materialmap.h"

#include <algorithm>
#include "il/il.h"
#include "il/ilu.h"

using std::vector;

//---------------------------------------------------------------------------------------------------------------------
/**
	this constructor creates a normal chunk, i.e. one that represents a part of the height- and material map
*/
CSplatChunkTerrainTile::CSplatChunkTerrainTile(CSplatChunkManager* p_pxChunkMgr, int p_iMapX, int p_iMapZ, int iMapWidth)
{
	m_iMapXPos = p_iMapX;
	m_iMapZPos = p_iMapZ;
	m_iMapWidth = iMapWidth;
	m_pxChunkMgr = p_pxChunkMgr;

	// find all materials used in this chunk (we need the number for the next step)
	FindMaterials();

	CreateVertexBuffer();
	CreateIndexBuffer();

	if(m_axSplats.size() > 1)
	{
		// if a chunk has only one material, we will not use splatting and do not need a base texture

		CreateBlendTextures();
		CreateBaseTexture();
	}
}

//---------------------------------------------------------------------------------------------------------------------

CSplatChunkTerrainTile::~CSplatChunkTerrainTile()
{
}

//---------------------------------------------------------------------------------------------------------------------
void
CSplatChunkTerrainTile::CreateIndexBuffer()
{
	// create index list; we need to know its size before we create the actual buffer
	vector<unsigned short> aiIndices;

	int iBlockSize = m_iMapWidth;


	// iBase   iBase+1        x -->
	//  +------+
	//  | **   |          z
	//  |   ** |          |
	//  +------+	      |
	// iUp    iUp+1       v

	for(int iSplat=0; iSplat<(int)m_axSplats.size(); ++iSplat)
	{
		int iFirstIndex = (int) aiIndices.size();

		for(int z=0; z<iBlockSize; ++z)
		{
			for(int x=0; x<iBlockSize; ++x)
			{
				if(m_pxChunkMgr->GetMaterialMap()->IsMaterialOrNeighborIsMaterial(x + m_iMapXPos, z + m_iMapZPos, m_axSplats[iSplat].m_iMaterialIndex))
				{
					int iBase = z*(iBlockSize+1) + x;
					int iUp = iBase + iBlockSize + 1;  // one row higher
	
					aiIndices.push_back( iUp+1		);
					aiIndices.push_back( iBase+1	);
					aiIndices.push_back( iBase		);

					aiIndices.push_back( iUp		);
					aiIndices.push_back( iUp+1		);
					aiIndices.push_back( iBase		);
				}
			}
		}
		
		m_axSplats[iSplat].m_iFirstIndex		= iFirstIndex;
		m_axSplats[iSplat].m_iNumIndices		= (int) aiIndices.size() - iFirstIndex;
		m_axSplats[iSplat].m_iNumPrimitives	= m_axSplats[iSplat].m_iNumIndices / 3;
	}

	// write index buffer
	m_hIndexBuffer = m_pxChunkMgr->GetEngineController()->GetIndexBufferFactory()->CreateIndexBuffer((int) aiIndices.size(), D3DFMT_INDEX16, D3DUSAGE_WRITEONLY, D3DPOOL_MANAGED);

	unsigned short* piIndexBuffer;
	m_hIndexBuffer->Lock(0, 0, (void**) &piIndexBuffer, 0);
	assert(piIndexBuffer);
	for(unsigned int i=0; i<aiIndices.size(); ++i)
	{
		piIndexBuffer[i] = aiIndices[i];
	}
	m_hIndexBuffer->Unlock();
}

//---------------------------------------------------------------------------------------------------------------------
CVec3*
CSplatChunkTerrainTile::CalculateTriangleNormals() 
{
	int iBlockSize = m_iMapWidth; 
	int iVertexWidth = iBlockSize + 3;
	int iQuadWidth = iBlockSize + 2;

	CVec3* avCoordinates = new CVec3[iVertexWidth * iVertexWidth];

	int iIndex = 0;
	for(int z=0; z<iVertexWidth; ++z)
	{
		for(int x=0; x<iVertexWidth; ++x)
		{
			avCoordinates[iIndex++] = m_pxChunkMgr->GetMapVertex(x - 1 + m_iMapXPos, z - 1 + m_iMapZPos);
		}
	}

	CVec3* avTriangleNormals = new CVec3[iQuadWidth * iQuadWidth * 2];

	iIndex = 0;
	for(int z=0; z<iQuadWidth; ++z)
	{
		for(int x=0; x<iQuadWidth; ++x)
		{
			// iBase   iBase+1        x -->
			//  +------+
			//  | ** 1 |          z
			//  | 2 ** |          |
			//  +------+	      |
			// iUp    iUp+1       v

			int iBase = z*iVertexWidth + x;
			int iUp = iBase + iVertexWidth;  // one row higher

			CPlane xPlane1(	avCoordinates[iUp+1],
							avCoordinates[iBase+1],
							avCoordinates[iBase]);

			CPlane xPlane2( avCoordinates[iUp],
							avCoordinates[iUp+1],
							avCoordinates[iBase]);

			avTriangleNormals[iIndex++] = xPlane1.m_vNormal;
			avTriangleNormals[iIndex++] = xPlane2.m_vNormal;
		}
	}

	delete [] avCoordinates;

	return avTriangleNormals;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	creates a vertex buffer for this chunk
	also creates calculates the bounding box for this chunk
*/
void
CSplatChunkTerrainTile::CreateVertexBuffer()
{
	// all vertices on the edges must be doubled, because we want to create a "skirt" around the tile
	m_iNumVertices = (m_iMapWidth+1) * (m_iMapWidth+1) + (4 * m_iMapWidth);

	m_hVertexBuffer		  = m_pxChunkMgr->GetEngineController()->GetVertexBufferFactory()->CreateVertexBufferFVF(m_iNumVertices, Vertices::g_FVFTable[VT_3NT2T2], D3DUSAGE_WRITEONLY, D3DPOOL_MANAGED);
    m_hVertexDeclaration  = m_pxChunkMgr->GetEngineController()->GetVertexDeclarationFactory()->CreateVertexDeclaration(Vertices::g_FVFTable[VT_3NT2T2]);

	// write vertex buffer
	Vertex3NT2T2* pVertexBuffer;
	HRESULT hr = m_hVertexBuffer->Lock(0, 0, (void**) &pVertexBuffer, 0);
	assert(SUCCEEDED(hr));
	assert(pVertexBuffer);

	// first, create the normal terrain vertices
		
	int iTextureRepeatitions = m_pxChunkMgr->GetConfig().m_iTextureRepeatitions;
	int iIndex = 0;

	float uv1start = 0.5f / (m_iMapWidth);
	float uv1step = (1.0f - 2.0f*uv1start) / (m_iMapWidth); 

	float fYMin =  FLT_MAX;
	float fYMax = -FLT_MAX;

	CVec3* pvNormals = CalculateTriangleNormals();
	int iQuadWidth = m_iMapWidth +2;

	float v1 = uv1start;
	for (int z = 0; z < m_iMapWidth+1; ++z)
    {
		float u1 = uv1start;
		for (int x = 0; x < m_iMapWidth+1; ++x)
        {
			CVec3 vVertex = m_pxChunkMgr->GetMapVertexLocal(x, z, x + m_iMapXPos, z + m_iMapZPos);

			fYMin = min(fYMin, vVertex.y());
			fYMax = max(fYMax, vVertex.y());

			pVertexBuffer[iIndex].p = vVertex;

			pVertexBuffer[iIndex].t0.x() = (float) x * iTextureRepeatitions / m_iMapWidth;
			pVertexBuffer[iIndex].t0.y() = (float) z * iTextureRepeatitions / m_iMapWidth;

			pVertexBuffer[iIndex].t1.x() = u1;
			pVertexBuffer[iIndex].t1.y() = v1;

			u1 += uv1step;

			int iNWQuad = (z * iQuadWidth + x) * 2;
			int iNEQUad = iNWQuad + 2;
			int iSWQuad = iNWQuad + iQuadWidth * 2;
			int iSEQuad = iSWQuad + 2;

			CVec3 vNormal = (pvNormals[iNWQuad] + pvNormals[iNWQuad + 1] + pvNormals[iNEQUad +1] + 
							 pvNormals[iSWQuad] + pvNormals[iSEQuad] + pvNormals[iSEQuad + 1]) * (1.0f / 6.0f);

			pVertexBuffer[iIndex].n = vNormal;
//			DebugPrint("%.2f %.2f %.2f", vNormal.x(), vNormal.y(), vNormal.z());

			iIndex++;
        }
		v1 += uv1step;
    }


	// now copy all edge vertices to create the skirt

	int iFirstSkirtIndex = iIndex;
	for(int x=1; x<m_iMapWidth+1; ++x)
	{
		pVertexBuffer[iIndex++] = pVertexBuffer[x];
	}
	for(int z=1; z<m_iMapWidth+1; ++z)
	{
		pVertexBuffer[iIndex++] = pVertexBuffer[(m_iMapWidth+1) * z + m_iMapWidth];
	}
	for(int x=m_iMapWidth-1; x>=0; --x)
	{
		pVertexBuffer[iIndex++] = pVertexBuffer[(m_iMapWidth+1) * m_iMapWidth + x];
	}
	for(int z=m_iMapWidth-1; z>=0; --z)
	{
		pVertexBuffer[iIndex++] = pVertexBuffer[(m_iMapWidth+1) * z];
	}

	for(int i=iFirstSkirtIndex; i<iIndex; ++i)
	{
		pVertexBuffer[i].p.y() -= 2.0f;		
	}


	hr = m_hVertexBuffer->Unlock();
	assert(SUCCEEDED(hr));

	CVec3 vMinVertex = m_pxChunkMgr->GetMapVertexLocal(0, 0, m_iMapXPos, m_iMapZPos);
	CVec3 vMaxVertex = m_pxChunkMgr->GetMapVertexLocal(m_iMapWidth, m_iMapWidth, m_iMapXPos + m_iMapWidth, m_iMapZPos + m_iMapWidth);
	m_xAABB.m_vMin = CVec3(vMinVertex.x(), fYMin, vMinVertex.z());
	m_xAABB.m_vMax = CVec3(vMaxVertex.x(), fYMax, vMaxVertex.z());

	delete [] pvNormals;
}

//---------------------------------------------------------------------------------------------------------------------
/**
	determines the number of materials involved in this chunk
*/
void	
CSplatChunkTerrainTile::FindMaterials()
{ 
	int iXStart = max(m_iMapXPos-1, 0);
	int iXEnd   = min(m_iMapXPos + m_iMapWidth+1, m_pxChunkMgr->GetMapXSize());
	int iZStart = max(m_iMapZPos-1, 0);
	int iZEnd   = min(m_iMapZPos + m_iMapWidth+1, m_pxChunkMgr->GetMapZSize());

	for(int x=iXStart; x<iXEnd; ++x)
	{
		for(int z=iZStart; z<iZEnd; ++z)
		{
			int iSplat = m_pxChunkMgr->GetMaterialMap()->GetMaterial(x, z);
			for(unsigned int i=0; i<m_axSplats.size(); ++i)
			{
				if(m_axSplats[i].m_iMaterialIndex == iSplat)
				{
					m_axSplats[i].m_iCount++;
					break;
				}
			}
			if(i >= m_axSplats.size())
			{
				m_axSplats.push_back(CSplat(iSplat));
			}
		}
	}

	std::sort(m_axSplats.begin(), m_axSplats.end());
//	DebugPrint("block @ %d,%d has %d material(s)", m_iMapXPos, m_iMapZPos, m_axSplats.size());
}


//---------------------------------------------------------------------------------------------------------------------
/**
	calculates an array with blend weights for each material
	this array is p_iMultiplier times the resolution of the material map
	(example: p_iMultiplier=4, Chunk Width = 32 --> blend weight resolution = 128)
*/
float* 
CSplatChunkTerrainTile::CreateBlendWeights(int p_iMultiplier)
{
	int iTextureWidth = m_iMapWidth * p_iMultiplier;
	int iTextureWidthSquared = iTextureWidth * iTextureWidth;
	float* pfBlendWeights = new float[m_axSplats.size() * iTextureWidthSquared];
	memset(pfBlendWeights, 0, m_axSplats.size() * iTextureWidthSquared * sizeof(float)); 

	float fTexelWidth = (float) m_iMapWidth / (float) (iTextureWidth - 2);	   // width of texel in material map coordinates

	int iIndex = 0;
	for(unsigned int iSplat=0; iSplat<m_axSplats.size(); ++iSplat)
	{
		int iMaterial = m_axSplats[iSplat].m_iMaterialIndex;

		float fTexelY = - (fTexelWidth / 2.0f);				// texel center y in material map coordinates
		for(int y=0; y<iTextureWidth; ++y)
		{
			int iQuadY = fTexelY < 0   ?  -1 : (int) fTexelY;
			float fQuadCenterY = 0.5f + (float) iQuadY;
			iQuadY += m_iMapZPos;


			float fTexelX = - (fTexelWidth / 2.0f);			// texel center x in material map coordinates
			for(int x=0; x<iTextureWidth; ++x)
			{
				int iQuadX = fTexelX < 0   ?  -1 : (int) fTexelX;
				float fQuadCenterX = 0.5f + (float) iQuadX;
				iQuadX += m_iMapXPos;

				for(int i=-1; i<=1; ++i)
				{
					for(int j=-1; j<=1; ++j)
					{
						if(m_pxChunkMgr->GetMaterialMap()->GetMaterial(iQuadX+i, iQuadY+j) == iMaterial)
						{
							pfBlendWeights[iIndex] += Weight(fTexelX, fTexelY, fQuadCenterX + (float) i, fQuadCenterY + (float) j);
						}
					}
				}

				iIndex++;
				fTexelX += fTexelWidth;
			} // x-loop

			fTexelY += fTexelWidth;
		} // y-loop
	}

	// normalize Weights to 1
	for(int x=0; x<iTextureWidth; ++x)
	{
		for(int y=0; y<iTextureWidth; ++y)
		{
			float fTotalWeight = 0;
			for(unsigned int iSplat=0; iSplat<m_axSplats.size(); ++iSplat)
			{
				fTotalWeight += pfBlendWeights[iSplat * iTextureWidthSquared + y * iTextureWidth + x];
			}
			for(unsigned int iSplat=0; iSplat<m_axSplats.size(); ++iSplat)
			{
				pfBlendWeights[iSplat * iTextureWidthSquared + y * iTextureWidth + x] /= fTotalWeight;
			}
		}
	}

	return pfBlendWeights;
}


//---------------------------------------------------------------------------------------------------------------------
/** 
	Creates a blend texture for every material (if there is more than one material)
	Only the alpha cannel of the blend texture is used. It contains the alpha-blend values for
	every vertex of this materials mesh
*/
void	
CSplatChunkTerrainTile::CreateBlendTextures()
{
	int iBlockWidth = m_iMapWidth *2;
	int iBlockWidthSquared = iBlockWidth * iBlockWidth;
	float* pfBlendWeights = CreateBlendWeights(2);

	// create alpha-texture from the weights
	for(unsigned int iSplat=0; iSplat<m_axSplats.size(); ++iSplat)
	{	
		HRESULT hr;

		// first, create a texture in system memory and write the calculated blend weights into it
			
		TTextureHandle hTexture = m_pxChunkMgr->GetEngineController()->GetTextureFactory()->CreateTexture(
			iBlockWidth, iBlockWidth, 1, 0, m_pxChunkMgr->GetConfig().m_eBlendTextureFormat, D3DPOOL_MANAGED);

		assert(hTexture.GetPtr());

	    _D3DLOCKED_RECT xLockedRect;
	    hr = ((IDirect3DTexture9*) hTexture.GetPtr())->LockRect(0, &xLockedRect, NULL, 0);
		assert(SUCCEEDED(hr));

		int iPitch;

		if(m_pxChunkMgr->GetConfig().m_eBlendTextureFormat == D3DFMT_A8)
		{
			char* pBitmap = (char*) xLockedRect.pBits;
			float* pfReadPtr = pfBlendWeights + (iSplat * iBlockWidthSquared);
			iPitch = xLockedRect.Pitch / sizeof(char);

			for(int x=0; x<iBlockWidth; ++x)
			{
				for(int y=0; y<iBlockWidth; ++y)
				{
					char c = (char) (*pfReadPtr * 255.0f);
					*pBitmap = c;
					pfReadPtr++;
					pBitmap++;
				}
				pBitmap += (iPitch - iBlockWidth);
			}
		}
		else if(m_pxChunkMgr->GetConfig().m_eBlendTextureFormat == D3DFMT_A8R8G8B8)
		{
			DWORD* pBitmap = (DWORD*) xLockedRect.pBits;
			iPitch = xLockedRect.Pitch / sizeof(DWORD);
			for(int x=0; x<iBlockWidth; ++x)
			{
				for(int y=0; y<iBlockWidth; ++y)
				{
					DWORD dw = (DWORD) (pfBlendWeights[iSplat * iBlockWidthSquared + y * iBlockWidth + x] * 255.0f); 
					pBitmap[y * iPitch + x] = dw + (dw << 8) + (dw << 16) + (dw << 24);
				}
			}
		}
		else
		{
			WORD* pBitmap = (WORD*) xLockedRect.pBits;
			iPitch = xLockedRect.Pitch / sizeof(unsigned short);
			assert(m_pxChunkMgr->GetConfig().m_eBlendTextureFormat == D3DFMT_A4R4G4B4);
			for(int x=0; x<iBlockWidth; ++x)
			{
				for(int y=0; y<iBlockWidth; ++y)
				{
					WORD dw = (WORD) (pfBlendWeights[iSplat * iBlockWidthSquared + y * iBlockWidth + x] * 15.0f); 
					pBitmap[y * iPitch + x] = dw + (dw << 4) + (dw << 8) + (dw << 12);
				}
			}
		}

		// draw borders (for debug purposes)
		if(m_pxChunkMgr->GetConfig().m_eBlendTextureFormat == D3DFMT_A8R8G8B8)
		{
			DWORD* pBitmap = (DWORD*) xLockedRect.pBits;
			for(int x=0; x<iBlockWidth; ++x)
			{
				((DWORD*) pBitmap)[x * iPitch + 0] |= 0x00FF0000; 
				((DWORD*) pBitmap)[0 * iPitch + x] |= 0x00FF0000; 
				((DWORD*) pBitmap)[(iBlockWidth-1) * iPitch + x] |= 0x00FF0000; 
				((DWORD*) pBitmap)[x * iPitch + (iBlockWidth-1)] |= 0x00FF0000; 
			}
		}

	    hr = ((IDirect3DTexture9*) hTexture.GetPtr())->UnlockRect(0);
		assert(SUCCEEDED(hr));

		m_axSplats[iSplat].m_hBlendTexture = hTexture;
	}

	delete [] pfBlendWeights;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	precomputes a base texture for this chunk
	the base texture is a low-res bitmap of the blended materials
	it is used as a base pass for splatting and as a LOD level
*/
void
CSplatChunkTerrainTile::CreateBaseTexture()
{	
	int iBlockWidth = m_iMapWidth * 4;
	int iBlockWidthSquared = iBlockWidth * iBlockWidth;
	float* pfBlendWeights = CreateBlendWeights(4);

	// first, create a texture in system memory 
	TTextureHandle hTexture = m_pxChunkMgr->GetEngineController()->GetTextureFactory()->CreateTexture(
		iBlockWidth,
		iBlockWidth,
		1,
		0,
		D3DFMT_A8R8G8B8, 
		D3DPOOL_MANAGED);

	assert(hTexture.GetPtr());

	HRESULT hr;

	_D3DLOCKED_RECT xLockedRect;
	hr = ((IDirect3DTexture9*) hTexture.GetPtr())->LockRect(0, &xLockedRect, NULL, 0);
	assert(SUCCEEDED(hr));
	unsigned long* pBitmap = (unsigned long*)xLockedRect.pBits;
	int iPitch = xLockedRect.Pitch / sizeof(unsigned long);

	int iLowResImageWidth = m_pxChunkMgr->GetConfig().m_iLowResImageWidth;

	for(int x=0; x<iBlockWidth; ++x)
	{
		for(int y=0; y<iBlockWidth; ++y)
		{
			float r, g, b, a;
			r = g = b = a = 0;
			for(unsigned int iSplat=0; iSplat<m_axSplats.size(); ++iSplat)
			{	
				float fWeight = pfBlendWeights[iSplat * iBlockWidthSquared + y * iBlockWidth + x];
				const CTerrainMaterial* pxMaterial = m_pxChunkMgr->GetMaterial(m_axSplats[iSplat].m_iMaterialIndex);

				DWORD dwColor = pxMaterial->m_piLowResImage[(y % iLowResImageWidth) * iLowResImageWidth + (x % iLowResImageWidth)];
				r += fWeight * ((dwColor & 0xFF000000) >> 24); 
				g += fWeight * ((dwColor & 0x00FF0000) >> 16); 
				b += fWeight * ((dwColor & 0x0000FF00) >> 8);
				a += fWeight * ((dwColor & 0x000000FF));
			}
			DWORD dw =	((((DWORD) r) & 0xFF) << 24) |
						((((DWORD) g) & 0xFF) << 16) | 
						((((DWORD) b) & 0xFF) << 8)  |
						 (((DWORD) a) & 0xFF);
			((DWORD*) pBitmap)[y * iPitch + x] = dw;
		}
	}

	hr = ((IDirect3DTexture9*) hTexture.GetPtr())->UnlockRect(0);
	assert(SUCCEEDED(hr));

	// keep one, discard the other
	m_hBaseTexture = hTexture;

	delete [] pfBlendWeights;
}


//---------------------------------------------------------------------------------------------------------------------
void		
CSplatChunkTerrainTile::Render(TRenderContextPtr spxRenderContext, const CMat4S* p_pmatViewProj, const CMat4S* p_pmatWorld, float p_fDistanceFromViewer) const
{
	CMat4S matWorldViewProj = *p_pmatWorld * *p_pmatViewProj;

	// first, determine LOD for this chunk

	bool bBasePassOnly = true;
	float fSplatFadeFactor = 1.0f;
	if(!m_pxChunkMgr->GetConfig().m_bBasePassOnly)
	{
		if(p_fDistanceFromViewer < m_pxChunkMgr->GetConfig().m_fTextureLODFadeEnd)
		{
			bBasePassOnly = false;
			if(p_fDistanceFromViewer >= m_pxChunkMgr->GetConfig().m_fTextureLODFadeStart)
			{
				fSplatFadeFactor = 1.0f - (p_fDistanceFromViewer - m_pxChunkMgr->GetConfig().m_fTextureLODFadeStart) /
					(m_pxChunkMgr->GetConfig().m_fTextureLODFadeEnd - m_pxChunkMgr->GetConfig().m_fTextureLODFadeStart);
			}
		}
	}

	const CSplatChunkManager::CIndexBufferLodLevel* pxLODInfo = m_pxChunkMgr->GetLodLevelInfo();
	int iGeometryLOD = 0;
	if(m_pxChunkMgr->GetConfig().m_bRenderLowestLODOnly)
	{
		iGeometryLOD = 2;
	}
	else if(bBasePassOnly || m_axSplats.size() < 2)
	{
		if(p_fDistanceFromViewer > 300.0f)
		{
			iGeometryLOD = 1;
		}
		if(p_fDistanceFromViewer > 600.0f)
		{
			iGeometryLOD = 2;
		}
	}
	
	int iFirstIndex = pxLODInfo[iGeometryLOD].m_iStartIndex;
	int iNumPrimitives = m_pxChunkMgr->GetConfig().m_bRenderSkirts ? 
		pxLODInfo[iGeometryLOD].m_iNumPrimitivesWithSkirt : pxLODInfo[iGeometryLOD].m_iNumPrimitivesWithOutSkirt;

	HRESULT hr;

    CEngineController* pxEngineController = m_pxChunkMgr->GetEngineController();
    CDeviceStateMgr* pd3dDeviceStateMgr = pxEngineController->GetDeviceStateMgr();

	pd3dDeviceStateMgr->SetStreamSource(0, m_hVertexBuffer.GetPtr(), 0, sizeof(Vertex3NT2T2));
    pd3dDeviceStateMgr->SetVertexDeclaration( m_hVertexDeclaration.GetPtr());
	CEffectShader* pxShader = m_pxChunkMgr->GetTerrainShader().GetPtr();
	pxShader->SetWorldViewProjectionMatrix(matWorldViewProj);
	pxShader->SetWorldMatrix(*p_pmatWorld);
	hr = pxShader->GetD3DXEffect()->SetInt("c_iCullMode", m_pxChunkMgr->GetConfig().m_bDoubleSided ? D3DCULL_NONE : D3DCULL_CW);
	assert(SUCCEEDED(hr));

	if(m_axSplats.size() < 2)
	{
		// if there is only one material, rendering is trivial and uses neither splatting nor the base texture

		hr = pd3dDeviceStateMgr->SetIndices(m_pxChunkMgr->GetSharedIndexBuffer().GetPtr());
		assert(SUCCEEDED(hr));

		pxShader->SetTechnique("trivial_shadowed");
		int iMaterial = m_axSplats[0].m_iMaterialIndex;
		pxShader->SetDiffuseMap(m_pxChunkMgr->GetMaterial(iMaterial)->m_hTexture);
//		pxShader->GetD3DXEffect()->SetBool("c_bBlending", false);

		DoDrawing(pxShader, iFirstIndex, iNumPrimitives);
	}
	else
	{
		// otherwise: normal base + splat rendering

		// ---- BASE PASS ----
		
		hr = pd3dDeviceStateMgr->SetIndices(m_pxChunkMgr->GetSharedIndexBuffer().GetPtr());
		assert(SUCCEEDED(hr));
		pxShader->SetDiffuseMap(m_hBaseTexture);

		// debug code: render blend maps :)
		if(m_pxChunkMgr->GetConfig().m_iMaterialOfBlendMapToRender >= 0)
		{
			for(unsigned int i=0; i<m_axSplats.size(); ++i)
			{
				if(m_pxChunkMgr->GetConfig().m_iMaterialOfBlendMapToRender == m_axSplats[i].m_iMaterialIndex)
				{
					pxShader->SetDiffuseMap(m_axSplats[i].m_hBlendTexture);
				}
			}
		}

		if(bBasePassOnly  &&  m_pxChunkMgr->GetConfig().m_bShadows)
		{
			// if we are doing the base pass *only* the we can apply the shadows at the same time
			pxShader->SetTechnique("basepass_shadowed");
		}
		else
		{
			pxShader->SetTechnique("basepass");
		}

		DoDrawing(pxShader, iFirstIndex, iNumPrimitives);

		if(bBasePassOnly)
		{
			return;
		}

		// ---- SPLAT PASSES ----

		hr = pd3dDeviceStateMgr->SetIndices(m_hIndexBuffer.GetPtr());
		assert(SUCCEEDED(hr));


		pxShader->SetTechnique("standard");
		for(unsigned int i=0; i<m_axSplats.size(); ++i)
		{
			int iMaterial = m_axSplats[i].m_iMaterialIndex;
			pxShader->SetDiffuseMap(m_pxChunkMgr->GetMaterial(iMaterial)->m_hTexture);
			pxShader->SetDetailMap(m_axSplats[i].m_hBlendTexture);
	
			HRESULT hr = pxShader->GetD3DXEffect()->SetBool("c_bBlending", true);
			assert(SUCCEEDED(hr));

			DWORD dwTFactor = ((DWORD) (fSplatFadeFactor * 255.0f)) << 24;
			hr = pxShader->GetD3DXEffect()->SetInt("c_iTFactor", dwTFactor);
			assert(SUCCEEDED(hr));

			DoDrawing(pxShader, m_axSplats[i].m_iFirstIndex, m_axSplats[i].m_iNumPrimitives);
		}


		// ---- SHADOW PASSES ----

		if(!m_pxChunkMgr->GetConfig().m_bShadows)
		{
			return;
		}

		hr = pd3dDeviceStateMgr->SetIndices(m_pxChunkMgr->GetSharedIndexBuffer().GetPtr());
		assert(SUCCEEDED(hr));

		pxShader->SetTechnique("shadowpass");
		DoDrawing(pxShader, iFirstIndex, iNumPrimitives);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CSplatChunkTerrainTile::DoDrawing(CEffectShader* p_pxShader, int p_iStartIndex, int p_iPrimitiveCount) const
{
	UINT uiNumPasses;
	p_pxShader->Begin(&uiNumPasses, D3DXFX_DONOTSAVESTATE);

	for (UINT uiPass = 0; uiPass < uiNumPasses; uiPass++)
	{
		// rendern
		p_pxShader->BeginPass(uiPass);

		HRESULT hr = m_pxChunkMgr->GetEngineController()->GetDevice()->DrawIndexedPrimitive(
			D3DPT_TRIANGLELIST, 
			0,						// BaseVertex 
			0,						// MinVertex
			m_iNumVertices,			// Number of Vertices
			p_iStartIndex,			// Start Index
			p_iPrimitiveCount);		// Primitive Count

		assert(SUCCEEDED(hr));

		p_pxShader->EndPass();
	}
	p_pxShader->End();
}

//---------------------------------------------------------------------------------------------------------------------
bool							
CSplatChunkTerrainTile::HitTest(const CVec3& p_rvWorldPos, const CRay& p_rxRay, CVec3& po_rxCollisionPoint) const
{
	CAxisAlignedBoundingBox xAABB = m_xAABB.GetTranslated(p_rvWorldPos);
	if(!xAABB.Overlaps(p_rxRay))
	{
		return false;
	}

	//DebugPrint("triangle test for bb %.2f %.2f %.2f - %.2f %.2f %.2f", 
	//	xAABB.m_vMin.x(), xAABB.m_vMin.y(), xAABB.m_vMin.z(),
	//	xAABB.m_vMax.x(), xAABB.m_vMax.z(), xAABB.m_vMax.z());

	int iBlockSize = m_iMapWidth; 
	int iVertexWidth = iBlockSize + 3;
	int iQuadWidth = iBlockSize + 2;

	CVec3* avCoordinates = new CVec3[iVertexWidth * iVertexWidth];

	int iIndex = 0;
	for(int z=0; z<iVertexWidth; ++z)
	{
		for(int x=0; x<iVertexWidth; ++x)
		{
			avCoordinates[iIndex++] = m_pxChunkMgr->GetMapVertex(x - 1 + m_iMapXPos, z - 1 + m_iMapZPos);
		}
	}

	bool bFound = false;
	CVec3 vNearestPoint = CVec3(FLT_MAX, FLT_MAX, FLT_MAX);
	float fNearestPointSquaredDistance = FLT_MAX;

	iIndex = 0;
	for(int z=0; z<iQuadWidth; ++z)
	{
		for(int x=0; x<iQuadWidth; ++x)
		{
			// iBase   iBase+1        x -->
			//  +------+
			//  | ** 1 |          z
			//  | 2 ** |          |
			//  +------+	      |
			// iUp    iUp+1       v

			int iBase = z*iVertexWidth + x;
			int iUp = iBase + iVertexWidth;  // one row higher
			float fPos;

			CTriangle xTriangle1(	avCoordinates[iUp+1],
									avCoordinates[iBase+1],
									avCoordinates[iBase]);

			if(xTriangle1.IntersectsSingleSided(p_rxRay, fPos))
			{
				CVec3 vCollisionPoint = p_rxRay.m_vBase + p_rxRay.m_vDirection * fPos;
				bFound = true; 
				float fDistSquared = (p_rxRay.m_vBase - vCollisionPoint).AbsSquare();
				if(fNearestPointSquaredDistance > fDistSquared)
				{
					fNearestPointSquaredDistance = fDistSquared;
					vNearestPoint = vCollisionPoint;
				}
			}

			CTriangle xTriangle2(	avCoordinates[iUp],
									avCoordinates[iUp+1],
									avCoordinates[iBase]);

			if(xTriangle2.IntersectsSingleSided(p_rxRay, fPos))
			{
				CVec3 vCollisionPoint = p_rxRay.m_vBase + p_rxRay.m_vDirection * fPos;
				bFound = true; 
				float fDistSquared = (p_rxRay.m_vBase - vCollisionPoint).AbsSquare();
				if(fNearestPointSquaredDistance > fDistSquared)
				{
					fNearestPointSquaredDistance = fDistSquared;
					vNearestPoint = vCollisionPoint;
				}
			}
		}
	}

	delete [] avCoordinates;

	if(bFound)
	{
		po_rxCollisionPoint = vNearestPoint;
	}
	return bFound;
}
//---------------------------------------------------------------------------------------------------------------------


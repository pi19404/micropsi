
#include "Application/stdinc.h"
#include "e42/core/EngineController.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/EffectShader.h"
#include "e42/Vertices.h"

#include "Engine/Terrain/sinuswatertile.h"

//---------------------------------------------------------------------------------------------------------------------
CSinusWaterTile::CSinusWaterTile(CEngineController* p_pxEngineController, int p_iWidthInVertices, CVec3 p_vWorldSpaceScaling) :	CWaterTile(p_pxEngineController)
{
	m_iWidthInVertices = p_iWidthInVertices;
	m_vWorldSpaceScaling = p_vWorldSpaceScaling;
	m_fTextureRepeatitions = 1.0f;
	m_iCycleDuration = 100;
	m_pxSinusTable = 0;

	CreateSinusTable();

	m_hWaterShader = m_pxEngineController->GetEffectFactory()->CreateEffect("shader>water.fx");
	m_hWaterTexture = p_pxEngineController->GetTextureFactory()->CreateTextureFromFile("texture>water2.tga");

	CreateVertexBuffer();
	CreateIndexBuffer();
	FillVertexBuffer();
}

//---------------------------------------------------------------------------------------------------------------------

CSinusWaterTile::~CSinusWaterTile()
{
	delete [] m_pxSinusTable;
}


//---------------------------------------------------------------------------------------------------------------------
/**
	generate the sinus table that is used to calculate the waves
*/
void CSinusWaterTile::CreateSinusTable()
{
	assert(!m_pxSinusTable);
	m_pxSinusTable = new float[m_iCycleDuration];

	float fTwoPI = PIf * 2.0f;

	int i;
	for(i=0; i<m_iCycleDuration; ++i)
	{
		m_pxSinusTable[i] = (float) sin((float) i * (fTwoPI / (float) m_iCycleDuration)) / 6.0f;
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CSinusWaterTile::CreateIndexBuffer()
{
	m_iNumIndices = (m_iWidthInVertices-1) * (m_iWidthInVertices-1) * 6;

	// iBase   iBase+1        x -->
	//  +------+
	//  | **   |          z
	//  |   ** |          |
	//  +------+	      |
	// iUp    iUp+1       v

	m_hIndexBuffer		  = m_pxEngineController->GetIndexBufferFactory()->CreateIndexBuffer(m_iNumIndices, D3DFMT_INDEX16, D3DUSAGE_WRITEONLY, D3DPOOL_MANAGED);

	unsigned short* piIndexBuffer;
	HRESULT hr = m_hIndexBuffer->Lock(0, 0, (void**) &piIndexBuffer, 0);
	assert(SUCCEEDED(hr));
	assert(piIndexBuffer);

	int iIndex = 0;
	for(int z=0; z<m_iWidthInVertices-1; ++z)
	{
		for(int x=0; x<m_iWidthInVertices-1; ++x)
		{
			int iBase = z*m_iWidthInVertices + x;
			int iUp = iBase + m_iWidthInVertices;  // one row higher

			piIndexBuffer[iIndex++] = iUp+1;
			piIndexBuffer[iIndex++] = iBase+1;
			piIndexBuffer[iIndex++] = iBase;

			piIndexBuffer[iIndex++] = iUp;
			piIndexBuffer[iIndex++] = iUp+1;
			piIndexBuffer[iIndex++] = iBase;
		}
	}

	hr = m_hIndexBuffer->Unlock();
	assert(SUCCEEDED(hr));
}

//---------------------------------------------------------------------------------------------------------------------
/**
	creates a vertex buffer for this chunk
	also creates calculates the bounding box for this chunk
*/
void
CSinusWaterTile::CreateVertexBuffer()
{
	m_iNumVertices = m_iWidthInVertices * m_iWidthInVertices;
	m_iNumPrimitives = (m_iWidthInVertices-1) * (m_iWidthInVertices-1) * 2;

	m_iVertexBufferSize = 2 * m_iNumVertices;
	m_hVertexBuffer		  = m_pxEngineController->GetVertexBufferFactory()->CreateVertexBufferFVF(m_iVertexBufferSize, Vertices::g_FVFTable[VT_3NT2T2], D3DUSAGE_WRITEONLY | D3DUSAGE_DYNAMIC, D3DPOOL_DEFAULT);
    m_hVertexDeclaration  = m_pxEngineController->GetVertexDeclarationFactory()->CreateVertexDeclaration(Vertices::g_FVFTable[VT_3NT2T2]);

	m_iVBWritePos = 0;
	m_iVBReadPos = 0;

	m_xAABB.m_vMin = CVec3(0.0f, -1.0f, 0.0f);
	m_xAABB.m_vMax = CVec3(m_vWorldSpaceScaling.x(), 1.0f, m_vWorldSpaceScaling.z());
}

//---------------------------------------------------------------------------------------------------------------------
void
CSinusWaterTile::FillVertexBuffer()
{
	// write vertex buffer

	Vertex3NT2T2* pVertexBuffer = 0;
	m_iVBReadPos = m_iVBWritePos;
	HRESULT hr;


	hr = m_hVertexBuffer->Lock(m_iVBWritePos * sizeof(Vertex3NT2T2), m_iNumVertices * sizeof(Vertex3NT2T2), 
		(void**) &pVertexBuffer, D3DLOCK_NOOVERWRITE);
	assert(SUCCEEDED(hr));
	assert(pVertexBuffer);

	m_iVBWritePos += m_iNumVertices;
	if(m_iVBWritePos + m_iNumVertices > m_iVertexBufferSize)
	{
		m_iVBWritePos = 0;
	}

	int iIndex = 0;

	float uv1start = 0.5f / ((m_iWidthInVertices-1));
	float uv1step = (1.0f - 2.0f*uv1start) / ((m_iWidthInVertices-1)); 

	int	iPeriodes = 5;				// number of wave periodes in one tile
//	int	iTime = (int) C3DView2::Get()->GetSimTimeCtrl()->GetContinuousSimTime() * 100.0;
	static int iTime = 1;
	iTime++;

	float v1 = uv1start;
	for (int z = 0; z < m_iWidthInVertices; ++z)
    {
		float fZ = (float) z / (float) (m_iWidthInVertices-1) * m_vWorldSpaceScaling.z();
		float u1 = uv1start;
		for (int x = 0; x < m_iWidthInVertices; ++x)
        {
			float fX = (float) x / (float) (m_iWidthInVertices-1) * m_vWorldSpaceScaling.x();

			int iTick = iTime % m_iCycleDuration;
			float fSinStep = (float) m_iCycleDuration * iPeriodes / (float) (m_iWidthInVertices-1);
			int iXSin1 = (iTick +   0 + (int)(fSinStep * x)) % m_iCycleDuration;
			int iXSin2 = (iTick +  10 + (int)(fSinStep * x)) % m_iCycleDuration / 2;
			int iYSin1 = (iTick +  30 + (int)(fSinStep * z)) % m_iCycleDuration;
			int iYSin2 = (iTick +   0 + (int)(fSinStep * z)) % m_iCycleDuration / 2;

			float fY = m_pxSinusTable[iXSin1] + m_pxSinusTable[iXSin2] +
						m_pxSinusTable[iYSin1] + m_pxSinusTable[iYSin2];

//			fY = 0.0f;
			pVertexBuffer[iIndex].p = CVec3(fX, fY, fZ);

			pVertexBuffer[iIndex].t0.x() = (float) x * m_fTextureRepeatitions / (m_iWidthInVertices-1);
			pVertexBuffer[iIndex].t0.y() = (float) z * m_fTextureRepeatitions / (m_iWidthInVertices-1);

			pVertexBuffer[iIndex].t1.x() = u1;
			pVertexBuffer[iIndex].t1.y() = v1;

			u1 += uv1step;

			CVec3 vNormal = CVec3(0.0f, 1.0f, 0.0f);
			pVertexBuffer[iIndex].n = vNormal;

			iIndex++;
        }
		v1 += uv1step;
    }

	hr = m_hVertexBuffer->Unlock();
	assert(SUCCEEDED(hr));
}

//---------------------------------------------------------------------------------------------------------------------
void							
CSinusWaterTile::Tick(double p_dTimeInSeconds)
{
	FillVertexBuffer();
}

//---------------------------------------------------------------------------------------------------------------------
void		
CSinusWaterTile::Render(TRenderContextPtr spxRenderContext, const CMat4S* p_pmatWorldViewProj, const CMat4S* p_pmatWorld)
{
	HRESULT hr;

    CEngineController* pxEngineController = m_pxEngineController;
    CDeviceStateMgr* pd3dDeviceStateMgr = pxEngineController->GetDeviceStateMgr();

	hr = pd3dDeviceStateMgr->SetTexture(0, m_hWaterReflectionMap.GetPtr());
	assert(SUCCEEDED(hr));

	pd3dDeviceStateMgr->SetStreamSource(0, m_hVertexBuffer.GetPtr(), 0, sizeof(Vertex3NT2T2));
	pd3dDeviceStateMgr->SetIndices(m_hIndexBuffer.GetPtr());
    pd3dDeviceStateMgr->SetVertexDeclaration( m_hVertexDeclaration.GetPtr());

	m_hWaterShader->SetWorldViewProjectionMatrix(*p_pmatWorldViewProj);
    m_hWaterShader->SetWorldMatrix(*p_pmatWorld);
//	m_hWaterShader->SetLightDirVector((D3DXVECTOR4*)&spxRenderContext->m_vLightDir.GetExtended(0));

	CEffectShader* pxShader = m_hWaterShader.GetPtr();
	pxShader->SetTechnique("standard");

	UINT uiNumPasses;
	pxShader->Begin(&uiNumPasses, D3DXFX_DONOTSAVESTATE);
	for (UINT uiPass = 0; uiPass < uiNumPasses; uiPass++)
	{
        // undo
        if ((uiPass > 0) && (uiPass == uiNumPasses - 1))
        {
            ID3DXEffect* pd3dEffect = pxShader->GetD3DXEffect();
            D3DXHANDLE hndPass = pd3dEffect->GetPass("standard", uiPass);
            assert(hndPass);

            if (hndPass)
            {
                D3DXPASS_DESC xPassDesc;
                xPassDesc.Name = 0;
                pd3dEffect->GetPassDesc(hndPass, &xPassDesc);

                if ((xPassDesc.Name) &&
                    (strcmp(xPassDesc.Name, "restore") == 0))
                {
                    pxShader->BeginPass(uiPass);
                    pxShader->EndPass();
                    break;
                }
            }
        }

		// rendern
		pxShader->BeginPass(uiPass);

		hr = pxEngineController->GetDevice()->DrawIndexedPrimitive(
			D3DPT_TRIANGLELIST, 
			m_iVBReadPos,			// BaseVertex 
			0,						// MinVertex
			m_iNumVertices,			// Number of Vertices
			0,						// Start Index
			m_iNumPrimitives);		// Primitive Count

		assert(SUCCEEDED(hr));
		pxShader->EndPass();
	}
	pxShader->End();
}

//---------------------------------------------------------------------------------------------------------------------

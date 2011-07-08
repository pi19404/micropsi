
#include "Application/stdinc.h"
#include "e42/core/EngineController.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/EffectShader.h"
#include "e42/Vertices.h"

#include "Engine/Terrain/flatterraintile.h"

//---------------------------------------------------------------------------------------------------------------------
/**
	this constructor creates a 'trivial', i.e. completely flat chunk with only one material
	these chunks are used to extend the map on the borders as far as necessary
*/
CFlatTerrainTile::CFlatTerrainTile(CEngineController* p_pxEngineController, CVec3 p_vWorldSpaceScaling, const char* p_pcTextureName, float p_fTextureRepeatitions)
{
	m_pxEngineController = p_pxEngineController;
	m_vWorldSpaceScaling = p_vWorldSpaceScaling;
	m_fTextureRepeatitions = p_fTextureRepeatitions;

	m_hShader = m_pxEngineController->GetEffectFactory()->CreateEffect("shader>terrain.fx");
	m_hTexture = p_pxEngineController->GetTextureFactory()->CreateTextureFromFile(std::string("texture>") + p_pcTextureName);

	m_iNumVertices = 4;
	m_iNumPrimitives = 2;

	// Index Buffer

	m_iNumIndices = 6;
	m_hIndexBuffer = m_pxEngineController->GetIndexBufferFactory()->CreateIndexBuffer(m_iNumIndices, D3DFMT_INDEX16, D3DUSAGE_WRITEONLY, D3DPOOL_MANAGED);

	unsigned short* piIndexBuffer;
	m_hIndexBuffer->Lock(0, 0, (void**) &piIndexBuffer, 0);
	assert(piIndexBuffer);
	for(int i=0; i<m_iNumIndices; ++i)
	{
		piIndexBuffer[0] = 3;
		piIndexBuffer[1] = 1;
		piIndexBuffer[2] = 0;
		piIndexBuffer[3] = 2;
		piIndexBuffer[4] = 3;
		piIndexBuffer[5] = 0;
	}
	m_hIndexBuffer->Unlock();


	// Vertex Buffer

	m_hVertexBuffer		  = m_pxEngineController->GetVertexBufferFactory()->CreateVertexBufferFVF(m_iNumVertices, Vertices::g_FVFTable[VT_3NT2T2], D3DUSAGE_WRITEONLY, D3DPOOL_MANAGED);
    m_hVertexDeclaration  = m_pxEngineController->GetVertexDeclarationFactory()->CreateVertexDeclaration(Vertices::g_FVFTable[VT_3NT2T2]);

	Vertex3NT2T2* pVertexBuffer;
	m_hVertexBuffer->Lock(0, 0, (void**) &pVertexBuffer, 0);
	assert(pVertexBuffer);

	pVertexBuffer[0].p = CVec3(0.0f, 0.0f, 0.0f);
	pVertexBuffer[0].t0 = CVec2(0.0f, 0.0f);
	pVertexBuffer[0].t1 = CVec2(0.0f, 0.0f);
	pVertexBuffer[0].n = CVec3(0.0f, 1.0f, 0.0f);

	pVertexBuffer[1].p = CVec3(m_vWorldSpaceScaling.x(), 0.0f, 0.0f);
	pVertexBuffer[1].t0 = CVec2(m_fTextureRepeatitions, 0.0f);
	pVertexBuffer[1].t1 = CVec2(1.0f, 0.0f);
	pVertexBuffer[1].n = CVec3(0.0f, 1.0f, 0.0f);

	pVertexBuffer[2].p = CVec3(0.0f, 0.0f, m_vWorldSpaceScaling.z());
	pVertexBuffer[2].t0 = CVec2(0.0f, m_fTextureRepeatitions);
	pVertexBuffer[2].t1 = CVec2(0.0f, 1.0f);
	pVertexBuffer[2].n = CVec3(0.0f, 1.0f, 0.0f);

	pVertexBuffer[3].p = CVec3(m_vWorldSpaceScaling.x(), 0.0f, m_vWorldSpaceScaling.z());
	pVertexBuffer[3].t0 = CVec2(m_fTextureRepeatitions, m_fTextureRepeatitions);
	pVertexBuffer[3].t1 = CVec2(1.0f, 1.0f);
	pVertexBuffer[3].n = CVec3(0.0f, 1.0f, 0.0f);

	m_hVertexBuffer->Unlock();

	m_xAABB.m_vMin = CVec3(0.0f, 0.0f, 0.0f);
	m_xAABB.m_vMax = CVec3(m_vWorldSpaceScaling.x(), 0.1f, m_vWorldSpaceScaling.z());
}


//---------------------------------------------------------------------------------------------------------------------

CFlatTerrainTile::~CFlatTerrainTile()
{
}

//---------------------------------------------------------------------------------------------------------------------
void		
CFlatTerrainTile::Render(TRenderContextPtr spxRenderContext, const CMat4S* p_pmatViewProj, const CMat4S* p_pmatWorld, float p_fDistanceFromViewer) const
{
	p_fDistanceFromViewer; // unused parameter

	CMat4S matWorldViewProj;
    matWorldViewProj = *p_pmatWorld * *p_pmatViewProj;

	HRESULT hr;

    CEngineController* pxEngineController = m_pxEngineController;
    CDeviceStateMgr* pd3dDeviceStateMgr = pxEngineController->GetDeviceStateMgr();

	pd3dDeviceStateMgr->SetStreamSource(0, m_hVertexBuffer.GetPtr(), 0, sizeof(Vertex3NT2T2));
	pd3dDeviceStateMgr->SetIndices(m_hIndexBuffer.GetPtr());
    pd3dDeviceStateMgr->SetVertexDeclaration( m_hVertexDeclaration.GetPtr());

	CEffectShader* pxShader = m_hShader.GetPtr();
	pxShader->SetTechnique("trivial_shadowed");
	pxShader->SetWorldViewProjectionMatrix(matWorldViewProj);
    pxShader->SetWorldMatrix(*p_pmatWorld);
	pxShader->SetDiffuseMap(m_hTexture);

	pxShader->GetD3DXEffect()->SetBool("c_bBlending", false);

	UINT uiNumPasses;
	pxShader->Begin(&uiNumPasses, D3DXFX_DONOTSAVESTATE);
	for (UINT uiPass = 0; uiPass < uiNumPasses; uiPass++)
	{
		// rendern
		pxShader->BeginPass(uiPass);

		hr = pxEngineController->GetDevice()->DrawIndexedPrimitive(
			D3DPT_TRIANGLELIST, 
			0,						// BaseVertex 
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
bool							
CFlatTerrainTile::HitTest(const CVec3& p_rvWorldPos, const CRay& p_rxRay, CVec3& po_rxCollisionPoint) const
{
	CPlane xPlane(CVec3(0.0f, 1.0f, 0.0f), p_rvWorldPos);
	CVec3 vHitPoint;
	if(xPlane.HitTest(p_rxRay, vHitPoint))
	{
		if(vHitPoint.x() >= p_rvWorldPos.x()  &&  
		   vHitPoint.z() >= p_rvWorldPos.y()  && 
		   vHitPoint.x() <= p_rvWorldPos.x() + m_vWorldSpaceScaling.x()  && 
		   vHitPoint.z() <= p_rvWorldPos.z() + m_vWorldSpaceScaling.z())
		{
			po_rxCollisionPoint = vHitPoint;
			return true;
		}
	}
	return false;
}
//---------------------------------------------------------------------------------------------------------------------


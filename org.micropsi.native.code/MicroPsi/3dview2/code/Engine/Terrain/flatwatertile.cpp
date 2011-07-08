
#include "Application/stdinc.h"
#include "Application/3dview2.h"

#include "e42/camera.h"
#include "e42/Vertices.h"
#include "e42/core/EngineController.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/EffectShader.h"
#include "baselib/geometry/Triangle.h"

#include "Engine/Terrain/flatwatertile.h"


//---------------------------------------------------------------------------------------------------------------------
CFlatWaterTile::CFlatWaterTile(CEngineController* p_pxEngineController, CVec3 p_vWorldSpaceScaling, float p_fTextureRepeatitions) : CWaterTile(p_pxEngineController)
{
	m_vWorldSpaceScaling = p_vWorldSpaceScaling;
	m_fTextureRepeatitions = p_fTextureRepeatitions;

	m_fCurrentTime		= 0.0f;
	
	m_hWaterShader		= m_pxEngineController->GetEffectFactory()->CreateEffect("shader>water.fx");
	m_hWaterDiffusemap	= p_pxEngineController->GetTextureFactory()->CreateTextureFromFile("texture>water.tga");
	m_hWaterBumpMap		= p_pxEngineController->GetTextureFactory()->CreateTextureFromFile("texture>water_bump.dds");
	m_hWaterBumpMap2	= p_pxEngineController->GetTextureFactory()->CreateTextureFromFile("texture>water_bump2.tga");

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
	m_xAABB.m_vMax = CVec3(m_vWorldSpaceScaling.x(), 0.0f, m_vWorldSpaceScaling.z());
}

//---------------------------------------------------------------------------------------------------------------------

CFlatWaterTile::~CFlatWaterTile()
{
}

//---------------------------------------------------------------------------------------------------------------------
void							
CFlatWaterTile::Tick(double p_dTimeInSeconds)
{
	m_fCurrentTime = (float) p_dTimeInSeconds;
	// this tile is not animated - simply do nothing :)
}

//---------------------------------------------------------------------------------------------------------------------
void		
CFlatWaterTile::Render(TRenderContextPtr spxRenderContext, const CMat4S* p_pmatWorldViewProj, const CMat4S* p_pmatWorld)
{
	HRESULT hr;

    CEngineController* pxEngineController = m_pxEngineController;
    CDeviceStateMgr* pd3dDeviceStateMgr = pxEngineController->GetDeviceStateMgr();

	pd3dDeviceStateMgr->SetStreamSource(0, m_hVertexBuffer.GetPtr(), 0, sizeof(Vertex3NT2T2));
	pd3dDeviceStateMgr->SetIndices(m_hIndexBuffer.GetPtr());
    pd3dDeviceStateMgr->SetVertexDeclaration( m_hVertexDeclaration.GetPtr());

	m_hWaterShader->SetWorldViewProjectionMatrix(*p_pmatWorldViewProj);
    m_hWaterShader->SetWorldMatrix(*p_pmatWorld);
//	m_hWaterShader->SetLightDirVector((D3DXVECTOR4*)&spxRenderContext->m_vLightDir.GetExtended(0));

	CEffectShader* pxShader = m_hWaterShader.GetPtr();
	pxShader->SetTechnique("standard");
	pxShader->SetBumpMap(m_hWaterBumpMap);
	pxShader->SetNormalMap(m_hWaterBumpMap2);
	pxShader->SetEnvironmentMap(m_hWaterReflectionMap);
	pxShader->SetDiffuseMap(m_hWaterDiffusemap);
	pxShader->SetTime(m_fCurrentTime);

	CVec4 vEye = C3DView2::Get()->GetCamera()->GetPos().GetExtended();
	pxShader->SetEyePosition(vEye);

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
			0,						// BaseVertex 
			0,						// MinVertex
			m_iNumVertices,			// Number of Vertices
			0,						// Start Index
			m_iNumPrimitives);		// Primitive Count

		assert(SUCCEEDED(hr));
		pxShader->EndPass();
	}
	pxShader->End();

    pxShader->SetEnvironmentMap(TTextureHandle(NULL));
}

//---------------------------------------------------------------------------------------------------------------------

#include "stdafx.h"

#include "e42/LetterBoxCamera.h"
#include "e42/core/DeviceStateMgr.h"
#include "e42/core/EngineController.h"

//-------------------------------------------------------------------------------------------------------------------------------------------
CLetterBoxCamera::CLetterBoxCamera(CEngineController* pxEngineController)
:   m_pxEngineController(pxEngineController)
{
    m_fViewFrustumShrink = 0.0f;
}
//-------------------------------------------------------------------------------------------------------------------------------------------
CLetterBoxCamera::~CLetterBoxCamera()
{
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CLetterBoxCamera::UpdateClipPlanes()
{
    CDeviceStateMgr* pd3dDeviceStateMgr =
        m_pxEngineController->GetDeviceStateMgr();

    if (m_fViewFrustumShrink > 0)
    {
        const CVec4 vPlaneTop(0, -1.0f, 0, +1.0f - m_fViewFrustumShrink);
        const CVec4 vPlaneBtm(0, +1.0f, 0, +1.0f - m_fViewFrustumShrink);

        pd3dDeviceStateMgr->SetClipPlane(0, (const float*)&vPlaneTop);
        pd3dDeviceStateMgr->SetClipPlane(1, (const float*)&vPlaneBtm);

        pd3dDeviceStateMgr->SetRenderState(D3DRS_CLIPPLANEENABLE, D3DCLIPPLANE0 | D3DCLIPPLANE1);
    }
    else
    {
        pd3dDeviceStateMgr->SetRenderState(D3DRS_CLIPPLANEENABLE, 0);
    }
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CLetterBoxCamera::UpdateViewFrustum()
{
	m_ViewFrustum.Update(m_matViewProjectionInverse, m_fNearPlaneDistance, m_fFarPlaneDistance, m_bPerspective, m_bLeftHandedWorldCoordinateSystem, m_fViewFrustumShrink);
    UpdateClipPlanes();
}
//-------------------------------------------------------------------------------------------------------------------------------------------
void 
CLetterBoxCamera::SetScreenShrink(float fShrink)
{
    m_fViewFrustumShrink = fShrink;
}
//-------------------------------------------------------------------------------------------------------------------------------------------

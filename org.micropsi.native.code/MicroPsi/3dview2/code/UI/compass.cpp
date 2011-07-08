#include "Application/stdinc.h"
#include "UI/compass.h"

#include "e42/core/EngineController.h"

#include "uilib/core/directx9device.h"
#include "uilib/core/windowmanager.h"
#include "uilib/controls/label.h"

#include "Application/3dview2.h"
#include "Observers/Observer.h"


//---------------------------------------------------------------------------------------------------------------------
CCompass::CCompass()
{
    CEngineController* pxEngineController = C3DView2::Get();

	int iWindowWidth = C3DView2::Get()->GetWindowWidth();
	int iWindowHeight = C3DView2::Get()->GetWindowHeight();

	m_pxBGLabel = UILib::CLabel::Create();
	m_pxBGLabel->SetBitmap("compass.png");
	m_pxBGLabel->SetWriteAlpha(true);
	int iBGWidth = m_pxBGLabel->GetBitmap()->GetSize().cx;
	int iBGHeight = m_pxBGLabel->GetBitmap()->GetSize().cy;

	m_pxBGDevice = new UILib::CDirectX9Device(pxEngineController->GetDevice(), iBGWidth, iBGHeight, iWindowWidth, iWindowHeight);
    m_pxBGDevice->EnableBlendShader();

	UILib::CWindowMgr::Get().AddDevice(m_pxBGDevice, CRct(0, 0, iBGWidth, iBGHeight), m_pxBGLabel);
	m_pxBGDevice->SetDeviceTranslation((float) C3DView2::Get()->GetWindowWidth() - iBGWidth, (float) C3DView2::Get()->GetWindowHeight() - iBGHeight);


	m_pxPointerLabel = UILib::CLabel::Create();
	m_pxPointerLabel->SetBitmap("compass2.png");
	m_pxPointerLabel->SetWriteAlpha(true);
	int iPointerWidth = m_pxPointerLabel->GetBitmap()->GetSize().cx;
	int iPointerHeight = m_pxPointerLabel->GetBitmap()->GetSize().cy;

	m_pxPointerDevice = new UILib::CDirectX9Device(pxEngineController->GetDevice(), iPointerWidth, iPointerHeight, iWindowWidth, iWindowHeight);
    m_pxPointerDevice->EnableBlendShader();
	UILib::CWindowMgr::Get().AddDevice(m_pxPointerDevice, CRct(0, 0, iPointerWidth, iPointerHeight), m_pxPointerLabel);
	m_pxPointerDevice->SetDeviceTranslation((float) C3DView2::Get()->GetWindowWidth() - iPointerWidth, (float) C3DView2::Get()->GetWindowHeight() - iPointerHeight);
}

//---------------------------------------------------------------------------------------------------------------------
CCompass::~CCompass()
{
	UILib::CWindowMgr::Get().RemoveDevice(m_pxPointerDevice);
	UILib::CWindowMgr::Get().RemoveDevice(m_pxBGDevice);
	m_pxPointerLabel->Destroy();
	m_pxBGLabel->Destroy();

	delete m_pxPointerDevice;
	delete m_pxBGDevice;
}

//---------------------------------------------------------------------------------------------------------------------
void CCompass::Render()
{
	float fRot = C3DView2::Get()->GetCurrentObserver()->GetAngleXZ();
	m_pxPointerDevice->SetDeviceRotation(-PIf/2.0f - fRot);

	
	m_pxBGDevice->Render2D();
	m_pxPointerDevice->Render2D();
}

//---------------------------------------------------------------------------------------------------------------------



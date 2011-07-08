#include "stdafx.h"

#include "e42/core/EngineController.h"

#include "baselib/filelocator.h"

#include "e42/core/EngineStats.h"

#include "e42/core/DeviceStateMgr.h"
#include "e42/core/XFileLoader.h"

#include "e42/core/PixelShaderFactory.h"
#include "e42/core/VertexShaderFactory.h"
#include "e42/core/TextureFactory.h"
#include "e42/core/VertexDeclarationFactory.h"
#include "e42/core/IndexBufferFactory.h"
#include "e42/core/VertexBufferFactory.h"
#include "e42/core/SurfaceFactory.h"
#include "e42/core/EffectFactory.h"
#include "e42/core/ModelFactory.h"
#include "e42/core/AnimationFactory.h"
#include "e42/core/MeshFactory.h"


//-----------------------------------------------------------------------------------------------------------------------------------------
CEngineController*	CEngineController::ms_pxEngineControllerSingleton = NULL;
unsigned int		CEngineController::ms_iNumEngineControllerInstances = 0;
//-----------------------------------------------------------------------------------------------------------------------------------------
CEngineController::CEngineController()
:   m_pd3d                          (NULL),
    m_pd3dDevice                    (NULL),
    m_pxDeviceCaps                  (NULL),
    m_pd3dDeviceStateMgr            (NULL),

    m_pxFileLocator                 (NULL),
    m_pxXFileLoader                 (NULL),
    m_pxEngineStats                 (NULL),

    m_pxPixelShaderFactory          (NULL),
    m_pxVertexShaderFactory         (NULL),
    m_pxTextureFactory              (NULL),
    m_pxVertexDeclarationFactory    (NULL),
    m_pxIndexBufferFactory          (NULL),
    m_pxVertexBufferFactory         (NULL),
    m_pxSurfaceFactory              (NULL),
    m_pxEffectFactory               (NULL),
    m_pxModelFactory                (NULL),
    m_pxAnimationFactory            (NULL),
    m_pxMeshFactory                 (NULL),

    m_dEngineTime                   (0),
    m_bEffectDebuggingEnabled       (false),
    m_bErrorMessagesEnabled         (true)
{
	if (ms_iNumEngineControllerInstances == 0)
	{
		assert(ms_pxEngineControllerSingleton == NULL);
		ms_pxEngineControllerSingleton = this;
	}
	ms_iNumEngineControllerInstances++;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
CEngineController::~CEngineController()
{
	ms_iNumEngineControllerInstances--;
	if (ms_iNumEngineControllerInstances == 0)
	{
		ms_pxEngineControllerSingleton = NULL;
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CEngineController::InitEngineController(IDirect3D9* pd3d, IDirect3DDevice9* pd3dDevice)
{
    // Direct3D-Basisschnittstellen übernehmen
    m_pd3d = pd3d;
    m_pd3dDevice = pd3dDevice;


    // Device Caps auslesen
    assert(m_pxDeviceCaps == 0);
    m_pxDeviceCaps = new _D3DCAPS9;
    ZeroMemory(m_pxDeviceCaps, sizeof(_D3DCAPS9));
    m_pd3dDevice->GetDeviceCaps(m_pxDeviceCaps);


    // Format des Backbuffers bestimmen
	CComObjectPtr<IDirect3DSurface9> spxBackBuffer;
	HRESULT hr = m_pd3dDevice->GetBackBuffer(0, 0, D3DBACKBUFFER_TYPE_MONO, &spxBackBuffer);
	assert(SUCCEEDED(hr));
	spxBackBuffer->GetDesc(&m_xBackbufferDesc);
    spxBackBuffer = NULL;


    // E42-Core-Objekte erzeugen
    m_pxFileLocator =                   new CFileLocator();
    m_pxEngineStats =                   new CEngineStats();

    m_pd3dDeviceStateMgr =              CDeviceStateMgr::Create(this);

    m_pxPixelShaderFactory =            new CPixelShaderFactory(this);
    m_pxVertexShaderFactory =           new CVertexShaderFactory(this);
    m_pxTextureFactory =                new CTextureFactory(this);
    m_pxVertexDeclarationFactory =      new CVertexDeclarationFactory(this);
    m_pxIndexBufferFactory =            new CIndexBufferFactory(this);
    m_pxVertexBufferFactory =           new CVertexBufferFactory(this);
    m_pxSurfaceFactory =                new CSurfaceFactory(this);
    m_pxEffectFactory =                 new CEffectFactory(this);

    m_pxXFileLoader =                   new CXFileLoader(this);
    m_pxAnimationFactory =              new CAnimationFactory(this);
    m_pxMeshFactory =                   new CMeshFactory(this);
    m_pxModelFactory =                  new CModelFactory(this);

    m_dEngineTime = 0;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void 
CEngineController::ShutEngineController()
{
    delete m_pxModelFactory;                m_pxModelFactory = NULL;            // erzeugt auch Effect, Textur- & Anim-Handles
    delete m_pxMeshFactory;                 m_pxMeshFactory = NULL;
    delete m_pxAnimationFactory;            m_pxAnimationFactory = NULL;
    delete m_pxXFileLoader;                 m_pxXFileLoader = NULL;

    delete m_pxEffectFactory;               m_pxEffectFactory = NULL;
    delete m_pxSurfaceFactory;              m_pxSurfaceFactory = NULL;
    delete m_pxVertexBufferFactory;         m_pxVertexBufferFactory = NULL;
    delete m_pxIndexBufferFactory;          m_pxIndexBufferFactory = NULL;
    delete m_pxVertexDeclarationFactory;    m_pxVertexDeclarationFactory = NULL;
    delete m_pxTextureFactory;              m_pxTextureFactory = NULL;
    delete m_pxVertexShaderFactory;         m_pxVertexShaderFactory = NULL;
    delete m_pxPixelShaderFactory;          m_pxPixelShaderFactory = NULL;

    if (m_pd3dDeviceStateMgr)
        m_pd3dDeviceStateMgr->Release();

    delete m_pxEngineStats;                 m_pxEngineStats = NULL;
    delete m_pxFileLocator;                 m_pxFileLocator = NULL;

    delete m_pxDeviceCaps;                  m_pxDeviceCaps = NULL;

    m_pd3dDevice = NULL;
    m_pd3d = NULL;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CEngineController::GetPixelShaderDebuggingEnabled() const
{
    return false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CEngineController::GetVertexShaderDebuggingEnabled() const
{
    return false;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
bool
CEngineController::GetEffectDebuggingEnabled() const
{
    return m_bEffectDebuggingEnabled;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CEngineController::SetEffectDebuggingEnabled(bool bEnabled)
{
    m_bEffectDebuggingEnabled = bEnabled;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
void
CEngineController::PrecacheData(bool bOpenScene) const
{
    if (bOpenScene)
        m_pd3dDevice->BeginScene();

    m_pxTextureFactory->PrecacheTextures();

    if (bOpenScene)
        m_pd3dDevice->EndScene();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
float
CEngineController::GetAspectRatio(int iHead) const
{
	return 4.0f / 3.0f;
}
//-----------------------------------------------------------------------------------------------------------------------------------------


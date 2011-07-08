#ifndef E42_DX9APPSETTINGS_H_INCLUDED
#define E42_DX9APPSETTINGS_H_INCLUDED

#include <string>
#include <windows.h>
#include <d3d9types.h>

#include "baselib/DynArray.h"

class CAppSettings
{
public:

	CAppSettings();
	~CAppSettings();


	void Init();																///< setzt Parameter auf Default
	void InitByCommandLine(const std::string& sCommandLine);					///< setzt Parameter aus übergebener Kommandozeile
	void InitFromDialog();														///< zeigt Dialog an und setzt danach Parameter

	void SetPresentParameters(D3DPRESENT_PARAMETERS* pxPresentParametersOut);	///< setzt eine D3DPRESENT_PARAMETERS-Struktur nach den aktuellen Settings


	// Window-Props
	int					m_iWindowWidth;
	int					m_iWindowHeight;
	bool				m_bFullScreen;

	
	// D3D-Settings
	UINT				m_D3DAdapter;
	D3DDEVTYPE			m_D3DDeviceType;
	DWORD				m_D3DBehaviorFlags;

	bool				m_bUseSDKVersion90b;
	D3DFORMAT			m_DepthStencilFormat;
	bool				m_bUseMultipleHeads;
	bool				m_bMultiHeadDebugFake;
	bool				m_bPerfHudMode;				///< Schalter für NVIDIA PerfHud-Profiler
	bool				m_bAutoDepthStencil;
	bool				m_bTripleBuffering;
	bool				m_bVSyncEnable;
	DWORD				m_dwMaxMultiSampleQuality;


	// DInput-Flags
	unsigned int		m_uiMouseCoopFlags;
	unsigned int		m_uiKeyboardCoopFlags;
	unsigned int		m_uiGamepadCoopFlags;
};

#endif // E42_DX9APPSETTINGS_H_INCLUDED

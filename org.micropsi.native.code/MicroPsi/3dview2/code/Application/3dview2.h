#ifndef C3DVIEW2_H_INCLUDED
#define C3DVIEW2_H_INCLUDED

#include "stdinc.h"

#include "GameLib/Utilities/ExtendedConfigFile.h"

#include "e42/E42Application.h"
#include "e42/Camera.h"
#include "e42/core/Model.h"

class CObserver;
class CObserverControllerSwitcher;
class CWorld;
class CUIScreenStateMachine;
class CInputManager;
class CSimTimeCtrl;
class CRenderTargetTexture;
class CPerformanceWindow;
class CCommunicationModule;

//------------------------------------------------------------------------------
class C3DView2 : public CE42Application
{
public:
    float GetWindowAspectRatio() const;

    C3DView2(HINSTANCE hInstance);
    ~C3DView2();

	static C3DView2*				Get();

	static void						SetParentWindow(HWND hWnd);	
	static HWND						GetParentWindow();
	static void						SetBasePath(std::string p_sPath);
	static void						SetCommandLine(std::string p_sCommandLine);

	CWorld*							GetWorld() const;
	const CCamera*					GetCamera() const;
	CObserver*						GetCurrentObserver() const;
	CObserverControllerSwitcher*	GetObserverControllerSwitcher() const;
	const CInputManager*			GetInputManager() const;
	const CSimTimeCtrl*				GetSimTimeCtrl() const;
	CCommunicationModule*			GetCommunicationModule() const;
	const CConfigFile&				GetConfiguration() const;

	bool							IsRunning() const;

	class CPerformanceData
	{
	public:
		float						m_fAvgFPS;							///< average frames per second
		int							m_iFirstPassTerrainTiles;			///< terrain tiles visible in first pass
		int							m_iSecondPassTerrainTiles;			///< terrain tiles visible in second pass
		int							m_iFirstPassObjects;				///< objects visible in first pass
		int							m_iSecondPassObjects;				///< objects visible in second pass
		int							m_iWaterReflectionObjects;			///< objects visible in water reflection
		int							m_iShadowMapObjects;				///< objects visible in shadow map
	};

	const CPerformanceData&			GetPerformanceData() const;

private:

	bool							m_bIsRunning;				///< true while the application is running

	CConfigFile						m_xConfiguration;			///< global configuration	

	CRenderTargetTexture*			m_pxWaterReflectionTexture;	///< Render Target for Water Reflections
	bool							m_bShowReflectionTexture;	///< true: water reflection texture is shown as a small image
	bool							m_bWaterReflectsShadows;	///< true: water reflection shows object shadows

	CRenderTargetTexture*			m_pxShadowMapTexture;		///< Render Target for Shadow Mapping
	TTextureHandle					m_hShadowFadeTexture;		///< Texture for shadow fading
	bool							m_bShowShadowMap;			///< true: shadow map shown as a small image
	bool							m_bShadowsEnabled;			///< true: shadows are enabled
	bool							m_bShadowCastersVisible;	///< true: shadow casters are visible at the moment

	bool							m_bWireframe;				///< true: render in wireframe mode
	bool							m_bDebugKeysEnabled;		///< true: debug keys are available
	int								m_iDefaultTextureResolution;///< default texture resolution
	std::string						m_sShaderVersion;			///< shader version to use: dx7, dx8, dx9
	float							m_fRangeOfVision;			///< range of vision
	float							m_fMaxObjDistance;			///< range of vision for objects
	float							m_fMaxObjReflectionDistance;///< range of vision for objects in reflection
	bool							m_bDistancePasses;			///< make two passes for different distances to avoid z-fighting

    double							m_dTimeOfCurrentFrame;		///< system timer at start of current frame
    double							m_dDurationOfLastFrame;		///< duration of last frame

	CVec3							m_vLightDirection;			///< direction of light

	CPerformanceData				m_xPerformanceData;			///< statistic info about system performance; used for debug purposes only
	CPerformanceWindow*				m_pxPerformanceWindow;		///< window that displays performance information

	CCamera							m_xCamera;					///< engine camera
	CObserver*						m_pxObserver;				///< virtual camera; can be used the set up the real camera
	CObserverControllerSwitcher*	m_pxObserverControllerSwitcher;	///< owns different (input) controllers for the observer

	CUIScreenStateMachine*		m_pxUIScreenStateMachine;	///< keeps track of the graphical user interface
	CInputManager*					m_pxInputManager;			///< keeps track of input devices
	CSimTimeCtrl*					m_pxSimTimeCtrl;			///< keeps track of simulation time			
	CCommunicationModule*			m_pxCommunicationModule;	///< keeps track of all communication with server; supports different methods

	CWorld*							m_pxWorld;					///< the world contains terrain, objects and sky

	static C3DView2*				ms_pxInstance;				///< one and only instance of this class
	static HWND						ms_hParentWindow;			///< parent window the 3d window is supposed to use (may be NULL)
	static std::string				ms_sBasePath;				///< path to executable
	static std::string				ms_sCommandLine;			///< command line

    // overloaded functions from E42Application:
    void			CreateScene();
    void			OnIdle();
	void			Terminate();
    bool			OnWindowMessage(UINT msg, WPARAM wParam, LPARAM lParam);

	// own functions

    void			Render();	
	void			SetDefaultSamplerState();
	void			SetDefaultRenderState();

	void			SetFog(bool p_bEnabled);
	void			RenderEverything();
	void			RenderSky();
	void			RenderWaterReflections();
	void			RenderShadowMap();

	void			SetDefaultConfig(); 	
	void			ReadCommandLineOptions();
	void			SetInputMapping();
	void			SetFilesystemMapping();
	void			SelectShaderVersion();
	void			CreateWaterReflectionTexture();
	void			CreateShadowMapTexture();

	void			AttachToParentWindow();
	void			UpdateFPS();
	void			SetFog(DWORD p_dwColor, float p_fFogStart, float p_fFogEnd);
	void			CheckDebugKeys();
	void			UpdateLightDirection();
	void			TakeStartUpActions();

	void			OnSimulationTick();
};

#include "3dview2.inl"

//------------------------------------------------------------------------------

#endif // C3DVIEW2_H_INCLUDED

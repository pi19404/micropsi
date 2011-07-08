#ifndef C3DEMOTION_H_INCLUDED
#define C3DEMOTION_H_INCLUDED

#include "stdinc.h"

#include "baselib/configfile.h"

#include "GameLib/GameLibApplication.h"
#include "e42/Camera.h"
#include "e42/core/Model.h"

class CWindowSystemController;
class CSimTimeCtrl;
class CFace;

//------------------------------------------------------------------------------
class C3DEmotion : public CGameLibApplication
{
public:
    C3DEmotion(HINSTANCE hInstance);
    ~C3DEmotion();

	static C3DEmotion*				Get();

	static void						SetParentWindow(HWND hWnd);	
	static HWND						GetParentWindow();
	static void						SetBasePath(std::string p_sPath);
	static void						SetCommandLine(std::string p_sCommandLine);

	const CCamera*					GetCamera() const;
	const CSimTimeCtrl*				GetSimTimeCtrl() const;
	CFace*							GetFace() const;

	bool							IsRunning() const;

private:

	bool							m_bIsRunning;				///< true while the application is running
    bool							m_bWideScreen;				///< true if the application is running in widescreen mode

	CConfigFile						m_xConfiguration;			///< global configuration	

	bool							m_bWireframe;				///< true: render in wireframe mode
	bool							m_bDebugKeysEnabled;		///< true: debug keys are available
	int								m_iDefaultTextureResolution;///< default texture resolution
	std::string						m_sShaderVersion;			///< shader version to use: dx7, dx8, dx9

	float							m_fAvgFPS;					///< average frames per second

	CCamera							m_xCamera;					///< engine camera

	CSimTimeCtrl*					m_pxSimTimeCtrl;			///< keeps track of simulation time			
	
	CFace*							m_pxFace;					///< the 3D face object

	static C3DEmotion*				ms_pxInstance;				///< one and only instance of this class
	static HWND						ms_hParentWindow;			///< parent window the 3d window is supposed to use (may be NULL)
	static std::string				ms_sBasePath;				///< path to executable
	static std::string				ms_sCommandLine;			///< command line

    // overloaded functions from E42Application:
    void			CreateScene();
	void			Terminate();

	virtual void	Input();
	virtual void	Update();
	virtual void	Output();

	// own functions

    void			Render();	
	void			SetDefaultSamplerState();
	void			SetDefaultRenderState();

	void			SetDefaultConfig(); 	
	void			ReadCommandLineOptions();
	void			SetInputMapping();
	void			SetFilesystemMapping();
	void			SelectShaderVersion();

	void			AttachToParentWindow();
	void			UpdateFPS();
	void			CheckDebugKeys();
	void			MoveCamera();

	void			OnSimulationTick();
};

#include "3DEmotion.inl"

//------------------------------------------------------------------------------

#endif // C3DEMOTION_H_INCLUDED

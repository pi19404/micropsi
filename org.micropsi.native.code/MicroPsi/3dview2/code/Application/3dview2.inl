//---------------------------------------------------------------------------------------------------------------------
inline
CWorld*		
C3DView2::GetWorld() const
{
	return m_pxWorld;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CCamera*	
C3DView2::GetCamera() const
{
	return &m_xCamera;
}
//---------------------------------------------------------------------------------------------------------------------
inline 
C3DView2*
C3DView2::Get()
{
	return ms_pxInstance;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
C3DView2::IsRunning() const
{
	return m_bIsRunning;
}
//---------------------------------------------------------------------------------------------------------------------
inline 
void
C3DView2::SetParentWindow(HWND hWnd)		
{ 
	ms_hParentWindow = hWnd; 
}
//---------------------------------------------------------------------------------------------------------------------
inline 
HWND
C3DView2::GetParentWindow()
{ 
	return ms_hParentWindow; 
}
//---------------------------------------------------------------------------------------------------------------------
/**
	when the application is run as a dll the working directory will not be what the application expects
	therefore, ms_sBasePath will be initialized with the correct working dir before the application is started
*/
inline
void			
C3DView2::SetBasePath(std::string p_sPath)
{
	ms_sBasePath = p_sPath;
}
//---------------------------------------------------------------------------------------------------------------------
/**
	because the application can be run as a dll and dlls do not have command lines, there must be a way to 
	pass the command line string to the application in another way
	when run as a dll, ms_pcCommandLine will be initialized before the application is started
*/
inline
void			
C3DView2::SetCommandLine(std::string p_sCommandLine)
{
	ms_sCommandLine = p_sCommandLine;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CObserver*	
C3DView2::GetCurrentObserver() const
{
	return m_pxObserver;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CObserverControllerSwitcher*	
C3DView2::GetObserverControllerSwitcher() const
{
	return m_pxObserverControllerSwitcher;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const C3DView2::CPerformanceData&				
C3DView2::GetPerformanceData() const
{
	return m_xPerformanceData;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CInputManager*		
C3DView2::GetInputManager() const
{
	return m_pxInputManager;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CSimTimeCtrl*	
C3DView2::GetSimTimeCtrl() const
{
	return m_pxSimTimeCtrl;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CCommunicationModule*			
C3DView2::GetCommunicationModule() const
{
	return m_pxCommunicationModule;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CConfigFile&				
C3DView2::GetConfiguration() const
{
	return m_xConfiguration;	
}
//---------------------------------------------------------------------------------------------------------------------


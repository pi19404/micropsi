//---------------------------------------------------------------------------------------------------------------------
inline 
C3DEmotion*
C3DEmotion::Get()
{
	return ms_pxInstance;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
C3DEmotion::IsRunning() const
{
	return m_bIsRunning;
}
//---------------------------------------------------------------------------------------------------------------------
inline 
void
C3DEmotion::SetParentWindow(HWND hWnd)		
{ 
	ms_hParentWindow = hWnd; 
}
//---------------------------------------------------------------------------------------------------------------------
inline 
HWND
C3DEmotion::GetParentWindow()
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
C3DEmotion::SetBasePath(std::string p_sPath)
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
C3DEmotion::SetCommandLine(std::string p_sCommandLine)
{
	ms_sCommandLine = p_sCommandLine;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CCamera*	
C3DEmotion::GetCamera() const
{
	return &m_xCamera;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CSimTimeCtrl*	
C3DEmotion::GetSimTimeCtrl() const
{
	return m_pxSimTimeCtrl;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CFace*
C3DEmotion::GetFace() const
{
	return m_pxFace;
}
//---------------------------------------------------------------------------------------------------------------------

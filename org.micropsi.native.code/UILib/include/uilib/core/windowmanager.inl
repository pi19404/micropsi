//---------------------------------------------------------------------------------------------------------------------
inline
CWindowMgr& 
CWindowMgr::Get()							
{ 
    if(!ms_pxInst)
    {
        ms_pxInst = new CWindowMgr();
    }
    return *ms_pxInst; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
COutputDevice* 
CWindowMgr::GetDevice(WHDL p_hWnd) const
{
	assert(m_axAllWindows.IsValid(p_hWnd));
	return m_axAllWindows.Element(p_hWnd).m_pxDevice;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CWindow* 
CWindowMgr::GetWindow(WHDL p_hWnd) const
{
	TWindowInfo* p = m_axAllWindows.ElementPtr(p_hWnd);
	return p ? p->m_pxWnd : 0;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool 
CWindowMgr::IsValid(WHDL p_hWnd) const
{
	return GetWindow(p_hWnd) != 0;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CWindowMgr::DeleteWindowDelayed(CWindow* p_pxWindow)		
{ 
	m_apxDeleteList.Include(p_pxWindow); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
const COutputDevice* 
CWindowMgr::GetDeviceConst(WHDL p_hWnd) const
{
	TWindowInfo* p = m_axAllWindows.ElementPtr(p_hWnd);
	assert(p);
	return p ? p->m_pxDevice : 0;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const bool 
CWindowMgr::GetWindowProperty(WHDL p_hWnd, WindowProperties p_eProp) const
{
	TWindowInfo* p = m_axAllWindows.ElementPtr(p_hWnd);
	assert(p);
	return p ? (p->m_iWindowProps & p_eProp) != 0 : false;
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CWindowMgr::SetStandardVisualization(const CStr& p_rsType)		
{ 
	SetStandardVisualizationType(CFourCC(p_rsType)); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CWindowMgr::SetStandardVisualizationType(CFourCC p_xType)	
{ 
	m_xStandardVisType = p_xType; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
CFourCC	
CWindowMgr::GetStandardVisualizationType()					
{ 
	return m_xStandardVisType; 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CWindowMgr::SetMouseEnterAndLeaveMsg(WHDL p_hWnd, bool b)
{ 
	SetWindowProperty(p_hWnd, WP_WantMouseEnterAndLeave, b); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
void	
CWindowMgr::SetIndirectActivationMessages(WHDL p_hWnd, bool b)  
{ 
	SetWindowProperty(p_hWnd, WP_WantIndirectActivateMsg, b); 
}
//---------------------------------------------------------------------------------------------------------------------
inline
WHDL	
CWindowMgr::GetFocusWindow() const						
{ 
	return m_hTopWindow;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CPnt
CWindowMgr::GetMousePos() const							
{ 
	return m_xMousePos; 
}
//---------------------------------------------------------------------------------------------------------------------

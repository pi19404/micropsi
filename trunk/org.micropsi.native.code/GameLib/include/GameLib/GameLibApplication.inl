//-----------------------------------------------------------------------------------------------------------------------
inline
CGameLibApplication& 
CGameLibApplication::Get()
{
	return (CGameLibApplication&)CE42Application::Get();
}
//-----------------------------------------------------------------------------------------------------------------------
inline
CInputManager*
CGameLibApplication::GetInputManager() const
{
	return m_pxInputManager;
}
//-----------------------------------------------------------------------------------------------------------------------
inline
CUIScreenStateMachine*	
CGameLibApplication::GetUIScreenStateMachine() const
{
	return m_pxUIScreenStateMachine;
}
//-----------------------------------------------------------------------------------------------------------------------
inline
double
CGameLibApplication::GetTimeInSecondsSinceLastUpdate()
{
	return m_dTimeSinceLastUpdate;
}
//-----------------------------------------------------------------------------------------------------------------------
inline
void
CGameLibApplication::SetNumUIScreenDesktops(int iNumUIScreenDesktops)
{
	m_iNumUIScreenDesktops = iNumUIScreenDesktops;
}
//-----------------------------------------------------------------------------------------------------------------------

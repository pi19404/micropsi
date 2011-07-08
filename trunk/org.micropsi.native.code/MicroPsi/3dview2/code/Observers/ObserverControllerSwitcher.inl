//---------------------------------------------------------------------------------------------------------------------
inline
void		
CObserverControllerSwitcher::SetCurrentObserver(CObserver* p_pxObserver)
{
	m_pxObserver = p_pxObserver;
	if(m_pxCurrentController)
	{
		m_pxCurrentController->SetCurrentObserver(p_pxObserver);
	}
}
//---------------------------------------------------------------------------------------------------------------------
inline
CObserver*	
CObserverControllerSwitcher::GetCurrentObserver() const
{
	return m_pxObserver;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CObserverController*	
CObserverControllerSwitcher::GetCurrentObserverController() const
{
	return m_pxCurrentController;
}
//---------------------------------------------------------------------------------------------------------------------

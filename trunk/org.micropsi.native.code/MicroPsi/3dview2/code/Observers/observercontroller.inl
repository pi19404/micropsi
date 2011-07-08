//---------------------------------------------------------------------------------------------------------------------
inline
void		
CObserverController::SetCurrentObserver(CObserver* p_pxObserver)
{
	m_pxObserver = p_pxObserver;
}

//---------------------------------------------------------------------------------------------------------------------
inline
CObserver*	
CObserverController::GetCurrentObserver() const
{
	return m_pxObserver;
}

//---------------------------------------------------------------------------------------------------------------------

//-----------------------------------------------------------------------------------------------------------------------------------------
inline
double
CSimTimeCtrl::GetSimTimeFactor() const
{
    return m_dSimTimeFactor;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CSimTimeCtrl::SetSimTimeFactor(const double dFactor)
{
    m_dSimTimeFactor = dFactor;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// Achtung: diese Funktion sollte nur zum Zeitpunkt 0 gesetzt werden 
inline
void
CSimTimeCtrl::SetSimStepDuration(double dSimStepDuration)
{
    assert(m_i64CurrentSimStep <= 0);
    m_dSimStepDuration = dSimStepDuration;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
double
CSimTimeCtrl::GetSimStepDuration() const
{
    return m_dSimStepDuration;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
double 
CSimTimeCtrl::GetDiscreteSimTime() const
{
    return m_dDiscreteSimTime;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
double 
CSimTimeCtrl::GetContinuousSimTime() const
{
        return m_dContinuousSimTime;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
double 
CSimTimeCtrl::GetContinuousSimTimeDelta() const
{
    return m_dContinuousSimTimeDelta;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void 
CSimTimeCtrl::SetSimCallback(const CMemberCallback& rxCallback)
{
    m_xCallback = rxCallback;
}
//-----------------------------------------------------------------------------------------------------------------------------------------
inline
void
CSimTimeCtrl::SetPauseState(unsigned int iStateIdx, bool bEnable)
{
	assert(iStateIdx >= 0 && iStateIdx < 32);

	if (bEnable)
	{
		m_iPauseState |= 1 << iStateIdx;
	}
	else
	{
		m_iPauseState &= ~(1 << iStateIdx);
	}
}
//-----------------------------------------------------------------------------------------------------------------------------------------

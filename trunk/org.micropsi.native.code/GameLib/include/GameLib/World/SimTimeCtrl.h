/*******************************************************************************
 SimTimeCtrl.h
    Die Klasse CSimTimeCtrl erlaubt es, eine bestimmte Funktion mit einer fest
    definierten zeitlichen Frequenz zu aufzurufen. Dazu muss die Funktion 
    CSimTimeCtrl::ElapsTime regelmäßig aufgerufen werden, diese führt dann die
    gesetzte Callbackfunktion so oft aus, dass die definierte Frequenz 
    eingehalten wird.
    Zu diesem Zweck hat die Klasse einen eigenen Zeitzähler, der bei jedem 
    Aufruf um den übergebenen Zeitwert erhöht wird. 
*******************************************************************************/
#pragma once

#ifndef SIMTIMECTRL_H_INCLUDED
#define SIMTIMECTRL_H_INCLUDED

#include "baselib/MemberCallback.h"

class CSimTimeCtrl
{
private:
    CMemberCallback m_xCallback;

	unsigned int m_iPauseState;

    __int64 m_i64CurrentSimStep;

    double  m_dSimTimePassedSinceLastSimStep; 
    double  m_dDiscreteSimTime;
    double  m_dContinuousSimTime;
    double  m_dContinuousSimTimeDelta;
    double  m_dSimStepDuration;

    double  m_dSimTimeFactor;

public:
    CSimTimeCtrl();
    ~CSimTimeCtrl();

    void    ElapseTime(double dRealTimeOfLastFrame);
   
    void    Reset();

    void    SetSimCallback(const CMemberCallback& rxCallback);


    void    SetSimTimeFactor(const double dFactor);
    double  GetSimTimeFactor() const;

    void    SetSimStepDuration(const double dSimStepDuration);                          // Achtung: diese Funktion sollte wegen der impliziten Berechnung nur zum Zeitpunkt 0 verwendet werden werden 
    double  GetSimStepDuration() const;

    double  GetDiscreteSimTime() const;
    double  GetContinuousSimTime() const;
    double  GetContinuousSimTimeDelta() const;

	void	SetPauseState(unsigned int iStateIdx, bool bEnable);
};

#include "SimTimeCtrl.inl"

#endif // SIMTIMECTRL_H_INCLUDED


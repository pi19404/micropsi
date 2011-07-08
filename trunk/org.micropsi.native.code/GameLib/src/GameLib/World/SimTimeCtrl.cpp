#include "stdafx.h"

#include "GameLib/World/SimTimeCtrl.h"
#include <float.h>

//-----------------------------------------------------------------------------------------------------------------------------------------
// Konstruktor
CSimTimeCtrl::CSimTimeCtrl()
{
    m_dSimStepDuration = 1.0f;

    Reset();
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// Destruktor
CSimTimeCtrl::~CSimTimeCtrl()
{
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// alle Werte zurück auf Start
void
CSimTimeCtrl::Reset()
{
    m_dSimTimeFactor = 1.0f;

    m_i64CurrentSimStep = -1;

    m_dSimStepDuration = DBL_MAX;
    m_dSimTimePassedSinceLastSimStep = 0;

    m_dDiscreteSimTime = 0;
    m_dContinuousSimTime = 0;
    m_dContinuousSimTimeDelta = 0;

	m_iPauseState = 0;


#ifdef DEBUGWATCHES
    SetDebugWatch("d simtime: %.2f", &m_dDiscreteSimTime);
    SetDebugWatch("d simtimefact: %.2f", &m_dSimTimeFactor);
#endif 
}
//-----------------------------------------------------------------------------------------------------------------------------------------
// lässt eine gewisse Zeit verstreichen und ruft die gesetzte Callbackfunktion auf, sollte dies erforderlich sein um die 
// gesetzte Frequenz zu erreichen. Die Frequenz ist mit 1 / SimStepDuration gegeben.
void 
CSimTimeCtrl::ElapseTime(double dRTimeOfLastFrame)
{
    // Anmerkung:
    // die hier stattfindenden Berechnungen könnten zwar klarer ausgedrückt werden, jedoch
    // wird versucht die implizite Berechnung aus m_i64CurrentSimStep der Reihenebtwicklung
    // vorzuziehen, um so Probleme mit der Rechengenauigkeit zu vermeiden.


    double dSimTimePassed = dRTimeOfLastFrame * m_dSimTimeFactor;

	if (m_iPauseState != 0)
	{
		dSimTimePassed = 0;
	}
    
    m_dSimTimePassedSinceLastSimStep += dSimTimePassed;


    while (m_dSimTimePassedSinceLastSimStep >= m_dSimStepDuration)
    {
        m_i64CurrentSimStep++;
        m_dDiscreteSimTime = m_i64CurrentSimStep * m_dSimStepDuration;

        m_xCallback.Call();

        m_dSimTimePassedSinceLastSimStep -= m_dSimStepDuration;
        

        // hierbei wird zwar davon ausgegangen, dass der Simstep selbst 0 dauert 
        // (die ContinuesSimTime, die fürs Rendern benutzt wird, ist die vom Anfang des Frames)
        // aber das ist jetzt egal
        // bleibt die frage, ob bei 48Hz Sim überhaupt eine continous Simtime nötig ist.
    }


    double dNewContinousSimTime = m_dDiscreteSimTime + m_dSimTimePassedSinceLastSimStep;

    m_dContinuousSimTimeDelta = dNewContinousSimTime - m_dContinuousSimTime;

    m_dContinuousSimTime = dNewContinousSimTime;

    // Simulation ist jetzt up to date - jedenfalls relativ zum !BEGINN! der Funktion
}
//-----------------------------------------------------------------------------------------------------------------------------------------

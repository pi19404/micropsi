#ifndef GAMELIB_DYNAMICSWORLD_H_INCLUDED
#define GAMELIB_DYNAMICSWORLD_H_INCLUDED

#include "baselib/geometry/CVector.h"

class CDynamicsWorld
{
public:

	CDynamicsWorld();
	~CDynamicsWorld();

	/// Simulationsupdate, bewegt alle Objekte
	void	Update(double dDeltaTimeInSeconds);

	/// setzt den Gravitationsvektor in m/s, z.B. CVec(0.0f, -9.81f, 0.0f) (das ist das default)
	void	SetGravity(CVec3 vGravity);

	/// liefert den aktuellen Gravitationsvektor
	CVec3	GetGravity() const;

	/// setzt den ERP - bestimmt, wie schnell Constraint-Verletzungen repariert werden. Zulässige Werte 0..1, empfohlen 0.1 .. 0.8
	void	SetErrorReductionParameter(double dERP);

	/// liefert den ERP
	double	GetErrorReductionParameter() const;

	/// setzt den globalen CFM - Werte über 0 erlauben eine gewisse Verletzung von Constraints (bei zur CFM proportionaler Kraftanwendung)
	void	SetConstraintForceMixing(double dCFM);

	/// liefert aktuell eingestelle CFM
	double	GetConstraintForceMixing() const;
   
	/// bestimmt, wie tief Objekte ineinander einsinken dürfen - theoretisch 0.0, aber ein kleiner Wert (0.001) kann Zittern verhindern (ist das default)
	void	SetContactSurfaceLayer(double dLayer);

	/// liefert die aktuelle Dicke der Oberflächenschicht, d.h. wie tief Objekte ineinander eindringen dürfen
	double	GetContactSurfaceLayer() const;
	
	/// auf true setzen, um QuickStep zu benutzen. QuickStep ist ein schnelles Näherungsverfahren, funktioniert u.U. aber nicht so gut wie die korrekte Berechnung
	void	SetUseQuickStep(bool bQuickStep);

	/// liefert true, wenn aktuell das Quickstep-Verfahren zum Einsatz kommt
	bool	GetUseQuickStep() const;

	/// setzt die Anzahl Iterationen für das Quickstep-Verfahren; mehr = besser = langsamer
	void	SetQuickStepNumIterations(int iNum);

	/// liefert die Anzahl Iterationen, die das Quickstep-Verfahren macht
	int		GetQuickStepNumIterations() const;

	/// get pointer to ODE world representation
	dWorld*	GetODEWorld() const;

	/// cast auf ODE-dWorldID, fast alle ODE-Funktionen brauchen eine dWorldID
	operator dWorldID() const;

private:

	dWorld*				m_pWorld;
	bool				m_bQuickStep;
};

#include "Gamelib/Physics/DynamicsWorld.inl"

#endif GAMELIB_DYNAMICSWORLD_H_INCLUDED

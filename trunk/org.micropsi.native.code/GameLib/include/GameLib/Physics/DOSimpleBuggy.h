#ifndef GAMELIB_DYNAMICSOBJECTSIMPLEBUGGY_H_INCLUDED
#define GAMELIB_DYNAMICSOBJECTSIMPLEBUGGY_H_INCLUDED

#include "GameLib/Physics/DynamicsObject.h"

class dSphere;
class dHinge2Joint;

class CDOSimpleBuggy : public CDynamicsObject
{
public:

	CDOSimpleBuggy(CDynamicsSimulation* pSim);
	~CDOSimpleBuggy();

	virtual void	SetPosition(const CVec3& vPos);	
	virtual CVec3	GetPosition() const;
	virtual void	SetRotation(const CQuat& vQuat);
	virtual CQuat	GetRotation() const;

	/// Update --> sollte einmal vor jedem Physiksimulationsschritt gerufen werden
	void			Update();
	
	virtual	void	Render(const CCamera& camera);

	/// liefert aktuelle Geschwindigkeit in Km/h (gemessen am Chassis, nicht den Achsen!)
	float			GetVelocityInKmH() const;

private:

	enum Wheel
	{
		W_FrontLeft,
		W_FrontRight,
		W_RearLeft,
		W_RearRight,
		W_NumWheels
	};

	dBody*	m_pChassisBody;							///< Body für Chassis
	dBox*	m_pChassisGeometry;						///< Geometrie für Chassis

	struct TWheel
	{
		dBody*			m_pWheelBody;				///< Radkörper
		dSphere*		m_pWheelGeometry;			///< Radgeometrie
		dHinge2Joint*	m_pJoint;					///< Joint zur Aufhängung
	};

	TWheel		m_axWheels[W_NumWheels];			///< alle Räder

	float		m_fWheelRadius;						///< Radradius
	float		m_fWheelWidth;						///< Breite eines Rades
	float		m_fWheelMass;						///< Masse eines Rades

	float		m_fChassisMass;						///< Masse des Chassis
	CVec3		m_vChassisSize;						///< Größe des Chassis (x, y, z)
};


#endif // GAMELIB_DYNAMICSOBJECTSIMPLEBUGGY_H_INCLUDED


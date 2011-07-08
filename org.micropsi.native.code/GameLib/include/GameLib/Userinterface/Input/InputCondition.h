/**
	Syntax:

	keyboard:control.down
	keyboard:control.up
	keyboard:control.held
	keyboard:control.notheld
	keyboard:control.repeatevent

	joystick0:button0.down

	joystick0:axis0.value<0.5
	joystick0:axis0.value>=0.5
*/

#pragma once
#ifndef INPUTCONDITION_H_INCLUDED
#define INPUTCONDITION_H_INCLUDED

#include "InputCommons.h"
#include <vector>

class CInputCondition
{
public:
	CInputCondition();
    CInputCondition(const char* p_pcCondition, float p_fUserDefinesAxisValue);
	~CInputCondition();

	enum Condition
	{
		CD_INVALID,
		CD_BUTTONUP,				///< Taste runter (Flanke)
		CD_BUTTONDOWN,				///< Taste hoch (Flanke)
		CD_BUTTONREPEATEVENT,		///< Taste ist unten, wiederholtes Event
		CD_ANYBUTTON,				///< irgendeine Taste ist unten
		CD_BUTTONHELD,				///< Taste ist gerade unten
		CD_BUTTONNOTHELD,			///< Taste ist gerade oben 
		CD_AXISGREATER,				///< Achsenwert >  Vergleichswert
		CD_AXISGREATEROREQUAL,		///< Achsenwert >= Vergleichswert
		CD_AXISEQUAL,				///< Achsenwert == Vergleichswert
		CD_AXISNOTEQUAL,			///< Achsenwert != Vergleichswert
		CD_AXISLESS,				///< Achsenwert <  Vergleichswert
		CD_AXISLESSOREQUAL,			///< Achsenwert <= Vergleichswert
		CD_SCROLLLOCKACTIVE,		///< Scrolllock ist an (Keyboard)
		CD_ALWAYSFALSE,				///< Condition ist immer false
		CD_ALWAYSTRUE				///< Condition ist immer true
	};

	bool					IsValid() const;	
	bool					IsAxisCondition() const;
	bool					IsButtonCondition() const;
	float					GetUserDefinedAxisValue() const;

	/// führt die Überprüfung eines übergebenen Achsenwertes mit dem Vergleichswert aus
	bool					CheckAxisCondition(float p_fAxisValue) const;

	InputDevice				m_eDevice;
	Condition				m_eCondition;
	
	int			m_iButton;					///< wenn Condition Joystick-Button oder Key auf Tastatur betrifft
	int			m_iAxis;					///< wenn Condition Achse betrifft
	float		m_fComparisionValue;		///< Vergleichswert (falls nötig)
	float		m_fUserDefinesAxisValue;	///< benutzerdefinierter Achenwert dieser Bedingung; Default is 0.0f (kann so Keyboard-Conditions Achsenwerte zuweisen)
};


/// ComplexInputCondition: Alternative von Konjunktionen
class CComplexInputCondition
{
public:
	CComplexInputCondition() {}
	CComplexInputCondition(std::string p_sConditionText) : m_sConditionText(p_sConditionText) {};
	
	int						NumAlternatives() const;
	int						AddAlternative();

	int						NumConjunctions(int p_iAlternative) const;
	const CInputCondition&	GetCondition(int p_iAlternative, int p_iElement) const;
	int						AddCondition(int p_iAlternative, const CInputCondition& p_xrCondition); 

private:

	std::vector< std::vector<CInputCondition> >		m_aaxConditions;	///< äußerer vektor: Alternative; innerer: Konjuktion
	std::string				m_sConditionText;							///< die Condition nochmal als Text - für Debugzwecke
};

#include "InputCondition.inl"


#endif // INPUTCONDITION_H_INCLUDED


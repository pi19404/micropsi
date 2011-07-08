#ifndef SPINCONTROLNUMBER_H_INCLUDED 
#define SPINCONTROLNUMBER_H_INCLUDED

#include "uilib/controls/basicspincontrol.h"


namespace UILib
{

class CSpinControlNumber : public CBasicSpinControl
{
public:

	/// erzeugt ein neues CSpinControlNumber
	static CSpinControlNumber* Create();

	/// setzt den Wert
	void	SetValue(int p_iValue);

	/// setzt den Wert
	void	SetValue(float p_fValue);

	/// setzt den Wert
	void	SetValue(double p_dValue);

	/// setzt Minimalwert, Maximalwert und Schrittweiter
	void	SetLimits(int p_iMin, int p_iMax, int p_iStep);

	/// setzt Minimalwert, Maximalwert und Schrittweiter
	void	SetLimits(float p_fMin, float p_fMax, float p_fStep);

	/// setzt Minimalwert, Maximalwert und Schrittweiter
	void	SetLimits(double p_dMin, double p_dMax, double p_dStep);

	/// liefert Minimalwert, Maximalwert und Schrittweiter
	void	GetLimitsFloat(float& p_rfMin, float& p_rfMax, float& p_rfStep);

	/// \return Wert
	double	GetValue() const;

	/// \return Wert als float
	float	GetValueFloat() const;

	/// \return Wert als int
	int		GetValueInt() const;

	/// setzt die Anzahl anzuzeigender Dezimalstellen
	void	SetDecimals(int p_iDecimals);

	/// setzt den Wert eines benannten Attributes
	virtual bool SetAttrib(const CStr& p_rsName, const CStr& p_rsValue);

	/// liefert den Wert eines benannten Attributes
	virtual bool GetAttrib(const CStr& p_rsName, CStr& po_srValue) const;

protected:

	CSpinControlNumber();
	virtual ~CSpinControlNumber();

	virtual CStr GetDebugString() const;

	void			Update();
	virtual void	Up();
	virtual void	Down();

	virtual bool HandleMsg(const CMessage& p_rxMessage);

	double			m_dValue;			///< Wert
	int				m_iDecimals;		///< Anzahl anzuzeigender Dezimalstellen
	double			m_dMinValue;		///< Minimalwert
	double			m_dMaxValue;		///< Maximalwert
    double			m_dStep;			///< Schrittweite, mit der der Wert verändert wird

private:
	CSpinControlNumber(const CSpinControlNumber&) {}
	operator=(const CSpinControlNumber&) {}
};

#include "spincontrolnumber.inl"

} //namespace UILib


#endif // ifndef SPINCONTROLNUMBER_H_INCLUDED


#ifndef UILIB_LINEARINTERPOLATOR_H_INCLUDED
#define UILIB_LINEARINTERPOLATOR_H_INCLUDED

namespace UILib
{

class CLinearInterpolator
{
public:
	CLinearInterpolator(float* p_pfValue, float p_fEndValue, float p_fStartTime, float p_fTotalTime)
	{
		assert(p_fTotalTime >= 0);
		m_pfValue			= p_pfValue;
		m_piValue			= 0;
		m_fStartTime		= p_fStartTime;
		m_fEndValue			= p_fEndValue;
		m_fTotalTime		= p_fTotalTime;
		m_fStartValue		= *p_pfValue;
		m_fStepPerTimeUnit	= (m_fEndValue - m_fStartValue) / m_fTotalTime;
		assert(m_fStepPerTimeUnit != 0  ||  m_fStartValue == m_fEndValue);
	}

	CLinearInterpolator(int* p_piValue, int p_iEndValue, float p_fStartTime, float p_fTotalTime)
	{
		assert(p_fTotalTime >= 0);
		m_pfValue			= 0;
		m_piValue			= p_piValue;
		m_fStartTime		= p_fStartTime;
		m_fEndValue			= (float) p_iEndValue;
		m_fTotalTime		= p_fTotalTime;
		m_fStartValue		= (float) *p_piValue;
		m_fStepPerTimeUnit	= (m_fEndValue - m_fStartValue) / m_fTotalTime;
		assert(m_fStepPerTimeUnit != 0  ||  m_fStartValue == m_fEndValue);
	}


	/**
		interpoliert die Variable weiter; liefert true, wenn der Endwert erreicht ist
	*/
	bool Update(float p_fCurrentTime)
	{
		assert(p_fCurrentTime >= m_fStartTime);
		if(p_fCurrentTime >= m_fStartTime + m_fTotalTime)
		{
			if(m_pfValue)		{ *m_pfValue = m_fEndValue; }
			if(m_piValue)		{ *m_piValue = (int) m_fEndValue; }
			return true;
		}
		else
		{
			if(m_pfValue)
			{
				*m_pfValue = m_fStartValue + m_fStepPerTimeUnit * (p_fCurrentTime - m_fStartTime);
			}
			if(m_piValue)
			{
				*m_piValue = (int) (m_fStartValue + m_fStepPerTimeUnit * (p_fCurrentTime - m_fStartTime));
			}
			return false;
		}
	}

private:
	float*	m_pfValue;
	int*	m_piValue;

	float	m_fStartTime;
	float	m_fStartValue;
	float	m_fEndValue;
	float	m_fTotalTime;
	float	m_fStepPerTimeUnit;

};


} // namespace UILib

#endif // ifndef UILIB_LINEARINTERPOLATOR_H_INCLUDED


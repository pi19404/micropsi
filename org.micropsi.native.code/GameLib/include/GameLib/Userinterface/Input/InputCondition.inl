//---------------------------------------------------------------------------------------------------------------------
inline
bool
CInputCondition::IsValid() const
{
	return m_eCondition != CD_INVALID;
}					
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CInputCondition::IsButtonCondition() const
{
	return m_iButton >= 0  ||  m_eCondition <= CD_ANYBUTTON;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CInputCondition::IsAxisCondition() const
{
	return m_iAxis >= 0;
}
//---------------------------------------------------------------------------------------------------------------------
inline
bool
CInputCondition::CheckAxisCondition(float p_fAxisValue) const
{
	switch(m_eCondition)
	{
		case CInputCondition::CD_AXISEQUAL:
			return p_fAxisValue == m_fComparisionValue;
		case CInputCondition::CD_AXISNOTEQUAL:
			return p_fAxisValue != m_fComparisionValue;
		case CInputCondition::CD_AXISLESS:
			return p_fAxisValue < m_fComparisionValue;
		case CInputCondition::CD_AXISGREATER:
			return p_fAxisValue > m_fComparisionValue;
		case CInputCondition::CD_AXISLESSOREQUAL:
			return p_fAxisValue <= m_fComparisionValue;
		case CInputCondition::CD_AXISGREATEROREQUAL:
			return p_fAxisValue >= m_fComparisionValue;
		default:
			assert(false);		// diese Bedingung ist ja gar kein Vergeich mit einer Achse!
			return false;
	}
}
//---------------------------------------------------------------------------------------------------------------------
inline
float					
CInputCondition::GetUserDefinedAxisValue() const
{
	return m_fUserDefinesAxisValue;
}
//---------------------------------------------------------------------------------------------------------------------
inline
int						
CComplexInputCondition::NumAlternatives() const
{
	return (int) m_aaxConditions.size();
}
//---------------------------------------------------------------------------------------------------------------------
inline
int						
CComplexInputCondition::AddAlternative()
{
	m_aaxConditions.push_back(std::vector<CInputCondition>());
	return (int) m_aaxConditions.size()-1;
}
//---------------------------------------------------------------------------------------------------------------------
inline
int						
CComplexInputCondition::NumConjunctions(int p_iAlternative) const
{
	return (int) m_aaxConditions[p_iAlternative].size();
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CInputCondition&	
CComplexInputCondition::GetCondition(int p_iAlternative, int p_iElement) const
{
	return m_aaxConditions[p_iAlternative][p_iElement];
}
//---------------------------------------------------------------------------------------------------------------------
inline
int						
CComplexInputCondition::AddCondition(int p_iAlternative, const CInputCondition& p_xrCondition)
{
	m_aaxConditions[p_iAlternative].push_back(p_xrCondition);
	return (int) m_aaxConditions[p_iAlternative].size()-1;
} 
//---------------------------------------------------------------------------------------------------------------------

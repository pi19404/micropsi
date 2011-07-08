//---------------------------------------------------------------------------------------------------------------------
inline
const std::string&		
CQuestion::GetQuestionName() const
{
	return m_sQuestionName;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const std::string&		
CQuestion::GetOrigin() const
{
	return m_sOrigin;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const std::string&		
CQuestion::GetDestination() const
{
	return m_sTargetComponent;
}
//---------------------------------------------------------------------------------------------------------------------
inline
__int64						
CQuestion::GetCurrentStep() const
{
	return m_iCurrentStep;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CQuestion::AnswerMode				
CQuestion::GetAnswerMode() const
{
	return m_eAnswerMode;
}
//---------------------------------------------------------------------------------------------------------------------
inline
const CParameterList&	
CQuestion::GetParameters() const
{
	return m_xParameters;
}
//---------------------------------------------------------------------------------------------------------------------
inline
CParameterList&			
CQuestion::Parameters()
{
	return m_xParameters;
}
//---------------------------------------------------------------------------------------------------------------------

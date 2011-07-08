#include "Application/stdinc.h"
#include "Question.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
CQuestion::CQuestion()
{
}

//---------------------------------------------------------------------------------------------------------------------
CQuestion::CQuestion(string p_sTargetComponent, __int64 p_iCurrentStep, AnswerMode p_eAnswerMode, string p_sOrigin, string p_sQuestionName, CParameterList& p_xParameters)
{
	m_sTargetComponent	= p_sTargetComponent;			
	m_iCurrentStep		= p_iCurrentStep;				
	m_eAnswerMode		= p_eAnswerMode;				
	m_sOrigin			= p_sOrigin;					
	m_sQuestionName		= p_sQuestionName;	
	m_xParameters		= p_xParameters;
}
//---------------------------------------------------------------------------------------------------------------------
CQuestion::~CQuestion()
{
}
//---------------------------------------------------------------------------------------------------------------------

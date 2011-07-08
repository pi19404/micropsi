#pragma once
#ifndef QUESTION_H_INCLUDED
#define QUESTION_H_INCLUDED

#include "Message.h"
#include "ParameterList.h"
#include <string>


class CQuestion : public CMessage
{
public:

	enum AnswerMode
	{
		AM_DONT_ANSWER = 0,					///< Execute the question, but do not return an answer.
		AM_STOP_ANSWERING = 1,				///< Do not execute the question and remove it from the list of stored answers (if there)
		AM_ANSWER_ONCE = 2,					///< Execute the question and return the answer exactly once.
		AM_ANSWER_CONTINUOUSLY = 3,			///< Execute-and-answer the question every step.
		AM_ANSWER_EVERY_5_STEPS = 4,		///< Execute-and-answer the question in the step the question is received and every 5th step from then on.
		AM_ANSWER_EVERY_10_STEPS = 5,		///< Execute-and-answer the question in the step the question is received and every 10th step from then on.
		AM_ANSWER_EVERY_50_STEPS = 6,		///< Execute-and-answer the question in the step the question is received and every 50th step from then on.
		AM_ANSWER_EVERY_100_STEPS = 7,		///< Execute-and-answer the question in the step the question is received and every 100th step from then on
	};

	CQuestion();
	CQuestion(std::string p_sTargetComponent, __int64 p_iCurrentStep, AnswerMode p_eAnswerMode, std::string p_sOrigin, std::string p_sQuestionName, CParameterList& p_xParameters = CParameterList());
	virtual ~CQuestion();

	const std::string&		GetQuestionName() const;
	const std::string&		GetOrigin() const;
	const std::string&		GetDestination() const;
	__int64					GetCurrentStep() const;
	AnswerMode				GetAnswerMode() const;
	const CParameterList&	GetParameters() const;
	CParameterList&			Parameters();

protected:

	std::string				m_sTargetComponent;			///< component this question is directed to; i.e. "world"
	__int64					m_iCurrentStep;				///< the server simulation step number we believe is current
	AnswerMode				m_eAnswerMode;				///< desired answer mode for this question
	std::string				m_sOrigin;					///< origin of the question (component)
	std::string				m_sQuestionName;			///< name of the question
	CParameterList			m_xParameters;				///< question parameters
};

#include "Question.inl"

#endif // ifndef QUESTION_H_INCLUDED
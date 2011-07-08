#ifndef ANSWER_H_INCLUDED
#define ANSWER_H_INCLUDED

#include "Message.h"

/**
	Base class for all answers
*/
class CAnswer : public CMessage
{
public:
	enum AnswerType
	{
		ANSWER_TYPE_STRING			= 0,	///< A simple string answer. The content field of the AnswerIF will be of type String.
		ANSWER_TYPE_OK				= 1,	///< No real answer, just a confirmation that the question was executed
		ANSWER_TYPE_ERROR			= 2,	///< executed, but failed. content may or may not contain an error message string
		ANSWER_TYPE_COMPLEX_MESSAGE = 3		///< complex answer; content is xml (currently always MTreeNode) 
	};		

protected:

   	AnswerType				m_eType;					///< type of answer
	__int64					m_iStep;					///< simulation step at time of answer
	std::string				m_sDestination;				///< destination of the answer
	std::string				m_sOrigin;					///< origin of the answer
};

#endif // ANSWER_H_INCLUDED

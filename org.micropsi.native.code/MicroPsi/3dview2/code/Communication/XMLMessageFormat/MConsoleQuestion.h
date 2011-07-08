#ifndef MCONSOLEQUESTION_H_INCLUDED
#define MCONSOLEQUESTION_H_INCLUDED

#include "Question.h"

class CMConsoleQuestion : public CQuestion
{
public:

	CMConsoleQuestion();
	CMConsoleQuestion(std::string p_sTargetComponent, __int64 p_iCurrentStep, AnswerMode p_eAnswerMode, std::string p_sOrigin, 
		std::string p_sQuestionName, CParameterList& p_xParameters = CParameterList());
	virtual ~CMConsoleQuestion();

	virtual MessageType		GetMessageType() const;
	virtual std::string		ToXMLString() const;   

	virtual bool			FromXMLElement(const TiXmlElement* p_pxXMLElement);
};

#endif // MCONSOLEQUESTION_H_INCLUDED

#ifndef MCONSOLEANSWER_H_INCLUDED
#define MCONSOLEANSWER_H_INCLUDED

#include "Answer.h"
class CMConsoleQuestion;
class CMTreeNode;

class CMConsoleAnswer : public CAnswer
{
public:	

	CMConsoleAnswer();
	CMConsoleAnswer(const CMConsoleAnswer& p_rxOther);
	virtual ~CMConsoleAnswer();

	virtual MessageType			GetMessageType() const;
	virtual std::string			ToXMLString() const;

	virtual bool				FromXMLElement(const TiXmlElement* p_pxXMLElement);

	const CMConsoleQuestion*	GetQuestion() const;
	const CMTreeNode*			GetDataTree() const;

private:

	CMConsoleQuestion*		m_pxQuestion;			///< question that lead to this answer (may be 0)
	CMTreeNode*				m_pxTree;				///< tree if answer is complex (may be 0)
};

#include "MConsoleAnswer.inl"

#endif // MCONSOLEANSWER_H_INCLUDED

#include "Application/stdinc.h"
#include "MConsoleAnswer.h"

#include "MTreeNode.h"
#include "MConsoleQuestion.h"
#include "tinyxml.h"

using std::string;

//---------------------------------------------------------------------------------------------------------------------
CMConsoleAnswer::CMConsoleAnswer()
{
	m_pxQuestion	= 0;
	m_pxTree		= 0;
}

//---------------------------------------------------------------------------------------------------------------------
CMConsoleAnswer::CMConsoleAnswer(const CMConsoleAnswer& p_rxOther)
{
	if(p_rxOther.m_pxQuestion)
	{
		m_pxQuestion = new CMConsoleQuestion(*p_rxOther.m_pxQuestion);
	}
	else
	{
		m_pxQuestion = 0;
	}
	if(p_rxOther.m_pxTree)
	{
		m_pxTree = new CMTreeNode(*p_rxOther.m_pxTree);
	}
	else
	{
		m_pxTree = 0;
	}
}

//---------------------------------------------------------------------------------------------------------------------
CMConsoleAnswer::~CMConsoleAnswer()
{
	delete m_pxQuestion;
	delete m_pxTree;
}

//---------------------------------------------------------------------------------------------------------------------
CMessage::MessageType		
CMConsoleAnswer::GetMessageType() const
{
	return CMessage::MTYPE_CONSOLE_ANSWER;
}

//---------------------------------------------------------------------------------------------------------------------
std::string		
CMConsoleAnswer::ToXMLString() const
{
	return "";
}

//---------------------------------------------------------------------------------------------------------------------
bool			
CMConsoleAnswer::FromXMLElement(const TiXmlElement* p_pxXMLElement)
{
	if(string(p_pxXMLElement->Value()) != "m303")
	{
		assert(false);
		return false;
	}

	// parse attributes

	const TiXmlAttribute* pxAttrib = p_pxXMLElement->FirstAttribute();
	while(pxAttrib)
	{
		string sName = pxAttrib->Name();
		if(sName == "step")
		{
			m_iStep = _atoi64(pxAttrib->Value());
		}
		else if(sName == "type")
		{
			m_eType = (CAnswer::AnswerType) atoi(pxAttrib->Value());
		}
		else if(sName == "dest")
		{
			m_sDestination = pxAttrib->Value(); 
		}
		else if(sName == "origin")
		{
			m_sOrigin = pxAttrib->Value(); 
		}

		pxAttrib = pxAttrib->Next();
	}


	// parse children

	const TiXmlElement* pxElement = p_pxXMLElement->FirstChildElement();
	while(pxElement)
	{
		string sName = pxElement->Value();
		if(sName == "m302")			// question
		{
			assert(!m_pxQuestion);
			if(!m_pxQuestion)
			{
				m_pxQuestion = new CMConsoleQuestion();
				m_pxQuestion->FromXMLElement(pxElement);
			}
		}
		else if(sName == "m304")	// treenode
		{
			assert(!m_pxTree);
			if(!m_pxTree)
			{
				m_pxTree = new CMTreeNode();
				m_pxTree->FromXMLElement(pxElement);
			}
		}
		else
		{
			DebugPrint("Warning: CMConsoleResponse::FromXMLElement(): Message Type %s - parsing not implemented", sName.c_str());
		}

		pxElement = pxElement->NextSiblingElement();
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------

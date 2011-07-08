#include "Application/stdinc.h"
#include "MConsoleQuestion.h"

#include "baselib/str.h"
#include "tinyxml.h"

using std::string;


//---------------------------------------------------------------------------------------------------------------------
CMConsoleQuestion::CMConsoleQuestion()
{
}

//---------------------------------------------------------------------------------------------------------------------
CMConsoleQuestion::CMConsoleQuestion(string p_sTargetComponent, __int64 p_iCurrentStep, AnswerMode p_eAnswerMode, string p_sOrigin, string p_sQuestionName, CParameterList& p_xParameters)
	: CQuestion(p_sTargetComponent, p_iCurrentStep, p_eAnswerMode, p_sOrigin, p_sQuestionName, p_xParameters)
{
}

//---------------------------------------------------------------------------------------------------------------------
CMConsoleQuestion::~CMConsoleQuestion()
{
}

//---------------------------------------------------------------------------------------------------------------------
CMessage::MessageType		
CMConsoleQuestion::GetMessageType() const
{
	return MTYPE_CONSOLE_QUESTION;
}

//---------------------------------------------------------------------------------------------------------------------
std::string		
CMConsoleQuestion::ToXMLString() const
{
	CStr sRes = CStr::Create("<m%d dest=\"%s\" step=\"%I64d\" am=\"%d\" origin=\"%s\" qname=\"%s\" %s/>", 
			MTYPE_CONSOLE_QUESTION, m_sTargetComponent.c_str(), m_iCurrentStep, m_eAnswerMode, 
			m_sOrigin.c_str(), m_sQuestionName.c_str(), m_xParameters.ToXMLString().c_str());
	return sRes.c_str();
}

//---------------------------------------------------------------------------------------------------------------------
bool		
CMConsoleQuestion::FromXMLElement(const TiXmlElement* p_pxXMLElement)
{
	if(string(p_pxXMLElement->Value()) != "m302")
	{
		assert(false);
		return false;
	}

	const TiXmlAttribute* pxAttrib = p_pxXMLElement->FirstAttribute();
	while(pxAttrib)
	{
		string sName = pxAttrib->Name();
		if(sName == "step")
		{
			m_iCurrentStep = _atoi64(pxAttrib->Value());
		}
		else if(sName == "am")
		{
			m_eAnswerMode = (CQuestion::AnswerMode) atoi(pxAttrib->Value());
		}
		else if(sName == "dest")
		{
			m_sTargetComponent = pxAttrib->Value(); 
		}
		else if(sName == "origin")
		{
			m_sOrigin = pxAttrib->Value(); 
		}
		else if(sName == "qname")
		{
			m_sQuestionName = pxAttrib->Value(); 
		}
		else
		{
			DebugPrint("Warning: Question Parameter XML Parsing not supported yet!");
		}

		pxAttrib = pxAttrib->Next();
	}

	return true;
}
//---------------------------------------------------------------------------------------------------------------------

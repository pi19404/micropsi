#include "Application/stdinc.h"
#include "MConsoleRequest.h"
#include "baselib/str.h"

using std::vector;
using std::string;

//---------------------------------------------------------------------------------------------------------------------
CMConsoleRequest::CMConsoleRequest(RequestType p_eRequestType)
{
	m_eRequestType = p_eRequestType;
}

//---------------------------------------------------------------------------------------------------------------------
CMConsoleRequest::~CMConsoleRequest()
{
}

//---------------------------------------------------------------------------------------------------------------------
void
CMConsoleRequest::AddQuestion(const CMConsoleQuestion& p_rxQuestion)
{
	m_axQuestions.push_back(p_rxQuestion);
}

//---------------------------------------------------------------------------------------------------------------------
CMessage::MessageType		
CMConsoleRequest::GetMessageType() const
{
	return MTYPE_CONSOLE_REQ;
}

//---------------------------------------------------------------------------------------------------------------------
std::string		
CMConsoleRequest::ToXMLString() const
{
	string sQuestions;
	vector<CMConsoleQuestion>::const_iterator i;
	for(i=m_axQuestions.begin(); i!=m_axQuestions.end(); ++i)
	{
		sQuestions += i->ToXMLString();
	}

	CStr sRes = CStr::Create("<m%d type=\"%d\">%s</m%d>", MTYPE_CONSOLE_REQ, m_eRequestType, sQuestions.c_str(), MTYPE_CONSOLE_REQ);

	return sRes.c_str();
}

//---------------------------------------------------------------------------------------------------------------------
bool		
CMConsoleRequest::FromXMLElement(const TiXmlElement* p_pxXMLElement)
{
	return false;
}
//---------------------------------------------------------------------------------------------------------------------

#include "Application/stdinc.h"
#include "Communication/XMLCommunication/XMLCommunicationService.h"

#include "Communication/XMLMessageFormat/MAgentResponse.h"
#include "Communication/XMLMessageFormat/MConsoleResponse.h"

#include "tinyxml.h"

using std::vector;
using std::string;


//---------------------------------------------------------------------------------------------------------------------
CXMLCommunicationService::CXMLCommunicationService(std::string p_sComponentName)
{
	m_sComponentName = p_sComponentName;
}

//---------------------------------------------------------------------------------------------------------------------
bool		
CXMLCommunicationService::TranslateMessage(const string p_rsMessage, vector<CRootMessage*>& po_rapxMessages, string& po_rsComponent, int& po_riType)
{
	TiXmlDocument xDoc;
	xDoc.Parse(p_rsMessage.c_str());

	TiXmlElement* pxRoot = xDoc.RootElement();
	if(string(pxRoot->Value()) != "resp")
	{
		DebugPrint("Error: Message does not start with 'resp'");
		return false;
	}

	const TiXmlAttribute* pxRootAttrib = pxRoot->FirstAttribute();
	while(pxRootAttrib)
	{
		string sName = pxRootAttrib->Name();
		if(sName == "id")
		{
			po_rsComponent = pxRootAttrib->Value();
		}
		else if(sName == "type")
		{
			po_riType = atoi(pxRootAttrib->Value());
		}
		pxRootAttrib = pxRootAttrib->Next();
	}

	TiXmlElement* pxElement = pxRoot->FirstChildElement();
	while(pxElement)
	{
		string sName = pxElement->Value();
		if(sName == "m201")		// agentresponse
		{
			CMAgentResponse* pxAgentResponse = new CMAgentResponse();
			pxAgentResponse->FromXMLElement(pxElement);
			po_rapxMessages.push_back(pxAgentResponse);
		}
		else if(sName == "m301") // consoleresponse
		{
			CMConsoleResponse* pxConsoleResponse = new CMConsoleResponse();
			pxConsoleResponse->FromXMLElement(pxElement);
			po_rapxMessages.push_back(pxConsoleResponse);
		}
		else
		{
			DebugPrint("Warning: TranslateMessage(): Message Type %s - parsing not implemented", sName.c_str());
		}

		pxElement = pxElement->NextSiblingElement();
	}

	return true;
} 

//---------------------------------------------------------------------------------------------------------------------
void		
CXMLCommunicationService::DestroyMessages(vector<CRootMessage*>& po_rapxMessages)
{
	for(unsigned int i=0; i<po_rapxMessages.size(); ++i)
	{
		delete po_rapxMessages[i];
	}
	po_rapxMessages.clear();
}

//---------------------------------------------------------------------------------------------------------------------
void		
CXMLCommunicationService::ReceiveMessage(const std::string& p_rsMessage)
{
//	DebugPrint("Received message: %s", p_rsMessage.c_str());

	vector<CRootMessage*> apxMessages;
	string sComponent;
	int iType;

	TranslateMessage(p_rsMessage, apxMessages, sComponent, iType);
	for(unsigned int i=0; i<apxMessages.size(); ++i)
	{
		HandleMessage(*apxMessages[i], sComponent, iType);
	}

	DestroyMessages(apxMessages);
}
//---------------------------------------------------------------------------------------------------------------------
const std::string&	
CXMLCommunicationService::GetComponentName() const
{
	return m_sComponentName;
}
//---------------------------------------------------------------------------------------------------------------------
void				
CXMLCommunicationService::SetComponentName(std::string p_sComponentName)
{
	m_sComponentName = p_sComponentName;
}
//---------------------------------------------------------------------------------------------------------------------

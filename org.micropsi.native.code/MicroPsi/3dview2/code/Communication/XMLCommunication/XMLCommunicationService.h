#ifndef XMLCOMMUNICATIONSERVICE_H_INCLUDED
#define XMLCOMMUNICATIONSERVICE_H_INCLUDED

#include <string>
#include <vector>
#include "Communication/XMLMessageFormat/RootMessage.h"

/**
	Base class for xml-based communication services.

	SendMsg() must be implemented by subclasses
	ReceiveMessage(const std::string&) must be called by subclasses for every xml message string received
	HandleMessage(const CRootMessage&, const std::string&, int) must be implemented and handle the received messages

*/
class CXMLCommunicationService
{
public:

	CXMLCommunicationService(std::string p_sComponentName);

	/**
		sends a message; must be immplemented by subclasses 
		if p_bDropIfBusy is set to true, the message can be dropped if the communication line is too busy
		returns true if the message was sent; false if it could not be sent or was dropped
	*/
	virtual bool		SendMsg(const CRootMessage& p_xrMessage, bool p_bDropIfBusy = false) = 0;

	/// handles a received message; must be implemented by subclasses 
	virtual bool		HandleMessage(const CRootMessage& p_xrMessage, const std::string& p_rsComponent, int p_iType) = 0;

	/// returns our component name 
	const std::string&	GetComponentName() const;

	/// sets our component name
	void				SetComponentName(std::string p_sComponentName);

protected:

	/// converts an xml message string to Message Objects and calls ReceiveMessage(const CRootMessage&, const std::string&, int) for each one
	virtual void		ReceiveMessage(const std::string& p_rsMessage);

	/// translates an xml string into an array of micropsi messages
	virtual bool		TranslateMessage(const std::string p_rsMessage, std::vector<CRootMessage*>& po_rapxMessages, std::string& po_rsComponent, int& po_riType); 

	/// deletes message array created by TranslateMessage()
	virtual void		DestroyMessages(std::vector<CRootMessage*>& po_rapxMessages);

private:
	std::string			m_sComponentName;					///< name under which this component will be identified with the server
};

#endif // XMLCOMMUNICATIONSERVICE_H_INCLUDED

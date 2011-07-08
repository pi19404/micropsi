#ifndef XMLWORLDCONTROLLER_H_INCLUDED
#define XMLWORLDCONTROLLER_H_INCLUDED

#include <string>
#include "baselib/Thread.h"
#include "Communication/XMLMessageFormat/Question.h"
#include "Communication/WorldController.h"

class CXMLCommunicationService;
class CRootMessage;
class CMConsoleAnswer;
class CMTreeNode;

class CXMLWorldController : public CWorldController
{
public:

	CXMLWorldController(CXMLCommunicationService* p_pxCommunicationService, CWorld* p_pxWorld);
	virtual ~CXMLWorldController();    

	virtual void		ClearAllObjects();
	virtual	bool		SaveWorld(); 
	virtual	bool		SaveWorldAs(const std::string& p_rsFilename); 
	virtual std::string	GetCurrentWorldFileName() const;

	virtual void		CreateNewObject(const char* p_pcObjectClassName, const CVec3& p_vPos, 
										float p_fOrientationAngle, float p_fHeight, int p_iVariation = -1);

	virtual bool		DeleteObject(__int64 p_iID);

	virtual void		Tick(double p_dTime);

	/// handles a received message
	virtual bool		HandleMessage(const CRootMessage& p_xrMessage, const std::string& p_rsComponent, int p_iType);

protected:

	unsigned long		TouchCreatorThreadProc(int p_iUnused);

	void				RequestCompleteObjectList();
	void				RequestGlobalData();
	void				RequestStopObjectUpdates();
	void				SendQuestion(CQuestion::AnswerMode p_eAnswerMode, std::string p_sQuestionName, CParameterList& p_xParameters = CParameterList());
	void				SendEmptyRequest();
	void				Disconnect();

	void				ProcessAnswer(const CMConsoleAnswer& p_rxAnswer);
	void				ProcessObjectList(const CMTreeNode& p_rxObjectListNode);
	void				ProcessObject(const CMTreeNode& p_rxObjectListNode);
	void				ProcessGroundMap(const CMTreeNode& p_rxObjectListNode);


	CThread							m_xTouchCreatorThread;
	bool							m_bStopTouchCreatorThread;

	CXMLCommunicationService*		m_pxCommunicationService;			///< our communication service, used to send messages
	__int64							m_iCurrentWorldSimStep;				///< current world simulationj step
	int								m_iGlobalsVersion;					///< version number of global world settings on the server
	std::string						m_sWorldFileName;					///< name of xml world file on this server
	std::string						m_sGroundMapFileName;				///< name of the ground map (pixel image) file on the server
	CVec3							m_vGroundMapLowerCorner;			///< coordinates of the lower left corner of the ground map 
	CVec3							m_vGroundMapUpperCorner;			///< coordinates of the upper left corner of the ground map 

	bool							m_bFirstMessage;					///< is the next message the first message we send to the server?

private:

};

#endif // XMLWORLDCONTROLLER_H_INCLUDED

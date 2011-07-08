#ifndef XMLREMOTEAGENTCONTROLLER_H_INCLUDED
#define XMLREMOTEAGENTCONTROLLER_H_INCLUDED

#include <string>
#include "baselib/Thread.h"
#include "Communication/RemoteAgentController.h"
#include "baselib/geometry/CVector.h"

class CRootMessage;
class CXMLCommunicationService;

class CXMLRemoteAgentController : public CRemoteAgentController
{
public:

	CXMLRemoteAgentController(CXMLCommunicationService* p_pxCommunicationService);
	virtual ~CXMLRemoteAgentController();    

	void			Tick(double p_dTime);

	virtual bool	HandleMessage(const CRootMessage& p_xrMessage, const std::string& p_rsComponent, int p_iType);

	virtual void		Move(float p_fDeltaX, float p_fDeltaY);
	virtual void		Eat(__int64 p_iObject);
	virtual	void		Drink(__int64 p_iObject);
	virtual void		Focus(__int64 p_iObject);

protected:

	unsigned long	UpdateThreadProc(int p_iUnused);

	void	Register();
	void	Unregister();

	bool							m_bStopUpdateThread;
	CThread							m_xUpdateThread;
	CXMLCommunicationService*		m_pxCommunicationService;

private:

	CVec3	m_vDesiredPositionOffset;
};

#endif // XMLREMOTEAGENTCONTROLLER_H_INCLUDED

#ifndef REMOTEAGENTCONTROLLER_H_INCLUDED
#define REMOTEAGENTCONTROLLER_H_INCLUDED

#include <string>
#include "baselib/geometry/CVector.h"

class CWorld;

class CRemoteAgentController
{
public:

	CRemoteAgentController() {};
	virtual ~CRemoteAgentController() {};

	virtual void		Tick(double p_dTime) {};
	virtual void		Move(float p_fDeltaX, float p_fDeltaY) = 0;
	virtual void		Eat(__int64 p_iObject) = 0;
	virtual void		Drink(__int64 p_iObject) = 0;
	virtual void		Focus(__int64 p_iObject) = 0;

	const std::string&	GetAgentName() const;

protected:

	std::string					m_sAgentName;

};

#include "RemoteAgentController.inl"

#endif // REMOTEAGENTCONTROLLER_H_INCLUDED

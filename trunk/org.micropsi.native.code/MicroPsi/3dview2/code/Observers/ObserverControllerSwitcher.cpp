
#include "Application/stdinc.h"
#include "Observers/ObserverControllerSwitcher.h"
#include "Observers/WalkingObserverController.h"
#include "Observers/PassiveObserverController.h"
#include "Observers/FreelookObserverController.h"
#include "Observers/HelicopterObserverController.h"
#include "Observers/ObjectFocusObserverController.h"

using std::map;
using std::string;

//---------------------------------------------------------------------------------------------------------------------
CObserverControllerSwitcher::CObserverControllerSwitcher(CWorld* p_pxWorld)
{
	m_pxObserver	= 0;
	m_pxCurrentController = 0;
	m_pxWorld = p_pxWorld;

	AddObserver("passive", new CPassiveObserverController());
	AddObserver("walk", new CWalkingObserverController());
	AddObserver("freelook", new CFreeLookObserverController());
	AddObserver("helicopter", new CHelicopterObserverController());
	AddObserver("objectfocus", new CObjectFocusObserverController());
}

//---------------------------------------------------------------------------------------------------------------------
CObserverControllerSwitcher::~CObserverControllerSwitcher()
{
	std::map<std::string, CObserverController*>::iterator i;
	if(m_mxAllControllers.size() > 0)
	{
		for(i=m_mxAllControllers.begin(); i!=m_mxAllControllers.end(); i++)
		{
			delete i->second;
		}
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CObserverControllerSwitcher::AddObserver(std::string p_sName, CObserverController* p_pxObserver)
{
	std::map<std::string, CObserverController*>::iterator i;
	i = m_mxAllControllers.find(p_sName);
	assert(m_mxAllControllers.size() == 0  ||  i == m_mxAllControllers.end());

	m_mxAllControllers[p_sName] = p_pxObserver;

	if(!m_pxCurrentController)
	{
		SwitchObserver(p_sName);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CObserverControllerSwitcher::Tick(CInputManager* p_pxInputManager)
{
	if(m_pxCurrentController)
	{
		m_pxCurrentController->Tick(p_pxInputManager, m_pxWorld);
	}
}

//---------------------------------------------------------------------------------------------------------------------
void
CObserverControllerSwitcher::SwitchObserver(std::string p_sNewObserverName)
{
	std::map<std::string, CObserverController*>::iterator i;
	i = m_mxAllControllers.find(p_sNewObserverName);
	if(m_mxAllControllers.size() == 0  ||  i == m_mxAllControllers.end())
	{
		assert(false); // nicht gefunden :(
		return;
	}
	
	if(m_pxCurrentController)
	{
		m_pxCurrentController->OnDeactivate(m_pxWorld);
	}
	m_pxCurrentController = i->second;
	m_pxCurrentController->SetCurrentObserver(m_pxObserver);
	m_pxCurrentController->OnActivate(m_pxWorld);
}
//---------------------------------------------------------------------------------------------------------------------

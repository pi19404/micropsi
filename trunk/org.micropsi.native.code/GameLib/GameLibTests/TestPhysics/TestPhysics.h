#ifndef TESTINPUTMANAGER_H_INCLUDED
#define TESTINPUTMANAGER_H_INCLUDED

#include "stdinc.h"

#include <vector>
#include "GameLib/GameLibApplication.h"
#include "e42/core/ResourceHandles.h"
#include "e42/Camera.h"

class CTestScreen;
class CDynamicsSimulation;
class CDOPlane;
class CDOBox;
class CDOSimpleBuggy;

//-----------------------------------------------------------------------------------------------------------------------
class CTestPhysics : public CGameLibApplication
{
private:
    void InitFileLocator();

    void CreateScene();
    void Terminate();

	void Input();
	void Update();
	void Output();

protected:

	void CreateInputManagerMapping();
	void SetFilesystemMapping();
	void MoveCamera(float fDeltaTime);
	void UpdateWorld();

	CCamera							m_xCamera;					///< engine camera
	CTestScreen*					m_pTestScreen;
	CDynamicsSimulation*			m_pDynamicsSimulation;
	TModelHandle					m_hBoxModel;
	TModelHandle					m_hLevel;

	bool							m_bDebugRendering;
	bool							m_bCameraFollowsBuggy;

	// Physikobjekte:

	CDOPlane*					m_pPlane;
	std::vector<CDOBox*>		m_apBoxes;
	CDOSimpleBuggy*				m_pBuggy;

public:
    CTestPhysics(HINSTANCE hInstance);
    ~CTestPhysics();
};
//-----------------------------------------------------------------------------------------------------------------------

#endif // TESTINPUTMANAGER_H_INCLUDED
#include "stdafx.h"
#include "uilib/core/windowmanager.h"
#include "WindowSystemTest/MainWindow.h"

using namespace UILib;

CWTMainWindow* pxMainWindow;

void TestWindowSystem_OnInit(UILib::COutputDevice* p_pxUIDevice)
{
	pxMainWindow = CWTMainWindow::Create();
	CWindowMgr::Get().AddTopLevelWindow(pxMainWindow->GetWHDL());
}

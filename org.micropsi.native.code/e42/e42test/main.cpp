#include <windows.h>
#include <assert.h>

#include "TestSceneGraphIterator.h"
#include "TestSceneGraph.h"


//-------------------------------------------------------------------------------------------------------------------------------------------
INT 
WINAPI WinMain( HINSTANCE hInstance, HINSTANCE, LPSTR, INT)
{
	static volatile bool bTestResult = true;

	for (int i = 0; i < 10; i++)
	{
		bTestResult &= TestSceneGraph();
		bTestResult &= TestSceneGraphIterator();
	}

    return 0;
}
//-------------------------------------------------------------------------------------------------------------------------------------------

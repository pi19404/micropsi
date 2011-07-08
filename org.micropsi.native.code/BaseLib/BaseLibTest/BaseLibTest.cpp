// BaseLibTest.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <crtdbg.h>
#include "TestHelpers.h"
#include "BaseLib/FileLocator.h"


// forwards aus den anderen cpp-Dateien

void TestMisc();
void TestCStr();
void TestCRct();
void TestCRctList();
void TestHandledSet();
void TestSmartPtr();
void TestFileLocator();
void TestComObjectPtr();
void TestThread();
void TestFunctionPointer();
void TestVariant();

int __cdecl _tmain(int argc, _TCHAR* argv[])
{
	_CrtSetDbgFlag(_CRTDBG_ALLOC_MEM_DF);


	cout << "BaseLib Test" << endl;

	TestMisc();
	TestCStr();
	TestCRct();
	TestCRctList();
	TestHandledSet();
	TestSmartPtr();
	TestFileLocator();
	TestComObjectPtr();
	TestThread();
	TestFunctionPointer();
	TestVariant();

	if(g_iTestHelperErrorCount == 0)
	{
		cout << "... OK, no errors." << endl; 
	}
	else
	{
		cout << "There were " << g_iTestHelperErrorCount << " Errors!" << endl;
	}

	if(_CrtDumpMemoryLeaks())
	{
		cout << "Memory leaks detected!!! (see logger for details)" << endl;
	}


	return 0;
}


#include "stdafx.h"
#include <iostream>

using namespace std;

int g_iTestHelperErrorCount = 0;

void Assertion(bool p_bCondition, int p_iLine, const char* p_pcFilename, const char* p_pcMessage = "")
{
	if(!p_bCondition)
	{
		cout << "Error: '" << p_pcMessage << "' " << p_pcFilename << " Line " << p_iLine << endl;
		g_iTestHelperErrorCount++;
	}
}
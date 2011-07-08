#include "stdafx.h"

#include "TestHelpers.h"

#include "BaseLib/Thread.h"

class CMyTestClass
{
public:
	CMyTestClass()
	{
		m_iCounter = 0;
	}

	unsigned long aTestFunction(int p_iTargetCount)
	{
		while(m_iCounter != p_iTargetCount)
		{
			p_iTargetCount > m_iCounter ? m_iCounter++ : m_iCounter--;
//			printf("%d\n", m_iCounter);
		}
		return 0;
	}

	int m_iCounter;
};

static float g_fCounter = 0.0f;
unsigned long AGlobalFunction(float p_fTargetValue)
{
	while(g_fCounter < p_fTargetValue)
	{
		g_fCounter += 0.01f;
	}
	return 0;
}

void TestThread()
{
	cout << "Testing CThread..." << endl;
	
	CMyTestClass o;

	CThread xThread1 = StartNewThread(o, CMyTestClass::aTestFunction, 100, true);
	Assertion(xThread1.IsValid(), "createthread failed");

	// aktuelle Timeslice aufgeben - der Thread sollte suspended sein, also darf noch nix passieren
	Sleep(0);	
	Assertion(o.m_iCounter == 0, "thread is not suspended");	

	// thread aufwecken und auf Ende warten
	xThread1.Resume();
	xThread1.Wait();
	Assertion(o.m_iCounter == 100, "thread did not run");	
	
	CThread xThread2 = StartNewThread(AGlobalFunction, 100.0f);
	xThread2.Wait();
	Assertion(g_fCounter >= 100.0f, "thread did not run");	
}
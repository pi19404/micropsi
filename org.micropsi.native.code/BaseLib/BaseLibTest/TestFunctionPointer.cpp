#include "stdafx.h"

#include "TestHelpers.h"
#include "BaseLib/FunctionPointer.h"


/// einface Testk
class CFunctionPointerTestClass
{
public:

	CFunctionPointerTestClass()
	{
		m_iCounter0ArgCalls = 0;
		m_iIntArg			= 0;
		m_fFloatArg			= 0.0f;
	}

	void FunctionWith0Args()					{ m_iCounter0ArgCalls++; }
	void FunctionWithIntArg(int iArg)			{ m_iIntArg = iArg; }
	void FunctionWithFloatArg(float fArg)		{ m_fFloatArg = fArg; }
	int FunctionWithIntReturn()					{ return 90210; }
	int CalculateSquare(int iArg)				{ return iArg * iArg; }
	int Add(int iArg0, int iArg1)				{ return iArg0 + iArg1; }

	void FunctionWithIntAndFloatArg(int iArg, float fArg)		{ m_iIntArg = iArg; m_fFloatArg = fArg; }

    int		m_iCounter0ArgCalls;
	int		m_iIntArg;
	float	m_fFloatArg;
};


static int	  g_iCounter0ArgCalls;				///< zählt die Aufrufe von GlobalFunctionWith0Args()
static int	  g_iGlobalIntVar   = 0;
static float  g_fGlobalFloatVar = 0;

void GlobalFunctionWith0Args()					
{
	g_iCounter0ArgCalls++;
}


void GlobalFunctionWith1Arg(int iArg)
{
	g_iGlobalIntVar = iArg;
}

int GlobalFunctionWithIntReturn()
{
	return 190666;
}

int CalculateDouble(int iArg)
{
	return iArg * 2;
}

void GlobalFunctionWith2Args(int iArg, float fArg)
{
	g_iGlobalIntVar = iArg;
	g_fGlobalFloatVar = fArg;
}

float Multiply(float fArg0, float fArg1)
{
	return fArg0 * fArg1;
}

void TestFunctionPointer()
{
	cout << "Testing CFunctionPointer..." << endl;

	CFunctionPointerTestClass xTestClass;


	// Alle Function-Pointer haben einen Parameterlosen default-ctor und einen bool-operator

	{
		CFunctionPointer0 xP0;
		Assertion(!xP0, "CFunctionPointer0 default ctor or bool operator not correct");
		CFunctionPointer0R<int> xP0R;
		Assertion(!xP0R, "CFunctionPointer0R default ctor or bool operator not correct");
		CFunctionPointer1<int> xP1;
		Assertion(!xP1, "CFunctionPointer1 default ctor or bool operator not correct");
		CFunctionPointer1R<int, int> xP1R;
		Assertion(!xP1R, "CFunctionPointer1R default ctor or bool operator not correct");
		CFunctionPointer2<int, int> xP2;
		Assertion(!xP2, "CFunctionPointer2  default ctor or bool operator not correct");
		CFunctionPointer2R<int, int, int> xP2R;
		Assertion(!xP2R, "CFunctionPointer2R default ctor or bool operator not correct");
	}


	// MemberFunktion ohne Argumente

	{
		CFunctionPointer0 xPointer0Args = CreateFunctionPointer0(&xTestClass, CFunctionPointerTestClass::FunctionWith0Args);
		Assertion(xTestClass.m_iCounter0ArgCalls == 0, "Counter should be 0 after Init");
		xPointer0Args();
		Assertion(xTestClass.m_iCounter0ArgCalls == 1, "member function call with no args did not work");
	}


	// MemberFunktion mit int-Argument

	{
		xTestClass.m_iIntArg = 0;
		CFunctionPointer1<int> xPointer1Arg = CreateFunctionPointer1(&xTestClass, CFunctionPointerTestClass::FunctionWithIntArg);
		xPointer1Arg(5);
		Assertion(xTestClass.m_iIntArg == 5, "member function call with 1 arg (int) did not work");

		xTestClass.m_fFloatArg = 0.0f;
		CFunctionPointer1<float> xAnotherPointer1Arg = CreateFunctionPointer1(&xTestClass, CFunctionPointerTestClass::FunctionWithFloatArg);
		xAnotherPointer1Arg(5.0f);
		Assertion(xTestClass.m_fFloatArg == 5.0f, "member function call with 1 arg (float) did not work");
	}


	// MemberFunktion mit Returnwert

	{
		CFunctionPointer0R<int> xPointer = CreateFunctionPointer0R(&xTestClass, CFunctionPointerTestClass::FunctionWithIntReturn);
		int iRet = xPointer();
		Assertion(iRet == 90210, "member function call without args and return value (int) did not work");
	}


	// Memberfunktion mit Parameter und Return-Wert
	{
		CFunctionPointer1R<int, int> xPointer = CreateFunctionPointer1R(&xTestClass, CFunctionPointerTestClass::CalculateSquare);
		int iRet = xPointer(6);
		Assertion(iRet == 36, "member function call with one arg and return value (int) did not work");
		iRet = xPointer(12);
		Assertion(iRet == 144, "member function call with one arg and return value (int) did not work");
	}


	// Memberfunktion mit zwei Argumenten

	{
		xTestClass.m_iIntArg = 0;
		xTestClass.m_fFloatArg = 0.0f;
		CFunctionPointer2<int, float> xPointer = CreateFunctionPointer2(&xTestClass, CFunctionPointerTestClass::FunctionWithIntAndFloatArg);
		xPointer(123, 456.7f);
		Assertion(xTestClass.m_iIntArg == 123, "member function call with two arguments did not work");
		Assertion(xTestClass.m_fFloatArg == 456.7f, "member function call with two arguments did not work");
	}


	// Memberfunktion mit zwei Argumenten und Rückgabewert

	{
		CFunctionPointer2R<int, int, int> xPointer = CreateFunctionPointer2R(&xTestClass, CFunctionPointerTestClass::Add);
		int iRet = xPointer(12, 177);
		Assertion(iRet == 189, "member function call with two arguments and return value did not work");
	}


	// global Funktion ohne Argumente

	{
		Assertion(g_iCounter0ArgCalls == 0, "Counter should be 0 after Init");
		CFunctionPointer0 xPointer = CreateFunctionPointer0(GlobalFunctionWith0Args);
		xPointer();
		Assertion(g_iCounter0ArgCalls == 1, "static function call without arguments did not work");
	}


	// global Funktion mit einem Argument

	{
		g_iGlobalIntVar = 0;
		CFunctionPointer1<int> xPointer = CreateFunctionPointer1(GlobalFunctionWith1Arg);
		xPointer(779);
		Assertion(g_iGlobalIntVar == 779, "static function call with one argument did not work");
	}


	// global Funktion ohne Argumente aber mit int-Returnwert

	{
		CFunctionPointer0R<int> xPointer = CreateFunctionPointer0R(GlobalFunctionWithIntReturn);
		int iRet = xPointer();
		Assertion(iRet == 190666, "static function call without args and return value (int) did not work");
	}


	// globale Funktion mit einem Argument und Return-Wert

	{
		g_iGlobalIntVar = 0;
		CFunctionPointer1R<int, int> xPointer = CreateFunctionPointer1R(CalculateDouble);
		int iRet = xPointer(15);
		Assertion(iRet == 30, "static function call with one arg and return value (int) did not work");
	}


	// globale Funktion mit zwei Argumenten

	{
		g_iGlobalIntVar = 0;
		g_fGlobalFloatVar = 0.0f;
		CFunctionPointer2<int, float> xPointer = CreateFunctionPointer2(GlobalFunctionWith2Args);
		xPointer(123, 456.7f);
		Assertion(g_iGlobalIntVar == 123, "static function call with two arguments did not work");
		Assertion(g_fGlobalFloatVar == 456.7f, "static function call with two arguments did not work");
	}


	// globale Funktion mit zwei Argumenten und Rückgabewert

	{
		CFunctionPointer2R<float, float, float> xPointer = CreateFunctionPointer2R(Multiply);
		float fRet = xPointer(2.5f, 4.0f);
		Assertion(fRet == 10.0f, "static function call with two arguments and return value did not work");
	}
}


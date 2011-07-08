#include "stdafx.h"
#include "TestHelpers.h"

#include "BaseLib/pnt.h"
#include "BaseLib/size.h"
#include "BaseLib/fourcc.h"
#include "BaseLib/color.h"


void TestMisc()
{
	cout << "Testing CFourCC..." << endl;

	// --- CFourCC ---
	{
		CFourCC xTest = "BLAH";
		Assertion(xTest == "BLAH", "CFourCC == failed");
		Assertion(xTest != "BLUH", "CFourCC != failed");
		Assertion(xTest <  "CLAH", "CFourCC < failed");
		Assertion(xTest > "ALAH", "CFourCC > failed");
		Assertion(xTest.AsString() == "BLAH", "CFourCC AsString() failed");
	}

	cout << "Testing Macros..." << endl;

	// --- Macros ---
	{
		Assertion(clamp(10, 5, 15) == 10, "clamp (1) failed");
		Assertion(clamp(5, 5, 15) == 5, "clamp (2) failed");
		Assertion(clamp(15, 5, 15) == 15, "clamp (3) failed");
		Assertion(clamp(-1, -1, 1) == -1, "clamp (4) failed");
		Assertion(clamp(1, -1, 1) == 1, "clamp (5) failed");
		Assertion(clamp(-2, -1, 1) == -1, "clamp (6) failed");
		Assertion(clamp(2, -1, 1) == 1, "clamp (7) failed");
	}
}

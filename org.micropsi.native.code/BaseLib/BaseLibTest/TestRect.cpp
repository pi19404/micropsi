#include "stdafx.h"
#include "TestHelpers.h"

#include "BaseLib/rect.h"


void TestCRct()
{
	cout << "Testing CRct..." << endl;

	{
		Assertion(CRct(1, 2, 3, 4) == CRct(1, 2, 3, 4), "== failed");
		Assertion(CRct(0, 2, 3, 4) != CRct(1, 2, 3, 4), "!= failed");
		Assertion(CRct(1, 0, 3, 4) != CRct(1, 2, 3, 4), "!= failed");
		Assertion(CRct(1, 2, 0, 4) != CRct(1, 2, 3, 4), "!= failed");
		Assertion(CRct(1, 2, 3, 0) != CRct(1, 2, 3, 4), "!= failed");

		Assertion(CRct(1, 2, 3, 4) + CPnt(1, 2) == CRct(2, 4, 4, 6), "CRct + failed");
	}

	{
		CRct r = CRct(1, 2, 8, 5);
		CPnt A, B; 

		// senkrechte Linien
			
		A = CPnt(4, 1);    B = CPnt(4, 7);
		Assertion(r.ClipLine(A, B) == true, "ClipLine failed");
		Assertion(A == CPnt(4, 2)  &&  B == CPnt(4,4), "ClipLine failed");

		A = CPnt(1, 1);    B = CPnt(1, 7);
		Assertion(r.ClipLine(A, B) == true, "ClipLine failed");
		Assertion(A == CPnt(1, 2)  &&  B == CPnt(1,4), "ClipLine failed");

		A = CPnt(7, 1);    B = CPnt(7, 7);
		Assertion(r.ClipLine(A, B) == true, "ClipLine failed");
		Assertion(A == CPnt(7, 2)  &&  B == CPnt(7,4), "ClipLine failed");

		A = CPnt(7, 1);    B = CPnt(7, 7);
		Assertion(r.ClipLine(B, A) == true, "ClipLine failed");
		Assertion(A == CPnt(7, 2)  &&  B == CPnt(7,4), "ClipLine failed");

		A = CPnt(8, 1);    B = CPnt(8, 7);
		Assertion(r.ClipLine(A, B) == false, "ClipLine failed");

		A = CPnt(0, 1);    B = CPnt(0, 7);
		Assertion(r.ClipLine(A, B) == false, "ClipLine failed");

		A = CPnt(5, 3);    B = CPnt(5, 7);
		Assertion(r.ClipLine(B, A) == true, "ClipLine failed");
		Assertion(A == CPnt(5, 3)  &&  B == CPnt(5,4), "ClipLine failed");

		A = CPnt(5, -2);    B = CPnt(5, 3);
		Assertion(r.ClipLine(B, A) == true, "ClipLine failed");
		Assertion(A == CPnt(5, 2)  &&  B == CPnt(5,3), "ClipLine failed");

		A = CPnt(5, 3);    B = CPnt(5, 4);
		Assertion(r.ClipLine(B, A) == true, "ClipLine failed");
		Assertion(A == CPnt(5, 3)  &&  B == CPnt(5,4), "ClipLine failed");
	}

}







#include "stdafx.h"
#include "TestHelpers.h"

#include <time.h>
#include "BaseLib/rectlist.h"


void TestCRctList()
{
	cout << "Testing CRctList..." << endl;

	{
		CRctList xRL;
		xRL.Push(CRct(0, 0, 10, 10));
		xRL.Push(CRct(10, 0, 20, 10));
		xRL.Push(CRct(0, 10, 10, 20));
		xRL.Push(CRct(10, 10, 20, 20));
		Assertion(xRL.Size() == 4, "Push() failed");

		xRL.Compact();
		Assertion(xRL.Size() == 1, "Compact() failed");

		xRL.Push(20, 0, 30, 20);
		xRL.Push(50, 50, 60, 60);
		xRL.Compact();
		Assertion(xRL.Size() == 2, "Compact() failed");

		Assertion(xRL.PointHitTest(CPnt(0, 0)) == true, "Compact() failed");
		Assertion(xRL.PointHitTest(CPnt(29, 19)) == true, "Compact() failed");
		Assertion(xRL.PointHitTest(CPnt(30, 20)) == false, "Compact() failed");
		Assertion(xRL.PointHitTest(CPnt(55, 55)) == true, "Compact() failed");
	}


	{
		CRctList xRLDisjunct;
		CRctList xRLNormalList;
		
		/*  Testidee für die disjunkte Liste: 
			Zufällige Rechtecke erzeugen und zur Liste hinzufügen. Nach jeder Aktion muss die Liste noch disjunkt sein.
			Parallel normale Liste aller Rechtecke führen. Nach jeder Aktion müssen alle Eckpunkte der Rechtecke in der normalen
			Liste auch in der disjunkten Liste enthalten sein.
		*/

		srand( (unsigned)time( NULL ) );
		int i;
		for(i=0; i<250; ++i)
		{
			CRct r;
			r.left   = rand() % 1000;
			r.top    = rand() % 1000;
			r.right  = r.left + 1 + (rand() % (1000 - r.left));
			r.bottom = r.top  + 1 + (rand() % (1000 - r.top));

			xRLNormalList.Push(r);
			xRLDisjunct.Add(r);

			Assertion(xRLNormalList.Size() == i+1, "Push() failed");
			Assertion(xRLDisjunct.IsDisjunct(), "Add() failed; no longer disjunct");

			unsigned int iter;
			xRLNormalList.StartIterate(iter);
			CRct xTestRect;
			while(xRLNormalList.Iterate(iter, xTestRect))
			{
				Assertion(xRLDisjunct.PointHitTest(CPnt(xTestRect.left, xTestRect.top)), "Add() failed; area not covered correctly");
				Assertion(xRLDisjunct.PointHitTest(CPnt(xTestRect.right-1, xTestRect.bottom-1)), "Add() failed; area not covered correctly");
			}
		}

		while(xRLNormalList.Size() > 0)
		{
			CRct r;
			xRLNormalList.Pop(r);
			xRLDisjunct.Sub(r);

			Assertion(xRLDisjunct.IsDisjunct(), "Sub() failed; no longer disjunct");
			Assertion(xRLDisjunct.PointHitTest(CPnt(r.left, r.top)) == false, "Sub() failed; area still covered");
			Assertion(xRLDisjunct.PointHitTest(CPnt(r.right-1, r.bottom-1)) == false, "Sub() failed; area still covered");
		}


	}

}






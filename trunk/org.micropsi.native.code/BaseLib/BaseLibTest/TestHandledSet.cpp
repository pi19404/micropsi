#include "stdafx.h"
#include "TestHelpers.h"

#include "BaseLib/handledset.h"

void TestHandledSet()
{
	cout << "Testing CHandledSet..." << endl;

	CHandledSet<int>	m_xIntSet;

	unsigned long h1, h2, h3;

	h1 = m_xIntSet.PushEntry();
	m_xIntSet.SetElement(h1, 10);
	Assertion(m_xIntSet.Size() == 1, "Size() failed");

	h2 = m_xIntSet.PushEntry();
	m_xIntSet.SetElement(h2, 15);
	Assertion(m_xIntSet.Size() == 2, "Size() failed");

	h3 = m_xIntSet.PushEntry();
	m_xIntSet.SetElement(h3, 20);
	Assertion(m_xIntSet.Size() == 3, "Size() failed");

	Assertion(m_xIntSet.IsValid(h1), "IsValid() failed");
	Assertion(m_xIntSet.IsValid(h2), "IsValid() failed");
	Assertion(m_xIntSet.IsValid(h3), "IsValid() failed");

	Assertion(m_xIntSet.Element(h3) == 20, "Element() failed");
	Assertion(m_xIntSet.Element(h2) == 15, "Element() failed");
	Assertion(m_xIntSet.Element(h1) == 10, "Element() failed");

	m_xIntSet.DeleteEntry(h2);
	Assertion(m_xIntSet.IsValid(h1) == true, "IsValid() failed");
	Assertion(m_xIntSet.IsValid(h2) == false, "IsValid() failed");
	Assertion(m_xIntSet.IsValid(h3) == true, "IsValid() failed");
	Assertion(m_xIntSet.Size() == 2, "Size() failed");

	unsigned long h4;
	h4 = m_xIntSet.PushEntry();				/// sollte jetzt den Platz von h2 wiederverwenden...
	m_xIntSet.SetElement(h4, 666);
	Assertion(m_xIntSet.Size() == 3, "Size() failed");
	Assertion(m_xIntSet.IsValid(h2) == false, "IsValid() failed");
	Assertion(m_xIntSet.IsValid(h4) == true, "IsValid() failed");

	// iteration testen:

	int iSum = 0;
	unsigned long iter;
	int* pi = 0;
	m_xIntSet.StartIterate(iter);
	while(m_xIntSet.Iterate(iter, pi))
	{
		iSum += *pi;
	}
	Assertion(iSum == 696, "Iteration failed");
}
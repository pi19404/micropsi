#include "stdafx.h"

#include "TestHelpers.h"

#include "BaseLib/comobjectptr.h"

class CFakeComObject
{
public:
	CFakeComObject() : m_iRefCount(0) {};
	void	AddRef()				{ m_iRefCount++; }
	void	Release()				{ m_iRefCount--; }
	int		SomeFunction()			{ return 666; }

	int		AnotherFunction(CFakeComObject* p_pxPtr)	{ return p_pxPtr->SomeFunction() / 2; }

	int		m_iRefCount;
};


void TestComObjectPtr()
{
	cout << "Testing CComObjectPtr..." << endl;
	
	CFakeComObject o1, o2;

	{
		CComObjectPtr<CFakeComObject> p1;
		Assertion(p1.IsNull(),					"ctor failed");

		CComObjectPtr<CFakeComObject> p2(&o2);
		Assertion(!p2.IsNull(),					"pointer ctor failed");
		Assertion(o2.m_iRefCount == 1,			"pointer ctor failed");

		CComObjectPtr<CFakeComObject> p3(p2);
		Assertion(!p3.IsNull(),					"pointer ctor failed");
		Assertion(o2.m_iRefCount == 2,			"pointer ctor failed");

		Assertion(p2 == p3,						"== failed");
		Assertion(p1 != p3,						"!= failed");

		Assertion(p2 == &o2,					"== failed");
		Assertion(p1 != &o2,					"!= failed");
		Assertion(p2 != &o1,					"== failed");

		Assertion(&(*p2) == (&o2),				"* failed");	
		Assertion(p2->SomeFunction() == 666,	"--> failed");	

		Assertion(p2->AnotherFunction(p3)==333, "auto type cast failed");

		p1 = p2;
		Assertion(p1 != 0,						"= failed");
		Assertion(p2->m_iRefCount == 3,			"= failed");
		
		p1 = p2;
		Assertion(p2->m_iRefCount == 3,			"= failed");

		p1 = 0;
		Assertion(p2->m_iRefCount == 2,			"= failed");

		p1 = p2;
		p1 = &o1;
		Assertion(o1.m_iRefCount == 1,			"= failed");
		Assertion(o2.m_iRefCount == 2,			"= failed");
	}

	Assertion(o1.m_iRefCount == 0,			"dtor failed");
	Assertion(o2.m_iRefCount == 0,			"dtor failed");
}
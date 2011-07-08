#include "stdafx.h"

#include "TestHelpers.h"

#include "BaseLib/refcountedobject.h"


bool g_bObjAllocated = false;

class CObject : public CRefCountedObject
{
private:
    CObject()   {g_bObjAllocated = true; iID = -1;}
    ~CObject()  {g_bObjAllocated = false;}

public:
    static CObject* Create()    {return new CObject();}
    void            Destroy()   {delete this;}

    int iID;
};

typedef CSmartPointer<CObject> TObjectPtr;

void TestSmartPtr()
{
	cout << "Testing CSmartPointer..." << endl;


    TObjectPtr spObject0;
    TObjectPtr spObject1;

    Assertion(!spObject0, "smartpointer test failed");

    spObject0.Create();
    Assertion(spObject0, "smartpointer test failed");

    spObject0->iID = 0;
	Assertion(g_bObjAllocated && (spObject0->iID == 0), "smartpointer test failed");

    spObject0 = spObject0;
    Assertion(g_bObjAllocated && (spObject0->iID == 0), "smartpointer test failed");

    spObject0 = 0;
	Assertion(!g_bObjAllocated, "smartpointer test failed");

    spObject0.Create();
    spObject0->iID = 1;
	Assertion(g_bObjAllocated && (spObject0->iID == 1), "smartpointer test failed");

    spObject1 = spObject0;
	Assertion(g_bObjAllocated && (spObject1->iID == 1), "smartpointer test failed");

    spObject0 = 0;
	Assertion(g_bObjAllocated && (spObject1->iID == 1), "smartpointer test failed");

    TObjectPtr spObject2(spObject1);
	Assertion(g_bObjAllocated && (spObject2->iID == 1), "smartpointer test failed");

    spObject1 = 0;
	Assertion(g_bObjAllocated && (spObject2->iID == 1), "smartpointer test failed");

    spObject2 = 0;
	Assertion(!g_bObjAllocated, "smartpointer test failed");
}
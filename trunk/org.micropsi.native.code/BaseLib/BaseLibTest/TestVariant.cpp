#include "stdafx.h"
#include "TestHelpers.h"

#include "BaseLib/Variant.h"

void TestVariant()
{
	cout << "Testing CVariant..." << endl;

	{
		CVariantTypeInt xIntType("numHitpoints", "anzahl der hitpunkte", 0, 100, 100);
		CVariantTypeFloat xFloatType("numHitpoints", "anzahl der hitpunkte", 0, 100, 100);
		CVariantTypeString xStringType("spielername", "der name den die anderen spieler im netzwerk sehen", "blaster");
		CVariantTypeVec3 xVec3Type("test");


		CVariant xVariant0(&xIntType);
		CVariant xVariant1(&xFloatType);
		CVariant xVariant2(&xStringType);
		CVariant xVariant3(&xVec3Type);
		
		xVariant0 = 5;
		xVariant2 = (string)"hallo";

		xVariant3 = CVec3(1.00001f, 2, 3);

		std::string sVector;
		xVariant3.SaveToString(&sVector);

		xVariant3 = CVec3(3, 2, 1);
		xVariant3.LoadFromString(&sVector);
		assert((CVec3)xVariant3 == CVec3(1.00001f, 2, 3));


		const char* pcName =
			xVariant2.GetDesc()->GetTypeId().name();

		if (xVariant0.GetDesc()->GetTypeId() == typeid(float))
		{
			float f = xVariant0;
		}
		else
		if (xVariant0.GetDesc()->GetTypeId() == typeid(int))
		{
			int i = xVariant0;
		}

		assert(xVariant2.GetDesc()->GetTypeId() == typeid(std::string));

		std::string s = xVariant2;
		Assertion(s == "hallo", "Variant int failed");

		xVariant2.ReAssign(xVariant0);

		int i = xVariant0;
		Assertion(i == 5, "Variant int failed");

		int j = xVariant2;
		Assertion(j == 5, "Variant int failed");
	}
}

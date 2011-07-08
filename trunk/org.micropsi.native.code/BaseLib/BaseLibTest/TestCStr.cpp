#include "stdafx.h"
#include "TestHelpers.h"

#include "BaseLib/str.h"

void TestCStr()
{
	cout << "Testing CStr..." << endl;

	// test copy ctor
	{
		CStr sOriginal = "Teststring";
		CStr sCopy = sOriginal;
		Assertion(sCopy == sOriginal, "test copy ctor failed");
		Assertion(sOriginal == "Teststring", "test copy ctor failed");		
		Assertion(sCopy == "Teststring", "test copy ctor failed");
	}

	// test assignment
	{
		CStr sOriginal = "Teststring";
		CStr sCopy = "blah";
		sCopy = sOriginal;
		Assertion(sCopy == "Teststring", "test assignment failed");
	}

	// test concatenation
	{
		CStr sString1 = "FirstPart";
		CStr sString2 = "SecondPart";
		CStr sBoth = sString1 + sString2;
		Assertion(sBoth == "FirstPartSecondPart", "test concatenation failed");
	}

	// test Find(), FindReverse()
	{
		CStr s = "0123210";
		Assertion(s.Find('0') == 0, "test Find() failed");
		Assertion(s.Find('1') == 1, "test Find() failed");
		Assertion(s.Find('2') == 2, "test Find() failed");
		Assertion(s.Find('3') == 3, "test Find() failed");
		Assertion(s.Find('2', 3) == 4, "test Find() failed");
		Assertion(s.Find('1', 3) == 5, "test Find() failed");
		Assertion(s.Find('3', 4) == -1, "test Find() failed");

		Assertion(s.FindReverse('0') == 6, "test FindReverse() failed");
		Assertion(s.FindReverse('1') == 5, "test FindReverse() failed");
		Assertion(s.FindReverse('2') == 4, "test FindReverse() failed");
		Assertion(s.FindReverse('3') == 3, "test FindReverse() failed");
		Assertion(s.FindReverse('2', 3) == 2, "test FindReverse() failed");
		Assertion(s.FindReverse('1', 3) == 1, "test FindReverse() failed");
		Assertion(s.FindReverse('3', 2) == -1, "test FindReverse() failed");

	}

	// test Left(), Right(), Mid()
	{
		CStr s = "Semmelbrot";
		Assertion(s.Left(3) == "Sem", "test Right() failed");
		Assertion(s.Left(7) == "Semmelb", "test Right() failed");
		Assertion(s.Right(3) == "rot", "test Left() failed");
		Assertion(s.Right(99) == "Semmelbrot", "test Left() failed");
		Assertion(s.Mid(4, 5) == "elbro", "test Mid() failed");
		Assertion(s.Mid(3, 15) == "melbrot", "test Mid() failed");
		Assertion(s.Mid(-3, 3) == "Sem", "test Mid() failed");
	}

	// test ToInt()
	{
		CStr s = "10";
		Assertion(s.ToInt() == 10, "test ToInt() failed");
	}

	// test Delete()
	{
		CStr sOriginal = "0123456789";
		CStr s = sOriginal;
		s.Delete(4,3);
		Assertion(sOriginal == "0123456789", "test Delete() failed");
		Assertion(s == "0123789", "test Delete() failed");
		s = sOriginal;
		s.Delete(0,5);
		Assertion(sOriginal == "0123456789", "test Delete() failed");
		Assertion(s == "56789", "test Delete() failed");
		s = sOriginal;
		s.Delete(0,10);
		Assertion(sOriginal == "0123456789", "test Delete() failed");
		Assertion(s.IsEmpty(), "test Delete() failed");
		s = sOriginal;
		s.Delete(5,5);
		Assertion(sOriginal == "0123456789", "test Delete() failed");
		Assertion(s == "01234", "test Delete() failed");
		Assertion(s.GetLength() == 5, "test Delete() failed");

	}

	// test Insert(char)
	{
		CStr sOriginal = "0123456789";
		CStr s = sOriginal;
		s.Insert(3, 'a');
		Assertion(s == "012a3456789", "test Insert(char) failed");
		s.Insert(0, 'b');
		Assertion(s == "b012a3456789", "test Insert(char) failed");
		s.Insert(12, 'c');
		Assertion(s == "b012a3456789c", "test Insert(char) failed");
		s.Insert(10, 'd');
		Assertion(s == "b012a34567d89c", "test Insert(char) failed");
		Assertion(sOriginal == "0123456789", "test Insert(char) failed");
	}

	// test Insert(char*)
	{
		CStr sOriginal = "0123456789";
		CStr s = sOriginal;
		s.Insert(3, "abc");
		Assertion(s == "012abc3456789", "test Insert(char*) failed");
		s.Insert(0, "ABC");
		Assertion(s == "ABC012abc3456789", "test Insert(char*) failed");
		s.Insert(16, "XXX");
		Assertion(s == "ABC012abc3456789XXX", "test Insert(char*) failed");
		s.Insert(10, "blahblah");
		Assertion(s == "ABC012abc3blahblah456789XXX", "test Insert(char*) failed");
		Assertion(sOriginal == "0123456789", "test Insert(char) failed");
	}


	// test Replace
	{
		CStr sOriginal = "I am a Teststring";
		CStr s = sOriginal;
        s.Replace('a', 'e');
		Assertion(sOriginal == "I am a Teststring", "test Replace() failed");
		Assertion(s == "I em e Teststring", "test Replace() failed");
	}

	// test Replace
	{
		CStr sOriginal = "I am a :) Teststring :)";
		CStr s = sOriginal;
		s.Replace(":)", "@");
		Assertion(sOriginal == "I am a :) Teststring :)", "test Replace() failed");
		Assertion(s == "I am a @ Teststring @", "test Replace() failed");
	}


	// test Remove
	{
		CStr sOriginal = "Hey, you Stoopid!";
		CStr s = sOriginal;
		s.Remove('o');
		Assertion(sOriginal == "Hey, you Stoopid!", "test Remove() failed");
		Assertion(s == "Hey, yu Stpid!", "test Remove() failed");
		s.Remove('y');
		Assertion(s == "He, u Stpid!", "test Remove() failed");
		s.Remove('x');
		Assertion(s == "He, u Stpid!", "test Remove() failed");
		s = "xxxxxxxxxxxxxxx";
		s.Remove('x');
		Assertion(s.GetLength() == 0, "test Remove() failed");
	}


	// test TrimLeft
	{
		CStr sOriginal = "xxI am a Teststring";
		CStr s = sOriginal;
		s.TrimLeft('x');
		Assertion(sOriginal == "xxI am a Teststring", "test TrimLeft() failed");
		Assertion(s == "I am a Teststring", "test TrimLeft() failed");
		s.TrimLeft('y');
		Assertion(s == "I am a Teststring", "test TrimLeft() failed");
		s.TrimLeft('I');
		Assertion(s == " am a Teststring", "test TrimLeft() failed");
		s = "xxxxxxxxxxxxxxx";
		s.TrimLeft('x');
		Assertion(s.GetLength() == 0, "test TrimLeft() failed");
	}


	// test TrimRight
	{
		CStr sOriginal = "I am a Teststringggg";
		CStr s = sOriginal;
		s.TrimRight('g');
		Assertion(sOriginal == "I am a Teststringggg", "test TrimRight() failed");
		Assertion(s == "I am a Teststrin", "test TrimRight() failed");
		s.TrimRight('y');
		Assertion(s == "I am a Teststrin", "test TrimRight() failed");
		s.TrimRight('n');
		Assertion(s == "I am a Teststri", "test TrimRight() failed");
		s = "xxxxxxxxxxxxxxx";
		s.TrimRight('x');
		Assertion(s.GetLength() == 0, "test TrimRight() failed");
	}

	// test SetAt()
	{
		CStr sOriginal = "0123456789";
		CStr s = sOriginal;
		s.SetAt(0, 'a');
		Assertion(sOriginal == "0123456789", "test SetAt() failed");
		Assertion(s == "a123456789", "test SetAt() failed");
		s.SetAt(8, 'b');
		Assertion(s == "a1234567b9", "test SetAt() failed");
	}

	// test += char
	{
		CStr sOriginal = "0123456789";
		CStr s = sOriginal;
		s += "c";
		Assertion(sOriginal == "0123456789", "test +=(char) failed");
		Assertion(s == "0123456789c", "test +=(char) failed");
	}

	// test Split()
	{
		CStr s = "This; is ; a ;Test";
		CDynArray<CStr> asArray;
		s.Split(asArray, ";");
		Assertion(asArray.Size() == 4, "test Split() failed");
		Assertion(asArray[0] == "This", "test Split() failed");
		Assertion(asArray[1] == " is ", "test Split() failed");
		Assertion(asArray[2] == " a ", "test Split() failed");
		Assertion(asArray[3] == "Test", "test Split() failed");
	}

	// test MakeUpper, ToUpper
	{
		CStr sOriginal = "a^2+b^2=C^2";
		CStr s = sOriginal;
		s.MakeUpper();
		Assertion(sOriginal == "a^2+b^2=C^2", "MakeUpper() failed");
		Assertion(sOriginal.ToUpper() == "A^2+B^2=C^2", "ToUpper() failed");
		Assertion(s == "A^2+B^2=C^2", "MakeUpper failed");
	}

	// test MakeLower, ToLower
	{
		CStr sOriginal = "A^2+b^2=C^2";
		CStr s = sOriginal;
		s.MakeLower();
		Assertion(sOriginal == "A^2+b^2=C^2", "MakeLower() failed");
		Assertion(sOriginal.ToLower() == "a^2+b^2=c^2", "ToLower() failed");
		Assertion(s == "a^2+b^2=c^2", "MakeLower failed");
	}

	// test MakeReverse, ToReverse
	{
		CStr sOriginal = "a^2+b^2=C^2";
		CStr s = sOriginal;
		s.MakeReverse();
		Assertion(sOriginal == "a^2+b^2=C^2", "MakeReverse() failed");
		Assertion(sOriginal.ToReverse() == "2^C=2^b+2^a", "ToReverse() failed");
		Assertion(s == "2^C=2^b+2^a", "MakeReverse failed");
	}

}

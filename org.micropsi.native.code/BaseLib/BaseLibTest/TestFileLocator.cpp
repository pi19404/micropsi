#include "stdafx.h"
#include "TestHelpers.h"

#include "BaseLib/FileLocator.h"


std::string MyCallBackFunction(const CFileLocator* p_pxFileLocator, std::string p_sAlias, std::string p_sRemainingPath, std::string p_sDefinedPath, void* p_pUserData)
{
	Assertion(p_sAlias== "textures", "...failed!");
	Assertion(p_sRemainingPath== "red.tga", "...failed!");

	return "green.tga";
}

void TestFileLocator()
{
	cout << "Testing CFileLocator..." << endl;

	CFileLocator l;
	l.SetAlias("media", "../media");
	l.SetAlias("shaders", "media>shaders/dx7");

	{
		std::string s = l.GetPath("shaders>blah.fx");
		Assertion(s == "../media/shaders/dx7/blah.fx", "...failed!");
	}

	{
		std::string s = l.GetPath("texture>blah.tga");
		Assertion(s == "blah.tga", "...failed!");
	}


	l.SetAlias("media", "../newmedia/quark/");

	{
		std::string s = l.GetPath("shaders>blah.fx");
		Assertion(s == "../newmedia/quark/shaders/dx7/blah.fx", "...failed!");
	}

	l.SetAlias("textures", "", MyCallBackFunction);
	{
		std::string s = l.GetPath("textures>red.tga");
		Assertion(s == "green.tga", "...failed!");
	}


    Assertion(CFileLocator::CompactPath("../../../aaa/bbb/../test.dat") == "../../../aaa/test.dat", "...failed!");
    Assertion(CFileLocator::CompactPath("c:.\\../BBB/.\\AAAA/../../.\\abc.dat") == "c:../abc.dat", "...failed!");
    Assertion(CFileLocator::CompactPath("c:\\abc.dat") == "c:/abc.dat", "...failed!");
    Assertion(CFileLocator::CompactPath("") == "", "...failed!");
    Assertion(CFileLocator::CompactPath("./") == "", "...failed!");
    Assertion(CFileLocator::CompactPath("123.dat") == "123.dat", "...failed!");
    Assertion(CFileLocator::CompactPath("c:/test/test/") == "c:/test/test/", "...failed!");
    Assertion(CFileLocator::CompactPath("c:/test/test/abc.dat") == "c:/test/test/abc.dat", "...failed!");
    Assertion(CFileLocator::CompactPath("c:/../test.dat") == "c:/../test.dat", "...failed!");       // < fehlerhafter pfad
}

